# SudokuNova Feature Reference

This document is the implementation-aligned feature reference for SudokuNova. It describes functionality present in the repository through the v0.9 release-hardening development line. Planned ideas belong in `ROADMAP.md`, not in this file.

## Product Identity

- Project: **SudokuNova**
- Tagline: **Think. Solve. Master the Grid.**
- Platform: Android
- Minimum Android API: 26
- Application ID: `in.sanskar.sudokunova`
- Kotlin namespace: `com.sanskar.sudokunova`
- License: MIT
- Base application model: offline-first, local-data-first, no account required

## Classic Sudoku Engine

SudokuNova implements standard Classic 9×9 Sudoku with:

- immutable 81-cell board representation;
- row, column, and 3×3 box validation;
- conflict detection;
- candidate calculation;
- puzzle parse/serialization support;
- solver-backed solution discovery;
- no-solution and multiple-solution analysis;
- unique-solution validation;
- solver metrics including search nodes, guesses, backtracks, and depth;
- deterministic seeded generation;
- seven difficulty targets;
- logical-difficulty and complexity evidence;
- reproducible generator and solver regression coverage.

## Difficulty Levels

The supported difficulty enum covers seven targets:

1. Beginner
2. Easy
3. Medium
4. Hard
5. Expert
6. Master
7. Extreme

Difficulty is not determined only by clue count. The project also uses logical-analysis and solver evidence to calibrate puzzles. See `DIFFICULTY_SYSTEM.md` and `PUZZLE_GENERATION.md`.

## Gameplay

A normal game supports:

- cell-first input;
- number-first input;
- number entry 1–9;
- notes/candidate marks;
- erase;
- undo;
- redo;
- pause/resume;
- restart;
- timer;
- mistake tracking;
- optional mistake limit;
- automatic mistake checking;
- automatic removal of solved peer notes;
- same-number highlighting;
- peer highlighting;
- selected-cell highlighting;
- conflict/error presentation;
- hints;
- autosave and active-game resume;
- completion handling;
- replay-aware history/statistics behavior.

## Hardware Keyboard

The game board supports hardware-keyboard actions for navigation and gameplay, including arrow movement, number entry, erase, notes mode, and hint access. Keyboard behavior should remain covered by accessibility and large-screen QA.

## Hints and Teaching Evidence

The hint system is backed by the same structured teaching pipeline used by logical analysis. Supported logical techniques are:

- Naked Single;
- Hidden Single;
- Naked Pair;
- Pointing Pair / Triple;
- Box-Line Reduction;
- Hidden Pair;
- Naked Triple;
- Hidden Triple;
- X-Wing in row and column orientations.

A teaching step can carry:

- technique identity;
- source cells;
- optional source unit;
- target cells;
- exact candidate eliminations;
- optional final placement.

Hints prefer supported logical evidence. If the supported teaching pipeline cannot produce a placement, Reveal is an explicit solver-backed fallback and does not pretend to be a logical technique.

## Learning Center

The Learn area contains:

- introductory Sudoku lessons;
- candidate lessons;
- solving-habit guidance;
- technique lessons for each supported logical strategy;
- deterministic offline practice exercises;
- interactive answer choices;
- correct/incorrect practice feedback;
- local per-technique lesson views;
- local practice attempt/success counts;
- derived per-technique mastery;
- overall mastery;
- mastered-technique count;
- safe learning-progress reset.

Learning data is local and does not require an account or cloud service.

## Daily and Weekly Challenges

SudokuNova supports deterministic challenge identities for:

- Daily Challenge;
- Weekly Challenge.

Challenge functionality includes:

- deterministic keys/seeds;
- challenge-specific difficulty selection;
- archive/history presentation;
- completion record storage;
- first-completion performance records;
- challenge statistics;
- replay-safe provenance.

## Custom Puzzles

The Custom Puzzle flow supports:

- manual clue entry;
- contradiction validation;
- solvability validation;
- exactly-one-solution validation before play;
- solution preview/validation flow where implemented;
- playing the validated custom puzzle;
- saving custom puzzles;
- replaying saved custom puzzles.

Invalid, contradictory, unsolvable, or non-unique inputs must fail closed rather than becoming playable puzzles.

## History and Saved Puzzles

Room-backed local storage provides:

