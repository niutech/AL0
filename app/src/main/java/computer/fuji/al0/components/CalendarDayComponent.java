package computer.fuji.al0.components;

import android.content.Context;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import computer.fuji.al0.R;

public class CalendarDayComponent extends LinearLayout {
    private TextView dayTextView;
    private boolean isHighlight = false;

    public CalendarDayComponent(@NonNull Context context) {
        super(context);
        init(context);
    }

    private void init (Context context) {
        inflate(context, R.layout.calendar_day, this);
        dayTextView = (TextView) findViewById(R.id.calendar_day);
    }

    public void setText (String text) {
        dayTextView.setText(text);
    }

    public void setHighlight () {
        isHighlight = true;
        dayTextView.setTextColor(getResources().getColor(R.color.color_5, null));
        dayTextView.setBackground(getResources().getDrawable(R.drawable.calendar_day_active_background));
    }

    public void setLight () {
        if (!isHighlight) {
            dayTextView.setTextAppearance(R.style.calendar_day_light);
        }
    }
}
