package computer.fuji.al0.activities;

import android.app.Activity;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import computer.fuji.al0.R;
import computer.fuji.al0.adapters.PhoneContactActivityAdapter;
import computer.fuji.al0.components.Button;
import computer.fuji.al0.components.Dialog;
import computer.fuji.al0.controllers.PhoneContactActivityController;
import computer.fuji.al0.fragments.SmsComposerFragment;
import computer.fuji.al0.models.ActivityItem;
import computer.fuji.al0.models.Contact;
import computer.fuji.al0.models.Sms;
import computer.fuji.al0.utils.Cutout;
import computer.fuji.al0.utils.LockScreen;
import computer.fuji.al0.utils.PhoneNumber;
import computer.fuji.al0.utils.Status;
import computer.fuji.al0.utils.UI;

import java.util.ArrayList;
import java.util.Collections;

public class PhoneContactActivity extends AppCompatActivity implements PhoneContactActivityAdapter.PhoneContactActivityListener {
    public static String CONTACT_ID_BUNDLE_KEY = "CONTACT_ID";
    public static String CONTACT_NUMBER_BUNDLE_KEY = "CONTACT_NUMBER";
    public static String START_IN_SMS_MODE_BUNDLE_KEY = "START_IN_SMS_MODE";
    public static String SCROLL_TO_ACTIVITY_ITEM_ID_BUNDLE_KEY = "SCROLL_TO_ACTIVITY_ITEM_ID";
    public static String SCROLL_TO_ACTIVITY_ITEM_NUMBER_OF_MINIMUM_ITEMS_BUNDLE_KEY = "SCROLL_TO_ACTIVITY_ITEM_NUMBER_OF_MINIMUM_ITEMS";

    private ArrayList<ActivityItem> activityList;

    private PhoneContactActivityController controller;
    private ConstraintLayout activityWrapper;
    private RecyclerView activityListView;
    private RecyclerView.LayoutManager layoutManager;
    private PhoneContactActivityAdapter phoneContactActivityAdapter;
    private SmsComposerFragment smsComposerFragment;
    private LinearLayout footer;

    // Status
    private LinearLayout phoneStatusWrapper;
    private TextView statusSignalLabel;
    private TextView statusBatteryLabel;

    private TextView title;
    private TextView number;
    private TextView labelNoActivity;

    private Button buttonClose;
    private Button buttonSms;
    private Button buttonCall;
    private Button buttonCurrentCall;
    private Button buttonCurrentCallTime;
    private Button buttonAddContact;
    private Button buttonDeleteAllItems;
    private Button buttonDeleteContact;
    private Button buttonDeleteItem;
    private Button buttonCopySms;
    private View viewSpacerButtonCopySms;


    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // show activity on lock screen
        LockScreen.addShowOnLockScreenFlags(this);

        setContentView(R.layout.activity_phone_contact);
        UI.hideNavigationBar(this);

        // status
        phoneStatusWrapper = (LinearLayout) findViewById(R.id.phone_contact_activity_status);
        statusSignalLabel = (TextView) findViewById(R.id.phone_contact_activity_status_signal);
        statusBatteryLabel = (TextView) findViewById(R.id.phone_contact_activity_status_battery);

