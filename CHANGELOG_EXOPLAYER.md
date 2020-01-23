# 2.11.1.0

### Breaking changes

- Migrated to AndroidX
- Minimum supported API changed from 16 to 19

### New features and improvements

- Updated PreviewSeekBar to 3.0.0
- Added new attribute previewEnabled to PreviewTimeBar to enable or disable previews. Defaults to true

### Bug fixes

- Fixed PreviewTimeBar not changing the scrubber color when setPreviewThumbTint is called
- Fixed PreviewTimeBar not using the correct thumb size
- Fixed PreviewTimeBar not moving the preview if the player position changes programmatically

# 2.8.1.0

#### Starting from this version, the previewseekbar-exoplayer extension is tied to the ExoPlayer version

- Use previewseekbar 2.0.0
- Updated ExoPlayer to 2.8.1


# 2.6.0

- Updated ExoPlayer to 2.6.0
- Removed custom LoadControl and TrackSelection classes.
- Updated support library

# 2.5.1

- Add missing implementation of setAdGroupTimesMs https://github.com/google/ExoPlayer/commit/4180b9656d46f26cdf62bb2e4d03cd068d569af5
