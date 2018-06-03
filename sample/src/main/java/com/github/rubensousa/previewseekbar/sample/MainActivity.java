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

package com.github.rubensousa.previewseekbar.sample;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.github.rubensousa.previewseekbar.PreviewView;
import com.github.rubensousa.previewseekbar.exoplayer.PreviewTimeBar;
import com.github.rubensousa.previewseekbar.sample.exoplayer.ExoPlayerManager;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;


public class MainActivity extends AppCompatActivity implements Toolbar.OnMenuItemClickListener, PreviewView.OnPreviewChangeListener {

    private static final int PICK_FILE_REQUEST_CODE = 2;

    private ExoPlayerManager exoPlayerManager;
    private PreviewTimeBar previewTimeBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SimpleExoPlayerView playerView = findViewById(R.id.player_view);
        previewTimeBar = playerView.findViewById(R.id.exo_progress);
        previewTimeBar.addOnPreviewChangeListener(this);
        exoPlayerManager = new ExoPlayerManager(playerView, previewTimeBar,
                (ImageView) findViewById(R.id.imageView), getString(R.string.url_thumbnails));
        exoPlayerManager.play(Uri.parse(getString(R.string.url_dash)));
        previewTimeBar.setPreviewLoader(exoPlayerManager);
        requestFullScreenIfLandscape();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FILE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            exoPlayerManager.play(data.getData());
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        exoPlayerManager.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        exoPlayerManager.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        exoPlayerManager.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        exoPlayerManager.onStop();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if (item.getItemId() == R.id.action_local) {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("video/*.mp4");
            startActivityForResult(intent, PICK_FILE_REQUEST_CODE);
        } else if (item.getItemId() == R.id.action_toggle) {
            if (previewTimeBar.isShowingPreview()) {
                previewTimeBar.hidePreview();
                exoPlayerManager.stopPreview(previewTimeBar.getProgress());
            } else {
                previewTimeBar.showPreview();
                exoPlayerManager.loadPreview(previewTimeBar.getProgress(),
                        previewTimeBar.getMax());
            }
        } else {
            startActivity(new Intent(this, LocalActivity.class));
        }
        return true;
    }

    private void requestFullScreenIfLandscape() {
        if (getResources().getBoolean(R.bool.landscape)) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN);
        } else {
            Toolbar toolbar = findViewById(R.id.toolbar);
            toolbar.inflateMenu(R.menu.main);
            toolbar.setOnMenuItemClickListener(this);
        }
    }


    @Override
    public void onStartPreview(PreviewView previewView, int progress) {
        if (getResources().getBoolean(R.bool.landscape)) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN);
        }
    }

    @Override
    public void onStopPreview(PreviewView previewView, int progress) {
        exoPlayerManager.stopPreview(progress);
    }

    @Override
    public void onPreview(PreviewView previewView, int progress, boolean fromUser) {

    }
}
