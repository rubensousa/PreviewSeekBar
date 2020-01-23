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

public interface PreviewView {

    /**
     * @return the current progress
     */
    int getProgress();

    /**
     * @return the maximum value that the progress can have
     */
    int getMax();

    /**
     * @return the diameter of the draggable thumb of the SeekBar
     */
    int getThumbOffset();

    /**
     * @return the color of the scrubber
     */
    int getScrubberColor();

    boolean isShowingPreview();

    boolean isPreviewEnabled();

    void showPreview();

    void hidePreview();

    void setPreviewAnimator(@NonNull PreviewAnimator animator);

    void setPreviewAnimationEnabled(boolean enabled);

    void setPreviewEnabled(boolean previewEnabled);

    void setPreviewLoader(@Nullable PreviewLoader previewLoader);

    void setPreviewThumbTint(@ColorInt int color);

    void setPreviewThumbTintResource(@ColorRes int colorResource);

    void attachPreviewFrameLayout(@NonNull FrameLayout frameLayout);

    void addOnPreviewChangeListener(OnPreviewChangeListener listener);

    void removeOnPreviewChangeListener(OnPreviewChangeListener listener);

    interface OnPreviewChangeListener {
        void onStartPreview(PreviewView previewView, int progress);

        void onStopPreview(PreviewView previewView, int progress);

        void onPreview(PreviewView previewView, int progress, boolean fromUser);
    }

}
