# What Changed

## Current Development Line

**Current app version:** `0.6.0-development`  
**Current cumulative branch:** `feature/v0.6-consolidated`  
**Android application ID:** `in.sanskar.sudokunova`  
**Kotlin source namespace:** `com.sanskar.sudokunova`  
**Project license:** MIT  
**Requested project commit email:** `sanskarin@outlook.in`

> `feature/v0.6-consolidated` is the authoritative development line for continued work. Older phase branches diverged from one another and must not be merged directly because doing so could drop previously implemented functionality.

## Repository Consolidation

A repository-history audit found that earlier v0.2, v0.3, v0.4, v0.5, and v0.6 work had been developed on divergent branches instead of one strictly cumulative line. Continuing to merge those branches directly would have caused feature loss.

The project was therefore rebuilt onto a cumulative branch starting from the stable v0.4 accessibility/localization line and then restoring v0.5 player-data features and integrating v0.6 challenge/archive features on top of the same codebase.

Obsolete/divergent development branches are being treated as reference sources only. Their useful code is copied/adapted into the consolidated branch with new focused commits and verified again.

## Build / Toolchain

Current consolidated build configuration includes:

- Android Gradle Plugin `9.3.1`
- Kotlin `2.4.10`
- KSP `2.3.10`
- Room `2.8.3`
- compile SDK `37`
- target SDK `37`
- min SDK `26`
- Java/JVM target `17`
- Android versionCode `600`
- Android versionName `0.6.0`
- Compose BOM `2026.08.00`
- Gradle wrapper currently `9.5`

Room annotation processing was moved from kapt to **KSP2** because the divergent kapt-based v0.6 line exposed a build-plugin classpath conflict. The KSP-based consolidated build now compiles Room sources successfully through Android unit-test and instrumentation-test compilation stages.

## v0.4 Accessibility / Localization Foundation Preserved

The cumulative line retains the v0.4 work, including:

- English/Hindi resource-backed user-facing UI.
- Translation parity verification script.
- Localized difficulty labels.
- Localized theme labels.
- Resource-backed Sudoku accessibility semantics.
- High-contrast board behavior.
- Material 3 UI foundation.
- Learn center localization.
- Localized Home, Game, Settings, Statistics, About, and Custom Puzzle surfaces.
- Accessibility-oriented layout and semantic foundations.

The translation verifier now checks v0.4, v0.5, v0.6, difficulty, and theme resource families together.

Current parity gate checks **209 localized string keys** across English and Hindi resources.

## v0.5 Player Data Restored on the Consolidated Line

### Room Player Database

Restored/added:

- `GameHistoryEntity`
- `GameHistoryDao`
- `SavedPuzzleEntity`
- `SavedPuzzleDao`
- `HistoryRepository`
- versioned `SudokuNovaDatabase`
- exported Room schema configuration through KSP

### Completed Game History

Implemented locally on-device:

- Completed-game records.
- Puzzle and solution storage for replay.
- Difficulty.
- Elapsed time.
- Mistakes.
- Hints used.
- Started/completed timestamps.
- Daily Challenge marker.
- Perfect-game marker.
- Favorite marker.
- Replay provenance.

### History UI

Implemented:

- History destination.
- All/Favorites filters.
- Difficulty filters.
- Per-difficulty summaries.
- Game count.
- Average time.
- Best time.
- Perfect-game count.
- Favorite/unfavorite.
- Delete local history item.
- Replay completed puzzle.
- Replay badge.

Replay attempts are deliberately excluded from normal aggregate difficulty-summary calculations so repeatedly replaying the same puzzle does not inflate normal progress statistics.

### Saved Puzzles

Implemented:

- Saved Puzzles destination.
- Local Room persistence.
- Unique-puzzle constraint.
- All/Favorites filters.
- Favorite/unfavorite.
- Delete.
- Play saved puzzle.
- Difficulty/source metadata.

### Custom Puzzle Save Flow

Validated custom puzzles can now be saved locally after uniqueness validation.

The save flow includes:

- duplicate detection,
- optional solved-board storage,
- local persistence only,
- English/Hindi save/duplicate feedback.

## Consolidated Saved-Game State

The active-game state now carries all cumulative metadata required by current features:

