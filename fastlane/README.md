# Fastlane Play Console Release Setup

The `Google Play Internal Release` workflow is the supported CI path for an
internal-testing upload. It runs only through a manual dispatch, builds one AAB,
verifies and encrypts its evidence, and calls
`bundle exec fastlane android internal_release` to upload that already-built AAB.
The workflow cannot publish to closed or production tracks and does not update
Play store media.

## Protected GitHub environment

An authorized GitHub administrator must create the `google-play-internal`
environment before first use:

1. Add required reviewers so the environment approval happens before the job
   builds or uploads the candidate.
2. Under deployment branches and tags, choose **Selected branches and tags**.
   Allow branch `master` and tags matching `v*`. These settings match the
   workflow's only trusted refs, `refs/heads/master` and `refs/tags/v*`.
3. Add exactly these eight environment secrets:

   - `PLAY_STORE_JSON_KEY`
   - `ANDROID_KEYSTORE_BASE64`
   - `KEYSTORE_PASSWORD`
   - `KEY_ALIAS`
   - `KEY_PASSWORD`
   - `ADMOB_APP_ID`
   - `ADMOB_BANNER_ID`
   - `RELEASE_EVIDENCE_PASSWORD`

The eighth secret is the explicit cost of protecting release evidence in this
public repository. Make `RELEASE_EVIDENCE_PASSWORD` a high-entropy passphrase,
retain it separately in a private password manager, and never place it in the
repository, an issue, a release note, or a shell command line. The workflow
scopes it only to the encryption step and never prints it.

Encode the upload keystore before saving `ANDROID_KEYSTORE_BASE64`. On macOS or
Linux, run this from the directory containing the keystore:

```sh
base64 < upload-keystore.jks | tr -d '\n'
```

Keep the original keystore and every secret value outside the repository and
build logs.

## Least-privilege Play service account

Create a dedicated Google Cloud service account for internal release automation.
In Play Console **Users and permissions**, invite that account, choose **App
permissions**, and select only Math Brainer (`eu.indiewalkabout.mathbrainer`).
Grant only **Release apps to testing tracks**, the upload permission required by
this workflow.

Do not grant account-wide access, **Admin**, **Release to production, exclude
devices, and use Play App Signing**, **Manage store presence**, financial access,
or **Manage testing tracks and edit tester lists**. Put only this dedicated
account's JSON in `PLAY_STORE_JSON_KEY`, and revoke the identity when it is no
longer used.

## Available lanes

- `bundle exec fastlane android build_release_bundle` runs `bundleRelease`.
- `bundle exec fastlane android internal_release` uploads the fixed
  `app/build/outputs/bundle/release/app-release.aab` only after it matches the
  adjacent `.sha256` file. It does not build and cannot change track or status.
- `bundle exec fastlane android sync_store_media` copies existing assets from
  `store-media/google-play/` into Fastlane's metadata layout.
- `bundle exec fastlane android store_release` remains the separate, explicit
  store-media/general-release lane for authorized local operation. The protected
  internal workflow never calls it.

For local Fastlane authentication outside the protected workflow,
`PLAY_STORE_JSON_KEY_PATH` may point to a service-account JSON file, or
`PLAY_STORE_JSON_KEY` may contain inline JSON. Local use is not a substitute for
the protected workflow's artifact and approval chain.

## Internal release operator flow

1. Increment `versionCode` and update `versionName` when appropriate in
   `app/build.gradle.kts`.
2. Merge the immutable candidate to `master` or create the intended `v*` tag,
   then wait for normal CI to pass.
3. In GitHub Actions, choose **Google Play Internal Release**, select **Run
   workflow**, choose the trusted ref, and complete the manual dispatch.
4. Approve the `google-play-internal` environment. This approval authorizes the
   workflow to build, verify, encrypt evidence, and upload one candidate.
5. Confirm the job verified the AAB and mappings before Play upload and that the
   post-upload SHA-256 check passed.
6. Download `mathbrainer-internal-release-<commit SHA>`. It contains
   `release-evidence.tar.gz.enc` plus a non-sensitive checksum and manifest; it
   never contains a plaintext AAB, `mapping.txt`, or `resources.txt`.
7. Confirm the submitted version appears in Play internal testing. Downstream
   approval and testing use this exact workflow-produced AAB; do not rebuild or
   upload a second candidate through Internal App Sharing.

Closed or production promotion is a separate owner action in Play Console after
the runbook's internal-testing gates are complete.

## Decrypt and retain release evidence

Work in a private directory. Read the retained passphrase without echoing it,
decrypt with the same AES-256-CBC and PBKDF2 settings, then extract the archive:

```sh
read -rsp "Evidence password: " RELEASE_EVIDENCE_PASSWORD
export RELEASE_EVIDENCE_PASSWORD
openssl enc -d -aes-256-cbc -pbkdf2 -iter 100000 \
  -in release-evidence.tar.gz.enc \
  -out release-evidence.tar.gz \
  -pass env:RELEASE_EVIDENCE_PASSWORD
unset RELEASE_EVIDENCE_PASSWORD
tar -xzf release-evidence.tar.gz
```

Compare the extracted AAB against `app-release.aab.sha256`. Retain the decrypted
AAB and version-matched mappings only in the private release archive, keep the
passphrase in the private password manager, and remove temporary plaintext
copies when the review is complete.
