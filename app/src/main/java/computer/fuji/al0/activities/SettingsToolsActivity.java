package computer.fuji.al0.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;

import computer.fuji.al0.R;
import computer.fuji.al0.adapters.ListItemsAdapter;
import computer.fuji.al0.components.Button;
import computer.fuji.al0.components.Keyboard;
import computer.fuji.al0.components.ListItemsDividerItemDecoration;
import computer.fuji.al0.components.TextInputMovableCursor;
import computer.fuji.al0.controllers.SettingsToolsActivityController;
import computer.fuji.al0.models.KeyboardButtonModel;
import computer.fuji.al0.models.ListItem;
import computer.fuji.al0.utils.ClipboardUtils;
import computer.fuji.al0.utils.LockScreen;
import computer.fuji.al0.utils.UI;

public class SettingsToolsActivity  extends AppCompatActivity implements ListItemsAdapter.ListItemListener {
    private SettingsToolsActivityController controller;
    private ArrayList<ListItem> menuListItems;

    private Button buttonClose;
    private Button buttonSet;
    private View buttonSetDivider;
    private TextInputMovableCursor textInputFindAppQuery;
    private Button buttonSetNothing;
    private Button buttonAllApps;
    private Button buttonOpenApp;
    private Button buttonOpenAppSymbol;
    private Button buttonFindApp;
    private RelativeLayout menuListViewWrapper;
    private RecyclerView menuListView;
    private View menuListViewFadeUpDown;
    private View menuListViewFadeDownUp;
    private ListItemsAdapter menuListViewAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private LinearLayout footerWrapper;
    private Keyboard keyboard;

    public enum Layout { DEFAULT, LIST_APPS_WITH_FOOTER, LIST_APPS_WITH_KEYBOARD }
    private Layout currentLayout = Layout.DEFAULT;

