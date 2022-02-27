package computer.fuji.al0.utils;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatDelegate;

import java.util.ArrayList;
import java.util.Collections;

public class Preferences {
    Context context;
    SharedPreferences sharedPreferences;
    private static final String FIRST_RUN_KEY = "FIRST_RUN";
    private static final String FOCUS_MODE = "FOCUS_MODE";
    private static final String SETTIGS_CAMERA_USE_COLOR = "SETTIGS_CAMERA_USE_COLOR";
    private static final String SETTIGS_CAMERA_FRONT_CAMERA = "SETTIGS_CAMERA_FRONT_CAMERA";
    private static final String SETTIGS_CAMERA_NO_FLASH = "SETTIGS_CAMERA_NO_FLASH";
    private static final String SETTINGS_NIGHT_MODE_CHANGED = "SETTINGS_NIGHT_MODE_CHANGED";
    private static final String NIGHT_MODE_PREFERENCES = "NIGHT_MODE_PREFERENCES";
    private static final String SOUND_SETTINGS_DEFAULT_RINGTONE = "SOUND_SETTINGS_DEFAULT_RINGTONE";
    private static final String SOUND_SETTINGS_DEFAULT_ALARM = "SOUND_SETTINGS_DEFAULT_ALARM";
    private static final String SOUND_SETTINGS_DEFAULT_NOTIFICATION = "SOUND_SETTINGS_DEFAULT_NOTIFICATION";
    private static final String SOUND_SETTINGS_ENABLED_VIBRATION = "SOUND_SETTINGS_ENABLED_VIBRATION";
    private static final String NOTIFICATION_LISTENER_SERVICE_NOTIFICATIONS_ENABLED = "NOTIFICATION_LISTENER_SERVICE_NOTIFICATIONS_ENABLED";
    private static final String SHOULD_IGNORE_APP_SERVICES = "SHOULD_IGNORE_APP_SERVICES";
    private static final String SHOULD_SHOW_LOCK_SCREEN = "SHOULD_SHOW_LOCK_SCREEN";
    private static final String TOOLS_HIDDEN = "TOOLS_HIDDEN";
    private static final String TOOLS_HIDDEN_CHANGED = "TOOLS_HIDDEN_CHANGED";
    private static final String EXTERNAL_APP_1 = "EXTERNAL_APP_1";
    private static final String EXTERNAL_APP_1_NAME = "EXTERNAL_APP_1_NAME";
    private static final String EXTERNAL_APP_2 = "EXTERNAL_APP_2";
    private static final String EXTERNAL_APP_2_NAME = "EXTERNAL_APP_2_NAME";
    private static final String EXTERNAL_APP_3 = "EXTERNAL_APP_3";
    private static final String EXTERNAL_APP_3_NAME = "EXTERNAL_APP_3_NAME";
    private static final String ENABLE_EXPERIMENTAL_FEATURES = "ENABLE_EXPERIMENTAL_FEATURES";
    private static final String EXTERNAL_APP_NOTIFICATION_NOT_SEEN_IDS = "EXTERNAL_APP_NOTIFICATION_NOT_SEEN_IDS";
    private static final String SOUND_SETTINGS_STANDARD_APPS_SILENT = "SOUND_SETTINGS_STANDARD_APPS_SILENT";
    private static final String SOUND_SETTINGS_STANDARD_APPS_NO_NOTIFICATION_MARK = "SOUND_SETTINGS_STANDARD_APPS_NO_NOTIFICATION_MARK";

    public Preferences (Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
    }

    // getters

    // check if is the app's first run
    // on first run set a variable on sharedPreferences to keep the value on later app executions
    // ! on the first run this getter act as a setter
    public boolean getIsFirstRun () {
        boolean isFirstRun = sharedPreferences.getBoolean(FIRST_RUN_KEY, true);
        if (isFirstRun) {
            sharedPreferences.edit().putBoolean(FIRST_RUN_KEY, false).apply();
        }

        return isFirstRun;
    }

    // check if the user want to keep the app in FOCUS_MODE
    public boolean getIsFocusMode() {
        return sharedPreferences.getBoolean(FOCUS_MODE, false);
    }

