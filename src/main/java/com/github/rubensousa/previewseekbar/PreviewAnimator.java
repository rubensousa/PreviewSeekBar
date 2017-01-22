package com.github.rubensousa.previewseekbar;


import android.view.View;

public class PreviewAnimator {

    private PreviewSeekBar previewSeekBar;
    private View previewView;

    public PreviewAnimator(PreviewSeekBar previewSeekBar) {
        this.previewSeekBar = previewSeekBar;
    }

    public void setPreviewView(View previewView) {
        this.previewView = previewView;
    }

    public PreviewSeekBar getPreviewSeekBar() {
        return previewSeekBar;
    }

    public void morph() {

    }

    public void unmorph() {

    }
}
