package com.github.rubensousa.previewseekbar.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.github.rubensousa.previewseekbar.PreviewSeekBar;


public class MainActivity extends AppCompatActivity {

    private PreviewSeekBar previewSeekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        previewSeekBar = (PreviewSeekBar) findViewById(R.id.previewSeekBar);
        View view = findViewById(R.id.previewView);
        previewSeekBar.setPreviewView(view);
    }

}
