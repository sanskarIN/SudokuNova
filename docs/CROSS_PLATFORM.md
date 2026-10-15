# Cross-Platform Development and Builds

SudokuNova 2.0.15 keeps the mature Android application as its primary production surface while continuing a shared Kotlin Multiplatform (KMP) engine and Compose Multiplatform gameplay surface for Android, iOS/iPadOS, Windows, macOS, Linux, and Web.

This document distinguishes **repository build support** from **production release evidence**. A target is repository-supported when its source, build configuration, and CI compilation/package path exist. Production signing, notarization, store submission, physical-device QA, browser compatibility QA, and distribution approval still require real external evidence.

## Current platform matrix

| Platform | Repository target | Main build entry point | Repository status |
| --- | --- | --- | --- |
| Android | Android API 26+ | `:app` + `:sharedUI` Android KMP library | Mature primary app; shared UI host available |
| ChromeOS | Android compatibility | Android APK/AAB | Uses Android compatibility path |
| iOS | Kotlin/Native arm64 | `SudokuNovaSharedUI.framework` | Shared framework + SwiftUI host sources |
| iPadOS | Kotlin/Native arm64 | same Apple target family | Shared framework + SwiftUI host sources |
| Windows | Desktop JVM | `:sharedUI` desktop target | Desktop application image/package path |
| macOS | Desktop JVM | `:sharedUI` desktop target | Desktop application image/package path |
| Linux | Desktop JVM | `:sharedUI` desktop target | Desktop application image/package path |
| Web | Kotlin/Wasm | `:sharedUI` Wasm browser target | Browser distribution path; runtime QA remains separate |

## Architecture

1. `sudoku-engine` is Kotlin Multiplatform and owns Classic Sudoku correctness, generation, solving, difficulty logic, teaching/hints, puzzle-code transport, and validated puzzle exchange.
2. `sharedUI` contains portable Compose Multiplatform gameplay state/UI, localization, semantics, keyboard-grid handling, active-game persistence, settings, and puzzle-exchange session contracts.
3. `app` remains the Android production application and retains Room/DataStore, full navigation, challenges, learning/statistics, backup/transfer, mature Android accessibility integration, Macrobenchmark, and release tooling.
4. `iosApp` contains SwiftUI host sources that bridge to `SudokuNovaSharedUI.framework`.

`MainActivity` remains Android's launcher. `CrossPlatformActivity` is intentionally non-exported and hosts the shared UI used by staged Android and other targets.

## Shared gameplay capabilities

The portable UI/state includes:

- generated Classic 9×9 puzzles and seven difficulties;
- fixed/editable cells, number-pad input, candidate notes, peer-note cleanup;
- conflict feedback with visual, semantic, and non-color `!` indication;
- erase, bounded undo, engine-backed hints, reset, and new game;
- responsive board sizing;
- typed locale-neutral gameplay status events;
- English/Hindi resources and translation parity guards;
- localized cell semantics and selected-state semantics;
- deterministic grid selection movement;
- focusable arrow navigation, `1`–`9` entry, `N` Notes, `H` Hint, and Backspace/Delete erase;
- `SNG1` generated active-game snapshots and `SNG2` imported-session snapshots;
- native local active-game adapters for staged Android, Desktop, Web/Wasm, and Apple hosts;
- `SNS1` settings encoding with native local settings adapters;
- common restore/autosave ownership;
- localized persisted System/Light/Dark theme selection;
- validated `SNP1` puzzle-code import/export through `PuzzleExchangeService`.

## Feature-parity matrix

“Shared” means the behavior lives in common Kotlin/Compose code; it does not imply every target has completed runtime QA.

