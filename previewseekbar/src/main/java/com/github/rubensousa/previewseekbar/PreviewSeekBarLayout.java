package com.github.rubensousa.previewseekbar;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

public class PreviewSeekBarLayout extends RelativeLayout {

    private PreviewDelegate delegate;
    private PreviewSeekBar seekBar;
    private FrameLayout previewFrameLayout;
    private View morphView;
    private View frameView;
    private boolean firstLayout = true;
    private int tintColor;

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
        tintColor = ContextCompat.getColor(context, outValue.resourceId);

        // Create morph view
        morphView = new View(getContext());
        morphView.setBackgroundResource(R.drawable.previewseekbar_morph);

        // Create frame view for the circular reveal
        frameView = new View(getContext());
        delegate = new PreviewDelegate(this);
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
                        "and a FrameLayout as direct childs");
            }

            // Set proper seek bar margins
            setupSeekbarMargins();

            // Setup colors for the morph view and frame view
            setupColors();

            delegate.setup();

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

    public boolean isShowingPreview() {
        return delegate.isShowing();
    }

    public void showPreview() {
        delegate.show();
    }

    public void hidePreview() {
        delegate.hide();
    }

    public FrameLayout getPreviewFrameLayout() {
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

    public void setTintColor(@ColorInt int color) {
        tintColor = color;
        Drawable drawable = DrawableCompat.wrap(morphView.getBackground());
        DrawableCompat.setTint(drawable, color);
        morphView.setBackground(drawable);
        frameView.setBackgroundColor(color);
    }

    public void setTintColorResource(@ColorRes int color) {
        setTintColor(ContextCompat.getColor(getContext(), color));
    }

    private void setupColors() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ColorStateList list = seekBar.getThumbTintList();
            if (list != null) {
                tintColor = list.getDefaultColor();
            }
        }
        setTintColor(tintColor);
    }

    /**
     * Align seekbar thumb with the frame layout center
     */
    private void setupSeekbarMargins() {
        LayoutParams layoutParams = (LayoutParams) seekBar.getLayoutParams();

        layoutParams.rightMargin = (int) (previewFrameLayout.getWidth() / 2
                - seekBar.getThumb().getIntrinsicWidth() * 0.9f);
        layoutParams.leftMargin = layoutParams.rightMargin;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            layoutParams.setMarginEnd(layoutParams.leftMargin);
            layoutParams.setMarginStart(layoutParams.leftMargin);
        }

        seekBar.setLayoutParams(layoutParams);
        requestLayout();
        invalidate();
    }

    private boolean checkChilds() {
        int childs = getChildCount();

        if (childs < 2) {
            return false;
        }

        boolean hasSeekbar = false;
        boolean hasFrameLayout = false;

        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);

            if (child instanceof PreviewSeekBar) {
                hasSeekbar = true;
                seekBar = (PreviewSeekBar) child;
            } else if (child instanceof FrameLayout) {
                previewFrameLayout = (FrameLayout) child;
                hasFrameLayout = true;
            }

            if (hasSeekbar && hasFrameLayout) {
                return true;
            }
        }

        return hasSeekbar && hasFrameLayout;
    }

}
