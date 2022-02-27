package computer.fuji.al0.controllers;

import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.view.View;

import computer.fuji.al0.R;
import computer.fuji.al0.activities.CallActivity;
import computer.fuji.al0.activities.PhoneContactActivity;
import computer.fuji.al0.activities.PhoneContactsNewActivity;
import computer.fuji.al0.components.Button;
import computer.fuji.al0.components.Dialog;
import computer.fuji.al0.models.ActivityItem;
import computer.fuji.al0.models.Contact;
import computer.fuji.al0.models.Sms;
import computer.fuji.al0.services.ActivityService;
import computer.fuji.al0.services.ContactsService;
import computer.fuji.al0.services.InCallService;
import computer.fuji.al0.services.LogCallService;
import computer.fuji.al0.services.OutCallService;
import computer.fuji.al0.services.SmsReceiverService;
import computer.fuji.al0.services.SmsService;
import computer.fuji.al0.utils.Network;
import computer.fuji.al0.utils.PhoneNumber;
import computer.fuji.al0.utils.Time;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class PhoneContactActivityController {
    private PhoneContactActivity activity;
    private Contact contact;

    private ArrayList<ActivityItem> activityList;
    private ActivityService activityService;
    private Timer updateActivityListTimer;
    private Timer callTimer;
    private boolean isFirstResume = true;
    private boolean isSmsComposeMode = false;
    private boolean isNewContact = false;
    private boolean isTitleSelected = false;
    private String scrollToActivityItemId;
    private ActivityItem selectedItem;

    // use this to listen InCallService's current call events
    private InCallService.InCallServiceEventsListener inCallServiceEventsListener;

    // use this variable to understand the number of activities item to fetch
    private int activityItemToFetch = 20;

    // use this variable to increment the number of activities to fetch
    private int activityItemToFetchIncrement = 20;

    TelephonyManager telephonyManager;
    // status
    // signal strength variables
    private String networkClass = "";
    private int networkStrength = 0;

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
            TelephonyManager telephonyManager = (TelephonyManager) activity.getSystemService(Context.TELEPHONY_SERVICE);
            int simState = telephonyManager.getSimState();
            boolean isSimAvailable = simState != TelephonyManager.SIM_STATE_ABSENT && simState != TelephonyManager.SIM_STATE_UNKNOWN;
            activity.updateStatusSignal(networkStrength, networkClass);
        }
    };

    public PhoneContactActivityController (final PhoneContactActivity activity) {
        this.activity = activity;

        activityService = new ActivityService();

        contact = new Contact("", "", "");

        Intent intent = activity.getIntent();

        // get contact from activity's intent
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            onIntentBundle(bundle);
        }

        // check intent from external app
        String action = intent.getAction();
        if (action != null) {
            onIntentAction(intent, action);
        }

        activityList = new ArrayList<>();

        activity.updateContact(contact, isNewContact);
        // updateActivityList();
    }

    public void onActivityWindowFocusChanged (boolean hasFocus) {
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
                        setButtonCurrentCallVisible(true);
                        startCallTimer();
                        break;
                    default:
                        // hide call menu list item
                        setButtonCurrentCallVisible(false);
                        break;
                }
            } else {
                // hide call menu list item
                setButtonCurrentCallVisible(false);
            }
        } else {
            stopCallTimer();
        }
    }

    private void setButtonCurrentCallVisible (boolean isButtonCurrentCallVisible) {
        if (isButtonCurrentCallVisible) {
            // get dial number
            String dialNumber = InCallService.getCurrentPhoneNumber();
            // get contact from number
            Contact contact = ContactsService.getContactFromPhoneNumber(activity, dialNumber);
            // show contact name or phone number in list item
            String callContactName = contact != null ? contact.getName() : PhoneNumber.formatPhoneNumber(dialNumber);
            String callSymbol = InCallService.getIsInboundCall() ? activity.getString(R.string.call_inbound_symbol) : activity.getString(R.string.call_outbound_symbol);
            String space = " ";
            activity.setButtonCurrentCallText(callSymbol.concat(space.concat(callContactName)));
            activity.setButtonCurrentCallVisible(true);
        } else {
            activity.setButtonCurrentCallVisible(false);
        }
    }

    private void registerInCallServiceCallback () {
        inCallServiceEventsListener = new InCallService.InCallServiceEventsListener() {
            @Override
            public void onCallRemoved(android.telecom.Call call) {
                activity.setButtonCurrentCallVisible(false);
                stopCallTimer();
            }
        };

        InCallService.setInCallServiceEventsListener(inCallServiceEventsListener);
    }

    private void startCallTimer () {
        activity.setButtonCurrentCallTime(Time.secondsToHMS(InCallService.getCurrentCallDuration()));
        callTimer = new Timer ();
        callTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        activity.setButtonCurrentCallTime(Time.secondsToHMS(InCallService.getCurrentCallDuration()));
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

    private void updateActivityList () {
        // make sure to empty activity list
        activityList.clear();

        ArrayList<ActivityItem> activityItems = activityService.getAllContactActivityItems (activity, contact, activityItemToFetch);
        activityList.addAll(activityItems);
    }

    // getter
    public ArrayList<ActivityItem> getActivityList() {
        return activityList;
    }

    public boolean getIsNewContact () {
        return this.isNewContact;
    }

    // events
    public void onButtonClosePress () {
        activity.finish();
    }

    public void onBackButtonPress () {
        // hide sms composer on back button press
        if (isSmsComposeMode) {
            activity.setSmsComposerVisible(false);
        }
    }

    public void onButtonSmsPress () {
        // deselect current selected button, if any
        activity.showButtonDeleteAllItems();
        selectedItem = null;
        activity.setSelectedActivityItem(null);
        // set in sms compose mode
        isSmsComposeMode = true;
        activity.setSmsComposerVisible(true);
    }

    public void onButtonCallPress() {
        OutCallService.addCall(activity, contact.getPhoneNumber());
    }

    public void onButtonCurrentCallPress () {
        startCallActivity();
    }

    public void onButtonDeleteAllItemsPress () {
        showDeleteAllItemsDialog();
    }

    public void onButtonDeleteItemPress () {
        if (selectedItem != null) {
            showDeleteItemDialog();
        } else {
            // no selected item to delete
        }
    }

    public void onButtonCopySmsPress () {
        if (selectedItem != null && selectedItem.getType() == ActivityItem.Type.SMS) {
            ClipboardManager clipboard = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
            String smsText = selectedItem.getSms().getBody();
            ClipData clipData = ClipData.newPlainText("AL0_copy_sms", smsText);
            clipboard.setPrimaryClip(clipData);
        }
    }

    public void onButtonAddContactPress () {
        activity.setTitleActive(false);
        isTitleSelected = false;
        Intent intent = new Intent(activity, PhoneContactsNewActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(PhoneContactsNewActivity.CONTACT_NUMBER_BUNDLE_KEY, contact.getPhoneNumber());
        intent.putExtras(bundle);
        activity.startActivity(intent);
    }

    public void onButtonDeleteContactPress () {
        if (!isNewContact) {
            showDeleteContactDialog();
        } else {
            // cannot delete not saved contact
        }
    }

    public void onSmsSent (Sms smsSent) {
        ActivityItem newItem = ActivityItem.smsToActivityItem(smsSent);
        activityList.add(newItem);
        activity.addActivityItem(newItem);
    }

    private void deleteSelectedItem () {
        if (selectedItem != null) {
            // check if is a call item
            if (selectedItem.getType() == ActivityItem.Type.CALL) {
                LogCallService.deleteCall(activity, selectedItem.getCall());
            } else {
                // is a sms item
                SmsService.deleteSms(activity, selectedItem.getSms());
            }

            String selectedItemId = selectedItem.getId();
            // remove selected item from activity's list view
            activity.removeActivityItem(selectedItem);
            selectedItem = null;
            activity.setSelectedActivityItem(selectedItem);
            activity.showButtonDeleteAllItems();
            // remove deleted item from activityList
            for (int i = 0; i < activityList.size(); i++) {
                if (activityList.get(i).getId().equals(selectedItemId)) {
                    activityList.remove(i);
                    return;
                }
            }
        }
    }

    private void deleteAllItems () {
        SmsService.deleteAllContactSms(activity, contact);
        LogCallService.deleteAllContactCall(activity, contact);
        updateActivityList();
        activity.updateActivityList(activityList);
    }

    private void deleteContact () {
        if (!isNewContact) {
            // remove contact from service
            ContactsService.deleteContact(activity, contact);
            // update activity with new contact view, show delete all items button
            setContactFromPhoneNumber(contact.getPhoneNumber());
            activity.updateContact(contact, isNewContact);
            activity.setTitleActive(false);
            isTitleSelected = false;
        } else {
            // cannot delete not saved contact
        }
    }

    private void showDeleteItemDialog () {
        final Dialog dialog = activity.addGenericDialog();
        dialog.setText(
                activity.getResources().getString(R.string.phone_contact_activity_dialog_delete_item_title),
                activity.getResources().getString(R.string.phone_contact_activity_dialog_delete_item_body),
                activity.getResources().getString(R.string.phone_contact_activity_dialog_cancel_button),
                activity.getResources().getString(R.string.phone_contact_activity_dialog_delete_button));



        Button deleteButton = dialog.getDialogButtonActionRight();
        Button cancelButton = dialog.getDialogButtonActionLeft();

        // on click ok
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteSelectedItem();
                activity.removeGenericDialog(dialog);
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

    private void showDeleteAllItemsDialog () {
        final Dialog dialog = activity.addGenericDialog();
        dialog.setText(
                activity.getResources().getString(R.string.phone_contact_activity_dialog_delete_all_title),
                activity.getResources().getString(R.string.phone_contact_activity_dialog_delete_all_body),
                activity.getResources().getString(R.string.phone_contact_activity_dialog_cancel_button),
                activity.getResources().getString(R.string.phone_contact_activity_dialog_delete_button));



        Button deleteButton = dialog.getDialogButtonActionRight();
        Button cancelButton = dialog.getDialogButtonActionLeft();

        // on click ok
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteAllItems();
                activity.removeGenericDialog(dialog);
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

    private void showDeleteContactDialog () {
        // build and show the dialog
        final Dialog dialog = activity.addGenericDialog();
        // build dialog body, check if need phone and massging default app settings
        dialog.setText(
                activity.getResources().getString(R.string.phone_contact_activity_dialog_delete_contact_title),
                activity.getResources().getString(R.string.phone_contact_activity_dialog_delete_contact_body),
                activity.getResources().getString(R.string.phone_contact_activity_dialog_cancel_button),
                activity.getResources().getString(R.string.phone_contact_activity_dialog_delete_button));



        Button deleteButton = dialog.getDialogButtonActionRight();
        Button cancelButton = dialog.getDialogButtonActionLeft();

        // on click ok
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteContact();
                activity.removeGenericDialog(dialog);
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

    public void onTitlePress () {
        selectedItem = null;
        // toggle title selection
        // show delete/add contact or deselect title and show delete all items button
        if (isTitleSelected) {
            activity.showButtonDeleteAllItems();
        } else {
            // deselect item
            activity.setSelectedActivityItem(null);
            if (!isNewContact) {
                // a known contact can be deleted
                activity.showButtonDeleteContact();
            } else {
                // a new contact can be saved
                activity.showButtonAddContact();
            }
        }

        isTitleSelected = !isTitleSelected;

        activity.setTitleActive(isTitleSelected);
    }


    public void onPhoneContactActivityClick (ActivityItem item) {
        boolean isSelectedItemNonNull = selectedItem != null;
        boolean isItemNonNull = item != null;
        // if user click on a selected item, unselect the item
        if (isSelectedItemNonNull && isItemNonNull && item.getId().equals(selectedItem.getId())) {
            selectedItem = null;
            activity.showButtonDeleteAllItems();
        } else {
            selectedItem = item;
            activity.showButtonDeleteItem();
            // show copy button if selected item is an sms
            activity.setShowButtonCopySmsVisible(selectedItem.getType() == ActivityItem.Type.SMS);
        }

        activity.setSelectedActivityItem(selectedItem);
    }

    public void onActivityResume () {
        // if is first resume update immediately activityList
        if (isFirstResume) {
            updateActivityList();
            activity.updateActivityList(activityList);
            isFirstResume = false;
            // scroll to the passed item id if any
            if (scrollToActivityItemId != null) {
                activity.scrollActivityListTo(scrollToActivityItemId);
                // find the selected activity
                for(ActivityItem item : activityList) {
                    if (item.getId().equals(scrollToActivityItemId)) {
                        // set as selected item
                        selectedItem = item;
                        activity.showButtonDeleteItem();
                        activity.setSelectedActivityItem(selectedItem);
                        // show copy button if selected item is an sms
                        activity.setShowButtonCopySmsVisible(selectedItem.getType() == ActivityItem.Type.SMS);
                    }
                }

            }
        } else {
            delayedUpdateActivityList(false);
        }

        Contact contactServiceNewContact = ContactsService.getNewAddedContact();
        ContactsService.resetNewAddedContact();
        // check if there is a recent added contact on contact service
        if (contactServiceNewContact != null && contact != null) {
            // check if is the current contact
            String contactServiceNewContactCleanedNumber = PhoneNumber.cleanPhoneNumber(contactServiceNewContact.getPhoneNumber());
            String contactCleanedNumber = PhoneNumber.cleanPhoneNumber(contact.getPhoneNumber());
            if (contactServiceNewContactCleanedNumber.equals(contactCleanedNumber)) {
                // update name
                isNewContact = false;
                contact = contactServiceNewContact;
                activity.updateContact(contact, false);
            }
        }

        // listen for new sms received
        registerSmsReceivedCallback();

        // register battery events
        activity.registerReceiver(batteryManagerReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        // register signal events
        telephonyManager = (TelephonyManager) activity.getSystemService(Context.TELEPHONY_SERVICE);
        telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
    }

    // on top reach
    // load asynchronously activity incrementing the day duration
    public void onScrollReachTop () {
        final int previousActivityListSize = activityList.size();
        activityItemToFetch = activityItemToFetch + activityItemToFetchIncrement;

        new Thread() {
            @Override
            public void run () {
                // update activity list data
                updateActivityList();

                // update activity list UI
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        activity.updateActivityList(activityList);
                        activity.scrollActivityListTo(previousActivityListSize);
                    }
                });
            }
        }.start();
    }

    // update activity list after delay
    // this to make sure call made/received from this activity get listed on LogCallService
    // recursively recall the method when stopActivityListPolling is false
    private void delayedUpdateActivityList (final boolean stopActivityListPolling) {
        resetUpdateActivityListTimer();

        updateActivityListTimer = new Timer();
        updateActivityListTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                updateActivityList();

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        activity.updateActivityList(activityList);
                        if (!stopActivityListPolling) {
                            delayedUpdateActivityList(true);
                        }
                    }
                });
            }
        }, 500);
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
        // prevent activityList to get update if activity is not running
        resetUpdateActivityListTimer();
        // set sms read
        setSmsRead();
        // stop listening new sms received
        SmsReceiverService.setOnSmsReceivedCallback(null);
        // stop listening InCallService callback
        inCallServiceEventsListener = null;
        stopCallTimer();
    }

    // reset activityListTimer
    private void resetUpdateActivityListTimer () {
        if (updateActivityListTimer != null) {
            updateActivityListTimer.cancel();
        }
    }

    // set all sms read
    private void setSmsRead () {
        for (int i = 0; i < activityList.size(); i++) {
            ActivityItem item = activityList.get(i);
            // check if item is SMS
            if (item.getType() == ActivityItem.Type.SMS) {
                Sms sms = item.getSms();
                // check if sms is not read and is an inbound sms
                if (!sms.getIsRead() && sms.getType() == Sms.Type.INBOUND) {
                    // set sms read
                    SmsService.setSmsRead(activity, sms);
                    // update item's sms
                    sms.setIsRead(true);
                    item.setSms(sms);
                    // notify activity to update UI
                    activity.updateActivityListItem(item);
                }
            }
        }
    }

    // utils

    // check for passed intent's bundle
    // use this method to check whatever the activity need to start to given behaviour
    // in example show a given contact or show a particular activity item
    // bundle keys are defined on PhoneContactActivity
    private void onIntentBundle (Bundle bundle) {
        String contactId = bundle.getString(PhoneContactActivity.CONTACT_ID_BUNDLE_KEY);
        String contactNumber = bundle.getString(PhoneContactActivity.CONTACT_NUMBER_BUNDLE_KEY);
        Boolean shouldStartInSmsMode = bundle.getBoolean(PhoneContactActivity.START_IN_SMS_MODE_BUNDLE_KEY);
        scrollToActivityItemId = bundle.getString(PhoneContactActivity.SCROLL_TO_ACTIVITY_ITEM_ID_BUNDLE_KEY);
        // use this number to fetch the minimum number of items needed to show the "scrollToActivityItemId" related item
        int scrollToNumberOfMinimumItems = bundle.getInt(PhoneContactActivity.SCROLL_TO_ACTIVITY_ITEM_NUMBER_OF_MINIMUM_ITEMS_BUNDLE_KEY);

        if (scrollToNumberOfMinimumItems > activityItemToFetch) {
            activityItemToFetch = scrollToNumberOfMinimumItems + 2;
        }

        // Get the contact
        if (contactId != null) {
            // Get contact from ID
            contact = ContactsService.getContact(activity, contactId);
        } else {
            setContactFromPhoneNumber(contactNumber);
        }

        // check if should start in SMS mode
        if (shouldStartInSmsMode) {
            isSmsComposeMode = true;
            activity.setSmsComposerVisible(true);
        }
    }

    // check for passed intent's actions
    // used by external app to send sms
    private void onIntentAction (Intent intent, String action) {
        switch (action) {
            // action send, used to start send sms intent
            case Intent.ACTION_SENDTO:
                // check intent string
                String intentString = intent.getDataString();
                // check if is send to sms
                if (intentString.indexOf("smsto:") > -1) {
                    intentString = intentString.replace("smsto:", "");
                    String phoneNumber = PhoneNumber.cleanPhoneNumber(intentString);
                    setContactFromPhoneNumber(phoneNumber);
                    isSmsComposeMode = true;
                    activity.setSmsComposerVisible(true);
                }

                break;
            default:
                // insert other cases to handle
                break;
        }
    }

    private void setContactFromPhoneNumber (String contactNumber) {
        // check if is passed a valid contact number
        if (contactNumber != null) {
            // Get contact from number
            contact = ContactsService.getContactFromPhoneNumber(activity, contactNumber);
            if (contact == null) {
                // create a new unknown contact
                isNewContact = true;
                contact = new Contact(contactNumber, contactNumber, contactNumber);
            }
        } else {
            // create an empty contact
            contact = new Contact("Invalid Contact", "", "");
        }
    }

    private void registerSmsReceivedCallback () {
        SmsReceiverService.setOnSmsReceivedCallback(new SmsReceiverService.OnSmsReceivedListener() {
            @Override
            public void onSmsReceived(Sms sms) {
                // compare phone number to understand if sms is from/to the current contact
                // not using ID because it could be an unknown contact or contact can be removed than added making the id different
                String smsCleanedPhoneNumber = PhoneNumber.cleanPhoneNumber(sms.getContact().getPhoneNumber());
                String contactCleanedPhoneNumber = PhoneNumber.cleanPhoneNumber(contact.getPhoneNumber());
                if (smsCleanedPhoneNumber.equals(contactCleanedPhoneNumber)) {
                    // set previous sms read
                    setSmsRead();
                    // this contact send an sms
                    ActivityItem newItem = ActivityItem.smsToActivityItem(sms);
                    activityList.add(newItem);
                    activity.addActivityItem(newItem);
                } else {
                    // do nothing
                }
            }
        });
    }

    private void startCallActivity () {
        Intent callActivityIntent = new Intent(activity, CallActivity.class);
        activity.startActivity(callActivityIntent);
    }
}
