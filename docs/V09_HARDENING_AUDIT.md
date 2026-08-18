# v0.9 Release-Hardening Audit

This document records concrete v0.9 audit findings and their repository disposition. It is an engineering evidence log, not a claim that manual release QA is complete.

## Scope

The audit covers:

- main-thread CPU and blocking I/O risks;
- asynchronous stale-result races;
- Room migration/configuration hygiene;
- import/export/backup boundaries;
- Android manifest and repository-secret surface;
- accessibility semantics and deterministic UI testability;
- English/Hindi presentation boundaries;
- debug/release build verification;
- documentation accuracy.

Manual TalkBack, physical-device, large-font, high-contrast, reduced-motion, performance-trace, and store-listing signoff remain separate evidence requirements.

## Findings and fixes

### Game hint computation

**Finding:** teaching/hint analysis could run synchronously from the UI-facing ViewModel path.

**Fix:** hint computation runs on `Dispatchers.Default`, previous hint work is cancellable, and a result is published only when the same board is still current, playable, and unpaused.

### Custom Puzzle solver work

**Finding:** Custom Puzzle uniqueness analysis and solution preview called the backtracking solver directly from UI actions.

**Fix:** both paths run on `Dispatchers.Default`. Superseded work is cancelled and stale results are discarded when the board has changed.

### Puzzle-code validation race

**Finding:** a puzzle code could be edited while uniqueness validation was in flight, allowing an old validation result to overwrite the state for new input.

**Fix:** validation jobs are cancellable and results are published only when the exact requested input is still current.

### Transfer busy-state interaction

**Finding:** cancelling a puzzle-validation job during text editing could clear a `busy` state owned by unrelated backup/restore work.

**Fix:** text edits clear `busy` only when the cancelled operation was the active puzzle validation; unrelated transfer work keeps ownership of the busy state.

### Selected game-cell instrumentation race

**Finding:** API-35 instrumentation run `32129482037` failed because the test asserted the first game-cell semantic node immediately after selecting Easy, while puzzle generation is asynchronous.

**Fix:** the connected test waits for the stable first-cell semantic tag to enter the Compose tree, then performs the same selected/unselected assertions. The assertion itself was not weakened.

### Custom Puzzle editor accessibility

**Finding:** editor cell selection/conflict state was primarily visual and empty cells did not expose the same useful row/column semantics as the main game board.

**Fix:** editor cells now expose localized row/column/value descriptions, conflict text, Compose selected semantics, and stable per-cell test tags. Connected coverage verifies selected-state transitions.

### Custom Puzzle localization boundary

**Finding:** validation/solver status prose lived as raw English strings in `CustomPuzzleViewModel`.

**Fix:** the ViewModel emits typed `CustomPuzzleMessage` state. Compose maps the state to paired English/Hindi resources.

### Game error localization boundary

**Finding:** abandon and load errors could expose raw English/exception text through `GameScreenState.Error`.

**Fix:** the ViewModel now exposes typed `GameError` state and `GameErrorPresentation.kt` maps it to paired English/Hindi resources.

### Game completion summary

**Finding:** the completion dialog concatenated English `mistake(s)` and `hint(s)` fragments even though a localized completion resource already existed.

**Fix:** the dialog now uses `v04_completion_summary` and its existing Hindi counterpart.

### Room migration warning

**Finding:** the migration override parameter name differed from the current Room API parameter name and produced a compiler warning.

**Fix:** the parameter is aligned to `db`; SQL/schema behavior is unchanged.

### Backup file boundary

**Audit:** `BackupFileIo` uses bounded reads/writes based on `BackupCodec.MAX_BACKUP_BYTES`; JVM coverage includes empty, oversized, exact-limit, UTF-8, and invalid-limit cases.

### Backup format boundary

**Audit:** `SNB1` decoding enforces maximum bytes, line/record/text/counter/time bounds, schema marker/checksum validation, and fail-closed parsing. Restore semantics remain duplicate-safe.

### Puzzle-code boundary

**Audit:** `SNP1` has a maximum code length, version marker, difficulty parsing, 81-cell payload requirement, checksum validation, Sudoku validity validation, and Android-side unique-solution validation before imported play.

### Manifest and permissions

**Audit:** the current app manifest declares no runtime permissions. The launcher activity is exported for the launcher intent filter; no broad storage permission is required for document-picker transfer.

### Room configuration

**Audit:** the database is explicitly versioned, `MIGRATION_1_2` is registered, destructive fallback is not enabled, and current history/saved/challenge indexes cover the principal existing lookup/filter/sort fields. No speculative migration was introduced merely for v0.9.

### Repository secrets and signing

**Audit/fix:** CI runs the repository security guard through `scripts/verify_no_secrets.py`, which delegates to the authoritative verifier. Production signing material remains outside Git and release documentation does not substitute debug signing for production signing.

### Release build gates

**Added:** standard CI verifies debug/release lint, debug APK, minified/resource-shrunk release APK, release AAB, test reports, and short-lived release build evidence artifacts.

## State-layer main-thread review

Reviewed UI/state areas include:

- `GameViewModel`;
- `CustomPuzzleViewModel`;
- `TransferViewModel`;
- `ChallengesViewModel`;
- `HistoryViewModel`;
- `SavedPuzzlesViewModel`;
- `LearnViewModel`;
- `StatisticsViewModel`;
- `HomeViewModel`;
- app/settings state.

The correctness-sensitive CPU paths identified in game generation, game hints, Custom Puzzle solving, and imported-puzzle uniqueness analysis are dispatched away from the main thread. Backup document I/O uses `Dispatchers.IO`; Room and DataStore are accessed through their asynchronous APIs.

This is a source audit. It is not a substitute for measured Android traces on representative devices.

## Still requires evidence

The following must not be marked complete solely because this source audit exists:

- full manual TalkBack traversal/focus order;
- 200% font-scale and representative window/device layout QA;
- high-contrast and reduced-motion manual verification;
- measured startup/frame/memory/ANR traces on representative hardware;
- final signed artifact installation/signing verification;
- real store screenshot/listing review;
- final exact-head Android CI;
- final exact-head API-35 connected instrumentation.

## Evidence rule

When the branch head changes, prior CI results are not final-head evidence. The final release decision must cite workflow runs for the exact commit being considered for merge/tagging.