        activityWrapper = (ConstraintLayout) findViewById(R.id.phone_contact_activity_main_wrapper);
        title = (TextView) findViewById(R.id.phone_contact_activity_title);
        number = (TextView) findViewById(R.id.phone_contact_activity_number);
        labelNoActivity = (TextView) findViewById(R.id.phone_contact_activity_label_no_activity);
        buttonClose = (Button) findViewById(R.id.phone_contact_activity_button_close);
        buttonSms = (Button) findViewById(R.id.phone_contact_activity_button_sms);
        buttonCall = (Button) findViewById(R.id.phone_contact_activity_button_call);
        buttonCurrentCall = (Button) findViewById(R.id.phone_contact_activity_button_current_call);
        buttonCurrentCallTime = (Button) findViewById(R.id.phone_contact_activity_button_current_call_time);
        buttonDeleteAllItems = (Button) findViewById(R.id.phone_contact_activity_button_delete_all_itens);
        buttonDeleteContact = (Button) findViewById(R.id.phone_contact_activity_button_delete_contact);
        buttonDeleteItem = (Button) findViewById(R.id.phone_contact_activity_button_delete);
        buttonCopySms = (Button) findViewById(R.id.phone_contact_activity_button_copy);
        viewSpacerButtonCopySms = (View) findViewById(R.id.phone_contact_activity_button_copy_spacer);
        buttonDeleteItem.setIsDisabled(true);
        buttonAddContact = (Button) findViewById(R.id.phone_contact_activity_button_add_contact);
        // viewSpacerButtonAddContact = (View) findViewById(R.id.phone_contact_activity_spacer_button_add_contact);
        footer = (LinearLayout) findViewById(R.id.phone_contact_activity__footer);

        if (savedInstanceState == null){
            smsComposerFragment = (SmsComposerFragment) getSupportFragmentManager().findFragmentById(R.id.phone_contact_activity__sms_composer);
            smsComposerFragment.getView().setVisibility(View.GONE);
            smsComposerFragment.setSmsComposerFragmentEventsListener(new SmsComposerFragment.SmsComposerFragmentEventsListener() {
                @Override
                public void onSmsSent(Sms smsSent) {
                    // update activity list
                    // should show the just sent sms
                    // updateActivityList(controller.getActivityList());
                    controller.onSmsSent(smsSent);
                    // controller.onActivityResume();
                }

                @Override
                public void onClose() {
                    setSmsComposerVisible(false);
                }
            });
        }


        controller = new PhoneContactActivityController(this);

