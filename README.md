# PreviewSeekBar

A SeekBar suited for showing a preview of something. As seen in Google Play Movies.


### Google Play Movies

<img src="screenshots/playmovies.gif" width=600></img>

### PreviewSeekBar's sample

<img src="screenshots/sample.gif" width=600></img>


## Build

Add the following to your app's build.gradle:

```groovy
dependencies {
    implementation 'com.github.rubensousa:previewseekbar:1.1'
    implementation 'com.github.rubensousa:previewseekbar-exoplayer:2.6.0'
}
```

## How to use

#### Add the following XML:

```xml
<com.github.rubensousa.previewseekbar.PreviewSeekBarLayout
      android:id="@+id/previewSeekBarLayout"
      android:layout_width="match_parent"
      android:layout_height="wrap_content"
      android:orientation="vertical">

      <FrameLayout
          android:id="@+id/previewFrameLayout"
          android:layout_width="@dimen/video_preview_width"
          android:layout_height="@dimen/video_preview_height">

          <ImageView
              android:id="@+id/imageView"
              android:layout_width="match_parent"
              android:layout_height="match_parent"
              android:background="@color/colorPrimary" />

      </FrameLayout>

      <com.github.rubensousa.previewseekbar.PreviewSeekBar
          android:id="@+id/previewSeekBar"
          android:layout_width="match_parent"
          android:layout_height="wrap_content"
          android:layout_below="@id/previewFrameLayout"
          android:layout_marginTop="25dp"
          android:max="800" />
          
</com.github.rubensousa.previewseekbar.PreviewSeekBarLayout>
```
#### You need to add at least one PreviewSeekBar and a FrameLayout inside PreviewSeekBarLayout, else an exception will be thrown.
PreviewSeekBarLayout extends from RelativeLayout so you can add other views or layouts there. 

#### Create a PreviewLoader and pass it to PreviewSeekBarLayout:



```java
// Create a class that implements this interface and implement your own preview logic there
public interface PreviewLoader {
    void loadPreview(long currentPosition, long max);
}

PreviewLoader loader = new ExoPlayerLoader();
previewSeekBarLayout.setup(loader);
```

## How to use with ExoPlayer

#### Add a custom controller to your SimpleExoPlayerView

```xml
<com.google.android.exoplayer2.ui.SimpleExoPlayerView
    android:id="@+id/player_view"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    app:controller_layout_id="@layout/exoplayer_controls"/>
```

Here's the sample's exoplayer_controls: https://github.com/rubensousa/PreviewSeekBar/blob/master/sample/src/main/res/layout/exoplayer_controls.xml

The PreviewSeekBarLayout inside exoplayer_controls should be similar to this:

```xml
<com.github.rubensousa.previewseekbar.exoplayer.PreviewTimeBarLayout
    android:id="@+id/previewTimeBarLayout"
    android:layout_width="0dp"
    android:layout_height="wrap_content"
    android:layout_weight="1">

    <FrameLayout
        android:id="@+id/previewFrameLayout"
        android:layout_width="@dimen/video_preview_width"
        android:layout_height="@dimen/video_preview_height"
        android:background="@drawable/video_frame"
        android:padding="@dimen/video_frame_width">

        <ImageView
            android:id="@+id/imageView"
            android:layout_width="match_parent"
            android:layout_height="match_parent"/>

    </FrameLayout>

    <com.github.rubensousa.previewseekbar.exoplayer.PreviewTimeBar
        android:id="@+id/exo_progress"
        style="?android:attr/progressBarStyleHorizontal"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_below="@id/previewFrameLayout"
        android:layout_marginTop="10dp"
        android:max="800" />

</com.github.rubensousa.previewseekbar.PreviewSeekBarLayout>
```

#### Create your own ExoPlayerLoader that seeks the video to the current position

In this sample, Glide is used with a custom transformation to crop the thumbnails from a thumbnail sprite.

[GlideThumbnailTransformation](https://github.com/rubensousa/PreviewSeekBar/blob/master/sample/src/main/java/com/github/rubensousa/previewseekbar/sample/glide/GlideThumbnailTransformation.java)

```java
@Override
public void loadPreview(long currentPosition, long max) {
    player.setPlayWhenReady(false);
    GlideApp.with(imageView)
            .load(thumbnailsUrl)
            .override(GlideThumbnailTransformation.IMAGE_WIDTH,
                    GlideThumbnailTransformation.IMAGE_HEIGHT)
            .transform(new GlideThumbnailTransformation(currentPosition))
            .into(imageView);
}
```

## License

    Copyright 2017 The Android Open Source Project
    Copyright 2017 Rúben Sousa
    
    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at
    
        http://www.apache.org/licenses/LICENSE-2.0
    
    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
