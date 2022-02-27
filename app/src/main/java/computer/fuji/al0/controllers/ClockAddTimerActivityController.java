package computer.fuji.al0.controllers;

import computer.fuji.al0.activities.ClockAddTimerActivity;
import computer.fuji.al0.models.NumpadButtonModel;
import computer.fuji.al0.services.TimerAlarmSchedulerService;

public class ClockAddTimerActivityController {
    private ClockAddTimerActivity activity;
    private  String duration;
    private static int minuteInSeconds = 60;
    private static int hourInSeconds = minuteInSeconds * 60;

    public ClockAddTimerActivityController (ClockAddTimerActivity activity) {
        this.activity = activity;
        duration = "";
        updateActivityInput();
    }

    // events
    public void onButtonStartPress () {
        // convert duration string in array of seconds, minutes, hours
        String[]  durationSMH = durationToSMH(duration);
        // convert duration parts in seconds
        long seconds = durationSMH[0].length() > 0 ? Long.parseLong(durationSMH[0]) : 0;
        long minutes = durationSMH[1].length() > 0 ? Long.parseLong(durationSMH[1]) * minuteInSeconds : 0;
        long hours = durationSMH[2].length() > 0 ? Long.parseLong(durationSMH[2]) * hourInSeconds : 0;
        // sum all duration parts
        long durationInSeconds = seconds + minutes + hours;
        // add time to timer service
        TimerAlarmSchedulerService.addTimer(activity, durationInSeconds);
        activity.finish();
    }

    public void onButtonCancelPress () {
        this.activity.finish();
    }

    public void onButtonDeletePress () {
        deleteDurationLastDigit();
    }

    public void onNumpadButtonPress(NumpadButtonModel buttonModel) {
        NumpadButtonModel.NumpadButtonId typedButton = buttonModel.getId();
        // check if user pressed a valid number
        boolean isDigit = true;
        if (isDigit) {
            addDigitToDuration(buttonModel.getNumber());
        } else {
            // TODO
        }
    }

    public void onNumpadButtonTouchStart(NumpadButtonModel buttonModel) {
        // do nothing
    }

    public void onNumpadButtonTouchEnd(NumpadButtonModel buttonModel, boolean isTailTouchEvent) {
        // do nothing
    }

    private void addDigitToDuration (String number) {
        duration = duration.concat(number);
        updateActivityInput();
    }

    private void deleteDurationLastDigit () {
        duration = duration.substring(0, duration.length() - 1);
        updateActivityInput();
    }

    // convert duration input to array of Seconds, Minutes and Hours
    // starting from the tile, 2 digits for seconds, 2 minutes, remaining digits are for hours
    // return an array [Seconds, Minutes, Hours]
    private String[] durationToSMH (String duration) {
        String seconds = "";
        String minutes = "";
        String hours = "";

        int durationLength = duration.length();
        for (int i =  durationLength - 1; i >= 0; i--) {
            if ((durationLength - i) <= 2) {
                seconds = duration.charAt(i) + seconds;
            } else if ((durationLength - i) <= 4) {
                minutes = duration.charAt(i) + minutes;
            } else {
                hours = duration.charAt(i) + hours;
            }
        }

        return new String [] { seconds, minutes, hours };
    }

    private void updateActivityInput () {
        String[]  durationSMH = durationToSMH(duration);
        activity.updateInputDuration(durationSMH[0], durationSMH[1], durationSMH[2]);
    }
}
