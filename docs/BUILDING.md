# Building SudokuNova

This guide covers the current SudokuNova 2.0.13 repository, including the mature Android application and Kotlin/Compose Multiplatform targets. For the detailed platform-support matrix and evidence boundaries, also read [`CROSS_PLATFORM.md`](CROSS_PLATFORM.md).

## Required Tooling

Current repository configuration:

- JDK 17
- Gradle 9.5 via the committed wrapper
- Android Gradle Plugin 9.3.1
- Kotlin 2.4.10
- Compose Multiplatform 1.11.1
- Room 2.8.4 for mature Android persistence
- Android compile/target SDK 37
- Android application min SDK 26
- Macrobenchmark test-module min SDK 29
- AndroidX Benchmark Macrobenchmark 1.4.1
- AndroidX ProfileInstaller 1.4.1

Use the committed Gradle wrapper so local and CI builds use the same Gradle distribution.

Additional platform tooling:

- Android: Android SDK 37 and current command-line/build tools;
- iOS/iPadOS: macOS + Xcode for simulator/device hosting, signing, and distribution;
- Desktop native images/packages: a JDK containing `jpackage` on the host OS;
- Web: a modern WebAssembly-capable browser for runtime QA.

## Current 2.0.13 Release Contract

```text
applicationId          = in.sanskar.sudokunova
Android versionCode    = 2013
Android versionName    = 2.0.13
Desktop packageVersion = 2.0.13
minSdk                 = 26
targetSdk              = 37
compileSdk             = 37
```

[`V2_0_13_RELEASE.md`](V2_0_13_RELEASE.md) is the current release authority. `V2_0_12_RELEASE.md` and older release documents remain historical evidence.

The release-contract guard requires the Android version name and Desktop package version to remain synchronized in addition to ordinary/protected Android CI identity expectations.

## Clone

```bash
git clone https://github.com/sanskarIN/SudokuNova.git
cd SudokuNova
```

Use `main` for the last merged state. When validating a pull request, checkout the exact PR head/branch and record its commit SHA; never reuse evidence from another head.

## Basic Gradle Commands

Configuration check:

```bash
./gradlew tasks
```

Windows PowerShell:

```powershell
.\gradlew.bat tasks
```

Clean only when stale generated state is suspected or a release-evidence procedure explicitly requires it:

```bash
./gradlew clean
```

# Shared Multiplatform Builds

## Shared Engine Tests

Linux/macOS:

```bash
./gradlew :sudoku-engine:desktopTest --stacktrace
```

Windows PowerShell:

```powershell
.\gradlew.bat :sudoku-engine:desktopTest --stacktrace
```

The suite includes Classic Sudoku correctness, generation/solver/difficulty/teaching tests, the fixed `SNP1` compatibility vector, and `PuzzleExchangeService` unique-solution acceptance.

## Shared Gameplay/Persistence Tests

```bash
./gradlew :sharedUI:desktopTest --stacktrace
```

Coverage includes gameplay state, deterministic grid navigation, typed statuses, notes/undo/reset/hints, `SNG1` serialization, persistence/store wiring, and fail-closed restoration.

## Compile Shared Desktop and Web/Wasm

```bash
./gradlew :sharedUI:compileKotlinDesktop :sharedUI:compileKotlinWasmJs --stacktrace
```

This compile gate protects common input/resource/semantics code across representative targets; it does not replace runtime QA.

## Desktop Development Run

```bash
./gradlew :sharedUI:run
```

Windows:

```powershell
.\gradlew.bat :sharedUI:run
```

## Desktop Application Image

```bash
./gradlew :sharedUI:createDistributable --stacktrace
```

Output is under:

```text
sharedUI/build/compose/binaries/main/app/
```

Native distribution configuration declares Windows MSI, macOS DMG, and Linux DEB formats, with package version `2.0.13`. Build native packages on their corresponding host OS. Code signing/notarization/reputation/distribution are separate external evidence.

## Web/Wasm Production Distribution

```bash
./gradlew :sharedUI:wasmJsBrowserDistribution --stacktrace
```

Windows:

```powershell
.\gradlew.bat :sharedUI:wasmJsBrowserDistribution --stacktrace
```

Output:

