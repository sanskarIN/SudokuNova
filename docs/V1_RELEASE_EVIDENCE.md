# SudokuNova v1.0 Release Evidence Ledger

This is the concise exact-evidence ledger for the first stable SudokuNova release. Use [v1.0 Release Candidate Evidence Worksheet](V1_RELEASE_CANDIDATE.md) for the detailed manual test matrix.

## Current candidate

- Preparation branch: `release/v1.0-rc1-prep`
- PR #27: **verified and merged**
- Final verified PR head: `7016e21f36c8ecb8a495c446ffd8b57e9f20a4ea`
- PR #27 merge commit: `2329881aff8dabaf8d040918e16b6113e3900245`
- Candidate versionName: `1.0.0-rc.1`
- Candidate versionCode: `1000`
- Application ID: `in.sanskar.sudokunova`
- Namespace: `com.sanskar.sudokunova`
- Minimum Android API: `26`
- Compile/target SDK: `37`
- Stable `v1.0.0`: **not yet claimed**

If version code `1000` is accepted by a distribution track, the stable build must use a strictly higher version code.

## Repository-side RC evidence — VERIFIED

All rows below were verified on the same exact final PR #27 head `7016e21f36c8ecb8a495c446ffd8b57e9f20a4ea`.

| Gate | Exact head | Run/evidence | Result |
|---|---|---|---|
| Repository secret guard | `7016e21f36c8ecb8a495c446ffd8b57e9f20a4ea` | Android CI `32151771317` | PASS |
| Release-verifier Python tests | `7016e21f36c8ecb8a495c446ffd8b57e9f20a4ea` | Android CI `32151771317` | PASS |
| Partial signing fails closed | `7016e21f36c8ecb8a495c446ffd8b57e9f20a4ea` | Android CI `32151771317` | PASS |
| English/Hindi parity | `7016e21f36c8ecb8a495c446ffd8b57e9f20a4ea` | Android CI `32151771317` | PASS |
| Sudoku engine tests | `7016e21f36c8ecb8a495c446ffd8b57e9f20a4ea` | Android CI `32151771317` | PASS |
| Android JVM tests | `7016e21f36c8ecb8a495c446ffd8b57e9f20a4ea` | Android CI `32151771317` | PASS |
| AndroidTest compilation | `7016e21f36c8ecb8a495c446ffd8b57e9f20a4ea` | Android CI `32151771317` | PASS |
| Debug + release lint | `7016e21f36c8ecb8a495c446ffd8b57e9f20a4ea` | Android CI `32151771317` | PASS |
| Debug APK | `7016e21f36c8ecb8a495c446ffd8b57e9f20a4ea` | Android CI `32151771317` | PASS |
| R8/resource-shrunk release APK | `7016e21f36c8ecb8a495c446ffd8b57e9f20a4ea` | Android CI `32151771317` | PASS |
| Release AAB | `7016e21f36c8ecb8a495c446ffd8b57e9f20a4ea` | Android CI `32151771317` | PASS |
| APK/AAB/R8 structure/version check | `7016e21f36c8ecb8a495c446ffd8b57e9f20a4ea` | Android CI `32151771317` | PASS |
| SHA-256/size evidence generation | `7016e21f36c8ecb8a495c446ffd8b57e9f20a4ea` | Android CI `32151771317` | PASS |
| API-35 connected Compose/Room | `7016e21f36c8ecb8a495c446ffd8b57e9f20a4ea` | Android Instrumentation `32151771297` | PASS |

Android CI: run #635 / `32151771317` — GREEN.  
Android Instrumentation: run #188 / `32151771297` — GREEN.

No branch commit was added after this successful pair before PR #27 was merged.

## RC build artifact evidence — VERIFIED FOR UNSIGNED CI ARTIFACTS

Normal PR CI verified unsigned build outputs using:

