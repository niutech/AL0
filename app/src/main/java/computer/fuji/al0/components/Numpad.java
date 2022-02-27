package computer.fuji.al0.components;

import android.content.Context;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import computer.fuji.al0.R;
import computer.fuji.al0.models.NumpadButtonModel;
import computer.fuji.al0.utils.Geometry;
import computer.fuji.al0.utils.NumpadButtonTonePlayer;

import java.util.Timer;

public class Numpad extends LinearLayout {
    final String emptyString = getResources().getString(R.string.empty_string);

    final NumpadButtonModel[] buttonModels = new NumpadButtonModel[] {
           new NumpadButtonModel(NumpadButtonModel.NumpadButtonId.BUTTON_1, R.id.numpad_button_1, getResources().getString(R.string.numpad_button_1_number), emptyString, ToneGenerator.TONE_DTMF_1),
           new NumpadButtonModel(NumpadButtonModel.NumpadButtonId.BUTTON_2, R.id.numpad_button_2, getResources().getString(R.string.numpad_button_2_number), getResources().getString(R.string.numpad_button_2_letters), ToneGenerator.TONE_DTMF_2),
           new NumpadButtonModel(NumpadButtonModel.NumpadButtonId.BUTTON_3, R.id.numpad_button_3, getResources().getString(R.string.numpad_button_3_number), getResources().getString(R.string.numpad_button_3_letters), ToneGenerator.TONE_DTMF_3),
           new NumpadButtonModel(NumpadButtonModel.NumpadButtonId.BUTTON_4, R.id.numpad_button_4, getResources().getString(R.string.numpad_button_4_number), getResources().getString(R.string.numpad_button_4_letters), ToneGenerator.TONE_DTMF_4),
           new NumpadButtonModel(NumpadButtonModel.NumpadButtonId.BUTTON_5, R.id.numpad_button_5, getResources().getString(R.string.numpad_button_5_number), getResources().getString(R.string.numpad_button_5_letters), ToneGenerator.TONE_DTMF_5),
           new NumpadButtonModel(NumpadButtonModel.NumpadButtonId.BUTTON_6, R.id.numpad_button_6, getResources().getString(R.string.numpad_button_6_number), getResources().getString(R.string.numpad_button_6_letters), ToneGenerator.TONE_DTMF_6),
           new NumpadButtonModel(NumpadButtonModel.NumpadButtonId.BUTTON_7, R.id.numpad_button_7, getResources().getString(R.string.numpad_button_7_number), getResources().getString(R.string.numpad_button_7_letters), ToneGenerator.TONE_DTMF_7),
           new NumpadButtonModel(NumpadButtonModel.NumpadButtonId.BUTTON_8, R.id.numpad_button_8, getResources().getString(R.string.numpad_button_8_number), getResources().getString(R.string.numpad_button_8_letters), ToneGenerator.TONE_DTMF_8),
           new NumpadButtonModel(NumpadButtonModel.NumpadButtonId.BUTTON_9, R.id.numpad_button_9, getResources().getString(R.string.numpad_button_9_number), getResources().getString(R.string.numpad_button_9_letters), ToneGenerator.TONE_DTMF_9),
           new NumpadButtonModel(NumpadButtonModel.NumpadButtonId.BUTTON_0, R.id.numpad_button_0, getResources().getString(R.string.numpad_button_0_number), getResources().getString(R.string.numpad_button_0_letters), ToneGenerator.TONE_DTMF_0),
           new NumpadButtonModel(NumpadButtonModel.NumpadButtonId.BUTTON_STAR, R.id.numpad_button_star, getResources().getString(R.string.numpad_button_star_number), getResources().getString(R.string.numpad_button_star_letters), ToneGenerator.TONE_DTMF_S),
           new NumpadButtonModel(NumpadButtonModel.NumpadButtonId.BUTTON_HASH, R.id.numpad_button_hash, getResources().getString(R.string.numpad_button_hash_number), getResources().getString(R.string.numpad_button_hash_letters), ToneGenerator.TONE_DTMF_P)
    };

