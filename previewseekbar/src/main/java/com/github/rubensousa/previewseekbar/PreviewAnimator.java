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

/**
 * Animates the container that has the View responsible for showing a preview
 * <p>
 * Default implementations: {@link PreviewFadeAnimator} and {@link PreviewMorphAnimator}
 */
public interface PreviewAnimator {

    void move(FrameLayout previewFrameLayout, PreviewView previewView);

    void show(FrameLayout previewFrameLayout, PreviewView previewView);

    void hide(FrameLayout previewFrameLayout, PreviewView previewView);

    void cancel(FrameLayout previewFrameLayout, PreviewView previewView);

}
