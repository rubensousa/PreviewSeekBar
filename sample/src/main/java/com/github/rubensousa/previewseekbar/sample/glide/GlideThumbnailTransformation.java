/*
 * Copyright 2017 Rúben Sousa
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

package com.github.rubensousa.previewseekbar.sample.glide;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

import java.nio.ByteBuffer;
import java.security.MessageDigest;


public class GlideThumbnailTransformation extends BitmapTransformation {

    public static final int MAX_LINES = 7;
    public static final int MAX_COLUMNS = 7;
    public static final int THUMBNAIL_WIDTH = 120;
    public static final int THUMBNAIL_HEIGHT = 67;
    public static final int IMAGE_WIDTH = 840;
    public static final int IMAGE_HEIGHT = 469;
    public static final int THUMBNAILS_EACH = 5000; // millisseconds

    private int x;
    private int y;

    public GlideThumbnailTransformation(long position) {
        int square = (int) (Math.floor(position / THUMBNAILS_EACH));
        y = square / MAX_LINES;
        x = square - y * MAX_COLUMNS;
    }

    private int getX() {
        return x;
    }

    private int getY() {
        return y;
    }

    @Override
    protected Bitmap transform(@NonNull BitmapPool pool, @NonNull Bitmap toTransform,
                               int outWidth, int outHeight) {
        int startX = x * toTransform.getWidth() / MAX_COLUMNS;
        int startY = y * toTransform.getHeight() / MAX_LINES;
        return Bitmap.createBitmap(toTransform, startX, startY,
                toTransform.getWidth() / MAX_COLUMNS, toTransform.getHeight() / MAX_LINES);
    }

    @Override
    public void updateDiskCacheKey(MessageDigest messageDigest) {
        byte[] data = ByteBuffer.allocate(8).putInt(x).putInt(y).array();
        messageDigest.update(data);
    }

    @Override
    public int hashCode() {
        return (String.valueOf(x) + String.valueOf(y)).hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof GlideThumbnailTransformation)) {
            return false;
        }
        GlideThumbnailTransformation transformation = (GlideThumbnailTransformation) obj;
        return transformation.getX() == x && transformation.getY() == y;
    }
}
