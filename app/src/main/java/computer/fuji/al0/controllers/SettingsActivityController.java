package computer.fuji.al0.controllers;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.view.View;

import androidx.appcompat.app.AppCompatDelegate;

import computer.fuji.al0.R;
import computer.fuji.al0.activities.AboutPhoneActivity;
import computer.fuji.al0.activities.FilesExplorerActivity;
import computer.fuji.al0.activities.SettingsActivity;
import computer.fuji.al0.activities.SettingsToolsActivity;
import computer.fuji.al0.components.Button;
import computer.fuji.al0.components.Dialog;
import computer.fuji.al0.models.ListItem;
import computer.fuji.al0.models.SoundModel;
import computer.fuji.al0.services.NotificationListenerService;
import computer.fuji.al0.services.PermissionsService;
import computer.fuji.al0.services.SoundManager;
import computer.fuji.al0.utils.Preferences;

import java.util.ArrayList;
import java.util.Arrays;

import static computer.fuji.al0.constants.SettingsActivityMainMenu.*;

public class SettingsActivityController implements PermissionsService.PermissionCheckedListener {
    SettingsActivity activity;
    private ArrayList<ListItem> menuListItems;
    private ArrayList<ListItem> soundMenuListItems;
    private ArrayList<ListItem> soundPhoneVolumeMenuListItems;
    private ArrayList<ListItem> soundRingtoneMenuListItems;
    private ArrayList<ListItem> soundAlarmMenuListItems;
    private ArrayList<ListItem> soundNotificationsMenuListItems;
    private ArrayList<ListItem> cameraMenuListItems;
    private ArrayList<ListItem> currentListItems;
    private PermissionsService permissionsService;
    private Preferences preferences;
    private boolean shouldStartLockTaskOnFocus = false;
    private ListItem selectedListItem;
    private boolean isAndroidMarshmellow = Build.VERSION.SDK_INT <= 23;
    private boolean isAndroidOreoOrPie = Build.VERSION.SDK_INT <= 28 && Build.VERSION.SDK_INT >= 26;
    private boolean shouldUseExternalRingtonePicker = isAndroidMarshmellow || isAndroidOreoOrPie;

    private enum CurrentMenu {
        MAIN_MENU,
        CAMERA_MENU,
        SOUND_MENU, SOUND_RINGTONE_MENU, SOUND_ALARM_MENU, SOUND_NOTIFICATIONS_MENU, SOUND_VOLUME_MENU
    };
    private CurrentMenu currentMenu;

    public SettingsActivityController (SettingsActivity activity) {
        this.activity = activity;
        preferences = new Preferences(activity);
        permissionsService = new PermissionsService(activity, this);
        populateMenuListItems();
        populateCameraMenuListItems();
        currentMenu = CurrentMenu.MAIN_MENU;
        currentListItems = menuListItems;
    }

    public void updateListState () {
        switch (currentMenu) {
            case MAIN_MENU:
                // updateMenuItemIsActive(MENU_KEEP_LOCK, preferences.getKeepInLockMode());
                activity.setButtonFocusModeActive(preferences.getIsFocusMode());
                updateMenuItemIsActive(MENU_DARK_MODE, preferences.getNightModePreferences() == AppCompatDelegate.MODE_NIGHT_YES);
                break;
            case SOUND_MENU:
                updateMenuItemIsActive(MENU_SOUND_SILENT_MODE, SoundManager.getIsSilentMode(activity));
                updateMenuItemIsActive(MENU_SOUND_VIBRATION, preferences.getSoundSettingsVibrationEnabled());
                updateMenuItemIsActive(MENU_SOUND_STANDARD_APPS_SILENT, preferences.getSoundSettingsStandardAppsSilent());
                updateMenuItemIsActive(MENU_SOUND_STANDARD_APPS_NO_NOTIFICATION_MARK, preferences.getSoundSettingsStandardAppsNoNotificationMark());
                break;
            case SOUND_RINGTONE_MENU:
                SoundModel currentRingtone = SoundManager.getDefaultRingtone(activity);
                updateMenuItemIsActive(currentRingtone.getUri().toString(), true);
                break;
            case SOUND_ALARM_MENU:
                SoundModel currentAlarm = SoundManager.getDefaultAlarm(activity);
                updateMenuItemIsActive(currentAlarm.getUri().toString(), true);
                break;
            case SOUND_NOTIFICATIONS_MENU:
                SoundModel currentNotification = SoundManager.getDefaultNotification(activity);
                updateMenuItemIsActive(currentNotification.getUri().toString(), true);
                break;
            case CAMERA_MENU:
                updateMenuItemIsActive(MENU_CAMERA_COLOR, preferences.getSettingsCameraColor());
                // updateMenuItemIsActive(MENU_CAMERA_FRONT_CAMERA, preferences.getSettingsCameraFrontCamera());
                updateMenuItemIsActive(MENU_CAMERA_NO_FLASH, preferences.getSettingsCameraNoFlash());
                break;
            default:
                // do nothing
                break;
        }
    }

