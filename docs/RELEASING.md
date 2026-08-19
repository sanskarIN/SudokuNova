# Releasing SudokuNova

SudokuNova has completed repository-side preparation for its first v1.0 release candidate. This document defines the controlled path from that verified RC state to a stable release. It does not imply that a production package has already been signed, Play-listed, manually device-verified, or published.

## Release Principles

A release is acceptable only when:

- Sudoku correctness is preserved;
- user data/migrations are safe;
- privacy/security documentation matches the binary;
- required automated gates pass on the exact release commit;
- release APK/AAB/R8 processing succeeds;
- release artifacts match the expected application ID and version metadata and checksum evidence exists;
- required manual QA is performed and recorded;
- signing material stays outside version control;
- signed artifact identity is verified against trusted certificate fingerprints;
- release notes describe actual shipped behavior.

Do not lower these requirements to meet an arbitrary date.

## Current v1.0 RC line

The repository-side RC1 preparation completed as:

- preparation branch: `release/v1.0-rc1-prep`;
- PR #27: **verified and merged**;
- final verified PR head: `7016e21f36c8ecb8a495c446ffd8b57e9f20a4ea`;
- PR #27 merge commit: `2329881aff8dabaf8d040918e16b6113e3900245`;
- `applicationId = "in.sanskar.sudokunova"`;
- `versionCode = 1000`;
- `versionName = "1.0.0-rc.1"`.

If version code `1000` is accepted by a distribution track during RC testing, the final stable build must use a strictly higher version code.

Stable `v1.0.0` must not be tagged simply because RC1 compiles or because repository-side RC preparation merged. Use [v1.0 RC Preparation](V1_RELEASE_PREP.md), [v1.0 RC Evidence](V1_RELEASE_CANDIDATE.md), and [v1.0 Release Evidence Ledger](V1_RELEASE_EVIDENCE.md) as promotion gates.

## Versioning

Use Semantic Versioning where practical:

- `0.x.y` — pre-1.0 development;
- `1.0.0-rc.N` — stable-release candidate;
- `1.0.0` — first stable Classic Sudoku release;
- `1.x.0` — backward-compatible feature release;
- `1.x.y` — bug/security/maintenance fix;
- `2.0.0` — intentional incompatible product/data/API change when justified.

Android `versionCode` must increase monotonically for distributed builds and must not reuse a code already accepted by a store/distribution track.

## 1. Freeze Scope

Before creating or refreshing a release candidate:

- stop adding unrelated features;
- identify the intended release branch/commit;
- ensure root `ROADMAP.md` matches included/deferred scope;
- resolve release-blocking issues;
- review open PRs/issues for known blockers;
- avoid dependency/toolchain churn unless required for release correctness/security;
- keep the Classic 9×9 product contract stable.

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
- `V1_RELEASE_PREP.md`;
- `V1_RELEASE_CANDIDATE.md`;
- `V1_RELEASE_EVIDENCE.md`;
- `PRODUCTION_SIGNING.md`;
- `PRODUCTION_RELEASE_VALIDATION.md`;
- `PLAY_STORE_RELEASE.md`;
- `GITHUB_REPOSITORY_SETTINGS.md`;
- `what_changed.md`.

Remove stale “planned” statements for implemented work and stale “implemented” statements for removed work.

## 3. Repository Security and Release-Verifier Tests

Run:

```bash
python scripts/verify_no_secrets.py
python -m unittest scripts.tests.test_verify_release_outputs
python scripts/verify_translations.py
```

The normal CI also verifies that a partial release-signing environment fails closed.

Release-verifier regression tests cover archive structure, version metadata, production `applicationId`, checksum evidence, signature tool failure modes, signer SHA-256 parsing, expected-certificate mismatch rejection, and normalized signature evidence.

## 4. Local/CI Verification

Recommended broad local non-connected verification:

```bash
python scripts/verify_no_secrets.py
python -m unittest scripts.tests.test_verify_release_outputs
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

The pull request must then pass on the exact final head:

- Android CI;
- Android Instrumentation (API-35 connected suite).

No earlier-head run counts after the candidate changes.

## 5. Verify Release Outputs

For the unsigned RC verification path, after release APK/AAB/mapping are built:

```bash
python scripts/verify_release_outputs.py \
  --apk app/build/outputs/apk/release/app-release-unsigned.apk \
  --aab app/build/outputs/bundle/release/app-release.aab \
  --mapping app/build/outputs/mapping/release/mapping.txt \
  --metadata app/build/outputs/apk/release/output-metadata.json \
  --expected-version-code 1000 \
  --expected-version-name 1.0.0-rc.1 \
  --expected-application-id in.sanskar.sudokunova \
  --output app/build/outputs/release-evidence/sha256.txt