        activityListView = (RecyclerView) findViewById(R.id.phone_contact_activity__list);
        layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, true);
        activityListView.setLayoutManager(layoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(activityListView.getContext(), RecyclerView.VERTICAL);
        dividerItemDecoration.setDrawable(getDrawable(R.drawable.list_item_divider));
        activityListView.addItemDecoration(dividerItemDecoration);

        activityList = new ArrayList<>();
        activityList.addAll(controller.getActivityList());
        Collections.reverse(activityList);

        phoneContactActivityAdapter = new PhoneContactActivityAdapter(this, activityList, this);
        activityListView.setAdapter(phoneContactActivityAdapter);
        layoutManager.scrollToPosition(activityList.size() - 1);

        buttonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controller.onButtonClosePress();
            }
        });

        buttonSms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controller.onButtonSmsPress();
            }
        });

        buttonCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controller.onButtonCallPress();
            }
        });

        buttonCurrentCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controller.onButtonCurrentCallPress();
            }
        });

        buttonCurrentCallTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controller.onButtonCurrentCallPress();
            }
        });

        buttonDeleteItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                controller.onButtonDeleteItemPress();
            }
        });

        buttonCopySms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                controller.onButtonCopySmsPress();
            }
        });

        buttonAddContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controller.onButtonAddContactPress();
            }
        });

        buttonDeleteAllItems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                controller.onButtonDeleteAllItemsPress();
            }
        });

        buttonDeleteContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                controller.onButtonDeleteContactPress();
            }
        });

        title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controller.onTitlePress();
            }
        });
        title.setSelected(true);

        activityListView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                boolean reachTop = !activityListView.canScrollVertically(-1);
                // boolean reachBottom = !activityListView.canScrollVertically(View.FOCUS_DOWN);
                if (reachTop) {
                    controller.onScrollReachTop();
                }
            }
        });

        showButtonDeleteAllItems();
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

    @Override
    protected void onResume() {
        super.onResume();

        controller.onActivityResume();
    }

    @Override
    protected void onPause() {
        super.onPause();

        controller.onActivityPause();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        controller.onActivityWindowFocusChanged(hasFocus);
        if (hasFocus) {
            UI.hideNavigationBar(this);
        }
    }

    private void hideUpperActionButtons () {
        buttonDeleteAllItems.setVisibility(View.GONE);
        buttonDeleteItem.setVisibility(View.GONE);
        buttonCopySms.setVisibility(View.GONE);
        viewSpacerButtonCopySms.setVisibility(View.GONE);
        buttonAddContact.setVisibility(View.GONE);
        buttonDeleteContact.setVisibility(View.GONE);
    }

    public void showButtonDeleteAllItems () {
        hideUpperActionButtons();
        buttonDeleteAllItems.setVisibility(View.VISIBLE);
    }

    // show button delete
    public void showButtonDeleteItem () {
        hideUpperActionButtons();
        buttonDeleteItem.setVisibility(View.VISIBLE);
    }

    public void setShowButtonCopySmsVisible (boolean visible) {
        buttonCopySms.setVisibility(visible ? View.VISIBLE : View.GONE);
        viewSpacerButtonCopySms.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    public void showButtonAddContact () {
        hideUpperActionButtons();
        buttonAddContact.setVisibility(View.VISIBLE);
    }

    public void showButtonDeleteContact () {
        hideUpperActionButtons();
        buttonDeleteContact.setVisibility(View.VISIBLE);
    }

    public void setButtonCurrentCallVisible (boolean isCurrentCallVisible) {
        buttonCurrentCall.setVisibility(isCurrentCallVisible ? View.VISIBLE : View.GONE);
        buttonCurrentCallTime.setVisibility(isCurrentCallVisible ? View.VISIBLE : View.GONE);
        buttonCall.setVisibility(isCurrentCallVisible ? View.GONE : View.VISIBLE);
    }

    public void setButtonCurrentCallText (String text) {
        buttonCurrentCall.setText(text);
    }

    public void setButtonCurrentCallTime (String time) {
        buttonCurrentCallTime.setText(time);
    }

    // underline title when action button is related to the contact
    // use this when show delete/add contact buttons
    public void setTitleActive (boolean isActive) {
        if (isActive) {
            title.setPaintFlags( title.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        } else {
            title.setPaintFlags( title.getPaintFlags() & (~Paint.UNDERLINE_TEXT_FLAG));
        }
    }

    private void hideShowDeleteContactButton () {
        buttonDeleteContact.setVisibility(View.GONE);
        buttonAddContact.setVisibility(View.GONE);
        setTitleActive(false);
        buttonDeleteItem.setVisibility(View.VISIBLE);
    }

    private void updateDeleteAllItemsButton () {
        if (this.activityList.size() > 0) {
            buttonDeleteAllItems.setIsDisabled(false);
        } else {
            buttonDeleteAllItems.setIsDisabled(true);
        }
    }

    public void updateContact (Contact contact, boolean isNewContact) {
        title.setText(contact.getName());
        if (isNewContact) {
            number.setText(getString(R.string.phone_contact_activity_label_new_contact));
            title.setText(PhoneNumber.formatPhoneNumber(contact.getPhoneNumber()));
        } else {
            number.setText(PhoneNumber.formatPhoneNumber(contact.getPhoneNumber()));
        }

        showButtonDeleteAllItems();
        smsComposerFragment.setContact(contact);
    }

    public void updateActivityListItem (ActivityItem item) {
        for (int i = 0; i < activityList.size(); i++) {
            if (activityList.get(i).getId().equals(item.getId())) {
                activityList.set(i, item);
                phoneContactActivityAdapter.notifyItemRangeChanged(i, 1);
                phoneContactActivityAdapter.notifyDataSetChanged();

                refreshActivityListVisibility();
                return;
            }
        }
    }

    public void updateActivityList (ArrayList<ActivityItem> activityList) {
        if (this.activityList != null) {
            this.activityList.clear();
            this.activityList.addAll(activityList);
            Collections.reverse(this.activityList);
            phoneContactActivityAdapter.notifyItemRangeChanged(0, activityList.size());
            phoneContactActivityAdapter.notifyDataSetChanged();
            layoutManager.scrollToPosition(0);
            updateDeleteAllItemsButton();
        }

        refreshActivityListVisibility();
    }

    public void addActivityItem (ActivityItem item) {
        this.activityList.add(0, item);
        phoneContactActivityAdapter.notifyItemInserted(0);
        layoutManager.scrollToPosition(0);
        updateDeleteAllItemsButton();

        refreshActivityListVisibility();
    }

    public void removeActivityItem (ActivityItem item) {
        // get item to delete index
        for (int i = 0; i < activityList.size(); i++) {
            ActivityItem currentItem = activityList.get(i);
            if (currentItem.getId().equals(item.getId())) {
                // delete item from activity list
                this.activityList.remove(i);
                phoneContactActivityAdapter.notifyItemRemoved(i);
                updateDeleteAllItemsButton();
            }
        }

        refreshActivityListVisibility();
    }

    // scroll activity list view to the item with the passed id
    public void scrollActivityListTo (String activityItemId) {
        // search for activityList id's, when found scroll the list view to the found item position
        for (int i = activityList.size() - 1; i >= 0; i--) {
            if (activityList.get(i).getId().equals(activityItemId)) {
                int scrollToPosition = i > 0 ? i-1 : i;
                layoutManager.scrollToPosition(scrollToPosition);
                return;
            }
        }
    }

    public void scrollActivityListTo (int index) {
        layoutManager.scrollToPosition(index);
    }

    public void setSmsComposerVisible (boolean smsComposerVisible) {
        if (smsComposerFragment != null) {
            if (smsComposerVisible) {
                phoneStatusWrapper.setVisibility(View.VISIBLE);
                footer.setVisibility(View.GONE);
                smsComposerFragment.getView().setVisibility(View.VISIBLE);
            } else {
                phoneStatusWrapper.setVisibility(View.GONE);
                footer.setVisibility(View.VISIBLE);
                smsComposerFragment.getView().setVisibility(View.GONE);
            }

            // check if activity list exsist
            // it could still not exsist when this method is called by the activity's controller constructor
            if (activityList != null) {
                layoutManager.scrollToPosition(0);
            }

        } else {
            // Sms Composer Frgamnet not available
        }
    }

    private void refreshActivityListVisibility () {
        if (this.activityList.size() > 0) {
            labelNoActivity.setVisibility(View.GONE);
        } else {
            labelNoActivity.setVisibility(View.VISIBLE);
        }
    }

    /*
     * Generic Dialog, used to inform user about contextual confirmation
     * eg. confirm delete an user
     */

    public void removeGenericDialog (Dialog dialog) {
        if (dialog != null) {
            activityWrapper.removeView(dialog);
        }

        setAllActivityWrapperChildVisible(true);
    }

    public Dialog addGenericDialog () {
        setAllActivityWrapperChildVisible(false);
        Dialog genericDialog = new Dialog(this);
        activityWrapper.addView(genericDialog, 0);
        return genericDialog;
    }

    private void setAllActivityWrapperChildVisible (boolean areVisible) {
        for (int i = 0; i < activityWrapper.getChildCount(); i++) {
            activityWrapper.getChildAt(i).setVisibility(areVisible ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public void onBackPressed() {
        if (smsComposerFragment != null && smsComposerFragment.getView().getVisibility() == View.VISIBLE) {
            controller.onBackButtonPress();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onPhoneContactActivityClick(int position) {
        ActivityItem selectedItem = activityList.get(position);
        controller.onPhoneContactActivityClick(selectedItem);
    }

    // set a list view element selected
    public void setSelectedActivityItem (ActivityItem selectedItem) {
        if (buttonDeleteContact.getVisibility() == View.VISIBLE || buttonAddContact.getVisibility() == View.VISIBLE) {
            hideShowDeleteContactButton();
        }

        if (selectedItem != null) {
            phoneContactActivityAdapter.setSelectedItemID(selectedItem.getId());
            buttonDeleteItem.setIsDisabled(false);
        } else {
            phoneContactActivityAdapter.setSelectedItemID(null);
            buttonDeleteItem.setIsDisabled(true);
        }

        setTitleActive(false);
    }

    // Status
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
}
