# What Changed

## Current Development State — SudokuNova 2.0.13 Preparation — 2026-08-24

**Repository:** `https://github.com/sanskarIN/SudokuNova`  
**Base branch:** `main`  
**Current verified main checkpoint before 2.0.13 work:** `fc95093405c1dc03141e888cc77923fe8f92bcec`  
**Active branch:** `release/v2.0.13-prep`  
**Active pull request:** `#41`  
**Head immediately before this ledger update:** `9ffd48c6ab9445d66fddf24277ce27eeb4dcccda`  
**Current source release target:** `2.0.13`  
**Android versionCode:** `2013`  
**Android versionName:** `2.0.13`  
**Desktop package version:** `2.0.13`  
**Android application ID:** `in.sanskar.sudokunova`  
**minSdk:** `26`  
**targetSdk / compileSdk:** `37`  
**JDK/JVM:** `17`  
**Android Gradle Plugin:** `9.3.1`  
**Kotlin:** `2.4.10`  
**Compose Multiplatform:** `1.11.1`  
**Room:** `2.8.4`  
**License:** MIT

This is the active implementation/handoff ledger. Historical 2.0.12 work remains preserved in Git history, merged pull requests, `docs/V2_0_12_RELEASE.md`, and the repository archive. Exact-head workflow evidence applies only to the commit it tested; final 2.0.13 merge verification must use the final PR #41 head after the last source/documentation commit.

---

## Verified Work Merged Before 2.0.13

### 2.0.12 cross-platform foundation — PR #33

PR #33 was merged only after all required workflow families were green on exact final head:

```text
514ff1a1b79dce0ee75c9c20af7211af51362649
```

Exact-head evidence:

- Android CI #919 / run `32627989504` — SUCCESS;
- Android Instrumentation #335 / run `32627989511` — SUCCESS;
- Cross-Platform CI #70 / run `32627989561` — SUCCESS.

That line established:

- Kotlin Multiplatform `sudoku-engine`;
- Compose Multiplatform `sharedUI`;
- Android shared host while preserving mature `MainActivity`;
- Desktop JVM, iOS/iPadOS framework, SwiftUI host sources, and Web/Wasm targets;
- shared gameplay state/UI;
- English/Hindi shared resources and parity checks;
- shared Sudoku-cell semantics and non-color conflict feedback;
- hosted multi-OS cross-platform CI.

### Cross-platform active-game persistence — PR #39

PR #39 was merged only after exact final head:

```text
2a83189356640cbd8ef6f88e7fbf76bbb2fcb845
```

passed:

- Android CI #969 / run `32684886751` — SUCCESS;
- Android Instrumentation #358 / run `32684886794` — SUCCESS;
- Cross-Platform CI #90 / run `32684886753` — SUCCESS.

It added:

- `SharedGameSnapshot`;
- deterministic bounded `SNG1` encoding;
- fail-closed snapshot decoding/restore validation;
- `SharedGameStore` and `SharedGameTextStore` boundaries;
- `EncodedSharedGameStore`;
- common restore/autosave ownership;
- staged Android `SharedPreferences` adapter;
- Desktop `Preferences` adapter;
- Web `localStorage` adapter;
- Apple `NSUserDefaults` adapter;
- persistence regression tests and `docs/SHARED_PERSISTENCE.md`.

The mature Android Room/DataStore model remained intact.

### Room 2.8.4 maintenance — PR #40

PR #40 recreated the Room update on the verified post-persistence `main` rather than merging stale-base Dependabot PR #35.

Verified head:

```text
56183604d8693f7b5997c399030a9916cb56e567
```

Exact-head evidence:

- Android CI #976 / run `32685637068` — SUCCESS;
- Android Instrumentation #361 / run `32685637074` — SUCCESS;
- Cross-Platform CI #94 / run `32685637057` — SUCCESS.

Merged `main` checkpoint used for 2.0.13:

```text
fc95093405c1dc03141e888cc77923fe8f92bcec
```

The stale Dependabot PR #35 was then closed as superseded.

---

## 2.0.13 Branch and Pull Request

A clean branch was created from verified `main`:

```text
release/v2.0.13-prep
```

Draft PR:

```text
#41 — release: prepare SudokuNova 2.0.13 parity hardening
```

The release line remains draft until source/documentation hardening is complete and one exact final head passes every required workflow family.

---

## Shared Keyboard and Focus Baseline

`SharedGameState` now owns deterministic one-cell grid movement through `moveSelection(rowDelta, columnDelta)`.

Rules:

- row/column deltas are limited to `-1..1`;
- zero movement is rejected;
- when no cell is selected, the first movement establishes index `0` deterministically;
- movement clamps at all four board edges;
- movement always resolves to a valid `0..80` cell index;
- selection status continues through the existing typed `SharedGameStatus` model.

Regression coverage verifies:

- initial keyboard-style selection;
- horizontal and vertical movement;
- top/left edge clamping;
- bottom/right edge clamping;
- rejection of zero and multi-cell movement requests.

