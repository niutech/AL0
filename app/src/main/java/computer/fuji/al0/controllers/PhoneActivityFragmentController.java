package computer.fuji.al0.controllers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import computer.fuji.al0.activities.PhoneContactActivity;
import computer.fuji.al0.fragments.PhoneActivityFragment;
import computer.fuji.al0.models.ActivityItem;
import computer.fuji.al0.models.Call;
import computer.fuji.al0.models.Contact;
import computer.fuji.al0.models.Sms;
import computer.fuji.al0.services.ActivityService;
import computer.fuji.al0.services.LogCallService;
import computer.fuji.al0.services.AL0NotificationService;
import computer.fuji.al0.services.SmsReceiverService;
import computer.fuji.al0.services.SmsService;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class PhoneActivityFragmentController {
    private PhoneActivityFragment fragment;
    private ArrayList<ActivityItem> activityList;
    private ActivityService activityService;
    private Timer updateActivityListTimer;

    // use this boolean to understand if this fragment has been displayed,
    // make sure to avoid heavy process while FALSE or PhoneActivity, this fragment parent, will start slow
    private boolean isFirstStart = true;

    // use this boolean to understand if fragment has seen after an onFragmentHide call
    // this variable is useful to understand when ActivityItems can be considered seen
    private boolean isFragmentSeen = false;

    // use this variable to understand the number of activities item to fetch
    private int activityItemToFetch = 20;

    // use this variable to increment the number of activities to fetch
    private int activityItemToFetchIncrement = 20;

    public PhoneActivityFragmentController (PhoneActivityFragment phoneActivityFragment) {
        this.fragment = phoneActivityFragment;
        activityList = new ArrayList<>();
        activityService = new ActivityService();
    }

    private void updateActivityList (boolean updateUI) {        // make sure to empty activity list
        activityList.clear();
        Context context = fragment.getContext();
        if (context != null) {
            ArrayList<ActivityItem> activityItems = activityService.getAllContactsActivityItems (context, activityItemToFetch);
            activityList.addAll(activityItems);

            // update Activity UI
            if (updateUI) {
                fragment.updateActivityList(activityList);
            }
        }
    }

    // update activity list on a different thread
    // use shouldScrollToBottom flag to understand if the list should be scrolled to bottom, in example to show the latest items
    private void updateActivityListOnDifferentThread (final boolean shouldScrollToBottom) {
        new Thread() {
            @Override
            public void run () {
                // update activity list data
                updateActivityList(false);

                // update activity list UI
                fragment.getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        fragment.updateActivityList(activityList);
                        if (shouldScrollToBottom) {
                            fragment.scrollActivityListToBottom();
                        }
                    }
                });
            }
        }.start();
    }

    // update activity list after delay
    // this to make sure call made/received from this activity get listed on LogCallService
    // recursively recall the method when stopActivityListPolling is false
    private void delayedUpdateActivityList (final boolean shouldScrollToBottom, final boolean stopActivityListPolling, boolean isShortDelay) {
        resetUpdateActivityListTimer();

        updateActivityListTimer = new Timer();
        updateActivityListTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                updateActivityList(false);
                Activity activity = fragment.getActivity();
                if (activity != null) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            fragment.updateActivityList(activityList);
                            if (shouldScrollToBottom) {
                                fragment.scrollActivityListToBottom();
                            }

                            if (!stopActivityListPolling) {
                                delayedUpdateActivityList(shouldScrollToBottom, true, false);
                            }
                        }
                    });
                }

            }
        }, isShortDelay ? 25 : 500);
    }

    private void resetUpdateActivityListTimer () {
        if (updateActivityListTimer != null) {
            updateActivityListTimer.cancel();
            updateActivityListTimer = null;
        }
    }

    // mark all sms seen
    private void setSmsSeen () {
        for (int i = 0; i < activityList.size(); i++) {
            ActivityItem item = activityList.get(i);
            if (item.getType() == ActivityItem.Type.SMS) {
                Sms sms = item.getSms();
                if (!sms.getIsSeen()) {
                    // set sms seen
                    SmsService.setSmsSeen(fragment.getContext(), sms);
                    // update item's sms
                    sms.setIsSeen(true);
                    item.setSms(sms);
                    // notify fragment to update UI
                    fragment.updateActivityListItem(item);
                }
            }
        }
    }

    // mark all call seen
    private void setCallSeen () {
        for (int i = 0; i < activityList.size(); i++) {
            ActivityItem item = activityList.get(i);
            if (item.getType() == ActivityItem.Type.CALL) {
                Call call = item.getCall();
                boolean isMissedCall = call.getType() == Call.Type.MISSED;
                if (call.getIsNew() && isMissedCall) {
                    // set call seen
                    LogCallService.setCallSeen(fragment.getContext(), call);
                    // update item's call
                    call.setIsNew(false);
                    item.setCall(call);
                    fragment.updateActivityListItem(item);
                }
            }
        }
    }

    // events
    public void onResume () {
        // check if is not the first start,
        // when isFirstStart is true the parent Activity, PhoneActivity is starting
        // its better to postpone intensive task or PhoneActivity will be slow
        if (!isFirstStart) {
            // updateActivityListOnDifferentThread(false);
            delayedUpdateActivityList(false, false, false);
            registerSmsReceivedCallback();
        }
    }

    public void onPause () {
        setSmsSeen();
        setCallSeen();
        // stop getting callback from sms receiver service
        SmsReceiverService.setOnSmsReceivedCallback(null);
        // update notification service that all recent notifications have been seen
        if (isFragmentSeen) {
            AL0NotificationService.setLastPhoneNotificationIsSeen(true);
        }

        // make sure to prevent timer's task to access fragment
        resetUpdateActivityListTimer();
    }

    public void onFragmentShow () {
        isFragmentSeen = true;

        if (isFirstStart) {
            // this is the first time the fragment has been displayed
            // fetch contact's activity
            isFirstStart = false;
            // updateActivityListOnDifferentThread(true);
            delayedUpdateActivityList(true, true, true);
        } else {
            if (SmsService.shouldPhoneActivityNotificationBeVisible()) {
                // update activity if there are recent notifications
                // updateActivityListOnDifferentThread(true);
                delayedUpdateActivityList(true, true, false);
            }
        }

        // register callback for new sms
        registerSmsReceivedCallback();

        // scroll activity list to bottom
        fragment.scrollActivityListToBottom();
    }

    public void onFragmentHide () {
        // user changed tab
        // set sms seen
        setSmsSeen();
        // set call seen
        setCallSeen();
        // unregister callback for new sms
        SmsReceiverService.setOnSmsReceivedCallback(null);
        // notify notification service that all notification have been seen
        AL0NotificationService.setLastPhoneNotificationIsSeen(true);
        isFragmentSeen = false;

        // cancel update activity timer
        resetUpdateActivityListTimer();
    }

    // when click an activity item open the item's contact's activity passing the id of the clicked item
    public void onPhoneActivityClick (ActivityItem activityItem) {
        Contact activityItemContact = activityItem.getContact();
        String activityItemNumber = "";
        if (activityItemContact != null) {
            activityItemNumber = activityItemContact.getPhoneNumber();
        }

        // calculate the number of items necessary on ContactActivity to be able to display the pressed item
        int minimumContactItemNumberToFetch = 0;
        String itemContactPhoneNumber = activityItem.getContact().getPhoneNumber();
        for (int i = activityList.size() - 1; i >= 0 ; i--) {
            ActivityItem currentItem = activityList.get(i);
            if (currentItem.getContact().getPhoneNumber().equals(itemContactPhoneNumber)) {
                minimumContactItemNumberToFetch = minimumContactItemNumberToFetch + 1;

                if(currentItem.getId().equals(activityItem.getId())) {
                    break;
                }
            }
        }

        Intent intent = new Intent(fragment.getActivity(), PhoneContactActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(PhoneContactActivity.CONTACT_NUMBER_BUNDLE_KEY, activityItemNumber);
        bundle.putString(PhoneContactActivity.SCROLL_TO_ACTIVITY_ITEM_ID_BUNDLE_KEY, activityItem.getId());
        bundle.putInt(PhoneContactActivity.SCROLL_TO_ACTIVITY_ITEM_NUMBER_OF_MINIMUM_ITEMS_BUNDLE_KEY, minimumContactItemNumberToFetch);
        intent.putExtras(bundle);
        fragment.startActivity(intent);
    }

    // on top reach
    // load asynchronously activity incrementing the number of activity to fetch duration
    public void onScrollReachTop () {
        final int previousActivityListSize = activityList.size();
        activityItemToFetch = activityItemToFetch + activityItemToFetchIncrement;

        new Thread() {
            @Override
            public void run () {
                // update activity list data
                updateActivityList(false);

                // update activity list UI
                fragment.getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        fragment.updateActivityList(activityList);
                        fragment.scrollActivityListTo(previousActivityListSize);
                    }
                });
            }
        }.start();
    }

    private void registerSmsReceivedCallback () {
        SmsReceiverService.setOnSmsReceivedCallback(new SmsReceiverService.OnSmsReceivedListener() {
            @Override
            public void onSmsReceived(Sms sms) {
                ActivityItem newItem = ActivityItem.smsToActivityItem(sms);
                activityList.add(newItem);
                // updateActivityList();
                fragment.updateActivityList(activityList);
                fragment.scrollActivityListToBottom();
                SmsService.setLastActivitySeenSms(sms);
            }
        });
    }
}
