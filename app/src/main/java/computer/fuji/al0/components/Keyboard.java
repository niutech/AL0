package computer.fuji.al0.components;

import android.content.Context;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import computer.fuji.al0.R;
import computer.fuji.al0.models.KeyboardButtonModel;
import computer.fuji.al0.models.KeyboardModel;
import computer.fuji.al0.utils.Geometry;
import computer.fuji.al0.utils.RepeatListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import static computer.fuji.al0.models.KeyboardButtonModel.KeyboardButtonId;
import static computer.fuji.al0.models.KeyboardModel.lowercaseKeyboardButtonLineMiddleModels;

public class Keyboard extends LinearLayout {
    public static enum KeyboardType {
        LETTERS_UPPERCASE, LETTERS_LOWERCASE, NUMBERS, SYMBOLS,
        LETTERS_UPPERCASE_DIACRITICS_A, LETTERS_UPPERCASE_DIACRITICS_E, LETTERS_UPPERCASE_DIACRITICS_I, LETTERS_UPPERCASE_DIACRITICS_O, LETTERS_UPPERCASE_DIACRITICS_U, LETTERS_UPPERCASE_DIACRITICS_S,  LETTERS_UPPERCASE_DIACRITICS_C, LETTERS_UPPERCASE_DIACRITICS_N,
        LETTERS_LOWERCASE_DIACRITICS_A, LETTERS_LOWERCASE_DIACRITICS_E, LETTERS_LOWERCASE_DIACRITICS_I, LETTERS_LOWERCASE_DIACRITICS_O, LETTERS_LOWERCASE_DIACRITICS_U, LETTERS_LOWERCASE_DIACRITICS_S, LETTERS_LOWERCASE_DIACRITICS_C, LETTERS_LOWERCASE_DIACRITICS_N
    }
    private KeyboardType currentKeyboardType = KeyboardType.LETTERS_UPPERCASE;
    private KeyboardType previousKeyboardType = null;
    private KeyboardButtonModel lastTypedKeyboardButton;
    private boolean isFullNameMode = false;
    private boolean isDiacriticsMode = false;

    private LinearLayout lineControls;
    private LinearLayout lineUpper;
    private LinearLayout lineMiddle;
    private LinearLayout lineLower;

    private ArrayList<KeyboardButton> lineControlsKeyboardButtons = new ArrayList<>();
    private ArrayList<KeyboardButton> lineUpperKeyboardButtons = new ArrayList<>();
    private ArrayList<KeyboardButton> lineMiddleKeyboardButtons = new ArrayList<>();
    private ArrayList<KeyboardButton> lineLowerKeyboardButtons = new ArrayList<>();

    private int lineControlsKeyboardButtonsNumber = 10;
    private int lineUpperKeyboardButtonsNumber = 10;
    private int lineMiddleKeyboardButtonsNumber = 9;
    private int lineLowerKeyboardButtonsNumber = 10;

    private KeyboardEventsListener keyboardEventsListener;
    private KeyboardButtonModel currentPressedButton;

    // touch position
    private double touchStartX;
    private double touchStartY;
    private double touchX;
    private double touchY;
    private double touchMaximumValidDistance = 80;

    // footer buttons
    Button closeButton;
    Button leftActionButton;
    Button centerActionButton;
    Button rightActionButton;

    public interface KeyboardEventsListener {
        public void onButtonPress(KeyboardButtonModel buttonModel);
        public void onButtonTouchStart(KeyboardButtonModel buttonModel);
        public void onButtonTouchEnd(KeyboardButtonModel buttonModel, boolean isTailTouchEvent);
        public void onButtonSpacePress(KeyboardButtonModel buttonModel);
        public void onButtonRightActionPress();
        public void onButtonClosePress();
    }

    public void setKeyboardEventsListener(KeyboardEventsListener keyboardEventsListener) {
        this.keyboardEventsListener = keyboardEventsListener;
    }

    public Keyboard(@NonNull Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init(context, attributeSet);
    }

