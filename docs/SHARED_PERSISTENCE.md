# Shared Cross-Platform Active-Game Persistence

This guide documents the portable active-game persistence layer used by SudokuNova's Compose Multiplatform surface. It covers source behavior and repository verification only; it does not convert hosted builds into physical-device, store, signing, notarization, or distribution evidence.

## Scope

The mature Android application continues to own its existing Room/DataStore data model. Shared persistence is intentionally separate so Desktop, Web, iOS/iPadOS, and the staged Android shared host can save the portable gameplay state without importing Android storage APIs into common Kotlin.

The shared layer persists only the active shared game required to resume the portable gameplay surface:

- difficulty;
- deterministic generator seed;
- current 81-cell board;
- candidate notes;
- selected cell;
- notes-mode state.

Undo history is intentionally not persisted. History is cleared during restore so stale transient actions cannot cross a process/application boundary.

## Common types

`SharedGameSnapshot` is the platform-neutral state model.

`SharedGameStore` is the snapshot-level suspendable contract:

```text
load() -> SharedGameSnapshot?
save(snapshot)
clear()
```

`SharedGameTextStore` is the minimal native-storage boundary:

```text
read() -> String?
write(value)
clear()
```

`EncodedSharedGameStore` composes the two contracts. Platform code only stores one text value; common code owns the versioned encoding and validation rules.

The common storage key is:

```text
sudokunova.shared.active-game.v1
```

## `SNG1` format

`SharedGameSnapshotCodec` writes a deterministic bounded text payload with version marker `SNG1`.

The top-level field order is:

```text
SNG1|DIFFICULTY|SEED|BOARD|SELECTED_INDEX|NOTES_MODE|NOTES
```

Example shape:

```text
SNG1|HARD|42|<81 board digits>|40|1|3:27,40:159
```

Rules:

- the encoded payload is capped at 2048 characters;
- the version must be exactly `SNG1`;
- difficulty must match a supported `Difficulty` enum entry;
- seed must parse as a signed `Long`;
- board must contain exactly 81 digits in `0..9`;
- selected index is `-1` for no selection or `0..80` for a selected cell;
- notes mode is `0` or `1`;
- note cell indexes are unique and bounded to `0..80`;
- note digits are unique non-empty values in `1..9`;
- note entries and digits are encoded in sorted order for deterministic output.

Malformed or unsupported encoded text decodes to `null` rather than publishing partially parsed state.

## Two-stage restore validation

Decoding is not sufficient to restore a game.

`SharedGameState.restore` remains the Sudoku-aware authority. After transport decoding it:

1. regenerates the starting puzzle from saved difficulty and seed;
2. parses the saved board;
3. requires every original fixed clue to remain unchanged;
4. validates every note index/value;
5. rejects notes on fixed or already-filled cells;
6. validates the selected-cell bound;
7. copies accepted note sets;
8. clears undo history;
9. publishes restored state only after all checks pass.

This separation prevents platform storage adapters from becoming a second Sudoku-validation implementation.

## Compose restore/autosave helper

`rememberPersistedSharedGameState(store)` owns the common Compose lifecycle for the shared surface:

- create a fresh `SharedGameState`;
- attempt one restore from the supplied store;
- leave the fresh game playable if storage is absent/corrupt/unavailable;
- begin saving observable snapshots only after the restore attempt completes;
- retry naturally when later snapshot changes cause another save attempt.

Storage failure is therefore isolated from Sudoku correctness and does not make the shared UI unusable.

## Platform adapters

### Android shared host

`CrossPlatformSharedPreferencesGameTextStore` uses application-private `SharedPreferences` only for the non-exported staged `CrossPlatformActivity` shared UI.

`CrossPlatformActivity` restores through the common store contract and saves on stop. The mature `MainActivity` Room/DataStore behavior is not replaced or migrated by this adapter.

### Desktop JVM

`DesktopPreferencesGameTextStore` uses `java.util.prefs.Preferences` beneath the SudokuNova user node and flushes writes/removals.

The Desktop entry point uses `rememberPersistedSharedGameState` with `EncodedSharedGameStore`.

### Web/Wasm

`WebLocalStorageGameTextStore` stores the `SNG1` payload in browser `localStorage` using the common key.

The Web entry point uses the same common restore/autosave helper. Browser storage availability, quota, privacy modes, clearing behavior, and browser-version compatibility still require runtime QA.

### iOS/iPadOS

`AppleUserDefaultsGameTextStore` stores the `SNG1` payload in `NSUserDefaults` using the common key.

The Compose `MainViewController` bridge uses the same common restore/autosave helper. Physical-device lifecycle behavior, app-container backup behavior, signing, and App Store expectations remain external evidence.

## Compatibility and migration policy

`SNG1` is the first shared active-game transport version. Future incompatible formats must use a new version marker rather than silently changing the meaning of existing fields.

When a newer app cannot understand a stored payload, it must fail closed to a fresh game unless an explicit tested migration is implemented. Never reinterpret malformed data or bypass fixed-clue validation simply to make restore succeed.

The Android mature app's existing active-game/DataStore/Room formats are separate contracts and must not be rewritten merely to match `SNG1`.

## Privacy and security boundary

The shared active-game payload is local gameplay state. The common layer does not introduce accounts, cloud sync, analytics, advertising identifiers, or network transport.

Platform storage remains inside the normal application/browser storage boundary. Do not place signing material, credentials, tokens, secrets, or unrelated personal data in `SNG1`.

A local persistence implementation is not encryption. If a future feature requires stronger confidentiality than the platform's normal application storage provides, design and review that requirement explicitly rather than implying that this text format provides cryptographic protection.

## Tests and CI

Common JVM/Desktop tests cover:

- deterministic codec round trips;
- sorted note encoding;
- no-selection/no-notes cases;
- unsupported version/difficulty rejection;
- malformed seed/board/flags rejection;
- selected-index bounds;
- malformed/duplicate/out-of-range note rejection;
- encode-side invalid snapshot rejection;
- oversized payload rejection;
- encoded text-store save/load/clear behavior;
- corrupted stored payload fail-closed behavior;
- `SharedGameState` save/restore/clear wiring.

Cross-Platform CI must continue compiling Android shared integration, Web/Wasm, iOS Simulator framework, and Desktop targets so platform adapter API drift is caught before merge.

## Evidence boundary

Repository tests/builds can prove the shared format and adapters compile and satisfy deterministic unit tests. They cannot prove:

- process-death recovery on every device/OS;
- browser persistence behavior across all intended browsers/privacy modes;
- iOS/iPadOS physical-device lifecycle behavior;
- Desktop clean-machine persistence behavior;
- backup/restore semantics of each host platform;
- store acceptance or publication.

Record those claims only after the corresponding real runtime evidence exists.
