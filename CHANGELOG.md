# Changelog

All notable SudokuNova changes are documented here. The project follows Semantic Versioning where practical and separates **source/repository milestones** from **actual public release evidence**.

## [Unreleased] — v0.1.2 Preparation

SudokuNova v0.1.2 is being prepared on `release/v0.1.2-prep` as a deliberately separate semantic-version line from the repository's later 2.x development history. Source metadata is `versionCode 2016` / `versionName 0.1.2` on this branch. Creating the preparation branch or changing source metadata does not prove public publication, production signing, device QA, store approval, tagging, or distribution.

### Changed

- Android release identity is aligned to:
  - `versionCode 2016`;
  - `versionName 0.1.2`.
- Compose Desktop native package version is aligned to `0.1.2`.
- Ordinary Android CI release verification is aligned to `2016 / 0.1.2`.
- Protected Production Release Validation defaults are aligned to `2016 / 0.1.2`.
- `docs/V0_1_2_RELEASE.md` defines the v0.1.2 preparation contract and evidence boundary.
- `docs/README.md` indexes the v0.1.2 release line and current shared puzzle-exchange documentation.

### Preserved

- Classic 9×9 Sudoku correctness and unique generated-puzzle requirements.
- Existing notes, undo/redo, hints, pause/restart, timer, and mistake handling.
- Mature Android Room/DataStore persistence.
- `SNP1` puzzle-code validation and exchange compatibility.
- `SNG1` active-game persistence compatibility.
- Shared `SNS1` settings validation and local persistence foundation.
- English/Hindi shared-resource parity guards.
- Offline-first/privacy-oriented behavior.
- Cross-platform engine/shared-UI foundation without overstating production readiness.
- Fail-closed signing, secret handling, and release-artifact identity verification.
- Exact-head workflow evidence rules.

### Verification Status

The first v0.1.2 Android CI run on commit `de1bd364ba692734e90466f2e9213d4eb346d251` failed at repository consistency verification. The failure was actionable rather than a product-test failure: the existing documentation index did not link `docs/SHARED_PUZZLE_EXCHANGE.md` and `docs/V2_0_15_RELEASE.md`, and later v0.1.2 work corrected the documentation index. The run also confirmed that security, release-verifier regression, repository-guard regression, translation-parity, and fail-closed partial-signing checks reached their expected successful states before the documentation gate stopped the job.

The failed run must not be treated as release evidence. Fresh workflow runs are required on the final v0.1.2 head after all source, CI, and documentation changes are complete.

### Still Pending / Not Claimed

- Fresh exact-head Android CI, Android Instrumentation, and Cross-Platform CI evidence.
- Release APK/AAB build and identity verification on the final head.
- Protected production signing and certificate-bound validation.
- Physical-device Android QA.
- Google Play approval/publication.
- Apple signing/provisioning/TestFlight/App Store evidence.
- Windows/macOS/Linux production signing, installation, notarization, and distribution evidence.
- Broad browser runtime/accessibility evidence.
- Immutable `v0.1.2` tag and public GitHub Release.

---

## [Unreleased] — 2.0.14 Preparation

SudokuNova 2.0.14 is being prepared on PR #44 from the exact-head verified and merged 2.0.13 `main` checkpoint. Source metadata is now `versionCode 2014` / `versionName 2.0.14`, but public release, signing, device QA, store approval, tagging, and distribution are not inferred from source changes.

### Added

- Portable shared user-settings model:
  - `SharedUserSettings`;
  - `SharedTheme` (`SYSTEM`, `LIGHT`, `DARK`);
  - `SharedInputMode` (`CELL_FIRST`, `NUMBER_FIRST`);
  - validated mistake limits (`0`, `3`, `5`).
- Deterministic bounded `SNS1` settings transport with fail-closed decoding.
- Common settings storage boundaries:
  - `SharedSettingsStore`;
  - `SharedSettingsTextStore`;
  - `EncodedSharedSettingsStore`.
