# Cross-Platform Development and Builds

SudokuNova 2.0.12 keeps the mature Android application as its primary production surface while adding a shared Kotlin Multiplatform (KMP) engine and Compose Multiplatform gameplay surface for Android, iOS/iPadOS, Windows, macOS, Linux, and Web.

This document distinguishes **repository build support** from **production release evidence**. A target is considered repository-supported when its source, build configuration, and CI compilation/package path exist. Production signing, notarization, store submission, physical-device QA, browser compatibility QA, and distribution approval still require real external evidence and are never inferred from source control.

## Current platform matrix

| Platform | Repository target | Main build entry point | Repository status |
| --- | --- | --- | --- |
| Android | Android API 26+ | `:app` plus `:sharedUI` Android KMP library | Primary production app preserved; shared UI host available |
| ChromeOS | Android compatibility | Android APK/AAB | Uses Android application compatibility path |
| iOS | Kotlin/Native arm64 | `SudokuNovaSharedUI.framework` | Shared framework + SwiftUI host sources |
| iPadOS | Kotlin/Native arm64 | same iOS framework | Shared framework + SwiftUI host sources |
| Windows | Desktop JVM | `:sharedUI` desktop target | Desktop application image/package path |
| macOS | Desktop JVM | `:sharedUI` desktop target | Desktop application image/package path |
| Linux | Desktop JVM | `:sharedUI` desktop target | Desktop application image/package path |
| Web | Kotlin/Wasm | `:sharedUI` Wasm browser target | Browser distribution path; upstream Compose Web remains Beta |

## Architecture

The cross-platform design deliberately separates portable domain/UI code from mature Android-only capabilities:

1. `sudoku-engine` is Kotlin Multiplatform. Existing engine implementation and tests are mapped into common source sets so generation, solving, difficulty logic, hints, learning logic, and puzzle-code behavior are not duplicated by platform.
2. `sharedUI` contains the portable Compose Multiplatform gameplay state and responsive Sudoku surface.
3. `app` remains the Android production application. It consumes both `sudoku-engine` and `sharedUI`, while retaining Android-specific Room, DataStore, navigation, accessibility work, backup/transfer, Macrobenchmark, release verification, and Play Store tooling.
4. `iosApp` contains the minimal SwiftUI host sources that bridge to the generated `SudokuNovaSharedUI.framework`.

`MainActivity` remains Android's launcher. `CrossPlatformActivity` is intentionally non-exported and provides an Android host for the same shared UI used by the other platforms. This staged migration avoids removing mature Android functionality merely to claim cross-platform parity.

## Shared gameplay capabilities

The portable UI currently includes:

- generated Sudoku puzzles;
- Beginner, Easy, Medium, Hard, Expert, Master, and Extreme difficulties;
- fixed and editable cells;
- number entry;
- candidate notes;
- automatic peer-note cleanup after a placement;
- conflict indication;
- erase;
- bounded undo history;
- engine-backed hints;
- reset;
- new-game generation;
- responsive layout for smaller and larger windows;
- typed, locale-neutral gameplay status events;
- English/Hindi Compose resources for the migrated shared surface;
- explicit Sudoku-cell semantics for row, column, value/notes, fixed/editable, selected, and conflict state;
- a non-color conflict marker in addition to semantic conflict state;
- a portable active-game snapshot and storage contract with fail-closed restore validation.

Android-only capabilities remain in the Android application until equivalent multiplatform service abstractions are implemented and tested. This is an explicit feature-parity boundary, not missing evidence hidden behind a generic “supported” label.

## Feature-parity matrix

This table is the source-controlled parity boundary between the mature Android product and the shared surface. “Shared” means the behavior lives in common Kotlin/Compose code; it does not imply that every target has completed runtime QA.

