package computer.fuji.al0.controllers;

import android.app.ActivityManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.os.BatteryManager;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import computer.fuji.al0.MainActivity;
import computer.fuji.al0.R;
import computer.fuji.al0.activities.CalculatorActivity;
import computer.fuji.al0.activities.CalendarActivity;
import computer.fuji.al0.activities.CallActivity;
import computer.fuji.al0.activities.CameraActivity;
import computer.fuji.al0.activities.ClockActivity;
import computer.fuji.al0.activities.OnboardingActivity;
import computer.fuji.al0.activities.PhoneActivity;
import computer.fuji.al0.activities.SettingsActivity;
import computer.fuji.al0.components.Button;
import computer.fuji.al0.components.Dialog;
import computer.fuji.al0.models.Call;
import computer.fuji.al0.models.Contact;
import computer.fuji.al0.models.ListItem;
import computer.fuji.al0.models.Sms;
import computer.fuji.al0.services.ContactsService;
import computer.fuji.al0.services.FlashLightService;
import computer.fuji.al0.services.InCallService;
import computer.fuji.al0.services.PermissionsService;
import computer.fuji.al0.services.AL0NotificationService;
import computer.fuji.al0.utils.CurrentCall;
import computer.fuji.al0.utils.Network;
import computer.fuji.al0.utils.PhoneNumber;
import computer.fuji.al0.utils.Preferences;
import computer.fuji.al0.utils.StringUtils;
import computer.fuji.al0.utils.Time;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import static computer.fuji.al0.constants.MainActivityMainMenu.*;

public class MainActivityController implements PermissionsService.PermissionCheckedListener {
    MainActivity activity;
    private ArrayList<ListItem> menuListItems;
    private PermissionsService permissionsService;
    private Preferences preferences;
    private boolean shouldStartLockTaskOnFocus = false;
    private boolean startPhoneAfterOfferingDefaultDialerAndMessagingAppReplace = false;
    private boolean hasLocationPermission = false;
    private Timer timeTimer;
    private Timer callTimer;
    // signal strength variables
    private TelephonyManager telephonyManager;
    private String networkClass = "";
    private int networkStrength = 0;

