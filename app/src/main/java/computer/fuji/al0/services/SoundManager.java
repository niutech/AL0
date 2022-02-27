package computer.fuji.al0.services;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;

import computer.fuji.al0.models.SoundModel;
import computer.fuji.al0.utils.Preferences;

import java.util.ArrayList;

public class SoundManager {
    private static Ringtone ringtone;
    private static MediaPlayer mediaPlayer;
    private static AudioManager audioManager;
    public static int VOLUME_RANGE_UPPER = 10;
    public static int VOLUME_RANGE_LOWER = 0;
    public static int VOLUME_RANGE_DELTA = VOLUME_RANGE_UPPER - VOLUME_RANGE_LOWER;

    // play a ringtone
    public static void playRingtone (final Context context, Uri uri) {
        if (ringtone != null) {
            ringtone.stop();
            ringtone = null;
        }
        ringtone = RingtoneManager.getRingtone(context, uri);
        ringtone.play();
    }

    public static void playRingtone (Context context, Uri uri, int audioStreamType, boolean shouldLoop) {
        stopRingtone();
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(context, uri);
            final AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            mediaPlayer.setAudioStreamType(audioStreamType);
            mediaPlayer.setLooping(shouldLoop);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch(Exception e) {
            // do nothing
        }
}

    public static void stopRingtone () {
        if (ringtone != null) {
            ringtone.stop();
            ringtone = null;
        }

        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer = null;
        }
    }

    public static void playNotificationRingtone (Context context) {
        Uri alert = SoundManager.getDefaultNotification(context).getUri();
        ringtone = RingtoneManager.getRingtone(context,  alert);
        ringtone.setAudioAttributes(new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE).build());
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        int systemVolume = audioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION);

        ringtone.play();
    }

    // get all tones
    public static ArrayList<SoundModel> getRingtoneList (Context context) {
        return getAllSounds(context, RingtoneManager.TYPE_RINGTONE);
    }

    public static ArrayList<SoundModel> getAlarmList (Context context) {
        return getAllSounds(context, RingtoneManager.TYPE_ALARM);
    }

    public static ArrayList<SoundModel> getNotificationList (Context context) {
        return getAllSounds(context, RingtoneManager.TYPE_NOTIFICATION);
    }

    private static ArrayList<SoundModel> getAllSounds (Context context, int ringtoneManagerType) {
        ArrayList<SoundModel> soundList = new ArrayList<>();

        ArrayList<SoundModel> ringtoneList = new ArrayList<>();
        RingtoneManager manager = new RingtoneManager(context);
        manager.setType(ringtoneManagerType);
        Cursor cursor = manager.getCursor();


        while (cursor.moveToNext()) {
            SoundModel sound = new SoundModel(
                    cursor.getString(RingtoneManager.TITLE_COLUMN_INDEX),
                    Uri.parse(cursor.getString(RingtoneManager.URI_COLUMN_INDEX).concat("/").concat(cursor.getString(RingtoneManager.ID_COLUMN_INDEX)))
            );

            ringtoneList.add(sound);
        }

        return ringtoneList;
    }

    // Get default tones
    public static SoundModel getDefaultRingtone (Context context) {
        return getDefaultSound(context, RingtoneManager.TYPE_RINGTONE);
    }

    public static SoundModel getDefaultAlarm (Context context) {
        return getDefaultSound(context, RingtoneManager.TYPE_ALARM);
    }

    public static SoundModel getDefaultNotification (Context context) {
        return getDefaultSound(context, RingtoneManager.TYPE_NOTIFICATION);
    }

    private static SoundModel getDefaultSound (Context context, int ringtoneManagerType) {
        Preferences preferences = new Preferences(context);

        Uri defaultSystemRingtoneUri = RingtoneManager.getActualDefaultRingtoneUri(context.getApplicationContext(), ringtoneManagerType);
        Uri defaultRingtoneUri = defaultSystemRingtoneUri;

        switch (ringtoneManagerType) {
            case RingtoneManager.TYPE_RINGTONE:
                String ringtonePath = preferences.getDefaultRingtoneRingtonePath();
                if (ringtonePath != null) {
                    defaultRingtoneUri = Uri.parse(ringtonePath);
                }
                break;
            case RingtoneManager.TYPE_ALARM:
                String alarmPath = preferences.getDefaultAlarmRingtonePath();
                if (alarmPath != null) {
                    defaultRingtoneUri = Uri.parse(alarmPath);
                }
                break;
            case RingtoneManager.TYPE_NOTIFICATION:
                String notificationPath = preferences.getDefaultNotificationRingtonePath();
                if (notificationPath != null) {
                    defaultRingtoneUri = Uri.parse(notificationPath);
                }
                break;
            default:
                // do nothing
                break;
        }

        Ringtone currentRingtone = RingtoneManager.getRingtone(context, defaultRingtoneUri);
        return new SoundModel(currentRingtone.getTitle(context), defaultRingtoneUri);
    }

    // set default tones
    public static void setDefaultRingtone (Activity activity, Uri uri) {
        setDefaultSound(activity, RingtoneManager.TYPE_RINGTONE, uri);
    }

    public static void setDefaultAlarm (Activity activity, Uri uri) {
        setDefaultSound(activity, RingtoneManager.TYPE_ALARM, uri);
    }

    public static void setDefaultNotification (Activity activity, Uri uri) {
        setDefaultSound(activity, RingtoneManager.TYPE_NOTIFICATION, uri);
    }

    private static void setDefaultSound (Activity activity, int ringtoneManagerType, Uri uri) {
        Preferences preferences = new Preferences(activity);

        switch (ringtoneManagerType) {
            case RingtoneManager.TYPE_RINGTONE:
                preferences.setDefaultRingtoneRingtonePath(uri.toString());
                break;
            case RingtoneManager.TYPE_ALARM:
                preferences.setDefaultAlarmRingtonePath(uri.toString());
                break;
            case RingtoneManager.TYPE_NOTIFICATION:
                preferences.setDefaultNotificationRingtonePath(uri.toString());
                break;
            default:
                // do nothing
                break;
        }
    }

    // get volume
    public static int getRingtoneVolume (Context context) {
        return getAudioStreamVolume(context, AudioManager.STREAM_RING);
    }

    public static int getAlarmVolume (Context context) {
        return getAudioStreamVolume(context, AudioManager.STREAM_ALARM);
    }

    public static int getSystemVolume (Context context) {
        return getAudioStreamVolume(context, AudioManager.STREAM_SYSTEM);
    }

    public static int getVoiceVolume (Context context) {
        return getAudioStreamVolume(context, AudioManager.STREAM_VOICE_CALL);
    }

    // volume range 0/10
    private static int getAudioStreamVolume (Context context, int audioStreamType) {
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        float volumeMax = audioManager.getStreamMaxVolume(audioStreamType);
        float volume = audioManager.getStreamVolume(audioStreamType);
        return (int) (volume / volumeMax * VOLUME_RANGE_DELTA) + VOLUME_RANGE_LOWER;
    }

    // set volume
    public static void incrementRingtoneVolume (Context context, int volume) {
        incrementAudioStreamVolume(context, AudioManager.STREAM_RING, volume);
    }

    public static void incrementAlarmVolume (Context context, int volume) {
        incrementAudioStreamVolume(context, AudioManager.STREAM_ALARM, volume);
    }

    public static void incrementSystemVolume (Context context, int volume) {
        incrementAudioStreamVolume(context, AudioManager.STREAM_SYSTEM, volume);
    }

    public static void incrementVoiceVolume (Context context, int volume) {
        incrementAudioStreamVolume(context, AudioManager.STREAM_VOICE_CALL, volume);
    }

    // volume range 0/10
    private static void incrementAudioStreamVolume (Context context, int audioStreamType, int increment) {
        // make sure volume is in the range
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        int volumeMax = audioManager.getStreamMaxVolume(audioStreamType);
        int volumeCurrent = audioManager.getStreamVolume(audioStreamType);
        int nextVolume = volumeCurrent + increment;

        if (nextVolume >= volumeMax && increment > 0) {
            nextVolume = volumeMax;
        } else if (volumeCurrent <= 0 && increment < 0) {
            nextVolume = 0;
        }

        audioManager.setStreamVolume(audioStreamType, nextVolume,0);
    }

    // silent mode
    public static boolean getIsSilentMode (Context context) {
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        return audioManager.getRingerMode() == AudioManager.RINGER_MODE_SILENT;
    }

    public static void setIsSilentMode (Context context, boolean isSilentMode) {
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        audioManager.setRingerMode(isSilentMode ? AudioManager.RINGER_MODE_SILENT : AudioManager.RINGER_MODE_NORMAL);
    }
}
