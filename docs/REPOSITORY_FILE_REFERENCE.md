# Repository File Reference and Documentation Coverage

This document defines how every Git-tracked file in SudokuNova is connected to maintained documentation. It is intentionally path-oriented: contributors should be able to start from any file in the repository, identify the subsystem it belongs to, and find the canonical documents that explain its purpose, invariants, testing expectations, and release impact.

The source of truth for the actual file set is Git, not a manually copied tree. Run:

```bash
python scripts/verify_documentation_coverage.py --verbose
```

The command executes `git ls-files -z`, resolves **every tracked path**, and fails if even one tracked file has no documentation ownership rule. It also fails if a rule points to a canonical document that is no longer tracked.

For an auditable per-file Markdown table, run:

```bash
python scripts/verify_documentation_coverage.py --markdown
```

This design avoids a static file inventory becoming stale the moment a source file is added, renamed, or deleted.

## Coverage contract

Every tracked path must satisfy all of these conditions:

1. It matches exactly one effective first-match area in `scripts/verify_documentation_coverage.py`.
2. That area identifies one or more canonical documentation files.
3. Every referenced canonical document is itself tracked.
4. New top-level areas fail closed until documentation ownership is added deliberately.
5. More-specific rules are ordered before broad module rules so tests, resources, schemas, and benchmark overlays retain distinct documentation ownership.
6. CI runs both the verifier regression suite and the direct repository-wide coverage check.

The coverage guard proves documentation ownership. It does **not** prove every sentence is current; implementation-alignment, link integrity, release evidence, privacy, security, and manual QA still have their own review rules.

## Root repository files

### Project and community documents

The root policy/documentation family includes:

- `README.md`
- `AUTHORS.md`
- `CHANGELOG.md`
- `CODE_OF_CONDUCT.md`
- `CONTRIBUTING.md`
- `LICENSE`
- `ROADMAP.md`
- `SECURITY.md`
- `SUPPORT.md`
- `THIRD_PARTY_NOTICES.md`
- `what_changed.md`

Canonical ownership:

- [`docs/README.md`](README.md) for documentation navigation;
- [`docs/DOCUMENTATION_STANDARDS.md`](DOCUMENTATION_STANDARDS.md) for truth/status/evidence rules;
- the file itself when it is the authoritative policy or historical record.

Important boundaries:

- `README.md` is the public landing page, not the deep technical reference.
- `CHANGELOG.md` records release-oriented notable changes, not every commit.
- `ROADMAP.md` tracks intended/current milestone work and is not verification evidence.
- `SECURITY.md` is the authoritative vulnerability-reporting policy.
- `THIRD_PARTY_NOTICES.md` tracks applicable dependency/tool licensing notices.
- `what_changed.md` is the detailed implementation and handoff record and must preserve historical evidence without fabricating tests or manual validation.

### Build entry points

Tracked root build files are:

- `build.gradle.kts`
- `settings.gradle.kts`
- `gradle.properties`
- `gradlew`
- `gradlew.bat`
- files below `gradle/`, including the version catalog and wrapper metadata

Canonical ownership:

- [`BUILDING.md`](BUILDING.md)
- [`DEVELOPMENT_SETUP.md`](DEVELOPMENT_SETUP.md)
- [`PROJECT_STRUCTURE.md`](PROJECT_STRUCTURE.md)

Changes here can affect every module. Dependency/plugin/JDK/SDK/Gradle version changes must be synchronized with build documentation and CI expectations.

### Editor and ignore configuration

- `.editorconfig`
- `.gitignore`

Canonical ownership:

- [`CONTRIBUTING_GUIDE.md`](CONTRIBUTING_GUIDE.md)
- [`PROJECT_STRUCTURE.md`](PROJECT_STRUCTURE.md)

These files define repository hygiene rather than application behavior. Ignore rules must never be used as a substitute for the repository security guard.

## `.github/` repository automation and collaboration

