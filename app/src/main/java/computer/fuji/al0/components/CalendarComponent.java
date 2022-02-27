package computer.fuji.al0.components;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewTreeObserver;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import computer.fuji.al0.R;
import computer.fuji.al0.utils.StringUtils;
import computer.fuji.al0.utils.Time;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class CalendarComponent extends LinearLayout {
    private TextView monthLabel;
    private GridLayout daysWrapper;
    private GregorianCalendar calendar;
    private GregorianCalendar todayCalendar;
    private int minDayWidth = 0;
    private int firstDayOfWeek = 0;

    public CalendarComponent(Context context) {
        super(context);
        init();
    }

    private void init () {
        LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layoutInflater.inflate(R.layout.calendar, this);

        monthLabel = (TextView) findViewById(R.id.calendar_month_label);
        daysWrapper = (GridLayout) findViewById(R.id.calendar_days);
        calendar = new GregorianCalendar();
        todayCalendar = new GregorianCalendar();
        todayCalendar.setTime(new Date());

        // add global layout listener to find GridView width
        daysWrapper.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                // remove listener since grid width wont change
                daysWrapper.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                updateDaysWidth(daysWrapper.getWidth());
            }
        });
    }

    // update all days width to match GridView width
    private void updateDaysWidth (int daysWrapperWidth) {
        int dayWidth = daysWrapperWidth / 7;
        // store day min width in minDayWidth to set directly min width on "addDay" method on future "setMonth" call
        minDayWidth = dayWidth;
        for (int i = 0; i < daysWrapper.getChildCount(); i++) {
            CalendarDayComponent day = (CalendarDayComponent) daysWrapper.getChildAt(i);
            day.getChildAt(0).setMinimumWidth(dayWidth);
        }
    }

    // set different style for weekend days
    private void updateDaysWeekendStyle () {
        int firstDayOfWeekStartingFrom0 = firstDayOfWeek - 1;
        for (int i = 0; i < daysWrapper.getChildCount(); i++) {
            int sundayIndex = 7 - firstDayOfWeekStartingFrom0;
            int saturdayIndex = 6 - firstDayOfWeekStartingFrom0;

            if ((i - sundayIndex) % 7 == 0 || (i - saturdayIndex) % 7 == 0) {
                CalendarDayComponent day = (CalendarDayComponent) daysWrapper.getChildAt(i);
                day.setLight();
            }
        }
    }

    // add day to daysWrapper
    private void addDay (CalendarDayComponent day) {
        // when minDayWidth is computed set day min width in order to fill the grid view
        if (minDayWidth > 0) {
            day.getChildAt(0).setMinimumWidth(minDayWidth);
        }

        daysWrapper.addView(day);
    }

    // populate week days
    private void populateWeekDays (int firstDayOfWeek) {
        Calendar weekCalendar = Calendar.getInstance(Locale.getDefault());
        for (int i = firstDayOfWeek; i <= firstDayOfWeek + 6; i ++) {
            boolean isWeekEnd = false;
            if (i <= 7) {
                weekCalendar.set(Calendar.DAY_OF_WEEK, i);
            } else {
                weekCalendar.set(Calendar.DAY_OF_WEEK, i - 7);
            }

            CalendarDayComponent day = new CalendarDayComponent(getContext());
            day.setText(weekCalendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault()).substring(0, 1).toUpperCase());

            addDay(day);
        }

    }

    // add previous month's days which happens in the current month's first week
    private void populatePreviousMonthWeek (int firstWeekDay, int firstMonthWeekDay) {
        int previousMonthDays = 0;

        if (firstMonthWeekDay < firstWeekDay) {
            previousMonthDays = firstMonthWeekDay + 7 - firstWeekDay;
        } else {
            previousMonthDays = firstMonthWeekDay - firstWeekDay;
        }

        if (previousMonthDays > 0) {
            for (int i = 0; i < previousMonthDays; i++) {
                CalendarDayComponent day = new CalendarDayComponent(getContext());
                day.setText("");
                addDay(day);
            }
        } else {
            // do nothing
        }
    }

    // add previous month's days which happens in the current month's first week
    private void populateMonthDays (int monthDays, boolean isCurrentMonth) {
        int currentDay = -1;
        if (isCurrentMonth) {
            currentDay = todayCalendar.get(Calendar.DAY_OF_MONTH);
        }

        for (int i = 1; i <= monthDays; i++) {
            CalendarDayComponent day = new CalendarDayComponent(getContext());
            day.setText(String.valueOf(i));
            if (currentDay == i) {
                day.setHighlight();
            }
            addDay(day);
        }
    }

    private void populateDays (Date date) {
        calendar.setTime(date);
        // check if date is same month and year than today
        boolean isCurrentMonth = calendar.get(Calendar.YEAR) == todayCalendar.get(Calendar.YEAR) && calendar.get(Calendar.MONTH) == todayCalendar.get(Calendar.MONTH);
        // get number of days in current month
        int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        // get first week day in current locale
        firstDayOfWeek = calendar.getFirstDayOfWeek();
        // calculate the number of days in the month's first week belonging to the previous month
        Calendar firstDayCalendar = new GregorianCalendar();
        firstDayCalendar.setTime(date);
        firstDayCalendar.set(Calendar.DAY_OF_MONTH, 1);
        int firstWeekDayOfMonth = firstDayCalendar.get(Calendar.DAY_OF_WEEK);

        populateWeekDays(firstDayOfWeek);
        populatePreviousMonthWeek(firstDayOfWeek, firstWeekDayOfMonth);
        populateMonthDays(daysInMonth, isCurrentMonth);
    }

    // set current month
    public void setMonth (Date date) {
        daysWrapper.removeAllViews();
        monthLabel.setText(StringUtils.capitalize(Time.MMMMyyyyFormat.format(date)));
        populateDays(date);
        updateDaysWeekendStyle();
    }
}
