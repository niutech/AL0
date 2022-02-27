package computer.fuji.al0.services;

import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Vibrator;
import android.telecom.Call;
import android.telecom.CallAudioState;
import android.telecom.VideoProfile;

import computer.fuji.al0.activities.CallActivity;
import computer.fuji.al0.models.ActivityItem;
import computer.fuji.al0.utils.CurrentCall;
import computer.fuji.al0.utils.PhoneNumber;
import computer.fuji.al0.utils.Preferences;

import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import static android.telecom.CallAudioState.ROUTE_EARPIECE;
import static android.telecom.CallAudioState.ROUTE_SPEAKER;
import static android.telecom.CallAudioState.ROUTE_BLUETOOTH;

public class InCallService extends android.telecom.InCallService {
    static final String CHANNEL_ID_INCOMING_CALL = "CHANNEL_ID_INCOMING_CALL";
    private static InCallServiceEventsListener inCallServiceEventsListener;
    private static Ringtone ringtone;
    private static Vibrator vibrator;
    private static InCallService inCallService;
    private static boolean isInboundCall;
    private static Timer notifyMissedCallTimer;
    private static ActivityService activityService = new ActivityService();
    private static int ringerMode;
    private static Date currentCallStartTime;

    private static boolean isAndroidMarshmallow = Build.VERSION.SDK_INT <= 23;
    private static boolean isAndroidOreoOrPie = Build.VERSION.SDK_INT <= 28 && Build.VERSION.SDK_INT >= 26;
    private static int beforeMuteRingerVolume = 0;

    public interface InCallServiceEventsListener {
        public void onCallRemoved(Call call);
    }

    public static void setInCallServiceEventsListener(InCallServiceEventsListener inCallServiceEventsListener) {
        InCallService.inCallServiceEventsListener = inCallServiceEventsListener;
    }

    public static void answer () {
        stopRingtone();
        Call currentCall = CurrentCall.getCurrentCall();

        if (currentCall != null) {
            currentCall.answer(VideoProfile.STATE_AUDIO_ONLY);
        }
    }

    public static void hangup () {
        Call currentCall = CurrentCall.getCurrentCall();
        if (currentCall != null) {
            currentCall.disconnect();

        }
    }

    public static void onCallFinish () {
        inCallService.resetRingtoneMode();
    }

    // set audio mute
    public static void setMute (boolean isMuted) {
        inCallService.setMuted(isMuted);
    }

    // set silent
    public static void setSilent() {
        inCallService.muteRingtone();
    }

    // set audio speaker
    public static void setSpeaker (boolean useSpeaker) {
        if (useSpeaker) {
            inCallService.setAudioRoute(ROUTE_SPEAKER);
        } else {
            inCallService.setAudioRoute(ROUTE_EARPIECE);
        }
    }

    // set audio bluetooth
    public static void setUseBluetooth (boolean useBluetooth) {
        if (useBluetooth) {
            inCallService.setAudioRoute(ROUTE_BLUETOOTH);
        } else {
            setSpeaker(false);
        }
    }

    public static void setCurrentCallStartTime (Date time) {
        if (currentCallStartTime != null) {
            // do nothing
        } else {
            currentCallStartTime = time;
        }
    }

    // getters
    public static Call getCurrentCall() {
        return CurrentCall.getCurrentCall();
    }

    public static String getCurrentPhoneNumber () {
        String currentPhoneNumber = "";
        Call currentCall = CurrentCall.getCurrentCall();
        if (currentCall != null) {
            // get phone number from current call
            currentPhoneNumber = currentCall.getDetails().getHandle().toString();
            // remove "tel:" and replace "%2B" with +
            // return currentPhoneNumber.replace("tel:", "");

            return PhoneNumber.cleanPhoneNumber(currentPhoneNumber.replace("tel:", "").replace("%2B", "+"));
        }

        return currentPhoneNumber;
    }
    public static boolean getIsInboundCall() {
        return isInboundCall;
    }

    public static boolean getIsBluetooth () {
        return inCallService.getCallAudioState().equals(ROUTE_BLUETOOTH);
    }

    public static boolean getHasBluetooth () {
        boolean hasBluetooth = (inCallService.getCallAudioState().getSupportedRouteMask() & ROUTE_BLUETOOTH) == ROUTE_BLUETOOTH;
        return hasBluetooth;
    }

    public static int getCurrentCallDuration () {
        if (currentCallStartTime != null) {
            Date currentTime = new Date();
            long durationInMilliseconds = currentTime.getTime() - currentCallStartTime.getTime();
            return (int) TimeUnit.MILLISECONDS.toSeconds(durationInMilliseconds);
        } else {
            return 0;
        }
    }

    public static boolean getMute () {
        return inCallService.getCallAudioState().isMuted();
    }

    public static boolean getSpeaker () {
        return inCallService.getCallAudioState().getRoute() == ROUTE_SPEAKER;
    }

