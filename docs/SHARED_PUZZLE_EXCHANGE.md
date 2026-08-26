# Shared Cross-Platform Puzzle Exchange

This guide documents the common puzzle-code exchange boundary used by SudokuNova's shared UI.

## SNP1 Source Codes

`PuzzleExchangeService` exports and imports the versioned `SNP1` puzzle code:

```text
SNP1.<DIFFICULTY>.<81-digit-puzzle>.<CRC32>
```

Import is fail-closed. The code must decode successfully, pass Sudoku validation, and have exactly one solution before it becomes a playable shared session.

## Imported Session Provenance

`SharedGameState.importPuzzleCode()` retains the canonical SNP1 code as `sourceCode`. The active starting puzzle, solved board, difficulty, and assessment are reconstructed from the validated import.

The source code is authoritative for imported-session restoration; a generated seed is not used to recreate an imported puzzle.

## Active-Game Snapshots

Generated shared sessions continue to use `SNG1`.

Imported sessions use `SNG2`:

```text
SNG2|<difficulty>|<seed>|<current-board>|<selected-index>|<notes-mode>|<notes>|<source-code>
```

The imported source code is validated again during restore. A snapshot that changes a fixed starting clue, applies notes to a fixed/filled cell, or references an invalid source puzzle is rejected without mutating the current session.

`SNG1` remains readable for backward compatibility. Generated sessions continue to encode as SNG1 so older stored data is not rewritten unnecessarily.

## Shared UI

The common Compose UI exposes:

- an SNP1 input field;
- an explicit Import action;
- selectable current-puzzle code output;
- localized English/Hindi labels and feedback;
- non-mutating failure behavior for invalid imports.

Text selection is intentionally platform-neutral. Clipboard, share-sheet, and file-picker adapters are a follow-up boundary and should be implemented behind platform interfaces rather than adding Android-only APIs to common code.

## Testing Expectations

Changes to this contract should retain coverage for:

- SNP1 checksum and unique-solution validation;
- imported assessment/provenance retention;
- SNG1 backward compatibility;
- deterministic SNG2 round trips;
- malformed/oversized payload rejection;
- fixed-clue and note integrity during restore;
- invalid import fail-closed behavior;
- English/Hindi translation parity;
- Android, Desktop, Web/Wasm, and Apple compilation paths where the shared UI is used.