    // screen pixels density
    private float dp;
    //  initial menu list padding bottom
    int menuListViewInitialPaddingBottom = 0;
    int menuListViewInitialPaddingTop = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_tools);
        UI.hideNavigationBar(this);

        // show activity on lock screen
        LockScreen.addShowOnLockScreenFlags(this);

        dp = getResources().getDisplayMetrics().density;

        textInputFindAppQuery = (TextInputMovableCursor) findViewById(R.id.settings_tools_activity_find_query_text_input);
        buttonClose = (Button) findViewById(R.id.settings_activity_tools_button_close);
        buttonSet = (Button) findViewById(R.id.settings_activity_tools_button_set);
        buttonSetDivider = (View) findViewById(R.id.settings_activity_tools_button_set_divider);
        buttonSetNothing = (Button) findViewById(R.id.settings_activity_tools_button_set_nothing);
        buttonAllApps =  (Button) findViewById(R.id.settings_activity_tools_button_all_apps);
        buttonOpenApp = (Button) findViewById(R.id.settings_activity_tools_button_open_app);
        buttonOpenAppSymbol = (Button) findViewById(R.id.settings_activity_tools_button_open_app_symbol);
        buttonFindApp = (Button) findViewById(R.id.settings_activity_tools_button_find);
        footerWrapper = (LinearLayout) findViewById(R.id.constraint_footer_wrapper);
        keyboard = (Keyboard) findViewById(R.id.settings_tools_activity_find_keyboard);
        // set keyboard close button contextual
        keyboard.setCloseButtonText(getString(R.string.close_contestual_symbol));
        // set keyboard right action button text, cancel
        keyboard.setRightActionButtonText(getString(R.string.settings_tools_activity_keyboard_cancel));

        controller = new SettingsToolsActivityController(this);

        menuListViewWrapper = (RelativeLayout) findViewById(R.id.settings_tools_activity_wrapper);
        menuListView = (RecyclerView) findViewById(R.id.settings_tools_activity_menu_list);
        menuListViewInitialPaddingBottom = menuListView.getPaddingBottom();
        menuListViewInitialPaddingTop =  menuListView.getPaddingTop();
        menuListViewFadeUpDown = (View) findViewById(R.id.settings_tools_activity_list_fade_up_down);
        menuListViewFadeDownUp = (View) findViewById(R.id.settings_tools_activity_list_fade_down_up);
        layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, true);
        menuListView.setLayoutManager(layoutManager);

        // ListItemsDividerItemDecoration dividerItemDecoration = new ListItemsDividerItemDecoration(menuListView.getContext(), RecyclerView.VERTICAL);
        // dividerItemDecoration.setDrawable(getDrawable(R.drawable.list_item_divider));
        ListItemsDividerItemDecoration dividerItemDecoration = new ListItemsDividerItemDecoration(getDrawable(R.drawable.list_item_divider));
        menuListView.addItemDecoration(dividerItemDecoration);

        menuListItems = new ArrayList<>();
        menuListItems.addAll(controller.getMenuListItems());
        // reverse menuListItems to keep visual order after reverseLayout
        // Collections.reverse(menuListItems);

        menuListViewAdapter = new ListItemsAdapter(menuListItems, this, false);
        menuListView.setAdapter(menuListViewAdapter);

        buttonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controller.onButtonClosePress();
            }
        });

        buttonSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controller.onButtonSetPress();
            }
        });

        buttonSetNothing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controller.onButtonSetNothingPress();
            }
        });

        buttonAllApps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controller.onButtonAllAppsPress();
            }
        });

        buttonOpenApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controller.onButtonOpenAppPress();
            }
        });

        buttonOpenAppSymbol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controller.onButtonOpenAppPress();
            }
        });

        buttonFindApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controller.onButtonFindPress();
            }
        });

        keyboard.setKeyboardEventsListener(new Keyboard.KeyboardEventsListener() {

            @Override
            public void onButtonPress(KeyboardButtonModel buttonModel) {
                // after an error reset label if user edit the message
                // calling setInComposeMode
                switch (buttonModel.getId()) {
                    case BUTTON_DELETE:
                        textInputFindAppQuery.deleteAtCursorPosition();
                        controller.onTextInputFindAppQueryChange(textInputFindAppQuery.getText().toString());
                        break;
                    case BUTTON_DELETE_WORD:
                        textInputFindAppQuery.deleteWordAtCursorPosition();
                        controller.onTextInputFindAppQueryChange(textInputFindAppQuery.getText().toString());
                        break;
                    case BUTTON_MOVE_CURSOR_LEFT:
                        textInputFindAppQuery.moveCursorLeft(true);
                        break;
                    case BUTTON_MOVE_CURSOR_RIGHT:
                        textInputFindAppQuery.moveCursorRight(true);
                        break;
                    case BUTTON_MOVE_CURSOR_RIGHT_WORD:
                        textInputFindAppQuery.moveCursorRightWord();
                        break;
                    case BUTTON_MOVE_CURSOR_LEFT_WORD:
                        textInputFindAppQuery.moveCursorLeftWord();
                        break;
                    case BUTTON_PASTE:
                        // get clipboard content
                        String pasteString = ClipboardUtils.getPlainText(getBaseContext());
                        if (pasteString != null) {
                            for (int i = 0; i < pasteString.length(); i ++) {
                                textInputFindAppQuery.addCharacter("" + pasteString.charAt(i));
                            }

                            controller.onTextInputFindAppQueryChange(textInputFindAppQuery.getText().toString());
                        }

                        break;
                    default:
                        // do nothing
                        break;
                }
            }

            @Override
            public void onButtonTouchStart(KeyboardButtonModel buttonModel) {

            }

            @Override
            public void onButtonTouchEnd(KeyboardButtonModel buttonModel, boolean isTailTouchEvent) {
                switch (buttonModel.getId()) {
                    case BUTTON_DELETE:
                    case BUTTON_MOVE_CURSOR_LEFT:
                    case BUTTON_MOVE_CURSOR_RIGHT:
                    case BUTTON_PASTE:
                        // do nothing
                        break;
                    default:
                        if (isTailTouchEvent) {
                            textInputFindAppQuery.addCharacter(buttonModel.getKey());
                            controller.onTextInputFindAppQueryChange(textInputFindAppQuery.getText().toString());
                        }
                        break;
                }
            }

            @Override
            public void onButtonSpacePress(KeyboardButtonModel buttonModel) {
                textInputFindAppQuery.addCharacter(buttonModel.getKey());
                controller.onTextInputFindAppQueryChange(textInputFindAppQuery.getText().toString());
            }

            @Override
            public void onButtonRightActionPress() {
                resetTextInputFindAppQuery();
                controller.onButtonActionPress();
            }

            @Override
            public void onButtonClosePress() {
                resetTextInputFindAppQuery();
                controller.onButtonCloseKeyboardPress();
            }
        });

        controller.onActivityReady();
        setLayout(Layout.DEFAULT);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            UI.hideNavigationBar(this);
        }

        controller.onActivityWindowFocusChanged(hasFocus);
    }

    public void updateMenuListItem (ListItem item, int index) {
        menuListItems.set(index, item);
        menuListViewAdapter.notifyItemRangeChanged(index, 1);
        menuListViewAdapter.notifyDataSetChanged();
    }

    public void updateMenuListItems (ArrayList<ListItem> items) {
        menuListItems.clear();
        menuListItems.addAll(items);
        menuListViewAdapter.notifyItemRangeChanged(0, menuListItems.size());
        menuListViewAdapter.notifyDataSetChanged();
    }

    public void setSetButtonVisible (boolean visible) {
        buttonSet.setVisibility(visible ? View.VISIBLE : View.GONE);
        buttonSetDivider.setVisibility(visible ? View.VISIBLE : View.GONE);
        buttonSetNothing.setVisibility(visible ? View.VISIBLE : View.GONE);
        // update keyboard right action button
        if (visible) {
            keyboard.setRightActionButtonText(getString(R.string.settings_tools_activity_button_set));
        }
    }

    public void setButtonAllAppsVisible (boolean visible) {
        buttonAllApps.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    public void setOpenAppButtonVisible (boolean visible) {
        buttonOpenApp.setVisibility(visible ? View.VISIBLE : View.GONE);
        buttonOpenAppSymbol.setVisibility(visible ? View.VISIBLE : View.GONE);
        // update keyboard right action button
        if (visible) {
            keyboard.setRightActionButtonText(getString(R.string.settings_tools_activity_button_open_app));
        }
    }

    public void setSetButtonEnabled (boolean enabled) {
        buttonSet.setIsDisabled(!enabled);
        keyboard.setKeyboardRightActionButtonEnabled(enabled);
    }

    public void setOpenAppButtonEnabled (boolean enabled) {
        buttonOpenApp.setIsDisabled(!enabled);
        buttonOpenAppSymbol.setIsDisabled(!enabled);
        keyboard.setKeyboardRightActionButtonEnabled(enabled);
    }

    public void setSelectedMenuListItem (ListItem item) {
        if (item != null) {
            menuListViewAdapter.setSelectedItemID(item.getId());
        } else {
            menuListViewAdapter.setSelectedItemID(null);
        }
    }

    public void resetTextInputFindAppQuery () {
        textInputFindAppQuery.setText("");
    }

    public void setLayout (Layout layout) {
        currentLayout = layout;
        switch (currentLayout) {
            case LIST_APPS_WITH_FOOTER:
                footerWrapper.setVisibility(View.VISIBLE);
                textInputFindAppQuery.setVisibility(View.GONE);
                keyboard.setVisibility(View.GONE);
                buttonFindApp.setVisibility(View.VISIBLE);
                break;
            case LIST_APPS_WITH_KEYBOARD:
                footerWrapper.setVisibility(View.GONE);
                textInputFindAppQuery.setVisibility(View.VISIBLE);
                keyboard.setVisibility(View.VISIBLE);
                menuListView.scrollToPosition(0);
                break;
            case DEFAULT:
            default:
                footerWrapper.setVisibility(View.VISIBLE);
                buttonFindApp.setVisibility(View.GONE);
                textInputFindAppQuery.setVisibility(View.GONE);
                keyboard.setVisibility(View.GONE);
                break;
        }

        updateMenuListViewWrapperPadding(layout);
    }

    // update menuListViewWrapper padding to fit UI components according to layout
    private void updateMenuListViewWrapperPadding (Layout layout) {
        // menu list view wrapper
        int menuListViewWrapperPaddingBottom = 0;
        int menuListViewPaddingTop = menuListViewInitialPaddingTop;
        int menuListViewPaddingBottom = menuListViewInitialPaddingBottom;

        switch (currentLayout) {
            case LIST_APPS_WITH_FOOTER:
                // menuListViewWrapperPaddingBottom = (int) (dp * 92);
                menuListViewWrapperPaddingBottom = (int) (dp * 52);
                menuListViewPaddingTop = (int) (dp * 8);
                menuListViewPaddingBottom = (int) (dp * 44);
                menuListViewFadeUpDown.setVisibility(View.VISIBLE);
                menuListViewFadeDownUp.setVisibility(View.VISIBLE);
                break;
            case LIST_APPS_WITH_KEYBOARD:
                // menuListViewWrapperPaddingBottom = (int) (dp * 224);
                menuListViewWrapperPaddingBottom = (int) (dp * 184);
                menuListViewPaddingTop = (int) (dp * 8);
                menuListViewPaddingBottom = (int) (dp * 44);
                menuListViewFadeUpDown.setVisibility(View.VISIBLE);
                menuListViewFadeDownUp.setVisibility(View.VISIBLE);
                break;
            case DEFAULT:
            default:
                menuListViewFadeUpDown.setVisibility(View.GONE);
                menuListViewFadeDownUp.setVisibility(View.GONE);
                menuListViewWrapperPaddingBottom = 0;
                break;
        }

        menuListViewWrapper.setPadding(menuListViewWrapper.getPaddingLeft(), menuListViewWrapper.getPaddingTop(), menuListViewWrapper.getPaddingRight(),menuListViewWrapperPaddingBottom);
        menuListView.setPadding(menuListView.getPaddingLeft(), menuListViewPaddingTop, menuListView.getPaddingRight(), menuListViewPaddingBottom);
    }

    // Events
    @Override
    public void onListItemClick(int position) {
        ListItem clickedItem = menuListItems.get(position);
        controller.onMenuItemClick(clickedItem);
    }
}
