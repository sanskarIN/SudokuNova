# History and Saved Puzzles

SudokuNova v0.5 introduces versioned Room persistence for completed-game history and saved puzzles. This storage remains local to the Android application and does not require an account or network connection.

## Database

Database class:

`com.sanskar.sudokunova.data.history.SudokuNovaDatabase`

Database file:

`sudokunova.db`

Current Room schema version:

`1`

The schema is exported into `app/schemas/` and committed to Git so future migrations can be reviewed and tested. Production code does not use destructive migration as a shortcut.

## Game History

Each completed attempt records:

- original puzzle string,
- solution string,
- difficulty,
- completion status,
- elapsed time,
- mistakes,
- hints used,
- approximate start time,
- completion time,
- Daily Challenge flag,
- perfect-game flag,
- favorite state,
- replay provenance when the attempt is a replay of an earlier history row.

The History screen supports:

- newest-first local history,
- All/Favorites scope,
- difficulty filtering,
- per-difficulty summaries,
- favorite toggling,
- deletion with confirmation,
- replaying completed puzzles.

## Replay Statistics Policy

Replaying a completed puzzle creates a new history row with `replayOfHistoryId` pointing to the source attempt.

Replay attempts remain visible in History but are excluded from the normal per-difficulty summary query. They also do not increment the existing DataStore aggregate completion/streak statistics. This prevents repeatedly replaying one puzzle from inflating normal progression statistics while still preserving the replay attempt itself.

## Saved Puzzles

`saved_puzzles` stores:

- puzzle string,
- optional solution,
- optional title,
- difficulty,
- source,
- creation time,
- favorite state.

The puzzle string has a unique index, so the same puzzle is not duplicated in the saved collection.

Validated custom puzzles can be saved locally from the Custom Puzzle screen. Saved Puzzles supports:

- All/Favorites scope,
- favorite toggling,
- deletion,
- playing a saved puzzle later.

When a saved puzzle does not store a solution, the existing custom-puzzle game path validates and solves it again before gameplay begins.

## Data Integrity

Room constraints and application checks are used together:

- puzzle strings remain validated by the Sudoku engine when played,
- duplicate saved puzzle strings are ignored by the unique index,
- difficulty strings are parsed defensively by UI code,
- elapsed/mistake/hint values are normalized before writing history,
- Room is accessed off the normal synchronous UI path through suspend/Flow APIs,
- database version changes must add explicit migrations rather than destructive fallback.

## Tests

Connected Android tests cover:

- history insert/read,
- favorite update,
- per-difficulty aggregates,
- saved-puzzle unique constraint,
- saved-puzzle favorite toggling,
- replay history visibility,
- replay exclusion from normal difficulty summaries.

Compose smoke tests cover navigation into History and Saved Puzzles.

## Future Migration Rule

Schema version 1 is the first Room schema, so there is no earlier Room schema to migrate from. Starting with schema version 2, every production schema change must include:

1. an explicit Room `Migration`,
2. a migration test using the exported prior schema,
3. updated schema JSON committed to `app/schemas/`,
4. changelog and data-storage documentation updates.

No stable release should introduce a Room schema change without those migration checks.
