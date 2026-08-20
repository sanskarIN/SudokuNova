# SudokuNova Project Structure

This document maps the repository so contributors can find the correct layer before making a change. For the complete tracked-file ownership contract, including every current path family and its canonical documentation, use [`REPOSITORY_FILE_REFERENCE.md`](REPOSITORY_FILE_REFERENCE.md) and run `python scripts/verify_documentation_coverage.py --verbose`.

## Top-Level Layout

```text
SudokuNova/
├── .github/                 # CI, instrumentation, protected release validation and community configuration
├── app/                     # Android application module
├── macrobenchmark/          # Release-like Android performance benchmark test module
├── docs/                    # Product, engineering, QA and release documentation
├── gradle/                  # Version catalog and Gradle wrapper metadata
├── scripts/                 # Repository/release verification scripts and their Python tests
├── sudoku-engine/           # Platform-independent Classic Sudoku domain module
├── build.gradle.kts         # Root Gradle plugin declarations
├── settings.gradle.kts      # Module and repository configuration
├── gradle.properties        # Gradle/Kotlin/Android build properties
├── README.md                # Project landing page
├── CHANGELOG.md             # Release history and current unreleased work
├── ROADMAP.md               # Product/milestone roadmap
├── SECURITY.md              # Vulnerability reporting and security policy
├── CONTRIBUTING.md          # Top-level contribution policy
├── THIRD_PARTY_NOTICES.md   # Third-party licensing notices
└── what_changed.md          # Evidence-oriented implementation history
```

This human-readable tree is intentionally compact. Git is the authoritative complete file inventory; the documentation-coverage verifier fails if any tracked path falls outside the documented ownership taxonomy.

## `sudoku-engine/`

The engine is intentionally Android-independent. This is the correctness-critical domain module and should not import Compose, Android resources, Context, Room, DataStore, or Android lifecycle types.

Primary package:

```text
sudoku-engine/src/main/kotlin/com/sanskar/sudokunova/engine/
```

Important production files include:

- `SudokuBoard.kt` — immutable board model, parsing, serialization, validation, candidates and conflicts.
- `SudokuSolver.kt` — MRV-style recursive solver, solution counting and search metrics.
- `SudokuGenerator.kt` — deterministic seeded generation with uniqueness preservation.
- `Difficulty.kt` — supported difficulty definitions/targets.
- `DifficultyCalibrator.kt` — difficulty calibration support.
- `LogicalDifficultyAnalyzer.kt` — logical/complexity analysis.
- `LogicalSolver.kt` — shared logical solving pipeline.
- `HintEngine.kt` — logical teaching-hint selection plus explicit Reveal fallback.
- `TeachingStep.kt` — structured technique/source/target/elimination/placement evidence.
- `TeachingStepFinder.kt` — deterministic logical-technique search/candidate-state transformation.
- `TeachingPractice.kt` — deterministic offline practice catalog.
- `PuzzleCodeCodec.kt` — versioned `SNP1` puzzle-code format.

Engine tests live under:

```text
sudoku-engine/src/test/kotlin/com/sanskar/sudokunova/engine/
```

They cover board correctness, solving, uniqueness, generation, difficulty, teaching evidence, practice, hint identity and regression corpora.

## `app/`

The Android application owns platform concerns:

- Compose UI;
- navigation;
- Android lifecycle/ViewModels;
- localization/resources;
- Room persistence;
- Preferences DataStore;
- Android sharing/document picker;
- accessibility presentation;
- release/output/signing configuration;
- Android-specific test infrastructure.

### Production source

```text
app/src/main/java/com/sanskar/sudokunova/
```

The package is divided into broad areas.

### `data/`

Local data and persistence layer.

Representative responsibilities:

- `AppPreferencesRepository.kt` — Preferences DataStore-backed settings, active game, statistics and learning progress.
- `UserSettings.kt` — settings model.
- `LearningProgress.kt` — learning/mastery domain values used by Android presentation.
- `history/` — Room database, entities, DAOs, migrations and history/saved-puzzle repository behavior.
- `challenge/` — challenge descriptors/keys/results/repository behavior.
- `transfer/` — backup models, `SNB1` codec, bounded file I/O and restore/export repository logic.

### `game/`

Android-independent-or-near-independent active-game state used by the app module, including `GameState` and game-state serialization/restoration support.

### `ui/`

Compose screens, ViewModels and presentation helpers. Feature-oriented packages include areas such as:

