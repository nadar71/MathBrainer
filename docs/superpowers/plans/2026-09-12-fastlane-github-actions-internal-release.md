# Fastlane and GitHub Actions Internal Release Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Add a manually dispatched, protected GitHub Actions workflow that builds a production-signed Math Brainer AAB and uploads only that bundle to Google Play's internal testing track through Fastlane.

**Architecture:** A fixed-purpose Fastlane lane owns the Gradle build and Play upload, while a dedicated GitHub Actions workflow reconstructs credentials under `RUNNER_TEMP`, creates release evidence, invokes the lane, and archives the evidence. A small Ruby configuration test guards the security boundaries without needing live credentials or a Play upload.

**Tech Stack:** Android Gradle Plugin 8.9.1, Gradle wrapper, Ruby/Bundler, Fastlane 2.239.0, Minitest, GitHub Actions, Google Play Developer API

**Spec:** `docs/superpowers/specs/2026-09-11-fastlane-github-actions-internal-release-design.md`

## Global Constraints

- The workflow is triggered only by `workflow_dispatch` and publishes only to Google Play track `internal` with release status `completed`.
- The publishing job uses the protected GitHub environment `google-play-internal` and never writes credentials inside the repository checkout.
- Routine app releases upload only the AAB; metadata, changelogs, screenshots, images, and APKs are skipped.
- Required secrets are `PLAY_STORE_JSON_KEY`, `ANDROID_KEYSTORE_BASE64`, `KEYSTORE_PASSWORD`, `KEY_ALIAS`, `KEY_PASSWORD`, `ADMOB_APP_ID`, and `ADMOB_BANNER_ID`.
- GitHub Actions are pinned to immutable commit SHAs and annotated with their release tags.
- Fastlane is pinned to version `2.239.0`; all Ruby dependencies are resolved in `Gemfile.lock` and invoked through `bundle exec`.
- Existing unrelated staged and unstaged files must not be modified, committed, or reformatted.

---

### Task 1: Pin the release toolchain and guard the Fastlane lane

**Files:**
- Create: `Gemfile`
- Create: `Gemfile.lock`
- Create: `test/release_configuration_test.rb`
- Modify: `fastlane/Fastfile`

**Interfaces:**
- Consumes: existing `build_release_bundle` lane and `RELEASE_AAB_PATH` constant.
- Produces: `bundle exec fastlane android internal_release`, fixed to Play track `internal` and status `completed`.

- [ ] **Step 1: Add a failing configuration test**

Create `test/release_configuration_test.rb` with Minitest assertions that read `fastlane/Fastfile` and require a lane named `internal_release`, `track: "internal"`, `release_status: "completed"`, `skip_upload_metadata: true`, `skip_upload_changelogs: true`, `skip_upload_images: true`, `skip_upload_screenshots: true`, `skip_upload_apk: true`, and no options argument on the lane.

```ruby
# frozen_string_literal: true

require "minitest/autorun"

class ReleaseConfigurationTest < Minitest::Test
  ROOT = File.expand_path("..", __dir__)
  FASTFILE = File.read(File.join(ROOT, "fastlane", "Fastfile"))

  def test_internal_lane_has_fixed_destination_and_bundle_only_upload
    lane = FASTFILE.match(/lane :internal_release do\n(?<body>.*?)\n  end/m)
    refute_nil lane, "internal_release lane is missing or accepts options"

    body = lane[:body]
    assert_includes body, 'track: "internal"'
    assert_includes body, 'release_status: "completed"'
    %w[apk changelogs metadata images screenshots].each do |kind|
      assert_includes body, "skip_upload_#{kind}: true"
    end
  end
end
```

- [ ] **Step 2: Run the test and confirm the lane is absent**

Run: `ruby test/release_configuration_test.rb`

Expected: one failure stating that `internal_release` is missing.

- [ ] **Step 3: Pin Fastlane and resolve the lockfile**

Create `Gemfile`:

```ruby
# frozen_string_literal: true

source "https://rubygems.org"

gem "fastlane", "2.239.0"
```

Run: `bundle lock` and `bundle check`.

Expected: `Gemfile.lock` is generated and `bundle check` exits successfully after dependencies are installed or already available.

- [ ] **Step 4: Implement the fixed internal release lane**

Add a lane to `fastlane/Fastfile` that validates `PLAY_STORE_JSON_KEY`, calls `build_release_bundle`, verifies the AAB exists, and calls `upload_to_play_store` with the exact fixed settings asserted by the test. Pass the service-account JSON as `json_key_data` and do not call `sync_store_media`.

