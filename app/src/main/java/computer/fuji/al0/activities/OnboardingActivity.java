package computer.fuji.al0.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import computer.fuji.al0.R;
import computer.fuji.al0.components.Button;
import computer.fuji.al0.components.Dialog;
import computer.fuji.al0.controllers.OnboardingActivityController;
import computer.fuji.al0.utils.UI;

public class OnboardingActivity extends AppCompatActivity {
    private OnboardingActivityController controller;

    private LinearLayout wrapperView;
    private Dialog introDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);
        UI.hideNavigationBar(this);
        wrapperView = (LinearLayout) findViewById(R.id.onboarding_activity_wrapper);

        controller = new OnboardingActivityController(this);
    }

    /*
     *  Intro Dialog, used on the app first run to invete user to use LockMode and set app as default screen
     */
    public void removeIntroDialog () {
        if (introDialog != null) {
            wrapperView.removeView(introDialog);
        }
    }

    // Step 1 Intro Dialog, welcome
    public void showIntroDialogStep1() {
        removeIntroDialog();
        introDialog = new Dialog(this);
        wrapperView.addView(introDialog, 0);
        introDialog.setText(
                getResources().getString(R.string.main_activity_intro_step1_title),
                getResources().getString(R.string.main_activity_intro_step1_body),
                "",
                getResources().getString(R.string.main_activity_intro_step1_continue_button));
        introDialog.setButtonActionLeftVisible(false);
        Button continueButton = introDialog.getDialogButtonActionRight();
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controller.onIntroStep1ContinuePress();
            }
        });
    }

    // Step 2 Intro Dialog, ask to set Lock Mode
    public void showIntroDialogStep2 () {
        removeIntroDialog();
        introDialog = new Dialog(this);
        //mainActivityWrapper.removeView(menuListView);
        wrapperView.addView(introDialog, 0);
        introDialog.setText(
                getResources().getString(R.string.main_activity_intro_step2_title),
                getResources().getString(R.string.main_activity_intro_step2_body),
                getResources().getString(R.string.main_activity_intro_step2_no_button),
                getResources().getString(R.string.main_activity_intro_step2_yes_button));
        Button noButton = introDialog.getDialogButtonActionLeft();
        Button yesButton = introDialog.getDialogButtonActionRight();


        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controller.onIntroStep2NoPress();
            }
        });

        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controller.onIntroStep2YesPress();
            }
        });
    }

    // Step 3 Ask set default Phone App
    public void showIntroDialogStep3 () {
        removeIntroDialog();
        introDialog = new Dialog(this);
        wrapperView.addView(introDialog, 0);
        introDialog.setText(
                getResources().getString(R.string.main_activity_intro_step3_title),
                getResources().getString(R.string.main_activity_intro_step3_body),
                getResources().getString(R.string.main_activity_intro_step3_no_button),
                getResources().getString(R.string.main_activity_intro_step3_yes_button));
        Button noButton = introDialog.getDialogButtonActionLeft();
        Button yesButton = introDialog.getDialogButtonActionRight();


        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controller.onIntroStep3NoPress();
            }
        });

        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controller.onIntroStep3YesPress();
            }
        });
    }

    // Step 4 Ask set default Phone App
    public void showIntroDialogStep4 () {
        removeIntroDialog();
        introDialog = new Dialog(this);
        wrapperView.addView(introDialog, 0);
        introDialog.setText(
                getResources().getString(R.string.main_activity_intro_step4_title),
                getResources().getString(R.string.main_activity_intro_step4_body),
                getResources().getString(R.string.main_activity_intro_step4_no_button),
                getResources().getString(R.string.main_activity_intro_step4_yes_button));
        Button noButton = introDialog.getDialogButtonActionLeft();
        Button yesButton = introDialog.getDialogButtonActionRight();


        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controller.onIntroStep4NoPress();
            }
        });

        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controller.onIntroStep4YesPress();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        controller.onActivityAskPermissionResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        controller.onActivityRequestPermissionsResult(requestCode, permissions, grantResults);
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
