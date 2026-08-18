# SudokuNova v1.0 Release Preparation

This document is the repository-side handoff from the verified v0.9 hardening milestone to the first v1.0 release candidate.

## Current candidate

- Branch: `release/v1.0-rc1-prep`
- Pull request: `#27`
- Application ID: `in.sanskar.sudokunova`
- Version code: `1000`
- Version name: `1.0.0-rc.1`
- Stable `v1.0.0` release: **not yet claimed**

The RC version exists to validate the complete release path without pretending that real device, accessibility-service, production-signing, repository-admin, or store-publication evidence has already been collected.

The older alternate PR #26 / `release/v1.0-readiness` / version code `990` was closed as superseded after its materially stronger unique ideas were absorbed into this line. PR #27 is the single authoritative v1.0 RC preparation path.

## Repository-side work completed for RC preparation

### Release metadata

The Android app identifies this line as `1.0.0-rc.1` / version code `1000`.

If version code `1000` is uploaded to a store track that reserves it, the final stable build must use a higher version code.

### Release artifact verification

`scripts/verify_release_outputs.py` validates:

- release APK exists and is non-empty;
- release APK is a valid ZIP-based Android artifact;
- APK contains `AndroidManifest.xml` and `classes.dex`;
- release AAB exists and is non-empty;
- AAB contains `BundleConfig.pb`, base manifest and base DEX;
- R8 mapping exists and is non-empty;
- APK `output-metadata.json` contains exactly one release element;
- version code/name match the expected RC values;
- SHA-256 and byte-size evidence is written for APK, AAB and R8 mapping.

For a protected signed-release validation run, `--require-signatures` additionally requires:

- APK verification through `apksigner`;
- AAB verification through `jarsigner`.

The verifier does not own or read signing passwords/keys. It validates already-produced artifacts.

The verifier has direct Python unit tests under `scripts/tests/`, including signature-verifier failure/success cases.

### CI release gates

Standard Android CI now additionally verifies:

1. release-artifact verifier unit tests;
2. partially configured release-signing environments fail closed;
3. release APK/AAB/mapping structural/version checks;
4. SHA-256 evidence generation;
5. checksum/evidence upload with the release build artifacts.

Existing security, translation, engine, app JVM, AndroidTest compilation, lint, debug build, R8 release build and AAB gates remain intact.

Normal PR CI intentionally does not require production signatures because it receives no production signing secrets.

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

See [Production Signing](PRODUCTION_SIGNING.md).

### Evidence records

Two complementary evidence documents are maintained:

- [v1.0 Release Evidence Ledger](V1_RELEASE_EVIDENCE.md) — concise exact-head/run/artifact/signature/manual/store status record;
- [v1.0 Release Candidate Evidence Worksheet](V1_RELEASE_CANDIDATE.md) — detailed real-target test matrix and ship/no-ship decision.

Automated CI cannot mark manual-production rows complete.

### Canonical stable release notes

[v1.0 Release Notes Source](V1_RELEASE_NOTES.md) contains the canonical truthful stable-release notes draft and pending exact evidence fields. It must not be published as final while the build remains RC or mandatory stable evidence is pending.

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

[GitHub Repository Settings](GITHUB_REPOSITORY_SETTINGS.md) documents the recommended `main` protection/ruleset, required CI checks, force-push/deletion restrictions, least-privilege Actions settings and security features.

At RC-prep start, the GitHub API reported `main` as unprotected. The connector used for this work does not expose a branch-protection write action, so this remains a real repository-admin setting to enable manually and later record as evidence.

## Automated RC merge gates

Before PR #27 can leave draft state, its exact final head must pass:

- repository security guard;
- release verifier unit tests;
- partial-signing fail-closed regression;
- English/Hindi parity;
- `:sudoku-engine:test`;
- `:app:testDebugUnitTest`;
- `:app:assembleDebugAndroidTest`;
- debug and release lint;
- debug APK;
- R8/resource-shrunk release APK;
- release AAB;
- release artifact structural/version/checksum verification;
- API-35 connected Compose/Room suite.

No earlier-head run counts as final evidence after the branch changes.

## Stable v1.0 gates that remain manual/production

The following must remain pending until real evidence exists:

- TalkBack traversal/focus order;
- representative 200% font/narrow/tablet/window QA;
- high-contrast/reduced-motion manual review;
- measured startup/frame/memory/ANR evidence;
- process-death/lifecycle manual scenarios;
- actual production/upload key configuration;
- signed APK certificate verification;
- signed AAB signature/distribution validation;
- signed production/production-equivalent install smoke test;
- final release-only R8 smoke on signed artifacts;
- final screenshots/listing/privacy declarations;
- GitHub `main` protection/ruleset settings if required for stable release;
- stable version-code decision after any RC store uploads;
- stable source tag/GitHub Release/store publication.

## Stable promotion rule

Do not simply rename RC1 to stable.

When manual/production evidence passes:

1. choose the final stable version code (strictly higher than every accepted store version code);
2. set `versionName = "1.0.0"`;
3. make only release-blocking fixes after the approved RC, with revalidation proportional to those fixes;
4. pass exact-head automated gates again;
5. produce signed stable APK/AAB;
6. run `scripts/verify_release_outputs.py --require-signatures` against the exact signed artifacts and final version metadata;
7. complete the manual evidence worksheet and concise evidence ledger;
8. update `V1_RELEASE_NOTES.md`, `CHANGELOG.md`, `ROADMAP.md`, `README.md` and `what_changed.md` with actual evidence;
9. tag the exact approved source commit as `v1.0.0`;
10. create the GitHub Release from that tag;
11. publish through the chosen Android distribution track only after the final ship decision.

## Evidence integrity

A credible v1.0 release is more important than making the roadmap appear finished. Unknown or unperformed checks stay `PENDING`; failed checks stay `FAIL` until fixed and repeated.
