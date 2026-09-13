# Fastlane and GitHub Actions Internal Release Implementation Plan

**Goal:** Provide a manually approved workflow that builds one production-signed
Math Brainer AAB and uploads that same verified artifact only to Google Play's
internal testing track.

**Architecture:** The workflow owns the complete candidate lifecycle. It builds
through `build_release_bundle`, verifies and checksums the output, encrypts the
private evidence, calls an upload-only `internal_release` lane, then confirms the
post-upload file hash still matches. The job is protected by the
`google-play-internal` environment and accepts only `refs/heads/master` or
`refs/tags/v*` before any step receives a secret.

**Tech stack:** Android Gradle Plugin 8.9.1, Gradle wrapper, Ruby 3.3,
Fastlane 2.239.0, Minitest, GitHub Actions, OpenSSL, Google Play Developer API

**Spec:** `docs/superpowers/specs/2026-09-11-fastlane-github-actions-internal-release-design.md`

## Global Constraints

- Trigger only through `workflow_dispatch`; never accept a track input.
- Allow only `refs/heads/master` and `refs/tags/v*` in workflow code and mirror
  that policy with the environment's **Selected branches and tags** rules.
- Require protected-environment approval before the job builds or uploads.
- Fix Play destination to package `eu.indiewalkabout.mathbrainer`, track
  `internal`, status `completed`, and AAB-only upload.
- Build exactly once. The `internal_release` lane uploads the existing
  `app/build/outputs/bundle/release/app-release.aab` only after its SHA-256
  matches the workflow-generated checksum.
- Map each secret only to the step that consumes it. The eight environment
  secrets are `PLAY_STORE_JSON_KEY`, `ANDROID_KEYSTORE_BASE64`,
  `KEYSTORE_PASSWORD`, `KEY_ALIAS`, `KEY_PASSWORD`, `ADMOB_APP_ID`,
  `ADMOB_BANNER_ID`, and `RELEASE_EVIDENCE_PASSWORD`.
- Never upload a plaintext AAB or mapping from this public repository. Encrypt
  private evidence with AES-256-CBC, PBKDF2, 100,000 iterations, and random salt;
  upload only the encrypted archive plus non-sensitive checksum and manifest.
- Keep every GitHub Action pinned to an immutable commit SHA with a release-tag
  comment. Do not contact Play or use real credentials during local verification.

## Task 1: Guard the revised contract test-first

**Files:** `test/release_configuration_test.rb`

1. Add separate regression tests for Ruby 3.3, trusted-ref behavior, secret
   scope, upload-only Fastlane behavior, strict step order, post-upload hash
   equality, encrypted artifact contents, and operator guidance.
2. Execute the trusted-ref script in the test for both allowed refs and several
   rejected refs rather than checking presence alone.
3. Parse the workflow and compare step indices so a later reordering fails.
4. Run `ruby test/release_configuration_test.rb` before implementation and
   retain the expected failing output as RED evidence.

## Task 2: Make Fastlane upload an existing verified AAB

**Files:** `fastlane/Fastfile`, `test/release_configuration_test.rb`

1. Keep `build_release_bundle` as the sole Gradle build lane.
2. Remove `build_release_bundle` from `internal_release`.
3. Require the fixed AAB and adjacent `.sha256` record to be non-empty.
4. Compute the AAB SHA-256 in Ruby and reject an invalid or mismatched record
   before calling `upload_to_play_store`.
5. Preserve the fixed package, `internal` track, `completed` status, and all
   AAB-only skip flags.
6. Call Fastlane's `skip_docs` control so lane discovery or execution cannot
   overwrite the hand-maintained operator instructions in `fastlane/README.md`.

## Task 3: Enforce the secure workflow sequence

**Files:** `.github/workflows/release-internal.yml`,
`test/release_configuration_test.rb`

Implement this exact order in the environment-protected job:

1. Checkout and install pinned Java, Gradle, Ruby 3.3, and Bundler dependencies.
2. Run the configuration contract.
3. Accept `refs/heads/master` or `refs/tags/v*`; fail every other ref before any
   secret mapping.
4. Decode the keystore below `RUNNER_TEMP` with only
   `ANDROID_KEYSTORE_BASE64` in scope.
5. Build with only signing and AdMob secrets in scope.
6. Require the AAB and both mapping outputs, verify the complete JAR signature,
   and create SHA-256 plus commit/version/timestamp manifest evidence.
7. With only `RELEASE_EVIDENCE_PASSWORD` in scope, tar the AAB, mappings,
   checksum, and manifest; encrypt the tar through OpenSSL's environment
   passphrase interface; remove the plaintext tar.
8. Upload the already-built AAB to Play using only `PLAY_STORE_JSON_KEY`.
9. Run `sha256sum --check` after a successful Play upload.
10. With `if: always()`, archive the encrypted evidence after both successful
    and failed Play uploads, but only if encryption succeeded. Include plaintext
    checksum and manifest as non-sensitive lookup metadata; never include raw AAB
    or mapping paths in `actions/upload-artifact`.

## Task 4: Align setup, operation, and downstream approval

**Files:** `fastlane/README.md`, `docs/release/RELEASE_RUNBOOK.md`,
`docs/release/RELEASE_CHECKLIST.md`, and the linked design spec

1. Document the eight secrets and the cost of retaining one additional protected
   environment passphrase.
2. Configure required reviewers and **Selected branches and tags** for `master`
   and `v*`, matching the workflow's canonical refs.
3. Configure a dedicated Play service account with app access only to
   `eu.indiewalkabout.mathbrainer` and only **Release apps to testing tracks**;
   explicitly withhold production, store, finance, tester-list, admin, and
   account-wide rights.
4. Explain that protected-environment approval precedes the workflow build and
   upload, and that all later validation/promotion decisions use the exact
   workflow-produced AAB already uploaded to internal testing.
5. Document AES-256-CBC/PBKDF2 decryption without placing the passphrase on the
   command line, and require private password-manager retention outside GitHub
   and the repository.
6. Remove guidance to rebuild or separately upload the candidate through Play
   Internal App Sharing.

## Task 5: Verify and hand off without a live release

Run from the isolated worktree:

```sh
ruby test/release_configuration_test.rb
ruby -c fastlane/Fastfile
ruby -e 'require "yaml"; YAML.safe_load(File.read(".github/workflows/release-internal.yml"), aliases: true); puts "YAML syntax OK"'
actionlint .github/workflows/release-internal.yml
BUNDLE_PATH=<isolated-path> bundle check
BUNDLE_PATH=<isolated-path> bundle exec fastlane lanes
git diff --check
git grep -n -E 'BEGIN PRIVATE KEY|private_key_id|ANDROID_KEYSTORE_BASE64=' -- ':!docs/superpowers/**'
```

Exercise the evidence commands locally with disposable files and a disposable
passphrase: create checksum/manifest inputs, encrypt and decrypt the tar, compare
the recovered files byte-for-byte, and confirm the upload-artifact path exposes
no plaintext private output.

Do not call `internal_release`, Play Console, or the Developer API. External
verification remains an authorized owner's approved workflow run and visual
confirmation that the new version appears on the internal track.
