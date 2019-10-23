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
import android.widget.ImageView;

import com.bumptech.glide.request.target.Target;
import com.github.rubensousa.previewseekbar.PreviewLoader;
import com.github.rubensousa.previewseekbar.exoplayer.PreviewTimeBar;
import com.github.rubensousa.previewseekbar.sample.glide.GlideApp;
import com.github.rubensousa.previewseekbar.sample.glide.GlideThumbnailTransformation;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.util.Util;


public class ExoPlayerManager implements PreviewLoader {

    private ExoPlayerMediaSourceBuilder mediaSourceBuilder;
    private PlayerView playerView;
    private SimpleExoPlayer player;
    private PreviewTimeBar previewTimeBar;
    private String thumbnailsUrl;
    private ImageView imageView;
    private Player.EventListener eventListener = new Player.EventListener() {
        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
            if (playbackState == Player.STATE_READY && playWhenReady) {
                previewTimeBar.hidePreview();
            }
        }
    };

    public ExoPlayerManager(PlayerView playerView,
                            PreviewTimeBar previewTimeBar, ImageView imageView,
                            String thumbnailsUrl) {
        this.playerView = playerView;
        this.imageView = imageView;
        this.previewTimeBar = previewTimeBar;
        this.mediaSourceBuilder = new ExoPlayerMediaSourceBuilder(playerView.getContext());
        this.thumbnailsUrl = thumbnailsUrl;
    }

    public void play(Uri uri) {
        mediaSourceBuilder.setUri(uri);
    }

    public void onStart() {
        if (Util.SDK_INT > 23) {
            createPlayers();
        }
    }

    public void onResume() {
        if (Util.SDK_INT <= 23) {
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

    public void stopPreview(long position) {
        player.seekTo(position);
        player.setPlayWhenReady(true);
    }

    private void releasePlayers() {
        if (player != null) {
            player.release();
            player = null;
        }
    }

    private void createPlayers() {
        if (player != null) {
            player.release();
        }
        player = createFullPlayer();
        playerView.setPlayer(player);
    }

    private SimpleExoPlayer createFullPlayer() {
        SimpleExoPlayer player = ExoPlayerFactory.newSimpleInstance(playerView.getContext());
        player.setPlayWhenReady(true);
        player.prepare(mediaSourceBuilder.getMediaSource(false));
        player.addListener(eventListener);
        return player;
    }

    @Override
    public void loadPreview(long currentPosition, long max) {
        player.setPlayWhenReady(false);
        GlideApp.with(imageView)
                .load(thumbnailsUrl)
                .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                .transform(new GlideThumbnailTransformation(currentPosition))
                .into(imageView);
    }
}
