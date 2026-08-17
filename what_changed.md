# What Changed

## Current Development State

**Latest merged milestone:** `v0.7.0`  
**Authoritative cumulative branch:** `main`  
**v0.7 merged PR:** `#20` — `feat: add v0.7 safe sharing import export and backup`  
**v0.7 merge commit:** `e51044c9b2f1b1a6a9c9e6522886abeb1e82ec74`  
**Final verified v0.7 PR head:** `f6c90a3988cc1919cf7ac07cf317e1f65e1318c4`  
**Completed milestone issue:** `#19`  
**Next focused milestone issue:** `#21` — v0.8 teaching steps, advanced hints, practice, and local learning progress  
**Android application ID:** `in.sanskar.sudokunova`  
**Kotlin source namespace:** `com.sanskar.sudokunova`  
**License:** MIT  
**Project commit email:** `sanskarin@outlook.in`

SudokuNova now has one cumulative v0.2–v0.7 history. Older divergent phase branches remain reference-only and must not replace or bypass `main`.

## Final v0.7 Verification — GREEN

v0.7 was merged only after both required gates passed on the exact clean head `f6c90a3988cc1919cf7ac07cf317e1f65e1318c4`.

### Standard Android CI — GREEN

Run: `32035368535`

Passed:

1. English/Hindi translation parity.
2. `:sudoku-engine:test`.
3. `:app:testDebugUnitTest`.
4. `:app:assembleDebugAndroidTest`.
5. `:app:lintDebug`.
6. `:app:assembleDebug`.
7. report upload and post-job cleanup.

### API-35 Connected Instrumentation — GREEN

Run: `32035368537`

Passed on the Android API-35 emulator gate, including Compose navigation and Room-backed transfer/restore coverage.

The final connected suite includes regression coverage for:

- duplicate-safe backup restore,
- restored settings,
- Favorite-state promotion on natural duplicates,
- replay-provenance preservation,
- replay exclusion from normal difficulty summaries,
- Backup & Transfer navigation,
- existing History/Saved Puzzles/Challenges/Settings/Custom Puzzle smoke coverage.

## v0.7 — Safe Sharing / Import / Export / Backup

### Puzzle codes

Classic 9×9 puzzle sharing uses the versioned `SNP1` text format.

Implemented protections:

- maximum code length of 160 characters,
- CRC32 integrity checksum,
- version validation,
- difficulty validation,
- 81-cell digit validation,
- Sudoku clue-layout validation,
- unique-solution validation before an imported puzzle can be played.

Puzzle codes can be copied/shared from supported History and Saved Puzzle surfaces without exposing local database IDs or private application state.

### Local backup format

Local backup/restore uses the versioned `SNB1` format.

Supported exported data:

- user settings,
- completed-game history,
- saved puzzles,
- Daily/Weekly challenge results.

Intentionally excluded:

- active/in-progress game state,
- credentials,
- signing material,
- arbitrary paths,
- executable content,
- raw Room primary keys,
- raw replay source IDs.

Replay/non-replay provenance is preserved as a bounded semantic flag instead of restoring an untrusted source ID.

### Backup hard limits and validation

The decoder fails closed for invalid or unsupported input.

Current limits/validation include:

- maximum encoded backup size: 2 MiB,
- maximum history records: 5,000,
- maximum saved puzzles: 2,000,
- maximum challenge results: 2,000,
- bounded Base64 metadata fields,
- bounded counters and elapsed times,
- bounded timestamps,
- chronology validation,
- non-negative challenge keys,
- difficulty/challenge enum validation,
- puzzle/solution consistency,
- perfect-result consistency,
- unknown-record rejection,
- checksum validation,
- NUL/newline rejection in decoded metadata.

CRC32 is used as an integrity/error-detection checksum; the format does not claim cryptographic authentication.

### Duplicate-safe restore

Room-backed restore is transactional.

Restore behavior:

