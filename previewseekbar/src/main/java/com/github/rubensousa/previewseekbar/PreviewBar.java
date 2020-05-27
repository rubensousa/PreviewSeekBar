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


import android.widget.FrameLayout;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.github.rubensousa.previewseekbar.animator.PreviewAnimator;

/**
 * A progress bar that displays a preview when it's scrubbed.
 * <p>
 * Default implementation: {@link PreviewSeekBar}
 */
public interface PreviewBar {

    /**
     * @return the current progress
     */
    int getProgress();

    /**
     * @return the maximum value that the progress can have
     */
    int getMax();

    /**
     * @return the radius of the draggable thumb of this Bar
     */
    int getThumbOffset();

    /**
     * @return the color of the scrubber
     */
    int getScrubberColor();

    /**
     * @return true if the preview is currently shown, false otherwise
     */
    boolean isShowingPreview();

    /**
     * @return true if the preview mode is enabled, false otherwise
     */
    boolean isPreviewEnabled();

    /**
     * @param enabled true if the preview view should be shown when this PreviewBar is scrubbed
     */
    void setPreviewEnabled(boolean enabled);

    /**
     * Starts displaying the preview above this bar
     */
    void showPreview();

    /**
     * Hides the preview displaying above this bar
     */
    void hidePreview();

    /**
     * @param autoHide true if the preview should be hidden when the user stops touching this bar
     */
    void setAutoHidePreview(boolean autoHide);

    /**
     * @param animator a custom animator that'll animate the preview layout above this bar
     */
    void setPreviewAnimator(@NonNull PreviewAnimator animator);

    /**
     * @param enabled true if the preview view should be animated
     */
    void setPreviewAnimationEnabled(boolean enabled);

    /**
     * Sets a {@link PreviewLoader} that'll display preview during calls to
     * {@link OnScrubListener#onScrubMove(PreviewBar, int, boolean)}}
     *
     * @param previewLoader a PreviewLoader that'll display previews
     *                      or null to clear the current one
     */
    void setPreviewLoader(@Nullable PreviewLoader previewLoader);

    /**
     * @param color the color for the thumb that displays the current progress
     */
    void setPreviewThumbTint(@ColorInt int color);

    /**
     * @param colorResource the color resource to apply to the thumb that displays
     *                      the current progress
     */
    void setPreviewThumbTintResource(@ColorRes int colorResource);

    /**
     * The view passed here must share the same parent as the PreviewBar
     *
     * @param previewView the preview that's displayed when this PreviewBar is scrubbed.
     */
    void attachPreviewView(@NonNull FrameLayout previewView);

    void addOnScrubListener(OnScrubListener listener);

    void removeOnScrubListener(OnScrubListener listener);

    void addOnPreviewVisibilityListener(OnPreviewVisibilityListener listener);

    void removeOnPreviewVisibilityListener(OnPreviewVisibilityListener listener);

    /**
     * Listener for Preview scrub events
     */
    interface OnScrubListener {

        /**
         * Is called when the user started scrubbing this PreviewBar
         *
         * @param previewBar the PreviewBar that started being scrubbed
         */
        void onScrubStart(PreviewBar previewBar);

        /**
         * Is called when this PreviewBar is being scrubbed by the user or manually.
         *
         * @param previewBar the PreviewBar that was scrubbed
         * @param progress   the current progress
         * @param fromUser   true if this event was triggered by the user or false otherwise
         */
        void onScrubMove(PreviewBar previewBar, int progress, boolean fromUser);

        /**
         * Is called when the user stopped scrubbing this PreviewBar
         *
         * @param previewBar the PreviewBar that was scrubbed
         */
        void onScrubStop(PreviewBar previewBar);
    }

    /**
     * Listener for visibility change events
     */
    interface OnPreviewVisibilityListener {

        /**
         * @param previewBar       the PreviewBar that contains the preview
         * @param isPreviewShowing true if the preview is now showing, false otherwise
         */
        void onVisibilityChanged(PreviewBar previewBar, boolean isPreviewShowing);
    }


}
