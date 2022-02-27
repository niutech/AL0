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
import computer.fuji.al0.controllers.PhoneNumpadFragmentController;
import computer.fuji.al0.models.NumpadButtonModel;
import computer.fuji.al0.utils.PhoneActivityTabFragment;
import computer.fuji.al0.utils.RepeatListener;

public class PhoneNumpadFragment extends Fragment implements PhoneActivityTabFragment {
    PhoneNumpadFragmentController controller;
    private PhoneNumpadFragmentEventsListener phoneNumpadFragmentEventsListener;
    private PhoneTabsEventListener phoneTabsEventListener;

    // tabs buttons
    private Button numpadButton;
    private Button contactsButton;
    private Button activityButton;


    private Button closeButton;
    private Button smsButton;
    private Button callButton;
    private Button deleteButton;
    private TextInput numberTextInput;
    private Numpad numpad;

    public interface PhoneNumpadFragmentEventsListener {
        public void onSmsButtonPress(String number);
        public void onCallButtonPress(String number);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_phone_numpad, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // PhoneActivity tabs button
        closeButton = (Button) getView().findViewById(R.id.phone_activity_close_button);
        numpadButton = (Button) getView().findViewById(R.id.phone_activity_numpad_button);
        contactsButton = (Button) getView().findViewById(R.id.phone_activity_contacts_button);
        activityButton  = (Button) getView().findViewById(R.id.phone_activity_activity_button);

        numpadButton.setIsActive(true);

        smsButton = (Button) getView().findViewById(R.id.fragment_phone_numpad_button_sms);
        callButton = (Button) getView().findViewById(R.id.fragment_phone_numpad_button_call);
        deleteButton = (Button) getView().findViewById(R.id.fragment_phone_numpad_button_delete);
        numberTextInput = (TextInput) getView().findViewById(R.id.fragment_phone_numpad_text_input);
        numpad = (Numpad) getView().findViewById(R.id.fragment_phone_numpad_numpad);

        controller = new PhoneNumpadFragmentController(this);

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if  (phoneTabsEventListener != null) {
                    phoneTabsEventListener.onCloseButtonPress();
                }
            }
        });

        numpadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if  (phoneTabsEventListener != null) {
                    phoneTabsEventListener.onNumpadButtonPress();
                }
            }
        });

        contactsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if  (phoneTabsEventListener != null) {
                    phoneTabsEventListener.onContactsButtonPress();
                }
            }
        });

        activityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if  (phoneTabsEventListener != null) {
                    phoneTabsEventListener.onActivityButtonPress();
                }
            }
        });

        smsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controller.onSmsButtonPress();
            }
        });

        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controller.onCallButtonPress();
            }
        });



        // use repeater listener to repeat action while button pressed
        deleteButton.setOnTouchListener(new RepeatListener(400, 80, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controller.onDeleteButtonPress();
            }
        }));

        numpad.setNumpadEventsListener(new Numpad.NumpadEventsListener() {
            @Override
            public void onButtonPress(NumpadButtonModel numpadButton) {
                controller.onNumpadButtonPress(numpadButton);
            }

            @Override
            public void onButtonTouchStart(NumpadButtonModel buttonModel) {
                numpad.playNumpadButtonDTMFTone(buttonModel);
            }

            @Override
            public void onButtonTouchEnd(NumpadButtonModel buttonModel, boolean isTailTouchEvent) {
                if (isTailTouchEvent) {
                    numpad.stopDTMFTone();
                }
            }
        });
    }

    // getters
    public PhoneNumpadFragmentEventsListener getPhoneNumpadFragmentEventsListener () {
        return phoneNumpadFragmentEventsListener;
    }

    // add event listener
    public void setPhoneNumpadFragmentEventsListener (PhoneNumpadFragmentEventsListener phoneNumpadFragmentEventsListener) {
        this.phoneNumpadFragmentEventsListener = phoneNumpadFragmentEventsListener;
    }

    @Override
    public void setPhoneTabsEventListener(PhoneTabsEventListener eventListener) {
        this.phoneTabsEventListener = eventListener;
    }

    // enable / disable SMS, CALL, DELETE buttons
    public void setActionsButtonsDisabled (boolean actionsButtonsDisabled) {
        smsButton.setIsDisabled(actionsButtonsDisabled);
        callButton.setIsDisabled(actionsButtonsDisabled);
        deleteButton.setIsDisabled(actionsButtonsDisabled);
    }

    // update UI textInputNumber
    public void updateTextInputNumber (String number) {
        numberTextInput.setText(number);
    }

    @Override
    public void onShow() {
        // do nothing
    }

    @Override
    public void onHide() {
        // do nothing
    }

    @Override
    public void setActivityButtonText(String text) {
        activityButton.setText(text);
    }
}
