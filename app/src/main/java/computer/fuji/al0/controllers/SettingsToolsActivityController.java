package computer.fuji.al0.controllers;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import computer.fuji.al0.R;
import computer.fuji.al0.activities.SettingsToolsActivity;
import computer.fuji.al0.constants.MainActivityMainMenu;
import computer.fuji.al0.models.ListItem;
import computer.fuji.al0.services.FlashLightService;
import computer.fuji.al0.utils.Apps;
import computer.fuji.al0.utils.Preferences;

public class SettingsToolsActivityController {
    private enum CurrentView {TOOLS_SETTINGS, SELECT_APP };
    private SettingsToolsActivity activity;
    private ArrayList<ListItem> menuListItems;
    private ArrayList<ListItem> filteredMenuListItems;
    private Preferences preferences;
    private String al0PackageName;

    private CurrentView currentView;

    private static final String whiteSpace = " ";

    private String currentExternalAppSettingId;
    private String selectedAppId;
    private String selectedAppName;

    private Boolean isOpenAppMode = false;

    private final int MAX_SELECTABLE_NUMBER_OF_TOOLS_AND_APPS = 5;

    private final String EMPTY_APP_LIST_ITEM_ID = "EMPTY_APP";
    private ListItem emptyAppListItem;


    private static final String EXTERNAL_APP_1 = "EXTERNAL_APP_1";
    private static final String EXTERNAL_APP_2 = "EXTERNAL_APP_2";
    private static final String EXTERNAL_APP_3 = "EXTERNAL_APP_3";

    public SettingsToolsActivityController (SettingsToolsActivity settingsToolsActivity) {
        this.activity = settingsToolsActivity;
        al0PackageName = activity.getPackageName();
        preferences = new Preferences(activity);
        menuListItems = new ArrayList<ListItem>();
        emptyAppListItem = new ListItem(EMPTY_APP_LIST_ITEM_ID, activity.getString(R.string.settings_tools_activity_empty_app_option), false);
    }

    public ArrayList<ListItem> getMenuListItems () {
        return menuListItems;
    }

    // Events
    public void onActivityReady () {
        showMenuListItems();
    }

    public void onActivityWindowFocusChanged (boolean hasFocus) {
        if (hasFocus) {
            asynchronouslyResetAppsCache();
            // update apps list view
            if (currentView.equals(CurrentView.SELECT_APP)) {
                showAppsList();
            } else {
                // do nothing
            }
        }
    }

    public void onMenuItemClick (ListItem menuItem) {
        // Check current view
        if (currentView == CurrentView.TOOLS_SETTINGS) {
            if (menuItem.getType() == ListItem.Type.ITEM && !itemIsExternalApp(menuItem.getId())) {
                // get hidden items
                ArrayList<String> hiddenItems = new ArrayList<String>(Arrays.asList(preferences.getToolsHidden()));

                // check if should show or hide item
                if (isToolHidden(hiddenItems, menuItem.getId())) {
                    hiddenItems.remove(menuItem.getId());
                } else {
                    hiddenItems.add(menuItem.getId());
                }

                // update preferences
                preferences.setToolsHidden(hiddenItems.toArray(new String[hiddenItems.size()]));
                preferences.setToolsHiddenChanged(true);

                for (int i = 0; i < menuListItems.size(); i++) {
                    ListItem item = menuListItems.get(i);
                    if (item.getType() == ListItem.Type.ITEM && !itemIsExternalApp(item.getId())) {
                        boolean isToolHidden = isToolHidden(hiddenItems, item.getId());
                        item.setIsActive(!isToolHidden);
                        activity.updateMenuListItem(item, i);
                    }
                }
            } else if (menuItem.getType() == ListItem.Type.ITEM && itemIsExternalApp(menuItem.getId())) {
                currentExternalAppSettingId = menuItem.getId();
                showAppsList();
            }
        } else if (currentView == CurrentView.SELECT_APP) {
            for (int i = 0; i < menuListItems.size(); i++) {
                ListItem item = menuListItems.get(i);
                if (item.getId().equals(menuItem.getId())) {
                    if (selectedAppId != null && selectedAppId.equals(item.getId())) {
                        activity.setSelectedMenuListItem(null);
                        activity.setOpenAppButtonEnabled(false);
                        activity.setSetButtonEnabled(false);
                        selectedAppId = null;
                        selectedAppName = null;
                    } else {
                        activity.setSelectedMenuListItem(item);
                        activity.setOpenAppButtonEnabled(true);
                        activity.setSetButtonEnabled(true);
                        selectedAppId = item.getId();
                        selectedAppName = item.getText();
                    }
                }
            }
        }

        // update external apps state
        updateExternalAppsState();
    }

