package computer.fuji.al0.models;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class ClockTimer {
    private String id;
    private int index;
    private String name;
    private Date endTime;
    private long duration;
    private static long secondInMillis = 1000;

    public ClockTimer (String id, int index, String name, long durationInSeconds) {
        this.id = id;
        this.index = index;
        this.name = name;
        this.duration = durationInSeconds;
        // set end time
        Date endTime = new Date();
        endTime.setTime(System.currentTimeMillis() + (durationInSeconds * secondInMillis));
        this.endTime = endTime;
    }

    // getters
    public String getId () {
        return this.id;
    }

    public int getIndex () {
        return this.index;
    }

    public String getName () {
        return this.name;
    }

    public long getDuration () {
        return duration;
    }

    public Date getEndTime () {
        return this.endTime;
    }

    public String toJSONObject () {
        JSONObject timerJSON = new JSONObject();
        try {
            timerJSON.put("id", id);
            timerJSON.put("index", String.valueOf(index));
            timerJSON.put("name", name);
            timerJSON.put("duration", String.valueOf(duration));
            timerJSON.put("endTime", endTime.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return timerJSON.toString();
    }

    // setters
    public void setEndTime (Date endTime) {
        this.endTime = endTime;
    }

    public static ClockTimer serializedTimerToClockTimer (String serializedTimer) {
        try {
            JSONObject jsonTimer = new JSONObject(serializedTimer);
            ClockTimer timer = new ClockTimer(
                    jsonTimer.getString("id"),
                    jsonTimer.getInt("index"),
                    jsonTimer.getString("name"),
                    jsonTimer.getLong("duration")
            );

            timer.setEndTime(new Date(jsonTimer.getString("endTime")));

            return timer;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }
}