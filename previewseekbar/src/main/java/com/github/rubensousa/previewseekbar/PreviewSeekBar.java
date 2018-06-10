package com.github.rubensousa.previewseekbar;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.AppCompatSeekBar;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.SeekBar;

import java.util.ArrayList;
import java.util.List;

/**
 * A SeekBar that morphs its indicator into a preview frame while scrubbing.
 */
public class PreviewSeekBar extends AppCompatSeekBar implements PreviewView,
        SeekBar.OnSeekBarChangeListener {

    private List<PreviewView.OnPreviewChangeListener> listeners;
    private PreviewDelegate delegate;
    private int frameLayoutId = View.NO_ID;

    public PreviewSeekBar(Context context) {
        this(context, null, 0);
    }

    public PreviewSeekBar(Context context, AttributeSet attrs) {
        this(context, attrs, android.support.v7.appcompat.R.attr.seekBarStyle);
    }

    public PreviewSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (!delegate.isSetup() && getWidth() != 0 && getHeight() != 0 && !isInEditMode()) {
            delegate.onLayout((ViewGroup) getParent(), frameLayoutId);
        }
    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
                    R.styleable.PreviewSeekBar, 0, 0);
            frameLayoutId = a.getResourceId(R.styleable.PreviewSeekBar_previewFrameLayout,
                    View.NO_ID);
        }
        listeners = new ArrayList<>();
        delegate = new PreviewDelegate(this, getDefaultColor());
        delegate.setEnabled(isEnabled());
        super.setOnSeekBarChangeListener(this);
    }

    @Override
    public void setOnSeekBarChangeListener(OnSeekBarChangeListener l) {
        // No-op
    }

    @Override
    public void attachPreviewFrameLayout(FrameLayout frameLayout) {
        delegate.attachPreviewFrameLayout(frameLayout);
    }

    @Override
    public void setPreviewColorTint(int color) {
        delegate.setPreviewColorTint(color);
        Drawable drawable = DrawableCompat.wrap(getThumb());
        DrawableCompat.setTint(drawable, color);
        setThumb(drawable);

        drawable = DrawableCompat.wrap(getProgressDrawable());
        DrawableCompat.setTint(drawable, color);
        setProgressDrawable(drawable);
    }

    @Override
    public void setPreviewColorResourceTint(int color) {
        setPreviewColorTint(ContextCompat.getColor(getContext(), color));
    }

    @Override
    public int getDefaultColor() {
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
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        for (OnPreviewChangeListener listener : listeners) {
            listener.onPreview(this, progress, fromUser);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        for (OnPreviewChangeListener listener : listeners) {
            listener.onStartPreview(this, seekBar.getProgress());
        }
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        for (OnPreviewChangeListener listener : listeners) {
            listener.onStopPreview(this, seekBar.getProgress());
        }
    }

}