    // populate menuListItem ArrayList
    private void populateMenuListItems () {
        ListItem hotspotListItem = new ListItem(MENU_HOTSPOT, activity.getString(R.string.settings_activity_menu_hotspot), false);
        ListItem allSettingsListItem = new ListItem(MENU_ALL_SETTINGS, activity.getString(R.string.settings_activity_menu_all_settings), false);
        hotspotListItem.setRightText(activity.getString(R.string.external_link_symbol));
        allSettingsListItem.setRightText(activity.getString(R.string.external_link_symbol));
        boolean isFocusMode = preferences.getIsFocusMode();
        String toolsSettingsTitle = isFocusMode ? activity.getString(R.string.settings_activity_menu_settings_tools_focus) : activity.getString(R.string.settings_activity_menu_settings_tools);
        menuListItems = new ArrayList<ListItem>(Arrays.asList(
                new ListItem(MENU_ABOUT_PHONE, activity.getString(R.string.settings_activity_menu_about_phone), false),
                new ListItem(MENU_SOUND, activity.getString(R.string.settings_activity_menu_sound), false),
                new ListItem(MENU_CAMERA, activity.getString(R.string.settings_activity_menu_camera), false),
                new ListItem(MENU_SETTINGS_TOOLS, toolsSettingsTitle, false),
                new ListItem(MENU_DARK_MODE, activity.getString(R.string.settings_activity_menu_dark_mode), false)
        ));

        // add link to external activities when not in focus mode
        if (!isFocusMode) {
            menuListItems.add(menuListItems.size() - 1, allSettingsListItem);
            menuListItems.add(menuListItems.size() - 1, hotspotListItem);
        }
    }

    private void populateSoundMenuListItems () {
        ListItem ringtoneListItem = new ListItem(MENU_SOUND_RINGTONE, activity.getString(R.string.settings_activity_menu_sound_ringtone), false);
        // mark ringtone as external menu in android 23 or Oreo/Pie
        if (shouldUseExternalRingtonePicker) {
            ringtoneListItem.setRightText(activity.getString(R.string.external_link_symbol));
        }

        soundMenuListItems = new ArrayList<ListItem>(Arrays.asList(
                new ListItem(MENU_SOUND_PHONE_VOLUME, activity.getString(R.string.settings_activity_menu_sound_phone_volume), false),
                ringtoneListItem,
                new ListItem(MENU_SOUND_ALARM, activity.getString(R.string.settings_activity_menu_sound_alarm), false),
                new ListItem(MENU_SOUND_NOTIFICATIONS, activity.getString(R.string.settings_activity_menu_sound_notifications), false),
                new ListItem(MENU_SOUND_SILENT_MODE, activity.getString(R.string.settings_activity_menu_sound_silent_mode), false),
                new ListItem(MENU_SOUND_VIBRATION, activity.getString(R.string.settings_activity_menu_sound_vibration), false)
        ));

        // add standard mode apps settings
        boolean isFocusMode = preferences.getIsFocusMode();
        if (!isFocusMode) {
            // add standard mode apps settings title
            ListItem standardModeAppsTitleListItem = new ListItem("", activity.getString(R.string.settings_activity_menu_standard_apps_title), false);
            standardModeAppsTitleListItem.setType(ListItem.Type.HEADER);
            soundMenuListItems.add(standardModeAppsTitleListItem);
            soundMenuListItems.add(new ListItem(MENU_SOUND_STANDARD_APPS_SILENT, activity.getString(R.string.settings_activity_menu_sound_mute_standard_app), false));
            soundMenuListItems.add(new ListItem(MENU_SOUND_STANDARD_APPS_NO_NOTIFICATION_MARK, activity.getString(R.string.settings_activity_menu_sound_hide_notification_standard_app), false));
        } else if (isFocusMode && shouldUseExternalRingtonePicker) {
            // remove ringtone menu if should user external ringtone picker
            for (int i = 0; i < soundMenuListItems.size(); i++) {
                if (soundMenuListItems.get(i).getId().equals(MENU_SOUND_RINGTONE)) {
                    soundMenuListItems.remove(i);
                }
            }
        }
    }

