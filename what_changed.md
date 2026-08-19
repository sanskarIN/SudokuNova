# What Changed

## Current Development State — SudokuNova 2.0.12 — 2026-08-19

**Repository:** `https://github.com/sanskarIN/SudokuNova`  
**Base branch:** `main`  
**Active final branch:** `docs/complete-repository-coverage`  
**Active pull request:** `#30`  
**Current source release target:** `2.0.12`  
**Android versionCode:** `2012`  
**Android versionName:** `2.0.12`  
**Android application ID:** `in.sanskar.sudokunova`  
**Kotlin namespace:** `com.sanskar.sudokunova`  
**minSdk:** `26`  
**targetSdk:** `37`  
**compileSdk:** `37`  
**Java/JVM target:** `17`  
**License:** MIT  
**Project commit email:** `sanskarin@outlook.in`

SudokuNova's current source line has been deliberately promoted from the historical `1.0.0-rc.1 / versionCode 1000` release-candidate metadata to **`2.0.12 / versionCode 2012`**. This is a source/release-contract promotion requested after the prior final-hardening head had already been prepared. It invalidates all earlier queued or green workflow runs as final evidence for the new version because exact-head verification applies to the new 2.0.12 commits.

The project must not claim that 2.0.12 is published merely because source metadata now uses that version. Production signing, protected-release execution, physical-device/manual QA, repository administration, store validation, final exact-head CI, tagging, GitHub Release creation, and distribution remain separate evidence requirements.

The cumulative pre-final-continuation development ledger remains preserved at:

`docs/archive/what_changed_through_2026-08-19.md`

Historical v1 release preparation and post-RC validation evidence remains preserved in the v1-specific documentation and merged PR history. This active file records the final repository-hardening and 2.0.12 continuation.

---

## 2.0.12 Version Promotion

### Android source metadata

`app/build.gradle.kts` now defines:

```text
applicationId = in.sanskar.sudokunova
versionCode = 2012
versionName = 2.0.12
minSdk = 26
targetSdk = 37
```

The production namespace and package identity are unchanged. The version code is strictly greater than the previous RC code `1000`.

### Ordinary Android CI

`.github/workflows/ci.yml` now verifies generated release outputs against:

```text
--expected-version-code 2012
--expected-version-name 2.0.12
--expected-application-id in.sanskar.sudokunova
--require-apk-manifest
--expected-min-sdk 26
--expected-target-sdk 37
```

The release-output path continues to require:

- non-empty APK/AAB/R8 outputs;
- archive integrity and required entries;
- exact application/version metadata;
- embedded APK application/version/minimum-SDK/target-SDK identity;
- `debuggable=false` for the release APK;
- deterministic SHA-256 and byte-size evidence;
- deterministic `apk-identity.txt` evidence.

### Protected production validation

`.github/workflows/release-validation.yml` now defaults its manually dispatched version inputs to:

- `expected_version_code = 2012`;
- `expected_version_name = 2.0.12`.

The protected workflow continues to pin:

- production application ID `in.sanskar.sudokunova`;
- minimum SDK `26`;
- target SDK `37`.

It still requires external protected signing material and trusted expected APK/AAB signer certificate SHA-256 identities before a real production-validation run can succeed.

### Source/workflow release contract

`scripts/verify_release_contract.py` remains the fail-closed consistency guard. It reads `app/build.gradle.kts` and compares it with ordinary CI and protected-workflow release expectations.

A successful direct guard on the final head must therefore prove that all three locations agree on the 2.0.12 source contract.

---

## 2.0.12 Current Release Authority

Added:

`docs/V2_0_12_RELEASE.md`

This is the current release-version authority and documents:

- exact source version/package/SDK contract;
- version sources of truth;
- required repository-side quality gates;
- exact-head policy;
- ordinary unsigned CI artifact contract;
- protected production validation contract;
- real installation/lifecycle evidence requirements;
- TalkBack/200% font/adaptive-layout/contrast/motion/keyboard evidence requirements;
- representative physical-device Macrobenchmark requirements;
- store/distribution validation requirements;
- final evidence fields;
- `SHIP` / `NO-SHIP` decision boundary;
- `v2.0.12` tag/publication boundary;
- historical v1 documentation boundary.

`docs/README.md` now indexes this file as the first current release entry and explicitly classifies v1 release documents as historical release-line evidence.

`docs/PRODUCTION_RELEASE_VALIDATION.md` now documents the 2012/2.0.12 workflow defaults and points current evidence capture to the 2.0.12 release authority.

