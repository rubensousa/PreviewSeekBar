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

package com.github.rubensousa.previewseekbar.animator;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.view.View;
import android.widget.FrameLayout;

import com.github.rubensousa.previewseekbar.PreviewBar;

/**
 * A {@link PreviewAnimator} that just fades the preview frame
 */
public class PreviewFadeAnimator implements PreviewAnimator {

    private static final int FADE_DURATION = 350;

    private long showDuration;
    private long hideDuration;

    public PreviewFadeAnimator() {
        this(FADE_DURATION, FADE_DURATION);
    }

    public PreviewFadeAnimator(long showDuration, long hideDuration) {
        this.showDuration = showDuration;
        this.hideDuration = hideDuration;
    }

    @Override
    public void move(FrameLayout previewView, PreviewBar previewBar) {

    }

    @Override
    public void show(FrameLayout previewView, PreviewBar previewBar) {
        previewView.animate().setListener(null);
        previewView.animate().cancel();
        previewView.setAlpha(0f);
        previewView.setVisibility(View.VISIBLE);
        previewView.animate()
                .setDuration(showDuration)
                .alpha(1f);
    }

    @Override
    public void hide(final FrameLayout previewView, PreviewBar previewBar) {
        previewView.animate().setListener(null);
        previewView.animate().cancel();
        previewView.setVisibility(View.VISIBLE);
        previewView.setAlpha(1f);
        previewView.animate()
                .setDuration(hideDuration)
                .alpha(0f)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        previewView.setAlpha(1.0f);
                        previewView.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        super.onAnimationCancel(animation);
                        previewView.setAlpha(1.0f);
                        previewView.setVisibility(View.INVISIBLE);
                    }
                });
    }

    @Override
    public void cancel(FrameLayout previewView, PreviewBar previewBar) {
        previewView.animate().setListener(null);
        previewView.animate().cancel();
    }

}
