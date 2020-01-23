# 3.0.0

### Breaking changes

- Migrated to AndroidX
- Renamed PreviewView to PreviewBar
- Renamed getDefaultColor to getScrubberColor
- Renamed setPreviewColorTint/setPreviewColorResourceTint to setPreviewThumbTint/setPreviewThumbTintResource
- Renamed attachPreviewFrameLayout to attachPreviewView
- Minimum supported API changed from 16 to 19

### New features and improvements

- Added support for custom animators. Default animators available:
	- PreviewFadeAnimator (API 19+)
	- PreviewMorphAnimator (API 21+)
- Added support for disabling preview animations with PreviewBar's setPreviewAnimationEnabled
- Allow disabling/enabling the preview mode with PreviewBar's setPreviewEnabled
- Added new attribute previewThumbTint to PreviewSeekBar
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

