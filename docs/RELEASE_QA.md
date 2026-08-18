# SudokuNova Release QA Matrix

This document separates automated evidence from manual release validation. A checkbox must remain unchecked until that exact activity has actually been performed against a named build/artifact. Do not infer a device result from compilation, emulator CI, or source review.

## Evidence Rules

For every manual QA session record:

- commit SHA;
- application version;
- artifact filename and checksum if available;
- device/emulator model;
- Android version / API level;
- screen size/orientation when relevant;
- system font scale when relevant;
- accessibility service/settings when relevant;
- pass/fail result;
- date;
- defect/issue link for any failure.

Automated CI results may satisfy automated rows only. They do not automatically satisfy TalkBack, visual, ergonomics, thermal, battery, store-console, or physical-device rows.

## Automated Release Gates

These are required on the exact final clean pull-request head.

- [ ] English/Hindi translation parity passes.
- [ ] Release/security hygiene verification passes.
- [ ] Dependency hygiene verification passes.
- [ ] `:sudoku-engine:test` passes.
- [ ] `:app:testDebugUnitTest` passes.
- [ ] `:app:assembleDebugAndroidTest` passes.
- [ ] `:app:lintDebug` passes.
- [ ] `:app:assembleDebug` passes.
- [ ] `:app:lintRelease` passes.
- [ ] `:app:assembleRelease` passes with R8/resource shrinking.
- [ ] `:app:bundleRelease` passes.
- [ ] release APK/AAB/R8 mapping output verification passes.
- [ ] API-35 connected instrumentation passes.
- [ ] Room 1→2 migration regression test passes on connected instrumentation.
- [ ] teaching-evidence accessibility semantics regression test passes on connected instrumentation.

## Required Device/API Coverage

### Minimum supported Android

- [ ] API 26 device/emulator launches the release candidate.
- [ ] Home, Classic game, Settings, Learn, History/Saved, Custom Puzzle, and About are reachable.
- [ ] active game persists across app stop/relaunch.
- [ ] document picker transfer flow behaves correctly on this API.

### Connected CI reference target

- [ ] API 35 connected workflow is green on the final clean head.

### Current/modern Android target

- [ ] A modern Android device/emulator compatible with target SDK 37 launches and completes core flows.
- [ ] system back behavior is correct across dialogs/navigation.
- [ ] no unexpected permission prompts appear.

### Large screen

- [ ] tablet/large-window layout is usable in portrait.
- [ ] tablet/large-window layout is usable in landscape.
- [ ] game board and controls remain reachable without overlap.
- [ ] Learn/practice cards remain readable.

## Installation / Upgrade / Data Preservation

- [ ] clean install succeeds.
- [ ] upgrade from the previous published/dev v0.8 data state succeeds without destructive reset.
- [ ] existing History rows remain readable after upgrade.
- [ ] existing Saved Puzzles remain readable after upgrade.
- [ ] challenge records remain readable after upgrade.
- [ ] DataStore settings remain intact after upgrade.
- [ ] active-game restore either succeeds or safely rejects corrupted/unsupported state without crashing.
- [ ] app uninstall removes local application data as expected.

## Classic Gameplay

Test at least one fresh puzzle at each supported difficulty:

- [ ] Beginner
- [ ] Easy
- [ ] Medium
- [ ] Hard
- [ ] Expert
- [ ] Master
- [ ] Extreme

For representative games verify:

- [ ] unique valid puzzle is presented.
- [ ] cell selection works.
- [ ] Cell-first input works.
- [ ] Number-first input works.
- [ ] notes entry/removal works.
- [ ] automatic note cleanup works when enabled.
- [ ] erase works only where allowed.
- [ ] undo/redo works across value and note actions.
- [ ] pause/resume works.
- [ ] timer visibility preference works.
- [ ] restart works.
- [ ] configured mistake limit works.
- [ ] completion state is recorded once.
- [ ] failed game is not double-counted.
- [ ] replay does not inflate ordinary completion statistics.

## Hardware Keyboard / Chromebook-Like Input

