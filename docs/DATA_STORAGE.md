# Data Storage

SudokuNova is designed to work without an account. Core game, history, challenge, saved-puzzle, transfer, and learning data remain local unless the user explicitly exports or shares something through Android system UI.

## Preferences DataStore

`AppPreferencesRepository` uses Android Preferences DataStore for lightweight preferences and counters.

### Stored settings

Examples include:

- Theme selection
- Dynamic color
- Cell-first/number-first input mode
- Peer/same-number highlighting
- Automatic mistake checking
- Automatic note cleanup
- Timer visibility
- Haptics/sound preferences
- Reduced motion/high contrast
- Mistake limit

### Active game

The current active game is encoded by `GameStateCodec` and stored as a versioned text value. It includes the original puzzle, solution, current board, notes, selection, notes mode, elapsed time, mistakes, hints, difficulty, seed, pause/status metadata, challenge metadata, and replay metadata where applicable.

Malformed encoded state is rejected by the decoder instead of being partially trusted.

### Aggregate statistics

DataStore values include:

- Games started/completed/abandoned
- Total play time
- Best time
- Total mistakes/hints
- Perfect games
- No-hint games
- Current/longest streak
- Last completed epoch day used for streak calculation

### v0.8 learning progress

Learning progress is also stored in Preferences DataStore. Every supported `LogicalTechnique` has local counters for:

- lesson views;
- practice attempts;
- practice successes.

Counters are bounded in `AppPreferencesRepository` to avoid integer overflow. The app derives mastery from these counters through the pure `LearningProgress` model.

Learning progress does not require a user account, network access, analytics service, advertising SDK, or cloud backend.

`resetLearningProgress()` removes only learning counters. It does not remove:

- settings;
- active game state;
- game statistics;
- Room history;
- saved/custom puzzles;
- challenge records.

## Room structured storage

SudokuNova uses Room for queryable collections introduced in the player-data milestones. Repository-backed Room data includes history/saved/challenge-related records and is covered by schema/migration and instrumentation checks where applicable.

Structured-storage rules:

- Entity and schema versions are explicit.
- DAO behavior is tested.
- Production migrations must be tested before schema changes merge.
- Destructive migration must not silently erase user data.
- Useful indexes should follow actual query patterns.
- Repository APIs remain the application-facing boundary instead of exposing raw database details to Compose UI.

## Import/export and backups

v0.7 introduced versioned local transfer formats. Imported puzzle codes and backup data are untrusted input and must pass format, bounds, checksum, size, and duplicate-safety checks before they affect local state.

Learning counters are intentionally independent from game correctness and do not influence puzzle generation, game history, or solver behavior.

## Versioning

`GameStateCodec` contains an explicit encoding version. Transfer/backup formats also carry their own format/version identity. Any incompatible change must either migrate old state or deliberately reject it with safe fallback behavior.

New persistent fields should not silently change an existing wire format. If a future backup version includes learning progress, it must be introduced as an explicit compatible format evolution rather than changing prior backup semantics in place.

## Data integrity rules

- Never persist a board value outside 0–9.
- Never accept a custom puzzle as unique without solver validation.
- Do not record completion twice.
- Handle corrupted/unsupported active saves without crashing.
- Treat imported backup/puzzle data as untrusted.
- Do not let learning counters modify Sudoku board truth or solver decisions.
- Keep practice successes less than or equal to practice attempts.
- Keep learning counters non-negative and bounded.

## Reset behavior

Statistics reset removes only statistics/streak keys. Learning reset removes only learning keys. Transfer restore follows the documented backup merge/replace rules. None of these operations should silently erase unrelated local data.