- imported database IDs are never trusted or reused,
- natural/exact duplicate History records are skipped,
- duplicate Saved Puzzles use the existing unique puzzle identity,
- duplicate challenge results use the existing `(challengeType, challengeKey)` identity,
- backed-up `Favorite=true` can promote an existing History/Saved Puzzle record,
- restore never demotes an existing Favorite because a backup says false,
- replay/non-replay provenance participates in History identity so replay attempts do not collapse into normal attempts,
- replay attempts remain excluded from normal aggregate difficulty summaries after restore.

Backed-up DataStore settings are applied only after the Room transaction succeeds. Room and DataStore are separate stores, so cross-store atomicity is explicitly not claimed.

### File and clipboard transfer

Backup & Transfer supports:

- clipboard backup copy,
- Android share sheet backup sharing,
- restore from clipboard with confirmation,
- `.snb` file export through Android Storage Access Framework,
- `.snb` file import through Android Storage Access Framework,
- no broad storage permission,
- bounded stream reading before parsing,
- file read/write on `Dispatchers.IO` rather than blocking the Compose/UI thread.

### Result sharing

Account-free, non-sensitive game result summaries can be shared through Android's standard share sheet.

### v0.7 documentation

Detailed format, privacy, validation, restore behavior, and security notes are maintained in:

- `docs/TRANSFER_BACKUP_V07.md`

## v0.7 Review Defects Found and Fixed Before Merge

### Main-thread document I/O

**Problem:** Storage Access Framework read/write originally occurred directly in Activity-result callbacks.

**Fix:** file operations now run on `Dispatchers.IO`, while Compose state updates return to the coroutine's main context.

### Favorite state lost on duplicate restore

**Problem:** a local duplicate was skipped even when the backup carried `Favorite=true`.

**Fix:** duplicate restore now promotes Favorite state without inserting another row and never demotes a local Favorite.

### Saved-puzzle timestamp bound asymmetry

**Problem:** saved-puzzle creation time initially checked only non-negativity while other imported timestamps had a maximum supported epoch.

**Fix:** saved-puzzle timestamps now use the same bounded epoch range and have regression coverage.

### Replay provenance lost during backup/restore

**Problem:** raw replay source IDs were correctly excluded, but restoring the record as `replayOfHistoryId=null` converted a replay attempt into a normal attempt and could inflate aggregate difficulty statistics.

**Fix:** backup history now carries an `isReplay` semantic flag. Restore maps that to a local non-null sentinel rather than trusting/exporting the original source ID. Replay badges and summary exclusion remain correct after restore.

### Temporary development workflows

Several one-time helper workflows/scripts were used while applying repository-side patches. Every temporary helper was removed from the final PR tree before verification and merge. The final changed-file list contains only product source, tests, resources, documentation, build metadata, and the permanent translation verifier.

## v0.2–v0.6 Cumulative Foundation Preserved

### v0.2 gameplay hardening

- Cell-first input.
- Number-first input.
- persisted input-mode preference.
- persisted selected cell/number.
- hardware keyboard arrows / 1–9 / erase / Notes / Hint.
- settings-controlled haptic and click feedback.
- safe keyboard movement that does not auto-place Number-first selections.

### v0.3 logical difficulty foundation

Current engine-supported logical techniques:

- Naked Single.
- Hidden Single.
- Naked Pair.
- Pointing Pair/Triple.
- Box-Line Reduction.

Current engine components:

- `LogicalSolver`
- `LogicalDifficultyAnalyzer`
- `DifficultyCalibrator`
- deterministic technique evidence/scoring at aggregate level.

The generator's unique-solution requirement remains authoritative; logical difficulty evidence is additive and must never weaken puzzle correctness.

### v0.4 accessibility/localization

- English/Hindi resource-backed UI.
- translation-parity CI gate.
- localized difficulty/theme labels.
- Sudoku accessibility semantics.
- high-contrast behavior.
- Material 3 foundation.
- adaptive layouts.
- localized Home/Game/Learn/Settings/Statistics/About/Custom/History/Saved/Challenges/Transfer surfaces.

### v0.5 local player data

Room-backed:

- completed-game History,
- Favorite History,
- replay provenance,
- Saved Puzzles,
- custom-puzzle saving,
- difficulty summaries,
- replay-safe statistics.

