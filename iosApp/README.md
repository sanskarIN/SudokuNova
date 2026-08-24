# SudokuNova iOS/iPadOS Host

This directory contains the SwiftUI host sources for SudokuNova's Compose Multiplatform UI.

## Build the shared framework

On macOS, from the repository root:

```bash
./gradlew :sharedUI:linkDebugFrameworkIosSimulatorArm64
```

For an arm64 device framework:

```bash
./gradlew :sharedUI:linkReleaseFrameworkIosArm64
```

The framework is named `SudokuNovaSharedUI.framework`.

## Host sources

- `SudokuNovaApp.swift` is the SwiftUI application entry point.
- `ContentView.swift` wraps the Kotlin `MainViewController()` inside `UIViewControllerRepresentable`.

## Xcode integration boundary

The repository provides the native host source and KMP framework build target, but it does not claim that an Apple production application has already been signed or published. To create a distributable Apple app, configure a real Xcode app target with the desired bundle identifier, deployment target, assets, capabilities, framework linkage, signing team, provisioning profile, and store metadata.

For simulator development, build the simulator framework first and link/embed it into the Xcode host target. For physical devices or distribution, use the device framework and complete Apple signing/provisioning requirements.

Do not treat successful framework compilation as evidence of App Store approval or physical-device QA; record those checks only after they are actually performed.
