package com.github.rubensousa.previewseekbar;


import android.widget.SeekBar;

public interface PreviewView {

    int getProgress();

    int getMax();

    int getThumbOffset();

    void addOnSeekBarChangeListener(SeekBar.OnSeekBarChangeListener listener);

    void removeOnSeekBarChangeListener(SeekBar.OnSeekBarChangeListener listener);
}
