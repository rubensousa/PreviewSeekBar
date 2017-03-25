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


import android.content.Context;
import android.net.Uri;
import android.os.Handler;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
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
import com.google.android.exoplayer2.util.Util;

public class ExoPlayerMediaSourceBuilder {

    private DefaultBandwidthMeter bandwidthMeter;
    private Context context;
    private Uri uri;
    private int streamType;
    private Handler mainHandler = new Handler();

    public ExoPlayerMediaSourceBuilder(Context context) {
        this.context = context;
        this.bandwidthMeter = new DefaultBandwidthMeter();
    }

    public void setUri(Uri uri) {
        this.uri = uri;
        this.streamType = Util.inferContentType(uri.getLastPathSegment());
    }

    public MediaSource getMediaSource(boolean preview) {
        switch (streamType) {
            case C.TYPE_SS:
                return new SsMediaSource(uri, new DefaultDataSourceFactory(context, null,
                        getHttpDataSourceFactory(preview)),
                        new DefaultSsChunkSource.Factory(getDataSourceFactory(preview)),
                        mainHandler, null);
            case C.TYPE_DASH:
                return new DashMediaSource(uri,
                        new DefaultDataSourceFactory(context, null,
                                getHttpDataSourceFactory(preview)),
                        new DefaultDashChunkSource.Factory(getDataSourceFactory(preview)),
                        mainHandler, null);
            case C.TYPE_HLS:
                return new HlsMediaSource(uri, getDataSourceFactory(preview), mainHandler, null);
            case C.TYPE_OTHER:
                return new ExtractorMediaSource(uri, getDataSourceFactory(preview),
                        new DefaultExtractorsFactory(), mainHandler, null);
            default: {
                throw new IllegalStateException("Unsupported type: " + streamType);
            }
        }
    }

    private DataSource.Factory getDataSourceFactory(boolean preview) {
        return new DefaultDataSourceFactory(context, preview ? null : bandwidthMeter,
                getHttpDataSourceFactory(preview));
    }

    private DataSource.Factory getHttpDataSourceFactory(boolean preview) {
        return new DefaultHttpDataSourceFactory(Util.getUserAgent(context,
                "ExoPlayerDemo"), preview ? null : bandwidthMeter);
    }
}
