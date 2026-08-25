# Cross-Platform Development and Builds

SudokuNova 2.0.14 keeps the mature Android application as its primary production surface while continuing a shared Kotlin Multiplatform (KMP) engine and Compose Multiplatform gameplay surface for Android, iOS/iPadOS, Windows, macOS, Linux, and Web.

This document distinguishes **repository build support** from **production release evidence**. A target is repository-supported when its source, build configuration, and CI compilation/package path exist. Production signing, notarization, store submission, physical-device QA, browser compatibility QA, and distribution approval still require real external evidence and are never inferred from source control.

## Current platform matrix

| Platform | Repository target | Main build entry point | Repository status |
| --- | --- | --- | --- |
| Android | Android API 26+ | `:app` plus `:sharedUI` Android KMP library | Mature primary app preserved; shared UI host available |
| ChromeOS | Android compatibility | Android APK/AAB | Uses Android compatibility path |
| iOS | Kotlin/Native arm64 | `SudokuNovaSharedUI.framework` | Shared framework + SwiftUI host sources |
| iPadOS | Kotlin/Native arm64 | same Apple target family | Shared framework + SwiftUI host sources |
| Windows | Desktop JVM | `:sharedUI` desktop target | Desktop application image/package path |
| macOS | Desktop JVM | `:sharedUI` desktop target | Desktop application image/package path |
| Linux | Desktop JVM | `:sharedUI` desktop target | Desktop application image/package path |
| Web | Kotlin/Wasm | `:sharedUI` Wasm browser target | Browser distribution path; runtime browser QA remains separate |

## Architecture

The cross-platform design deliberately separates portable domain/UI code from mature Android-only capabilities:

1. `sudoku-engine` is Kotlin Multiplatform and owns Classic Sudoku correctness, generation, solving, difficulty logic, teaching/hints, puzzle-code transport, and validated puzzle exchange.
2. `sharedUI` contains portable Compose Multiplatform gameplay state/UI, localization, semantics, keyboard-grid handling, active-game persistence, and local settings contracts.
3. `app` remains the Android production application. It consumes both shared modules while retaining Android Room/DataStore, full navigation, challenges, learning/statistics, backup/transfer, mature accessibility integration, Macrobenchmark, and release tooling.
4. `iosApp` contains SwiftUI host sources that bridge to the generated `SudokuNovaSharedUI.framework`.

`MainActivity` remains Android's launcher. `CrossPlatformActivity` is intentionally non-exported and hosts the same shared UI used by other targets. This staged migration avoids deleting mature Android capabilities merely to claim superficial parity.

## Shared gameplay capabilities

The portable UI/state currently includes:

- generated Classic 9×9 puzzles;
- Beginner, Easy, Medium, Hard, Expert, Master, and Extreme difficulties;
- fixed/editable cells and number-pad input;
- candidate notes and automatic peer-note cleanup;
- conflict feedback with visual, semantic, and non-color `!` indication;
- erase, bounded undo, engine-backed hints, reset, and new game;
- responsive board sizing;
- typed locale-neutral gameplay status events;
- English/Hindi Compose resources and deterministic key/placeholder parity checks;
- cell semantics for row, column, value/notes, fixed/editable, selected, and conflict state;
- selected-state semantics for Notes mode and shared theme selection;
- deterministic shared grid selection movement;
- a focusable shared grid with arrow navigation, `1`–`9` entry, `N` Notes, `H` Hint, and Backspace/Delete erase handling;
- versioned `SNG1` active-game encoding and fail-closed restore validation;
- native local active-game adapters for staged Android shared host, Desktop JVM, Web/Wasm, and Apple hosts;
- versioned `SNS1` user-settings encoding and fail-closed decoding;
- native local settings adapters for staged Android shared host, Desktop JVM, Web/Wasm, and Apple hosts;
- common settings state and Compose restore/autosave ownership;
- localized persisted System/Light/Dark shared theme selection;
- common restore/autosave ownership for gameplay Compose hosts;
- common `PuzzleExchangeService` around `SNP1`, including unique-solution import acceptance.

Android-only capabilities remain in the Android application until equivalent shared domain/service/presentation work is implemented and tested. This is an explicit parity boundary.

## Feature-parity matrix

“Shared” means the behavior lives in common Kotlin/Compose code; it does not imply every target has completed runtime QA.