```bash
python scripts/verify_release_outputs.py \
  --apk app/build/outputs/apk/release/app-release-unsigned.apk \
  --aab app/build/outputs/bundle/release/app-release.aab \
  --mapping app/build/outputs/mapping/release/mapping.txt \
  --metadata app/build/outputs/apk/release/output-metadata.json \
  --expected-version-code 1000 \
  --expected-version-name 1.0.0-rc.1 \
  --output app/build/outputs/release-evidence/sha256.txt
```

Exact evidence from the verified PR #27 head:

- APK SHA-256: `422a151ab3bb47268a69548ce5669b7a141169cc822400d3ef1376fa476b53c7`
- APK size: `1,849,599` bytes
- AAB SHA-256: `1bbbb2f227fc432efa74fa6efe16f2f17ae3aa5bf4a59ffac9e2e71de9a7cdfd`
- AAB size: `4,349,513` bytes
- R8 mapping SHA-256: `0f8b128679e858e0d835f0e3d23bfb629448efc4215703dd6c0f69b155e3f3ac`
- R8 mapping size: `39,198,732` bytes
- CI artifact name: `unsigned-release-builds`
- CI artifact ID: `9330415157`
- CI artifact size: `12,793,995` bytes
- GitHub artifact digest: `sha256:0f1fa33127f6ae46d633c039bf0aad2e308b11e94bbd5567dd0fbc4805b4263c`
- Recorded artifact expiry: `2026-09-01`

These are unsigned repository-CI verification artifacts. They are not production-signed release evidence.

## Post-RC release-validation hardening — TOOLING ONLY

A later repository hardening line adds stricter release validation without claiming that production signing or manual QA has already happened.

The release verifier now supports:

- exact `applicationId` validation through `--expected-application-id`;
- APK signature verification through `apksigner`;
- AAB signature verification through `jarsigner`;
- AAB signer-certificate inspection through `keytool`;
- expected APK signer SHA-256 identity through `--expected-apk-cert-sha256`;
- expected AAB signer/upload SHA-256 identity through `--expected-aab-cert-sha256`;
- normalized signer-certificate evidence through `--signature-output`;
- fail-closed certificate mismatch handling.

`.github/workflows/release-validation.yml` provides a manually dispatched protected validation path using the `production-release` GitHub Environment. It can build signed artifacts from protected secrets, verify their package/version/signature/certificate identities, record hashes and exact workflow context, and remove the temporary keystore afterward.

**Important:** the existence of this tooling is not production evidence. A real successful protected run on the exact intended release ref is still required before any corresponding production row below can move from `PENDING` to `PASS`.

## Production-signed artifact evidence

For the final signed artifacts, use the certificate-bound verifier path (or the protected Production Release Validation workflow):

```bash
python scripts/verify_release_outputs.py \
  --apk path/to/signed-release.apk \
  --aab path/to/signed-release.aab \
  --mapping path/to/mapping.txt \
  --metadata path/to/output-metadata.json \
  --expected-version-code <final-code> \
  --expected-version-name 1.0.0 \
  --expected-application-id in.sanskar.sudokunova \
  --output path/to/sha256.txt \
  --require-signatures \
  --expected-apk-cert-sha256 <expected-apk-cert-sha256> \
  --expected-aab-cert-sha256 <expected-aab-cert-sha256> \
  --signature-output path/to/signatures.txt
```

Certificate fingerprints must come from a trusted release/platform record rather than from the artifact being validated. With Play App Signing, distinguish the upload certificate used for the submitted AAB from the app-signing certificate used by Google Play for distributed APKs.

Production evidence remains pending:

- `production-release` GitHub Environment configured/restricted: `PENDING`
- Production/upload signing configured outside Git: `PENDING`
- Protected signed-release validation run on exact release ref: `PENDING`
- APK application ID/version metadata verification on signed output: `PENDING`
- APK signature verification: `PENDING`
- AAB signature verification: `PENDING`
- Expected APK certificate fingerprint/digest confirmed: `PENDING`
- Expected AAB upload certificate fingerprint/digest confirmed: `PENDING`
- Signed artifact SHA-256/size evidence recorded: `PENDING`
- Signed APK install/launch smoke: `PENDING`
- AAB testing/store-track validation: `PENDING`

