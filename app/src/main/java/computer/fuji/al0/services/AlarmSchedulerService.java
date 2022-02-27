package computer.fuji.al0.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import computer.fuji.al0.utils.Time;
import java.util.Calendar;
import java.util.Date;

public class AlarmSchedulerService {
    private static int alarmId = 23;

    private static String DAILY_ALARM_KEY = "DAILY_ALARM_KEY";
    private static SharedPreferences sharedPreferences;

    // set daily alarm
    // hour interval 0-11, noon is 0 pm, mindnight 0 am
    // minutes interval 0-59
    // isAm true for AM false for PM
    public static void setDailyAlarm (Context context, int hour, int minute, boolean isAm) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, alarmId, intent, 0);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.AM_PM, isAm ? 0 : 1);

        if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
            calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) + 1);
        }

        // alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, alarmIntent);
        AlarmManager.AlarmClockInfo alarmClockInfo = new AlarmManager.AlarmClockInfo(calendar.getTimeInMillis(), alarmIntent);
        alarmManager.setAlarmClock(alarmClockInfo, alarmIntent);
        storeDailyAlarm(context, calendar.getTime());
    }

    // delete daily alarm
    public static void cancelDailyAlarm(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent alarmPendingIntent = PendingIntent.getBroadcast(context, alarmId, intent, 0);
        alarmManager.cancel(alarmPendingIntent);
        deleteStoredDailyAlarm(context);
    }

    // schedule a previously set alarm
    // check if a valid alarm is set in shared preferences then schedule the alarm
    public static void setStoredAlarm (Context context) {
        Date scheduledDate = getScheduledDailyAlarm(context);
        if (scheduledDate != null) {
            int hour = Integer.parseInt(Time.hhFormat.format(scheduledDate));
            int minute = Integer.parseInt(Time.mmFormat.format(scheduledDate));
            boolean isAm = Time.aFormat.format(scheduledDate).toLowerCase().equals("am");

            setDailyAlarm(context, hour, minute, isAm);
        }
    }

    // get the current daily alarm
    public static Date getScheduledDailyAlarm (Context context) {
        return getStoredDailyAlarm(context);
    }

    // store the daily alarm on sharedPreferences
    private static void storeDailyAlarm (Context context, Date date) {
        sharedPreferences = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        sharedPreferences.edit().putString(DAILY_ALARM_KEY, date.toString()).apply();
    }

    // delete daily alarm from sharedPreferences
    private static void deleteStoredDailyAlarm (Context context) {
        sharedPreferences = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        sharedPreferences.edit().remove(DAILY_ALARM_KEY).apply();
    }

    // get daily alarm from sharedPreferences
    private static Date getStoredDailyAlarm (Context context) {
        sharedPreferences = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        String storedDailyAlarm =  sharedPreferences.getString(DAILY_ALARM_KEY, null);
        if (storedDailyAlarm != null) {
            return new Date(storedDailyAlarm);
        } else {
            return null;
        }
    }
}
