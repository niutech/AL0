package computer.fuji.al0.components;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import computer.fuji.al0.R;

public class NumpadButton extends LinearLayout {
    private String number = "";
    private String letters = "";

    private TextView numberTextView;
    private TextView lettersTextView;

    public NumpadButton(@NonNull Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init(context, attributeSet);
    }

    private void init (Context context, AttributeSet attributeSet) {
        inflate(context, R.layout.numpad_button, this);
        numberTextView = (TextView) findViewById(R.id.numpad_button_number);
        lettersTextView = (TextView) findViewById(R.id.numpad_button_letters);
    }

    @Override
    public boolean performClick() {
        super.performClick();
        return true;
    }

    // getters
    public String getNumber () {
       return number;
    }

    public String getLetters () {
        return letters;
    }

    // setters
    public void setNumber (String number) {
        this.number = number;
       numberTextView.setText(number);
    }

    public void setLetters (String letters) {
        this.letters = letters;
        lettersTextView.setText(letters);
    }
}
