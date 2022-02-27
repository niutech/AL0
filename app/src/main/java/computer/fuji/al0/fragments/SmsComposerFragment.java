package computer.fuji.al0.fragments;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import computer.fuji.al0.R;
import computer.fuji.al0.components.Keyboard;
import computer.fuji.al0.components.TextInputMovableCursor;
import computer.fuji.al0.controllers.SmsComposerFragmentController;
import computer.fuji.al0.models.Contact;
import computer.fuji.al0.models.KeyboardButtonModel;
import computer.fuji.al0.models.Sms;
import computer.fuji.al0.utils.ClipboardUtils;

import static android.content.ClipDescription.MIMETYPE_TEXT_PLAIN;

public class SmsComposerFragment extends Fragment {
    SmsComposerFragmentController controller;
    private SmsComposerFragmentEventsListener smsComposerFragmentEventsListener;

    private TextView smsLabel;
    private TextInputMovableCursor smsBodyInput;
    private Keyboard keyboard;
    private String smsLabelPrefix;

    public interface SmsComposerFragmentEventsListener {
        public void onSmsSent(Sms smsSent);
        public void onClose();
    }

    public void setSmsComposerFragmentEventsListener (SmsComposerFragmentEventsListener smsComposerFragmentEventsListener) {
        this.smsComposerFragmentEventsListener = smsComposerFragmentEventsListener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sms_composer, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        smsLabelPrefix = getActivity().getString(R.string.fragment_sms_composer_label_message);

        smsLabel = (TextView) view.findViewById(R.id.sms_composer_fragment_sms_label);
        smsBodyInput = (TextInputMovableCursor) view.findViewById(R.id.sms_composer_fragment_sms_body);
        keyboard = (Keyboard) view.findViewById(R.id.sms_composer_fragment_keyboard);

        controller = new SmsComposerFragmentController(this);

        keyboard.setCloseButtonText(getActivity().getString(R.string.close_contestual_symbol));

        keyboard.setRightActionButtonText(getActivity().getString(R.string.fragment_sms_composer_button_send));
        setInComposeMode();

        keyboard.setKeyboardEventsListener(new Keyboard.KeyboardEventsListener() {
            @Override
            public void onButtonPress(KeyboardButtonModel buttonModel) {
                // after an error reset label if user edit the message
                // calling setInComposeMode
                switch (buttonModel.getId()) {
                    case BUTTON_DELETE:
                        smsBodyInput.deleteAtCursorPosition();
                        controller.onSmsBodyChange(smsBodyInput.getText().toString());
                        setInComposeMode();
                        break;
                    case BUTTON_DELETE_WORD:
                        smsBodyInput.deleteWordAtCursorPosition();
                        controller.onSmsBodyChange(smsBodyInput.getText().toString());
                        setInComposeMode();
                        break;
                    case BUTTON_MOVE_CURSOR_LEFT:
                        smsBodyInput.moveCursorLeft(true);
                        setInComposeMode();
                        break;
                    case BUTTON_MOVE_CURSOR_RIGHT:
                        smsBodyInput.moveCursorRight(true);
                        setInComposeMode();
                        break;
                    case BUTTON_MOVE_CURSOR_RIGHT_WORD:
                        smsBodyInput.moveCursorRightWord();
                        setInComposeMode();
                        break;
                    case BUTTON_MOVE_CURSOR_LEFT_WORD:
                        smsBodyInput.moveCursorLeftWord();
                        setInComposeMode();
                        break;
                    case BUTTON_PASTE:
                        // get clipboard content
                        String pasteString = ClipboardUtils.getPlainText(getContext());
                        if (pasteString != null) {
                            for (int i = 0; i < pasteString.length(); i ++) {
                                smsBodyInput.addCharacter("" + pasteString.charAt(i));
                            }

                            controller.onSmsBodyChange(smsBodyInput.getText().toString());
                            setInComposeMode();
                        }

                        break;
                    default:
                        // do nothing
                        break;
                }
            }

            @Override
            public void onButtonTouchStart(KeyboardButtonModel buttonModel) {

            }

            @Override
            public void onButtonTouchEnd(KeyboardButtonModel buttonModel, boolean isTailTouchEvent) {
                // after an error reset label if user edit the message
                // calling setInComposeMode()
                switch (buttonModel.getId()) {
                    case BUTTON_DELETE:
                    case BUTTON_MOVE_CURSOR_LEFT:
                    case BUTTON_MOVE_CURSOR_RIGHT:
                    case BUTTON_PASTE:
                        // do nothing
                        break;
                    default:
                        if (isTailTouchEvent) {
                            smsBodyInput.addCharacter(buttonModel.getKey());
                            controller.onSmsBodyChange(smsBodyInput.getText().toString());
                            setInComposeMode();
                        }
                        break;
                }
            }

            @Override
            public void onButtonSpacePress(KeyboardButtonModel buttonModel) {
                smsBodyInput.addCharacter(buttonModel.getKey());
                controller.onSmsBodyChange(smsBodyInput.getText().toString());
                setInComposeMode();
            }

            @Override
            public void onButtonRightActionPress() {
                controller.onButtonSendPress();
            }

            @Override
            public void onButtonClosePress() {
                controller.onButtonClosePress();
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();

        // notify controller the fragment is on pause to clear the registered broadcast receiver
        controller.onFragmentPause();
    }

    // set Contact
    public void setContact (Contact contact) {
        controller.setContact(contact);
    }

    private String smsBodyToComposeLabel (String body) {
        int[] smsMessageStats = SmsMessage.calculateLength(body, false);
        return smsLabelPrefix.concat(" (")
                .concat(String.valueOf(smsMessageStats[2]))
                .concat("/")
                .concat(String.valueOf(smsMessageStats[0]))
                .concat(")");
    }

    private void scrollSmsBodyInputToBottom () {
        int scrollAmount =
                smsBodyInput.getPaddingTop() + (smsBodyInput.getLineHeight() * smsBodyInput.getLineCount())
                - smsBodyInput.getPaddingBottom() - smsBodyInput.getLineHeight() * (smsBodyInput.getMaxLines() - 1);

        smsBodyInput.scrollTo(0, scrollAmount);
    }

    // update UI
    public void clearSmsBody () {
        smsBodyInput.clearText();
        keyboard.setTypedText("");
        keyboard.setKeyboardType(Keyboard.KeyboardType.LETTERS_UPPERCASE);
        keyboard.setKeyboardRightActionButtonEnabled(false);
    }

    public void setInSendingMode () {
        if (getActivity() != null) {
            smsLabel.setText(getActivity().getString(R.string.fragment_sms_composer_label_sending));
            setKeyboardEnabled(false);
        }
    }

    public void setInSendErrorMode (int errorMode) {
        if (getActivity() != null) {
            switch (errorMode) {
                case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                    smsLabel.setText(getActivity().getString(R.string.fragment_sms_composer_label_send_error_generic));
                    break;
                case SmsManager.RESULT_ERROR_NO_SERVICE:
                    smsLabel.setText(getActivity().getString(R.string.fragment_sms_composer_label_send_error_no_service));
                    break;
                case SmsManager.RESULT_ERROR_NULL_PDU:
                    smsLabel.setText(getActivity().getString(R.string.fragment_sms_composer_label_send_error_pdu));
                    break;
                case SmsManager.RESULT_ERROR_RADIO_OFF:
                    smsLabel.setText(getActivity().getString(R.string.fragment_sms_composer_label_send_error_radio_off));
                    break;
            }
        }

        setKeyboardEnabled(true);
    }

    public void setInComposeMode () {
        if (getActivity() != null) {
            smsLabel.setText(smsBodyToComposeLabel(smsBodyInput.getText().toString()));
        }
        setKeyboardEnabled(true);
        boolean canSendSms = smsBodyInput.getText().length() > 0;
        keyboard.setKeyboardRightActionButtonEnabled(canSendSms);
    }

    public void onSmsSent (Sms smsSent) {
        if (smsComposerFragmentEventsListener != null) {
            smsComposerFragmentEventsListener.onSmsSent(smsSent);
        }
    }

    public void onClose () {
        if (smsComposerFragmentEventsListener != null) {
            smsComposerFragmentEventsListener.onClose();
        }
    }

    private void setKeyboardEnabled (boolean keyboardEnabled) {
        keyboard.setKeyboardEnabled(keyboardEnabled);
    }
}
