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
- [ ] The protected workflow-produced AAB is the only release candidate; its
  signature, SHA-256, version code, commit SHA, mapping, and build timestamp are
  verified before Play upload and retained in encrypted evidence.
- [ ] Upgrade compatibility is checked against the currently published Room
  database/schema; no destructive fallback is present.
- [ ] `PLAY_CONSOLE_CHECKLIST.md` has no open release-blocking item.

## Internal Play Release

- [ ] `versionCode` is incremented in `app/build.gradle.kts` and is higher than
  the last Play upload; `versionName` is updated when required.
- [ ] Normal CI checks pass for the immutable candidate before the manual
  dispatch of `Google Play Internal Release`.
- [ ] The selected ref is `refs/heads/master` or `refs/tags/v*`; the protected
  `google-play-internal` environment uses required reviewers and **Selected
  branches and tags** rules for branch `master` and tags `v*`.
- [ ] The environment contains exactly eight secrets: `PLAY_STORE_JSON_KEY`,
  `ANDROID_KEYSTORE_BASE64`, `KEYSTORE_PASSWORD`, `KEY_ALIAS`, `KEY_PASSWORD`,
  `ADMOB_APP_ID`, `ADMOB_BANNER_ID`, and `RELEASE_EVIDENCE_PASSWORD`.
- [ ] The dedicated Play service account is limited to
  `eu.indiewalkabout.mathbrainer` and **Release apps to testing tracks**; it has
  no account-wide, admin, production, store, financial, or tester-list access.
- [ ] A release owner approves the protected environment before the workflow
  builds or uploads the candidate.
- [ ] The workflow builds once, verifies the AAB signature/mappings and creates
  its checksum/manifest before `internal_release`, then the post-upload hash
  check confirms the same file remained unchanged.
- [ ] `mathbrainer-internal-release-<commit SHA>` contains only the encrypted
  `release-evidence.tar.gz.enc` private bundle and non-sensitive
  checksum/manifest; no plaintext AAB, `mapping.txt`, or `resources.txt` was
  uploaded as an Actions artifact.
- [ ] The evidence was decrypted in a private directory with AES-256-CBC and
  PBKDF2, the extracted AAB matches the checksum, and the passphrase remains in
  a private password manager rather than GitHub logs or the repository.
- [ ] The version is confirmed in Play Console's internal testing track.
- [ ] Downstream testing and approval use that exact workflow-produced AAB from
  internal testing; no rebuild or Internal App Sharing upload replaced it.
- [ ] No closed or production promotion is requested from this workflow; that
  promotion is a separate owner action in Play Console. Store media remains
  unchanged by this workflow.

## Device And Product Matrix

- [ ] Fresh install and upgrade from production pass with the workflow-uploaded
  Play internal-testing version on API 26, 30, 35, and 36.
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
- [ ] Release owner approves promotion of the exact internal version: __________
- [ ] QA/device evidence owner signs off: __________
- [ ] Privacy/Play declarations owner signs off: __________
- [ ] Rollback/hotfix owner is available during rollout: __________

Do not check an item without retained evidence. Record an approved exception
with owner, reason, risk, and expiry rather than silently skipping it.
