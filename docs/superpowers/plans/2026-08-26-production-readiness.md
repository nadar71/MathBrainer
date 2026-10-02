# Math Brainer Production Readiness Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Produce a policy-compliant, signed, regression-tested Math Brainer AAB that can move safely through Google Play internal testing to production.

**Architecture:** Readiness is implemented as independent release gates: deterministic configuration, consent-safe ads, durable persistence, automated quality checks, release observability, and Play Console validation. Each gate must be independently testable and committed before rollout proceeds.

**Tech Stack:** Kotlin, Android SDK 36, Jetpack Compose, Navigation 3, Hilt, Room, Coroutines/StateFlow, Google Mobile Ads, UMP, Gradle, JUnit, Compose UI Test, GitHub Actions, Google Play Console.

**Spec:** `docs/superpowers/specs/2026-08-26-production-readiness.md`

## Global Constraints

- Preserve application ID `eu.indiewalkabout.mathbrainer` and existing Play signing identity.
- Set `targetSdk = 36` before submitting an update after August 31, 2026.
- Increment `versionCode` above 12 for every uploaded artifact.
- Debug builds must use Google test ads; release builds must use production IDs.
- Never commit signing keys, passwords, production credentials, or service-account JSON.
- Existing game IDs, scores, statistics, routes, English copy, and Italian copy must remain compatible.

---

### Task 1: Make Release Configuration Deterministic

**Files:**
- Modify: `app/build.gradle.kts`
- Modify: `gradle/libs.versions.toml`
- Modify: `.gitignore`
- Delete obsolete commented configuration from: `build.gradle.kts`
- Create: `keystore.properties.example`

**Interfaces:**
- Produces: `BuildConfig.ADMOB_BANNER_ID`, variant-specific `admob_app_id`, and optional local signing properties.
- Consumes: existing Play upload keystore supplied locally or by CI secrets.

- [ ] Remove the archived Groovy Gradle blocks, duplicate/unused dependencies, `dataBinding = true`, the non-Compose ConstraintLayout dependency, duplicate Coil entries, and any dependency proven unused by `./gradlew :app:dependencies` plus source search.
- [ ] Set `targetSdk = 36`, increment `versionCode` to the next unused Play value, and set the approved release `versionName`.
- [ ] Define debug/test AdMob IDs directly in the debug variant and read release IDs from untracked Gradle properties; fail release configuration when a production ID is absent or equals a Google sample ID.
- [ ] Load `KEYSTORE_FILE`, `KEYSTORE_PASSWORD`, `KEY_ALIAS`, and `KEY_PASSWORD` from untracked properties or CI environment variables and apply them only to `release`.
- [ ] Add `keystore.properties`, `*.jks`, `*.keystore`, and service-account JSON patterns to `.gitignore`; document property names with empty values in `keystore.properties.example`.
- [ ] Run `./gradlew :app:assembleDebug :app:bundleRelease` and verify the AAB with `jarsigner -verify -verbose -certs app/build/outputs/bundle/release/app-release.aab`.
- [ ] Commit with `build: harden production build configuration`.

**Acceptance:** Debug packages only test ad IDs; the release bundle cannot be produced with missing/sample production IDs; no secret appears in `git grep` or `git status`.

### Task 2: Correct Manifest, Backup, and Component Exposure

**Files:**
- Modify: `app/src/main/AndroidManifest.xml`
- Create: `app/src/main/res/xml/backup_rules.xml`
- Create: `app/src/main/res/xml/data_extraction_rules.xml`
- Delete if unused: `app/src/main/java/eu/indiewalkabout/mathbrainer/feat_games/feat_sound_seq/presentation/ui/SoundsSeqActivity.kt`

**Interfaces:**
- Consumes: variant resource `@string/admob_app_id` from Task 1.
- Produces: one valid AdMob metadata declaration and explicit backup/data-transfer policy.

