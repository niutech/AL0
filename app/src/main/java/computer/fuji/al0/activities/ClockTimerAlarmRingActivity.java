package computer.fuji.al0.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import computer.fuji.al0.R;
import computer.fuji.al0.components.Button;
import computer.fuji.al0.models.ClockTimer;
import computer.fuji.al0.services.TimerAlarmSchedulerService;
import computer.fuji.al0.services.TimerAlarmService;
import computer.fuji.al0.services.WakeLocker;
import computer.fuji.al0.utils.Preferences;
import computer.fuji.al0.utils.Time;
import computer.fuji.al0.utils.UI;

import java.util.ArrayList;

public class ClockTimerAlarmRingActivity extends AppCompatActivity {
    private static ClockTimerAlarmRingActivity currentActivity;
    private Preferences preferences;

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clock_timer_alarm_ring);
        // force turn on screen
        WakeLocker.wakeDevice(this);
        UI.hideNavigationBar(this);

        // prevent running multiple instance of this activity
        if (currentActivity != null) {
            currentActivity.finish();
        }

        currentActivity = this;

        Intent intent = getIntent();
        String timerId = intent.getStringExtra(TimerAlarmSchedulerService.INTENT_EXTRA_TIMER_ID);

        TextView timerTitle = (TextView) findViewById(R.id.clock_timer_ring_activity_label_timer_title);
        TextView timerInfoLabel = (TextView) findViewById(R.id.clock_timer_ring_activity_label_timer_info);
        Button buttonStop = (Button) findViewById(R.id.clock_timer_ring_activity_button_stop);

        if (timerId != null) {
            ArrayList<ClockTimer> timers = TimerAlarmSchedulerService.getClockTimers(this);
            for (ClockTimer timer : timers) {
                if (timer.getId().equals(timerId)) {
                    timerTitle.setText(getString(R.string.clock_timer_ring_activity_label_timer).concat(" ").concat(timer.getName()));
                    timerInfoLabel.setText(Time.secondsToHMS((int) timer.getDuration()));
                }
            }
        }

        preferences = new Preferences(this);
        // disable lock screen
        preferences.setShouldShowLockScreen(false);

        buttonStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                Intent intentService = new Intent(getApplicationContext(), TimerAlarmService.class);
                getApplicationContext().stopService(intentService);
                finish();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        WakeLocker.releaseWakeDevice(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // enable lock screen
        preferences.setShouldShowLockScreen(true);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            UI.hideNavigationBar(this);
        }
    }

}
