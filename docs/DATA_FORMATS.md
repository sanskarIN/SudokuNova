# SudokuNova Data Formats

This document describes persistent and transferable formats that are part of SudokuNova's compatibility boundary. Format changes must be deliberate because old active games, databases, puzzle codes, and backups can outlive the code version that created them.

## General Compatibility Rules

For any persistent or transferable format:

- validate before use;
- fail closed on malformed input;
- bound input size/counts;
- do not silently reinterpret unsupported versions;
- preserve backward compatibility when practical;
- add migrations/versioned decoders when compatibility requires them;
- add deterministic regression tests;
- update this document, `DATA_STORAGE.md`, and release notes when the contract changes.

## Sudoku Board String

The core engine represents a Classic Sudoku board as 81 cells.

Canonical serialized puzzle strings use 81 numeric characters:

- `0` = empty;
- `1`–`9` = placed clue/value.

`SudokuBoard.parse()` also accepts `.` for an empty cell during parsing where supported by the board parser.

A board is not considered safe merely because it has 81 characters. Sudoku validity and, for playable imports, unique solvability are separate checks.

## `SNP1` Puzzle Code

Puzzle sharing uses the versioned `SNP1` format implemented by `PuzzleCodeCodec`.

Logical structure:

```text
SNP1.<DIFFICULTY>.<81-digit-puzzle>.<CRC32>
```

Example shape only:

```text
SNP1.EASY.000000000...<81 digits total>...000000000.A1B2C3D4
```

The checksum is calculated over:

```text
<DIFFICULTY>.<81-digit-puzzle>
```

and rendered as an uppercase eight-digit hexadecimal CRC32 value.

### Decoder constraints

The current codec requires:

- non-empty trimmed code;
- maximum length `160` characters;
- exactly four dot-separated fields;
- exact version `SNP1`;
- difficulty matching the `Difficulty` enum;
- exactly 81 puzzle digits;
- digits only (`0`–`9`);
- matching CRC32 checksum;
- valid Sudoku board constraints.

The shared import flow performs an additional unique-solution analysis before accepting a decoded puzzle for play.

### Compatibility rule

Do not alter `SNP1` semantics in place. If the transport contract changes incompatibly, introduce a new explicit version such as `SNP2` and retain a decoder strategy for supported older codes.

## `SNB1` Backup Format

Application backup/restore uses the versioned text format implemented by `BackupCodec`.

The first line is:

```text
SNB1
```

Records are line-oriented and pipe-separated. The current record families are:

- `S` — settings;
- `H` — history;
- `P` — saved puzzle;
- `C` — challenge result.

The final line is a footer:

```text
Z|<CRC32>
```

The checksum covers all lines before the footer, joined with newline characters.

### Current hard bounds

`BackupCodec` enforces:

- maximum total UTF-8 size: `2 MiB`;
- maximum history records: `5,000`;
- maximum saved puzzles: `2,000`;
- maximum challenge results: `2,000`;
- maximum logical line count: `10,005`;
- maximum encoded text-field bytes: `512`;
- bounded counters;
- bounded elapsed time;
- bounded timestamps.

The file I/O layer also performs bounded reading so an oversized stream can be rejected before creating an unbounded in-memory string.

### Settings record

The current settings record carries values including:

- theme;
- dynamic color;
- input mode;
- peer highlighting;
- same-number highlighting;
- automatic mistake checking;
- automatic note removal;
- timer visibility;
- haptics;
- sounds;
- reduced motion;
- high contrast;
- mistake limit.

Enum values are validated against current enums and booleans use strict parsing.

### History record

History backup records include puzzle/solution and gameplay metadata such as:

- difficulty;
- completion state;
- elapsed time;
- mistakes;
- hints;
- start/completion timestamps;
- Daily Challenge marker;
- perfect status;
- Favorite status;
- replay marker.

The codec validates puzzle/solution relationships, counter/timestamp bounds, difficulty values, and perfect-game consistency.

### Saved-puzzle record

Saved-puzzle records can include:

- puzzle;
- optional solution;
- optional title;
- difficulty;
- source;
- creation timestamp;
- Favorite state.

Text values use bounded encoding/decoding rules.

### Challenge record

Challenge records preserve challenge identity/performance data and are validated against the challenge type/difficulty/value bounds expected by the codec.