```

The verifier checks archive structure, exact APK output version metadata, exact production application ID, a non-empty R8 mapping, and SHA-256/size evidence.

This is build/artifact evidence, not production-signing evidence.

## 6. Production Signing

Follow [Production Signing](PRODUCTION_SIGNING.md).

The release build supports four secret-backed environment variables:

- `SUDOKUNOVA_KEYSTORE_PATH`;
- `SUDOKUNOVA_KEYSTORE_PASSWORD`;
- `SUDOKUNOVA_KEY_ALIAS`;
- `SUDOKUNOVA_KEY_PASSWORD`.

Rules:

- none supplied → unsigned CI-safe release verification;
- all supplied → signing enabled;
- partial configuration → Gradle fails closed.

Never commit production signing material.

Do not commit:

- `.jks`/`.keystore` production files;
- private keys;
- keystore passwords;
- key passwords;
- service-account credentials;
- signing tokens.

Production signing must come from a controlled local/CI secret environment with least privilege, masked logs and secure key recovery/backup.

### Protected GitHub validation path

The repository includes `.github/workflows/release-validation.yml`, a manually dispatched **Production Release Validation** workflow. Configure and restrict a GitHub Environment named `production-release` according to [Production Release Validation Workflow](PRODUCTION_RELEASE_VALIDATION.md).

That workflow is deliberately separate from ordinary PR CI. It requires protected signing material and trusted expected signer-certificate fingerprints, builds signed R8 APK/AAB outputs, verifies package/version/signature/certificate identity, records non-secret evidence, and removes the temporary keystore.

The existence of the workflow is not evidence that production signing has been configured or executed. Only a real successful protected run on the exact intended release ref can satisfy the corresponding release-evidence rows.

## 7. Verify Signed Artifact Identity

After producing the actual signed APK, verify it with Android SDK Build Tools:

```bash
apksigner verify --verbose --print-certs app-release.apk
```

For the AAB, verify JAR-signature integrity and inspect the signer certificate using JDK tooling. The repository verifier can perform these checks and compare both artifacts against trusted expected SHA-256 fingerprints:

```bash
python scripts/verify_release_outputs.py \
  --apk path/to/signed-release.apk \
  --aab path/to/signed-release.aab \
  --mapping path/to/mapping.txt \
  --metadata path/to/output-metadata.json \
  --expected-version-code <final-version-code> \
  --expected-version-name 1.0.0 \
  --expected-application-id in.sanskar.sudokunova \
  --output path/to/sha256.txt \
  --require-signatures \
  --expected-apk-cert-sha256 <trusted-apk-cert-sha256> \
  --expected-aab-cert-sha256 <trusted-aab-cert-sha256> \
  --signature-output path/to/signatures.txt
