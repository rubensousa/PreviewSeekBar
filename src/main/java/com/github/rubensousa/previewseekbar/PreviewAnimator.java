package com.github.rubensousa.previewseekbar;


import android.view.View;

abstract class PreviewAnimator {

    static final int MORPH_REVEAL_DURATION = 250;
    static final int MORPH_MOVE_DURATION = 200;
    static final int UNMORPH_MOVE_DURATION = 200;
    static final int UNMORPH_UNREVEAL_DURATION = 250;

    PreviewSeekBar previewSeekBar;
    PreviewSeekBarLayout previewSeekBarLayout;
    View previewView;
    View frameView;
    View morphView;

    public PreviewAnimator(PreviewSeekBarLayout previewSeekBarLayout) {
        this.previewSeekBarLayout = previewSeekBarLayout;
        this.previewSeekBar = previewSeekBarLayout.getSeekBar();
        this.previewView = previewSeekBarLayout.getPreviewFrameLayout();
        this.morphView = previewSeekBarLayout.getMorphView();
        this.frameView = previewSeekBarLayout.getFrameView();
    }

    public void move() {
        previewView.setX(getPreviewX());
        morphView.setX(getPreviewCenterX(morphView.getWidth()));
    }

    public abstract void show();

    public abstract void hide();

    float getWidthOffset(int progress) {
        return (float) progress / previewSeekBar.getMax();
    }

    float getPreviewCenterX(int width) {
        return (previewSeekBarLayout.getWidth() - previewView.getWidth())
                * getWidthOffset(previewSeekBar.getProgress()) + previewView.getWidth() / 2f
                - width / 2f;
    }

    float getPreviewX() {
        return ((float) (previewSeekBarLayout.getWidth() - previewView.getWidth()))
                * getWidthOffset(previewSeekBar.getProgress());
    }

    float getHideY() {
        return previewSeekBar.getY() + previewSeekBar.getThumbOffset();
    }

    float getShowY(){
       return (int) (previewView.getY() + previewView.getHeight() / 2f);
    }
}
