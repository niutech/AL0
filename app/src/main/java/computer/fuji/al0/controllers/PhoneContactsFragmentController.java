package computer.fuji.al0.controllers;

import android.content.Intent;
import android.os.Bundle;

import computer.fuji.al0.activities.PhoneContactActivity;
import computer.fuji.al0.activities.PhoneContactsFindActivity;
import computer.fuji.al0.activities.PhoneContactsNewActivity;
import computer.fuji.al0.fragments.PhoneContactsFragment;
import computer.fuji.al0.models.Contact;
import computer.fuji.al0.models.ListItem;
import computer.fuji.al0.services.ContactsService;

import java.util.ArrayList;
import java.util.List;

public class PhoneContactsFragmentController {
    private PhoneContactsFragment fragment;
    private List<Contact> contactsList;
    private ArrayList<ListItem> contactsListItems;
    private boolean isFirstStart = true;

    public PhoneContactsFragmentController (PhoneContactsFragment fragment) {
        this.fragment = fragment;
    }

    // get all contacts
    public ArrayList<ListItem> getContactsListItems () {
        return contactsListItems;
    }

    // events
    // fetch contact's activity the first time the frament is visible
    public void onFragmentShow () {
        if (isFirstStart) {
            isFirstStart = false;
            updateContactsListOnDifferentThread();
        } else {
            // do nothing
        }
    }

    // on click on list item contact
    public void onContactsListItemClick (ListItem listItem) {
        Intent intent = new Intent(fragment.getActivity(), PhoneContactActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(PhoneContactActivity.CONTACT_ID_BUNDLE_KEY, listItem.getId());
        intent.putExtras(bundle);
        fragment.startActivity(intent);
    }

    // on click find button
    // start find contact activity
    public void onFindButtonClick () {
        Intent intent = new Intent(fragment.getContext(), PhoneContactsFindActivity.class);
        fragment.startActivity(intent);
    }

    // on click new button
    // start new contact activity
    public void onNewButtonClick () {
        Intent intent = new Intent(fragment.getContext(), PhoneContactsNewActivity.class);
        fragment.startActivity(intent);
    }

    // on resume
    // to make sure a new added contact is shown
    // check if ContactsService have a valid newContact
    // add the validNewContact to list
    public void onResume () {
        if (!isFirstStart) {
            Contact newContact = ContactsService.getNewAddedContact();
            Contact deletedContact = ContactsService.getNewDeletedContact();

            // update contact list when a contact get added or removed
            if (newContact != null || deletedContact != null) {
                updateContactsList(true);
            }

            // if a new contact get added scroll to it
            if (newContact != null) {
                fragment.scrollContactListToContact(newContact);
                ContactsService.resetNewAddedContact();
            }

            if (deletedContact != null) {
                ContactsService.resetNewDeletedContact();
            }
        }
    }

    // Utils
    private void updateContactsList (boolean updateUi) {
        contactsList = ContactsService.getContacts(fragment.getContext(), false);
        contactsListItems = new ArrayList<>();

        for (Contact contact: contactsList) {
            contactsListItems.add(new ListItem(contact.getId(), contact.getName(), false));
        }

        if (updateUi) {
            fragment.updateContactList(contactsListItems);
        }
    }

    // run update contact list on a different thread
    private void updateContactsListOnDifferentThread () {
        new Thread() {
            @Override
            public void run () {
                // update activity list data
                updateContactsList(false);

                // update activity list UI
                fragment.getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        fragment.updateContactList(contactsListItems);
                    }
                });
            }
        }.start();
    }
}
