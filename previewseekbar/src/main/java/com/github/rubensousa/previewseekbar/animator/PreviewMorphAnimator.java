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

package com.github.rubensousa.previewseekbar.animator;


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

import com.github.rubensousa.previewseekbar.PreviewBar;
import com.github.rubensousa.previewseekbar.R;

/**
 * A {@link PreviewAnimator} that morphs the {@link PreviewBar} thumb
 * into the preview view.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class PreviewMorphAnimator implements PreviewAnimator {

    private static final int MORPH_SHOW_DURATION = 125;
    private static final int MORPH_HIDE_DURATION = 125;
    private static final int TRANSLATION_SHOW_DURATION = 100;
    private static final int TRANSLATION_HIDE_DURATION = 100;

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
    public void cancel(FrameLayout previewView, PreviewBar previewBar) {
        final View overlayView = getOrCreateOverlayView(previewView);
        final View morphView = getOrCreateMorphView(previewView, previewBar);
        overlayView.setVisibility(View.INVISIBLE);
        morphView.setVisibility(View.INVISIBLE);
        cancelPendingAnimations(previewView, overlayView, morphView);
    }

    @Override
    public void move(FrameLayout previewView, PreviewBar previewBar) {
        // We only need to handle moves when we're animating the appearance/disappearance
        if (!isMovingToHide && !isMovingToShow) {
            return;
        }
        final View morphView = getOrCreateMorphView(previewView, previewBar);
        float nextX;
        if (isMovingToShow) {
            nextX = getMorphEndX(previewView, previewBar);
        } else {
            nextX = getMorphStartX(previewBar, getOffset(previewBar));
        }

        // Cancel the current animator since we're going to update manually
        if (translationAnimator != null) {
            translationAnimator.removeAllUpdateListeners();
            translationAnimator.cancel();
        }
        morphView.setX(nextX);
    }

    @Override
    public void show(final FrameLayout previewView, final PreviewBar previewBar) {
        if (previewBar.getMax() == 0 || isShowing) {
            return;
        }

        isHiding = false;
        isShowing = true;

        final View overlayView = getOrCreateOverlayView(previewView);
        final View morphView = getOrCreateMorphView(previewView, previewBar);
        cancelPendingAnimations(previewView, overlayView, morphView);

        // If we were still moving to hide the preview,
        // we can resume from there instead
        if (isMovingToHide || isMorphingToHide) {
            isMovingToHide = false;
            isMorphingToHide = false;
            startCircularReveal(previewView, overlayView, morphView);
            return;
        }

        tintViews(previewBar, morphView, overlayView);

        morphView.setY(getMorphStartY(previewBar));
        morphView.setX(getMorphStartX(previewBar, getOffset(previewBar)));
        morphView.setScaleX(0f);
        morphView.setScaleY(0f);
        morphView.setAlpha(1.0f);
        startShowTranslation(previewView, previewBar, overlayView, morphView);
    }

    @Override
    public void hide(FrameLayout previewView, PreviewBar previewBar) {
        if (isHiding) {
            return;
        }
        isShowing = false;
        isHiding = true;

        final View morphView = getOrCreateMorphView(previewView, previewBar);
        final View overlayView = getOrCreateOverlayView(previewView);

        cancelPendingAnimations(previewView, overlayView, morphView);

        // If we were still moving to show the preview,
        // we can resume from there instead
        if (isMovingToShow) {
            isMovingToShow = false;
            startHideTranslation(previewView, previewBar, overlayView, morphView);
            return;
        }

        // If we're still morphing to show, just start the translation process
        if (isMorphingToShow) {
            isMorphingToShow = false;
            startHideTranslation(previewView, previewBar, overlayView, morphView);
            return;
        }

        tintViews(previewBar, morphView, overlayView);

        overlayView.setVisibility(View.VISIBLE);
        previewView.setVisibility(View.VISIBLE);

        final float targetScale = getMorphScale(previewView, morphView);
        morphView.setX(getMorphEndX(previewView, previewBar));
        morphView.setY(getMorphEndY(previewView, morphView));
        morphView.setScaleX(targetScale);
        morphView.setScaleY(targetScale);
        morphView.setVisibility(View.INVISIBLE);
        if (previewView.isAttachedToWindow()) {
            startReverseCircularReveal(previewView, previewBar, overlayView, morphView);
        }
    }

    /**
     * Starts the translation to the center of the preview frame
     */
    private void startShowTranslation(final FrameLayout previewView,
                                      PreviewBar previewBar,
                                      final View overlayView,
                                      final View morphView) {
        isMovingToShow = true;
        final float targetScale = getMorphScale(previewView, morphView);
        overlayView.setVisibility(View.INVISIBLE);
        previewView.setVisibility(View.INVISIBLE);
        morphView.setVisibility(View.VISIBLE);

        animateMorphViewX(morphView, getMorphEndX(previewView, previewBar),
                showTranslationDuration);

        morphView.animate()
                .y(getMorphEndY(previewView, morphView))
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
                        if (previewView.isAttachedToWindow()) {
                            startCircularReveal(previewView, overlayView, morphView);
                        }
                    }
                }).start();
    }

    /**
     * Starts the circular reveal of the preview with an overlay above that fades out
     */
    private void startCircularReveal(final FrameLayout previewView,
                                     final View overlayView,
                                     final View morphView) {
        isMorphingToShow = true;

        float startRadius = previewView.getHeight() / 2f;
        float endRadius = getRadius(previewView);
        long duration = morphShowDuration;

        morphAnimator = ViewAnimationUtils.createCircularReveal(previewView,
                getCenterX(previewView),
                getCenterY(previewView),
                startRadius,
                endRadius);
        morphAnimator.setTarget(previewView);
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
        previewView.setVisibility(View.VISIBLE);
        overlayView.setVisibility(View.VISIBLE);
        morphView.setVisibility(View.INVISIBLE);
        overlayView.animate()
                .alpha(0f)
                .setDuration(morphShowDuration / 2);

    }

    private void startReverseCircularReveal(final FrameLayout previewView,
                                            final PreviewBar previewBar,
                                            final View overlayView,
                                            final View morphView) {
        isMorphingToHide = true;

        float startRadius = getRadius(previewView);
        float endRadius = previewView.getHeight() / 2f;
        long duration = morphHideDuration;

        morphAnimator = ViewAnimationUtils.createCircularReveal(previewView,
                getCenterX(previewView),
                getCenterY(previewView),
                startRadius,
                endRadius);
        morphAnimator.setDuration(duration);
        morphAnimator.setInterpolator(new AccelerateInterpolator());
        morphAnimator.setTarget(previewView);
        morphAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                isMorphingToHide = false;
                startHideTranslation(previewView, previewBar, overlayView, morphView);
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
    private void startHideTranslation(FrameLayout previewView,
                                      PreviewBar previewBar,
                                      View overlayView,
                                      final View morphView) {
        isMovingToHide = true;
        overlayView.setVisibility(View.INVISIBLE);
        previewView.setVisibility(View.INVISIBLE);
        morphView.setVisibility(View.VISIBLE);

        animateMorphViewX(morphView, getMorphStartX(previewBar, getOffset(previewBar)),
                hideTranslationDuration);

        morphView.animate()
                .y(getMorphStartY(previewBar))
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

    private void cancelPendingAnimations(FrameLayout previewView,
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
        previewView.animate().setListener(null);
        previewView.animate().cancel();
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
    private View getOrCreateOverlayView(FrameLayout previewView) {
        View overlay = previewView.findViewById(R.id.previewSeekBarOverlayViewId);

        if (overlay != null) {
            return overlay;
        }

        // Create frame view for the circular reveal
        overlay = new View(previewView.getContext());
        overlay.setVisibility(View.INVISIBLE);
        overlay.setId(R.id.previewSeekBarOverlayViewId);

        // The overlay needs to cover the whole frame
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);

        previewView.addView(overlay, layoutParams);
        return overlay;
    }

    /**
     * Creates the morph view that'll move from the thumb of the PreviewBar
     * to the center of the preview view
     */
    private View getOrCreateMorphView(FrameLayout previewView,
                                      PreviewBar previewBar) {

        final ViewGroup parent = (ViewGroup) previewView.getParent();
        View morphView = parent.findViewById(R.id.previewSeekBarMorphViewId);

        if (morphView != null) {
            return morphView;
        }

        // Create morph view
        morphView = new View(previewView.getContext());
        morphView.setVisibility(View.INVISIBLE);
        morphView.setBackgroundResource(R.drawable.previewseekbar_morph);
        morphView.setId(R.id.previewSeekBarMorphViewId);

        // Setup morph view
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(
                previewBar.getThumbOffset(), previewBar.getThumbOffset());

        // Add the morph view to the parent so we can move it from the SeekBar
        // to the preview frame without worrying about the view bounds
        parent.addView(morphView, layoutParams);
        return morphView;
    }

    private void tintViews(PreviewBar previewBar,
                           View morphView,
                           View frameView) {
        int color = previewBar.getScrubberColor();
        if (morphView.getBackgroundTintList() == null
                || morphView.getBackgroundTintList().getDefaultColor() != color) {
            Drawable drawable = DrawableCompat.wrap(morphView.getBackground());
            DrawableCompat.setTint(drawable, color);
            morphView.setBackground(drawable);
            frameView.setBackgroundColor(color);
        }
    }

    private float getOffset(PreviewBar previewBar) {
        if (previewBar.getMax() == 0) {
            return 0.0f;
        }
        return (float) previewBar.getProgress() / previewBar.getMax();
    }

    private float getMorphScale(FrameLayout previewView, View morphView) {
        return (float) (previewView.getHeight() / morphView.getLayoutParams().height);
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
    private float getMorphStartX(PreviewBar previewBar, float offset) {
        float previewPadding = previewBar.getThumbOffset();
        float previewLeftX = ((View) previewBar).getLeft();
        float previewRightX = ((View) previewBar).getRight();
        float previewSeekBarStartX = previewLeftX + previewPadding;
        float previewSeekBarEndX = previewRightX - previewPadding;
        float currentX = previewSeekBarStartX
                + (previewSeekBarEndX - previewSeekBarStartX) * offset;
        return currentX - previewPadding / 2f;
    }

    /**
     * The starting Y position of the view that'll morph into the preview
     */
    private float getMorphStartY(PreviewBar previewBar) {
        return ((View) previewBar).getY() + previewBar.getThumbOffset();
    }

    /**
     * The destination X of the view that'll morph into the preview
     */
    private float getMorphEndX(FrameLayout previewView,
                               PreviewBar previewBar) {
        return previewView.getX()
                + (previewView.getWidth() / 2f)
                - previewBar.getThumbOffset() / 2f;
    }

    /**
     * The destination Y of the view that'll morph into the preview
     */
    private float getMorphEndY(FrameLayout previewView, View morphView) {
        return (int) (previewView.getY()
                + previewView.getHeight() / 2f)
                - morphView.getHeight() / 2f;
    }

}
