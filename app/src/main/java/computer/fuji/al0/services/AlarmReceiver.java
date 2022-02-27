package computer.fuji.al0.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        WakeLocker.acquire(context, WakeLocker.Type.ALARM);
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            startScheduleAlarmService(context, intent);
        } else {
            startAlarmService(context, intent);
            AlarmSchedulerService.setStoredAlarm(context);
        }
    }

    private void startAlarmService (Context context, Intent intent) {
        Intent intentService = new Intent(context, AlarmService.class);
        context.startService(intentService);
    }

    private void startScheduleAlarmService(Context context, Intent intent) {
        AlarmSchedulerService.setStoredAlarm(context);
        WakeLocker.release(WakeLocker.Type.ALARM);
    }
}