- Home/navigation;
- Game;
- Challenges;
- Custom Puzzle;
- History;
- Saved Puzzles;
- Learn;
- Statistics;
- Settings;
- Backup & Transfer;
- About;
- theme/design helpers.

The UI layer converts engine/domain evidence into localized Android resources rather than pushing player-facing prose into `sudoku-engine`.

### Resources

```text
app/src/main/res/
```

Important resource categories include:

- `values/` — default/English strings and theme resources;
- `values-hi/` — Hindi strings;
- `drawable/` / `mipmap-*` — launcher/splash/vector assets;
- `xml/` — Android backup/data-extraction rules and related configuration.

English/Hindi parity is checked by `scripts/verify_translations.py`.

### Benchmark-only app source set

```text
app/src/benchmark/AndroidManifest.xml
```

The `benchmark` app build type is initialized from `release`, keeps release R8/resource shrinking behavior, is non-debuggable, and uses debug signing so maintainers can measure locally without production signing material. The benchmark-only manifest enables shell profiling for Macrobenchmark. That profiling declaration does not live in the production `release` manifest.

The target app also includes AndroidX ProfileInstaller so Macrobenchmark can perform the supported profile/reset and shader-cache operations required by Android's benchmark tooling.

## Android JVM Tests

```text
app/src/test/java/com/sanskar/sudokunova/
```

The JVM test tree is organized into areas including:

- `data/` — persistence/transfer/learning models and codecs;
- `game/` — active game/state serialization/behavior;
- `ui/` — pure presentation/helper behavior where JVM testing is appropriate.

Use JUnit4 in the Android app module unless the module configuration is intentionally changed.

## Android Instrumentation Tests

```text
app/src/androidTest/java/com/sanskar/sudokunova/
```

Connected tests cover Compose navigation and Room-backed persistence/transfer flows on the configured API-35 emulator CI gate. Stable semantic test tags are used when duplicate/off-screen text would make tests brittle.

## `macrobenchmark/`

The Macrobenchmark module is a separate `com.android.test` module targeting the release-like `:app` benchmark variant.

Important files:

```text
macrobenchmark/build.gradle.kts
macrobenchmark/src/main/AndroidManifest.xml
macrobenchmark/src/main/kotlin/com/sanskar/sudokunova/macrobenchmark/StartupBenchmark.kt
```

Responsibilities:

- target `in.sanskar.sudokunova` explicitly;
- keep benchmark instrumentation outside the target app process contract;
- expose the target package through benchmark-test package visibility;
- keep API-29 test-output compatibility in the benchmark test APK only;
- measure cold startup timing;
- measure warm startup timing;
- measure cold-start frame timing;
- use a defined `CompilationMode.None()` starting state;
- compile in ordinary CI without turning hosted-emulator timings into production evidence.

Representative physical-device measurement is a separate release-evidence step. See `PERFORMANCE_BENCHMARKING.md`.

## Room Schemas

```text
app/schemas/
```

Room schema export is enabled. Schema history is part of the persistence contract and should remain reviewable when the database version changes.

Do not add destructive migration fallback as a shortcut for schema changes that should preserve user data.

## `docs/`

Documentation is part of the product. The categorized documentation map is `docs/README.md`.

High-value references include:

- user guide/features;
- architecture/project structure;
- repository file reference/documentation ownership;
- engine/teaching/difficulty/generation;
- data storage/formats/backup;
- build/setup/testing/CI/repository guards;
- performance benchmarking/evidence;
- accessibility/localization/privacy/security;
- production signing/release validation;
- release QA/releasing/checklists/evidence;
- contribution/maintenance/documentation standards.

`docs/REPOSITORY_FILE_REFERENCE.md` is the path-oriented companion to this architectural map. It defines which documentation owns each tracked repository area and explains how additions/moves/deletions remain covered.

## `.github/workflows/`

Current quality/release workflows include:

- `ci.yml` — repository security guard; release-verifier tests; documentation-link, documentation-coverage and release-contract regression/direct guards; signing fail-closed regression; translation parity; engine tests; Android JVM tests; Android instrumentation-test compilation; Macrobenchmark harness compilation; debug/release lint; debug APK; R8/resource-shrunk release APK; release AAB; package/version/artifact verification; report/evidence upload.
- `instrumentation.yml` — API-35 connected Compose/Room verification using an emulator with KVM when available.
- `release-validation.yml` — manually dispatched, protected signed-release validation through the `production-release` GitHub Environment; verifies package/version/signature/certificate/hash evidence without making production publication automatic.

