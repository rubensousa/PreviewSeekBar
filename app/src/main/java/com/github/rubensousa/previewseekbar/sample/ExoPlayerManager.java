package com.github.rubensousa.previewseekbar.sample;


import android.net.Uri;
import android.os.Handler;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource;
import com.google.android.exoplayer2.trackselection.AdaptiveVideoTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.google.android.exoplayer2.util.Util;

public class ExoPlayerManager {

    private SimpleExoPlayerView playerView;
    private SimpleExoPlayer player;
    private View seekSurfaceView;
    private View defaultSurfaceView;

    public ExoPlayerManager(SimpleExoPlayerView playerView, View seekSurfaceView) {
        this.playerView = playerView;
        this.seekSurfaceView = seekSurfaceView;
        this.player = buildPlayer();
        this.playerView.setPlayer(player);
        this.defaultSurfaceView = playerView.getVideoSurfaceView();
    }

    public void preview(float offset) {
        player.seekTo((long) (offset * player.getDuration()));
    }

    public void startPreview() {
        if (seekSurfaceView instanceof SurfaceView) {
            ((SurfaceView) seekSurfaceView).setZOrderMediaOverlay(true);
            player.setVideoSurfaceView((SurfaceView) seekSurfaceView);
        } else if (seekSurfaceView instanceof TextureView) {
            player.setVideoTextureView((TextureView) seekSurfaceView);
        }
    }

    public void stopPreview() {
        if (seekSurfaceView instanceof SurfaceView) {
            ((SurfaceView) seekSurfaceView).setZOrderMediaOverlay(false);
        }
        if (defaultSurfaceView instanceof TextureView) {
            player.setVideoTextureView((TextureView) defaultSurfaceView);
        } else if (defaultSurfaceView instanceof SurfaceView) {
            player.setVideoSurfaceView((SurfaceView) defaultSurfaceView);
        }
    }

    private SimpleExoPlayer buildPlayer() {
        // 1. Create a default TrackSelector
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory =
                new AdaptiveVideoTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector =
                new DefaultTrackSelector(videoTrackSelectionFactory);

        // 2. Create a default LoadControl
        LoadControl loadControl = new DefaultLoadControl();

        // 3. Create the player
        return ExoPlayerFactory.newSimpleInstance(playerView.getContext(), trackSelector, loadControl);
    }


    private void prepareMedia() {
        // Measures bandwidth during playback. Can be null if not required.
        DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();

        // Produces DataSource instances through which media data is loaded.
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(playerView.getContext(),
                Util.getUserAgent(playerView.getContext(), playerView.getContext().getPackageName()), bandwidthMeter);

        // Produces Extractor instances for parsing the media data.
        ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();

        Uri uri = Uri.parse(playerView.getContext().getString(R.string.url_dash));

        // This is the MediaSource representing the media to be played.
        /*MediaSource videoSource = new SsMediaSource(uri, buildDataSourceFactory(bandwidthMeter),
                new DefaultSsChunkSource.Factory(buildDataSourceFactory(bandwidthMeter)),
                new Handler(), null);*/

        MediaSource videoSource = new DashMediaSource(uri,
                dataSourceFactory,
                new DefaultDashChunkSource.Factory(buildDataSourceFactory(bandwidthMeter)),
                new Handler(), null);

        // Prepare the player with the source.
        player.prepare(videoSource);
    }

    private DataSource.Factory buildDataSourceFactory(DefaultBandwidthMeter bandwidthMeter) {
        return new DefaultDataSourceFactory(playerView.getContext(), bandwidthMeter,
                buildHttpDataSourceFactory(bandwidthMeter));
    }

    private HttpDataSource.Factory buildHttpDataSourceFactory(DefaultBandwidthMeter bandwidthMeter) {
        return new DefaultHttpDataSourceFactory(Util.getUserAgent(playerView.getContext(),
                "ExoPlayerDemo"), bandwidthMeter);
    }
}
