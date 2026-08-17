# Installation

This guide covers installation of the SudokuNova development project. A public store release is not yet documented as available.

## Prerequisites

Install:

- Git
- JDK 17
- Android Studio compatible with Android Gradle Plugin 9.3.0
- Android SDK Platform 37
- Android SDK Build Tools required by the configured Android plugin
- An Android emulator or physical device running API 26 or newer

## Clone

```bash
git clone https://github.com/sanskarIN/SudokuNova.git
cd SudokuNova
```

For active development on the current milestone branch, check the repository's branch/PR state before switching away from `main`.

## Open in Android Studio

1. Start Android Studio.
2. Choose **Open**.
3. Select the cloned `SudokuNova` directory.
4. Allow Gradle project import/sync to complete.
5. Confirm Android Studio is using JDK 17 for the project.
6. Install missing Android SDK components if Android Studio prompts for them.

## Command-Line Verification

macOS/Linux:

```bash
./gradlew :sudoku-engine:test :app:testDebugUnitTest :app:lintDebug :app:assembleDebug
```

Windows:

```bat
gradlew.bat :sudoku-engine:test :app:testDebugUnitTest :app:lintDebug :app:assembleDebug
```

## Install a Debug APK

After a successful debug build, the APK is produced under the app module's build outputs. With an emulator/device available through ADB:

```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

The debug build uses the configured debug application ID suffix, so it can be distinguished from a future production build.

## No Signing Secrets Required for Debug

Debug development does not require production signing credentials. Never commit release keystores, passwords, tokens, or certificates.

For production release preparation, see `RELEASING.md`.
