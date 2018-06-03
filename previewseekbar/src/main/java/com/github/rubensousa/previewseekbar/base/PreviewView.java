package com.github.rubensousa.previewseekbar.base;


import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.widget.FrameLayout;

public interface PreviewView {

    int getProgress();

    int getMax();

    int getThumbOffset();

    int getDefaultColor();

    boolean isShowingPreview();

    void showPreview();

    void hidePreview();

    void setPreviewLoader(PreviewLoader previewLoader);

    void setPreviewColorTint(@ColorInt int color);

    void setPreviewColorResourceTint(@ColorRes int color);

    void attachPreviewFrameLayout(FrameLayout frameLayout);

    void addOnPreviewChangeListener(OnPreviewChangeListener listener);

    void removeOnPreviewChangeListener(OnPreviewChangeListener listener);

    interface OnPreviewChangeListener {
        void onStartPreview(PreviewView previewView);

        void onStopPreview(PreviewView previewView);

        void onPreview(PreviewView previewView, int progress, boolean fromUser);
    }
}