    private void init(Context context, AttributeSet attributeSet) {
        inflate(context, R.layout.keyboard, this);
        // set footer buttons
        closeButton = (Button) findViewById(R.id.keyboard_close_button);
        leftActionButton = (Button) findViewById(R.id.keyboard_left_button);
        centerActionButton = (Button) findViewById(R.id.keyboard_center_button);
        rightActionButton = (Button) findViewById(R.id.keyboard_right_button);

        // set buttons lines views
        lineControls = (LinearLayout) findViewById(R.id.keyboard_line_controls);
        lineUpper = (LinearLayout) findViewById(R.id.keyboard_line_upper);
        lineMiddle = (LinearLayout) findViewById(R.id.keyboard_line_middle);
        lineLower = (LinearLayout) findViewById(R.id.keyboard_line_lower);

        // populate keyboard buttons
        populateLine(lineControls, lineControlsKeyboardButtons, lineControlsKeyboardButtonsNumber);
        populateLine(lineUpper, lineUpperKeyboardButtons, lineUpperKeyboardButtonsNumber);
        populateLine(lineMiddle, lineMiddleKeyboardButtons, lineMiddleKeyboardButtonsNumber);
        populateLine(lineLower, lineLowerKeyboardButtons, lineLowerKeyboardButtonsNumber);

        // add actions event listener
        leftActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLeftActionButtonClick();
            }
        });

        centerActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCenterActionButtonClick();
            }
        });

        rightActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRightActionButtonClick();
            }
        });

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCloseButtonClick();
            }
        });

        setKeyboardLettersUppercase();
    }

    // populate keyboard lines with buttons
    private void populateLine (LinearLayout lineView, ArrayList<KeyboardButton> keyboardButtons, int buttonsNumber) {
        int buttonWidth = calculateButtonWidth();

        for (int i = 0; i < buttonsNumber; i++) {
            final KeyboardButton keyboardButton = new KeyboardButton(getContext(), null);
            keyboardButtons.add(keyboardButton);
            lineView.addView(keyboardButton);
            // set button width in order to make the keyboard large as the screen width
            keyboardButton.getLayoutParams().width = buttonWidth;

            /*
            keyboardButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onKeyboardButtonPress(keyboardButton.getButtonModel());
                }
            });

             */

            // add a repeater listener to
            // the delete button, last button on the 3rd line
            // the move left button, penultimate button on controls line
            // the move right bytton, last button on controls line
            boolean isLastButton = i == buttonsNumber - 1;
            boolean isPenultimateButton = i == buttonsNumber -2;
            boolean isControlsLineView = lineView == lineControls;
            boolean isLastLineView = lineView == lineLower;
            boolean isDeleteButton = isLastButton && isLastLineView;
            boolean isMoveLeftButton = isControlsLineView && isPenultimateButton;
            boolean isMoveRightButton = isControlsLineView && isLastButton;

            if (isDeleteButton || isMoveLeftButton || isMoveRightButton) {
                keyboardButton.setOnTouchListener(new RepeatListener(400, 80, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (keyboardButton.getIsEnabled()) {
                            onKeyboardButtonPress(keyboardButton.getButtonModel());
                        } else {
                            // button disabled
                        }
                    }
                }));
            } else {
                // is a normal keyboard button button
                keyboardButton.setOnTouchListener(new OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        touchX = event.getX();
                        touchY = event.getY();

                        KeyboardButtonModel buttonModel = keyboardButton.getButtonModel();
                        // check if is a valid button
                        if (buttonModel != null && buttonModel.getId() != KeyboardButtonModel.KeyboardButtonId.BUTTON_NOTHING) {
                            // disable touch when disabled
                            if (!keyboardButton.getIsEnabled()) {
                                return false;
                            }

                            switch (event.getAction()) {
                                case MotionEvent.ACTION_DOWN:
                                    boolean shouldLowercaseCurrentPressedButton = false;
                                    KeyboardType initialKeyboardType = currentKeyboardType;
                                    if (currentPressedButton != null) {
                                        onEndTouch(currentPressedButton);
                                        if (initialKeyboardType != currentKeyboardType && currentKeyboardType == KeyboardType.LETTERS_LOWERCASE) {
                                            shouldLowercaseCurrentPressedButton = true;
                                        }
                                    }

                                    onKeyboardButtonTouchStart(buttonModel);
                                    if (shouldLowercaseCurrentPressedButton) {
                                        currentPressedButton = computer.fuji.al0.utils.Keyboard.keyboardButtonModelToLowerCase(buttonModel);
                                    } else {
                                        currentPressedButton = buttonModel;
                                    }

                                    keyboardButton.playSoundEffect(SoundEffectConstants.CLICK);

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
                                    keyboardButton.performClick();
                                    return onEndTouch(buttonModel);
                            }

                            return false;
                        }

                        return false;
                    }
                });
            }
        }
    }

    // Setters
    public void setRightActionButtonText (String text) {
        rightActionButton.setText(text);
    }

    public void setCloseButtonText (String text) {
        closeButton.setText(text);
    }

    // enable / disable keyboard's buttons
    public void setKeyboardEnabled (boolean keyboardEnabled) {
        // set footer buttons
        leftActionButton.setIsDisabled(!keyboardEnabled);
        centerActionButton.setIsDisabled(!keyboardEnabled);
        rightActionButton.setIsDisabled(!keyboardEnabled);
        // set keys buttons
        setKeyboardLineEnabled(lineControlsKeyboardButtons, keyboardEnabled);
        setKeyboardLineEnabled(lineUpperKeyboardButtons, keyboardEnabled);
        setKeyboardLineEnabled(lineMiddleKeyboardButtons, keyboardEnabled);
        setKeyboardLineEnabled(lineLowerKeyboardButtons, keyboardEnabled);

    }

    // enable / disable keyboard's buttons
    public void setKeyboardRightActionButtonEnabled (boolean keyboardEnabled) {
        rightActionButton.setIsDisabled(!keyboardEnabled);
    }

    // enable disable a keyboard line's button
    private void setKeyboardLineEnabled (ArrayList<KeyboardButton> line, boolean keyboardLineEnabled) {
        for (KeyboardButton button : line) {
            button.setEnabled(keyboardLineEnabled);
        }
    }

    // Getters
    public KeyboardType getCurrentKeyboardType () {
        return currentKeyboardType;
    }

    // Events
    // footer buttons
    private void onLeftActionButtonClick () {
        switch (currentKeyboardType) {
            case SYMBOLS:
            case NUMBERS:
                setKeyboardType(KeyboardType.LETTERS_UPPERCASE);
                // check if should set keyboard lowercase
                if (checkIfShouldSetKeyboardLettersLowercase(null)) {
                    setKeyboardLettersLowercase();
                }
                break;
            case LETTERS_LOWERCASE:
            case LETTERS_UPPERCASE:
                setKeyboardType(KeyboardType.NUMBERS);
                break;
        }
    }

    private void onCenterActionButtonClick() {
        if (keyboardEventsListener != null) {
            keyboardEventsListener.onButtonSpacePress(KeyboardModel.buttonSpace);
            if (isFullNameMode && currentKeyboardType == KeyboardType.LETTERS_LOWERCASE) {
                setKeyboardType(KeyboardType.LETTERS_UPPERCASE);
            }
        }
    }

    private void onRightActionButtonClick() {
        if (keyboardEventsListener != null) {
            keyboardEventsListener.onButtonRightActionPress();
        }
    }

    private void onCloseButtonClick () {
        if (keyboardEventsListener != null) {
            keyboardEventsListener.onButtonClosePress();
        }
    }

    // Keyboard buttons
    private boolean onEndTouch (KeyboardButtonModel buttonModel) {
        // detect if ACTION_UP is related to the last touch event
        boolean isTailPress = false;
        if (currentPressedButton != null) {
            isTailPress = currentPressedButton.getId() == buttonModel.getId();
        }

        if (isTailPress) {
            currentPressedButton = null;
        }

        onKeyboardButtonTouchEnd(buttonModel, isTailPress);

        return true;
    }

    // check if passed KeyboardButton should be followed by showing the uppercase keyboard
    // uppercase keyboard is requested after pressing Uppercase button or
    // [.] [!] [?] buttons
    private boolean checkKeyboardButtonShouldBeFollowedByShowingUppercaseKeyboard (KeyboardButtonModel keyboardButton) {
        KeyboardButtonId buttonId = keyboardButton.getId();
        switch (buttonId) {
            case BUTTON_UPPERCASE:
            case BUTTON_DOT:
            case BUTTON_EXCLAMATION_MARK:
            case BUTTON_QUESTION_MARK:
                return true;
            default:
                return false;
        }
    }

    // check if keyboard should be changed to lowercase
    private boolean checkIfShouldSetKeyboardLettersLowercase (KeyboardButtonId buttonId) {
        // when press DIACRITIC button should not change keyboard to lowercase
        if (buttonId != null && buttonId == KeyboardButtonId.BUTTON_DIACRITIC) {
            return false;
        }

        if (lastTypedKeyboardButton != null) {
            // check if current keyboard is UPPERCASE keyboard
            if (currentKeyboardType == KeyboardType.LETTERS_UPPERCASE) {
                // check if should show the uppercase keyboard
                if (checkKeyboardButtonShouldBeFollowedByShowingUppercaseKeyboard(lastTypedKeyboardButton)) {
                    return false;
                } else if (lastTypedKeyboardButton.getId() == KeyboardButtonId.BUTTON_DELETE) {
                    // ignore also delete button press
                    return false;
                } else {
                    return true;
                }
            } else {
                // current keyboard is NOT the UPPERCASE Keyboard
                return false;
            }
        } else {
            // no button pressed before, it must be a just opened keyboard
            return false;
        }
    }

    private void onKeyboardButtonPress (KeyboardButtonModel buttonModel) {
        // if is an action button keyboardEventListener should ignore the pressed key
        if (isActionButton(buttonModel.getId()) && !isSpecialActionButton(buttonModel.getId())) {
            onActionButtonPress(buttonModel.getId());
        } else if (keyboardEventsListener != null) {
            // check if is special action key
            if (isSpecialActionButton(buttonModel.getId())) {
                KeyboardButtonModel specialActionButtonModel = buttonModelToSpecialActionButtonModel(buttonModel.getId());
                if (specialActionButtonModel != null) {
                    keyboardEventsListener.onButtonPress(specialActionButtonModel);
                }
            } else {
                keyboardEventsListener.onButtonPress(buttonModel);
            }

            // make sure to leave diacritics mode after button press
            if (isDiacriticsMode &&  previousKeyboardType != null) {
                isDiacriticsMode = false;
                setKeyboardType(previousKeyboardType);
            }

        }

        // automate keyboard changes
        // after typing Uppercase set always lowercase
        lastTypedKeyboardButton = buttonModel;

        // check if should set keyboard lowercase
        if (checkIfShouldSetKeyboardLettersLowercase(buttonModel.getId())) {
            // setKeyboardLettersLowercase();
            setKeyboardType(KeyboardType.LETTERS_LOWERCASE);
        }
    }

    private void onKeyboardButtonTouchStart (KeyboardButtonModel buttonModel) {
        // if is an action button keyboardEventListener should ignore the touched key
        if (isActionButton(buttonModel.getId()) || isSpecialActionButton(buttonModel.getId())) {
            // do nothing
        } else if (keyboardEventsListener != null) {
            keyboardEventsListener.onButtonTouchStart(buttonModel);
        }
    }

    private void onKeyboardButtonTouchEnd (KeyboardButtonModel buttonModel, boolean isTailTouchEvent) {
        // if is an action button keyboardEventListener should ignore the touched key
        if (isActionButton(buttonModel.getId()) || isSpecialActionButton(buttonModel.getId())) {
            // onActionButtonPress(buttonModel.getId());
        } else if (keyboardEventsListener != null) {
            keyboardEventsListener.onButtonTouchEnd(buttonModel, isTailTouchEvent);
        }

        onKeyboardButtonPress(buttonModel);
    }

    private boolean buttonHasDiacritics (KeyboardButtonModel.KeyboardButtonId buttonId) {
        switch (buttonId) {
            case BUTTON_A: case BUTTON_a:
            case BUTTON_E: case BUTTON_e:
            case BUTTON_I: case BUTTON_i:
            case BUTTON_O: case BUTTON_o:
            case BUTTON_U: case BUTTON_u:
            case BUTTON_S: case BUTTON_s:
            case BUTTON_C: case BUTTON_c:
            case BUTTON_N: case BUTTON_n:
                return true;
            default:
                return false;
        }
    }

    private boolean isSpecialActionButton (KeyboardButtonModel.KeyboardButtonId buttonId) {
        if (isDiacriticsMode) {
            switch (buttonId) {
                case BUTTON_V: case BUTTON_v:
                case BUTTON_DELETE:
                case BUTTON_MOVE_CURSOR_LEFT:
                case BUTTON_MOVE_CURSOR_RIGHT:
                    return true;
                default:
                    return false;
            }
        } else {
            return false;
        }
    }

    private KeyboardButtonModel buttonModelToSpecialActionButtonModel (KeyboardButtonModel.KeyboardButtonId buttonId) {
        switch (buttonId) {
            case BUTTON_V: case BUTTON_v:
                return new KeyboardButtonModel(KeyboardButtonId.BUTTON_PASTE, "");
            case BUTTON_DELETE:
                return new KeyboardButtonModel(KeyboardButtonId.BUTTON_DELETE_WORD, "");
            case BUTTON_MOVE_CURSOR_LEFT:
                return new KeyboardButtonModel(KeyboardButtonId.BUTTON_MOVE_CURSOR_LEFT_WORD, "");
            case BUTTON_MOVE_CURSOR_RIGHT:
                return new KeyboardButtonModel(KeyboardButtonId.BUTTON_MOVE_CURSOR_RIGHT_WORD, "");
            default:
                return null;
        }
    }

    // check if is an action button
    // action buttons are Uppercase, Lowercase, Symbols buttons
    private boolean isActionButton (KeyboardButtonModel.KeyboardButtonId buttonId) {
        switch (buttonId) {
            case BUTTON_UPPERCASE:
            case BUTTON_LOWERCASE:
            case BUTTON_NUMBER:
            case BUTTON_SYMBOLS:
            case BUTTON_DIACRITIC:
                return true;
            default:
                // when is in diacricts mode check if the pressed button is related to a letter with diacritics
                // when the letter has diacritics, eg A E I O U S C is an action button
                if (isDiacriticsMode && buttonHasDiacritics(buttonId)) {
                    return true;
                } else {
                    return false;
                }

        }
    }

    // do keybaard actions related to action button press
    private void onActionButtonPress (KeyboardButtonModel.KeyboardButtonId buttonId) {
        switch (buttonId) {
            case BUTTON_UPPERCASE:
                setKeyboardType(KeyboardType.LETTERS_UPPERCASE);
                break;
            case BUTTON_LOWERCASE:
                setKeyboardType(KeyboardType.LETTERS_LOWERCASE);
                break;
            case BUTTON_NUMBER:
                setKeyboardType(KeyboardType.NUMBERS);
                break;
            case BUTTON_SYMBOLS:
                setKeyboardType(KeyboardType.SYMBOLS);
                break;
            case BUTTON_DIACRITIC:
                if (isDiacriticsMode && previousKeyboardType != null) {
                    isDiacriticsMode = false;
                    setKeyboardType(previousKeyboardType);
                } else {
                    previousKeyboardType = currentKeyboardType;
                    isDiacriticsMode = true;
                    setLineButtonKeyboardModels(lineControlsKeyboardButtons, KeyboardModel.controlsKeyboardDiacriticsActiveButtonLineModels);
                }
                break;
            // Diacritic mode
            case BUTTON_A:
                setKeyboardType(KeyboardType.LETTERS_UPPERCASE_DIACRITICS_A);
                break;
            case BUTTON_a:
                setKeyboardType(KeyboardType.LETTERS_LOWERCASE_DIACRITICS_A);
                break;
            case BUTTON_E:
                setKeyboardType(KeyboardType.LETTERS_UPPERCASE_DIACRITICS_E);
                break;
            case BUTTON_e:
                setKeyboardType(KeyboardType.LETTERS_LOWERCASE_DIACRITICS_E);
                break;
            case BUTTON_I:
                setKeyboardType(KeyboardType.LETTERS_UPPERCASE_DIACRITICS_I);
                break;
            case BUTTON_i:
                setKeyboardType(KeyboardType.LETTERS_LOWERCASE_DIACRITICS_I);
                break;
            case BUTTON_O:
                setKeyboardType(KeyboardType.LETTERS_UPPERCASE_DIACRITICS_O);
                break;
            case BUTTON_o:
                setKeyboardType(KeyboardType.LETTERS_LOWERCASE_DIACRITICS_O);
                break;
            case BUTTON_U:
                setKeyboardType(KeyboardType.LETTERS_UPPERCASE_DIACRITICS_U);
                break;
            case BUTTON_u:
                setKeyboardType(KeyboardType.LETTERS_LOWERCASE_DIACRITICS_U);
                break;
            case BUTTON_S:
                setKeyboardType(KeyboardType.LETTERS_UPPERCASE_DIACRITICS_S);
                break;
            case BUTTON_s:
                setKeyboardType(KeyboardType.LETTERS_LOWERCASE_DIACRITICS_S);
                break;
            case BUTTON_C:
                setKeyboardType(KeyboardType.LETTERS_UPPERCASE_DIACRITICS_C);
                break;
            case BUTTON_c:
                setKeyboardType(KeyboardType.LETTERS_LOWERCASE_DIACRITICS_C);
                break;
            case BUTTON_N:
                setKeyboardType(KeyboardType.LETTERS_UPPERCASE_DIACRITICS_N);
                break;
            case BUTTON_n:
                setKeyboardType(KeyboardType.LETTERS_LOWERCASE_DIACRITICS_N);
                break;
        }
    }

    // Setters
    private void setLineButtonKeyboardModels (ArrayList<KeyboardButton> lineKeyboardButtons, KeyboardButtonModel[] keyboardButtonModels) {
        if (lineKeyboardButtons.size() <= keyboardButtonModels.length) {
            for (int i = 0; i < lineKeyboardButtons.size(); i++) {
                lineKeyboardButtons.get(i).setModel(keyboardButtonModels[i]);
            }
        } else {
            // keyboardButtonsModels are less than lineKeyboardButtons
        }
    }

    // set keyboard in uppercase mode
    private void setKeyboardLettersUppercase () {
        setLineButtonKeyboardModels(lineControlsKeyboardButtons, KeyboardModel.controlsKeyboardButtonLineModels);
        setLineButtonKeyboardModels(lineUpperKeyboardButtons, KeyboardModel.uppercaseKeyboardButtonLineUpperModels);
        setLineButtonKeyboardModels(lineMiddleKeyboardButtons, KeyboardModel.uppercaseKeyboardButtonLineMiddleModels);
        setLineButtonKeyboardModels(lineLowerKeyboardButtons, KeyboardModel.uppercaseKeyboardButtonLineLowerModels);
    }

    // set keyboard in lowercase mode
    private void setKeyboardLettersLowercase () {
        setLineButtonKeyboardModels(lineControlsKeyboardButtons, KeyboardModel.controlsKeyboardButtonLineModels);
        setLineButtonKeyboardModels(lineUpperKeyboardButtons, KeyboardModel.lowercaseKeyboardButtonLineUpperModels);
        setLineButtonKeyboardModels(lineMiddleKeyboardButtons, lowercaseKeyboardButtonLineMiddleModels);
        setLineButtonKeyboardModels(lineLowerKeyboardButtons, KeyboardModel.lowercaseKeyboardButtonLineLowerModels);
    }

    // set keyboard in number mode
    private void setKeyboardNumbers () {
        setLineButtonKeyboardModels(lineControlsKeyboardButtons, KeyboardModel.controlsKeyboardButtonLineModels);
        setLineButtonKeyboardModels(lineUpperKeyboardButtons, KeyboardModel.numbersKeyboardButtonLineUpperModels);
        setLineButtonKeyboardModels(lineMiddleKeyboardButtons, KeyboardModel.numbersKeyboardButtonLineMiddleModels);
        setLineButtonKeyboardModels(lineLowerKeyboardButtons, KeyboardModel.numbersKeyboardButtonLineLowerModels);
    }

    // set keyboard in symbols mode
    private void setKeyboardSymbols () {
        setLineButtonKeyboardModels(lineControlsKeyboardButtons, KeyboardModel.controlsKeyboardButtonLineModels);
        setLineButtonKeyboardModels(lineUpperKeyboardButtons, KeyboardModel.symbolsKeyboardButtonLineUpperModels);
        setLineButtonKeyboardModels(lineMiddleKeyboardButtons, KeyboardModel.symbolsKeyboardButtonLineMiddleModels);
        setLineButtonKeyboardModels(lineLowerKeyboardButtons, KeyboardModel.symbolsKeyboardButtonLineLowerModels);
    }

    private void setKeyboardDiacritics (KeyboardType keyboardType) {
        setLineButtonKeyboardModels(lineControlsKeyboardButtons, KeyboardModel.controlsKeyboardDiacriticsActiveButtonLineModels);
        setLineButtonKeyboardModels(lineMiddleKeyboardButtons, KeyboardModel.emptyKeyboardButtonLineModels);
        setLineButtonKeyboardModels(lineLowerKeyboardButtons, KeyboardModel.emptyKeyboardButtonLowerLineModels);
        switch (keyboardType) {
            case LETTERS_UPPERCASE_DIACRITICS_A:
                setLineButtonKeyboardModels(lineUpperKeyboardButtons, KeyboardModel.uppercaseADiacriticsKeyboardButtonLineModels);
                break;
            case LETTERS_LOWERCASE_DIACRITICS_A:
                setLineButtonKeyboardModels(lineUpperKeyboardButtons, KeyboardModel.lowercaseADiacriticsKeyboardButtonLineModels);
                break;
            case LETTERS_UPPERCASE_DIACRITICS_E:
                setLineButtonKeyboardModels(lineUpperKeyboardButtons, KeyboardModel.uppercaseEDiacriticsKeyboardButtonLineModels);
                break;
            case LETTERS_LOWERCASE_DIACRITICS_E:
                setLineButtonKeyboardModels(lineUpperKeyboardButtons, KeyboardModel.lowercaseEDiacriticsKeyboardButtonLineModels);
                break;
            case LETTERS_UPPERCASE_DIACRITICS_I:
                setLineButtonKeyboardModels(lineUpperKeyboardButtons, KeyboardModel.uppercaseIDiacriticsKeyboardButtonLineModels);
                break;
            case LETTERS_LOWERCASE_DIACRITICS_I:
                setLineButtonKeyboardModels(lineUpperKeyboardButtons, KeyboardModel.lowercaseIDiacriticsKeyboardButtonLineModels);
                break;
            case LETTERS_UPPERCASE_DIACRITICS_O:
                setLineButtonKeyboardModels(lineUpperKeyboardButtons, KeyboardModel.uppercaseODiacriticsKeyboardButtonLineModels);
                break;
            case LETTERS_LOWERCASE_DIACRITICS_O:
                setLineButtonKeyboardModels(lineUpperKeyboardButtons, KeyboardModel.lowercaseODiacriticsKeyboardButtonLineModels);
                break;
            case LETTERS_UPPERCASE_DIACRITICS_U:
                setLineButtonKeyboardModels(lineUpperKeyboardButtons, KeyboardModel.uppercaseUDiacriticsKeyboardButtonLineModels);
                break;
            case LETTERS_LOWERCASE_DIACRITICS_U:
                setLineButtonKeyboardModels(lineUpperKeyboardButtons, KeyboardModel.lowercaseUDiacriticsKeyboardButtonLineModels);
                break;
            case LETTERS_UPPERCASE_DIACRITICS_S:
                setLineButtonKeyboardModels(lineUpperKeyboardButtons, KeyboardModel.uppercaseSDiacriticsKeyboardButtonLineModels);
                break;
            case LETTERS_LOWERCASE_DIACRITICS_S:
                setLineButtonKeyboardModels(lineUpperKeyboardButtons, KeyboardModel.lowercaseSDiacriticsKeyboardButtonLineModels);
                break;
            case LETTERS_UPPERCASE_DIACRITICS_C:
                setLineButtonKeyboardModels(lineUpperKeyboardButtons, KeyboardModel.uppercaseCDiacriticsKeyboardButtonLineModels);
                break;
            case LETTERS_LOWERCASE_DIACRITICS_C:
                setLineButtonKeyboardModels(lineUpperKeyboardButtons, KeyboardModel.lowercaseCDiacriticsKeyboardButtonLineModels);
                break;
            case LETTERS_UPPERCASE_DIACRITICS_N:
                setLineButtonKeyboardModels(lineUpperKeyboardButtons, KeyboardModel.uppercaseNDiacriticsKeyboardButtonLineModels);
                break;
            case LETTERS_LOWERCASE_DIACRITICS_N:
                setLineButtonKeyboardModels(lineUpperKeyboardButtons, KeyboardModel.lowercaseNDiacriticsKeyboardButtonLineModels);
                break;
        }
    }

    public void setKeyboardType (KeyboardType keyboardType) {
        currentKeyboardType = keyboardType;

        switch (keyboardType) {
            case LETTERS_UPPERCASE:
                leftActionButton.setText(getContext().getString(R.string.keyboard_button_left_numbers));
                setKeyboardLettersUppercase();
                break;
            case LETTERS_LOWERCASE:
                leftActionButton.setText(getContext().getString(R.string.keyboard_button_left_numbers));
                setKeyboardLettersLowercase();
                break;
            case NUMBERS:
                leftActionButton.setText(getContext().getString(R.string.keyboard_button_left_letters));
                setKeyboardNumbers();
                break;
            case SYMBOLS:
                leftActionButton.setText(getContext().getString(R.string.keyboard_button_left_letters));
                setKeyboardSymbols();
                break;
            // diacritics
            case LETTERS_UPPERCASE_DIACRITICS_A:
            case LETTERS_LOWERCASE_DIACRITICS_A:
            case LETTERS_UPPERCASE_DIACRITICS_E:
            case LETTERS_LOWERCASE_DIACRITICS_E:
            case LETTERS_UPPERCASE_DIACRITICS_I:
            case LETTERS_LOWERCASE_DIACRITICS_I:
            case LETTERS_UPPERCASE_DIACRITICS_O:
            case LETTERS_LOWERCASE_DIACRITICS_O:
            case LETTERS_UPPERCASE_DIACRITICS_U:
            case LETTERS_LOWERCASE_DIACRITICS_U:
            case LETTERS_UPPERCASE_DIACRITICS_S:
            case LETTERS_LOWERCASE_DIACRITICS_S:
            case LETTERS_UPPERCASE_DIACRITICS_C:
            case LETTERS_LOWERCASE_DIACRITICS_C:
            case LETTERS_UPPERCASE_DIACRITICS_N:
            case LETTERS_LOWERCASE_DIACRITICS_N:
                setKeyboardDiacritics(keyboardType);
                break;
        }
    }

    // Utils
    // find the row with the greater number of buttons
    private int getGreaterLineKeyboardButtonsNumber () {
        Integer [] lineKeyboardButtonsNumbers = new Integer[] {
                lineUpperKeyboardButtonsNumber, lineMiddleKeyboardButtonsNumber, lineLowerKeyboardButtonsNumber };

        Arrays.sort(lineKeyboardButtonsNumbers, Collections.reverseOrder());
        return lineKeyboardButtonsNumbers[0];

    }

    // calculate button size width
    // the screen's width divided by the greater number of buttons in a row
    private int calculateButtonWidth () {
        WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;

        return size.x /  getGreaterLineKeyboardButtonsNumber();
    }

    // set what user typed with keybaord
    // use this method to understand when user delete all text to reset to show keyboard in UPPERCASE
    public void setTypedText (String text) {
        if (text.length() == 0 && lastTypedKeyboardButton != null) {
            if  (lastTypedKeyboardButton.getId() == KeyboardButtonId.BUTTON_DELETE) {
                // text is empty and last pressed key is [DELETE]
                setKeyboardType(KeyboardType.LETTERS_UPPERCASE);
            }
        }
    }

    // Fullname Mode, change to Uppercase also after a white space
    public void setIsFullNameMode (boolean isFullNameMode) {
        this.isFullNameMode = isFullNameMode;
    }
}
