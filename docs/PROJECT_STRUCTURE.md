# SudokuNova Project Structure

This document maps the repository so contributors can find the correct layer before making a change.

## Top-Level Layout

```text
SudokuNova/
├── .github/                 # CI workflows, issue/community configuration
├── app/                     # Android application module
├── docs/                    # Product, engineering, QA and release documentation
├── gradle/                  # Version catalog and Gradle wrapper metadata
├── scripts/                 # Repository verification scripts
├── sudoku-engine/           # Platform-independent Classic Sudoku domain module
├── build.gradle.kts         # Root Gradle plugin declarations
├── settings.gradle.kts      # Module and repository configuration
├── gradle.properties        # Gradle/Kotlin/Android build properties
├── README.md                # Project landing page
├── CHANGELOG.md             # Release history and current unreleased work
├── ROADMAP.md               # Product/milestone roadmap
├── SECURITY.md              # Vulnerability reporting and security policy
├── CONTRIBUTING.md          # Top-level contribution policy
├── THIRD_PARTY_NOTICES.md   # Third-party licensing notices
└── what_changed.md          # Evidence-oriented implementation history
```

## `sudoku-engine/`

The engine is intentionally Android-independent. This is the correctness-critical domain module and should not import Compose, Android resources, Context, Room, DataStore, or Android lifecycle types.

Primary package:

```text
sudoku-engine/src/main/kotlin/com/sanskar/sudokunova/engine/
```

Important production files include:

- `SudokuBoard.kt` — immutable board model, parsing, serialization, validation, candidates and conflicts.
- `SudokuSolver.kt` — MRV-style recursive solver, solution counting and search metrics.
- `SudokuGenerator.kt` — deterministic seeded generation with uniqueness preservation.
- `Difficulty.kt` — supported difficulty definitions/targets.
- `DifficultyCalibrator.kt` — difficulty calibration support.
- `LogicalDifficultyAnalyzer.kt` — logical/complexity analysis.
- `LogicalSolver.kt` — shared logical solving pipeline.
- `HintEngine.kt` — logical teaching-hint selection plus explicit Reveal fallback.
- `TeachingStep.kt` — structured technique/source/target/elimination/placement evidence.
- `TeachingStepFinder.kt` — deterministic logical-technique search/candidate-state transformation.
- `TeachingPractice.kt` — deterministic offline practice catalog.
- `PuzzleCodeCodec.kt` — versioned `SNP1` puzzle-code format.

Engine tests live under:

```text
sudoku-engine/src/test/kotlin/com/sanskar/sudokunova/engine/
```

They cover board correctness, solving, uniqueness, generation, difficulty, teaching evidence, practice, hint identity and regression corpora.

## `app/`

The Android application owns platform concerns:

- Compose UI;
- navigation;
- Android lifecycle/ViewModels;
- localization/resources;
- Room persistence;
- Preferences DataStore;
- Android sharing/document picker;
- accessibility presentation;
- Android-specific test infrastructure.

### Production source

```text
app/src/main/java/com/sanskar/sudokunova/
```

The package is divided into broad areas.

### `data/`

Local data and persistence layer.

Representative responsibilities:

- `AppPreferencesRepository.kt` — Preferences DataStore-backed settings, active game, statistics and learning progress.
- `UserSettings.kt` — settings model.
- `LearningProgress.kt` — learning/mastery domain values used by Android presentation.
- `history/` — Room database, entities, DAOs, migrations and history/saved-puzzle repository behavior.
- `challenge/` — challenge descriptors/keys/results/repository behavior.
- `transfer/` — backup models, `SNB1` codec, bounded file I/O and restore/export repository logic.

### `game/`

Android-independent-or-near-independent active-game state used by the app module, including `GameState` and game-state serialization/restoration support.

### `ui/`

Compose screens, ViewModels and presentation helpers. Feature-oriented packages include areas such as:

- Home/navigation;
- Game;
- Challenges;
- Custom Puzzle;
- History;
- Saved Puzzles;
- Learn;
- Statistics;
- Settings;
- Backup & Transfer;
- About;
- theme/design helpers.

