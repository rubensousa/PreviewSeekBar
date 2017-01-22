package com.github.rubensousa.previewseekbar;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.os.Build;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateInterpolator;

class PreviewAnimator {

    private static final int MORPH_REVEAL_DURATION = 2000;
    private static final int MORPH_MOVE_DURATION = 200;
    private static final int UNMORPH_MOVE_DURATION = 200;
    private static final int UNMORPH_UNREVEAL_DURATION = 2000;

    private PreviewSeekBar previewSeekBar;
    private View previewView;
    private View previewParentView;
    private View morphView;
    private Animator.AnimatorListener morphListener = new AnimatorListenerAdapter() {
        @Override
        public void onAnimationEnd(Animator animation) {
            super.onAnimationEnd(animation);
            morphView.setVisibility(View.INVISIBLE);
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

    public PreviewAnimator(PreviewSeekBar previewSeekBar) {
        this.previewSeekBar = previewSeekBar;
    }

    public void setPreviewView(View previewView) {
        this.previewView = previewView;
        this.previewParentView = (View) previewView.getParent();
    }

    public void setMorphView(View morphView) {
        this.morphView = morphView;
    }

    public void move() {
        previewView.setX(getPreviewX());
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
                .setInterpolator(new AccelerateInterpolator())
                .setListener(morphListener);
    }

    public void unmorph() {
        previewView.setVisibility(View.INVISIBLE);
        startUnreveal();
    }

    void startReveal() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            startRevealLollipop();
            return;
        }
    }

    void startUnreveal() {
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
                morphView.getWidth(),
                PreviewSeekbarUtils.getRadius(previewView));

        animation.setDuration(MORPH_REVEAL_DURATION).setInterpolator(new AccelerateInterpolator());
        previewView.setVisibility(View.VISIBLE);
        animation.start();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void startUnrevealLollipop() {
        Animator animation = ViewAnimationUtils.createCircularReveal(previewView,
                PreviewSeekbarUtils.getCenterX(previewView),
                PreviewSeekbarUtils.getCenterY(previewView),
                PreviewSeekbarUtils.getRadius(previewView), morphView.getWidth());

        animation.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                previewView.setVisibility(View.INVISIBLE);
                morphView.setVisibility(View.VISIBLE);
                morphView.animate()
                        .y(previewSeekBar.getY())
                        .scaleY(1.0f)
                        .scaleX(1.0f)
                        .setDuration(UNMORPH_MOVE_DURATION)
                        .setInterpolator(new AccelerateInterpolator())
                        .setListener(unmorphListener);
            }
        });
        animation.setDuration(UNMORPH_UNREVEAL_DURATION).setInterpolator(new AccelerateInterpolator());
        animation.start();
    }


    private float getWidthOffset(int progress) {
        return (float) progress / previewSeekBar.getMax();
    }

    private float getPreviewCenterX(int width) {
        return (previewParentView.getWidth() - previewView.getWidth())
                * getWidthOffset(previewSeekBar.getProgress()) + previewView.getWidth() / 2f
                - width / 2f;
    }

    private float getPreviewX() {
        return ((float) (previewParentView.getWidth() - previewView.getWidth()))
                * getWidthOffset(previewSeekBar.getProgress());
    }
}
