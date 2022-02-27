package computer.fuji.al0.services;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;

public class FlashLightService {
    private static boolean isFlashOn = false;

    public static boolean getIsFlashLightAvailable (Context context) {
        return context.getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
    }

    public static boolean getIsFlashLightOn () {
        return isFlashOn;
    }

    public static void toggleFlash (Context context) throws CameraAccessException {
        CameraManager cameraManager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
        String cameraId = null;
        try {
            // find facing back camera
            for (String currentCameraId : cameraManager.getCameraIdList()) {
                CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(currentCameraId);
                Integer facing = characteristics.get(CameraCharacteristics.LENS_FACING);
                if (facing != null && facing == CameraCharacteristics.LENS_FACING_BACK && characteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE)) {
                    cameraId = currentCameraId;
                }
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

        // toggle isFlashOn boolean
        isFlashOn = !isFlashOn;
        try {
            if (cameraId != null) {
                cameraManager.setTorchMode(cameraId, isFlashOn);
            } else {
                // do nothing
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }
}