- [ ] Add a manifest-merge test or CI assertion that exactly one `com.google.android.gms.ads.APPLICATION_ID` entry exists.
- [ ] Remove the duplicate AdMob metadata and obsolete `android.test.*` `<uses-library>` declarations.
- [ ] Remove the unfinished `SoundsSeqActivity` if no route launches it; otherwise mark it non-exported and complete its behavior before release.
- [ ] Replace implicit `allowBackup="true"` behavior with explicit backup and extraction rules that preserve user statistics only if the privacy policy promises cloud/device transfer; otherwise exclude the Room database and preferences.
- [ ] Run `./gradlew :app:processReleaseMainManifest` and inspect `app/build/intermediates/merged_manifests/release/processReleaseManifest/AndroidManifest.xml` for exported components, permissions, and duplicate metadata.
- [ ] Commit with `fix: harden production manifest and backup policy`.

**Acceptance:** Only the launcher activity is exported, only required permissions remain, and backup behavior matches the privacy declaration.

### Task 3: Make Ads and Consent Policy-Safe

**Files:**
- Modify: `app/src/main/java/eu/indiewalkabout/mathbrainer/AppMathBrainer.kt`
- Modify: `app/src/main/java/eu/indiewalkabout/mathbrainer/feat_ads/util/ConsentManager.kt`
- Modify: `app/src/main/java/eu/indiewalkabout/mathbrainer/feat_ads/util/RequestConfigurationUtils.kt`
- Modify: `app/src/main/java/eu/indiewalkabout/mathbrainer/feat_ads/presentation/AdMobBannerView.kt`
- Modify: `app/src/main/java/eu/indiewalkabout/mathbrainer/feat_home/presentation/ui/HomeGameActivity.kt`
- Create: `app/src/test/java/eu/indiewalkabout/mathbrainer/feat_ads/ConsentManagerTest.kt`
- Create: `app/src/androidTest/java/eu/indiewalkabout/mathbrainer/feat_ads/ConsentFlowTest.kt`

**Interfaces:**
- Produces: `AdsState` with `Loading`, `Allowed`, and `Unavailable`; `requestConsent(activity: Activity, onResult: (AdsState) -> Unit)`.
- Consumes: `BuildConfig.DEBUG` and `BuildConfig.ADMOB_BANNER_ID` from Task 1.

- [ ] Write tests proving ads cannot load before UMP returns `canRequestAds() == true`, errors fail closed, and debug-only test-device IDs never apply to release.
- [ ] Replace nullable `Activity` and unsafe `context as Activity` casts with an `Activity`-required API.
- [ ] Remove global mutable `canRequestAdsFlag`, `TEST_DEVICE_ID`, and `appContext`; expose consent/ad readiness as lifecycle-aware state owned by the activity or an injected coordinator.
- [ ] Move `MobileAds.initialize` after consent resolution and make initialization idempotent.
- [ ] Make `AdMobBannerView` consume `AdsState` and the variant-provided ad unit ID; dispose `AdView` with `destroy()` when the composable leaves composition.
- [ ] Verify EEA consent required/not-required/error scenarios with UMP debug geography and verify the in-app privacy-options entry reopens the form.
- [ ] Commit with `fix: gate AdMob behind verified UMP consent`.

**Acceptance:** No ad request occurs before consent eligibility; debug/release IDs are mechanically separated; changing consent does not require clearing app data.

### Task 4: Guarantee Statistics Persistence Across Upgrades

**Files:**
- Modify: `app/src/main/java/eu/indiewalkabout/mathbrainer/feat_statistics/data/local/db/MathBrainerDatabase.kt`
- Create: `app/schemas/eu.indiewalkabout.mathbrainer.feat_statistics.data.local.db.MathBrainerDatabase/10.json`
- Create: `app/src/androidTest/java/eu/indiewalkabout/mathbrainer/feat_statistics/data/local/db/MathBrainerMigrationTest.kt`
- Modify: `app/build.gradle.kts`

**Interfaces:**
- Produces: checked Room schemas and explicit `Migration` objects for every supported upgrade path.
- Consumes: the database currently installed in production and its historical schema/version evidence.