    public int getNightModePreferences () {
        return sharedPreferences.getInt(NIGHT_MODE_PREFERENCES, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
    }

    public boolean getSettingsNightModeChanged () {
        return sharedPreferences.getBoolean(SETTINGS_NIGHT_MODE_CHANGED, false);
    }

    // check camera settings
    public boolean getSettingsCameraColor () {
        return sharedPreferences.getBoolean(SETTIGS_CAMERA_USE_COLOR, false);
    }

    public boolean getSettingsCameraFrontCamera () {
        return sharedPreferences.getBoolean(SETTIGS_CAMERA_FRONT_CAMERA, false);
    }

    public boolean getSettingsCameraNoFlash () {
        return sharedPreferences.getBoolean(SETTIGS_CAMERA_NO_FLASH, false);
    }

    // setters
    public void setFocusMode(boolean focusMode) {
        sharedPreferences.edit().putBoolean(FOCUS_MODE, focusMode).apply();
    }

    public void setNightModePreferences (int nightMode) {
        sharedPreferences.edit().putInt(NIGHT_MODE_PREFERENCES, nightMode).apply();
    }

    public void setSettingsNightModeChanged (boolean darkModeChanged) {
        sharedPreferences.edit().putBoolean(SETTINGS_NIGHT_MODE_CHANGED, darkModeChanged).apply();
    }

    // set camera settings
    public void setSettingsCameraColor (boolean cameraColor) {
        sharedPreferences.edit().putBoolean(SETTIGS_CAMERA_USE_COLOR, cameraColor).apply();
    }

    public void setSettingsCameraFrontCamera (boolean useFrontCamera) {
        sharedPreferences.edit().putBoolean(SETTIGS_CAMERA_FRONT_CAMERA, useFrontCamera).apply();
    }

    public void setSettingsCameraNoFlash (boolean noFlash) {
        sharedPreferences.edit().putBoolean(SETTIGS_CAMERA_NO_FLASH, noFlash).apply();
    }

    // Sound Settings
    public String getDefaultRingtoneRingtonePath () {
        return sharedPreferences.getString(SOUND_SETTINGS_DEFAULT_RINGTONE, null);
    }

    public String getDefaultAlarmRingtonePath () {
        return sharedPreferences.getString(SOUND_SETTINGS_DEFAULT_ALARM, null);
    }

    public String getDefaultNotificationRingtonePath () {
        return sharedPreferences.getString(SOUND_SETTINGS_DEFAULT_NOTIFICATION, null);
    }

    public void setDefaultRingtoneRingtonePath (String path) {
        sharedPreferences.edit().putString(SOUND_SETTINGS_DEFAULT_RINGTONE, path).apply();
    }

    public void setDefaultAlarmRingtonePath (String path) {
        sharedPreferences.edit().putString(SOUND_SETTINGS_DEFAULT_ALARM, path).apply();
    }

    public void setDefaultNotificationRingtonePath (String path) {
        sharedPreferences.edit().putString(SOUND_SETTINGS_DEFAULT_NOTIFICATION, path).apply();
    }

    // Sound Vibration
    public boolean getSoundSettingsVibrationEnabled () {
        return sharedPreferences.getBoolean(SOUND_SETTINGS_ENABLED_VIBRATION, true);
    }

    public void setSoundSettingsVibrationEnabled (Boolean isEnabled) {
        sharedPreferences.edit().putBoolean(SOUND_SETTINGS_ENABLED_VIBRATION, isEnabled).apply();
    }

    // NotificationListenerService
    public boolean getNotificationListenerServiceNotificationsEnabled () {
        return sharedPreferences.getBoolean(NOTIFICATION_LISTENER_SERVICE_NOTIFICATIONS_ENABLED, true);
    }

    public void setNotificationListenerServiceNotificationsEnabled (boolean isEnabled) {
        sharedPreferences.edit().putBoolean(NOTIFICATION_LISTENER_SERVICE_NOTIFICATIONS_ENABLED, isEnabled).apply();
    }

    // Ignore Application Services
    public boolean getShouldIgnoreAppServices () {
        return sharedPreferences.getBoolean(SHOULD_IGNORE_APP_SERVICES, false);
    }

    public void setShouldIgnoreAppServices (boolean shouldIgnoreAppServices) {
        sharedPreferences.edit().putBoolean(SHOULD_IGNORE_APP_SERVICES, shouldIgnoreAppServices).apply();
    }

    // ShouldShowLockScreen
    public boolean getShouldShowLockScreen () {
        return sharedPreferences.getBoolean(SHOULD_SHOW_LOCK_SCREEN, true);
    }

    public void setShouldShowLockScreen (boolean shouldShowLockScreen) {
        sharedPreferences.edit().putBoolean(SHOULD_SHOW_LOCK_SCREEN, shouldShowLockScreen).apply();
    }

    // Tools Hidden
    public String[] getToolsHidden () {
        String[] hiddenKeys = sharedPreferences.getString(TOOLS_HIDDEN, "").split(",");
        return hiddenKeys;
    }

    public void setToolsHidden (String[] toolsHidden) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < toolsHidden.length; i++) {
            stringBuilder.append(toolsHidden[i]).append(",");
        }

