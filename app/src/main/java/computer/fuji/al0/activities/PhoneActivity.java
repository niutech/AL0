package computer.fuji.al0.activities;

import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import computer.fuji.al0.R;
import computer.fuji.al0.controllers.PhoneActivityController;
import computer.fuji.al0.fragments.PhoneActivityFragment;
import computer.fuji.al0.fragments.PhoneContactsFragment;
import computer.fuji.al0.fragments.PhoneNumpadFragment;
import computer.fuji.al0.utils.LockScreen;
import computer.fuji.al0.utils.PhoneActivityTabFragment;
import computer.fuji.al0.utils.UI;

public class PhoneActivity extends AppCompatActivity {
    public enum TabId { NUMPAD, CONTACTS, ACTIVITY};
    private static TabId currentTab;
    private String phoneActivityText;
    private String phoneActivityTextWithNotification;

    PhoneActivityController controller;

    LinearLayout fragmentsWrapper;
    FragmentManager fragmentManager;
    Fragment currentFragment;
    PhoneNumpadFragment phoneNumpadFragment;
    PhoneContactsFragment phoneContactsFragment;
    PhoneActivityFragment phoneActivityFragment;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // show activity on lock screen
        LockScreen.addShowOnLockScreenFlags(this);

        setContentView(R.layout.activity_phone);
        UI.hideNavigationBar(this);

        phoneActivityText = getString(R.string.phone_activity_button_activity);
        phoneActivityTextWithNotification = getString(R.string.notification_mark).concat(phoneActivityText);

        initFragments();

        controller = new PhoneActivityController(this);
    }

    @Override
    public void onResume () {
        super.onResume();
        controller.onActivityResume();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            UI.hideNavigationBar(this);
        }
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

        // notify current fragment is hidden
        if (currentFragment instanceof PhoneActivityTabFragment) {
            ((PhoneActivityTabFragment) currentFragment).onHide();
        }

        // notify fragment is visible
        if (fragment instanceof PhoneActivityTabFragment) {
            ((PhoneActivityTabFragment) fragment).onShow();
        }

        currentFragment = fragment;
    }

    // set current tab
    public void setCurrentTab(TabId tabId) {
        currentTab = tabId;
        switch (tabId) {
            case NUMPAD:
                showFragment(phoneNumpadFragment);
                break;
            case CONTACTS:
                showFragment(phoneContactsFragment);
                break;
            case ACTIVITY:
                showFragment(phoneActivityFragment);
                break;
        }
    }

    public TabId getCurrentTabId () {
        return currentTab;
    }

    // show notification on activity tab
    public void showNotificationOnActivityTab (boolean notificationVisible) {
        if (notificationVisible) {
            ((PhoneActivityTabFragment) currentFragment).setActivityButtonText(phoneActivityTextWithNotification);
        } else {
            ((PhoneActivityTabFragment) currentFragment).setActivityButtonText(phoneActivityText);
        }
    }

    // init Numpad, Contacts, Activity fragments
    private void initFragments () {
        fragmentsWrapper = (LinearLayout) findViewById(R.id.phone_activity_fragments);
        fragmentManager = getSupportFragmentManager();

        phoneNumpadFragment = new PhoneNumpadFragment();
        phoneContactsFragment = new PhoneContactsFragment();
        phoneActivityFragment = new PhoneActivityFragment();

        initFragmentsEventListener();

        fragmentManager
                .beginTransaction()
                .add(fragmentsWrapper.getId(), phoneNumpadFragment, "PhoneNumpadFragment")
                .add(fragmentsWrapper.getId(), phoneContactsFragment, "PhoneContactsFragment")
                .hide(phoneContactsFragment)
                .add(fragmentsWrapper.getId(), phoneActivityFragment, "PhoneActivityFragment")
                .hide(phoneActivityFragment)
                .commit();

        currentFragment = phoneNumpadFragment;
    }

    // listen for events triggered by the following fragments
    // phoneNumpadFragment
    // phoneContactsFragment
    // phoneActivityFragment
    private void initFragmentsEventListener () {
        phoneNumpadFragment.setPhoneNumpadFragmentEventsListener(new PhoneNumpadFragment.PhoneNumpadFragmentEventsListener() {
            @Override
            public void onSmsButtonPress(String number) {
                controller.onNumpadSmsButtonPress(number);
            }

            @Override
            public void onCallButtonPress(String number) {
                controller.onNumpadCallButtonPress(number);
            }
        });

        PhoneActivityTabFragment.PhoneTabsEventListener phoneTabsEventListener = new PhoneActivityTabFragment.PhoneTabsEventListener() {
            @Override
            public void onCloseButtonPress() {
                controller.onCloseButtonPress();
            }

            @Override
            public void onNumpadButtonPress() {
                controller.onNumpadButtonPress();
            }

            @Override
            public void onContactsButtonPress() {
                controller.onContactsButtonPress();
            }

            @Override
            public void onActivityButtonPress() {
                controller.onActivityButtonPress();
            }
        };

        phoneNumpadFragment.setPhoneTabsEventListener(phoneTabsEventListener);
        phoneContactsFragment.setPhoneTabsEventListener(phoneTabsEventListener);
        phoneActivityFragment.setPhoneTabsEventListener(phoneTabsEventListener);
    }

    public void onNewPhoneContactAdded () {
        // do nothing
    }
}
