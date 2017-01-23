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


    public PreviewAnimatorImpl(PreviewSeekBarLayout previewSeekBarLayout) {
        super(previewSeekBarLayout);
    }


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

    @Override
    public void show() {
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

    @Override
    public void hide() {
        frameView.setAlpha(0f);
        frameView.setVisibility(View.VISIBLE);
        previewView.setVisibility(View.VISIBLE);
        startUnreveal();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void startReveal() {
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
    private void startUnreveal() {
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
}
