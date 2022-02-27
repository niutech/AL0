package computer.fuji.al0.components;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import computer.fuji.al0.R;

public class Dialog extends LinearLayout {
    private TextView dialogText;
    private Button dialogButtonActionRight;
    private Button dialogButtonActionLeft;
    private static final String whiteLineSpace = "\n\n";

    public Dialog(Activity activity) {
        super(activity);
        init(activity);
    }

    private void init (Activity activity) {
        LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layoutInflater.inflate(R.layout.dialog, this);
        dialogText = (TextView) findViewById(R.id.dialog_text);
        dialogButtonActionLeft = (Button) findViewById(R.id.dialog_button_action_left);
        dialogButtonActionRight = (Button) findViewById(R.id.dialog_button_action_right);
    }

    // getters
    public Button getDialogButtonActionLeft () {
        return dialogButtonActionLeft;
    }

    public Button getDialogButtonActionRight () {
        return dialogButtonActionRight;
    }

    // setters
    public void setText (String title, String body, String buttonActionLeft, String buttonActionRight) {
        dialogText.setText(title + whiteLineSpace + body);
        dialogButtonActionLeft.setText(buttonActionLeft);
        dialogButtonActionRight.setText(buttonActionRight);
    }

    public void setButtonActionLeftVisible (boolean visible) {
        dialogButtonActionLeft.setVisibility(visible ? VISIBLE : INVISIBLE);
    }

    public void setButtonActionRightVisible (boolean visible) {
        dialogButtonActionRight.setVisibility(visible ? VISIBLE : INVISIBLE);
    }

    public void setButtonActionLeftIsActive (boolean isActive) {
        dialogButtonActionLeft.setIsActive(isActive);
    }

    public void setButtonActionRightIsActive (boolean isActive) {
        dialogButtonActionRight.setIsActive(isActive);
    }
}
