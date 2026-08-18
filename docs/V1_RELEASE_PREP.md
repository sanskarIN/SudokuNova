# SudokuNova v1.0 Release Preparation

This document is the repository-side handoff from the verified v0.9 hardening milestone to the first v1.0 release candidate.

## Current candidate

- Branch: `release/v1.0-rc1-prep`
- Pull request: `#27`
- Application ID: `in.sanskar.sudokunova`
- Version code: `1000`
- Version name: `1.0.0-rc.1`
- Stable `v1.0.0` release: **not yet claimed**

The RC version exists to validate the complete release path without pretending that real device, accessibility-service, production-signing, or store-publication evidence has already been collected.

## Repository-side work completed for RC preparation

### Release metadata

The Android app now identifies this line as `1.0.0-rc.1` / version code `1000`.

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

The verifier has direct Python unit tests under `scripts/tests/`.

### CI release gates

Standard Android CI now additionally verifies:

1. release-artifact verifier unit tests;
2. partially configured release-signing environments fail closed;
3. release APK/AAB/mapping structural/version checks;
4. SHA-256 evidence generation;
5. checksum/evidence upload with the release build artifacts.

Existing security, translation, engine, app JVM, AndroidTest compilation, lint, debug build, R8 release build and AAB gates remain intact.

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

### Store/publication preparation

[Play Store Release Preparation](PLAY_STORE_RELEASE.md) contains:

- project/store identity;
- suggested title/short/full description;
- screenshot/asset checklist;
- current project privacy/data facts to map to the live store forms;
- stable privacy URL requirements;
- artifact/signing checklist;
- draft v1.0 release-note copy;
- rollout/fix-forward discipline.

It deliberately avoids freezing current Play Console policy wording because store requirements can change independently of this repository.

### Manual evidence worksheet

[v1.0 Release Candidate Evidence Worksheet](V1_RELEASE_CANDIDATE.md) is the mandatory manual-production checklist for:

- installation/upgrade;
- gameplay;
- challenges;
- custom puzzles;
- Learn/hints;
- History/Saved/statistics;
- sharing/import/export/backup;
- TalkBack;
- 200% font/adaptive layouts;
- high contrast/reduced motion;
- keyboard input;
- lifecycle/process death;
- measured performance/ANR/memory;
- production signing;
- store/repository assets;
- final ship/no-ship decision.

Automated CI cannot mark those manual rows complete.

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
- signed production/production-equivalent install smoke test;
- AAB distribution-platform validation;
- final screenshots/listing/privacy declarations;
- stable version-code decision after any RC store uploads;
- stable source tag/GitHub Release/store publication.

## Stable promotion rule

Do not simply rename RC1 to stable.

When manual/production evidence passes:

1. choose the final stable version code (strictly higher than every accepted store version code);
2. set `versionName = "1.0.0"`;
3. make only release-blocking fixes after the approved RC, with revalidation proportional to those fixes;
4. pass exact-head automated gates again;
5. produce and verify signed stable APK/AAB;
6. complete the manual evidence worksheet;
7. update `CHANGELOG.md`, `ROADMAP.md`, `README.md` and `what_changed.md` with actual evidence;
8. tag the exact approved source commit as `v1.0.0`;
9. create the GitHub Release from that tag;
10. publish through the chosen Android distribution track only after the final ship decision.

## Evidence integrity

A credible v1.0 release is more important than making the roadmap appear finished. Unknown or unperformed checks stay `PENDING`; failed checks stay `FAIL` until fixed and repeated.
