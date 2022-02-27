package computer.fuji.al0.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import computer.fuji.al0.models.ClockTimer;

public class TimerAlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        WakeLocker.acquire(context, WakeLocker.Type.TIMER);
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            startTimerScheduleAlarmService(context, intent);
        } else {
            startTimerAlarmService(context, intent);
        }
    }

    private void startTimerAlarmService (Context context, Intent intent) {
        String timerId = intent.getStringExtra(TimerAlarmSchedulerService.INTENT_EXTRA_TIMER_ID);
        if (timerId != null) {
        } else {
            ClockTimer timer = TimerAlarmSchedulerService.getMostRecentElapsedClockTimer(context);
            timerId = timer.getId();
        }

        Intent intentService = new Intent(context, TimerAlarmService.class);
        intentService.putExtra(TimerAlarmSchedulerService.INTENT_EXTRA_TIMER_ID, timerId);
        context.startService(intentService);
    }

    private void startTimerScheduleAlarmService(Context context, Intent intent) {
        TimerAlarmSchedulerService.setStoredTimers(context);
        WakeLocker.release(WakeLocker.Type.TIMER);
    }
}