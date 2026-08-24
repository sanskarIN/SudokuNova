# Building SudokuNova

This guide covers the current SudokuNova 2.0.12 repository, including the mature Android application and the Kotlin/Compose Multiplatform targets. For the detailed platform-support matrix and host-specific boundaries, also read [`CROSS_PLATFORM.md`](CROSS_PLATFORM.md).

## Required Tooling

Current repository configuration:

- JDK 17
- Gradle 9.5 via the committed wrapper
- Android Gradle Plugin 9.3.1
- Kotlin 2.4.10
- Compose Multiplatform 1.11.1
- Android compile/target SDK 37
- Android application min SDK 26
- Macrobenchmark test-module min SDK 29
- AndroidX Benchmark Macrobenchmark 1.4.1
- AndroidX ProfileInstaller 1.4.1

Use the committed Gradle wrapper rather than a globally installed Gradle version so local and CI builds use the same Gradle distribution.

Additional platform tooling:

- Android: Android SDK 37 and current command-line/build tools;
- iOS/iPadOS: macOS + Xcode for simulator/device hosting, signing, and distribution;
- Desktop native images/packages: a JDK containing `jpackage` on the host OS;
- Web: a modern WebAssembly-capable browser for runtime QA.

## Current 2.0.12 Android Release Contract

The active source/release line uses:

- application ID: `in.sanskar.sudokunova`
- version code: `2012`
- version name: `2.0.12`
- minimum SDK: `26`
- target SDK: `37`
- compile SDK: `37`

`V2_0_12_RELEASE.md` is the current Android release authority. Historical `1000 / 1.0.0-rc.1` metadata belongs to preserved v1 evidence and must not be used as the current build contract.

Cross-platform extraction does not change the Android production package/version contract unless a later release intentionally updates those values.

## Clone

```bash
git clone https://github.com/sanskarIN/SudokuNova.git
cd SudokuNova
```

Use `main` for the last merged state. When validating a pull request, checkout that exact PR head/branch and record its commit SHA; never reuse evidence from a different head.

## Gradle Sync

Android Studio/IntelliJ performs sync when opening the project. From the command line, a lightweight configuration check is:

```bash
./gradlew tasks
```

Windows PowerShell:

```powershell
.\gradlew.bat tasks
```

## Clean

```bash
./gradlew clean
```

Do not make clean builds the default development workflow. Use `clean` when stale generated state is actually suspected or when a release-evidence procedure explicitly requires a fresh build.

# Shared Multiplatform Builds

## Shared Sudoku Engine Tests

The engine is Kotlin Multiplatform, so the host-neutral JVM test task used by CI is now:

Linux/macOS:

```bash
./gradlew :sudoku-engine:desktopTest --stacktrace
```

Windows PowerShell:

```powershell
.\gradlew.bat :sudoku-engine:desktopTest --stacktrace
```

## Shared Gameplay-State Tests

```bash
./gradlew :sharedUI:desktopTest --stacktrace
```

Windows PowerShell:

```powershell
.\gradlew.bat :sharedUI:desktopTest --stacktrace
```

## Compile Shared Desktop and Web/Wasm Code

```bash
./gradlew :sharedUI:compileKotlinDesktop :sharedUI:compileKotlinWasmJs --stacktrace
```

This is a compile gate, not a substitute for host/runtime QA.

## Desktop Development Run

Linux/macOS:

```bash
./gradlew :sharedUI:run
```

Windows PowerShell:

```powershell
.\gradlew.bat :sharedUI:run
```

## Desktop Application Image

```bash
./gradlew :sharedUI:createDistributable --stacktrace
```

The output is under:

```text
sharedUI/build/compose/binaries/main/app/
```

Native package configuration declares Windows MSI, macOS DMG, and Linux DEB formats. Build platform-native packages on the corresponding host OS; code signing/notarization/distribution are separate external steps.

## Web/Wasm Production Distribution

Linux/macOS:

```bash
./gradlew :sharedUI:wasmJsBrowserDistribution --stacktrace
```

Windows PowerShell:

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

Use the URL printed by Gradle. A successful Wasm build does not by itself prove compatibility with every browser/device that may be targeted for publication.

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

`iosApp/` contains SwiftUI host sources. A production Apple application additionally needs a real Xcode application target/project, bundle identity, assets/capabilities, signing/provisioning, physical-device QA, and App Store validation. Framework compilation is not publication evidence.

# Android Application Builds

## Debug APK

Linux/macOS:

```bash
./gradlew :app:assembleDebug
```

Windows PowerShell:

```powershell
.\gradlew.bat :app:assembleDebug
```

Expected output:

```text
app/build/outputs/apk/debug/app-debug.apk
```

