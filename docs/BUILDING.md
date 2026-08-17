# Building SudokuNova

## Required Tooling

Current repository configuration:

- JDK 17
- Gradle 9.5.0
- Android Gradle Plugin 9.3.0
- Kotlin 2.4.10
- Android compile/target SDK 37
- Android min SDK 26

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

## Debug APK

```bash
./gradlew :app:assembleDebug
```

Expected output directory:

```text
app/build/outputs/apk/debug/
```

## Release APK

A release build can be compiled with:

```bash
./gradlew :app:assembleRelease
```

The current repository does not commit production signing credentials. A distributable signed release must use secure external signing configuration before publication.

## Android App Bundle (AAB)

```bash
./gradlew :app:bundleRelease
```

Expected output directory:

```text
app/build/outputs/bundle/release/
```

## Unit Tests

```bash
./gradlew :sudoku-engine:test
./gradlew :app:testDebugUnitTest
```

## Android Lint

```bash
./gradlew :app:lintDebug
```

For release preparation also run the applicable release lint/build tasks supported by the configured Android plugin.

## Full Current Verification

```bash
./gradlew :sudoku-engine:test :app:testDebugUnitTest :app:lintDebug :app:assembleDebug
```

## Install Debug APK with ADB

```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

## Gradle Wrapper Verification

The repository's wrapper JAR is generated from Gradle 9.5.0 and its SHA-256 is verified in the bootstrap workflow before being committed. GitHub's Gradle setup action also validates wrapper JARs during CI.

## Build Types

### Debug

- Development build
- Debug application-ID suffix
- Intended for local/CI testing

### Release

- Minification enabled
- Resource shrinking enabled
- Uses optimized default ProGuard rules plus `app/proguard-rules.pro`
- Must be tested with production signing outside version control before distribution

## Common Failure Checks

If a build fails:

1. Confirm Java 17.
2. Confirm Android SDK 37 is installed.
3. Confirm you are building the current branch/commit.
4. Run `./gradlew clean` only when stale build state is suspected; do not make clean builds the default workaround.
5. Read the first real compilation/test error, not only the final Gradle stack trace.
6. Check `TROUBLESHOOTING.md` and CI logs.
