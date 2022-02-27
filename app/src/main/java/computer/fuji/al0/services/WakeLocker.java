package computer.fuji.al0.services;

import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.os.PowerManager;
import android.view.WindowManager;

import static android.content.Context.KEYGUARD_SERVICE;
import static android.os.PowerManager.*;

public class WakeLocker {
    private static final String tagDefault = "phone:wakelock";
    private static final String tagDevice = "phone:wakelock:device";
    private static final String tagAlarm = "phone:wakelock:timer";
    private static final String tagTimer = "phone:wakelock:timer";
    private static final String tagCall = "phone:wakelock:call";

    public static enum Type { ALARM, TIMER, CALL }

    private static PowerManager.WakeLock wakeDeviceLock;
    private static PowerManager.WakeLock wakeLock;
    private static PowerManager.WakeLock wakeLockAlarm;
    private static PowerManager.WakeLock wakeLockTimer;
    private static PowerManager.WakeLock wakeLockCall;

    public static void acquire(Context context, Type type) {
        release(type);

        switch (type) {
            case ALARM:
                wakeLockAlarm = acquireLock(context, tagAlarm);
                break;
            case TIMER:
                wakeLockTimer = acquireLock(context, tagTimer);
                break;
            case CALL:
                wakeLockAlarm = acquireLock(context, tagCall);
                break;
            default:
                wakeLock = acquireLock(context, tagDefault);
                break;
        }
    }

    public static void release(Type type) {
        switch (type) {
            case ALARM:
                if (wakeLockAlarm != null) {
                    wakeLockAlarm.release();
                    wakeLockAlarm = null;
                }
                break;
            case TIMER:
                if (wakeLockTimer != null) {
                    wakeLockTimer.release();
                    wakeLockTimer = null;
                }
                break;
            case CALL:
                if (wakeLockCall != null) {
                    wakeLockCall.release();
                    wakeLockCall = null;
                }
                break;
            default:
                if (wakeLock != null) {
                    wakeLock.release();
                    wakeLock = null;
                }
                break;
        }
    }

    public static void wakeDevice (Activity activity) {
        releaseWakeDevice(activity);
        // wake device
        KeyguardManager.KeyguardLock lock = ((KeyguardManager) activity.getSystemService(KEYGUARD_SERVICE)).newKeyguardLock(KEYGUARD_SERVICE);
        PowerManager powerManager = ((PowerManager) activity.getSystemService(Context.POWER_SERVICE));
        PowerManager.WakeLock wakeDeviceLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, tagDevice);

        lock.disableKeyguard();
        wakeDeviceLock.acquire();
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON);
    }

    public static void releaseWakeDevice (Activity activity) {
        activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        if (wakeDeviceLock != null) {
            wakeDeviceLock.release();
            wakeDeviceLock = null;
        }
    }

    private static PowerManager.WakeLock acquireLock (Context context, String tag) {
        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wake = powerManager.newWakeLock(
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                        ACQUIRE_CAUSES_WAKEUP |
                        ON_AFTER_RELEASE, tag);

        if (wake != null && wake.isHeld() == false) {
            wake.acquire();
        }

        return wake;
    }
}