The debug build uses the `.debug` application-ID suffix and `-debug` version-name suffix.

The Android application consumes both `:sudoku-engine` and `:sharedUI`. `MainActivity` remains the mature production launcher; `CrossPlatformActivity` is a non-exported host used to validate the same shared UI surface used by other platforms.

## Release APK

```bash
./gradlew :app:assembleRelease
```

Windows PowerShell:

```powershell
.\gradlew.bat :app:assembleRelease
```

Expected output directory:

```text
app/build/outputs/apk/release/
```

Without production-signing environment values, ordinary local/CI verification intentionally produces an unsigned release APK such as:

```text
app/build/outputs/apk/release/app-release-unsigned.apk
```

The release build enables R8 code shrinking and resource shrinking.

## Android App Bundle

```bash
./gradlew :app:bundleRelease
```

Windows PowerShell:

```powershell
.\gradlew.bat :app:bundleRelease
```

Expected output:

```text
app/build/outputs/bundle/release/app-release.aab
```

A store-distributable production artifact must be signed through the protected release environment described in [`PRODUCTION_SIGNING.md`](PRODUCTION_SIGNING.md) and [`PRODUCTION_RELEASE_VALIDATION.md`](PRODUCTION_RELEASE_VALIDATION.md). Source control intentionally contains no production signing secret.

## Release Mapping Output

Preserve the generated mapping files with the exact source commit used for a minified release:

```text
app/build/outputs/mapping/release/
app/build/outputs/mapping/release/mapping.txt
```

Mapping output is needed to de-obfuscate matching production crash traces.

## Benchmark Build and Macrobenchmark Harness

SudokuNova has a release-like Android `benchmark` build type plus a separate `:macrobenchmark` Android test module.

The benchmark app variant:

- derives from production `release`;
- preserves R8/resource shrinking;
- remains non-debuggable;
- uses debug signing so measurement setup does not require production signing material;
- enables shell profiling only through `app/src/benchmark/AndroidManifest.xml`;
- leaves the production release manifest unchanged.

The app includes AndroidX ProfileInstaller 1.4.1 so Android Macrobenchmark tooling can perform supported profile/reset and shader-cache operations. This does **not** mean SudokuNova currently ships a project-generated Baseline Profile.

Compile the benchmark harness:

```bash
./gradlew :macrobenchmark:assembleBenchmark --stacktrace
```

Run the committed benchmark tests on a connected representative physical target:

```bash
./gradlew :macrobenchmark:connectedBenchmarkAndroidTest --stacktrace
```

Hosted-emulator timing must not be presented as production performance evidence. See [`PERFORMANCE_BENCHMARKING.md`](PERFORMANCE_BENCHMARKING.md).

# Android Production Signing and Release Verification

## Production Signing

SudokuNova supports optional Android release signing through four environment values:

- `SUDOKUNOVA_KEYSTORE_PATH`
- `SUDOKUNOVA_KEYSTORE_PASSWORD`
- `SUDOKUNOVA_KEY_ALIAS`
- `SUDOKUNOVA_KEY_PASSWORD`

Behavior is fail closed:

- none configured → unsigned CI-safe release build;
- all four configured → release signing enabled;
- one to three configured → Gradle configuration fails.

See [`PRODUCTION_SIGNING.md`](PRODUCTION_SIGNING.md) for secure examples and certificate verification.

## Protected Signed-Release Validation

`.github/workflows/release-validation.yml` is manually dispatched and intended for a restricted GitHub Environment named `production-release`.

Current protected-workflow defaults:

- version code `2012`;
- version name `2.0.12`;
- application ID `in.sanskar.sudokunova`;
- minimum SDK `26`;
- target SDK `37`.

When the environment is actually configured, the workflow can reconstruct a temporary keystore outside the repository, build signed APK/AAB outputs, validate application/version/SDK/debuggable identity, verify signatures and expected signer-certificate SHA-256 identities, retain non-secret evidence, and remove temporary signing material afterward.

Committed source does not prove that GitHub Environment reviewers, allowed refs, secrets, or production keys are configured. See [`PRODUCTION_RELEASE_VALIDATION.md`](PRODUCTION_RELEASE_VALIDATION.md).

## Release Contract Guard

```bash
python -m unittest scripts.tests.test_verify_release_contract
python scripts/verify_release_contract.py
```

The guard requires `app/build.gradle.kts`, ordinary Android CI, and protected production validation to agree on application ID, version code/name, minimum SDK, and target SDK. It rejects invalid numeric ordering, unsafe release names, duplicate/missing contract values, and production IDs ending in `.debug`.

## Documentation / Repository Guards

