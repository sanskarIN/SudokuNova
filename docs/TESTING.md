# SudokuNova Testing Guide

SudokuNova treats deterministic correctness and regression coverage as merge requirements. The test strategy now spans the Kotlin Multiplatform Sudoku engine, portable Compose gameplay state, the mature Android application, repository/release tooling, connected Android behavior, host-specific cross-platform builds, Macrobenchmark infrastructure, and explicit real-world release evidence.

The current Android source/release target is **2.0.12** (`versionCode 2012` / `versionName 2.0.12`). Historical green runs prove only the exact commits they tested.

See [`CROSS_PLATFORM.md`](CROSS_PLATFORM.md) for platform-build commands and [`CI_CD.md`](CI_CD.md) for hosted workflow ownership.

## Testing Layers

The project uses complementary layers:

1. KMP `sudoku-engine` common tests for Sudoku truth and deterministic domain behavior;
2. `sharedUI` common gameplay-state tests executed through the Desktop target;
3. shared Desktop and Web/Wasm compile gates;
4. Android app JVM tests for codecs/models/presentation-independent application logic;
5. Python regression tests for repository/release tooling;
6. direct documentation, release-contract, translation, and security guards;
7. Android instrumentation-test compilation;
8. connected API-35 Compose/Room tests;
9. Android debug/release lint and full APK/AAB builds;
10. release APK/AAB/R8 identity/checksum verification;
11. host-specific Web, iOS framework, and Desktop application-image builds;
12. Macrobenchmark harness compilation plus representative physical-device measurement;
13. real manual accessibility/device/signing/store/browser/distribution QA.

No single layer replaces the others.

## Shared Sudoku Engine Tests

Run:

```bash
./gradlew :sudoku-engine:desktopTest --stacktrace
```

Windows PowerShell:

```powershell
.\gradlew.bat :sudoku-engine:desktopTest --stacktrace
```

The existing engine tests use `kotlin.test` and live under:

```text
sudoku-engine/src/test/kotlin/com/sanskar/sudokunova/engine/
```

The KMP build maps that tree into `commonTest`, allowing shared domain tests to protect the engine independently of Android.

### Board correctness

Coverage includes parsing/serialization, row/column/box validation, conflicts, candidates, immutable updates, and invalid input behavior.

### Solver correctness

Coverage includes known puzzle solving, invalid/unsolvable behavior, solution counting, uniqueness checks, and search metrics.

### Generator correctness

Coverage includes seeded determinism, generated-board validity, unique-solution preservation, clue/difficulty targets, deterministic corpora, and relevant complexity regressions.

### Difficulty and logical analysis

Coverage includes logical technique accounting, calibration, corpus expectations, and deterministic logical results.

### Teaching and hints

Coverage includes deterministic teaching traces, singles, pairs/triples, pointing/box-line techniques, X-Wing, exact source/target/elimination evidence, solution-safe eliminations, hint technique identity, and explicit Reveal fallback separation.

### Practice and puzzle codes

Tests protect deterministic practice lookup/answers/evidence and the versioned `SNP1` puzzle-code format.

A failure in this suite is a shared correctness defect because Android, Desktop, iOS, and Web all depend on this engine.

## Shared Gameplay-State Tests

Run:

```bash
./gradlew :sharedUI:desktopTest --stacktrace
```

Current common tests protect:

- fixed clues cannot be edited;
- notes can be toggled/entered;
- correct number placement clears the cell's notes;
- undo restores the previous board/notes snapshot;
- reset restores the generated starting puzzle;
- hints make valid progress and retain board validity.

Add portable state tests here when behavior can be verified without Android/Apple/browser APIs.

## Shared Compile Gates

Run:

```bash
./gradlew :sharedUI:compileKotlinDesktop :sharedUI:compileKotlinWasmJs --stacktrace
```

These gates catch common Compose/source-set/API incompatibilities before host packaging. Compilation does not replace runtime/browser/accessibility QA.