### Workflows

Every file under `.github/workflows/` is release/quality automation and is owned by:

- [`CI_CD.md`](CI_CD.md)
- [`RELEASING.md`](RELEASING.md)
- [`PRODUCTION_RELEASE_VALIDATION.md`](PRODUCTION_RELEASE_VALIDATION.md)
- [`PERFORMANCE_BENCHMARKING.md`](PERFORMANCE_BENCHMARKING.md) where benchmark compilation/execution applies

Current workflow responsibilities include ordinary Android CI, API-35 instrumentation, and protected production-release validation. Workflow source proves what automation is configured; it does not prove a particular run passed unless exact run/head evidence is recorded.

### Collaboration metadata

Other tracked `.github/` files include repository ownership, sponsorship metadata, issue forms/templates, pull-request template, Dependabot configuration, and generated-release-note configuration.

Canonical ownership:

- [`MAINTAINER_GUIDE.md`](MAINTAINER_GUIDE.md)
- [`GITHUB_REPOSITORY_SETTINGS.md`](GITHUB_REPOSITORY_SETTINGS.md)
- [`../CONTRIBUTING.md`](../CONTRIBUTING.md)

Repository-admin settings such as branch rules and protected environments remain separate from files committed under `.github/`.

## `app/` Android application module

The `app` module owns Android integration, persistence, Compose presentation, navigation, settings, transfer/backup, and platform-facing behavior. It consumes the platform-independent Sudoku engine rather than duplicating engine rules.

### `app/src/main/`

Application source and the production manifest are owned collectively by:

- [`ARCHITECTURE.md`](ARCHITECTURE.md)
- [`PROJECT_STRUCTURE.md`](PROJECT_STRUCTURE.md)
- [`FEATURES.md`](FEATURES.md)
- [`DATA_STORAGE.md`](DATA_STORAGE.md)
- [`UI_UX.md`](UI_UX.md)

Use the more specialized references below when the changed file belongs to a specific behavior.

#### Persistence and local data

For Room, DataStore, active-game state, challenge/history/saved-puzzle persistence, backup/restore, codecs, migrations, or data repositories, also read:

- [`DATA_STORAGE.md`](DATA_STORAGE.md)
- [`DATA_FORMATS.md`](DATA_FORMATS.md)
- [`BACKUP_RESTORE.md`](BACKUP_RESTORE.md)
- [`PRIVACY.md`](PRIVACY.md)
- [`SECURITY.md`](SECURITY.md)

Persistent-format changes require compatibility tests and documentation updates before release.

#### Gameplay presentation and navigation

For Compose screens/components, game input, settings, dialogs, navigation, themes, and interaction behavior, also read:

- [`USER_GUIDE.md`](USER_GUIDE.md)
- [`FEATURES.md`](FEATURES.md)
- [`UI_UX.md`](UI_UX.md)
- [`DESIGN_SYSTEM.md`](DESIGN_SYSTEM.md)
- [`ACCESSIBILITY.md`](ACCESSIBILITY.md)
- [`KEYBOARD_SHORTCUTS.md`](KEYBOARD_SHORTCUTS.md) when hardware input is affected

UI code must preserve semantics, large-font usability, contrast, motion preferences, and localization contracts rather than treating them as post-release documentation work.

#### Learning and hint presentation

Android rendering of structured teaching evidence is additionally owned by:

- [`LEARNING_AND_HINTS.md`](LEARNING_AND_HINTS.md)
- [`ACCESSIBILITY.md`](ACCESSIBILITY.md)
- [`LOCALIZATION.md`](LOCALIZATION.md)

The engine owns logical evidence; Android owns localized, accessible presentation.

### `app/src/main/res/`

All tracked Android XML/value/drawable/mipmap/resource files are specifically owned by:

