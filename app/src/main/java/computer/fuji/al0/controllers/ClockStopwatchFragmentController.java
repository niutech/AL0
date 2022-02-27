package computer.fuji.al0.controllers;

import android.content.Context;
import android.content.SharedPreferences;

import computer.fuji.al0.fragments.ClockStopwatchFragment;
import computer.fuji.al0.services.StopwatchService;

import java.util.Timer;
import java.util.TimerTask;

public class ClockStopwatchFragmentController {
    private ClockStopwatchFragment fragment;
    private Context context;
    private int elapsedTimeInDecimal = 0;
    private boolean isRunning = false;
    private Timer stopwatchTimer;
    private SharedPreferences sharedPreferences;

    public ClockStopwatchFragmentController (ClockStopwatchFragment fragment) {
        this.fragment = fragment;
        this.context = fragment.getContext();
    }

    // events
    public void onButtonStartPress () {
        startTimer();
    }

    public void onButtonStopPress () {
        stopTimer();
    }

    public void onButtonResetPress () {
        resetTimer();
    }

    public void onFragmentResume () {
        context = fragment.getContext();
        updateTimeFromService();
        fragment.setCurrentTime(elapsedTimeInDecimal);
        fragment.setTimerIsStarted(isRunning);
        if (elapsedTimeInDecimal > 0) {
            fragment.setButtonResetEnabled(true);
        }
    }

    public void onFragmentPause () {
        stopStopwatchTimer();
    }

    private void startTimer () {
        // delete stored elapsed time
        StopwatchService.deleteStopwatchElapsedTime(context);
        // start time is current minus elapsed time
        long startTime = System.currentTimeMillis() - (elapsedTimeInDecimal * 100);
        // store start time
        StopwatchService.storeStopwatchStartTime(context, startTime);
        StopwatchService.storeStopwatchIsRunning(context, true);
        startStopwatchTimer();
        fragment.setTimeIsRunning(true);
        fragment.setTimerIsStarted(true);
    }

    private void stopTimer () {
        // delete stored start time
        StopwatchService.deleteStopwatchStartTime(context);
        // store elapsed time
        StopwatchService.storeStopwatchElapsedTime(context, elapsedTimeInDecimal);
        StopwatchService.storeStopwatchIsRunning(context, false);
        stopStopwatchTimer();
        fragment.setTimeIsRunning(false);
    }

    private void resetTimer () {
        StopwatchService.deleteStopwatchStartTime(context);
        StopwatchService.deleteStopwatchElapsedTime(context);
        StopwatchService.deleteStopwatchIsRunning(context);
        stopStopwatchTimer();
        fragment.setTimeIsRunning(false);
        elapsedTimeInDecimal = 0;
        fragment.setCurrentTime(elapsedTimeInDecimal / 10);
        fragment.setTimerIsStarted(false);
    }

    private void updateTimeFromService () {
        long stopwatchStartTime = StopwatchService.getStopwatchStartTime(context);
        int stopwatchElapsedTime = StopwatchService.getStopwatchElapsedTime(context);
        boolean stopwatchIsRunning = StopwatchService.getStopwatchIsRunning(context);

        // check stored stopwatch start time
        if (stopwatchStartTime > -1) {
            long elapsedTimeInMillis = System.currentTimeMillis() - stopwatchStartTime;
            elapsedTimeInDecimal = (int) (elapsedTimeInMillis / 100);
            StopwatchService.deleteStopwatchStartTime(context);
        }

        // check stored stopwatch elapsed time
        if (stopwatchElapsedTime > -1){
            elapsedTimeInDecimal = stopwatchElapsedTime;
            StopwatchService.deleteStopwatchElapsedTime(context);
        }

        // check stored running stopwatch
        isRunning = stopwatchIsRunning;
        if (stopwatchIsRunning) {
            startTimer();
        } else {
            stopTimer();
        }
    }

    private void startStopwatchTimer () {
        fragment.setCurrentTime(elapsedTimeInDecimal);

        stopwatchTimer = new Timer ();
        stopwatchTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                elapsedTimeInDecimal = elapsedTimeInDecimal + 1;
                fragment.getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        fragment.setCurrentTime(elapsedTimeInDecimal);
                    }
                });
            }
        }, 100, 100);

    }

    // stop call timer
    private void stopStopwatchTimer () {
        if (stopwatchTimer != null) {
            stopwatchTimer.cancel();
        }
    }
}
