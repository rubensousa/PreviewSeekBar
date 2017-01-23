/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.rubensousa.previewseekbar.sample.exoplayer;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.RendererCapabilities;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;


/**
 * A track selector for the video preview
 */
public class PreviewTrackSelector extends DefaultTrackSelector {

    private TrackSelection.Factory videoTrackSelectionFactory;

    public PreviewTrackSelector(TrackSelection.Factory videoTrackSelectionFactory) {
        super(videoTrackSelectionFactory);
        this.videoTrackSelectionFactory = videoTrackSelectionFactory;
    }

    @Override
    protected TrackSelection[] selectTracks(RendererCapabilities[] rendererCapabilities,
                                            TrackGroupArray[] rendererTrackGroupArrays,
                                            int[][][] rendererFormatSupports)
            throws ExoPlaybackException {
        TrackSelection[] rendererTrackSelections = new TrackSelection[1];
        Parameters params = getParameters();
        for (int i = 0; i < rendererCapabilities.length; i++) {
            switch (rendererCapabilities[i].getTrackType()) {
                case C.TRACK_TYPE_VIDEO:
                    rendererTrackSelections[0] = selectVideoTrack(rendererCapabilities[i],
                            rendererTrackGroupArrays[i], rendererFormatSupports[i], params.maxVideoWidth,
                            params.maxVideoHeight, params.allowNonSeamlessAdaptiveness,
                            params.allowMixedMimeAdaptiveness, params.viewportWidth, params.viewportHeight,
                            params.orientationMayChange, videoTrackSelectionFactory,
                            params.exceedVideoConstraintsIfNecessary,
                            params.exceedRendererCapabilitiesIfNecessary);
                    break;
            }
            if (rendererTrackSelections[0] != null) {
                return rendererTrackSelections;
            }
        }
        return rendererTrackSelections;
    }
}
