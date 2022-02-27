package computer.fuji.al0.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import computer.fuji.al0.R;
import computer.fuji.al0.components.Keyboard;
import computer.fuji.al0.components.TextInputMovableCursor;
import computer.fuji.al0.models.KeyboardButtonModel;

public class PhoneContactsNewNameFragment extends Fragment {
    private PhoneContactsNewNameFragmentEventListener phoneContactsNewNameFragmentEventListener;

    public interface PhoneContactsNewNameFragmentEventListener {
        public void onKeyboardRightActionButtonPress();
        public void onKeyboardCloseButtonPress();
        public void onViewReady();
        public void onNameTextInputChange(String name);
    }

    String rightActionButtonTextCancel;
    String rightActionButtonTextSave;

    private Keyboard keyboard;
    private TextInputMovableCursor nameTextInput;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_phone_contacts_new_name, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rightActionButtonTextCancel = getActivity().getString(R.string.phone_contacts_new_activity_button_cancel);
        rightActionButtonTextSave = getActivity().getString(R.string.phone_contacts_new_activity_button_save);

        keyboard = view.findViewById(R.id.phone_contacts_new_activity_keyboard);
        keyboard.setIsFullNameMode(true);
        keyboard.setCloseButtonText(getActivity().getString(R.string.close_contestual_symbol));
        nameTextInput = view.findViewById(R.id.phone_contacts_new_activity_text_input_name);

        keyboard.setKeyboardEventsListener(new Keyboard.KeyboardEventsListener() {
            @Override
            public void onButtonPress(KeyboardButtonModel buttonModel) {
                // use only for delete button
                // delete button need to listen for repeated events
                switch (buttonModel.getId()) {
                    case BUTTON_DELETE:
                        nameTextInput.deleteAtCursorPosition();
                        if (phoneContactsNewNameFragmentEventListener != null) {
                            phoneContactsNewNameFragmentEventListener.onNameTextInputChange(nameTextInput.getText().toString());
                        }
                        break;
                    case BUTTON_MOVE_CURSOR_LEFT:
                        nameTextInput.moveCursorLeft(true);
                        break;
                    case BUTTON_MOVE_CURSOR_RIGHT:
                        nameTextInput.moveCursorRight(true);
                        break;
                    default:
                        // do nothing
                        break;
                }
            }

            @Override
            public void onButtonTouchStart(KeyboardButtonModel buttonModel) {
                // do nothing
            }

            @Override
            public void onButtonTouchEnd(KeyboardButtonModel buttonModel, boolean isTailTouchEvent) {
                switch (buttonModel.getId()) {
                    case BUTTON_DELETE:
                    case BUTTON_MOVE_CURSOR_LEFT:
                    case BUTTON_MOVE_CURSOR_RIGHT:
                        // do nothing
                        break;
                    default:
                        if (isTailTouchEvent) {
                            if (phoneContactsNewNameFragmentEventListener != null) {
                                nameTextInput.addCharacter(buttonModel.getKey());
                                phoneContactsNewNameFragmentEventListener.onNameTextInputChange(nameTextInput.getText().toString());
                            }
                        }
                        break;
                }
            }

            @Override
            public void onButtonSpacePress(KeyboardButtonModel buttonModel) {
                if (phoneContactsNewNameFragmentEventListener != null) {
                    nameTextInput.addCharacter(buttonModel.getKey());
                    phoneContactsNewNameFragmentEventListener.onNameTextInputChange(nameTextInput.getText().toString());
                }
            }

            @Override
            public void onButtonRightActionPress() {
                if (phoneContactsNewNameFragmentEventListener != null) {
                    phoneContactsNewNameFragmentEventListener.onKeyboardRightActionButtonPress();
                }
            }

            @Override
            public void onButtonClosePress() {
                if (phoneContactsNewNameFragmentEventListener != null){
                    phoneContactsNewNameFragmentEventListener.onKeyboardCloseButtonPress();
                }
            }
        });

        if (phoneContactsNewNameFragmentEventListener != null) {
            phoneContactsNewNameFragmentEventListener.onViewReady();
        }
    }

    // setters
    public void setPhoneContactsNewNameFragmentEventListener (PhoneContactsNewNameFragmentEventListener phoneContactsNewNameFragmentEventListener) {
        this.phoneContactsNewNameFragmentEventListener = phoneContactsNewNameFragmentEventListener;
    }

    public void setName (String name) {
        nameTextInput.setText(name);
        keyboard.setTypedText(name);
    }

    public void setKeyboardRightActionCanSave (boolean canSave) {
        keyboard.setKeyboardRightActionButtonEnabled(canSave);
        keyboard.setRightActionButtonText(rightActionButtonTextSave);
    }
}
