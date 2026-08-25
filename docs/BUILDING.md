# Building SudokuNova

This guide covers the SudokuNova 2.0.14 repository, including the mature Android application and Kotlin/Compose Multiplatform targets. For the detailed platform-support matrix and evidence boundaries, also read [`CROSS_PLATFORM.md`](CROSS_PLATFORM.md).

## Required Tooling

Current repository configuration:

- JDK 17;
- Gradle 9.5 through the committed wrapper;
- Android Gradle Plugin 9.3.1;
- Kotlin 2.4.10;
- Compose Multiplatform 1.11.1;
- Room 2.8.4 for mature Android persistence;
- Android compile/target SDK 37;
- Android application min SDK 26;
- Macrobenchmark test-module min SDK 29.

Use the committed Gradle wrapper so local and CI builds use the same Gradle distribution.

Additional platform tooling:

- Android: Android SDK 37 and current command-line/build tools;
- iOS/iPadOS: macOS + Xcode for simulator/device hosting, signing, and distribution;
- Desktop native images/packages: a JDK containing `jpackage` on the host OS;
- Web: a modern WebAssembly-capable browser for runtime QA.

## Current 2.0.14 Source Contract

```text
applicationId          = in.sanskar.sudokunova
Android versionCode    = 2014
Android versionName    = 2.0.14
Desktop packageVersion = 2.0.14
minSdk                 = 26
targetSdk              = 37
compileSdk             = 37
```

[`V2_0_14_RELEASE.md`](V2_0_14_RELEASE.md) is the current release-preparation authority. Older release documents remain historical evidence.

The release-contract guard requires Android source identity, ordinary Android CI expectations, protected production-validation defaults, and Desktop `packageVersion` to remain synchronized.

## Clone and Configuration Check

```bash
git clone https://github.com/sanskarIN/SudokuNova.git
cd SudokuNova
./gradlew tasks
```

Windows PowerShell:

```powershell
.\gradlew.bat tasks
```

Use `main` for the last merged state. When validating PR #44 or another pull request, checkout the exact head being evaluated and record its SHA. Never reuse evidence from another head.

## Shared Multiplatform Tests

### Sudoku engine

```bash
./gradlew :sudoku-engine:desktopTest --stacktrace
```

The engine suite protects board/solver/generator/difficulty/teaching/hints/practice behavior, the fixed `SNP1` compatibility vector, and unique-solution `PuzzleExchangeService` acceptance.

### Shared gameplay, active-game persistence, and settings

```bash
./gradlew :sharedUI:desktopTest --stacktrace
```

Coverage includes gameplay state, deterministic grid navigation, notes/undo/reset/hints, `SNG1` active-game serialization/store behavior, `SNS1` settings serialization/store/state behavior, and fail-closed restoration.

### Compile shared Desktop and Web/Wasm

```bash
./gradlew :sharedUI:compileKotlinDesktop :sharedUI:compileKotlinWasmJs --stacktrace
```

This protects portable Compose/resource/input/settings APIs across representative targets. Compilation is not runtime accessibility, focus, lifecycle, or browser evidence.

## Desktop

Development run:

```bash
./gradlew :sharedUI:run
```

Application image:

```bash
./gradlew :sharedUI:createDistributable --stacktrace
```

Native distribution configuration declares MSI on Windows, DMG on macOS, and DEB on Linux with:

```text
packageVersion = 2.0.14
```

Build native packages on the corresponding host. Code signing, notarization, reputation, and clean-machine install/upgrade QA remain external evidence.

## Web/Wasm

Production distribution:

```bash
./gradlew :sharedUI:wasmJsBrowserDistribution --stacktrace
```

Development server:

```bash
./gradlew :sharedUI:wasmJsBrowserDevelopmentRun
```

Production output is under:

```text
sharedUI/build/dist/wasmJs/productionExecutable/
```

A successful build does not prove the intended browser matrix, accessibility/focus behavior, or `localStorage` behavior in private/privacy modes.

## iOS/iPadOS Frameworks

Simulator framework on macOS:

```bash
./gradlew :sharedUI:linkDebugFrameworkIosSimulatorArm64 --stacktrace
```

Expected output:

```text
sharedUI/build/bin/iosSimulatorArm64/debugFramework/SudokuNovaSharedUI.framework
```

Device framework:

```bash
./gradlew :sharedUI:linkReleaseFrameworkIosArm64 --stacktrace
```

Expected output:

```text
sharedUI/build/bin/iosArm64/releaseFramework/SudokuNovaSharedUI.framework
```

`iosApp/` contains SwiftUI host sources. A production Apple application additionally needs a real/reproducible Xcode application host, final identity/deployment/assets, signing/provisioning, physical-device QA, and App Store validation.

