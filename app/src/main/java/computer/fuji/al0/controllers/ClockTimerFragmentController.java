package computer.fuji.al0.controllers;

import android.content.Intent;

import computer.fuji.al0.activities.ClockAddTimerActivity;
import computer.fuji.al0.fragments.ClockTimerFragment;
import computer.fuji.al0.models.ClockTimer;
import computer.fuji.al0.services.TimerAlarmSchedulerService;

import java.util.ArrayList;
import java.util.Timer;

public class ClockTimerFragmentController {
    ClockTimerFragment fragment;
    ArrayList<ClockTimer> timers;
    Timer updateTimer;
    ClockTimer selectedTimer;

    public ClockTimerFragmentController (ClockTimerFragment fragment) {
        this.fragment = fragment;
        // init timers array
        timers = new ArrayList<>();
        // populate timers
        updateTimers();
    }

    // events
    public void onFragmentResume () {
        updateTimers();
    }

    public void onFragmentPause () {
        // do nothing
    }

    public void onFragmentShow () {
        updateTimers();
    }

    public void onFragmentHide () {
        TimerAlarmSchedulerService.removePassedTimers(fragment.getContext());
    }

    public void onButtonAddPress () {
        Intent clockAddTimerActivityIntent = new Intent(fragment.getActivity(), ClockAddTimerActivity.class);
        fragment.startActivity(clockAddTimerActivityIntent);
    }

    public void onButtonDeletePress () {
        TimerAlarmSchedulerService.deleteTimer(fragment.getContext(), selectedTimer);
        updateTimers();
    }

    public void onTimerListItemClick (ClockTimer timer) {
        // check a timer is selected
        if (selectedTimer != null) {
            // check if user clicked on a selected item, then deselect
            if (selectedTimer.getId().equals(timer.getId())) {
                selectedTimer = null;
            } else {
                selectedTimer = timer;
            }
         } else {
            selectedTimer = timer;
        }

        fragment.setSelectedClockTimer(selectedTimer);
    }

    // gel all timers in ClockTimerService and display in the list
    private void updateTimers () {
        timers.clear();
        timers.addAll(TimerAlarmSchedulerService.getClockTimers(fragment.getContext()));
        fragment.updateTimerList(timers);
        if  (selectedTimer != null) {
            fragment.setSelectedClockTimer(selectedTimer);
        }
    }
}