- puzzle,
- solution,
- current board,
- notes,
- selected cell,
- selected number,
- Notes mode,
- elapsed time,
- mistakes,
- hints,
- difficulty,
- seed,
- pause/status metadata,
- Daily Challenge marker,
- history replay source ID,
- challenge type,
- challenge key.

### Saved-Game Codec

The active-game codec is now **version 4**.

Backward decoding remains supported for:

- v1 legacy saves,
- v2 selected-number saves,
- v3 replay-provenance saves,
- v4 replay + challenge provenance.

Strict validation rejects malformed selected indices, selected numbers, counters, replay IDs, candidate-note values, or partially present challenge metadata.

## v0.6 Daily / Weekly Challenges

### Deterministic Challenge Keys

Implemented:

- Daily keys derived from local epoch day.
- Weekly keys derived from ISO week-based year/week.
- Type-separated deterministic seeds.
- Daily and Weekly challenge namespaces can safely use identical numeric keys without collision.

### Challenge Difficulty

Current development defaults:

- Daily Challenge: `MEDIUM`
- Weekly Challenge: `HARD`

These can be recalibrated later after the v0.3 logical-difficulty system is fully restored onto the cumulative line.

### Challenge Result Persistence

Added Room `ChallengeResultEntity` and `ChallengeResultDao` storing:

- challenge type,
- challenge key,
- difficulty,
- puzzle,
- elapsed time,
- mistakes,
- hints,
- completion timestamp,
- perfect completion status.

A unique `(challengeType, challengeKey)` index preserves one first-completion record per challenge.

### Room Migration

Database v2 adds the challenge-results table through an explicit `MIGRATION_1_2` instead of destructive migration.

### Challenge Archive UI

Implemented:

- Challenges destination.
- Daily/Weekly selector.
- 31-day Daily archive window.
- 13-week Weekly archive window.
- Current challenge markers.
- Completed/not-completed status.
- Saved completion performance.
- Play challenge.
- Replay challenge.
- English/Hindi challenge resources.
- Offline/account-free explanation.

### Challenge Gameplay Integration

Game navigation carries deterministic challenge type/key metadata into `GameViewModel`.

When a challenge is completed:

- normal game history is recorded,
- challenge result is recorded,
- active-game persistence is cleared,
- replay-safe statistics rules remain applied.

## Current Home / Navigation Destinations

The consolidated navigation graph now includes:

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

Home exposes History and Saved Puzzles alongside existing play/learn/settings/support entry points.

## Testing Added / Restored

### JVM Tests

Current consolidated tests include coverage for:

- v4 active-game codec round trip,
- v1 migration,
- v3 replay migration,
- malformed saved-state rejection,
- deterministic Daily Challenge keys,
- deterministic ISO Weekly Challenge keys,
- type-separated deterministic challenge seeds,
- existing engine solver/generator regression suite.

### Android / Room Instrumentation Sources

Added challenge Room coverage for:

- storing Daily results,
- querying results,
- unique challenge type/key behavior,
- Daily/Weekly namespace separation.

Existing instrumentation sources continue to compile as part of CI.

## CI Quality Gate

Current standard Android CI runs:

1. English/Hindi translation parity.
2. `:sudoku-engine:test`.
3. `:app:testDebugUnitTest`.
4. `:app:assembleDebugAndroidTest`.
5. `:app:lintDebug`.
6. `:app:assembleDebug`.
7. Report upload.

CI was also improved so a lint failure prints the complete lint text report directly into the job log instead of hiding the remaining errors in an artifact.

## Build / Lint Defects Found and Fixed During Consolidation

### Divergent repository history

**Problem:** phase branches were not strictly cumulative; directly merging the newer-looking branch could remove v0.5 features.

**Fix:** created `feature/v0.6-consolidated` and reconstructed the cumulative line explicitly.

### kapt / plugin classpath conflict

**Problem:** the divergent Room branch exposed a build-plugin conflict with the kapt setup.

**Fix:** moved Room processing to KSP2 on the consolidated line.

### Missing cumulative Room sources

**Problem:** a partial branch referenced Room history/saved-puzzle classes without carrying every required DAO/database source.

