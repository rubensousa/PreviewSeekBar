package com.github.rubensousa.previewseekbar.sample.exoplayer;


import android.content.Context;
import android.net.Uri;
import android.os.Handler;

import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.source.smoothstreaming.DefaultSsChunkSource;
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.google.android.exoplayer2.util.Util;

public class ExoPlayerMediaSourceBuilder {

    private HttpDataSource.Factory httpDataSourceFactory;
    private DataSource.Factory dataSourceFactory;
    private DefaultBandwidthMeter bandwidthMeter;
    private Context context;
    private String url;
    private Handler mainHandler = new Handler();

    public ExoPlayerMediaSourceBuilder(Context context, String url) {
        this.context = context;
        this.url = url;

        // Measures bandwidth during playback. Can be null if not required.
        bandwidthMeter = new DefaultBandwidthMeter();

        httpDataSourceFactory = new DefaultHttpDataSourceFactory(Util.getUserAgent(context,
                "ExoPlayerDemo"), bandwidthMeter);

        // Produces DataSource instances through which media data is loaded.
        dataSourceFactory = new DefaultDataSourceFactory(context, bandwidthMeter,
                httpDataSourceFactory);
    }


    public MediaSource getMediaSourceDash() {
        return new DashMediaSource(Uri.parse(url), dataSourceFactory,
                new DefaultDashChunkSource.Factory(dataSourceFactory),
                mainHandler, null);
    }

    public MediaSource getMediaSourceHls() {
        return new HlsMediaSource(Uri.parse(url), dataSourceFactory, mainHandler, null);
    }

    public MediaSource getMediaSourceSs() {
        return new SsMediaSource(Uri.parse(url),
                new DefaultDataSourceFactory(context, null, httpDataSourceFactory),
                new DefaultSsChunkSource.Factory(dataSourceFactory), mainHandler, null);
    }

}
