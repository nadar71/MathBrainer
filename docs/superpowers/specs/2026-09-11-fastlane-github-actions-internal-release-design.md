# Fastlane and GitHub Actions Internal Release Design

## Goal

Provide a secure, repeatable way for a release owner to build Math Brainer from an immutable repository commit and publish the signed Android App Bundle to Google Play's internal testing track by manually dispatching a GitHub Actions workflow.

## Scope

The release automation is limited to Google Play's `internal` track. It builds and uploads an AAB, verifies and archives release evidence, and does not promote a release to closed testing or production. It also does not change Play listing text, screenshots, icons, or feature graphics during routine app releases.

The release owner must increment `versionCode` and update `versionName` in `app/build.gradle.kts` before dispatch. Google Play remains the authority that rejects a reused or invalid version code.

## Architecture

A dedicated `.github/workflows/release-internal.yml` workflow is triggered only through `workflow_dispatch`. Its publishing job targets a protected GitHub environment named `google-play-internal`, allowing repository owners to require approval and restrict access to production credentials.

The job checks out the selected commit, installs the project's Java and Ruby toolchains, restores Gradle and Bundler caches, reconstructs the upload keystore in the runner's temporary directory, and supplies all signing, AdMob, and Play API values through environment variables. No credential file is written inside the checkout.

Fastlane owns the release orchestration. A dedicated `internal_release` lane builds the release bundle and uploads only that AAB to the fixed `internal` track with `completed` status. The lane does not accept a track override and skips metadata, changelog, image, and APK uploads. Existing store-media synchronization remains available as a separate explicit operation.

## Credentials and Secret Handling

The `google-play-internal` GitHub environment provides these secrets:

- `PLAY_STORE_JSON_KEY`: complete Google Play service-account JSON content.
- `ANDROID_KEYSTORE_BASE64`: base64-encoded Android upload keystore.
- `KEYSTORE_PASSWORD`: upload keystore password.
- `KEY_ALIAS`: upload key alias.
- `KEY_PASSWORD`: upload key password.
- `ADMOB_APP_ID`: production AdMob application ID.
- `ADMOB_BANNER_ID`: production banner placement ID.

The workflow masks and avoids printing secret values. It decodes `ANDROID_KEYSTORE_BASE64` to a path below `${RUNNER_TEMP}` and sets `KEYSTORE_FILE` to that absolute path. The existing Gradle release validation rejects missing credentials, missing keystore files, and Google sample AdMob identifiers.

The Play service account has only the Play Console permissions necessary to upload releases for `eu.indiewalkabout.mathbrainer`. GitHub environment approval and branch/tag restrictions are configured in repository settings and cannot be enforced by repository files alone.

## Dependency Management

A root `Gemfile` pins Fastlane to an explicit compatible version, and `Gemfile.lock` records all resolved Ruby dependencies. CI invokes Fastlane with `bundle exec`. GitHub Actions dependencies are referenced by immutable commit SHA and annotated with their release tag, matching the existing Android CI policy.

## Release Flow

1. The release owner updates version metadata, merges the candidate, confirms normal Android CI is green, and manually dispatches the internal-release workflow for the intended Git ref.
2. The protected GitHub environment applies any configured reviewer gate.
3. The workflow validates that all required secrets are non-empty, decodes the keystore outside the checkout, and installs pinned dependencies.
4. Fastlane runs Gradle `bundleRelease`, which invokes the existing release configuration validation.
5. The workflow verifies the AAB signature, checks that ProGuard mapping outputs exist, and produces an SHA-256 checksum plus a small manifest containing commit and version metadata.
6. Fastlane uploads the exact verified AAB to Play's internal track without modifying store content.
7. GitHub archives the AAB, mapping files, checksum, and release manifest with a bounded retention period, including when the Play upload fails after the bundle was built.
8. Further testing and promotion follow `docs/release/RELEASE_RUNBOOK.md` and remain deliberate owner actions.

The workflow concurrency key permits only one internal release for the repository at a time and does not cancel a running upload when another dispatch begins.

## Failure Handling

The job fails before upload if a required secret is absent, the keystore cannot be decoded, Gradle release validation fails, the AAB or mapping files are missing, or signature verification fails. Fastlane surfaces Play API authentication, version-code, permission, and upload errors without attempting another track.

Release artifacts are uploaded with `if: always()` after the build/evidence phase, but their step fails if the expected evidence was never produced. Temporary credentials are runner-scoped and disappear when the GitHub-hosted runner is destroyed.

The workflow never retries by rebuilding a changed artifact. A failed upload may be re-run from the same immutable commit; a source, resource, configuration, or dependency change requires a new candidate and the applicable checks to run again.

## Verification

Repository-level verification covers:

- Ruby syntax and Fastlane configuration parsing.
- Bundler lockfile consistency.
- YAML parsing and static checks for manual-only triggering, fixed internal track, protected environment, immutable action references, concurrency, secret names, and artifact retention.
- A local signed release bundle using non-production validation credentials, followed by AAB signature verification and checks for mapping outputs.
- Existing unit, lint, manifest, debug-build, and smoke checks remain the release prerequisites; the publishing workflow does not duplicate the full Android CI suite.

The actual Play upload cannot be proven locally. Its final verification is a manually approved workflow run using the configured GitHub environment and confirmation that the new version appears on the Play internal testing track.

## Documentation

`fastlane/README.md` will document local lanes and the internal-only upload behavior. `docs/release/RELEASE_RUNBOOK.md` and `docs/release/RELEASE_CHECKLIST.md` will document the manual dispatch, required GitHub environment, evidence artifact, and separation between internal upload and later promotion.
