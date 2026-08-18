# SudokuNova Privacy Policy

Last updated: 2026-08-18

This policy describes the current open-source SudokuNova base application represented by this repository. It must be reviewed again before production distribution and whenever data handling, permissions, analytics, advertising, accounts, cloud services, or external SDK behavior changes.

## Privacy Summary

The current base application is designed to work without:

- a SudokuNova account;
- a SudokuNova-operated remote gameplay backend;
- advertising SDKs;
- analytics SDKs;
- cloud synchronization dependency;
- location, contacts, microphone, camera, SMS, or call-log permissions for core play.

Core gameplay and learning data is stored locally on the Android device/application storage.

SudokuNova does support explicit user-directed actions that can send data outside the app, such as Android sharing, clipboard use, document-picker export/import, external GitHub/Buy Me a Coffee links, and email support.

## Local Data Stored

The current application can store categories such as:

### Preferences DataStore

- theme and dynamic-color preference;
- input mode;
- highlight/mistake/note settings;
- timer/haptics/sound preferences;
- reduced-motion/high-contrast preferences;
- mistake limit;
- active-game serialized state for resume;
- aggregate gameplay/statistics/streak values;
- local per-technique learning progress counters.

### Room database

- completed-game History;
- puzzle/difficulty/timing/mistake/hint metadata;
- completion timestamps;
- Favorite state;
- replay provenance;
- Saved Puzzles and related metadata;
- challenge completion records;
- challenge identity/performance information.

These records are application-local unless copied/exported through user/platform actions.

## Why Data Is Stored

Local data supports functionality such as:

- remembering settings;
- resuming an in-progress Sudoku;
- displaying History/Saved Puzzles;
- statistics and streaks;
- Daily/Weekly Challenge records;
- Favorite/replay behavior;
- learning/practice progress;
- backup/restore.

## Data Transmission by SudokuNova

The current open-source base application does not include a SudokuNova-operated network service that receives gameplay/history/learning data.

It does not include a bundled analytics or ads SDK in the current documented base application.

If those behaviors are introduced in a future build, this policy and applicable consent/store disclosures must be updated **before** that build is represented as released.

## Explicit User-Directed External Actions

Data or navigation can leave SudokuNova when the user explicitly chooses an external action.

Examples include:

- sharing a puzzle code through Android's share sheet;
- sharing a game result;
- copying/pasting transfer text through the clipboard;
- exporting/importing a backup through Android's document picker;
- opening the GitHub repository;
- opening Buy Me a Coffee;
- opening an email client for support.

SudokuNova does not control the privacy practices of the destination app/service chosen by the user.

## Puzzle Codes

The `SNP1` puzzle-share format contains puzzle/difficulty/integrity data. It is not intended to contain player identity, credentials, or hidden tracking identifiers.

## User Backup Files

The `SNB1` user-controlled backup format can contain supported local data such as:

- settings;
- History records;
- Saved Puzzles;
- challenge results.

The format uses validation/checksum protection but is **not encrypted**.

Anyone with access to an exported backup file may be able to read its gameplay-related contents. Users should choose storage/sharing destinations accordingly.

SudokuNova does not automatically upload `SNB1` files to a SudokuNova server.

## Android Platform Backup / Device Transfer

The Android manifest/configuration allows Android backup/device-transfer behavior according to the XML rules in the app resources.

This platform-managed behavior is separate from the explicit `SNB1` user export.

Actual Android backup/device-transfer behavior can depend on device, OS, account and distribution settings. The backup XML configuration must be reviewed together with this privacy policy before a production release.

## Android Permissions

The current manifest does not declare broad sensitive runtime permissions for core gameplay such as:

- location;
- contacts;
- microphone;
- camera;
- SMS;
- phone/call logs;
- broad storage access.

File transfer uses Android document-picker/content-URI flows rather than requesting unrestricted storage access.

If future features add a permission, documentation/store disclosures must explain why it is necessary.

## Data Deletion and Reset

SudokuNova provides scoped local reset behavior for categories such as statistics and learning progress where implemented.

Important scope examples:

- resetting learning progress removes learning counters without intentionally erasing History, Saved Puzzles, settings, challenges, or the active game;
- database/history/saved-puzzle reset operations should affect only the explicitly confirmed scope;
- completed/cleared active games remove the active-game resume state as appropriate.

Users can also remove application-local data through Android system app-data clearing or uninstall behavior.

Before stable release, all reset/delete controls should be verified against `DATA_STORAGE.md` and release QA so UI wording matches actual deletion scope.

## Data Integrity and Import Safety

Imported puzzle/backup data is treated as untrusted.

SudokuNova uses controls including:

- format versions;
- checksums;
- input-size limits;
- record-count/field bounds;
- Sudoku validity checks;
- unique-solution validation before imported/custom play;
- duplicate-safe restore behavior.

These controls protect integrity and resource usage. They are not a privacy guarantee if the user intentionally shares a file/code externally.

## No Sale of User Data by the Base App

The current open-source base application does not implement a system for selling gameplay/history/learning data or sharing it with advertisers.

This statement applies to the behavior represented by this repository and must be revisited if third-party commercial/analytics services are added.

## Children and Accounts

The current base application does not require an account and does not ask users to create a SudokuNova profile containing personal identity information.

If future account/community features are added, age/privacy/legal requirements must be reviewed before release.

## Security

The repository security model aims to minimize unnecessary permission/network surface and validate imported data.

For technical security details see:

- `SECURITY.md` in this docs directory;
- root `../SECURITY.md` for vulnerability reporting;
- `DATA_FORMATS.md` for parser boundaries.

## Policy Changes

This policy must be updated before release when behavior changes materially, including the addition of:

- analytics;
- crash-reporting data transmission;
- advertising;
- account/login systems;
- cloud sync;
- online leaderboards;
- multiplayer/network services;
- new sensitive permissions;
- new third-party SDKs that collect/transmit user/device data.

## Contact

Support: `supportramsandesh@gmail.com`  
Business: `sanskarin@outlook.in`  
Business: `sanskarin.business@gmail.com`

Repository: https://github.com/sanskarIN/SudokuNova

**Made by the Sanskar**
