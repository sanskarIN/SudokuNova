# Releasing SudokuNova

This document defines the controlled path from a verified source line to a production SudokuNova release. The current source target is **2.0.12** with `versionCode 2012` / `versionName 2.0.12`.

Source metadata and release tooling do **not** imply that a production package has already been signed, device-verified, store-accepted, tagged, or published. Those claims require exact evidence.

The current release authority is [SudokuNova 2.0.12 Release Line](V2_0_12_RELEASE.md). Historical v1 RC documents remain preserved as release-engineering evidence.

## Release Principles

A release is acceptable only when:

- Sudoku correctness is preserved;
- user data and migrations are safe;
- privacy/security documentation matches the binary;
- required automated gates pass on the exact release commit;
- release APK/AAB/R8 processing succeeds;
- release artifacts match the expected application ID, version, SDK, and non-debuggable identity;
- checksum/identity evidence exists;
- required manual QA is performed and recorded;
- signing material stays outside version control;
- signed artifact identity is verified against trusted certificate fingerprints;
- release notes describe actual shipped behavior;
- repository/store administrative claims are supported by real evidence;
- tagging/publication occurs only after a deliberate `SHIP` decision.

Do not lower these requirements to meet an arbitrary date.

## Current 2.0.12 Source Line

Current source contract:

- final source PR: `#30`;
- `applicationId = "in.sanskar.sudokunova"`;
- `versionCode = 2012`;
- `versionName = "2.0.12"`;
- `minSdk = 26`;
- `targetSdk = 37`;
- `compileSdk = 37`.

The ordinary CI and protected production-validation workflow use matching expected values. `scripts/verify_release_contract.py` fails when Gradle source, ordinary CI, and protected workflow expectations drift.

The previous `versionCode 1000` / `versionName 1.0.0-rc.1` line is historical v1 RC evidence. Its successful workflow runs do not count as final verification for 2.0.12.

## Historical v1 Foundation

The current release process inherits verified source/tooling from earlier v1 work:

- PR #27 final head `7016e21f36c8ecb8a495c446ffd8b57e9f20a4ea` passed Android CI `32151771317` and API-35 instrumentation `32151771297`, then merged as `2329881aff8dabaf8d040918e16b6113e3900245`;
- PR #28 final head `c3e0e3fc217062e374a434cfea46235fd6595f83` passed Android CI `32211246803` and API-35 instrumentation `32211246802`, then merged as `27640cb9089ddae4a9242bb84a8927c3761201f4`.

Those historical runs prove only those exact source heads. The current 2.0.12 line requires fresh exact-head evidence.

## Versioning

Use Semantic Versioning where practical.

Android `versionCode` must increase monotonically for distributed builds and must not reuse a code already accepted by a store/distribution track.

For the current source line:

```text
versionCode = 2012
versionName = 2.0.12
```

Any later distributed build must use a version code higher than every code already accepted by the intended distribution platform.

## 1. Freeze Scope

Before final release verification:

- stop adding unrelated features;
- identify the intended release branch/commit;
- ensure root `ROADMAP.md` matches included/deferred scope;
- resolve release-blocking issues;
- review open PRs/issues for known blockers;
- avoid dependency/toolchain churn unless required for correctness/security;
- keep the Classic 9×9 product/data contract stable unless an intentional migration is fully designed and tested;
- confirm all new tracked paths have documentation ownership;
- confirm all detailed documentation pages are discoverable from `docs/README.md`.

## 2. Documentation Audit

Review at minimum:

