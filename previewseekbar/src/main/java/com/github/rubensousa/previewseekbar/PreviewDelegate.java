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

import com.github.rubensousa.previewseekbar.animator.PreviewAnimator;
import com.github.rubensousa.previewseekbar.animator.PreviewFadeAnimator;
import com.github.rubensousa.previewseekbar.animator.PreviewMorphAnimator;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles the logic to display and animate a preview view when a {@link PreviewBar} is scrubbed
 */
public class PreviewDelegate {

    private FrameLayout previewView;
    private PreviewLoader previewLoader;
    private PreviewAnimator animator;
    private PreviewBar previewBar;
    private List<PreviewBar.OnScrubListener> scrubListeners;
    private List<PreviewBar.OnPreviewVisibilityListener> visibilityListeners;

    private boolean showingPreview;
    private boolean previewViewAttached;
    private boolean previewEnabled;
    private boolean animationEnabled;
    private boolean previewAutoHide;
    /**
     * True if the user is currently scrubbing
     * Will only be true after a first pass
     * on {@link PreviewDelegate#onScrubMove(int, boolean)}
     * to avoid detecting clicks as scrub movements
     */
    private boolean isUserScrubbing;

    public PreviewDelegate(PreviewBar previewBar) {
        this.scrubListeners = new ArrayList<>();
        this.visibilityListeners = new ArrayList<>();
        this.previewBar = previewBar;
        this.animationEnabled = true;
        this.previewAutoHide = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            animator = new PreviewMorphAnimator();
        } else {
            animator = new PreviewFadeAnimator();
        }
    }

    @Nullable
    public static FrameLayout findPreviewView(@NonNull ViewGroup parent, int previewViewId) {
        if (previewViewId == View.NO_ID) {
            return null;
        }
        for (int i = 0; i < parent.getChildCount(); i++) {
            View child = parent.getChildAt(i);
            if (child.getId() == previewViewId && child instanceof FrameLayout) {
                return (FrameLayout) child;
            }
        }
        return null;
    }

    public void onScrubStart() {
        for (PreviewBar.OnScrubListener listener : scrubListeners) {
            listener.onScrubStart(previewBar);
        }
    }

    public void onScrubMove(int progress, boolean fromUser) {
        if (!previewViewAttached) {
            return;
        }

        final int targetX = updatePreviewX(progress, previewBar.getMax());
        previewView.setX(targetX);

        if (animationEnabled) {
            animator.move(previewView, previewBar);
        }

        if (!isUserScrubbing && fromUser && previewEnabled) {
            isUserScrubbing = true;
            show();
        }

        for (PreviewBar.OnScrubListener listener : scrubListeners) {
            listener.onScrubMove(previewBar, progress, fromUser);
        }

        if (previewLoader != null && showingPreview) {
            previewLoader.loadPreview(progress, previewBar.getMax());
        }
    }

    public void show() {
        if (!showingPreview && previewViewAttached && previewEnabled) {
            if (animationEnabled) {
                animator.show(previewView, previewBar);
            } else {
                animator.cancel(previewView, previewBar);
                previewView.setVisibility(View.VISIBLE);
            }
            showingPreview = true;
            for (PreviewBar.OnPreviewVisibilityListener listener : visibilityListeners) {
                listener.onVisibilityChanged(previewBar, true);
            }
        }
    }

    public void onScrubStop() {
        isUserScrubbing = false;
        if (previewAutoHide) {
            hide();
        }
        for (PreviewBar.OnScrubListener listener : scrubListeners) {
            listener.onScrubStop(previewBar);
        }
    }

    public void setPreviewLoader(@Nullable PreviewLoader previewLoader) {
        this.previewLoader = previewLoader;
    }

    public void setAnimator(@NonNull PreviewAnimator animator) {
        this.animator = animator;
    }

    public boolean isPreviewEnabled() {
        return previewEnabled;
    }

    public boolean isShowingPreview() {
        return showingPreview;
    }

    /**
     * Hides the preview view
     */
    public void hide() {
        if (showingPreview && previewViewAttached) {
            if (animationEnabled) {
                animator.hide(previewView, previewBar);
            } else {
                animator.cancel(previewView, previewBar);
                previewView.setVisibility(View.INVISIBLE);
            }
            showingPreview = false;
            for (PreviewBar.OnPreviewVisibilityListener listener : visibilityListeners) {
                listener.onVisibilityChanged(previewBar, false);
            }
        }
    }

    public void setPreviewEnabled(boolean previewEnabled) {
        this.previewEnabled = previewEnabled;
    }

    public void setAnimationEnabled(boolean enabled) {
        this.animationEnabled = enabled;
    }

    public void setAutoHidePreview(boolean autoHide) {
        this.previewAutoHide = autoHide;
    }

    public void attachPreviewView(@NonNull FrameLayout previewView) {
        this.previewView = previewView;
        this.previewView.setVisibility(View.INVISIBLE);
        previewViewAttached = true;
    }

    public boolean isPreviewViewAttached() {
        return previewViewAttached;
    }

    public boolean isUserScrubbing() {
        return isUserScrubbing;
    }

    public void updateProgress(int progress, int max) {
        // This is a manual update, so check if the user isn't currently scrubbing
        // to avoid inconsistencies between the current scrubbed position
        // and the real position of the preview
        if (isShowingPreview() && !isUserScrubbing()) {
            onScrubMove(progress, false);
        }
    }

    public void addOnScrubListener(PreviewBar.OnScrubListener listener) {
        if (!scrubListeners.contains(listener)) {
            scrubListeners.add(listener);
        }
    }

    public void removeOnScrubListener(PreviewBar.OnScrubListener listener) {
        scrubListeners.remove(listener);
    }

    public void addOnPreviewVisibilityListener(PreviewBar.OnPreviewVisibilityListener listener) {
        if (!visibilityListeners.contains(listener)) {
            visibilityListeners.add(listener);
        }
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

    public void removeOnPreviewVisibilityListener(PreviewBar.OnPreviewVisibilityListener listener) {
        visibilityListeners.remove(listener);
    }

}
