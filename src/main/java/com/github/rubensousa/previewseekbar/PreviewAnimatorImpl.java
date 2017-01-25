package com.github.rubensousa.previewseekbar;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.os.Build;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

class PreviewAnimatorImpl extends PreviewAnimator {

    private Animator.AnimatorListener showListener = new AnimatorListenerAdapter() {
        @Override
        public void onAnimationEnd(Animator animation) {
            super.onAnimationEnd(animation);
            morphView.animate().setListener(null);
            startReveal();
        }
    };

    private Animator.AnimatorListener hideListener = new AnimatorListenerAdapter() {
        @Override
        public void onAnimationEnd(Animator animation) {
            super.onAnimationEnd(animation);
            morphView.setVisibility(View.INVISIBLE);
            morphView.animate().setListener(null);
        }
    };

    public PreviewAnimatorImpl(PreviewSeekBarLayout previewSeekBarLayout) {
        super(previewSeekBarLayout);
    }

    @Override
    public void show() {
        previewView.setScaleX(getScaleXStart());
        previewView.setScaleY(getScaleYStart());
        morphView.setX(getPreviewCenterX(morphView.getWidth()));
        morphView.setY(previewSeekBar.getY());
        morphView.setVisibility(View.VISIBLE);
        morphView.animate()
                .y(getShowY())
                .scaleY(4.0f)
                .scaleX(4.0f)
                .setDuration(MORPH_MOVE_DURATION)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .setListener(showListener);
    }

    @Override
    public void hide() {
        frameView.setVisibility(View.VISIBLE);
        previewView.setVisibility(View.VISIBLE);
        morphView.setY(getShowY());
        morphView.setScaleX(4.0f);
        morphView.setScaleY(4.0f);
        morphView.setVisibility(View.INVISIBLE);
        startUnreveal();
    }

    private void startReveal() {
        previewView.animate()
                .setInterpolator(new AccelerateInterpolator())
                .setDuration(MORPH_REVEAL_DURATION)
                .scaleX(1)
                .scaleY(1)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        super.onAnimationStart(animation);
                        frameView.setAlpha(1f);
                        previewView.setVisibility(View.VISIBLE);
                        frameView.setVisibility(View.VISIBLE);
                        morphView.setVisibility(View.INVISIBLE);
                        frameView.animate().alpha(0f).setDuration(MORPH_REVEAL_DURATION);
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        previewView.animate().setListener(null);
                        previewView.animate().setListener(null);
                        frameView.setVisibility(View.INVISIBLE);
                    }
                });
    }

    private void startUnreveal() {
        frameView.animate().alpha(1f).setDuration(UNMORPH_UNREVEAL_DURATION)
                .setInterpolator(new AccelerateInterpolator());

        previewView.animate()
                .setDuration(UNMORPH_UNREVEAL_DURATION)
                .setInterpolator(new AccelerateInterpolator())
                .scaleX(getScaleXStart())
                .scaleY(getScaleYStart())
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        previewView.animate().setListener(null);
                        frameView.setVisibility(View.INVISIBLE);
                        previewView.setVisibility(View.INVISIBLE);
                        morphView.setVisibility(View.VISIBLE);
                        morphView.animate()
                                .y(getHideY())
                                .scaleY(0.5f)
                                .scaleX(0.5f)
                                .setDuration(UNMORPH_MOVE_DURATION)
                                .setInterpolator(new AccelerateInterpolator())
                                .setListener(hideListener);
                    }
                });
    }

    private float getScaleXStart() {
        return morphView.getWidth() / previewView.getWidth();
    }

    private float getScaleYStart() {
        return (morphView.getWidth() * 2) / previewView.getWidth();
    }

}
