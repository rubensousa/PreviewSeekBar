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

import java.util.ArrayList;
import java.util.List;

/**
 * A {@link PreviewBar} that extends from {@link AppCompatSeekBar}.
 * <p>
 */
public class PreviewSeekBar extends AppCompatSeekBar implements PreviewBar {

    private List<PreviewBar.OnPreviewChangeListener> listeners;
    private PreviewDelegate delegate;
    private int previewId = View.NO_ID;
    private int scrubberColor = 0;
    private SeekBar.OnSeekBarChangeListener seekBarChangeListener = new OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            PreviewSeekBar.this.onProgressChanged(progress, fromUser);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            PreviewSeekBar.this.onStartTrackingTouch(seekBar);
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            PreviewSeekBar.this.onStopTrackingTouch(seekBar);
        }
    };

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
        listeners = new ArrayList<>();
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

        typedArray.recycle();
        super.setOnSeekBarChangeListener(seekBarChangeListener);
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
            if (delegate.isShowingPreview()) {
                for (OnPreviewChangeListener listener : listeners) {
                    listener.onPreview(this, progress, false);
                }
            }
        }
    }

    @Override
    public void setProgress(int progress, boolean animate) {
        super.setProgress(progress, animate);
        delegate.updateProgress(progress, getMax());
        if (delegate.isShowingPreview()) {
            for (OnPreviewChangeListener listener : listeners) {
                listener.onPreview(this, progress, false);
            }
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
     * Use {@link PreviewBar.OnPreviewChangeListener}
     * instead with {@link PreviewSeekBar#addOnPreviewChangeListener(OnPreviewChangeListener)}
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
        if (isPreviewEnabled()) {
            delegate.show();
        }
    }

    @Override
    public void hidePreview() {
        delegate.hide();
    }

    @Override
    public void setPreviewLoader(PreviewLoader previewLoader) {
        delegate.setPreviewLoader(previewLoader);
    }

    @Override
    public void addOnPreviewChangeListener(OnPreviewChangeListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    @Override
    public void removeOnPreviewChangeListener(OnPreviewChangeListener listener) {
        listeners.remove(listener);
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

    private void onProgressChanged(int progress, boolean fromUser) {
        for (OnPreviewChangeListener listener : listeners) {
            listener.onPreview(this, progress, fromUser);
        }
    }

    private void onStartTrackingTouch(SeekBar seekBar) {
        for (OnPreviewChangeListener listener : listeners) {
            listener.onStartPreview(this, seekBar.getProgress());
        }
    }

    private void onStopTrackingTouch(SeekBar seekBar) {
        for (OnPreviewChangeListener listener : listeners) {
            listener.onStopPreview(this, seekBar.getProgress());
        }
    }

}
