package computer.fuji.al0.utils;

import android.app.Activity;
import android.view.Window;
import android.view.WindowManager;

public class LockScreen {
    public static void addShowOnLockScreenFlags (Activity activity) {
        Window window = activity.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        // window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
    }
}