- [ ] Retrieve the schema of the current production APK/AAB and compare it with Room version 10 before defining migrations.
- [ ] Configure Room schema export to `app/schemas` and commit the generated version 10 schema.
- [ ] Replace `fallbackToDestructiveMigration(true)` with explicit migrations; if no schema change is required, remove the fallback and prove the production schema opens as version 10.
- [ ] Add `MigrationTestHelper` tests that seed representative scores/statistics using the previous schema, migrate, and assert every value remains.
- [ ] Add DAO tests for empty database initialization, upsert, observation, and clearing statistics.
- [ ] Run `./gradlew :app:connectedDebugAndroidTest` on API 26 and API 36 emulators.
- [ ] Commit with `fix: preserve player statistics during database upgrades`.

**Acceptance:** Upgrading from the currently published version preserves all scores and statistics; destructive fallback is absent from release code.

### Task 5: Establish Automated Release Quality Gates

**Files:**
- Create: `.github/workflows/android-ci.yml`
- Create: `app/src/androidTest/java/eu/indiewalkabout/mathbrainer/ReleaseSmokeTest.kt`
- Modify: `app/build.gradle.kts`
- Modify: `app/proguard-rules.pro`

**Interfaces:**
- Produces: CI checks named `unit-tests`, `lint`, `debug-build`, `instrumentation-smoke`, and `release-bundle`.
- Consumes: test-safe release placeholders or protected CI secrets without publishing artifacts publicly.

- [ ] Add a Compose smoke test that launches Home, opens Statistics and Settings, enters one choose game and one memory game, navigates back, and survives activity recreation.
- [ ] Add locale/device matrix coverage for English and Italian on phone, 7-inch tablet, and 10-inch tablet profiles; keep the mandatory PR gate to one phone and run tablets nightly if CI time is high.
- [ ] Configure CI with a pinned JDK, Gradle cache, `./gradlew testDebugUnitTest lintDebug assembleDebug`, and API 26/API 36 emulator smoke tests.
- [ ] Enable `isMinifyEnabled = true` and `isShrinkResources = true` for release, add only evidence-based keep rules, and run the full smoke suite against the minified artifact.
- [ ] Generate and archive `mapping.txt`, `resources.txt`, dependency report, lint report, and unsigned test AAB as private CI artifacts.
- [ ] Commit with `ci: enforce Android production quality gates`.

**Acceptance:** A pull request cannot merge when tests, lint, manifest checks, or builds fail; the minified release passes the same core journeys as debug.

### Task 6: Add Release Observability and Error Hygiene

**Files:**
- Modify: `gradle/libs.versions.toml`
- Modify: `app/build.gradle.kts`
- Modify: `app/src/main/java/eu/indiewalkabout/mathbrainer/core/util/GenericUtil.kt`
- Create: `app/src/main/java/eu/indiewalkabout/mathbrainer/core/observability/AppErrorReporter.kt`
- Create: `app/src/main/java/eu/indiewalkabout/mathbrainer/core/observability/NoOpErrorReporter.kt`
- Create integration files for the selected crash reporter only after owner approval.

**Interfaces:**
- Produces: `AppErrorReporter.record(error: Throwable, context: Map<String, String> = emptyMap())`.
- Consumes: a privacy-approved crash-reporting provider or `NoOpErrorReporter`.

- [ ] Choose Crashlytics, Sentry, or Play Console Android vitals only; document data collected, retention, and consent implications before adding an SDK.
- [ ] Add the framework-free reporter interface and inject it where recoverable failures are currently swallowed.
- [ ] Remove debug logging from release paths and avoid scores, answers, consent state, advertising IDs, or other user-linked values in telemetry.
- [ ] Upload release mapping/native symbols when applicable and verify one non-fatal event on the internal track.
- [ ] Define launch thresholds: crash-free users at least 99.5%, user-perceived ANR below 0.47%, and no new P1/P2 issue during the observation window.
- [ ] Commit with `feat: add privacy-aware release diagnostics`.

**Acceptance:** Production failures are diagnosable without collecting gameplay or consent-sensitive data; obfuscated traces deobfuscate correctly.

### Task 7: Complete Play Console and Legal Readiness

