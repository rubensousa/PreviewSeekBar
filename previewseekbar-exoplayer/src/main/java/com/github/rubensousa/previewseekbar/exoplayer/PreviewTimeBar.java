package com.github.rubensousa.previewseekbar.exoplayer;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.github.rubensousa.previewseekbar.PreviewView;

import java.util.ArrayList;
import java.util.List;


public class PreviewTimeBar extends CustomTimeBar implements PreviewView {

    private List<OnPreviewChangeListener> listeners;

    public PreviewTimeBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        listeners = new ArrayList<>();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean wasScrubbing = isScrubbing();
        boolean returnValue = super.onTouchEvent(event);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (isScrubbing()) {
                    if (!wasScrubbing) {
                        for (OnPreviewChangeListener listener : listeners) {
                            listener.onStartPreview(this);
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (wasScrubbing) {
                    for (OnPreviewChangeListener listener : listeners) {
                        listener.onPreview(this, getProgress(), true);
                    }
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                if (wasScrubbing) {
                    for (OnPreviewChangeListener listener : listeners) {
                        listener.onStopPreview(this);
                    }
                }
                break;
        }

        return returnValue;
    }

    @Override
    public int getProgress() {
        return (int) getPosition();
    }

    @Override
    public int getMax() {
        return (int) getDuration();
    }

    @Override
    public int getThumbOffset() {
        return getResources().getDimensionPixelOffset(R.dimen.previewseekbar_indicator_width);
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

}
