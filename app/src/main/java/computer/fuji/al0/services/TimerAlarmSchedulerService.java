package computer.fuji.al0.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;

import computer.fuji.al0.models.ClockTimer;

import java.util.ArrayList;

public class TimerAlarmSchedulerService {
    private static ArrayList<ClockTimer> timers = new ArrayList<>();
    private static SharedPreferences sharedPreferences;
    private static String TIMER_KEY_PREFIX = "TIMER_KEY";
    private static String TIMER_STORED_KEYS_KEY = "TIMER_STORED_KEYS_KEY";
    public static String INTENT_EXTRA_TIMER_ID = "INTENT_EXTRA_TIMER_INFO";

    public static ArrayList<ClockTimer> getClockTimers (Context context) {
        timers.clear();
        timers.addAll(getStoredTimers(context));
        return timers;
    }

    public static ClockTimer getMostRecentElapsedClockTimer (Context context) {
        ArrayList<ClockTimer> timers = getClockTimers(context);
        ClockTimer mostRecentElapsedClockTimer = null;
        long timeNow = System.currentTimeMillis();
        for (ClockTimer timer : timers) {
            // check if timer is elapsed
            long timerTime = timer.getEndTime().getTime();
            if (timerTime < timeNow) {
                // check if timer is later than mostRecentElapsedClockTimer
                if (mostRecentElapsedClockTimer == null) {
                    mostRecentElapsedClockTimer = timer;
                } else if (timerTime > mostRecentElapsedClockTimer.getEndTime().getTime()) {
                    mostRecentElapsedClockTimer = timer;
                }
            }
        }

        return mostRecentElapsedClockTimer;
    }

    public static void deleteTimer (Context context, ClockTimer timer) {
        for (int i = 0; i < timers.size(); i++) {
            if (timers.get(i).getId().equals(timer.getId())) {
                // delete timer from list
                timers.remove(i);
                // delete timer from scheduled timers alarms
                cancelTimerAlarm(context, timer);
                // delete timer from shared preferences
                deleteStoredTimer(context, timer);
                return;
            }
        }
    }

    public static void addTimer (Context context, long durationInSeconds) {
        int timerIndex = getFirstAvailableTimerIndex() + 1;
        String timerIndexString = String.valueOf(timerIndex);
        ClockTimer newTimer = new ClockTimer("clock_timer" + timerIndexString, timerIndex, "T" + timerIndexString, durationInSeconds);
        timers.add(newTimer);
        // add timer to receiver service
        addTimerAlarm(context, newTimer);
        // store timer
        storeTimer(context, newTimer);
    }

    public static void setStoredTimers (Context context) {
        timers = getStoredTimers(context);
        for (ClockTimer timer : timers) {
            // check if timer is not passed
            if (timer.getEndTime().getTime() >= System.currentTimeMillis()) {
                addTimerAlarm(context, timer);
            }
        }
    }

    // remove all passed timers
    public static void removePassedTimers (Context context) {
        // get all store timers
        ArrayList<ClockTimer> storedTimers = getStoredTimers(context);
        // clear current timers list
        timers.clear();
        for (ClockTimer timer : storedTimers) {
            // check if timer is passed
            if (timer.getEndTime().getTime() < System.currentTimeMillis()) {
                // remove timer
                cancelTimerAlarm(context, timer);
                deleteStoredTimer(context, timer);
            } else {
                // add timer to timers list
                timers.add(timer);
            }
        }
    }

    // get the first available index,
    // make sure new added timer does not get an index lower then the present timers
    // find the first minimum index available
    // eg 1, 2, 3, 4 -> 5
    // eg 1, 5 -> 6
    // eg nothing -> 0
    private static int getFirstAvailableTimerIndex () {
        int lastAvailableIndex = 0;
        for (int i = 0; i < timers.size(); i++) {
            int timerIndex = timers.get(i).getIndex();
            if (timerIndex > lastAvailableIndex) {
                lastAvailableIndex = timerIndex;
            }
        }

        return lastAvailableIndex;
    }

