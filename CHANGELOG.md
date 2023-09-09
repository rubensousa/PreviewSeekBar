# 3.1.1

- Bumped `androidx.appcompat:appcompat` to 1.6.1

# 3.1.0

- Bumped `androidx.appcompat:appcompat` to 1.5.1
- Released to mavenCentral

# 3.0.0


### Breaking changes

- Migrated to AndroidX
- Renamed PreviewView to PreviewBar
- Renamed getDefaultColor to getScrubberColor
- Renamed setPreviewColorTint/setPreviewColorResourceTint to setPreviewThumbTint/setPreviewThumbTintResource
- Renamed attachPreviewFrameLayout to attachPreviewView
- Renamed OnPreviewChangeListener to OnScrubListener
- Minimum supported API changed from 16 to 19

### New features and improvements

- Added support for custom animators. Default animators available:
	- PreviewFadeAnimator (API 19+)
	- PreviewMorphAnimator (API 21+)
- Added support for listening to preview visibility changes with OnVisibilityChangeListener
- Added support for disabling preview animations with setPreviewAnimationEnabled
- Added support for disabling/enabling auto hiding of previews with setAutoHidePreview. Includes new xml attribute previewAutoHide
- Allow disabling/enabling the preview mode with setPreviewEnabled. Includes new xml attribute previewEnabled
- Added new attribute previewThumbTint to PreviewSeekBar to change the scrubber color
- Added new setProgressTint and setProgressTintResource to tint the PreviewSeekBar progress color
- Instead of restarting the morph animation completely,
PreviewMorphAnimator now resumes from where it was previously animating

### Bug fixes
- Fixed issue with the morph animation on PreviewSeekBar.
- Fixed issue with tinting on PreviewSeekBar not being applied correctly.

# 2.0.0

- Attach the preview frame's container automatically without needing a custom ViewGroup
- Improved the morph animation
- Pass current progress in PreviewView.OnPreviewChangeListener

# 1.2

- Added support for enabled state
- Speed up animations a bit

# 1.1

- Removed string not used
- Updated dependencies

# 1.0

- First release

