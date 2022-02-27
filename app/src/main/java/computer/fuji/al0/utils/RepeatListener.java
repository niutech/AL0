package computer.fuji.al0.utils;

// credits:
// https://stackoverflow.com/questions/4284224/android-hold-button-to-repeat-action/12795551#12795551

import android.os.Handler;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;

/**
 * A class, that can be used as a TouchListener on any view (e.g. a Button).
 * It cyclically runs a clickListener, emulating keyboard-like behaviour. First
 * click is fired immediately, next one after the initialInterval, and subsequent
 * ones after the normalInterval.
 *
 * <p>Interval is scheduled after the onClick completes, so it has to run fast.
 * If it runs slow, it does not generate skipped onClicks. Can be rewritten to
 * achieve this.
 */
public class RepeatListener implements OnTouchListener {

    private Handler handler = new Handler();

    private int initialInterval;
    private final int normalInterval;
    private final OnClickListener clickListener;
    private View touchedView;

    // touch position
    private double touchStartX;
    private double touchStartY;
    private double touchX;
    private double touchY;
    private double touchMaximumValidDistance = 80;

    private Runnable handlerRunnable = new Runnable() {
        @Override
        public void run() {
            if(touchedView.isEnabled()) {
                handler.postDelayed(this, normalInterval);
                clickListener.onClick(touchedView);
                touchedView.playSoundEffect(SoundEffectConstants.CLICK);
            } else {
                // if the view was disabled by the clickListener, remove the callback
                handler.removeCallbacks(handlerRunnable);
                touchedView.setPressed(false);
                touchedView = null;
            }
        }
    };

    /**
     * @param initialInterval The interval after first click event
     * @param normalInterval The interval after second and subsequent click
     *       events
     * @param clickListener The OnClickListener, that will be called
     *       periodically
     */
    public RepeatListener(int initialInterval, int normalInterval,
                          OnClickListener clickListener) {
        if (clickListener == null)
            throw new IllegalArgumentException("null runnable");
        if (initialInterval < 0 || normalInterval < 0)
            throw new IllegalArgumentException("negative interval");

        this.initialInterval = initialInterval;
        this.normalInterval = normalInterval;
        this.clickListener = clickListener;
    }

    public boolean onTouch(View view, MotionEvent motionEvent) {
        touchX = motionEvent.getX();
        touchY = motionEvent.getY();

        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                handler.removeCallbacks(handlerRunnable);
                handler.postDelayed(handlerRunnable, initialInterval);
                touchedView = view;
                touchedView.setPressed(true);
                clickListener.onClick(view);
                touchedView.playSoundEffect(SoundEffectConstants.CLICK);

                // update touch position
                touchStartX = touchX;
                touchStartY = touchY;
                return true;
            case MotionEvent.ACTION_MOVE:
                // check if touch moved away from initial position
                // when touch moved more than touchMaximumDistance repetitions should stop
                double touchMoveDistance = Geometry.getDistance(touchX, touchY, touchStartX, touchStartY);
                if (touchMoveDistance > touchMaximumValidDistance) {
                    return cancelAction();
                }

                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                return cancelAction();
        }

        return false;
    }

    private boolean cancelAction () {
        handler.removeCallbacks(handlerRunnable);
        if (touchedView != null) {
            touchedView.setPressed(false);
            touchedView = null;
        }
        return true;
    }
}