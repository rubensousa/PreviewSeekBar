package com.github.rubensousa.previewseekbar;

import android.content.Context;
import android.support.v7.widget.AppCompatSeekBar;
import android.util.AttributeSet;
import android.view.View;
import android.widget.SeekBar;


public class PreviewSeekBar extends AppCompatSeekBar implements SeekBar.OnSeekBarChangeListener {

    private PreviewAnimator animator;
    private View previewView;
    private boolean isPreviewing;
    private OnSeekBarChangeListener seekBarChangeListener;

    public PreviewSeekBar(Context context) {
        super(context);
        init(context, null);
    }

    public PreviewSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public PreviewSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        super.setOnSeekBarChangeListener(this);
        animator = new PreviewAnimator(this);
    }

    @Override
    public void setOnSeekBarChangeListener(OnSeekBarChangeListener l) {
        seekBarChangeListener = l;
    }

    public void setPreviewView(View view) {
        this.previewView = view;
        animator.setPreviewView(view);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        animator.move((float) progress / seekBar.getMax());
        if (seekBarChangeListener != null) {
            seekBarChangeListener.onProgressChanged(seekBar, progress, fromUser);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        animator.morph();
        if (seekBarChangeListener != null) {
            seekBarChangeListener.onStartTrackingTouch(seekBar);
        }
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        animator.unmorph();
        if (seekBarChangeListener != null) {
            seekBarChangeListener.onStopTrackingTouch(seekBar);
        }
    }
}
