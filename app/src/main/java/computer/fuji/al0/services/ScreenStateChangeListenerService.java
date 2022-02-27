package computer.fuji.al0.services;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import computer.fuji.al0.activities.LockScreenActivity;
import computer.fuji.al0.utils.Preferences;

public class ScreenStateChangeListenerService extends Service {
    private Preferences preferences;

    private BroadcastReceiver screenStateBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean shouldListenScreenChange = !preferences.getShouldIgnoreAppServices();
            boolean shouldShowLockScreen = preferences.getShouldShowLockScreen();
            if (shouldShowLockScreen && shouldListenScreenChange && intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                Intent lockScreenActivityIntent = new Intent(context, LockScreenActivity.class);
                lockScreenActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(lockScreenActivityIntent);
                preferences.setShouldShowLockScreen(true);
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_SCREEN_ON);
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(screenStateBroadcastReceiver, intentFilter);
        preferences = new Preferences(getApplicationContext());
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(screenStateBroadcastReceiver);
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
