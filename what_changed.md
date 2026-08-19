# What Changed

## Current Development State — 2026-08-19

**Repository:** `https://github.com/sanskarIN/SudokuNova`  
**Base branch:** `main`  
**Base commit for this final continuation:** `4b42c009e6efc9a03806d5cf0122802c0cee7203`  
**Final consolidation branch:** `docs/complete-repository-coverage`  
**Final consolidation pull request:** `#30`  
**Original release-verifier hardening pull request:** `#29` — integrated into PR #30  
**Temporary integration/conflict-detection pull request:** `#31`  
**Stable release:** **not yet claimed**  
**Stable-release tracker:** issue `#5`  
**Android application ID:** `in.sanskar.sudokunova`  
**Android versionCode:** `1000`  
**Android versionName:** `1.0.0-rc.1`  
**minSdk:** `26`  
**targetSdk:** `37`  
**License:** MIT  
**Project commit email:** `sanskarin@outlook.in`

The cumulative pre-continuation development ledger is preserved unchanged at:

`docs/archive/what_changed_through_2026-08-19.md`

This root file is the active final repository-hardening ledger. The archive exists specifically so earlier milestone detail is not discarded while the active record stays focused on the final release-verification and complete-documentation work.

---

## Final Continuation Goal

Complete every remaining repository-side task that can be performed truthfully in source control without fabricating evidence that requires a real device, production signing secrets, GitHub repository administration, Play Console access, store assets, or an actual stable-publication decision.

The final pass therefore focused on two repository-level gaps:

1. release verification needed to prove important identity values from the built APK itself instead of trusting only Gradle/metadata representations; and
2. documentation needed an enforceable ownership/discoverability contract covering the complete Git-tracked repository rather than a one-time prose audit.

Those two workstreams originally existed as PR #29 and PR #30. The final pass also found that merging them independently would leave an integration hole because both modified CI/documentation and PR #29 added a historical Markdown file that PR #30's new documentation-index guard would require to be discoverable. The workstreams were therefore consolidated into PR #30 and validated as one final repository state.

---

## Release Verifier Hardening Integrated

### Embedded APK manifest identity inspection

`scripts/verify_release_outputs.py` supports `--require-apk-manifest` and independently inspects the built APK with Android SDK tooling.

The verifier validates the artifact-embedded:

- application ID;
- version code;
- version name;
- minimum SDK;
- target SDK;
- debuggable state.

The release artifact fails verification when an expected value cannot be read, when application/version/SDK identity drifts, or when the release APK is debuggable.

This check is deliberately independent of `output-metadata.json`. Ordinary CI therefore verifies both Android build metadata and the identity embedded in the APK.

### Deterministic APK identity evidence

The verifier can write `apk-identity.txt` containing:

- `application_id`;
- `version_code`;
- `version_name`;
- `min_sdk`;
- `target_sdk`;
- `debuggable`.

Ordinary CI retains this as short-lived verification evidence together with SHA-256/size evidence, APK/AAB outputs, and R8 mapping output.

### Signature-scheme hardening

Mandatory signed-release verification no longer treats a successful `apksigner` process plus certificate output as sufficient by itself.

The verifier parses verified APK signature schemes and requires at least one verified v2-or-newer scheme for the protected signed-release path. A v1-only APK is rejected by mandatory production-signature validation.

### SDK CLI validation

Release-verifier CLI validation rejects:

- non-positive expected minimum SDK;
- non-positive expected target SDK;
- target SDK lower than minimum SDK;
- SDK expectations without manifest verification;
- APK identity-evidence output without manifest verification.

These checks run before artifact access where applicable so invalid release contracts fail early.

---

## Release Contract Hardening Integrated

`scripts/verify_release_contract.py` now treats `app/build.gradle.kts` as the source-controlled release contract for:

- production application ID;
- version code;
- version name;
- minimum SDK;
- target SDK.

The guard compares those values with ordinary `.github/workflows/ci.yml` and protected `.github/workflows/release-validation.yml` expectations.

It rejects missing/duplicate values, non-positive numeric release values, target SDK below minimum SDK, unsafe version-name characters, a production application ID ending in `.debug`, and workflow drift from the Gradle source contract.

The current contract intentionally remains RC metadata:

- `in.sanskar.sudokunova`;
- `1000`;
- `1.0.0-rc.1`;
- `minSdk 26`;
- `targetSdk 37`.

No stable `1.0.0` metadata was fabricated or promoted during this continuation.

---

## Protected Production Validation Hardening Integrated

`.github/workflows/release-validation.yml` now combines the existing protected signing/certificate checks with embedded APK identity verification.

The protected workflow:

1. validates operator-supplied version inputs;
2. reconstructs signing material only in `$RUNNER_TEMP`;
3. builds signed R8 APK/AAB outputs;
4. binds application/version/minimum-SDK/target-SDK expectations to the source release contract;
5. independently verifies the APK reports `debuggable=false`;
6. verifies APK and AAB signatures;
7. requires a verified APK v2-or-newer signature scheme;
8. compares signer certificate SHA-256 identities with protected expected fingerprints;
9. records non-secret hash/identity/signature/workflow-context evidence;
10. cleans the temporary keystore;
11. keeps signed artifact upload explicit and short-lived rather than automatic.