- root `README.md`;
- `docs/README.md`;
- `V2_0_12_RELEASE.md`;
- `FEATURES.md`;
- `USER_GUIDE.md`;
- `BUILDING.md`;
- `TESTING.md`;
- `CI_CD.md`;
- `REPOSITORY_GUARDS.md`;
- `REPOSITORY_FILE_REFERENCE.md`;
- `EXACT_HEAD_VERIFICATION.md`;
- `ACCESSIBILITY.md`;
- `LOCALIZATION.md`;
- `DATA_STORAGE.md`;
- `DATA_FORMATS.md`;
- `PRIVACY.md`;
- root `SECURITY.md`;
- `THIRD_PARTY_NOTICES.md`;
- root `CHANGELOG.md`;
- root `ROADMAP.md`;
- `RELEASE_CHECKLIST.md`;
- `RELEASE_QA.md`;
- `PRODUCTION_SIGNING.md`;
- `PRODUCTION_RELEASE_VALIDATION.md`;
- `PERFORMANCE_BENCHMARKING.md`;
- `PLAY_STORE_RELEASE.md`;
- `GITHUB_REPOSITORY_SETTINGS.md`;
- root `what_changed.md`.

Historical v1 files (`V1_RELEASE_PREP.md`, `V1_RELEASE_CANDIDATE.md`, `V1_RELEASE_EVIDENCE.md`, `V1_RELEASE_NOTES.md`, `POST_RC_VALIDATION_EVIDENCE.md`) should remain truthful historical records rather than being rewritten as 2.0.12 work.

Remove stale “planned” statements for implemented work and stale “implemented” statements for removed work. Do not convert a checklist into a claim that a real check happened.

## 3. Repository Consistency and Security Gates

Run:

```bash
python scripts/verify_no_secrets.py
python -m unittest scripts.tests.test_verify_documentation_links
python -m unittest scripts.tests.test_verify_documentation_coverage
python -m unittest scripts.tests.test_verify_release_contract
python -m unittest scripts.tests.test_verify_release_outputs
python -m unittest scripts.tests.test_verify_release_cli_validation
python scripts/verify_documentation_links.py
python scripts/verify_documentation_coverage.py
python scripts/verify_release_contract.py
python scripts/verify_translations.py
```

The documentation-coverage verifier reads the authoritative tracked-file set with `git ls-files -z`. A new unowned path, missing canonical guide, or unindexed detailed documentation page fails closed.

The normal CI also verifies that a partial release-signing environment fails closed.

## 4. Local/CI Verification

Recommended broad local non-connected verification:

```bash
python scripts/verify_no_secrets.py
python -m unittest scripts.tests.test_verify_documentation_links
python -m unittest scripts.tests.test_verify_documentation_coverage
python -m unittest scripts.tests.test_verify_release_contract
python -m unittest scripts.tests.test_verify_release_outputs
python -m unittest scripts.tests.test_verify_release_cli_validation
python scripts/verify_documentation_links.py
python scripts/verify_documentation_coverage.py
python scripts/verify_release_contract.py
python scripts/verify_translations.py
./gradlew :sudoku-engine:test \
  :app:testDebugUnitTest \
  :app:assembleDebugAndroidTest \
  :macrobenchmark:assembleBenchmark \
  :app:lintDebug \
  :app:lintRelease \
  :app:assembleDebug \
  :app:assembleRelease \
  :app:bundleRelease \
  --stacktrace
```

Windows can use `gradlew.bat` with the same Gradle tasks.

The pull request must then pass on the **exact final head**:

- Android CI;
- Android Instrumentation (API-35 connected suite).

No earlier-head run counts after the source/documentation head changes.

## 5. Verify 2.0.12 Release Outputs

For the ordinary unsigned verification path, after release APK/AAB/mapping are built:

```bash
python scripts/verify_release_outputs.py \
  --apk app/build/outputs/apk/release/app-release-unsigned.apk \
  --aab app/build/outputs/bundle/release/app-release.aab \
  --mapping app/build/outputs/mapping/release/mapping.txt \
  --metadata app/build/outputs/apk/release/output-metadata.json \
  --expected-version-code 2012 \
  --expected-version-name 2.0.12 \
  --expected-application-id in.sanskar.sudokunova \
  --require-apk-manifest \
  --expected-min-sdk 26 \
  --expected-target-sdk 37 \
  --output app/build/outputs/release-evidence/sha256.txt \
  --apk-identity-output app/build/outputs/release-evidence/apk-identity.txt
```