    private void populateSoundPhoneVolumeMenuListItems () {
        // get volume
        int ringtoneVolume = SoundManager.getRingtoneVolume(activity);
        int alarmVolume = SoundManager.getAlarmVolume(activity);
        // int systemVolume = SoundManager.getSystemVolume(activity);
        int voiceVolume = SoundManager.getVoiceVolume(activity);

        ListItem ringtoneListItem = new ListItem(MENU_SOUND_PHONE_VOLUME_RINGTONE, activity.getString(R.string.settings_activity_menu_sound_phone_volume_ringtone), false);
        ListItem alarmListItem = new ListItem(MENU_SOUND_PHONE_VOLUME_ALARM, activity.getString(R.string.settings_activity_menu_sound_phone_volume_alarm), false);
        // ListItem notificationListItem = new ListItem(MENU_SOUND_PHONE_VOLUME_SOUND, activity.getString(R.string.settings_activity_menu_sound_phone_volume_sound), false);
        ListItem voiceListItem = new ListItem(MENU_SOUND_PHONE_VOLUME_VOICE, activity.getString(R.string.settings_activity_menu_sound_phone_volume_voice), false);

        // add volume to list items
        ringtoneListItem.setRightText(volumeToListItemRightText(ringtoneVolume));
        alarmListItem.setRightText(volumeToListItemRightText(alarmVolume));
        // notificationListItem.setRightText(volumeToListItemRightText(systemVolume));
        voiceListItem.setRightText(volumeToListItemRightText(voiceVolume));

        soundPhoneVolumeMenuListItems = new ArrayList<ListItem>(Arrays.asList(
                ringtoneListItem,
                alarmListItem,
                // notificationListItem,
                voiceListItem
        ));
    }

    private String volumeToListItemRightText (int volume) {
        return String.valueOf(volume).concat("/").concat(String.valueOf(SoundManager.VOLUME_RANGE_UPPER));
    }

    private void populateSoundRingtoneMenuListItems () {
        soundRingtoneMenuListItems = new ArrayList<ListItem>();
        ArrayList<SoundModel> ringtoneList = SoundManager.getRingtoneList(activity);
        for (SoundModel sound : ringtoneList) {
            soundRingtoneMenuListItems.add(new ListItem(sound.getUri().toString(), sound.getName(), false));
        }

    }

    private void populateSoundAlarmMenuListItems () {
        soundAlarmMenuListItems = new ArrayList<ListItem>();
        ArrayList<SoundModel> alarmList = SoundManager.getAlarmList(activity);
        for (SoundModel sound : alarmList) {
            soundAlarmMenuListItems.add(new ListItem(sound.getUri().toString(), sound.getName(), false));
        }
    }

    private void populateSoundNotificationMenuListItems () {
        soundNotificationsMenuListItems = new ArrayList<ListItem>();
        ArrayList<SoundModel> notificationList = SoundManager.getNotificationList(activity);
        for (SoundModel sound : notificationList) {
            soundNotificationsMenuListItems.add(new ListItem(sound.getUri().toString(), sound.getName(), false));
        }
    }