`docs/CI_CD.md` now describes the current 2.0.12 CI/release-artifact contract rather than referring to the v1 RC identity as current.

---

## Final Repository Hardening Preserved

The 2.0.12 promotion is built on the final repository-hardening work already consolidated into PR #30.

### Complete tracked-file documentation coverage

`scripts/verify_documentation_coverage.py` uses `git ls-files -z` as the authoritative repository file inventory.

It fails closed when:

- a tracked path has no maintained documentation owner;
- a coverage rule points to an untracked/missing canonical document;
- a detailed tracked `docs/*.md` guide is not discoverable from `docs/README.md`.

It supports:

```bash
python scripts/verify_documentation_coverage.py
python scripts/verify_documentation_coverage.py --verbose
python scripts/verify_documentation_coverage.py --markdown
```

Regression coverage lives in `scripts/tests/test_verify_documentation_coverage.py`.

`docs/REPOSITORY_FILE_REFERENCE.md` defines the complete path-oriented ownership taxonomy and maintenance rules.

### Documentation link integrity

`scripts/verify_documentation_links.py` and its regression suite continue to reject missing repository-local Markdown targets and repository-escaping links.

### Embedded APK identity verification

`scripts/verify_release_outputs.py` independently reads the built APK with Android SDK `apkanalyzer` and validates:

- application ID;
- version code;
- version name;
- minimum SDK;
- target SDK;
- debuggable state.

This means the release gate does not trust only `output-metadata.json`.

### APK signature-scheme verification

Mandatory signed-release verification parses `apksigner` output and requires at least one verified APK v2-or-newer signature scheme. A v1-only APK is rejected in the protected mandatory-signature path.

### Release-verifier CLI validation

`scripts/tests/test_verify_release_cli_validation.py` verifies invalid SDK arguments and APK-identity output misuse fail before artifact access.

### Release-contract SDK synchronization

`scripts/verify_release_contract.py` synchronizes application ID, version code, version name, minimum SDK, and target SDK between source and workflows. It rejects non-positive values, invalid SDK ordering, unsafe version names, `.debug` production IDs, and workflow/source drift.

### Repository/public-project hardening retained

The cumulative line retains:

- repository secret/signing-material guards;
- English/Hindi translation parity;
- Sudoku engine deterministic tests;
- Android JVM tests;
- API-35 Compose/Room connected instrumentation;
- instrumentation-test APK compilation;
- debug/release lint;
- debug APK assembly;
- R8/resource-shrunk release APK assembly;
- release AAB assembly;
- Macrobenchmark harness compilation;
- production signing fail-closed behavior;
- protected production-release validation workflow;
- certificate-bound APK/AAB validation;
- repository documentation-link and coverage guards;
- release source/workflow contract guard;
- CODEOWNERS;
- Dependabot;
- issue/PR templates;
- security/support/contribution policies;
- funding metadata;
- generated-release-note configuration;
- complete user/developer/maintainer/release documentation.

---

## Historical v1 Release Work Preserved

The following history remains valid but is not the current version target.

### Verified v1 RC1 preparation

PR #27:

- final verified head: `7016e21f36c8ecb8a495c446ffd8b57e9f20a4ea`;
- Android CI run #635 / `32151771317` — GREEN;
- API-35 instrumentation run #188 / `32151771297` — GREEN;
- merge commit: `2329881aff8dabaf8d040918e16b6113e3900245`;
- source metadata at that historical checkpoint: `versionCode 1000` / `versionName 1.0.0-rc.1`.

### Verified post-RC validation hardening

PR #28:

- final verified head: `c3e0e3fc217062e374a434cfea46235fd6595f83`;
- Android CI run #706 / `32211246803` — GREEN;
- API-35 instrumentation run #229 / `32211246802` — GREEN;
- merge commit: `27640cb9089ddae4a9242bb84a8927c3761201f4`.

Those workflow runs prove only those historical heads. They cannot be used as final 2.0.12 evidence.

Historical documents retained include:

- `docs/V1_RELEASE_PREP.md`;
- `docs/V1_RELEASE_CANDIDATE.md`;
- `docs/V1_RELEASE_EVIDENCE.md`;
- `docs/V1_RELEASE_NOTES.md`;
- `docs/POST_RC_VALIDATION_EVIDENCE.md`;
- `docs/BRANCH_FREEZE.md`.

---

## PR #29 / #30 / #31 Consolidation History

Before the 2.0.12 promotion, the final repository audit found two independently developed hardening lines:

- PR #29 — embedded APK/release-verifier hardening;
- PR #30 — complete tracked-file documentation coverage.

