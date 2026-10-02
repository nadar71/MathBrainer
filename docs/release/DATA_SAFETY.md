# Data Safety Draft

This is an evidence worksheet, not proof of a submitted Play Console form.
Revalidate it against the exact release AAB and the current Google Mobile Ads
SDK disclosure before every release.

## App-Owned Data

- Math Brainer stores scores, wins, losses, levels, and game counts in its local
  Room database. The app has no account system and does not send these gameplay
  statistics to an Indie Walkabout server.
- The approved Android backup rules include the Room database. A signed-in
  device may therefore transfer gameplay statistics through Google's backup
  service for restore/device migration. The live privacy policy must disclose
  this behavior. Disable database backup before release if it will not.
- Preferences, including consent-related local state, are excluded from backup.
- The app offers no cloud account, social feature, or user-generated content.

## Google Mobile Ads And UMP

The release includes Google Mobile Ads and User Messaging Platform. Google's
current SDK disclosure says Mobile Ads automatically handles the following for
advertising, analytics, and fraud prevention. Confirm the exact Play form
categories and whether Play treats each use as collection and/or sharing:

- Approximate location derived from IP address.
- App interactions, including launches, taps, and ad/video interactions.
- Diagnostics and performance information.
- Device or other identifiers, including advertising ID and app set ID when
  available and permitted.

The SDK encrypts data in transit. Ad requests and Mobile Ads initialization are
gated behind UMP eligibility in this app. Consent does not remove the need to
declare SDK data handling. Do not mark data as optional merely because UMP is
shown; validate the Play definition against each runtime path.

Source reviewed 2026-08-27:
https://developers.google.com/admob/android/privacy/play-data-disclosure

## Proposed Play Answers

- Data collection/sharing: Yes, because the advertising SDK handles data.
- Data encrypted in transit: Yes for Mobile Ads traffic, per Google.
- Users can request deletion: Not applicable to an app account because there is
  no account. Local app data can be removed by clearing storage/uninstalling;
  the privacy policy must explain any applicable ad-data controls.
- Independent security review: Do not claim one without current evidence.
- Ads: Yes.

## Final Verification

- Export the final dependency graph and confirm there is no additional SDK with
  a disclosure obligation.
- Inspect the merged release manifest for `AD_ID` and complete the matching Play
  advertising-ID declaration.
- Reconcile every answer with the live privacy policy and UMP behavior.
- Save dated screenshots/PDF evidence of the submitted Data Safety form in the
  private release archive, not in this public repository.
- Obtain legal review where required; this document is engineering evidence,
  not legal advice.
