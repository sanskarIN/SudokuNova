# Building SudokuNova

## Required Tooling

Current repository configuration:

- JDK 17
- Gradle 9.5.0
- Android Gradle Plugin 9.3.0
- Kotlin 2.4.10
- Android compile/target SDK 37
- Android min SDK 26

Use the committed Gradle wrapper rather than a globally installed Gradle version so local and CI builds use the same Gradle distribution.

## Clone

```bash
git clone https://github.com/sanskarIN/SudokuNova.git
cd SudokuNova
```

## Gradle Sync

Android Studio performs sync when the project opens. From the command line, any Gradle task will configure the build. A lightweight check is:

```bash
./gradlew tasks
```

Windows:

```bat
gradlew.bat tasks
```

## Clean

```bash
./gradlew clean
```

Do not make clean builds the default development workflow. Use `clean` when stale generated state is actually suspected.

## Debug APK

Linux/macOS:

```bash
./gradlew :app:assembleDebug
```

Windows:

```bat
gradlew.bat :app:assembleDebug
```

Expected output:

```text
app/build/outputs/apk/debug/app-debug.apk
```

The debug build uses the `.debug` application-ID suffix and `-debug` version-name suffix.

## Release APK

Compile and shrink the release APK with:

```bash
./gradlew :app:assembleRelease
```

Windows:

```bat
gradlew.bat :app:assembleRelease
```

Expected output directory:

```text
app/build/outputs/apk/release/
```

The release build enables R8 code shrinking and resource shrinking. CI deliberately verifies that the release variant can pass R8 without depending on repository-committed signing credentials.

A distributable production APK must be signed outside version control with protected release credentials. Never commit keystores, signing passwords, private keys, service-account credentials, or generated credential files.

## Android App Bundle (AAB)

Linux/macOS:

```bash
./gradlew :app:bundleRelease
```

Windows:

```bat
gradlew.bat :app:bundleRelease
```

Expected output:

```text
app/build/outputs/bundle/release/app-release.aab
```

Google Play publication requires a correctly signed release configured by the release operator or CI environment. Repository source control intentionally contains no production signing secret.

## Release Mapping Output

When minification succeeds, preserve the generated release mapping files together with the exact source commit used for the build:

```text
app/build/outputs/mapping/release/
```

These files are needed to de-obfuscate production crash traces for the matching release. Do not publish sensitive diagnostic data merely because mapping files exist.

## Unit Tests

```bash
./gradlew :sudoku-engine:test
./gradlew :app:testDebugUnitTest
```

Windows:

```bat
gradlew.bat :sudoku-engine:test
gradlew.bat :app:testDebugUnitTest
```

## Android Instrumentation-Test Compilation

```bash
./gradlew :app:assembleDebugAndroidTest
```

This verifies connected-test sources compile before an emulator/device run.

## Android Lint

Debug and release lint should both pass for v0.9 release hardening:

```bash
./gradlew :app:lintDebug :app:lintRelease
```

## Translation Parity

English/Hindi resource parity is a required release gate:

```bash
python scripts/verify_translations.py
```

## Full v0.9 Local Verification

Linux/macOS:

```bash
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

Windows PowerShell:

```powershell
python scripts/verify_translations.py
.\gradlew.bat :sudoku-engine:test `
  :app:testDebugUnitTest `
  :app:assembleDebugAndroidTest `
  :app:lintDebug `
  :app:lintRelease `
  :app:assembleDebug `
  :app:assembleRelease `
  :app:bundleRelease `
  --stacktrace
```

This is the standard non-connected release-hardening command set. API-35 connected tests remain a separate emulator/device gate.

## Install Debug APK with ADB

```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

## CI Release Verification

`.github/workflows/ci.yml` verifies on the v0.9 line:

1. English/Hindi translation parity.
2. Sudoku engine tests.
3. Android JVM unit tests.
4. Android instrumentation-test compilation.
5. Debug lint.
6. Release lint.
7. Debug APK assembly.
8. Release APK assembly with R8/resource shrinking.
9. Release AAB assembly.
10. Verification reports and release outputs as short-lived workflow artifacts.

The CI-produced release artifacts are build-verification outputs. They are not automatically production-publishable packages and must not be presented as store-ready unless production signing and release QA have actually been completed.

## Gradle Wrapper Verification

The repository wrapper is pinned through `gradle/wrapper/gradle-wrapper.properties`. GitHub's Gradle setup action validates wrapper JARs during CI.

For reproducible investigation of a release failure, record at minimum:

- exact Git commit SHA;
- Java version;
- Gradle wrapper version;
- Android SDK/Build Tools environment;
- command executed;
- relevant workflow run ID when CI was used.

## Build Types

### Debug

- Development build.
- Debug application-ID suffix.
- Debug version-name suffix.
- Intended for local/CI testing.

### Release

- Minification enabled.
- Resource shrinking enabled.
- Uses optimized default ProGuard rules plus `app/proguard-rules.pro`.
- Must pass release lint and R8/AAB assembly before a v0.9 release-quality claim.
- Production signing remains external to source control.

## Release-Signing Rules

SudokuNova follows these source-control rules:

- no `.jks`, `.keystore`, `.p12`, `.pfx`, PEM private keys, or signing passwords in Git;
- no signing secret embedded in Gradle files, `local.properties`, Android resources, Kotlin/Java code, or workflow YAML;
- CI production signing, if added later, must consume repository/environment secrets at runtime;
- fork and pull-request builds must remain safe when signing secrets are unavailable;
- a failed or missing production-signing step must never be bypassed by committing credentials.

## Common Failure Checks

If a build fails:

1. Confirm Java 17.
2. Confirm Android SDK 37 is installed.
3. Confirm you are building the intended branch and exact commit.
4. Confirm the Gradle wrapper rather than an unrelated global Gradle is being used.
5. Run `./gradlew clean` only when stale build state is suspected.
6. Read the first real compilation/test/lint/R8 error rather than only the final Gradle stack trace.
7. For release-only failures, inspect R8 diagnostics and `app/proguard-rules.pro` before adding broad keep rules.
8. Check `TROUBLESHOOTING.md` and CI logs.

## Release Evidence Policy

A release-quality statement must be tied to actual evidence. Documentation may describe required checks before they run, but it must not claim physical-device coverage, accessibility success, performance numbers, release signing, or store readiness unless those checks were actually performed and recorded.
