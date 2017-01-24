/*
 * Copyright 2016 The Android Open Source Project
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

package com.github.rubensousa.previewseekbar.sample.exoplayer;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.source.TrackGroup;
import com.google.android.exoplayer2.trackselection.BaseTrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelection;


public class WorstVideoTrackSelection extends BaseTrackSelection {


    public static final class Factory implements TrackSelection.Factory {

        public Factory() {
        }

        @Override
        public WorstVideoTrackSelection createTrackSelection(TrackGroup group, int... tracks) {
            return new WorstVideoTrackSelection(group, tracks);
        }

    }

    public WorstVideoTrackSelection(TrackGroup group, int... tracks) {
        super(group, tracks);
    }

    @Override
    public int getSelectedIndex() {
        // last index has the stream with the lowest bitrate
        return getTrackGroup().length - 1;
    }

    @Override
    public int getSelectionReason() {
        return C.SELECTION_REASON_MANUAL;
    }

    @Override
    public Object getSelectionData() {
        return null;
    }

    @Override
    public void updateSelectedTrack(long bufferedDurationUs) {
        // No-op
    }
}