**Files:**
- Create: `docs/release/PLAY_CONSOLE_CHECKLIST.md`
- Create: `docs/release/DATA_SAFETY.md`
- Modify: in-app Credits/Settings UI to expose the approved privacy-policy link.
- Verify external page: `https://www.indie-walkabout.eu/privacy/`

**Interfaces:**
- Produces: evidence-backed Play declarations and a reachable app-specific privacy policy.
- Consumes: final SDK dependency report and actual runtime network/data behavior from Tasks 1-6.

- [ ] Update the privacy page so it explicitly names Math Brainer and Indie Walkabout/developer identity, explains AdMob/UMP data handling, local statistics, retention/deletion, contact details, and effective date.
- [ ] Add the same privacy-policy URL inside the app and verify it opens over HTTPS without redirects to unrelated content.
- [ ] Derive Data Safety answers from the final dependency/runtime audit, including advertising/device identifiers handled by Google Mobile Ads; do not copy declarations from another app blindly.
- [ ] Complete and screenshot evidence for Ads, Data Safety, target audience, content rating, app access, privacy policy, and AD_ID declarations.
- [ ] Confirm store title, descriptions, release notes, icon, feature graphic, and selected localized phone/tablet screenshots match the shipped UI.
- [ ] Verify Play pre-launch report has no accessibility, stability, security, or compatibility blocker.
- [ ] Commit with `docs: add Play production compliance checklist`.

**Acceptance:** Every Play Console App Content item is actioned, the privacy URL passes review requirements, and declarations match the final AAB.

### Task 8: Execute Staged Release and Rollback Procedure

**Files:**
- Create: `docs/release/RELEASE_RUNBOOK.md`
- Create: `docs/release/RELEASE_CHECKLIST.md`
- Create: `docs/release/ROLLBACK.md`

**Interfaces:**
- Produces: repeatable release approval, rollout, monitoring, halt, and hotfix process.
- Consumes: signed AAB, mapping file, Play declarations, and CI evidence from earlier tasks.

- [ ] Install the release candidate through Play Internal App Sharing and smoke-test fresh install plus upgrade from production on API 26, 30, 35, and 36.
- [ ] Test all games at least once, score/stat persistence, navigation/back behavior, English/Italian text, offline launch, consent paths, ad rendering, rotation policy, process death, and phone/tablet layouts.
- [ ] Promote to internal testing for at least 24 hours, then closed testing for 3-7 days with representative devices and documented sign-off.
- [ ] Start production at 5%; monitor Android vitals, reviews, ad errors, consent errors, and support contacts for 24 hours.
- [ ] Advance to 20%, 50%, and 100% only when thresholds remain green for each observation window; halt immediately for data loss, startup/navigation blocker, policy violation, crash spike, or broken consent.
- [ ] Document that Play cannot roll back to a lower `versionCode`; rollback means halt rollout or ship a tested hotfix with a higher code.
- [ ] Tag the final commit as `v<versionName>` only after 100% rollout and archive the AAB, mapping, checksums, release notes, and Play approval evidence.
- [ ] Commit with `docs: define staged production release runbook`.

**Acceptance:** The release has named approval evidence, measurable health gates, and a tested halt/hotfix path.

## Recommended Order and Priority

1. **P0 before any upload:** Tasks 1-4 and Task 7.
2. **P0 before production:** Task 5 and release-candidate portions of Task 8.
3. **P1 strongly recommended:** Task 6 before broad rollout; use Android vitals alone only as a documented temporary choice.
4. **Final launch:** Complete staged rollout in Task 8 after all prior acceptance criteria pass.

## Final Release Command Set

```bash
./gradlew clean testDebugUnitTest lintDebug connectedDebugAndroidTest :app:bundleRelease
jarsigner -verify -verbose -certs app/build/outputs/bundle/release/app-release.aab
shasum -a 256 app/build/outputs/bundle/release/app-release.aab
git status --short
```

Expected result: every Gradle task succeeds, signature verification succeeds, checksum is archived, and the worktree contains no unexpected release inputs.