- completed-game history;
- difficulty metadata;
- elapsed time;
- mistakes;
- hints used;
- timestamps;
- perfect-game status;
- favorites;
- replay provenance;
- saved puzzles;
- saved-puzzle favorites;
- saved-puzzle source metadata;
- difficulty summaries.

Replay records are kept separate from original completion statistics so replays do not inflate first-play statistics.

## Statistics

Local statistics include aggregate gameplay and challenge-oriented metrics such as:

- games started/completed where represented by the current data model;
- completion rate;
- best/average timing data where available;
- mistakes and hints summaries;
- streak-related data;
- perfect-game information;
- difficulty summaries;
- challenge results;
- achievements/progress presentation.

Statistics are local to the device/application data unless the user explicitly exports a backup.

## Puzzle Sharing

SudokuNova supports the versioned `SNP1` puzzle-code format.

The codec validates:

- maximum code length;
- format version;
- difficulty enum;
- 81-cell puzzle payload;
- numeric cell encoding;
- CRC32 checksum;
- Sudoku board validity.

The Android transfer flow additionally validates unique solvability before an imported puzzle is accepted for play.

## Backup and Restore

The versioned `SNB1` backup format supports application data such as:

- settings;
- history records;
- saved puzzles;
- challenge results.

The codec uses bounded, fail-closed parsing with limits on total bytes, record counts, lines, text fields, counters, elapsed time, and timestamps. Restore logic is designed to preserve data integrity and avoid unsafe duplicate behavior.

See `BACKUP_RESTORE.md`, `TRANSFER_BACKUP_V07.md`, and `DATA_FORMATS.md`.

## Android Sharing and File Transfer

User-initiated transfer features use Android platform surfaces rather than broad storage access. Implemented flows include:

- copy/paste text;
- Android share sheet;
- puzzle-code sharing;
- result sharing;
- document-picker backup export/import.

The app does not require broad storage permission for these flows.

## Settings

Settings include controls for:

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
- mistake limit;
- local reset actions.

## Themes and Visual Design

The application supports:

- Material 3;
- light theme;
- dark theme;
- system theme behavior;
- dynamic color where supported;
- high-contrast board presentation;
- reduced-motion preference foundation;
- responsive phone/larger-width layouts;
- original launcher/splash vector assets.

## Accessibility

Implemented accessibility foundations include:

- row/column/value cell descriptions;
- original-clue semantics;
- conflict semantics;
- selected-cell semantics;
- hint source/target semantics;
- candidate-elimination semantics;
- final hint-placement semantics;
- hardware-keyboard support;
- high-contrast preference;
- large-font/adaptive layout foundations;
- content descriptions for major controls.

Automated semantics tests complement but do not replace manual TalkBack and device QA.

## Localization

Maintained player-facing resource sets include:

- English;
- Hindi.

CI verifies English/Hindi resource-key and formatting parity. Engine/domain logic avoids embedding player-facing localized prose where Android resources should own presentation.

## Privacy

The open-source base application is designed without:

- account requirement;
- SudokuNova-operated backend;
- ads SDK;
- analytics SDK;
- cloud synchronization dependency;
- unnecessary sensitive Android permissions.

Gameplay data is stored locally. External navigation occurs only when the user explicitly opens GitHub, Buy Me a Coffee, email, Android sharing, or document-picker surfaces.

## Security and Release Hardening

Release-hardening controls include:

- strict import validation;
- bounded backup parsing;
- checksum verification;
- unique-solution gates;
- no committed production signing material;
- repository secret/signing guard in CI;
- debug and release lint;
- R8/resource-shrunk release APK verification;
- release AAB verification;
- API-35 connected Compose/Room verification;
- release QA matrix and evidence rules.

## Project and Community Features

The repository includes:

- MIT license;
- contribution guide;
- code of conduct;
- security policy;
- support guide;
- authors/credits;
- third-party notices;
- changelog;
- roadmap;
- GitHub Actions CI;
- issue templates and repository policies;
- extensive technical documentation.

## Non-Features of the Current Base Application

The current base application does not claim to provide:

- cloud account sync;
- multiplayer Sudoku;
- online leaderboards;
- ads/analytics;
- broad storage access;
- Sudoku variants such as Killer, Samurai, Thermo, or Jigsaw;
- production publication merely because a release artifact can be built.

These should not be represented as implemented unless corresponding code and verification are added.
