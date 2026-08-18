# SudokuNova Privacy Policy

Last updated: 2026-08-18

This policy describes the behavior of the current open-source SudokuNova base application represented by this repository. It must be reviewed again before production distribution and whenever data behavior changes.

## Core Privacy Model

SudokuNova is designed to work offline without an account.

The current open-source base application does not include:

- advertising SDKs;
- analytics SDKs;
- remote crash-reporting SDKs;
- user accounts or profile registration;
- a SudokuNova-operated network backend;
- cloud synchronization;
- remote gameplay telemetry.

The main Android manifest contains no `<uses-permission>` declarations. In particular, the app does not request contacts, location, microphone, camera, SMS, call-log, broad storage, or network permissions for core operation.

The application also explicitly disables cleartext network traffic as a defense-in-depth release invariant.

## Data Stored on the Device

SudokuNova stores data needed for offline gameplay and learning.

### Preferences DataStore

Examples include:

- theme and dynamic-color preference;
- input and highlighting preferences;
- timer, haptics, sound, high-contrast, and reduced-motion preferences;
- mistake-limit preference;
- active puzzle/session state for resume;
- aggregate game statistics;
- streak information;
- per-technique lesson views;
- per-technique practice attempts and successes.

### Room database

Structured local records include data used by features such as:

- completed-game history;
- saved/favorite puzzles;
- custom-puzzle records;
- challenge records and challenge performance.

The exact schema is versioned and exported by Room for migration testing and review.

## Why Local Data Is Stored

Local data is used to:

- remember user preferences;
- restore an active Sudoku after activity/process recreation;
- show local statistics, achievements, and streaks;
- browse/replay local history and saved puzzles;
- preserve local custom puzzles and challenge records;
- show offline learning/practice progress.

Learning counters do not alter Sudoku solution truth, puzzle generation, or game history.

## Data Transmission

The current base application does not transmit gameplay, history, saved-puzzle, challenge, or learning data to SudokuNova servers because no SudokuNova backend is configured.

### Explicit user actions

Data can leave the app only when the user chooses an Android system-mediated action, for example:

- sharing a puzzle code;
- sharing a game result;
- copying or sharing an exported local backup;
- creating a backup document through the Android document picker;
- opening an external GitHub, Buy Me a Coffee, or email-support destination.

Once data is intentionally shared with another app/service, that destination's privacy practices apply.

## Android Backup and Device Migration

SudokuNova does not allow its persisted app data to be copied into Android cloud backup through the configured Android 12+ extraction rules.

For Android 12+ device migration, the app allows ordinary app files, Room databases, and preferences to participate in device-to-device transfer. This helps a user migrate their local state directly between devices without turning cloud backup into an implicit synchronization feature.

For Android 11 and lower, the legacy backup rules restrict included files/databases/preferences with the `deviceToDeviceTransfer` transport flag.

SudokuNova also provides its own explicit versioned backup/export and restore/import flow. Imported backup data is treated as untrusted input and is subject to version, size, count, timestamp, counter, format, and duplicate-safety validation before it changes local state.

## Data Deletion and Reset

Available scoped controls include:

- statistics reset;
- learning-progress reset;
- replacement/clearing of active game state through normal game lifecycle;
- deletion/management flows exposed by the history/saved/custom-puzzle surfaces where implemented.

Learning reset does not remove game history or settings. Statistics reset does not remove learning progress or unrelated Room records.

Android system app-data clearing or uninstall removes the application's local data from that device.

## Clipboard, Share Sheet, and Document Picker

SudokuNova does not request broad storage permission for transfer features.

Backup document import/export uses Android's system document contracts and content URIs. URI stream I/O is dispatched off the main UI thread and is byte-bounded on import.

Clipboard and share-sheet operations happen only after an explicit user action. The app does not continuously monitor clipboard contents.

## Security-Related Privacy Invariants

The v0.9 release-hygiene CI check fails when key privacy/security assumptions drift, including:

- any permission appearing in the main manifest;
- unexpected exported Android components;
- cleartext traffic no longer being explicitly disabled;
- cloud-backup exclusions being removed;
- release minification/resource shrinking being disabled;
- signing/secret files becoming trackable by the repository configuration.

These checks are safeguards, not a claim that automated tooling can replace manual security/privacy review.

## Children and Accounts

SudokuNova does not currently require an account and does not ask users to submit profile information through the app.

## Future Data Features

If analytics, remote crash reporting, cloud sync, accounts, ads, or another data-transmitting service is ever introduced, it must not be represented as part of the released privacy model until this policy, user controls, platform disclosures, and applicable consent/legal requirements have been reviewed and updated.

## Contact

Support: `supportramsandesh@gmail.com`  
Business: `sanskarin@outlook.in`  
Business: `sanskarin.business@gmail.com`

Repository: https://github.com/sanskarIN/SudokuNova

**Made by the Sanskar**
