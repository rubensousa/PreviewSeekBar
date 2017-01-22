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
        setOnSeekBarChangeListener(this);
    }

    public void setPreviewView(View view) {
        this.previewView = view;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        animator.morph();
        seekBar.getProgress();
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        animator.unmorph();
    }
}
