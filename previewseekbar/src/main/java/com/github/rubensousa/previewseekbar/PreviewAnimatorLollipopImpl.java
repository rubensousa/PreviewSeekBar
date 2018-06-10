/*
 * Copyright 2018 Rúben Sousa
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.rubensousa.previewseekbar;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.os.Build;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.widget.FrameLayout;

class PreviewAnimatorLollipopImpl extends PreviewAnimator {

    static final int MORPH_REVEAL_DURATION = 150;
    static final int MORPH_MOVE_DURATION = 200;
    static final int UNMORPH_MOVE_DURATION = 200;
    static final int UNMORPH_UNREVEAL_DURATION = 150;

    private Animator.AnimatorListener showListener = new AnimatorListenerAdapter() {
        @Override
        public void onAnimationEnd(Animator animation) {
            super.onAnimationEnd(animation);
            morphView.animate().setListener(null);
            startReveal();
            mShowing = false;
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

    PreviewAnimatorLollipopImpl(ViewGroup parent, PreviewView previewView, View morphView,
                                FrameLayout previewFrameLayout, View previewFrameView) {
        super(parent, previewView, morphView, previewFrameLayout, previewFrameView);
    }

    private boolean mShowing;

    @Override
    public void move() {
        previewFrameLayout.setX(getFrameX());
        morphView.animate().x(mShowing ? getMorphEndX() : getMorphStartX());
    }

    @Override
    public void show() {
        mShowing = true;
        move();
        previewFrameLayout.setVisibility(View.INVISIBLE);
        previewFrameView.setVisibility(View.INVISIBLE);
        morphView.setY(((View) previewView).getY());
        morphView.setX(getMorphStartX());
        morphView.setScaleX(0f);
        morphView.setScaleY(0f);
        morphView.setVisibility(View.VISIBLE);
        morphView.animate()
                .x(getMorphEndX())
                .y(getMorphEndY())
                .scaleY(4.0f)
                .scaleX(4.0f)
                .setDuration(MORPH_MOVE_DURATION)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .setListener(showListener);
    }

    @Override
    public void hide() {
        mShowing = false;
        previewFrameView.setVisibility(View.VISIBLE);
        previewFrameLayout.setVisibility(View.VISIBLE);
        morphView.setX(getMorphEndX());
        morphView.setY(getMorphEndY());
        morphView.setScaleX(4.0f);
        morphView.setScaleY(4.0f);
        morphView.setVisibility(View.INVISIBLE);
        morphView.animate().cancel();
        previewFrameLayout.animate().cancel();
        startUnreveal();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void startReveal() {
        Animator animator = ViewAnimationUtils.createCircularReveal(previewFrameLayout,
                getCenterX(previewFrameLayout),
                getCenterY(previewFrameLayout),
                morphView.getWidth() * 2,
                getRadius(previewFrameLayout));

        animator.setTarget(previewFrameLayout);
        animator.setDuration(MORPH_REVEAL_DURATION);
        animator.setInterpolator(new AccelerateInterpolator());
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                previewFrameView.setAlpha(1f);
                previewFrameLayout.setVisibility(View.VISIBLE);
                previewFrameView.setVisibility(View.VISIBLE);
                morphView.setVisibility(View.INVISIBLE);
                previewFrameView.animate()
                        .alpha(0f)
                        .setDuration(MORPH_REVEAL_DURATION);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                previewFrameLayout.animate().setListener(null);
                previewFrameView.setVisibility(View.INVISIBLE);
            }

        });

        animator.start();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void startUnreveal() {
        Animator animator = ViewAnimationUtils.createCircularReveal(previewFrameLayout,
                getCenterX(previewFrameLayout),
                getCenterY(previewFrameLayout),
                getRadius(previewFrameLayout), morphView.getWidth() * 2);
        animator.setTarget(previewFrameLayout);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                previewFrameLayout.animate().setListener(null);
                previewFrameView.setVisibility(View.INVISIBLE);
                previewFrameLayout.setVisibility(View.INVISIBLE);
                morphView.setVisibility(View.VISIBLE);
                morphView.setX(getMorphEndX());
                morphView.animate()
                        .x(getMorphStartX())
                        .y(getMorphStartY())
                        .scaleY(0f)
                        .scaleX(0f)
                        .setDuration(UNMORPH_MOVE_DURATION)
                        .setInterpolator(new AccelerateInterpolator())
                        .setListener(hideListener);
            }
        });
        previewFrameView.animate().alpha(1f).setDuration(UNMORPH_UNREVEAL_DURATION)
                .setInterpolator(new AccelerateInterpolator());
        animator.setDuration(UNMORPH_UNREVEAL_DURATION)
                .setInterpolator(new AccelerateInterpolator());
        animator.start();
    }

    private int getRadius(View view) {
        return (int) Math.hypot(view.getWidth() / 2, view.getHeight() / 2);
    }

    private int getCenterX(View view) {
        return view.getWidth() / 2;
    }

    private int getCenterY(View view) {
        return view.getHeight() / 2;
    }

    /**
     * Get the x position for the view that'll morph into the preview FrameLayout
     */
    private float getMorphStartX() {
        float startX = getPreviewViewStartX() + previewView.getThumbOffset();
        float endX = getPreviewViewEndX() - previewView.getThumbOffset();
        float nextX = (endX - startX) * getWidthOffset(previewView.getProgress())
                + startX - previewView.getThumbOffset();
        return nextX;
    }

    private float getMorphEndX() {
        return getFrameX() + previewFrameLayout.getWidth() / 2f - previewView.getThumbOffset();
    }

    private float getMorphStartY() {
        return ((View) previewView).getY() + previewView.getThumbOffset();
    }

    private float getMorphEndY() {
        return (int) (previewFrameLayout.getY() + previewFrameLayout.getHeight() / 2f);
    }

}
