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
    // Base implementation with a standard SeekBar
    implementation 'com.github.rubensousa:previewseekbar:3.0.0'

    // ExoPlayer extension that contains a TimeBar. 
    // Grab this one if you're going to integrate with ExoPlayer
    implementation 'com.github.rubensousa:previewseekbar-exoplayer:2.11.1.0'
}
```

## How to use with a standard SeekBar

1. Setup your layout like the following:

```xml
<FrameLayout
  android:id="@+id/previewFrameLayout"
  android:layout_width="160dp"
  android:layout_height="90dp">

  <ImageView
      android:id="@+id/imageView"
      android:layout_width="match_parent"
      android:layout_height="match_parent" />

</FrameLayout>

<com.github.rubensousa.previewseekbar.PreviewSeekBar
  android:id="@+id/previewSeekBar"
  android:layout_width="match_parent"
  android:layout_height="wrap_content"
  android:layout_marginTop="24dp"
  android:max="800"
  app:previewFrameLayout="@id/previewFrameLayout"/>
```

Place the View you want to use to display the preview in the FrameLayout above. 
In this example it's an ImageView but you can place any View inside.
PreviewSeekBar will animate and show that FrameLayout for you automatically.

**The FrameLayout must have the same parent as the PreviewSeekBar if you want the default animations to work**

2. Create a PreviewLoader and pass it to the PreviewSeekBar

**Note**: A PreviewLoader is an interface that you need to implement yourself. 
This library isn't opinionated about how you actually show a preview.
Check the sample code for an example on how it's done with ExoPlayer using thumbnail sprites.

```java

PreviewSeekBar previewSeekBar = findViewById(R.id.previewSeekBar);

PreviewLoader imagePreviewLoader = ImagePreviewLoader();

previewSeekbar.setPreviewLoader(imagePreviewLoader);
```

3. Customize the PreviewSeekBar

```java
// Disables auto hiding the preview. 
// Default is true, which means the preview is hidden 
// when the user stops scrubbing the PreviewSeekBar
previewSeekBar.setAutoHidePreview(false);

// Shows the preview
previewSeekBar.showPreview();

// Hides the preview
previewSeekBar.hidePreview();

// Disables revealing the preview
previewSeekBar.setPreviewEnabled(false);

// Disables the current animation
previewSeekBar.setPreviewAnimationEnabled(false);

// Change the default animation
previewSeekBar.setPreviewAnimator(new PreviewFadeAnimator());

// Changes the color of the thumb
previewSeekBar.setPreviewThumbTint(Color.RED);

// Listen for scrub touch changes
previewSeekBar.addOnScrubListener(new PreviewBar.OnScrubListener() {
    @Override
    public void onScrubStart(PreviewBar previewBar) {
        
    }

    @Override
    public void onScrubMove(PreviewBar previewBar, int progress, boolean fromUser) {
        
    }

    @Override
    public void onScrubStop(PreviewBar previewBar) {
        
    }
});

// Listen for preview visibility changes
previewSeekBar.addOnPreviewVisibilityListener(new PreviewBar.OnPreviewVisibilityListener() {
    @Override
    public void onVisibilityChanged(PreviewBar previewBar, boolean isPreviewShowing) {
        
    }
});

```

## How to use with ExoPlayer

1. Add a custom controller to your SimpleExoPlayerView

```xml
<com.google.android.exoplayer2.ui.SimpleExoPlayerView
    android:id="@+id/player_view"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    app:controller_layout_id="@layout/exoplayer_controls"/>
```

Here's the sample's exoplayer_controls: https://github.com/rubensousa/PreviewSeekBar/blob/master/sample/src/main/res/layout/exoplayer_controls.xml

The PreviewTimeBar inside exoplayer_controls should be similar to this:

```xml
<FrameLayout
    android:id="@+id/previewFrameLayout"
    android:layout_width="@dimen/video_preview_width"
    android:layout_height="@dimen/video_preview_height"
    android:background="@drawable/video_frame">

    <ImageView
        android:id="@+id/imageView"
        android:layout_width="match_parent"
        android:layout_height="match_parent"/>

</FrameLayout>

<com.github.rubensousa.previewseekbar.exoplayer.PreviewTimeBar
    android:id="@+id/exo_progress"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    app:previewFrameLayout="@id/previewFrameLayout"/>
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
    Copyright 2020 Rúben Sousa
    
    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at
    
        http://www.apache.org/licenses/LICENSE-2.0
    
    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
