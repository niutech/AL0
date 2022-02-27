package computer.fuji.al0.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import computer.fuji.al0.R;
import computer.fuji.al0.components.Button;
import computer.fuji.al0.components.CalendarComponent;
import computer.fuji.al0.controllers.CalendarActivityController;
import computer.fuji.al0.utils.LockScreen;
import computer.fuji.al0.utils.UI;

import java.util.Date;


public class CalendarActivity extends AppCompatActivity {
    CalendarActivityController controller;

    private LinearLayout calendarWrapper;
    private CalendarComponent calendar;

    private Button closeButton;
    private Button previousMonthButton;
    private Button nextMonthButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // show activity on lock screen
        LockScreen.addShowOnLockScreenFlags(this);

        setContentView(R.layout.activity_calendar);
        UI.hideNavigationBar(this);

        calendarWrapper = findViewById(R.id.calendar_activity_calendar);
        calendar = new CalendarComponent(this);
        calendarWrapper.addView(calendar, calendarWrapper.getChildCount());

        closeButton = (Button) findViewById(R.id.calendar_activity_close_button);
        previousMonthButton = (Button) findViewById(R.id.calendar_activity_button_previous_month);
        nextMonthButton = (Button) findViewById(R.id.calendar_activity_button_next_month);

        controller = new CalendarActivityController(this);

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                controller.onButtonClosePress();
            }
        });

        previousMonthButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                controller.onButtonPreviousMonthPress();
            }
        });

        nextMonthButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                controller.onButtonNextMonthPress();
            }
        });
    }


    public void setMonth (Date monthDay, String previousMonthNameString, String nextMonthNameString) {
        calendar.setMonth(monthDay);
        String previousMonthText = getString(R.string.calendar_activity_button_previous_month_prefix).concat(previousMonthNameString);
        String nextMonthText =  getString(R.string.calendar_activity_button_next_month_prefix).concat(nextMonthNameString);
        previousMonthButton.setText(previousMonthText);
        nextMonthButton.setText(nextMonthText);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            UI.hideNavigationBar(this);
        }
    }
}
