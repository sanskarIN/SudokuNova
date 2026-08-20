# Changelog

All notable SudokuNova changes are documented here. The project follows Semantic Versioning where practical and separates **source/repository milestones** from **actual public release evidence**.

## [Unreleased]

### Cross-Platform Foundation on the Verified 2.0.12 Baseline

The current development line extends SudokuNova beyond the mature Android application without weakening or replacing the verified Android 2.0.12 source/release contract.

#### Added

- Kotlin Multiplatform targets for the `sudoku-engine` domain module:
  - Android;
  - Desktop JVM;
  - iOS arm64;
  - iOS Simulator arm64;
  - Web/Wasm.
- New `sharedUI` Compose Multiplatform module.
- Portable `SharedGameState` with:
  - generated puzzles;
  - seven difficulty levels;
  - fixed/editable cells;
  - number entry;
  - candidate notes;
  - peer-note cleanup;
  - conflict feedback;
  - erase;
  - bounded undo;
  - engine-backed hints;
  - reset/new game.
- Responsive shared Sudoku UI using Compose Multiplatform/Material 3.
- Desktop Compose application entry point.
- iOS/iPadOS `ComposeUIViewController` bridge and static `SudokuNovaSharedUI.framework` configuration.
- SwiftUI host sources under `iosApp/`.
- Web/Wasm entry point, production host page, and responsive browser shell.
- Non-exported Android `CrossPlatformActivity` that hosts the shared UI while preserving `MainActivity` as the mature production launcher.
- Portable gameplay-state regression tests.
- Dedicated `.github/workflows/cross-platform.yml` hosted matrix covering:
  - shared tests/compile;
  - Android shared integration;
  - Web production distribution;
  - iOS Simulator framework linking on macOS;
  - Desktop application images on Linux, Windows, and macOS.
- Cross-platform documentation authority: `docs/CROSS_PLATFORM.md`.
- iOS host integration guide under `iosApp/README.md`.
- Documentation-coverage ownership/rules/tests for `sharedUI/` and `iosApp/`.

#### Changed

- `sudoku-engine` migrated from JVM-only configuration to Kotlin Multiplatform while retaining its platform-independent implementation/test source trees.
- Root Gradle/version-catalog configuration now registers KMP and Compose Multiplatform plugins/dependencies while retaining:
  - Android Gradle Plugin 9.3.1;
  - Kotlin 2.4.10;
  - Android compile/target SDK 37;
  - JDK/JVM 17.
- Android `:app` now consumes `:sharedUI` in addition to `:sudoku-engine`.
- Standard Android CI now runs shared engine tests, shared gameplay-state tests, and shared Desktop/Web compilation before the existing Android/release gates.
- `README.md`, `docs/README.md`, `ARCHITECTURE.md`, `PROJECT_STRUCTURE.md`, `BUILDING.md`, `TESTING.md`, `CI_CD.md`, and `REPOSITORY_FILE_REFERENCE.md` were aligned with the real multiplatform build graph and evidence boundaries.
- Documentation continues to distinguish repository build support from production signing/notarization/store/device/browser evidence.

#### Preserved

The mature Android production surface remains intact. Cross-platform extraction does **not** remove or silently downgrade Android-only capabilities such as Room/DataStore persistence, history/saved puzzles, backup/transfer, full navigation, learning/statistics surfaces, localization, Android accessibility integration, Macrobenchmark tooling, or the 2.0.12 release verification pipeline.

#### Verification Boundary

This work is not considered merge-verified until `Android CI`, `Android Instrumentation`, and every `Cross-Platform CI` job are green on the same exact final PR head SHA.

Repository compilation/framework/package generation does not by itself prove:

- Apple signing/provisioning or App Store acceptance;
- macOS signing/notarization;
- Windows signing/reputation;
- Linux distribution-channel compatibility;
- broad browser/device compatibility;
- physical-device accessibility/lifecycle/performance quality;
- public store/release publication.

### 2.0.12 Android Repository/Release Baseline — Verified and Merged

PR #30 consolidated the final Android 2.0.12 source/version/documentation/release-hardening line and merged to `main` at:

```text
5fdafd77332b4889c5bd64bd23b1c4869ade0962
```

The final PR #30 head was:

```text
f097f7fb58a4d9b5e4f87b998b01e9d082ac9f29
```

That exact head passed the required Android CI and API-35 Android Instrumentation gates before merge.

The source contract is:

```text
applicationId = in.sanskar.sudokunova
versionCode   = 2012
versionName   = 2.0.12
minSdk        = 26
targetSdk     = 37
compileSdk    = 37
```

#### Added/Strengthened

- fail-closed tracked-file documentation ownership and detailed-guide indexing;
- embedded APK application/version/minSdk/targetSdk/debuggable identity inspection;
- deterministic APK identity evidence;
- release-verifier CLI-boundary regression tests;
- mandatory signed-release APK v2-or-newer signature-scheme enforcement;
- `docs/V2_0_12_RELEASE.md` as the current Android release authority;
- protected production validation defaults synchronized to 2012/2.0.12;
- release-contract synchronization across source, ordinary CI, and protected release validation.

#### Evidence Boundary

The verified repository baseline does not itself prove a published 2.0.12 store release. Production signing, physical-device/manual QA, representative performance evidence, repository administration, Play Console validation, final SHIP decision, tagging, GitHub Release creation, and public distribution remain separate real-evidence requirements.

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

Major repository-side additions included certificate-bound signed-release verification, protected release validation, Macrobenchmark infrastructure, repository consistency guards, exact release-identity checks, and performance-evidence documentation.

These runs are historical evidence only and do not prove newer heads.

### v1.0 RC1 Repository Preparation

PR #27 final head:

```text
7016e21f36c8ecb8a495c446ffd8b57e9f20a4ea
```

Exact-head evidence:

- Android CI run #635 / ID `32151771317` — green;
- API-35 instrumentation run #188 / ID `32151771297` — green;
- merge commit `2329881aff8dabaf8d040918e16b6113e3900245`.

This historical line introduced the release-output verifier, optional secret-backed fail-closed signing, release evidence/checksums, Play Store preparation, GitHub settings guidance, and stable-release documentation foundations.

### v0.9.0 Release Hardening — 2026-08-18

PR #25 final head:

```text
7bc5d095cfdde17dc92250581e3bc28a6fbc54c9
```

Exact-head evidence:

- Android CI run `32139568718` — green;
- API-35 instrumentation run `32139568591` — green;
- merge commit `18944dc56757e1c1c9d51939cb0cafa72e4b5ee2`.

Major work included repository security checks, bounded backup/file validation, accessibility and large-text hardening, off-main-thread solver/hint work, Room/DataStore integrity review, localization cleanup, release R8/AAB verification, repository metadata/policies, and comprehensive documentation.

## Earlier Product Milestones

Earlier milestones established the current feature-rich Android product foundation, including:

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

Detailed milestone history is preserved in the repository's historical documentation, merged pull requests, and:

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