The shared Compose Sudoku grid is now explicitly focusable and handles key-down events for:

- Up/Down/Left/Right → shared deterministic selection movement;
- Backspace/Delete → the existing clue-protected shared erase action.

Direct shared digit-key, Notes-shortcut, and Hint-shortcut parity is **not** claimed yet. Visible controls remain available.

---

## Shared Accessibility Action Semantics

Shared cells already expose localized row/column/value-or-notes/fixed-or-editable/selected/conflict descriptions and selected semantics.

2.0.13 extends action-state semantics by marking the portable Notes action selected when Notes mode is enabled. The existing visible check mark is retained, so Notes mode is not communicated by color alone.

Real TalkBack, VoiceOver, desktop accessibility API, browser accessibility-tree, keyboard/focus traversal, pointer/touch, large-font, and resize behavior still require target runtime evidence.

---

## Common Puzzle-Code Exchange Boundary

Added:

```text
sudoku-engine/src/main/kotlin/com/sanskar/sudokunova/engine/PuzzleExchangeService.kt
```

`PuzzleExchangeService` separates transport decoding from playable-import acceptance.

Export:

- delegates to the established `PuzzleCodeCodec`;
- retains the existing `SNP1` wire format and checksum behavior.

Import:

1. decodes/validates `SNP1` through `PuzzleCodeCodec`;
2. runs `SudokuSolver.analyze(..., solutionLimit = 2)`;
3. rejects malformed, unsolvable, or multiple-solution inputs;
4. returns `ImportedPuzzle` only when exactly one solution is proven;
5. returns the original puzzle, unique solution, and encoded difficulty as structured data.

Added:

```text
sudoku-engine/src/test/kotlin/com/sanskar/sudokunova/engine/PuzzleExchangeServiceTest.kt
```

Coverage verifies:

- successful unique-puzzle import;
- returned unique solution/difficulty;
- exact established `SNP1` compatibility vector;
- ambiguous empty-board rejection;
- malformed-code rejection.

Important boundary: `SNG1` active-game persistence is generated-game/seed based. An imported/custom puzzle must not be persisted by inventing an unrelated generator seed. Shared custom/imported gameplay sessions therefore still need explicit provenance/state design before that parity item can be called complete.

---

## 2.0.13 Version Promotion

Android source metadata is now:

```text
applicationId = in.sanskar.sudokunova
versionCode   = 2013
versionName   = 2.0.13
minSdk        = 26
targetSdk     = 37
compileSdk    = 37
```

Desktop packaging now declares:

```text
packageName    = SudokuNova
packageVersion = 2.0.13
```

Ordinary Android CI release-output verification now expects:

```text
--expected-version-code 2013
--expected-version-name 2.0.13
```

Protected Production Release Validation defaults now also use:

```text
expected_version_code = 2013
expected_version_name = 2.0.13
```

No production signing or publication is inferred from these source changes.

---

## Release Contract Hardened for Desktop

The existing release-contract verifier previously synchronized:

- Android `app/build.gradle.kts`;
- ordinary Android CI expected identity;
- protected production-validation defaults.

2.0.13 extends it to parse `sharedUI/build.gradle.kts` and require:

```text
Desktop packageVersion == Android versionName
```

The guard now fails when the Desktop package is missing, duplicated, malformed, or version-drifted from the Android source release identity.

Regression tests cover:

- valid Desktop package-version parsing;
- duplicate Desktop version rejection;
- matching Android/Desktop/CI/protected contracts;
- Desktop mismatch included in drift diagnostics.

This prevents a future version promotion from accidentally leaving a stale Desktop installer/application version.

---

## 2.0.13 Documentation Authority

Added:

```text
docs/V2_0_13_RELEASE.md
```

It is now the current release-version authority and documents:

- 2013 / 2.0.13 source identity;
- Desktop 2.0.13 package identity;
- repository-verifiable 2.0.13 scope;
- exact-head three-workflow merge rule;
- ordinary and protected release expectations;
- cross-platform evidence boundaries;
- manual/device/signing/store requirements that source control cannot prove.

`docs/V2_0_12_RELEASE.md` is retained unchanged as historical 2.0.12 authority.

Updated documentation includes:

- `docs/README.md` — indexes 2.0.13 as current and 2.0.12 as historical;
- `docs/SUDOKU_ENGINE.md` — documents `PuzzleExchangeService` and the distinction between codec validity and uniqueness acceptance;
- `docs/KEYBOARD_SHORTCUTS.md` — separates mature Android keyboard coverage from the shared 2.0.13 arrow/erase baseline;
- `docs/CROSS_PLATFORM.md` — updates version identity, feature-parity matrix, puzzle exchange, keyboard/focus, Room 2.8.4, build commands, and evidence boundary.

---

## Atomic Commit Structure for This Continuation

The 2.0.13 line intentionally uses focused commits. Current commit families include:

### Maintenance integration