    final NumpadButtonModel[] buttonModelsTimeMode = new NumpadButtonModel[] {
            new NumpadButtonModel(NumpadButtonModel.NumpadButtonId.BUTTON_1, R.id.numpad_button_1, getResources().getString(R.string.numpad_button_1_number), emptyString, ToneGenerator.TONE_DTMF_1),
            new NumpadButtonModel(NumpadButtonModel.NumpadButtonId.BUTTON_2, R.id.numpad_button_2, getResources().getString(R.string.numpad_button_2_number), emptyString, ToneGenerator.TONE_DTMF_2),
            new NumpadButtonModel(NumpadButtonModel.NumpadButtonId.BUTTON_3, R.id.numpad_button_3, getResources().getString(R.string.numpad_button_3_number), emptyString, ToneGenerator.TONE_DTMF_3),
            new NumpadButtonModel(NumpadButtonModel.NumpadButtonId.BUTTON_4, R.id.numpad_button_4, getResources().getString(R.string.numpad_button_4_number), emptyString, ToneGenerator.TONE_DTMF_4),
            new NumpadButtonModel(NumpadButtonModel.NumpadButtonId.BUTTON_5, R.id.numpad_button_5, getResources().getString(R.string.numpad_button_5_number), emptyString, ToneGenerator.TONE_DTMF_5),
            new NumpadButtonModel(NumpadButtonModel.NumpadButtonId.BUTTON_6, R.id.numpad_button_6, getResources().getString(R.string.numpad_button_6_number), emptyString, ToneGenerator.TONE_DTMF_6),
            new NumpadButtonModel(NumpadButtonModel.NumpadButtonId.BUTTON_7, R.id.numpad_button_7, getResources().getString(R.string.numpad_button_7_number), emptyString, ToneGenerator.TONE_DTMF_7),
            new NumpadButtonModel(NumpadButtonModel.NumpadButtonId.BUTTON_8, R.id.numpad_button_8, getResources().getString(R.string.numpad_button_8_number), emptyString, ToneGenerator.TONE_DTMF_8),
            new NumpadButtonModel(NumpadButtonModel.NumpadButtonId.BUTTON_9, R.id.numpad_button_9, getResources().getString(R.string.numpad_button_9_number), emptyString, ToneGenerator.TONE_DTMF_9),
            new NumpadButtonModel(NumpadButtonModel.NumpadButtonId.BUTTON_0, R.id.numpad_button_0, getResources().getString(R.string.numpad_button_0_number), emptyString, ToneGenerator.TONE_DTMF_0),
            new NumpadButtonModel(NumpadButtonModel.NumpadButtonId.BUTTON_AM, R.id.numpad_button_star, getResources().getString(R.string.numpad_button_AM_number), emptyString, ToneGenerator.TONE_DTMF_S),
            new NumpadButtonModel(NumpadButtonModel.NumpadButtonId.BUTTON_PM, R.id.numpad_button_hash, getResources().getString(R.string.numpad_button_PM_number), emptyString, ToneGenerator.TONE_DTMF_P)
    };