- [`DESIGN_SYSTEM.md`](DESIGN_SYSTEM.md)
- [`LOCALIZATION.md`](LOCALIZATION.md)
- [`ACCESSIBILITY.md`](ACCESSIBILITY.md)
- [`PROJECT_STRUCTURE.md`](PROJECT_STRUCTURE.md)

String-resource changes must preserve English/Hindi key parity. Visual resources must remain compatible with the project license and accessibility requirements.

### `app/schemas/`

Exported Room schema history is owned by:

- [`DATA_STORAGE.md`](DATA_STORAGE.md)
- [`DATA_FORMATS.md`](DATA_FORMATS.md)
- [`TESTING.md`](TESTING.md)

Do not rewrite historical schema snapshots to make a migration look cleaner. Database upgrades require registered migrations and compatibility evidence.

### `app/src/test/`

JVM tests are owned by:

- [`TESTING.md`](TESTING.md)
- [`PROJECT_STRUCTURE.md`](PROJECT_STRUCTURE.md)

They are expected to protect deterministic application logic, codecs, repositories, backup limits, state transitions, and other Android-independent behavior where possible.

### `app/src/androidTest/`

Connected/instrumentation tests are owned by:

- [`TESTING.md`](TESTING.md)
- [`QA_MATRIX.md`](QA_MATRIX.md)
- [`ACCESSIBILITY.md`](ACCESSIBILITY.md)

They cover Android/Compose/Room behavior that requires a device or emulator. Automated semantics assertions do not replace real TalkBack/manual accessibility evidence.

### `app/src/benchmark/`

Benchmark-only application overlays are owned by:

- [`PERFORMANCE_BENCHMARKING.md`](PERFORMANCE_BENCHMARKING.md)
- [`PERFORMANCE.md`](PERFORMANCE.md)

Benchmark-only manifest capabilities must not leak into the production release manifest.

### Other `app/` files

Module-level Gradle and ProGuard/R8 configuration are owned by:

- [`BUILDING.md`](BUILDING.md)
- [`PROJECT_STRUCTURE.md`](PROJECT_STRUCTURE.md)
- [`PRODUCTION_SIGNING.md`](PRODUCTION_SIGNING.md)

Release configuration changes must stay synchronized with artifact verification and protected release validation.

## `sudoku-engine/` platform-independent domain module

### Engine implementation

Every tracked file under `sudoku-engine/src/main/` is owned by:

- [`SUDOKU_ENGINE.md`](SUDOKU_ENGINE.md)
- [`PUZZLE_GENERATION.md`](PUZZLE_GENERATION.md)
- [`DIFFICULTY_SYSTEM.md`](DIFFICULTY_SYSTEM.md)
- [`LEARNING_AND_HINTS.md`](LEARNING_AND_HINTS.md)

This area contains Sudoku validity, solving, generation, uniqueness, difficulty, logical techniques, structured teaching evidence, hints, practice, and portable puzzle-code behavior. Domain logic should remain free of Android UI dependencies.

### Engine tests

Every tracked file under `sudoku-engine/src/test/` is additionally owned by:

- [`TESTING.md`](TESTING.md)

Correctness is higher priority than generation speed or feature breadth. Uniqueness/determinism/logical-technique changes require regression evidence.

### Engine build configuration

Other tracked `sudoku-engine/` files are owned by:

- [`SUDOKU_ENGINE.md`](SUDOKU_ENGINE.md)
- [`BUILDING.md`](BUILDING.md)
- [`PROJECT_STRUCTURE.md`](PROJECT_STRUCTURE.md)

## `macrobenchmark/` performance test module

Every tracked file under `macrobenchmark/` is owned by:

- [`PERFORMANCE_BENCHMARKING.md`](PERFORMANCE_BENCHMARKING.md)
- [`PERFORMANCE.md`](PERFORMANCE.md)
- [`TESTING.md`](TESTING.md)

The module is a reproducible measurement harness, not proof that a representative physical-device benchmark was run. Measured results/traces must be recorded separately when actually collected.

