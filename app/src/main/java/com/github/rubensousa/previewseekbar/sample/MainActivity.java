package com.github.rubensousa.previewseekbar.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.github.rubensousa.previewseekbar.PreviewSeekBarLayout;


public class MainActivity extends AppCompatActivity {

    private PreviewSeekBarLayout previewSeekBarLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        previewSeekBarLayout = (PreviewSeekBarLayout) findViewById(R.id.previewSeekBarLayout);
    }

}
