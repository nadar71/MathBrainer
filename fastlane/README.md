# Fastlane Play Console Release Setup

This project uses Fastlane Android lanes to build a signed release AAB and upload it to Google Play.

## Required environment

- `PLAY_STORE_JSON_KEY_PATH` = path to your Play service account JSON file  
  (or `PLAY_STORE_JSON_KEY` with inline JSON content).

## Available lanes

- `fastlane android sync_store_media`  
  Copies already-created store assets from `store-media/google-play/` into the Fastlane metadata layout used by Play uploads.
- `fastlane android build_release_bundle`  
  Runs `bundleRelease` locally.
- `fastlane android store_release`  
  Builds the release AAB and uploads it through Google Play.

### `store_release` usage

- Internal track (default):  
  `bundle exec fastlane android store_release`
- Internal track + explicit options:  
  `bundle exec fastlane android store_release track:internal release_status:draft`
- Production rollout:  
  `bundle exec fastlane android store_release track:production release_status:completed`

## Notes

- `prepare_store_media` is done automatically inside `store_release`.
- The lane uploads from `RELEASE_AAB_PATH = app/build/outputs/bundle/release/app-release.aab`.
- `store-media/google-play` already contains all required icons, feature graphic, and screenshots; fastlane just reuses them at release time.
