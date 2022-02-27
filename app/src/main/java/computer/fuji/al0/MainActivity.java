package computer.fuji.al0;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import computer.fuji.al0.adapters.ListItemsAdapter;
import computer.fuji.al0.components.Dialog;
import computer.fuji.al0.components.ListItemsDividerItemDecoration;
import computer.fuji.al0.controllers.MainActivityController;
import computer.fuji.al0.models.ListItem;
import computer.fuji.al0.services.ScreenStateChangeListenerService;
import computer.fuji.al0.utils.Cutout;
import computer.fuji.al0.utils.LockScreen;
import computer.fuji.al0.utils.Preferences;
import computer.fuji.al0.utils.Status;
import computer.fuji.al0.utils.Time;
import computer.fuji.al0.utils.UI;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements ListItemsAdapter.ListItemListener {
    private MainActivityController controller;
    private ArrayList<ListItem> menuListItems;

    private LinearLayout mainActivityWrapper;
    private RecyclerView menuListView;
    private ListItemsAdapter menuListViewAdapter;
    private RecyclerView.LayoutManager layoutManager;

    private LinearLayout phoneStatusWrapper;
    private TextView statusSignalLabel;
    private TextView statusBatteryLabel;

    private Preferences preferences;
    private static int currentNightmode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // show activity on lock screen
        LockScreen.addShowOnLockScreenFlags(this);

        // start screen state change listener service
        Intent intent = new Intent(this, ScreenStateChangeListenerService.class);
        startService(intent);

        preferences = new Preferences(this);
        // check if should force set night mode
        if (preferences.getNightModePreferences() == AppCompatDelegate.MODE_NIGHT_YES) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        currentNightmode = getCurrentNightMode();

        setContentView(R.layout.activity_main);
        UI.hideNavigationBar(this);
        // status
        phoneStatusWrapper = (LinearLayout) findViewById(R.id.main_activity_status);
        statusSignalLabel = (TextView) findViewById(R.id.main_activity_status_signal);
        statusBatteryLabel = (TextView) findViewById(R.id.main_activity_status_battery);

        // menu
        mainActivityWrapper = (LinearLayout) findViewById(R.id.main_activity_wrapper);

        controller = new MainActivityController(this);

        menuListView = (RecyclerView) findViewById(R.id.main_activity_menu_list);
        layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, true);
        menuListView.setLayoutManager(layoutManager);

        // DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(menuListView.getContext(), RecyclerView.VERTICAL);
        // dividerItemDecoration.setDrawable(getDrawable(R.drawable.list_item_divider));
        ListItemsDividerItemDecoration dividerItemDecoration = new ListItemsDividerItemDecoration(getDrawable(R.drawable.list_item_divider));
        menuListView.addItemDecoration(dividerItemDecoration);

        menuListItems = controller.getMenuListItems();
        // reverse menuListItems to keep visual order after reverseLayout
        // Collections.reverse(menuListItems);

        menuListViewAdapter = new ListItemsAdapter(menuListItems, this, false);
        menuListView.setAdapter(menuListViewAdapter);
    }

    @Override
    public void onAttachedToWindow () {
        super.onAttachedToWindow();
        // handle cutout
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            Cutout cutout = new Cutout(getWindow());
            cutout.addPaddingToViewAtCutoutPosition(phoneStatusWrapper);
        }
    }

    // inform controller window focus changed
    // using onWindowFocusChanged instead of onResume to prevent unexpected behaviours setting lock mode while app is not in focus
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        controller.onActivityWindowFocusChanged(hasFocus);
        if (hasFocus) {
            UI.hideNavigationBar(this);
        }
    }

    // inform controller activity resumed
    @Override
    public void onResume () {
        super.onResume();
        // update time format preferences
        Time.updateIs24HourFormat(this);

        // check if settings night mode changed
        if (preferences.getSettingsNightModeChanged()) {
            preferences.setSettingsNightModeChanged(false);
            // force update theme
            recreate();
        }

        // check if night mode changed
        int updatedCurrentNightMode = getCurrentNightMode();
        if (updatedCurrentNightMode != currentNightmode) {
            currentNightmode = getCurrentNightMode();
            // force update theme
            recreate();
        }

        // cancel notification
        preferences.setNotificationListenerServiceNotificationsEnabled(false);
        // enable ignorable app services
        preferences.setShouldIgnoreAppServices(false);
        // resume controller
        controller.onActivityResume();
    }

    @Override
    public void onPause () {
        super.onPause();

        controller.onActivityPause();
        ActivityManager activityManager = (ActivityManager) getApplicationContext()
                .getSystemService(Context.ACTIVITY_SERVICE);

        // activityManager.moveTaskToFront(getTaskId(), 0);
    }

    /*
     * Generic Dialog, used to inform user about contextual exeptions
     * eg. not available persmissions to access a given feature
     */

    public void removeGenericDialog (Dialog dialog) {
        if (dialog != null) {
            mainActivityWrapper.removeView(dialog);
        }
    }

    public Dialog addGenericDialog () {
        Dialog genericDialog = new Dialog(this);
        mainActivityWrapper.addView(genericDialog, 0);
        return genericDialog;
    }

    // UI
    public void updateMenuListItem (ListItem item, int index) {
        menuListItems.set(index, item);
        menuListViewAdapter.notifyItemRangeChanged(index, 1);
        menuListViewAdapter.notifyDataSetChanged();
    }

    public void menuListItemsChanged () {
        menuListViewAdapter.notifyDataSetChanged();
    }

    public void updateStatusBattery (final int batteryLevel, final boolean isCharging) {
        final Activity activity = this;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (statusBatteryLabel != null) {
                    statusBatteryLabel.setText(Status.batteryLevelToString(activity, batteryLevel, isCharging));
                }

            }
        });
    }

    public void updateStatusSignal (final int signalLevel, final String networkClass) {
        final Activity activity = this;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (statusBatteryLabel != null) {
                    statusSignalLabel.setText(Status.signalToString(activity, signalLevel, networkClass, true));
                }

            }
        });
    }

    private int getCurrentNightMode () {
        return getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
    }

    @Override
    public void onListItemClick(int position) {
        ListItem clickedItem = menuListItems.get(position);
        controller.onMenuItemClick(clickedItem);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        controller.onActivityAskPermissionResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        controller.onActivityRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
