package computer.fuji.al0.activities;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import computer.fuji.al0.R;
import computer.fuji.al0.adapters.ListItemsAdapter;
import computer.fuji.al0.components.Button;
import computer.fuji.al0.components.Dialog;
import computer.fuji.al0.components.ListItemsDividerItemDecoration;
import computer.fuji.al0.controllers.SettingsActivityController;
import computer.fuji.al0.models.ListItem;
import computer.fuji.al0.utils.Cutout;
import computer.fuji.al0.utils.LockScreen;
import computer.fuji.al0.utils.UI;

import java.util.ArrayList;
import java.util.Collections;

public class SettingsActivity extends AppCompatActivity implements ListItemsAdapter.ListItemListener {
    private SettingsActivityController controller;
    private ArrayList<ListItem> menuListItems;

    private LinearLayout mainActivityWrapper;
    private LinearLayout footerWrapper;
    private Button buttonFocusMode;
    private Button buttonClose;
    private Button buttonUp;
    private Button buttonDown;
    private View buttonUpDownSpacer;
    private Button buttonSet;
    private RecyclerView menuListView;
    private ListItemsAdapter menuListViewAdapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        UI.hideNavigationBar(this);

        // show activity on lock screen
        LockScreen.addShowOnLockScreenFlags(this);

        controller = new SettingsActivityController(this);

        mainActivityWrapper = (LinearLayout) findViewById(R.id.settings_activity_wrapper);
        footerWrapper = (LinearLayout) findViewById(R.id.constraint_footer_wrapper);
        buttonFocusMode = (Button) findViewById(R.id.settings_activity_button_keep_lock);
        buttonClose = (Button) findViewById(R.id.settings_activity_button_close);
        buttonUp = (Button) findViewById(R.id.settings_activity_button_up);
        buttonDown = (Button) findViewById(R.id.settings_activity_button_down);
        buttonUpDownSpacer = (View) findViewById(R.id.settings_activity_button_up_down_spacer);
        buttonSet = (Button) findViewById(R.id.settings_activity_button_set);
        menuListView = (RecyclerView) findViewById(R.id.settings_activity_menu_list);
        layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, true);
        menuListView.setLayoutManager(layoutManager);

        // DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(menuListView.getContext(), RecyclerView.VERTICAL);
        // dividerItemDecoration.setDrawable(getDrawable(R.drawable.list_item_divider));
        ListItemsDividerItemDecoration dividerItemDecoration = new ListItemsDividerItemDecoration(getDrawable(R.drawable.list_item_divider));
        menuListView.addItemDecoration(dividerItemDecoration);

        menuListItems = controller.getMenuListItems();
        // reverse menuListItems to keep visual order after reverseLayout
        Collections.reverse(menuListItems);

        menuListViewAdapter = new ListItemsAdapter(menuListItems, this, false);
        menuListView.setAdapter(menuListViewAdapter);

        buttonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controller.onButtonClosePress();
            }
        });

        buttonFocusMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                controller.toggleFocusMode();
            }
        });

        buttonSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                controller.onButtonSetPress();
            }
        });

        buttonUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                controller.onButtonUpPress();
            }
        });

        buttonDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                controller.onButtonDownPress();
            }
        });

        controller.updateListState();
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
    public void onResume() {
        super.onResume();
    }

    // UI
    public void updateMenuListItem(ListItem item) {
        for (int index = 0; index < menuListItems.size(); index ++) {
            if (menuListItems.get(index).getId().equals(item.getId())) {
                menuListItems.set(index, item);
                menuListViewAdapter.notifyItemRangeChanged(index, 1);
            }
        }
    }

    public void setSelectedMenuListItem (ListItem item) {
        if (item != null) {
            menuListViewAdapter.setSelectedItemID(item.getId());
        } else {
            menuListViewAdapter.setSelectedItemID(null);
        }
    }

    public void updateMenuList () {
        menuListItems.clear();
        menuListItems.addAll(controller.getMenuListItems());
        Collections.reverse(menuListItems);
        menuListViewAdapter.notifyDataSetChanged();
    }

    public void setButtonFocusModeVisible(boolean isVisible) {
        buttonFocusMode.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    public void setButtonFocusModeActive(boolean isActive) {
        buttonFocusMode.setIsActive(isActive);
    }

    public void setButtonsUpDownVisible (boolean areVisible) {
        buttonUpDownSpacer.setVisibility(areVisible ? View.VISIBLE : View.GONE);
        buttonDown.setVisibility(areVisible ? View.VISIBLE : View.GONE);
        buttonUp.setVisibility(areVisible ? View.VISIBLE : View.GONE);
    }

    public void setButtonsUpDownEnabled (boolean areEnabled) {
        buttonDown.setIsDisabled(!areEnabled);
        buttonUp.setIsDisabled(!areEnabled);
    }

    public void setButtonSetVisible (boolean isVisible) {
        buttonSet.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    public void setButtonSetEnabled (boolean isEnabled) {
        buttonSet.setIsDisabled(!isEnabled);
    }

    /*
     * Generic Dialog, used to inform user about contextual exeptions
     * eg. not available permissions to access a given feature
     */

    public void removeGenericDialog (Dialog dialog) {
        footerWrapper.setVisibility(View.VISIBLE);

        if (dialog != null) {
            mainActivityWrapper.removeView(dialog);
        }
    }

    public Dialog addGenericDialog () {
        Dialog genericDialog = new Dialog(this);
        footerWrapper.setVisibility(View.GONE);
        mainActivityWrapper.addView(genericDialog, 0);
        return genericDialog;
    }

    @Override
    public void onListItemClick(int position) {
        ListItem clickedItem = menuListItems.get(position);
        controller.onMenuItemClick(clickedItem);
    }

    @Override
    public void onBackPressed() {
        controller.onButtonClosePress();
    }
}
