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
import android.animation.AnimatorSet;
import android.annotation.TargetApi;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;

import androidx.core.graphics.drawable.DrawableCompat;

/**
 * A {@link PreviewAnimator} that morphs the PreviewView's scrubber
 * into the frame that holds the preview
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class PreviewMorphAnimator implements PreviewAnimator {

    private static final int MORPH_SHOW_DURATION = 5000;
    private static final int MORPH_HIDE_DURATION = 5000;
    private static final int TRANSLATION_SHOW_DURATION = 5000;
    private static final int TRANSLATION_HIDE_DURATION = 5000;

    private long showTranslationDuration;
    private long morphShowDuration;
    private long hideTranslationDuration;
    private long morphHideDuration;
    private boolean isShowing;
    private boolean isHiding;
    private Animator animator;

    public PreviewMorphAnimator() {
        this(TRANSLATION_SHOW_DURATION, MORPH_SHOW_DURATION, MORPH_HIDE_DURATION,
                TRANSLATION_HIDE_DURATION);
    }

    public PreviewMorphAnimator(long showTranslationDuration,
                                long morphShowDuration,
                                long morphHideDuration,
                                long hideTranslationDuration) {
        this.showTranslationDuration = showTranslationDuration;
        this.morphShowDuration = morphShowDuration;
        this.morphHideDuration = morphHideDuration;
        this.hideTranslationDuration = hideTranslationDuration;
    }

    @Override
    public void move(FrameLayout previewFrameLayout, PreviewView previewView) {
        if (!isShowing && !isHiding) {
            return;
        }
        final View morphView = getOrCreateMorphView(previewFrameLayout, previewView);
        final float offset = getOffset(previewView);
        final ViewPropertyAnimator animator;
        if (isShowing) {
            animator = morphView.animate().x(getMorphEndX(previewFrameLayout, previewView));
        } else {
            animator = morphView.animate().x(getMorphStartX(previewView, offset));
        }
        animator.start();
    }

    @Override
    public void show(final FrameLayout previewFrameLayout, final PreviewView previewView) {
        if (previewView.getMax() == 0) {
            return;
        }
        isHiding = false;
        isShowing = true;
        move(previewFrameLayout, previewView);
        final float offset = getOffset(previewView);

        previewFrameLayout.setVisibility(View.INVISIBLE);

        final View frameView = getOrCreateOverlayView(previewFrameLayout);
        frameView.setVisibility(View.INVISIBLE);

        final View morphView = getOrCreateMorphView(previewFrameLayout, previewView);
        morphView.setVisibility(View.VISIBLE);

        tintViews(previewView, morphView, frameView);
        cancelPendingAnimations(previewFrameLayout, frameView, morphView);

        morphView.setY(getMorphStartY(previewView));
        morphView.setX(getMorphStartX(previewView, offset));
        final float targetScale = getMorphScale(previewFrameLayout, morphView);
        final AnimatorSet animatorSet = new AnimatorSet();
        morphView.setScaleX(0f);
        morphView.setScaleY(0f);
        morphView.setAlpha(1.0f);
        morphView.animate()
                .x(getMorphEndX(previewFrameLayout, previewView))
                .y(getMorphEndY(previewFrameLayout, morphView))
                .scaleY(targetScale)
                .scaleX(targetScale)
                .setDuration(showTranslationDuration)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        morphView.animate().setListener(null);
                        startReveal(previewFrameLayout, frameView, morphView);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        super.onAnimationCancel(animation);
                        morphView.animate().setListener(null);
                        isShowing = false;
                    }
                });
    }

    @Override
    public void hide(FrameLayout previewFrameLayout, PreviewView previewView) {
        isShowing = false;
        isHiding = true;
        final View overlayView = getOrCreateOverlayView(previewFrameLayout);

        overlayView.setVisibility(View.VISIBLE);
        previewFrameLayout.setVisibility(View.VISIBLE);

        final View morphView = getOrCreateMorphView(previewFrameLayout, previewView);

        tintViews(previewView, morphView, overlayView);

        cancelPendingAnimations(previewFrameLayout, overlayView, morphView);

        final float targetScale = getMorphScale(previewFrameLayout, morphView);
        morphView.setX(getMorphEndX(previewFrameLayout, previewView));
        morphView.setY(getMorphEndY(previewFrameLayout, morphView));
        morphView.setScaleX(targetScale);
        morphView.setScaleY(targetScale);
        morphView.setVisibility(View.INVISIBLE);

        final float offset = (float) previewView.getProgress() / previewView.getMax();
        startUnreveal(previewFrameLayout, previewView, overlayView, morphView, offset);
    }

    private void tintViews(PreviewView previewView,
                           View morphView,
                           View frameView) {
        int color = previewView.getScrubberColor();
        if (morphView.getBackgroundTintList() == null
                || morphView.getBackgroundTintList().getDefaultColor() != color) {
            Drawable drawable = DrawableCompat.wrap(morphView.getBackground());
            DrawableCompat.setTint(drawable, color);
            morphView.setBackground(drawable);
            frameView.setBackgroundColor(color);
        }
    }

    private View getOrCreateOverlayView(FrameLayout previewFrameLayout) {
        View overlay = previewFrameLayout.findViewById(R.id.previewSeekBarOverlayViewId);

        if (overlay != null) {
            return overlay;
        }

        // Create frame view for the circular reveal
        overlay = new View(previewFrameLayout.getContext());
        overlay.setVisibility(View.INVISIBLE);
        overlay.setId(R.id.previewSeekBarOverlayViewId);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        previewFrameLayout.addView(overlay, layoutParams);
        return overlay;
    }

    @SuppressWarnings("SuspiciousNameCombination")
    private View getOrCreateMorphView(FrameLayout previewFrameLayout,
                                      PreviewView previewView) {

        final ViewGroup parent = (ViewGroup) previewFrameLayout.getParent();
        View morphView = parent.findViewById(R.id.previewSeekBarMorphViewId);

        if (morphView != null) {
            return morphView;
        }

        // Create morph view
        morphView = new View(previewFrameLayout.getContext());
        morphView.setVisibility(View.INVISIBLE);
        morphView.setBackgroundResource(R.drawable.previewseekbar_morph);
        morphView.setId(R.id.previewSeekBarMorphViewId);

        // Setup morph view
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(
                previewView.getThumbOffset(), previewView.getThumbOffset());

        // Add the morph view to the parent so we can move it from the SeekBar
        // to the preview frame without worrying about the view bounds
        parent.addView(morphView, layoutParams);
        return morphView;
    }

    private void startReveal(final FrameLayout previewFrameLayout,
                             final View previewFrameView,
                             final View morphView) {
        animator = ViewAnimationUtils.createCircularReveal(previewFrameLayout,
                getCenterX(previewFrameLayout),
                getCenterY(previewFrameLayout),
                (float) previewFrameLayout.getHeight() / 2,
                getRadius(previewFrameLayout));

        animator.setTarget(previewFrameLayout);
        animator.setDuration(morphShowDuration);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                previewFrameView.animate().cancel();
                previewFrameView.setAlpha(0.0f);
                previewFrameLayout.animate().setListener(null);
                previewFrameView.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                super.onAnimationCancel(animation);
                previewFrameView.setAlpha(0.0f);
                previewFrameLayout.animate().setListener(null);
                previewFrameView.setVisibility(View.INVISIBLE);
            }
        });
        previewFrameView.setAlpha(1f);
        previewFrameLayout.setVisibility(View.VISIBLE);
        previewFrameView.setVisibility(View.VISIBLE);
        morphView.setVisibility(View.INVISIBLE);
        previewFrameView.animate()
                .alpha(0f)
                .setDuration(morphShowDuration);
        animator.start();
    }

    private void startUnreveal(final FrameLayout previewFrameLayout,
                               final PreviewView previewView,
                               final View previewOverlay,
                               final View morphView,
                               final float offset) {
        animator = ViewAnimationUtils.createCircularReveal(previewFrameLayout,
                getCenterX(previewFrameLayout),
                getCenterY(previewFrameLayout),
                getRadius(previewFrameLayout),
                (float) previewFrameLayout.getHeight() / 2);
        animator.setTarget(previewFrameLayout);
        final Animator.AnimatorListener morphFinishListener = new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                isHiding = false;
                morphView.setVisibility(View.INVISIBLE);
                morphView.animate().setListener(null);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                super.onAnimationCancel(animation);
                isHiding = false;
                morphView.setVisibility(View.INVISIBLE);
                morphView.animate().setListener(null);
            }
        };
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                previewFrameLayout.animate().setListener(null);
                previewOverlay.setVisibility(View.INVISIBLE);
                previewFrameLayout.setVisibility(View.INVISIBLE);
                morphView.setVisibility(View.VISIBLE);
                morphView.setX(getMorphEndX(previewFrameLayout, previewView));
                morphView.animate()
                        .x(getMorphStartX(previewView, offset))
                        .y(getMorphStartY(previewView))
                        .scaleY(0f)
                        .scaleX(0f)
                        .setDuration(hideTranslationDuration)
                        .setInterpolator(new AccelerateInterpolator())
                        .setListener(morphFinishListener);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                super.onAnimationCancel(animation);
                isHiding = false;
                previewFrameLayout.animate().setListener(null);
                previewOverlay.setVisibility(View.INVISIBLE);
                previewFrameLayout.setVisibility(View.INVISIBLE);
                morphView.setVisibility(View.INVISIBLE);
                morphView.setX(getMorphStartX(previewView, offset));
                morphView.setY(getMorphStartY(previewView));
                morphView.setScaleX(0f);
                morphView.setScaleX(0f);
            }
        });
        previewOverlay.setVisibility(View.VISIBLE);
        previewOverlay.setAlpha(0.0f);
        previewOverlay.animate().alpha(1f).setDuration(morphHideDuration)
                .setInterpolator(new AccelerateInterpolator());
        animator.setDuration(morphHideDuration)
                .setInterpolator(new AccelerateInterpolator());
        animator.start();
    }

    private void cancelPendingAnimations(FrameLayout previewFrameLayout,
                                         View overlayView,
                                         View morphView) {
        if (animator != null) {
            animator.removeAllListeners();
            animator.cancel();
            animator = null;
        }
        previewFrameLayout.animate().setListener(null);
        previewFrameLayout.animate().cancel();
        previewFrameLayout.animate().setListener(null);
        overlayView.animate().cancel();
        morphView.animate().setListener(null);
        morphView.animate().cancel();
    }

    private float getOffset(PreviewView previewView) {
        if (previewView.getMax() == 0) {
            return 0.0f;
        }
        return (float) previewView.getProgress() / previewView.getMax();
    }

    private float getMorphScale(FrameLayout previewFrameLayout, View morphView) {
        return (float) (previewFrameLayout.getHeight() / morphView.getLayoutParams().height);
    }

    private int getRadius(View view) {
        return (int) Math.hypot(view.getWidth() / 2f, view.getHeight() / 2f);
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
    private float getMorphStartX(PreviewView previewView, float offset) {
        float previewPadding = previewView.getThumbOffset();
        float previewLeftX = ((View) previewView).getLeft();
        float previewRightX = ((View) previewView).getRight();
        float previewSeekBarStartX = previewLeftX + previewPadding;
        float previewSeekBarEndX = previewRightX - previewPadding;
        float currentX = previewSeekBarStartX
                + (previewSeekBarEndX - previewSeekBarStartX) * offset;
        return currentX - previewPadding / 2f;
    }

    private float getMorphStartY(PreviewView previewView) {
        return ((View) previewView).getY() + previewView.getThumbOffset();
    }

    /**
     * The destination X of the morph view
     */
    private float getMorphEndX(FrameLayout previewFrameLayout,
                               PreviewView previewView) {
        return previewFrameLayout.getX()
                + (previewFrameLayout.getWidth() / 2f)
                - previewView.getThumbOffset() / 2f;
    }

    /**
     * The destination Y of the view that'll morph
     */
    private float getMorphEndY(FrameLayout previewFrameLayout, View morphView) {
        return (int) (previewFrameLayout.getY()
                + previewFrameLayout.getHeight() / 2f)
                - morphView.getHeight() / 2f;
    }

}
