package com.github.rubensousa.previewseekbar.exoplayer;

import android.content.Context;
import android.util.AttributeSet;

import com.github.rubensousa.previewseekbar.base.PreviewView;
import com.google.android.exoplayer2.ui.TimeBar;

import java.util.ArrayList;
import java.util.List;


public class PreviewTimeBar extends CustomTimeBar implements PreviewView, TimeBar.OnScrubListener {

    private List<OnPreviewChangeListener> listeners;

    public PreviewTimeBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        listeners = new ArrayList<>();
        setListener(this);
    }

    @Override
    public int getProgress() {
        return (int) getScrubPosition();
    }

    @Override
    public int getMax() {
        return (int) getDuration();
    }

    @Override
    public int getThumbOffset() {
        return getResources().getDimensionPixelOffset(R.dimen.previewseekbar_thumb_offset);
    }

    @Override
    public int getDefaultColor() {
        return getScrubberColor();
    }

    @Override
    public void addOnPreviewChangeListener(OnPreviewChangeListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeOnPreviewChangeListener(OnPreviewChangeListener listener) {
        listeners.remove(listener);
    }

    @Override
    public void onScrubStart(TimeBar timeBar, long position) {
        for (OnPreviewChangeListener listener : listeners) {
            listener.onStartPreview(this);
        }
    }

    @Override
    public void onScrubMove(TimeBar timeBar, long position) {
        for (OnPreviewChangeListener listener : listeners) {
            listener.onPreview(this, (int) position, true);
        }
    }

    @Override
    public void onScrubStop(TimeBar timeBar, long position, boolean canceled) {
        for (OnPreviewChangeListener listener : listeners) {
            listener.onStopPreview(this);
        }
    }
}
