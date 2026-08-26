# SudokuNova CI/CD and Automated Quality Gates

SudokuNova uses GitHub Actions as an evidence-oriented verification system. Hosted CI proves source, test, build, packaging, and artifact invariants for the exact commit tested; it does not automatically prove production signing, physical-device quality, store acceptance, notarization, or publication.

For platform boundaries see [`CROSS_PLATFORM.md`](CROSS_PLATFORM.md). For the current release-preparation contract see [`V2_0_14_RELEASE.md`](V2_0_14_RELEASE.md).

## Workflow Overview

### Android CI — `.github/workflows/ci.yml`

The pull-request gate protects shared KMP code and the mature Android release graph.

Current 2.0.14 stages include:

1. checkout, Java 17, Gradle validation/cache;
2. Android SDK `apkanalyzer` discovery;
3. repository secret/signing-material guard;
4. release-output verifier unit/CLI tests;
5. documentation-link, documentation-coverage, release-contract, and translation regression tests;
6. partial release-signing fail-closed regression;
7. direct documentation/release-contract/translation guards;
8. `:sudoku-engine:desktopTest`;
9. `:sharedUI:desktopTest`, including gameplay, `SNG1`, and `SNS1` settings tests;
10. shared Desktop/Web compilation;
11. Android JVM tests;
12. Android instrumentation-test compilation;
13. Macrobenchmark compilation;
14. debug/release lint;
15. debug APK;
16. R8/resource-shrunk release APK;
17. release AAB;
18. exact `in.sanskar.sudokunova` / `2014` / `2.0.14` identity verification;
19. embedded minSdk 26 / targetSdk 37 / `debuggable=false` verification;
20. deterministic SHA-256, byte-size, and APK-identity evidence;
21. short-lived report/release-output uploads.

Shared tests remain before expensive Android release work so portable failures are fixed at their source rather than hidden behind target-specific patches.

### Cross-Platform CI — `.github/workflows/cross-platform.yml`

Hosted target coverage includes:

- shared engine and sharedUI tests;
- Desktop and Web/Wasm compilation;
- Android shared integration via `:app:assembleDebug`;
- Web/Wasm production distribution;
- iOS Simulator framework linking on macOS;
- Desktop application-image creation on Linux, Windows, and macOS.

The shared test suite now covers active-game persistence plus deterministic `SNS1` settings codec/store/state behavior. Native settings adapters compile through their corresponding host targets.

Desktop package version is `2.0.14` and is synchronized with Android `versionName` by the release-contract guard.

A successful hosted package/framework build is repository evidence only. It does not prove signing, notarization, real-device behavior, browser compatibility, clean-machine installation, or accessibility quality.

### Android Instrumentation — `.github/workflows/instrumentation.yml`

The connected API-35 suite protects mature Android Compose/Room behavior. Instrumentation-test compilation in Android CI is not equivalent to this connected result.

### Production Release Validation — `.github/workflows/release-validation.yml`

This manually dispatched workflow is reserved for trusted Android production-signing validation through the `production-release` GitHub Environment.

Current source defaults:

```text
applicationId = in.sanskar.sudokunova
versionCode   = 2014
versionName   = 2.0.14
minSdk        = 26
targetSdk     = 37
```

When the protected external environment is actually configured, the workflow can build signed R8 APK/AAB outputs, verify embedded identity, require expected signature schemes/certificate fingerprints, record non-secret evidence, and remove temporary signing material.

Committed workflow source does not prove environment reviewers, allowed refs, secrets, signing keys, or a successful protected run.

## Exact-Head Pull-Request Policy

PR #44 is merge-verified only when all three required workflow families are green on the **same exact final head SHA**:

- Android CI;
- Android Instrumentation;
- Cross-Platform CI, with every required matrix job successful.

Any later code or documentation commit invalidates older runs as final evidence. Never combine green runs from different SHAs.

Historical 2.0.13/2.0.12/v1 results remain evidence only for their exact historical commits.

## Concurrency and Cancellation

PR workflows may cancel superseded in-progress work when a newer head appears. A cancelled old run is expected during active development and must not be treated as a failure of the newer head or reused as final evidence.

## Repository Guards

### Security

```bash
python scripts/verify_no_secrets.py
```

### Documentation links and tracked-file ownership

```bash
python -m unittest scripts.tests.test_verify_documentation_links
python -m unittest scripts.tests.test_verify_documentation_coverage
python scripts/verify_documentation_links.py
python scripts/verify_documentation_coverage.py
```

The coverage guard uses tracked files as its authority and fails when a path has no maintained documentation owner, a canonical guide is absent, or a detailed `docs/*.md` page is not indexed from `docs/README.md`.

### Release identity synchronization

