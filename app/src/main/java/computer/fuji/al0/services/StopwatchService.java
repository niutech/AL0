package computer.fuji.al0.services;

import android.content.Context;
import android.content.SharedPreferences;

public class StopwatchService {
    private static String STOPWATCH_START_TIME_KEY = "STOPWATCH_START_TIME_KEY";
    private static String STOPWATCH_ELAPSED_TIME_KEY = "STOPWATCH_ELAPSED_TIME_KEY";
    private static String STOPWATCH_IS_RUNNING_KEY = "STOPWATCH_IS_RUNNING_KEY";
    private static SharedPreferences sharedPreferences;

    // store stopwatch start time
    public static void storeStopwatchStartTime (Context context, Long timeInMillis) {
        sharedPreferences = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        sharedPreferences.edit().putLong(STOPWATCH_START_TIME_KEY, timeInMillis).apply();
    }

    // store stopwatch elapsed time
    public static void storeStopwatchElapsedTime (Context context, int timeInDecimal) {
        sharedPreferences = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        sharedPreferences.edit().putInt(STOPWATCH_ELAPSED_TIME_KEY, timeInDecimal).apply();
    }

    // store stopwatch is running
    public static void storeStopwatchIsRunning (Context context, boolean isRunning) {
        sharedPreferences = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        sharedPreferences.edit().putBoolean(STOPWATCH_IS_RUNNING_KEY, isRunning).apply();
    }

    // delete stopwatch start time
    public static void deleteStopwatchStartTime (Context context) {
        sharedPreferences = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        sharedPreferences.edit().remove(STOPWATCH_START_TIME_KEY).apply();
    }

    // delete stopwatch elapsed time
    public static void deleteStopwatchElapsedTime (Context context) {
        sharedPreferences = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        sharedPreferences.edit().remove(STOPWATCH_ELAPSED_TIME_KEY).apply();
    }

    // delete stopwatch is running
    public static void deleteStopwatchIsRunning (Context context) {
        sharedPreferences = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        sharedPreferences.edit().remove(STOPWATCH_IS_RUNNING_KEY).apply();
    }

    // get stopwatch start time
    public static Long getStopwatchStartTime (Context context) {
        sharedPreferences = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        return sharedPreferences.getLong(STOPWATCH_START_TIME_KEY, -1);
    }

    // get stopwatch elapsed time
    public static int getStopwatchElapsedTime (Context context) {
        sharedPreferences = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        return sharedPreferences.getInt(STOPWATCH_ELAPSED_TIME_KEY, -1);
    }

    // get stopwatch is running
    public static boolean getStopwatchIsRunning (Context context) {
        sharedPreferences = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(STOPWATCH_IS_RUNNING_KEY, false);
    }
}
