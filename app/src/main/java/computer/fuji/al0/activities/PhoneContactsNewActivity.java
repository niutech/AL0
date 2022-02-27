package computer.fuji.al0.activities;

import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import computer.fuji.al0.R;
import computer.fuji.al0.controllers.PhoneContactsNewActivityController;
import computer.fuji.al0.fragments.PhoneContactsNewNameFragment;
import computer.fuji.al0.fragments.PhoneContactsNewNumberFragment;
import computer.fuji.al0.models.Contact;
import computer.fuji.al0.models.NumpadButtonModel;
import computer.fuji.al0.utils.LockScreen;
import computer.fuji.al0.utils.UI;

public class PhoneContactsNewActivity extends AppCompatActivity {
    public static String CONTACT_NUMBER_BUNDLE_KEY = "CONTACT_NUMBER_BUNDLE_KEY";

    PhoneContactsNewActivityController controller;

    LinearLayout fragmentsWrapper;
    FragmentManager fragmentManager;
    PhoneContactsNewNumberFragment phoneContactsNewNumberFragment;
    PhoneContactsNewNameFragment phoneContactsNewNameFragment;
    Fragment currentFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // show activity on lock screen
        LockScreen.addShowOnLockScreenFlags(this);

        setContentView(R.layout.activity_phone_contacts_new);
        UI.hideNavigationBar(this);

        initFragments();
        controller = new PhoneContactsNewActivityController(this);
    }

    // show fragment
    // hide currentFragment
    // show passed fragment
    // set passed fragment as currentFragment
    private void showFragment (Fragment fragment) {
        fragmentManager
                .beginTransaction()
                .hide(currentFragment)
                .show(fragment)
                .commit();

        currentFragment = fragment;
    }

    // show New Contact Number fragment
    public void showNewContactNumberFragment () {
        showFragment(phoneContactsNewNumberFragment);
    }

    // show New contact Name fragment
    public void showNewContactNameFragment () {
        showFragment(phoneContactsNewNameFragment);
    }

    public void updateNewContactNumberTextInput (Contact newContact) {
        phoneContactsNewNumberFragment.setNumber(newContact.getPhoneNumber());
        // check number length to enable/disable delete and enter name buttons
        boolean disableEnterNameButton = newContact.getPhoneNumber().length() > 0;
        boolean disableDeleteButton = disableEnterNameButton;

        phoneContactsNewNumberFragment.setEnterNameButtonIsEnabled(disableEnterNameButton);
        phoneContactsNewNumberFragment.setDeleteButtonIsEnabled(disableDeleteButton);

    }

    public void updateNewContactNameTextInput (Contact newContact) {
        phoneContactsNewNameFragment.setName(newContact.getName());
        boolean canSave = newContact.getName().length() > 0;
        phoneContactsNewNameFragment.setKeyboardRightActionCanSave(canSave);
    }

    private void setPhoneContactsNewNameFragmentCanSave (String name) {
        phoneContactsNewNameFragment.setKeyboardRightActionCanSave(name.length() > 0);
    }

    // fragments
    private void initFragments () {
        fragmentsWrapper = (LinearLayout) findViewById(R.id.phone_contacts_new_activity_fragments);
        fragmentManager = getSupportFragmentManager();

        phoneContactsNewNumberFragment = new PhoneContactsNewNumberFragment();
        phoneContactsNewNameFragment = new PhoneContactsNewNameFragment();

        initFragmentsEventListener();

        fragmentManager
                .beginTransaction()
                .add(fragmentsWrapper.getId(), phoneContactsNewNumberFragment, "PhoneContactsNewNumberFragment")
                .add(fragmentsWrapper.getId(), phoneContactsNewNameFragment, "PhoneContactsNewNameFragment")
                .commit();

        currentFragment = phoneContactsNewNumberFragment;
    }

    // listen for events triggered by the following fragments
    // PhoneContactsNewNumberFragment
    // PhoneContactsNewNameFragment
    private void initFragmentsEventListener () {
        // Number Events
        phoneContactsNewNumberFragment.setPhoneContactsNewNumberFragmentEventsListener(new PhoneContactsNewNumberFragment.PhoneContactsNewNumberFragmentEventsListener() {
            @Override
            public void onNumpadButtonPress(NumpadButtonModel numpadButtonModel) {
                controller.onNewNumberFragmentNumpadButtonPress(numpadButtonModel);
            }

            @Override
            public void onDeleteButtonPress() {
                controller.onNewNumberFragmentDeleteButtonPress();
            }

            @Override
            public void onEnterNameButtonPress() {
                controller.onNewNumberFragmentEnterNameButtonPress();
            }

            @Override
            public void onCancelButtonPress() {
                controller.onNewNumberFragmentCancelButtonPress();
            }

            @Override
            public void onViewReady() {
                controller.onPhoneContactsNewNumberFragmentReady();
            }
        });

        // Name events
        phoneContactsNewNameFragment.setPhoneContactsNewNameFragmentEventListener(new PhoneContactsNewNameFragment.PhoneContactsNewNameFragmentEventListener() {
            @Override
            public void onKeyboardRightActionButtonPress() {
                controller.onNewNameKeyboardRightActionButtonPress();
            }

            @Override
            public void onKeyboardCloseButtonPress() {
                controller.onNewNameKeyboardCloseButtonPress();
            }

            @Override
            public void onViewReady() {
                controller.onPhoneContactsNewNameFragmentReady();
            }

            @Override
            public void onNameTextInputChange(String name) {
                controller.onNewNameTextInputChange(name);
                setPhoneContactsNewNameFragmentCanSave(name);
            }
        });
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            UI.hideNavigationBar(this);
        }
    }
}