```ruby
desc "Build and upload the release AAB to Google Play internal testing."
lane :internal_release do
  json_key_data = ENV["PLAY_STORE_JSON_KEY"]
  UI.user_error!("Set PLAY_STORE_JSON_KEY before running this lane.") if json_key_data.to_s.empty?

  build_release_bundle
  absolute_aab_path = File.join(project_root, RELEASE_AAB_PATH)
  UI.user_error!("Missing release bundle: #{absolute_aab_path}") unless File.exist?(absolute_aab_path)

  upload_to_play_store(
    package_name: APP_PACKAGE_NAME,
    track: "internal",
    release_status: "completed",
    aab: absolute_aab_path,
    json_key_data: json_key_data,
    skip_upload_apk: true,
    skip_upload_changelogs: true,
    skip_upload_metadata: true,
    skip_upload_images: true,
    skip_upload_screenshots: true
  )
end
```

- [ ] **Step 5: Verify Ruby and Fastlane configuration**

Run:

```bash
ruby test/release_configuration_test.rb
ruby -c fastlane/Fastfile
bundle exec fastlane lanes
git diff --check -- Gemfile Gemfile.lock fastlane/Fastfile test/release_configuration_test.rb
```

Expected: the test passes, Ruby reports `Syntax OK`, Fastlane lists `android internal_release`, and the diff check emits no errors.

- [ ] **Step 6: Commit the isolated toolchain change**

```bash
git add Gemfile Gemfile.lock fastlane/Fastfile test/release_configuration_test.rb
git commit -m "build: add internal Play release lane"
```

### Task 2: Add the protected manual GitHub Actions workflow

**Files:**
- Create: `.github/workflows/release-internal.yml`
- Modify: `test/release_configuration_test.rb`

**Interfaces:**
- Consumes: the `internal_release` lane and seven GitHub environment secrets listed in Global Constraints.
- Produces: manual workflow `Google Play Internal Release` and artifact `mathbrainer-internal-release-<commit SHA>` containing the AAB, mapping outputs, checksum, and release manifest.

- [ ] **Step 1: Extend the test with workflow security assertions**

Read `.github/workflows/release-internal.yml` as text and assert it contains `workflow_dispatch:`, `environment: google-play-internal`, `cancel-in-progress: false`, every required `${{ secrets.NAME }}` mapping, `${{ runner.temp }}/mathbrainer-upload.jks`, `bundle exec fastlane android internal_release`, `jarsigner -verify`, `sha256sum`, `if: always()`, `retention-days: 30`, and commit-SHA-form `uses:` references. Assert it does not contain `push:`, `pull_request:`, `schedule:`, `track: production`, or a workflow input for `track`.

- [ ] **Step 2: Run the test and confirm the workflow is absent**

Run: `ruby test/release_configuration_test.rb`

Expected: a file-not-found error for `.github/workflows/release-internal.yml`.

- [ ] **Step 3: Implement the manual internal-release workflow**

Create one job with `permissions: contents: read`, repository-wide non-cancelling concurrency, `ubuntu-24.04`, a 45-minute timeout, and `environment: google-play-internal`. Use the same immutable checkout, Java, and Gradle setup action SHAs as `.github/workflows/android-ci.yml`, plus an immutable `ruby/setup-ruby` SHA annotated with its release tag. Configure Bundler caching.

Map secrets to environment variables, decode the keystore with `base64 --decode` into `${RUNNER_TEMP}`, verify required values without printing them, and set `KEYSTORE_FILE` to the temporary path. Run the configuration test before building.

Invoke `bundle exec fastlane android internal_release`. After Fastlane returns, run `jarsigner -verify -verbose -certs` against `app/build/outputs/bundle/release/app-release.aab`, require `mapping.txt` and `resources.txt`, create `app-release.aab.sha256`, and create `release-manifest.txt` containing `GITHUB_SHA`, `versionCode`, and `versionName` extracted from `app/build.gradle.kts`.

Upload the AAB, checksum, manifest, `mapping.txt`, and `resources.txt` with the pinned `actions/upload-artifact` SHA already used by Android CI, `if: always()`, `if-no-files-found: error`, and 30-day retention.

- [ ] **Step 4: Run static verification**

Run:

```bash
ruby test/release_configuration_test.rb
ruby -e 'require "yaml"; YAML.safe_load(File.read(".github/workflows/release-internal.yml"), aliases: true); puts "YAML syntax OK"'
git diff --check -- .github/workflows/release-internal.yml test/release_configuration_test.rb
```

Expected: tests pass, YAML reports `YAML syntax OK`, and the diff check emits no errors.

- [ ] **Step 5: Commit the workflow**