## Android Builds

Debug APK:

```bash
./gradlew :app:assembleDebug --stacktrace
```

Output:

```text
app/build/outputs/apk/debug/app-debug.apk
```

The debug build uses `.debug` application-ID and `-debug` version-name suffixes.

Release APK and AAB:

```bash
./gradlew :app:assembleRelease :app:bundleRelease --stacktrace
```

Without all production-signing environment values, ordinary/local verification intentionally produces an unsigned release APK. Release builds retain R8 and resource shrinking.

Preserve:

```text
app/build/outputs/apk/release/
app/build/outputs/bundle/release/app-release.aab
app/build/outputs/mapping/release/mapping.txt
```

## Production Signing

Optional Android release signing uses exactly four environment values:

- `SUDOKUNOVA_KEYSTORE_PATH`;
- `SUDOKUNOVA_KEYSTORE_PASSWORD`;
- `SUDOKUNOVA_KEY_ALIAS`;
- `SUDOKUNOVA_KEY_PASSWORD`.

Behavior is fail closed:

- none configured → unsigned CI-safe release build;
- all four configured → signing enabled;
- one to three configured → Gradle configuration fails.

See [`PRODUCTION_SIGNING.md`](PRODUCTION_SIGNING.md).

## Protected Production Release Validation

`.github/workflows/release-validation.yml` is manually dispatched through the protected `production-release` GitHub Environment.

Current source defaults:

```text
versionCode = 2014
versionName = 2.0.14
applicationId = in.sanskar.sudokunova
minSdk = 26
targetSdk = 37
```

When the external environment is actually configured, the workflow can build signed R8 APK/AAB outputs, validate embedded package/version/SDK/debuggable identity, require expected signature/certificate identity, record hashes/evidence, and remove temporary signing material.

Committed workflow source does not prove that protected secrets, reviewers, allowed refs, or a successful protected run exist.

## Repository Guards

Run:

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
```

The documentation guard fails closed when a tracked path has no documentation owner or a detailed guide is not indexed from `docs/README.md`.

## 2.0.14 Release Artifact Verification

After unsigned outputs exist:

```bash
python scripts/verify_release_outputs.py \
  --apk app/build/outputs/apk/release/app-release-unsigned.apk \
  --aab app/build/outputs/bundle/release/app-release.aab \
  --mapping app/build/outputs/mapping/release/mapping.txt \
  --metadata app/build/outputs/apk/release/output-metadata.json \
  --expected-version-code 2014 \
  --expected-version-name 2.0.14 \
  --expected-application-id in.sanskar.sudokunova \
  --require-apk-manifest \
  --expected-min-sdk 26 \
  --expected-target-sdk 37 \
  --output app/build/outputs/release-evidence/sha256.txt \
  --apk-identity-output app/build/outputs/release-evidence/apk-identity.txt
```

This unsigned path is build/artifact-integrity evidence only. Real production signing requires the protected certificate-bound workflow.

## Android Test and Quality Tasks

```bash
./gradlew :app:testDebugUnitTest --stacktrace
./gradlew :app:assembleDebugAndroidTest --stacktrace
./gradlew :macrobenchmark:assembleBenchmark --stacktrace
./gradlew :app:lintDebug :app:lintRelease --stacktrace
```

Representative physical-device performance measurement:

```bash
./gradlew :macrobenchmark:connectedBenchmarkAndroidTest --stacktrace
```

Hosted-emulator timing must not be described as representative production performance evidence.

## Full Local Non-Connected Verification

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

Then run connected/platform-native/runtime checks on the required targets.

## CI Build Contract

The final PR #44 head must pass on one exact SHA:

1. **Android CI** — repository guards, shared tests/compile, Android unit/test-APK/Macrobenchmark compilation, lint, debug/release/R8/AAB builds, and exact 2014/2.0.14 identity evidence;
2. **Android Instrumentation** — API-35 connected Compose/Room suite;
3. **Cross-Platform CI** — shared tests, Android shared integration, Web production distribution, iOS Simulator framework, and Desktop application images on Linux/Windows/macOS.

Any later source or documentation commit invalidates older runs as final evidence.

## Evidence Boundary

Repository build success does not itself prove:

- Android production certificate identity;
- physical-device lifecycle/accessibility/performance quality;
- target settings persistence across real restarts/privacy modes;
- Apple signing/provisioning/App Store acceptance;
- macOS signing/notarization;
- Windows signing/reputation/clean-machine install;
- Linux clean install/upgrade/remove;
- intended browser/device compatibility;
- store acceptance, final `SHIP`, tagging, or publication.

Record those only after the corresponding real checks occur. See [`V2_0_14_RELEASE.md`](V2_0_14_RELEASE.md) and issue #43.
