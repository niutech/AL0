package computer.fuji.al0.controllers;

import android.content.Intent;
import android.os.Bundle;

import computer.fuji.al0.activities.PhoneContactActivity;
import computer.fuji.al0.activities.PhoneContactsFindActivity;
import computer.fuji.al0.models.Contact;
import computer.fuji.al0.models.ListItem;
import computer.fuji.al0.services.ContactsService;

import java.util.ArrayList;
import java.util.List;

public class PhoneContactsFindActivityController {
    PhoneContactsFindActivity activity;
    private List<Contact> contactsList;
    private ArrayList<ListItem> contactsListItems;
    private ArrayList<ListItem> filteredContactListItems;
    private String contactQueryString = "";

    public PhoneContactsFindActivityController (PhoneContactsFindActivity activity) {
        this.activity = activity;

        contactsList = ContactsService.getContacts(activity, false);
        contactsListItems = new ArrayList<>();
        filteredContactListItems = new ArrayList<>();

        for (Contact contact: contactsList) {
            contactsListItems.add(new ListItem(contact.getId(), contact.getName(), false));
        }

        filteredContactListItems.addAll(contactsListItems);
    }


    public ArrayList<ListItem> getContactsListItems () {
        return filteredContactListItems;
    }

    // events
    public void onContactsListItemClick (ListItem listItem) {
        Intent intent = new Intent(activity, PhoneContactActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(PhoneContactActivity.CONTACT_ID_BUNDLE_KEY, listItem.getId());
        intent.putExtras(bundle);
        activity.startActivity(intent);
        activity.finish();
    }

    public void onContactQueryChange (String query) {
        contactQueryString = query;
        onContactQueryStringChange();
    }

    private void onContactQueryStringChange () {
        filteredContactListItems = new ArrayList<>();
        for (ListItem contactListItem : contactsListItems) {
            // check if contact list item contains query string
            // ignore letter case
            String contactListItemTextIgnoreCase = contactListItem.getText().toLowerCase();
            String queryStringIgnoreCase = contactQueryString.toLowerCase();
            boolean contactListItemMatchQueryString = contactListItemTextIgnoreCase.contains(queryStringIgnoreCase);
            if (contactListItemMatchQueryString) {
                filteredContactListItems.add(contactListItem);
            }
        }

        activity.updateFindQueryTextView(contactQueryString);
        activity.updateContactsListItems(filteredContactListItems);
    }

    public void onKeyboardCancelButtonPress () {
        activity.finish();
    }
}
