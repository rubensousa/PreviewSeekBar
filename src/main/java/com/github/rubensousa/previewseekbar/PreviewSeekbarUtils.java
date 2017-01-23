package com.github.rubensousa.previewseekbar;


import android.view.View;

class PreviewSeekbarUtils {

    public static int getRadius(View view) {
        return (int) Math.hypot(view.getWidth() / 2, view.getHeight() / 2);
    }

    public static int getCenterX(View view) {
        return view.getWidth() / 2;
    }

    public static int getCenterY(View view) {
        return view.getHeight() / 2;
    }
}
