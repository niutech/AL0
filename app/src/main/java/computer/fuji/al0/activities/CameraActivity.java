package computer.fuji.al0.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import computer.fuji.al0.R;
import computer.fuji.al0.components.AutoFitTextureView;
import computer.fuji.al0.components.Button;
import computer.fuji.al0.controllers.CameraActivityController;
import computer.fuji.al0.fragments.CameraFragment;
import computer.fuji.al0.utils.LockScreen;
import computer.fuji.al0.utils.UI;

public class CameraActivity extends AppCompatActivity {
    public static int CAMERA_PAUSE_IMMEDIATE = 0;
    public static int CAMERA_PAUSE_SHORT = 250;
    public static int CAMERA_PAUSE_LONG = 750;

    private CameraActivityController controller;

    private Button buttonClose;
    private Button buttonTakePhoto;
    private Button buttonTakeVideo;
    private Button buttonStopVideo;
    private Button buttonUseFrontCamera;

    private FrameLayout cameraWrapper;
    private CameraFragment cameraFragment;
    private AutoFitTextureView cameraView;

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // show activity on lock screen
        LockScreen.addShowOnLockScreenFlags(this);

        setContentView(R.layout.activity_camera);
        UI.hideNavigationBar(this);

        buttonClose = (Button) findViewById(R.id.camera_activity_button_close);
        buttonTakePhoto = (Button) findViewById(R.id.camera_activity_button_take_photo);
        buttonTakeVideo = (Button) findViewById(R.id.camera_activity_button_take_video);
        buttonStopVideo = (Button) findViewById(R.id.camera_activity_button_stop_video);
        buttonUseFrontCamera = (Button) findViewById(R.id.camera_activity_button_use_front_camera);
        cameraView = (AutoFitTextureView) findViewById(R.id.camera_view);

        controller = new CameraActivityController(this, cameraView);

        buttonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                controller.onButtonClosePress();
            }
        });

        buttonTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                controller.onButtonTakePhotoPress();
            }
        });

        buttonTakeVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                controller.onButtonTakeVideoPress();
            }
        });

        buttonStopVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                controller.onButtonStopVideoPress();
            }
        });

        buttonUseFrontCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                controller.onButtonUseFrontCameraPress();
            }
        });
    }

    // UI
    public void setIsUsingFrontCamera (boolean isUsingFrontCamera) {
        buttonUseFrontCamera.setIsActive(isUsingFrontCamera);
    }

    public void showBlackFrameTemporarily (int time) {
        cameraView.setInvisible(true);
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                cameraView.setInvisible(false);
                            }
                        });
                    }
                },
                time);
    }

    public void setRecordingVideo (boolean isRecordingVideo) {
        if (isRecordingVideo) {
            buttonTakeVideo.setVisibility(View.GONE);
            buttonStopVideo.setVisibility(View.VISIBLE);
            buttonTakePhoto.setIsDisabled(true);
            buttonUseFrontCamera.setIsDisabled(true);
        } else {
            buttonTakeVideo.setVisibility(View.VISIBLE);
            buttonStopVideo.setVisibility(View.GONE);
            buttonTakePhoto.setIsDisabled(false);
            buttonUseFrontCamera.setIsDisabled(false);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        controller.onActivityResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        controller.onActivityPause();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            UI.hideNavigationBar(this);
        }
    }

}
