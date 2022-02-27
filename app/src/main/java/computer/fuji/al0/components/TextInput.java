package computer.fuji.al0.components;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.widget.AppCompatTextView;

import computer.fuji.al0.R;

public class TextInput extends AppCompatTextView {
    private String prefixText = "";
    private String postfixText = "";

    public TextInput(Context context, @Nullable AttributeSet attributeSet) {
        super(new ContextThemeWrapper(context, R.style.text_input), attributeSet);
        this.prefixText = "";
        this.postfixText = "";
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        // always add "_"
        // super.setText(text.toString().concat("_"), type);
        if (prefixText != null && postfixText != null) {
            super.setText(this.prefixText.concat(text.toString()).concat("_").concat(this.postfixText), type);
        } else {
            super.setText(text.toString().concat("_"), type);
        }

    }

    public CharSequence getText () {
        // always remove last char, "_"
        CharSequence currentText = super.getText();
        // remove prefix
        currentText = currentText.subSequence(prefixText.length(), currentText.length());
        // remove postfix
        currentText = currentText.subSequence(0, currentText.length() - postfixText.length());
        return currentText.subSequence(0, currentText.length() - 1);
    }

    public void setPostfix (String postfix) {
        this.postfixText = postfix;
    }

    public void setPrefix (String prefix) {
        this.prefixText = prefix;
    }
}
