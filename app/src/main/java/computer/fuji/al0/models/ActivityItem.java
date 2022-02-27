package computer.fuji.al0.models;

import java.util.Comparator;
import java.util.Date;

public class ActivityItem {
    public static enum Type {
        CALL, SMS
    }

    public static enum Direction {
        INBOUND, OUTBOUND
    }

    public static Comparator dateComparator = new Comparator<ActivityItem>() {
        @Override
        public int compare(ActivityItem activityItem1, ActivityItem activityItem2) {
            return activityItem1.getDate().compareTo(activityItem2.getDate());
        }
    };

    private String id;
    private Type type;
    private Direction direction;
    private Date date;
    private Sms sms;
    private Call call;

    public ActivityItem (Type type, Direction direction, Date date, Sms sms, Call call) {
        this.type = type;
        this.direction = direction;
        this.date = date;
        this.sms = sms;
        this.call = call;
        this.id = type == Type.CALL ? call.getId() : sms.getId();
    }

    public static ActivityItem smsToActivityItem (Sms sms) {
        Direction direction = sms.getType() == Sms.Type.OUTBOUND ? Direction.OUTBOUND : Direction.INBOUND;
        return new ActivityItem(Type.SMS, direction, sms.getDate(), sms, null);
    }

    public static ActivityItem callToActivityItem (Call call) {
        Direction direction;

        switch (call.getType()) {
            case OUTGOING:
                direction = Direction.OUTBOUND;
                break;
            case MISSED:
            case INCOMING:
            default:
                direction = Direction.INBOUND;
                break;
        }

        return new ActivityItem(Type.CALL, direction, call.getDate(), null, call);
    }

    // getters

    public Type getType() {
        return type;
    }

    public Direction getDirection () {
        return direction;
    }

    public Date getDate () {
        return date;
    }

    public Sms getSms () {
        return sms;
    }

    public Call getCall () {
        return call;
    }

    public String getId () {
        return id;
    }

    public Contact getContact () {
        if (type == Type.SMS) {
            return this.sms.getContact();
        } else {
            return this.call.getContact();
        }
    }

    // setters
    public void setSms (Sms sms) {
        this.sms = sms;
    }

    public void setCall (Call call) {
        this.call = call;
    }
}
