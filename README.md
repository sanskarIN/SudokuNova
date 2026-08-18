# SudokuNova

> **Think. Solve. Master the Grid.**

SudokuNova is a modern, open-source Android Sudoku application built with Kotlin and Jetpack Compose. The project is designed for offline-first play, maintainable architecture, accessibility, deterministic testing, safe local data handling, and long-term community development.

**Made by the Sanskar**

[![Android CI](https://github.com/sanskarIN/SudokuNova/actions/workflows/ci.yml/badge.svg)](https://github.com/sanskarIN/SudokuNova/actions/workflows/ci.yml)
[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.4.10-7F52FF.svg)](https://kotlinlang.org/)
[![Android](https://img.shields.io/badge/Android-API%2026%2B-3DDC84.svg)](https://developer.android.com/)

## Current Development Status

The current development line is **v0.8.0 — Learning and Advanced Hints**. v0.1 through v0.7 functionality is already integrated on `main`; v0.8 is being verified through the repository's standard Android CI and API-35 connected instrumentation gates before merge.

Features below are listed as implemented only when code exists in this repository.

## Implemented

### Core Sudoku

- Native Android application in Kotlin
- Jetpack Compose + Material 3 UI
- Platform-independent `sudoku-engine` module
- Immutable Classic 9×9 board model
- Board validation, conflicts, candidates, and serialization
- MRV backtracking solver
- No-solution and multiple-solution analysis
- Unique-solution verification
- Seeded deterministic puzzle generation
- Seven calibrated difficulty targets: Beginner through Extreme
- Deterministic logical-analysis corpus and generator tests

### Gameplay

- Responsive Sudoku board for phones and larger widths
- Cell-first and number-first input modes
- Number entry, notes, eraser, undo, redo, pause, restart
- Timer, mistake counting/limits, progress, autosave, and resume
- Automatic candidate-note cleanup
- Hardware-keyboard navigation and game shortcuts
- Haptics and optional sound feedback
- High-contrast and reduced-motion preferences
- Light, dark, system, and Material You dynamic color support

### v0.8 Teaching and Advanced Hints

- Structured, platform-independent `TeachingStep` evidence
- Naked Single
- Hidden Single
- Naked Pair
- Pointing Pair / Triple
- Box-Line Reduction
- Hidden Pair
- Naked Triple
- Hidden Triple
- X-Wing in row and column orientations
- Exact source cells, source unit, target cells, candidate eliminations, and placements
- Deterministic teaching traces
- Hint engine backed by the same teaching pipeline as the logical solver
- Explicit Reveal fallback kept separate from logical explanations
- Live source/target/elimination/placement board emphasis while a hint is open
- Accessibility descriptions for teaching evidence
- English/Hindi localized hint names and explanations

### Learning Center

- Introductory Sudoku, candidate, and solving-habit lessons
- Technique lessons for every supported v0.8 strategy
- Deterministic offline practice catalog covering every supported technique
- Interactive correct/incorrect practice states
- Per-technique local lesson/practice counters
- Per-technique and overall mastery presentation
- Safe learning-progress reset without affecting gameplay data
- No account or cloud service required

See [docs/LEARNING_AND_HINTS.md](docs/LEARNING_AND_HINTS.md).

### Challenges, Custom Puzzles, and Player Data

- Deterministic Daily Challenge
- Challenge archive/history foundations
- Weekly challenges
- Custom puzzle editor
- Contradiction, solvability, and uniqueness validation
- Custom puzzle save/replay flows
- Room-backed history/saved-data foundations
- Local statistics, streaks, and achievements

### Safe Sharing, Import, Export, and Backup

- Versioned puzzle codes
- Bounds/checksum/format validation
- Strict import size limits
- Text/clipboard/Android share flows
- Android document-picker transfer
- Result sharing/export support
- Versioned local backup/restore
- Duplicate-safe restore behavior

### Localization, Accessibility, and Project Quality

- English and Hindi resource sets
- Translation parity verification in CI
- Semantic Sudoku cell descriptions
- Teaching evidence semantics
- Adaptive layouts
- Original launcher, monochrome, and splash vector assets
- GitHub Actions unit-test/lint/build quality gate
- API-35 connected Compose instrumentation gate
- Open-source repository policies, support documentation, and contributor guidance

## Next Milestone

After v0.8 is merged, v0.9 focuses on release hardening:

- full regression-suite audit;
- TalkBack/focus-order and large-font QA;
- performance and memory audit;
- security/privacy review;
- dependency/license audit;
- device QA matrix;
- release shrinking/signing verification;
- final UI/store-asset polish.

See [ROADMAP.md](ROADMAP.md) for complete milestone tracking.

## Technology Stack

- Kotlin 2.4.10
- Android Gradle Plugin 9.3.1
- Gradle 9.5
- Java 17
- Android compile/target SDK 37
- Minimum Android API 26
- Jetpack Compose + Material 3
- AndroidX Lifecycle + ViewModel
- Navigation Compose
- Kotlin Coroutines / Flow
- Preferences DataStore
- Room
- JUnit / Kotlin Test
- Compose UI Test / AndroidX Test
- Android Lint

## Architecture

SudokuNova uses two Gradle modules:

```text
SudokuNova/
├── app/                    # Android UI, navigation, persistence and app state
├── sudoku-engine/          # Platform-independent Sudoku logic, teaching evidence and tests
├── docs/                   # Technical and contributor documentation
├── .github/                # CI, instrumentation and community templates
└── gradle/                 # Version catalog and Gradle wrapper
```

The Sudoku engine deliberately has no Android dependency so it can be tested quickly and reused by future platform clients. The Android layer owns localization, Compose presentation, persistence, Android sharing/document APIs, and lifecycle-aware UI state.

Detailed design: [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md)

## Requirements

- Android Studio compatible with AGP 9.3.1
- JDK 17
- Android SDK 37 for compilation
- Android device/emulator on API 26 or newer
- API 35 emulator/device for reproducing the repository's connected verification gate

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

Run the standard local verification tasks:

```bash
python scripts/verify_translations.py
./gradlew :sudoku-engine:test --stacktrace
./gradlew :app:testDebugUnitTest --stacktrace
./gradlew :app:assembleDebugAndroidTest --stacktrace
./gradlew :app:lintDebug --stacktrace
./gradlew :app:assembleDebug --stacktrace
```

More build types, executable outputs, tooling requirements, and release commands are documented in [docs/BUILDING.md](docs/BUILDING.md).

## Testing

SudokuNova treats deterministic correctness as a merge requirement.

Important suites include:

- board/solver/generator tests;
- uniqueness and difficulty corpus tests;
- game-state codec and persistence model tests;
- transfer/import/backup tests;
- teaching-trace solution-safety tests;
- controlled Hidden Pair, Naked Triple, Hidden Triple, and X-Wing evidence tests;
- practice-catalog completeness/determinism tests;
- learning-progress JVM tests;
- Compose connected smoke tests on API 35.

GitHub pull requests are expected to pass both `Android CI` and `Android Instrumentation` on the final clean head.

## Privacy

SudokuNova is designed to work without an account, login, analytics, or advertising in the open-source base application. Gameplay settings, active-game state, statistics, structured local records, and v0.8 learning progress are stored locally. No sensitive Android permissions are requested for core play.

Sharing/export happens only through explicit user actions and Android system UI.

See [docs/PRIVACY.md](docs/PRIVACY.md) and [docs/DATA_STORAGE.md](docs/DATA_STORAGE.md).

## Accessibility

The project uses semantic cell descriptions, large touch targets where practical, contrast-aware states, adaptive layouts, keyboard support, and high-contrast/reduced-motion preferences. v0.8 adds semantics for hint sources, targets, candidate removals, and final placements so teaching logic is not represented only by color.

Accessibility remains a release quality gate and requires manual TalkBack/large-font verification in addition to automated tests.

See [docs/ACCESSIBILITY.md](docs/ACCESSIBILITY.md).

## Contributing

Contributions to code, tests, documentation, accessibility, translations, and design are welcome. Start with [CONTRIBUTING.md](CONTRIBUTING.md) and read the [Code of Conduct](CODE_OF_CONDUCT.md).

Use Conventional Commits such as:

```text
feat(engine): add a tested logical technique
test(engine): cover candidate elimination safety
fix(ui): preserve hint accessibility semantics
a11y: improve board focus descriptions
docs: document release verification
```

For a new advanced Sudoku technique, implementation, structured evidence, deterministic correctness tests, localized explanation resources, and learning/practice representation should land together.

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
