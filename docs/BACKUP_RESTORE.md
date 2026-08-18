# SudokuNova Backup and Restore

SudokuNova has two separate backup/device-transfer concepts:

1. Android-managed application backup/device transfer controlled by Android XML rules;
2. the user-controlled versioned `SNB1` export/import feature in **Backup & Transfer**.

Do not confuse these two mechanisms.

## User-Controlled `SNB1` Backup

The application can export supported local data to a text backup through Android's document picker.

The current format is versioned with header:

```text
SNB1
```

and uses a checksum-protected footer.

Supported record families include:

- settings;
- completed-game history;
- saved puzzles;
- challenge results.

The format intentionally does not execute imported content and is not an arbitrary object-serialization format.

## What Is Not an `SNB1` Confidentiality Feature

`SNB1` uses structural validation and CRC32 integrity/error detection. It is **not encrypted**.

An exported backup may contain gameplay history, saved puzzle titles/source information, settings, and challenge records. Users should protect the file if those details matter to them.

## Export Flow

At a high level:

1. Open Backup & Transfer.
2. Choose the backup/export action.
3. The application gathers supported local data.
4. `BackupCodec` validates record limits and encodes `SNB1` text.
5. Android's document picker lets the user choose a destination.
6. Bounded file I/O writes the UTF-8 backup.

The project does not require broad storage permission for this flow.

## Import / Restore Flow

At a high level:

1. Choose the restore/import action.
2. Android's document picker provides the selected document URI.
3. `BackupFileIo` performs a bounded read.
4. `BackupCodec` verifies version, size, checksum, line/record structure and field bounds.
5. `BackupRepository` restores accepted records using duplicate-safe persistence behavior.
6. The UI reports success/failure without executing arbitrary file content.

Malformed or unsupported content fails closed.

## Hard Limits

Current `BackupCodec` limits include:

- maximum backup size: `2 MiB` UTF-8;
- history records: at most `5,000`;
- saved puzzles: at most `2,000`;
- challenge results: at most `2,000`;
- total logical lines: bounded;
- text fields: bounded;
- counters, elapsed time, and timestamps: bounded.

The file reader also rejects a stream that exceeds the maximum rather than reading an unbounded file into memory.

See `DATA_FORMATS.md` for the maintained format reference.

## Validation

Restore validation includes categories such as:

- exact supported header/version;
- footer/checksum validation;
- record type/field-count validation;
- strict boolean/enum/number parsing;
- Sudoku puzzle/solution validation;
- timestamp/counter bounds;
- perfect-game consistency;
- record-count limits;
- string-field limits.

Unexpected record types or unsupported structures are rejected.

## Duplicate-Safe Restore

Restore behavior is designed to avoid unsafe duplication and loss of important state.

Important rules include:

- saved puzzle uniqueness is respected;
- Favorite state can be promoted during duplicate reconciliation rather than incorrectly demoted;
- replay provenance is preserved;
- challenge identity constraints are preserved;
- restored data must remain valid for Room constraints.

When changing restore behavior, add regression tests for duplicate and provenance cases.

## What the Backup Does Not Currently Promise

The user backup should not be described as a byte-for-byte clone of every Android application file.

It is a versioned application-level export of supported data categories. Internal caches, build information, Android runtime state, and unrelated platform metadata are not part of the `SNB1` contract.

Learning-progress counters/active game data should only be described as included if/when the current `SNB1` model explicitly contains them. Consult `BackupModels.kt`/`BackupCodec.kt` before changing this statement.

## Android Platform Backup / Device Transfer

The Android application also references:

- `res/xml/backup_rules.xml`;
- `res/xml/data_extraction_rules.xml`.

These control Android-managed backup/device-transfer domains and are separate from the explicit `SNB1` export.

Actual platform backup behavior depends on Android/device/distribution configuration. It must be reviewed with `PRIVACY.md` before production release.

## Compatibility

`SNB1` is a persistent transfer contract.

Do not change field order/meaning incompatibly under the same version. If an incompatible format is needed:

1. introduce a new explicit format version;
2. decide which older versions remain importable;
3. add a decoder/migration path or fail-safe unsupported-version result;
4. add round-trip and old-version tests;
5. update this file and `DATA_FORMATS.md`.

## Testing

Current automated coverage includes backup codec/repository behavior, bounded file reading, Room-integrated restore paths, and connected transfer flows.

Important regression cases include:

- export/import round trip;
- exact-size and oversized file boundaries;
- empty input;
- corrupt checksum;
- unknown record type;
- invalid puzzle/solution;
- invalid enum/boolean/number;
- out-of-range timestamps/counters;
- duplicate saved/history/challenge data;
- Favorite promotion behavior;
- replay provenance preservation.

See `TESTING.md`.

## Failure Handling

If a backup cannot be parsed or restored, the application should reject it safely and report an invalid/failed result. It should not partially interpret arbitrary unsupported lines.

For a restore defect that may corrupt/drop user data:

- treat it as release-blocking;
- preserve a safe reproduction file when possible;
- add regression coverage;
- verify database state before/after the fix.

## Privacy and Sharing

Backup files are user-controlled exports. SudokuNova does not automatically upload `SNB1` backups to a SudokuNova server.

Once a user saves or shares a file through Android/system/external apps, those destinations have their own privacy/security behavior.

Do not add credentials, device identifiers, secrets, or unrelated private app data to the backup format.
