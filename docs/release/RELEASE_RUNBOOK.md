# Production Release Runbook

## 1. Build The Candidate

1. Select an immutable commit whose required GitHub checks are green.
2. Build with production IDs and signing credentials supplied externally.
3. Run the final command set from the production-readiness plan, verify the AAB
   signature, and calculate its SHA-256 checksum.
4. Archive the AAB, `mapping.txt`, checksum, commit SHA, version metadata,
   release notes, dependency report, test results, and Play evidence privately.
5. Complete `RELEASE_CHECKLIST.md` and `PLAY_CONSOLE_CHECKLIST.md`.

Never rebuild an artifact after approval. Any code, resource, dependency,
configuration, or declaration change creates a new candidate and restarts the
applicable checks.

## 2. Validate Through Play

1. Upload the exact candidate through Play Internal App Sharing.
2. Test fresh install and upgrade from the current production version on API
   26, 30, 35, and 36, including representative phone and tablet devices.
3. Execute the full product/device matrix in `RELEASE_CHECKLIST.md`.
4. Confirm mapping association, production ad configuration, UMP behavior, and
   the live privacy-policy link from the installed candidate.
5. Resolve the pre-launch report or record an owner-approved false-positive
   rationale. P1/P2 issues cannot be waived.

## 3. Promote In Stages

1. Internal testing: observe for at least 24 hours with named tester sign-off.
2. Closed testing: observe for 3-7 days on representative devices and upgrade
   paths. Restart the window after a candidate change.
3. Production 5%: observe for at least 24 hours.
4. Production 20%: observe for at least 24 hours.
5. Production 50%: observe for at least 24 hours.
6. Production 100%: promote only after all prior gates remain green.

At every stage inspect Android vitals, Play pre-launch/reviews, support reports,
and observed ad/consent failures. Record stage start/end, population, metrics,
issues, and approver. Advance only when crash-free users are at least 99.5%,
user-perceived ANR is below 0.47%, and no new P1/P2 issue is open.

## 4. Close The Release

After stable 100% rollout, tag the exact source commit as `v<versionName>` and
archive final Play approval and rollout evidence. Update the Data Safety review
whenever SDKs or runtime data behavior change. Keep version-matched artifacts
for the retention period chosen by the owner.

External controls such as branch protection, Play submissions, production
credentials, live policy publication, observation windows, and rollout actions
must be completed by an authorized owner; repository changes cannot satisfy
them alone.