| Capability | Mature Android app | Shared targets | Current boundary |
| --- | --- | --- | --- |
| Classic 9×9 generation/solving | Yes | Yes | Shared `sudoku-engine` |
| Seven difficulty levels | Yes | Yes | Shared engine + localized picker |
| Number-pad entry, erase, reset | Yes | Yes | Shared gameplay state/UI |
| Candidate notes + peer cleanup | Yes | Yes | Shared gameplay state/UI |
| Undo | Yes | Yes | Shared bounded in-memory history |
| Engine-backed hints | Yes | Yes | Shared hint engine |
| Conflict feedback | Yes | Yes | Shared visual + semantic + non-color marker |
| English/Hindi text | Yes | Yes | Shared resources + parity guard |
| Active-game save model | Yes | Yes at source level | `SNG1`/`SNG2` + native local adapters; lifecycle QA still required |
| Shared user-settings model | Yes | Yes at source level | `SNS1` + native local adapters; mature Android DataStore remains separate |
| System/Light/Dark theme | Yes | Yes at source level | Common controls apply/persist shared theme; runtime QA remains target-specific |
| Full settings behavior breadth | Yes | Partial | Some Android-only behavior still needs target-safe parity |
| Room/DataStore persistence | Yes | No | Android-only; common code uses narrow store interfaces |
| Puzzle-code format | Yes | Yes | `SNP1` compatibility retained |
| Puzzle-code acceptance | Android flow | Yes | Malformed, unsolvable, and non-unique imports are rejected |
| Imported/custom puzzle session | Yes | Yes at source level | `SharedGameState` retains canonical SNP1 provenance and restores from it |
| Clipboard/share/file-picker adapters | Yes | Not yet | Native interaction adapters remain the next parity boundary |
| Challenges/custom puzzle UI | Yes | Not yet | Shared presentation migration remains pending |
| Learning/statistics UI | Yes | Not yet | Presentation/persistence remains pending |
| History/saved-puzzle persistence | Yes | Not yet | Shared active-game/settings stores do not replace Android Room |
| Backup/restore transfer | Yes | Not yet | Requires platform file/share adapters and compatibility design |
| Accessibility source semantics | Mature Android coverage | Shared baseline implemented | Real assistive-technology QA remains external evidence |
| Keyboard/focus navigation | Mature Android coverage | Common mapping implemented | Real target focus/hardware QA remains pending |
| Macrobenchmark/performance evidence | Yes | No shared equivalent | Android measurements must not be generalized |
| Production signing/store release | Android process documented | No | Per-platform external evidence required |

## Shared puzzle exchange contract

`PuzzleCodeCodec` is the platform-independent `SNP1` transport codec. `PuzzleExchangeService` is the common acceptance boundary:

1. decode and validate SNP1 version, bounds, payload, checksum, difficulty, and board validity;
2. analyze with `SudokuSolver` up to two solutions;
3. reject anything that does not have exactly one solution;
4. return the puzzle, uniquely proven solution, difficulty, and assessment when accepted.

`SharedGameState.importPuzzleCode()` stores the canonical code as `sourceCode`, reconstructs the imported puzzle/solution, resets transient gameplay state, and publishes a typed import status. Invalid input does not replace the current puzzle.

See [Shared Cross-Platform Puzzle Exchange](SHARED_PUZZLE_EXCHANGE.md).

## Shared active-game persistence

Generated sessions use `SNG1`:

```text
SNG1|<difficulty>|<seed>|<current-board>|<selected-index>|<notes-mode>|<notes>
```

Imported sessions use `SNG2`:

```text
SNG2|<difficulty>|<seed>|<current-board>|<selected-index>|<notes-mode>|<notes>|<source-code>
```

`SNG1` remains fully readable. Generated sessions continue to encode as SNG1 so older stored data is not rewritten unnecessarily.

For SNG2, the canonical validated SNP1 source code is authoritative for reconstructing the starting puzzle and solution. Restore validates the source code again, checks fixed clues and notes before mutation, and rejects malformed/invalid snapshots fail-closed.

Platform storage remains behind `SharedGameTextStore`; current adapters are private `SharedPreferences` on staged Android, `java.util.prefs.Preferences` on Desktop, browser `localStorage` on Web/Wasm, and `NSUserDefaults` on Apple.

## Shared settings persistence

`SharedUserSettings` and `SharedSettingsState` provide the portable settings vocabulary and observable common state. `SharedSettingsCodec` uses bounded deterministic `SNS1` encoding. Current native local adapters mirror the active-game storage boundaries.

System/Light/Dark theme selection is persisted and applied by common Compose code. Other settings fields remain progressive parity work and are not claimed complete merely because their schema exists.

