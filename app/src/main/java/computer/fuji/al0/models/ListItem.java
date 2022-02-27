package computer.fuji.al0.models;

public class ListItem {
    public enum Type { HEADER, ITEM, EMPTY }
    private String id;
    private String text;
    private boolean isMarked;
    private String rightText;
    private boolean isActive;
    private Type type;
    private String customMark;

    public ListItem (String id, String text, boolean isMarked) {
        this.id = id;
        this.text = text;
        this.isMarked = isMarked;
        this.isActive = false;
        this.type = Type.ITEM;
    }

    // getters
    public String getId () { return this.id; }
    public String getText () { return this.text; }
    public boolean getIsMarked () { return this.isMarked; }
    public String getRightText () { return this.rightText; }
    public boolean getIsActive () { return this.isActive; }
    public Type getType () { return this.type; }
    public String getCustomMark () { return this.customMark; }

    // setters
    public void setText (String text) {
        this.text = text;
    }

    public void setRightText (String text) {
        this.rightText = text;
    }

    public void setIsMarked (boolean isMarked) {
        this.isMarked = isMarked;
    }

    public void setIsActive (boolean isActive) {
        this.isActive = isActive;
    }

    public void setType (Type type) {
        this.type = type;
    }

    public void setCustomMark (String customMark) {
        this.customMark = customMark;
    }
}
