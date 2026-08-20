# Cross-Platform Development and Builds

SudokuNova now has a Kotlin Multiplatform and Compose Multiplatform layer in addition to the existing Android application.

## Supported targets

| Platform | Target | Build entry point | Status |
| --- | --- | --- | --- |
| Android | Android API 26+ | `:app` + `:sharedUI` Android KMP library | Supported |
| ChromeOS | Android runtime | Android APK/AAB | Supported through Android compatibility |
| Windows | Desktop JVM | `sharedUI` desktop target | Supported |
| macOS | Desktop JVM | `sharedUI` desktop target | Supported |
| Linux | Desktop JVM | `sharedUI` desktop target | Supported |
| iOS | Kotlin/Native arm64 | `SudokuNovaSharedUI.framework` | Supported framework target |
| iPadOS | Kotlin/Native arm64 | same iOS framework | Supported framework target |
| Web | Kotlin/Wasm | `sharedUI` Wasm browser target | Supported; Compose web remains upstream Beta |

## Architecture

The repository keeps three important boundaries:

1. `sudoku-engine` contains Sudoku models, solving, generation, difficulty rating, puzzle-code support, hints, logical teaching steps, and practice logic. It is compiled as Kotlin Multiplatform code.
2. `sharedUI` contains the portable Compose Multiplatform gameplay surface and platform entry points for Desktop, iOS, and Web.
3. `app` remains the mature Android application and consumes the shared modules without discarding Android-specific Room, DataStore, navigation, accessibility, release hardening, or Play Store tooling.

This staged structure avoids reducing the Android application's current feature set while making the reusable game engine and a playable UI available to non-Android targets.

## Required tools

### All platforms

- JDK 17
- Git
- the repository Gradle wrapper (`gradlew` / `gradlew.bat`)

### Android

- Android Studio with Android SDK 37
- Android SDK Build Tools compatible with the selected Android Gradle Plugin

### iOS / iPadOS

- macOS
- Xcode compatible with the Kotlin version used by this repository
- Apple signing only when installing on a physical device or distributing an app

### Desktop

Desktop development uses the JVM target. Native distributable generation uses the JDK `jpackage` tooling bundled with a compatible JDK.

### Web

A modern browser with WebAssembly GC support is required for the Wasm build.

## Common commands

Run commands from the repository root.

### Shared engine tests

Linux/macOS:

```bash
./gradlew :sudoku-engine:desktopTest
```

Windows PowerShell:

```powershell
.\gradlew.bat :sudoku-engine:desktopTest
```

### Android debug APK

Linux/macOS:

```bash
./gradlew :app:assembleDebug
```

Windows PowerShell:

```powershell
.\gradlew.bat :app:assembleDebug
```

The APK is written under `app/build/outputs/apk/debug/`.

### Android release APK and AAB

```bash
./gradlew :app:assembleRelease :app:bundleRelease
```

Production signing remains controlled by the environment variables documented in `PRODUCTION_SIGNING.md`.

### Run Desktop during development

Linux/macOS:

```bash
./gradlew :sharedUI:run
```

Windows PowerShell:

```powershell
.\gradlew.bat :sharedUI:run
```

### Create a Desktop application image

```bash
./gradlew :sharedUI:createDistributable
```

The current operating system determines the generated desktop application image. Build Windows packages on Windows, macOS packages on macOS, and Linux packages on Linux.

### Create platform installers

The Compose Desktop configuration declares these formats:

- Windows: MSI
- macOS: DMG
- Linux: DEB

Use the platform-specific packaging task from the appropriate host. `createDistributable` is the portable CI-friendly application-image task.

### Build the Web/Wasm production distribution

Linux/macOS:

```bash
./gradlew :sharedUI:wasmJsBrowserDistribution
```

Windows PowerShell:

```powershell
.\gradlew.bat :sharedUI:wasmJsBrowserDistribution
```

The generated web files are under `sharedUI/build/dist/wasmJs/productionExecutable/`.

### Run Web/Wasm locally

```bash
./gradlew :sharedUI:wasmJsBrowserDevelopmentRun
```

The Gradle development server prints the local URL and port.

### Build the iOS Simulator framework

On macOS:

```bash
./gradlew :sharedUI:linkDebugFrameworkIosSimulatorArm64
```

Framework output:

`sharedUI/build/bin/iosSimulatorArm64/debugFramework/SudokuNovaSharedUI.framework`

### Build the iOS device framework

On macOS:

```bash
./gradlew :sharedUI:linkReleaseFrameworkIosArm64
```

Framework output:

`sharedUI/build/bin/iosArm64/releaseFramework/SudokuNovaSharedUI.framework`

## Shared UI capabilities

The current portable gameplay surface includes:

- generated Sudoku puzzles;
- Beginner, Easy, Medium, Hard, Expert, Master, and Extreme difficulty choices;
- editable and fixed cell handling;
- conflict indication;
- number input;
- candidate notes;
- erasing;
- undo history;
- logical/reveal hints through the existing engine;
- puzzle reset;
- new-game generation;
- responsive board sizing for phone, tablet, browser, and desktop windows.

## Platform entry points

- Desktop: `sharedUI/src/desktopMain/.../Main.kt`
- Web/Wasm: `sharedUI/src/wasmJsMain/.../Main.kt`
- iOS/iPadOS: `sharedUI/src/iosMain/.../MainViewController.kt`
- Android: the existing `app` module remains the production entry point and consumes both `sudoku-engine` and `sharedUI`.

## CI validation

`.github/workflows/cross-platform.yml` validates:

- shared engine tests;
- shared desktop compilation;
- shared Web/Wasm compilation and production distribution;
- Android debug assembly;
- iOS Simulator framework linking on macOS;
- Desktop application-image creation on Linux, Windows, and macOS.

The existing Android CI and instrumentation workflows remain in place for Android-specific unit, lint, APK/AAB, Room/Compose instrumentation, release-verifier, and macrobenchmark coverage.

## Stability boundary

Kotlin Multiplatform and Compose Multiplatform are stable for Android, iOS, and Desktop. Compose Multiplatform's Web/Wasm target is upstream Beta, so web-specific migration issues can occur between framework releases even though SudokuNova treats the web build as a supported repository target.

## Feature parity

The portable UI intentionally starts with the complete core puzzle loop. Android-only capabilities such as Room-backed history, DataStore preferences, Android sharing/document pickers, haptics, platform notifications, and Play Store release integration remain in the Android application until equivalent multiplatform abstractions are implemented. The engine and shared UI boundaries are designed so those services can be migrated incrementally without duplicating Sudoku logic.
