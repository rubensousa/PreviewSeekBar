package com.github.rubensousa.previewseekbar;

import android.content.Context;
import android.support.v7.widget.AppCompatSeekBar;
import android.util.AttributeSet;
import android.widget.SeekBar;

import java.util.ArrayList;
import java.util.List;

/**
 * A SeekBar that should be used inside PreviewSeekBarLayout
 */
public class PreviewSeekBar extends AppCompatSeekBar implements SeekBar.OnSeekBarChangeListener {

    private List<OnSeekBarChangeListener> listeners;

    public PreviewSeekBar(Context context) {
        super(context);
        init();
    }

    public PreviewSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PreviewSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        listeners = new ArrayList<>();
        setOnSeekBarChangeListener(this);
    }

    public void addOnSeekBarChangeListener(OnSeekBarChangeListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public void removeOnSeekBarChangeListener(OnSeekBarChangeListener listener) {
        listeners.remove(listener);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        for (OnSeekBarChangeListener listener : listeners) {
            listener.onProgressChanged(seekBar, progress, fromUser);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        for (OnSeekBarChangeListener listener : listeners) {
            listener.onStartTrackingTouch(seekBar);
        }
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        for (OnSeekBarChangeListener listener : listeners) {
            listener.onStopTrackingTouch(seekBar);
        }
    }
}