    // add timer to TimerAlarmReceiver service
    // and store the added timer
    public static void addTimerAlarm (Context context, ClockTimer timer) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, TimerAlarmReceiver.class);
        intent.putExtra(INTENT_EXTRA_TIMER_ID, timer.getId());

        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, timerToPendingIntentRequestCode(timer), intent, 0);

        /*
        if (alarmManager != null) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timer.getEndTime().getTime(), alarmIntent);
        }
         */
        AlarmManager.AlarmClockInfo alarmClockInfo = new AlarmManager.AlarmClockInfo(timer.getEndTime().getTime(), alarmIntent);
        alarmManager.setAlarmClock(alarmClockInfo, alarmIntent);
    }

    // create a request code specific to a given timer
    private static int timerToPendingIntentRequestCode (ClockTimer timer) {
        // use timer index and add an arbitrary number to prevent clash with other pending intent, like the Alarm one
        return timer.getIndex() + 100;
    }

    // delete daily alarm
    public static void cancelTimerAlarm(Context context, ClockTimer timer) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, TimerAlarmReceiver.class);
        PendingIntent alarmPendingIntent = PendingIntent.getBroadcast(context, timerToPendingIntentRequestCode(timer), intent, 0);
        alarmManager.cancel(alarmPendingIntent);
        deleteStoredTimer(context, timer);
    }

    // store a timer
    private static void storeTimer (Context context, ClockTimer timer) {
        sharedPreferences = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        String serializedTimer = timer.toJSONObject();
        String timerKey = TIMER_KEY_PREFIX + timer.getIndex();
        sharedPreferences.edit().putString(timerKey, serializedTimer).apply();

        String [] storedKeys = getStoredTimerKeys(context);
        // add new key to stored keys
        updateStoredTimerKeys(context, timerKey.concat(",").concat(TextUtils.join(",", storedKeys)));
    }

    // delete a stored timer
    private static void deleteStoredTimer (Context context, ClockTimer timer) {
        sharedPreferences = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        String timerKey = TIMER_KEY_PREFIX + timer.getIndex();
        // remove stored timer
        sharedPreferences.edit().remove(timerKey).apply();
        // remove stored timer key
        deleteStoredTimerKey(context, timerKey);
    }

    // update stored timer keys
    private static void updateStoredTimerKeys (Context context, String keys) {
        sharedPreferences = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        sharedPreferences.edit().putString(TIMER_STORED_KEYS_KEY, keys ).apply();
    }

    private static void deleteStoredTimerKey (Context context, String timerKey) {
        // remove stored timer key
        String [] storedKeys = getStoredTimerKeys(context);
        // populate store key without the removed one
        ArrayList<String> updatedStoredKeys = new ArrayList<>();
        for(String key : storedKeys) {
            if (!key.equals(timerKey)) {
                updatedStoredKeys.add(key);
            }
        }

        // store new keys
        updateStoredTimerKeys(context, TextUtils.join(",", updatedStoredKeys));
    }

    // get all stored timer keys
    private static String[] getStoredTimerKeys (Context context) {
        sharedPreferences = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        String storedKeys = sharedPreferences.getString(TIMER_STORED_KEYS_KEY, "");
        return storedKeys.split(",");
    }

    // get all stored timers
    private static ArrayList<ClockTimer> getStoredTimers (Context context) {
        sharedPreferences = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        // get all stored timers keys
        String[] storedKeys = getStoredTimerKeys(context);
        // init timers list
        ArrayList<ClockTimer> timers = new ArrayList<>();
        // load stored ClockTimer at stored keys
        for (int i = 0; i < storedKeys.length; i++) {
            String timerKey = storedKeys[i];
            // check for a valid string
            if (timerKey.length() > 0) {
                // get JSON string
                String serializedTimer = sharedPreferences.getString(timerKey, null);
                // check if is a valid timer
                if (serializedTimer != null) {
                    timers.add(ClockTimer.serializedTimerToClockTimer(serializedTimer));
                } else {
                    // delete the stored key if is not a valid timer
                    deleteStoredTimerKey(context, timerKey);
                }
            }
        }

        return timers;
    }

}
