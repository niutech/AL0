package computer.fuji.al0.controllers;

import android.content.Intent;

import computer.fuji.al0.activities.ClockSetAlarmActivity;
import computer.fuji.al0.fragments.ClockClockFragment;
import computer.fuji.al0.services.AlarmSchedulerService;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class ClockClockFragmentController {
    private ClockClockFragment fragment;
    private Date currentAlarm;
    private Timer timeTimer;

    public ClockClockFragmentController(ClockClockFragment fragment) {
        this.fragment = fragment;
        updateCurrentAlarm();
    }

    public void onButtonSetAlarmPress () {
        Intent clockSetTimeActivityIntent = new Intent(fragment.getActivity(), ClockSetAlarmActivity.class);
        fragment.startActivity(clockSetTimeActivityIntent);
    }

    public void onButtonDeletePress () {
        AlarmSchedulerService.cancelDailyAlarm(fragment.getContext());
        updateCurrentAlarm();
    }

    public void onFragmentResume () {
        startTimeTimer();
        updateCurrentAlarm();
    }

    public void onFragmentPause () {
        stopTimeTimer();
    }

    private void updateCurrentAlarm () {
        currentAlarm = AlarmSchedulerService.getScheduledDailyAlarm(fragment.getContext());
        if (currentAlarm != null) {
            fragment.setAlarmTime(currentAlarm);
        } else {
            fragment.setNoAlarm();
        }
    }

    private void startTimeTimer () {
        fragment.setTime(new Date());

        timeTimer = new Timer ();
        timeTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                fragment.getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        fragment.setTime(new Date());
                    }
                });
            }
        }, 1000, 1000);

    }

    // stop call timer
    private void stopTimeTimer () {
        if (timeTimer != null) {
            timeTimer.cancel();
        }
    }
}
