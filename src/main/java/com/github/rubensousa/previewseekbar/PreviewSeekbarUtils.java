package com.github.rubensousa.previewseekbar;


import android.view.View;

public class PreviewSeekbarUtils {

    public static int getRadius(View view) {
        return (int) Math.hypot(view.getWidth() / 2, view.getHeight() / 2);
    }

    public static int getCenterX(View view) {
        return (int) (view.getX() + view.getWidth() / 2f);
    }

    public static int getCenterY(View view) {
        return (int) (view.getY() + view.getHeight() / 2f);
    }
}
