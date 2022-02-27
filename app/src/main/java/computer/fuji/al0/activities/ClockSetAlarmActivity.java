package computer.fuji.al0.activities;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import computer.fuji.al0.R;
import computer.fuji.al0.components.Button;
import computer.fuji.al0.components.Numpad;
import computer.fuji.al0.components.TextInput;
import computer.fuji.al0.controllers.ClockSetAlarmActivityController;
import computer.fuji.al0.models.NumpadButtonModel;
import computer.fuji.al0.utils.LockScreen;
import computer.fuji.al0.utils.Time;
import computer.fuji.al0.utils.UI;

public class ClockSetAlarmActivity extends AppCompatActivity {
    ClockSetAlarmActivityController controller;

    private TextInput textInputTime;
    private Button buttonSet;
    private Button buttonCancel;
    private Button buttonDelete;
    private Numpad numpad;

    private String amPostfix;
    private String pmPostfix;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // show activity on lock screen
        LockScreen.addShowOnLockScreenFlags(this);

        setContentView(R.layout.activity_clock_set_alarm);
        UI.hideNavigationBar(this);

        // init labels Strings
        amPostfix = getResources().getString(R.string.clock_set_alarm_activity_postfix_am);
        pmPostfix = getResources().getString(R.string.clock_set_alarm_activity_postfix_pm);

        // init UI comonents
        textInputTime = (TextInput) findViewById(R.id.clock_set_alarm_activity_text_input);
        buttonSet = (Button) findViewById(R.id.clock_set_alarm_activity_button_set);
        buttonCancel = (Button) findViewById(R.id.clock_set_alarm_activity_button_cancel);
        buttonDelete = (Button) findViewById(R.id.clock_set_alarm_activity_button_delete);
        numpad = (Numpad) findViewById(R.id.clock_set_alarm_activity_numpad);
        // set numpad in time mode, use AM and PM buttons instead of hash and star.
        if (Time.getIs24HourFormat()) {
            numpad.setNumberMode();
        } else {
            numpad.setTime12HMode();
        }


        // init controller
        controller = new ClockSetAlarmActivityController(this);

        // add events listeners
        buttonSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                controller.onButtonSetPress();
            }
        });

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                controller.onButtonCancelPress();
            }
        });

        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                controller.onButtonDeletePress();
            }
        });

        numpad.setNumpadEventsListener(new Numpad.NumpadEventsListener() {
            @Override
            public void onButtonPress(NumpadButtonModel buttonModel) {
                controller.onNumpadButtonPress(buttonModel);
            }

            @Override
            public void onButtonTouchStart(NumpadButtonModel buttonModel) {
                controller.onNumpadButtonTouchStart(buttonModel);
            }

            @Override
            public void onButtonTouchEnd(NumpadButtonModel buttonModel, boolean isTailTouchEvent) {
                controller.onNumpadButtonTouchEnd(buttonModel, isTailTouchEvent);
            }
        });
    }

    // UI
    public void updateInputTime (String time, boolean isAm) {
        if (!Time.getIs24HourFormat()) {
            textInputTime.setPostfix(isAm ? amPostfix : pmPostfix);
        }

        textInputTime.setText(formatAlarmTime(time));
        buttonDelete.setIsDisabled(time.length() == 0);
        buttonSet.setIsDisabled(time.length() != 4);
    }

    // utils
    // Add : between hours and minutes
    private String formatAlarmTime (String time) {
        String formattedAlarmTime = "";
        for (int i = 0; i < time.length(); i++) {
            formattedAlarmTime = formattedAlarmTime + time.charAt(i);

            if (i == 1) {
                formattedAlarmTime = formattedAlarmTime + ":";
            }
        }

        return formattedAlarmTime;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            UI.hideNavigationBar(this);
        }
    }

}
