package computer.fuji.al0.controllers;

import computer.fuji.al0.activities.CameraActivity;
import computer.fuji.al0.components.AutoFitTextureView;
import computer.fuji.al0.services.CameraService;
import computer.fuji.al0.services.VideoCameraService;
import computer.fuji.al0.utils.Preferences;

public class CameraActivityController {
    CameraActivity activity;
    AutoFitTextureView cameraView;
    private boolean isUsingColor = false;
    private boolean isFlashAuto = false;
    Preferences preferences;

    public CameraActivityController (CameraActivity activity, AutoFitTextureView cameraView) {
        this.activity = activity;
        preferences = new Preferences(activity);
        isUsingColor = preferences.getSettingsCameraColor();
        isFlashAuto = !preferences.getSettingsCameraNoFlash();
        activity.setIsUsingFrontCamera(CameraService.getIsUsingFrontCamera());
        this.cameraView = cameraView;
        activity.showBlackFrameTemporarily(CameraActivity.CAMERA_PAUSE_IMMEDIATE);
    }

    // events
    public void onButtonClosePress () {
        if (VideoCameraService.getIsRecordingVideo()) {
            VideoCameraService.stopRecording();
        }

        activity.finish();
    }

    public void onButtonTakePhotoPress () {
        CameraService.takePicture();
    }

    public void onButtonTakeVideoPress () {
        CameraService.stopCamera();
        boolean isRecordingVideo = VideoCameraService.getIsRecordingVideo();
        if (isRecordingVideo) {
            VideoCameraService.stopRecording();
            activity.showBlackFrameTemporarily(CameraActivity.CAMERA_PAUSE_LONG);
            CameraService.startCamera(activity, cameraView);
        } else {
            CameraService.stopCamera();
            boolean isUsingFrontCamera = CameraService.getIsUsingFrontCamera();
            VideoCameraService.startVideoCamera(activity, cameraView, isUsingFrontCamera, isUsingColor);
            VideoCameraService.startRecording();
        }

        activity.setRecordingVideo(!isRecordingVideo);
    }

    public void onButtonStopVideoPress () {
        onButtonTakeVideoPress();
    }

    public void onButtonUseFrontCameraPress () {
        activity.showBlackFrameTemporarily(CameraActivity.CAMERA_PAUSE_LONG);
        // stop camera service
        CameraService.stopCamera();
        CameraService.setUseFrontCamera(!CameraService.getIsUsingFrontCamera());
        activity.setIsUsingFrontCamera(CameraService.getIsUsingFrontCamera());
        // re-init camera service
        onActivityResume();
    }

    public void onActivityResume () {
        activity.showBlackFrameTemporarily(CameraActivity.CAMERA_PAUSE_LONG);
        boolean isUsingFrontCamera = CameraService.getIsUsingFrontCamera();
        CameraService.setUseFrontCamera(isUsingFrontCamera);
        CameraService.setIsFlashAuto(isFlashAuto);
        CameraService.setIsColorMode(isUsingColor);
        CameraService.startCamera(activity, cameraView);
    }

    public void onActivityPause () {
        CameraService.stopCamera();
    }
}
