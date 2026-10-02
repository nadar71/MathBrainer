# Math Brainer Production Readiness Specification

## Objective

Prepare Math Brainer for a safe Google Play production update with reproducible builds, policy-compliant advertising and privacy behavior, preserved player statistics, tested gameplay/navigation, verified store assets, and an observable staged rollout.

## Current Baseline

- `compileSdk` is 36, but `targetSdk` is 35. Google Play requires mobile app updates to target API 36 from August 31, 2026.
- The debug APK and focused Quick Count regression tests build successfully.
- The manifest declares the AdMob application ID twice.
- Ad IDs are stored in an ignored resource file and currently default to Google's sample IDs; release behavior is not enforced by Gradle.
- UMP consent exists, but Mobile Ads initializes before consent resolution and consent state is stored in global mutable fields.
- Release minification is disabled and there is no explicit release signing configuration or checked release-build workflow.
- Room database version 10 uses destructive fallback and has no exported schema directory or migration tests.
- The project has substantial ViewModel/session/navigation unit tests, but little meaningful instrumentation coverage and no CI workflow.
- English and Italian store media exist for phone, 7-inch tablet, and 10-inch tablet.
- No crash-reporting or release-health integration is present.

## Release Gates

- Production AAB targets API 36, is signed with the existing Play upload key, and contains production AdMob IDs.
- Debug builds always use Google test ad IDs and test-device configuration; release builds cannot package test IDs.
- UMP consent is requested before ad initialization/request, and privacy options remain accessible in-app.
- Existing database data survives upgrade; no destructive migration is allowed in release.
- Unit tests, lint, release bundle, dependency checks, and representative instrumentation smoke tests pass in CI.
- Play Console privacy policy, Data Safety, ads, target audience, content rating, and app-access declarations match actual behavior.
- Internal and closed-track testing show no blocker crashes, ANRs, navigation failures, data loss, or ad-policy violations.

## Constraints

- Preserve package name `eu.indiewalkabout.mathbrainer` and existing Play signing identity.
- Preserve current game behavior, routes, game IDs, high scores, and statistics.
- Keep English and Italian localization.
- Keep AdMob monetization and Google UMP consent; Unity Ads remains removed.
- Do not commit signing secrets, upload keys, `local.properties`, or production credentials.