### Backup security properties

`SNB1` provides integrity/error detection through CRC32 and strict structural validation. It is **not encryption** and should not be described as confidential storage.

Anyone who obtains a backup text file may be able to read its contents. Users should protect exported backups appropriately.

### Compatibility rule

Never change `SNB1` field meaning/order in place if it would make existing backups ambiguous. Introduce a new version and explicit migration/decoder handling for incompatible changes.

## Preferences DataStore

Preferences DataStore stores lightweight local application state, including categories such as:

- user settings;
- active-game serialized state;
- aggregate gameplay statistics;
- streak values;
- per-technique learning counters.

DataStore keys are an internal persistent contract. Renaming/removing a key can effectively reset or orphan user data, so key changes should be reviewed like a migration.

Learning counters are bounded to avoid integer overflow.

## Active Game Format

The mature Android active game is encoded/decoded through `GameStateCodec` and stored through `AppPreferencesRepository`.

The shared cross-platform active game is encoded/decoded through `SharedGameSnapshotCodec`.

### Shared `SNG1` / `SNG2` snapshots

Generated shared sessions use `SNG1` and include:

```text
SNG1|<difficulty>|<seed>|<current-board>|<selected-index>|<notes-mode>|<notes>
```

Imported shared sessions use `SNG2` and append the canonical validated SNP1 source code:

```text
SNG2|<difficulty>|<seed>|<current-board>|<selected-index>|<notes-mode>|<notes>|<source-code>
```

`SNG1` remains readable for backward compatibility. Generated sessions continue to encode as SNG1 so older stored data is not rewritten unnecessarily.

For SNG2, the source code is authoritative for reconstructing the starting puzzle and solution. Restore validates the source code again, checks that the restored board does not change fixed clues, and rejects notes on fixed or filled cells. Invalid snapshots must leave the current game unchanged.

The shared codec bounds encoded size, board length, selection indexes, note indexes/values, source-code length, and field structure. Unsupported versions fail closed.

The shared text-storage boundary remains platform-neutral; Android, Desktop, Web, and Apple hosts can provide native local storage without putting platform APIs in common code.

## Room Database

The current Room database is `sudokunova.db` and uses exported schemas.

Current entity families include:

- game history;
- saved puzzles;
- challenge results.

The database is versioned and uses explicit migrations. Existing code includes migration `1 → 2` for challenge results.

### Important database constraints

Examples of intentional constraints/indexes include:

- auto-generated primary keys;
- saved-puzzle puzzle uniqueness;
- difficulty indexes;
- Favorite indexes;
- completion-time indexes;
- challenge type/key uniqueness;
- challenge completion-time index.

Do not remove uniqueness or migration guarantees simply to make an import easier.

## Room Schema Files

Room schema export is enabled through KSP configuration and schemas are stored under `app/schemas/`.

When changing an entity/DAO/database version:

1. design the migration;
2. increment the database version when required;
3. generate/export the new schema;
4. retain old schema files needed for migration tests/history;
5. add/update migration tests;
6. verify backup/restore interactions;
7. document the change.

## Android Backup / Device Transfer Rules

The manifest references Android data-extraction/full-backup XML rules. Platform backup behavior is separate from the user-visible `SNB1` export feature.

Before a production release, Android backup rules and the privacy policy must be reviewed together so the shipped behavior matches the stated privacy/data-retention model.

## Clipboard and Share Payloads

Puzzle codes and result text can leave the application through explicit user actions such as clipboard/share sheet. These are user-directed exports and are not equivalent to background telemetry.

The shared 2.0.15 UI intentionally exposes selectable puzzle-code text without adding an Android-only clipboard dependency to common code. Native clipboard/share/file-picker adapters remain a follow-up cross-platform boundary.

Do not add hidden identifiers, secrets, or unrelated local data to share payloads.

## Format Change Review Checklist

A persistent-format pull request should answer:

- What old data exists?
- Can the new code read it?
- Can old code encounter new data?
- Is an explicit version bump needed?
- Are size/count limits still safe?
- Are integrity checks still valid?
- Are database indexes/constraints preserved?
- Does backup restore remain duplicate-safe?
- Does replay/challenge provenance survive?
- Are tests deterministic?
- Is the privacy/security documentation still accurate?
