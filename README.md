# PreviewSeekBar

A SeekBar suited for showing a video preview. As seen in Google Play Movies

### Google Play Movies

<img src="screenshots/playmovies.gif" width=600></img>

### PreviewSeekBar's sample

<img src="screenshots/sample.gif" width=600></img>


## Build

Add the following to your app's build.gradle:

```groovy
dependencies {
    // Base implementation with a standard SeekBar
    implementation 'com.github.rubensousa:previewseekbar:3.1.1'
    // Media3 extension that contains a TimeBar. 
    implementation 'com.github.rubensousa:previewseekbar-media3:1.1.1.0'
}
```

## How to use with Media3

### Add a custom controller to your PlayerView

```xml
<androidx.media3.ui.PlayerView
    android:id="@+id/playerView"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    app:controller_layout_id="@layout/exoplayer_controls"/>
```

Here's the sample's exoplayer_controls: https://github.com/rubensousa/PreviewSeekBar/blob/master/sample/src/main/res/layout/exoplayer_controls.xml

### Change your TimeBar to a PreviewTimeBar

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
    android:layout_marginStart="8dp"
    android:layout_marginEnd="8dp"
    app:previewAnimationEnabled="true"
    app:previewFrameLayout="@id/previewFrameLayout"/>
```

Place the View you want to use to display the preview in the FrameLayout above. 
In this example it's an ImageView but you can place any View inside.
PreviewTimeBar will animate and show that FrameLayout for you automatically.

**The FrameLayout must have the same parent as the PreviewTimeBar if you want the default animations to work**

### Create a PreviewLoader and pass it to the PreviewTimeBar

**Note**: A PreviewLoader is an interface that you need to implement yourself. 
This library isn't opinionated about how you actually show a preview.
Check the sample code for an example on how it can be done using thumbnail sprites.

```java
PreviewLoader imagePreviewLoader = ImagePreviewLoader();

previewTimeBar.setPreviewLoader(imagePreviewLoader);
```

In this project's sample, Glide is used with a custom transformation to crop the thumbnails from a thumbnail sprite.

[GlideThumbnailTransformation](https://github.com/rubensousa/PreviewSeekBar/blob/master/sample/src/main/java/com/github/rubensousa/previewseekbar/sample/glide/GlideThumbnailTransformation.java)

```java
@Override
public void loadPreview(long currentPosition, long max) {
    GlideApp.with(imageView)
            .load(thumbnailsUrl)
            .override(GlideThumbnailTransformation.IMAGE_WIDTH,
                    GlideThumbnailTransformation.IMAGE_HEIGHT)
            .transform(new GlideThumbnailTransformation(currentPosition))
            .into(imageView);
}
```

### Listen for scrub events to control playback state

When the user starts scrubbing the PreviewTimeBar, you should pause the video playback
After the user is done selecting the new video position, you can resume it.

```java
previewTimeBar.addOnScrubListener(new PreviewBar.OnScrubListener() {
    @Override
    public void onScrubStart(PreviewBar previewBar) {
        player.setPlayWhenReady(false);
    }

    @Override
    public void onScrubMove(PreviewBar previewBar, int progress, boolean fromUser) {
        
    }

    @Override
    public void onScrubStop(PreviewBar previewBar) {
        player.setPlayWhenReady(true);
    }
});
```

### Customize the PreviewTimeBar

Available XML attributes:

```xml
<attr name="previewAnimationEnabled" format="boolean" />
<attr name="previewEnabled" format="boolean" />
<attr name="previewAutoHide" format="boolean" />
```

```java
// Disables auto hiding the preview. 
// Default is true, which means the preview is hidden 
// when the user stops scrubbing the PreviewSeekBar
previewTimeBar.setAutoHidePreview(false);

// Shows the preview
previewTimeBar.showPreview();

// Hides the preview
previewTimeBar.hidePreview();

// Disables revealing the preview
previewTimeBar.setPreviewEnabled(false);

// Disables the current animation
previewTimeBar.setPreviewAnimationEnabled(false);

// Change the default animation
previewTimeBar.setPreviewAnimator(new PreviewFadeAnimator());

// Changes the color of the thumb
previewTimeBar.setPreviewThumbTint(Color.RED);

// Listen for scrub touch changes
previewTimeBar.addOnScrubListener(new PreviewBar.OnScrubListener() {
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
previewTimeBar.addOnPreviewVisibilityListener(new PreviewBar.OnPreviewVisibilityListener() {
    @Override
    public void onVisibilityChanged(PreviewBar previewBar, boolean isPreviewShowing) {
        
    }
});

```

## How to use with a standard SeekBar

### Setup your layout like the following:

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

### Create a PreviewLoader and pass it to the PreviewSeekBar

```java

PreviewSeekBar previewSeekBar = findViewById(R.id.previewSeekBar);

PreviewLoader imagePreviewLoader = ImagePreviewLoader();

previewSeekbar.setPreviewLoader(imagePreviewLoader);
```

### Customization

Available XML attributes for styling:

```xml
<attr name="previewAnimationEnabled" format="boolean" />
<attr name="previewEnabled" format="boolean" />
<attr name="previewThumbTint" format="color" />
<attr name="previewAutoHide" format="boolean" />
```

## Important note

**This library is just an UI component for displaying previews. It doesn't handle generating thumbnail sprites from videos.**


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
