# SudokuNova Project Structure

This document maps the repository so contributors can place changes in the correct layer. For the complete tracked-file ownership contract, use [`REPOSITORY_FILE_REFERENCE.md`](REPOSITORY_FILE_REFERENCE.md) and run `python scripts/verify_documentation_coverage.py --verbose`. For platform-specific build commands and support boundaries, use [`CROSS_PLATFORM.md`](CROSS_PLATFORM.md).

## Top-Level Layout

```text
SudokuNova/
├── .github/                 # CI, cross-platform CI, instrumentation, release validation, community config
├── app/                     # Mature Android production application
├── iosApp/                  # SwiftUI host sources for the shared iOS/iPadOS framework
├── macrobenchmark/          # Android release-like performance benchmark test module
├── sharedUI/                # Compose Multiplatform gameplay state/UI and platform entry points
├── sudoku-engine/           # Kotlin Multiplatform Sudoku domain/solver/generator engine
├── docs/                    # Product, engineering, platform, QA, and release documentation
├── gradle/                  # Version catalog and Gradle wrapper metadata
├── scripts/                 # Repository/release verification scripts and Python regression tests
├── build.gradle.kts         # Root plugin declarations
├── settings.gradle.kts      # Modules and repository configuration
├── gradle.properties        # Gradle/Kotlin/Android build properties
├── README.md                # Project landing page
├── CHANGELOG.md             # Release history and current unreleased work
├── ROADMAP.md               # Product/milestone roadmap
├── SECURITY.md              # Vulnerability reporting/security policy
├── CONTRIBUTING.md          # Top-level contribution policy
├── THIRD_PARTY_NOTICES.md   # Third-party licensing notices
└── what_changed.md          # Evidence-oriented implementation history
```

Git is the authoritative complete file inventory. `scripts/verify_documentation_coverage.py` fails closed if any tracked path falls outside the maintained ownership taxonomy.

## Gradle Module Graph

SudokuNova currently has four Gradle modules:

```text
:sudoku-engine   Kotlin Multiplatform domain engine
      ↑
:sharedUI        Compose Multiplatform gameplay UI/state
      ↑
:app             Android application (also directly consumes :sudoku-engine)

:macrobenchmark  Android test module targeting :app's benchmark variant
```

The non-Gradle `iosApp/` directory contains SwiftUI host sources for the generated `SudokuNovaSharedUI.framework`.

The architecture intentionally keeps the mature Android application intact while extracting reusable behavior into KMP. Cross-platform work should increase shared capability without deleting established Android-only features merely to make platform labels look symmetrical.

## `sudoku-engine/`

The engine is Kotlin Multiplatform and remains UI/platform independent. Existing implementation sources are mapped into `commonMain`, and existing engine tests are mapped into `commonTest`.

Current targets:

- Android KMP library target, minSdk 26 / compileSdk 37;
- Desktop JVM target using JVM 17;
- iOS arm64;
- iOS Simulator arm64;
- Web/Wasm browser target.

Primary production package:

```text
sudoku-engine/src/main/kotlin/com/sanskar/sudokunova/engine/
```

Important files include:

- `SudokuBoard.kt` — immutable board model, parsing, serialization, validation, candidates, conflicts;
- `SudokuSolver.kt` — MRV-style recursive solver, solution counting, search metrics;
- `SudokuGenerator.kt` — deterministic seeded generation with uniqueness preservation;
- `Difficulty.kt` / `DifficultyCalibrator.kt` — supported difficulty definitions and calibration;
- `LogicalDifficultyAnalyzer.kt` / `LogicalSolver.kt` — logical/complexity analysis and solving;
- `HintEngine.kt` — teaching-hint selection plus explicit Reveal fallback;
- `TeachingStep.kt` / `TeachingStepFinder.kt` — structured technique evidence and deterministic technique search;
- `TeachingPractice.kt` — deterministic offline practice catalog;
- `PuzzleCodeCodec.kt` — versioned `SNP1` puzzle-code format.

Engine tests remain under:

```text
sudoku-engine/src/test/kotlin/com/sanskar/sudokunova/engine/
```

The engine must not depend on Compose, Android resources, `Context`, Room, DataStore, Android lifecycle types, UIKit, browser DOM APIs, or Desktop window APIs.

## `sharedUI/`

`sharedUI` owns the portable gameplay surface implemented with Compose Multiplatform.

### Common source

```text
sharedUI/src/commonMain/kotlin/com/sanskar/sudokunova/shared/
```

Key files:

- `SharedGameState.kt` — generated puzzle lifecycle, difficulty, selection, notes, peer-note cleanup, placement, conflict feedback, bounded undo, hint, reset, and new-game state;
- `SudokuNovaSharedApp.kt` — responsive Material 3 Sudoku board, difficulty picker, number pad, actions, status, and shared branding.

Common regression tests:

```text
sharedUI/src/commonTest/kotlin/com/sanskar/sudokunova/shared/
```

`SharedGameStateTest.kt` protects fixed clues, notes, entry, undo/reset consistency, and hint progression.

### Desktop source