Do not weaken gates simply to make a pull request green. Fix the underlying defect or document a justified workflow change.

Do not expose production signing secrets to ordinary pull-request workflows. The protected release-validation path remains distinct from normal CI.

## `scripts/`

Repository verification scripts include:

- `verify_translations.py` — English/Hindi key/format parity verification.
- `verify_no_secrets.py` / the underlying repository-security verifier — committed signing/private-key/obvious credential guard.
- `verify_documentation_links.py` — repository-local Markdown target integrity and repository-boundary validation.
- `verify_documentation_coverage.py` — fail-closed `git ls-files` ownership check requiring every tracked path to have canonical documentation.
- `verify_release_contract.py` — source/ordinary-CI/protected-release workflow application/version identity synchronization.
- `verify_release_outputs.py` — APK/AAB/R8 structure, package/version identity, checksums and optional signed/certificate-bound release verification.

Regression tests under `scripts/tests/` cover the documentation-link, documentation-coverage, release-contract, and release-output verifiers. Guards that can block CI/releases should remain deterministic and have both acceptance and rejection coverage.

Useful direct audit commands include:

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

Scripts used by CI should be deterministic, fast, fail closed for security/release/documentation ownership checks, and produce actionable output.

## Root Documentation Files

Root files serve repository-wide purposes:

- `README.md` — concise landing page and current implemented capabilities.
- `CHANGELOG.md` — release-oriented change history.
- `ROADMAP.md` — milestone status.
- `what_changed.md` — detailed implementation/verification evidence; it must not invent successful tests or device checks.
- `SECURITY.md` — authoritative vulnerability reporting policy.
- `CONTRIBUTING.md` — contributor entry point.
- `THIRD_PARTY_NOTICES.md` — direct dependency/tooling notice summary.

Other root policy/community files such as `AUTHORS.md`, `CODE_OF_CONDUCT.md`, `LICENSE`, and `SUPPORT.md` remain covered by the repository file-reference taxonomy even though this section highlights the most frequently edited documents.

More specialized material belongs under `docs/` so the root remains navigable.

## Complete Tracked-File Ownership Invariant

A compact structure document inevitably groups related source files. That grouping must never be interpreted as permission to skip individual tracked paths during documentation review.

Run:

```bash
python scripts/verify_documentation_coverage.py --verbose
```

The verifier resolves every current `git ls-files` path to a canonical documentation area. Any unmatched new file fails CI. For a per-file Markdown table, use:

```bash
python scripts/verify_documentation_coverage.py --markdown
```

See [`REPOSITORY_FILE_REFERENCE.md`](REPOSITORY_FILE_REFERENCE.md) for the complete ownership policy and [`REPOSITORY_GUARDS.md`](REPOSITORY_GUARDS.md) for enforcement details.

## Change Placement Rules

When adding or fixing behavior, prefer the narrowest correct layer:

- Sudoku truth/validation/solver/generator/teaching evidence → `sudoku-engine`.
- Local database/DataStore/backup persistence → `app/.../data`.
- Active-game state transformations → game state/ViewModel layer, keeping pure logic separable where practical.
- Player-facing strings → Android resources.
- Compose presentation → feature UI package.
- Android share/document APIs → Android transfer/presentation layer.
- Release-like startup/frame measurement → `macrobenchmark` plus the benchmark-only app variant.
- Signing/package/certificate release verification → release workflow/scripts/docs, never committed secret material.
- Repository consistency/documentation ownership → `scripts/` plus the corresponding `docs/` authorities.
- Cross-cutting policy/process → root or `docs/`.

## New Feature Checklist

Before adding a new feature family, identify:

1. domain model/engine impact;
2. persistence changes and migration needs;
3. ViewModel/state changes;
4. Compose UI changes;
5. English/Hindi resources;
6. accessibility semantics;
7. JVM tests;
8. connected tests when needed;
9. performance/benchmark implications;
10. privacy/security implications;
11. backup compatibility implications;
12. release/signing implications;
13. documentation updates;
14. tracked-file documentation ownership for every new path;
15. changelog/roadmap impact.

Run both documentation guards after adding/moving files:

```bash
python scripts/verify_documentation_coverage.py
python scripts/verify_documentation_links.py
```

This prevents a feature from landing as UI-only code without correctness, persistence, accessibility, localization, performance, security, release support, or documentation ownership.
