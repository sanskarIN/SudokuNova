# SudokuNova Testing Guide

SudokuNova treats deterministic correctness and regression coverage as merge requirements. The test strategy spans the Kotlin Multiplatform Sudoku engine, portable Compose gameplay/persistence/settings state, the mature Android application, repository/release tooling, connected Android behavior, host-specific cross-platform builds, Macrobenchmark infrastructure, and explicit real-world release evidence.

The current source/release-preparation target is **2.0.14** (`versionCode 2014` / `versionName 2.0.14`, Desktop `packageVersion 2.0.14`). Historical green runs prove only the exact commits they tested.

See [`CROSS_PLATFORM.md`](CROSS_PLATFORM.md) for platform-build boundaries, [`SHARED_SETTINGS.md`](SHARED_SETTINGS.md) for `SNS1`, and [`CI_CD.md`](CI_CD.md) for hosted workflow ownership.

## Testing Layers

The project uses complementary layers:

1. KMP `sudoku-engine` common tests for Sudoku truth and deterministic domain behavior;
2. `sharedUI` common tests for gameplay, `SNG1` active-game persistence, and `SNS1` settings behavior;
3. shared Desktop and Web/Wasm compile gates;
4. Android app JVM tests for codecs/models/application logic;
5. Python regression tests for repository/release tooling;
6. direct documentation/release-contract/translation/security guards;
7. Android instrumentation-test compilation;
8. connected API-35 Compose/Room tests;
9. Android debug/release lint and APK/AAB/R8 builds;
10. release output identity/checksum verification;
11. host-specific Web, iOS framework, and Desktop application-image builds;
12. Macrobenchmark harness compilation plus representative physical-device measurement;
13. real manual accessibility/device/signing/store/browser/distribution QA.

No single layer replaces the others.

## Shared Sudoku Engine Tests

Run:

```bash
./gradlew :sudoku-engine:desktopTest --stacktrace
```

The KMP engine tests live under:

```text
sudoku-engine/src/test/kotlin/com/sanskar/sudokunova/engine/
```

Coverage includes:

- board parsing/serialization, immutable updates, validation, conflicts, and candidates;
- solver correctness, unsolvable input, solution counting, and uniqueness;
- seeded deterministic generation and generated-puzzle uniqueness;
- difficulty/logical analysis and calibration;
- teaching traces, hints, supported techniques, and Reveal fallback separation;
- deterministic practice behavior;
- fixed `SNP1` compatibility vectors;
- `PuzzleExchangeService` rejection of malformed, unsolvable, or non-unique imported puzzles.

A failure here is a shared correctness defect because every target depends on the engine.

## Shared Gameplay / Persistence / Settings Tests

Run:

```bash
./gradlew :sharedUI:desktopTest --stacktrace
```

### Gameplay-state coverage

Protects:

- fixed clue immutability;
- note entry/removal;
- normal value placement;
- peer-note cleanup;
- undo/reset;
- hint progress and validity;
- difficulty changes;
- deterministic selection movement and edge clamping;
- snapshot restore validation.

### `SNG1` active-game persistence coverage

Protects:

- deterministic bounded encoding;
- malformed version/field/board/selection/note rejection;
- encoded-store load/save/clear;
- corrupt stored state fail-closed behavior;
- shared state save/restore/clear integration.

### `SNS1` user-settings coverage

Files include:

```text
SharedSettingsCodecTest.kt
EncodedSharedSettingsStoreTest.kt
SharedSettingsStateTest.kt
```

Coverage includes:

- exact deterministic default `SNS1` vector;
- non-default settings round trip;
- unsupported version rejection;
- malformed boolean rejection;
- unknown enum rejection;
- invalid mistake-limit rejection;
- missing/extra/duplicate field rejection;
- oversized payload rejection;
- encoded settings store save/load/clear;
- corrupt stored payload fail-closed behavior;
- observable settings update/replace/restore/save/clear behavior.

Add portable tests here when behavior can be verified without Android/Apple/browser-specific runtime APIs.

## Shared Compile Gates

```bash
./gradlew :sharedUI:compileKotlinDesktop :sharedUI:compileKotlinWasmJs --stacktrace
```

These gates catch common Compose/source-set/API incompatibilities in shared resources, System/Light/Dark theme handling, keyboard/focus mappings, and common state before host packaging.

Compilation does not replace runtime keyboard, accessibility, lifecycle, or browser QA.

## Cross-Platform Hosted Build Tests

`.github/workflows/cross-platform.yml` provides target-specific repository build evidence.

### Android shared integration

```bash
./gradlew :app:assembleDebug --stacktrace
```

This compiles the staged Android shared settings adapter and host integration in addition to the mature application.

### Web production distribution

```bash
./gradlew :sharedUI:wasmJsBrowserDistribution --stacktrace
```

This compiles Web `localStorage` active-game/settings adapters and creates the production Wasm distribution. It does not prove every intended browser/privacy mode.

### iOS Simulator framework

On macOS:

```bash
./gradlew :sharedUI:linkDebugFrameworkIosSimulatorArm64 --stacktrace
```