Both had started from the same earlier `main` checkpoint and both modified CI/documentation. A temporary PR #31 exposed their integration conflicts.

The final combined state was consolidated onto PR #30 rather than merging two separately verified branches whose joint state had not been tested.

A concrete integration defect was fixed: PR #29 added `docs/archive/what_changed_through_2026-08-19.md`, while PR #30's new documentation guard requires every detailed tracked guide to be indexed. `docs/README.md` was updated so the archive remains both preserved and discoverable.

PR #31 was closed as a temporary conflict-detection helper. PR #29 was closed as superseded after its complete hardening work was integrated into PR #30.

PR #30 remains the single current final source line.

---

## Focused Final Commits

Final consolidation commits before the version promotion included:

- `48f4591aa47de88936334e552a0e38fb5c438096` — `release: integrate embedded APK verification hardening`;
- `31ea4f0990e1e6f5c50807ef37901245e3a7f03a` — `ci: combine documentation and embedded APK verification gates`;
- `a47c27d4a1613364fe4d2ed9e847fad09c4867b2` — `docs(index): expose archived development ledger`;
- `9f4316905c039468c8568b0187ffc3575bf560ad` — `docs(ci): reconcile repository and APK identity gates`;
- `2ed4ab9ad63a0a9a709c1ee2324d6b738b991bfb` — `docs(progress): record final consolidated hardening state`;
- `6ea987c7548a4b4e0f427b7486d4ad6465304932` — `docs(changelog): record final repository hardening line`.

2.0.12 promotion commits include:

- `b804c50d318b5af84751fe188d188526cd4ae4d7` — `chore(release): promote Android metadata to 2.0.12`;
- `df3529de7dc1f43156fe7af4a49f5b6c036a4831` — `ci(release): verify version 2.0.12 artifacts`;
- `ae8c750472765f3725cc59f0f7154cecabe76309` — `ci(release): default protected validation to 2.0.12`;
- `d1c80e73fa791fb68bb0b984cfb649ea2327e04f` — `docs(release): add 2.0.12 release authority`;
- `9655d4f91e5312aad0369ad6b9ef029359994a9b` — `docs(index): make 2.0.12 the current release authority`;
- `7548168328fa8321e5261a4a99f7932a3f98eee4` — `docs(release): align protected validation with 2.0.12`;
- `351aa8ba47796e16145d5c0ab91367beb4bd79ee` — `docs(ci): align quality gates with 2.0.12`.

This update is another focused documentation commit and therefore changes the exact pull-request head again. Workflow results from any earlier head are historical only.

---

## Required Final 2.0.12 Verification

Before PR #30 can merge as the 2.0.12 source line, its exact final head must pass:

1. `Android CI`;
2. `Android Instrumentation` on API 35.

The standard CI run must include the current version contract and all repository guards. The connected run must exercise the maintained Compose/Room integration suite.

No merge should occur from an older successful SHA after a later documentation or source commit changes the head.

---

## 2.0.12 Production Evidence Still Pending

The following must remain unclaimed until real evidence exists:

- `main` branch/ruleset protection and required-check administration;
- actual `production-release` GitHub Environment access/ref/reviewer controls;
- actual production/upload signing key material and recovery process;
- real protected Production Release Validation execution on the exact intended 2.0.12 release ref;
- expected APK signer certificate evidence;
- expected AAB signer/upload certificate evidence;
- signed APK installation/launch smoke where direct distribution is intended;
- distribution-platform AAB validation;
- TalkBack traversal/focus-order QA;
- representative 200% font, narrow phone, large phone, tablet/window/resize/orientation QA;
- high-contrast/reduced-motion device QA;
- representative physical-device Macrobenchmark startup/frame evidence and traces;
- memory and ANR evidence;
- process-death/lifecycle real-target QA;
- final signed-artifact R8 smoke QA;
- current store screenshots/listing/privacy/data/content/target-API validation;
- final signed artifact hashes/signature/identity evidence;
- final `SHIP` decision;
- immutable `v2.0.12` tag;
- GitHub Release;
- Android/store publication.

Repository-side work can prepare and verify the mechanisms for these tasks, but it cannot truthfully manufacture their real-world results.

---

## Current Release Principle

The repository must fail closed when release identity, documentation ownership/discoverability, signing configuration, translation parity, artifact structure, embedded APK identity, or expected signer identity drifts.

The 2.0.12 line should merge only from an exact head that passes the complete automated gate. Publication should occur only after the additional production/manual/admin/store evidence in `docs/V2_0_12_RELEASE.md` is actually completed.

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