- [ ] arrow keys move selection correctly.
- [ ] number keys 1–9 enter/select values according to input mode.
- [ ] erase/backspace behavior is correct.
- [ ] notes shortcut works.
- [ ] hint shortcut works.
- [ ] visible focus/selection remains understandable.

## Hints / Teaching Evidence

- [ ] Naked Single direct hint is understandable.
- [ ] Hidden Single direct hint is understandable.
- [ ] advanced elimination-chain hint identifies its hardest enabling technique.
- [ ] source cells are visually distinguishable.
- [ ] target cells are visually distinguishable.
- [ ] final placement target is visually distinguishable.
- [ ] candidate-removal explanation matches the highlighted evidence.
- [ ] applying a hint changes only the supported final value, not unrelated notes/cells.
- [ ] Reveal is visibly described as the stronger fallback rather than a fake logical technique.

## Learn / Practice

- [ ] Learning Progress card loads.
- [ ] all nine technique lesson cards are reachable.
- [ ] Study Technique opens localized content.
- [ ] practice opens for each technique.
- [ ] correct answer records one success.
- [ ] incorrect answer records one attempt without a success.
- [ ] repeated tap after answering does not record duplicate attempts.
- [ ] Next Practice works.
- [ ] learning progress survives relaunch.
- [ ] learning reset clears only learning counters.
- [ ] game statistics/history remain after learning reset.

## Daily / Weekly Challenges

- [ ] Daily Challenge opens deterministic daily identity.
- [ ] Weekly Challenge opens deterministic weekly identity.
- [ ] archive entries are reachable.
- [ ] first-completion result is persisted correctly.
- [ ] challenge replay/provenance remains correct after restore.
- [ ] Daily and Weekly keys cannot collide into the same identity path.

## Custom Puzzle

- [ ] valid unique puzzle validates.
- [ ] contradictory puzzle is rejected.
- [ ] unsolvable puzzle is rejected.
- [ ] multiple-solution puzzle is rejected.
- [ ] solved preview does not overwrite original clues.
- [ ] custom puzzle can be saved.
- [ ] saved custom puzzle can be replayed.

## History / Saved Puzzles / Statistics

- [ ] completed game appears in History once.
- [ ] Favorite state can be promoted/preserved.
- [ ] Saved Puzzle can be opened/replayed.
- [ ] difficulty summaries match stored data.
- [ ] statistics reset is scoped and does not erase unrelated saved/history data.
- [ ] best-time logic is correct.
- [ ] streak update behavior is correct for same-day and consecutive-day completions.

## Puzzle Code / Share / Backup / Restore

### Puzzle code

- [ ] valid puzzle code round-trips.
- [ ] invalid version is rejected.
- [ ] invalid checksum is rejected.
- [ ] oversized input is rejected.
- [ ] malformed field/bounds input is rejected.
- [ ] imported puzzle must still pass Sudoku solvability/uniqueness checks.

### Clipboard / share sheet

- [ ] copy operation occurs only after explicit user action.
- [ ] share sheet opens only after explicit user action.
- [ ] result share text contains intended data only.

### Document export/import

- [ ] backup export creates a document through Android system UI.
- [ ] exported backup can be imported.
- [ ] import size bound is enforced.
- [ ] malformed backup fails safely without partial destructive restore.
- [ ] duplicate restore does not duplicate records.
- [ ] Favorite state is not demoted during duplicate-safe restore.
- [ ] replay provenance survives restore.
- [ ] file I/O does not visibly freeze the UI.

## Offline / Privacy / Permissions

- [ ] core play works with network connectivity disabled.
- [ ] Learn/practice works with network connectivity disabled.
- [ ] no login/account prompt appears.
- [ ] no advertising appears from the open-source base app.
- [ ] no analytics consent/prompt appears because no analytics SDK is configured.
- [ ] Android app-info permissions page shows no requested sensitive permissions.
- [ ] app does not make ordinary cloud-backup data available under configured backup rules.
- [ ] device-to-device migration behavior matches configured Android backup rules when actually tested.

## Accessibility — TalkBack

Perform with TalkBack enabled; record device/API.