    final NumpadButtonModel[] buttonModelsNumberMode = new NumpadButtonModel[] {
            new NumpadButtonModel(NumpadButtonModel.NumpadButtonId.BUTTON_1, R.id.numpad_button_1, getResources().getString(R.string.numpad_button_1_number), emptyString, ToneGenerator.TONE_DTMF_1),
            new NumpadButtonModel(NumpadButtonModel.NumpadButtonId.BUTTON_2, R.id.numpad_button_2, getResources().getString(R.string.numpad_button_2_number), emptyString, ToneGenerator.TONE_DTMF_2),
            new NumpadButtonModel(NumpadButtonModel.NumpadButtonId.BUTTON_3, R.id.numpad_button_3, getResources().getString(R.string.numpad_button_3_number), emptyString, ToneGenerator.TONE_DTMF_3),
            new NumpadButtonModel(NumpadButtonModel.NumpadButtonId.BUTTON_4, R.id.numpad_button_4, getResources().getString(R.string.numpad_button_4_number), emptyString, ToneGenerator.TONE_DTMF_4),
            new NumpadButtonModel(NumpadButtonModel.NumpadButtonId.BUTTON_5, R.id.numpad_button_5, getResources().getString(R.string.numpad_button_5_number), emptyString, ToneGenerator.TONE_DTMF_5),
            new NumpadButtonModel(NumpadButtonModel.NumpadButtonId.BUTTON_6, R.id.numpad_button_6, getResources().getString(R.string.numpad_button_6_number), emptyString, ToneGenerator.TONE_DTMF_6),
            new NumpadButtonModel(NumpadButtonModel.NumpadButtonId.BUTTON_7, R.id.numpad_button_7, getResources().getString(R.string.numpad_button_7_number), emptyString, ToneGenerator.TONE_DTMF_7),
            new NumpadButtonModel(NumpadButtonModel.NumpadButtonId.BUTTON_8, R.id.numpad_button_8, getResources().getString(R.string.numpad_button_8_number), emptyString, ToneGenerator.TONE_DTMF_8),
            new NumpadButtonModel(NumpadButtonModel.NumpadButtonId.BUTTON_9, R.id.numpad_button_9, getResources().getString(R.string.numpad_button_9_number), emptyString, ToneGenerator.TONE_DTMF_9),
            new NumpadButtonModel(NumpadButtonModel.NumpadButtonId.BUTTON_0, R.id.numpad_button_0, getResources().getString(R.string.numpad_button_0_number), emptyString, ToneGenerator.TONE_DTMF_0),
            new NumpadButtonModel(NumpadButtonModel.NumpadButtonId.BUTTON_AM, R.id.numpad_button_star, emptyString, emptyString, ToneGenerator.TONE_DTMF_S),
            new NumpadButtonModel(NumpadButtonModel.NumpadButtonId.BUTTON_PM, R.id.numpad_button_hash, emptyString, emptyString, ToneGenerator.TONE_DTMF_P)
    };