    private void populateCameraMenuListItems () {
        cameraMenuListItems = new ArrayList<ListItem>(Arrays.asList(
                new ListItem(MENU_CAMERA_COLOR, activity.getString(R.string.settings_activity_menu_camera_color), false),
                // new ListItem(MENU_CAMERA_FRONT_CAMERA, activity.getString(R.string.settings_activity_menu_camera_front_camera), false),
                new ListItem(MENU_CAMERA_NO_FLASH, activity.getString(R.string.settings_activity_menu_camera_no_flash), false),
                new ListItem(MENU_CAMERA_EXPLORE, activity.getString(R.string.settings_activity_menu_camera_explore), false)
        ));
    }

    // change menu view
    private void showMainMenu () {
        currentMenu = CurrentMenu.MAIN_MENU;
        populateMenuListItems();
        currentListItems = menuListItems;
        updateListState();
        activity.updateMenuList();
        // hide set and up/down buttons
        activity.setButtonsUpDownVisible(false);
        activity.setButtonSetVisible(false);
        activity.setButtonFocusModeVisible(true);
    }

    private void showSoundMenu () {
        currentMenu = CurrentMenu.SOUND_MENU;
        populateSoundMenuListItems();
        currentListItems = soundMenuListItems;
        updateListState();
        activity.updateMenuList();
        // hide set and up/down buttons
        activity.setButtonsUpDownVisible(false);
        activity.setButtonSetVisible(false);
        activity.setButtonFocusModeVisible(false);
    }

    private void showSoundPhoneVolumeMenu () {
        currentMenu = CurrentMenu.SOUND_VOLUME_MENU;
        populateSoundPhoneVolumeMenuListItems();
        currentListItems = soundPhoneVolumeMenuListItems;
        updateListState();
        activity.updateMenuList();
        // show up/down buttons
        activity.setButtonsUpDownVisible(true);
        activity.setButtonsUpDownEnabled(false);
        activity.setButtonFocusModeVisible(false);
    }

    private void showSoundRingtoneMenu () {
        currentMenu = CurrentMenu.SOUND_RINGTONE_MENU;
        populateSoundRingtoneMenuListItems();
        currentListItems = soundRingtoneMenuListItems;
        updateListState();
        activity.updateMenuList();
        // show set button
        activity.setButtonSetVisible(true);
        activity.setButtonSetEnabled(false);
        activity.setButtonFocusModeVisible(false);
    }

    private void showSoundAlarmMenu () {
        currentMenu = CurrentMenu.SOUND_ALARM_MENU;
        populateSoundAlarmMenuListItems();
        currentListItems = soundAlarmMenuListItems;
        updateListState();
        activity.updateMenuList();
        // show set button
        activity.setButtonSetVisible(true);
        activity.setButtonSetEnabled(false);
        activity.setButtonFocusModeVisible(false);
    }

    private void showSoundNotificationsMenu () {
        currentMenu = CurrentMenu.SOUND_NOTIFICATIONS_MENU;
        populateSoundNotificationMenuListItems();
        currentListItems = soundNotificationsMenuListItems;
        updateListState();
        activity.updateMenuList();
        // show set button
        activity.setButtonSetVisible(true);
        activity.setButtonSetEnabled(false);
        activity.setButtonFocusModeVisible(false);
    }

    private void showCameraMenu () {
        currentMenu = CurrentMenu.CAMERA_MENU;
        populateCameraMenuListItems();
        currentListItems = cameraMenuListItems;
        updateListState();
        activity.updateMenuList();
        activity.setButtonFocusModeVisible(false);
    }

    // getters
    public ArrayList<ListItem> getMenuListItems () {
        return currentListItems;
    }

    public void onButtonClosePress () {
        switch (currentMenu) {
            case MAIN_MENU:
                activity.finish();
                break;
            case SOUND_VOLUME_MENU:
            case SOUND_RINGTONE_MENU:
            case SOUND_ALARM_MENU:
            case SOUND_NOTIFICATIONS_MENU:
                SoundManager.stopRingtone();
                showSoundMenu();
                break;
            default:
                showMainMenu();
                break;
        }
    }

