package computer.fuji.al0.controllers;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.telecom.Call;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;

import computer.fuji.al0.activities.CallActivity;
import computer.fuji.al0.models.Contact;
import computer.fuji.al0.models.NumpadButtonModel;
import computer.fuji.al0.services.ContactsService;
import computer.fuji.al0.services.InCallService;
import computer.fuji.al0.utils.Network;
import computer.fuji.al0.utils.PhoneNumber;
import computer.fuji.al0.utils.Preferences;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class CallActivityController implements InCallService.InCallServiceEventsListener {
    final CallActivity activity;
    private Timer callTimer;
    private boolean isCallMuted = false;
    private boolean isCallSpeaker = false;
    private String dialNumber = "";
    private Contact contact;
    private Preferences preferences;

    TelephonyManager telephonyManager;

    private BroadcastReceiver batteryManagerReceiver = new BroadcastReceiver(){
        @Override
        public void onReceive(Context ctxt, Intent intent) {
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
            int chargingStatus = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
            boolean isCharging = chargingStatus == BatteryManager.BATTERY_STATUS_CHARGING;
            activity.updateStatusBattery(level, isCharging);
        }
    };

    private PhoneStateListener phoneStateListener = new PhoneStateListener() {
        public void onSignalStrengthsChanged (SignalStrength signalStrength) {
            String networkClass = Network.getNetworkClass(activity);
            int networkStrength = signalStrength.getLevel();
            activity.updateStatusSignal(networkStrength, networkClass);
        }
    };

    public CallActivityController (CallActivity callActivity) {
        this.activity = callActivity;

        InCallService.setInCallServiceEventsListener(this);
        // get current call from InCallService
        Call currentCall = InCallService.getCurrentCall();
        setContactConnectingLabel();
        /*
        // get current call phone number
        dialNumber = InCallService.getCurrentPhoneNumber();
        contact = ContactsService.getContactFromPhoneNumber(activity, dialNumber);
        // update activity UI number
        if (contact != null) {
            activity.updateLabelConnectingNumber(contact.getName());
        } else {
            activity.updateLabelConnectingNumber(PhoneNumber.formatPhoneNumber(dialNumber));
        }
         */

        preferences = new Preferences(activity);
        // disable lock screen
        preferences.setShouldShowLockScreen(false);

        // update activity Inbound / Outbound call state
        if (InCallService.getIsInboundCall()) {
            activity.showCallInRingingState();
        } else {
            activity.showCallInDialingState();
        }

        // register Call callback for changing state
        if (currentCall != null) {
            onCallStateCallback(currentCall, currentCall.getState(), activity);
            currentCall.registerCallback(new Call.Callback() {
                @Override
                public void onStateChanged(Call call, int state) {
                    super.onStateChanged(call, state);
                    onCallStateCallback(call, state, activity);
                }
            });
        }

        // listen for Bluetooth devices
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        activity.registerReceiver(bluetoothReceiver, filter);

        activity.updateMuteState(InCallService.getMute());
        activity.updateSpeakerState(InCallService.getSpeaker());
    }

    private void setContactConnectingLabel () {
        // get current call phone number
        dialNumber = InCallService.getCurrentPhoneNumber();
        contact = ContactsService.getContactFromPhoneNumber(activity, dialNumber);
        // update activity UI number
        if (contact != null) {
            activity.updateLabelConnectingNumber(contact.getName());
        } else {
            activity.updateLabelConnectingNumber(PhoneNumber.formatPhoneNumber(dialNumber));
        }
    }

    // check call states here:
    // https://developer.android.com/reference/android/telecom/Call#STATE_ACTIVE
    private void onCallStateCallback (Call call, int state, CallActivity activity) {
        switch (state) {
            case Call.STATE_RINGING:
                activity.showCallInRingingState();
                break;
            case Call.STATE_DIALING:
                activity.showCallInDialingState();
                break;
            case Call.STATE_CONNECTING:
                // to do
                // activity.showCallInConnectingState()
                break;
            case Call.STATE_ACTIVE:
                onCallIsActive();
                break;
            case Call.STATE_HOLDING:
                // to do
                // activity.showCallInHoldState();
                break;
        }
    }

    private final BroadcastReceiver bluetoothReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                //Device found
            }
            else if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
                //Device is now connected
                activity.showBluetoothButton(true);
                activity.setBluetoothButtonActive(true);
            }
            else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                //Done searching
            }
            else if (BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED.equals(action)) {
                //Device is about to disconnect
            }
            else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
                //Device has disconnected
                activity.setBluetoothButtonActive(false);
                activity.showBluetoothButton(false);
            }
        }
    };


    // start call timer
    // update UI call duration
    private void startCallTimer () {
        activity.updateCallTime(InCallService.getCurrentCallDuration());

        callTimer = new Timer ();
        callTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        activity.updateCallTime(InCallService.getCurrentCallDuration());
                    }
                });
            }
        }, 1000, 1000);

    }

    // stop call timer
    private void stopCallTimer () {
        if (callTimer != null) {
            callTimer.cancel();
        }
    }

    // events
    // called when users can talk
    private void onCallIsActive () {
        InCallService.setCurrentCallStartTime(new Date());
        dialNumber = InCallService.getCurrentPhoneNumber();
        boolean isInboundCall = InCallService.getIsInboundCall();
        contact = ContactsService.getContactFromPhoneNumber(activity, dialNumber);

        // update activity UI number
        if (contact != null) {
            activity.showCallInCallState(contact.getName(), isInboundCall);
        } else {
            activity.showCallInCallState(PhoneNumber.formatPhoneNumber(dialNumber), isInboundCall);
        }

        // show bluetooh button when is using bluetooth
        activity.showBluetoothButton(InCallService.getHasBluetooth());
        activity.setBluetoothButtonActive(InCallService.getHasBluetooth());
        // start call timer
        startCallTimer();
    }

    // called when call is removed
    @Override
    public void onCallRemoved(Call call) {
        stopCallTimer();
        InCallService.setInCallServiceEventsListener(null);
        activity.finish();
    }

    // UI buttons
    public void onAnswerPress () {
        InCallService.answer();
    }

    public void onHangupPress () {
        Call currentCall = InCallService.getCurrentCall();

        if (currentCall != null) {
            InCallService.hangup();
        } else {
            // force close call activity
            onCallRemoved(currentCall);
        }
    }

    public void onCollapsePress () {
        activity.finish();
    }

    public void onNumpadButtonPress (NumpadButtonModel numpadButtonModel) {
        Call currentCall = InCallService.getCurrentCall();
        dialNumber = dialNumber.concat(numpadButtonModel.getNumber());
        activity.updateLabelConnectingNumber(dialNumber);
    }

    public void onNumpadButtonTouchStart (NumpadButtonModel numpadButtonModel) {
        Call currentCall = InCallService.getCurrentCall();
        Character dtmfTone = numpadButtonModel.getNumber().charAt(0);
        currentCall.playDtmfTone(dtmfTone);
    }

    public void onNumpadButtonTouchEnd (NumpadButtonModel numpadButtonModel, boolean isTailTouchEvent) {
        if (isTailTouchEvent) {
            Call currentCall = InCallService.getCurrentCall();
            currentCall.stopDtmfTone();
        }
    }

    // toggle mute state
    public void onMutePress () {
        isCallMuted = !isCallMuted;
        InCallService.setMute(isCallMuted);
        activity.updateMuteState(isCallMuted);
    }

    public void onSilentPress() {
        InCallService.setSilent();
    }

    public void onSpeakerPress () {
        isCallSpeaker = !isCallSpeaker;
        InCallService.setSpeaker(isCallSpeaker);
        activity.updateSpeakerState(isCallSpeaker);
    }

    public void onBluetoothPress () {
        InCallService.setUseBluetooth(true);
        activity.setBluetoothButtonActive(true);
    }

    public void onNumpadMenuButtonPress () {
        setContactConnectingLabel();
        activity.toggleNumpad();
    }

    public void onActivityResume () {
        // register battery events
        activity.registerReceiver(batteryManagerReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        // register signal events
        telephonyManager = (TelephonyManager) activity.getSystemService(Context.TELEPHONY_SERVICE);
        telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
    }

    public void onActivityPause () {
        stopCallTimer();
        // unregister battery manager receiver receivers
        try {
            activity.unregisterReceiver(batteryManagerReceiver);
        } catch(IllegalArgumentException e) {
            // receiver not registered
        }
        // unregister phone state listener
        telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE);
    }

    public void onDestroy() {
        InCallService.onCallFinish();
        // enable lock screen
        preferences.setShouldShowLockScreen(true);
    }
}
