package computer.fuji.al0.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import computer.fuji.al0.R;
import computer.fuji.al0.components.Button;
import computer.fuji.al0.components.Numpad;
import computer.fuji.al0.controllers.CalculatorActivityController;
import computer.fuji.al0.models.NumpadButtonModel;
import computer.fuji.al0.utils.LockScreen;
import computer.fuji.al0.utils.UI;

public class CalculatorActivity extends AppCompatActivity {
    private CalculatorActivityController controller;

    private Button buttonClear;
    private Button buttonClose;
    private Button buttonDelete;
    private TextView textView;
    private Numpad numpad;
    private final String zeroString = "0";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // show activity on lock screen
        LockScreen.addShowOnLockScreenFlags(this);

        setContentView(R.layout.activity_calculator);
        UI.hideNavigationBar(this);

        buttonClose = (Button) findViewById(R.id.calculator_activity_button_close);
        buttonClear = (Button) findViewById(R.id.calculator_activity_button_clear);
        buttonDelete = (Button) findViewById(R.id.calculator_activity_button_delete);

        textView = (TextView) findViewById(R.id.calculator_activity_text_view);
        numpad = (Numpad) findViewById(R.id.calculator_activity_numpad);
        numpad.setCalculatorMode();

        controller = new CalculatorActivityController(this);

        buttonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                controller.onButtonClosePress();
            }
        });

        buttonClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                controller.onButtonClearPress();
            }
        });

        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                controller.onButtonDeletePress();
            }
        });

        numpad.setNumpadEventsListener(new Numpad.NumpadEventsListener() {
            @Override
            public void onButtonPress(NumpadButtonModel buttonModel) {
                controller.onNumpadButtonPress(buttonModel);
            }

            @Override
            public void onButtonTouchStart(NumpadButtonModel buttonModel) {
                controller.onNumpadButtonTouchStart(buttonModel);
            }

            @Override
            public void onButtonTouchEnd(NumpadButtonModel buttonModel, boolean isTailTouchEvent) {
                controller.onNumpadButtonTouchEnd(buttonModel, isTailTouchEvent);
            }
        });
    }

    public void updateTextView (String value) {
        textView.setText(value);
        if (value.equals(zeroString)) {
            buttonDelete.setIsDisabled(true);
        } else {
            buttonDelete.setIsDisabled(false);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            UI.hideNavigationBar(this);
        }
    }
}
