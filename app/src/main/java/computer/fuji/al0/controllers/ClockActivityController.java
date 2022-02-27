package computer.fuji.al0.controllers;

import computer.fuji.al0.activities.ClockActivity;

public class ClockActivityController {
    ClockActivity activity;

    public ClockActivityController (ClockActivity activity) {
        this.activity = activity;

        activity.setCurrentTab(ClockActivity.TabId.ALARM);
    }

    // events
    public void onCloseButtonPress () {
        activity.finish();
    }

    public void onAlarmButtonPress () {
        activity.setCurrentTab(ClockActivity.TabId.ALARM);
    }

    public void onStopwatchButtonPress () {
        activity.setCurrentTab(ClockActivity.TabId.STOPWATCH);
    }

    public void onTimerButtonPress () {
        activity.setCurrentTab(ClockActivity.TabId.TIMER);
    }
}
