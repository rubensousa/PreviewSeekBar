package com.github.rubensousa.previewseekbar;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.FrameLayout;


public class PreviewFrameLayout extends FrameLayout {

    public PreviewFrameLayout(Context context) {
        super(context);
    }

    public PreviewFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PreviewFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public PreviewFrameLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }
}
