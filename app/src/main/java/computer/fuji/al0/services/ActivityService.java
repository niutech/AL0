package computer.fuji.al0.services;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CallLog;
import android.provider.Telephony;

import computer.fuji.al0.models.ActivityItem;
import computer.fuji.al0.models.Call;
import computer.fuji.al0.models.Contact;
import computer.fuji.al0.models.Sms;
import computer.fuji.al0.utils.PhoneNumber;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import static android.provider.Telephony.TextBasedSmsColumns.MESSAGE_TYPE_SENT;

public class ActivityService {
    // get a contact recent activity item
    // query both SMS and LogCall Uris to get recent activity items
    public ArrayList<ActivityItem> getAllContactActivityItems (Context context, Contact contact, int numberOfItems) {
        boolean includeSmsBody = true;
        // Sms cursor
        // make sure find the contact number querying both formatted and unformatted ones
        String wildcardifiedPhoneNumber = PhoneNumber.wildcardifyPhoneNumber(contact.getPhoneNumber());
        String cursorSmsQuerySelection = Telephony.Sms.ADDRESS.concat(" LIKE ?");
        String [] cursorSmsSelectionArgs = new String[] { wildcardifiedPhoneNumber };
        Cursor cursorContactSms = getSmsCursor(context, cursorSmsQuerySelection, cursorSmsSelectionArgs, numberOfItems, includeSmsBody);
        // Call cursor
        // make sure find the contact number querying both formatted and unformatted ones
        String cursorCallQuerySelection = CallLog.Calls.NUMBER.concat(" LIKE ?");
        String [] cursorCallSelectionArgs = new String[] { wildcardifiedPhoneNumber };
        Cursor cursorContactCall = getLogCallCursor(context, cursorCallQuerySelection, cursorCallSelectionArgs, numberOfItems);

        ArrayList<ActivityItem> activityItems = getCursorsActivityItems(context, cursorContactSms, cursorContactCall, numberOfItems, includeSmsBody);

        //cursorSmsSelectionArgs.clone();
        cursorContactSms.close();
        cursorContactCall.close();
        cursorContactSms = null;
        cursorContactCall = null;
        return activityItems;
    }

    // get all contacts recent activity item
    // query both SMS and LogCall Uris to get recent activity items
    public ArrayList<ActivityItem> getAllContactsActivityItems (Context context, int numberOfItems) {
        boolean includeSmsBody = false;
        Cursor cursorAllContactsSms = getSmsCursor(context, null, null, numberOfItems, includeSmsBody);
        Cursor cursorAllContactsCall = getLogCallCursor(context, null, null, numberOfItems);

        ArrayList<ActivityItem> activityItems = getCursorsActivityItems(context, cursorAllContactsSms, cursorAllContactsCall, numberOfItems, includeSmsBody);

        cursorAllContactsSms.close();
        cursorAllContactsCall.close();
        cursorAllContactsSms = null;
        cursorAllContactsCall = null;
        return activityItems;
    }

    // given 2 cursor, CursorA and cursorB, return the latest numberOfItems acticvityItems
    private ArrayList<ActivityItem> getCursorsActivityItems (Context context, Cursor cursorA, Cursor cursorB, int numberOfItems, boolean includeSmsBody) {
        ArrayList<ActivityItem> activityItems = new ArrayList<>();

        cursorA.moveToNext();
        cursorB.moveToNext();

        while (cursorsHasNext(cursorA, cursorB) && activityItems.size() <= numberOfItems) {
            ActivityItem currentActivityItem = getLatestItem(context, cursorA, cursorB, includeSmsBody);
            // check if current activity item is not null
            // when current activity item is null means both cursors finished iteration
            if (currentActivityItem != null) {
                activityItems.add(currentActivityItem);
            } else {
                // stop while, no more item available
                break;
            }

        }

        cursorA.close();
        cursorB.close();

        cursorA = null;
        cursorB = null;

        Collections.reverse(activityItems);
        return activityItems;
    }

    // Sms

    // get all contacts sms cursor
    // use includeSmsBody flag to read or ignore Body field
    // when body isnt needed to be displayed, in example on PhoneActivityFragment, its more efficient to exclude from the query
    private Cursor getSmsCursor (Context context, String querySelection, String [] selectionArgs, int limit, boolean includeSmsBody) {
        Uri uri = Uri.parse(SmsService.SMS_URI_ALL);
        String [] projection;
        if (includeSmsBody) {
            projection = new String[] { Telephony.Sms._ID, Telephony.Sms.ADDRESS, Telephony.Sms.BODY, Telephony.Sms.SEEN, Telephony.Sms.READ, Telephony.Sms.DATE, Telephony.Sms.TYPE};
        } else {
            projection = new String[] { Telephony.Sms._ID, Telephony.Sms.ADDRESS, Telephony.Sms.SEEN, Telephony.Sms.READ, Telephony.Sms.DATE, Telephony.Sms.TYPE};
        }

        String sortOrder = Telephony.Sms.DATE.concat(" desc").concat(" LIMIT ").concat(String.valueOf(limit));
        return context.getContentResolver().query(uri, projection, querySelection, selectionArgs, sortOrder);
    }