    final NumpadButtonModel[] buttonModelsCalculatorMode = new NumpadButtonModel[] {
            // new NumpadButtonModel(NumpadButtonModel.NumpadButtonId.BUTTON_CLEAR, R.id.numpad_button_clear, getResources().getString(R.string.numpad_button_clear_number), emptyString, 0),
            // new NumpadButtonModel(NumpadButtonModel.NumpadButtonId.BUTTON_PLUS_MINUS, R.id.numpad_button_plus_minus, getResources().getString(R.string.numpad_button_plus_minus_number), emptyString, 0),
            // new NumpadButtonModel(NumpadButtonModel.NumpadButtonId.BUTTON_PERCENT, R.id.numpad_button_percent, getResources().getString(R.string.numpad_button_percent_number), emptyString, 0),
            new NumpadButtonModel(NumpadButtonModel.NumpadButtonId.BUTTON_DIVISION, R.id.numpad_button_division, getResources().getString(R.string.numpad_button_division_number), emptyString, 0),
            new NumpadButtonModel(NumpadButtonModel.NumpadButtonId.BUTTON_7, R.id.numpad_button_7, getResources().getString(R.string.numpad_button_7_number), emptyString, ToneGenerator.TONE_DTMF_7),
            new NumpadButtonModel(NumpadButtonModel.NumpadButtonId.BUTTON_8, R.id.numpad_button_8, getResources().getString(R.string.numpad_button_8_number), emptyString, ToneGenerator.TONE_DTMF_8),
            new NumpadButtonModel(NumpadButtonModel.NumpadButtonId.BUTTON_9, R.id.numpad_button_9, getResources().getString(R.string.numpad_button_9_number), emptyString, ToneGenerator.TONE_DTMF_9),
            new NumpadButtonModel(NumpadButtonModel.NumpadButtonId.BUTTON_MULTIPLICATION, R.id.numpad_button_multiplication, getResources().getString(R.string.numpad_button_multiplication_number), emptyString, 0),
            new NumpadButtonModel(NumpadButtonModel.NumpadButtonId.BUTTON_4, R.id.numpad_button_4, getResources().getString(R.string.numpad_button_4_number), emptyString, ToneGenerator.TONE_DTMF_4),
            new NumpadButtonModel(NumpadButtonModel.NumpadButtonId.BUTTON_5, R.id.numpad_button_5, getResources().getString(R.string.numpad_button_5_number), emptyString, ToneGenerator.TONE_DTMF_5),
            new NumpadButtonModel(NumpadButtonModel.NumpadButtonId.BUTTON_6, R.id.numpad_button_6, getResources().getString(R.string.numpad_button_6_number), emptyString, ToneGenerator.TONE_DTMF_6),
            new NumpadButtonModel(NumpadButtonModel.NumpadButtonId.BUTTON_MINUS, R.id.numpad_button_minus, getResources().getString(R.string.numpad_button_minus_number), emptyString, 0),
            new NumpadButtonModel(NumpadButtonModel.NumpadButtonId.BUTTON_1, R.id.numpad_button_1, getResources().getString(R.string.numpad_button_1_number), emptyString, ToneGenerator.TONE_DTMF_1),
            new NumpadButtonModel(NumpadButtonModel.NumpadButtonId.BUTTON_2, R.id.numpad_button_2, getResources().getString(R.string.numpad_button_2_number), emptyString, ToneGenerator.TONE_DTMF_2),
            new NumpadButtonModel(NumpadButtonModel.NumpadButtonId.BUTTON_3, R.id.numpad_button_3, getResources().getString(R.string.numpad_button_3_number), emptyString, ToneGenerator.TONE_DTMF_3),
            new NumpadButtonModel(NumpadButtonModel.NumpadButtonId.BUTTON_PLUS, R.id.numpad_button_plus, getResources().getString(R.string.numpad_button_plus_number), emptyString, 0),
            // new NumpadButtonModel(NumpadButtonModel.NumpadButtonId.BUTTON_DELETE, R.id.numpad_button_delete, getResources().getString(R.string.numpad_button_delete_number), emptyString, 0),
            new NumpadButtonModel(NumpadButtonModel.NumpadButtonId.BUTTON_0, R.id.numpad_button_0, getResources().getString(R.string.numpad_button_0_number), emptyString, ToneGenerator.TONE_DTMF_0),
            new NumpadButtonModel(NumpadButtonModel.NumpadButtonId.BUTTON_COMMA, R.id.numpad_button_comma, getResources().getString(R.string.numpad_button_comma_number), emptyString, 0),
            new NumpadButtonModel(NumpadButtonModel.NumpadButtonId.BUTTON_EQUAL, R.id.numpad_button_equal, getResources().getString(R.string.numpad_button_equal_number), emptyString, 0)
    };

    private NumpadEventsListener numpadEventsListener;
    private NumpadButtonModel currentPressedButton;

    // touch position
    private double touchStartX;
    private double touchStartY;
    private double touchX;
    private double touchY;
    private double touchMaximumValidDistance = 80;

    private AudioManager audioManager;
    private int ringVolumeMin = 0;
    private int ringVolumeMax;
    private int ringVolume;

    // variable used to make sure a numpad tone last at least MINIMUM_TONE_PLAYING_DURATION ms
    private Timer minimumTonePlayingDurationTimer;
    private boolean minimumTonePlayingDurationIsPassed;
    private boolean toneShouldBeStopInDelay;
    private long MINIMUM_TONE_PLAYING_DURATION = 180;


    public interface NumpadEventsListener {
        public void onButtonPress(NumpadButtonModel buttonModel);
        public void onButtonTouchStart(NumpadButtonModel buttonModel);
        public void onButtonTouchEnd(NumpadButtonModel buttonModel, boolean isTailTouchEvent);
    }

    public void setNumpadEventsListener(NumpadEventsListener numpadEventsListener) {
        this.numpadEventsListener = numpadEventsListener;
    }

    private Context context;
    private AttributeSet attributeSet;

