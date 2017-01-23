package com.github.rubensousa.previewseekbar.sample;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.SeekBar;

import com.github.rubensousa.previewseekbar.PreviewSeekBar;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;


public class MainActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener,
        Toolbar.OnMenuItemClickListener {

    private ExoPlayerManager exoPlayerManager;
    private SimpleExoPlayerView playerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.main);
        toolbar.setOnMenuItemClickListener(this);
        playerView = (SimpleExoPlayerView) findViewById(R.id.player_view);
        PreviewSeekBar seekBar = (PreviewSeekBar) playerView.findViewById(R.id.exo_progress);

        seekBar.addOnSeekBarChangeListener(this);
        exoPlayerManager = new ExoPlayerManager(playerView, findViewById(R.id.surfaceView));
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        // load video progress
        exoPlayerManager.preview((float) progress / seekBar.getMax());
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        exoPlayerManager.startPreview();
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        exoPlayerManager.stopPreview();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        startActivity(new Intent(this, LocalActivity.class));
        return true;
    }
}