```bash
python -m unittest scripts.tests.test_verify_documentation_links
python -m unittest scripts.tests.test_verify_documentation_coverage
python scripts/verify_documentation_links.py
python scripts/verify_documentation_coverage.py
```

The coverage verifier reads `git ls-files -z`. Every tracked file—including `sharedUI/` and `iosApp/`—must have a maintained documentation owner, and every detailed `docs/*.md` guide must remain discoverable from `docs/README.md`.

Per-file audit:

```bash
python scripts/verify_documentation_coverage.py --verbose
```

## Release Artifact Verifier

`scripts/verify_release_outputs.py` validates non-empty APK/AAB/mapping outputs, ZIP structure/core entries, expected release metadata, embedded APK application/version/minSdk/targetSdk/debuggable identity, release `debuggable=false`, SHA-256/byte-size evidence, and optionally cryptographic signatures/certificate identities.

Verifier tests:

```bash
python -m unittest scripts.tests.test_verify_release_outputs
python -m unittest scripts.tests.test_verify_release_cli_validation
```

After unsigned 2.0.12 outputs exist:

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

PowerShell:

```powershell
python scripts/verify_release_outputs.py `
  --apk app/build/outputs/apk/release/app-release-unsigned.apk `
  --aab app/build/outputs/bundle/release/app-release.aab `
  --mapping app/build/outputs/mapping/release/mapping.txt `
  --metadata app/build/outputs/apk/release/output-metadata.json `
  --expected-version-code 2012 `
  --expected-version-name 2.0.12 `
  --expected-application-id in.sanskar.sudokunova `
  --require-apk-manifest `
  --expected-min-sdk 26 `
  --expected-target-sdk 37 `
  --output app/build/outputs/release-evidence/sha256.txt `
  --apk-identity-output app/build/outputs/release-evidence/apk-identity.txt
```

This unsigned path is build/artifact integrity evidence only. For the actual signed release, use certificate-bound verification documented in the signing/release authorities.

# Test and Quality Commands

## Android JVM Unit Tests

```bash
./gradlew :app:testDebugUnitTest
```

## Android Instrumentation-Test Compilation

```bash
./gradlew :app:assembleDebugAndroidTest
```

## Macrobenchmark-Test Compilation

```bash
./gradlew :macrobenchmark:assembleBenchmark
```

## Android Lint

```bash
./gradlew :app:lintDebug :app:lintRelease
```

## Repository Security Guard

```bash
python scripts/verify_no_secrets.py
```

## Translation Parity

```bash
python scripts/verify_translations.py
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
  --expected-version-code 2012 \
  --expected-version-name 2.0.12 \
  --expected-application-id in.sanskar.sudokunova \
  --require-apk-manifest \
  --expected-min-sdk 26 \
  --expected-target-sdk 37 \
  --output app/build/outputs/release-evidence/sha256.txt \
  --apk-identity-output app/build/outputs/release-evidence/apk-identity.txt
```

Windows PowerShell:

```powershell
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
.\gradlew.bat `
  :sudoku-engine:desktopTest `
  :sharedUI:desktopTest `
  :sharedUI:compileKotlinDesktop `
  :sharedUI:compileKotlinWasmJs `
  :app:testDebugUnitTest `
  :app:assembleDebugAndroidTest `
  :macrobenchmark:assembleBenchmark `
  :app:lintDebug `
  :app:lintRelease `
  :app:assembleDebug `
  :app:assembleRelease `
  :app:bundleRelease `
  --stacktrace
```

API-35 connected Android tests, iOS framework validation on macOS, Desktop application-image builds on their host OSes, Web runtime/browser QA, and representative physical-device Macrobenchmark evidence remain separate gates.

## Partial-Signing Fail-Closed Regression

Bash-compatible shell:

```bash
SUDOKUNOVA_KEY_ALIAS=partial-test ./gradlew :app:tasks --quiet
```

This should fail with the repository partial-signing configuration error. Never set real secret values merely to test the guard.

## Install Debug APK with ADB

```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

For a signed release installation, use the exact signed artifact and record manual evidence in [`V2_0_12_RELEASE.md`](V2_0_12_RELEASE.md).

# CI Build Contract

## Android CI

`.github/workflows/ci.yml` verifies:

1. Android SDK `apkanalyzer` discovery;
2. repository security guard;
3. release-verifier unit/CLI tests;
4. documentation/release-contract regression tests;
5. partial release-signing fail-closed behavior;
6. direct documentation/release-contract guards;
7. English/Hindi translation parity;
8. KMP Sudoku engine Desktop tests;
9. shared gameplay-state Desktop tests;
10. shared Desktop and Web/Wasm compilation;
11. Android JVM unit tests;
12. Android instrumentation-test compilation;
13. Macrobenchmark harness compilation;
14. debug/release lint;
15. debug APK;
16. R8/resource-shrunk release APK;
17. release AAB;
18. exact 2012/2.0.12 package/version/SDK/debuggable verification;
19. SHA-256/APK-identity evidence;
20. short-lived reports and release outputs.

