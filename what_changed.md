# What Changed

## Current Development State

**Merged cumulative milestone:** `v0.6.0-development`  
**Authoritative branch after consolidation:** `main`  
**Cumulative merge commit:** `2c78872948a18e06555ed8e47229c719f8f126b7`  
**Merged PR:** `#17` — `feat: consolidate SudokuNova v0.2–v0.6 cumulative development`  
**Android application ID:** `in.sanskar.sudokunova`  
**Kotlin source namespace:** `com.sanskar.sudokunova`  
**License:** MIT  
**Project commit email:** `sanskarin@outlook.in`

The earlier v0.2–v0.6 phase branches diverged from one another. Their useful work was rebuilt onto one cumulative line, current `main` ancestry was explicitly joined, every required quality gate was rerun, and the consolidated branch was merged only after standard CI and connected API-35 instrumentation were both green.

## Final v0.2–v0.6 Verification

The final PR head was `807cdc7d1ca0ed988539bc64e1fee7783b985db1`.

### Standard Android CI — GREEN

Run: `32030210323`

Passed:

1. English/Hindi translation parity.
2. `:sudoku-engine:test`.
3. `:app:testDebugUnitTest`.
4. `:app:assembleDebugAndroidTest`.
5. `:app:lintDebug`.
6. `:app:assembleDebug`.
7. report upload.

### API-35 Connected Instrumentation — GREEN

Run: `32030210239`

Passed on an Android API 35 x86_64 Pixel 6 emulator:

- Compose navigation smoke tests.
- Room instrumentation tests.
- Daily/Weekly challenge persistence tests.
- History/Saved Puzzles navigation coverage.
- Settings input-mode coverage.
- Custom Puzzle entry/save coverage.

### Connected-test defect fixed before merge

The first connected emulator run exposed two smoke-test defects rather than application defects:

1. `Play challenge` legitimately appeared more than once in the challenge archive, but the test expected a single node.
2. The Settings smoke test attempted to resolve off-screen LazyColumn rows before those rows were composed.

Fixes:

- Challenge smoke test now intentionally selects the first matching `Play challenge` node.
- Settings smoke test verifies the immediately composed Cell-first/Number-first controls instead of assuming off-screen rows are already in the semantics tree.

Both standard and connected gates were rerun on the corrected exact head before merge.

## Repository History Consolidation

A repository-history audit found that earlier v0.2, v0.3, v0.4, v0.5, and v0.6 work had been developed on divergent branches. Directly merging those branches could remove later functionality.

The cumulative line was rebuilt and an explicit two-parent Git merge commit joined current `main` history without replacing the verified cumulative implementation tree:

- cumulative parent: `0ffe1e085525f16237de588846269a8819e957bb`
- then-current main parent: `b404d950481be2d778b00613eeff2d608fb8a475`
- ancestry-join commit: `fae2ffc0881c2ad52fbee549f9d8dc6f4de39d74`

A temporary one-time Actions synchronization workflow was removed after ancestry was repaired.

## Build / Toolchain

Current cumulative build configuration includes:

- Android Gradle Plugin `9.3.1`
- Kotlin `2.4.10`
- KSP `2.3.10`
- Room `2.8.3`
- Compose BOM `2026.08.00`
- compile SDK `37`
- target SDK `37`
- min SDK `26`
- Java/JVM target `17`
- Android versionCode `600`
- Android versionName `0.6.0`

Room processing uses KSP2. This replaced the divergent kapt configuration that exposed a build-plugin classpath conflict.

## v0.2 Gameplay Hardening — Restored

### Input modes

- Cell-first mode.
- Number-first mode.
- Persisted input-mode preference.
- Persisted selected cell.
- Persisted selected number.
- Number-first selected-number highlight.
- Number-first cell placement behavior.

### Hardware keyboard

A testable keyboard mapping layer supports:

- Arrow keys: move selected Sudoku cell.
- `1`–`9`: number input.
- Backspace/Delete: erase.
- `N`: toggle Notes.
- `H`: request Hint.
- Movement clamped inside the 9×9 board.

Keyboard movement is separated from touch selection so moving with arrows in Number-first mode does not accidentally place a number.

