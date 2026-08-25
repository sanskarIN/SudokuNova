# Changelog

All notable SudokuNova changes are documented here. The project follows Semantic Versioning where practical and separates **source/repository milestones** from **actual public release evidence**.

## [Unreleased] — 2.0.13 Preparation

SudokuNova 2.0.13 is being prepared on PR #41 from the verified post-persistence/Room `main` checkpoint. Source metadata is now `versionCode 2013` / `versionName 2.0.13`, but public release, signing, device QA, store approval, tagging, and distribution are not inferred from source changes.

### Added

- Deterministic shared Sudoku-grid navigation in `SharedGameState`:
  - one-cell row/column movement;
  - deterministic initial selection;
  - board-edge clamping;
  - invalid movement rejection.
- Portable shared Compose keyboard/focus baseline:
  - focusable Sudoku grid;
  - Up/Down/Left/Right navigation;
  - Backspace/Delete erase through existing clue-protected state logic.
- Shared Notes action selected-state semantics in addition to the existing visible mode indicator.
- Common `PuzzleExchangeService` around `SNP1` puzzle codes.
- Unique-solution import acceptance in common engine code; syntactically valid but ambiguous puzzle codes are rejected.
- `PuzzleExchangeServiceTest` compatibility/uniqueness regression coverage.
- `docs/V2_0_13_RELEASE.md` as the current release-version authority.
- Desktop package-version synchronization in `scripts/verify_release_contract.py` and its regression suite.

### Changed

- Android source version promoted to:
  - `versionCode 2013`;
  - `versionName 2.0.13`.
- Compose Desktop native package version promoted to `2.0.13`.
- Ordinary Android CI release verification now expects 2013 / 2.0.13.
- Protected Production Release Validation defaults now expect 2013 / 2.0.13.
- Release-contract verification now fails when Desktop `packageVersion` differs from Android `versionName`.
- `README.md`, `docs/README.md`, `docs/CROSS_PLATFORM.md`, `docs/SUDOKU_ENGINE.md`, `docs/KEYBOARD_SHORTCUTS.md`, and `what_changed.md` are aligned with the 2.0.13 source/parity boundary.

### Preserved

- Existing `SNP1` checksum/vector compatibility.
- Classic 9×9 Sudoku correctness and unique generated-puzzle requirements.
- Mature Android Room/DataStore state and product features.
- English/Hindi shared-resource parity guards.
- `SNG1` active-game validation and local adapters.
- Fail-closed signing/secret/release-output guards.
- Exact-head workflow evidence rules.

### Still Pending / Not Claimed

- Shared imported/custom-puzzle gameplay provenance/persistence model.
- Shared clipboard/share/file-picker adapters.
- Shared challenges, learning/statistics, and full settings parity.
- Direct shared digit/Notes/Hint keyboard shortcuts.
- Real target focus, touch/pointer, resize, large-font, accessibility, lifecycle, and browser E2E evidence.
- Apple production host/signing/provisioning/App Store evidence.
- Windows/macOS/Linux production signing/install/notarization/distribution evidence.
- Android production signing, physical-device QA, store acceptance, final `SHIP`, `v2.0.13` tag, or public release.

### Verification Boundary

PR #41 is merge-ready only after `Android CI`, `Android Instrumentation`, and `Cross-Platform CI` are all green on one exact final head SHA. Older successful runs cannot be mixed with a newer head.

---

## Recently Merged Cross-Platform Foundations

### Shared Active-Game Persistence — PR #39

PR #39 added the versioned `SNG1` active-game transport and common/native persistence boundary.

Major additions:

- `SharedGameSnapshot` and fail-closed restore validation;
- deterministic bounded `SharedGameSnapshotCodec`;
- `SharedGameStore` / `SharedGameTextStore` interfaces;
- encoded-store composition;
- Compose restore/autosave ownership;
- staged Android shared-host `SharedPreferences` adapter;
- Desktop `Preferences` adapter;
- Web `localStorage` adapter;
- Apple `NSUserDefaults` adapter;
- persistence regression tests;
- `docs/SHARED_PERSISTENCE.md`.

Exact final head:

```text
2a83189356640cbd8ef6f88e7fbf76bbb2fcb845
```

That head passed Android CI, Android Instrumentation, and Cross-Platform CI before merge.

### Room 2.8.4 Maintenance — PR #40

Room runtime/ktx/compiler/testing were updated to 2.8.4 on the verified post-persistence base rather than merging a stale-base dependency branch.

Exact final head:

```text
56183604d8693f7b5997c399030a9916cb56e567
```

That head passed all three required workflow families before merge. The resulting `main` checkpoint used to start 2.0.13 is:

```text
fc95093405c1dc03141e888cc77923fe8f92bcec
```

The older Dependabot PR #35 was closed as superseded.

### Cross-Platform Foundation — PR #33

The 2.0.12 cross-platform foundation introduced:

