package com.github.rubensousa.previewseekbar.sample.exoplayer;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.source.TrackGroup;
import com.google.android.exoplayer2.trackselection.BaseTrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelection;


public class SingleVideoTrackSelection extends BaseTrackSelection {


    public static final class Factory implements TrackSelection.Factory {

        public Factory() {
        }

        @Override
        public SingleVideoTrackSelection createTrackSelection(TrackGroup group, int... tracks) {
            return new SingleVideoTrackSelection(group, tracks);
        }

    }

    public SingleVideoTrackSelection(TrackGroup group, int... tracks) {
        super(group, tracks);
    }

    @Override
    public int getSelectedIndex() {
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

    }
}
