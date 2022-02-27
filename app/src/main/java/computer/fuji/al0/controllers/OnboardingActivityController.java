package computer.fuji.al0.controllers;

import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import computer.fuji.al0.activities.OnboardingActivity;
import computer.fuji.al0.services.PermissionsService;
import computer.fuji.al0.utils.Preferences;

public class OnboardingActivityController implements PermissionsService.PermissionCheckedListener {
    private OnboardingActivity activity;
    private PermissionsService permissionsService;
    private Preferences preferences;

    public OnboardingActivityController (OnboardingActivity activity) {
        this.activity = activity;
        permissionsService = new PermissionsService(activity, this);
        preferences = new Preferences(activity);
        activity.showIntroDialogStep1();
    }

    public void onIntroStep1ContinuePress () {
        activity.showIntroDialogStep2();
    }

    public void onIntroStep2YesPress () {
        permissionsService.askPhoneNotificationListenerPermissions();
    }

    public void onIntroStep2NoPress () {
        activity.showIntroDialogStep3();
    }

    // on step 3 offer replacing default dealer
    // then on result offer to become the default messaging app
    public void onIntroStep3YesPress () {
        permissionsService.offerReplacingDefaultDialer();
    }

    public void onIntroStep3NoPress () {
        activity.showIntroDialogStep4();
    }

    public void onIntroStep4YesPress () {
        preferences.setFocusMode(true);
        activity.startLockTask();
        activity.finish();
    }

    public void onIntroStep4NoPress () {
        preferences.setFocusMode(false);
        activity.stopLockTask();
        activity.finish();
    }

    // check permission response to handle navigation between intro steps
    // Intro Step 2 -> ask permission Access Notifications -> yes -> on success / on failure open Intro Step 3
    // Intro Step 3 -> ask permission Default Dealer app -> yes -> on success / on failure ->
    //              -> ask permission Default messaging app -> on success /on failure finish onboarding
    @Override
    public void onPermissionCheckSuccess(int requestCode) {
        switch (requestCode) {
            // answer to notification persmissions
            case PermissionsService.REQUEST_CODE_NOTIFICATION_PERMISSIONS:
                activity.showIntroDialogStep3();
                break;
            // answer to default dialer app
            case PermissionsService.REQUEST_CODE_SET_DEFAULT_DIALER:
                // check for Phone, Sms, Contacts permissions before starting phone activity
                // activity.removeIntroDialog();
                permissionsService.offerReplacingDefaultMessagingApp();
                break;
            // answer to default SMS app
            case PermissionsService.REQUEST_CODE_SET_DEFAULT_MESSAGING_APP:
                activity.showIntroDialogStep4();
                break;
        }
    }

    @Override
    public void onPermissionCheckFailure(int requestCode, boolean isNeverAskAgain) {
        switch (requestCode) {
            // answer to notification persmissions
            case PermissionsService.REQUEST_CODE_NOTIFICATION_PERMISSIONS:
                activity.showIntroDialogStep3();
                break;
            // answer to default dialer app
            case PermissionsService.REQUEST_CODE_SET_DEFAULT_DIALER:
                // after asking for replacing the default dealer app, should ask to replace the default messaging app
                if (isNeverAskAgain) {
                    permissionsService.openDefaultAppSettings();
                } else {
                    // do nothing
                }

                break;
            // answer to default SMS app
            case PermissionsService.REQUEST_CODE_SET_DEFAULT_MESSAGING_APP:
                if (isNeverAskAgain) {
                    permissionsService.openDefaultAppSettings();
                } else {
                    // do nothing
                }

                break;
        }
    }

    // events
    public void onActivityAskPermissionResult (int requestCode, int resultCode, @Nullable Intent data) {
        permissionsService.onActivityAskPermissionResult(requestCode, resultCode, data);
    }

    public void onActivityRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsService.onActivityRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
