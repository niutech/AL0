package computer.fuji.al0.components;

import android.content.Context;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.ContextThemeWrapper;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;

import computer.fuji.al0.R;

public class Button extends AppCompatButton {
    private boolean isActive = false;
    private boolean isDisabled = false;

    public Button(@NonNull Context context, AttributeSet attributeSet) {
        super(new ContextThemeWrapper(context, R.style.button), attributeSet, R.attr.borderlessButtonStyle);
    }

    // getters
    public boolean getIsActive () { return isActive; }
    public boolean getIsDisabled () { return isDisabled; }

    // setters
    @Override
    public void setText(CharSequence text, BufferType type) {
        // when isActive make text style to strikethrough
        // default is underline
        if (isActive) {
            setPaintFlags(getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            setPaintFlags(getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        }

        // when isDisabled set text color to colorDisabled
        // default is primaryColor

        Context context = this.getContext();
        if (this.isDisabled) {
            this.setTextColor(ContextCompat.getColor(context, R.color.colorDisabled));
            this.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.button_background_disabled));
        } else {
            this.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
            this.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.button_background));
        }

        super.setText(text, type);
    }

    public void setIsActive (boolean isActive) {
        this.isActive = isActive;
        setText(getText());
    }

    public void setIsDisabled (boolean isDisabled) {
        this.isDisabled = isDisabled;
        setEnabled(!isDisabled);
        setText(getText());
    }

    @Override
    public boolean performClick() {
        super.performClick();
        return true;
    }
}