```text
sharedUI/src/desktopMain/kotlin/com/sanskar/sudokunova/shared/desktop/Main.kt
```

This is the Compose Desktop window entry point. The module declares native distribution formats for Windows MSI, macOS DMG, and Linux DEB; packages must be built on the corresponding host OS.

### iOS/iPadOS source

```text
sharedUI/src/iosMain/kotlin/com/sanskar/sudokunova/shared/ios/MainViewController.kt
```

This exposes `ComposeUIViewController` through the generated static `SudokuNovaSharedUI.framework` for Swift/SwiftUI hosting.

### Web/Wasm source

```text
sharedUI/src/wasmJsMain/kotlin/com/sanskar/sudokunova/shared/web/Main.kt
sharedUI/src/wasmJsMain/resources/index.html
sharedUI/src/wasmJsMain/resources/styles.css
```

The browser target mounts Compose into `#webApp` and builds a production Wasm distribution.

## `iosApp/`

`iosApp/` contains native Apple host sources, not a claim of completed Apple distribution.

```text
iosApp/
├── SudokuNovaApp.swift      # SwiftUI @main entry point
├── ContentView.swift        # UIViewControllerRepresentable bridge
└── README.md                # Framework build/Xcode/signing boundary
```

A distributable iOS/iPadOS application still requires a real Xcode target/project, bundle identity, assets, capabilities, signing/provisioning, device QA, and App Store validation. Those external requirements are documented rather than fabricated.

## `app/`

The Android application remains SudokuNova's mature primary production surface. It consumes both `:sudoku-engine` and `:sharedUI` while retaining Android-specific capabilities.

Android-owned concerns include:

- mature Compose screens/navigation;
- lifecycle/ViewModels;
- English/Hindi Android resources;
- Room persistence/history/saved puzzles;
- Preferences DataStore;
- Android sharing/document picker and transfer flows;
- accessibility presentation;
- Android release/output/signing configuration;
- Android unit/instrumentation test infrastructure;
- release-like benchmark target integration.

### Android entry points

```text
app/src/main/java/com/sanskar/sudokunova/MainActivity.kt
app/src/main/java/com/sanskar/sudokunova/CrossPlatformActivity.kt
```

`MainActivity` remains the exported launcher. `CrossPlatformActivity` is non-exported and hosts the same portable Compose UI used by Desktop/iOS/Web. This lets maintainers validate shared Android integration without prematurely replacing the feature-richer launcher.

### Android production source

```text
app/src/main/java/com/sanskar/sudokunova/
```

Broad areas include:

- `data/` — DataStore, Room, challenge, history, saved-puzzle, backup/transfer persistence;
- `game/` — active Android game state and serialization/restoration support;
- `ui/` — screens, ViewModels, presentation helpers, theme/design code.

Player-facing Android prose should stay in localized resources rather than being pushed into the shared engine.

### Android resources

```text
app/src/main/res/
```

Important categories include default/English values, Hindi values, launcher/splash/vector assets, themes, and Android backup/data-extraction XML. English/Hindi parity is checked by `scripts/verify_translations.py`.

### Android JVM tests

```text
app/src/test/java/com/sanskar/sudokunova/
```

Use JUnit4 unless the app test configuration is intentionally migrated.

### Android instrumentation tests

```text
app/src/androidTest/java/com/sanskar/sudokunova/
```

Connected tests cover Compose navigation and Room-backed flows on the API-35 emulator gate. Stable semantic test tags are preferred where duplicate/off-screen text would make tests brittle.

### Benchmark-only app overlay

```text
app/src/benchmark/AndroidManifest.xml
```

The `benchmark` build type derives from `release`, keeps R8/resource shrinking and non-debuggable behavior, and uses debug signing so local/CI measurement setup does not require production signing material. Profiling declarations remain benchmark-only.

## `macrobenchmark/`

The Macrobenchmark module is a separate `com.android.test` module targeting the release-like `:app` benchmark variant.

Important files:

```text
macrobenchmark/build.gradle.kts
macrobenchmark/src/main/AndroidManifest.xml
macrobenchmark/src/main/kotlin/com/sanskar/sudokunova/macrobenchmark/StartupBenchmark.kt
```

Responsibilities include explicit targeting of `in.sanskar.sudokunova`, cold/warm startup, frame timing, controlled compilation state, and ordinary-CI compile verification. Representative physical-device performance remains separate release evidence; see [`PERFORMANCE_BENCHMARKING.md`](PERFORMANCE_BENCHMARKING.md).

## Room Schemas

```text
app/schemas/
```

Room schema export is part of the persistence contract. Keep schema history reviewable and do not use destructive migration fallback as a shortcut when user data should be preserved.

## `docs/`

Documentation is part of the product. `docs/README.md` is the complete categorized index.

High-value current authorities include:

