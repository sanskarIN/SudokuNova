# Shared Cross-Platform User Settings

SudokuNova 2.0.14 introduces a portable local settings contract for the Compose Multiplatform gameplay surface. The goal is to move cross-platform preference ownership into common code without importing Android DataStore or any other platform-specific storage API into `commonMain`.

This document describes the source-backed contract. It does not claim that every mature Android setting already changes behavior on every shared target.

## Architecture

Common code owns:

```text
SharedUserSettings
SharedTheme
SharedInputMode
SharedSettingsCodec
SharedSettingsStore
SharedSettingsTextStore
EncodedSharedSettingsStore
SharedSettingsState
rememberPersistedSharedSettingsState
```

Platform code owns only the local text storage mechanism.

The dependency direction is:

```text
shared UI / settings state
        |
        v
SharedSettingsStore
        |
        v
EncodedSharedSettingsStore
        |
        v
SharedSettingsTextStore
        |
        +--> Android SharedPreferences
        +--> Desktop Preferences
        +--> Web localStorage
        +--> Apple NSUserDefaults
```

Common code therefore remains free of Android `Context`, DataStore, Java preferences, browser DOM storage, and Foundation APIs.

## Portable Settings Model

`SharedUserSettings` currently mirrors the mature Android preference vocabulary where it is safe to define a cross-platform representation:

- theme;
- dynamic-color preference;
- input mode;
- peer highlighting;
- same-number highlighting;
- automatic mistake checking;
- automatic note removal;
- timer visibility;
- haptics preference;
- sound preference;
- reduced-motion preference;
- high-contrast preference;
- mistake limit.

The schema representation does not mean every field is already behaviorally implemented on every target.

### Currently applied by the shared UI

The 2.0.14 shared surface applies:

- System theme;
- Light theme;
- Dark theme.

The shared UI exposes localized English/Hindi theme controls and persists the selection.

### Persisted for progressive parity

Other settings remain in the portable model so later shared features can adopt one compatible local settings record instead of introducing incompatible per-feature formats.

Android-only Material You dynamic color is not falsely generalized to Desktop/Web/Apple targets. Platform-specific feedback capabilities such as haptics also require real native behavior before parity is claimed.

## `SNS1` Text Format

Shared settings use a deterministic versioned text payload beginning with:

```text
SNS1
```

The current ordered fields are:

```text
theme
dynamicColor
inputMode
highlightPeers
highlightSameNumbers
autoCheckMistakes
autoRemoveNotes
showTimer
haptics
sounds
reducedMotion
highContrast
mistakeLimit
```

A default payload is exactly:

```text
SNS1|theme=SYSTEM|dynamicColor=1|inputMode=CELL_FIRST|highlightPeers=1|highlightSameNumbers=1|autoCheckMistakes=1|autoRemoveNotes=1|showTimer=1|haptics=1|sounds=0|reducedMotion=0|highContrast=0|mistakeLimit=3
```

Encoding rules:

- booleans are `1` or `0`;
- enum values use their stable uppercase names;
- mistake limit is one of `0`, `3`, or `5`;
- fields are emitted in deterministic order;
- the payload is bounded to 512 characters.

## Fail-Closed Decoding

The decoder rejects malformed state instead of silently guessing.

Rejected input includes:

- empty payloads;
- unsupported version prefixes;
- wrong field counts;
- malformed key/value fields;
- unknown keys;
- duplicate fields;
- missing fields;
- booleans other than `0`/`1`;
- unknown theme or input-mode values;
- unsupported mistake limits;
- oversized payloads.

A storage read/decode failure must not make gameplay unusable. Compose restore ownership catches storage failures and leaves safe in-memory defaults active.

## Storage Key

The current key is:

```text
sudokunova.shared.settings.v1
```

Changing the key or format requires an explicit migration/compatibility decision.

## Platform Adapters

### Android staged shared host

`CrossPlatformSharedPreferencesSettingsTextStore` uses an application-private `SharedPreferences` file:

```text
sudokunova_shared_settings
```

The mature Android application's existing DataStore settings remain authoritative for the mature Android product surface. This staged adapter belongs to the shared cross-platform host and does not silently replace or migrate mature Android settings.

### Desktop

`DesktopPreferencesSettingsTextStore` uses:

```text
Preferences.userRoot().node("in/sanskar/sudokunova")
```

The exact backing location is OS/JRE dependent.

### Web

`WebLocalStorageSettingsTextStore` stores the `SNS1` payload in browser `localStorage`.

Browser storage availability, quotas, clearing behavior, private-browsing behavior, and user/site-data controls vary. Repository compilation is not runtime persistence evidence for every browser mode.

### iOS/iPadOS

`AppleUserDefaultsSettingsTextStore` uses `NSUserDefaults.standardUserDefaults`.

Framework compilation does not prove physical-device lifecycle, backup, restore, signing, or App Store behavior.

## Compose Restore and Autosave

`rememberPersistedSharedSettingsState`:

1. creates default shared settings;
2. attempts one restore from the supplied store;
3. leaves defaults active if restore fails;
4. marks restoration complete;
5. autosaves later observable settings changes.

This mirrors the resilience rule used by shared active-game persistence: storage is important, but a broken preference record must not break the Sudoku surface.

The Android staged shared host uses lifecycle-owned restore/save because it already owns a `ComponentActivity` lifecycle.

## Privacy and Security Boundary

The shared settings implementation is local-first and introduces no network dependency.

It does not add:

- account synchronization;
- remote analytics;
- advertising identifiers;
- cloud preference backup controlled by SudokuNova common code;
- telemetry upload.

Platform or OS backup behavior may still exist outside this common contract and must be evaluated separately for distribution/privacy documentation.

`SNS1` is a validation/compatibility format, not encryption. It must not be used to store passwords, signing keys, access tokens, or other secrets.

## Tests

Common regression coverage includes:

- deterministic default encoding;
- non-default round trip;
- version rejection;
- malformed boolean rejection;
- unknown enum rejection;
- invalid mistake-limit rejection;
- missing/extra/oversized payload rejection;
- encoded-store save/load/clear;
- corrupt stored payload rejection;
- state update/replace/restore/save/clear behavior.

Hosted CI also compiles the native adapters through the Android/Desktop/Web/iOS target build paths.

## Compatibility Policy

`SNS1` is now a source-controlled compatibility surface.

When changing settings persistence:

1. do not reinterpret existing `SNS1` fields with incompatible meaning;
2. preserve deterministic encoding while the version remains `SNS1`;
3. add a new version/migration path for incompatible schema changes;
4. bound all untrusted stored input before parsing;
5. add regression vectors before changing the codec;
6. keep storage adapters minimal;
7. update this guide and `docs/CROSS_PLATFORM.md`;
8. require exact-head CI before merge.

## Evidence Boundary

Source/tests/host compilation prove the repository contract only.

Before claiming production settings parity on a target, verify real runtime behavior including:

- restart persistence;
- storage clearing/reset expectations;
- OS/browser privacy modes;
- theme application;
- lifecycle/background behavior;
- target accessibility;
- target-specific settings whose behavior has actually been implemented.

Do not infer production parity from a successful Kotlin compilation or framework/package build.