```

Do not obtain the “expected” certificate value from the same artifact being validated. The comparison value must come from a trusted release/key/platform record.

If Play App Signing is used, distinguish the local upload-key certificate used for the submitted AAB from the Google Play app-signing certificate used for APKs delivered to users. Validate the correct identity at each stage.

A successful Gradle build alone is not proof that the intended key was used.

## 8. R8 / Resource-Shrinking Validation

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

## 9. Manual Release QA

Use [v1.0 Release Candidate Evidence](V1_RELEASE_CANDIDATE.md) as the authoritative worksheet. `RELEASE_QA.md` and `QA_MATRIX.md` remain useful supporting matrices.

Manual categories include:

- install/upgrade/startup;
- new game/completion/resume;
- Daily/Weekly Challenge;
- Custom Puzzle;
- History/Saved Puzzles;
- Learn/practice;
- Settings/statistics;
- backup/transfer;
- orientation/window sizing;
- 200% font scaling;
- high contrast;
- reduced motion;
- TalkBack/focus order;
- hardware keyboard;
- process death/lifecycle;
- measured performance/ANR/memory;
- signed release APK/AAB behavior.

Do not mark a device row as passed because CI passed on an emulator.

## 10. Data/Privacy/Security Review

Before release verify:

- Android manifest permissions/components;
- Android backup/data extraction rules;
- Privacy policy matches actual data handling;
- no analytics/ads/cloud behavior has appeared without policy updates;
- `SNP1`/`SNB1` parsing bounds remain intact;
- unique-solution import/custom checks remain intact;
- Room migrations are explicit and tested;
- dependencies/licenses/notices are current;
- no secrets/signing files are committed;
- current store privacy/data declarations match the exact stable binary.

## 11. GitHub Repository Settings

Follow [GitHub Repository Settings](GITHUB_REPOSITORY_SETTINGS.md).

At v1.0 RC-prep start, GitHub reported `main` as unprotected. Before stable release, enable an appropriate branch-protection/ruleset configuration where repository administration permits it.

Recommended required checks:

- `Android CI` / `verify`;
- `Android Instrumentation` / `connected-tests`.

Also restrict the `production-release` GitHub Environment to trusted maintainers and intended release refs/tags, using required reviewers where supported.

Do not claim repository/environment protection until it has actually been enabled in GitHub settings.

## 12. Store Metadata Preparation

Use [Play Store Release Preparation](PLAY_STORE_RELEASE.md).

Before a store submission, verify the **current** distribution-platform requirements at release time rather than relying on old documentation.

Prepare and validate:

- app name;
- short/full description;
- icon/feature graphic;
- real screenshots from the release build;
- privacy policy URL;
- data/privacy declarations matching the binary;
- current content/app-access/other required declarations;
- target API compliance;
- signed AAB;
- release notes;
- contact/support details.

Do not use mock screenshots showing unimplemented features.

## 13. Final Exact-Head Evidence

Immediately before merge/tag/release record:

- exact commit SHA;
- application ID;
- versionCode/versionName;
- Android CI run ID/status;
- API-35 instrumentation run ID/status;
- protected signed-release validation run ID/status where used;
- APK/AAB/mapping SHA-256 evidence;
- release build outputs verified;
- expected APK/AAB certificate fingerprints verified against trusted records;
- manual QA evidence actually completed;
- known limitations/blockers.

Record exact repository history/evidence in `what_changed.md` and release notes as appropriate.

If the head changes after recording evidence, rerun the required gates and determine which manual checks must be repeated.

## 14. Merge Verified Release-Hardening Changes

PR #27 already completed and merged the repository-side RC1 preparation from an exact green head.

Any later release-hardening or stable-metadata pull request must independently pass the exact-final-head automated gates before merge. Do not reuse PR #27’s green status as evidence for newer source commits.

Merging repository hardening does **not** automatically mean stable v1.0 is approved. Manual, signing, administrative and store evidence can remain open in issue #5 until actually completed.

## 15. Stable Promotion

Once the RC evidence worksheet reaches a real ship decision:

1. choose a stable version code strictly higher than every accepted distributed version code;
2. set `versionName = "1.0.0"`;
3. keep `applicationId = "in.sanskar.sudokunova"`;
4. run all exact-head automated gates again;
5. build and verify signed stable artifacts against trusted certificate identities;
6. repeat manual checks affected by any post-RC changes;
7. update changelog/roadmap/README/what_changed with actual stable evidence;
8. confirm no release blocker remains.

Do not simply rename an unvalidated RC build to stable.

## 16. Tag

After the exact stable release commit is final and verified:

```bash
git tag -a v1.0.0 -m "SudokuNova v1.0.0"
git push origin v1.0.0
```

Do not tag an unverified intermediate commit.

If a published tag is wrong, correct the release process deliberately; avoid silently moving published tags.

## 17. GitHub Release

`.github/release.yml` provides generated release-note categories, but generated notes are only a starting point.

A GitHub Release should include:

- exact tag/version;
- release date;
- concise notes derived from `CHANGELOG.md`;
- minimum supported Android version;
- major features/fixes;
- security/data migration notes if applicable;
- known limitations;
- source/license/privacy/security/support links;
- artifact checksums when binaries are attached.

If binary artifacts are attached, clearly state whether they are signed production artifacts or development/testing artifacts.

Never attach a keystore, secret, `local.properties`, private test data or internal-only log.

## 18. Store Submission

Submit the final signed AAB that was actually validated.

After upload:

- review automated store checks;
- verify generated APK splits/device availability where the platform exposes them;
- confirm listing/version text;
- use the safest appropriate testing/staged rollout path;
- monitor crash/ANR/user reports during rollout.

Do not claim store publication until the store actually accepts/publishes the release.

## 19. Post-Release Monitoring

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

- `V1_RELEASE_PREP.md` — verified RC1 repository handoff;
- `BUILDING.md` — build outputs and artifact verifier;
- `PRODUCTION_SIGNING.md` — secret-backed signing and certificate checks;
- `PRODUCTION_RELEASE_VALIDATION.md` — protected signed-release workflow setup and evidence;
- `V1_RELEASE_CANDIDATE.md` — manual/production evidence worksheet;
- `V1_RELEASE_EVIDENCE.md` — concise exact-evidence ledger;
- `PLAY_STORE_RELEASE.md` — listing/privacy/store preparation;
- `GITHUB_REPOSITORY_SETTINGS.md` — branch/repository/environment protection checklist;
- `CI_CD.md` — automated gates;
- `TESTING.md` — test strategy;
- `RELEASE_CHECKLIST.md` — general checklist;
- `RELEASE_QA.md` — supporting evidence matrix;
- `RELEASING.md` — process/order (this file);
- root `CHANGELOG.md` — shipped/unreleased changes;
- root `what_changed.md` — detailed exact evidence/history.
