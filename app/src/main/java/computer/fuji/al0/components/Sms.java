package computer.fuji.al0.components;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import computer.fuji.al0.R;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Sms extends LinearLayout {
    private computer.fuji.al0.models.Sms sms;
    private TextView textDate;
    private TextView textBody;

    private SimpleDateFormat lessThanOneWeekDateFormat = new SimpleDateFormat("EEEE, h:mm a");
    private SimpleDateFormat lessThanOneYearDateFormat = new SimpleDateFormat("d MMM, h:mm a");
    private SimpleDateFormat defaultDateFormat = new SimpleDateFormat("d MMM yyyy, h:mm a");

    public Sms(Context context) {
        super(context);
        init(context);
    }

    private void init (Context context) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layoutInflater.inflate(R.layout.sms, this);
        textDate = (TextView) findViewById(R.id.sms_date);
        textBody = (TextView) findViewById(R.id.sms_body);
    }

    // set sms
    public void setSms (computer.fuji.al0.models.Sms sms) {
        this.sms = sms;

        textDate.setText(formatDate(sms.getDate()));
        textBody.setText(sms.getBody());
    }

    // utils
    // format sms date
    private String formatDate (Date date) {
        long MIN_IN_MS = 1000 * 60;
        long DAY_IN_MS = MIN_IN_MS * 60 * 24;
        Date now = new Date(System.currentTimeMillis() - MIN_IN_MS);
        Date oneWeekAgo = new Date(System.currentTimeMillis() - (7 * DAY_IN_MS));
        Date oneYearAgo = new Date(System.currentTimeMillis() - (365 * DAY_IN_MS));
        // check how old is date
        if (date.after(now)) {
          // less than 1 minute
          return getContext().getResources().getString(R.string.phone_contact_activity_time_now);
        } else if (date.after(oneWeekAgo)) {
            // less than 1 week
            return lessThanOneWeekDateFormat.format(date);
        } else if (date.after(oneYearAgo)) {
            // less than 1 year
            return lessThanOneYearDateFormat.format(date);
        } else {
            return defaultDateFormat.format(date);
        }
    }
}
