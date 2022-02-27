package computer.fuji.al0.controllers;

import android.content.Intent;
import android.os.Bundle;

import computer.fuji.al0.activities.PhoneActivity;
import computer.fuji.al0.activities.PhoneContactActivity;
import computer.fuji.al0.models.Call;
import computer.fuji.al0.models.Sms;
import computer.fuji.al0.services.OutCallService;
import computer.fuji.al0.services.AL0NotificationService;

public class PhoneActivityController {
    private PhoneActivity activity;

    public PhoneActivityController (PhoneActivity activity) {
        this.activity = activity;

        activity.setCurrentTab(PhoneActivity.TabId.NUMPAD);
    }

    // events
    // on footer numpad tab button press set numpad current visible fragment
    public void onCloseButtonPress () {
        activity.finish();
    }

    public void onNumpadButtonPress () {
        activity.setCurrentTab(PhoneActivity.TabId.NUMPAD);
    }

    // on footer contacts tab button press set contacts current visible fragment
    public void onContactsButtonPress () {
        activity.setCurrentTab(PhoneActivity.TabId.CONTACTS);
    }

    public void onActivityResume () {
        // check if there are PhoneActivity notifications
        if (activity.getCurrentTabId() != PhoneActivity.TabId.ACTIVITY) {
            if (AL0NotificationService.getLastPhoneNotificationIsSeen()) {
                activity.showNotificationOnActivityTab(false);
            } else {
                activity.showNotificationOnActivityTab(true);
            }
        }

        // init listener for PhoneActivity notifications
        initPhoneActivityNotificationListener();
    }

    // on footer activity tab button press set acticity current visible fragment
    public void onActivityButtonPress () {
        activity.showNotificationOnActivityTab(false);
        activity.setCurrentTab(PhoneActivity.TabId.ACTIVITY);
    }

    private void initPhoneActivityNotificationListener () {
        AL0NotificationService.setNotificationListener(new AL0NotificationService.NotificationListener() {
            @Override
            public void onSmsReceived(Sms sms) {
                if (activity.getCurrentTabId() != PhoneActivity.TabId.ACTIVITY) {
                    activity.showNotificationOnActivityTab(true);
                }

            }

            @Override
            public void onMissedCallReceived(Call call) {
                // missed call notification come from a different thread
                if (activity.getCurrentTabId() != PhoneActivity.TabId.ACTIVITY) {
                    new Thread() {
                        @Override
                        public void run () {
                            // update activity list UI
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    activity.showNotificationOnActivityTab(true);
                                }
                            });
                        }
                    }.start();
                }
            }

            @Override
            public void onExternalAppNotification(String appId) {
                // do nothing
            }
        });
    }

    // numpad fragment events
    // on numpad SMS button press
    public void onNumpadSmsButtonPress (String number) {
        Intent intent = new Intent(activity, PhoneContactActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(PhoneContactActivity.CONTACT_NUMBER_BUNDLE_KEY, number);
        bundle.putBoolean(PhoneContactActivity.START_IN_SMS_MODE_BUNDLE_KEY, true);
        intent.putExtras(bundle);
        activity.startActivity(intent);
    }

    // on numpad CALL button press
    public void onNumpadCallButtonPress (String number) {
        OutCallService.addCall(activity, number);
    }
}