| Capability | Mature Android app | Shared targets | Current boundary |
| --- | --- | --- | --- |
| Classic 9×9 generation/solving | Yes | Yes | Shared `sudoku-engine` |
| Seven difficulty levels | Yes | Yes | Shared engine + localized picker |
| Number-pad entry, erase, reset | Yes | Yes | Shared gameplay state/UI |
| Candidate notes + peer cleanup | Yes | Yes | Shared gameplay state/UI |
| Undo | Yes | Yes | Shared bounded in-memory history |
| Engine-backed hints | Yes | Yes | Shared hint engine + localized technique/status text |
| Conflict feedback | Yes | Yes | Shared visual + semantic + non-color marker |
| English/Hindi migrated text | Yes | Yes | Shared Compose resources; key/placeholder parity guard |
| Active-game save model | Yes | Yes at source level | `SNG1` + Android shared/Desktop/Web/Apple adapters; real lifecycle QA still required |
| Shared user-settings model | Yes | Yes at source level | `SNS1` + Android shared/Desktop/Web/Apple adapters; mature Android DataStore remains separate |
| System/Light/Dark theme | Yes | Yes at source level | Localized common controls apply/persist the shared theme; runtime QA remains target-specific |
| Full settings behavior breadth | Yes | Partial | Portable schema exists; dynamic color, feedback, timer/mistake/input behavior still need target-safe behavioral parity |
| Room/DataStore persistence | Yes | No | Android-only; common code uses narrow store interfaces |
| Puzzle-code format | Yes | Yes | Shared `PuzzleCodeCodec` retains `SNP1` compatibility |
| Puzzle-code acceptance boundary | Android flow | Yes | `PuzzleExchangeService` rejects malformed, unsolvable, or non-unique imported puzzles |
| Imported/custom puzzle gameplay session | Yes | Not yet | `SNG1` is generated-seed based; custom/import provenance needs an explicit shared model before persistence/UI parity |
| Clipboard/share/file-picker adapters | Yes | Not yet | Platform interaction adapters remain pending |
| Challenges/custom puzzle UI | Yes | Not yet | Shared domain/presentation migration remains pending |
| Learning/statistics UI | Yes | Not yet | Engine pieces may be shared; presentation/persistence remains pending |
| History/saved-puzzle persistence | Yes | Not yet | Shared active-game/settings stores do not replace Android Room history/saves |
| Backup/restore transfer | Yes | Not yet | Requires platform file/share adapters and compatibility design |
| Accessibility source semantics | Mature Android coverage | Shared baseline implemented | Real assistive-technology QA remains external evidence |
| Keyboard/focus navigation | Mature Android coverage | Common gameplay mapping implemented | Arrows, digits, Notes, Hint, Backspace/Delete; real target focus/hardware QA remains pending |
| Macrobenchmark/performance evidence | Yes | No shared equivalent | Android measurements must not be generalized |
| Production signing/store release | Android process documented | No | Per-platform external evidence required |

## Shared puzzle exchange contract

`PuzzleCodeCodec` remains the platform-independent `SNP1` transport codec and preserves the established checksum/vector compatibility contract.

`PuzzleExchangeService` is the common acceptance boundary:

1. decode and validate `SNP1` version, bounds, payload, checksum, difficulty, and board validity;
2. analyze the board with `SudokuSolver` up to two solutions;
3. reject anything that does not have exactly one solution;
4. return the puzzle, uniquely proven solution, and encoded difficulty when accepted.

This prevents each UI host from inventing a separate uniqueness rule. It does **not** yet create a shared custom-puzzle gameplay/persistence session: active `SNG1` persistence regenerates games from a difficulty/seed, so imported provenance must be represented explicitly before shared save/resume can claim custom-puzzle support.

## Shared localization contract

The shared Compose localization source of truth is `sharedUI/src/commonMain/composeResources/`:

- `values/strings.xml` is default English;
- `values-hi/strings.xml` is Hindi;
- generated resources use package `com.sanskar.sudokunova.shared.resources`;
- gameplay state emits `SharedGameStatus` rather than translated strings;
- difficulty, theme, and hint-technique labels are resolved in UI resources;
- `scripts/verify_translations.py` checks shared English/Hindi key and printf-placeholder parity.

Default `values` is the fallback locale. Unsupported locales use English until explicitly added. A green repository head should not rely on fallback for a missing Hindi key because the parity guard rejects that drift.

Representative Hindi layout, font-scale, truncation, and runtime locale-switch QA are still real-host evidence requirements.

## Shared active-game persistence contract

`SharedGameSnapshot` captures difficulty, deterministic generation seed, board contents, notes, selected cell, and Notes mode. `SharedGameStore` is the snapshot-level suspendable interface; `SharedGameTextStore` is the minimal native boundary for one encoded text value. Common interfaces import no Room, DataStore, filesystem, browser-storage, or Apple-framework type.

`SharedGameSnapshotCodec` owns bounded deterministic `SNG1` serialization. It validates version, difficulty, seed, exactly 81 board digits, selection bounds, Notes-mode encoding, note cell/digit bounds, duplicates, and maximum payload size.

`SharedGameState.restore` remains Sudoku-aware and fails closed before mutation: it regenerates the starting puzzle from difficulty/seed, requires fixed clues to match, validates note targets/values and selection bounds, clears undo history, and only then publishes restored state.

