# SudokuNova

> **Think. Solve. Master the Grid.**

SudokuNova is a modern, open-source Android Sudoku application built with Kotlin and Jetpack Compose. The project is designed for offline-first play, maintainable architecture, accessibility, deterministic testing, safe local data handling, and long-term community development.

**Made by the Sanskar**

[![Android CI](https://github.com/sanskarIN/SudokuNova/actions/workflows/ci.yml/badge.svg)](https://github.com/sanskarIN/SudokuNova/actions/workflows/ci.yml)
[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.4.10-7F52FF.svg)](https://kotlinlang.org/)
[![Android](https://img.shields.io/badge/Android-API%2026%2B-3DDC84.svg)](https://developer.android.com/)

## Current Development Status

**v0.9.0 — Release Hardening is merged and verified on `main`.** Final PR head `7bc5d095cfdde17dc92250581e3bc28a6fbc54c9` passed Android CI run `32139568718` and API-35 connected instrumentation run `32139568591` before PR #25 was merged as `18944dc56757e1c1c9d51939cb0cafa72e4b5ee2`.

**v1.0 RC1 repository-side preparation is also verified and merged.** Final PR #27 head `7016e21f36c8ecb8a495c446ffd8b57e9f20a4ea` passed Android CI run `32151771317` and API-35 connected instrumentation run `32151771297`, then merged as `2329881aff8dabaf8d040918e16b6113e3900245`. The merged candidate metadata is `versionCode 1000` / `versionName 1.0.0-rc.1`.

The repository now includes verified RC artifact structure/version checks, SHA-256 evidence, release-verifier tests, fail-closed partial-signing checks, optional secret-backed production signing configuration, mandatory signed-artifact verification mode for protected release environments, Play Store preparation, generated release-note configuration, repository-settings guidance, and real-device/manual evidence worksheets.

Stable `v1.0.0` is **not yet claimed**. Issue #5 remains open for TalkBack, representative 200% font/device/window QA, measured performance/ANR evidence, process-death scenarios, GitHub `main` protection/ruleset administration, actual production signing, signed artifact verification, real store/repository assets/declarations, final stable metadata, and publication evidence.

## Complete Documentation

The complete categorized documentation hub is:

**[docs/README.md](docs/README.md)**

High-value entry points:

- [v1.0 RC Preparation](docs/V1_RELEASE_PREP.md)
- [v1.0 RC Evidence Worksheet](docs/V1_RELEASE_CANDIDATE.md)
- [v1.0 Release Evidence Ledger](docs/V1_RELEASE_EVIDENCE.md)
- [v1.0 Release Notes Source](docs/V1_RELEASE_NOTES.md)
- [Production Signing](docs/PRODUCTION_SIGNING.md)
- [Play Store Release Preparation](docs/PLAY_STORE_RELEASE.md)
- [GitHub Repository Settings](docs/GITHUB_REPOSITORY_SETTINGS.md)
- [User Guide](docs/USER_GUIDE.md)
- [Complete Feature Reference](docs/FEATURES.md)
- [Project Structure](docs/PROJECT_STRUCTURE.md)
- [Architecture](docs/ARCHITECTURE.md)
- [Sudoku Engine](docs/SUDOKU_ENGINE.md)
- [Learning and Advanced Hints](docs/LEARNING_AND_HINTS.md)
- [Data Storage](docs/DATA_STORAGE.md)
- [Data Formats](docs/DATA_FORMATS.md)
- [Backup and Restore](docs/BACKUP_RESTORE.md)
- [Testing](docs/TESTING.md)
- [CI/CD](docs/CI_CD.md)
- [Performance / ANR Hardening](docs/PERFORMANCE.md)
- [Accessibility](docs/ACCESSIBILITY.md)
- [Localization](docs/LOCALIZATION.md)
- [Privacy](docs/PRIVACY.md)
- [Security Design](docs/SECURITY.md)
- [Building APK/AAB](docs/BUILDING.md)
- [Releasing](docs/RELEASING.md)
- [Release Checklist](docs/RELEASE_CHECKLIST.md)
- [Release QA](docs/RELEASE_QA.md)
- [Maintainer Guide](docs/MAINTAINER_GUIDE.md)
- [v0.9 Hardening Audit](docs/V09_HARDENING_AUDIT.md)
- [Documentation Standards](docs/DOCUMENTATION_STANDARDS.md)
- [Glossary](docs/GLOSSARY.md)

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
- Selected game/custom-cell semantics
- Number-first and Notes selected-state semantics
- Large-text source hardening across primary action/filter surfaces
- Adaptive layouts
- Original launcher, monochrome, and splash vector assets
- GitHub Actions unit-test/lint/build quality gate
- Debug/release lint and release APK/AAB/R8 verification
- Repository secret/signing-material guard
- API-35 connected Compose/Room instrumentation gate
- Release APK/AAB structural/version verification and SHA-256 evidence in the v1.0 RC line
- Partial production-signing configuration fails closed in CI
- Optional secret-backed production signing without committed credentials
- Optional mandatory APK/AAB signature-verification mode for protected signed-release validation
- Structured bug/feature/accessibility/performance/documentation issue forms
- Pull-request template and contributor policies
- Weekly Dependabot checks for Gradle and GitHub Actions
- `.github/CODEOWNERS` ownership routing
- `.github/FUNDING.yml` with optional Buy Me a Coffee support metadata
- `.github/release.yml` generated release-note categories
- Open-source repository policies, support documentation, and contributor guidance

## Verified v0.9 Release-Hardening Milestone

v0.9 completed the project’s source, automation, documentation, security/privacy, accessibility, data-integrity, and public-repository hardening pass.

Major completed work includes:

- regression-suite and bounded-backup review;
- selected-state and large-text accessibility hardening;
- main-thread CPU/I/O review and off-main-thread solver/hint fixes;
- stale-result protection in Custom Puzzle and imported puzzle validation;
- Room/DataStore integrity and migration review;
- security/privacy and import/export/backup review;
- dependency/license notice review;
- repository secret/signing-material guard;
- release R8/shrinking verification;
- debug APK, release APK, and release AAB verification in CI;
- lifecycle/restoration source review;
- English/Hindi localization cleanup;
- complete release QA, build, maintenance, security, accessibility, and project documentation;
- public-repository tooling review, including CODEOWNERS and funding metadata;
- stale issue/PR cleanup so the cumulative main line remains authoritative.

Exact v0.9 verification:

- final PR head: `7bc5d095cfdde17dc92250581e3bc28a6fbc54c9`;
- Android CI run #583 / `32139568718` — green;
- API-35 instrumentation run #155 / `32139568591` — green;
- PR #25 merge commit: `18944dc56757e1c1c9d51939cb0cafa72e4b5ee2`.

See [ROADMAP.md](ROADMAP.md), [CHANGELOG.md](CHANGELOG.md), and [docs/V09_HARDENING_AUDIT.md](docs/V09_HARDENING_AUDIT.md).

## Verified v1.0 RC1 Repository Preparation

PR #27 prepared the first stable-release candidate without weakening the evidence boundary and is now merged.

Repository-side RC work includes:

- candidate metadata `1000` / `1.0.0-rc.1`;
- `scripts/verify_release_outputs.py` and unit tests;
- release APK/AAB/R8 structure/version checks;
- SHA-256 release evidence;
- optional mandatory APK/AAB signature verification for protected signed-release validation;
- optional release signing through environment-backed secrets;
- fail-closed behavior for incomplete signing configuration;
- production signing handbook;
- Play Store listing/privacy/release preparation;
- detailed real-device/manual RC evidence worksheet and concise evidence ledger;
- canonical stable release-notes source;
- generated GitHub release-note categories;
- GitHub repository-protection/settings checklist.

Exact RC1 repository verification:

- final PR #27 head: `7016e21f36c8ecb8a495c446ffd8b57e9f20a4ea`;
- Android CI run #635 / `32151771317` — green;
- API-35 instrumentation run #188 / `32151771297` — green;
- unsigned release artifact ID `9330415157`, GitHub digest `sha256:0f1fa33127f6ae46d633c039bf0aad2e308b11e94bbd5567dd0fbc4805b4263c`;
- unsigned APK SHA-256 `422a151ab3bb47268a69548ce5669b7a141169cc822400d3ef1376fa476b53c7`;
- release AAB SHA-256 `1bbbb2f227fc432efa74fa6efe16f2f17ae3aa5bf4a59ffac9e2e71de9a7cdfd`;
- R8 mapping SHA-256 `0f8b128679e858e0d835f0e3d23bfb629448efc4215703dd6c0f69b155e3f3ac`;
- PR #27 merge commit `2329881aff8dabaf8d040918e16b6113e3900245`.

Still requiring actual real-world evidence before stable v1.0:

- real TalkBack focus-order traversal;
- representative 200% font and window/device QA;
- high-contrast and reduced-motion device review;
- measured startup/frame/memory/ANR traces;
- process-death/lifecycle manual scenarios;
- GitHub `main` protection/ruleset administration;
- secure production/upload-key signing;
- signed production artifact installation/signature verification and expected certificate confirmation;
- final R8 smoke QA on signed artifacts;
- real store/repository screenshots, listing text, privacy declarations and release assets;
- final stable version code, `versionName = 1.0.0`, fresh stable exact-head CI/API-35 verification, SHIP decision, tag and publication.

See [docs/V1_RELEASE_PREP.md](docs/V1_RELEASE_PREP.md), [docs/V1_RELEASE_EVIDENCE.md](docs/V1_RELEASE_EVIDENCE.md), [docs/V1_RELEASE_CANDIDATE.md](docs/V1_RELEASE_CANDIDATE.md), and issue #5.

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
├── .github/                # CI, instrumentation, ownership, funding and community templates
└── gradle/                 # Version catalog and Gradle wrapper
```

The Sudoku engine deliberately has no Android dependency so it can be tested quickly and reused by future platform clients. The Android layer owns localization, Compose presentation, persistence, Android sharing/document APIs, and lifecycle-aware UI state.

Detailed design: [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md)

Repository map: [docs/PROJECT_STRUCTURE.md](docs/PROJECT_STRUCTURE.md)

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
python scripts/verify_no_secrets.py
python -m unittest scripts.tests.test_verify_release_outputs
python scripts/verify_translations.py
./gradlew :sudoku-engine:test --stacktrace
./gradlew :app:testDebugUnitTest --stacktrace
./gradlew :app:assembleDebugAndroidTest --stacktrace
./gradlew :app:lintDebug :app:lintRelease --stacktrace
./gradlew :app:assembleDebug --stacktrace
./gradlew :app:assembleRelease --stacktrace
./gradlew :app:bundleRelease --stacktrace
```

The v1.0 RC CI verifies the built release outputs through `scripts/verify_release_outputs.py` and records SHA-256 evidence. Protected signed-release validation can additionally run the same verifier with `--require-signatures`. More build types, executable outputs, tooling requirements, signing rules, and release commands are documented in [docs/BUILDING.md](docs/BUILDING.md), [docs/PRODUCTION_SIGNING.md](docs/PRODUCTION_SIGNING.md), and [docs/V1_RELEASE_PREP.md](docs/V1_RELEASE_PREP.md).

## Testing

SudokuNova treats deterministic correctness as a merge requirement.

Important suites include:

- board/solver/generator tests;
- uniqueness and difficulty corpus tests;
- game-state codec and persistence model tests;
- transfer/import/backup tests;
- bounded backup file-I/O tests;
- teaching-trace solution-safety tests;
- controlled Hidden Pair, Naked Triple, Hidden Triple, and X-Wing evidence tests;
- practice-catalog completeness/determinism tests;
- learning-progress JVM tests;
- release artifact verifier unit tests;
- release signing fail-closed CI regression;
- Compose/Room connected tests on API 35;
- selected Sudoku-cell semantics regression coverage;
- adaptive connected reachability checks for scrollable large-text layouts.

GitHub pull requests are expected to pass both `Android CI` and `Android Instrumentation` on the exact final head when those gates are required for the milestone.

Latest fully verified merged repository-side release evidence:

- v1.0 RC1 preparation head `7016e21f36c8ecb8a495c446ffd8b57e9f20a4ea`;
- Android CI `32151771317`;
- API-35 connected instrumentation `32151771297`;
- merge commit `2329881aff8dabaf8d040918e16b6113e3900245`.

See [docs/TESTING.md](docs/TESTING.md) and [docs/CI_CD.md](docs/CI_CD.md).

## Privacy

SudokuNova is designed to work without an account, login, analytics, advertising, or SudokuNova-operated cloud backend in the open-source base application. Gameplay settings, active-game state, statistics, structured local records, challenges, saved puzzles, History, and learning progress are stored locally according to the current DataStore/Room model.

Sharing/export happens only through explicit user actions and Android system/platform surfaces. The user-controlled `SNB1` backup is integrity-checked but is not encrypted.

See [docs/PRIVACY.md](docs/PRIVACY.md), [docs/DATA_STORAGE.md](docs/DATA_STORAGE.md), and [docs/DATA_FORMATS.md](docs/DATA_FORMATS.md).

## Accessibility

The project uses semantic cell descriptions, selected-state semantics, large touch targets where practical, contrast-aware states, adaptive layouts, keyboard support, and high-contrast/reduced-motion preferences. Structured hints add semantics for sources, targets, candidate removals, and final placements so teaching logic is not represented only by color.

Source/automated accessibility hardening and API-35 semantics coverage are verified. Real TalkBack and representative large-font/device validation remain stable-v1.0 release gates and are not claimed as completed.

See [docs/ACCESSIBILITY.md](docs/ACCESSIBILITY.md) and [docs/V1_RELEASE_CANDIDATE.md](docs/V1_RELEASE_CANDIDATE.md).

## Contributing

Contributions to code, tests, documentation, accessibility, translations, and design are welcome. Start with [CONTRIBUTING.md](CONTRIBUTING.md), [docs/CONTRIBUTING_GUIDE.md](docs/CONTRIBUTING_GUIDE.md), and the [Code of Conduct](CODE_OF_CONDUCT.md).

The repository includes structured issue forms, a pull-request template, Dependabot, and CODEOWNERS to keep reports and reviews maintainable.

Use Conventional Commits such as:

```text
feat(engine): add a tested logical technique
test(engine): cover candidate elimination safety
fix(ui): preserve hint accessibility semantics
a11y: improve board focus descriptions
docs: document release verification
```

For a new advanced Sudoku technique, implementation, structured evidence, deterministic correctness tests, localized explanation resources, accessibility presentation, and learning/practice representation should land together.

## Security

Do not report exploitable vulnerabilities in public issues. Follow [SECURITY.md](SECURITY.md) for responsible disclosure guidance.

Technical design: [docs/SECURITY.md](docs/SECURITY.md)

Production signing: [docs/PRODUCTION_SIGNING.md](docs/PRODUCTION_SIGNING.md)

## Support SudokuNova

If SudokuNova helps you, you can support continued open-source development through Buy Me a Coffee:

**☕ https://buymeacoffee.com/sanskarIN**

The same optional link is exposed through `.github/FUNDING.yml` so supported GitHub surfaces can show project funding metadata.

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