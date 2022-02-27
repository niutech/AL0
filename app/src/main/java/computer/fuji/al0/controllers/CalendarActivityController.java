package computer.fuji.al0.controllers;

import computer.fuji.al0.activities.CalendarActivity;
import computer.fuji.al0.utils.StringUtils;
import computer.fuji.al0.utils.Time;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class CalendarActivityController {
    private CalendarActivity activity;
    private Calendar calendar;

    public CalendarActivityController (CalendarActivity activity) {
        this.activity = activity;
        calendar = new GregorianCalendar();
        updateCurrentMonth(new Date());
    }

    // Given a month index, 0-11, get current locale month name
    private String getMonthName (int monthIndex) {
        Calendar adjacentMonths = new GregorianCalendar();
        adjacentMonths.set(Calendar.MONTH, monthIndex);
        String monthName = Time.MMMMFormat.format(adjacentMonths.getTime());
        // capitalize month name
        return StringUtils.capitalize(monthName);
    }

    // get previous month index, 0-11
    private int getPreviousMonthIndex () {
        int currentMonthIndex = calendar.get(Calendar.MONTH);
        int previousMonthIndex = currentMonthIndex > 0 ? currentMonthIndex - 1 : 11;
        return previousMonthIndex;
    }

    // get previous month year
    // when current month is January previous month year should be current year - 1
    private int getPreviousMonthYear () {
        int currentMonthIndex = calendar.get(Calendar.MONTH);
        int currentMonthYear = calendar.get(Calendar.YEAR);
        int previousMonthYear = currentMonthIndex > 0 ? currentMonthYear : currentMonthYear - 1;
        return previousMonthYear;
    }

    // get next month index, 0-11
    private int getNextMonthIndex () {
        int currentMonthIndex = calendar.get(Calendar.MONTH);
        int nextMonthIndex = currentMonthIndex < 11 ? currentMonthIndex + 1 : 0;
        return nextMonthIndex;
    }

    // get next month year
    // when current month is December next month year should be current year + 1
    private int getNextMonthYear () {
        int currentMonthIndex = calendar.get(Calendar.MONTH);
        int currentMonthYear = calendar.get(Calendar.YEAR);
        int nextMonthYear = currentMonthIndex < 11 ? currentMonthYear : currentMonthYear + 1;
        return nextMonthYear;
    }

    // update current month
    private void updateCurrentMonth (Date currentMonth) {
        calendar.setTime(currentMonth);
        String previousMonthName = getMonthName(getPreviousMonthIndex());
        String nextMonthName = getMonthName(getNextMonthIndex());
        activity.setMonth(currentMonth, previousMonthName, nextMonthName);
    }

    // events
    public void onButtonClosePress () {
        activity.finish();
    }

    public void onButtonNextMonthPress () {
        calendar.set(Calendar.YEAR, getNextMonthYear());
        calendar.set(Calendar.MONTH, getNextMonthIndex());
        updateCurrentMonth(calendar.getTime());
    }

    public void onButtonPreviousMonthPress () {
        calendar.set(Calendar.YEAR, getPreviousMonthYear());
        calendar.set(Calendar.MONTH, getPreviousMonthIndex());
        updateCurrentMonth(calendar.getTime());
    }
}