    public void onButtonUpPress () {
        onButtonUpDownPress(true);
    }

    public void onButtonDownPress () {
        onButtonUpDownPress(false);
    }

    private void onButtonUpDownPress (boolean isUp) {
        int increment = isUp ? 1 : -1;
        int currentVolume = 0;
        if (selectedListItem != null && selectedListItem != null && currentMenu == CurrentMenu.SOUND_VOLUME_MENU) {
            switch (selectedListItem.getId()) {
                case MENU_SOUND_PHONE_VOLUME_RINGTONE:
                    currentVolume = SoundManager.getRingtoneVolume(activity);
                    SoundManager.incrementRingtoneVolume(activity, increment);
                    currentVolume = SoundManager.getRingtoneVolume(activity);
                    updateMenuItemRightText(selectedListItem.getId(), volumeToListItemRightText(currentVolume));
                    break;
                case MENU_SOUND_PHONE_VOLUME_ALARM:
                    currentVolume = SoundManager.getAlarmVolume(activity);
                    SoundManager.incrementAlarmVolume(activity, increment);
                    currentVolume = SoundManager.getAlarmVolume(activity);
                    updateMenuItemRightText(selectedListItem.getId(), volumeToListItemRightText(currentVolume));
                    break;
                case MENU_SOUND_PHONE_VOLUME_SOUND:
                    currentVolume = SoundManager.getSystemVolume(activity);
                    SoundManager.incrementSystemVolume(activity, increment);
                    currentVolume = SoundManager.getSystemVolume(activity);
                    updateMenuItemRightText(selectedListItem.getId(), volumeToListItemRightText(currentVolume));
                    break;
                case MENU_SOUND_PHONE_VOLUME_VOICE:
                    currentVolume = SoundManager.getVoiceVolume(activity);
                    SoundManager.incrementVoiceVolume(activity, increment);
                    currentVolume = SoundManager.getVoiceVolume(activity);
                    updateMenuItemRightText(selectedListItem.getId(), volumeToListItemRightText(currentVolume));
                    break;
                default:
                    // do nothing
                    break;
            }

            soundVolumeMenuPreviewSoundVolume(selectedListItem);
        }
    }

    public void onButtonSetPress () {
        if (selectedListItem != null) {
            switch (currentMenu) {
                case SOUND_RINGTONE_MENU:
                    // deactivate previous item
                    SoundModel previousRingtone = SoundManager.getDefaultRingtone(activity);
                    if (previousRingtone != null) {
                        updateMenuItemIsActive(previousRingtone.getUri().toString(), false);
                    }
                    // activate selected item
                    SoundManager.setDefaultRingtone(activity, Uri.parse(selectedListItem.getId()));
                    updateMenuItemIsActive(selectedListItem.getId(), true);
                    onButtonClosePress();
                    break;
                case SOUND_ALARM_MENU:
                    // deactivate previous item
                    SoundModel previousAlarm = SoundManager.getDefaultAlarm(activity);
                    if (previousAlarm != null) {
                        updateMenuItemIsActive(previousAlarm.getUri().toString(), false);
                    }
                    // activate selected item
                    SoundManager.setDefaultAlarm(activity, Uri.parse(selectedListItem.getId()));
                    updateMenuItemIsActive(selectedListItem.getId(), true);
                    onButtonClosePress();
                    break;
                case SOUND_NOTIFICATIONS_MENU:
                    // deactivate previous item
                    SoundModel previousNotification = SoundManager.getDefaultNotification(activity);
                    if (previousNotification != null) {
                        updateMenuItemIsActive(previousNotification.getUri().toString(), false);
                    }
                    // activate selected item
                    SoundManager.setDefaultNotification(activity, Uri.parse(selectedListItem.getId()));
                    updateMenuItemIsActive(selectedListItem.getId(), true);
                    onButtonClosePress();
                    break;
            }
        }
    }

