# SudokuNova v0.1.2 Release Preparation

SudokuNova v0.1.2 is being prepared as a deliberately separate semantic-version line from the repository's later 2.x development history. This branch is a release-preparation checkpoint; creating it or changing source metadata does not prove that v0.1.2 has been publicly shipped.

## Source Baseline

Preparation branch:

```text
release/v0.1.2-prep
```

The branch starts from the current `main` checkpoint and keeps the repository's modern Sudoku engine, persistence, shared UI, localization, and cross-platform foundation intact.

## Intended Android Source Contract

```text
applicationId  = in.sanskar.sudokunova
versionCode    = 2016
versionName    = 0.1.2
minSdk         = 26
targetSdk      = 37
compileSdk     = 37
JDK/JVM        = 17
```

`versionCode` remains monotonically increasing for Android packaging even though the displayed semantic `versionName` is `0.1.2`. It must not be lowered merely to mirror the semantic version number.

## v0.1.2 Scope

The release should be limited to stabilization and release-contract work rather than introducing an unrelated large feature set.

### Included baseline

- Classic 9x9 Sudoku gameplay, solving, and unique puzzle-generation requirements.
- Existing notes, undo/redo, hints, pause/restart, timer, and mistake handling.
- Existing Room/DataStore-backed Android persistence.
- Existing `SNP1` puzzle-code validation and exchange compatibility.
- Existing `SNG1` active-game persistence compatibility.
- Existing shared `SNS1` settings validation and local persistence foundation.
- English/Hindi localization and shared-resource parity guards.
- Offline-first behavior with no new cloud/account/analytics dependency.
- Existing cross-platform engine/shared-UI foundation, without overstating production readiness for any platform.
- Release signing and artifact checks that fail closed when required evidence or secrets are missing.

## v0.1.2 Stabilization Goals

Before publication, the release line should verify:

1. Android version identity is exactly `0.1.2` with monotonically increasing `versionCode`.
2. Existing puzzle-code, active-game, and settings compatibility remains intact.
3. Release R8/minification and artifact identity checks pass.
4. Android unit/instrumentation tests pass on the exact final commit.
5. Cross-platform CI passes on the exact final commit where applicable.
6. English/Hindi resources remain complete and placeholder-compatible.
7. No release artifact contains development signing credentials or accidentally packaged secrets.
8. Changelog, README/release documentation, and source metadata agree on the intended release line.

## Evidence Boundary

The repository and hosted CI do not by themselves prove:

- production signing identity ownership or acceptance;
- physical-device Android QA;
- Google Play approval or publication;
- Apple signing, provisioning, TestFlight, or App Store acceptance;
- Windows signing/reputation or clean-machine MSI behavior;
- macOS signing/notarization/Gatekeeper behavior;
- Linux clean-environment installation/upgrade behavior;
- broad browser runtime/accessibility evidence;
- a public GitHub Release or immutable published tag.

These remain unchecked until real evidence exists.

## Release Artifacts

Only upload artifacts that were actually built and verified from the exact release commit. Candidate Android assets are:

```text
SudokuNova-v0.1.2.apk
SudokuNova-v0.1.2.apk.sha256
SudokuNova-v0.1.2.aab
SudokuNova-v0.1.2.aab.sha256
```

Do not create placeholder files merely to populate a GitHub Release.

## GitHub Release Contract

Recommended release metadata:

```text
Tag:     v0.1.2
Target:  release/v0.1.2-prep final commit (or the final release commit after merge)
Title:   SudokuNova v0.1.2 — Stabilization & Release Readiness
Prerelease: No, only after all publication gates are satisfied
```

The tag and GitHub Release must be created only after the final exact-head verification and required manual/device/store evidence are complete.

## Known Limitations

This release-preparation document does not claim complete feature parity across every platform. In particular, shared history/saved-puzzle persistence, native clipboard/share/file-picker adapters, challenges/learning/statistics presentation, broad browser E2E coverage, and production distribution evidence may remain outside the v0.1.2 scope unless separately verified.

## Invariants

Do not weaken or bypass:

- classic Sudoku correctness and uniqueness;
- `SNP1` compatibility and validation;
- `SNG1` active-game validation;
- `SNS1` settings validation;
- mature Android Room/DataStore integrity;
- English/Hindi parity;
- accessibility semantics;
- offline-first/privacy boundaries;
- fail-closed signing and secret handling;
- release artifact identity verification;
- exact-head CI evidence rules.
