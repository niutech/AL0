package computer.fuji.al0.controllers;

import computer.fuji.al0.fragments.PhoneNumpadFragment;
import computer.fuji.al0.models.NumpadButtonModel;

public class PhoneNumpadFragmentController {
    PhoneNumpadFragment fragment;
    String currentNumber = "";

    public PhoneNumpadFragmentController (PhoneNumpadFragment fragment) {
        this.fragment = fragment;
        fragment.setActionsButtonsDisabled(true);
    }

    // events
    // SMS button press
    public void onSmsButtonPress () {
        PhoneNumpadFragment.PhoneNumpadFragmentEventsListener phoneNumpadFragmentEventsListener = fragment.getPhoneNumpadFragmentEventsListener();
        if (phoneNumpadFragmentEventsListener != null) {
            phoneNumpadFragmentEventsListener.onSmsButtonPress(currentNumber);
        }
    }

    // CALL button press
    public void onCallButtonPress () {
        PhoneNumpadFragment.PhoneNumpadFragmentEventsListener phoneNumpadFragmentEventsListener = fragment.getPhoneNumpadFragmentEventsListener();
        if (phoneNumpadFragmentEventsListener != null) {
            phoneNumpadFragmentEventsListener.onCallButtonPress(currentNumber);
        }
    }

    // DELETE button press
    public void onDeleteButtonPress () {
        // if number is longer than 0 remove last char
        // disable action buttons when current number length is 0
        if (currentNumber.length() <= 1) {
            fragment.setActionsButtonsDisabled(true);
            currentNumber = "";
        } else {
            currentNumber = currentNumber.substring(0, currentNumber.length() - 1);
        }

        fragment.updateTextInputNumber(currentNumber);
    }

    public void onNumpadButtonPress (NumpadButtonModel numpadButton) {
        // add typed number to current number
        // enable action buttons
        currentNumber = currentNumber.concat(numpadButton.getNumber());
        fragment.updateTextInputNumber(currentNumber);
        fragment.setActionsButtonsDisabled(false);
    }
}
