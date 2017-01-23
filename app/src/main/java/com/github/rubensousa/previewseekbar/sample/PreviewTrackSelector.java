package com.github.rubensousa.previewseekbar.sample;

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
