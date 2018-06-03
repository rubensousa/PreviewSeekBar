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

import com.github.rubensousa.previewseekbar.base.PreviewView;

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

    public PreviewAnimatorLollipopImpl(ViewGroup parent, PreviewView previewView, View morphView,
                                       FrameLayout previewFrameLayout, View previewFrameView) {
        super(parent, previewView, morphView, previewFrameLayout, previewFrameView);
    }

    @Override
    public void show() {
        previewFrameLayout.setVisibility(View.INVISIBLE);
        previewFrameView.setVisibility(View.INVISIBLE);
        morphView.setX(getMorphX());
        morphView.setY(((View) previewView).getY());
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
        previewFrameView.setVisibility(View.VISIBLE);
        previewFrameLayout.setVisibility(View.VISIBLE);
        morphView.setY(getShowY());
        morphView.setScaleX(4.0f);
        morphView.setScaleY(4.0f);
        morphView.setVisibility(View.INVISIBLE);
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
                previewFrameView.animate().alpha(0f).setDuration(MORPH_REVEAL_DURATION);
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
                morphView.animate()
                        .y(getHideY())
                        .scaleY(0.5f)
                        .scaleX(0.5f)
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
}
