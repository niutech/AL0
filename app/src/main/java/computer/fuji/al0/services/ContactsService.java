package computer.fuji.al0.services;

import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.telephony.PhoneNumberUtils;

import computer.fuji.al0.models.Contact;
import computer.fuji.al0.utils.PhoneNumber;

import java.util.ArrayList;
import java.util.List;

public class ContactsService {
    private final static String CONTACTS_QUERY_SORT_ORDER = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY + " COLLATE NOCASE ASC";

    private static Contact newAddedContact;
    private static Contact newDeletedContact;

    private static ArrayList<Contact> phoneBookContacts;

    // query variables
    private static String id;
    private static Cursor cursorInfo;
    private static Cursor cursorInfoNumber;
    private static String currentContactName;
    private static String currentContactPhoneNumber;

    private static String[] projectionContacts = new String [] {
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
            ContactsContract.CommonDataKinds.Phone.NUMBER,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY
    };


    public static List<Contact> getContacts (Context context, boolean forceFetch) {
        List<Contact> phoneBookContacts = getPhoneBookContacts(context, forceFetch);
        // add sim contacts
        // List<Contact> simContacts = getSimContacts(context);
        // phoneBookContacts.addAll(simContacts);

        return phoneBookContacts;
    }

    public static Contact getContact (Context context, String id) {
        for (Contact contact : getContacts(context, false)) {
            if (contact.getId().equals(id)) {
                return contact;
            }
        }

        return new Contact("CONTACT_NOT_FOUND", "", "");
    }

    public static Contact getContactFromPhoneNumber(Context context, String number) {
        for (Contact contact : getContacts(context, false)) {
            if (PhoneNumberUtils.compare(contact.getPhoneNumber(), number)) {
                return contact;
            }
        }

        return null;
    }

    public static void deleteContact (Context context, Contact contact) {
        if (contact.getId().trim().length() > 0) {
            Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, contact.getId());
            context.getContentResolver().delete(uri, null, null);

            // remove contact from cached collection
            for (int i = 0; i < phoneBookContacts.size(); i++) {
                Contact currentContact = phoneBookContacts.get(i);
                if (currentContact.getId().equals(contact.getId())) {
                    phoneBookContacts.remove(i);
                    newDeletedContact = contact;
                    return;
                }
            }
        } else {
            // invalid id
        }
    }

    // Get all phone book contacts
    private static List<Contact> getPhoneBookContacts (Context context, boolean forceFetch) {
        if (phoneBookContacts != null && !forceFetch) {
            return phoneBookContacts;
        } else {
            phoneBookContacts = new ArrayList<>();

            ContentResolver contentResolver = context.getContentResolver();
            Cursor cursor = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, projectionContacts, null, null, CONTACTS_QUERY_SORT_ORDER);

            int phoneBookContactsCurrentIndex = 0;
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    id = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
                    currentContactName = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY));
                    currentContactPhoneNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    // check if current contact is a duplicate contact
                    boolean isDuplicateContact = phoneBookContactsCurrentIndex > 0 && phoneBookContacts.get(phoneBookContactsCurrentIndex - 1).getId().equals(id);
                    // add the current phone number to the previous contact when is a duplicate contact
                    if (isDuplicateContact) {
                        phoneBookContacts.get(phoneBookContactsCurrentIndex - 1).addAlternateNumber(currentContactPhoneNumber);
                    } else {
                        // is a new contact, create and add to the list
                        phoneBookContacts.add(new Contact(id, currentContactName, currentContactPhoneNumber));
                        phoneBookContactsCurrentIndex = phoneBookContactsCurrentIndex + 1;
                    }
                }
                cursor.close();
            }

            return phoneBookContacts;
        }
    }

    // get all sim contacts
    private static List<Contact> getSimContacts (Context context) {
        List<Contact> contacts = new ArrayList<>();
        ContentResolver contentResolver = context.getContentResolver();
        Uri simUri = Uri.parse("content://icc/adn");
        Cursor cursor = contentResolver.query(simUri, null, null, null, CONTACTS_QUERY_SORT_ORDER);

        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                id = cursor.getString(cursor.getColumnIndex("_id"));
                currentContactName = cursor.getString(cursor.getColumnIndex("name"));
                currentContactPhoneNumber = cursor.getString(cursor.getColumnIndex("number"));
                contacts.add(new Contact(id, currentContactName, currentContactPhoneNumber));
            }

            cursor.close();
        }

        return contacts;
    }

    // store a contact on the phone
    public static void storeContact (Context context, Contact contact) {
        ArrayList<ContentProviderOperation> contentProviderOperations = new ArrayList<ContentProviderOperation>();
        int contactIndex = contentProviderOperations.size();

        // insert contact
        contentProviderOperations
                .add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                .build());

        // insert display name
        contentProviderOperations
                .add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, contactIndex)
                .withValue(ContactsContract.RawContacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, contact.getName())
                .build());

        // insert phone number
        contentProviderOperations
                .add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, contactIndex)
                .withValue(ContactsContract.RawContacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, contact.getPhoneNumber())
                .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MAIN)
                .build());

        try {
            // add data
            ContentProviderResult[] contentProresult = null;
            context.getContentResolver().applyBatch(ContactsContract.AUTHORITY, contentProviderOperations);
            newAddedContact = contact;
            getPhoneBookContacts(context, true);

            // make sure to set an updated newAddedContact
            // passed "contact" dont have the DB id
            for(Contact phoneBookContact : phoneBookContacts) {
                if (phoneBookContact.getPhoneNumber().equals(newAddedContact.getPhoneNumber())) {
                    newAddedContact = phoneBookContact;
                }
            }
        } catch (RemoteException exp) {
            // do nothing
        } catch (OperationApplicationException exp) {
            // do nothing
        }
    }

    // getters
    public static Contact getNewAddedContact() {
        return newAddedContact;
    }
    public static Contact getNewDeletedContact() {
        return newDeletedContact;
    }


    // setters
    public static void resetNewAddedContact () {
        newAddedContact = null;
    }
    public static void resetNewDeletedContact () {
        newDeletedContact = null;
    }
}
