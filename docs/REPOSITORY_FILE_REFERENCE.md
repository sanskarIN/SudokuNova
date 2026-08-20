# Repository File Reference and Documentation Coverage

This document defines how every Git-tracked file in SudokuNova is connected to maintained documentation. It is path-oriented: start from a file, identify its subsystem, then follow the canonical guides for purpose, invariants, tests, platform impact, and release evidence.

The authoritative file inventory is Git, not this prose. Run:

```bash
python scripts/verify_documentation_coverage.py --verbose
```

The verifier executes `git ls-files -z`, resolves **every tracked path**, and fails if even one tracked file has no documentation owner. It also fails when a rule points to a canonical document that is not tracked or when a detailed `docs/*.md` guide is hidden from `docs/README.md`.

For an auditable Markdown table:

```bash
python scripts/verify_documentation_coverage.py --markdown
```

## Coverage Contract

Every tracked path must satisfy these conditions:

1. It matches one effective first-match area in `scripts/verify_documentation_coverage.py`.
2. That area names one or more canonical documentation files.
3. Every canonical document is tracked.
4. New top-level areas fail closed until ownership is added deliberately.
5. Specific rules appear before broad module rules so tests/resources/schemas/host source keep correct ownership.
6. Every detailed tracked guide under `docs/` remains discoverable from `docs/README.md`.
7. CI runs both verifier regression tests and the direct repository-wide check.

The guard proves documentation **ownership**, not factual correctness. Source review, link validation, builds/tests, release evidence, privacy/security review, and manual QA remain separate responsibilities.

# Root Repository Files

## Project and Community Documents

Tracked root policy/documentation files include:

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

- [`docs/README.md`](README.md)
- [`DOCUMENTATION_STANDARDS.md`](DOCUMENTATION_STANDARDS.md)
- the file itself when it is the authoritative policy/history.

Important boundaries:

- `README.md` is the public landing page, not the complete engineering manual.
- `CHANGELOG.md` records release-relevant changes rather than every commit.
- `ROADMAP.md` records planned/current milestone direction and is not verification evidence.
- `SECURITY.md` is the authoritative vulnerability-reporting policy.
- `THIRD_PARTY_NOTICES.md` records applicable third-party dependency/tool notices.
- `what_changed.md` is the active evidence-oriented implementation/handoff ledger and must not invent successful checks.

## Root Build Entry Points

Tracked root build files include:

- `build.gradle.kts`
- `settings.gradle.kts`
- `gradle.properties`
- `gradlew`
- `gradlew.bat`
- files below `gradle/`, including `libs.versions.toml` and wrapper metadata.

Canonical ownership:

- [`BUILDING.md`](BUILDING.md)
- [`DEVELOPMENT_SETUP.md`](DEVELOPMENT_SETUP.md)
- [`PROJECT_STRUCTURE.md`](PROJECT_STRUCTURE.md)
- [`CROSS_PLATFORM.md`](CROSS_PLATFORM.md) when KMP/Compose target/plugin versions change.

These files configure the complete build graph. Plugin/Kotlin/Compose/JDK/SDK/Gradle changes can affect Android, Desktop, Apple, and Web targets simultaneously.

## Editor and Ignore Configuration

- `.editorconfig`
- `.gitignore`

Canonical ownership:

- [`CONTRIBUTING_GUIDE.md`](CONTRIBUTING_GUIDE.md)
- [`PROJECT_STRUCTURE.md`](PROJECT_STRUCTURE.md)

Ignore rules must never replace the repository security guard.

# `.github/` Automation and Collaboration

## Workflows

Every tracked file under `.github/workflows/` is quality/release automation and is owned by:

- [`CI_CD.md`](CI_CD.md)
- [`RELEASING.md`](RELEASING.md)
- [`PRODUCTION_RELEASE_VALIDATION.md`](PRODUCTION_RELEASE_VALIDATION.md)
- [`PERFORMANCE_BENCHMARKING.md`](PERFORMANCE_BENCHMARKING.md) where benchmark behavior applies.

Current workflow families include:

- `ci.yml` — shared KMP tests/compile plus the strict Android 2.0.12 release-quality gate;
- `cross-platform.yml` — hosted Android shared integration, Web distribution, iOS Simulator framework, and Linux/Windows/macOS Desktop application-image validation;
- `instrumentation.yml` — API-35 connected Android Compose/Room verification;
- `release-validation.yml` — protected/manual signed Android production-release validation.

