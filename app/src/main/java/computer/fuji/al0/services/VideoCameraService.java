package computer.fuji.al0.services;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.MediaActionSound;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.TextureView;

import androidx.annotation.NonNull;

import computer.fuji.al0.components.AutoFitTextureView;
import computer.fuji.al0.models.Media;
import computer.fuji.al0.utils.CompareSizesByArea;
import computer.fuji.al0.utils.Time;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class VideoCameraService {
    private static final String TAG = "VideoCameraService";

    private static final int SENSOR_ORIENTATION_DEFAULT_DEGREES = 90;
    private static final int SENSOR_ORIENTATION_INVERSE_DEGREES = 270;
    private static final SparseIntArray DEFAULT_ORIENTATIONS = new SparseIntArray();
    private static final SparseIntArray INVERSE_ORIENTATIONS = new SparseIntArray();

    private static Activity activity;
    private static boolean useLensFacingFront = false;
    private static AutoFitTextureView textureView;
    private static CameraDevice cameraDevice;
    private static CameraCaptureSession cameraCaptureSession;
    private static TextureView.SurfaceTextureListener surfaceTextureListener;
    private static Size previewSize;
    private static Size videoSize;
    private static MediaRecorder mediaRecorder;
    private static boolean isRecordingVideo;
    private static HandlerThread backgroundThread;
    private static Handler backgroundHandler;
    private static Semaphore cameraOpenCloseLock = new Semaphore(1);
    private static CameraDevice.StateCallback stateCallback;
    private static Integer sensorOrientation;
    private static String nextVideoAbsolutePath;
    private static CaptureRequest.Builder captureRequestBuilder;
    private static boolean isColorMode = false;
    private static final String fileExt = ".mp4";
    private static final String filePrefix = "VID_";
    private static final String fileMime = "video/mp4";

    private static MediaActionSound sound;

    // Start camera service
    public static void startVideoCamera (Activity currentActivity, AutoFitTextureView view, boolean useFrontCamera, boolean isColor) {
        // set current activity
        activity = currentActivity;
        useLensFacingFront = useFrontCamera;
        isColorMode = isColor;
        initSurfaceTextureListener();
        initStateCallBack();
        // start background thread
        startBackgroundThread();
        textureView = view;
        textureView.setInvisible(true);
        textureView.setSurfaceTextureListener(surfaceTextureListener);
        sound = new MediaActionSound();
    }

    public static void startRecording () {
        sound.play(MediaActionSound.START_VIDEO_RECORDING);

        // delay start to prevent hearing start video recording sound in recorded video
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (textureView.isAvailable()) {
                                    openCamera(textureView.getWidth(), textureView.getHeight());
                                } else {
                                    textureView.setSurfaceTextureListener(surfaceTextureListener);
                                }

                                isRecordingVideo = true;
                            }
                        });
                    }
                },
                250);

        /*
        if (textureView.isAvailable()) {
            openCamera(textureView.getWidth(), textureView.getHeight());
        } else {
            textureView.setSurfaceTextureListener(surfaceTextureListener);
        }

        isRecordingVideo = true;

         */
        // startRecordingVideo();
    }

    public static void stopRecording () {
        stopRecordingVideo();
        closeCamera();
        stopBackgroundThread();
        isRecordingVideo = false;
    }

    public static boolean getIsRecordingVideo () {
        return isRecordingVideo;
    }

    public static boolean getIsColorMode () {
        return isColorMode;
    }

    public static void setIsColorMode (boolean colorMode) {
        isColorMode = colorMode;
    }

    /**
     * Starts a background thread and its {@link Handler}.
     */
    private static void startBackgroundThread() {
        if (backgroundThread != null) {
            backgroundThread.quitSafely();
        }

        backgroundThread = new HandlerThread("VideoCameraBackground");
        backgroundThread.start();
        backgroundHandler = new Handler(backgroundThread.getLooper());
    }

    /**
     * Stops the background thread and its {@link Handler}.
     */
    private static void stopBackgroundThread() {
        backgroundThread.quitSafely();
        try {
            backgroundThread.join();
            backgroundThread = null;
            backgroundHandler = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Tries to open a {@link CameraDevice}. The result is listened by `mStateCallback`.
     */
    @SuppressWarnings("MissingPermission")
    private static void openCamera(int width, int height) {
        PermissionsService permissionsService = new PermissionsService(activity, null);
        if (permissionsService.getNotGrantedCameraRecordAudioPermissions().size() > 0) {
            // no permission to access camera or to record audio
            return;
        }

        if (null == activity || activity.isFinishing()) {
            return;
        }

        CameraManager manager = (CameraManager) activity.getSystemService(Context.CAMERA_SERVICE);
        try {
            if (!cameraOpenCloseLock.tryAcquire(2500, TimeUnit.MILLISECONDS)) {
                throw new RuntimeException("Time out waiting to lock camera opening.");
            }

            // init camera id with a default camera id
            String[] cameraIdList = manager.getCameraIdList();
            String cameraId = cameraIdList[0];

            // find the camera id according to lens direction
            for (String currentCameraId : cameraIdList) {
                CameraCharacteristics characteristics = manager.getCameraCharacteristics(currentCameraId);
                Integer facing = characteristics.get(CameraCharacteristics.LENS_FACING);

                if (useLensFacingFront) {
                    if (facing != null && facing == CameraCharacteristics.LENS_FACING_FRONT) {
                        cameraId = currentCameraId;
                    }
                } else {
                    if (facing != null && facing == CameraCharacteristics.LENS_FACING_BACK) {
                        cameraId = currentCameraId;
                    }
                }
            }

            // Choose the sizes for camera preview and video recording
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);
            StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            sensorOrientation = characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);
            if (map == null) {
                throw new RuntimeException("Cannot get available preview/video sizes");
            }
            videoSize = chooseVideoSize(map.getOutputSizes(MediaRecorder.class));
            previewSize = chooseOptimalSize(map.getOutputSizes(SurfaceTexture.class), width, height, videoSize);

            int orientation = activity.getResources().getConfiguration().orientation;
            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                textureView.setAspectRatio(previewSize.getWidth(), previewSize.getHeight());
            } else {
                textureView.setAspectRatio(previewSize.getHeight(), previewSize.getWidth());
            }

            configureTransform(width, height);
            mediaRecorder = new MediaRecorder();
            manager.openCamera(cameraId, stateCallback, null);
        } catch (CameraAccessException e) {
            activity.finish();
        } catch (NullPointerException e) {
            // Currently an NPE is thrown when the Camera2API is used but not supported on the
            // device this code runs.
        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted while trying to lock camera opening.");
        }
    }

    private static void closeCamera() {
        try {
            cameraOpenCloseLock.acquire();
            closePreviewSession();
            if (null != cameraDevice) {
                cameraDevice.close();
                cameraDevice = null;
            }
            if (null != mediaRecorder) {
                mediaRecorder.release();
                mediaRecorder = null;
            }
        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted while trying to lock camera closing.");
        } finally {
            cameraOpenCloseLock.release();
        }
    }

    /**
     * Start the camera preview.
     */
    private static void startPreview() {
        if (null == cameraDevice || !textureView.isAvailable() || null == previewSize) {
            return;
        }
        try {
            closePreviewSession();
            SurfaceTexture texture = textureView.getSurfaceTexture();
            assert texture != null;
            texture.setDefaultBufferSize(previewSize.getWidth(), previewSize.getHeight());
            captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);

            Surface previewSurface = new Surface(texture);
            captureRequestBuilder.addTarget(previewSurface);

            cameraDevice.createCaptureSession(Collections.singletonList(previewSurface),
                    new CameraCaptureSession.StateCallback() {

                        @Override
                        public void onConfigured(@NonNull CameraCaptureSession session) {
                            cameraCaptureSession = session;
                            updatePreview();
                        }

                        @Override
                        public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                            // do nothing
                        }
                    }, backgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * Update the camera preview. {@link #startPreview()} needs to be called in advance.
     */
    private static void updatePreview() {
        if (null == cameraDevice) {
            return;
        }
        try {
            setUpCaptureRequestBuilder(captureRequestBuilder);
            HandlerThread thread = new HandlerThread("CameraPreview");
            thread.start();
            cameraCaptureSession.setRepeatingRequest(captureRequestBuilder.build(), null, backgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void setUpCaptureRequestBuilder(CaptureRequest.Builder builder) {
        builder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
    }


    /**
     * Configures the necessary {@link android.graphics.Matrix} transformation to `mTextureView`.
     * This method should not to be called until the camera preview size is determined in
     * openCamera, or until the size of `mTextureView` is fixed.
     *
     * @param viewWidth  The width of `mTextureView`
     * @param viewHeight The height of `mTextureView`
     */
    private static void configureTransform(int viewWidth, int viewHeight) {
        if (null == textureView || null == previewSize || null == activity) {
            return;
        }
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        Matrix matrix = new Matrix();
        RectF viewRect = new RectF(0, 0, viewWidth, viewHeight);
        RectF bufferRect = new RectF(0, 0, previewSize.getHeight(), previewSize.getWidth());
        float centerX = viewRect.centerX();
        float centerY = viewRect.centerY();
        if (Surface.ROTATION_90 == rotation || Surface.ROTATION_270 == rotation) {
            bufferRect.offset(centerX - bufferRect.centerX(), centerY - bufferRect.centerY());
            matrix.setRectToRect(viewRect, bufferRect, Matrix.ScaleToFit.FILL);
            float scale = Math.max(
                    (float) viewHeight / previewSize.getHeight(),
                    (float) viewWidth / previewSize.getWidth());
            matrix.postScale(scale, scale, centerX, centerY);
            matrix.postRotate(90 * (rotation - 2), centerX, centerY);
        }
        textureView.setTransform(matrix);
    }

    private static void setUpMediaRecorder() throws IOException {
        if (null == activity) {
            return;
        }
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        if (nextVideoAbsolutePath == null || nextVideoAbsolutePath.isEmpty()) {
            nextVideoAbsolutePath = getVideoFilePath(activity);
        }
        mediaRecorder.setOutputFile(nextVideoAbsolutePath);
        mediaRecorder.setVideoEncodingBitRate(10000000);
        mediaRecorder.setVideoFrameRate(30);
        mediaRecorder.setVideoSize(videoSize.getWidth(), videoSize.getHeight());
        mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();

        switch (sensorOrientation) {
            case SENSOR_ORIENTATION_DEFAULT_DEGREES:
                mediaRecorder.setOrientationHint(DEFAULT_ORIENTATIONS.get(rotation) + 90);
                break;
            case SENSOR_ORIENTATION_INVERSE_DEGREES:
                mediaRecorder.setOrientationHint(INVERSE_ORIENTATIONS.get(rotation) + 270);
                break;
        }


        mediaRecorder.prepare();
    }

    private static  String getVideoFilePath(Context context) {
        String fileName = filePrefix + Time.cameraFileNameFormat.format(new Date()).concat(fileExt);
        String filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString().concat("/").concat(fileName);
        return filePath;
    }

    private static void startRecordingVideo() {
        if (null == cameraDevice || !textureView.isAvailable() || null == previewSize) {
            return;
        }
        try {
            // closePreviewSession();
            setUpMediaRecorder();
            final SurfaceTexture texture = textureView.getSurfaceTexture();
            assert texture != null;
            texture.setDefaultBufferSize(previewSize.getWidth(), previewSize.getHeight());
            captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_RECORD);
            if (!isColorMode) {
                captureRequestBuilder.set(CaptureRequest.CONTROL_EFFECT_MODE, CaptureRequest.CONTROL_EFFECT_MODE_MONO);
            }

            List<Surface> surfaces = new ArrayList<>();

            // Set up Surface for the camera preview
            Surface previewSurface = new Surface(texture);
            surfaces.add(previewSurface);
            captureRequestBuilder.addTarget(previewSurface);

            // Set up Surface for the MediaRecorder
            Surface recorderSurface = mediaRecorder.getSurface();
            surfaces.add(recorderSurface);
            captureRequestBuilder.addTarget(recorderSurface);

            // Start a capture session
            // Once the session starts, we can update the UI and start recording
            cameraDevice.createCaptureSession(surfaces, new CameraCaptureSession.StateCallback() {

                @Override
                public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSessionConfigured) {
                    cameraCaptureSession = cameraCaptureSessionConfigured;
                    updatePreview();
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // Start recording
                            mediaRecorder.start();
                            new android.os.Handler().postDelayed(
                                    new Runnable() {
                                        public void run() {
                                            activity.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    textureView.setInvisible(false);
                                                }
                                            });
                                        }
                                    },
                                    150);
                        }
                    });
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {
                    if (null != activity) {
                        // do nothing
                    }
                }
            }, backgroundHandler);
        } catch (CameraAccessException | IOException e) {
            e.printStackTrace();
        }
    }

    private static void closePreviewSession () {
        if (cameraCaptureSession != null) {
            cameraCaptureSession.close();
            cameraCaptureSession = null;
        }
    }

    private static void stopRecordingVideo() {
        isRecordingVideo = false;
        // Stop recording
        mediaRecorder.stop();
        mediaRecorder.reset();
        sound.play(MediaActionSound.STOP_VIDEO_RECORDING);

        if (null != activity) {
            // do nothing
        }

        MediaService.addMedia(activity, Media.Type.Video, nextVideoAbsolutePath, fileMime);
        nextVideoAbsolutePath = null;
        // startPreview();
    }

    /**
     * In this sample, we choose a video size with 3x4 aspect ratio. Also, we don't use sizes
     * larger than 1080p, since MediaRecorder cannot handle such a high-resolution video.
     *
     * @param choices The list of available sizes
     * @return The video size
     */
    private static Size chooseVideoSize(Size[] choices) {
        for (Size size : choices) {
            if (size.getWidth() == size.getHeight() * 4 / 3 && size.getWidth() <= 1080) {
                return size;
            }
        }
        return choices[choices.length - 1];
    }

    /**
     * Given {@code choices} of {@code Size}s supported by a camera, chooses the smallest one whose
     * width and height are at least as large as the respective requested values, and whose aspect
     * ratio matches with the specified value.
     *
     * @param choices     The list of sizes that the camera supports for the intended output class
     * @param width       The minimum desired width
     * @param height      The minimum desired height
     * @param aspectRatio The aspect ratio
     * @return The optimal {@code Size}, or an arbitrary one if none were big enough
     */
    private static Size chooseOptimalSize(Size[] choices, int width, int height, Size aspectRatio) {
        // Collect the supported resolutions that are at least as big as the preview Surface
        List<Size> bigEnough = new ArrayList<>();
        int w = aspectRatio.getWidth();
        int h = aspectRatio.getHeight();
        for (Size option : choices) {
            if (option.getHeight() == option.getWidth() * h / w &&
                    option.getWidth() >= width && option.getHeight() >= height) {
                bigEnough.add(option);
            }
        }

        // Pick the smallest of those, assuming we found any
        if (bigEnough.size() > 0) {
            return Collections.min(bigEnough, new CompareSizesByArea());
        } else {
            return choices[0];
        }
    }

    /*
     * TextureView.SurfaceTextureListener
     */
    private static void initSurfaceTextureListener () {
        surfaceTextureListener = new TextureView.SurfaceTextureListener() {

            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                openCamera(width, height);
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int width, int height) {
                configureTransform(width, height);
            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
                return true;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
                // do nothing
            }
        };
    }

    /*
     * CameraDevice.StateCallback
     */
    private static void initStateCallBack () {
        stateCallback = new CameraDevice.StateCallback() {

            @Override
            public void onOpened(@NonNull CameraDevice cameraDeviceOpened) {
                cameraDevice = cameraDeviceOpened;
                startPreview();
                cameraOpenCloseLock.release();
                if (null != textureView) {
                    configureTransform(textureView.getWidth(), textureView.getHeight());
                    // to check
                    startRecordingVideo();
                }
            }

            @Override
            public void onDisconnected(@NonNull CameraDevice cameraDeviceDisconnected) {
                cameraOpenCloseLock.release();
                cameraDevice = cameraDeviceDisconnected;
                cameraDevice.close();
                cameraDevice = null;
            }

            @Override
            public void onError(@NonNull CameraDevice cameraDeviceError, int error) {
                cameraOpenCloseLock.release();
                cameraDevice.close();
                cameraDevice = null;
                if (null != activity) {
                    activity.finish();
                }
            }
        };
    }
}