- merge verified Room 2.8.4 PR #40;
- close stale Dependabot PR #35 as superseded.

### Shared input/accessibility

- `feat(shared): add deterministic grid navigation`;
- `test(shared): cover keyboard-style grid navigation`;
- `feat(shared): add portable keyboard grid controls`.

### Puzzle exchange

- `feat(engine): add validated puzzle exchange service`;
- `test(engine): verify puzzle exchange uniqueness gate`.

### Version/release identity

- `release(android): promote app metadata to 2.0.13`;
- `release(desktop): promote package version to 2.0.13`;
- `ci: verify Android 2.0.13 release identity`;
- `release(ci): promote protected validation defaults to 2.0.13`.

### Release/documentation

- `docs(release): add 2.0.13 release authority`;
- `docs: index the 2.0.13 release line`;
- `docs(engine): document validated puzzle exchange boundary`;
- `docs(input): document shared keyboard navigation`;
- `docs(platform): align parity matrix with 2.0.13`.

### Release guard

- `build: guard desktop package version parity`;
- `test: cover desktop release version guard`;
- this active-ledger update.

---

## Exact-Head Verification Status

During this continuation, each source/documentation commit creates a newer PR #41 head. Workflow runs attached to earlier heads become historical and must not be combined into final evidence.

Required final PR #41 evidence on one SHA:

1. `Android CI` — success;
2. `Android Instrumentation` — success;
3. `Cross-Platform CI` — success, including shared code/tests, Android shared integration, Web/Wasm distribution, iOS Simulator framework, and Desktop application images on Linux/Windows/macOS.

No final 2.0.13 green claim is recorded in this section yet because the branch is still being edited. After the final documentation/changelog/PR metadata commit, freeze the branch and use only workflows attached to that final SHA.

If a final-head workflow exposes a real source defect, fix that defect and repeat the entire exact-head evidence requirement on the new SHA.

---

## Remaining Repository Work After Current 2.0.13 Scope

Issue #34 remains open. Important remaining work includes:

- explicit shared custom/imported gameplay-session provenance and persistence;
- platform-safe clipboard/share/file-picker abstractions where justified;
- shared challenges/custom-puzzle presentation/domain integration;
- shared learning/statistics presentation and persistence;
- portable settings/theme breadth and local settings adapters;
- direct shared digit/Notes/Hint keyboard shortcut parity;
- reliable target runtime input/focus/resize/browser smoke tests;
- expensive puzzle generation/hint/import analysis lifecycle/cancellation ownership where measurement shows it is needed;
- real iOS/iPadOS production host/project/assets/signing/device validation;
- clean-machine Windows/macOS/Linux packaging/runtime validation;
- Web browser-support matrix and runtime accessibility/persistence verification.

Do not convert these into completed checkboxes merely because adjacent source abstractions exist.

---

## External Evidence Not Claimed

This repository continuation does **not** claim completion of:

- Android production keystore/certificate validation unless the protected workflow actually succeeds;
- final signed installation/upgrade QA on representative Android devices;
- final TalkBack/VoiceOver/desktop/browser accessibility QA;
- representative physical-device startup/frame/memory/ANR evidence;
- process-death/lifecycle recovery on each target;
- representative Hindi layout/font-scale runtime evidence;
- Apple Xcode production target/signing/provisioning/TestFlight/App Store acceptance;
- macOS signing/notarization/Gatekeeper validation;
- Windows signing/reputation/clean-machine installation evidence;
- Linux clean install/upgrade/remove evidence;
- intended Web browser/device compatibility/privacy-mode evidence;
- Play Console or other store acceptance;
- final `SHIP` decision;
- immutable `v2.0.13` tag;
- GitHub Release or public store/Web distribution.

Those require real-world evidence and must remain explicit release tasks.

---

## Current Authorities

Current release authority:

```text
docs/V2_0_13_RELEASE.md
```

Historical 2.0.12 authority:

```text
docs/V2_0_12_RELEASE.md
```

Cross-platform authority:

```text
docs/CROSS_PLATFORM.md
```

Shared persistence authority:

```text
docs/SHARED_PERSISTENCE.md
```

Detailed history through 2026-08-19 remains archived at:

```text
docs/archive/what_changed_through_2026-08-19.md
```

The previous active 2.0.12 cross-platform ledger remains recoverable from Git history before this update.

---

## Current Handoff Rule

PR #41 is the active 2.0.13 preparation line from verified `main` checkpoint `fc950934...`.

Do not merge PR #41 based on conflict-free status, intermediate green runs, or older 2.0.12 evidence. Complete the remaining repository synchronization, freeze one final head, require all three workflow families on that exact SHA, repair any concrete failure without weakening gates, and only then consider merging the repository-side 2.0.13 preparation.

Even a fully green merge does not by itself prove production signing, manual/device QA, store acceptance, tagging, or publication.

**SudokuNova — Think. Solve. Master the Grid.**  
**Made by the Sanskar**