A successful protected workflow can establish identity/signature/certificate facts for the exact generated artifacts. It still does not prove physical-device QA, accessibility QA, representative performance, repository-admin configuration, Play Console/listing correctness, or publication.

---

## Release Verification Regression Coverage Integrated

### `scripts/tests/test_verify_release_outputs.py`

Coverage includes embedded APK identity parsing, debuggable-release rejection, SDK-drift rejection, deterministic identity evidence, signature-scheme parsing, v2+ acceptance, v1-only rejection in mandatory signed mode, certificate identity, archive structure, package/version metadata, checksum evidence, and existing signature tool failure behavior.

### `scripts/tests/test_verify_release_contract.py`

Coverage includes Gradle minimum/target SDK parsing, invalid SDK ordering, ordinary-CI expectations, protected-workflow expectations, and release-contract drift.

### `scripts/tests/test_verify_release_cli_validation.py`

Dedicated command-line boundary coverage includes zero/invalid SDK expectations and identity-output misuse without manifest verification.

---

## Complete Repository Documentation Coverage

### Tracked-file ownership guard

`scripts/verify_documentation_coverage.py` asks Git for the authoritative tracked-file set with `git ls-files -z`.

Every tracked path must resolve to a documented repository area. Each coverage rule points to canonical documentation, and those documents must themselves remain tracked. Unknown new path families fail closed until maintainers document their ownership.

The guard also requires every tracked detailed Markdown guide under `docs/` to remain linked from `docs/README.md`.

### Regression coverage

`scripts/tests/test_verify_documentation_coverage.py` verifies representative ownership for:

- Android production source;
- Android resources;
- Android JVM tests;
- Android instrumentation tests;
- Room schema history;
- Sudoku engine source/tests;
- Macrobenchmark source;
- repository scripts and script tests;
- GitHub Actions and collaboration metadata;
- Gradle/root/editor files;
- documentation;
- unknown-path rejection;
- missing canonical-document rejection;
- documentation-index omission rejection;
- deterministic rendering and path normalization.

### Repository reference documentation

`docs/REPOSITORY_FILE_REFERENCE.md` provides the path-oriented ownership map and change rules for the complete repository tree.

The documentation pass also expanded or corrected:

- `docs/REPOSITORY_GUARDS.md`;
- `docs/DOCUMENTATION_STANDARDS.md`;
- `docs/PROJECT_STRUCTURE.md`;
- `docs/TESTING.md`;
- `docs/CI_CD.md`;
- `docs/MAINTAINER_GUIDE.md`;
- `docs/CONTRIBUTING_GUIDE.md`;
- root `CONTRIBUTING.md`;
- root `README.md`;
- `docs/README.md`;
- `docs/QUALITY_GATES.md`;
- `docs/EXACT_HEAD_VERIFICATION.md`;
- `docs/BRANCH_FREEZE.md`;
- `docs/V1_RELEASE_CANDIDATE.md`.

Stale documentation that still described already-completed PR #28 verification as pending was corrected. Build-tool documentation was synchronized with the repository's actual Gradle/AGP/Kotlin line.

---

## Integration Defect Found and Fixed

PR #29 and PR #30 both started from the same older `main` checkpoint and both changed CI/documentation. Treating each pull request as independently merge-ready would not have verified their combined result.

The final audit found a concrete combined-state defect:

- PR #29 added `docs/archive/what_changed_through_2026-08-19.md`;
- PR #30 introduced a fail-closed rule requiring every tracked detailed `docs/*.md` file to be discoverable from `docs/README.md`;
- neither branch, in isolation, could prove that the final combined tree satisfied both contracts.

PR #31 was opened only to expose the branch-level integration conflict. The work was then deliberately consolidated on PR #30 instead of merging two independently-tested but jointly-unverified branches.

The final documentation index now links the archived ledger, so the archived history is both preserved and discoverable and the complete-documentation guard remains strict.

---

## Combined Android CI Contract

`.github/workflows/ci.yml` now preserves both workstreams in one quality gate.

Before expensive Android work, CI performs:

1. Java/Gradle setup;
2. `apkanalyzer` discovery;
3. repository secret/signing-material verification;
4. release-output verifier unit tests;
5. release-verifier CLI-boundary tests;
6. documentation-link regression tests;
7. complete tracked-file documentation-coverage regression tests;
8. release-contract regression tests;
9. partial signing fail-closed regression;
10. direct documentation-link verification;
11. direct tracked-file documentation-coverage verification;
12. direct release-contract verification;
13. English/Hindi translation parity.

It then runs the existing engine/JVM/instrumentation-compilation/Macrobenchmark-compilation/lint/build gates, builds release APK/AAB/R8 outputs, and verifies both metadata and embedded APK identity before retaining short-lived evidence.