    public void onButtonSetPress () {
        // store selected app
        if (selectedAppId != null) {
            // check if user selected nothing
            boolean isSelectedEmptyApp = selectedAppId == EMPTY_APP_LIST_ITEM_ID;

            switch (currentExternalAppSettingId) {
                case EXTERNAL_APP_1:
                    preferences.setExternalApp1(isSelectedEmptyApp ? null : selectedAppId);
                    preferences.setExternalApp1Name(isSelectedEmptyApp ? null : selectedAppName);
                    break;
                case EXTERNAL_APP_2:
                    preferences.setExternalApp2(isSelectedEmptyApp ? null : selectedAppId);
                    preferences.setExternalApp2Name(isSelectedEmptyApp ? null : selectedAppName);
                    break;
                case EXTERNAL_APP_3:
                    preferences.setExternalApp3(isSelectedEmptyApp ? null : selectedAppId);
                    preferences.setExternalApp3Name(isSelectedEmptyApp ? null : selectedAppName);
                    break;
            }
        }

        // update view
        for (int i = 0; i < menuListItems.size(); i++) {
            ListItem item = menuListItems.get(i);
            if (item.getId().equals(selectedAppId)) {
                item.setIsActive(true);
                activity.updateMenuListItem(item, i);
            }
        }

        activity.setSelectedMenuListItem(null);
        showMenuListItems();
        // make sure to refresh main menu screen
        preferences.setToolsHiddenChanged(true);
    }

    public void onButtonSetNothingPress () {
        selectedAppId = EMPTY_APP_LIST_ITEM_ID;
        onButtonSetPress();
    }

    public void onButtonOpenAppPress () {
        if (selectedAppId != null) {
            startExternalActivity(selectedAppId);
        }
    }

    public void onButtonClosePress () {
        isOpenAppMode = false;
        switch (currentView) {
            case SELECT_APP:
                showMenuListItems();
                break;
            case TOOLS_SETTINGS:
                activity.finish();
                break;
        }
    }

    public void onButtonAllAppsPress () {
        isOpenAppMode = true;
        showAppsList();
        // reset current external app
        currentExternalAppSettingId = null;
    }

    public void onButtonFindPress () {
        activity.setLayout(SettingsToolsActivity.Layout.LIST_APPS_WITH_KEYBOARD);
    }

    public void onTextInputFindAppQueryChange(String query) {
        filteredMenuListItems = new ArrayList<>();
        for (ListItem menuListItem : menuListItems) {
            // check if contact list item contains query string
            // ignore letter case
            String menuListItemTextIgnoreCase = menuListItem.getText().toLowerCase();
            String queryStringIgnoreCase = query.toLowerCase();
            boolean menuListItemMatchQueryString = menuListItemTextIgnoreCase.contains(queryStringIgnoreCase);
            if (menuListItemMatchQueryString) {
                filteredMenuListItems.add(menuListItem);
            }
        }

        activity.updateMenuListItems(filteredMenuListItems);
    }

    public void onButtonActionPress () {
        activity.updateMenuListItems(menuListItems);
        onButtonCloseKeyboardPress();

        if (isOpenAppMode) {
            onButtonOpenAppPress();
        } else {
            onButtonSetPress();
        }
    }

    public void onButtonCloseKeyboardPress () {
        activity.updateMenuListItems(menuListItems);
        activity.setLayout(SettingsToolsActivity.Layout.LIST_APPS_WITH_FOOTER);
        activity.resetTextInputFindAppQuery();
    }

