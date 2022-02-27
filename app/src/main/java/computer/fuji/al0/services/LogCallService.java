package computer.fuji.al0.services;

import android.app.NotificationManager;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CallLog;

import computer.fuji.al0.models.Call;
import computer.fuji.al0.models.Contact;
import computer.fuji.al0.utils.PhoneNumber;

import java.util.ArrayList;
import java.util.Date;

public class LogCallService {
    public static String CALL_LOG_URI = "content://call_log/calls";
    private static final String allCallLimit = ""; // " LIMIT 10";
    private static String sortOrder = CallLog.Calls.DATE.concat(" desc");

    // get all call log from a contact
    public static ArrayList<Call> getContactLogCallList (Context context, Contact contact, Date until) {
        ArrayList<Call> contactCallList = new ArrayList<>();
        // create call query uri
        Uri uri = Uri.parse(CALL_LOG_URI);
        String[] projection = new String[] { CallLog.Calls._ID, CallLog.Calls.CACHED_NAME, CallLog.Calls.NUMBER, CallLog.Calls.DATE, CallLog.Calls.TYPE, CallLog.Calls.DURATION, CallLog.Calls.NEW };
        // add contact number in query
        String querySelection =
                CallLog.Calls.NUMBER.concat("=? ")
                        .concat(" AND ")
                        .concat(CallLog.Calls.DATE).concat(" >= ?");

        // set contact phone number as argument
        // use a regex to remove unwanted chars like parentheses and dashes
        String[] selectionArgs = new String[] { PhoneNumber.cleanPhoneNumber(contact.getPhoneNumber()), String.valueOf(until.getTime()) };
        // create cursor
        Cursor cursor = context.getContentResolver().query(uri, projection, querySelection, selectionArgs, sortOrder);
        while (cursor.moveToNext()) {
            // populate contactCallList with found call
            String id = cursor.getString(cursor.getColumnIndex(CallLog.Calls._ID));
            Date date = new Date(cursor.getLong(cursor.getColumnIndex(CallLog.Calls.DATE)));
            int type = cursor.getInt(cursor.getColumnIndex(CallLog.Calls.TYPE));
            int duration = cursor.getInt(cursor.getColumnIndex(CallLog.Calls.DURATION));
            boolean isNew = cursor.getInt(cursor.getColumnIndex(CallLog.Calls.NEW)) == 1;
            contactCallList.add(new Call(id, contact, type, date, duration, isNew));
        }

        cursor.close();

        return contactCallList;
    }

    // get all call log from all contacts
    public static ArrayList<Call> getAllContactLogCallList (Context context, Date until) {
        ArrayList<Call> contactCallList = new ArrayList<>();
        // create call query uri
        Uri uri = Uri.parse(CALL_LOG_URI);
        String[] projection = new String[] { CallLog.Calls._ID, CallLog.Calls.CACHED_NAME, CallLog.Calls.NUMBER, CallLog.Calls.DATE, CallLog.Calls.TYPE, CallLog.Calls.DURATION, CallLog.Calls.NEW };
        String querySelection = "".concat(CallLog.Calls.DATE).concat(" >= ?");
        String [] selectionArgs = new String[] { String.valueOf(until.getTime()) };

        // create cursor
        Cursor cursor = context.getContentResolver().query(uri, projection, querySelection, selectionArgs, sortOrder.concat(allCallLimit));
        while (cursor.moveToNext()) {
            // populate contactCallList with found call
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

            contactCallList.add(new Call(id, contact, type, date, duration, isNew));
        }

        cursor.close();

        return contactCallList;
    }

    // Mark a call as seen
    // set new property false
    public static void setCallSeen (Context context, Call call) {
        setCallParam(context, call, CallLog.Calls.NEW, 0);
        NotificationListenerService.cancelMissedCallNotifications();
    }

    public static void deleteCall (Context context, Call call) {
        if (call.getId().trim().length() > 0) {
            Uri uri = Uri.parse(CALL_LOG_URI);
            String querySelection = CallLog.Calls._ID.concat(" = ?");
            String [] selectionArgs = new String[] { call.getId() };
            context.getContentResolver().delete(uri, querySelection, selectionArgs);
        } else {
            // invalid id
        }
    }

    public static void deleteAllContactCall (Context context, Contact contact) {
        String phoneNumber = contact.getPhoneNumber();
        if (phoneNumber.trim().length() > 0) {
            String wildcardifiedPhoneNumber = PhoneNumber.wildcardifyPhoneNumber(phoneNumber);
            Uri uri = Uri.parse(CALL_LOG_URI);
            String querySelection = CallLog.Calls.NUMBER.concat(" LIKE ?");
            String [] selectionArgs = new String[] { wildcardifiedPhoneNumber };
            context.getContentResolver().delete(uri, querySelection, selectionArgs);
        } else {
            // invalid id
        }
    }

    private static void setCallParam (Context context, Call call, String param, int paramValue) {
        if (call.getId().trim().length() > 0) {
            String querySelectionId = "".concat(CallLog.Calls._ID).concat(" = ").concat(call.getId());
            Uri uri = Uri.parse(CALL_LOG_URI);
            String[] projection = new String[] { CallLog.Calls._ID, param};
            String querySelection = querySelectionId;
            String [] selectionArgs = null;

            ContentValues values = new ContentValues();
            values.put(param, paramValue);
            context.getContentResolver().update(uri, values, querySelectionId, null);
        } else {
            // invalid id passed
        }

    }
}
