package computer.fuji.al0.activities;

import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import computer.fuji.al0.R;
import computer.fuji.al0.controllers.ClockActivityController;
import computer.fuji.al0.fragments.ClockClockFragment;
import computer.fuji.al0.fragments.ClockStopwatchFragment;
import computer.fuji.al0.fragments.ClockTimerFragment;
import computer.fuji.al0.utils.ClockActivityTabFragment;
import computer.fuji.al0.utils.LockScreen;
import computer.fuji.al0.utils.UI;

public class ClockActivity extends AppCompatActivity {
    public enum TabId { ALARM, STOPWATCH, TIMER };
    private static TabId currentTab;

    ClockActivityController controller;

    String timerActivityText;
    String timerActivityTextWithNotification;

    LinearLayout fragmentsWrapper;
    FragmentManager fragmentManager;
    Fragment currentFragment;
    ClockClockFragment clockAlarmFragment;
    ClockStopwatchFragment clockStopwatchFragment;
    ClockTimerFragment clockTimerFragment;

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // show activity on lock screen
        LockScreen.addShowOnLockScreenFlags(this);

        setContentView(R.layout.activity_clock);
        UI.hideNavigationBar(this);

        timerActivityText = getString(R.string.clock_activity_button_timer);
        timerActivityTextWithNotification = getString(R.string.notification_mark).concat(timerActivityText);

        initFragments();

        controller = new ClockActivityController(this);
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
        if (currentFragment instanceof ClockActivityTabFragment) {
            ((ClockActivityTabFragment) currentFragment).onHide();
        }

        // notify fragment is visible
        if (fragment instanceof ClockActivityTabFragment) {
            ((ClockActivityTabFragment) fragment).onShow();
        }

        currentFragment = fragment;
    }

    // set current tab
    public void setCurrentTab(TabId tabId) {
        currentTab = tabId;
        switch (tabId) {
            case ALARM:
                showFragment(clockAlarmFragment);
                break;
            case STOPWATCH:
                showFragment(clockStopwatchFragment);
                break;
            case TIMER:
                showFragment(clockTimerFragment);
                break;
        }
    }

    public TabId getCurrentTabId () {
        return currentTab;
    }

    private void initFragments () {
        fragmentsWrapper = (LinearLayout) findViewById(R.id.clock_activity_fragments);
        fragmentManager = getSupportFragmentManager();

        clockAlarmFragment = new ClockClockFragment();
        clockStopwatchFragment = new ClockStopwatchFragment();
        clockTimerFragment = new ClockTimerFragment();

        fragmentManager
                .beginTransaction()
                .add(fragmentsWrapper.getId(), clockAlarmFragment, "ClockAlarmFragment")
                .add(fragmentsWrapper.getId(), clockStopwatchFragment, "ClockStopwatchFragment")
                .hide(clockStopwatchFragment)
                .add(fragmentsWrapper.getId(), clockTimerFragment, "ClockTimerFragment")
                .hide(clockTimerFragment)
                .commit();

        currentFragment = clockAlarmFragment;

        ClockActivityTabFragment.ClockTabsEventListener clockTabsEventListener = new ClockActivityTabFragment.ClockTabsEventListener() {
            @Override
            public void onCloseButtonPress() {
                controller.onCloseButtonPress();
            }

            @Override
            public void onClockButtonPress() {
                controller.onAlarmButtonPress();
            }

            @Override
            public void onStopwatchButtonPress() {
                controller.onStopwatchButtonPress();
            }

            @Override
            public void onTimerButtonPress() {
                controller.onTimerButtonPress();
            }
        };

        clockAlarmFragment.setClockTabsEventListener(clockTabsEventListener);
        clockStopwatchFragment.setClockTabsEventListener(clockTabsEventListener);
        clockTimerFragment.setClockTabsEventListener(clockTabsEventListener);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            UI.hideNavigationBar(this);
        }
    }
}