```bash
python -m unittest scripts.tests.test_verify_release_contract
python scripts/verify_release_contract.py
```

The guard synchronizes:

- Android application/version/SDK identity;
- Desktop `packageVersion = 2.0.14` with Android `versionName`;
- ordinary Android CI expected values;
- protected production-validation defaults and immutable application/SDK expectations.

### Translation parity

```bash
python -m unittest scripts.tests.test_verify_translations
python scripts/verify_translations.py
```

This covers mature Android localization and shared English/Hindi key/placeholder parity, including the new shared theme labels.

### Release verifier

```bash
python -m unittest scripts.tests.test_verify_release_outputs
python -m unittest scripts.tests.test_verify_release_cli_validation
```

Coverage includes archive structure, metadata/embedded identity, SDK/debuggable checks, deterministic hashes, signature-scheme parsing, certificate fingerprints, and CLI boundaries.

## Shared Test and Compile Gates

Engine:

```bash
./gradlew :sudoku-engine:desktopTest --stacktrace
```

Shared gameplay/persistence/settings:

```bash
./gradlew :sharedUI:desktopTest --stacktrace
```

This protects fixed clues, notes, entry, undo/reset/hints, deterministic grid movement, `SNG1`, `SNS1`, encoded stores, and common settings state behavior.

Portable compile:

```bash
./gradlew :sharedUI:compileKotlinDesktop :sharedUI:compileKotlinWasmJs --stacktrace
```

The shared theme/resource/keyboard/focus APIs must compile on supported hosted targets rather than being disabled to make CI pass.

## Android Build Gates

```bash
./gradlew :app:testDebugUnitTest --stacktrace
./gradlew :app:assembleDebugAndroidTest --stacktrace
./gradlew :macrobenchmark:assembleBenchmark --stacktrace
./gradlew :app:lintDebug :app:lintRelease --stacktrace
./gradlew :app:assembleDebug :app:assembleRelease :app:bundleRelease --stacktrace
```

The release path retains R8/resource shrinking and exact 2014/2.0.14 artifact verification.

Connected instrumentation and representative physical-device Macrobenchmark evidence remain separate from compilation.

## Artifact Evidence

Ordinary Android CI writes deterministic unsigned release hash/identity evidence. Cross-Platform CI uploads short-lived Web, iOS Simulator framework, and Desktop application-image artifacts after successful host builds.

A CI artifact is not automatically a production-distribution artifact.

## Failure Triage Order

When a workflow fails:

1. identify the exact failing SHA;
2. read the first failed job/step and its logs;
3. fix KMP/common configuration or shared code at the root instead of dropping targets;
4. treat engine failures as shared correctness/puzzle-exchange defects;
5. treat sharedUI failures as gameplay/persistence/settings/resource/input defects;
6. investigate a single-host packaging failure as a host/toolchain issue only after shared gates pass;
7. if Android release guards fail, preserve the 2014/2.0.14 identity/signing/evidence contract and repair drift;
8. if documentation guards fail, document/index the new area rather than adding bypasses;
9. push the focused fix;
10. discard superseded workflow evidence and require a new exact-head set.

## Local Pre-Push Verification

```bash
python scripts/verify_no_secrets.py
python -m unittest scripts.tests.test_verify_documentation_links
python -m unittest scripts.tests.test_verify_documentation_coverage
python -m unittest scripts.tests.test_verify_release_contract
python -m unittest scripts.tests.test_verify_release_outputs
python -m unittest scripts.tests.test_verify_release_cli_validation
python -m unittest scripts.tests.test_verify_translations
python scripts/verify_documentation_links.py
python scripts/verify_documentation_coverage.py
python scripts/verify_release_contract.py
python scripts/verify_translations.py
./gradlew \
  :sudoku-engine:desktopTest \
  :sharedUI:desktopTest \
  :sharedUI:compileKotlinDesktop \
  :sharedUI:compileKotlinWasmJs \
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

Then run connected/platform-native/runtime checks on the required hosts.

## Required External Evidence

Source and hosted CI cannot by themselves establish:

- Android production signing/certificate identity unless protected validation succeeds;
- physical-device Android lifecycle/accessibility/performance quality;
- real settings persistence/theme/keyboard behavior on every target;
- Apple production host/signing/provisioning/device/App Store evidence;
- macOS notarization/Gatekeeper evidence;
- Windows signing/reputation/clean-machine evidence;
- Linux clean install/upgrade/remove evidence;
- intended Web browser/device/accessibility/privacy-mode persistence compatibility;
- store listing/privacy/rollout acceptance;
- final `SHIP`, tag, GitHub Release, or public publication.

Record those results only after they occur. Issue #43 is the 2.0.14 evidence tracker.