| Capability | Mature Android app | Shared targets | Current boundary |
| --- | --- | --- | --- |
| Classic 9×9 generation/solving | Yes | Yes | Shared `sudoku-engine` |
| Seven difficulty levels | Yes | Yes | Shared engine + localized picker |
| Number entry, erase, reset | Yes | Yes | Shared gameplay state/UI |
| Candidate notes + peer cleanup | Yes | Yes | Shared gameplay state/UI |
| Undo | Yes | Yes | Shared bounded in-memory history |
| Engine-backed hints | Yes | Yes | Shared hint engine + localized technique/status text |
| Conflict feedback | Yes | Yes | Shared visual + semantic + non-color marker |
| English/Hindi player-facing text | Yes | Yes for migrated shared surface | Shared Compose resources; parity guard covers keys/placeholders |
| Active-game save model | Yes | Contract + validated snapshot | Platform persistence adapters are still required before save/resume parity is claimed |
| Room/DataStore persistence | Yes | No | Must remain Android-only; common code uses `SharedGameStore` instead |
| Challenges/custom puzzles UI | Yes | Not yet | Domain migration remains pending |
| Learning/statistics UI | Yes | Not yet | Engine pieces may be shared; presentation/persistence parity remains pending |
| Puzzle-code codec | Yes | Engine available | Common import/export service/UI integration remains pending |
| Backup/restore transfer | Yes | Not yet | Requires platform file/share adapters and compatibility design |
| Themes/settings breadth | Yes | Basic shared Material theme only | Portable settings persistence remains pending |
| Accessibility source semantics | Mature Android coverage | Cell semantics added | Target assistive-technology QA remains external evidence |
| Keyboard/focus navigation | Android-specific coverage | Pending | Add only through APIs that remain valid across supported Compose targets |
| Macrobenchmark/performance evidence | Yes | No shared equivalent yet | Android evidence must not be generalized to other targets |
| Production signing/store release | Android process documented | No | Per-platform external evidence is required |

## Shared localization contract

The shared Compose surface uses `sharedUI/src/commonMain/composeResources/` as the localization source of truth:

- `values/strings.xml` is the default English catalog;
- `values-hi/strings.xml` is the Hindi catalog;
- generated resources use the stable package `com.sanskar.sudokunova.shared.resources`;
- gameplay state emits `SharedGameStatus` values rather than player-facing strings;
- difficulty and hint-technique labels are resolved in the UI from resources;
- `scripts/verify_translations.py` validates English/Hindi key parity for the shared catalog and validates shared printf-style placeholder signatures as a CI guard.

The default `values` catalog is the repository fallback locale. If a target locale has no matching localized resource, Compose resource resolution falls back to the default catalog. The shared parity guard intentionally requires Hindi keys for every currently tracked shared player-facing string, so fallback should not be needed for an accidentally missing Hindi key in a green repository head. Unsupported locales still use the default English catalog until an explicit locale is added.

Locale-resource parity is source evidence only. Representative Hindi layout checks, font scaling, truncation, and runtime locale switching must still be exercised on real target hosts before claiming production localization parity.

## Shared persistence contract

`SharedGameSnapshot` captures the portable active-game state required for save/resume work: difficulty, deterministic generation seed, board contents, notes, selected cell, and notes mode. `SharedGameStore` is a suspendable common interface with `load`, `save`, and `clear` operations; it deliberately has no Room, DataStore, filesystem, browser-storage, or Apple-framework types.

`SharedGameState.restore` fails closed before mutation. It regenerates the starting puzzle from the saved difficulty/seed, requires all fixed clues to remain unchanged, validates note indexes/values/targets, validates the selected-cell bound, copies restored note sets, clears undo history, and only then publishes restored state. This provides a tested common boundary while platform-specific adapters remain pending.

A source-level store contract is not a claim that save/resume already works on iOS, Desktop, or Web. Each production host still needs an adapter, lifecycle ownership, migration/versioning policy, corruption handling, and runtime tests.

## Shared accessibility boundary

Portable Sudoku cells expose a localized content description containing row, column, value or candidate notes, fixed/editable state, selection state, and conflict state. Selection is also published through Compose semantics. Conflicts use an explicit `!` marker in addition to color and semantic state, and notes mode displays a visible check mark when enabled.

These source semantics improve the common baseline but do not replace TalkBack, VoiceOver, desktop accessibility API, browser accessibility-tree, keyboard/focus, pointer/touch, large-font, or resize testing on real hosts. Those checks remain evidence-gated work in issue #34.

## Toolchain

The repository currently uses:

- JDK 17;
- Kotlin 2.4.10;
- Android Gradle Plugin 9.3.1;
- compile/target SDK 37 for the Android production app;
- minimum Android SDK 26;
- Compose Multiplatform 1.11.1 for shared UI foundations;
- the repository Gradle wrapper.

Additional host requirements:

- Android: Android Studio/Android SDK 37;
- iOS/iPadOS: macOS and Xcode for the native host, signing, simulator/device execution, and distribution;
- Desktop: a JDK containing `jpackage` for native application packaging;
- Web: a modern WebAssembly-capable browser.

## Common build and test commands

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

### Shared gameplay-state tests

Linux/macOS:

```bash
./gradlew :sharedUI:desktopTest
```

Windows PowerShell:

```powershell
.\gradlew.bat :sharedUI:desktopTest
```

