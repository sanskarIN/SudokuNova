# SudokuNova 2.0.15 Release Preparation

SudokuNova 2.0.15 is the next patch line after the exact-head verified and merged 2.0.14 settings/keyboard parity checkpoint.

## Source Baseline

The 2.0.15 preparation branch starts from the merged 2.0.14 source line.

Active preparation branch:

```text
feature/v2.0.15-shared-puzzle-exchange
```

## Intended 2.0.15 Source Contract

Before final exact-head verification, the repository source line is promoted together to:

```text
applicationId  = in.sanskar.sudokunova
versionCode    = 2015
versionName    = 2.0.15
packageVersion = 2.0.15
minSdk         = 26
targetSdk      = 37
compileSdk     = 37
JDK/JVM        = 17
```

The Android version, ordinary CI expectations, protected production-validation defaults, and Desktop package version must remain synchronized by the release-contract guard.

## 2.0.15 Product / Parity Scope

The 2.0.15 parity slice makes validated puzzle-code exchange a real shared-session feature instead of an engine-only capability.

### Imported puzzle provenance

Common gameplay state now owns:

- validated SNP1 import through `PuzzleExchangeService`;
- canonical puzzle-code retention for imported sessions;
- imported-session detection and typed import success/failure status;
- exact imported puzzle/solution/difficulty reconstruction without generating a replacement puzzle;
- exported code for the currently active starting puzzle.

Imported sessions use a zero seed only as an implementation detail; the retained SNP1 source code is authoritative for restoration.

### Active-game persistence upgrade

`SNG1` remains fully supported for generated sessions. `SNG2` adds the original validated SNP1 source code for imported sessions so restore can reconstruct the same puzzle across shared hosts.

The codec remains deterministic, bounded, and fail-closed. Existing SNG1 snapshots are not rewritten unnecessarily.

### Shared UI exchange surface

The common Compose UI now exposes:

- an SNP1 puzzle-code input field;
- a shared import action;
- selectable current-puzzle code output for copy/paste;
- localized English/Hindi exchange labels and status messages;
- explicit invalid-code feedback without mutating the active game.

The first slice intentionally uses platform-neutral text selection rather than pretending a clipboard API is already portable. Native clipboard/share/file-picker adapters remain a follow-up parity milestone.

## Regression Coverage

The 2.0.15 test suite covers:

- imported puzzle assessment retention;
- SNP1 export compatibility;
- malformed/ambiguous puzzle-code rejection;
- SNG1 backward-compatible snapshot decoding;
- deterministic SNG2 imported-session round trips;
- malformed notes/source-code rejection;
- oversized snapshot rejection;
- imported session load/export/restore behavior;
- invalid import fail-closed behavior;
- fixed-clue and note integrity during imported-session restore;
- English/Hindi translation key/placeholder parity through the existing repository guard.

## Exact-Head Merge Gate

This preparation line must not merge based on earlier 2.0.14 workflow evidence.

The exact final 2.0.15 preparation head must independently pass on one SHA:

- Android CI;
- Android Instrumentation;
- Cross-Platform CI.

The required scope includes repository security/documentation/release guards, English/Hindi parity, engine/shared gameplay tests, Android tests/lint/builds/R8/AAB identity checks, Web/Wasm distribution, iOS Simulator framework linking, and Desktop application-image builds on Linux, Windows, and macOS.

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
- `SNG1` active-game compatibility/validation;
- `SNG2` imported-session provenance validation;
- `SNS1` settings validation;
- mature Android Room/DataStore integrity;
- English/Hindi parity;
- accessibility semantics;
- offline-first/privacy boundaries;
- fail-closed signing and secret handling;
- release R8/artifact identity verification;
- documentation ownership/discoverability;
- exact-head CI evidence rules.