The UI layer converts engine/domain evidence into localized Android resources rather than pushing player-facing prose into `sudoku-engine`.

### Resources

```text
app/src/main/res/
```

Important resource categories include:

- `values/` — default/English strings and theme resources;
- `values-hi/` — Hindi strings;
- `drawable/` / `mipmap-*` — launcher/splash/vector assets;
- `xml/` — Android backup/data-extraction rules and related configuration.

English/Hindi parity is checked by `scripts/verify_translations.py`.

## Android JVM Tests

```text
app/src/test/java/com/sanskar/sudokunova/
```

The JVM test tree is organized into areas including:

- `data/` — persistence/transfer/learning models and codecs;
- `game/` — active game/state serialization/behavior;
- `ui/` — pure presentation/helper behavior where JVM testing is appropriate.

Use JUnit4 in the Android app module unless the module configuration is intentionally changed.

## Android Instrumentation Tests

```text
app/src/androidTest/java/com/sanskar/sudokunova/
```

Connected tests cover Compose navigation and Room-backed persistence/transfer flows on the configured API-35 emulator CI gate. Stable semantic test tags are used when duplicate/off-screen text would make tests brittle.

## Room Schemas

```text
app/schemas/
```

Room schema export is enabled. Schema history is part of the persistence contract and should remain reviewable when the database version changes.

Do not add destructive migration fallback as a shortcut for schema changes that should preserve user data.

## `docs/`

Documentation is part of the product. The categorized documentation map is `docs/README.md`.

High-value references include:

- user guide/features;
- architecture/project structure;
- engine/teaching/difficulty/generation;
- data storage/formats/backup;
- build/setup/testing/CI;
- accessibility/localization/privacy/security;
- release QA/releasing/checklists;
- contribution/maintenance/documentation standards.

## `.github/workflows/`

Current quality workflows include:

- `ci.yml` — repository security guard, translation parity, engine tests, Android JVM tests, instrumentation-test compilation, debug/release lint, debug APK, R8/resource-shrunk release APK, release AAB and artifact/report evidence.
- `instrumentation.yml` — API-35 connected Compose/Room verification using an emulator with KVM when available.

Do not weaken gates simply to make a pull request green. Fix the underlying defect or document a justified workflow change.

## `scripts/`

Repository verification scripts include:

- `verify_translations.py` — English/Hindi key/format parity verification.
- `verify_no_secrets.py` — v0.9 repository guard against committed signing/private-key material and obvious credential patterns.

Scripts used by CI should be deterministic, fast, and fail with actionable output.

## Root Documentation Files

Root files serve repository-wide purposes:

- `README.md` — concise landing page and current implemented capabilities.
- `CHANGELOG.md` — release-oriented change history.
- `ROADMAP.md` — milestone status.
- `what_changed.md` — detailed implementation/verification evidence; it must not invent successful tests or device checks.
- `SECURITY.md` — authoritative vulnerability reporting policy.
- `CONTRIBUTING.md` — contributor entry point.

More specialized material belongs under `docs/` so the root remains navigable.

## Change Placement Rules

When adding or fixing behavior, prefer the narrowest correct layer:

- Sudoku truth/validation/solver/generator/teaching evidence → `sudoku-engine`.
- Local database/DataStore/backup persistence → `app/.../data`.
- Active-game state transformations → game state/ViewModel layer, keeping pure logic separable where practical.
- Player-facing strings → Android resources.
- Compose presentation → feature UI package.
- Android share/document APIs → Android transfer/presentation layer.
- Cross-cutting policy/process → root or `docs/`.

## New Feature Checklist

Before adding a new feature family, identify:

1. domain model/engine impact;
2. persistence changes and migration needs;
3. ViewModel/state changes;
4. Compose UI changes;
5. English/Hindi resources;
6. accessibility semantics;
7. JVM tests;
8. connected tests when needed;
9. privacy/security implications;
10. backup compatibility implications;
11. documentation updates;
12. changelog/roadmap impact.

This prevents a feature from landing as UI-only code without correctness, persistence, accessibility, localization or release support.
