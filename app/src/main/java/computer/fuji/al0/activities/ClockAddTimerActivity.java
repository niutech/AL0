package computer.fuji.al0.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import computer.fuji.al0.R;
import computer.fuji.al0.components.Button;
import computer.fuji.al0.components.Numpad;
import computer.fuji.al0.controllers.ClockAddTimerActivityController;
import computer.fuji.al0.models.NumpadButtonModel;
import computer.fuji.al0.utils.LockScreen;
import computer.fuji.al0.utils.UI;

public class ClockAddTimerActivity extends AppCompatActivity {
    ClockAddTimerActivityController controller;

    private TextView textInputDuration;
    private Button buttonStart;
    private Button buttonCancel;
    private Button buttonDelete;
    private Numpad numpad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // show activity on lock screen
        LockScreen.addShowOnLockScreenFlags(this);

        setContentView(R.layout.activity_clock_add_timer);
        UI.hideNavigationBar(this);

        // init UI comonents
        textInputDuration = (TextView) findViewById(R.id.clock_timer_add_timer_activity_text_view);
        buttonStart = (Button) findViewById(R.id.clock_timer_add_timer_activity_button_start);
        buttonCancel = (Button) findViewById(R.id.clock_timer_add_timer_activity_button_cancel);
        buttonDelete = (Button) findViewById(R.id.clock_timer_add_timer_activity_button_delete);
        numpad = (Numpad) findViewById(R.id.clock_timer_add_timer_activity_numpad);
        // set numpad in numbers only mode, use empty buttons buttons instead of hash and star.
        numpad.setNumberMode();

        // init controller
        controller = new ClockAddTimerActivityController(this);

        // add events listeners
        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                controller.onButtonStartPress();
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
    public void updateInputDuration (String seconds, String minutes, String hours) {
        textInputDuration.setText(formatDurationParts(seconds, minutes, hours));
        boolean isValidTime = seconds.length() > 0 || minutes.length() > 0 || hours.length() > 0;
        buttonDelete.setIsDisabled(!isValidTime);
        buttonStart.setIsDisabled(!isValidTime);
    }

    public String formatDurationParts(String seconds, String minutes, String hours) {
        String hh = hours.length() > 0 ? hours.concat("h "): "";
        String mm = minutes.length() > 0 ? minutes.concat("m ") : "";
        String ss = seconds.length() > 0 ? seconds.concat("s") : "0s";

        return hh.concat(mm).concat(ss);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            UI.hideNavigationBar(this);
        }
    }

}
