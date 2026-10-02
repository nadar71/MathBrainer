# Production Release Runbook

## 1. Prepare the release ref

1. Increment `versionCode` in `app/build.gradle.kts` for every Play upload and
   update `versionName` when the release version changes.
2. Merge the candidate source to `master` or create the intended `v*` tag. The
   release workflow rejects every ref except `refs/heads/master` and
   `refs/tags/v*`.
3. Confirm the normal unit, lint, manifest, debug-build, and release validation
   checks are green for that immutable commit. CI validation outputs are not the
   production candidate.
4. Complete `RELEASE_CHECKLIST.md` and `PLAY_CONSOLE_CHECKLIST.md` through the
   pre-dispatch gates.

The production-signed candidate is created once by the protected release
workflow after owner approval. Never substitute a local build, rebuild after the
workflow verifies it, or upload another artifact under the same candidate
record. A code, resource, dependency, configuration, or declaration change
requires a new ref and a new workflow run.

## 2. Build and publish to internal testing

1. Confirm the `google-play-internal` environment has required reviewers and
   **Selected branches and tags** restricted to branch `master` and tags `v*`.
2. Confirm its eight secrets are current: `PLAY_STORE_JSON_KEY`,
   `ANDROID_KEYSTORE_BASE64`, `KEYSTORE_PASSWORD`, `KEY_ALIAS`, `KEY_PASSWORD`,
   `ADMOB_APP_ID`, `ADMOB_BANNER_ID`, and `RELEASE_EVIDENCE_PASSWORD`.
3. Confirm the dedicated Play service account can access only
   `eu.indiewalkabout.mathbrainer` and has only **Release apps to testing
   tracks**. It must not have account-wide, admin, production, store-presence,
   financial, or tester-list permissions.
4. In GitHub Actions, manually dispatch **Google Play Internal Release** for the
   trusted ref. The environment reviewer approves before the job builds or
   uploads anything.
5. The workflow runs `build_release_bundle`, verifies the signature and mapping
   outputs, and records SHA-256 plus version/commit/timestamp metadata before it
   calls the upload-only `bundle exec fastlane android internal_release` lane.
6. Confirm the post-upload hash check passed and the submitted version is visible
   in Play Console's internal testing track.
7. Download `mathbrainer-internal-release-<commit SHA>`. Because this repository
   is public, the artifact contains only `release-evidence.tar.gz.enc` plus the
   non-sensitive checksum and manifest. The AAB, `mapping.txt`, and
   `resources.txt` exist only inside the AES-256-CBC/PBKDF2 encrypted archive.
8. Follow the decryption procedure in `fastlane/README.md` in a private
   directory. Retain the passphrase separately in a private password manager and
   never print it or put it on a command line. Compare the decrypted AAB to the
   published checksum, then keep the decrypted files only in the private release
   archive.

If Play upload fails after encryption, the workflow still retains that encrypted
evidence for diagnosis. Do not retry by rebuilding it: fix the cause, increment
the version if Play accepted it, and create a new candidate when any input must
change.

The workflow uploads only to internal testing. It cannot publish to closed or
production tracks and does not upload or update store media. Closed or
production promotion is a separate owner action in Play Console after the
applicable approval and observation gates are met.

## 3. Validate the workflow-produced candidate

1. Install the version already uploaded by the workflow from the Play internal
   testing opt-in path. Do not upload a second copy through Internal App Sharing.
2. Test fresh install and upgrade from the current production version on API 26,
   30, 35, and 36, including representative phone and tablet devices.
3. Execute the full product/device matrix in `RELEASE_CHECKLIST.md`.
4. Confirm the installed version code matches the release manifest and Play
   Console, and confirm the retained checksum identifies the exact
   workflow-produced AAB used by downstream approval.
5. Confirm mapping association, production ad configuration, UMP behavior, and
   the live privacy-policy link from the installed candidate.
6. Resolve the pre-launch report or record an owner-approved false-positive
   rationale. P1/P2 issues cannot be waived.

## 4. Promote in stages

1. Internal testing: observe for at least 24 hours with named tester sign-off.
2. Closed testing: promote the exact internal version and observe for 3-7 days on
   representative devices and upgrade paths. Restart the window after a
   candidate change.
3. Production 5%: observe for at least 24 hours.
4. Production 20%: observe for at least 24 hours.
5. Production 50%: observe for at least 24 hours.
6. Production 100%: promote only after all prior gates remain green.

At every stage inspect Android vitals, Play pre-launch/reviews, support reports,
and observed ad/consent failures. Record stage start/end, population, metrics,
issues, and approver. Advance only when crash-free users are at least 99.5%,
user-perceived ANR is below 0.47%, and no new P1/P2 issue is open.

## 5. Close the release

After stable 100% rollout, tag the exact source commit as `v<versionName>` and
archive final Play approval and rollout evidence. Update the Data Safety review
whenever SDKs or runtime data behavior change. Keep version-matched artifacts
for the retention period chosen by the owner.

External controls such as environment protection, Play permissions and
submissions, production credentials, live policy publication, observation
windows, and rollout actions must be completed by an authorized owner;
repository changes cannot satisfy them alone.