```text
sharedUI/build/dist/wasmJs/productionExecutable/
```

Development server:

```bash
./gradlew :sharedUI:wasmJsBrowserDevelopmentRun
```

A successful build does not prove the intended browser/device matrix, focus/accessibility behavior, or privacy-mode persistence.

## iOS Simulator Framework

On macOS:

```bash
./gradlew :sharedUI:linkDebugFrameworkIosSimulatorArm64 --stacktrace
```

Output:

```text
sharedUI/build/bin/iosSimulatorArm64/debugFramework/SudokuNovaSharedUI.framework
```

## iOS Device Framework

On macOS:

```bash
./gradlew :sharedUI:linkReleaseFrameworkIosArm64 --stacktrace
```

Output:

```text
sharedUI/build/bin/iosArm64/releaseFramework/SudokuNovaSharedUI.framework
```

`iosApp/` contains SwiftUI host sources. A production Apple application additionally needs a real/reproducible Xcode application host, final identity/deployment/assets, signing/provisioning, physical-device QA, and App Store validation. Framework compilation is not publication evidence.

# Android Application Builds

## Debug APK

```bash
./gradlew :app:assembleDebug --stacktrace
```

Windows:

```powershell
.\gradlew.bat :app:assembleDebug --stacktrace
```

Expected output:

```text
app/build/outputs/apk/debug/app-debug.apk
```

The debug build uses `.debug` application-ID and `-debug` version-name suffixes.

The Android app consumes `:sudoku-engine` and `:sharedUI`. `MainActivity` remains the mature launcher; `CrossPlatformActivity` is non-exported and validates the shared host surface.

## Release APK

```bash
./gradlew :app:assembleRelease --stacktrace
```

Expected output directory:

```text
app/build/outputs/apk/release/
```

Without production-signing environment values, local/ordinary CI verification intentionally produces an unsigned release APK such as:

```text
app/build/outputs/apk/release/app-release-unsigned.apk
```

Release enables R8 code shrinking and resource shrinking.

## Android App Bundle

```bash
./gradlew :app:bundleRelease --stacktrace
```

Expected output:

```text
app/build/outputs/bundle/release/app-release.aab
```

A production artifact must use the protected signing process in [`PRODUCTION_SIGNING.md`](PRODUCTION_SIGNING.md) and [`PRODUCTION_RELEASE_VALIDATION.md`](PRODUCTION_RELEASE_VALIDATION.md). Source control intentionally contains no production signing secret.

## R8 Mapping

Preserve mapping output with the exact release commit:

```text
app/build/outputs/mapping/release/mapping.txt
```

## Benchmark Build and Macrobenchmark

Compile the release-like benchmark harness:

```bash
./gradlew :macrobenchmark:assembleBenchmark --stacktrace
```

Run committed benchmark tests on a representative physical target:

```bash
./gradlew :macrobenchmark:connectedBenchmarkAndroidTest --stacktrace
```

Hosted-emulator timing must not be presented as production performance evidence. See [`PERFORMANCE_BENCHMARKING.md`](PERFORMANCE_BENCHMARKING.md).

# Production Signing and Release Verification

## Production Signing

Optional Android release signing uses exactly four environment values:

- `SUDOKUNOVA_KEYSTORE_PATH`
- `SUDOKUNOVA_KEYSTORE_PASSWORD`
- `SUDOKUNOVA_KEY_ALIAS`
- `SUDOKUNOVA_KEY_PASSWORD`

Behavior is fail closed:

- none configured → unsigned CI-safe release build;
- all four configured → release signing enabled;
- one to three configured → Gradle configuration fails.

See [`PRODUCTION_SIGNING.md`](PRODUCTION_SIGNING.md).

## Protected Signed-Release Validation

`.github/workflows/release-validation.yml` is manually dispatched through the protected `production-release` GitHub Environment.

Current workflow defaults:

- version code `2013`;
- version name `2.0.13`;
- application ID `in.sanskar.sudokunova`;
- minimum SDK `26`;
- target SDK `37`.

When the external environment is actually configured, the workflow can reconstruct a temporary keystore outside the repository, build signed R8 APK/AAB outputs, verify embedded application/version/SDK/debuggable identity, verify APK/AAB signatures and protected certificate fingerprints, retain non-secret evidence, and remove the temporary keystore.

