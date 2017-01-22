package com.github.rubensousa.previewseekbar;


import android.view.View;

class PreviewAnimator {

    private PreviewSeekBar previewSeekBar;
    private View previewView;
    private View previewParentView;
    private int marginEnd;

    public PreviewAnimator(PreviewSeekBar previewSeekBar) {
        this.previewSeekBar = previewSeekBar;
    }

    public void setPreviewView(View previewView) {
        this.previewView = previewView;
        this.previewParentView = (View) previewView.getParent();
    }

    public PreviewSeekBar getPreviewSeekBar() {
        return previewSeekBar;
    }

    public void move(float widthOffset) {
        previewView.setX((previewParentView.getWidth() - previewView.getWidth()) * widthOffset);
    }

    public void morph() {
        previewView.setVisibility(View.VISIBLE);
    }

    public void unmorph() {
        previewView.setVisibility(View.INVISIBLE);
    }
}