### Feedback

Settings-controlled local feedback is implemented:

- Haptics.
- Click sound effects.
- Feedback on number/cell interaction, erase, notes, undo/redo, hint, pause, restart, and hint application.

No network or tracking is required for feedback.

## v0.3 Logical Difficulty Calibration — Restored

Added a human-style logic layer without weakening the existing unique-solution generator gate.

### Supported logical techniques

- Naked Single.
- Hidden Single.
- Naked Pair candidate elimination.
- Pointing Pair/Triple candidate elimination.
- Box-Line Reduction candidate elimination.

### Engine components

- `LogicalSolver`
- `LogicalDifficultyAnalyzer`
- `DifficultyCalibrator`
- deterministic logical evidence and scoring
- unresolved-cell / hardest-technique evidence

### Safety coverage

Tests verify:

- deterministic logical solving,
- no invented values,
- common-puzzle correctness,
- generated puzzle correctness,
- all-difficulty corpus consistency with each unique solution,
- calibration determinism,
- combined calibration never silently weakens the legacy score.

The logical calibration remains additive/observational; public generator uniqueness requirements remain intact.

## v0.4 Accessibility / Localization

Preserved and verified:

- English/Hindi resource-backed core UI.
- translation parity CI check.
- localized difficulty labels.
- localized theme labels.
- resource-backed Sudoku accessibility semantics.
- High Contrast Sudoku board behavior.
- Material 3 UI foundation.
- localized Learn center.
- localized Home, Game, Settings, Statistics, About, Custom Puzzle, History, Saved Puzzles, and Challenges surfaces.
- adaptive phone/wider layouts.

## v0.5 Local Player Data

### Room database

The local database includes:

- `GameHistoryEntity`
- `GameHistoryDao`
- `SavedPuzzleEntity`
- `SavedPuzzleDao`
- `HistoryRepository`
- versioned `SudokuNovaDatabase`
- exported Room schema configuration

### Game history

Stored locally:

- puzzle,
- solution,
- difficulty,
- elapsed time,
- mistakes,
- hints,
- timestamps,
- Daily Challenge marker,
- perfect-game marker,
- favorite marker,
- replay provenance.

History UI includes:

- All/Favorites filters.
- difficulty filters.
- per-difficulty summaries.
- game count.
- average/best time.
- perfect-game count.
- favorite/unfavorite.
- delete.
- replay.

Replay attempts are excluded from normal aggregate difficulty summaries so repeated replaying does not inflate progress statistics.

### Saved Puzzles

Implemented:

- local persistence.
- unique puzzle constraint.
- All/Favorites filtering.
- favorite/unfavorite.
- delete.
- play saved puzzle.
- source/difficulty metadata.

Validated custom puzzles can be saved after uniqueness validation with duplicate detection and English/Hindi status feedback.

## v0.6 Daily / Weekly Challenges

### Deterministic challenge identity

- Daily key: local epoch day.
- Weekly key: ISO week-based year/week.
- type-separated deterministic seeds.
- Daily/Weekly namespaces cannot collide even if the numeric key matches.

### Current challenge difficulty defaults

- Daily: `MEDIUM`
- Weekly: `HARD`

### Challenge persistence

Room stores:

- challenge type,
- challenge key,
- difficulty,
- puzzle,
- elapsed time,
- mistakes,
- hints,
- completion timestamp,
- perfect status.

Unique `(challengeType, challengeKey)` indexing preserves one first-completion result for each challenge.

### Room migration

Database v2 adds challenge results through explicit `MIGRATION_1_2`; destructive migration is not used.

### Challenge archive

Implemented:

- Challenges destination.
- Daily/Weekly selector.
- 31-day Daily archive.
- 13-week Weekly archive.
- current challenge labels.
- completion status.
- saved performance.
- play/replay.
- English/Hindi challenge resources.
- offline/account-free behavior.

## Active Game Codec

Active-game persistence is version **4**.

Current state includes:

- puzzle,
- solution,
- current board,
- notes,
- selected cell,
- selected number,
- Notes mode,
- timer,
- mistakes,
- hints,
- difficulty,
- seed,
- pause/status,
- Daily marker,
- replay source ID,
- challenge type/key.