Never record passwords, keystore bytes, private keys, tokens, or other secrets here.

## Manual device and accessibility evidence

These require actual target execution. CI does not satisfy them.

| Check | Target / OS | Evidence | Result |
|---|---|---|---|
| Fresh install / launch | Pending | Pending | PENDING |
| Upgrade from supported prior build | Pending | Pending | PENDING |
| Active-game process-death restoration | Pending | Pending | PENDING |
| TalkBack core navigation/focus order | Pending | Pending | PENDING |
| TalkBack Sudoku/hint semantics | Pending | Pending | PENDING |
| 200% font scale | Pending | Pending | PENDING |
| Narrow phone/window | Pending | Pending | PENDING |
| Large phone/window | Pending | Pending | PENDING |
| Tablet / large window | Pending | Pending | PENDING |
| Portrait/landscape/resize | Pending | Pending | PENDING |
| High Contrast | Pending | Pending | PENDING |
| Reduced Motion | Pending | Pending | PENDING |
| Hardware keyboard | Pending | Pending | PENDING |
| Release-only R8 smoke | Pending | Pending | PENDING |
| Backup export/restore | Pending | Pending | PENDING |
| Puzzle import/share | Pending | Pending | PENDING |

The detailed checklist and notes fields are in `V1_RELEASE_CANDIDATE.md`.

## Measured performance evidence

Status: **PENDING real measurement**.

Record target, exact commit, input, method and result for:

- startup;
- gameplay/navigation frame behavior;
- puzzle generation;
- hint computation;
- Custom Puzzle solving/validation;
- bounded backup import/restore;
- History/Saved Puzzles with representative records;
- memory behavior;
- ANR observations.

Do not convert subjective observations into invented benchmark numbers.

## Repository settings evidence

At RC-prep start, GitHub reported `main` as unprotected. The connected repository tool used for this work does not expose branch-protection/ruleset mutation.

- `main` protection/ruleset enabled: `PENDING`
- Required `Android CI / verify`: `PENDING`
- Required `Android Instrumentation / connected-tests`: `PENDING`
- Force-push/deletion restrictions reviewed: `PENDING`
- Security/Dependabot/secret-scanning settings reviewed: `PENDING`
- `production-release` environment access/reviewer/ref restrictions reviewed: `PENDING`

See `GITHUB_REPOSITORY_SETTINGS.md` and `PRODUCTION_RELEASE_VALIDATION.md`.

## Store / publication evidence

Status: **PENDING**.

Before completion record:

- actual screenshots from the final release UI;
- final listing title/short/full description;
- public privacy policy URL and binary-policy match;
- current data/privacy declarations;
- current content/app-access/other required declarations;
- target API acceptance;
- support contact;
- final release notes;
- final signed AAB upload/test result;
- staged rollout/publication state.

See `PLAY_STORE_RELEASE.md`.

## Stable promotion gate

Promote to stable `1.0.0` only when:

1. repository-side RC preparation is merged from an exact green head — **completed for PR #27**;
2. any stable-metadata/source changes receive fresh exact-head Android CI and API-35 verification;
3. release APK/AAB/R8 integrity/hash evidence exists — **completed for the unsigned RC verification artifacts; must be repeated for final signed stable artifacts**;
4. required device/accessibility/lifecycle QA is recorded;
5. measured release performance has no release-blocking defect;
6. production signing is configured outside Git and signed artifacts are verified against the intended certificate identities;
7. the exact signed artifact application ID/version metadata is verified;
8. repository/store release assets represent the actual release UI;
9. no release-blocking issue remains;
10. final versionCode is higher than every accepted distributed candidate code;
11. README, changelog, roadmap, release notes and `what_changed.md` match the final stable source commit;
12. final decision in `V1_RELEASE_CANDIDATE.md` is `SHIP`.

Until those remaining conditions are satisfied, SudokuNova remains a verified repository-side release candidate rather than a stable-production claim.