Workflow source proves configured automation. A passing claim requires an actual run tied to the exact commit SHA.

## Collaboration Metadata

Other `.github/` files include CODEOWNERS, funding, issue/PR templates, Dependabot, and generated-release-note configuration.

Canonical ownership:

- [`MAINTAINER_GUIDE.md`](MAINTAINER_GUIDE.md)
- [`GITHUB_REPOSITORY_SETTINGS.md`](GITHUB_REPOSITORY_SETTINGS.md)
- [`../CONTRIBUTING.md`](../CONTRIBUTING.md)

Repository-admin settings such as rulesets and protected environments remain external account configuration unless separately verified.

# `sudoku-engine/` Kotlin Multiplatform Domain Module

## Engine Implementation — `sudoku-engine/src/main/`

Canonical ownership:

- [`SUDOKU_ENGINE.md`](SUDOKU_ENGINE.md)
- [`PUZZLE_GENERATION.md`](PUZZLE_GENERATION.md)
- [`DIFFICULTY_SYSTEM.md`](DIFFICULTY_SYSTEM.md)
- [`LEARNING_AND_HINTS.md`](LEARNING_AND_HINTS.md)

This area owns Sudoku validity, solving, generation, uniqueness, difficulty, logical techniques, structured teaching evidence, hints, practice, and portable puzzle-code behavior.

The implementation tree is mapped into KMP `commonMain`. It must remain free of Android, Compose, UIKit, browser, Desktop-window, Room, and DataStore dependencies.

## Engine Tests — `sudoku-engine/src/test/`

Canonical ownership:

- [`SUDOKU_ENGINE.md`](SUDOKU_ENGINE.md)
- [`TESTING.md`](TESTING.md)
- [`PUZZLE_GENERATION.md`](PUZZLE_GENERATION.md)

The existing tests use `kotlin.test` and are mapped into KMP `commonTest`. Shared correctness is higher priority than generation speed or platform feature breadth.

## Engine Build Configuration — other `sudoku-engine/` files

Canonical ownership:

- [`SUDOKU_ENGINE.md`](SUDOKU_ENGINE.md)
- [`BUILDING.md`](BUILDING.md)
- [`PROJECT_STRUCTURE.md`](PROJECT_STRUCTURE.md)
- [`CROSS_PLATFORM.md`](CROSS_PLATFORM.md)

Target/source-set changes can affect Android, Desktop, Apple, and Web simultaneously and must be validated through the cross-platform workflow.

# `sharedUI/` Compose Multiplatform Module

## Common/Platform Implementation and Resources — `sharedUI/src/`

Canonical ownership:

- [`CROSS_PLATFORM.md`](CROSS_PLATFORM.md)
- [`ARCHITECTURE.md`](ARCHITECTURE.md)
- [`PROJECT_STRUCTURE.md`](PROJECT_STRUCTURE.md)

This family includes:

- `commonMain` portable gameplay state/UI;
- `desktopMain` Desktop window entry point;
- `iosMain` Compose `UIViewController` bridge;
- `wasmJsMain` browser entry point and static host resources.

Shared source must not absorb platform APIs solely for convenience. Put host-specific behavior in the appropriate platform source set/app or behind a common interface.

## Shared UI Tests — `sharedUI/src/commonTest/`

Canonical ownership:

- [`CROSS_PLATFORM.md`](CROSS_PLATFORM.md)
- [`TESTING.md`](TESTING.md)
- [`PROJECT_STRUCTURE.md`](PROJECT_STRUCTURE.md)

Portable gameplay-state tests currently protect fixed clues, notes/entry, undo/reset consistency, and hint progression. Add deterministic common tests when common behavior grows.

## Shared UI Build Configuration — other `sharedUI/` files

Canonical ownership:

- [`CROSS_PLATFORM.md`](CROSS_PLATFORM.md)
- [`BUILDING.md`](BUILDING.md)
- [`PROJECT_STRUCTURE.md`](PROJECT_STRUCTURE.md)

This area owns KMP targets, Compose dependencies/compiler plugin, iOS framework configuration, Web executable configuration, and Desktop native distribution metadata.

Changes must preserve Android compatibility and be validated on all affected hosted targets rather than only the developer's host OS.

# `iosApp/` Apple SwiftUI Host Sources

Every tracked file under `iosApp/` is owned by:

- [`CROSS_PLATFORM.md`](CROSS_PLATFORM.md)
- [`BUILDING.md`](BUILDING.md)
- [`PROJECT_STRUCTURE.md`](PROJECT_STRUCTURE.md)

