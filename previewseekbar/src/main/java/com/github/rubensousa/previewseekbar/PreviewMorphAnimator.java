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
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.FrameLayout;

import androidx.core.graphics.drawable.DrawableCompat;

/**
 * A {@link PreviewAnimator} that morphs the PreviewView's scrubber
 * into the frame that holds the preview
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class PreviewMorphAnimator implements PreviewAnimator {

    private static final int MORPH_SHOW_DURATION = 150;
    private static final int MORPH_HIDE_DURATION = 150;
    private static final int TRANSLATION_SHOW_DURATION = 150;
    private static final int TRANSLATION_HIDE_DURATION = 150;

    private long showTranslationDuration;
    private long morphShowDuration;
    private long hideTranslationDuration;
    private long morphHideDuration;
    private boolean isShowing;
    private boolean isHiding;
    private boolean isMovingToShow;
    private boolean isMovingToHide;
    private boolean isMorphingToShow;
    private boolean isMorphingToHide;
    private Animator morphAnimator;
    private ValueAnimator translationAnimator;

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
    public void cancel(FrameLayout previewFrameLayout, PreviewView previewView) {
        final View overlayView = getOrCreateOverlayView(previewFrameLayout);
        final View morphView = getOrCreateMorphView(previewFrameLayout, previewView);
        overlayView.setVisibility(View.INVISIBLE);
        morphView.setVisibility(View.INVISIBLE);
        cancelPendingAnimations(previewFrameLayout, overlayView, morphView);
    }

    @Override
    public void move(FrameLayout previewFrameLayout, PreviewView previewView) {
        // We only need to handle moves when we're animating the appearance/disappearance
        if (!isMovingToHide && !isMovingToShow) {
            return;
        }
        final View morphView = getOrCreateMorphView(previewFrameLayout, previewView);
        float nextX;
        if (isMovingToShow) {
            nextX = getMorphEndX(previewFrameLayout, previewView);
        } else {
            nextX = getMorphStartX(previewView, getOffset(previewView));
        }

        // Cancel the current animator since we're going to update manually
        if (translationAnimator != null) {
            translationAnimator.removeAllUpdateListeners();
            translationAnimator.cancel();
        }
        morphView.setX(nextX);
    }

    @Override
    public void show(final FrameLayout previewFrameLayout, final PreviewView previewView) {
        if (previewView.getMax() == 0 || isShowing) {
            return;
        }

        isHiding = false;
        isShowing = true;

        final View overlayView = getOrCreateOverlayView(previewFrameLayout);
        final View morphView = getOrCreateMorphView(previewFrameLayout, previewView);
        cancelPendingAnimations(previewFrameLayout, overlayView, morphView);

        // If we were still moving to hide the preview,
        // we can resume from there instead
        if (isMovingToHide || isMorphingToHide) {
            isMovingToHide = false;
            isMorphingToHide = false;
            startCircularReveal(previewFrameLayout, overlayView, morphView);
            return;
        }

        tintViews(previewView, morphView, overlayView);

        morphView.setY(getMorphStartY(previewView));
        morphView.setX(getMorphStartX(previewView, getOffset(previewView)));
        morphView.setScaleX(0f);
        morphView.setScaleY(0f);
        morphView.setAlpha(1.0f);
        startShowTranslation(previewFrameLayout, previewView, overlayView, morphView);
    }

    @Override
    public void hide(FrameLayout previewFrameLayout, PreviewView previewView) {
        if (isHiding) {
            return;
        }
        isShowing = false;
        isHiding = true;

        final View morphView = getOrCreateMorphView(previewFrameLayout, previewView);
        final View overlayView = getOrCreateOverlayView(previewFrameLayout);

        cancelPendingAnimations(previewFrameLayout, overlayView, morphView);

        // If we were still moving to show the preview,
        // we can resume from there instead
        if (isMovingToShow) {
            isMovingToShow = false;
            startHideTranslation(previewFrameLayout, previewView, overlayView, morphView);
            return;
        }

        // If we're still morphing to show, just start the translation process
        if (isMorphingToShow) {
            isMorphingToShow = false;
            startHideTranslation(previewFrameLayout, previewView, overlayView, morphView);
            return;
        }

        tintViews(previewView, morphView, overlayView);

        overlayView.setVisibility(View.VISIBLE);
        previewFrameLayout.setVisibility(View.VISIBLE);

        final float targetScale = getMorphScale(previewFrameLayout, morphView);
        morphView.setX(getMorphEndX(previewFrameLayout, previewView));
        morphView.setY(getMorphEndY(previewFrameLayout, morphView));
        morphView.setScaleX(targetScale);
        morphView.setScaleY(targetScale);
        morphView.setVisibility(View.INVISIBLE);
        if (previewFrameLayout.isAttachedToWindow()) {
            startReverseCircularReveal(previewFrameLayout, previewView, overlayView, morphView);
        }
    }

    /**
     * Starts the translation to the center of the preview frame
     */
    private void startShowTranslation(final FrameLayout previewFrameLayout,
                                      PreviewView previewView,
                                      final View overlayView,
                                      final View morphView) {
        isMovingToShow = true;
        final float targetScale = getMorphScale(previewFrameLayout, morphView);
        overlayView.setVisibility(View.INVISIBLE);
        previewFrameLayout.setVisibility(View.INVISIBLE);
        morphView.setVisibility(View.VISIBLE);

        animateMorphViewX(morphView, getMorphEndX(previewFrameLayout, previewView),
                showTranslationDuration);

        morphView.animate()
                .y(getMorphEndY(previewFrameLayout, morphView))
                .scaleY(targetScale)
                .scaleX(targetScale)
                .setDuration(showTranslationDuration)
                .setInterpolator(new AccelerateInterpolator())
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        isMovingToShow = false;
                        overlayView.setAlpha(1.0f);
                        if (previewFrameLayout.isAttachedToWindow()) {
                            startCircularReveal(previewFrameLayout, overlayView, morphView);
                        }
                    }
                }).start();
    }

    /**
     * Starts the circular reveal of the preview with an overlay above that fades out
     */
    private void startCircularReveal(final FrameLayout previewFrameLayout,
                                     final View overlayView,
                                     final View morphView) {
        isMorphingToShow = true;

        float startRadius = previewFrameLayout.getHeight() / 2f;
        float endRadius = getRadius(previewFrameLayout);
        long duration = morphShowDuration;

        morphAnimator = ViewAnimationUtils.createCircularReveal(previewFrameLayout,
                getCenterX(previewFrameLayout),
                getCenterY(previewFrameLayout),
                startRadius,
                endRadius);
        morphAnimator.setTarget(previewFrameLayout);
        morphAnimator.setInterpolator(new AccelerateInterpolator());
        morphAnimator.setDuration(duration);
        morphAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                isMorphingToShow = false;
                isShowing = false;
                overlayView.setAlpha(0.0f);
                overlayView.setVisibility(View.INVISIBLE);
            }
        });
        morphAnimator.start();
        previewFrameLayout.setVisibility(View.VISIBLE);
        overlayView.setVisibility(View.VISIBLE);
        morphView.setVisibility(View.INVISIBLE);
        overlayView.animate()
                .alpha(0f)
                .setDuration(morphShowDuration / 2);

    }

    private void startReverseCircularReveal(final FrameLayout previewFrameLayout,
                                            final PreviewView previewView,
                                            final View overlayView,
                                            final View morphView) {
        isMorphingToHide = true;

        float startRadius = getRadius(previewFrameLayout);
        float endRadius = previewFrameLayout.getHeight() / 2f;
        long duration = morphHideDuration;

        morphAnimator = ViewAnimationUtils.createCircularReveal(previewFrameLayout,
                getCenterX(previewFrameLayout),
                getCenterY(previewFrameLayout),
                startRadius,
                endRadius);
        morphAnimator.setDuration(duration);
        morphAnimator.setInterpolator(new AccelerateInterpolator());
        morphAnimator.setTarget(previewFrameLayout);
        morphAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                isMorphingToHide = false;
                startHideTranslation(previewFrameLayout, previewView, overlayView, morphView);
            }
        });
        overlayView.setVisibility(View.VISIBLE);
        overlayView.animate().alpha(1f).setDuration(morphHideDuration / 2)
                .setInterpolator(new AccelerateInterpolator()).start();
        morphAnimator.start();
    }

    /**
     * Starts the translation to the center of the scrubber
     */
    private void startHideTranslation(FrameLayout previewFrameLayout,
                                      PreviewView previewView,
                                      View overlayView,
                                      final View morphView) {
        isMovingToHide = true;
        overlayView.setVisibility(View.INVISIBLE);
        previewFrameLayout.setVisibility(View.INVISIBLE);
        morphView.setVisibility(View.VISIBLE);

        animateMorphViewX(morphView, getMorphStartX(previewView, getOffset(previewView)),
                hideTranslationDuration);

        morphView.animate()
                .y(getMorphStartY(previewView))
                .scaleY(0)
                .scaleX(0)
                .setDuration(hideTranslationDuration)
                .setInterpolator(new AccelerateInterpolator())
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        isMovingToHide = false;
                        isHiding = false;
                        morphView.setVisibility(View.INVISIBLE);
                    }
                }).start();
    }

    /**
     * Animate the morph view X position using a different animator
     * since we can't cancel an animation running on a single property
     * without canceling all current animations.
     */
    private void animateMorphViewX(final View morphView, float toX, long duration) {
        if (translationAnimator != null) {
            translationAnimator.removeAllUpdateListeners();
            translationAnimator.cancel();
        }
        translationAnimator = ValueAnimator.ofFloat(morphView.getX(), toX);
        translationAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                morphView.setX((float) animation.getAnimatedValue());
            }
        });
        translationAnimator.setDuration(duration);
        translationAnimator.setInterpolator(new AccelerateInterpolator());
        translationAnimator.start();
    }

    private void cancelPendingAnimations(FrameLayout previewFrameLayout,
                                         View overlayView,
                                         View morphView) {
        if (morphAnimator != null) {
            morphAnimator.removeAllListeners();
            morphAnimator.cancel();
            morphAnimator = null;
        }
        if (translationAnimator != null) {
            translationAnimator.removeAllUpdateListeners();
            translationAnimator.cancel();
            translationAnimator = null;
        }
        previewFrameLayout.animate().setListener(null);
        previewFrameLayout.animate().cancel();
        overlayView.animate().setListener(null);
        overlayView.animate().cancel();
        morphView.animate().setListener(null);
        morphView.animate().cancel();
    }

    /**
     * Creates the overlay view that displays above the preview view.
     * <p>
     * This overlay is used for the circular reveal
     */
    private View getOrCreateOverlayView(FrameLayout previewFrameLayout) {
        View overlay = previewFrameLayout.findViewById(R.id.previewSeekBarOverlayViewId);

        if (overlay != null) {
            return overlay;
        }

        // Create frame view for the circular reveal
        overlay = new View(previewFrameLayout.getContext());
        overlay.setVisibility(View.INVISIBLE);
        overlay.setId(R.id.previewSeekBarOverlayViewId);

        // The overlay needs to cover the whole frame
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

    private float getOffset(PreviewView previewView) {
        if (previewView.getMax() == 0) {
            return 0.0f;
        }
        return (float) previewView.getProgress() / previewView.getMax();
    }

    private float getMorphScale(FrameLayout previewFrameLayout, View morphView) {
        return (float) (previewFrameLayout.getHeight() / morphView.getLayoutParams().height);
    }

    private float getRadius(View view) {
        return (float) Math.hypot(view.getWidth() / 2f, view.getHeight() / 2f);
    }

    private int getCenterX(View view) {
        return view.getWidth() / 2;
    }

    private int getCenterY(View view) {
        return view.getHeight() / 2;
    }

    /**
     * The starting X position of the view that'll morph into the preview.
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

    /**
     * The starting Y position of the view that'll morph into the preview
     */
    private float getMorphStartY(PreviewView previewView) {
        return ((View) previewView).getY() + previewView.getThumbOffset();
    }

    /**
     * The destination X of the view that'll morph into the preview
     */
    private float getMorphEndX(FrameLayout previewFrameLayout,
                               PreviewView previewView) {
        return previewFrameLayout.getX()
                + (previewFrameLayout.getWidth() / 2f)
                - previewView.getThumbOffset() / 2f;
    }

    /**
     * The destination Y of the view that'll morph into the preview
     */
    private float getMorphEndY(FrameLayout previewFrameLayout, View morphView) {
        return (int) (previewFrameLayout.getY()
                + previewFrameLayout.getHeight() / 2f)
                - morphView.getHeight() / 2f;
    }

}
