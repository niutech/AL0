package computer.fuji.al0.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.Ringtone;
import android.net.Uri;
import android.os.IBinder;
import android.os.Vibrator;

import androidx.annotation.Nullable;

import computer.fuji.al0.activities.ClockAlarmRingActivity;
import computer.fuji.al0.utils.Preferences;

public class AlarmService extends Service {
    private Ringtone ringtone;
    private Vibrator vibrator;
    private Uri alarmRingtoneUri;

    @Override
    public void onCreate () {
        super.onCreate();
        alarmRingtoneUri = SoundManager.getDefaultAlarm(getBaseContext()).getUri();
        // ringtone = RingtoneManager.getRingtone(getApplicationContext(), alarmRingtoneUri);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Intent notificationIntent = new Intent(this, ClockAlarmRingActivity.class);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        // PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        startActivity(notificationIntent);

        SoundManager.playRingtone(getBaseContext(), alarmRingtoneUri, AudioManager.STREAM_ALARM, true);

        Preferences preferences = new Preferences(getBaseContext());
        if (preferences.getSoundSettingsVibrationEnabled()) {
            long[] pattern = {0, 100, 1000};
            vibrator.vibrate(pattern, 0);
        }

        return START_STICKY;
    }

    @Override
    public void onDestroy () {
        super.onDestroy();
        WakeLocker.release(WakeLocker.Type.ALARM);
        SoundManager.stopRingtone();
        if (vibrator != null) {
            vibrator.cancel();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
