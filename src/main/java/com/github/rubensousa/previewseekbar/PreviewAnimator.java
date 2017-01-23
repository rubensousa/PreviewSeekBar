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

class PreviewAnimator {

    private static final int MORPH_REVEAL_DURATION = 250;
    private static final int MORPH_MOVE_DURATION = 200;
    private static final int UNMORPH_MOVE_DURATION = 200;
    private static final int UNMORPH_UNREVEAL_DURATION = 250;

    private PreviewSeekBar previewSeekBar;
    private PreviewSeekBarLayout previewSeekBarLayout;
    private View previewView;
    private View frameView;
    private View morphView;
    private Animator.AnimatorListener morphListener = new AnimatorListenerAdapter() {
        @Override
        public void onAnimationEnd(Animator animation) {
            super.onAnimationEnd(animation);
            startReveal();
            morphView.animate().setListener(null);
        }
    };
    private Animator.AnimatorListener unmorphListener = new AnimatorListenerAdapter() {
        @Override
        public void onAnimationEnd(Animator animation) {
            super.onAnimationEnd(animation);
            morphView.setVisibility(View.INVISIBLE);
            morphView.animate().setListener(null);
        }
    };

    public PreviewAnimator(PreviewSeekBarLayout previewSeekBarLayout, PreviewSeekBar previewSeekBar,
                           View previewView, View morphView, View frameView) {
        this.previewSeekBarLayout = previewSeekBarLayout;
        this.previewSeekBar = previewSeekBar;
        this.previewView = previewView;
        this.morphView = morphView;
        this.frameView = frameView;
    }

    public void move() {
        previewView.setX(getPreviewX());
        frameView.setX(previewView.getX());
        morphView.setX(getPreviewCenterX(morphView.getWidth()));
    }

    public void morph() {
        morphView.setX(getPreviewCenterX(morphView.getWidth()));
        morphView.setY(previewSeekBar.getY());
        morphView.setVisibility(View.VISIBLE);

        int endY = (int) (previewView.getY() + previewView.getHeight() / 2f);
        morphView.animate()
                .y(endY)
                .scaleY(4.0f)
                .scaleX(4.0f)
                .setDuration(MORPH_MOVE_DURATION)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .setListener(morphListener);
    }

    public void unmorph() {
        startUnreveal();
    }

    void startReveal() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            startRevealLollipop();
            return;
        }
    }

    void startUnreveal() {
        frameView.setAlpha(0f);
        frameView.setVisibility(View.VISIBLE);
        previewView.setVisibility(View.VISIBLE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            startUnrevealLollipop();
            return;
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void startRevealLollipop() {
        Animator animation = ViewAnimationUtils.createCircularReveal(previewView,
                PreviewSeekbarUtils.getCenterX(previewView),
                PreviewSeekbarUtils.getCenterY(previewView),
                morphView.getWidth() * 2,
                PreviewSeekbarUtils.getRadius(previewView));

        animation.setTarget(previewView);
        animation.setDuration(MORPH_REVEAL_DURATION);
        animation.setInterpolator(new AccelerateInterpolator());
        animation.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                previewView.setVisibility(View.VISIBLE);
                frameView.setVisibility(View.VISIBLE);
                morphView.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                previewView.setVisibility(View.VISIBLE);
                frameView.setVisibility(View.INVISIBLE);
            }

        });

        animation.start();
        frameView.animate().alpha(0f).setDuration(MORPH_REVEAL_DURATION);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void startUnrevealLollipop() {
        Animator animation = ViewAnimationUtils.createCircularReveal(previewView,
                PreviewSeekbarUtils.getCenterX(previewView),
                PreviewSeekbarUtils.getCenterY(previewView),
                PreviewSeekbarUtils.getRadius(previewView), morphView.getWidth());
        animation.setTarget(previewView);
        animation.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                frameView.setVisibility(View.INVISIBLE);
                previewView.setVisibility(View.INVISIBLE);
                morphView.setVisibility(View.VISIBLE);
                morphView.animate()
                        .y(previewSeekBar.getY())
                        .scaleY(0.5f)
                        .scaleX(0.5f)
                        .setDuration(UNMORPH_MOVE_DURATION)
                        .setInterpolator(new DecelerateInterpolator())
                        .setListener(unmorphListener);
            }
        });
        frameView.animate().alpha(1f).setDuration(UNMORPH_UNREVEAL_DURATION);
        animation.setDuration(UNMORPH_UNREVEAL_DURATION).setInterpolator(new AccelerateInterpolator());
        animation.start();
    }


    private float getWidthOffset(int progress) {
        return (float) progress / previewSeekBar.getMax();
    }

    private float getPreviewCenterX(int width) {
        return (previewSeekBarLayout.getWidth() - previewView.getWidth())
                * getWidthOffset(previewSeekBar.getProgress()) + previewView.getWidth() / 2f
                - width / 2f;
    }

    private float getPreviewX() {
        return ((float) (previewSeekBarLayout.getWidth() - previewView.getWidth()))
                * getWidthOffset(previewSeekBar.getProgress());
    }
}
