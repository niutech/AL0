package computer.fuji.al0.models;

import android.provider.CallLog;

import java.util.Date;

public class Call {
    public static enum  Type { MISSED, OUTGOING, INCOMING, UNKNOW_TYPE };

    private Contact contact;
    private Date date;
    private Type type;
    private int duration;
    private boolean isNew;
    private String id;

    public Call (String id, Contact contact, int callLogType, Date date, int duration, boolean isNew) {
        this.contact = contact;
        this.date = date;
        this.type = callLogTypeToCallType(callLogType);
        this.duration = duration;
        this.isNew = isNew;
        this.id = id;
    }

    private static Type callLogTypeToCallType (int callLogType) {
        switch (callLogType) {
            case CallLog.Calls.OUTGOING_TYPE:
                return Type.OUTGOING;

            case CallLog.Calls.INCOMING_TYPE:
                return Type.INCOMING;

            case CallLog.Calls.MISSED_TYPE:
                return Type.MISSED;

            default:
                return Type.UNKNOW_TYPE;
        }
    }

    // getters

    public Contact getContact() {
        return contact;
    }

    public Date getDate() {
        return date;
    }

    public Type getType() {
        return type;
    }

    public int getDuration() {
        return duration;
    }

    public boolean getIsNew() {
        return isNew;
    }

    public String getId() {
        return id;
    }

    //setters
    public void setIsNew (boolean isNew) {
      this.isNew = isNew;
    }
}