Current files include the SwiftUI `@main` entry point, `UIViewControllerRepresentable` bridge, and host integration guide.

These sources provide a native host boundary for `SudokuNovaSharedUI.framework`. They do not by themselves prove a complete Xcode application target, Apple signing/provisioning, physical-device behavior, or App Store publication.

If a full Xcode project/workspace is added later, extend the documentation ownership taxonomy narrowly for its project files/assets/configuration instead of hiding it under an unrelated rule.

# `app/` Android Application Module

The `app` module remains SudokuNova's mature primary production application. It consumes `:sudoku-engine` and `:sharedUI` while owning Android-specific persistence, navigation, localization, accessibility integration, transfer, and release packaging.

## `app/src/main/`

Canonical ownership:

- [`ARCHITECTURE.md`](ARCHITECTURE.md)
- [`PROJECT_STRUCTURE.md`](PROJECT_STRUCTURE.md)
- [`FEATURES.md`](FEATURES.md)
- [`DATA_STORAGE.md`](DATA_STORAGE.md)
- [`UI_UX.md`](UI_UX.md)

The manifest/source family includes both the mature `MainActivity` launcher and the non-exported `CrossPlatformActivity` shared-UI host.

### Persistence and Local Data

For Room, DataStore, active game, challenge/history/saved data, backup/restore, codecs, migrations, or repositories, also read:

- [`DATA_STORAGE.md`](DATA_STORAGE.md)
- [`DATA_FORMATS.md`](DATA_FORMATS.md)
- [`BACKUP_RESTORE.md`](BACKUP_RESTORE.md)
- [`PRIVACY.md`](PRIVACY.md)
- [`SECURITY.md`](SECURITY.md)

Persistent-format changes require compatibility tests/documentation before release.

### Gameplay Presentation and Navigation

For Android Compose screens/components/input/dialogs/navigation/theme behavior, also read:

- [`USER_GUIDE.md`](USER_GUIDE.md)
- [`FEATURES.md`](FEATURES.md)
- [`UI_UX.md`](UI_UX.md)
- [`DESIGN_SYSTEM.md`](DESIGN_SYSTEM.md)
- [`ACCESSIBILITY.md`](ACCESSIBILITY.md)
- [`KEYBOARD_SHORTCUTS.md`](KEYBOARD_SHORTCUTS.md) when hardware input changes.

### Learning and Hint Presentation

Also owned by:

- [`LEARNING_AND_HINTS.md`](LEARNING_AND_HINTS.md)
- [`ACCESSIBILITY.md`](ACCESSIBILITY.md)
- [`LOCALIZATION.md`](LOCALIZATION.md)

The engine owns structured logical evidence; presentation layers own localized accessible prose.

## Android Resources — `app/src/main/res/`

Canonical ownership:

- [`DESIGN_SYSTEM.md`](DESIGN_SYSTEM.md)
- [`LOCALIZATION.md`](LOCALIZATION.md)
- [`ACCESSIBILITY.md`](ACCESSIBILITY.md)
- [`PROJECT_STRUCTURE.md`](PROJECT_STRUCTURE.md)

English/Hindi string-resource changes must preserve key/placeholder parity. Visual resources must satisfy project licensing and accessibility requirements.

## Room Schemas — `app/schemas/`

Canonical ownership:

- [`DATA_STORAGE.md`](DATA_STORAGE.md)
- [`DATA_FORMATS.md`](DATA_FORMATS.md)
- [`TESTING.md`](TESTING.md)

Do not rewrite historical schema snapshots to make a migration look cleaner. Database upgrades require registered migrations and compatibility evidence.

## Android JVM Tests — `app/src/test/`

Canonical ownership:

- [`TESTING.md`](TESTING.md)
- [`PROJECT_STRUCTURE.md`](PROJECT_STRUCTURE.md)

Protect deterministic app-local logic, codecs, repositories, backup limits, state transitions, and presentation helpers where an Android runtime is unnecessary.

## Android Instrumentation Tests — `app/src/androidTest/`

Canonical ownership:

- [`TESTING.md`](TESTING.md)
- [`QA_MATRIX.md`](QA_MATRIX.md)
- [`ACCESSIBILITY.md`](ACCESSIBILITY.md)

These protect device/emulator-dependent Compose/Room behavior. Automated semantics checks do not replace real assistive-technology/manual evidence.

## Benchmark-only Android Overlay — `app/src/benchmark/`

Canonical ownership:

- [`PERFORMANCE_BENCHMARKING.md`](PERFORMANCE_BENCHMARKING.md)
- [`PERFORMANCE.md`](PERFORMANCE.md)

Benchmark-only manifest capabilities must never leak into the production release manifest.

## Other `app/` Files

Gradle and R8/ProGuard configuration is owned by:

- [`BUILDING.md`](BUILDING.md)
- [`PROJECT_STRUCTURE.md`](PROJECT_STRUCTURE.md)
- [`PRODUCTION_SIGNING.md`](PRODUCTION_SIGNING.md)

Changes must retain the Android 2.0.12 identity/signing/artifact-verification contract unless an intentional later release changes it.

# `macrobenchmark/` Performance Test Module

Every tracked file under `macrobenchmark/` is owned by:

- [`PERFORMANCE_BENCHMARKING.md`](PERFORMANCE_BENCHMARKING.md)
- [`PERFORMANCE.md`](PERFORMANCE.md)
- [`TESTING.md`](TESTING.md)

The module is a reproducible measurement harness. Compilation is not evidence that representative physical-device benchmarks ran.

# `scripts/` Deterministic Repository Tooling

## Verification Scripts

Every tracked non-test file under `scripts/` is owned by:

- [`REPOSITORY_GUARDS.md`](REPOSITORY_GUARDS.md)
- [`CI_CD.md`](CI_CD.md)
- [`MAINTAINER_GUIDE.md`](MAINTAINER_GUIDE.md)

Scripts currently protect documentation links/ownership, repository secret material, release source/workflow identity, release artifacts/signatures/certificates, and translations.

## Script Regression Tests — `scripts/tests/`

Canonical ownership:

- [`REPOSITORY_GUARDS.md`](REPOSITORY_GUARDS.md)
- [`TESTING.md`](TESTING.md)

Blocking guards must have deterministic acceptance and rejection coverage.

# `docs/` Maintained Documentation Library

Every tracked page under `docs/` is indexed by [`README.md`](README.md) and governed by [`DOCUMENTATION_STANDARDS.md`](DOCUMENTATION_STANDARDS.md).

Major responsibility groups:

- product/player behavior — Getting Started, Installation, User Guide, Features, Rules, FAQ, Troubleshooting;
- cross-platform/architecture — Cross Platform, Architecture, Project Structure, Repository File Reference;
- domain — Sudoku Engine, Puzzle Generation, Difficulty, Learning/Hints, Glossary;
- persistence/privacy/security — Data Storage, Data Formats, Backup/Restore, Privacy, Security;
- presentation/accessibility — UI/UX, Design System, Accessibility, Localization, Keyboard Shortcuts;
- contributor/maintenance — Development Setup, Building, Testing, CI/CD, Contributing, Maintainer Guide, Documentation Standards, Repository Guards, GitHub Settings;
- performance — Performance and Performance Benchmarking;
- release — signing, protected validation, Play Store prep, release QA/checklists/evidence/releasing;
- historical evidence — milestone-specific records preserving exact past implementation/verification context.

There should be one clear primary authority per technical contract even when several guides provide audience-specific context.

# What to Do When Adding a File

Before committing a new tracked file:

1. Decide which repository area owns it.
2. Update the narrowest technical guide when behavior/contracts change.
3. Run `python scripts/verify_documentation_coverage.py`.
4. If the path represents a genuinely new area, add a narrow coverage rule and regression test instead of hiding it beneath an unrelated broad rule.
5. Run `python scripts/verify_documentation_links.py` after documentation moves/links.
6. Add target-specific build/test coverage where the new file affects a platform.
7. Update `what_changed.md` for milestone/release-significant work.
8. Update `CHANGELOG.md` only for release-notable changes.
9. Re-run exact-head PR workflows after the final commit.

# What to Do When Moving or Deleting Files

1. Update references and imports/build configuration.
2. Update path-specific documentation.
3. Update coverage rules/tests if ownership boundaries changed.
4. Run documentation-link and coverage guards.
5. Run the narrowest affected build/test targets.
6. Run the full required PR workflow set on the final head.

Do not leave compatibility aliases or duplicate files solely to keep stale documentation links passing unless there is a real compatibility requirement.

# Evidence Boundary

File ownership and build configuration are not equivalent to platform-release proof. In particular, committed Apple/Web/Desktop source does not itself prove signing, notarization, physical-device behavior, browser compatibility, store acceptance, or public distribution. Record those facts only after the corresponding real checks occur.
