# Releasing SudokuNova

SudokuNova is currently pre-1.0. This document defines the controlled release process. It does not imply that a production package has already been signed, published, Play-listed, or manually device-verified.

## Release Principles

A release is acceptable only when:

- Sudoku correctness is preserved;
- user data/migrations are safe;
- privacy/security documentation matches the binary;
- required automated gates pass on the exact release commit;
- release APK/AAB/R8 processing succeeds;
- required manual QA is performed and recorded;
- signing material stays outside version control;
- release notes describe actual shipped behavior.

Do not lower these requirements to meet an arbitrary date.

## Versioning

Use Semantic Versioning where practical:

- `0.x.y` — pre-1.0 development;
- `1.0.0` — first stable Classic Sudoku release;
- `1.x.0` — backward-compatible feature release;
- `1.x.y` — bug/security/maintenance fix;
- `2.0.0` — intentional incompatible product/data/API change when justified.

Android `versionCode` must increase monotonically for distributed builds.

The current v0.9 development line uses `versionCode 900` / `versionName 0.9.0` on its branch. Verify build configuration immediately before release rather than relying on this sentence indefinitely.

## 1. Freeze Scope

Before creating a release candidate:

- stop adding unrelated features;
- identify the intended release commit/branch;
- ensure root `ROADMAP.md` matches included/deferred scope;
- resolve release-blocking issues;
- review open PRs/issues for known blockers;
- avoid dependency/toolchain churn unless required for release correctness/security.

## 2. Documentation Audit

Review at minimum:

- `README.md`;
- `docs/README.md`;
- `FEATURES.md`;
- `USER_GUIDE.md`;
- `BUILDING.md`;
- `TESTING.md`;
- `CI_CD.md`;
- `ACCESSIBILITY.md`;
- `LOCALIZATION.md`;
- `DATA_STORAGE.md`;
- `DATA_FORMATS.md`;
- `PRIVACY.md`;
- root `SECURITY.md`;
- `THIRD_PARTY_NOTICES.md`;
- `CHANGELOG.md`;
- `ROADMAP.md`;
- `RELEASE_CHECKLIST.md`;
- `RELEASE_QA.md`;
- `what_changed.md`.

Remove stale “planned” statements for implemented work and stale “implemented” statements for removed work.

## 3. Local/CI Verification

Recommended broad local verification:

```bash
python scripts/verify_no_secrets.py
python scripts/verify_translations.py
./gradlew :sudoku-engine:test \
  :app:testDebugUnitTest \
  :app:assembleDebugAndroidTest \
  :app:lintDebug \
  :app:lintRelease \
  :app:assembleDebug \
  :app:assembleRelease \
  :app:bundleRelease \
  --stacktrace
```

Windows can use `gradlew.bat` with the same Gradle tasks.

The pull request must then pass the repository's GitHub Actions gates on the exact final head:

- Android CI;
- Android Instrumentation (API 35 connected suite).

See `CI_CD.md`.

## 4. Release Build Outputs

### Debug APK

```bash
./gradlew :app:assembleDebug
```

Used for development/testing only.

### Release APK

```bash
./gradlew :app:assembleRelease
```

The release build enables minification and resource shrinking. It should produce R8 mapping output.

### Release AAB

```bash
./gradlew :app:bundleRelease
```

AAB is the primary store-oriented Android bundle format.

CI release outputs are verification artifacts unless production signing/provenance is explicitly configured and reviewed.

## 5. Production Signing

Never commit production signing material.

Do not commit:

- `.jks`/`.keystore` production files;
- private keys;
- keystore passwords;
- key passwords;
- service-account credentials;
- signing tokens.

Production signing should come from a controlled local/CI secret environment.

Requirements:

- least-privilege access;
- secrets masked from logs;
- no secrets in Gradle source/version control;
- keystore backup/recovery handled securely outside the public repository;
- final signed artifact verified after signing.

The repository's secret guard is defense-in-depth, not a signing system.

## 6. R8 / Resource-Shrinking Validation

Release smoke testing must use the release build because R8/resource shrinking can expose defects not present in debug.

Verify at least:

- app launch;
- navigation;
- new game generation;
- active game;
- hints/Learn;
- Room screens;
- settings/DataStore;
- puzzle import;
- backup export/restore;
- resource-backed localization;
- About/version presentation.

If a release-only defect occurs, add the narrowest correct keep/consumer rule or code fix. Do not globally disable shrinking as a first response.

## 7. Manual Release QA

Execute `RELEASE_QA.md` / `QA_MATRIX.md` on actual targets available to the release process.

Important manual categories:

- install/upgrade/startup;
- new game/completion/resume;
- Daily/Weekly Challenge;
- Custom Puzzle;
- History/Saved Puzzles;
- Learn/practice;
- Settings/statistics;
- backup/transfer;
- orientation/window sizing;
- high contrast;
- reduced motion;
- large font scaling;
- TalkBack;
- hardware keyboard;
- performance/ANR smoke;
- release APK/AAB behavior.

Do not mark a device row as passed because CI passed on an emulator.

## 8. Data/Privacy/Security Review

Before release verify:

- Android manifest permissions/components;
- Android backup/data extraction rules;
- Privacy policy matches actual data handling;
- no analytics/ads/cloud behavior has appeared without policy updates;
- `SNP1`/`SNB1` parsing bounds remain intact;
- unique-solution import/custom checks remain intact;
- Room migrations are explicit and tested;
- dependencies/licenses/notices are current;
- no secrets/signing files are committed.

## 9. Store Metadata Preparation

Before a Play Store submission, verify the **current** Play requirements at release time rather than relying on old documentation.

Prepare and validate:

- app name;
- short/full description;
- icon/feature graphic;
- real screenshots from the released UI;
- privacy policy;
- Data Safety answers matching the binary;
- content rating;
- target API compliance;
- signed AAB;
- release notes;
- contact/support details.

Do not use mock screenshots showing unimplemented features.

## 10. Final Exact-Head Evidence

Immediately before merge/tag/release record:

- exact commit SHA;
- versionCode/versionName;
- Android CI run ID/status;
- API-35 instrumentation run ID/status;
- release build outputs verified;
- manual QA evidence actually completed;
- known limitations/blockers (should be none classified release-blocking).

Record the evidence in `what_changed.md` and release notes as appropriate.

If the head changes after recording evidence, rerun the required gates.

## 11. Merge

For milestone PRs, merge only after the final intended PR head is verified.

Use the repository's chosen merge strategy and preserve enough history/evidence to trace the release implementation.

Do not close the milestone issue as completed before the intended merge succeeds if the issue process states closure follows merge.

## 12. Tag

After the exact release commit is final and verified:

```bash
git tag -a vX.Y.Z -m "SudokuNova vX.Y.Z"
git push origin vX.Y.Z
```

Do not tag an unverified intermediate commit.

If the tag is wrong, correct the release process deliberately; avoid silently moving published tags without clear reason/communication.

## 13. GitHub Release

A GitHub Release should include:

- tag/version;
- release date;
- concise notes derived from `CHANGELOG.md`;
- minimum supported Android version;
- major features/fixes;
- security/data migration notes if applicable;
- known limitations;
- source/license/privacy/security/support links;
- artifact checksums when binaries are attached.

If binary artifacts are attached, clearly state whether they are signed production artifacts or development/testing artifacts.

## 14. Play Store Submission

Submit the final signed AAB that was actually validated.

After upload:

- review automated store checks;
- verify generated APK splits/device availability;
- confirm listing/version text;
- use staged rollout when appropriate;
- monitor crash/ANR/user reports during rollout.

Do not claim store publication until the store actually accepts/publishes the release.

## 15. Post-Release Monitoring

After release:

- monitor GitHub issues/support reports;
- watch platform crash/ANR signals if available through the distribution environment;
- triage security reports privately;
- document confirmed release defects;
- prepare a higher-version-code fix release when needed.

The open-source base repository does not require adding an analytics SDK solely for monitoring.

## Rollback / Fix-Forward

Android update ecosystems generally require a higher version code for a replacement update.

If a bad release is discovered:

1. pause/stop rollout using distribution controls where available;
2. assess security/data-integrity impact;
3. identify root cause;
4. add regression coverage;
5. build a fixed release with a higher version code;
6. repeat required verification;
7. publish fix notes/advisory where appropriate.

Do not try to replace a distributed release with a lower version code as an ordinary update.

## Security Release

For a vulnerability:

- use private reporting/advisory workflow;
- minimize public exploit detail before a fix is available;
- prepare the fix on an appropriate branch;
- run full correctness/regression verification;
- update affected supported versions/advisories;
- publish remediation/update guidance.

## Release Documentation Authority

Use these files together:

- `BUILDING.md` — how to build outputs;
- `CI_CD.md` — automated gates;
- `TESTING.md` — test strategy;
- `RELEASE_CHECKLIST.md` — checklist;
- `RELEASE_QA.md` — evidence-oriented QA matrix;
- `RELEASING.md` — process/order (this file);
- root `CHANGELOG.md` — shipped changes;
- root `what_changed.md` — detailed exact evidence/history.
