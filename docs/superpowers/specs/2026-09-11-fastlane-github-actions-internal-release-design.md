# Fastlane and GitHub Actions Internal Release Design

## Goal

Provide a secure, repeatable way for a release owner to build Math Brainer from
an immutable repository commit and publish that exact signed Android App Bundle
to Google Play's internal testing track through a manually dispatched GitHub
Actions workflow.

## Scope

The automation is fixed to package `eu.indiewalkabout.mathbrainer`, Play track
`internal`, release status `completed`, and AAB-only upload. It cannot promote a
release to closed testing or production and does not change Play listing text,
release notes, screenshots, icons, or feature graphics.

The release owner increments `versionCode` and updates `versionName` in
`app/build.gradle.kts` before dispatch. Google Play remains the authority that
rejects a reused or invalid version code.

## Architecture

`.github/workflows/release-internal.yml` is manual-only and has one job protected
by the `google-play-internal` environment. Environment approval therefore occurs
before the job builds or uploads anything. Repository settings must configure
required reviewers and **Selected branches and tags** matching the repository
policy: `refs/heads/master` and `refs/tags/v*`. The workflow independently
enforces the same policy before its first secret-bearing step.

The workflow, rather than the upload lane, owns the artifact lifecycle:

1. `build_release_bundle` builds one signed AAB.
2. The workflow verifies its signature and mapping outputs, then writes its
   SHA-256 checksum and release manifest.
3. The private evidence is encrypted.
4. `internal_release` validates the AAB against the recorded checksum and uploads
   that already-built path without invoking Gradle.
5. The workflow recomputes the hash after a successful upload and fails if the
   local artifact changed.

This ordering prevents a build during upload and makes the workflow-produced AAB
the sole candidate used by Play, retained evidence, internal testing, and later
approval decisions.

## Credentials and Secret Handling

The protected environment contains exactly eight release secrets:

- `PLAY_STORE_JSON_KEY`: complete Google Play service-account JSON content.
- `ANDROID_KEYSTORE_BASE64`: base64-encoded Android upload keystore.
- `KEYSTORE_PASSWORD`: upload keystore password.
- `KEY_ALIAS`: upload key alias.
- `KEY_PASSWORD`: upload key password.
- `ADMOB_APP_ID`: production AdMob application ID.
- `ADMOB_BANNER_ID`: production banner placement ID.
- `RELEASE_EVIDENCE_PASSWORD`: high-entropy passphrase for the evidence archive.

Each secret is mapped only to the step that consumes it. In particular,
`RELEASE_EVIDENCE_PASSWORD` is available only to the encryption step, is passed
to OpenSSL through its environment-variable interface, and is never printed.
The keystore is decoded below `${RUNNER_TEMP}` and no credential file is written
inside the checkout.

Because this repository is public, Actions must not upload a plaintext AAB or
mapping file. The workflow creates a private tar archive containing the AAB,
`mapping.txt`, `resources.txt`, checksum, and manifest, then encrypts it with
OpenSSL AES-256-CBC, PBKDF2, 100,000 iterations, and a random salt. Only the
encrypted archive plus the non-sensitive checksum and manifest are uploaded as
the `mathbrainer-internal-release-<commit SHA>` Actions artifact. The release
owner retains the passphrase separately in a private password manager.

## Least-Privilege Play Identity

Use a dedicated Google Cloud service account for this workflow. Invite its email
from Play Console **Users and permissions**, choose **App permissions**, and
select only Math Brainer (`eu.indiewalkabout.mathbrainer`). Grant only **Release
apps to testing tracks**, which is the permission required to create and roll
out an internal-testing release. Do not grant account-wide access, **Admin**,
**Release to production, exclude devices, and use Play App Signing**, **Manage
store presence**, financial permissions, or tester-list management. Store that
dedicated account's JSON only as `PLAY_STORE_JSON_KEY` in the protected
environment and periodically review/revoke unused access.

## Dependency Management

The root `Gemfile` pins Fastlane to `2.239.0`, and `Gemfile.lock` records the
resolved Ruby dependencies. The workflow selects Ruby 3.3 explicitly and invokes
Fastlane through `bundle exec`. Every GitHub Action is referenced by immutable
commit SHA and annotated with its release tag.

## Release Flow

1. The release owner updates version metadata, merges the candidate to `master`
   or creates a trusted `v*` tag, and confirms normal Android CI is green.
2. The owner manually dispatches the workflow for that ref and approves the
   protected environment before any build or upload begins.
3. The workflow validates the ref, reconstructs the upload keystore, and builds
   the signed AAB once.
4. It verifies signature and mappings and writes checksum/manifest evidence.
5. It encrypts the private evidence with the protected passphrase.
6. Fastlane uploads the exact checksummed AAB to Play internal testing.
7. The workflow checks that the post-upload hash equals the pre-upload hash.
8. Even if Play upload fails, GitHub uploads the already-encrypted evidence when
   encryption succeeded. Downstream testing and promotion decisions refer to the
   exact workflow-produced artifact already submitted to the internal track.

Repository-wide non-cancelling concurrency permits only one internal release at
a time.

## Failure Handling

The job fails before Play upload for an untrusted ref, missing build credential,
keystore error, Gradle validation error, missing AAB/mapping, failed signature,
invalid manifest metadata, missing evidence passphrase, encryption failure, or
checksum mismatch. Fastlane surfaces Play authentication, version-code,
permission, and upload errors without changing tracks. No step retries by
rebuilding the artifact.

The encrypted Actions artifact is uploaded after a Play failure only when the
encryption step succeeded. Temporary plaintext credentials and evidence are
runner-scoped; the intermediate plaintext tar is removed by an exit trap.

## Verification

Repository-level verification covers Ruby syntax, Fastlane discovery, Bundler
lockfile consistency, YAML parsing, `actionlint`, Ruby 3.3 selection, manual-only
triggering, trusted-ref enforcement, protected environment, exact secret scope,
immutable Actions, build/evidence/upload order, encryption parameters, public
artifact contents, and post-upload hash equality.

A local non-production signed build may verify Gradle packaging, AAB signatures,
and mapping generation, but it must not call Play. Final external verification is
an owner-approved workflow run using the protected environment and confirmation
that the version appears on Play internal testing.