    private InCallService.InCallServiceEventsListener inCallServiceEventsListener;

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
            networkClass = Network.getNetworkClass(activity);
            networkStrength = signalStrength.getLevel();
            activity.updateStatusSignal(networkStrength, networkClass);
        }
    };

    public MainActivityController (MainActivity activity) {
        this.activity = activity;

        preferences = new Preferences(activity);
        menuListItems = new ArrayList<>();
        populateMenuListItem();
        permissionsService = new PermissionsService(activity, this);
        boolean isFirstRun = preferences.getIsFirstRun();
        boolean isKeepInLockMode = preferences.getIsFocusMode();

        if (isFirstRun) {
            preferences.setFocusMode(false);
            startOnboardingActivity();
        }

        if (isKeepInLockMode) {
            activity.startLockTask();
        }
    }

    // fired on activity's windowFocusChangedEvent
    // check if user selected keepInLock mode
    // when app is in focus
    // user wants keepInLockMode
    // if activity is not already locked or pinned start lock mode
    public void onActivityWindowFocusChanged (boolean hasFocus) {
        boolean isFocusMode = preferences.getIsFocusMode();

        if (hasFocus && shouldStartLockTaskOnFocus) {
            shouldStartLockTaskOnFocus = false;

            if (isFocusMode) {
                ActivityManager activityManager = (ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);
                boolean isLockMode = activityManager.getLockTaskModeState() == ActivityManager.LOCK_TASK_MODE_LOCKED;
                boolean isPinned = isLockMode = activityManager.getLockTaskModeState() == ActivityManager.LOCK_TASK_MODE_PINNED;

                if (!isLockMode || !isPinned) {
                    try {
                        activity.startLockTask();
                    } catch (Exception exception) {
                        // error
                    }
                }
            }
        }

        if (hasFocus) {
            // trigger update status signal to correctly show airplane mode when needed
            activity.updateStatusSignal(networkStrength, networkClass);

            // check if should show CallActivity
            if (InCallService.getCurrentCall() != null) {
                // start call activity if current call state is valid
                switch (InCallService.getCurrentCall().getState()) {
                    case android.telecom.Call.STATE_RINGING:
                    case android.telecom.Call.STATE_DIALING:
                    case android.telecom.Call.STATE_CONNECTING:
                    case android.telecom.Call.STATE_ACTIVE:
                    case android.telecom.Call.STATE_HOLDING:
                        registerInCallServiceCallback();
                        setCallMenuListItemVisible(true);
                        updateCallMenuListItemName();
                        startCallTimer();
                        break;
                    default:
                        // hide call menu list item
                        setCallMenuListItemVisible(false);
                        break;
                }
            } else {
                // hide call menu list item
                setCallMenuListItemVisible(false);
            }

            // update menu list items in case hidden tools list changed
            if (preferences != null && preferences.getToolsHiddenChanged()) {
                populateMenuListItem();
                activity.menuListItemsChanged();
                preferences.setToolsHiddenChanged(false);
            }

            checkExternalAppsNotifications();
        } else {
            // make sure to delete inCallServiceEventsListener
            stopCallTimer();
            inCallServiceEventsListener = null;
        }
    }

    public void onActivityResume () {
        // check if there are notifications
        if (AL0NotificationService.getLastPhoneNotificationIsSeen()) {
            setMenuItemIsMarked(MENU_PHONE, false);
        } else {
            setMenuItemIsMarked(MENU_PHONE, true);
        }

        // check if there are external apps notifications
        checkExternalAppsNotifications();

        // init listener for PhoneActivity notifications
        initPhoneActivityNotificationListener();

        // start clock timer
        startTimeTimer();

        // register battery events
        activity.registerReceiver(batteryManagerReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        // register signal evens
        telephonyManager = (TelephonyManager) activity.getSystemService(Context.TELEPHONY_SERVICE);
        telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
    }

    public void onActivityPause () {
        // unregister battery manager receiver receivers
        try {
            activity.unregisterReceiver(batteryManagerReceiver);
        } catch(IllegalArgumentException e) {
            // receiver not registered
        }
        // unregister phone state listener
        telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE);
        // stop clock timer
        stopTimeTimer();
        stopCallTimer();
    }

    // populate menuListItem ArrayList
    private void populateMenuListItem () {
        menuListItems.clear();
        menuListItems.addAll(new ArrayList<ListItem>(Arrays.asList(
                new ListItem(MENU_PHONE, activity.getString(R.string.main_activity_menu_list_phone), false),
                new ListItem(MENU_CALENDAR, activity.getString(R.string.main_activity_menu_list_calendar), false),
                new ListItem(MENU_CLOCK, activity.getString(R.string.main_activity_menu_list_clock), false),
                new ListItem(MENU_CAMERA, activity.getString(R.string.main_activity_menu_list_camera), false),
                new ListItem(MENU_TORCH, activity.getString(R.string.main_activity_menu_list_torch), false),
                /*
                new ListItem(MENU_HOTSPOT, activity.getString(R.string.main_activity_menu_list_hotspot), false),
                 */
                new ListItem(MENU_CALCULATOR, activity.getString(R.string.main_activity_menu_list_calculator), false),
                new ListItem(MENU_SETTINGS, activity.getString(R.string.main_activity_menu_list_settings), false)
        )));

        // remove torch if not available on device
        if (!FlashLightService.getIsFlashLightAvailable(activity)) {
            for (int i = 0; i < menuListItems.size(); i++) {
                if (menuListItems.get(i).getId().equals(MENU_TORCH)) {
                    menuListItems.remove(i);
                }
            }
        }

        // remove hidden tools
        if (preferences != null) {
            String[] hiddenTools = preferences.getToolsHidden();
            for (String hiddenTool : hiddenTools) {
                for (int i = 0; i < menuListItems.size(); i++) {
                    if (menuListItems.get(i).getId().equals(hiddenTool)) {
                        menuListItems.remove(i);
                    }
                }
            }
        }

        // add external apps
        boolean shouldShowExternalApps = !preferences.getIsFocusMode();
        // check if should add external apps
        if (shouldShowExternalApps) {
            String externalApp1 = preferences.getExternalApp1();
            String externalApp2 = preferences.getExternalApp2();
            String externalApp3 = preferences.getExternalApp3();
            // add existing added external apps
            if (externalApp1 != null) {
                ListItem externalApp1Item = new ListItem(externalApp1, preferences.getExternalApp1Name(), false);
                externalApp1Item.setRightText(activity.getString(R.string.external_link_symbol));
                menuListItems.add(menuListItems.size() -1, externalApp1Item);
            }

            if (externalApp2 != null) {
                ListItem externalApp2Item = new ListItem(externalApp2, preferences.getExternalApp2Name(), false);
                externalApp2Item.setRightText(activity.getString(R.string.external_link_symbol));
                menuListItems.add(menuListItems.size() -1, externalApp2Item);
            }

            if (externalApp3 != null) {
                ListItem externalApp3Item = new ListItem(externalApp3, preferences.getExternalApp3Name(), false);
                externalApp3Item.setRightText(activity.getString(R.string.external_link_symbol));
                menuListItems.add(menuListItems.size() -1, externalApp3Item);
            }
        }

        Collections.reverse(menuListItems);
    }

    private void updateTorchItemIsActive (boolean isActive) {
        for (int i = 0; i < menuListItems.size(); i++) {
            ListItem currentItem = menuListItems.get(i);
            if (currentItem.getId().equals(MENU_TORCH)) {
                currentItem.setIsActive(isActive);
                activity.updateMenuListItem(currentItem, i);
            }
        }
    }

    // update clock time, and calendar day
    private void updateItemsTime () {
        Date now = new Date();
        for (int i = 0; i < menuListItems.size(); i++) {
            ListItem currentItem = menuListItems.get(i);
            if (currentItem.getId().equals(MENU_CLOCK)) {
                currentItem.setRightText(Time.dateToHoursAndMinutes(now));
                activity.updateMenuListItem(currentItem, i);
            } else if (currentItem.getId().equals(MENU_CALENDAR)) {
                currentItem.setRightText(StringUtils.capitalizeAll(Time.EEEdMMMFormat.format(now)));
                activity.updateMenuListItem(currentItem, i);
            } else {
                // do nothing
            }
        }
    }

    // start timer to update clock and calendar
    private void startTimeTimer() {
        updateItemsTime();
        int currentSecond = Integer.parseInt(Time.sFormat.format(new Date()));
        int secondToNextMinute = 59 - currentSecond;

        timeTimer = new Timer ();
        timeTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateItemsTime();
                    }
                });
            }
        }, secondToNextMinute, Time.MIN_IN_MS);

    }

    // stop call timer
    private void stopTimeTimer() {
        if (timeTimer != null) {
            timeTimer.cancel();
        }
    }

    // getters
    public ArrayList<ListItem> getMenuListItems () {
        return menuListItems;
    }

    // events
    public void onActivityAskPermissionResult (int requestCode, int resultCode, @Nullable Intent data) {
        permissionsService.onActivityAskPermissionResult(requestCode, resultCode, data);
    }

    public void onActivityRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsService.onActivityRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void onMenuItemClick (ListItem menuItem) {
        switch (menuItem.getId()) {
            case MENU_PHONE:
                startPhoneAfterOfferingDefaultDialerAndMessagingAppReplace = true;
                // check phone is default dialer app, sms app and REQUEST_CODE_ALL_PHONE_SMS_CONTACTS is granted
                // open PhoneActivity if all checks pass
                // permissionsService.askPhoneSmsContactsPermissions();
                permissionsService.offerReplacingDefaultDialer();
                break;
            case MENU_CALENDAR:
                startCalendarActivity();
                break;
            case MENU_CLOCK:
                startClockActivity();
                break;
            case MENU_CAMERA:
                permissionsService.askPhoneCameraRecordAudioPermissions();
                // startCameraActivity();
                break;
            case MENU_TORCH:
                permissionsService.askPhoneFlashLightPermissions();
                break;
            case MENU_HOTSPOT:
                // do nothing
                break;
            case MENU_CALCULATOR:
                startCalculatorActivity();
                break;
            case MENU_SETTINGS:
                startSettingsActivity();
                break;
            case MENU_CURRENT_CALL:
                startCallActivity();
                break;
            default:
                String menuItemId = menuItem.getId();
                if (isMenuItemExternalApp(menuItemId)) {
                    startExternalActivity(menuItemId);
                } else {
                    // do nothing
                }
                break;
        }
    }

    private boolean isMenuItemExternalApp (String menuItemId) {
        String externalApp1 = preferences.getExternalApp1();
        String externalApp2 = preferences.getExternalApp2();
        String externalApp3 = preferences.getExternalApp3();
        if (menuItemId == externalApp1 || menuItemId == externalApp2 || menuItemId == externalApp3 ) {
            return true;
        } else {
            return false;
        }
    }

    // check permission response to handle navigation between intro steps, or to open feature needing specific permissions
    // Intro Step 2 -> ask permission Access Notifications -> yes -> on success / on failure open Intro Step 3
    // Intro Step 3 -> ask permission Default Dealer app -> yes -> on success / on failure ->
    //              -> ask permission Default messaging app -> on success /on failure open Intro Step 4
    @Override
    public void onPermissionCheckSuccess(int requestCode) {
        switch (requestCode) {
            // PhoneActivity - Check 1
            case PermissionsService.REQUEST_CODE_SET_DEFAULT_DIALER:
                // check for Phone, Sms, Contacts permissions before starting phone activity
                permissionsService.offerReplacingDefaultMessagingApp();
                break;
            // PhoneActivity - Check 2
            case PermissionsService.REQUEST_CODE_SET_DEFAULT_MESSAGING_APP:
                permissionsService.askPhoneSmsContactsPermissions();
                break;

            // PhoneActivity - Check 3, if all previous ok can start phoneActivity
            case PermissionsService.REQUEST_CODE_ALL_PHONE_SMS_CONTACTS:
                if (startPhoneAfterOfferingDefaultDialerAndMessagingAppReplace) {
                    startPhoneActivity();
                }
                break;
            case PermissionsService.REQUEST_CODE_NOTIFICATION_PERMISSIONS:
                // do nothing
                break;
            case PermissionsService.REQUEST_CODE_ALL_CAMERA_RECORD_AUDIO:
                startCameraActivity();
                break;
            case PermissionsService.REQUEST_CODE_ALL_FLASH_LIGHT:
                try {
                    FlashLightService.toggleFlash(activity);
                    updateTorchItemIsActive(FlashLightService.getIsFlashLightOn());
                } catch (CameraAccessException e) {
                    e.printStackTrace();
                }

        }
    }

    @Override
    public void onPermissionCheckFailure(int requestCode, boolean isNeverAskAgain) {
        switch (requestCode) {
            // PhoneActivity - Check 1
            case PermissionsService.REQUEST_CODE_SET_DEFAULT_DIALER:
                // after asking for replacing the default dealer app, should ask to replace the default messaging app
                if (isNeverAskAgain) {
                    showOpenDefaultAppSettingsDialog();
                }

                break;
            // PhoneActivity - Check 2
            case PermissionsService.REQUEST_CODE_SET_DEFAULT_MESSAGING_APP:
                if (isNeverAskAgain) {
                    showOpenDefaultAppSettingsDialog();
                }
                break;
            // PhoneActivity - Check 3
            case PermissionsService.REQUEST_CODE_ALL_PHONE_SMS_CONTACTS:
                // check if user selected "Never ask again" to show a custom dialog panel to invite change permissions
                if (isNeverAskAgain) {
                    showOpenAppSettingsDialogForPhone();
                }
                break;
            case PermissionsService.REQUEST_CODE_NOTIFICATION_PERMISSIONS:
                // do nothing
                break;
            case PermissionsService.REQUEST_CODE_ALL_CAMERA_RECORD_AUDIO:
                if (isNeverAskAgain) {
                    showOpenAppSettingsDialogForCamera();
                }
                break;
            case PermissionsService.REQUEST_CODE_ALL_FLASH_LIGHT:
                if (isNeverAskAgain) {
                    showOpenAppSettingsDialogForFlash();
                }
                break;
        }
    }

    // show dialog asking to set the app the default app
    private void showOpenDefaultAppSettingsDialog () {
        // build and show the dialog
        final Dialog dialog = activity.addGenericDialog();
        // build dialog body, check if need phone and massging default app settings
        String dialogBody =
                activity.getResources().getString(R.string.dialog_default_phone_and_messaging_app_permissions_body)
                .concat(permissionsService.getIsTheDefaultDialerApp() ? "" : activity.getResources().getString(R.string.dialog_default_phone_and_messaging_app_permissions_body_dialer))
                .concat(permissionsService.getIsTheDefaultMessagingApp() ? "" : activity.getResources().getString(R.string.dialog_default_phone_and_messaging_app_permissions_body_messaging_app));
        dialog.setText(
                activity.getResources().getString(R.string.dialog_default_phone_and_messaging_app_permissions_title),
                dialogBody,
                activity.getResources().getString(R.string.dialog_all_phone_sms_contacts_permissions_cancel_button),
                activity.getResources().getString(R.string.dialog_all_phone_sms_contacts_permissions_ok_button));



        Button okButton = dialog.getDialogButtonActionRight();
        Button cancelButton = dialog.getDialogButtonActionLeft();

        // on click ok
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.removeGenericDialog(dialog);
                // stop lock task to emable open settings
                activity.stopLockTask();
                // set flag to make sure to start lock on next app focus
                shouldStartLockTaskOnFocus = true;
                // open permissions settings
                permissionsService.openDefaultAppSettings();
            }
        });

        // on click cancel
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.removeGenericDialog(dialog);
            }
        });
    }


    private void showOpenAppSettingsDialogForPhone () {
        String dialogTitle = activity.getResources().getString(R.string.dialog_all_phone_sms_contacts_permissions_title);

        // get not granted permissions
        ArrayList<String> notGrantedPhoneSmsContactsPermissions = permissionsService.getNotGrantedPhoneSmsContactsPermissions();
        boolean showPhonePermissionString = permissionsService.containsAPhonePermission(notGrantedPhoneSmsContactsPermissions);
        boolean showSmsPermissionString = permissionsService.containsASmsPermission(notGrantedPhoneSmsContactsPermissions);
        boolean showContactsPermissionString = permissionsService.containsAContactPermission(notGrantedPhoneSmsContactsPermissions);
        boolean showCallLogsPermissionString = permissionsService.containsACallLogsPermission(notGrantedPhoneSmsContactsPermissions);
        String dialogBody = activity.getResources().getString(R.string.dialog_all_phone_sms_contacts_permissions_body)
                + (showPhonePermissionString ? activity.getResources().getString(R.string.dialog_all_phone_sms_contacts_permissions_body_permission_phone) : "")
                + (showSmsPermissionString ? activity.getResources().getString(R.string.dialog_all_phone_sms_contacts_permissions_body_permission_sms) : "")
                + (showContactsPermissionString ? activity.getResources().getString(R.string.dialog_all_phone_sms_contacts_permissions_body_permission_contacts) : "")
                + (showCallLogsPermissionString ? activity.getResources().getString(R.string.dialog_all_phone_sms_contacts_permissions_body_permission_call_logs) : "");

        showOpenAppSettingsDialog(dialogTitle, dialogBody);
    }

    private void showOpenAppSettingsDialogForCamera () {
        String dialogTitle = activity.getResources().getString(R.string.dialog_all_camera_permissions_title);
        // get not granted permissions
        ArrayList<String> notGrantedCameraPermissions = permissionsService.getNotGrantedCameraRecordAudioPermissions();
        boolean showCameraPermissionString = permissionsService.containsACameraPermission(notGrantedCameraPermissions);
        boolean showRecordAudioPermissionString = permissionsService.containsAAudioRecordPermission(notGrantedCameraPermissions);
        boolean showStoragePermissionString = permissionsService.containsAStoragePermission(notGrantedCameraPermissions);

        String dialogBody = activity.getResources().getString(R.string.dialog_all_camera_permissions_body)
                + (showCameraPermissionString ? activity.getResources().getString(R.string.dialog_all_camera_permissions_body_permission_camera) : "")
                + (showRecordAudioPermissionString ? activity.getResources().getString(R.string.dialog_all_camera_permissions_body_permission_microphone) : "")
                + (showStoragePermissionString ? activity.getResources().getString(R.string.dialog_all_storage_permissions_body_permission_storage) : "");

        showOpenAppSettingsDialog(dialogTitle, dialogBody);
    }

    private void showOpenAppSettingsDialogForFlash () {
        String dialogTitle = activity.getResources().getString(R.string.dialog_all_flash_light_permissions_title);
        // get not granted permissions
        ArrayList<String> notGrantedFlashLightPermissions = permissionsService.getNotGrantedFlashLightPermissions();
        boolean showCameraPermissionString = permissionsService.containsACameraPermission(notGrantedFlashLightPermissions);

        String dialogBody = activity.getResources().getString(R.string.dialog_all_flash_light_permissions_body)
                + (showCameraPermissionString ? activity.getResources().getString(R.string.dialog_all_flash_light_permissions_body_permission_camera) : "");

        showOpenAppSettingsDialog(dialogTitle, dialogBody);
    }
    // show dialog asking to grant phone, sms, contacts permissions
    // get not granted permissions from permissionService
    // detect if are Phone, Sms or Contacts permissions related
    // build the dialog body informing the user which permissions are needed
    private void showOpenAppSettingsDialog (String title, String body) {
        // build and show the dialog
        final Dialog dialog = activity.addGenericDialog();
        dialog.setText(
                title,
                body,
                activity.getResources().getString(R.string.dialog_all_phone_sms_contacts_permissions_cancel_button),
                activity.getResources().getString(R.string.dialog_all_phone_sms_contacts_permissions_ok_button));



        Button okButton = dialog.getDialogButtonActionRight();
        Button cancelButton = dialog.getDialogButtonActionLeft();

        // on click ok
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.removeGenericDialog(dialog);
                // stop lock task to emable open settings
                activity.stopLockTask();
                // set flag to make sure to start lock on next app focus
                shouldStartLockTaskOnFocus = true;
                // open permissions settings
                permissionsService.openPermissionsSettings();
            }
        });

        // on click cancel
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.removeGenericDialog(dialog);
            }
        });
    }

    private void setMenuItemIsMarked (String menuItemId, boolean isMarked) {
        for (int i = 0; i < menuListItems.size(); i++) {
            ListItem menuItem = menuListItems.get(i);
            if (menuItem.getId() == menuItemId) {
                menuItem.setIsMarked(isMarked);
            }

            activity.updateMenuListItem(menuItem, i);
        }
    }

    private void checkExternalAppsNotifications () {
        boolean isFocusMode = preferences.getIsFocusMode();
        boolean isStandardAppsNotificationDisabled = preferences.getSoundSettingsStandardAppsNoNotificationMark();
        // when not in focus mode check if any notification from external app need to be shown
        if (!isFocusMode && !isStandardAppsNotificationDisabled) {
            // reset standard apps notification state
            for (ListItem menuListItem : menuListItems) {
                String menuListItemId = menuListItem.getId();
                if (isMenuItemExternalApp(menuListItemId)) {
                    setMenuItemIsMarked(menuListItemId, false);
                }
            }

            // add standard apps missed notifications state
            for (String appId : AL0NotificationService.getLastExternalAppsNotificationsNotSeen(activity)) {
                for (ListItem menuListItem : menuListItems) {
                    String menuListItemId = menuListItem.getId();
                    if (menuListItemId.equals(appId)) {
                        setMenuItemIsMarked(menuListItemId, true);
                    }
                }
            }
        }
    }

    private void initPhoneActivityNotificationListener () {
        AL0NotificationService.setNotificationListener(new AL0NotificationService.NotificationListener() {
            @Override
            public void onSmsReceived(Sms sms) {
                setMenuItemIsMarked(MENU_PHONE, true);
            }

            @Override
            public void onMissedCallReceived(Call call) {
                // missed call notification come from a different thread
                new Thread() {
                    @Override
                    public void run () {
                        // update activity list UI
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                setMenuItemIsMarked(MENU_PHONE, true);
                            }
                        });
                    }
                }.start();
            }

            @Override
            public void onExternalAppNotification(final String appId) {
                boolean isFocusMode = preferences.getIsFocusMode();
                boolean isStandardAppsNotificationDisabled = preferences.getSoundSettingsStandardAppsNoNotificationMark();
                // notification from external app
                // check is not focus mode
                if (!isFocusMode && !isStandardAppsNotificationDisabled) {
                    new Thread() {
                        @Override
                        public void run () {
                            // update activity list UI
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    for (ListItem menuItem : menuListItems) {
                                        if (menuItem.getId().equals(appId)) {
                                            setMenuItemIsMarked(menuItem.getId(), true);
                                            return;
                                        }
                                    }
                                }
                            });
                        }
                    }.start();
                }
            }
        });
    }

    // activities
    private void startOnboardingActivity () {
        Intent onboardingActivityIntent = new Intent(activity, OnboardingActivity.class);
        activity.startActivity(onboardingActivityIntent);
    }

    private void startPhoneActivity () {
        Intent phoneActivityIntent = new Intent(activity, PhoneActivity.class);
        activity.startActivity(phoneActivityIntent);
    }

    private void startCalendarActivity () {
        Intent calendarActivityIntent = new Intent(activity, CalendarActivity.class);
        activity.startActivity(calendarActivityIntent);
    }

    private void startClockActivity () {
        Intent clockActivityIntent = new Intent(activity, ClockActivity.class);
        activity.startActivity(clockActivityIntent);
    }

    private void startCameraActivity () {
        // make sure flash is off before starting camera
        if (FlashLightService.getIsFlashLightOn()) {
            try {
                FlashLightService.toggleFlash(activity);
                updateTorchItemIsActive(false);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }

        Intent cameraActivityIntent = new Intent(activity, CameraActivity.class);
        activity.startActivity(cameraActivityIntent);
    }

    private void startCalculatorActivity () {
        Intent calculatorActivityIntent = new Intent(activity, CalculatorActivity.class);
        activity.startActivity(calculatorActivityIntent);
    }

    private void startSettingsActivity () {
        Intent settingsActivityIntent = new Intent(activity, SettingsActivity.class);
        activity.startActivity(settingsActivityIntent);
    }

    private boolean isCurrentCallListItemVisible () {
        // check if call menu list item is present
        for (ListItem menuItem : menuListItems) {
            if (menuItem.getId().equals(MENU_CURRENT_CALL)) {
                return true;
            }
        }
        return false;
    }

    private void registerInCallServiceCallback () {
        inCallServiceEventsListener = new InCallService.InCallServiceEventsListener() {
            @Override
            public void onCallRemoved(android.telecom.Call call) {
                setCallMenuListItemVisible(false);
                stopCallTimer();
            }
        };

        InCallService.setInCallServiceEventsListener(inCallServiceEventsListener);
    }

    private String getCurrentCallContactName () {
        // get dial number
        String dialNumber = InCallService.getCurrentPhoneNumber();
        // get contact from number
        Contact contact = ContactsService.getContactFromPhoneNumber(activity, dialNumber);
        // show contact name or phone number in list item
        return contact != null ? contact.getName() : PhoneNumber.formatPhoneNumber(dialNumber);
    }

    private void setCallMenuListItemVisible (boolean isVisible) {
        if (isVisible) {
            if (!isCurrentCallListItemVisible()) {

                // create current call ListItem
                ListItem currentCallListItem = new ListItem(MENU_CURRENT_CALL, getCurrentCallContactName(), false);
                currentCallListItem.setCustomMark(activity.getString(InCallService.getIsInboundCall() ? R.string.call_inbound_symbol : R.string.call_outbound_symbol));
                currentCallListItem.setIsMarked(true);

                menuListItems.add(currentCallListItem);
                // update view
                activity.menuListItemsChanged();
            } else {
                // do nothing
            }
        } else {
            // remove current call from menu list if present
            if (isCurrentCallListItemVisible()) {
                for (int i = 0; i < menuListItems.size(); i++) {
                    if (menuListItems.get(i).getId().equals(MENU_CURRENT_CALL)) {
                        menuListItems.remove(i);
                        activity.menuListItemsChanged();
                        return;
                    }
                }
            } else {
                // do nothing
            }
        }
    }

    private void updateCallMenuListItemName () {
        for (ListItem menuItem : menuListItems) {
            if (menuItem.getId().equals(MENU_CURRENT_CALL)) {
                menuItem.setText(getCurrentCallContactName());
            }
        }
    }

    private void startCallTimer () {
        updateCurrentCallTime();
        callTimer = new Timer ();
        callTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateCurrentCallTime();
                    }
                });
            }
        }, 1000, 1000);
    }

    private void stopCallTimer () {
        if (callTimer != null) {
            callTimer.cancel();
        }
    }

    private void updateCurrentCallTime () {
        for (int i = 0; i < menuListItems.size(); i++) {
            ListItem currentItem = menuListItems.get(i);
            if (currentItem.getId().equals(MENU_CURRENT_CALL)) {
                currentItem.setRightText(Time.secondsToHMS(InCallService.getCurrentCallDuration()));
                activity.updateMenuListItem(currentItem, i);
            } else {
                // do nothing
            }
        }
    }

    private void startCallActivity () {
        Intent callActivityIntent = new Intent(activity, CallActivity.class);
        activity.startActivity(callActivityIntent);
    }

    private void startExternalActivity (String activityId) {
        PackageManager pm = activity.getPackageManager();

        try {
            Intent intent = pm.getLaunchIntentForPackage(activityId);

            if (intent != null) {
                activity.startActivity(intent);
                setMenuItemIsMarked(activityId, false);
                AL0NotificationService.setLastExternalAppsNotificationsIsSeen(activityId, activity);
            }
        } catch (ActivityNotFoundException e)  {
            // do nothing
        }
    }
}
