# Third-Party Notices

SudokuNova is licensed under the MIT License. The project also depends on third-party open-source software distributed under their own licenses.

## Android and AndroidX

The mature Android application uses Android/AndroidX libraries including:

- AndroidX Core KTX;
- AndroidX Core SplashScreen;
- Activity Compose;
- Lifecycle Runtime / Runtime Compose / ViewModel Compose;
- Navigation Compose;
- DataStore Preferences;
- Room Runtime, Room KTX, Room Compiler, and Room Testing;
- ProfileInstaller;
- AndroidX Benchmark Macrobenchmark JUnit4 tooling;
- Jetpack Compose UI, tooling preview, Material 3, Material Icons Extended, and Compose UI testing;
- AndroidX Test JUnit, Test Runner, and Espresso.

These projects are provided by the Android Open Source Project / AndroidX and are generally distributed under the Apache License 2.0. Applicable license texts and notices are available from the official Android/AndroidX source distributions.

`ProfileInstaller` supports Android runtime-profile installation and Macrobenchmark profile/reset operations. Its presence does not mean SudokuNova ships a project-generated Baseline Profile.

`androidx.benchmark:benchmark-macro-junit4` is used by the dedicated `:macrobenchmark` test module for release-like performance measurement. A dependency/buildable benchmark harness is not evidence that representative physical-device performance tests have passed.

`androidx.test:runner` is declared directly by the Macrobenchmark test module so its instrumentation runner and test-size annotations are explicit rather than incidental transitive dependencies.

## Kotlin and Kotlin Multiplatform

SudokuNova uses Kotlin and Kotlin Multiplatform Gradle tooling maintained by JetBrains. Kotlin project code/tooling is distributed under the licensing terms published by the Kotlin project, including Apache License 2.0 components.

The multiplatform build uses Kotlin/JVM, Kotlin/Native Apple targets, and Kotlin/Wasm browser targets. Toolchain/runtime components pulled for those targets remain subject to their corresponding Kotlin/JetBrains licenses and notices.

## Compose Multiplatform

The `sharedUI` module uses JetBrains Compose Multiplatform libraries/tooling, including portable Compose runtime, foundation, UI, Material 3 integration, Desktop application tooling, Apple Compose hosting, and Web/Wasm support.

Compose Multiplatform is maintained by JetBrains and uses the licensing terms published in its upstream project/distributions. The shared UI also relies on underlying platform/runtime libraries appropriate to each target.

A repository-supported Compose target does not imply that all platform distribution requirements are supplied by these dependencies. Apple signing/provisioning, Desktop code signing/notarization/reputation, and browser compatibility QA remain project/distribution responsibilities.

## Kotlin Symbol Processing (KSP)

SudokuNova uses Kotlin Symbol Processing for Android Room code generation. KSP is an open-source Google project distributed under its repository license.

## Gradle and Android Gradle Plugin

Gradle and the Gradle Wrapper are open-source software maintained by Gradle Inc. Android build tooling is supplied through the Android Gradle Plugin, including the Android Kotlin Multiplatform library plugin used by the shared modules. Applicable licensing information is distributed with the corresponding upstream projects/releases.

## JUnit and Kotlin Test

- JUnit 4 is used by the Android app's JVM tests and is distributed under the Eclipse Public License 1.0.
- Shared multiplatform tests use Kotlin's `kotlin.test` APIs, whose implementation/tooling follows the Kotlin project licensing terms.

## GitHub Actions

Repository automation references reusable GitHub Actions including:

- `actions/checkout`;
- `actions/setup-java`;
- `actions/upload-artifact`;
- `gradle/actions/setup-gradle`;
- the Android emulator action used by the instrumentation workflow.

Those actions remain subject to the licenses in their respective repositories.

The cross-platform workflow executes on GitHub-hosted Ubuntu, Windows, and macOS runners. Runner-provided operating-system SDKs/tooling are governed by their respective platform/vendor terms and are not relicensed by SudokuNova.

## Platform SDKs and Distribution Tooling

Building some SudokuNova targets requires platform SDKs/tooling that are not part of the SudokuNova source license, for example:

- Android SDK/build tools;
- Xcode/Apple SDKs for iOS/iPadOS hosting/signing;
- JDK `jpackage` for Desktop application images/packages;
- supported browsers/runtime tooling for Web/Wasm testing.

Developers/distributors are responsible for following the licenses and distribution terms of those SDKs/toolchains.

## Application Artwork and Educational Content

The SudokuNova launcher/splash vector artwork and in-app Sudoku educational explanations committed to this repository are original project content unless a source is explicitly credited in the relevant file.

Do not add third-party fonts, sound packs, icons, images, puzzle databases, educational copy, or code samples without confirming redistribution rights and preserving required notices.

## Dependency Source of Truth

Direct dependency coordinates and versions are maintained primarily in:

```text
gradle/libs.versions.toml
```

Module/plugin usage is defined in:

```text
build.gradle.kts
app/build.gradle.kts
sudoku-engine/build.gradle.kts
sharedUI/build.gradle.kts
macrobenchmark/build.gradle.kts
```

Workflow action references live under:

```text
.github/workflows/
```

Release/dependency reviews should compare those source files with this notice. This document is not a substitute for the authoritative upstream license text or a machine-generated complete transitive dependency inventory.

## Maintaining This File

When adding or changing a dependency, asset, font, sound, icon set, animation, code sample, GitHub Action, benchmark tool, platform SDK integration, or other third-party material:

1. Verify that its license/terms are compatible with the intended use/distribution.
2. Preserve notices required by that license.
3. Add/update an entry here when attribution or notice is appropriate.
4. Update `gradle/libs.versions.toml`, module build files, or workflow files consistently.
5. Review whether the dependency applies to all targets or only a platform/source set.
6. Do not commit material whose redistribution rights are unclear.
7. Re-run dependency/license review before claiming release readiness.
8. Record any material licensing change in `CHANGELOG.md`/`what_changed.md` when release-significant.

This file is an informational notice and does not replace license texts supplied by each third-party project.

**Made by the Sanskar**
