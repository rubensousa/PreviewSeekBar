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

import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Handles the logic to display and animate a preview view when a {@link PreviewBar} is scrubbed
 */
public class PreviewDelegate {

    private FrameLayout previewView;
    private PreviewLoader previewLoader;
    private PreviewAnimator animator;
    private PreviewBar previewBar;

    private boolean showingPreview;
    private boolean hasPreviewFrameLayout;
    private boolean previewEnabled;
    private boolean animationEnabled;
    /**
     * True when the user has started scrubbing.
     * Will be true until {@link PreviewDelegate#onStopPreview()} gets called
     */
    private boolean hasUserStartedScrubbing;

    /**
     * True if the user is currently scrubbing
     * Will only be true after a first pass
     * on {@link PreviewDelegate#onPreview(int, boolean)}
     */
    private boolean isUserScrubbing;

    public PreviewDelegate(PreviewBar previewBar) {
        this.previewBar = previewBar;
        // We need to register ourselves to handle the animations
        this.previewBar.addOnPreviewChangeListener(new PreviewBar.OnPreviewChangeListener() {
            @Override
            public void onStartPreview(PreviewBar previewBar, int progress) {
                PreviewDelegate.this.onStartPreview();
            }

            @Override
            public void onStopPreview(PreviewBar previewBar, int progress) {
                PreviewDelegate.this.onStopPreview();
            }

            @Override
            public void onPreview(PreviewBar previewBar, int progress, boolean fromUser) {
                PreviewDelegate.this.onPreview(progress, fromUser);
            }
        });
        this.animationEnabled = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            animator = new PreviewMorphAnimator();
        } else {
            animator = new PreviewFadeAnimator();
        }
    }

    /**
     * Shows the preview view
     */
    public void show() {
        if (!showingPreview && hasPreviewFrameLayout) {
            if (animationEnabled) {
                animator.show(previewView, previewBar);
            } else {
                animator.cancel(previewView, previewBar);
                previewView.setVisibility(View.VISIBLE);
            }
            showingPreview = true;
        }
    }

    /**
     * Hides the preview view
     */
    public void hide() {
        if (showingPreview && hasPreviewFrameLayout) {
            if (animationEnabled) {
                animator.hide(previewView, previewBar);
            } else {
                animator.cancel(previewView, previewBar);
                previewView.setVisibility(View.INVISIBLE);
            }
            showingPreview = false;
        }
    }

    /**
     * Sets a {@link PreviewLoader} that'll display preview during calls to
     * {@link PreviewBar.OnPreviewChangeListener#onPreview(PreviewBar, int, boolean)}}
     *
     * @param previewLoader a PreviewLoader that'll display previews or null to clear the current one
     */
    public void setPreviewLoader(@Nullable PreviewLoader previewLoader) {
        this.previewLoader = previewLoader;
    }

    public void setAnimator(@NonNull PreviewAnimator animator) {
        this.animator = animator;
    }

    public void onLayout(ViewGroup previewParent, int frameLayoutId) {
        if (!hasPreviewFrameLayout) {
            FrameLayout frameLayout = findFrameLayout(previewParent, frameLayoutId);
            if (frameLayout != null) {
                attachPreviewView(frameLayout);
            }
        }
    }

    public void attachPreviewView(@NonNull FrameLayout previewView) {
        this.previewView = previewView;
        this.previewView.setVisibility(View.INVISIBLE);
        hasPreviewFrameLayout = true;
    }

    public boolean isPreviewEnabled() {
        return previewEnabled;
    }

    public boolean isShowingPreview() {
        return showingPreview;
    }

    public void setPreviewEnabled(boolean previewEnabled) {
        this.previewEnabled = previewEnabled;
    }

    public void setAnimationEnabled(boolean enabled) {
        this.animationEnabled = enabled;
    }

    public void updateProgress(int progress, int max) {
        if (hasPreviewFrameLayout()) {
            // This is a manual update, so check if the user isn't currently scrubbing
            // to avoid inconsistencies between the current scrubbed position
            // and the real position of the preview
            if (!isUserScrubbing && !hasUserStartedScrubbing) {
                previewView.setX(updatePreviewX(progress, max));
                animator.move(previewView, previewBar);
            }
        }
    }

    public boolean hasPreviewFrameLayout() {
        return hasPreviewFrameLayout;
    }

    private void onStartPreview() {
        hasUserStartedScrubbing = true;
    }

    private void onPreview(int progress, boolean fromUser) {
        if (!hasPreviewFrameLayout) {
            return;
        }

        if (fromUser) {
            final int targetX = updatePreviewX(progress, previewBar.getMax());
            previewView.setX(targetX);
            animator.move(previewView, previewBar);
        }

        if (!showingPreview && !isUserScrubbing && fromUser && previewEnabled) {
            show();
            isUserScrubbing = true;
        }

        if (previewLoader != null && showingPreview) {
            previewLoader.loadPreview(progress, previewBar.getMax());
        }
    }

    private void onStopPreview() {
        hide();
        showingPreview = false;
        isUserScrubbing = false;
        hasUserStartedScrubbing = false;
    }


    /**
     * Get the x position for the preview view. This method takes into account padding
     * that'll make the frame not move until the scrub position exceeds
     * at least half of the frame's width.
     */
    private int updatePreviewX(int progress, int max) {
        if (max == 0) {
            return 0;
        }

        final ViewGroup parent = (ViewGroup) previewView.getParent();
        final ViewGroup.MarginLayoutParams layoutParams
                = (ViewGroup.MarginLayoutParams) previewView.getLayoutParams();

        float offset = (float) progress / max;

        int minimumX = previewView.getLeft();
        int maximumX = parent.getWidth()
                - parent.getPaddingRight()
                - layoutParams.rightMargin;

        float previewPadding = previewBar.getThumbOffset();
        float previewLeftX = ((View) previewBar).getLeft();
        float previewRightX = ((View) previewBar).getRight();
        float previewSeekBarStartX = previewLeftX + previewPadding;
        float previewSeekBarEndX = previewRightX - previewPadding;

        float currentX = previewSeekBarStartX
                + (previewSeekBarEndX - previewSeekBarStartX) * offset;

        float startX = currentX - previewView.getWidth() / 2f;
        float endX = startX + previewView.getWidth();

        // Clamp the moves
        if (startX >= minimumX && endX <= maximumX) {
            return (int) startX;
        } else if (startX < minimumX) {
            return minimumX;
        } else {
            return maximumX - previewView.getWidth();
        }
    }

    @Nullable
    private FrameLayout findFrameLayout(ViewGroup parent, int id) {
        if (id == View.NO_ID || parent == null) {
            return null;
        }
        for (int i = 0; i < parent.getChildCount(); i++) {
            View child = parent.getChildAt(i);
            if (child.getId() == id && child instanceof FrameLayout) {
                return (FrameLayout) child;
            }
        }
        return null;
    }

}
