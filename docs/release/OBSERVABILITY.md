# Release Observability

## Current Provider Decision

Math Brainer currently uses **Google Play Android vitals only**. It is the
current production crash and ANR provider; no Crashlytics, Sentry, Firebase,
or other telemetry SDK is included in this repository. `AppErrorReporter` is a
provider-neutral boundary and `NoOpErrorReporter` is the installed
implementation until the owner approves a provider after a privacy review.

Android vitals diagnoses uncaught crashes and ANRs from Google Play installs.
It does not receive `AppErrorReporter` recoverable events while the no-op
provider is active. Do not add a provider implementation, dependency, plugin,
or service configuration without written owner approval.

## Data And Privacy Boundary

The app sends no observability event or context to a third party in this state.
The sole current call site uses a newly-created, static failure description and
the fixed context `operation=open_app_store`; it does not pass the rejected
intent, package name, URL, or original exception.

Every future `AppErrorReporter` call must use static operational categories
only. Never include scores, answers, game state, consent state, advertising or
device IDs, account information, package identifiers, URLs, error messages
originating from user input, or other user-linked data in `context`. The
provider adapter must fail closed and must not alter application behavior when
reporting fails.

Android vitals data is collected by Google from eligible Android devices only
when the device user has opted in to automatically share usage and diagnostics
data. This is an operating-system/Google Play choice, not in-app UMP consent.
Play Console shows Android vitals data for 90 days; the Play Developer
Reporting API retains data for three years. The app does not enable that API.
Review the current [Android vitals documentation](https://support.google.com/googleplay/android-developer/answer/9844486)
and complete the Task 7 Data Safety review against the final shipped SDK set.

## Mapping And Symbols Runbook

1. Build the signed release AAB and retain the exact private build evidence:
   `app/build/outputs/mapping/release/mapping.txt`, version code, commit SHA,
   AAB checksum, and build timestamp.
2. Upload the same AAB to the internal track. Standard Android App Bundles
   include the ReTrace mapping for Play association; confirm the artifact shows
   its mapping asset. If it is missing, upload that exact `mapping.txt` in App
   bundle explorer before collecting crash evidence.
3. Native debug symbols are not applicable while the app has no app-owned
   native libraries. If native code is added later, configure release symbols
   and upload the version-matched archive before promoting that build.
4. Trigger no intentionally crashing production behavior. During normal
   internal-track testing, inspect one naturally occurring or test-lab crash or
   ANR cluster, when available, and verify that its app frames are deobfuscated
   or symbolicated. If no cluster occurs, record `no event available` rather
   than manufacturing a failure.
5. A provider-backed non-fatal event is **not available** with Play Android
   vitals alone. Record this as an external gate; it can only be verified after
   owner approval for a privacy-reviewed reporter provider.

Google's [deobfuscation guidance](https://support.google.com/googleplay/android-developer/answer/9848633)
describes the version-matched mapping/symbol workflow and confirms that an AAB
with a modern Android Gradle Plugin normally associates its mapping file.

## Launch Health Gates

Monitor Android vitals daily for the internal/closed observation window and at
each 5%, 20%, 50%, and 100% production rollout stage. Advance only when all of
the following hold for the full stage window:

- Crash-free users are at least 99.5%.
- User-perceived ANR rate is below 0.47% overall and below Play's 8%
  per-device bad-behavior threshold.
- No new P1 or P2 issue is open. P1 includes data loss, startup failure,
  policy violation, or a widespread blocker; P2 includes a reproducible core
  navigation or gameplay regression without a safe workaround.

For a threshold breach or a new P1/P2: halt further rollout immediately,
preserve the version/cluster/mapping evidence, triage the highest-impact issue,
ship a tested higher-version hotfix if necessary, and restart the affected
observation window only after the owner approves it. Google Play normally uses
the last 28 days when assessing Android vitals and may act sooner after a spike;
the 0.47% overall and 8% per-device thresholds come from the
[Android vitals bad-behavior thresholds](https://developer.android.com/games/optimize/vitals).
