# SudokuNova

> **Think. Solve. Master the Grid.**

SudokuNova is a modern, open-source Android Sudoku application built with Kotlin and Jetpack Compose. The project is designed for offline-first play, maintainable architecture, accessibility, deterministic testing, and long-term community development.

**Made by the Sanskar**

[![Android CI](https://github.com/sanskarIN/SudokuNova/actions/workflows/ci.yml/badge.svg)](https://github.com/sanskarIN/SudokuNova/actions/workflows/ci.yml)
[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.4.10-7F52FF.svg)](https://kotlinlang.org/)
[![Android](https://img.shields.io/badge/Android-API%2026%2B-3DDC84.svg)](https://developer.android.com/)

## Current Development Status

The repository is under active development. The current implementation includes the v0.1 foundation plus a substantial portion of core gameplay. Features are listed as implemented only when code exists in this repository.

### Implemented

- Native Android application in Kotlin
- Jetpack Compose + Material 3 UI
- Light, dark, system, and Material You dynamic color support
- Original launcher icon, monochrome icon, and splash artwork
- Platform-independent `sudoku-engine` module
- Immutable 9×9 board model
- Board validation and conflict detection
- Candidate calculation
- MRV backtracking solver
- No-solution and multiple-solution analysis
- Unique-solution verification
- Seeded deterministic puzzle generation
- Seven difficulty targets: Beginner through Extreme
- Educational Naked Single and Hidden Single hints with safe reveal fallback
- Responsive Sudoku board for phones and larger widths
- Number entry, notes, eraser, undo, redo, hints, pause, restart
- Timer, mistake counting, progress, autosave, and resume
- Automatic candidate-note cleanup
- Deterministic offline Daily Challenge seed
- Custom puzzle entry, contradiction checking, solvability/uniqueness validation, solution preview, and play flow
- DataStore-backed settings and local statistics
- Basic achievements derived from local statistics
- In-app Sudoku learning center
- About, privacy, contact, GitHub, and support information
- Unit tests for board, solver, generator, saved-game codec, and statistics
- GitHub Actions build/test/lint quality gate

### Planned for later milestones

- Full game-history database and filtering
- Challenge calendar/archive and weekly/special challenges
- Deeper achievement catalog and per-difficulty statistics
- Advanced strategy hints such as pairs, triples, X-Wing and Swordfish
- Additional Sudoku variants after Classic 9×9 is hardened
- Backup/import/export and shareable puzzle codes
- Expanded accessibility audits and instrumentation coverage
- Release signing workflow and production store assets

See [ROADMAP.md](ROADMAP.md) for milestone tracking.

## Technology Stack

- Kotlin 2.4.10
- Android Gradle Plugin 9.3.0
- Gradle 9.5.0
- Java 17
- Jetpack Compose
- Material 3
- AndroidX Lifecycle + ViewModel
- Navigation Compose
- Kotlin Coroutines / Flow
- Preferences DataStore
- JUnit / Kotlin Test
- Android Lint

## Architecture

SudokuNova currently uses two Gradle modules:

```text
SudokuNova/
├── app/                    # Android UI, navigation, persistence and app state
├── sudoku-engine/          # Platform-independent Sudoku logic and tests
├── docs/                   # Technical and contributor documentation
├── .github/                # CI and community templates
└── gradle/                 # Version catalog and Gradle wrapper
```

The Sudoku engine deliberately has no Android dependency so it can be tested quickly and reused by future platform clients. The Android layer uses immutable UI state, ViewModels, repositories, Flow, and Compose.

Detailed design: [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md)

## Requirements

- Android Studio compatible with AGP 9.3.0
- JDK 17
- Android SDK 37 for compilation
- Android device/emulator on API 26 or newer

## Build

Clone the repository:

```bash
git clone https://github.com/sanskarIN/SudokuNova.git
cd SudokuNova
```

Build a debug APK:

```bash
./gradlew :app:assembleDebug
```

Windows PowerShell / Command Prompt:

```bat
gradlew.bat :app:assembleDebug
```

Run the main verification tasks:

```bash
./gradlew :sudoku-engine:test :app:testDebugUnitTest :app:lintDebug :app:assembleDebug
```

More commands and tooling requirements are documented in [docs/BUILDING.md](docs/BUILDING.md).

## Privacy

SudokuNova is designed to work without an account, login, analytics, or advertising in the open-source base application. Current gameplay settings, active-game state, and statistics are stored locally through Android DataStore. No sensitive Android permissions are requested.

See [docs/PRIVACY.md](docs/PRIVACY.md).

## Accessibility

The project uses semantic cell descriptions, large touch targets where practical, contrast-aware states, theme support, adaptive layouts, and settings for high contrast/reduced motion. Accessibility work remains a release quality gate and will continue through dedicated TalkBack and large-font testing.

See [docs/ACCESSIBILITY.md](docs/ACCESSIBILITY.md).

## Contributing

Contributions to code, tests, documentation, accessibility, translations, and design are welcome. Start with [CONTRIBUTING.md](CONTRIBUTING.md) and read the [Code of Conduct](CODE_OF_CONDUCT.md).

Use Conventional Commits such as:

```text
feat: add puzzle history filter
fix: restore timer after process recreation
test: cover generator uniqueness regression
a11y: improve board semantics
docs: document release verification
```

## Security

Do not report exploitable vulnerabilities in public issues. Follow [SECURITY.md](SECURITY.md) for responsible disclosure guidance.

## Support SudokuNova

If SudokuNova helps you, you can support continued open-source development through Buy Me a Coffee:

**☕ https://buymeacoffee.com/sanskarIN**

You can also support the project by starring the repository, reporting reproducible bugs, improving documentation, translating strings, and contributing code.

## Developer & Contact

**Developer:** Sanskar  
**GitHub:** https://www.github.com/sanskarIN  
**Repository:** https://github.com/sanskarIN/SudokuNova

**Business:**
- sanskarin@outlook.in
- sanskarin.business@gmail.com

**Support:**
- supportramsandesh@gmail.com

## License

SudokuNova is licensed under the [MIT License](LICENSE). Third-party dependencies and assets remain subject to their respective licenses; see [THIRD_PARTY_NOTICES.md](THIRD_PARTY_NOTICES.md).

---

**SudokuNova — Think. Solve. Master the Grid.**  
**Made by the Sanskar**  
☕ https://buymeacoffee.com/sanskarIN