    public void onMenuItemClick (ListItem menuItem) {
        // check current menu, process menuItem click according to menu type
        switch (currentMenu) {
            case SOUND_RINGTONE_MENU:
            case SOUND_ALARM_MENU:
            case SOUND_NOTIFICATIONS_MENU:
            case SOUND_VOLUME_MENU:
                toggleSelectedItem(menuItem);
                break;
            default:
                onNavigationMenuItemClick(menuItem);
                break;
        }
    }

    private void toggleSelectedItem (ListItem menuItem) {
        if (selectedListItem == null || !selectedListItem.getId().equals(menuItem.getId())) {
            selectedListItem = menuItem;
            activity.setSelectedMenuListItem(menuItem);
            activity.setButtonSetEnabled(true);
            activity.setButtonsUpDownEnabled(true);
            if (currentMenu == CurrentMenu.SOUND_VOLUME_MENU) {
                soundVolumeMenuPreviewSoundVolume(menuItem);
            } else {
                SoundManager.playRingtone(activity, Uri.parse(menuItem.getId()));
            }
        } else {
            selectedListItem = null;
            activity.setSelectedMenuListItem(null);
            activity.setButtonSetEnabled(false);
            activity.setButtonsUpDownEnabled(false);
            SoundManager.stopRingtone();
        }
    }

    private void soundVolumeMenuPreviewSoundVolume (ListItem menuItem) {
        SoundModel defaultNotification = SoundManager.getDefaultNotification(activity);
        if (defaultNotification != null) {
            switch (menuItem.getId()) {
                case MENU_SOUND_PHONE_VOLUME_RINGTONE:
                    SoundManager.playRingtone(activity, defaultNotification.getUri(), AudioManager.STREAM_RING, false);
                    break;
                case MENU_SOUND_PHONE_VOLUME_ALARM:
                    SoundManager.playRingtone(activity, defaultNotification.getUri(), AudioManager.STREAM_ALARM, false);
                    break;
                case MENU_SOUND_PHONE_VOLUME_VOICE:
                    SoundManager.playRingtone(activity, defaultNotification.getUri(), AudioManager.STREAM_VOICE_CALL, false);
                    break;
                default:
                    // do nothing
                    break;
            }
        }
    }

    private void onNavigationMenuItemClick (ListItem menuItem) {
        switch (menuItem.getId()) {
            case MENU_ABOUT_PHONE:
                startAboutPhone();
                break;
            case MENU_SOUND:
                startSoundSettings();
                break;
            case MENU_CAMERA:
                startCameraSettings();
                break;
            case MENU_HOTSPOT:
                startHotspotSettings();
                break;
            case MENU_KEEP_LOCK:
                toggleFocusMode();
                break;
            case MENU_ALL_SETTINGS:
                startAllSettingsSettings();
                break;
            case MENU_DARK_MODE:
                toggleDarkMode();
                break;
            case MENU_SETTINGS_TOOLS:
                startSettingsTools();
                break;
            // sound
            case MENU_SOUND_PHONE_VOLUME:
                startSoundPhoneVolumeSettings();
                break;
            case MENU_SOUND_RINGTONE:
                startSoundRingtoneSettings();
                break;
            case MENU_SOUND_ALARM:
                startSoundAlarmSettings();
                break;
            case MENU_SOUND_NOTIFICATIONS:
                startSoundNotificationSettings();
                break;
            case MENU_SOUND_SILENT_MODE:
                toggleSoundSilentMode();
                break;
            case MENU_SOUND_VIBRATION:
                toggleSoundVibration();
                break;
            case MENU_SOUND_STANDARD_APPS_SILENT:
                toggleSoundStandardAppsSilent();
                break;
            case MENU_SOUND_STANDARD_APPS_NO_NOTIFICATION_MARK:
                toggleSoundStandardAppsNoNotificationMark();
                break;
            // camera
            case MENU_CAMERA_COLOR:
                boolean isColorMode = preferences.getSettingsCameraColor();
                preferences.setSettingsCameraColor(!isColorMode);
                updateMenuItemIsActive(MENU_CAMERA_COLOR, !isColorMode);
                break;
            /*
            case MENU_CAMERA_FRONT_CAMERA:
                boolean isFrontCamera = preferences.getSettingsCameraFrontCamera();
                preferences.setSettingsCameraFrontCamera(!isFrontCamera);
                updateMenuItemIsActive(MENU_CAMERA_FRONT_CAMERA, !isFrontCamera);
                break;

            */
            case MENU_CAMERA_NO_FLASH:
                boolean isFlashAuto = !preferences.getSettingsCameraNoFlash();
                preferences.setSettingsCameraNoFlash(isFlashAuto);
                updateMenuItemIsActive(MENU_CAMERA_NO_FLASH, isFlashAuto);
                break;
            case MENU_CAMERA_EXPLORE:
                // startFilesExplorerActivity();
                permissionsService.askPhoneStoragePermissions();
                break;
            default:
                // do nothing
                break;
        }
    }