This compiles the `NSUserDefaults` adapters and links the shared framework. It does not prove a complete signed Xcode app, physical-device behavior, or App Store acceptance.

### Desktop application images

On Linux, Windows, and macOS:

```bash
./gradlew :sharedUI:createDistributable --stacktrace
```

This compiles Java `Preferences` active-game/settings adapters and validates host application-image creation. Native signing/notarization/clean-machine QA remain separate.

## Android JVM Tests

```bash
./gradlew :app:testDebugUnitTest --stacktrace
```

The mature Android JVM suite covers application-local codecs/models, settings/statistics calculations, learning-progress invariants, backup/transfer behavior, bounded file reading, and pure helpers where an Android runtime is unnecessary.

## Repository Guard Regression Tests

```bash
python -m unittest scripts.tests.test_verify_documentation_links
python -m unittest scripts.tests.test_verify_documentation_coverage
python -m unittest scripts.tests.test_verify_release_contract
python -m unittest scripts.tests.test_verify_release_outputs
python -m unittest scripts.tests.test_verify_release_cli_validation
python -m unittest scripts.tests.test_verify_translations
```

Then run the guards against the checked-out tree:

```bash
python scripts/verify_documentation_links.py
python scripts/verify_documentation_coverage.py
python scripts/verify_release_contract.py
python scripts/verify_no_secrets.py
python scripts/verify_translations.py
```

### Documentation coverage

Protects ownership/discoverability for Android, shared engine/UI, platform adapters, iOS host sources, Macrobenchmark, scripts/tests, workflows, Gradle/root files, and detailed documentation. New `SHARED_SETTINGS.md` and 2.0.14 release docs must remain indexed.

### Release contract

Protects synchronization among:

- Android source `2014 / 2.0.14` identity;
- Desktop `packageVersion 2.0.14`;
- ordinary Android CI expectations;
- protected production-validation defaults;
- min/target SDK and production application identity.

### Translation parity

Protects Android localization plus shared English/Hindi key and printf-placeholder parity, including 2.0.14 theme labels.

### Release-output verifier

Protects archive structure, output metadata, embedded APK application/version/SDK/debuggable identity, deterministic hashes, signature schemes/certificate identities, and CLI argument boundaries.

## Partial Signing Fail-Closed Regression

A configuration with only one signing variable must fail:

```bash
SUDOKUNOVA_KEY_ALIAS=partial-test ./gradlew :app:tasks --quiet
```

Never use real secrets just to test this guard.

## Android Instrumentation

Compile instrumentation APK:

```bash
./gradlew :app:assembleDebugAndroidTest --stacktrace
```

Run connected tests locally on a configured target:

```bash
./gradlew :app:connectedDebugAndroidTest --stacktrace
```

The hosted `instrumentation.yml` suite runs on API 35 and protects mature Android Compose/Room flows. Compilation alone is not connected evidence.

## Android Lint and Build Verification

```bash
./gradlew :app:lintDebug :app:lintRelease --stacktrace
./gradlew :app:assembleDebug :app:assembleRelease :app:bundleRelease --stacktrace
```

Ordinary CI then verifies exact `in.sanskar.sudokunova` / `2014` / `2.0.14` / minSdk 26 / targetSdk 37 / non-debuggable release identity and records hashes/evidence.

## Macrobenchmark

Compile:

```bash
./gradlew :macrobenchmark:assembleBenchmark --stacktrace
```

Representative physical-device execution:

```bash
./gradlew :macrobenchmark:connectedBenchmarkAndroidTest --stacktrace
```

Hosted-emulator or compile-only results must not be presented as representative production performance evidence.

## Shared Keyboard / Settings Runtime QA

Automated source tests and compile gates do not fully prove input/settings quality. Before production claims on a target, verify:

- arrow movement and board-edge behavior;
- digit `1`–`9` entry;
- `N` Notes toggle;
- `H` Hint;
- Backspace/Delete erase;
- no unacceptable keyboard/focus trap;
- visible and semantic selected state;
- System/Light/Dark theme application;
- theme persistence after restart;
- local settings behavior when storage is cleared/unavailable;
- Web private/privacy modes where intended;
- TalkBack/VoiceOver/browser/Desktop accessibility behavior;
- large-font, high-contrast, reduced-motion, resize, touch, and pointer behavior where applicable.

Record actual target/runtime evidence rather than inferring it from compilation.

## Exact-Head Merge Rule

PR #44 is merge-verified only after all required workflow families are green on one exact final SHA:

1. Android CI;
2. Android Instrumentation;
3. Cross-Platform CI.

A later documentation-only commit still changes the head and invalidates older runs as final evidence.

Do not weaken tests, remove targets, bypass validation, or mix results from multiple SHAs merely to obtain a green release line.

## Release Evidence Boundary

Even a fully green exact PR head proves repository preparation, not final public release readiness. Issue #43 remains the authority for protected signing, physical-device/accessibility/performance checks, platform runtime/distribution evidence, store validation, final `SHIP`, immutable `v2.0.14` tag, GitHub Release, and public distribution.