Committed workflow source does not prove environment reviewers, allowed refs, secrets, keys, or a successful production run.

## Release Contract Guard

```bash
python -m unittest scripts.tests.test_verify_release_contract
python scripts/verify_release_contract.py
```

The guard requires these current sources to agree:

- `app/build.gradle.kts` Android application/version/SDK identity;
- `sharedUI/build.gradle.kts` Desktop `packageVersion` = Android `versionName`;
- ordinary Android CI expected application/version/SDK identity;
- protected release-validation defaults and immutable application/SDK expectations.

It rejects duplicate/missing values, unsafe release names, invalid SDK ordering, production IDs ending in `.debug`, and Desktop/Android version drift.

## Repository Documentation Guards

```bash
python -m unittest scripts.tests.test_verify_documentation_links
python -m unittest scripts.tests.test_verify_documentation_coverage
python scripts/verify_documentation_links.py
python scripts/verify_documentation_coverage.py
```

Every tracked path must have a maintained documentation owner, and every detailed `docs/*.md` guide must remain indexed from `docs/README.md`.

## Translation Guard

```bash
python -m unittest scripts.tests.test_verify_translations
python scripts/verify_translations.py
```

This covers the established Android localization scope and full shared English/Hindi key/placeholder parity.

## Release Artifact Verifier

After unsigned 2.0.13 outputs exist:

```bash
python scripts/verify_release_outputs.py \
  --apk app/build/outputs/apk/release/app-release-unsigned.apk \
  --aab app/build/outputs/bundle/release/app-release.aab \
  --mapping app/build/outputs/mapping/release/mapping.txt \
  --metadata app/build/outputs/apk/release/output-metadata.json \
  --expected-version-code 2013 \
  --expected-version-name 2.0.13 \
  --expected-application-id in.sanskar.sudokunova \
  --require-apk-manifest \
  --expected-min-sdk 26 \
  --expected-target-sdk 37 \
  --output app/build/outputs/release-evidence/sha256.txt \
  --apk-identity-output app/build/outputs/release-evidence/apk-identity.txt
```

PowerShell:

```powershell
python scripts/verify_release_outputs.py `
  --apk app/build/outputs/apk/release/app-release-unsigned.apk `
  --aab app/build/outputs/bundle/release/app-release.aab `
  --mapping app/build/outputs/mapping/release/mapping.txt `
  --metadata app/build/outputs/apk/release/output-metadata.json `
  --expected-version-code 2013 `
  --expected-version-name 2.0.13 `
  --expected-application-id in.sanskar.sudokunova `
  --require-apk-manifest `
  --expected-min-sdk 26 `
  --expected-target-sdk 37 `
  --output app/build/outputs/release-evidence/sha256.txt `
  --apk-identity-output app/build/outputs/release-evidence/apk-identity.txt
```

This unsigned path is build/artifact integrity evidence only. Use the certificate-bound protected workflow for real production signing validation.

# Test and Quality Commands

Android JVM unit tests:

```bash
./gradlew :app:testDebugUnitTest --stacktrace
```

Android instrumentation-test compilation:

```bash
./gradlew :app:assembleDebugAndroidTest --stacktrace
```

Macrobenchmark compilation:

```bash
./gradlew :macrobenchmark:assembleBenchmark --stacktrace
```

Android lint:

```bash
./gradlew :app:lintDebug :app:lintRelease --stacktrace
```

Repository security guard:

```bash
python scripts/verify_no_secrets.py
```

# Full Local Non-Connected Verification

Linux/macOS:

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
python scripts/verify_release_outputs.py \
  --apk app/build/outputs/apk/release/app-release-unsigned.apk \
  --aab app/build/outputs/bundle/release/app-release.aab \
  --mapping app/build/outputs/mapping/release/mapping.txt \
  --metadata app/build/outputs/apk/release/output-metadata.json \
  --expected-version-code 2013 \
  --expected-version-name 2.0.13 \
  --expected-application-id in.sanskar.sudokunova \
  --require-apk-manifest \
  --expected-min-sdk 26 \
  --expected-target-sdk 37 \
  --output app/build/outputs/release-evidence/sha256.txt \
  --apk-identity-output app/build/outputs/release-evidence/apk-identity.txt
