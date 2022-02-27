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
import computer.fuji.al0.controllers.ClockStopwatchFragmentController;
import computer.fuji.al0.utils.ClockActivityTabFragment;
import computer.fuji.al0.utils.Time;

public class ClockStopwatchFragment extends Fragment implements ClockActivityTabFragment {
    private ClockStopwatchFragmentController controller;
    private ClockTabsEventListener clockTabsEventListener;

    Button closeButton;
    Button alarmButton;
    Button stopwatchButton;
    Button timerButton;

    private TextView currentTimeLabel;
    private Button buttonStart;
    private Button buttonStop;
    private Button buttonReset;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_clock_stopwatch, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        closeButton = view.findViewById(R.id.clock_activity_close_button);
        alarmButton = view.findViewById(R.id.clock_activity_alarm_button);
        stopwatchButton = view.findViewById(R.id.clock_activity_stopwatch_button);
        timerButton = view.findViewById(R.id.clock_activity_timer_button);

        stopwatchButton.setIsActive(true);

        // init UI components
        currentTimeLabel = (TextView) view.findViewById(R.id.fragment_clock_stopwatch_current_time_label);
        buttonStart = (Button) view.findViewById(R.id.fragment_clock_stopwatch_button_start);
        buttonStop = (Button) view.findViewById(R.id.fragment_clock_stopwatch_button_stop);
        buttonReset = (Button) view.findViewById(R.id.fragment_clock_stopwatch_button_reset);

        // init controller
        controller = new ClockStopwatchFragmentController(this);

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

        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controller.onButtonStartPress();
            }
        });

        buttonStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controller.onButtonStopPress();
            }
        });

        buttonReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controller.onButtonResetPress();
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

    public void setTimeIsRunning (boolean timeIsRunning) {
        if (timeIsRunning) {
            buttonStart.setVisibility(View.GONE);
            buttonStop.setVisibility(View.VISIBLE);
        } else {
            buttonStart.setVisibility(View.VISIBLE);
            buttonStop.setVisibility(View.GONE);
        }
    }

    public void setCurrentTime (int decimals) {
        currentTimeLabel.setText(Time.decimalsToHMS(decimals));
    }

    public void setTimerIsStarted (boolean isStarted) {
        buttonReset.setIsDisabled(!isStarted);
    }

    public void setButtonResetEnabled (boolean isEnabled) {
        buttonReset.setIsDisabled(!isEnabled);
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