- Observable `SharedSettingsState` and Compose restore/autosave ownership.
- Native local settings adapters:
  - Android staged shared host `SharedPreferences`;
  - Desktop `java.util.prefs.Preferences`;
  - Web `localStorage`;
  - Apple `NSUserDefaults`.
- Localized English/Hindi System/Light/Dark theme controls in the shared Compose surface.
- Shared keyboard parity additions:
  - digits `1` through `9` route through existing value/note entry rules;
  - `N` toggles Notes mode;
  - `H` requests a hint;
  - existing arrows and Backspace/Delete remain supported.
- Shared settings regression suites for codec, encoded store, and observable state behavior.
- `docs/SHARED_SETTINGS.md` as the `SNS1` settings architecture/compatibility authority.
- `docs/V2_0_14_RELEASE.md` as the current source/release preparation authority.
- Issue #43 as the 2.0.14 real-evidence release tracker.

### Changed

- Android source version promoted to:
  - `versionCode 2014`;
  - `versionName 2.0.14`.
- Compose Desktop native package version promoted to `2.0.14`.
- Ordinary Android CI release verification now expects 2014 / 2.0.14.
- Protected Production Release Validation defaults now expect 2014 / 2.0.14.
- `CrossPlatformActivity`, Desktop, Web, and Apple shared hosts now restore/save both active-game state and local shared settings.
- Shared Compose `MaterialTheme` follows persisted System/Light/Dark preference.
- `README.md`, `docs/README.md`, `docs/CROSS_PLATFORM.md`, `docs/KEYBOARD_SHORTCUTS.md`, and release documentation are aligned with the 2.0.14 source/parity boundary.

### Preserved

- Existing `SNP1` checksum/vector and unique-solution import compatibility.
- Classic 9×9 Sudoku correctness and unique generated-puzzle requirements.
- Mature Android Room/DataStore settings/history/product features.
- `SNG1` active-game validation and local adapters.
- English/Hindi shared-resource parity guards.
- Fail-closed signing/secret/release-output guards.
- Exact-head workflow evidence rules.
- Offline-first behavior: the new shared settings path introduces no cloud/account/analytics dependency.

### Still Pending / Not Claimed

- Behavioral parity for every field represented in `SharedUserSettings`.
- Shared history/saved-puzzle persistence.
- Shared imported/custom-puzzle gameplay provenance/persistence model.
- Shared clipboard/share/file-picker adapters.
- Shared challenges and learning/statistics presentation/persistence.
- Real target focus, touch/pointer, resize, large-font, accessibility, lifecycle, settings persistence, and browser E2E evidence.
- Apple production host/signing/provisioning/App Store evidence.
- Windows/macOS/Linux production signing/install/notarization/distribution evidence.
- Android production signing, physical-device QA, store acceptance, final `SHIP`, `v2.0.14` tag, or public release.

### Verification Boundary

PR #44 is merge-ready only after `Android CI`, `Android Instrumentation`, and `Cross-Platform CI` are all green on one exact final head SHA. Older successful runs cannot be mixed with a newer head.

---

## 2.0.13 Repository Preparation — Verified and Merged

PR #41 added deterministic shared grid navigation, focusable arrow/erase keyboard handling, Notes selected semantics, common unique-solution `SNP1` puzzle exchange, Desktop package-version release-contract synchronization, and the 2013 / 2.0.13 source promotion.

Final verified PR head:

```text
61371eab0c22197ffd4250805cf58caa6425418f
```

Exact-head results:

- Android CI #1013 / run `32733975637` — green;
- Android Instrumentation #384 / run `32733975646` — green;
- Cross-Platform CI #118 / run `32733975747` — green.

Merged to `main` as:

```text
adabaf9841eadaa172c8a236b2cbe4a6c4ef6699
```

The merge proved repository preparation only; issue #42 retains real production/manual/store evidence requirements for that source line.

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

That head passed all three required workflow families before merge. Its resulting `main` checkpoint was:

```text
fc95093405c1dc03141e888cc77923fe8f92bcec
```

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
