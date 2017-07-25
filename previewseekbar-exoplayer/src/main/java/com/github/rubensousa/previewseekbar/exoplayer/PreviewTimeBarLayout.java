package com.github.rubensousa.previewseekbar.exoplayer;


import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import com.github.rubensousa.previewseekbar.base.PreviewGeneralLayout;
import com.github.rubensousa.previewseekbar.base.PreviewView;

public class PreviewTimeBarLayout extends PreviewGeneralLayout {

    private PreviewTimeBar previewTimeBar;
    private FrameLayout previewFrameLayout;


    public PreviewTimeBarLayout(Context context) {
        super(context);
    }

    public PreviewTimeBarLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PreviewTimeBarLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean checkChilds() {
        int childs = getChildCount();

        if (childs < 2) {
            return false;
        }

        boolean hasSeekbar = false;
        boolean hasFrameLayout = false;

        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);

            if (child instanceof PreviewTimeBar) {
                hasSeekbar = true;
                previewTimeBar = (PreviewTimeBar) child;
            } else if (child instanceof FrameLayout) {
                previewFrameLayout = (FrameLayout) child;
                hasFrameLayout = true;
            }

            if (hasSeekbar && hasFrameLayout) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1
                        && getResources().getConfiguration().getLayoutDirection()
                        == View.LAYOUT_DIRECTION_RTL) {
                    previewTimeBar.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                }
                return true;
            }
        }

        return hasSeekbar && hasFrameLayout;
    }

    @Override
    public void setupMargins() {
        LayoutParams layoutParams = (LayoutParams) previewTimeBar.getLayoutParams();

        layoutParams.rightMargin = (int) (previewFrameLayout.getWidth() / 2
                - previewTimeBar.getThumbOffset() * 0.9f);
        layoutParams.leftMargin = layoutParams.rightMargin;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            layoutParams.setMarginEnd(layoutParams.leftMargin);
            layoutParams.setMarginStart(layoutParams.leftMargin);
        }

        previewTimeBar.setLayoutParams(layoutParams);
        requestLayout();
        invalidate();
    }

    @Override
    public PreviewView getPreviewView() {
        return previewTimeBar;
    }

    @Override
    public FrameLayout getPreviewFrameLayout() {
        return previewFrameLayout;
    }
}