Backward decoding supports v1, v2, v3, and v4 saves. Malformed indices, numbers, counters, replay IDs, notes, or challenge metadata are rejected.

## Current Navigation

- Home
- Game
- Challenges
- Custom Puzzle
- History
- Saved Puzzles
- Learn
- Statistics
- Settings
- About

## CI / QA Infrastructure

### Standard CI

`.github/workflows/ci.yml` checks translations, engine tests, JVM tests, instrumentation-test compilation, lint, debug APK assembly, and reports.

Lint failures print the full text report into CI logs.

### Connected instrumentation

`.github/workflows/instrumentation.yml` runs connected tests on Android API 35 using an x86_64 Pixel 6 emulator with KVM and animations disabled.

### Compose smoke tests

Current smoke coverage includes:

- Home.
- Challenges.
- History.
- Saved Puzzles.
- Settings Cell-first/Number-first controls.
- Custom Puzzle.

### Engine and state regression coverage

Includes:

- Sudoku solver/generator validity and uniqueness.
- v4 active-game round trip.
- v1/v3 save migration.
- malformed save rejection.
- deterministic challenge keys.
- keyboard mapping and grid movement.
- logical technique safety.
- all-difficulty logical corpus.

## Important Bugs / Build Problems Fixed

- Kotlin `in.*` package-keyword compiler failure → source namespace moved to `com.sanskar.sudokunova` while preserving Android application ID.
- Compose invalid `weight` imports.
- incorrect solver regression assertion.
- DataStore typed statistics reset issue.
- custom-puzzle solution preview data-loss issue.
- Gradle wrapper push race during initial bootstrap.
- Android unit-test JUnit API mismatch.
- divergent phase history that could drop features.
- Room kapt/plugin classpath conflict → KSP2.
- missing cumulative Room DAO/database sources.
- duplicate localization helpers.
- Compose locale observability lint error.
- obsolete untranslated bootstrap resources.
- challenge saved-state provenance mismatch.
- replay statistics inflation risk.
- connected Compose smoke-test assumptions about duplicate/off-screen nodes.

## Next Milestone

GitHub issue `#19` tracks **v0.7 — safe sharing, import/export, and backup/restore**.

Planned scope:

- versioned safe Classic 9×9 puzzle-code format.
- copy/share puzzle code.
- bounded and strictly validated paste/import.
- versioned local user-data export/import.
- backup/restore UI.
- settings/history/saved-puzzles/challenge-results backup support.
- duplicate/conflict handling without silent overwrite.
- destructive-action confirmations.
- result-sharing foundation without requiring an account.
- English/Hindi resources.
- codec/validator tests.
- Room/import tests.
- Compose smoke tests.
- privacy/security/data-storage/backup documentation updates.

## Later Milestones

### v0.8

- advanced logical hints.
- practice mode.
- learning progress.
- selected variants only after Classic 9×9 remains stable.

### v0.9

- full device matrix.
- performance profiling.
- accessibility audit.
- dependency/license audit.
- security/privacy audit.
- secure release-signing workflow.
- production APK/AAB verification.
- final screenshots/store assets.

### v1.0

Stable release/tag only after all required functional, migration, CI, connected-device, accessibility, privacy/security, and release gates are green.

## Branding / Support

- Project: **SudokuNova**
- Tagline: **Think. Solve. Master the Grid.**
- Repository: `https://github.com/sanskarIN/SudokuNova`
- GitHub: `https://www.github.com/sanskarIN`
- Buy Me a Coffee: `https://buymeacoffee.com/sanskarIN`
- Business: `sanskarin@outlook.in`
- Business: `sanskarin.business@gmail.com`
- Support: `supportramsandesh@gmail.com`
- Credit: **Made by the Sanskar**
- License: **MIT**

## Commit Policy

Project-authored work continues to use many focused Conventional Commit-style commits (`feat:`, `fix:`, `test:`, `docs:`, `build:`, `ci:`, `chore:`, `refactor:`) rather than one giant implementation commit.
