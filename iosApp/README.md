# SudokuNova iOS / iPadOS Host

This directory contains the native SwiftUI host for the Compose Multiplatform SudokuNova UI.

## Shared framework

The Kotlin module exports a static framework named:

`SudokuNovaSharedUI.framework`

Build the Simulator framework on macOS with:

```bash
./gradlew :sharedUI:linkDebugFrameworkIosSimulatorArm64
```

Build the device framework with:

```bash
./gradlew :sharedUI:linkReleaseFrameworkIosArm64
```

The SwiftUI bridge is implemented by `ContentView.swift`. It creates the Kotlin/Compose `MainViewController()` and embeds it with `UIViewControllerRepresentable`.

## Xcode host

Create or use an iOS application target named `SudokuNova` with iOS 14 or newer, then include:

- `SudokuNovaApp.swift`
- `ContentView.swift`
- the generated `SudokuNovaSharedUI.framework`

For local development, add a build phase that runs the appropriate Gradle framework task before Xcode links the application. Physical-device installation and App Store distribution still require the developer's own Apple Team, signing certificate, provisioning profile, bundle identifier, and store metadata; none of those credentials belong in this public repository.

## Supported Apple targets

The shared module builds:

- `iosArm64` for modern iPhone/iPad devices;
- `iosSimulatorArm64` for Apple Silicon simulators.

Compose Multiplatform 1.11.x supports iOS 14 and newer and no longer targets Apple x86_64 simulators.
