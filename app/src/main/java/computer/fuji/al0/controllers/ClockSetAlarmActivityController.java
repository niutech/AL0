package computer.fuji.al0.controllers;

import computer.fuji.al0.activities.ClockSetAlarmActivity;
import computer.fuji.al0.models.NumpadButtonModel;
import computer.fuji.al0.services.AlarmSchedulerService;
import computer.fuji.al0.utils.Time;

public class ClockSetAlarmActivityController {
    private ClockSetAlarmActivity activity;
    private String alarmTime;
    private boolean isAm = true;

    public ClockSetAlarmActivityController (ClockSetAlarmActivity activity) {
        this.activity = activity;
        alarmTime = "";
        activity.updateInputTime(alarmTime, isAm);
    }

    // events
    public void onButtonSetPress () {
        int hour = Integer.parseInt(alarmTime.substring(0, 2));
        int normalizedHour = hour >= 12 ? 0 : hour;
        int minute = Integer.parseInt(alarmTime.substring(2, 4));

        // convert in 12h time
        if (Time.getIs24HourFormat()) {
            normalizedHour = hour >= 12 ? hour - 12 : hour;
            isAm = hour < 12;
        }

        AlarmSchedulerService.setDailyAlarm(activity, normalizedHour, minute, isAm);

        activity.finish();
    }

    public void onButtonCancelPress () {
        activity.finish();
    }

    public void onButtonDeletePress () {
        alarmTime = alarmTime.substring(0, alarmTime.length() - 1);
        activity.updateInputTime(alarmTime, isAm);
    }

    public void onNumpadButtonPress(NumpadButtonModel buttonModel) {
        NumpadButtonModel.NumpadButtonId typedButton = buttonModel.getId();
        // check if user pressed AM
        if (typedButton.equals(NumpadButtonModel.NumpadButtonId.BUTTON_AM)) {
            isAm = true;

            // check if user pressed PM
        } else if (typedButton.equals(NumpadButtonModel.NumpadButtonId.BUTTON_PM)) {
            isAm = false;
            // user pressed a digit
        } else {
            if (Time.getIs24HourFormat()) {
                alarmTime = typedNumberToAlarm24HTime(buttonModel);
            } else {
                alarmTime = typedNumberToAlarm12HTime(buttonModel);
            }

        }

        activity.updateInputTime(alarmTime, isAm);
    }

    public void onNumpadButtonTouchStart(NumpadButtonModel buttonModel) {
        // do nothing
    }

    public void onNumpadButtonTouchEnd(NumpadButtonModel buttonModel, boolean isTailTouchEvent) {
        // do nothing
    }

    // convert typed number to a valid 24h time span value
    // prevent typing valure higher than 23 hours and 59 minutes
    private String typedNumberToAlarm24HTime (NumpadButtonModel buttonModel) {
        int typedNumber = Integer.parseInt(buttonModel.getNumber());
        switch (alarmTime.length()) {
            case 0:
                // check if typed a number greater than 2, in this case it should be the 2nd digit following a 0
                if (typedNumber <= 2) {
                    return String.valueOf(typedNumber);
                } else {
                    return "0".concat(String.valueOf(typedNumber));
                }
            case 1:
                int firstDigit = Integer.parseInt(alarmTime);
                // check if previous digit is 1, then the 2nd digit can only be a
                if (firstDigit == 2) {
                    // check if is a valid hour
                    if (typedNumber <= 3) {
                        return alarmTime.concat(String.valueOf(typedNumber));
                    } else {
                        // check if is a valid ten minutes
                        if (typedNumber <= 5) {
                            return alarmTime.concat("0").concat(String.valueOf(typedNumber));
                        } else {
                            // it can be minutes
                            return alarmTime.concat("00").concat(String.valueOf(typedNumber));
                        }
                    }
                } else {
                    return alarmTime.concat(String.valueOf(typedNumber));
                }
            case 2:
                // check if is a valid ten minutes
                if (typedNumber <= 5) {
                    return alarmTime.concat(String.valueOf(typedNumber));
                } else {
                    // it can be minutes
                    return alarmTime.concat("0").concat(String.valueOf(typedNumber));
                }
            case 3:
            default:
                return alarmTime.substring(0,3).concat(String.valueOf(typedNumber));
        }
    }

    // convert typed number to a valid 12h time span value
    // prevent typing valure higher than 12 hours and 59 minutes
    private String typedNumberToAlarm12HTime (NumpadButtonModel buttonModel) {
        int typedNumber = Integer.parseInt(buttonModel.getNumber());
        switch (alarmTime.length()) {
            case 0:
                // check if typed a number greater than 1, in this case it should be the 2nd digit following a 0
                if (typedNumber <= 1) {
                    return String.valueOf(typedNumber);
                } else {
                    return "0".concat(String.valueOf(typedNumber));
                }
            case 1:
                int firstDigit = Integer.parseInt(alarmTime);
                // check if previous digit is 1, then the 2nd digit can only be a
                if (firstDigit == 1) {
                    // check if is a valid hour
                    if (typedNumber <= 2) {
                        return alarmTime.concat(String.valueOf(typedNumber));
                    } else {
                        // check if is a valid ten minutes
                        if (typedNumber <= 5) {
                            return alarmTime.concat("0").concat(String.valueOf(typedNumber));
                        } else {
                            // it can be minutes
                            return alarmTime.concat("00").concat(String.valueOf(typedNumber));
                        }
                    }
                } else {
                    return alarmTime.concat(String.valueOf(typedNumber));
                }
            case 2:
                // check if is a valid ten minutes
                if (typedNumber <= 5) {
                    return alarmTime.concat(String.valueOf(typedNumber));
                } else {
                    // it can be minutes
                    return alarmTime.concat("0").concat(String.valueOf(typedNumber));
                }
            case 3:
            default:
                return alarmTime.substring(0,3).concat(String.valueOf(typedNumber));
        }
    }
}
