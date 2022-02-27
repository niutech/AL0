package computer.fuji.al0.components;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import computer.fuji.al0.R;
import computer.fuji.al0.models.KeyboardButtonModel;

public class KeyboardButton extends LinearLayout {
    private String key;
    TextView buttonText;
    KeyboardButtonModel buttonModel;
    private boolean isEnabled = true;

    public KeyboardButton(@NonNull Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init(context, attributeSet);
    }

    private void init (Context context, AttributeSet attributeSet) {
        inflate(context, R.layout.keyboard_button, this);
        buttonText = (TextView) findViewById(R.id.keyboard_button_letter);
    }

    // setters
    public void setModel (KeyboardButtonModel buttonModel) {
        this.buttonModel = buttonModel;
        buttonText.setText(buttonModel.getKey());
    }

    public void setEnabled (boolean isEnabled) {
        this.isEnabled = isEnabled;
        if (isEnabled) {
            buttonText.setTextColor(ContextCompat.getColor(getContext(), R.color.color_1));
        } else {
            buttonText.setTextColor(ContextCompat.getColor(getContext(), R.color.color_4));
        }
    }

    // getters
    public KeyboardButtonModel getButtonModel() {
        return buttonModel;
    }

    public boolean getIsEnabled () {
        return this.isEnabled;
    }

    @Override
    public boolean performClick() {
        super.performClick();
        return true;
    }
}
