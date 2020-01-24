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

/**
 * Loads the previews for a {@link PreviewBar}
 */
public interface PreviewLoader {

    /**
     * This is called by a {@link PreviewBar} when the current progress
     * or the current maximum value has changed, either by user input or programmatically.
     *
     * This is only called when the preview is showing,
     * unlike {@link PreviewBar.OnScrubListener#onScrubMove(PreviewBar, int, boolean)}
     *
     * @param currentPosition the current position, between 0 and max
     * @param max             the maximum possible value
     */
    void loadPreview(long currentPosition, long max);

}
