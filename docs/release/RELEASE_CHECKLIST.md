# Release Checklist

Release owner: __________  Version: __________  Commit: __________  Date: __________

## Candidate Evidence

- [ ] Feature branch is reviewed and merged; release commit is immutable.
- [ ] Required CI checks pass: unit-tests, lint, debug-build,
  instrumentation-smoke, and release-bundle.
- [ ] Production signing and AdMob values are supplied from the approved secret
  store; no secret or production credential is committed.
- [ ] `testDebugUnitTest`, `lintDebug`, `connectedDebugAndroidTest`,
  `verifyReleaseManifest`, and `bundleRelease` pass for the candidate.
- [ ] The signed AAB is verified and its SHA-256, version code, commit SHA,
  mapping file, and build timestamp are archived privately.
- [ ] Upgrade compatibility is checked against the currently published Room
  database/schema; no destructive fallback is present.
- [ ] `PLAY_CONSOLE_CHECKLIST.md` has no open release-blocking item.

## Device And Product Matrix

- [ ] Fresh install and upgrade from production pass through Play Internal App
  Sharing on API 26, 30, 35, and 36.
- [ ] At least one phone, 7-inch tablet, and 10-inch tablet layout is checked.
- [ ] Every game launches, accepts input, ends correctly, and updates statistics.
- [ ] Scores/statistics survive process death, relaunch, and supported upgrade.
- [ ] Home, Statistics, Settings, Credits, back navigation, and external privacy
  policy navigation work as expected.
- [ ] English and Italian store-facing journeys/assets have been reviewed; the
  app's current resource localization scope is accurately represented.
- [ ] Offline launch, rotation policy, and process recreation are smoke-tested.
- [ ] UMP first-run, consent-denied/limited, consent-allowed, privacy-options,
  and no-network paths fail safely; ads appear only when eligible.
- [ ] No test ad identifier, debug banner, placeholder, or sensitive value is
  present in the candidate.

## Approval

- [ ] Play pre-launch report has no unresolved P1/P2 blocker.
- [ ] Release owner approves internal promotion: __________
- [ ] QA/device evidence owner signs off: __________
- [ ] Privacy/Play declarations owner signs off: __________
- [ ] Rollback/hotfix owner is available during rollout: __________

Do not check an item without retained evidence. Record an approved exception
with owner, reason, risk, and expiry rather than silently skipping it.
