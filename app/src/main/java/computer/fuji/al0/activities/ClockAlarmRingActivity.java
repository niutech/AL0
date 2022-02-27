package computer.fuji.al0.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import computer.fuji.al0.R;
import computer.fuji.al0.components.Button;
import computer.fuji.al0.services.AlarmService;
import computer.fuji.al0.services.WakeLocker;
import computer.fuji.al0.utils.Preferences;
import computer.fuji.al0.utils.Time;
import computer.fuji.al0.utils.UI;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class ClockAlarmRingActivity extends AppCompatActivity {
    private TextView timeLabel;
    private Timer timeTimer;
    private Preferences preferences;

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clock_alarm_ring);
        // force turn on screen
        WakeLocker.wakeDevice(this);
        UI.hideNavigationBar(this);

        preferences = new Preferences(this);
        // disable lock screen
        preferences.setShouldShowLockScreen(false);

        timeLabel = (TextView) findViewById(R.id.clock_alarm_ring_activity_label_time);
        Button buttonStop = (Button) findViewById(R.id.clock_alarm_ring_activity_button_stop);

        buttonStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                Intent intentService = new Intent(getApplicationContext(), AlarmService.class);
                getApplicationContext().stopService(intentService);
                stopTimeTimer();
                finish();
            }
        });

        updateTimeLabel();
        startTimeTimer();
    }

    @Override
    public void onPause () {
        super.onPause();
        // clean turn on screen flag
        WakeLocker.releaseWakeDevice(this);
    }

    @Override
    public void onDestroy () {
        super.onDestroy();
        // enable lock screen
        preferences.setShouldShowLockScreen(true);
    }

    // update current time formatted in "hh:mm a" format
    private void updateTimeLabel () {
        timeLabel.setText(Time.dateTohhmma(new Date()));
    }

    // start a timer to update current time on the screen
    private void startTimeTimer () {
        updateTimeLabel();

        timeTimer = new Timer ();
        timeTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateTimeLabel();
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

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            UI.hideNavigationBar(this);
        }
    }

}
