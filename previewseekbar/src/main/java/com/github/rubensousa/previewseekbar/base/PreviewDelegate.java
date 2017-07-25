package com.github.rubensousa.previewseekbar.base;


import android.os.Build;
import android.view.View;

class PreviewDelegate implements PreviewView.OnPreviewChangeListener {

    private PreviewLayout previewLayout;
    private PreviewAnimator animator;
    private boolean showing;
    private boolean startTouch;
    private boolean setup;

    public PreviewDelegate(PreviewLayout previewLayout) {
        this.previewLayout = previewLayout;
    }

    public void setup() {
        previewLayout.getPreviewFrameLayout().setVisibility(View.INVISIBLE);
        previewLayout.getMorphView().setVisibility(View.INVISIBLE);
        previewLayout.getFrameView().setVisibility(View.INVISIBLE);
        previewLayout.getPreviewView().addOnPreviewChangeListener(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            this.animator = new PreviewAnimatorLollipopImpl(previewLayout);
        } else {
            this.animator = new PreviewAnimatorImpl(previewLayout);
        }
        setup = true;
    }

    public boolean isShowing() {
        return showing;
    }

    public void show() {
        if (!showing && setup) {
            animator.show();
            showing = true;
        }
    }

    public void hide() {
        if (showing) {
            animator.hide();
            showing = false;
        }
    }

    @Override
    public void onStartPreview(PreviewView previewView) {
        startTouch = true;
    }

    @Override
    public void onStopPreview(PreviewView previewView) {
        if (showing) {
            animator.hide();
        }
        showing = false;
        startTouch = false;
    }

    @Override
    public void onPreview(PreviewView previewView, int progress, boolean fromUser) {
        if (setup) {
            animator.move();
            if (!showing && !startTouch && fromUser) {
                animator.show();
                showing = true;
            }
        }
        startTouch = false;
    }
}
