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

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;

import androidx.annotation.OptIn;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.ui.PlayerView;

import com.github.rubensousa.previewseekbar.PreviewBar;
import com.github.rubensousa.previewseekbar.PreviewSeekBar;
import com.github.rubensousa.previewseekbar.animator.PreviewFadeAnimator;
import com.github.rubensousa.previewseekbar.animator.PreviewMorphAnimator;
import com.github.rubensousa.previewseekbar.media3.PreviewTimeBar;

public class MainActivity extends AppCompatActivity {

    private ExoPlayerManager exoPlayerManager;
    private PreviewTimeBar previewTimeBar;
    private PreviewSeekBar previewSeekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PlayerView playerView = findViewById(R.id.player_view);
        previewTimeBar = playerView.findViewById(R.id.exo_progress);
        previewSeekBar = findViewById(R.id.previewSeekBar);

        previewTimeBar.addOnPreviewVisibilityListener((previewBar, isPreviewShowing) -> {
            Log.d("PreviewShowing", String.valueOf(isPreviewShowing));
        });

        previewTimeBar.addOnScrubListener(new PreviewBar.OnScrubListener() {
            @Override
            public void onScrubStart(PreviewBar previewBar) {
                Log.d("Scrub", "START");
            }

            @Override
            public void onScrubMove(PreviewBar previewBar, int progress, boolean fromUser) {
                Log.d("Scrub", "MOVE to " + progress / 1000 + " FROM USER: " + fromUser);
            }

            @Override
            public void onScrubStop(PreviewBar previewBar) {
                Log.d("Scrub", "STOP");
            }
        });

        exoPlayerManager = new ExoPlayerManager(playerView, previewTimeBar,
                findViewById(R.id.imageView));

        setupOptions();
        requestFullScreenIfLandscape();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            requestFullScreenIfLandscape();
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

    @OptIn(markerClass = UnstableApi.class)
    private void setupOptions() {
        // Enable or disable the previews
        SwitchCompat previewSwitch = findViewById(R.id.previewEnabledSwitch);
        previewSwitch.setOnCheckedChangeListener(
                (buttonView, isChecked) -> {
                    previewTimeBar.setPreviewEnabled(isChecked);
                    previewSeekBar.setPreviewEnabled(isChecked);
                }
        );

        // Enable or disable auto-hide mode of previews
        SwitchCompat previewAutoHideSwitch = findViewById(R.id.previewAutoHideSwitch);
        previewAutoHideSwitch.setOnCheckedChangeListener(
                (buttonView, isChecked) -> {
                    exoPlayerManager.setResumeVideoOnPreviewStop(isChecked);
                    previewTimeBar.setAutoHidePreview(isChecked);
                    previewSeekBar.setAutoHidePreview(isChecked);
                }
        );

        // Change the animations
        RadioGroup animationRadioGroup = findViewById(R.id.previewAnimationRadioGroup);
        animationRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.noAnimationRadioButton) {
                previewTimeBar.setPreviewAnimationEnabled(false);
                previewSeekBar.setPreviewAnimationEnabled(false);
            } else {
                previewTimeBar.setPreviewAnimationEnabled(true);
                previewSeekBar.setPreviewAnimationEnabled(true);
                if (checkedId == R.id.fadeAnimationRadioButton) {
                    previewTimeBar.setPreviewAnimator(new PreviewFadeAnimator());
                    previewSeekBar.setPreviewAnimator(new PreviewFadeAnimator());
                } else {
                    previewTimeBar.setPreviewAnimator(new PreviewMorphAnimator());
                    previewSeekBar.setPreviewAnimator(new PreviewMorphAnimator());
                }
            }
        });

        // Toggle previews
        Button toggleButton = findViewById(R.id.previewToggleButton);
        toggleButton.setOnClickListener(v -> {
            if (previewTimeBar.isShowingPreview()) {
                previewTimeBar.hidePreview();
            } else {
                previewTimeBar.showPreview();
                exoPlayerManager.loadPreview(previewTimeBar.getProgress(),
                        previewTimeBar.getMax());
            }
            if (previewSeekBar.isShowingPreview()) {
                previewSeekBar.hidePreview();
            } else {
                previewSeekBar.showPreview();
            }
        });

        // Change colors
        Button changeColorsButton = findViewById(R.id.previewToggleColors);
        changeColorsButton.setOnClickListener(v -> {
            final int seekBarColor = previewSeekBar.getScrubberColor();
            final int timeBarColor = previewTimeBar.getScrubberColor();
            previewSeekBar.setPreviewThumbTint(timeBarColor);
            previewSeekBar.setProgressTint(timeBarColor);
            previewTimeBar.setPreviewThumbTint(seekBarColor);
            previewTimeBar.setPlayedColor(seekBarColor);
        });

    }

    private void requestFullScreenIfLandscape() {
        if (getResources().getBoolean(R.bool.landscape)) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_IMMERSIVE
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            // Hide the nav bar and status bar
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN);
        }
    }

}
