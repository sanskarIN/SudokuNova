# Third-Party Notices

SudokuNova is licensed under the MIT License. The project also depends on third-party open-source software distributed under their own licenses.

## Android and Jetpack

SudokuNova uses Android and AndroidX libraries, including:

- AndroidX Core KTX;
- AndroidX Core SplashScreen;
- Activity Compose;
- Lifecycle Runtime / Runtime Compose / ViewModel Compose;
- Navigation Compose;
- DataStore Preferences;
- Room Runtime, Room KTX, Room Compiler, and Room Testing;
- Jetpack Compose UI, tooling preview, Material 3, Material Icons Extended, and Compose UI testing;
- AndroidX Test JUnit and Espresso.

These projects are provided by the Android Open Source Project / AndroidX and are generally distributed under the Apache License 2.0. Project information and license texts are available from the official Android Open Source Project and AndroidX source distributions.

## Kotlin

Kotlin and the Kotlin Gradle tooling are maintained by JetBrains and released under the Apache License 2.0.

## Kotlin Symbol Processing (KSP)

SudokuNova uses Kotlin Symbol Processing for Room code generation. KSP is an open-source Google project distributed under its repository license.

## Gradle and Android Gradle Plugin

Gradle and the Gradle Wrapper are open-source software maintained by Gradle Inc. Android build tooling is supplied through the Android Gradle Plugin. Their applicable licensing information is distributed with the corresponding source/project releases.

## JUnit

JUnit 4 is used for unit testing and is distributed under the Eclipse Public License 1.0.

## GitHub Actions

Repository automation references reusable GitHub Actions including:

- `actions/checkout`;
- `actions/setup-java`;
- `actions/upload-artifact`;
- `gradle/actions/setup-gradle`;
- the Android emulator action used by the instrumentation workflow.

Those actions remain subject to the licenses in their respective repositories.

## Application Artwork and Educational Content

The SudokuNova launcher/splash vector artwork and in-app Sudoku educational explanations committed to this repository are original project content unless a source is explicitly credited in the relevant file.

## Dependency Source of Truth

Direct dependency coordinates and versions are maintained in:

```text
gradle/libs.versions.toml
```

Release reviews should compare that catalog, module build files, Gradle plugins, and GitHub Actions references against this notice rather than assuming this document alone is a complete dependency inventory.

## Maintaining This File

When adding a dependency, asset, font, sound, icon set, animation, code sample, GitHub Action, or other third-party material:

1. Verify that its license is compatible with SudokuNova's distribution.
2. Preserve notices required by that license.
3. Add an entry here when attribution or notice is appropriate.
4. Update `gradle/libs.versions.toml` or the relevant build/workflow file consistently.
5. Do not commit material whose redistribution rights are unclear.
6. Re-run the v0.9 release QA dependency/license review before claiming release readiness.

This file is an informational notice and does not replace the license text supplied by each third-party project.

**Made by the Sanskar**
