package com.github.rubensousa.previewseekbar;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import java.util.ArrayList;
import java.util.List;

/**
 * A SeekBar that morphs its indicator into a preview frame while scrubbing.
 */
public class PreviewSeekBar extends AppCompatSeekBar implements PreviewView {

    private List<PreviewView.OnPreviewChangeListener> listeners;
    private PreviewDelegate delegate;
    private int frameLayoutId = View.NO_ID;
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
        if (attrs != null) {
            TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs,
                    R.styleable.PreviewSeekBar, 0, 0);
            frameLayoutId = typedArray.getResourceId(R.styleable.PreviewSeekBar_previewFrameLayout,
                    View.NO_ID);
            delegate.setAnimationEnabled(typedArray.getBoolean(
                    R.styleable.PreviewSeekBar_previewAnimationEnabled, true));
            typedArray.recycle();
        }
        delegate.setEnabled(isEnabled());
        super.setOnSeekBarChangeListener(seekBarChangeListener);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (!delegate.hasPreviewFrameLayout() && getWidth() != 0 && getHeight() != 0 && !isInEditMode()) {
            delegate.onLayout((ViewGroup) getParent(), frameLayoutId);
        }
    }

    @Override
    public synchronized void setProgress(int progress) {
        super.setProgress(progress);
        // This can be called by the constructor of the PreviewSeekBar
        if (delegate != null) {
            delegate.updateProgress(progress, getMax());
            if (delegate.isShowing()) {
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
        if (delegate.isShowing()) {
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
     * Use {@link PreviewView.OnPreviewChangeListener}
     * instead with {@link PreviewSeekBar#addOnPreviewChangeListener(OnPreviewChangeListener)}
     */
    @Override
    public void setOnSeekBarChangeListener(OnSeekBarChangeListener l) {

    }

    @Override
    public void attachPreviewFrameLayout(@NonNull FrameLayout frameLayout) {
        delegate.attachPreviewFrameLayout(frameLayout);
    }

    @Override
    public void setPreviewColorTint(int color) {
        Drawable drawable = DrawableCompat.wrap(getThumb());
        DrawableCompat.setTint(drawable, color);
        setThumb(drawable);

        drawable = DrawableCompat.wrap(getProgressDrawable());
        DrawableCompat.setTint(drawable, color);
        setProgressDrawable(drawable);
    }

    @Override
    public void setPreviewColorResourceTint(int colorResource) {
        setPreviewColorTint(ContextCompat.getColor(getContext(), colorResource));
    }

    @Override
    public int getScrubberColor() {
        ColorStateList list = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            list = getThumbTintList();
        }
        if (list != null) {
            return list.getDefaultColor();
        } else {
            return 0;
        }
    }

    @Override
    public boolean isShowingPreview() {
        return delegate.isShowing();
    }

    @Override
    public void setPreviewEnabled(boolean previewEnabled) {
        delegate.setEnabled(previewEnabled);
    }

    @Override
    public boolean isPreviewEnabled() {
        return delegate.isEnabled();
    }

    @Override
    public void showPreview() {
        if (isEnabled()) {
            delegate.show();
        }
    }

    @Override
    public void hidePreview() {
        if (isEnabled()) {
            delegate.hide();
        }
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

    public void setPreviewAnimationEnabled(boolean enable) {
        delegate.setAnimationEnabled(enable);
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
