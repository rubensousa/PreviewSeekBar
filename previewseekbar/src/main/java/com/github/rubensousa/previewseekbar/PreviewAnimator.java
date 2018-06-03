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

import com.github.rubensousa.previewseekbar.base.PreviewView;

abstract class PreviewAnimator {

    static final int MORPH_REVEAL_DURATION = 200;
    static final int MORPH_MOVE_DURATION = 150;
    static final int UNMORPH_MOVE_DURATION = 150;
    static final int UNMORPH_UNREVEAL_DURATION = 200;

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

    void move() {
        previewFrameLayout.setX(getFrameX());
        morphView.setX(getMorphX());
    }

    public abstract void show();

    public abstract void hide();

    /**
     * Get x position for the morph view that'll animate and transform into the preview frame
     */
    float getMorphX() {
        float offset = getWidthOffset(previewView.getProgress());
        float startX = ((View) previewView).getX();
        float endX = ((View) previewView).getWidth() + startX;

        float ltr = (endX - startX) * offset - morphView.getWidth() / 2f;
        float rtl = (endX - startX) * (1 - offset) - morphView.getWidth() / 2f;

        return ((View) previewView).getLayoutDirection() == View.LAYOUT_DIRECTION_LTR ?
                ltr : rtl;
    }

    /**
     * Get x position for the preview frame. This method takes into account a margin
     * that'll make the frame not move until the scrub position exceeds half of the frame's width.
     */
    float getFrameX() {
        float offset = getWidthOffset(previewView.getProgress());
        float startX = parent.getResources()
                .getDimensionPixelOffset(R.dimen.previewseekbar_indicator_width);
        float endX = parent.getWidth() - startX;
        float ltr = startX;// (endX - startX) * offset;
        float rtl = (endX - startX) * (1 - offset);

        return ((View) previewView).getLayoutDirection() == View.LAYOUT_DIRECTION_LTR ? ltr : rtl;
    }

    float getHideY() {
        return ((View) previewView).getY() + previewView.getThumbOffset();
    }

    float getShowY() {
        return (int) (previewFrameLayout.getY() + previewFrameLayout.getHeight() / 2f);
    }

    private float getWidthOffset(int progress) {
        return (float) progress / previewView.getMax();
    }

}