    // events
    @Override
    public void onCallAdded(Call call) {
        inCallService = this;
        super.onCallAdded(call);
        //currentCall = call;
        CurrentCall.setCurrentCall(call);
        // check if phone should ring
        // check if is an inbound call
        // if call's state is STATE_RINGING is an inbound call
        isInboundCall = call.getState() == Call.STATE_RINGING;
        WakeLocker.acquire(getBaseContext(), WakeLocker.Type.CALL);
        if (isInboundCall) {
            // make the phone ring
            playRingtone(getApplicationContext());
        }

        AudioManager audioManager = (AudioManager)  getBaseContext().getSystemService(Context.AUDIO_SERVICE);
        ringerMode = audioManager.getRingerMode();

        // start CallActivity
        Intent startCallActivityIntent = new Intent(this, CallActivity.class);
        startCallActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startCallActivityIntent);
    }

    @Override
    // State changed events
    // isMuted
    // route eg. Speaker
    // activeBluetoothDevices
    public void onCallAudioStateChanged(CallAudioState audioState) {
        super.onCallAudioStateChanged(audioState);
        // use this to get state changed events
    }

    @Override
    public void onCallRemoved(Call call) {
        super.onCallRemoved(call);
        stopRingtone();

        if (inCallServiceEventsListener != null) {
            inCallServiceEventsListener.onCallRemoved(call);
        }

        // currentCall = null;
        CurrentCall.setCurrentCall(null);

        // reset current call time
        currentCallStartTime = null;

        // check if should notify a missed call

        if (notifyMissedCallTimer != null) {
            notifyMissedCallTimer.cancel();
        }

        notifyMissedCallTimer = new Timer();
        notifyMissedCallTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                // get last 10 recent activity items
                // fetch more than just 1 activity item, that because call activity item take time to get added on the DB
                // during this time could happen that a later activity item get added
                ArrayList<ActivityItem> recentActivityItems = activityService.getAllContactsActivityItems(getApplicationContext(), 10);
                // check if in latest activity items there is a valid item type call missed new
                ActivityItem item = getLatestMissedCall(recentActivityItems);
                if (item != null) {
                    AL0NotificationService.notifyMissedCallReceived(item.getCall());
                } else {
                    // no valid items found
                }
            }
        }, 1000);

        WakeLocker.release(WakeLocker.Type.CALL);
    }

    private ActivityItem getLatestMissedCall (ArrayList<ActivityItem> activityItems) {
        // loop all activity items
        ActivityItem item;

        for (int i = activityItems.size() -1; i > 0; i--) {
            item = activityItems.get(i);
            // check if item is a call
            if (item.getType() == ActivityItem.Type.CALL) {
                computer.fuji.al0.models.Call call = item.getCall();
                // check for a missed call
                if (call.getType() == computer.fuji.al0.models.Call.Type.MISSED) {
                    // check for a new item
                    if (call.getIsNew()) {
                        return item;
                    }
                }
            }
        }

        return null;
    }

    // play ringtone
    // use default system ringtone
    private static void playRingtone (Context context) {
        // make phone vibrate according to user preferences
        Preferences preferences = new Preferences(context);
        if (preferences.getSoundSettingsVibrationEnabled()) {
            vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            long[] pattern = { 0, 100, 1000 };
            vibrator.vibrate(pattern, 0);
        }



        if (isAndroidMarshmallow) {
            Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            ringtone = RingtoneManager.getRingtone(context, alert);
            ringtone.setAudioAttributes(new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE).build());

            ringtone.play();
        }  else if (isAndroidOreoOrPie) {
            // make sure to mute ringtone if is do not disturb mode
            if (SoundManager.getIsSilentMode(context)) {
                AudioManager audioManager = (AudioManager)  context.getSystemService(Context.AUDIO_SERVICE);
                beforeMuteRingerVolume = audioManager.getStreamVolume(AudioManager.STREAM_RING);
                audioManager.setStreamVolume(AudioManager.STREAM_RING, 0, 0);
            }
        } else {
            Uri ringtoneUri = SoundManager.getDefaultRingtone(context).getUri();
            SoundManager.playRingtone(context, ringtoneUri, AudioManager.STREAM_RING, true);
        }
    }

    private void muteRingtone () {
        AudioManager audioManager = (AudioManager)  getBaseContext().getSystemService(Context.AUDIO_SERVICE);
        audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);

        if (isAndroidMarshmallow) {
            if (ringtone != null) {
                ringtone.stop();
            }
        } else if (isAndroidOreoOrPie) {
            beforeMuteRingerVolume = audioManager.getStreamVolume(AudioManager.STREAM_RING);
            audioManager.setStreamVolume(AudioManager.STREAM_RING, 0, 0);
        } else {
            SoundManager.stopRingtone();
        }

        if (vibrator != null) {
            vibrator.cancel();
        }
    }

    private void resetRingtoneMode () {
        AudioManager audioManager = (AudioManager)  getBaseContext().getSystemService(Context.AUDIO_SERVICE);
        audioManager.setRingerMode(ringerMode);
        if (isAndroidOreoOrPie && beforeMuteRingerVolume > 0) {
            audioManager.setStreamVolume(AudioManager.STREAM_RING, beforeMuteRingerVolume, 0);
        }
    }

    // stop ringtone
    private static void stopRingtone () {
        // restore ringtone for future call in case ringtone get muted
        inCallService.resetRingtoneMode();

        if (Build.VERSION.SDK_INT <= 23) {
            if (ringtone != null) {
                ringtone.stop();
            }
        } else {
            SoundManager.stopRingtone();
        }

        if (vibrator != null) {
            vibrator.cancel();
        }
    }
}