# Play Console Production Checklist

Complete this checklist against the exact signed release AAB. Store Console
screenshots and approval evidence in the private release archive.

## Privacy And App Content

- [ ] Publish `https://www.indie-walkabout.eu/privacy/` as a public HTTPS page
  that opens without login, download, error, or unrelated redirect.
- [ ] Name both **Math Brainer** and **Indie Walkabout / Simone Mapelli**, exactly
  matching the Play listing developer identity.
- [ ] Disclose Mobile Ads/UMP purposes and data categories, local game
  statistics, Google backup/restore, retention/deletion choices, contact
  details, and an effective date.
- [ ] Confirm the in-app Credits privacy card opens that exact page.
- [ ] Submit the same URL in Play Console and verify it from a signed-out browser.
- [ ] Complete Data Safety using `DATA_SAFETY.md` and the final SDK inventory.
- [ ] Declare that the app contains ads.
- [ ] Complete target audience, content rating, app access, and advertising-ID
  declarations; do not classify the app as child-directed without a separate
  Families-policy and ad configuration review.

## Listing And Artifact

- [ ] Confirm title, short/full descriptions, icon, feature graphic, and release
  notes accurately match the shipped app.
- [ ] Review selected English and Italian phone, 7-inch tablet, and 10-inch
  tablet screenshots; remove debug/test-ad UI and obsolete screens.
- [ ] Verify package name, version name/code, target SDK, signing certificate,
  and production AdMob IDs from the uploaded artifact.
- [ ] Confirm release mapping is associated with the AAB in App bundle explorer.
- [ ] Resolve every Play pre-launch report blocker for stability, accessibility,
  security, and compatibility, or document owner-approved evidence for a false
  positive.

## External Release Gates

The source change does not complete these externally controlled actions:

- [ ] Live privacy page updated and manually verified.
- [ ] Play Console declarations submitted and evidence archived.
- [ ] Production signing and AdMob values supplied securely.
- [ ] Pre-launch report reviewed with no unresolved P1/P2 blocker.

Google Play requires the privacy policy to identify the app or listing entity
and requires disclosures to include SDK behavior. Re-check the current policy
before submission:
https://support.google.com/googleplay/android-developer/answer/10144311