- [ ] Home entry points have meaningful labels and focus order.
- [ ] Sudoku cells announce row, column, value/empty state.
- [ ] original clue state is distinguishable.
- [ ] conflict state is announced.
- [ ] teaching source is announced while hint is open.
- [ ] teaching target is announced.
- [ ] candidate elimination target announces exact candidates.
- [ ] final hint placement announces target/value.
- [ ] number pad actions are understandable.
- [ ] game action controls are understandable.
- [ ] dialogs move focus appropriately.
- [ ] Learn lesson/practice controls are understandable.
- [ ] Settings controls expose current state.
- [ ] Backup & Transfer actions are understandable.

## Accessibility — Font / Contrast / Motion

### Font scale

Test at normal and approximately 200% system font scale where supported.

- [ ] Home remains navigable.
- [ ] game metadata/controls remain reachable.
- [ ] Settings remains scrollable without clipped actions.
- [ ] Learn cards/dialogs remain readable.
- [ ] transfer dialogs remain usable.

### High Contrast

- [ ] selected cell is distinguishable.
- [ ] peer/same-number states remain distinguishable.
- [ ] conflict/error state remains distinguishable.
- [ ] source/target/placement teaching evidence remains distinguishable.
- [ ] notes remain legible.
- [ ] dark and light themes remain usable.

### Reduced Motion

- [ ] no essential meaning depends on animation.
- [ ] reduced-motion preference does not block an interaction.

## Orientation / Window / Lifecycle

- [ ] rotate during an active game without losing state.
- [ ] background/foreground during an active game without timer/state corruption.
- [ ] process recreation restores supported active state.
- [ ] corrupted saved state fails closed without crash.
- [ ] rotate/open/close hint dialog without losing valid board state.
- [ ] rotate during Learn/practice without duplicate progress recording.
- [ ] multi-window/resize does not hide essential controls on supported devices.

## Performance / Stability

- [ ] new puzzle generation remains responsive on representative physical hardware.
- [ ] Extreme generation does not hang the UI.
- [ ] hint generation does not visibly freeze the UI.
- [ ] History/Saved lists remain responsive with a realistic local record set.
- [ ] backup export/import remains responsive with a maximum-size valid payload.
- [ ] no ANR is observed during the manual suite.
- [ ] no crash is observed during the manual suite.
- [ ] memory usage does not show obvious unbounded growth across repeated game/replay/navigation cycles.

## Release Artifact Validation

Automated CI currently produces unsigned verification artifacts. For a real distribution candidate, separately verify the signed artifact.

- [ ] exact final commit SHA recorded.
- [ ] release APK built from exact final commit.
- [ ] release AAB built from exact final commit.
- [ ] R8 mapping file preserved.
- [ ] production signing material stayed outside source control.
- [ ] signed APK signature verified.
- [ ] signed APK installs and launches on intended test device(s).
- [ ] signed AAB accepted by the intended store/internal-test validation path.
- [ ] versionCode/versionName are correct.
- [ ] final binary/application ID is `in.sanskar.sudokunova`.

## Documentation / Store Readiness

- [ ] README matches implemented behavior.
- [ ] CHANGELOG matches implemented behavior.
- [ ] ROADMAP does not claim unverified work complete.
- [ ] `what_changed.md` records exact final CI/device evidence.
- [ ] Privacy policy matches current data behavior.
- [ ] Security policy matches current supported line and threat model.
- [ ] Third-party notices match direct dependency families.
- [ ] build/release instructions were executed from a clean checkout.
- [ ] screenshots/store assets represent the actual current UI.
- [ ] no debug-only branding, application ID, logs, or test fixtures appear in production assets.

## QA Session Record Template

Copy this section for each manual session.

```text
Date:
Tester:
Commit SHA:
Version:
Artifact filename:
Artifact SHA-256:
Device/emulator:
Android version / API:
Screen/window:
Font scale:
TalkBack / accessibility settings:
Network state:
Result: PASS / FAIL / PARTIAL
Issues found:
Notes:
```

## Current Status

The presence of this checklist does **not** mean the unchecked manual rows have been executed. GitHub Actions workflow evidence is recorded separately in `what_changed.md` only after the exact final head actually passes.
