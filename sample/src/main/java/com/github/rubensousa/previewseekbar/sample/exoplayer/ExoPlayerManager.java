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

import android.net.Uri;
import android.view.SurfaceView;
import android.view.View;

import com.github.rubensousa.previewseekbar.PreviewLoader;
import com.github.rubensousa.previewseekbar.PreviewSeekBarLayout;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveVideoTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.util.Util;

public class ExoPlayerManager implements ExoPlayer.EventListener, PreviewLoader {

    // 1 minute
    private static final int ROUND_DECIMALS_THRESHOLD = 1 * 60 * 1000;

    private ExoPlayerMediaSourceBuilder mediaSourceBuilder;
    private SimpleExoPlayerView playerView;
    private SimpleExoPlayerView previewPlayerView;
    private SimpleExoPlayer player;
    private SimpleExoPlayer previewPlayer;
    private PreviewSeekBarLayout seekBarLayout;

    public ExoPlayerManager(SimpleExoPlayerView playerView, SimpleExoPlayerView previewPlayerView,
                            PreviewSeekBarLayout seekBarLayout) {
        this.playerView = playerView;
        this.previewPlayerView = previewPlayerView;
        this.seekBarLayout = seekBarLayout;
        this.mediaSourceBuilder = new ExoPlayerMediaSourceBuilder(playerView.getContext());
    }

    public void play(Uri uri) {
        mediaSourceBuilder.setUri(uri);
        if (player != null) {
            player.release();
        }
        if (previewPlayer != null) {
            previewPlayer.release();
        }
        createPlayers();
    }

    public void onStart() {
        if (Util.SDK_INT > 23) {
            createPlayers();
        }
    }

    public void onResume() {
        if ((Util.SDK_INT <= 23 || player == null || previewPlayer == null)) {
            createPlayers();
        }
    }

    public void onPause() {
        if (Util.SDK_INT <= 23) {
            releasePlayers();
        }
    }

    public void onStop() {
        if (Util.SDK_INT > 23) {
            releasePlayers();
        }
    }

    public void stopPreview() {
        player.setPlayWhenReady(true);
        View view = previewPlayerView.getVideoSurfaceView();
        if (view instanceof SurfaceView) {
            view.setVisibility(View.INVISIBLE);
        }
    }

    private float roundOffset(float offset, int scale) {
        return (float) (Math.round(offset * Math.pow(10, scale)) / Math.pow(10, scale));
    }

    private void releasePlayers() {
        if (player != null) {
            player.release();
            player = null;
        }
        if (previewPlayer != null) {
            previewPlayer.release();
            previewPlayer = null;
        }
    }

    private void createPlayers() {
        player = createFullPlayer();
        playerView.setPlayer(player);
        previewPlayer = createPreviewPlayer();
        previewPlayerView.setPlayer(previewPlayer);
    }

    private SimpleExoPlayer createFullPlayer() {
        TrackSelection.Factory videoTrackSelectionFactory
                = new AdaptiveVideoTrackSelection.Factory(new DefaultBandwidthMeter());
        TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
        LoadControl loadControl = new DefaultLoadControl();
        SimpleExoPlayer player = ExoPlayerFactory.newSimpleInstance(playerView.getContext(),
                trackSelector, loadControl);
        player.setPlayWhenReady(true);
        player.prepare(mediaSourceBuilder.getMediaSource(false));
        player.addListener(this);
        return player;
    }

    private SimpleExoPlayer createPreviewPlayer() {
        TrackSelection.Factory videoTrackSelectionFactory = new WorstVideoTrackSelection.Factory();
        TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
        LoadControl loadControl = new PreviewLoadControl();
        SimpleExoPlayer player = ExoPlayerFactory.newSimpleInstance(previewPlayerView.getContext(),
                trackSelector, loadControl);
        player.setPlayWhenReady(false);
        player.prepare(mediaSourceBuilder.getMediaSource(true));
        return player;
    }

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest) {

    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    }

    @Override
    public void onLoadingChanged(boolean isLoading) {

    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        if (playbackState == ExoPlayer.STATE_READY && playWhenReady) {
            seekBarLayout.hidePreview();
        }
    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {

    }

    @Override
    public void onPositionDiscontinuity() {

    }

    @Override
    public void loadPreview(long currentPosition, long max) {
        float offset = (float) currentPosition / max;
        int scale = player.getDuration() >= ROUND_DECIMALS_THRESHOLD ? 2 : 1;
        float offsetRounded = roundOffset(offset, scale);
        player.setPlayWhenReady(false);
        previewPlayer.seekTo((long) (offsetRounded * previewPlayer.getDuration()));
        previewPlayer.setPlayWhenReady(false);
        View view = previewPlayerView.getVideoSurfaceView();
        if (view instanceof SurfaceView) {
            view.setVisibility(View.VISIBLE);
        }
    }
}
