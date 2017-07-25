package com.github.rubensousa.previewseekbar;


public interface PreviewView {

    int getProgress();

    int getMax();

    int getThumbOffset();

    int getDefaultColor();

    void addOnPreviewChangeListener(OnPreviewChangeListener listener);

    void removeOnPreviewChangeListener(OnPreviewChangeListener listener);

    interface OnPreviewChangeListener {
        void onStartPreview(PreviewView previewView);

        void onStopPreview(PreviewView previewView);

        void onPreview(PreviewView previewView, int progress, boolean fromUser);
    }
}
