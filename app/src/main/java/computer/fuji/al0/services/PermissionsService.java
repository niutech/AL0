package computer.fuji.al0.services;

import android.Manifest;
import android.app.Activity;
import android.app.role.RoleManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.provider.Telephony;
import android.telecom.TelecomManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.ArrayList;

import static android.Manifest.permission.CALL_PHONE;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_CALL_LOG;
import static android.Manifest.permission.READ_CONTACTS;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.READ_SMS;
import static android.Manifest.permission.RECEIVE_SMS;
import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.SEND_SMS;
import static android.Manifest.permission.WRITE_CALL_LOG;
import static android.Manifest.permission.WRITE_CONTACTS;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class PermissionsService {
    Activity activity;

    public static final int REQUEST_APP_SETTINGS = 22;
    public static final int NEVER_ASK_AGAIN_PERMISSION = 23;
    public static final int REQUEST_CODE_SET_DEFAULT_DIALER = 24;
    public static final int REQUEST_CODE_SET_DEFAULT_MESSAGING_APP = 25;
    public static final int REQUEST_CODE_ALL_PHONE_SMS_CONTACTS = 26;
    public static final int REQUEST_CODE_NOTIFICATION_PERMISSIONS = 27;
    public static final int REQUEST_CODE_ALL_CAMERA_RECORD_AUDIO = 28;
    public static final int REQUEST_CODE_ALL_FLASH_LIGHT = 29;
    public static final int REQUEST_CODE_ALL_STORAGE = 30;

    private static String [] phoneDefaultDialerPermissions_VERSION_CODES_EQUAL_GREATER_THAN_Q = {
            RoleManager.ROLE_DIALER
    };

    private static String [] phoneDefaultDialerPermissions_VERSION_CODES_LOWER_THEN_Q = {
            TelecomManager.ACTION_CHANGE_DEFAULT_DIALER
    };

    private static String [] phoneDefaultDialerPermissions = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q ? phoneDefaultDialerPermissions_VERSION_CODES_EQUAL_GREATER_THAN_Q : phoneDefaultDialerPermissions_VERSION_CODES_LOWER_THEN_Q;

    private static String [] phoneDefaultMessagingAppPermissions_VERSION_CODES_EQUAL_GREATER_THAN_Q = {
            RoleManager.ROLE_SMS
    };

    private static String [] phoneDefaultMessagingAppPermissions_VERSION_CODES_LOWER_THEN_Q = {
            Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT
    };

    private static String [] phoneDefaultMessagingAppPermissions = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q ? phoneDefaultMessagingAppPermissions_VERSION_CODES_EQUAL_GREATER_THAN_Q : phoneDefaultMessagingAppPermissions_VERSION_CODES_LOWER_THEN_Q;

    /*
    private static String [] phoneDefaultMessagingAppPermissions = {
            Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT
    };

     */

    private static String [] phoneNotificationListenerPermissions = {
            Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS
    };

    private static String [] phoneSmsContactsPermissions = {
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.CALL_PHONE,

            Manifest.permission.READ_SMS,
            Manifest.permission.SEND_SMS,
            Manifest.permission.RECEIVE_SMS,

            Manifest.permission.READ_CONTACTS,
            Manifest.permission.WRITE_CONTACTS,

            Manifest.permission.READ_CALL_LOG,
            Manifest.permission.WRITE_CALL_LOG
    };

    private static String [] phoneCameraRecordAudioPermissions = {
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private static String [] phoneFlashLightPermissions = {
            Manifest.permission.CAMERA
    };

    private static String [] phoneStoragePermissions = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    PermissionsService.PermissionCheckedListener permissionCheckedListener;

    public interface PermissionCheckedListener {
        public void onPermissionCheckSuccess(int requestCode);
        public void onPermissionCheckFailure(int requestCode, boolean isNeverAskAgain);
    }

    public PermissionsService (Activity activity, PermissionCheckedListener permissionCheckedListener) {
        this.activity = activity;
        this.permissionCheckedListener = permissionCheckedListener;

    }

    // check for user response on permission request
    public void onActivityAskPermissionResult (int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_SET_DEFAULT_DIALER:
                checkOfferReplacingDefaultDialerResult(resultCode);
                break;
            case REQUEST_CODE_SET_DEFAULT_MESSAGING_APP:
                checkOfferReplacingDefaultMessagingAppResult(resultCode);
                break;
            case REQUEST_CODE_ALL_PHONE_SMS_CONTACTS:
                checkAskPhoneSmsContactsPermissionsResult(resultCode);
                break;
            case REQUEST_CODE_NOTIFICATION_PERMISSIONS:
                checkAskPhoneNotificationListenerPermissionsResult(resultCode);
                break;
            case REQUEST_CODE_ALL_CAMERA_RECORD_AUDIO:
                checkAskPhoneCameraMicrophoneRecordPermissionsResult(resultCode);
                break;
            case REQUEST_CODE_ALL_FLASH_LIGHT:
                checkAskPhoneFlashLightPermissionsResult(resultCode);
                break;
            case REQUEST_CODE_ALL_STORAGE:
                checkAskPhoneStoragePermissionsResult(resultCode);
                break;
        }
    }

    public void onActivityRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length > 0) {
            int RESULT = RESULT_OK;

            for (int grantResult : grantResults) {
              if (RESULT == RESULT_CANCELED || grantResult != PackageManager.PERMISSION_GRANTED) {
                  RESULT = RESULT_CANCELED;
              }
            }

            switch (requestCode) {
                case REQUEST_CODE_SET_DEFAULT_DIALER:
                    checkOfferReplacingDefaultDialerResult(RESULT);
                    break;
                case REQUEST_CODE_SET_DEFAULT_MESSAGING_APP:
                    checkOfferReplacingDefaultMessagingAppResult(RESULT);
                    break;
                case REQUEST_CODE_ALL_PHONE_SMS_CONTACTS:
                    checkAskPhoneSmsContactsPermissionsResult(RESULT);
                    break;
                case REQUEST_CODE_NOTIFICATION_PERMISSIONS:
                    checkAskPhoneNotificationListenerPermissionsResult(RESULT);
                    break;
                case REQUEST_CODE_ALL_CAMERA_RECORD_AUDIO:
                    checkAskPhoneCameraMicrophoneRecordPermissionsResult(RESULT);
                    break;
                case REQUEST_CODE_ALL_FLASH_LIGHT:
                    checkAskPhoneFlashLightPermissionsResult(RESULT);
                    break;
                case REQUEST_CODE_ALL_STORAGE:
                    checkAskPhoneStoragePermissionsResult(RESULT);
                    break;
            }
        }
    }

    /*
     * Dialer permissions
     */

    public boolean getIsTheDefaultDialerApp () {
        TelecomManager telecomManager = (TelecomManager) activity.getSystemService(activity.TELECOM_SERVICE);
        return telecomManager.getDefaultDialerPackage().equals(activity.getPackageName());
    }

    // offer to become the default app dialer
    public void offerReplacingDefaultDialer () {
        if (!getIsTheDefaultDialerApp()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                RoleManager roleManager = (RoleManager) activity.getSystemService(Context.ROLE_SERVICE);
                Intent intent = roleManager.createRequestRoleIntent(RoleManager.ROLE_DIALER);
                activity.startActivityForResult(intent, REQUEST_CODE_SET_DEFAULT_DIALER);
            } else {
                Intent changeDefaultDialerIntent = new Intent();
                changeDefaultDialerIntent.setAction(TelecomManager.ACTION_CHANGE_DEFAULT_DIALER);
                changeDefaultDialerIntent.putExtra(TelecomManager.EXTRA_CHANGE_DEFAULT_DIALER_PACKAGE_NAME, activity.getPackageName());
                activity.startActivityForResult(changeDefaultDialerIntent, REQUEST_CODE_SET_DEFAULT_DIALER);
            }
        } else {
            if (permissionCheckedListener != null) {
                permissionCheckedListener.onPermissionCheckSuccess(REQUEST_CODE_SET_DEFAULT_DIALER);
            }
        }
    }

    // check if user approved to replace the default dialer app
    private void checkOfferReplacingDefaultDialerResult(int resultCode) {
        switch (resultCode) {
            case RESULT_OK:
                if (permissionCheckedListener != null) {
                    permissionCheckedListener.onPermissionCheckSuccess(REQUEST_CODE_SET_DEFAULT_DIALER);
                }
                break;
            case RESULT_CANCELED:
                if (permissionCheckedListener != null) {
                    // permissionCheckedListener.onPermissionCheckFailure(REQUEST_CODE_SET_DEFAULT_DIALER, false);

                    // check if user check never ask again
                    // check if is a never asked again permission in a try / catch
                    // to handle older android version where phone default dialer permission is handled in a different way
                    try {
                        boolean hasNeverAskedAgainPermission = hasNeverAskedAgainPermission(activity, phoneDefaultDialerPermissions);
                        if (hasNeverAskedAgainPermission) {
                            permissionCheckedListener.onPermissionCheckFailure(REQUEST_CODE_SET_DEFAULT_DIALER, true);
                        } else {
                            permissionCheckedListener.onPermissionCheckFailure(REQUEST_CODE_SET_DEFAULT_DIALER, false);
                        }
                    } catch (Exception e) {
                        // ignore error
                        permissionCheckedListener.onPermissionCheckFailure(REQUEST_CODE_SET_DEFAULT_DIALER, false);
                    }
                }
                break;
            case NEVER_ASK_AGAIN_PERMISSION:
                if (permissionCheckedListener != null) {
                    permissionCheckedListener.onPermissionCheckFailure(REQUEST_CODE_SET_DEFAULT_DIALER, true);
                }
                break;
            default:
                // do nothing
                break;
        }
    }

    /*
     * Default Messaging app
     */
    public boolean getIsTheDefaultMessagingApp () {
        return Telephony.Sms.getDefaultSmsPackage(activity).equals(activity.getPackageName());
    }

    public void offerReplacingDefaultMessagingApp () {
        if(!getIsTheDefaultMessagingApp()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                RoleManager roleManager = (RoleManager) activity.getSystemService(Context.ROLE_SERVICE);
                Intent intent = roleManager.createRequestRoleIntent(RoleManager.ROLE_SMS);
                activity.startActivityForResult(intent, REQUEST_CODE_SET_DEFAULT_MESSAGING_APP);
            } else {
                Intent changeDefaultSmsAppIntent = new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
                changeDefaultSmsAppIntent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, activity.getPackageName());
                activity.startActivityForResult(changeDefaultSmsAppIntent, REQUEST_CODE_SET_DEFAULT_MESSAGING_APP);
            }
        } else {
            if (permissionCheckedListener != null) {
                permissionCheckedListener.onPermissionCheckSuccess(REQUEST_CODE_SET_DEFAULT_MESSAGING_APP);
            }
        }
    }

    // check if user approved to replace the default messaging app
    private void checkOfferReplacingDefaultMessagingAppResult(int resultCode) {
        switch (resultCode) {
            case RESULT_OK:
                if (permissionCheckedListener != null) {
                    permissionCheckedListener.onPermissionCheckSuccess(REQUEST_CODE_SET_DEFAULT_MESSAGING_APP);
                }
                break;
            case RESULT_CANCELED:
                if (permissionCheckedListener != null) {
                    // permissionCheckedListener.onPermissionCheckFailure(REQUEST_CODE_SET_DEFAULT_MESSAGING_APP, false);
                    // check if is a never asked again permission in a try / catch
                    // to handle older android version where phone default sms permission is handled in a different way
                    try {
                        boolean hasNeverAskedAgainPermission = hasNeverAskedAgainPermission(activity, phoneDefaultMessagingAppPermissions);
                        if (hasNeverAskedAgainPermission) {
                            permissionCheckedListener.onPermissionCheckFailure(REQUEST_CODE_SET_DEFAULT_MESSAGING_APP, true);
                        } else {
                            permissionCheckedListener.onPermissionCheckFailure(REQUEST_CODE_SET_DEFAULT_MESSAGING_APP, false);
                        }
                    } catch (Exception e) {
                        // ignore error
                        permissionCheckedListener.onPermissionCheckFailure(REQUEST_CODE_SET_DEFAULT_MESSAGING_APP, false);
                    }
                }
                break;
            case NEVER_ASK_AGAIN_PERMISSION:
                if (permissionCheckedListener != null) {
                    permissionCheckedListener.onPermissionCheckFailure(REQUEST_CODE_SET_DEFAULT_MESSAGING_APP, true);
                }
            default:
                // do nothing
                break;
        }
    }

    /*
     * Phone Notification Listener permissions
     */
    public void askPhoneNotificationListenerPermissions () {
        if (!NotificationManagerCompat.getEnabledListenerPackages(activity.getApplicationContext())
                .contains(activity.getApplicationContext().getPackageName())) {
            Intent grantNotificationListenerIntent = new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS);
            activity.startActivityForResult(grantNotificationListenerIntent, REQUEST_CODE_NOTIFICATION_PERMISSIONS);
        } else {
            if (permissionCheckedListener != null) {
                permissionCheckedListener.onPermissionCheckSuccess(REQUEST_CODE_NOTIFICATION_PERMISSIONS);
            }
        }
    }

    // check if user approved to gran notification listener permissions
    private void checkAskPhoneNotificationListenerPermissionsResult(int resultCode) {
        switch (resultCode) {
            case RESULT_OK:
                if (permissionCheckedListener != null) {
                    permissionCheckedListener.onPermissionCheckSuccess(REQUEST_CODE_NOTIFICATION_PERMISSIONS);
                }
                break;
            case RESULT_CANCELED:
                if (permissionCheckedListener != null) {
                    permissionCheckedListener.onPermissionCheckFailure(REQUEST_CODE_NOTIFICATION_PERMISSIONS, false);
                }
                break;
            case NEVER_ASK_AGAIN_PERMISSION:
                if (permissionCheckedListener != null) {
                    permissionCheckedListener.onPermissionCheckFailure(REQUEST_CODE_NOTIFICATION_PERMISSIONS, true);
                }
            default:
                // do nothing
                break;
        }
    }

    /*
     * Camera, microphone permissions
     */

    // ask all camera permissions
    public void askPhoneCameraRecordAudioPermissions () {
        if (!hasPermissions(activity, phoneCameraRecordAudioPermissions)) {
            if (hasNeverAskedAgainPermission(activity, phoneCameraRecordAudioPermissions)) {
                checkAskPhoneCameraMicrophoneRecordPermissionsResult(NEVER_ASK_AGAIN_PERMISSION);
            } else {
                ActivityCompat.requestPermissions(activity, phoneCameraRecordAudioPermissions, REQUEST_CODE_ALL_CAMERA_RECORD_AUDIO);
            }

        } else {
            checkAskPhoneCameraMicrophoneRecordPermissionsResult(RESULT_OK);
        }
    }

    // get not granted CAMERA AUDIO RECORD
    public ArrayList<String> getNotGrantedCameraRecordAudioPermissions () {
        ArrayList<String> notGrantedPermissions = new ArrayList<String>();
        for (String permission: phoneCameraRecordAudioPermissions) {
            if (!hasPermissions(activity, permission)) {
                notGrantedPermissions.add(permission);
            }
        }

        return notGrantedPermissions;
    }

    private void checkAskPhoneCameraMicrophoneRecordPermissionsResult (int resultCode) {
        switch (resultCode) {
            case RESULT_OK:
                if (permissionCheckedListener != null) {
                    permissionCheckedListener.onPermissionCheckSuccess(REQUEST_CODE_ALL_CAMERA_RECORD_AUDIO);
                }
                break;
            case RESULT_CANCELED:
                if (permissionCheckedListener != null) {
                    permissionCheckedListener.onPermissionCheckFailure(REQUEST_CODE_ALL_CAMERA_RECORD_AUDIO, false);
                }
                break;

            case NEVER_ASK_AGAIN_PERMISSION:
                if (permissionCheckedListener != null) {
                    permissionCheckedListener.onPermissionCheckFailure(REQUEST_CODE_ALL_CAMERA_RECORD_AUDIO, true);
                }
                break;
            default:
                // do nothing
                break;
        }
    }

    /*
     * FlashLigh permissions
     */

    public void askPhoneFlashLightPermissions () {
        if (!hasPermissions(activity, phoneCameraRecordAudioPermissions)) {
            if (hasNeverAskedAgainPermission(activity, phoneFlashLightPermissions)) {
                checkAskPhoneFlashLightPermissionsResult(NEVER_ASK_AGAIN_PERMISSION);
            } else {
                ActivityCompat.requestPermissions(activity, phoneFlashLightPermissions, REQUEST_CODE_ALL_FLASH_LIGHT);
            }

        } else {
            checkAskPhoneFlashLightPermissionsResult(RESULT_OK);
        }
    }

    // get not granted CAMERA AUDIO RECORD
    public ArrayList<String> getNotGrantedFlashLightPermissions () {
        ArrayList<String> notGrantedPermissions = new ArrayList<String>();
        for (String permission: phoneFlashLightPermissions) {
            if (!hasPermissions(activity, permission)) {
                notGrantedPermissions.add(permission);
            }
        }

        return notGrantedPermissions;
    }

    private void checkAskPhoneFlashLightPermissionsResult (int resultCode) {
        switch (resultCode) {
            case RESULT_OK:
                if (permissionCheckedListener != null) {
                    permissionCheckedListener.onPermissionCheckSuccess(REQUEST_CODE_ALL_FLASH_LIGHT);
                }
                break;
            case RESULT_CANCELED:
                if (permissionCheckedListener != null) {
                    permissionCheckedListener.onPermissionCheckFailure(REQUEST_CODE_ALL_FLASH_LIGHT, false);
                }
                break;

            case NEVER_ASK_AGAIN_PERMISSION:
                if (permissionCheckedListener != null) {
                    permissionCheckedListener.onPermissionCheckFailure(REQUEST_CODE_ALL_FLASH_LIGHT, true);
                }
                break;
            default:
                // do nothing
                break;
        }
    }

    /*
     * Phone call, Sms, Contacts permissions
     */

    // ask all CALL_PHONE, Sms, Contacts permissions
    public void askPhoneSmsContactsPermissions () {

        if (!hasPermissions(activity, phoneSmsContactsPermissions)) {
            if (hasNeverAskedAgainPermission(activity, phoneSmsContactsPermissions)) {
                checkAskPhoneSmsContactsPermissionsResult(NEVER_ASK_AGAIN_PERMISSION);
            } else {
                ActivityCompat.requestPermissions(activity, phoneSmsContactsPermissions, REQUEST_CODE_ALL_PHONE_SMS_CONTACTS);
            }

        } else {
            checkAskPhoneSmsContactsPermissionsResult(RESULT_OK);
        }
    }

    // get not granted PHONE SMS CONTACTS
    public ArrayList<String> getNotGrantedPhoneSmsContactsPermissions () {
        ArrayList<String> notGrantedPermissions = new ArrayList<String>();
        for (String permission: phoneSmsContactsPermissions) {
            if (!hasPermissions(activity, permission)) {
                notGrantedPermissions.add(permission);
            }
        }

        return notGrantedPermissions;
    }

    private void checkAskPhoneSmsContactsPermissionsResult(int resultCode) {
        switch (resultCode) {
            case RESULT_OK:
                if (permissionCheckedListener != null) {
                    permissionCheckedListener.onPermissionCheckSuccess(REQUEST_CODE_ALL_PHONE_SMS_CONTACTS);
                }
                break;
            case RESULT_CANCELED:
                if (permissionCheckedListener != null) {
                    permissionCheckedListener.onPermissionCheckFailure(REQUEST_CODE_ALL_PHONE_SMS_CONTACTS, false);
                }
                break;

            case NEVER_ASK_AGAIN_PERMISSION:
                if (permissionCheckedListener != null) {
                    permissionCheckedListener.onPermissionCheckFailure(REQUEST_CODE_ALL_PHONE_SMS_CONTACTS, true);
                }
                break;
            default:
                // do nothing
                break;
        }
    }

    /*
     * Storage permissions
     */

    // ask all camera permissions
    public void askPhoneStoragePermissions () {
        if (!hasPermissions(activity, phoneStoragePermissions)) {
            if (hasNeverAskedAgainPermission(activity, phoneStoragePermissions)) {
                checkAskPhoneStoragePermissionsResult(NEVER_ASK_AGAIN_PERMISSION);
            } else {
                ActivityCompat.requestPermissions(activity, phoneStoragePermissions, REQUEST_CODE_ALL_STORAGE);
            }

        } else {
            checkAskPhoneStoragePermissionsResult(RESULT_OK);
        }
    }

    // get not granted STORAGE permissions
    public ArrayList<String> getNotGrantedStoragePermissions () {
        ArrayList<String> notGrantedPermissions = new ArrayList<String>();
        for (String permission: phoneStoragePermissions) {
            if (!hasPermissions(activity, permission)) {
                notGrantedPermissions.add(permission);
            }
        }

        return notGrantedPermissions;
    }

    private void checkAskPhoneStoragePermissionsResult (int resultCode) {
        switch (resultCode) {
            case RESULT_OK:
                if (permissionCheckedListener != null) {
                    permissionCheckedListener.onPermissionCheckSuccess(REQUEST_CODE_ALL_STORAGE);
                }
                break;
            case RESULT_CANCELED:
                if (permissionCheckedListener != null) {
                    permissionCheckedListener.onPermissionCheckFailure(REQUEST_CODE_ALL_STORAGE, false);
                }
                break;

            case NEVER_ASK_AGAIN_PERMISSION:
                if (permissionCheckedListener != null) {
                    permissionCheckedListener.onPermissionCheckFailure(REQUEST_CODE_ALL_STORAGE, true);
                }
                break;
            default:
                // do nothing
                break;
        }
    }

    // open settings
    public void openDefaultAppSettings () {
        activity.stopLockTask();
        Intent appSettings = new Intent(Settings.ACTION_MANAGE_DEFAULT_APPS_SETTINGS);
        activity.startActivity(appSettings);
    }

    public void openPermissionsSettings() {
        activity.stopLockTask();
        Intent appSettings = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + activity.getPackageName()));
        appSettings.addCategory(Intent.CATEGORY_DEFAULT);
        appSettings.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivityForResult(appSettings, REQUEST_APP_SETTINGS);
    }

    // get from https://stackoverflow.com/a/34343101
    private static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    // given a list of permission check if contain:

    // Phone permissions related
    public boolean containsAPhonePermission (ArrayList<String> permissions) {
        return permissions.indexOf(READ_PHONE_STATE) > -1 || permissions.indexOf(CALL_PHONE) > -1;
    }

    // SMS permissions related
    public boolean containsASmsPermission (ArrayList<String> permissions) {
        return permissions.indexOf(READ_SMS) > -1 || permissions.indexOf(SEND_SMS) > -1 || permissions.indexOf(RECEIVE_SMS) > -1;
    }

    // Contact permissions related
    public boolean containsAContactPermission (ArrayList<String> permissions) {
        return permissions.indexOf(READ_CONTACTS) > -1 || permissions.indexOf(WRITE_CONTACTS) > -1;
    }

    // Call Logs permissions related
    public boolean containsACallLogsPermission (ArrayList<String> permissions) {
        return permissions.indexOf(READ_CALL_LOG) > -1 || permissions.indexOf(WRITE_CALL_LOG) > -1;
    }

    // Camera permissions related
    public boolean containsACameraPermission (ArrayList<String> permissions) {
        return permissions.indexOf(CAMERA) > -1;
    }

    public boolean containsAAudioRecordPermission (ArrayList<String> permissions) {
        return permissions.indexOf(RECORD_AUDIO) > -1;
    }

    public boolean containsAStoragePermission (ArrayList<String> permissions) {
        return permissions.indexOf(READ_EXTERNAL_STORAGE) > -1 || permissions.indexOf(WRITE_EXTERNAL_STORAGE) > -1;
    }

    // check if user checked "never ask again"
    // first check if the the permission is not granted
    // then check for falsy shouldShowRequestPermissionRationale
    private static boolean hasNeverAskedAgainPermission(Activity activity, String... permissions) {
        if (activity != null && permissions != null) {
            for (String permission : permissions) {
                boolean hasNeverAskAgain = !ActivityCompat.shouldShowRequestPermissionRationale(activity, permission);
                boolean doesntHavePermissions = !hasPermissions(activity, permission);
                if(hasNeverAskAgain && doesntHavePermissions) {
                    return true;
                }
            }
        }
        return false;
    }
}
