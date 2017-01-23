package com.github.rubensousa.previewseekbar.sample;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.widget.SeekBar;

import com.github.rubensousa.previewseekbar.PreviewSeekBar;
import com.github.rubensousa.previewseekbar.PreviewSeekBarLayout;
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


public class MainActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener,
        Toolbar.OnMenuItemClickListener {

    private View defaultSurfaceView;
    private View seekSurfaceView;
    private PreviewSeekBarLayout previewSeekBarLayout;
    private SimpleExoPlayerView playerView;
    private SimpleExoPlayer player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.main);
        toolbar.setOnMenuItemClickListener(this);
        previewSeekBarLayout = (PreviewSeekBarLayout) findViewById(R.id.previewSeekBarLayout);
        playerView = (SimpleExoPlayerView) findViewById(R.id.player_view);
        seekSurfaceView = findViewById(R.id.surfaceView);
        PreviewSeekBar seekBar = (PreviewSeekBar) playerView.findViewById(R.id.exo_progress);

       /* ((SurfaceView) seekSurfaceView).setZOrderMediaOverlay(true);
        ((SurfaceView) seekSurfaceView).getHolder().setFormat(PixelFormat.TRANSPARENT);*/
        seekBar.addOnSeekBarChangeListener(this);
        player = setupPlayer();
        playerView.setPlayer(player);
        defaultSurfaceView = playerView.getVideoSurfaceView();
        prepareMedia();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        // load video progress
        player.seekTo(player.getDuration() * progress / seekBar.getMax());
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        if (seekSurfaceView instanceof SurfaceView) {
            ((SurfaceView) seekSurfaceView).setZOrderMediaOverlay(true);
            player.setVideoSurfaceView((SurfaceView) seekSurfaceView);
        } else if (seekSurfaceView instanceof TextureView) {
            player.setVideoTextureView((TextureView) seekSurfaceView);
        }
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if (seekSurfaceView instanceof SurfaceView) {
            ((SurfaceView) seekSurfaceView).setZOrderMediaOverlay(false);
        }
        if (defaultSurfaceView instanceof TextureView) {
            player.setVideoTextureView((TextureView) defaultSurfaceView);
        } else if (defaultSurfaceView instanceof SurfaceView) {
            player.setVideoSurfaceView((SurfaceView) defaultSurfaceView);
        }
    }

    private SimpleExoPlayer setupPlayer() {
        // 1. Create a default TrackSelector
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory =
                new AdaptiveVideoTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector =
                new DefaultTrackSelector(videoTrackSelectionFactory);

        // 2. Create a default LoadControl
        LoadControl loadControl = new DefaultLoadControl();

        // 3. Create the player
        return ExoPlayerFactory.newSimpleInstance(this, trackSelector, loadControl);
    }

    private void prepareMedia() {
        // Measures bandwidth during playback. Can be null if not required.
        DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();

        // Produces DataSource instances through which media data is loaded.
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this,
                Util.getUserAgent(this, getPackageName()), bandwidthMeter);

        // Produces Extractor instances for parsing the media data.
        ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();

        Uri uri = Uri.parse(getString(R.string.url_dash));

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
        return new DefaultDataSourceFactory(this, bandwidthMeter,
                buildHttpDataSourceFactory(bandwidthMeter));
    }

    private HttpDataSource.Factory buildHttpDataSourceFactory(DefaultBandwidthMeter bandwidthMeter) {
        return new DefaultHttpDataSourceFactory(Util.getUserAgent(this, "ExoPlayerDemo"),
                bandwidthMeter);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        startActivity(new Intent(this, LocalActivity.class));
        return true;
    }
}
