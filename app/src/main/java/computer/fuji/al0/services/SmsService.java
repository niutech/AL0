package computer.fuji.al0.services;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.Telephony;

import computer.fuji.al0.models.Contact;
import computer.fuji.al0.models.Sms;
import computer.fuji.al0.utils.PhoneNumber;

import java.util.ArrayList;
import java.util.Date;

import static android.provider.Telephony.TextBasedSmsColumns.MESSAGE_TYPE_SENT;

public class SmsService {
    private static final String SMS_URI_INBOX = "content://sms/inbox";
    private static final String SMS_URI_SENT = "content://sms/sent";
    public static final String SMS_URI_ALL = "content://sms/";
    private static final String sortOrder = Telephony.Sms.DATE.concat(" desc");
    private static final String allSmsLimit = ""; // " LIMIT 10";
    private static Sms lastSmsReceived = null;
    private static Sms lastActivitySeenSms = null;

    // Get last received sms
    public static Sms getLastSmsReceived () {
        return SmsService.lastSmsReceived;
    }

    // get last Activity's seen sms
    public static Sms getLastActivitySeenSms () {
        return SmsService.lastActivitySeenSms;
    }

    // set last Activity's seen sms
    public static void setLastActivitySeenSms (Sms sms) {
        SmsService.lastActivitySeenSms = sms;
    }

    // check if last Tab Activity's sms received is also the last sms received
    // use this method to show a notification relative to phone activity
    // and to check if Tab Activity should fetch new smss
    public static boolean shouldPhoneActivityNotificationBeVisible () {
        if (lastSmsReceived != null) {
            if (lastActivitySeenSms != null) {
                if (lastSmsReceived.getId().equals(lastActivitySeenSms.getId())) {
                    // SmsService and PhoneActivity have the same last sms
                    // no need to update activity list
                    return false;
                } else {
                    // SmsService and PhoneActivity have different last sms
                    // need to show notification
                    return true;
                }
            } else {
                // SmsService has a valid sms, PhoneActivity no, need to show notification
                return true;
            }
        } else {
            // no recent sms received, no need to show notification
            return false;
        }
    }

    // Get all sms from a contact
    public static ArrayList<Sms> getContactSmsList (Context context, Contact contact, Date until) {
        ArrayList<Sms> contactSmsList = new ArrayList<>();
        // create sms query uri
        Uri uri = Uri.parse(SMS_URI_ALL);
        String[] projection = new String[] { Telephony.Sms._ID, Telephony.Sms.ADDRESS, Telephony.Sms.PERSON, Telephony.Sms.SEEN, Telephony.Sms.READ, Telephony.Sms.BODY, Telephony.Sms.DATE, Telephony.Sms.TYPE};
        // add contact number in query
        // String querySelection = Telephony.Sms.ADDRESS.concat("='").concat(contact.getPhoneNumber()).concat("'");
        String querySelection = Telephony.Sms.ADDRESS.concat("=? ")
                .concat(" AND ")
                .concat(Telephony.Sms.DATE).concat(" >= ?");
        String [] selectionArgs = new String[] { PhoneNumber.cleanPhoneNumber(contact.getPhoneNumber()), String.valueOf(until.getTime()) };
        // create cursor
        Cursor cursor = context.getContentResolver().query(uri, projection, querySelection, selectionArgs, sortOrder);
        while (cursor.moveToNext()) {
            // populate contactSmsList with found sms
            String id = cursor.getString(cursor.getColumnIndex(Telephony.Sms._ID));
            String body = cursor.getString(cursor.getColumnIndex(Telephony.Sms.BODY));
            Date date = new Date(cursor.getLong(cursor.getColumnIndex(Telephony.Sms.DATE)));
            Sms.Type type = cursor.getInt(cursor.getColumnIndex(Telephony.Sms.TYPE)) == MESSAGE_TYPE_SENT ? Sms.Type.OUTBOUND : Sms.Type.INBOUND;
            boolean isSeen = cursor.getInt(cursor.getColumnIndex(Telephony.Sms.SEEN)) == 1;
            boolean isRead = cursor.getInt(cursor.getColumnIndex(Telephony.Sms.READ)) == 1;
            contactSmsList.add(new Sms(id, contact, type, body, date, isSeen, isRead));
        }

        cursor.close();

        return contactSmsList;
    }

