/*
 * Copyright 2018 Rúben Sousa
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.rubensousa.previewseekbar;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

public class PreviewDelegate implements PreviewView.OnPreviewChangeListener {

    private FrameLayout previewFrameLayout;
    private View morphView;
    private View previewFrameView;
    private ViewGroup previewParent;
    private PreviewAnimator animator;
    private PreviewView previewView;
    private PreviewLoader previewLoader;

    private int scrubberColor;
    private boolean showing;
    private boolean startTouch;
    private boolean setup;
    private boolean enabled;

    public PreviewDelegate(PreviewView previewView, int scrubberColor) {
        this.previewView = previewView;
        this.previewView.addOnPreviewChangeListener(this);
        this.scrubberColor = scrubberColor;
    }

    public void setPreviewLoader(PreviewLoader previewLoader) {
        this.previewLoader = previewLoader;
    }

    public void onLayout(ViewGroup previewParent, int frameLayoutId) {
        if (!setup) {
            this.previewParent = previewParent;
            FrameLayout frameLayout = findFrameLayout(previewParent, frameLayoutId);
            if (frameLayout != null) {
                attachPreviewFrameLayout(frameLayout);
            }
        }
    }

    public void attachPreviewFrameLayout(FrameLayout frameLayout) {
        if (setup) {
            return;
        }
        this.previewParent = (ViewGroup) frameLayout.getParent();
        this.previewFrameLayout = frameLayout;
        inflateViews(frameLayout);
        morphView.setVisibility(View.INVISIBLE);
        previewFrameLayout.setVisibility(View.INVISIBLE);
        previewFrameView.setVisibility(View.INVISIBLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            animator = new PreviewAnimatorLollipopImpl(previewParent, previewView, morphView,
                    previewFrameLayout, previewFrameView);
        } else {
            animator = new PreviewAnimatorImpl(previewParent, previewView, morphView,
                    previewFrameLayout, previewFrameView);
        }
        setup = true;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public boolean isShowing() {
        return showing;
    }

    public void show() {
        if (!showing && setup) {
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

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setPreviewColorTint(@ColorInt int color) {
        Drawable drawable = DrawableCompat.wrap(morphView.getBackground());
        DrawableCompat.setTint(drawable, color);
        morphView.setBackground(drawable);
        previewFrameView.setBackgroundColor(color);
    }

    public void setPreviewColorResourceTint(@ColorRes int color) {
        setPreviewColorTint(ContextCompat.getColor(previewParent.getContext(), color));
    }

    @Override
    public void onStartPreview(PreviewView previewView, int progress) {
        if (enabled) {
            startTouch = true;
        }
    }

    @Override
    public void onStopPreview(PreviewView previewView, int progress) {
        if (showing) {
            animator.hide();
        }
        showing = false;
        startTouch = false;
    }

    @Override
    public void onPreview(PreviewView previewView, int progress, boolean fromUser) {
        if (setup) {
            if (!showing && !startTouch && fromUser && enabled) {
                show();
            } else if (showing) {
                animator.move();
            }
            if (previewLoader != null && showing) {
                previewLoader.loadPreview(progress, previewView.getMax());
            }
        }
        startTouch = false;
    }

    public boolean isSetup() {
        return setup;
    }

    @SuppressWarnings("SuspiciousNameCombination")
    private void inflateViews(FrameLayout frameLayout) {

        // Create morph view
        morphView = new View(frameLayout.getContext());
        morphView.setBackgroundResource(R.drawable.previewseekbar_morph);

        // Setup morph view
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(0, 0);
        layoutParams.width = frameLayout.getResources()
                .getDimensionPixelSize(R.dimen.previewseekbar_indicator_width);
        layoutParams.height = layoutParams.width;
        previewParent.addView(morphView, layoutParams);

        // Create frame view for the circular reveal
        previewFrameView = new View(frameLayout.getContext());
        FrameLayout.LayoutParams frameLayoutParams
                = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        frameLayout.addView(previewFrameView, frameLayoutParams);

        // Apply same color for the morph and frame views
        setPreviewColorTint(scrubberColor);
        frameLayout.requestLayout();
    }

    @Nullable
    private FrameLayout findFrameLayout(ViewGroup parent, int id) {
        if (id == View.NO_ID || parent == null) {
            return null;
        }
        for (int i = 0; i < parent.getChildCount(); i++) {
            View child = parent.getChildAt(i);
            if (child.getId() == id && child instanceof FrameLayout) {
                return (FrameLayout) child;
            }
        }
        return null;
    }
}