**Fix:** restored the complete Room entity/DAO/repository/database layer.

### Duplicate localization helpers

**Problem:** consolidation temporarily produced duplicate `localizedDifficultyLabel` / theme helper declarations.

**Fix:** removed the duplicate file and retained the original v0.4 shared localization helpers.

### Missing Android resource imports

**Problem:** consolidated Game/Settings files used `stringResource`/`R` without all required imports.

**Fix:** corrected imports and recompiled through JVM + instrumentation-test compilation.

### Compose locale observability lint error

**Problem:** UI code fell back to `Locale.getDefault()`, which is not observable by Compose configuration changes.

**Fix:** History and Challenges now derive locale from `LocalConfiguration.current.locales[0]`.

### Legacy untranslated bootstrap strings

**Problem:** nine obsolete base strings produced `MissingTranslation` lint errors.

**Fix:** removed unused bootstrap strings and kept the `SudokuNova` application name as an explicit `translatable="false"` brand value.

## Current Verification State

A fresh full CI run is currently executing on the latest consolidated head after the final MissingTranslation cleanup.

The previous run had already passed:

- translation parity,
- engine tests,
- Android JVM unit tests,
- Android instrumentation-test APK compilation.

Its only blocker was Android lint. The nine reported lint errors have now been removed and a new full run is validating the exact corrected head.

Do **not** describe v0.6 as merged/release-ready until that latest gate is fully green and connected/emulator coverage is completed.

## Important Remaining Consolidation Work

### Restore v0.2 Gameplay Hardening onto the cumulative line

Some v0.2 implementation exists only on the old divergent reference branch and still needs careful reapplication without replacing v0.4-v0.6 code.

Remaining restoration targets include:

- complete Cell-first / Number-first input behavior,
- persisted input-mode preference,
- hardware keyboard navigation/input,
- complete haptic feedback layer,
- complete sound feedback layer,
- stronger process recreation/state restoration tests,
- additional gameplay regression tests.

### Restore v0.3 Logical Difficulty Calibration

The following v0.3 engine work exists on a divergent reference branch and is not yet fully restored to the cumulative line:

- `LogicalSolver`
- `LogicalDifficultyAnalyzer`
- `DifficultyCalibrator`
- logical-technique evidence metadata,
- generator calibration integration,
- v0.3 deterministic calibration corpus/tests.

This work must be reapplied after the v0.6 app/data consolidation gate is green.

## Next Versions After Consolidation

### v0.7 — Sharing / Import / Export / Backup

Planned after cumulative v0.2-v0.6 restoration is verified:

- safe puzzle-code format,
- share puzzle codes,
- paste/import with schema and size validation,
- export/import local user data,
- backup/restore UI,
- result sharing foundation.

### v0.8 — Advanced Learning / Variants Foundation

Planned:

- additional logical hint techniques,
- practice mode,
- learning progress,
- selected Sudoku variants only after Classic 9×9 remains stable.

### v0.9 — Release Hardening

Planned:

- full device matrix,
- performance profiling,
- accessibility audit,
- dependency/license audit,
- security/privacy audit,
- release signing workflow using repository secrets only,
- production APK/AAB verification,
- final screenshots/store assets,
- final documentation accuracy pass.

### v1.0 — Stable Release

A stable `v1.0.0` tag/release will only be created after all required functional, build, lint, instrumentation, accessibility, migration, security, and release gates pass.

## Branding / Support

- Project: **SudokuNova**
- Tagline: **Think. Solve. Master the Grid.**
- Repository: `https://github.com/sanskarIN/SudokuNova`
- GitHub profile: `https://www.github.com/sanskarIN`
- Buy Me a Coffee: `https://buymeacoffee.com/sanskarIN`
- Business: `sanskarin@outlook.in`
- Business: `sanskarin.business@gmail.com`
- Support: `supportramsandesh@gmail.com`
- Credit: **Made by the Sanskar**
- License: **MIT**

## Commit Policy

Development uses focused Conventional Commit-style messages such as:

- `feat:`
- `fix:`
- `test:`
- `docs:`
- `build:`
- `ci:`
- `chore:`
- `refactor:`

The project is intentionally being developed through many focused commits rather than one giant implementation commit.
