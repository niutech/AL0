package computer.fuji.al0.models;

public class NumpadButtonModel {
    public static enum NumpadButtonId {
        BUTTON_1, BUTTON_2, BUTTON_3, BUTTON_4, BUTTON_5, BUTTON_6, BUTTON_7, BUTTON_8, BUTTON_9, BUTTON_0,
        BUTTON_STAR, BUTTON_HASH, BUTTON_AM, BUTTON_PM,
        BUTTON_CLEAR, BUTTON_PLUS_MINUS, BUTTON_PERCENT,
        BUTTON_DIVISION, BUTTON_MULTIPLICATION, BUTTON_MINUS, BUTTON_PLUS,
        BUTTON_COMMA, BUTTON_EQUAL, BUTTON_DELETE
    }

    private final NumpadButtonId id;
    private final int viewId;
    private final String number;
    private final String letters;
    private final int dtmfTone;

    public NumpadButtonModel (NumpadButtonId id, int viewId, String number, String letters, int dtmfTone) {
        this.id = id;
        this.viewId = viewId;
        this.number = number;
        this.letters = letters;
        this.dtmfTone = dtmfTone;
    }

    public NumpadButtonId getId() {
        return id;
    }

    public int getViewId() {
        return viewId;
    }

    public String getNumber() {
        return number;
    }

    public String getLetters() {
        return letters;
    }

    public int getDtmfTone() {
        return dtmfTone;
    }
}
