package computer.fuji.al0.activities;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import computer.fuji.al0.R;
import computer.fuji.al0.components.Button;
import computer.fuji.al0.components.Numpad;
import computer.fuji.al0.controllers.CallActivityController;
import computer.fuji.al0.models.NumpadButtonModel;
import computer.fuji.al0.services.WakeLocker;
import computer.fuji.al0.utils.Cutout;
import computer.fuji.al0.utils.Status;
import computer.fuji.al0.utils.Time;
import computer.fuji.al0.utils.UI;

public class CallActivity extends AppCompatActivity {
    private TextView labelConnectingNumber;
    private LinearLayout phoneStatusWrapper;
    private TextView labelStatus;
    private TextView labelDetails;
    private TextView labelDetailsDivider;
    private Button speakerButton;
    private Button bluetoothButton;
    private View bluetoothButtonSpacer;
    private Button answerButton;
    private View numpadButtonSpacer;
    private Button numpadButton;
    private Button muteButton;
    private Button silentButton;
    private Button hangupButton;
    private Button collapseCallButton;
    private View collapseCallButtonSpacer;
    private Numpad numpad;

    private TextView statusSignalLabel;
    private TextView statusBatteryLabel;

    private CallActivityController callActivityController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call);
        // force turn on screen
        WakeLocker.wakeDevice(this);
        UI.hideNavigationBar(this);

        // status
        phoneStatusWrapper = (LinearLayout) findViewById(R.id.call_activity_status);
        statusSignalLabel = (TextView) findViewById(R.id.call_activity_status_signal);
        statusBatteryLabel = (TextView) findViewById(R.id.call_activity_status_battery);

        labelConnectingNumber = (TextView) findViewById(R.id.call_activity_label_connecting_number);
        labelStatus = (TextView) findViewById(R.id.call_activity_label_status);
        labelDetails = (TextView) findViewById(R.id.call_activity_label_details);
        labelDetailsDivider = (TextView) findViewById(R.id.call_activity_label_details_divider);
        answerButton = (Button) findViewById(R.id.call_activity_button_answer);
        hangupButton = (Button) findViewById(R.id.call_activity_button_hangup);
        collapseCallButton = (Button) findViewById(R.id.call_activity_button_collapse);
        collapseCallButtonSpacer = (View) findViewById(R.id.call_activity_spacer_button_collapse);
        numpadButtonSpacer = (View) findViewById(R.id.call_activity_button_numpad_spacer);
        numpadButton = (Button) findViewById(R.id.call_activity_button_numpad);
        silentButton = (Button) findViewById(R.id.call_activity_button_silent);
        muteButton = (Button) findViewById(R.id.call_activity_button_mute);
        speakerButton = (Button) findViewById(R.id.call_activity_button_speaker);
        bluetoothButton = (Button) findViewById(R.id.call_activity_button_bluetooth);
        bluetoothButtonSpacer = (View) findViewById(R.id.call_activity_button_bluetooth_spacer);
        numpad = (Numpad) findViewById(R.id.call_activity_numpad);

        callActivityController = new CallActivityController(this);
        // add click events
        answerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callActivityController.onAnswerPress();
            }
        });

        hangupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callActivityController.onHangupPress();
            }
        });

        collapseCallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callActivityController.onCollapsePress();
            }
        });

        silentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callActivityController.onSilentPress();
            }
        });

        muteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callActivityController.onMutePress();
            }
        });

        speakerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callActivityController.onSpeakerPress();
            }
        });

        bluetoothButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callActivityController.onBluetoothPress();
            }
        });

        numpadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callActivityController.onNumpadMenuButtonPress();
            }
        });

        numpad.setNumpadEventsListener(new Numpad.NumpadEventsListener() {
            @Override
            public void onButtonPress(NumpadButtonModel buttonModel) {
                callActivityController.onNumpadButtonPress(buttonModel);
            }

            @Override
            public void onButtonTouchStart(NumpadButtonModel buttonModel) {
                callActivityController.onNumpadButtonTouchStart(buttonModel);
            }

            @Override
            public void onButtonTouchEnd(NumpadButtonModel buttonModel, boolean isTailTouchEvent) {
                callActivityController.onNumpadButtonTouchEnd(buttonModel, isTailTouchEvent);
            }
        });

        // set connecting number show the whole string when longer than screen limit
        labelConnectingNumber.setSelected(true);
    }

    @Override
    public void onAttachedToWindow () {
        super.onAttachedToWindow();
        // handle cutout
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            Cutout cutout = new Cutout(getWindow());
            cutout.addPaddingToViewAtCutoutPosition(phoneStatusWrapper);
        }
    }

    @Override
    public void onResume () {
        super.onResume();

        callActivityController.onActivityResume();
    }

    @Override
    public void onPause () {
        super.onPause();
        // clean turn on screen flag
        WakeLocker.releaseWakeDevice(this);

        callActivityController.onActivityPause();
    }

    @Override
    public void onDestroy () {
        super.onDestroy();
        callActivityController.onDestroy();
    }

    // UI
    // show / hide answer button
    private void showAnswerButton (boolean showAnswerButton) {
        if (showAnswerButton) {
            answerButton.setVisibility(View.VISIBLE);
            numpadButton.setVisibility(View.GONE);
            numpadButtonSpacer.setVisibility(View.GONE);
        } else {
            answerButton.setVisibility(View.GONE);
        }
    }

    // show / hide answer button
    private void showSilentButton (boolean showSilenceButton) {
        if (showSilenceButton) {
            silentButton.setVisibility(View.VISIBLE);
            muteButton.setVisibility(View.GONE);
        } else {
            silentButton.setVisibility(View.GONE);
            muteButton.setVisibility(View.VISIBLE);
        }
    }

    // show / hide speaker button
    private void showSpeakerButton (boolean showSpeakerButton) {
        if (showSpeakerButton) {
            speakerButton.setVisibility(View.VISIBLE);
        } else {
            speakerButton.setVisibility(View.GONE);
        }
    }

    // show / hide bluetooth button
    public void showBluetoothButton (boolean showBluetoothButton) {
        if (showBluetoothButton) {
            showSpeakerButton(true);
            bluetoothButtonSpacer.setVisibility(View.VISIBLE);
            bluetoothButton.setVisibility(View.VISIBLE);
        } else {
            bluetoothButtonSpacer.setVisibility(View.GONE);
            bluetoothButton.setVisibility(View.GONE);
        }
    }

    public void setBluetoothButtonActive (boolean isActive) {
        bluetoothButton.setIsActive(isActive);
        if (isActive) {
            speakerButton.setIsActive(false);
        }
    }

    // show / hide numpad button
    private void showNumpadButton (boolean showNumpadButton) {
        if (showNumpadButton) {
            numpadButton.setVisibility(View.VISIBLE);
            numpadButtonSpacer.setVisibility(View.VISIBLE);
            answerButton.setVisibility(View.GONE);
        } else {
            numpadButton.setVisibility(View.GONE);
        }
    }

    // show / hide collapse button
    private void showCollapseButton (boolean showCollapseButton) {
        collapseCallButton.setVisibility(showCollapseButton ? View.VISIBLE : View.GONE);
        collapseCallButtonSpacer.setVisibility(showCollapseButton ? View.VISIBLE : View.GONE);
    }

    // show UI in ringing state
    public void showCallInRingingState () {
        showAnswerButton(true);
        showSilentButton(true);
        showSpeakerButton(false);
        showCollapseButton(false);
        labelStatus.setText(getString(R.string.call_activity_label_in_calling));
        labelDetailsDivider.setVisibility(View.GONE);
    }

    // show UI in dialing state
    public void showCallInDialingState () {
        showAnswerButton(false);
        showSilentButton(false);
        showSpeakerButton(true);
        showCollapseButton(false);
        labelStatus.setText(getString(R.string.call_activity_label_out_calling));
        labelDetailsDivider.setVisibility(View.GONE);
    }

    // show UI in in call state
    public void showCallInCallState (String currentCallNumber, boolean isInboundCall) {
        // hide answer button
        showAnswerButton(false);
        showSilentButton(false);
        showSpeakerButton(true);
        // show numpad button when call is outbound
        showNumpadButton(!isInboundCall);
        // update ui call state
        labelStatus.setText(isInboundCall ? getString(R.string.call_activity_label_in_call_in) : getString(R.string.call_activity_label_in_call_out));
        labelDetailsDivider.setVisibility(View.VISIBLE);
        updateLabelConnectingNumber(currentCallNumber);
        // show collapse buttons
        showCollapseButton(true);
    }

    // update UI call duration
    public void updateCallTime (int callTimeSeconds) {
        labelDetails.setText(Time.secondsToHMS(callTimeSeconds));
    }

    // update UI mute button state
    public void updateMuteState (boolean isMute) {
        muteButton.setIsActive(isMute);
    }

    // update UI speaker button state
    public void updateSpeakerState (boolean isCallSpeaker) {
        speakerButton.setIsActive(isCallSpeaker);
        bluetoothButton.setIsActive(false);
    }

    // update call number
    public void updateLabelConnectingNumber (String number) {
        if (labelConnectingNumber.getText() != number) {
            labelConnectingNumber.setText(number);
        }
    }

    // toggle Numpad visibility
    // update numpad button according to numpad state
    public void toggleNumpad () {
        boolean isNumpadVisible = numpad.getVisibility() == View.VISIBLE;

        numpadButton.setIsActive(!isNumpadVisible);
        if (isNumpadVisible) {
            numpad.setVisibility(View.GONE);
            labelConnectingNumber.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        } else {
            numpad.setVisibility(View.VISIBLE);
            labelConnectingNumber.setEllipsize(TextUtils.TruncateAt.END);
        }
    }

    public void updateStatusBattery (final int batteryLevel, final boolean isCharging) {
        final Activity activity = this;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (statusBatteryLabel != null) {
                    statusBatteryLabel.setText(Status.batteryLevelToString(activity, batteryLevel, isCharging));
                }

            }
        });
    }

    public void updateStatusSignal (final int signalLevel, final String networkClass) {
        final Activity activity = this;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (statusBatteryLabel != null) {
                    statusSignalLabel.setText(Status.signalToString(activity, signalLevel, networkClass, true));
                }

            }
        });
    }

    // events
    // ignore back button press
    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            UI.hideNavigationBar(this);
        }
    }
}
