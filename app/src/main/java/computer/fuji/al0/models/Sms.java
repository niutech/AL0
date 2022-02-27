package computer.fuji.al0.models;

import java.util.Date;

public class Sms {
    public enum Type { OUTBOUND, INBOUND };

    private Contact contact;
    private Type type;
    private String body;
    private Date date;
    private String id;
    private boolean isSeen;
    private boolean isRead;

    public Sms(String id, Contact contact, Type type, String body, Date date, boolean isSeen, boolean isRead) {
        this.id = id;
        this.contact = contact;
        this.type = type;
        this.body = body;
        this.date = date;
        // this.id = String.valueOf(date.getTime()).concat(type.toString()).concat(contact.getPhoneNumber());
        this.isSeen = isSeen;
        this.isRead = isRead;
    }

    // getters

    public Contact getContact() {
        return contact;
    }

    public Type getType() {
        return type;
    }

    public String getBody() {
        return body;
    }

    public Date getDate() {
        return date;
    }

    public String getId() {
        return id;
    }

    public boolean getIsSeen () {
        return isSeen;
    }

    public boolean getIsRead () {
        return isRead;
    }

    // setters

    public void setId (String id) {
        this.id = id;
    }

    public void setIsSeen (boolean isSeen) {
        this.isSeen = isSeen;
    }

    public void setIsRead (boolean isRead) {
        this.isRead = isRead;
    }
}