The CI merge intentionally preserved PR #30's documentation coverage steps while adding PR #29's APK Analyzer/CLI/embedded-identity steps. Neither side replaced the other.

---

## Focused Commits Added During This Final Integration

The final consolidation added focused commits rather than one monolithic merge:

- `48f4591aa47de88936334e552a0e38fb5c438096` — `release: integrate embedded APK verification hardening`;
- `31ea4f0990e1e6f5c50807ef37901245e3a7f03a` — `ci: combine documentation and embedded APK verification gates`;
- `a47c27d4a1613364fe4d2ed9e847fad09c4867b2` — `docs(index): expose archived development ledger`;
- `9f4316905c039468c8568b0187ffc3575bf560ad` — `docs(ci): reconcile repository and APK identity gates`.

This ledger update is intentionally a separate documentation commit so the final branch history keeps implementation, CI integration, documentation indexing, CI/CD documentation, and progress evidence independently reviewable.

---

## Final Changed-File Scope on PR #30

The consolidated pull request currently contains the complete documentation-coverage line plus the release-verifier hardening line. Its changed paths include:

### Workflows

- `.github/workflows/ci.yml`
- `.github/workflows/release-validation.yml`

### Root documentation/community files

- `CONTRIBUTING.md`
- `README.md`
- `what_changed.md`

### Documentation

- `docs/BRANCH_FREEZE.md`
- `docs/CI_CD.md`
- `docs/CONTRIBUTING_GUIDE.md`
- `docs/DOCUMENTATION_STANDARDS.md`
- `docs/EXACT_HEAD_VERIFICATION.md`
- `docs/MAINTAINER_GUIDE.md`
- `docs/PRODUCTION_RELEASE_VALIDATION.md`
- `docs/PROJECT_STRUCTURE.md`
- `docs/QUALITY_GATES.md`
- `docs/README.md`
- `docs/RELEASE_CHECKLIST.md`
- `docs/REPOSITORY_FILE_REFERENCE.md`
- `docs/REPOSITORY_GUARDS.md`
- `docs/TESTING.md`
- `docs/V1_RELEASE_CANDIDATE.md`
- `docs/archive/what_changed_through_2026-08-19.md`

### Repository verification scripts/tests

- `scripts/verify_documentation_coverage.py`
- `scripts/verify_release_contract.py`
- `scripts/verify_release_outputs.py`
- `scripts/tests/test_verify_documentation_coverage.py`
- `scripts/tests/test_verify_release_cli_validation.py`
- `scripts/tests/test_verify_release_contract.py`
- `scripts/tests/test_verify_release_outputs.py`

No unrelated application feature was added merely to increase commit count. The final pass preferred release correctness, deterministic tests, documentation completeness, and truthful evidence boundaries over speculative scope expansion immediately before stable-release validation.

---

## Exact-Head Verification Rule

PR #30 must pass both required pull-request workflows on its **exact final head**:

- `Android CI`;
- `Android Instrumentation` on API 35.

Any workflow result from PR #29, an earlier PR #30 head, or an intermediate integration commit is historical only after this ledger commit changes the branch head.

The consolidation must not be merged into `main` until the exact final PR #30 head is green for both required workflows. This preserves the same exact-head evidence discipline used for the verified RC1 and post-RC lines.

---

## Stable v1.0 Evidence Still Intentionally Pending

Repository-side hardening does **not** complete issue #5 by itself. Stable `v1.0.0` remains unclaimed until real evidence exists for the remaining non-source-control work, including as applicable:

- real-device installation/lifecycle checks;
- TalkBack/accessibility review;
- 200% font/adaptive-layout review;
- representative physical-device Macrobenchmark/performance evidence;
- production signing environment and protected secrets;
- protected production-validation workflow execution against the exact release source;
- trusted signer-certificate identity confirmation;
- GitHub branch-protection/ruleset/environment administration;
- Play Console/store listing/privacy/data declarations and assets;
- final stable version promotion;
- final exact-head verification after stable metadata changes;
- stable tag/GitHub Release/store publication decisions.

These items are not marked complete merely because repository automation and documentation are strong.

---

## Final Repository Principle

The final source-controlled state is designed to fail closed when release identity, documentation ownership, documentation discoverability, signing configuration, translation parity, or release-artifact expectations drift.

The project is repository-ready for the remaining evidence-driven v1.0 process, but repository readiness is not presented as proof that the stable production release has already happened.

## Branding / Support

- Project: **SudokuNova**
- Tagline: **Think. Solve. Master the Grid.**
- Repository: `https://github.com/sanskarIN/SudokuNova`
- GitHub: `https://www.github.com/sanskarIN`
- Buy Me a Coffee: `https://buymeacoffee.com/sanskarIN`
- Business: `sanskarin@outlook.in`
- Business: `sanskarin.business@gmail.com`
- Support: `supportramsandesh@gmail.com`
- Credit: **Made by the Sanskar**
- License: **MIT**
