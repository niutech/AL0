

/*
 * Copyright 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package computer.fuji.al0.components;

import android.content.Context;
import android.util.AttributeSet;
import android.view.TextureView;
import android.view.View;

/**
 * A {@link TextureView} that can be adjusted to a specified aspect ratio.
 */
public class AutoFitTextureView extends TextureView {

    private int mRatioWidth = 0;
    private int mRatioHeight = 0;

    public AutoFitTextureView(Context context) {
        this(context, null);
    }

    public AutoFitTextureView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AutoFitTextureView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setInvisible (boolean invisible) {
        setVisibility(invisible ? View.INVISIBLE : View.VISIBLE);
    }

    /**
     * Sets the aspect ratio for this view. The size of the view will be measured based on the ratio
     * calculated from the parameters. Note that the actual sizes of parameters don't matter, that
     * is, calling setAspectRatio(2, 3) and setAspectRatio(4, 6) make the same result.
     *
     * @param width  Relative horizontal size
     * @param height Relative vertical size
     */
    public void setAspectRatio(int width, int height) {
        if (width < 0 || height < 0) {
            throw new IllegalArgumentException("Size cannot be negative.");
        }
        mRatioWidth = width;
        mRatioHeight = height;
        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        if (0 == mRatioWidth || 0 == mRatioHeight) {
            setMeasuredDimension(width, height);
            centerView(width, height, width, height);
        } else {
            if (width > height * mRatioWidth / mRatioHeight) {
                int proportionalHeight = width * mRatioHeight / mRatioWidth;
                setMeasuredDimension(width, proportionalHeight);
                centerView(width, proportionalHeight, width, height);
            } else {
                int proportionalWidth = height * mRatioWidth / mRatioHeight;
                setMeasuredDimension(proportionalWidth, height);
                centerView(proportionalWidth, height, width, height);
            }
        }
    }

    // crop image to center preview
    private void centerView (int proportionalWidth, int proportionalHeight, int textureViewWidth, int textureViewHeight) {
        // center horizontally
        if (proportionalWidth >= textureViewWidth) {
            setTranslationX(-(proportionalWidth - textureViewWidth) / 2);
        } else {
            setTranslationX(0);
        }

        // center vertically
        if (proportionalHeight >= textureViewHeight) {
            setTranslationY(-(proportionalHeight - textureViewHeight) / 2);
        } else {
            setTranslationY(0);
        }
    }
}
