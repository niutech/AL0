package computer.fuji.al0.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import computer.fuji.al0.R;
import computer.fuji.al0.components.Button;
import computer.fuji.al0.controllers.ClockClockFragmentController;
import computer.fuji.al0.utils.ClockActivityTabFragment;
import computer.fuji.al0.utils.Time;

import java.util.Date;

public class ClockClockFragment extends Fragment implements ClockActivityTabFragment {
    private ClockClockFragmentController controller;
    private ClockTabsEventListener clockTabsEventListener;

    Button closeButton;
    Button alarmButton;
    Button stopwatchButton;
    Button timerButton;

    private TextView timeLabel;
    private TextView alarmLabel;
    private Button buttonSetAlarm;
    private Button buttonDelete;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_clock_clock, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        closeButton = view.findViewById(R.id.clock_activity_close_button);
        alarmButton = view.findViewById(R.id.clock_activity_alarm_button);
        stopwatchButton = view.findViewById(R.id.clock_activity_stopwatch_button);
        timerButton = view.findViewById(R.id.clock_activity_timer_button);

        alarmButton.setIsActive(true);

        // init UI components
        timeLabel = (TextView) view.findViewById(R.id.fragment_clock_clock_current_time_label);
        alarmLabel = (TextView) view.findViewById(R.id.fragment_clock_clock_current_alarm_label);
        buttonSetAlarm = (Button) view.findViewById(R.id.fragment_clock_clock_button_set);
        buttonDelete = (Button) view.findViewById(R.id.fragment_clock_clock_button_delete);

        // init controller
        controller = new ClockClockFragmentController(this);

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                if (clockTabsEventListener != null) {
                    clockTabsEventListener.onCloseButtonPress();
                }
            }
        });

        alarmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                if (clockTabsEventListener != null) {
                    clockTabsEventListener.onClockButtonPress();
                }
            }
        });

        stopwatchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                if (clockTabsEventListener != null) {
                    clockTabsEventListener.onStopwatchButtonPress();
                }
            }
        });

        timerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                if (clockTabsEventListener != null) {
                    clockTabsEventListener.onTimerButtonPress();
                }
            }
        });

        buttonSetAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controller.onButtonSetAlarmPress();
            }
        });

        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controller.onButtonDeletePress();
            }
        });
    }

    @Override
    public void onResume () {
        super.onResume();
        controller.onFragmentResume();
    }

    @Override
    public void onPause () {
        super.onPause();
        controller.onFragmentPause();
    }

    public void setNoAlarm () {
        buttonDelete.setIsDisabled(true);
        alarmLabel.setText(getResources().getString(R.string.fragment_clock_clock_no_alarm_set));
    }

    public void setAlarmTime (Date time) {
        buttonDelete.setIsDisabled(false);
        String alarmTimeLabelText =
                getResources().getString(R.string.fragment_clock_clock_alarm_set_label)
                .concat(" ")
                .concat(Time.dateToHoursAndMinutes(time));
        alarmLabel.setText(alarmTimeLabelText);
    }

    public void setTime (Date time) {
        timeLabel.setText(Time.dateToHoursAndMinutes(time));
    }

    @Override
    public void onShow() {
        // do nothing
    }

    @Override
    public void onHide() {
        // do nothing
    }

    @Override
    public void setClockTabsEventListener(ClockTabsEventListener clockTabsEventListener) {
        this.clockTabsEventListener = clockTabsEventListener;
    }
}