    // Get all sms from all contacts
    public static ArrayList<Sms> getAllContactSmsList (Context context, Date until) {
        ArrayList<Sms> contactSmsList = new ArrayList<>();

        // create sms query uri
        Uri uri = Uri.parse(SMS_URI_ALL);
        String[] projection = new String[] { Telephony.Sms._ID, Telephony.Sms.ADDRESS, Telephony.Sms.SEEN, Telephony.Sms.READ, Telephony.Sms.DATE, Telephony.Sms.TYPE};
        String querySelection = "".concat(Telephony.Sms.DATE).concat(" >= ?");
        String [] selectionArgs = new String[] { String.valueOf(until.getTime()) };

        // create cursor
        Cursor cursor = context.getContentResolver().query(uri, projection, querySelection, selectionArgs, sortOrder.concat(allSmsLimit));
        while (cursor.moveToNext()) {
            // populate contactSmsList with found sms
            String id = cursor.getString(cursor.getColumnIndex(Telephony.Sms._ID));
            String number = cursor.getString(cursor.getColumnIndex(Telephony.Sms.ADDRESS));
            String body = "";
            Date date = new Date(cursor.getLong(cursor.getColumnIndex(Telephony.Sms.DATE)));
            Sms.Type type = cursor.getInt(cursor.getColumnIndex(Telephony.Sms.TYPE)) == MESSAGE_TYPE_SENT ? Sms.Type.OUTBOUND : Sms.Type.INBOUND;
            Contact contact = ContactsService.getContactFromPhoneNumber(context, number);
            if (contact == null) {
                contact = new Contact(number, number, number);
            }
            boolean isSeen = cursor.getInt(cursor.getColumnIndex(Telephony.Sms.SEEN)) == 1;
            boolean isRead = cursor.getInt(cursor.getColumnIndex(Telephony.Sms.READ)) == 1;

            contactSmsList.add(new Sms(id, contact, type, body, date,  isSeen, isRead));
        }

        cursor.close();

        return contactSmsList;
    }

    public static Sms addSms (Context context, Sms sms) {
        ContentValues smsValues = new ContentValues();
        smsValues.put(Telephony.Sms.ADDRESS, sms.getContact().getPhoneNumber());
        smsValues.put(Telephony.Sms.BODY, sms.getBody());
        smsValues.put(Telephony.Sms.READ, false);
        smsValues.put(Telephony.Sms.SEEN, false);
        Uri addedSmsUri;

        if (sms.getType() == Sms.Type.INBOUND) {
            smsValues.put(Telephony.Sms.TYPE, Telephony.Sms.MESSAGE_TYPE_INBOX);
            addedSmsUri = context.getContentResolver().insert(Uri.parse(SMS_URI_INBOX), smsValues);
        } else {
            smsValues.put(Telephony.Sms.TYPE, MESSAGE_TYPE_SENT);
            addedSmsUri = context.getContentResolver().insert(Uri.parse(SMS_URI_SENT), smsValues);
        }

        String[] projection = new String[] { Telephony.Sms._ID };
        Cursor cursor = context.getContentResolver().query(addedSmsUri, projection, null, null, null);

        while (cursor.moveToNext()) {
            String id = cursor.getString(cursor.getColumnIndex(Telephony.Sms._ID));
            sms.setId(id);
            // set last received sms
            SmsService.lastSmsReceived = sms;
            return sms;
        }

        cursor.close();

        // set last received sms
        SmsService.lastSmsReceived = sms;

        return sms;
    }

    public static void setSmsRead (Context context, Sms sms) {
        setSmsParam(context, sms, Telephony.Sms.READ, 1);
    }

    public static void setSmsSeen (Context context, Sms sms) {
        setSmsParam(context, sms, Telephony.Sms.SEEN, 1);
    }

    public static void deleteSms (Context context, Sms sms) {
        if (sms.getId().trim().length() > 0) {
            Uri uri = Uri.parse(SMS_URI_ALL);
            String querySelection = Telephony.Sms._ID.concat(" = ?");
            String [] selectionArgs = new String[] { sms.getId() };
            context.getContentResolver().delete(uri, querySelection, selectionArgs);
        } else {
            // invalid id
        }
    }

    public static void deleteAllContactSms (Context context, Contact contact) {
        String phoneNumber = contact.getPhoneNumber();
        if (phoneNumber.trim().length() > 0) {
            String wildcardifiedPhoneNumber = PhoneNumber.wildcardifyPhoneNumber(phoneNumber);
            Uri uri = Uri.parse(SMS_URI_ALL);
            String querySelection = Telephony.Sms.ADDRESS.concat(" LIKE ?");
            String [] selectionArgs = new String[] { wildcardifiedPhoneNumber };
            context.getContentResolver().delete(uri, querySelection, selectionArgs);
        } else {
            // invalid id
        }
    }

    private static void setSmsParam (Context context, Sms sms, String param, int paramValue) {
        if (sms.getId().trim().length() > 0) {
            String querySelectionId = "".concat(Telephony.Sms._ID).concat(" = ").concat(sms.getId());
            Uri uri = Uri.parse(SMS_URI_ALL);
            String[] projection = new String[] { Telephony.Sms._ID, param, Telephony.Sms.BODY};
            String querySelection = querySelectionId;
            String [] selectionArgs = null;

            ContentValues values = new ContentValues();
            values.put(param, paramValue);
            int updatedRowsCount = context.getContentResolver().update(uri, values, querySelection, null);
        } else {
            // invalid id passed
        }

    }
}