### v0.6 challenges

- deterministic Daily challenge keys.
- deterministic ISO Weekly challenge keys.
- type-separated deterministic seeds.
- Daily/Weekly archive.
- first-completion challenge performance storage.
- explicit Room migration `MIGRATION_1_2`.
- challenge provenance in active-game codec v4.

## Current Build / Toolchain

- Android Gradle Plugin `9.3.1`
- Kotlin `2.4.10`
- KSP `2.3.10`
- Room `2.8.3`
- Compose BOM `2026.08.00`
- compile SDK `37`
- target SDK `37`
- min SDK `26`
- Java/JVM target `17`
- Android versionCode `700`
- Android versionName `0.7.0`
- Gradle wrapper `9.5`

Room annotation processing uses KSP2.

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
- Backup & Transfer
- About

## Permanent CI / QA Gates

### Standard CI

`.github/workflows/ci.yml` verifies:

1. English/Hindi translation parity.
2. engine tests.
3. Android JVM tests.
4. instrumentation-test APK compilation.
5. Android lint.
6. debug APK assembly.
7. report upload.

### Connected instrumentation

`.github/workflows/instrumentation.yml` runs connected tests on Android API 35 using an x86_64 Pixel 6 emulator with KVM and animations disabled.

## Important Historical Defects Already Fixed

- Kotlin `in.*` package-keyword compiler failure → source namespace moved to `com.sanskar.sudokunova` while Android application ID remained unchanged.
- invalid Compose `weight` imports.
- solver regression assertion error.
- DataStore typed statistics reset issue.
- custom-puzzle solution preview data loss.
- Gradle wrapper bootstrap push race.
- Android unit-test JUnit mismatch.
- divergent v0.2–v0.6 branch histories that could drop features.
- Room kapt/plugin classpath conflict → KSP2.
- missing cumulative DAO/database files.
- duplicate localization helpers.
- Compose locale observability lint error.
- obsolete untranslated resources.
- challenge saved-state provenance mismatch.
- replay statistics inflation risk.
- connected Compose assumptions about duplicate/off-screen nodes.
- v0.7 main-thread backup file I/O.
- v0.7 duplicate Favorite-state loss.
- v0.7 replay-provenance loss after restore.

## Next Milestone — v0.8 Learning / Advanced Hints

Focused GitHub issue: `#21` — **teaching steps, advanced hints, practice, and local learning progress**.

The existing `LogicalSolver` already detects Naked Single, Hidden Single, Naked Pair, Pointing Pair/Triple, and Box-Line Reduction. The current `HintEngine` exposes only Naked Single, Hidden Single, and a stronger Reveal fallback.

v0.8 should therefore start by adding a deterministic, platform-independent **teaching-step evidence model** rather than duplicating solving logic in Compose.

Planned order:

1. structured teaching-step model with technique/source/target/candidate/placement evidence;
2. deterministic step finder for currently proven techniques;
3. tests proving every placement/elimination is solution-safe;
4. refactor HintEngine to consume structured steps;
5. localized app-layer explanations and board highlighting;
6. interactive practice states;
7. local learning progress;
8. Hidden Pairs/Triples and Naked Triples only after correctness tests;
9. X-Wing only after deterministic elimination evidence and regression coverage;
10. no Sudoku variants until Classic 9×9 learning/hint behavior remains stable.

v0.8 merge gate remains: translation parity, engine tests, Android JVM tests, instrumentation compilation, lint, debug APK assembly, and API-35 connected tests on the final clean PR head.

## Later Milestones

### v0.9

- full device matrix,
- performance profiling,
- accessibility audit,
- dependency/license audit,
- security/privacy audit,
- release signing through repository secrets only,
- production APK/AAB verification,
- screenshots/store assets,
- final documentation audit.

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

Project-authored work continues to use focused Conventional Commit-style messages (`feat:`, `fix:`, `test:`, `docs:`, `build:`, `ci:`, `chore:`, `refactor:`) rather than one giant implementation commit.
