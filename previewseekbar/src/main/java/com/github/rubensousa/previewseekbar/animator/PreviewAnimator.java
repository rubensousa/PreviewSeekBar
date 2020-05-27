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

import android.widget.FrameLayout;

import com.github.rubensousa.previewseekbar.PreviewBar;

/**
 * Animates the FrameLayout container that has the View responsible for showing a preview.
 * <p>
 * Default implementations: {@link PreviewFadeAnimator} and {@link PreviewMorphAnimator}
 */
public interface PreviewAnimator {

    /**
     * Use {@link PreviewBar#getProgress()} and {@link PreviewBar#getMax()}
     * to determine how much the preview should move
     *
     * @param previewView The view that displays the preview
     * @param previewBar  The PreviewBar that's responsible for this preview
     */
    void move(FrameLayout previewView, PreviewBar previewBar);

    /**
     * Animates the preview appearance.
     * <p>
     * Please note that any animations started by
     * {@link PreviewAnimator#move(FrameLayout, PreviewBar)}
     * or {@link PreviewAnimator#hide(FrameLayout, PreviewBar)} might still be running
     * <p>
     *
     * @param previewView The view that displays the preview
     * @param previewBar  The PreviewBar that's responsible for this preview
     */
    void show(FrameLayout previewView, PreviewBar previewBar);

    /**
     * Animates the preview disappearance.
     * <p>
     * Please note that any animations started by
     * {@link PreviewAnimator#move(FrameLayout, PreviewBar)}
     * or {@link PreviewAnimator#show(FrameLayout, PreviewBar)} might still be running
     * <p>
     *
     * @param previewView The view that displays the preview
     * @param previewBar  The PreviewBar that's responsible for this preview
     */
    void hide(FrameLayout previewView, PreviewBar previewBar);

    /**
     * Cancels any animation started by {@link PreviewAnimator#move(FrameLayout, PreviewBar)},
     * {@link PreviewAnimator#show(FrameLayout, PreviewBar)}
     * or {@link PreviewAnimator#hide(FrameLayout, PreviewBar)}
     *
     * @param previewView The view that displays the preview
     * @param previewBar  The PreviewBar that's responsible for this preview
     */
    void cancel(FrameLayout previewView, PreviewBar previewBar);

}
