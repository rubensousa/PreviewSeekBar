package com.github.rubensousa.previewseekbar;


import android.os.Build;
import android.view.View;
import android.widget.SeekBar;

class PreviewDelegate implements SeekBar.OnSeekBarChangeListener {

    private PreviewAnimator animator;
    private boolean showing;
    private boolean startTouch;

    public PreviewDelegate(PreviewSeekBarLayout previewSeekBarLayout) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            this.animator = new PreviewAnimatorLollipopImpl(previewSeekBarLayout);
        } else {
            this.animator = new PreviewAnimatorImpl(previewSeekBarLayout);
        }

        previewSeekBarLayout.getPreviewFrameLayout().setVisibility(View.INVISIBLE);
        previewSeekBarLayout.getMorphView().setVisibility(View.INVISIBLE);
        previewSeekBarLayout.getFrameView().setVisibility(View.INVISIBLE);
        previewSeekBarLayout.getSeekBar().addOnSeekBarChangeListener(this);
    }

    public boolean isShowing() {
        return showing;
    }

    public void show() {
        if (!showing) {
            animator.show();
            showing = true;
        }
    }

    public void hide() {
        if (showing) {
            animator.hide();
            showing = false;
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        animator.move();
        if (!showing && !startTouch && fromUser) {
            animator.show();
            showing = true;
        }
        startTouch = false;
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        startTouch = true;
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if (showing) {
            animator.hide();
        }
        showing = false;
        startTouch = false;
    }

}
