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

## Platform Exchange Boundary

The common `PuzzleExchangePlatform` interface keeps clipboard, sharing, and text-file operations out of common Compose code. Platform adapters are responsible only for transport; `SharedGameState.importPuzzleCode()` remains the validation authority.

The current adapters provide:

- Android: clipboard, native share sheet, and Storage Access Framework import/export requests;
- Desktop/JVM: system clipboard and native file dialogs;
- Web/Wasm: bounded browser clipboard/file/share seams, with asynchronous browser APIs still requiring a future suspend/callback contract for full parity;
- Apple hosts: a bounded adapter seam for native host integration.

Adapters enforce bounded payloads before transport. File operations are also bounded on the read/write path; a platform must not turn arbitrary external text into a playable puzzle without passing the shared SNP1 validator.

## Shared UI Wiring

`SudokuNovaSharedApp()` accepts an optional `PuzzleExchangePlatform`. When supplied, the common `PuzzleExchangePanel` renders the reusable Copy, Paste, Share, Import file, and Export file controls through `PuzzleExchangeCoordinator`.

The Android `CrossPlatformActivity` now supplies `AndroidPuzzleExchangePlatform(this)` and forwards document-picker results back to that adapter. Existing callers that do not supply a platform retain the code-only exchange UI, so the common API remains backward compatible.

Desktop, Web/Wasm, and Apple hosts still require their native lifecycle integration before their transport operations can be considered production-complete.

## Testing Expectations

Changes to this contract should retain coverage for:

- SNP1 checksum and unique-solution validation;
- imported assessment/provenance retention;
- SNG1 backward compatibility;
- deterministic SNG2 round trips;
- malformed/oversized payload rejection;
- fixed-clue and note integrity during restore;
- invalid import fail-closed behavior;
- platform payload bounds before adapter invocation;
- English/Hindi translation parity;
- Android, Desktop, Web/Wasm, and Apple compilation paths where the shared UI is used;
- Android shared-host document lifecycle wiring.