    // utils
    private void showMenuListItems() {
        currentView = CurrentView.TOOLS_SETTINGS;
        boolean isInFocusMode = preferences.getIsFocusMode();
        // hide set button
        activity.setSetButtonVisible(false);
        activity.setOpenAppButtonVisible(false);

        menuListItems.clear();

        // menuListItems = new ArrayList<ListItem>(Arrays.asList(
        // add tools header
        ListItem toolsHeaderItem = new ListItem("TOOLS_HEADER", activity.getString(R.string.settings_tools_activity_focus_tools_label), false);
        toolsHeaderItem.setType(ListItem.Type.HEADER);
        //menuListItems.add(0, toolsHeaderItem);

        menuListItems.addAll(Arrays.asList(
                // new ListItem(MainActivityMainMenu.MENU_PHONE, activity.getString(R.string.main_activity_menu_list_phone), false),
                toolsHeaderItem,
                new ListItem(MainActivityMainMenu.MENU_CALENDAR, activity.getString(R.string.settings_tools_activity_use_prefix) + whiteSpace + activity.getString(R.string.main_activity_menu_list_calendar), false),
                new ListItem(MainActivityMainMenu.MENU_CLOCK, activity.getString(R.string.settings_tools_activity_use_prefix) + whiteSpace + activity.getString(R.string.main_activity_menu_list_clock), false),
                new ListItem(MainActivityMainMenu.MENU_CAMERA, activity.getString(R.string.settings_tools_activity_use_prefix) + whiteSpace + activity.getString(R.string.main_activity_menu_list_camera), false),
                new ListItem(MainActivityMainMenu.MENU_TORCH, activity.getString(R.string.settings_tools_activity_use_prefix) + whiteSpace + activity.getString(R.string.main_activity_menu_list_torch), false),
                new ListItem(MainActivityMainMenu.MENU_CALCULATOR, activity.getString(R.string.settings_tools_activity_use_prefix) + whiteSpace + activity.getString(R.string.main_activity_menu_list_calculator), false)
        ));

        // remove torch if not available on device
        if (!FlashLightService.getIsFlashLightAvailable(activity)) {
            for (int i = 0; i < menuListItems.size(); i++) {
                if (menuListItems.get(i).getId().equals(MainActivityMainMenu.MENU_TORCH)) {
                    menuListItems.remove(i);
                }
            }
        }

        // update hidden tools state
        ArrayList<String> hiddenItems = new ArrayList<String>(Arrays.asList(preferences.getToolsHidden()));

        for (ListItem item : menuListItems) {
            if (item.getType() == ListItem.Type.ITEM  && !itemIsExternalApp(item.getId())) {
                boolean isToolHidden = isToolHidden(hiddenItems, item.getId());
                item.setIsActive(!isToolHidden);
            }
        }

        // add external apps when not in focus mode
        if (!isInFocusMode) {
            // show all apps button
            activity.setButtonAllAppsVisible(true);
            // add apps header
            ListItem appsHeaderItem = new ListItem("APPS_HEADER", activity.getString(R.string.settings_tools_activity_normal_mode_label), false);
            appsHeaderItem.setType(ListItem.Type.HEADER);
            menuListItems.add(menuListItems.size(), appsHeaderItem);

            // add space for external apps
            ListItem externalApp1Item = new ListItem(EXTERNAL_APP_1, externalAppIdToAppName(EXTERNAL_APP_1, "1"), false);
            menuListItems.add(externalApp1Item);
            ListItem externalApp2Item = new ListItem(EXTERNAL_APP_2, externalAppIdToAppName(EXTERNAL_APP_2, "2"), false);
            menuListItems.add(externalApp2Item);
            ListItem externalApp3Item = new ListItem(EXTERNAL_APP_3, externalAppIdToAppName(EXTERNAL_APP_3, "3"), false);
            menuListItems.add(externalApp3Item);
        }

        Collections.reverse(menuListItems);
        activity.setLayout(SettingsToolsActivity.Layout.DEFAULT);
        activity.updateMenuListItems(menuListItems);
    }