The shared-state suite includes typed-status transitions, notes/undo/reset behavior, hints, difficulty switching, active-game snapshot round trips, and fail-closed snapshot validation for changed fixed clues and invalid note targets.

### Translation guards

```bash
python scripts/verify_translations.py
python -m unittest scripts.tests.test_verify_translations
```

These commands validate the existing Android localization scope plus full English/Hindi key parity and placeholder parity for shared Compose resources.

### Compile shared Desktop and Web code

```bash
./gradlew :sharedUI:compileKotlinDesktop :sharedUI:compileKotlinWasmJs
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

Output is under `app/build/outputs/apk/debug/`.

### Android 2.0.12 release outputs

```bash
./gradlew :app:assembleRelease :app:bundleRelease
```

The source-controlled release contract remains:

- application ID `in.sanskar.sudokunova`;
- version code `2012`;
- version name `2.0.12`;
- minSdk `26`;
- targetSdk `37`.

Production signing and certificate-bound verification remain governed by `PRODUCTION_SIGNING.md`, `PRODUCTION_RELEASE_VALIDATION.md`, and `V2_0_12_RELEASE.md`.

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

The host operating system determines the generated application image. Native package configuration declares:

- Windows: MSI;
- macOS: DMG;
- Linux: DEB.

Build platform-native packages on the corresponding host OS.

### Build Web/Wasm production distribution

Linux/macOS:

```bash
./gradlew :sharedUI:wasmJsBrowserDistribution
```

Windows PowerShell:

```powershell
.\gradlew.bat :sharedUI:wasmJsBrowserDistribution
```

Generated files are under `sharedUI/build/dist/wasmJs/productionExecutable/`.

### Run Web/Wasm locally

```bash
./gradlew :sharedUI:wasmJsBrowserDevelopmentRun
```

Use the URL printed by the Gradle development server. Browser QA should cover the actual browsers/versions intended for distribution before a public web release is claimed.

### Build iOS Simulator framework

On macOS:

```bash
./gradlew :sharedUI:linkDebugFrameworkIosSimulatorArm64
```

Expected output:

`sharedUI/build/bin/iosSimulatorArm64/debugFramework/SudokuNovaSharedUI.framework`

### Build iOS device framework

On macOS:

```bash
./gradlew :sharedUI:linkReleaseFrameworkIosArm64
```

Expected output:

`sharedUI/build/bin/iosArm64/releaseFramework/SudokuNovaSharedUI.framework`

The SwiftUI sources in `iosApp/` are host code. A production Apple application additionally requires a real Xcode application project/configuration, bundle identity, signing/provisioning, assets, device QA, and App Store validation. Those external release steps are not fabricated by repository compilation.

## Platform entry points

- Android mature production launcher: `app/src/main/java/com/sanskar/sudokunova/MainActivity.kt`
- Android shared UI host: `app/src/main/java/com/sanskar/sudokunova/CrossPlatformActivity.kt`
- Desktop: `sharedUI/src/desktopMain/kotlin/com/sanskar/sudokunova/shared/desktop/Main.kt`
- Web/Wasm: `sharedUI/src/wasmJsMain/kotlin/com/sanskar/sudokunova/shared/web/Main.kt`
- iOS/iPadOS Compose bridge: `sharedUI/src/iosMain/kotlin/com/sanskar/sudokunova/shared/ios/MainViewController.kt`
- SwiftUI host sources: `iosApp/`

## CI contract

The dedicated cross-platform workflow is intended to validate the exact PR head across these repository-build gates:

- shared engine Desktop tests;
- shared gameplay-state Desktop tests;
- shared Desktop compilation;
- shared Web/Wasm compilation;
- Android debug assembly;
- Web production distribution;
- iOS Simulator framework linking on macOS;
- Desktop application-image generation on Linux, Windows, and macOS.

The existing Android CI remains authoritative for the stricter 2.0.12 Android release contract, repository guards, Android unit/instrumentation compilation, lint, R8 release APK, AAB, embedded application/version/SDK/debuggable verification, and artifact evidence. The API-35 instrumentation workflow remains a separate exact-head gate.

## Evidence boundary

Cross-platform source/build success does **not** by itself prove:

- Apple production signing/provisioning or App Store acceptance;
- macOS signing/notarization;
- Windows code signing or installer reputation;
- Linux distribution-repository compatibility;
- browser/device compatibility across the intended web support matrix;
- physical-device accessibility/performance/lifecycle behavior outside the tests actually run;
- store/publication completion.

Record those results only after they actually occur. This follows the same exact-evidence discipline as the Android 2.0.12 release line.