## Accessibility and input boundary

Portable cells expose localized row/column/value-or-notes/fixed-or-editable/selected/conflict descriptions. Conflicts use an explicit `!` marker as well as color/semantics. Notes mode and theme selection expose selected state.

The shared grid handles:

- arrows → deterministic one-cell navigation;
- `1`–`9` → the same `SharedGameState.enter` path as the visible number pad;
- `N` → Notes mode;
- `H` → Hint;
- Backspace/Delete → clue-protected erase.

Visible controls remain available. TalkBack, VoiceOver, desktop accessibility APIs, browser accessibility trees, hardware keyboards, pointer/touch, large-font, resize, and runtime focus behavior remain evidence-gated.

## Localization

The shared source of truth is `sharedUI/src/commonMain/composeResources/`:

- `values/strings.xml` — English fallback;
- `values-hi/strings.xml` — Hindi;
- generated package `com.sanskar.sudokunova.shared.resources`;
- `SharedGameStatus` stays locale-neutral;
- `scripts/verify_translations.py` enforces key and printf-placeholder parity.

## Toolchain

- JDK 17;
- Kotlin 2.4.10;
- Android Gradle Plugin 9.3.1;
- compile/target SDK 37;
- minimum Android SDK 26;
- Compose Multiplatform 1.11.1;
- Room 2.8.4 for the mature Android persistence layer;
- repository Gradle wrapper.

## Common build and test commands

```bash
./gradlew :sudoku-engine:desktopTest --stacktrace
./gradlew :sharedUI:desktopTest --stacktrace
./gradlew :sharedUI:compileKotlinDesktop :sharedUI:compileKotlinWasmJs --stacktrace
./gradlew :app:assembleDebug --stacktrace
./gradlew :app:assembleRelease :app:bundleRelease --stacktrace
./gradlew :sharedUI:createDistributable --stacktrace
./gradlew :sharedUI:wasmJsBrowserDistribution --stacktrace
```

On macOS, shared Apple framework commands include:

```bash
./gradlew :sharedUI:linkDebugFrameworkIosSimulatorArm64 --stacktrace
./gradlew :sharedUI:linkReleaseFrameworkIosArm64 --stacktrace
```

## Platform entry points

- Android mature launcher: `app/src/main/java/com/sanskar/sudokunova/MainActivity.kt`
- Android shared host: `app/src/main/java/com/sanskar/sudokunova/CrossPlatformActivity.kt`
- Desktop entry point: `sharedUI/src/desktopMain/kotlin/com/sanskar/sudokunova/shared/desktop/Main.kt`
- Web entry point: `sharedUI/src/wasmJsMain/kotlin/com/sanskar/sudokunova/shared/web/Main.kt`
- Apple Compose bridge: `sharedUI/src/iosMain/kotlin/com/sanskar/sudokunova/shared/ios/MainViewController.kt`
- SwiftUI host sources: `iosApp/`

## CI contract

The final 2.0.15 pull-request head must be validated by the same exact SHA across:

- Android CI;
- Android Instrumentation;
- Cross-Platform CI.

The combined scope covers repository guards, translation parity, engine/shared tests, Android tests/lint/R8/APK/AAB identity, Web/Wasm distribution, iOS Simulator framework linking, and Desktop application-image generation on Linux/Windows/macOS.

Any later commit invalidates older exact-head evidence.

## Evidence boundary

Cross-platform source/build success does **not** prove:

- Apple production signing/provisioning, physical-device quality, or App Store acceptance;
- macOS signing/notarization/Gatekeeper behavior;
- Windows code signing, installer reputation, or clean-machine behavior;
- Linux distribution compatibility or clean install/upgrade/remove behavior;
- intended browser/device compatibility or privacy-mode behavior;
- real assistive-technology, keyboard/focus, touch/pointer, large-font, resize, performance, settings persistence, or lifecycle quality on each target;
- store/public publication completion.

Record those results only after they actually occur. See [2.0.15 Release Line](V2_0_15_RELEASE.md), [Shared Cross-Platform Puzzle Exchange](SHARED_PUZZLE_EXCHANGE.md), and issue #34 for the remaining parity/evidence work.