## Cross-Platform Hosted Build Tests

`.github/workflows/cross-platform.yml` adds target-specific repository build evidence.

### Android shared integration

```bash
./gradlew :app:assembleDebug --stacktrace
```

This proves the Android application can resolve and consume both KMP modules.

### Web production distribution

```bash
./gradlew :sharedUI:wasmJsBrowserDistribution --stacktrace
```

The hosted workflow uploads the resulting production distribution. A successful build does not prove every intended browser/device combination.

### iOS Simulator framework

On macOS:

```bash
./gradlew :sharedUI:linkDebugFrameworkIosSimulatorArm64 --stacktrace
```

This validates Kotlin/Native framework linking. It does not compile a complete signed Xcode app or prove physical-device/App Store behavior.

### Desktop application images

On Linux, Windows, and macOS:

```bash
./gradlew :sharedUI:createDistributable --stacktrace
```

Each hosted runner validates its own application image. Native package signing/notarization/reputation remains external evidence.

## Android JVM Tests

Run:

```bash
./gradlew :app:testDebugUnitTest --stacktrace
```

The app-module JVM suite covers areas such as:

- game-state codecs and malformed-state rejection;
- settings/statistics calculations;
- learning-progress invariants/mastery calculations;
- backup codec/model behavior;
- bounded backup file reading;
- transfer/persistence helper behavior;
- pure presentation helpers where Android runtime is unnecessary.

The Android app module remains JUnit4-based unless intentionally migrated.

## Backup Boundary Tests

`BackupFileIoTest` protects bounded reads, exact-limit content, empty input rejection, oversized input rejection, and positive maximum-size requirements. This protects the pre-parser memory boundary in addition to structural backup-codec validation.

## Repository Guard Regression Tests

Run:

```bash
python -m unittest scripts.tests.test_verify_documentation_links
python -m unittest scripts.tests.test_verify_documentation_coverage
python -m unittest scripts.tests.test_verify_release_contract
python -m unittest scripts.tests.test_verify_release_outputs
python -m unittest scripts.tests.test_verify_release_cli_validation
```

### Documentation-link suite

Protects repository-local Markdown targets, ignored/generated locations, repository-boundary rejection, and supported link forms.

### Documentation-coverage suite

Protects path ownership for:

- Android source/tests/resources/schemas;
- `sudoku-engine` source/tests;
- `sharedUI` common/platform source/tests/resources;
- `iosApp` host sources;
- Macrobenchmark;
- scripts/tests;
- workflows/GitHub metadata;
- Gradle/root/editor files;
- root documents and detailed docs.

It also verifies rejection of unknown paths, missing canonical docs, hidden detailed guides, deterministic report rendering, and Windows separator normalization.

### Release-contract suite

Protects application ID/version/minSdk/targetSdk synchronization between `app/build.gradle.kts`, ordinary Android CI, and protected release validation.

### Release-output suites

Protect archive structure, metadata identity, embedded APK application/version/SDK/debuggable inspection, deterministic hashes/evidence, signature scheme parsing, signer certificate identities, and CLI argument boundaries.

After regression tests, execute the guards against the checked-out repository:

```bash
python scripts/verify_documentation_links.py
python scripts/verify_documentation_coverage.py
python scripts/verify_release_contract.py
python scripts/verify_no_secrets.py
python scripts/verify_translations.py
```

For a per-file documentation ownership audit:

```bash
python scripts/verify_documentation_coverage.py --verbose
```

## Partial Release-Signing Fail-Closed Test

Android production signing accepts either none or all four `SUDOKUNOVA_*` values. CI intentionally sets only a harmless fake alias and requires Gradle configuration to fail with the expected partial-signing error.

A local Bash-compatible check is:

```bash
SUDOKUNOVA_KEY_ALIAS=partial-test ./gradlew :app:tasks --quiet
```

Do not use real secret values to test this guard.

## Android Instrumentation-Test Compilation

```bash
./gradlew :app:assembleDebugAndroidTest --stacktrace
```

