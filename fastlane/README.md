# Fastlane Play Console Release Setup

This project uses Fastlane Android lanes to build a signed release AAB and upload it to Google Play.

## GitHub Internal Release Setup

The `Google Play Internal Release` workflow is the supported CI path for an
internal-testing upload. It runs only through a manual dispatch and calls
`bundle exec fastlane android internal_release`; it cannot publish to closed or
production tracks, and it does not update Play store media.

Before its first use, an authorized GitHub administrator must create the
`google-play-internal` environment, add the required reviewers or deployment
protection rules, and place these environment secrets there:

- `PLAY_STORE_JSON_KEY`
- `ANDROID_KEYSTORE_BASE64`
- `KEYSTORE_PASSWORD`
- `KEY_ALIAS`
- `KEY_PASSWORD`
- `ADMOB_APP_ID`
- `ADMOB_BANNER_ID`

Encode the upload keystore before saving `ANDROID_KEYSTORE_BASE64`. On macOS or
Linux, run this from the directory containing the keystore:

```sh
base64 < upload-keystore.jks | tr -d '\n'
```

Copy the resulting single-line value into the GitHub secret. Keep the original
keystore and all secret values out of the repository and build logs.

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

## Internal Release Operator Flow

1. Increment `versionCode` (and update `versionName` when appropriate) in
   `app/build.gradle.kts`; Play rejects an upload that does not have a new,
   higher version code.
2. Push the immutable candidate commit and wait for the normal CI checks to
   pass.
3. In GitHub Actions, choose **Google Play Internal Release**, select **Run
   workflow**, and complete the manual dispatch for the protected
   `google-play-internal` environment.
4. After the run finishes, download and retain the
   `mathbrainer-internal-release-<commit SHA>` evidence artifact. It contains
   the signed AAB, checksum, release manifest, and mapping outputs needed for
   traceability.
5. In Play Console, confirm the submitted version appears in internal testing
   before inviting testers or starting validation.

Closed or production promotion is a separate owner action in Play Console after
the internal-testing gates in the release runbook are complete.
