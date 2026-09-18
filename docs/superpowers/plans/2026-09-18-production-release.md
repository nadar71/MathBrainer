# MathBrainer Production Release Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Add a manual, protected GitHub Actions release that builds MathBrainer 3.0.2 (13) once, distributes that AAB to Firebase testers, then publishes it and localized store media to Google Play production at 100%.

**Architecture:** Keep the existing internal-release path unchanged. Add explicit Fastlane lanes that consume one prebuilt, checksummed AAB, and one `production-release` GitHub Actions job that scopes credentials to their consuming steps and never persists release artifacts. Generate Google Play metadata for `en-US` and `it-IT` from checked-in release copy and media.

**Tech Stack:** GitHub Actions, Ruby 3.3, Fastlane 2.239.0, `fastlane-plugin-firebase_app_distribution` 1.0.0, Gradle/Android App Bundle, Minitest.

**Spec:** Approved conversation requirements: version `3.0.2` / code `13`, Play `production` with `completed` status and 100% rollout, Firebase app `1:632111455840:android:952c21d10fcd75073aed05`, group `owner-testers`, EN/IT screenshots and release notes, one AAB, no release-evidence artifact/password.

## Global Constraints

- Workflow trigger is manual `workflow_dispatch` only.
- Protected GitHub environment is exactly `production-release`.
- Build and sign the AAB exactly once; Firebase and Play consume the same checksummed file.
- Firebase distribution must complete before the Play production upload.
- Secrets remain step-scoped and temporary credential files are removed with `if: always()`.
- Existing internal-release behavior remains unchanged.
- Preserve unrelated working-tree changes.

---

### Task 1: Production release contract tests

**Files:**
- Create: `test/production_release_test.rb`

**Interfaces:**
- Consumes: approved workflow and Fastlane contracts above.
- Produces: executable Minitest assertions for fixed destinations, credential scoping, ordering, localization, and single-build behavior.

- [ ] **Step 1: Write failing contract tests for missing production workflow and lanes.**
- [ ] **Step 2: Run `ruby test/production_release_test.rb`; verify failure is caused by missing implementation.**

### Task 2: Fastlane production and Firebase lanes

**Files:**
- Modify: `Gemfile`
- Modify: `Gemfile.lock`
- Modify: `fastlane/Fastfile`

**Interfaces:**
- Consumes: `PLAY_PRODUCTION_JSON_KEY`, `FIREBASE_APP_ID`, `FIREBASE_TESTER_GROUPS`, `FIREBASE_AAB_PATH`, and `GOOGLE_APPLICATION_CREDENTIALS`.
- Produces: `sync_store_media`, `production_release`, and `distribute_firebase` lanes that consume `app/build/outputs/bundle/release/app-release.aab` without rebuilding.

- [ ] **Step 1: Add and lock `fastlane-plugin-firebase_app_distribution` 1.0.0.**
- [ ] **Step 2: Generate localized EN/IT metadata, changelogs, phone screenshots, seven-inch screenshots, and ten-inch screenshots.**
- [ ] **Step 3: Add checksum-enforced production and Firebase upload lanes.**
- [ ] **Step 4: Run the contract test and confirm remaining failures concern only the absent workflow.**

### Task 3: Protected production workflow

**Files:**
- Create: `.github/workflows/release-production.yml`

**Interfaces:**
- Consumes GitHub Environment secrets: `ANDROID_KEYSTORE_BASE64`, `KEYSTORE_PASSWORD`, `KEY_ALIAS`, `KEY_PASSWORD`, `ADMOB_APP_ID`, `ADMOB_BANNER_ID`, `FIREBASE_APP_DISTRIBUTION_JSON_KEY`, `PLAY_PRODUCTION_JSON_KEY`.
- Consumes variables: `FIREBASE_APP_ID`, `FIREBASE_TESTER_GROUPS`.
- Produces: Firebase tester distribution followed by Google Play production publication of the same verified AAB.

- [ ] **Step 1: Implement the manual one-job workflow with trusted-ref and version checks before secret access.**
- [ ] **Step 2: Build and verify one signed AAB and its SHA-256 checksum.**
- [ ] **Step 3: Materialize Firebase credentials temporarily, distribute, and clean them up.**
- [ ] **Step 4: Publish the same checksummed AAB and localized assets to Play production.**
- [ ] **Step 5: Run `ruby test/production_release_test.rb` and make it pass.**

### Task 4: Documentation and full verification

**Files:**
- Modify: `fastlane/README.md`
- Modify: `docs/release/RELEASE_RUNBOOK.md`
- Modify: `docs/release/RELEASE_CHECKLIST.md`

**Interfaces:**
- Consumes: final workflow/secret contract.
- Produces: operator instructions for production dispatch, failure boundaries, and post-release checks.

- [ ] **Step 1: Document the exact environment secrets, variables, dispatch ref, upload order, and recovery behavior.**
- [ ] **Step 2: Run all Ruby release tests.**
- [ ] **Step 3: Validate workflow YAML, Fastlane syntax/configuration, generated metadata, and repository diff.**
- [ ] **Step 4: Run the signed release build locally when protected local inputs are available.**
- [ ] **Step 5: Commit only intended production-release files while preserving unrelated user changes.**