    private void showAppsList () {
        // reset selected item state
        activity.setSelectedMenuListItem(null);
        currentView = CurrentView.SELECT_APP;
        activity.setButtonAllAppsVisible(false);
        // reset query string
        activity.resetTextInputFindAppQuery();
        // check if is open app mode
        if (isOpenAppMode) {
            activity.setOpenAppButtonVisible(true);
            activity.setOpenAppButtonEnabled(false);
        } else {
            activity.setSetButtonVisible(true);
            activity.setSetButtonEnabled(false);
        }
        // clear menu list items
        menuListItems.clear();

        String externalAppPackage = externalAppIdToAppPackage(currentExternalAppSettingId);
        for (ListItem app : Apps.getAppsList(activity)) {
                if (externalAppPackage != null && externalAppPackage.equals(app.getId())) {
                    app.setIsActive(true);
                } else {
                    app.setIsActive(false);
                }

                menuListItems.add(app);

        }

        // get all installed apps
        /*
        final PackageManager packageManager = activity.getPackageManager();
        List<ApplicationInfo> packages = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);

        String externalAppPackage = externalAppIdToAppPackage(currentExternalAppSettingId);
        for (ApplicationInfo packageInfo : packages) {
            if (packageManager.getLaunchIntentForPackage(packageInfo.packageName) != null && !packageInfo.packageName.equals(al0PackageName)) {
                ListItem listItem = new ListItem(packageInfo.packageName, packageInfo.loadLabel(packageManager).toString(), false);
                if (externalAppPackage != null && externalAppPackage.equals(listItem.getId())) {
                    listItem.setIsActive(true);
                } else {
                    listItem.setIsActive(false);
                }

                menuListItems.add(listItem);
            }
        }

        // sort packages by name
        Collections.sort(menuListItems, new Comparator<ListItem>() {
            @Override
            public int compare(ListItem o1, ListItem o2) {
                return o1.getText().toLowerCase().compareTo(o2.getText().toLowerCase());
            }
        });
         */

        // Collections.reverse(menuListItems);
        activity.setLayout(SettingsToolsActivity.Layout.LIST_APPS_WITH_FOOTER);
        activity.updateMenuListItems(menuListItems);
    }

    //
    private void updateExternalAppsState () {
        for (int i = 0; i < menuListItems.size(); i++) {
            ListItem item = menuListItems.get(i);
            switch (item.getId()) {
                case EXTERNAL_APP_1:
                    item.setText(externalAppIdToAppName(EXTERNAL_APP_1, "1"));
                    activity.updateMenuListItem(item, i);
                    break;
                case EXTERNAL_APP_2:
                    item.setText(externalAppIdToAppName(EXTERNAL_APP_2, "2"));
                    activity.updateMenuListItem(item, i);
                    break;
                case EXTERNAL_APP_3:
                    item.setText(externalAppIdToAppName(EXTERNAL_APP_3, "3"));
                    activity.updateMenuListItem(item, i);
                    break;
                default:
                    // do nothing
                    break;
            }
        }
    }

    private boolean isToolHidden (ArrayList<String> hiddenItems, String itemId) {
        // unmark hidden items
        for (String hiddenItem : hiddenItems) {
            if (itemId.equals(hiddenItem)) {
                return true;
            }
        }

        return false;
    }

    private boolean itemIsExternalApp (String id) {
        switch (id) {
            case EXTERNAL_APP_1:
            case EXTERNAL_APP_2:
            case EXTERNAL_APP_3:
                return true;
            default:
                return false;
        }
    }

    private String externalAppIdToAppName (String externalAppSettingId, String appNumber) {
        String storedExternalAppName = null;

        switch (externalAppSettingId) {
            case EXTERNAL_APP_1:
                storedExternalAppName = preferences.getExternalApp1Name();
                break;
            case EXTERNAL_APP_2:
                storedExternalAppName = preferences.getExternalApp2Name();
                break;
            case EXTERNAL_APP_3:
                storedExternalAppName = preferences.getExternalApp3Name();
                break;
            default:
                // do nothing
                break;
        }

        // check if there is a stored external app
        if (storedExternalAppName != null) {
            return storedExternalAppName;
        } else {
            return activity.getString(R.string.settings_tools_activity_set_app).concat(appNumber);
        }
    }

    private String externalAppIdToAppPackage (String externalAppSettingId) {
        if (externalAppSettingId != null) {
            switch (externalAppSettingId) {
                case EXTERNAL_APP_1:
                    return preferences.getExternalApp1();
                case EXTERNAL_APP_2:
                    return preferences.getExternalApp2();
                case EXTERNAL_APP_3:
                    return preferences.getExternalApp3();
                default:
                    return null;
            }
        } else {
            return null;
        }
    }

    private void startExternalActivity (String activityId) {
        PackageManager pm = activity.getPackageManager();

        try {
            Intent intent = pm.getLaunchIntentForPackage(activityId);

            if (intent != null) {
                activity.startActivity(intent);
            }
        } catch (ActivityNotFoundException e)  {
            // do nothing
        }
    }

    private void asynchronouslyResetAppsCache () {
        // reset cached apps list
        // reload apps
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Apps.clearCachedAppsList();
                Apps.populateCachedAppsList(activity);
            }
        });
    }
}