        sharedPreferences.edit().putString(TOOLS_HIDDEN, stringBuilder.toString()).apply();
    }

    public boolean getToolsHiddenChanged () {
        return sharedPreferences.getBoolean(TOOLS_HIDDEN_CHANGED, false);
    }

    public void setToolsHiddenChanged (boolean toolsHiddenChanged) {
        sharedPreferences.edit().putBoolean(TOOLS_HIDDEN_CHANGED, toolsHiddenChanged).apply();
    }

    // external apps
    public String getExternalApp1 () {
       return sharedPreferences.getString(EXTERNAL_APP_1, null);
    }

    public String getExternalApp2 () {
        return sharedPreferences.getString(EXTERNAL_APP_2, null);
    }

    public String getExternalApp3 () {
        return sharedPreferences.getString(EXTERNAL_APP_3, null);
    }

    public String getExternalApp1Name () {
        return sharedPreferences.getString(EXTERNAL_APP_1_NAME, null);
    }

    public String getExternalApp2Name () {
        return sharedPreferences.getString(EXTERNAL_APP_2_NAME, null);
    }

    public String getExternalApp3Name () {
        return sharedPreferences.getString(EXTERNAL_APP_3_NAME, null);
    }

    public void setExternalApp1 (String externalApp) {
        sharedPreferences.edit().putString(EXTERNAL_APP_1, externalApp).apply();
    }

    public void setExternalApp2 (String externalApp) {
        sharedPreferences.edit().putString(EXTERNAL_APP_2, externalApp).apply();
    }

    public void setExternalApp3 (String externalApp) {
        sharedPreferences.edit().putString(EXTERNAL_APP_3, externalApp).apply();
    }

    public void setExternalApp1Name (String externalApp) {
        sharedPreferences.edit().putString(EXTERNAL_APP_1_NAME, externalApp).apply();
    }

    public void setExternalApp2Name (String externalApp) {
        sharedPreferences.edit().putString(EXTERNAL_APP_2_NAME, externalApp).apply();
    }

    public void setExternalApp3Name (String externalApp) {
        sharedPreferences.edit().putString(EXTERNAL_APP_3_NAME, externalApp).apply();
    }

    public boolean getEnableExperimentalFeatures () {
        return sharedPreferences.getBoolean(ENABLE_EXPERIMENTAL_FEATURES, false);
    }

    public void setEnableExperimentalFeatures (boolean enable) {
        sharedPreferences.edit().putBoolean(ENABLE_EXPERIMENTAL_FEATURES, enable).apply();
    }

    // external apps notification ids
    public ArrayList<String> getExternalAppNotificationNotSeenIds() {
        String[] appIds = sharedPreferences.getString(EXTERNAL_APP_NOTIFICATION_NOT_SEEN_IDS, "").split(",");
        ArrayList<String> appIdsArrayList = new ArrayList<String>();
        Collections.addAll(appIdsArrayList, appIds);
        return appIdsArrayList;
    }

    public void setExternalAppNotificationNotSeenIds(ArrayList<String> notificationAppIds) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < notificationAppIds.size(); i++) {
            stringBuilder.append(notificationAppIds.get(i)).append(",");
        }

        sharedPreferences.edit().putString(EXTERNAL_APP_NOTIFICATION_NOT_SEEN_IDS, stringBuilder.toString()).apply();
    }

    // standard apps silent
    public boolean getSoundSettingsStandardAppsSilent() {
        return sharedPreferences.getBoolean(SOUND_SETTINGS_STANDARD_APPS_SILENT, false);
    }

    public void setSoundSettingsStandardAppsSilent(boolean isSilent) {
        sharedPreferences.edit().putBoolean(SOUND_SETTINGS_STANDARD_APPS_SILENT, isSilent).apply();
    }

    // standard apps home notification mark
    public boolean getSoundSettingsStandardAppsNoNotificationMark () {
        return sharedPreferences.getBoolean(SOUND_SETTINGS_STANDARD_APPS_NO_NOTIFICATION_MARK, false);
    }

    public void setSoundSettingsStandardAppsNoNotificationMark(boolean isNoNotificationMark) {
        sharedPreferences.edit().putBoolean(SOUND_SETTINGS_STANDARD_APPS_NO_NOTIFICATION_MARK, isNoNotificationMark).apply();
    }
}