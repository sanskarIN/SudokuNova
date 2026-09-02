# SudokuNova

> **Think. Solve. Master the Grid.**

SudokuNova is an open-source, offline-first Sudoku project built with Kotlin, Kotlin Multiplatform, Jetpack Compose, and Compose Multiplatform. Android remains the mature primary production surface while shared engine/gameplay foundations are extended across Android, iOS/iPadOS, Windows, macOS, Linux, and Web.

**Made by the Sanskar**

[![Android CI](https://github.com/sanskarIN/SudokuNova/actions/workflows/ci.yml/badge.svg)](https://github.com/sanskarIN/SudokuNova/actions/workflows/ci.yml)
[![Cross-Platform CI](https://github.com/sanskarIN/SudokuNova/actions/workflows/cross-platform.yml/badge.svg)](https://github.com/sanskarIN/SudokuNova/actions/workflows/cross-platform.yml)
[![Android Instrumentation](https://github.com/sanskarIN/SudokuNova/actions/workflows/instrumentation.yml/badge.svg)](https://github.com/sanskarIN/SudokuNova/actions/workflows/instrumentation.yml)
[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.4.10-7F52FF.svg)](https://kotlinlang.org/)
[![Android](https://img.shields.io/badge/Android-API%2026%2B-3DDC84.svg)](https://developer.android.com/)

## Current Development Status

The repository is preparing **SudokuNova 2.0.15** on draft PR #45 from the merged 2.0.14 settings/keyboard parity line.

Current source contract:

```text
applicationId          = in.sanskar.sudokunova
versionCode            = 2015
versionName            = 2.0.15
Desktop packageVersion = 2.0.15
minSdk                 = 26
targetSdk              = 37
compileSdk             = 37
JDK/JVM                = 17
```

### 2.0.15 focus

- validated `SNP1` puzzle import/export in the shared engine/UI;
- imported puzzle provenance retained in `SharedGameState`;
- backward-compatible `SNG1` active-game snapshots;
- new `SNG2` snapshots for imported-session restoration;
- shared English/Hindi puzzle-exchange UI;
- fail-closed import/restore validation and regression coverage;
- release-contract, CI, and documentation alignment for version 2.0.15.

**2.0.15 is not claimed as publicly released merely because source metadata, build paths, or CI exist.** Production signing, representative real-device QA, accessibility/performance evidence, platform signing/notarization, store validation, final SHIP/tagging, and publication require separate real evidence.

See [2.0.15 Release Line](docs/V2_0_15_RELEASE.md), [Shared Puzzle Exchange](docs/SHARED_PUZZLE_EXCHANGE.md), [Cross-Platform Development](docs/CROSS_PLATFORM.md), and [What Changed](what_changed.md).

## Platform Architecture

| Platform | Repository path | Current role |
| --- | --- | --- |
| Android | `app/` + KMP modules | Mature primary application; API 26+ |
| ChromeOS | Android compatibility | Uses Android application path |
| iOS/iPadOS | `sharedUI` framework + `iosApp/` | Shared framework/SwiftUI host foundation |
| Windows | `sharedUI` Desktop JVM | Compose Desktop application/package path |
| macOS | `sharedUI` Desktop JVM | Compose Desktop application/package path |
| Linux | `sharedUI` Desktop JVM | Compose Desktop application/package path |
| Web | `sharedUI` Kotlin/Wasm | Browser/Wasm distribution path |

Repository build support is not production distribution evidence. Apple signing/provisioning, macOS notarization, Windows signing/reputation, Linux clean-install/distribution QA, and browser/device compatibility remain separate checks.

## Module Graph

```text
:sudoku-engine   Kotlin Multiplatform Sudoku correctness/domain
      ↑
:sharedUI        Compose Multiplatform gameplay/persistence/settings/exchange UI
      ↑
:app             Mature Android application

:macrobenchmark  Android release-like performance harness

iosApp/          SwiftUI host sources for SudokuNovaSharedUI.framework
```

### `sudoku-engine`

Platform-independent correctness-critical code includes:

- immutable Classic 9×9 board model;
- validation, conflicts, candidates, serialization;
- solving and bounded solution counting;
- unique-solution validation;
- seeded deterministic generation;
- seven difficulties from Beginner through Extreme;
- logical analysis and structured teaching evidence;
- hints plus explicit Reveal fallback;
- deterministic practice catalog;
- versioned `SNP1` puzzle codes;
- `PuzzleExchangeService`, which accepts imported codes only after exactly one solution is proven.

### `sharedUI`

Portable Compose foundation includes:

- generated games and seven difficulty levels;
- responsive Sudoku grid and number pad;
- fixed/editable cells and notes;
- conflict feedback and accessibility semantics;
- erase, bounded undo, hints, reset, new game;
- English/Hindi resources;
- keyboard navigation and digit/Notes/Hint/erase shortcuts;
- `SNG1` generated active-game persistence;
- `SNG2` imported-session provenance persistence;
- `SNS1` local settings persistence;
- staged Android/Desktop/Web/Apple local-storage adapters;
- validated puzzle-code import/export UI.

### `app`

The Android application retains the mature product surface, including navigation, Room/DataStore persistence, history/saved puzzles, challenges/custom puzzles, learning/statistics/achievements, backup/transfer/import/export, Android accessibility, localization, themes/settings, release signing, artifact verification, and Macrobenchmark infrastructure.

## Current Cross-Platform Parity Boundary

Implemented in shared/common code:

- Classic Sudoku correctness and unique generation;
- seven difficulty levels;
- portable gameplay state/UI;
- English/Hindi shared resources;
- cell/Notes/theme selected semantics;
- non-color conflict communication;
- active-game local persistence abstractions/adapters;
- local user-settings format/state/storage adapters;
- persisted System/Light/Dark shared theme;
- validated `SNP1` puzzle-code exchange;
- imported-session provenance and `SNG2` restore;
- arrow navigation, digit entry, Notes/Hint keys, and Backspace/Delete erase.

Still intentionally incomplete for shared targets:

- clipboard/share/file-picker adapters;
- common history/saved-puzzle persistence parity;
- challenges/custom-puzzle presentation parity;
- learning/statistics presentation/persistence;
- behavioral parity for every stored settings field;
- reliable target runtime input/focus/resize/accessibility/browser E2E evidence;
- production Apple/Windows/macOS/Linux/Web distribution evidence.

See issue #34 and [CROSS_PLATFORM.md](docs/CROSS_PLATFORM.md).

## Build Quick Start

Clone:

```bash
git clone https://github.com/sanskarIN/SudokuNova.git
cd SudokuNova
```

### Shared tests

```bash
./gradlew :sudoku-engine:desktopTest :sharedUI:desktopTest --stacktrace
```

### Shared Desktop/Web compilation

```bash
./gradlew :sharedUI:compileKotlinDesktop :sharedUI:compileKotlinWasmJs --stacktrace
```

### Android debug APK

```bash
./gradlew :app:assembleDebug --stacktrace
```

### Android 2.0.15 release verification outputs

```bash
./gradlew :app:assembleRelease :app:bundleRelease --stacktrace
```

Ordinary CI may intentionally produce an unsigned release APK. Production signing is protected and secret-backed; signing keys/passwords do not belong in Git.

### Desktop application image

```bash
./gradlew :sharedUI:createDistributable --stacktrace
```

### Web/Wasm production distribution

```bash
./gradlew :sharedUI:wasmJsBrowserDistribution --stacktrace
```

### iOS Simulator framework

On macOS:

```bash
./gradlew :sharedUI:linkDebugFrameworkIosSimulatorArm64 --stacktrace
```

Complete host requirements and platform evidence boundaries are documented in [BUILDING.md](docs/BUILDING.md) and [CROSS_PLATFORM.md](docs/CROSS_PLATFORM.md).

## Automated Quality Gates

Three PR workflow families protect the current line:

1. **Android CI** — repository guards, shared tests/compile, Android tests/lint/debug/release/R8/AAB builds, and exact 2015/2.0.15 artifact identity evidence.
2. **Android Instrumentation** — connected Compose/Room regression suite.
3. **Cross-Platform CI** — shared tests, Android shared integration, Web production distribution, iOS Simulator framework, and Desktop application images on Linux/Windows/macOS.

A PR is merge-verified only when all required workflow families are green on the **same exact final head SHA**. Any later commit invalidates older runs as final evidence.

## Repository Guards

Useful local checks:

```bash
python scripts/verify_no_secrets.py
python -m unittest scripts.tests.test_verify_documentation_links
python -m unittest scripts.tests.test_verify_documentation_coverage
python -m unittest scripts.tests.test_verify_release_contract
python -m unittest scripts.tests.test_verify_release_outputs
python -m unittest scripts.tests.test_verify_release_cli_validation
python -m unittest scripts.tests.test_verify_translations
python scripts/verify_documentation_links.py
python scripts/verify_documentation_coverage.py
python scripts/verify_release_contract.py
python scripts/verify_translations.py
```

## Complete Documentation

Start at **[docs/README.md](docs/README.md)**.

High-value references:

- [2.0.15 Release Line](docs/V2_0_15_RELEASE.md)
- [Shared Puzzle Exchange](docs/SHARED_PUZZLE_EXCHANGE.md)
- [Cross-Platform Development and Builds](docs/CROSS_PLATFORM.md)
- [Shared Active-Game Persistence](docs/SHARED_PERSISTENCE.md)
- [Shared User Settings](docs/SHARED_SETTINGS.md)
- [Data Formats](docs/DATA_FORMATS.md)
- [Architecture](docs/ARCHITECTURE.md)
- [Building](docs/BUILDING.md)
- [Testing](docs/TESTING.md)
- [CI/CD](docs/CI_CD.md)
- [Accessibility](docs/ACCESSIBILITY.md)
- [Localization](docs/LOCALIZATION.md)
- [Production Signing](docs/PRODUCTION_SIGNING.md)
- [Production Release Validation](docs/PRODUCTION_RELEASE_VALIDATION.md)
- [Releasing](docs/RELEASING.md)
- [What Changed](what_changed.md)

## Release Evidence Boundary

Do not claim any of the following without the corresponding real result:

- Android production certificate identity and signed-build validation;
- physical-device Android lifecycle/accessibility/performance quality;
- Play Store acceptance/publication;
- complete signed iOS/iPadOS execution or App Store acceptance;
- macOS signing/notarization;
- Windows signing/reputation/clean-machine installation;
- Linux clean install/upgrade/remove compatibility;
- intended Web browser/device/accessibility/persistence compatibility;
- final public release/tag publication.

## Contributing

Read [CONTRIBUTING.md](CONTRIBUTING.md), [Development Setup](docs/DEVELOPMENT_SETUP.md), and [Maintainer Guide](docs/MAINTAINER_GUIDE.md). Changes should include the narrowest relevant tests/documentation, preserve correctness/security/accessibility/release boundaries, and pass exact-head CI before merge.

## License

SudokuNova is released under the [MIT License](LICENSE). Review [THIRD_PARTY_NOTICES.md](THIRD_PARTY_NOTICES.md) for third-party dependency/tool notices.

## Project Links

- GitHub: https://github.com/sanskarIN/SudokuNova
- Developer profile: https://www.github.com/sanskarIN
- LinkedIn: https://www.linkedin.com/in/sanskarIN
- YouTube: https://youtube.com/@Sanskar-in
- X: https://www.x.com/Sanskar_in
- Buy Me a Coffee: https://buymeacoffee.com/sanskarIN
- Business: `sanskarin@outlook.in`
- Business: `sanskarin.business@gmail.com`
- Support: `supportramsandesh@gmail.com`

---

**SudokuNova — Think. Solve. Master the Grid.**  
**Made by the Sanskar**