`rememberPersistedSharedGameState` attempts restoration once and autosaves later observable snapshots. Storage failures do not weaken Sudoku validation or make a fresh in-memory game unusable.

Current local/offline adapters:

- staged Android shared host: private `SharedPreferences` adapter;
- Desktop JVM: `java.util.prefs.Preferences`;
- Web/Wasm: browser `localStorage`;
- iOS/iPadOS: `NSUserDefaults`.

See [Shared Cross-Platform Active-Game Persistence](SHARED_PERSISTENCE.md).

Source adapters do not prove process-death recovery, browser privacy-mode behavior, Apple physical-device lifecycle behavior, Desktop clean-machine behavior, or platform backup semantics.

## Shared settings persistence contract

`SharedUserSettings` mirrors the portable vocabulary needed for progressive parity, including theme, dynamic-color preference, input mode, highlighting, mistake checking, note cleanup, timer visibility, haptics/sounds, reduced motion, high contrast, and mistake limit.

`SharedSettingsCodec` owns deterministic bounded `SNS1` serialization. The decoder fails closed on unsupported versions, malformed or duplicate/missing fields, invalid booleans/enums/mistake limits, and oversized payloads.

`SharedSettingsStore` is the settings-level suspendable interface. `SharedSettingsTextStore` is the minimal platform boundary for one encoded settings string. `SharedSettingsState` is observable common state; `rememberPersistedSharedSettingsState` owns best-effort restore and autosave for Compose hosts.

Current local/offline settings adapters:

- staged Android shared host: private `SharedPreferences`;
- Desktop JVM: `java.util.prefs.Preferences`;
- Web/Wasm: browser `localStorage`;
- iOS/iPadOS: `NSUserDefaults`.

The 2.0.14 shared UI currently applies and exposes System/Light/Dark theme selection. Other schema fields remain progressive parity surfaces and are not claimed as behaviorally complete merely because they are persisted.

See [Shared Cross-Platform User Settings](SHARED_SETTINGS.md).

## Shared accessibility and input boundary

Portable Sudoku cells expose localized row/column/value-or-notes/fixed-or-editable/selected/conflict descriptions. Selection is published through Compose semantics. Conflicts use an explicit `!` marker in addition to color and semantics. Notes mode and theme selection use visible/semantic selected state.

For 2.0.14, the shared board is focusable and handles key-down events for:

- arrow keys → deterministic one-cell row/column navigation;
- `1`–`9` → the same `SharedGameState.enter` path used by the visible number pad;
- `N` → toggle Notes mode;
- `H` → request a hint;
- Backspace/Delete → the same clue-protected erase action used by visible controls.

When no cell is selected, the first navigation action deterministically selects index `0`; edge movement clamps inside `0..80`.

Visible number/action controls remain available. Real TalkBack, VoiceOver, desktop accessibility API, browser accessibility-tree, keyboard/focus, pointer/touch, large-font, and resize testing remain evidence-gated work.

See [Hardware Keyboard Reference](KEYBOARD_SHORTCUTS.md).

## Toolchain

Current repository toolchain:

- JDK 17;
- Kotlin 2.4.10;
- Android Gradle Plugin 9.3.1;
- compile/target SDK 37;
- minimum Android SDK 26;
- Compose Multiplatform 1.11.1;
- Room 2.8.4 for the mature Android persistence layer;
- repository Gradle wrapper.

Host requirements include Android SDK 37 for Android, macOS/Xcode for Apple framework/host work, a JDK with `jpackage` for Desktop packaging, and a modern Wasm-capable browser for Web runtime validation.

## Common build and test commands

Run from repository root.

### Shared engine tests

```bash
./gradlew :sudoku-engine:desktopTest --stacktrace
```

This includes the `SNP1` compatibility vector and `PuzzleExchangeService` unique-solution acceptance tests.

### Shared gameplay/settings tests

```bash
./gradlew :sharedUI:desktopTest --stacktrace
```

Coverage includes gameplay state, typed statuses, notes/undo/reset, hints, difficulty switching, deterministic grid navigation, active-game snapshots, `SNG1`, settings model/state/store behavior, deterministic `SNS1`, encoded stores, and restore/save wiring.

### Translation guards

```bash
python scripts/verify_translations.py
python -m unittest scripts.tests.test_verify_translations
```

### Compile shared Desktop and Web UI

```bash
./gradlew :sharedUI:compileKotlinDesktop :sharedUI:compileKotlinWasmJs --stacktrace
```

### Android debug APK

```bash
./gradlew :app:assembleDebug --stacktrace
```

### Android 2.0.14 release outputs

```bash
./gradlew :app:assembleRelease :app:bundleRelease --stacktrace
```

Source-controlled contract:

- application ID `in.sanskar.sudokunova`;
- version code `2014`;
- version name `2.0.14`;
- minSdk `26`;
- targetSdk `37`.

Production signing/certificate-bound verification are governed by `PRODUCTION_SIGNING.md`, `PRODUCTION_RELEASE_VALIDATION.md`, and `V2_0_14_RELEASE.md`.

### Run Desktop during development

```bash
./gradlew :sharedUI:run
```

### Create Desktop application image

```bash
./gradlew :sharedUI:createDistributable
```

Native distribution configuration declares MSI on Windows, DMG on macOS, and DEB on Linux. Build platform-native packages on the corresponding host.

### Build Web/Wasm production distribution

```bash
./gradlew :sharedUI:wasmJsBrowserDistribution
```

Generated files are under `sharedUI/build/dist/wasmJs/productionExecutable/`.

### Run Web/Wasm locally

```bash
./gradlew :sharedUI:wasmJsBrowserDevelopmentRun
```

Browser compatibility, accessibility, persistence, focus, touch, and reload behavior must be verified against the actual intended browser matrix before public Web release claims.

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

The SwiftUI sources in `iosApp/` are host code. A production Apple application additionally requires a real Xcode application target/project or reproducible generated-host workflow, bundle identity, deployment target, assets, signing/provisioning, device QA, and App Store validation.

## Platform entry points

- Android mature launcher: `app/src/main/java/com/sanskar/sudokunova/MainActivity.kt`
- Android shared host: `app/src/main/java/com/sanskar/sudokunova/CrossPlatformActivity.kt`
- Android active-game adapter: `app/src/main/java/com/sanskar/sudokunova/CrossPlatformSharedPreferencesGameTextStore.kt`
- Android settings adapter: `app/src/main/java/com/sanskar/sudokunova/CrossPlatformSharedPreferencesSettingsTextStore.kt`
- Desktop entry point: `sharedUI/src/desktopMain/kotlin/com/sanskar/sudokunova/shared/desktop/Main.kt`
- Desktop active-game adapter: `sharedUI/src/desktopMain/kotlin/com/sanskar/sudokunova/shared/desktop/DesktopPreferencesGameTextStore.kt`
- Desktop settings adapter: `sharedUI/src/desktopMain/kotlin/com/sanskar/sudokunova/shared/desktop/DesktopPreferencesSettingsTextStore.kt`
- Web entry point: `sharedUI/src/wasmJsMain/kotlin/com/sanskar/sudokunova/shared/web/Main.kt`
- Web active-game adapter: `sharedUI/src/wasmJsMain/kotlin/com/sanskar/sudokunova/shared/web/WebLocalStorageGameTextStore.kt`
- Web settings adapter: `sharedUI/src/wasmJsMain/kotlin/com/sanskar/sudokunova/shared/web/WebLocalStorageSettingsTextStore.kt`
- iOS/iPadOS Compose bridge: `sharedUI/src/iosMain/kotlin/com/sanskar/sudokunova/shared/ios/MainViewController.kt`
- Apple active-game adapter: `sharedUI/src/iosMain/kotlin/com/sanskar/sudokunova/shared/ios/AppleUserDefaultsGameTextStore.kt`
- Apple settings adapter: `sharedUI/src/iosMain/kotlin/com/sanskar/sudokunova/shared/ios/AppleUserDefaultsSettingsTextStore.kt`
- SwiftUI host sources: `iosApp/`

## CI contract

The final 2.0.14 pull-request head must be validated by the same exact SHA across:

- shared engine tests;
- shared gameplay/settings/persistence tests;
- shared Desktop and Web/Wasm compilation;
- Android shared integration;
- Web production distribution;
- iOS Simulator framework linking;
- Desktop application-image generation on Linux, Windows, and macOS;
- ordinary Android repository/release gates;
- API-35 Android instrumentation.

Android CI is authoritative for Android 2.0.14 release identity, repository guards, Android tests/lint/R8/APK/AAB, embedded identity, and unsigned artifact evidence. Cross-Platform CI proves repository target build/package paths. Android Instrumentation remains a separate exact-head connected-test gate.

## Evidence boundary

Cross-platform source/build success does **not** by itself prove:

- Apple production signing/provisioning, physical-device quality, or App Store acceptance;
- macOS signing/notarization/Gatekeeper behavior;
- Windows code signing, installer reputation, or clean-machine behavior;
- Linux distribution-repository compatibility or clean install/upgrade/remove behavior;
- intended browser/device compatibility or privacy-mode behavior;
- real assistive-technology, keyboard/focus, touch/pointer, large-font, resize, performance, settings persistence, or lifecycle quality on each target;
- store/public publication completion.

Record those results only after they actually occur. See `V2_0_14_RELEASE.md`, issue #43, and issue #34 for current evidence-gated work.