    // get sms from current cursor position
    private Sms getSmsAtCurrentCursorPosition (Context context, Cursor cursor, boolean includeSmsBody) {
        String id = cursor.getString(cursor.getColumnIndex(Telephony.Sms._ID));
        String number = cursor.getString(cursor.getColumnIndex(Telephony.Sms.ADDRESS));
        String body = "";
        if (includeSmsBody) {
            body = cursor.getString(cursor.getColumnIndex(Telephony.Sms.BODY));
        } else {
            body = "";
        }

        Date date = new Date(cursor.getLong(cursor.getColumnIndex(Telephony.Sms.DATE)));
        Sms.Type type = cursor.getInt(cursor.getColumnIndex(Telephony.Sms.TYPE)) == MESSAGE_TYPE_SENT ? Sms.Type.OUTBOUND : Sms.Type.INBOUND;
        Contact contact = ContactsService.getContactFromPhoneNumber(context, number);
        if (contact == null) {
            contact = new Contact(number, number, number);
        }
        boolean isSeen = cursor.getInt(cursor.getColumnIndex(Telephony.Sms.SEEN)) == 1;
        boolean isRead = cursor.getInt(cursor.getColumnIndex(Telephony.Sms.READ)) == 1;

        return new Sms(id, contact, type, body, date,  isSeen, isRead);
    }

    // CallLog

    private Cursor getLogCallCursor (Context context, String querySelection, String [] selectionArgs, int limit) {
        Uri uri = Uri.parse(LogCallService.CALL_LOG_URI);
        String[] projection = new String[] { CallLog.Calls._ID, CallLog.Calls.NUMBER, CallLog.Calls.DATE, CallLog.Calls.TYPE, CallLog.Calls.DURATION, CallLog.Calls.NEW };
        String sortOrder = CallLog.Calls.DATE.concat(" desc").concat(" LIMIT ").concat(String.valueOf(limit));
        return context.getContentResolver().query(uri, projection, querySelection, selectionArgs, sortOrder);
    }

    private Call getCallAtCurrentCursorPosition (Context context, Cursor cursor) {
        String id = cursor.getString(cursor.getColumnIndex(CallLog.Calls._ID));
        Date date = new Date(cursor.getLong(cursor.getColumnIndex(CallLog.Calls.DATE)));
        int type = cursor.getInt(cursor.getColumnIndex(CallLog.Calls.TYPE));
        int duration = cursor.getInt(cursor.getColumnIndex(CallLog.Calls.DURATION));
        boolean isNew = cursor.getInt(cursor.getColumnIndex(CallLog.Calls.NEW)) == 1;
        String number = cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER));

        Contact contact = ContactsService.getContactFromPhoneNumber(context, number);
        if (contact == null) {
            contact = new Contact(number, number, number);
        }

        return new Call(id, contact, type, date, duration, isNew);
    }

    private ActivityItem getLatestItem (Context context, Cursor cursorSms, Cursor cursorCall, boolean includeSmsBody) {
        Sms currentSms;
        Call currentCall;

        // check if cursor sms have new items
        if (cursorSms.isAfterLast() || cursorSms.isClosed()) {
           currentSms = null;
        } else {
            currentSms = getSmsAtCurrentCursorPosition(context, cursorSms, includeSmsBody);

        }

        // check if cursor call have new items
        if (cursorCall.isAfterLast() || cursorCall.isClosed()) {
            currentCall = null;
        } else {
            currentCall = getCallAtCurrentCursorPosition(context, cursorCall);
        }

        if (currentSms == null && currentCall == null) {
            // both elements null, return null
            return  null;
        } else if (currentSms == null) {
            // sms null, return call
            cursorCall.moveToNext();
            return ActivityItem.callToActivityItem(currentCall);
        } else if (currentCall == null) {
            // call null, return sms
            cursorSms.moveToNext();
            return ActivityItem.smsToActivityItem(currentSms);
        } else if (currentSms.getDate().compareTo(currentCall.getDate()) >= 0) {
            // sms is later than call, return sms
            cursorSms.moveToNext();
            return ActivityItem.smsToActivityItem(currentSms);
        } else {
            // call is later then sms, return sms
            cursorCall.moveToNext();
            return ActivityItem.callToActivityItem(currentCall);
        }
    }

    // utils
    private boolean cursorsHasNext (Cursor cursorA, Cursor cursorB) {
        // boolean bothCursorsAreLast = cursorA.isLast() && cursorB.isLast();
        boolean bothCursorsAreAfterLast = cursorA.isAfterLast() && cursorB.isAfterLast();
        return !bothCursorsAreAfterLast;
    }
}