This proves the instrumentation test APK compiles, not that connected behavior passes.

## Connected Android Tests

Run on a configured emulator/device:

```bash
./gradlew :app:connectedDebugAndroidTest --stacktrace
```

The hosted `instrumentation.yml` gate runs the committed suite on API 35.

Connected coverage includes mature Android flows such as Compose navigation/semantics, Room-backed history/saved data, challenges, transfer/backup, and learning/practice integration.

When adding connected tests:

- prefer stable semantics/test tags;
- avoid timing-sensitive sleeps;
- isolate persistent state;
- preserve deterministic inputs;
- assert user-observable behavior rather than implementation trivia where practical.

## Android Lint

```bash
./gradlew :app:lintDebug :app:lintRelease --stacktrace
```

Both debug and release lint are required because release-only shrink/configuration/resource paths can expose different defects.

## Android Build Gates

```bash
./gradlew :app:assembleDebug --stacktrace
./gradlew :app:assembleRelease --stacktrace
./gradlew :app:bundleRelease --stacktrace
```

The release path must retain R8/resource shrinking and the exact 2.0.12 identity contract.

## Release Artifact Verification

After unsigned release outputs are built:

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

This proves artifact integrity and embedded identity for the exact built files. It does not prove production certificate identity unless the mandatory signed verification path actually runs.

## Macrobenchmark Harness Compilation

```bash
./gradlew :macrobenchmark:assembleBenchmark --stacktrace
```

This proves the release-like benchmark target and test graph compile. It does not produce representative performance evidence.

## Physical-Device Macrobenchmark Evidence

Run on a representative connected physical device:

```bash
./gradlew :macrobenchmark:connectedBenchmarkAndroidTest --stacktrace
```

Record:

- exact Git SHA;
- device model;
- Android version/API;
- build variant;
- command;
- raw benchmark output/traces;
- environmental notes relevant to interpretation.

Do not substitute hosted-emulator timing for production performance evidence.

## Accessibility and Manual QA

Automated semantics checks are useful but do not replace real manual verification. Before making release-quality claims, collect actual evidence for the intended production surface, including where applicable:

- screen reader navigation;
- 200% font scaling/adaptive layout;
- contrast and state distinguishability;
- reduced-motion behavior;
- hardware keyboard/focus behavior;
- rotation/resizing/window behavior;
- lifecycle/background/restore;
- physical-device install/upgrade;
- platform-specific input and window conventions.

Do not mark these successful merely because source code appears to support them.

## Browser QA

Before a public Web claim, test the actual intended browser/version/device matrix. Record at least browser name/version, OS/device, exact Git SHA/build artifact, result, and any known limitations.

## Apple QA

Kotlin/Native framework compilation is only the first Apple gate. A production iOS/iPadOS claim additionally requires a real Xcode host, signing/provisioning, simulator and physical-device execution as applicable, accessibility/lifecycle testing, and store validation when publishing.

## Desktop QA

A successful application image proves host buildability. Distribution-quality claims may additionally require installer testing, signing/notarization/reputation, window/input/accessibility validation, supported OS-version checks, and clean-machine installation tests.

## Exact-Head Rule

For pull requests touching the cross-platform build graph, final merge evidence must be green on one exact final SHA for:

- Android CI;
- Android Instrumentation;
- every Cross-Platform CI job.

Any subsequent commit invalidates older runs as final branch evidence. Fix failures with focused commits, then require new runs on the new head.

## Strong Local Non-Connected Baseline

Linux/macOS:

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

Host-specific iOS/Desktop/Web runtime/package checks and Android connected tests remain separate.

## Evidence Discipline

A test/build claim should identify the exact command/workflow and exact source SHA. A release/platform claim should state what remains external. Never convert:

- configured → verified;
- compiled → runtime-tested;
- built → signed/published;
- emulator-tested → physical-device-tested;
- framework-linked → App Store-ready;
- CI artifact → production distribution.
