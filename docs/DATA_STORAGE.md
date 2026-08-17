# Data Storage

## Current Persistence Technology

SudokuNova currently uses Android Preferences DataStore through `AppPreferencesRepository`.

## Stored Settings

Examples include:

- Theme selection
- Dynamic color
- Peer/same-number highlighting
- Automatic mistake checking
- Automatic note cleanup
- Timer visibility
- Haptics/sound preferences
- Reduced motion/high contrast
- Mistake limit

## Active Game

The current active game is encoded by `GameStateCodec` and stored as a versioned text value. It includes the original puzzle, solution, current board, notes, selection, notes mode, elapsed time, mistakes, hints, difficulty, seed, pause/status metadata, and Daily Challenge marker.

Malformed encoded state is rejected by the decoder instead of being partially trusted.

## Statistics

Aggregate DataStore values currently include:

- Games started/completed/abandoned
- Total play time
- Best time
- Total mistakes/hints
- Perfect games
- No-hint games
- Current/longest streak
- Last completed epoch day used for streak calculation

## Versioning

`GameStateCodec` contains an explicit encoding version. Any incompatible change must either migrate old state or deliberately reject it with safe fallback behavior.

## Future Structured Storage

Room is planned when the application needs queryable collections such as:

- Game history
- Per-difficulty records
- Saved/favorite puzzles
- Custom-puzzle archive
- Challenge history/calendar

When introduced:

- Entities and schema versions must be explicit.
- DAO behavior must be tested.
- Production migrations must be tested.
- Destructive migration must not silently erase user data.
- Useful indexes should be based on actual query patterns.

## Data Integrity Rules

- Never persist a board value outside 0–9.
- Never accept a custom puzzle as unique without solver validation.
- Do not record completion twice.
- Handle corrupted/unsupported active saves without crashing.
- Imported future backup data must be treated as untrusted.

## Reset Behavior

Statistics reset removes only statistics/streak keys. It intentionally leaves user preferences and the current active game unchanged.

## v0.5 Room History Database

SudokuNova now uses a Room database named `sudokunova.db` for completed-game history and saved puzzles. The initial Room schema is version 1 and is exported to `app/schemas/`. DataStore remains responsible for lightweight settings, active-game state, and aggregate statistics.

Production database construction does not use destructive migration. Any future schema version must include an explicit migration and migration test. See [History and Saved Puzzles](HISTORY_AND_SAVED_PUZZLES.md).