The verifier checks:

- non-empty APK/AAB/R8 outputs;
- ZIP/archive structure and required entries;
- exact APK output metadata;
- exact production application ID;
- exact `2012 / 2.0.12` version identity;
- embedded application/version/minSdk/targetSdk values from the APK;
- embedded `debuggable=false`;
- a non-empty R8 mapping;
- SHA-256/size evidence;
- deterministic APK identity evidence.

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

The current workflow defaults are:

```text
expected_version_code = 2012
expected_version_name = 2.0.12
```

The workflow is deliberately separate from ordinary PR CI. It requires protected signing material and trusted expected signer-certificate fingerprints, builds signed R8 APK/AAB outputs, verifies package/version/SDK/debuggable/signature/certificate identity, records non-secret evidence, and removes the temporary keystore.

The existence of the workflow is not evidence that production signing has been configured or executed. Only a real successful protected run on the exact intended 2.0.12 release ref can satisfy the corresponding release-evidence rows.

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
  --expected-version-code 2012 \
  --expected-version-name 2.0.12 \
  --expected-application-id in.sanskar.sudokunova \
  --require-apk-manifest \
  --expected-min-sdk 26 \
  --expected-target-sdk 37 \
  --apk-identity-output path/to/apk-identity.txt \
  --output path/to/sha256.txt \
  --require-signatures \
  --expected-apk-cert-sha256 <trusted-apk-cert-sha256> \
  --expected-aab-cert-sha256 <trusted-aab-cert-sha256> \
  --signature-output path/to/signatures.txt
