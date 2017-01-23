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

class PreviewAnimatorLollipopImpl extends PreviewAnimator {

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

    private Animator animator;

    public PreviewAnimatorLollipopImpl(PreviewSeekBarLayout previewSeekBarLayout) {
        super(previewSeekBarLayout);
    }

    @Override
    public void cancel() {
        morphView.animate().cancel();
        previewView.animate().cancel();
        frameView.animate().cancel();
        if (animator != null) {
            animator.cancel();
        }
    }

    @Override
    public void show() {
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
        frameView.setAlpha(1f);
        frameView.setVisibility(View.VISIBLE);
        previewView.setVisibility(View.VISIBLE);
        morphView.setY(getShowY());
        morphView.setScaleX(4.0f);
        morphView.setScaleY(4.0f);
        morphView.setVisibility(View.INVISIBLE);
        startUnreveal();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void startReveal() {
        animator = ViewAnimationUtils.createCircularReveal(previewView,
                PreviewSeekbarUtils.getCenterX(previewView),
                PreviewSeekbarUtils.getCenterY(previewView),
                morphView.getWidth() * 2,
                PreviewSeekbarUtils.getRadius(previewView));

        animator.setTarget(previewView);
        animator.setDuration(MORPH_REVEAL_DURATION);
        animator.setInterpolator(new AccelerateInterpolator());
        animator.addListener(new AnimatorListenerAdapter() {
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
                frameView.setVisibility(View.INVISIBLE);
            }

        });

        animator.start();

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void startUnreveal() {

        animator = ViewAnimationUtils.createCircularReveal(previewView,
                PreviewSeekbarUtils.getCenterX(previewView),
                PreviewSeekbarUtils.getCenterY(previewView),
                PreviewSeekbarUtils.getRadius(previewView), morphView.getWidth());
        animator.setTarget(previewView);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                previewView.animate().setListener(null);
                frameView.setVisibility(View.INVISIBLE);
                previewView.setVisibility(View.INVISIBLE);
                morphView.setVisibility(View.VISIBLE);
                morphView.animate()
                        .setStartDelay(50)
                        .y(getHideY())
                        .scaleY(0.5f)
                        .scaleX(0.5f)
                        .setDuration(UNMORPH_MOVE_DURATION)
                        .setInterpolator(new DecelerateInterpolator())
                        .setListener(hideListener);
            }
        });
        animator.setDuration(UNMORPH_UNREVEAL_DURATION).setInterpolator(new AccelerateInterpolator());
        animator.start();
    }
}