```

Windows PowerShell uses the same Python commands and `./gradlew` tasks through `.\gradlew.bat`.

API-35 connected tests, iOS framework validation on macOS, Desktop images on their host OSes, Web runtime/browser QA, and representative physical-device Macrobenchmark evidence remain separate gates.

## Partial-Signing Fail-Closed Regression

A Bash-compatible environment containing only one signing variable should fail configuration:

```bash
SUDOKUNOVA_KEY_ALIAS=partial-test ./gradlew :app:tasks --quiet
```

Never use real secrets merely to test this guard.

## Install Debug APK

```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

For signed 2.0.13 release installation, use the exact signed artifact and record evidence under [`V2_0_13_RELEASE.md`](V2_0_13_RELEASE.md).

# CI Build Contract

## Android CI

`.github/workflows/ci.yml` verifies repository guards, shared engine/UI tests/compile, Android unit/test-APK/Macrobenchmark compilation, lint, debug APK, R8 release APK, AAB, and exact 2013/2.0.13 package/version/SDK/debuggable evidence.

## Cross-Platform CI

`.github/workflows/cross-platform.yml` validates:

- shared engine/gameplay/persistence tests;
- shared Desktop/Web compilation;
- Android debug integration;
- Web production distribution;
- iOS Simulator framework linking on macOS;
- Desktop application images on Linux, Windows, and macOS.

## Android Instrumentation

`.github/workflows/instrumentation.yml` runs the API-35 connected Compose/Room suite.

## Protected Production Validation

`.github/workflows/release-validation.yml` performs protected signed Android release verification only after deliberate maintainer dispatch in a correctly configured `production-release` environment.

All workflow evidence applies only to the exact head SHA tested.

# Android Build Types

## Debug

- `.debug` application-ID suffix;
- `-debug` version-name suffix;
- development/local/CI testing.

## Benchmark

- initialized from release;
- R8/resource shrinking inherited;
- non-debuggable target app;
- debug signed for local measurement;
- shell-profileable only through benchmark source-set manifest;
- not a production artifact.

## Release

- minification/resource shrinking enabled;
- optimized default ProGuard rules plus `app/proguard-rules.pro`;
- must pass release lint/R8/AAB/output verification;
- may remain unsigned in ordinary CI;
- uses production signing only when all required secret-backed values are present.

# Release-Signing Rules

- Never commit keystores, private keys, certificates containing private material, or signing passwords.
- Never embed signing secrets in Gradle, `local.properties`, resources, source, or workflow YAML.
- Ordinary PR workflows must not receive production signing secrets.
- Signing is all-or-nothing and fails closed when partially configured.
- Missing/failed production signing must never be bypassed by committing credentials.

# Common Failure Checks

1. Confirm Java 17 and the committed Gradle wrapper.
2. Confirm target-specific Android SDK/Xcode/browser/`jpackage` tooling.
3. Confirm exact branch/commit under investigation.
4. Read the first real Gradle/Kotlin/compiler/test/lint/R8/verifier error.
5. For KMP configuration/compile failures, fix the shared source/configuration rather than removing target jobs.
6. For iOS linking failures, validate on macOS/Xcode and the relevant Kotlin/Native target.
7. For Desktop packaging failures, inspect host `jpackage`/packaging support.
8. For Android release failures, check partial `SUDOKUNOVA_*` environment state, R8 diagnostics, output metadata, and exact 2013/2.0.13 contract.
9. For release-contract failures, check Android metadata, Desktop `packageVersion`, ordinary CI, and protected workflow defaults together.
10. For documentation coverage failures, document/index the new path instead of creating an unrelated catch-all rule.
11. Check [`TROUBLESHOOTING.md`](TROUBLESHOOTING.md), [`CROSS_PLATFORM.md`](CROSS_PLATFORM.md), and CI logs.

# Evidence Policy

A release-quality or platform-support statement must be tied to actual evidence. Source/build configuration may describe repository targets, but it must not claim physical-device coverage, accessibility success, production performance, Android production certificate identity, Apple signing/provisioning, notarization, Windows signing/reputation, store acceptance, repository administration, tag/GitHub Release creation, or public distribution unless those actions/checks were actually performed and recorded.
