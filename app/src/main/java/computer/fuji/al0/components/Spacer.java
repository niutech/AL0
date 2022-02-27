package computer.fuji.al0.components;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ContextThemeWrapper;
import android.view.View;

import androidx.annotation.Nullable;

import computer.fuji.al0.R;

public class Spacer extends View {
    public Spacer(Context context, @Nullable AttributeSet attributeSet) {
        super(new ContextThemeWrapper(context, R.style.spacer), attributeSet);
    }
}
