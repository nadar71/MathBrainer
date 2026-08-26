# Task 2 Report: Manifest, Backup, and Component Exposure

## Status

Implementation and verification completed; the Task 2 commit uses `fix: harden production manifest and backup policy`.

## Files

- Modified `app/build.gradle.kts` to add `verifyReleaseManifest`, make `check` depend on it, keep Compose UI tooling debug-only, and remove the unused direct WorkManager dependency.
- Modified `gradle/libs.versions.toml` to remove the unused WorkManager version and alias.
- Modified `app/src/main/AndroidManifest.xml` to use exactly one `@string/admob_app_id`, declare explicit backup rules, remove test libraries, and remove the unused sound-sequence activity declaration.
- Added `app/src/main/res/xml/backup_rules.xml` for Android 11 and lower.
- Added `app/src/main/res/xml/data_extraction_rules.xml` for Android 12 and higher.
- Deleted `app/src/main/java/eu/indiewalkabout/mathbrainer/feat_games/feat_sound_seq/presentation/ui/SoundsSeqActivity.kt`.

## Decisions

- The manifest is verified after merging, rather than by source-text inspection. The Gradle task requires one `com.google.android.gms.ads.APPLICATION_ID`, requires `@string/admob_app_id`, and permits only `HomeGameActivity` as an exported app-owned activity, alias, service, or receiver. Required AndroidX and Google library components remain outside that namespace.
- `SoundsSeqActivity` was deleted because a repository-wide search found only its declaration and its own empty stub; no route or other reference launches it.
- Backup and device transfer explicitly whitelist Room database `MathBrainerDB` and its `-wal` and `-shm` journal files. The whitelist omits all shared preferences, excluding UMP consent and advertising identifiers while preserving player statistics.
- The direct release `ui-tooling` dependency was removed after merged-manifest inspection exposed `PreviewActivity` as an additional exported release activity. `ui-tooling-preview` remains available to source and `ui-tooling` remains debug-only.
- Repository-wide source search found no WorkManager APIs, workers, requests, or imports. The direct WorkManager dependency and its version-catalog alias were removed; WorkManager that remains transitively required by Google Mobile Ads is library-owned and remains permitted by the manifest gate.
- The task uses AGP 8.11's actual `merged_manifest/release/processReleaseMainManifest/AndroidManifest.xml` output for the assertion. The brief's older `merged_manifests/release/processReleaseManifest/AndroidManifest.xml` was also regenerated and inspected.

## Commands And Results

- `./gradlew :app:verifyReleaseManifest` initially failed as expected: two AdMob application-ID entries were found.
- `./gradlew :app:verifyReleaseManifest` then failed as expected after the exported-activity assertion was added: `PreviewActivity` was still exported.
- `./gradlew :app:verifyReleaseManifest :app:processReleaseMainManifest :app:processReleaseManifest` passed after the manifest and dependency fixes. The merged release manifest contains one AdMob application-ID entry, uses `@string/admob_app_id`, and has explicit backup rule references.
- `./gradlew :app:testDebugUnitTest :app:assembleDebug :app:verifyReleaseManifest :app:processReleaseMainManifest :app:processReleaseManifest` passed. Existing compiler deprecation warnings remain; no Task 2 compilation or test failures occurred.
- Fix round 1: `./gradlew :app:verifyReleaseManifest :app:processReleaseMainManifest :app:testDebugUnitTest :app:assembleDebug` passed after the WorkManager cleanup and expanded app-namespace component assertion.
- Fix round 1: repository-wide `rg` source search found no WorkManager APIs, workers, requests, imports, or direct dependency/catalog alias references.
- `./gradlew :app:check --dry-run` confirms that the existing Task 1 release-configuration gate is intentionally part of the aggregate `check` graph. The earlier failed aggregate invocation therefore reflected the expected absence of production IDs and signing inputs, not a Task 2 verification failure; focused Task 2 verification does not require those inputs.
- `git diff --check` passed.

## Concerns

- A real release bundle remains blocked until the owner supplies production AdMob identifiers and signing inputs through the Task 1 external configuration contract.
- Task 7 must publish and verify a privacy policy that accurately states player statistics transfer via Android backup/device transfer while advertising and consent identifiers do not, before any release upload. If that disclosure cannot be established, backups must be disabled before release.
- Google Mobile Ads still contributes its required non-app manifest components and permissions. Task 2's merged-manifest gate verifies that the app itself exposes only the launcher activity; Task 3 owns consent-safe Mobile Ads runtime behavior.