    public void onActivityWindowFocusChanged (boolean hasFocus) {
        if (hasFocus && shouldStartLockTaskOnFocus) {
            shouldStartLockTaskOnFocus = false;
            boolean isKeepInLockMode = preferences.getIsFocusMode();

            if (isKeepInLockMode) {
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

    }

    private void startAboutPhone() {
        Intent aboutPhoneActivityIntent = new Intent(activity, AboutPhoneActivity.class);
        activity.startActivity(aboutPhoneActivityIntent);
    }

    private void startSoundSettings () {
        showSoundMenu();
    }

    private void startCameraSettings () {
        showCameraMenu();
    }

    private void startSettingsTools () {
        Intent settingsToolsActivityIntent = new Intent(activity, SettingsToolsActivity.class);
        activity.startActivity(settingsToolsActivityIntent);
    }

    private void startExternalSoundSettings () {
        shouldStartLockTaskOnFocus = true;
        activity.stopLockTask();
        activity.startActivity(new Intent(Settings.ACTION_SOUND_SETTINGS));
    }

    private void startHotspotSettings () {
        shouldStartLockTaskOnFocus = true;
        activity.stopLockTask();
        final Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        final ComponentName componentName = new ComponentName("com.android.settings", "com.android.settings.TetherSettings");
        intent.setComponent(componentName);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
    }

    public void toggleFocusMode() {
        boolean isKeepInLockMode = preferences.getIsFocusMode();
        preferences.setFocusMode(!isKeepInLockMode);
        if (isKeepInLockMode) {
            activity.stopLockTask();
            updateMenuItemText(MENU_SETTINGS_TOOLS, activity.getString(R.string.settings_activity_menu_settings_tools));
        } else {
            activity.startLockTask();
            updateMenuItemText(MENU_SETTINGS_TOOLS, activity.getString(R.string.settings_activity_menu_settings_tools_focus));
        }

        preferences.setToolsHiddenChanged(true);
        activity.setButtonFocusModeActive(!isKeepInLockMode);

        // update main menu items
        showMainMenu();
    }

    private void startAllSettingsSettings () {
        shouldStartLockTaskOnFocus = true;
        activity.stopLockTask();
        activity.startActivity(new Intent(android.provider.Settings.ACTION_SETTINGS));
    }

    private void toggleDarkMode () {
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES || AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            preferences.setNightModePreferences(AppCompatDelegate.MODE_NIGHT_NO);
            updateMenuItemIsActive(MENU_DARK_MODE, false);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            preferences.setNightModePreferences(AppCompatDelegate.MODE_NIGHT_YES);
            updateMenuItemIsActive(MENU_DARK_MODE, true);
        }

        preferences.setSettingsNightModeChanged(true);
        // force update theme
        activity.recreate();
    }

    // sound
    private void startSoundPhoneVolumeSettings () {
        showSoundPhoneVolumeMenu();
    }

    private void startSoundRingtoneSettings () {
        if (shouldUseExternalRingtonePicker) {
            startExternalSoundSettings();
        } else {
            showSoundRingtoneMenu();
        }

    }

    private void startSoundAlarmSettings () {
        showSoundAlarmMenu();
    }

    private void startSoundNotificationSettings () {
        showSoundNotificationsMenu();
    }

    private void toggleSoundSilentMode () {
        boolean isSilentMode = SoundManager.getIsSilentMode(activity);
        SoundManager.setIsSilentMode(activity, !isSilentMode);
        updateMenuItemIsActive(MENU_SOUND_SILENT_MODE, !isSilentMode);
    }

    private void toggleSoundVibration () {
        boolean isVibrationEnabled = preferences.getSoundSettingsVibrationEnabled();
        preferences.setSoundSettingsVibrationEnabled(!isVibrationEnabled);
        updateMenuItemIsActive(MENU_SOUND_VIBRATION, !isVibrationEnabled);
    }

    private void toggleSoundStandardAppsSilent () {
        boolean isStandardAppsSilent = preferences.getSoundSettingsStandardAppsSilent();
        preferences.setSoundSettingsStandardAppsSilent(!isStandardAppsSilent);
        updateMenuItemIsActive(MENU_SOUND_STANDARD_APPS_SILENT, !isStandardAppsSilent);
    }

    private void toggleSoundStandardAppsNoNotificationMark () {
        boolean isStandardAppsNoNotificationMark = preferences.getSoundSettingsStandardAppsNoNotificationMark();
        preferences.setSoundSettingsStandardAppsNoNotificationMark(!isStandardAppsNoNotificationMark);
        updateMenuItemIsActive(MENU_SOUND_STANDARD_APPS_NO_NOTIFICATION_MARK, !isStandardAppsNoNotificationMark);
    }


    // camera
    private void startFilesExplorerActivity () {
        Intent filesExplorerActivityIntent = new Intent(activity, FilesExplorerActivity.class);
        activity.startActivity(filesExplorerActivityIntent);
    }

    private void updateMenuItemIsActive (String menuItemId, boolean isActive) {
        for (int i = 0; i < currentListItems.size(); i++) {
            ListItem currentItem = currentListItems.get(i);
            if (currentItem.getId().equals(menuItemId)) {
                currentItem.setIsActive(isActive);
                activity.updateMenuListItem(currentItem);
            }
        }
    }

    private void updateMenuItemRightText (String menuItemId, String rightText) {
        for (int i = 0; i < currentListItems.size(); i++) {
            ListItem currentItem = currentListItems.get(i);
            if (currentItem.getId().equals(menuItemId)) {
                currentItem.setRightText(rightText);
                activity.updateMenuListItem(currentItem);
            }
        }
    }

    private void updateMenuItemText (String menuItemId, String rightText) {
        for (int i = 0; i < currentListItems.size(); i++) {
            ListItem currentItem = currentListItems.get(i);
            if (currentItem.getId().equals(menuItemId)) {
                currentItem.setText(rightText);
                activity.updateMenuListItem(currentItem);
            }
        }
    }

    private void showOpenAppSettingsDialogForStorage () {
        String dialogTitle = activity.getResources().getString(R.string.dialog_all_storage_permissions_title);
        // get not granted permissions
        ArrayList<String> notGrantedExploreFilePermissions = permissionsService.getNotGrantedCameraRecordAudioPermissions();
        boolean showStoragePermissionString = permissionsService.containsAStoragePermission(notGrantedExploreFilePermissions);

        String dialogBody = activity.getResources().getString(R.string.dialog_all_storage_permissions_body)
                + (showStoragePermissionString ? activity.getResources().getString(R.string.dialog_all_storage_permissions_body_permission_storage) : "");

        showOpenAppSettingsDialog(dialogTitle, dialogBody);
    }

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
                // stop lock task to enable open settings
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

    @Override
    public void onPermissionCheckSuccess(int requestCode) {
        switch (requestCode) {
            case PermissionsService.REQUEST_CODE_ALL_STORAGE:
                // can access to files
                startFilesExplorerActivity();
                break;
        }
    }

    @Override
    public void onPermissionCheckFailure(int requestCode, boolean isNeverAskAgain) {
        switch (requestCode) {
            case PermissionsService.REQUEST_CODE_ALL_STORAGE:
                if (isNeverAskAgain) {
                    showOpenAppSettingsDialogForStorage();
                }
                break;
        }
    }
}
