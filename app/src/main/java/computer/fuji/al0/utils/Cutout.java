package computer.fuji.al0.utils;

import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.view.Display;
import android.view.DisplayCutout;
import android.view.View;
import android.view.Window;

import androidx.annotation.RequiresApi;

import java.util.List;

public class Cutout {
    public static enum Position { LEFT, RIGHT, CENTER, NOTHING }

    private Position cutoutPosition;
    private int screenWidth;
    private int cutoutPositionLeft;
    private int cutoutPositionRight;
    private int cutoutPositionBottom;

    public Cutout (Window window) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            Display display = window.getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            screenWidth = size.x;
            int screenCenterX = screenWidth / 2;
            int screenCenterY = size.y / 2;

            cutoutPositionLeft = 0;
            cutoutPositionRight = size.x;

            DisplayCutout displayCutout = window.getDecorView().getRootWindowInsets().getDisplayCutout();

            if (displayCutout != null) {
                List<Rect> bounding = displayCutout.getBoundingRects();
                for (int i = 0; i < bounding.size(); i++) {
                    if (bounding.get(i).top <= screenCenterY) {
                        cutoutPositionLeft = bounding.get(i).left;
                        cutoutPositionRight = bounding.get(i).right;
                        cutoutPositionBottom = bounding.get(i).bottom;
                    }
                }

                // check cutout position
                if (cutoutPositionLeft < screenCenterX && cutoutPositionRight > screenCenterY) {
                    cutoutPosition = Position.CENTER;
                } else if (cutoutPositionLeft < screenCenterX && cutoutPositionRight <= screenCenterX) {
                    cutoutPosition = Position.LEFT;
                } else if (cutoutPositionLeft > screenCenterX && cutoutPositionRight > screenCenterX) {
                    cutoutPosition = Position.RIGHT;
                } else {
                    cutoutPosition = Position.NOTHING;
                }
            } else {
                cutoutPosition = Position.NOTHING;
            }
        }
    }

    public Position getCutoutPosition () {
        return cutoutPosition;
    }

    public int getCutoutPositionLeft () {
        return cutoutPositionLeft;
    }

    public int getCutoutPositionRight () {
        return cutoutPositionRight;
    }

    public int getCutoutPositionBottom () {
        return cutoutPositionBottom;
    }

    public void addPaddingToViewAtCutoutPosition (View view) {
        int left = view.getPaddingLeft();
        int top = view.getPaddingTop();
        int right = view.getPaddingRight();
        int bottom = view.getPaddingBottom();

        if (getCutoutPosition() == Position.LEFT) {
            view.setPadding(left + getCutoutPositionRight(), top, right, bottom);
        } else if (getCutoutPosition() == Position.RIGHT) {
            int paddingRight = screenWidth - getCutoutPositionLeft();
            view.setPadding(left, top, right + paddingRight, bottom);
        }
    }

    public void addPaddingTopToViewAtCutoutTopPosition (View view) {
        int left = view.getPaddingLeft();
        int top = view.getPaddingTop();
        int right = view.getPaddingRight();
        int bottom = view.getPaddingBottom();

        int cutoutBottom = getCutoutPositionBottom();
        if (cutoutBottom > 0) {
            view.setPadding(left, top + cutoutBottom, right, bottom);
        }
    }
}
