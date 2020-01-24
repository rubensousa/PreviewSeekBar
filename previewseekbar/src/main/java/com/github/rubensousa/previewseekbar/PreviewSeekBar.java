package com.github.rubensousa.previewseekbar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.SeekBar;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import com.github.rubensousa.previewseekbar.animator.PreviewAnimator;

/**
 * A {@link PreviewBar} that extends from {@link AppCompatSeekBar}.
 */
public class PreviewSeekBar extends AppCompatSeekBar implements PreviewBar {

    private PreviewDelegate delegate;
    private int previewId = View.NO_ID;
    private int scrubberColor = 0;

    public PreviewSeekBar(Context context) {
        this(context, null);
    }

    public PreviewSeekBar(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.seekBarStyle);
    }

    public PreviewSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        delegate = new PreviewDelegate(this);

        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs,
                R.styleable.PreviewSeekBar, 0, 0);

        TypedArray themeTypedArray = context.getTheme().obtainStyledAttributes(
                new int[]{R.attr.colorAccent});

        final int defaultThumbColor = themeTypedArray.getColor(0, 0);

        themeTypedArray.recycle();

        previewId = typedArray.getResourceId(R.styleable.PreviewSeekBar_previewFrameLayout,
                View.NO_ID);

        scrubberColor = typedArray.getColor(R.styleable.PreviewSeekBar_previewThumbTint,
                defaultThumbColor);

        setPreviewThumbTint(scrubberColor);

        delegate.setAnimationEnabled(typedArray.getBoolean(
                R.styleable.PreviewSeekBar_previewAnimationEnabled, true));
        delegate.setPreviewEnabled(typedArray.getBoolean(
                R.styleable.PreviewSeekBar_previewEnabled, true));
        delegate.setAutoHidePreview(typedArray.getBoolean(
                R.styleable.PreviewSeekBar_previewAutoHide, true));
        typedArray.recycle();

        // Register a custom listener to handle the previews
        super.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                delegate.onScrubMove(progress, fromUser);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                delegate.onScrubStart();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                delegate.onScrubStop();
            }
        });
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (!delegate.isPreviewViewAttached() && !isInEditMode()) {
            final FrameLayout previewView = PreviewDelegate.findPreviewView(
                    (ViewGroup) getParent(), previewId);
            if (previewView != null) {
                delegate.attachPreviewView(previewView);
            }
        }
    }

    @Override
    public synchronized void setProgress(int progress) {
        super.setProgress(progress);
        // This can be called by the constructor of the PreviewSeekBar
        if (delegate != null) {
            delegate.updateProgress(progress, getMax());
        }
    }

    @Override
    public synchronized void setMax(int max) {
        super.setMax(max);
        // This can be called by the constructor of the PreviewSeekBar
        if (delegate != null) {
            delegate.updateProgress(getProgress(), getMax());
        }
    }

    /**
     * Use a {@link OnScrubListener}
     * instead with {@link PreviewSeekBar#addOnScrubListener(OnScrubListener)}
     */
    @Override
    public void setOnSeekBarChangeListener(OnSeekBarChangeListener l) {

    }

    @Override
    public void attachPreviewView(@NonNull FrameLayout previewView) {
        delegate.attachPreviewView(previewView);
    }

    @Override
    public void setPreviewThumbTint(int color) {
        Drawable drawable = DrawableCompat.wrap(getThumb());
        DrawableCompat.setTint(drawable, color);
        setThumb(drawable);
        scrubberColor = color;
    }

    @Override
    public void setPreviewThumbTintResource(int colorResource) {
        setPreviewThumbTint(ContextCompat.getColor(getContext(), colorResource));
    }

    @Override
    public int getScrubberColor() {
        return scrubberColor;
    }

    @Override
    public boolean isShowingPreview() {
        return delegate.isShowingPreview();
    }

    @Override
    public boolean isPreviewEnabled() {
        return delegate.isPreviewEnabled();
    }

    @Override
    public void setPreviewEnabled(boolean enabled) {
        delegate.setPreviewEnabled(enabled);
    }

    @Override
    public void showPreview() {
        delegate.show();
    }

    @Override
    public void hidePreview() {
        delegate.hide();
    }

    @Override
    public void setAutoHidePreview(boolean autoHide) {
        delegate.setAutoHidePreview(autoHide);
    }

    @Override
    public void setPreviewLoader(PreviewLoader previewLoader) {
        delegate.setPreviewLoader(previewLoader);
    }

    @Override
    public void addOnScrubListener(PreviewBar.OnScrubListener listener) {
        delegate.addOnScrubListener(listener);
    }

    @Override
    public void removeOnScrubListener(PreviewBar.OnScrubListener listener) {
        delegate.removeOnScrubListener(listener);
    }

    @Override
    public void addOnPreviewVisibilityListener(PreviewBar.OnPreviewVisibilityListener listener) {
        delegate.addOnPreviewVisibilityListener(listener);
    }

    @Override
    public void removeOnPreviewVisibilityListener(PreviewBar.OnPreviewVisibilityListener listener) {
        delegate.removeOnPreviewVisibilityListener(listener);
    }

    @Override
    public void setPreviewAnimator(@NonNull PreviewAnimator animator) {
        delegate.setAnimator(animator);
    }

    @Override
    public void setPreviewAnimationEnabled(boolean enable) {
        delegate.setAnimationEnabled(enable);
    }

    public void setProgressTint(@ColorInt int color) {
        Drawable drawable = DrawableCompat.wrap(getProgressDrawable());
        DrawableCompat.setTint(drawable, color);
        setProgressDrawable(drawable);
    }

    public void setProgressTintResource(@ColorRes int colorResource) {
        setProgressTint(ContextCompat.getColor(getContext(), colorResource));
    }

}
