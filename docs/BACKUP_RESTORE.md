# Backup and Restore

## Current Status

SudokuNova currently defines Android platform backup/device-transfer rules for local application data. A user-facing export/import backup feature is **planned**, not yet implemented.

## Android Platform Backup

The app includes:

- `res/xml/backup_rules.xml`
- `res/xml/data_extraction_rules.xml`

These currently permit applicable file/database/shared-preference domains for Android-managed backup/device transfer. Actual behavior depends on Android/device/distribution settings and must be reviewed before production release.

## Planned User-Controlled Backup

A future local backup feature may include:

- Settings
- Statistics
- Saved/current puzzles
- Custom puzzles
- History/favorites once implemented

It must use a versioned schema.

## Import Safety Requirements

Before any user-controlled import feature ships, it must enforce:

- File-size limits
- Backup schema version validation
- Strict field/value validation
- Puzzle value/rule validation
- Exception-safe parsing
- Rejection of malformed or unsupported data
- Clear conflict/replacement policy
- Confirmation before destructive replacement
- No execution of imported content

## Compatibility

A future backup format should include:

- Format version
- App schema version
- Export timestamp
- Explicit sections for data categories

New releases should either migrate older supported backup versions or reject them with a clear, non-crashing message.

## Privacy

Exports may contain gameplay history/settings. Do not include device identifiers, credentials, or unrelated private application data. The user should control where an exported file is shared.

## Testing Plan

When implemented, cover:

- Export/import round trip
- Empty dataset
- Large valid dataset within limits
- Corrupted file
- Unsupported version
- Missing required fields
- Out-of-range Sudoku values
- Duplicate/invalid puzzle data
- Replacement confirmation
- Migration from at least one older schema version