```

Mandatory signed-release validation requires at least one verified APK v2-or-newer signature scheme.

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

Use [SudokuNova 2.0.12 Release Line](V2_0_12_RELEASE.md) as the current authoritative release worksheet. `RELEASE_QA.md` and `QA_MATRIX.md` remain useful supporting matrices.

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
- current store privacy/data declarations match the exact 2.0.12 binary.

## 11. GitHub Repository Settings

Follow [GitHub Repository Settings](GITHUB_REPOSITORY_SETTINGS.md).

Historical audits reported `main` as unprotected. Before production publication, configure/review an appropriate branch-protection/ruleset configuration where repository administration permits it.

Recommended required checks include:

- `Android CI` / `verify`;
- `Android Instrumentation` / `connected-tests`.

Also restrict the `production-release` GitHub Environment to trusted maintainers and intended release refs/tags, using required reviewers where supported.

Do not claim repository/environment protection until it has actually been enabled in GitHub settings.

## 12. Performance Evidence

Ordinary CI compiles the Macrobenchmark harness but does not provide representative physical-device timing evidence.

For the production evidence path, run on representative physical hardware:

```bash
./gradlew :macrobenchmark:connectedBenchmarkAndroidTest --stacktrace
```

Record:

- exact source commit;
- device model;
- OS/API level;
- cold-start measurements;
- warm-start measurements;
- cold-start frame measurements;
- relevant traces;
- memory observations;
- ANR/crash findings.

Do not present emulator timing or compilation success as representative production-device performance evidence.

## 13. Store Metadata Preparation

Use [Play Store Release Preparation](PLAY_STORE_RELEASE.md).

Before a store submission, verify the **current** distribution-platform requirements at release time rather than relying on old documentation.

Prepare and validate:

- app name;
- short/full description;
- icon/feature graphic;
- real screenshots from the 2.0.12 release build;
- privacy policy URL;
- data/privacy declarations matching the binary;
- current content/app-access/other required declarations;
- target API compliance;
- signed AAB;
- release notes;
- contact/support details.

Do not use mock screenshots showing unimplemented features.

## 14. Final Exact-Head Evidence

Immediately before merge/tag/release record:

- exact commit SHA;
- application ID;
- versionCode/versionName;
- minSdk/targetSdk;
- Android CI run ID/status;
- API-35 instrumentation run ID/status;
- protected signed-release validation run ID/status;
- APK/AAB/mapping SHA-256 evidence;
- APK embedded identity evidence;
- release build outputs verified;
- expected APK/AAB certificate fingerprints verified against trusted records;
- manual QA evidence actually completed;
- performance evidence actually completed;
- repository/store evidence actually completed;
- known limitations/blockers.

Record exact repository history/evidence in `what_changed.md`, issue #5, and release notes as appropriate.

If the head changes after recording evidence, rerun the required gates and determine which manual checks must be repeated.

## 15. Merge Verified 2.0.12 Source Changes

PR #30 is the current final source-controlled release/hardening line.

It must independently pass the exact-final-head automated gates before merge. Do not reuse PR #27, PR #28, PR #29, or an earlier PR #30 run as evidence for a newer 2.0.12 head.

Merging repository hardening does **not** automatically mean 2.0.12 is approved for publication. Manual, signing, administrative, performance and store evidence can remain open in issue #5 until actually completed.

## 16. Final Production Decision

Once all mandatory evidence exists:

1. confirm `versionCode = 2012` and `versionName = "2.0.12"` are the intended distributed identity;
2. confirm the exact release commit has fresh required automated evidence;
3. confirm signed artifacts match trusted certificate identities;
4. confirm manual/device/accessibility/performance/store evidence is complete;
5. update changelog/roadmap/README/what_changed with actual final evidence if needed;
6. confirm no release blocker remains;
7. record the explicit decision as `SHIP` or `NO-SHIP`, with owner/date.

Do not reinterpret a source-version change as a ship decision.

## 17. Tag

After the exact 2.0.12 release commit is final, verified, and the decision is `SHIP`:

```bash
git tag -a v2.0.12 -m "SudokuNova v2.0.12"
git push origin v2.0.12
```

Do not tag an unverified intermediate commit.

If a published tag is wrong, correct the release process deliberately; avoid silently moving published tags.

## 18. GitHub Release

`.github/release.yml` provides generated release-note categories, but generated notes are only a starting point.

A GitHub Release should include:

- exact `v2.0.12` tag/version;
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

## 19. Store Submission

Submit the final signed AAB that was actually validated.

After upload:

- review automated store checks;
- verify generated APK splits/device availability where the platform exposes them;
- confirm listing/version text;
- use the safest appropriate testing/staged rollout path;
- monitor crash/ANR/user reports during rollout.

Do not claim store publication until the store actually accepts/publishes the release.

## 20. Post-Release Monitoring

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

Use these current files together:

- `V2_0_12_RELEASE.md` — current 2.0.12 source/release/evidence authority;
- `BUILDING.md` — build outputs and artifact verifier;
- `PRODUCTION_SIGNING.md` — secret-backed signing and certificate checks;
- `PRODUCTION_RELEASE_VALIDATION.md` — protected signed-release workflow setup and evidence;
- `PERFORMANCE_BENCHMARKING.md` — representative physical-device performance evidence;
- `PLAY_STORE_RELEASE.md` — listing/privacy/store preparation;
- `GITHUB_REPOSITORY_SETTINGS.md` — branch/repository/environment protection checklist;
- `CI_CD.md` — automated gates;
- `REPOSITORY_GUARDS.md` — deterministic repository contracts;
- `REPOSITORY_FILE_REFERENCE.md` — complete tracked-file documentation ownership;
- `TESTING.md` — test strategy;
- `RELEASE_CHECKLIST.md` — general checklist;
- `RELEASE_QA.md` — supporting evidence matrix;
- `RELEASING.md` — process/order (this file);
- root `CHANGELOG.md` — shipped/unreleased changes;
- root `ROADMAP.md` — current milestone state;
- root `what_changed.md` — detailed exact evidence/history.

Historical v1 release documents remain available for prior decisions/evidence but are not the current version authority.
