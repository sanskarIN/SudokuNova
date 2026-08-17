# SudokuNova v0.7 — Secure Sharing, Import/Export, and Backup

SudokuNova v0.7 adds local, account-free transfer features while keeping the base application offline-first and privacy-preserving.

## Puzzle codes

Classic 9×9 puzzles use a versioned `SNP1` text format containing:

- the format version,
- difficulty,
- the 81-cell puzzle payload,
- a CRC32 integrity checksum.

Import rejects unsupported versions, oversized codes, malformed digits, invalid Sudoku clue layouts, or checksum-modified data. The app performs an additional uniqueness analysis before an imported code is offered for play.

Puzzle codes can be copied or shared from supported History/Saved Puzzle surfaces without including local database IDs or private application state.

## Backup format

Local backups use the versioned `SNB1` format. A backup may contain only supported local data:

- user settings,
- completed-game history,
- saved puzzles,
- Daily/Weekly challenge results.

The following are intentionally excluded:

- active/in-progress game state,
- local Room IDs,
- replay source IDs (only a replay/non-replay provenance flag is preserved),
- secrets,
- signing material,
- credentials,
- arbitrary paths or executable content.

The whole backup is checksum-protected. Text metadata is URL-safe Base64 encoded inside bounded records.

## Hard limits

The decoder fails closed when limits are exceeded. Current development limits include:

- maximum encoded backup size: 2 MiB,
- maximum history records: 5,000,
- maximum saved puzzles: 2,000,
- maximum challenge results: 2,000,
- bounded text metadata fields,
- bounded counters and elapsed times,
- bounded timestamps,
- non-negative challenge keys.

## Restore behavior

The complete text backup is decoded and validated before persistence begins.

Room-backed data is restored inside a database transaction. Existing data is not silently overwritten:

- exact/natural duplicate history records are skipped while backed-up Favorite state can promote an existing record,
- replay/non-replay provenance is preserved without trusting or restoring exported source IDs,
- duplicate saved puzzles are skipped by the existing unique-puzzle constraint while backed-up Favorite state can promote an existing puzzle,
- duplicate challenge results are skipped by the unique `(challengeType, challengeKey)` constraint,
- new local database IDs are generated instead of trusting exported IDs.

Backed-up settings are applied after the Room transaction succeeds. Room and DataStore are separate stores, so the current implementation does **not** claim cross-store atomicity.

## Clipboard and sharing

The Backup & Transfer screen can:

- validate a pasted puzzle code,
- play a validated unique imported puzzle,
- copy a local backup to the clipboard,
- share a local backup through Android's standard share sheet,
- restore a validated backup from clipboard text,
- share puzzle codes,
- share non-sensitive game result summaries.

No account is required.

## File import/export

Backup files use Android's Storage Access Framework through the system document picker. SudokuNova does not request broad storage permissions.

The file reader is bounded before parsing. Files larger than the supported backup limit are rejected rather than fully accumulated in memory.

The default exported filename is `SudokuNova-backup.snb`.

## Security principles

- Never execute imported content.
- Never interpret imported values as filesystem paths.
- Never trust imported database IDs.
- Never accept unknown backup or puzzle-code versions.
- Never continue parsing after size/count/schema validation fails.
- Validate puzzle clues and solution relationships.
- Validate challenge type, difficulty, counters, timestamps, chronology, and perfect-game consistency.
- Keep signing secrets outside backups and outside the repository.

## Testing

v0.7 adds unit/instrumentation coverage for:

- puzzle-code round trips,
- checksum tampering,
- oversized/unknown puzzle codes,
- backup round trips,
- backup checksum tampering,
- unknown backup records,
- puzzle/solution consistency,
- metadata hardening,
- bounded input streams,
- duplicate-safe Room restore,
- restored settings,
- Backup & Transfer navigation.

The milestone is not considered complete until standard Android CI and connected API-35 instrumentation both pass on the final PR head.
