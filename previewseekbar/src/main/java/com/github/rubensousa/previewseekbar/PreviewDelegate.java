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
 * Handles the logic to display and animate a PreviewView
 */
public class PreviewDelegate implements PreviewView.OnPreviewChangeListener {

    private FrameLayout previewFrameLayout;
    private PreviewLoader previewLoader;
    private PreviewAnimator animator;
    private PreviewView previewView;

    private boolean showing;
    private boolean hasPreviewFrameLayout;
    private boolean enabled;
    private boolean animationEnabled;
    /**
     * True when the user has started scrubbing.
     * Will be true until {@link PreviewDelegate#onStopPreview(PreviewView, int)} gets called
     */
    private boolean startedScrubbing;

    /**
     * True if the user is currently scrubbing
     * Will only be true after a first pass
     * on {@link PreviewDelegate#onPreview(PreviewView, int, boolean)}
     */
    private boolean isUserScrubbing;

    public PreviewDelegate(PreviewView previewView) {
        this.previewView = previewView;
        // We need to register ourselves to handle the animations
        this.previewView.addOnPreviewChangeListener(this);
        this.animationEnabled = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            animator = new PreviewMorphAnimator();
        } else {
            animator = new PreviewFadeAnimator();
        }
    }

    @Override
    public void onStartPreview(PreviewView previewView, int progress) {
        startedScrubbing = true;
    }

    @Override
    public void onStopPreview(PreviewView previewView, int progress) {
        hide();
        showing = false;
        isUserScrubbing = false;
        startedScrubbing = false;
    }

    @Override
    public void onPreview(PreviewView previewView, int progress, boolean fromUser) {
        if (!hasPreviewFrameLayout) {
            return;
        }

        if (fromUser) {
            final int targetX = updateFrameX(progress, previewView.getMax());
            previewFrameLayout.setX(targetX);
            animator.move(previewFrameLayout, previewView);
        }

        if (!showing && !isUserScrubbing && fromUser && enabled) {
            show();
            isUserScrubbing = true;
        }

        if (previewLoader != null && showing) {
            previewLoader.loadPreview(progress, previewView.getMax());
        }
    }

    public void show() {
        if (!showing && hasPreviewFrameLayout) {
            if (animationEnabled) {
                animator.show(previewFrameLayout, previewView);
            } else {
                animator.cancel(previewFrameLayout, previewView);
                previewFrameLayout.setVisibility(View.VISIBLE);
            }
            showing = true;
        }
    }

    public void hide() {
        if (showing && hasPreviewFrameLayout) {
            if (animationEnabled) {
                animator.hide(previewFrameLayout, previewView);
            } else {
                animator.cancel(previewFrameLayout, previewView);
                previewFrameLayout.setVisibility(View.INVISIBLE);
            }
            showing = false;
        }
    }

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
                attachPreviewFrameLayout(frameLayout);
            }
        }
    }

    public void attachPreviewFrameLayout(@NonNull FrameLayout frameLayout) {
        previewFrameLayout = frameLayout;
        previewFrameLayout.setVisibility(View.INVISIBLE);
        hasPreviewFrameLayout = true;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public boolean isShowing() {
        return showing;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setAnimationEnabled(boolean enabled) {
        this.animationEnabled = enabled;
    }

    public void updateProgress(int progress, int max) {
        if (hasPreviewFrameLayout()) {
            if (!isUserScrubbing && !startedScrubbing) {
                previewFrameLayout.setX(updateFrameX(progress, max));
                animator.move(previewFrameLayout, previewView);
            }
        }
    }

    public boolean hasPreviewFrameLayout() {
        return hasPreviewFrameLayout;
    }

    /**
     * Get the x position for the preview frame. This method takes into account padding
     * that'll make the frame not move until the scrub position exceeds
     * at least half of the frame's width.
     */
    private int updateFrameX(int progress, int max) {
        if (max == 0) {
            return 0;
        }

        final ViewGroup parent = (ViewGroup) previewFrameLayout.getParent();
        final ViewGroup.MarginLayoutParams layoutParams
                = (ViewGroup.MarginLayoutParams) previewFrameLayout.getLayoutParams();

        float offset = (float) progress / max;

        int minimumX = previewFrameLayout.getLeft();
        int maximumX = parent.getWidth()
                - parent.getPaddingRight()
                - layoutParams.rightMargin;

        float previewPadding = previewView.getThumbOffset();
        float previewLeftX = ((View) previewView).getLeft();
        float previewRightX = ((View) previewView).getRight();
        float previewSeekBarStartX = previewLeftX + previewPadding;
        float previewSeekBarEndX = previewRightX - previewPadding;

        float currentX = previewSeekBarStartX
                + (previewSeekBarEndX - previewSeekBarStartX) * offset;

        float startX = currentX - previewFrameLayout.getWidth() / 2f;
        float endX = startX + previewFrameLayout.getWidth();

        // Clamp the moves
        if (startX >= minimumX && endX <= maximumX) {
            return (int) startX;
        } else if (startX < minimumX) {
            return minimumX;
        } else {
            return maximumX - previewFrameLayout.getWidth();
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
