package computer.fuji.al0.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import computer.fuji.al0.R;
import computer.fuji.al0.components.Button;
import computer.fuji.al0.components.Numpad;
import computer.fuji.al0.components.TextInput;
import computer.fuji.al0.models.NumpadButtonModel;
import computer.fuji.al0.utils.RepeatListener;

public class PhoneContactsNewNumberFragment extends Fragment {
    private TextInput textInput;
    private Numpad numpad;
    private Button closeButton;
    private Button deleteButton;
    private Button enterNameButton;
    private  PhoneContactsNewNumberFragmentEventsListener phoneContactsNewNumberFragmentEventsListener;

    public interface PhoneContactsNewNumberFragmentEventsListener {
        public void onNumpadButtonPress(NumpadButtonModel numpadButtonModel);
        public void onDeleteButtonPress();
        public void onEnterNameButtonPress();
        public void onCancelButtonPress();
        public void onViewReady();
    }

    public void setPhoneContactsNewNumberFragmentEventsListener (PhoneContactsNewNumberFragmentEventsListener phoneContactsNewNumberFragmentEventsListener) {
        this.phoneContactsNewNumberFragmentEventsListener = phoneContactsNewNumberFragmentEventsListener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_phone_contacts_new_number, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        closeButton = view.findViewById(R.id.phone_contacts_new_activity_button_close);
        deleteButton = view.findViewById(R.id.phone_contacts_new_activity_button_delete);
        enterNameButton = view.findViewById(R.id.phone_contacts_new_activity_enter_name_button);
        textInput = view.findViewById(R.id.fragment_phone_contacts_new_text_input);
        numpad = view.findViewById(R.id.fragment_phone_contacts_new_numpad);


        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (phoneContactsNewNumberFragmentEventsListener != null) {
                    phoneContactsNewNumberFragmentEventsListener.onCancelButtonPress();
                }
            }
        });

        deleteButton.setOnTouchListener(new RepeatListener(400, 80, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (phoneContactsNewNumberFragmentEventsListener != null) {
                    phoneContactsNewNumberFragmentEventsListener.onDeleteButtonPress();
                }
            }
        }));

        enterNameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (phoneContactsNewNumberFragmentEventsListener != null) {
                    phoneContactsNewNumberFragmentEventsListener.onEnterNameButtonPress();
                }
            }
        });

        numpad.setNumpadEventsListener(new Numpad.NumpadEventsListener() {
            @Override
            public void onButtonPress(NumpadButtonModel buttonModel) {
                // do nothing
            }

            @Override
            public void onButtonTouchStart(NumpadButtonModel buttonModel) {
                // do nothing
            }

            @Override
            public void onButtonTouchEnd(NumpadButtonModel buttonModel, boolean isTailTouchEvent) {
                if (phoneContactsNewNumberFragmentEventsListener != null && isTailTouchEvent) {
                    phoneContactsNewNumberFragmentEventsListener.onNumpadButtonPress(buttonModel);
                }
            }
        });

        if (phoneContactsNewNumberFragmentEventsListener != null) {
            phoneContactsNewNumberFragmentEventsListener.onViewReady();
        }
    }

    // setters
    // set number in text input
    public void setNumber (String number) {
        textInput.setText(number);
    }

    // enable/disable delete button
    public void setDeleteButtonIsEnabled (boolean isEnabled) {
        deleteButton.setIsDisabled(!isEnabled);
    }

    // enable/disable enter name button
    public void setEnterNameButtonIsEnabled (boolean isEnabled) {
        enterNameButton.setIsDisabled(!isEnabled);
    }
}
