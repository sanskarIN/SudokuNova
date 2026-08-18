# SudokuNova v1.0 Release Preparation

This document is the repository-side handoff from the verified v0.9 hardening milestone through the verified/merged first v1.0 release-candidate preparation and into the remaining stable-production validation.

## Current candidate status

- Preparation branch: `release/v1.0-rc1-prep`
- PR #27: **verified and merged**
- Final verified PR head: `7016e21f36c8ecb8a495c446ffd8b57e9f20a4ea`
- PR #27 merge commit: `2329881aff8dabaf8d040918e16b6113e3900245`
- Application ID: `in.sanskar.sudokunova`
- Candidate version code: `1000`
- Candidate version name: `1.0.0-rc.1`
- Android CI: run #635 / `32151771317` — GREEN
- API-35 connected instrumentation: run #188 / `32151771297` — GREEN
- Stable `v1.0.0` release: **not yet claimed**

The repository-side RC version validates the complete automated release path without pretending that real device, accessibility-service, production-signing, repository-admin, or store-publication evidence has already been collected.

The older alternate PR #26 / `release/v1.0-readiness` / version code `990` was closed as superseded after its materially stronger unique ideas were absorbed into PR #27. PR #27 was the single authoritative repository-side RC preparation path and is now merged.

If version code `1000` is uploaded to a store track that reserves it, the final stable build must use a higher version code.

## Repository-side work completed for RC preparation

### Release metadata

The merged Android candidate line identifies itself as `1.0.0-rc.1` / version code `1000`.

### Release artifact verification

`scripts/verify_release_outputs.py` validates:

- release APK exists and is non-empty;
- release APK is a valid ZIP-based Android artifact;
- APK contains `AndroidManifest.xml` and `classes.dex`;
- release AAB exists and is non-empty;
- AAB contains `BundleConfig.pb`, base manifest and base DEX;
- R8 mapping exists and is non-empty;
- APK `output-metadata.json` contains exactly one release element;
- version code/name match the expected candidate values;
- SHA-256 and byte-size evidence is written for APK, AAB and R8 mapping.

For a protected signed-release validation run, `--require-signatures` additionally requires:

- APK verification through `apksigner`;
- AAB verification through `jarsigner`.

The verifier does not own or read signing passwords/keys. It validates already-produced artifacts. The verifier has direct Python unit tests under `scripts/tests/`, including signature-verifier failure/success cases.

### Exact automated RC evidence — VERIFIED

Final exact PR #27 head: `7016e21f36c8ecb8a495c446ffd8b57e9f20a4ea`.

Android CI run #635 / `32151771317` passed:

1. repository security guard;
2. release-artifact verifier unit tests;
3. partially configured release-signing fail-closed regression;
4. English/Hindi translation parity;
5. `:sudoku-engine:test`;
6. `:app:testDebugUnitTest`;
7. `:app:assembleDebugAndroidTest`;
8. debug/release lint;
9. debug APK;
10. R8/resource-shrunk release APK;
11. release AAB;
12. release APK/AAB/R8 structural/version checks;
13. exact `1000 / 1.0.0-rc.1` metadata validation;
14. SHA-256 evidence generation;
15. checksum/evidence artifact upload.

API-35 connected instrumentation run #188 / `32151771297` also passed on the same exact final head.

No branch commit was added after the successful workflow pair before merge.

### Exact unsigned RC artifact evidence

- CI artifact name: `unsigned-release-builds`
- artifact ID: `9330415157`
- artifact size: `12,793,995` bytes
- GitHub artifact digest: `sha256:0f1fa33127f6ae46d633c039bf0aad2e308b11e94bbd5567dd0fbc4805b4263c`
- unsigned APK SHA-256: `422a151ab3bb47268a69548ce5669b7a141169cc822400d3ef1376fa476b53c7` — `1,849,599` bytes
- release AAB SHA-256: `1bbbb2f227fc432efa74fa6efe16f2f17ae3aa5bf4a59ffac9e2e71de9a7cdfd` — `4,349,513` bytes
- R8 mapping SHA-256: `0f8b128679e858e0d835f0e3d23bfb629448efc4215703dd6c0f69b155e3f3ac` — `39,198,732` bytes

These are unsigned repository-CI verification artifacts, not production-signed release artifacts.

### Secret-backed production signing

`app/build.gradle.kts` supports optional production signing through:

- `SUDOKUNOVA_KEYSTORE_PATH`;
- `SUDOKUNOVA_KEYSTORE_PASSWORD`;
- `SUDOKUNOVA_KEY_ALIAS`;
- `SUDOKUNOVA_KEY_PASSWORD`.

Behavior is fail closed:

- none provided → unsigned CI-safe release build;
- all four provided → release signing config is enabled;
- one to three provided → Gradle configuration fails.

