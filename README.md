# SudokuNova

> **Think. Solve. Master the Grid.**

SudokuNova is an open-source, offline-first Sudoku project built with Kotlin, Kotlin Multiplatform, Jetpack Compose, and Compose Multiplatform. The mature Android application remains the primary production surface while the shared Sudoku engine and portable gameplay UI are being extended across Android, iOS/iPadOS, Windows, macOS, Linux, and Web.

**Made by the Sanskar**

[![Android CI](https://github.com/sanskarIN/SudokuNova/actions/workflows/ci.yml/badge.svg)](https://github.com/sanskarIN/SudokuNova/actions/workflows/ci.yml)
[![Cross-Platform CI](https://github.com/sanskarIN/SudokuNova/actions/workflows/cross-platform.yml/badge.svg)](https://github.com/sanskarIN/SudokuNova/actions/workflows/cross-platform.yml)
[![Android Instrumentation](https://github.com/sanskarIN/SudokuNova/actions/workflows/instrumentation.yml/badge.svg)](https://github.com/sanskarIN/SudokuNova/actions/workflows/instrumentation.yml)
[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.4.10-7F52FF.svg)](https://kotlinlang.org/)
[![Android](https://img.shields.io/badge/Android-API%2026%2B-3DDC84.svg)](https://developer.android.com/)

## Current Development Status

The verified Android **2.0.12** release/documentation baseline was merged to `main` through PR #30 at merge commit:

```text
5fdafd77332b4889c5bd64bd23b1c4869ade0962
```

Its Android source contract remains:

```text
applicationId = in.sanskar.sudokunova
versionCode   = 2012
versionName   = 2.0.12
minSdk        = 26
targetSdk     = 37
compileSdk    = 37
JDK/JVM       = 17
```

The active cross-platform continuation is developed separately from that verified baseline. It adds Kotlin/Compose Multiplatform targets without replacing the mature Android launcher or weakening the 2.0.12 signing/release guards.

**2.0.12 is not claimed as publicly released merely because its source contract and repository gates exist.** Production signing, physical-device QA, representative performance evidence, store validation, final release approval/tagging, and publication require separate real evidence.

## Platform Architecture

| Platform | Repository path | Current role |
| --- | --- | --- |
| Android | `app/` + KMP modules | Mature primary application; API 26+ |
| ChromeOS | Android compatibility | Uses the Android application path |
| iOS | `sharedUI` iOS framework + `iosApp/` host sources | Shared framework/SwiftUI host foundation |
| iPadOS | same Apple target family | Shared framework/SwiftUI host foundation |
| Windows | `sharedUI` Desktop JVM | Compose Desktop application image/package path |
| macOS | `sharedUI` Desktop JVM | Compose Desktop application image/package path |
| Linux | `sharedUI` Desktop JVM | Compose Desktop application image/package path |
| Web | `sharedUI` Kotlin/Wasm | Browser/Wasm distribution path |

Repository build support is not the same as production distribution evidence. Apple signing/provisioning, macOS notarization, Windows signing/reputation, Linux distribution-channel QA, and broad browser/device compatibility must be verified separately.

See **[Cross-Platform Development and Builds](docs/CROSS_PLATFORM.md)**.

## Module Graph

```text
:sudoku-engine   Kotlin Multiplatform Sudoku domain/solver/generator
      ↑
:sharedUI        Compose Multiplatform gameplay state/UI
      ↑
:app             Mature Android application

:macrobenchmark  Android release-like performance test module

iosApp/          SwiftUI host sources for SudokuNovaSharedUI.framework
```

### `sudoku-engine`

Shared correctness-critical domain code:

- immutable Classic 9×9 board model;
- validation, conflicts, candidates, serialization;
- solving and solution counting;
- unique-solution checks;
- seeded deterministic generation;
- seven difficulty targets from Beginner through Extreme;
- logical difficulty analysis;
- structured teaching evidence;
- advanced hints and Reveal fallback separation;
- deterministic practice catalog;
- versioned puzzle-code support.

The engine has no Android/Compose/UI/persistence dependency.

### `sharedUI`

Portable Compose gameplay foundation:

- generated puzzles and difficulty selection;
- responsive Sudoku grid;
- fixed/editable cells;
- number entry;
- candidate notes and peer-note cleanup;
- conflict feedback;
- erase;
- bounded undo;
- engine-backed hints;
- reset/new game;
- Desktop, iOS, and Web entry points.

The shared surface intentionally does not yet pretend to contain every mature Android-only capability.

### `app`

The Android application retains the complete established product surface, including:

- full Compose navigation/screens;
- local Room/DataStore persistence;
- history/saved puzzles;
- challenges/custom puzzles;
- learning center and advanced hint presentation;
- statistics/achievements;
- backup/transfer/import/export;
- English/Hindi Android resources;
- accessibility integration;
- Android release/signing/artifact verification.

`MainActivity` remains the exported production launcher. `CrossPlatformActivity` is non-exported and hosts the portable UI for staged integration testing.

## Major Implemented Capabilities

### Core Sudoku

- Classic 9×9 rules;
- deterministic puzzle generation;
- unique-solution verification;
- conflict/candidate analysis;
- seven calibrated difficulty levels;
- logical solving/analysis;
- deterministic regression corpora.

### Gameplay

- responsive board layouts;
- cell-first and number-first Android input;
- notes, erase, undo/redo, pause/restart;
- timer, mistakes, hints, progress, autosave/resume;
- automatic note cleanup;
- hardware keyboard support;
- haptics/sound preferences;
- high contrast/reduced motion;
- light/dark/system/dynamic-color support.

### Teaching and Advanced Hints

The engine carries structured evidence for techniques including:

- Naked Single;
- Hidden Single;
- Naked Pair;
- Pointing Pair/Triple;
- Box-Line Reduction;
- Hidden Pair;
- Naked Triple;
- Hidden Triple;
- X-Wing.

Evidence records source/target cells, units, candidate eliminations, and placements. The Android layer turns that evidence into localized accessible explanations.

### Learning Center

The mature Android app includes:

- Sudoku/candidate lessons;
- technique lessons;
- deterministic offline practice;
- correct/incorrect practice state;
- per-technique local progress;
- mastery presentation;
- learning reset isolated from gameplay data.

### Challenges and Custom Puzzles

- deterministic Daily Challenge;
- challenge archive/history foundations;
- weekly challenges;
- custom puzzle editor;
- contradiction/solvability/uniqueness validation;
- custom save/replay;
- Room-backed local history/saved data;
- statistics, streaks, and achievements.

### Import, Export, and Backup

- versioned puzzle codes;
- bounded input validation;
- checksum/format validation;
- clipboard/share/document-picker flows on Android;
- versioned local backup/restore;
- duplicate-safe restore behavior.

### Accessibility, Localization, and Quality

- English/Hindi Android resource parity checks;
- semantic Sudoku cell descriptions;
- teaching-evidence semantics;
- selected-state semantics;
- large-text/adaptive-layout hardening;
- release-like Macrobenchmark harness;
- deterministic repository/documentation/release guards;
- API-35 connected Android test workflow;
- cross-platform hosted build matrix;
- fail-closed Android production-signing configuration;
- protected certificate-bound Android release-validation workflow.

## Build Quick Start

Clone:

```bash
git clone https://github.com/sanskarIN/SudokuNova.git
cd SudokuNova
```

### Shared correctness tests

```bash
./gradlew :sudoku-engine:desktopTest :sharedUI:desktopTest
```

### Shared Desktop/Web compilation

```bash
./gradlew :sharedUI:compileKotlinDesktop :sharedUI:compileKotlinWasmJs
```

### Android debug APK

```bash
./gradlew :app:assembleDebug
```

Output:

```text
app/build/outputs/apk/debug/app-debug.apk
```

### Android release APK/AAB

```bash
./gradlew :app:assembleRelease :app:bundleRelease
```

Normal CI may intentionally produce an unsigned release APK. Production signing is protected and secret-backed; no signing key/password belongs in Git.

### Desktop application image

```bash
./gradlew :sharedUI:createDistributable
```

### Web/Wasm production distribution

```bash
./gradlew :sharedUI:wasmJsBrowserDistribution
```

### iOS Simulator framework

On macOS:

```bash
./gradlew :sharedUI:linkDebugFrameworkIosSimulatorArm64
```

Complete platform requirements and Windows PowerShell equivalents are documented in [BUILDING.md](docs/BUILDING.md) and [CROSS_PLATFORM.md](docs/CROSS_PLATFORM.md).

## Automated Quality Gates

Three pull-request workflows protect the cross-platform line:

1. **Android CI** — repository guards, shared tests/compile, Android JVM tests, test-APK/Macrobenchmark compilation, lint, debug/release/R8/AAB builds, and 2.0.12 artifact identity evidence.
2. **Android Instrumentation** — API-35 connected Compose/Room regression suite.
3. **Cross-Platform CI** — shared tests, Android integration, Web production distribution, iOS Simulator framework, and Desktop application images on Linux/Windows/macOS.

A PR is considered verified only when all required jobs are green on the **same exact final head SHA**. A later commit invalidates older runs as final evidence.

See [CI/CD](docs/CI_CD.md) and [Testing](docs/TESTING.md).

## Repository Guards

Useful local checks:

```bash
python scripts/verify_no_secrets.py
python -m unittest scripts.tests.test_verify_documentation_links
python -m unittest scripts.tests.test_verify_documentation_coverage
python -m unittest scripts.tests.test_verify_release_contract
python -m unittest scripts.tests.test_verify_release_outputs
python -m unittest scripts.tests.test_verify_release_cli_validation
python scripts/verify_documentation_links.py
python scripts/verify_documentation_coverage.py
python scripts/verify_release_contract.py
python scripts/verify_translations.py
```

Documentation ownership is fail closed: every tracked path must map to maintained documentation, and every detailed documentation page must remain discoverable from the documentation index.

## Complete Documentation

Start at **[docs/README.md](docs/README.md)**.

High-value references:

- [Cross-Platform Development and Builds](docs/CROSS_PLATFORM.md)
- [2.0.12 Release Line](docs/V2_0_12_RELEASE.md)
- [Architecture](docs/ARCHITECTURE.md)
- [Project Structure](docs/PROJECT_STRUCTURE.md)
- [Repository File Reference](docs/REPOSITORY_FILE_REFERENCE.md)
- [Building](docs/BUILDING.md)
- [Testing](docs/TESTING.md)
- [CI/CD](docs/CI_CD.md)
- [Sudoku Engine](docs/SUDOKU_ENGINE.md)
- [Learning and Advanced Hints](docs/LEARNING_AND_HINTS.md)
- [Data Storage](docs/DATA_STORAGE.md)
- [Data Formats](docs/DATA_FORMATS.md)
- [Backup and Restore](docs/BACKUP_RESTORE.md)
- [Accessibility](docs/ACCESSIBILITY.md)
- [Localization](docs/LOCALIZATION.md)
- [Performance Benchmarking](docs/PERFORMANCE_BENCHMARKING.md)
- [Production Signing](docs/PRODUCTION_SIGNING.md)
- [Production Release Validation](docs/PRODUCTION_RELEASE_VALIDATION.md)
- [Play Store Release Preparation](docs/PLAY_STORE_RELEASE.md)
- [Releasing](docs/RELEASING.md)
- [Maintainer Guide](docs/MAINTAINER_GUIDE.md)
- [Documentation Standards](docs/DOCUMENTATION_STANDARDS.md)
- [What Changed](what_changed.md)

## Release Evidence Boundary

The repository deliberately distinguishes source/build support from real production evidence.

Do not claim any of the following without the corresponding real result:

- Android production certificate identity;
- physical-device Android lifecycle/accessibility/performance quality;
- Play Store acceptance/publication;
- complete signed iOS/iPadOS app execution or App Store acceptance;
- macOS signing/notarization;
- Windows signing/reputation;
- Linux distribution-channel compatibility;
- broad Web browser/device compatibility;
- final public release/tag publication.

See `what_changed.md` for the active evidence ledger.

## Historical Verification

The repository preserves earlier exact-head release-engineering evidence in dedicated historical documentation. Historical green runs remain useful for the commits they tested but are never reused as proof for a newer head.

The verified Android 2.0.12 baseline merged from PR #30 and is the foundation for the current cross-platform work.

## Contributing

Read:

- [CONTRIBUTING.md](CONTRIBUTING.md)
- [docs/CONTRIBUTING_GUIDE.md](docs/CONTRIBUTING_GUIDE.md)
- [docs/DEVELOPMENT_SETUP.md](docs/DEVELOPMENT_SETUP.md)
- [docs/MAINTAINER_GUIDE.md](docs/MAINTAINER_GUIDE.md)

Changes should include the narrowest relevant tests/documentation, preserve release/security/accessibility boundaries, and pass exact-head CI before merge.

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
