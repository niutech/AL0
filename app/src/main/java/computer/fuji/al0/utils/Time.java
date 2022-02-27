package computer.fuji.al0.utils;

import android.content.Context;
import android.text.format.DateUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Time {
    public static long MIN_IN_MS = 1000 * 60;
    private static long DAY_IN_MS = MIN_IN_MS * 60 * 24;
    private static boolean is24HourFormat = false;

    private static SimpleDateFormat todayDateFormat = new SimpleDateFormat(", h:mm a");
    private static SimpleDateFormat lessThanOneWeekDateFormat = new SimpleDateFormat("EEEE, h:mm a");
    private static SimpleDateFormat lessThanOneYearDateFormat = new SimpleDateFormat("d MMM, h:mm a");
    private static SimpleDateFormat defaultDateFormat = new SimpleDateFormat("d MMM yyyy, h:mm a");
    private static SimpleDateFormat hhmmaFormat = new SimpleDateFormat("hh:mm a");
    public static SimpleDateFormat hhFormat = new SimpleDateFormat("hh");
    public static SimpleDateFormat mmFormat = new SimpleDateFormat("mm");
    public static SimpleDateFormat sFormat = new SimpleDateFormat("s");
    public static SimpleDateFormat aFormat = new SimpleDateFormat("a");
    public static SimpleDateFormat HmmFormat = new SimpleDateFormat("H:mm");
    public static SimpleDateFormat hmmaFormat = new SimpleDateFormat("h:mm a");
    public static SimpleDateFormat dMMMhmmaFormat = new SimpleDateFormat("d MMM, h:mm a");
    public static SimpleDateFormat MMMMyyyyFormat = new SimpleDateFormat("MMMM yyyy");
    public static SimpleDateFormat EEEdMMMFormat = new SimpleDateFormat("EEE d MMM");
    public static SimpleDateFormat MMMMFormat = new SimpleDateFormat("MMMM");
    public static SimpleDateFormat cameraFileNameFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");

    public static void updateIs24HourFormat (Context context) {
        is24HourFormat = android.text.format.DateFormat.is24HourFormat(context);
    }

    public static boolean getIs24HourFormat () {
        return is24HourFormat;
    }

    // format seconds in xh ym zs
    // 45 -> 45s
    // 120 -> 2m 0s
    // 3602 -> 1h 2s
    public static String secondsToHMS (int seconds) {
        int h = seconds / 3600;
        int m = (seconds % 3600) / 60;
        int s = seconds % 60;

        String hString = h > 0 ? String.valueOf(h) + "h " : "";
        String mString = m > 0 ? String.valueOf(m) + "m " : "";
        return hString + mString + String.valueOf(s)+"s";
    }

    public static String decimalsToHMS (int decimals) {
        int seconds = decimals / 10;
        int d = decimals % 10;
        int h = seconds / 3600;
        int m = (seconds % 3600) / 60;
        int s = seconds % 60;

        String hString = h > 0 ? String.valueOf(h) + "h " : "";
        String mString = m > 0 ? String.valueOf(m) + "m " : "";
        return hString + mString + String.valueOf(s) +"." + String.valueOf(d) +"s";
    }

    public static String formatDate (Context context, Date date) {
        return (String) DateUtils.getRelativeDateTimeString(context, date.getTime(), DateUtils.DAY_IN_MILLIS, DateUtils.WEEK_IN_MILLIS, 0);
        /*
        Date now = new Date(System.currentTimeMillis() - MIN_IN_MS);
        Date oneWeekAgo = new Date(System.currentTimeMillis() - (7 * DAY_IN_MS));
        Date oneYearAgo = new Date(System.currentTimeMillis() - (365 * DAY_IN_MS));
        // check how old is date
        if (date.after(now)) {
            // less than 1 minute
            return context.getResources().getString(R.string.phone_contact_activity_time_now);
        } else if (DateUtils.isToday(date.getTime())) {
            return context.getResources().getString(R.string.phone_contact_activity_time_today).concat(todayDateFormat.format(date));
        } else if (date.after(oneWeekAgo)) {
            // less than 1 week
            return lessThanOneWeekDateFormat.format(date);
        } else if (date.after(oneYearAgo)) {
            // less than 1 year
            return lessThanOneYearDateFormat.format(date);
        } else {
            return defaultDateFormat.format(date);
        }

         */
    }

    public static Date addDay (Date date, int i) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DAY_OF_YEAR, i);
        return cal.getTime();
    }

    public static String dateTohhmma (Date date) {
        return hhmmaFormat.format(date);
    }

    public static String dateToHoursAndMinutes (Date date) {
        if (is24HourFormat) {
            return HmmFormat.format(date);
        } else {
            return hhmmaFormat.format(date);
        }
    }
}