```bash
git add .github/workflows/release-internal.yml test/release_configuration_test.rb
git commit -m "ci: publish manual releases to Play internal"
```

### Task 3: Document setup, operation, and evidence

**Files:**
- Modify: `fastlane/README.md`
- Modify: `docs/release/RELEASE_RUNBOOK.md`
- Modify: `docs/release/RELEASE_CHECKLIST.md`

**Interfaces:**
- Consumes: workflow/environment/secret names and evidence paths from Task 2.
- Produces: operator instructions for configuring GitHub, dispatching a release, confirming Play upload, and retaining evidence.

- [ ] **Step 1: Add documentation assertions to the configuration test**

Assert that the three documents collectively mention `google-play-internal`, every required secret, `internal_release`, manual dispatch, version-code increment, `mathbrainer-internal-release-`, and that closed/production promotion remains a separate owner action.

- [ ] **Step 2: Run the test and confirm documentation coverage fails**

Run: `ruby test/release_configuration_test.rb`

Expected: failures name the missing environment, secret, workflow, and artifact instructions.

- [ ] **Step 3: Update Fastlane and release documentation**

Document how to encode the keystore (`base64 < upload-keystore.jks | tr -d '\n'` on macOS/Linux), create and protect the `google-play-internal` environment, add all seven secrets, increment app version metadata, run normal CI, dispatch `Google Play Internal Release`, download and retain the evidence artifact, and confirm the version in Play internal testing. Clearly state that this workflow cannot publish to closed or production tracks and does not update store media.

- [ ] **Step 4: Verify documentation coverage and formatting**

Run:

```bash
ruby test/release_configuration_test.rb
rg -n "google-play-internal|ANDROID_KEYSTORE_BASE64|internal_release|manual|versionCode|mathbrainer-internal-release" fastlane/README.md docs/release/RELEASE_RUNBOOK.md docs/release/RELEASE_CHECKLIST.md
git diff --check -- fastlane/README.md docs/release/RELEASE_RUNBOOK.md docs/release/RELEASE_CHECKLIST.md test/release_configuration_test.rb
```

Expected: tests pass, each operational concept is found, and the diff check emits no errors.

- [ ] **Step 5: Commit documentation**

```bash
git add fastlane/README.md docs/release/RELEASE_RUNBOOK.md docs/release/RELEASE_CHECKLIST.md test/release_configuration_test.rb
git commit -m "docs: explain internal Play release workflow"
```

### Task 4: Verify the complete release configuration locally

**Files:**
- Verify only: all files from Tasks 1-3

**Interfaces:**
- Consumes: complete release automation.
- Produces: local verification evidence; no live Play upload is attempted.

- [ ] **Step 1: Run configuration and dependency checks**

```bash
bundle check
ruby test/release_configuration_test.rb
ruby -c fastlane/Fastfile
bundle exec fastlane lanes
```

Expected: every command exits zero and Fastlane lists `android internal_release`.

- [ ] **Step 2: Run existing Android verification**

```bash
./gradlew --no-daemon --stacktrace :app:testDebugUnitTest :app:lintDebug :app:assembleDebug
```

Expected: `BUILD SUCCESSFUL` with no failing tests or lint errors.

- [ ] **Step 3: Build and verify a non-production signed release candidate locally**

Create an ephemeral keystore under `mktemp -d`, use clearly non-production non-Google-sample AdMob identifiers, then run:

```bash
./gradlew --no-daemon --stacktrace :app:bundleRelease :app:verifyReleaseManifest
jarsigner -verify -verbose -certs app/build/outputs/bundle/release/app-release.aab
test -s app/build/outputs/mapping/release/mapping.txt
test -s app/build/outputs/mapping/release/resources.txt
```

Expected: Gradle reports `BUILD SUCCESSFUL`, `jarsigner` reports `jar verified`, and both mapping files are non-empty. Remove only the explicit temporary keystore directory after verification.

- [ ] **Step 4: Review the final diff and secret boundary**

```bash
git diff --check HEAD~3..HEAD
git status --short
git grep -n -E 'BEGIN PRIVATE KEY|private_key_id|ANDROID_KEYSTORE_BASE64=' -- ':!docs/superpowers/**'
```

Expected: no whitespace errors, only the user's pre-existing unrelated changes remain uncommitted, and no credential material is found.

- [ ] **Step 5: Record the required external verification**

The release owner configures the `google-play-internal` environment and its reviewer rules, adds the seven secrets, dispatches the workflow from a commit whose Android CI checks are green, and confirms the new `versionCode` appears on Play's internal track. This is the only step that performs a live upload.