    public Numpad(@NonNull Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.context = context;
        this.attributeSet = attributeSet;
        init(context, attributeSet, buttonModels);
        audioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
        ringVolumeMax = audioManager.getStreamMaxVolume(AudioManager.STREAM_RING);
    }

    public void setTime12HMode () {
        updateNumpadButtons(buttonModelsTimeMode);
    }

    public void setPhoneMode () {
        updateNumpadButtons(buttonModels);
    }

    public void setNumberMode () {
        updateNumpadButtons(buttonModelsNumberMode);
    }

    public void setCalculatorMode () {
        removeAllViews();
        inflate(context, R.layout.numpad_calculator, this);
        updateNumpadButtons(buttonModelsCalculatorMode);
    }

    public void playNumpadButtonDTMFTone (NumpadButtonModel numpadButtonModel) {
        ringVolume = audioManager.getStreamVolume(AudioManager.STREAM_RING);
        int toneVolume = NumpadButtonTonePlayer.volumeRangesToTonePlayerVolume(ringVolumeMin, ringVolumeMax, ringVolume);
        NumpadButtonTonePlayer.playTone(numpadButtonModel.getDtmfTone(), toneVolume);
    }

    public void stopDTMFTone () {
        NumpadButtonTonePlayer.stopTone();
    }

    private void init(Context context, AttributeSet attrs, NumpadButtonModel[] buttonModels) {
        inflate(context, R.layout.numpad, this);
        updateNumpadButtons(buttonModels);
    }

    private void updateNumpadButtons (NumpadButtonModel[] buttonModels) {
        for (final NumpadButtonModel buttonModel: buttonModels) {
            // get numpad button from layout
            final NumpadButton numpadButton = findViewById(buttonModel.getViewId());
            // set numpad button values
            numpadButton.setNumber(buttonModel.getNumber());
            numpadButton.setLetters(buttonModel.getLetters());

            numpadButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onEndTouch(buttonModel);
                }
            });

            // listen numpad button click
            numpadButton.setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    touchX = event.getX();
                    touchY = event.getY();

                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            onNumpadButtonTouchStart(buttonModel);
                            onNumpadButtonPress(buttonModel);
                            currentPressedButton = buttonModel;

                            touchStartX = touchX;
                            touchStartY = touchY;
                            return true;
                        case MotionEvent.ACTION_MOVE:
                            // check if touch moved away from initial position
                            // when touch moved more than touchMaximumDistance repetitions should stop
                            double touchMoveDistance = Geometry.getDistance(touchX, touchY, touchStartX, touchStartY);
                            if (touchMoveDistance > touchMaximumValidDistance) {
                                return onEndTouch(buttonModel);
                            }

                            break;
                        case MotionEvent.ACTION_UP:
                        case MotionEvent.ACTION_CANCEL:
                            numpadButton.performClick();
                            return onEndTouch(buttonModel);
                    }

                    return false;
                }
            });
        }
    }

    private boolean onEndTouch (NumpadButtonModel buttonModel) {
        // detect if ACTION_UP is related to the last touch event
        boolean isTailPress = false;
        if (currentPressedButton != null) {
            isTailPress = currentPressedButton.getId() == buttonModel.getId();
        }

        if (isTailPress) {
            currentPressedButton = null;
        }

        onNumpadButtonTouchEnd(buttonModel, isTailPress);
        return true;
    }

    private void onNumpadButtonPress (NumpadButtonModel numpadButton) {
        if (numpadEventsListener != null) {
            numpadEventsListener.onButtonPress(numpadButton);
        }
    }

    private void onNumpadButtonTouchStart (NumpadButtonModel numpadButton) {
        if (numpadEventsListener != null) {
            numpadEventsListener.onButtonTouchStart(numpadButton);
        }
    }

    private void onNumpadButtonTouchEnd (NumpadButtonModel numpadButton, boolean isTailTouchEvent) {
        if (numpadEventsListener != null) {
            numpadEventsListener.onButtonTouchEnd(numpadButton, isTailTouchEvent);
        }
    }
}
