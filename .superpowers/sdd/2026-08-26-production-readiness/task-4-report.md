# Task 4 Report: Non-destructive Room Statistics Persistence

## Status

Implemented a fail-closed Room v10 database path. The release builder no longer
uses `fallbackToDestructiveMigration`; an unknown schema version now refuses to
open rather than deleting player scores or statistics.

## Changes

- Configured KSP to export Room schemas to `app/schemas`.
- Committed the generated v10 schema for `MathBrainerDatabase`.
- Made the checked schemas available to instrumentation tests as test assets.
- Moved `room-testing` to the instrumentation test classpath.
- Added `MathBrainerMigrationTest` coverage for:
  - opening the exported v10 schema through the production builder while
    retaining representative `GameScores`, `GameStatistics`, and `GameStats`
    values;
  - refusing an unknown prior database version without erasing stored game
    statistics;
  - empty initialization, `GameStats` upsert, `Flow` observation, and clearing
    all three statistics tables.

## Verification

- `./gradlew :app:compileDebugAndroidTestKotlin` passed.
- `ANDROID_SERIAL=emulator-5556 ./gradlew :app:connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=eu.indiewalkabout.mathbrainer.feat_statistics.data.local.db.MathBrainerMigrationTest` passed on API 36: 6 tests, 0 failures.
- The same focused connected test command passed on the available API 35
  emulator: 6 tests, 0 failures.
- `./gradlew :app:testDebugUnitTest :app:assembleDebug --rerun-tasks` produced
  66 JVM tests with 0 failures/errors and regenerated the debug APK.
- A final `./gradlew :app:testDebugUnitTest :app:assembleDebug` completed with
  `BUILD SUCCESSFUL`.

## External Release Gate

No current production APK/AAB or historical Room schema is available locally.
The repository history shows version changes without exported schemas, so an
unverified migration would be fabricated and could corrupt player data.

Before publishing an update, obtain the currently installed Play artifact or a
database copied from a production installation. Extract and compare its
`PRAGMA user_version`, `sqlite_master` table definitions, and Room identity hash
with `app/schemas/eu.indiewalkabout.mathbrainer.feat_statistics.data.local.db.MathBrainerDatabase/10.json`.
If the production schema is not v10-identical, add only the evidence-backed
`Migration` objects, seed the extracted schema in `MigrationTestHelper`, and
verify every score/statistic value survives. If it is v10-identical, retain the
fail-closed builder and record the artifact comparison as release evidence.

## Remaining Environment Gap

API 26 instrumentation coverage was not run because this workspace has no API
26 AVD or installed API 26 system image. The required API 36 coverage passed;
the available API 35 emulator provided additional connected-test coverage.