## `scripts/` deterministic repository tooling

### Verification scripts

Every tracked non-test file under `scripts/` is owned by:

- [`REPOSITORY_GUARDS.md`](REPOSITORY_GUARDS.md)
- [`CI_CD.md`](CI_CD.md)
- [`MAINTAINER_GUIDE.md`](MAINTAINER_GUIDE.md)

Current scripts cover documentation links, documentation ownership, repository secret/security checks, release source/workflow identity, release artifacts/signatures/certificates, and translation parity.

### Script regression tests

Every tracked file under `scripts/tests/` is owned by:

- [`REPOSITORY_GUARDS.md`](REPOSITORY_GUARDS.md)
- [`TESTING.md`](TESTING.md)

A guard that can block releases must have deterministic tests for acceptance and failure paths.

## `docs/` maintained documentation library

Every tracked page under `docs/` is indexed by [`README.md`](README.md) and governed by [`DOCUMENTATION_STANDARDS.md`](DOCUMENTATION_STANDARDS.md).

The library is intentionally split by responsibility rather than duplicated into one enormous guide:

- product/player behavior: Getting Started, Installation, User Guide, Features, Rules, FAQ, Troubleshooting;
- architecture/domain: Architecture, Project Structure, Sudoku Engine, Puzzle Generation, Difficulty, Learning/Hints, Glossary;
- persistence/privacy/security: Data Storage, Data Formats, Backup/Restore, Privacy, Security;
- presentation/accessibility: UI/UX, Design System, Accessibility, Localization, Keyboard Shortcuts;
- contributor/maintenance: Development Setup, Building, Testing, CI/CD, Contributing, Maintainer Guide, Documentation Standards, Repository Guards, GitHub Settings;
- performance: Performance and Performance Benchmarking;
- release: signing, protected validation, Play Store preparation, release checklists/QA, release evidence, release notes, releasing;
- historical evidence: milestone-specific hardening/transfer/release records that preserve exact implementation context.

A page may belong to more than one audience, but there should be one clear primary source for each technical contract.

## What to do when adding a file

Before committing a new tracked file:

1. Decide which repository area owns it.
2. Add/update the relevant technical documentation if behavior or contracts change.
3. Run `python scripts/verify_documentation_coverage.py`.
4. If the new path is intentionally a new area, add a narrow coverage rule and regression test instead of hiding it beneath an unrelated broad rule.
5. Run `python scripts/verify_documentation_links.py` after documentation moves/links.
6. Update `what_changed.md` for milestone/release-significant work.
7. Update `CHANGELOG.md` only when the change is notable at release level.

## What to do when moving or deleting a file

1. Update imports/build configuration and local links.
2. Update path-specific documentation where the old location was named.
3. Run documentation-link verification.
4. Run documentation-coverage verification.
5. Run the relevant source/test/build gates.
6. Preserve historical evidence references when they intentionally identify an old commit/path; clarify that the reference is historical rather than rewriting history.

## Audit commands

From the repository root:

```bash
python -m unittest scripts.tests.test_verify_documentation_coverage
python scripts/verify_documentation_coverage.py
python scripts/verify_documentation_coverage.py --verbose
python scripts/verify_documentation_links.py
python scripts/verify_release_contract.py
python scripts/verify_no_secrets.py
python scripts/verify_translations.py
```

Android/source verification remains documented in [`TESTING.md`](TESTING.md) and [`BUILDING.md`](BUILDING.md).

## Completion boundary

Passing this coverage guard means no **tracked file path** is skipped by the documentation ownership model. It does not manufacture:

- a successful Android build that was not run;
- a green workflow run that was not observed;
- physical-device performance measurements;
- TalkBack/manual accessibility results;
- production signing identity;
- repository-admin settings;
- Play Store publication.

Those claims require their own exact evidence. This distinction keeps the repository deeply documented without turning documentation completeness into false release readiness.
