/*
 * Copyright 2017 Rúben Sousa
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
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

class PreviewAnimatorImpl extends PreviewAnimator {

    public static final int ALPHA_DURATION = 200;

    private AnimatorListenerAdapter hideListener = new AnimatorListenerAdapter() {
        @Override
        public void onAnimationEnd(Animator animation) {
            super.onAnimationEnd(animation);
            previewFrameLayout.setVisibility(View.INVISIBLE);
        }
    };

    public PreviewAnimatorImpl(ViewGroup parent, PreviewView previewView, View morphView,
                               FrameLayout previewFrameLayout, View previewFrameView) {
        super(parent, previewView, morphView, previewFrameLayout, previewFrameView);
    }

    @Override
    public void move() {
        previewFrameLayout.setX(getFrameX());
    }

    @Override
    public void show() {
        move();
        previewFrameLayout.setVisibility(View.VISIBLE);
        previewFrameLayout.setAlpha(0f);
        previewFrameLayout.animate().cancel();
        previewFrameLayout.animate()
                .setDuration(ALPHA_DURATION)
                .alpha(1f)
                .setListener(null);
    }

    @Override
    public void hide() {
        previewFrameLayout.setAlpha(1f);
        previewFrameLayout.animate().cancel();
        previewFrameLayout.animate()
                .setDuration(ALPHA_DURATION)
                .alpha(0f)
                .setListener(hideListener);
    }

}