- `CROSS_PLATFORM.md` — platform architecture/build matrix and evidence boundaries;
- `ARCHITECTURE.md` / `PROJECT_STRUCTURE.md` — system/module placement;
- `REPOSITORY_FILE_REFERENCE.md` — tracked-path ownership;
- `SUDOKU_ENGINE.md` — engine/domain behavior;
- `BUILDING.md` / `TESTING.md` / `CI_CD.md` — build and verification;
- `REPOSITORY_GUARDS.md` / `QUALITY_GATES.md` — deterministic quality enforcement;
- `V2_0_12_RELEASE.md` — current Android 2.0.12 release authority;
- signing/release/store/evidence documentation.

## `.github/workflows/`

Current quality/release workflows include:

- `ci.yml` — repository/release guards, KMP engine tests, shared-state tests, shared Desktop/Wasm compilation, Android JVM/instrumentation compilation, Macrobenchmark compile, lint, debug build, R8 release APK, AAB, package/version/SDK verification, and evidence upload;
- `cross-platform.yml` — hosted shared tests plus Android integration, Web distribution, iOS Simulator framework, and Desktop application-image builds on Linux/Windows/macOS;
- `instrumentation.yml` — API-35 connected Compose/Room verification;
- `release-validation.yml` — protected/manual production-signed Android release validation.

Do not weaken gates simply to make a PR green. Fix the underlying defect or document a justified workflow change. Production signing secrets must never enter ordinary pull-request workflows.

## `scripts/`

Repository verification scripts include:

- `verify_translations.py` — English/Hindi key/format parity;
- `verify_no_secrets.py` — committed signing/private-key/credential guard;
- `verify_documentation_links.py` — repository-local Markdown target/boundary integrity;
- `verify_documentation_coverage.py` — fail-closed `git ls-files` documentation ownership/index check;
- `verify_release_contract.py` — source/ordinary-CI/protected-workflow release identity synchronization;
- `verify_release_outputs.py` — APK/AAB/R8 structure, embedded identity, hashes, and optional signed/certificate verification.

Regression tests under `scripts/tests/` protect these guards. Blocking guards should remain deterministic and include both acceptance and rejection tests.

Useful audit commands:

```bash
python -m unittest scripts.tests.test_verify_documentation_links
python -m unittest scripts.tests.test_verify_documentation_coverage
python -m unittest scripts.tests.test_verify_release_contract
python -m unittest scripts.tests.test_verify_release_outputs
python scripts/verify_documentation_links.py
python scripts/verify_documentation_coverage.py
python scripts/verify_release_contract.py
python scripts/verify_no_secrets.py
python scripts/verify_translations.py
```

## Root Documentation Files

- `README.md` — concise project landing page/current capabilities;
- `CHANGELOG.md` — release-oriented change history;
- `ROADMAP.md` — milestone status;
- `what_changed.md` — detailed implementation/verification evidence;
- `SECURITY.md` — authoritative vulnerability-reporting policy;
- `CONTRIBUTING.md` — contributor entry point;
- `THIRD_PARTY_NOTICES.md` — direct dependency/tooling notice summary.

Other policy/community files remain covered by the repository ownership taxonomy.

## Complete Tracked-File Ownership Invariant

Run:

```bash
python scripts/verify_documentation_coverage.py --verbose
```

Every current `git ls-files` path must resolve to a canonical documentation area. Any unmatched new path fails CI. For a Markdown report:

```bash
python scripts/verify_documentation_coverage.py --markdown
```

See [`REPOSITORY_FILE_REFERENCE.md`](REPOSITORY_FILE_REFERENCE.md) and [`REPOSITORY_GUARDS.md`](REPOSITORY_GUARDS.md).

## Change Placement Rules

Prefer the narrowest correct layer:

- Sudoku truth/validation/solver/generator/teaching evidence → `sudoku-engine` common code;
- portable gameplay state/presentation → `sharedUI/commonMain`;
- Desktop window/distribution integration → `sharedUI/desktopMain`;
- Apple Compose bridge → `sharedUI/iosMain`; Swift host integration → `iosApp/`;
- browser/Wasm integration → `sharedUI/wasmJsMain`;
- Android production persistence/navigation/platform services → `app/`;
- player-facing Android strings → Android resources;
- release-like Android startup/frame measurement → `macrobenchmark` plus benchmark app variant;
- signing/package/certificate verification → release workflows/scripts/docs, never committed secrets;
- repository consistency/documentation ownership → `scripts/` plus canonical docs;
- cross-cutting policy/process → root or `docs/`.

## New Feature Checklist

Before adding a feature family, identify:

1. common engine impact;
2. shared UI/state impact;
3. platform-specific service/host impact;
4. Android persistence/migration impact;
5. ViewModel/state changes;
6. localization requirements;
7. accessibility semantics;
8. common/JVM tests;
9. connected/native/browser tests when needed;
10. performance implications;
11. privacy/security implications;
12. backup compatibility implications;
13. signing/store/distribution implications;
14. documentation updates;
15. tracked-file documentation ownership for every new path;
16. changelog/roadmap impact.

Run both documentation guards after adding or moving files:

```bash
python scripts/verify_documentation_coverage.py
python scripts/verify_documentation_links.py
```

A feature should not land as presentation-only code without considering correctness, persistence, accessibility, localization, performance, security, release support, platform boundaries, and documentation ownership.
