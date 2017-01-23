package com.github.rubensousa.previewseekbar;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.List;

public class PreviewSeekBarLayout extends LinearLayoutCompat {

    private PreviewDelegate delegate;
    private PreviewSeekBar seekBar;
    private PreviewFrameLayout previewFrameLayout;
    private View morphView;
    private View frameView;
    private boolean firstLayout = true;

    public PreviewSeekBarLayout(Context context) {
        super(context);
        init(context, null);
    }

    public PreviewSeekBarLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public PreviewSeekBarLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedValue outValue = new TypedValue();

        getContext().getTheme().resolveAttribute(R.attr.colorAccent, outValue, true);
        int colorRes = outValue.resourceId;

        // Create morph view
        morphView = new View(getContext());
        morphView.setBackgroundResource(R.drawable.previewseekbar_morph);

        // Tint to accent color
        Drawable drawable = morphView.getBackground();
        int colorInt = ContextCompat.getColor(getContext(), colorRes);
        drawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(drawable, colorInt);
        morphView.setBackground(drawable);

        // Create frame view for the circular reveal
        frameView = new View(getContext());
        frameView.setBackgroundResource(colorRes);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (getWidth() == 0 || getHeight() == 0) {
            return;
        } else if (firstLayout) {

            // Check if we have the proper views
            if (!checkChilds()) {
                throw new IllegalStateException("You need to add a PreviewSeekBar" +
                        "and a PreviewFrameLayout as direct childs");
            }

            delegate = new PreviewDelegate(this);

            // Setup morph view
            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(0, 0);
            layoutParams.width = getResources()
                    .getDimensionPixelSize(R.dimen.previewseekbar_indicator_width);
            layoutParams.height = layoutParams.width;
            addView(morphView, layoutParams);

            // Add frame view to the preview layout
            FrameLayout.LayoutParams frameLayoutParams
                    = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            frameLayoutParams.gravity = Gravity.CENTER;
            previewFrameLayout.addView(frameView, frameLayoutParams);
            firstLayout = false;
        }
    }


    public PreviewFrameLayout getPreviewFrameLayout() {
        return previewFrameLayout;
    }

    public PreviewSeekBar getSeekBar() {
        return seekBar;
    }

    View getFrameView() {
        return frameView;
    }

    View getMorphView() {
        return morphView;
    }

    private boolean checkChilds() {
        int childs = getChildCount();

        if (childs != 2) {
            return false;
        }

        View firstChild = getChildAt(0);
        View secondChild = getChildAt(1);

        if (firstChild instanceof PreviewSeekBar) {
            seekBar = (PreviewSeekBar) firstChild;
            if (secondChild instanceof PreviewFrameLayout) {
                previewFrameLayout = (PreviewFrameLayout) secondChild;
                return true;
            }
        } else if (secondChild instanceof PreviewSeekBar) {
            seekBar = (PreviewSeekBar) secondChild;
            if (firstChild instanceof PreviewFrameLayout) {
                previewFrameLayout = (PreviewFrameLayout) firstChild;
                return true;
            }
        }

        return false;
    }

}
