package com.github.rubensousa.previewseekbar;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.AppCompatSeekBar;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.SeekBar;


public class PreviewSeekBar extends AppCompatSeekBar implements SeekBar.OnSeekBarChangeListener {

    private PreviewAnimator animator;
    private View previewView;
    private View morphView;
    private View frameView;
    private boolean firstLayout;
    private boolean isPreviewing;
    private OnSeekBarChangeListener seekBarChangeListener;

    public PreviewSeekBar(Context context) {
        super(context);
        init(context, null);
    }

    public PreviewSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public PreviewSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        super.setOnSeekBarChangeListener(this);

        TypedValue outValue = new TypedValue();

        getContext().getTheme().resolveAttribute(R.attr.colorAccent, outValue, true);
        int colorRes = outValue.resourceId;

        // Create morph view
        morphView = new View(getContext());
        morphView.setBackgroundResource(R.drawable.previewseekbar_morph);
        morphView.setVisibility(View.INVISIBLE);

        // Tint to accent color
        Drawable drawable = morphView.getBackground();
        int colorInt = ContextCompat.getColor(getContext(), colorRes);
        drawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(drawable, colorInt);
        morphView.setBackground(drawable);


        // Create frame view for the circular reveal
        frameView = new View(getContext());
        frameView.setBackgroundResource(colorRes);
        frameView.setVisibility(View.INVISIBLE);

        // Create animator
        animator = new PreviewAnimator(this);
        animator.setMorphView(morphView);
        animator.setFrameView(frameView);
        firstLayout = true;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (getWidth() == 0 || getHeight() == 0) {
            return;
        } else if (firstLayout) {
            // Setup morph view
            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(0, 0);
            layoutParams.width = getResources()
                    .getDimensionPixelSize(R.dimen.previewseekbar_indicator_width);
            layoutParams.height = layoutParams.width;

            ViewGroup.LayoutParams frameLayoutParams
                    = new ViewGroup.LayoutParams(previewView.getWidth(), previewView.getHeight());

            // Add views to the parent layout
            ViewParent parent = getParent();
            if (parent instanceof ViewGroup) {
                ((ViewGroup) parent).addView(morphView, layoutParams);
                ((ViewGroup) parent).addView(frameView, frameLayoutParams);
            }

            firstLayout = false;
        }
    }

    @Override
    public void setOnSeekBarChangeListener(OnSeekBarChangeListener l) {
        seekBarChangeListener = l;
    }

    public void setPreviewView(View view) {
        previewView = view;
        previewView.setVisibility(View.INVISIBLE);
        animator.setPreviewView(view);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        animator.move();
        if (seekBarChangeListener != null) {
            seekBarChangeListener.onProgressChanged(seekBar, progress, fromUser);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        animator.morph();
        if (seekBarChangeListener != null) {
            seekBarChangeListener.onStartTrackingTouch(seekBar);
        }
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        animator.unmorph();
        if (seekBarChangeListener != null) {
            seekBarChangeListener.onStopTrackingTouch(seekBar);
        }
    }
}