The partial-configuration failure path is verified by Android CI. Actual production signing is still pending because no production key/secrets were supplied to ordinary repository CI.

See [Production Signing](PRODUCTION_SIGNING.md).

### Evidence records

Two complementary evidence documents are maintained:

- [v1.0 Release Evidence Ledger](V1_RELEASE_EVIDENCE.md) — exact-head/run/artifact/signature/manual/store status record, now populated with repository-side RC evidence;
- [v1.0 Release Candidate Evidence Worksheet](V1_RELEASE_CANDIDATE.md) — detailed real-target test matrix and ship/no-ship decision, with automated RC fields prefilled and manual fields still pending.

Automated CI cannot mark manual-production rows complete.

### Canonical stable release notes

[v1.0 Release Notes Source](V1_RELEASE_NOTES.md) contains the canonical truthful stable-release notes draft and pending exact stable evidence fields. It must not be published as final while mandatory stable evidence is pending.

### Store/publication preparation

[Play Store Release Preparation](PLAY_STORE_RELEASE.md) contains:

- project/store identity;
- suggested title/short/full description;
- screenshot/asset checklist;
- current project privacy/data facts to map to live store forms;
- stable privacy URL requirements;
- artifact/signing checklist;
- draft v1.0 release-note copy;
- rollout/fix-forward discipline.

It deliberately avoids freezing current Play Console policy wording because store requirements can change independently of this repository.

### GitHub repository health

`.github/release.yml` configures generated release-note categories.

[GitHub Repository Settings](GITHUB_REPOSITORY_SETTINGS.md) documents recommended `main` protection/ruleset, required CI checks, force-push/deletion restrictions, least-privilege Actions settings and security features.

At RC-prep start, the GitHub API reported `main` as unprotected. The connector used for this work does not expose a branch-protection write action, so this remains a real repository-admin setting to enable manually and later record as evidence.

## Repository-side RC merge gates — COMPLETED

The exact final PR #27 head passed:

- [x] repository security guard;
- [x] release verifier unit tests;
- [x] partial-signing fail-closed regression;
- [x] English/Hindi parity;
- [x] `:sudoku-engine:test`;
- [x] `:app:testDebugUnitTest`;
- [x] `:app:assembleDebugAndroidTest`;
- [x] debug and release lint;
- [x] debug APK;
- [x] R8/resource-shrunk release APK;
- [x] release AAB;
- [x] release artifact structural/version/checksum verification;
- [x] API-35 connected Compose/Room suite;
- [x] no repository-blocking final diff defect;
- [x] merge using expected verified head.

PR #27 merge commit: `2329881aff8dabaf8d040918e16b6113e3900245`.

## Stable v1.0 gates that remain manual/admin/production

The following must remain pending until real evidence exists:

- GitHub `main` protection/ruleset administration and required-check settings;
- TalkBack traversal/focus order;
- representative 200% font/narrow/large/tablet/window/orientation QA;
- high-contrast/reduced-motion manual review;
- measured startup/frame/memory/ANR evidence;
- process-death/lifecycle manual scenarios;
- actual production/upload key configuration;
- signed APK certificate verification and expected certificate identity;
- signed AAB signature/distribution validation;
- signed production/production-equivalent install smoke test;
- final release-only R8 smoke on signed artifacts;
- final screenshots/listing/privacy/data/content/target-API review;
- stable version-code decision after any RC store uploads;
- stable metadata promotion to `versionName = "1.0.0"`;
- fresh exact-head stable Android CI/API-35 verification after final stable changes;
- final signed artifact hash/signature evidence;
- final `SHIP` decision;
- stable source tag/GitHub Release/store publication.

## Stable promotion rule

Do not simply rename RC1 to stable.

When manual/admin/production evidence passes:

1. choose the final stable version code (strictly higher than every accepted store version code);
2. set `versionName = "1.0.0"`;
3. make only release-blocking fixes after the approved RC, with revalidation proportional to those fixes;
4. pass exact-head automated gates again;
5. produce signed stable APK/AAB;
6. run `scripts/verify_release_outputs.py --require-signatures` against the exact signed artifacts and final version metadata;
7. compare the signed APK certificate digest/fingerprint against the intended production/upload certificate;
8. complete the manual evidence worksheet and concise evidence ledger;
9. update `V1_RELEASE_NOTES.md`, `CHANGELOG.md`, `ROADMAP.md`, `README.md` and `what_changed.md` with actual stable evidence;
10. record a real `SHIP` decision;
11. tag the exact approved source commit as `v1.0.0`;
12. create the GitHub Release from that tag;
13. publish through the chosen Android distribution track only after the final ship decision.

## Evidence integrity

A credible v1.0 release is more important than making the roadmap appear finished. Unknown or unperformed checks stay `PENDING`; failed checks stay `FAIL` until fixed and repeated.
