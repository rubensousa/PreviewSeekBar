package com.github.rubensousa.previewseekbar.base;


import android.view.View;
import android.view.ViewGroup;

import com.github.rubensousa.previewseekbar.R;

abstract class PreviewAnimator {

    static final int MORPH_REVEAL_DURATION = 200;
    static final int MORPH_MOVE_DURATION = 150;
    static final int UNMORPH_MOVE_DURATION = 150;
    static final int UNMORPH_UNREVEAL_DURATION = 200;

    PreviewView previewView;
    PreviewLayout previewLayout;
    View previewChildView;
    View frameView;
    View morphView;
    ViewGroup parentLayout;

    PreviewAnimator(PreviewLayout previewLayout) {
        this.previewLayout = previewLayout;
        this.previewView = this.previewLayout.getPreviewView();
        this.parentLayout = (ViewGroup) ((View) this.previewLayout).getParent();
        this.previewChildView = this.previewLayout.getPreviewFrameLayout();
        this.morphView = this.previewLayout.getMorphView();
        this.frameView = this.previewLayout.getFrameView();
    }

    void move() {
        previewChildView.setX(getPreviewX());
        morphView.setX(getPreviewX());
    }

    public abstract void show();

    public abstract void hide();

    float getPreviewCenterX(int width) {
        float offset = getWidthOffset(previewView.getProgress());
        float startX = ((View) previewView).getX() - previewChildView.getWidth() / 2f;
        float endX = startX + ((View) previewView).getWidth();
        float ltr = (endX - startX) * offset;
        float rtl = (endX - startX) * (1 - offset);
        return ltr;
      /*  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return ((View) previewView).getLayoutDirection() == View.LAYOUT_DIRECTION_LTR ?
                    ltr : rtl;
        } else {
            return ltr;
        }*/
    }

    float getPreviewX() {
        float offset = getWidthOffset(previewView.getProgress());
        float startX = previewChildView.getResources().getDimensionPixelOffset(R.dimen.previewseekbar_indicator_width);
        float endX = parentLayout.getWidth() - startX;
        float ltr = startX;// (endX - startX) * offset;
        float rtl = (endX - startX) * (1 - offset);

        return ltr;
       /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return ((View) previewView).getLayoutDirection() == View.LAYOUT_DIRECTION_LTR ?
                    ltr : rtl;
        } else {
            return ltr;
        }*/
    }

    float getHideY() {
        return ((View) previewView).getY() + previewView.getThumbOffset();
    }

    float getShowY() {
        return (int) (previewChildView.getY() + previewChildView.getHeight() / 2f);
    }

    private float getWidthOffset(int progress) {
        return (float) progress / previewView.getMax();
    }
}