## Cross-Platform CI

`.github/workflows/cross-platform.yml` separately validates repository-build targets across hosted runners:

- shared engine tests;
- shared gameplay-state tests;
- shared Desktop/Web compilation;
- Android debug integration;
- Web production distribution;
- iOS Simulator framework linking on macOS;
- Desktop application images on Linux, Windows, and macOS.

## Connected Android Instrumentation

`.github/workflows/instrumentation.yml` runs the API-35 connected Compose/Room suite.

## Protected Production Validation

`.github/workflows/release-validation.yml` performs protected signed Android release verification only after deliberate maintainer dispatch in a correctly configured `production-release` environment.

CI compilation/package artifacts are evidence for the exact workflow head and target tested. They do not automatically prove production signing, physical-device behavior, notarization, App Store/Play acceptance, Windows reputation/signing, Linux distribution compatibility, or broad browser compatibility.

# Gradle Wrapper Verification

The wrapper is pinned through `gradle/wrapper/gradle-wrapper.properties`; GitHub's Gradle setup action validates wrapper JARs in CI.

For reproducible investigation, record at minimum:

- exact Git commit SHA;
- host OS/platform target;
- Java version;
- Gradle wrapper version;
- Android SDK/Build Tools or Xcode/browser environment when relevant;
- command executed;
- workflow run ID when CI was used;
- artifact SHA-256/identity evidence when outputs were produced;
- physical device/OS and raw benchmark output when performance evidence is collected.

# Android Build Types

## Debug

- development build;
- `.debug` application-ID suffix;
- `-debug` version-name suffix;
- intended for local/CI testing.

## Benchmark

- initialized from release;
- minification/resource shrinking inherited from release;
- non-debuggable target app;
- debug signed for local performance measurement;
- shell-profileable only through the benchmark source-set manifest;
- target of `:macrobenchmark`;
- not a production-distribution artifact.

## Release

- minification/resource shrinking enabled;
- optimized default ProGuard rules plus `app/proguard-rules.pro`;
- must pass release lint, R8, AAB, and output verification for the current release gate;
- may remain unsigned for ordinary CI verification;
- uses secret-backed signing only when all required signing values exist.

# Release-Signing Rules

- Never commit `.jks`, `.keystore`, `.p12`, `.pfx`, PEM private keys, or signing passwords.
- Never embed signing secrets in Gradle files, `local.properties`, resources, code, or workflow YAML.
- Ordinary pull-request workflows must not receive production signing secrets.
- Signing is all-or-nothing and fails closed when partial.
- Protected secrets should be scoped to the minimum practical workflow steps.
- Missing/failed production signing must never be bypassed by committing credentials.

# Common Failure Checks

1. Confirm Java 17 and the committed Gradle wrapper.
2. Confirm the Android SDK/Xcode/browser tooling needed for the target being built.
3. Confirm the exact branch and commit under investigation.
4. Read the first real Gradle/Kotlin/compiler/test/lint/R8/verifier error, not only the final stack trace.
5. If KMP configuration fails, inspect `sudoku-engine/build.gradle.kts`, `sharedUI/build.gradle.kts`, the plugin versions, and target-specific DSL before changing unrelated platform jobs.
6. If shared Desktop/Web compilation fails, isolate the exact source set/target rather than removing the target from CI.
7. If an iOS framework fails, validate on macOS/Xcode and the relevant Kotlin/Native target; do not claim Apple support from an Android/Ubuntu build.
8. If a Desktop image fails, inspect `jpackage`/host packaging support on that OS.
9. If Android release fails, check accidental partial `SUDOKUNOVA_*` environment values, R8 diagnostics, output metadata, and exact 2012/2.0.12 contract.
10. If documentation coverage fails, document/index the new path instead of adding an unrelated catch-all ownership rule.
11. Check [`TROUBLESHOOTING.md`](TROUBLESHOOTING.md), [`CROSS_PLATFORM.md`](CROSS_PLATFORM.md), and CI logs.

# Evidence Policy

A release-quality or platform-support statement must be tied to actual evidence. Source configuration may describe repository build targets, but it must not claim physical-device coverage, accessibility success, production performance numbers, Android production signing/certificate identity, Apple signing/provisioning, notarization, Windows signing/reputation, store acceptance, repository administration, tag/GitHub Release creation, or public distribution unless those actions/checks were actually performed and recorded.
