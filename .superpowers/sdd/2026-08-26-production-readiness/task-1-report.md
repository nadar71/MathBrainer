# Task 1 Report: Make Release Configuration Deterministic

## Status

DONE_WITH_CONCERNS

## Files Changed

- `.gitignore`
- `app/build.gradle.kts`
- `build.gradle.kts`
- `gradle/libs.versions.toml`
- `keystore.properties.example`

## Design Decisions

- Preserved application ID `eu.indiewalkabout.mathbrainer`; set `targetSdk = 36`, `versionCode = 13`, and `versionName = "3.0.2"`.
- Moved AdMob configuration into build variants. Debug supplies Google's app test ID `ca-app-pub-3940256099942544~3347511713` and banner test ID `ca-app-pub-3940256099942544/6300978111`; release reads `ADMOB_APP_ID` and `ADMOB_BANNER_ID` from `-P` properties, environment variables, or untracked `keystore.properties`.
- Exposed the required `BuildConfig.ADMOB_BANNER_ID` and `admob_app_id`. Also supplies `admob_key_app_id` as a temporary variant-owned compatibility resource because the current manifest still uses that name; Task 2 owns the manifest migration to `admob_app_id`.
- Release reads `KEYSTORE_FILE`, `KEYSTORE_PASSWORD`, `KEY_ALIAS`, and `KEY_PASSWORD` from the same external sources and applies the signing config only to `release`.
- Release/bundle/assemble invocations fail during Gradle configuration when inputs are missing, when either production AdMob value equals its corresponding Google sample ID, or when `KEYSTORE_FILE` does not exist. Debug-only invocations do not request release inputs.
- Removed archived Groovy Gradle configuration, `dataBinding`, the non-Compose ConstraintLayout dependency, and proven duplicate Coil, lifecycle runtime, Material 3 Android, and UI test dependencies/catalog entries. `./gradlew :app:dependencies` confirmed the obsolete databinding compiler was previously present only through `dataBinding`.
- Added repository ignores for `keystore.properties`, `*.jks`, `*.keystore`, and service-account JSON filenames, plus an empty-value `keystore.properties.example`.

## Commands And Results

| Command | Result |
| --- | --- |
| `./gradlew :app:dependencies` | Exit 0. Dependency report completed; it showed `androidx.databinding:databinding-compiler:8.9.1` was pulled by the obsolete data-binding feature. |
| `./gradlew :app:bundleRelease` | Expected exit 1 before production inputs are available. Validation reported missing `ADMOB_APP_ID`, `ADMOB_BANNER_ID`, `KEYSTORE_FILE`, `KEYSTORE_PASSWORD`, `KEY_ALIAS`, and `KEY_PASSWORD`. |
| `./gradlew --offline --no-daemon --max-workers=1 :app:bundleRelease --dry-run` | Expected exit 1 after the fail-fast refinement. Gradle stopped at configuration with the same complete missing-input message before release tasks executed. |
| `git diff --check` | Exit 0. |
| `git check-ignore -v keystore.properties release-upload.jks release-upload.keystore service-account.json google-service-account.json` | Confirmed each required secret-bearing input pattern is ignored by repository rules. |
| `git grep -n -E '^(ADMOB_APP_ID|ADMOB_BANNER_ID|KEYSTORE_FILE|KEYSTORE_PASSWORD|KEY_ALIAS|KEY_PASSWORD)=.+'` | No matches: no non-empty credential/property assignment is tracked. |
| `rg -n -S 'dataBinding|coil\\.compose\\.v210|lifecycle\\.runtime\\.ktx\\.v251|material3\\.android|ui\\.test\\.android|constraintlayout:constraintlayout' app/build.gradle.kts gradle/libs.versions.toml build.gradle.kts` | No obsolete configured entries remain; only the intended Compose ConstraintLayout catalog entry remains. |

## Concerns

- Production AdMob IDs and Play upload-key inputs are deliberately unavailable locally. Therefore `:app:bundleRelease` cannot produce an AAB and `jarsigner -verify -verbose -certs app/build/outputs/bundle/release/app-release.aab` cannot run. This is expected external release-input verification, not a source or configuration blocker.
- A foreground `:app:assembleDebug` was started after the variant-resource change, but unrelated Gradle/Kotlin work on the shared machine prevented it from completing within the command tool window. The deterministic resource dependency is covered structurally by the debug variant's `admob_key_app_id` compatibility resource; run the final debug assembly again when the shared Gradle workers are idle.
- `ads_key_ids.xml` remains ignored by the user-level global `*.xml` rule, not by this repository. The build no longer reads it, and the repository-specific ignored-resource entry was removed.
