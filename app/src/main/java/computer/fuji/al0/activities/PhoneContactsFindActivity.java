package computer.fuji.al0.activities;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import computer.fuji.al0.R;
import computer.fuji.al0.adapters.ListItemsAdapter;
import computer.fuji.al0.components.Keyboard;
import computer.fuji.al0.components.TextInputMovableCursor;
import computer.fuji.al0.controllers.PhoneContactsFindActivityController;
import computer.fuji.al0.models.KeyboardButtonModel;
import computer.fuji.al0.models.ListItem;
import computer.fuji.al0.utils.LockScreen;
import computer.fuji.al0.utils.UI;

import java.util.ArrayList;
import java.util.Collections;

public class PhoneContactsFindActivity extends AppCompatActivity implements ListItemsAdapter.ListItemListener {
    private PhoneContactsFindActivityController controller;
    private ArrayList<ListItem> contactsListItems;

    private TextInputMovableCursor findQueryTextView;
    private RecyclerView contactsListView;
    private RecyclerView.Adapter contactsListViewAdapter;
    private RecyclerView.LayoutManager layoutManager;
    Keyboard keyboard;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // show activity on lock screen
        LockScreen.addShowOnLockScreenFlags(this);

        setContentView(R.layout.activity_phone_contacts_find);
        UI.hideNavigationBar(this);

        findQueryTextView = (TextInputMovableCursor) findViewById(R.id.phone_contacts_find_query_text_input);
        keyboard = (Keyboard) findViewById(R.id.phone_contacts_find_keyboard);
        // init contacts list's RecyclerView
        contactsListView = (RecyclerView) findViewById(R.id.phone_contacts_find_list);
        layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, true);
        contactsListView.setLayoutManager(layoutManager);

        // initialize activity's controller
        controller = new PhoneContactsFindActivityController(this);
        // populate RecyclerView with contacts list items
        contactsListItems = controller.getContactsListItems();
        contactsListViewAdapter = new ListItemsAdapter(contactsListItems, this, false);
        contactsListView.setAdapter(contactsListViewAdapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(contactsListView.getContext(), RecyclerView.VERTICAL);
        dividerItemDecoration.setDrawable(getDrawable(R.drawable.list_item_divider));
        contactsListView.addItemDecoration(dividerItemDecoration);

        // set keyboard close button
        keyboard.setCloseButtonText(getString(R.string.close_contestual_symbol));
        // set keyboard right button text
        keyboard.setRightActionButtonText(getString(R.string.keyboard_button_right_cancel));
        // bind keyboards events
        keyboard.setKeyboardEventsListener(new Keyboard.KeyboardEventsListener() {
            @Override
            public void onButtonPress(KeyboardButtonModel buttonModel) {
                // use only for delete button
                // delete button need to listen for repeated events
                switch (buttonModel.getId()) {
                    case BUTTON_DELETE:
                        findQueryTextView.deleteAtCursorPosition();
                        controller.onContactQueryChange(findQueryTextView.getText().toString());
                        break;
                    case BUTTON_MOVE_CURSOR_LEFT:
                        findQueryTextView.moveCursorLeft(true);
                        break;
                    case BUTTON_MOVE_CURSOR_RIGHT:
                        findQueryTextView.moveCursorRight(true);
                        break;
                    default:
                        // do nothing
                        break;
                }
            }

            @Override
            public void onButtonTouchStart(KeyboardButtonModel buttonModel) {
                // do nothing
            }

            @Override
            public void onButtonTouchEnd(KeyboardButtonModel buttonModel, boolean isTailTouchEvent) {
                // use touch end in order to ignore unwanted keybaord press
                // ignore BUTTON_DELETE, BUTTON_MOVE_CURSOR which is listened in onButtonPress
                switch (buttonModel.getId()) {
                    case BUTTON_DELETE:
                    case BUTTON_MOVE_CURSOR_LEFT:
                    case BUTTON_MOVE_CURSOR_RIGHT:
                        // do nothing
                        break;
                    default:
                        if (isTailTouchEvent) {
                            findQueryTextView.addCharacter(buttonModel.getKey());
                            controller.onContactQueryChange(findQueryTextView.getText().toString());
                        }
                        break;
                }
            }

            @Override
            public void onButtonSpacePress(KeyboardButtonModel buttonModel) {
                // controller.onKeyboardKeyPress(buttonModel);
                findQueryTextView.addCharacter(buttonModel.getKey());
                controller.onContactQueryChange(findQueryTextView.getText().toString());
            }

            @Override
            public void onButtonRightActionPress() {
                controller.onKeyboardCancelButtonPress();
            }

            @Override
            public void onButtonClosePress() {
                controller.onKeyboardCancelButtonPress();
            }
        });
    }

    // update query
    public void updateFindQueryTextView (String text) {
        // findQueryTextView.setText(text);
        keyboard.setTypedText(text);
    }

    // update contact list item
    public void updateContactsListItems (ArrayList<ListItem> contactsListItems) {
        this.contactsListItems.clear();
        this.contactsListItems.addAll(contactsListItems);
        Collections.reverse(this.contactsListItems);
        contactsListViewAdapter.notifyItemRangeChanged(0, contactsListItems.size());
        contactsListViewAdapter.notifyDataSetChanged();
    }

    // events
    @Override
    public void onListItemClick(int position) {
        controller.onContactsListItemClick(contactsListItems.get(position));
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            UI.hideNavigationBar(this);
        }
    }
}
