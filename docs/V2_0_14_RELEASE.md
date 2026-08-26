# SudokuNova 2.0.14 Release Preparation

SudokuNova 2.0.14 is the next patch line after the exact-head verified and merged 2.0.13 repository baseline.

## Source Baseline

The 2.0.14 preparation branch starts from the verified 2.0.13 merge commit:

```text
adabaf9841eadaa172c8a236b2cbe4a6c4ef6699
```

Active preparation branch:

```text
release/v2.0.14-prep
```

## Intended 2.0.14 Source Contract

Before final exact-head verification, the repository source line is promoted together to:

```text
applicationId  = in.sanskar.sudokunova
versionCode    = 2014
versionName    = 2.0.14
packageVersion = 2.0.14
minSdk         = 26
targetSdk      = 37
compileSdk     = 37
JDK/JVM        = 17
```

The Android version, ordinary CI expectations, protected production-validation defaults, and Desktop package version must remain synchronized by the release-contract guard.

## 2.0.14 Product / Parity Scope

The first 2.0.14 parity slice adds a portable local user-settings foundation without importing Android DataStore into common code.

### Shared settings contract

Common code owns:

- `SharedUserSettings` with portable equivalents of mature Android preferences;
- `SharedTheme` and `SharedInputMode` enums;
- allowed mistake-limit validation (`0`, `3`, or `5`);
- versioned deterministic `SNS1` text encoding;
- bounded fail-closed decoding;
- `SharedSettingsStore` and `SharedSettingsTextStore` storage boundaries;
- `EncodedSharedSettingsStore` composition;
- observable `SharedSettingsState`;
- Compose restore/autosave ownership.

### Native local adapters

The staged shared hosts use local platform storage:

- Android: private `SharedPreferences`;
- Desktop: `java.util.prefs.Preferences`;
- Web: browser `localStorage`;
- iOS/iPadOS: `NSUserDefaults`.

No network synchronization, advertising identifier, analytics identifier, or cloud account is introduced by this settings work.

### Shared theme behavior

The common Compose surface exposes localized System / Light / Dark theme controls. The selected theme is immediately applied and persisted through the shared settings contract.

The portable `dynamicColor` field is retained for schema/parity compatibility, but common targets do not pretend Android-only Material You dynamic color behavior exists everywhere.

### Keyboard parity

The shared focusable Sudoku grid extends the 2.0.13 arrow/erase baseline with:

- digit keys `1` through `9` for value/note entry through existing state rules;
- `N` for Notes mode;
- `H` for Hint;
- existing arrow navigation;
- existing Backspace/Delete erase.

Visible controls remain available; keyboard shortcuts are an enhancement rather than an exclusive interaction path.

## Regression Coverage

The shared test suite covers:

- exact deterministic default `SNS1` encoding;
- non-default settings round trips;
- unsupported version rejection;
- malformed boolean rejection;
- unknown enum rejection;
- invalid mistake-limit rejection;
- missing/extra/oversized payload rejection;
- encoded settings store save/load/clear;
- corrupt stored payload fail-closed behavior;
- observable settings state update/replace/restore/save/clear.

## Exact-Head Merge Gate

This preparation line must not merge based on older 2.0.13 workflow evidence.

The exact final 2.0.14 preparation head must independently pass on one SHA:

- Android CI;
- Android Instrumentation;
- Cross-Platform CI.

The required scope includes repository security/documentation/release guards, English/Hindi parity, shared engine/settings/game tests, Android tests/lint/builds/R8/AAB identity checks, Web/Wasm distribution, iOS Simulator framework linking, and Desktop application-image builds on Linux, Windows, and macOS.

Any later commit invalidates earlier exact-head evidence and requires fresh final runs.

## Evidence Boundary

Repository source, tests, framework linking, package generation, and hosted CI do not prove:

- protected production signing identities or signed artifact acceptance;
- physical-device Android lifecycle/accessibility/performance QA;
- real Apple app signing/provisioning/TestFlight/App Store acceptance;
- Windows signing/reputation or clean-machine MSI behavior;
- macOS signing/notarization/Gatekeeper behavior;
- Linux clean-environment install/upgrade support;
- broad browser/privacy-mode/runtime accessibility behavior;
- store approval;
- final `SHIP` decision;
- immutable tag, GitHub Release, or public distribution.

Those claims require real external evidence and must remain unchecked until performed.

## Invariants

Do not weaken or bypass:

- Classic 9×9 Sudoku correctness and uniqueness;
- `SNP1` puzzle-code compatibility/validation;
- `SNG1` active-game validation;
- `SNS1` settings validation;
- mature Android Room/DataStore integrity;
- English/Hindi parity;
- accessibility semantics;
- offline-first/privacy boundaries;
- fail-closed signing and secret handling;
- release R8/artifact identity verification;
- documentation ownership/discoverability;
- exact-head CI evidence rules.
