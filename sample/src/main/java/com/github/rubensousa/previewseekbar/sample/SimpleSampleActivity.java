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

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.github.rubensousa.previewseekbar.PreviewSeekBar;

public class SimpleSampleActivity extends AppCompatActivity {

    private PreviewSeekBar previewSeekBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seekbar);
        previewSeekBar = findViewById(R.id.previewSeekBar);

        SwitchCompat switchCompat = findViewById(R.id.animationSwitch);
        switchCompat.setOnCheckedChangeListener(
                (buttonView, isChecked) -> previewSeekBar.setPreviewAnimationEnabled(isChecked));
    }


}


