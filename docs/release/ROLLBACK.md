# Rollback And Hotfix Procedure

Google Play cannot restore an older artifact with a lower `versionCode` over an
installed newer release. In this runbook, rollback means halting an active
staged rollout or shipping a corrected, tested build with a higher version code.

## Halt Conditions

Halt promotion immediately for any of the following:

- Data loss, destructive database migration, or corrupted statistics.
- Startup failure, widespread navigation/gameplay blocker, or broken upgrade.
- Privacy/policy violation, consent bypass, or ads requested before eligibility.
- Crash-free users below 99.5% or user-perceived ANR at/above 0.47%.
- Any new P1/P2 issue, unexpected production credential/ID, or signing concern.

## Incident Response

1. Stop the staged rollout in Play Console; do not promote another track.
2. Record version, rollout percentage, time, affected devices, reproduction,
   vitals cluster, screenshots/logs, and version-matched mapping evidence.
3. Assign incident owner and severity. Notify privacy/policy stakeholders
   immediately when data, consent, ads, or declarations may be affected.
4. Decide whether halting contains the issue. Users who already received the
   build remain on it, so prepare a hotfix when impact continues.
5. Branch from the released source, make the smallest safe fix, increment
   `versionCode`, and add regression coverage.
6. Run the complete candidate gates, upgrade/data tests, and affected product
   matrix. Do not bypass minification, signing, consent, or migration checks.
7. Release the hotfix through internal testing first, then staged production;
   use accelerated windows only with explicit owner approval and recorded risk.
8. Confirm recovery at each stage, close the incident, and capture root cause,
   preventive action, and any policy/Data Safety/privacy-policy correction.

## Emergency Contacts

Release owner: __________  Android owner: __________  Play/privacy owner: __________

Keep contacts and Play permissions current before rollout. Never place private
credentials, personal phone numbers, or incident-user data in this repository.
