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

import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * Morphs the draggable view of the SeekBar into a frame that contains the preview
 * <p>
 * Implementations: {@link PreviewAnimatorImpl} and {@link PreviewAnimatorLollipopImpl}
 */
abstract class PreviewAnimator {

    View morphView;
    View previewFrameView;
    FrameLayout previewFrameLayout;
    PreviewView previewView;
    ViewGroup parent;

    public PreviewAnimator(ViewGroup parent, PreviewView previewView, View morphView,
                           FrameLayout previewFrameLayout, View previewFrameView) {
        this.parent = parent;
        this.previewView = previewView;
        this.morphView = morphView;
        this.previewFrameLayout = previewFrameLayout;
        this.previewFrameView = previewFrameView;
    }

    public abstract void move();

    public abstract void show();

    public abstract void hide();

    /**
     * Get x position for the preview frame. This method takes into account a margin
     * that'll make the frame not move until the scrub position exceeds half of the frame's width.
     */
    float getFrameX() {
        ViewGroup.MarginLayoutParams params
                = (ViewGroup.MarginLayoutParams) previewFrameLayout.getLayoutParams();
        float offset = getWidthOffset(previewView.getProgress());
        float low = previewFrameLayout.getLeft();
        float high = parent.getWidth() - params.rightMargin - previewFrameLayout.getWidth();

        float startX = getPreviewViewStartX() + previewView.getThumbOffset();
        float endX = getPreviewViewEndX() - previewView.getThumbOffset();
        float center = (endX - startX) * offset + startX;
        float nextX = center - previewFrameLayout.getWidth() / 2f;

        // Don't move if we still haven't reached half of the width
        if (nextX < low) {
            return low;
        } else if (nextX > high) {
            return high;
        } else {
            return nextX;
        }
    }

    float getPreviewViewStartX() {
        return ((View) previewView).getX();
    }

    float getPreviewViewEndX() {
        return getPreviewViewStartX() + ((View) previewView).getWidth();
    }

    float getWidthOffset(int progress) {
        return (float) progress / previewView.getMax();
    }

}