- Kotlin Multiplatform `sudoku-engine` targets for Android, Desktop JVM, iOS/iOS Simulator, and Web/Wasm;
- Compose Multiplatform `sharedUI`;
- responsive portable gameplay state/UI;
- Desktop launcher/package path;
- iOS/iPadOS framework + SwiftUI host sources;
- Web/Wasm launcher/distribution shell;
- non-exported Android shared host;
- English/Hindi shared resource parity;
- shared cell semantics and non-color conflict evidence;
- hosted multi-OS Cross-Platform CI;
- fail-closed documentation ownership for shared/platform files.

Exact final head:

```text
514ff1a1b79dce0ee75c9c20af7211af51362649
```

Exact-head results:

- Android CI #919 / `32627989504` — green;
- Android Instrumentation #335 / `32627989511` — green;
- Cross-Platform CI #70 / `32627989561` — green.

Repository build support did not and does not imply signing, notarization, store, physical-device, or broad browser evidence.

---

## 2.0.12 Android Repository/Release Baseline — Verified and Merged

PR #30 consolidated the Android 2.0.12 source/version/documentation/release-hardening line and merged to `main` at:

```text
5fdafd77332b4889c5bd64bd23b1c4869ade0962
```

Final PR #30 head:

```text
f097f7fb58a4d9b5e4f87b998b01e9d082ac9f29
```

The exact head passed required Android CI and API-35 Android Instrumentation before merge.

Historical source contract:

```text
applicationId = in.sanskar.sudokunova
versionCode   = 2012
versionName   = 2.0.12
minSdk        = 26
targetSdk     = 37
compileSdk    = 37
```

Major release-engineering work included fail-closed tracked-file documentation ownership, embedded APK identity inspection, release verifier regression/CLI checks, certificate-bound signed validation paths, synchronized source/workflow release contracts, and `docs/V2_0_12_RELEASE.md`.

The repository baseline did not itself prove a publicly shipped 2.0.12 binary.

---

## Historical Release-Engineering Foundations

### v1.0 Post-RC Validation Hardening

PR #28 final head:

```text
c3e0e3fc217062e374a434cfea46235fd6595f83
```

Exact-head evidence:

- Android CI run #706 / ID `32211246803` — green;
- API-35 instrumentation run #229 / ID `32211246802` — green;
- merge commit `27640cb9089ddae4a9242bb84a8927c3761201f4`.

Major additions included certificate-bound signed-release verification, protected release validation, Macrobenchmark infrastructure, repository consistency guards, exact release-identity checks, and performance-evidence documentation.

### v1.0 RC1 Repository Preparation

PR #27 final head:

```text
7016e21f36c8ecb8a495c446ffd8b57e9f20a4ea
```

Exact-head evidence:

- Android CI run #635 / ID `32151771317` — green;
- API-35 instrumentation run #188 / ID `32151771297` — green;
- merge commit `2329881aff8dabaf8d040918e16b6113e3900245`.

This line introduced release-output verification, optional fail-closed secret-backed signing, release evidence/checksums, Play Store preparation, GitHub settings guidance, and stable-release documentation foundations.

### v0.9.0 Release Hardening — 2026-08-18

PR #25 final head:

```text
7bc5d095cfdde17dc92250581e3bc28a6fbc54c9
```

Exact-head evidence:

- Android CI run `32139568718` — green;
- API-35 instrumentation run `32139568591` — green;
- merge commit `18944dc56757e1c1c9d51939cb0cafa72e4b5ee2`.

Major work included bounded backup/file validation, accessibility and large-text hardening, off-main-thread solver/hint work, Room/DataStore integrity review, localization cleanup, R8/AAB verification, repository metadata/policies, and comprehensive documentation.

## Earlier Product Milestones

Earlier milestones established the feature-rich Android product foundation, including:

- Classic Sudoku board/solver/generator;
- seven difficulty levels;
- gameplay state, notes, undo/redo, hints, pause/restart, timer, mistakes;
- challenges/custom puzzles;
- history/saved data/statistics/achievements;
- learning center and structured logical techniques;
- puzzle-code/import/export/backup flows;
- English/Hindi localization;
- accessibility semantics and adaptive layouts;
- Room/DataStore local persistence;
- offline-first/privacy-oriented behavior.

Detailed history is preserved in merged PRs and:

- `docs/archive/what_changed_through_2026-08-19.md`;
- `docs/V09_HARDENING_AUDIT.md`;
- `docs/V1_RELEASE_PREP.md`;
- `docs/V1_RELEASE_CANDIDATE.md`;
- `docs/V1_RELEASE_EVIDENCE.md`;
- `docs/POST_RC_VALIDATION_EVIDENCE.md`;
- `what_changed.md`.

## Changelog Rules

- Record notable product, architecture, release, compatibility, security, and platform changes.
- Do not list every atomic commit.
- Keep planned work clearly separate from implemented work.
- Do not mark a source/repository milestone as a public release without real publication evidence.
- Workflow evidence applies only to the exact commit SHA tested.
- Use `what_changed.md` for the detailed active implementation/verification ledger.
