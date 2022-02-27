package computer.fuji.al0.controllers;

import android.os.Bundle;

import computer.fuji.al0.activities.PhoneContactsNewActivity;
import computer.fuji.al0.models.Contact;
import computer.fuji.al0.models.NumpadButtonModel;
import computer.fuji.al0.services.ContactsService;
import computer.fuji.al0.utils.PhoneNumber;

public class PhoneContactsNewActivityController {
    PhoneContactsNewActivity activity;
    private Contact newContact;

    public PhoneContactsNewActivityController (PhoneContactsNewActivity activity) {
        this.activity = activity;
        newContact = new Contact("NEW_CONTACT", "", "");

        activity.showNewContactNumberFragment();

        Bundle bundle = activity.getIntent().getExtras();
        if (bundle != null) {
            String phoneNumber = bundle.getString(PhoneContactsNewActivity.CONTACT_NUMBER_BUNDLE_KEY);

            if (phoneNumber != null) {
                newContact.setPhoneNumber(PhoneNumber.cleanPhoneNumber(phoneNumber));
                // activity.updateNewContactNumberTextInput(newContact);
            }
        }
    }

    // events

    // NUMBER
    // on number fragments ready
    public void onPhoneContactsNewNumberFragmentReady () {
        activity.updateNewContactNumberTextInput(newContact);
    }

    // Number Fragment Numpad button press
    public void onNewNumberFragmentNumpadButtonPress (NumpadButtonModel buttonModel) {
        newContact.setPhoneNumber(newContact.getPhoneNumber().concat(buttonModel.getNumber()));
        activity.updateNewContactNumberTextInput(newContact);
    }

    // Number Fragment Delete button press
    public void onNewNumberFragmentDeleteButtonPress () {
        String number = newContact.getPhoneNumber();
        if (number.length() > 0) {
            newContact.setPhoneNumber(number.substring(0, number.length() - 1));
            activity.updateNewContactNumberTextInput(newContact);
        }
    }

    // Number Fragment Enter Name button press
    public void onNewNumberFragmentEnterNameButtonPress() {
        activity.showNewContactNameFragment();
    }

    public void onNewNumberFragmentCancelButtonPress() {
        activity.finish();
    }

    // NAME
    // on name fragments ready
    public void onPhoneContactsNewNameFragmentReady () {
        activity.updateNewContactNameTextInput(newContact);
    }

    public void onNewNameTextInputChange (String name) {
        newContact.setName(name);
    }

    public void onNewNameKeyboardCloseButtonPress () {
        activity.showNewContactNumberFragment();
    }

    public void onNewNameKeyboardRightActionButtonPress () {
        // if name is filled its save
        if (newContact.getName().length() > 0) {
            ContactsService.storeContact(activity, newContact);
            activity.finish();
        } else {
            // its cancel
            activity.showNewContactNumberFragment();
        }
    }
}
