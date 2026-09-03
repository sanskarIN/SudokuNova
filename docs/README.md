# SudokuNova Documentation

This directory is the maintained documentation hub for SudokuNova. Documentation follows an evidence-first rule: source-backed behavior may be described as implemented; future work must be labeled planned; automated, manual, signing, store, and device-verification claims are recorded only after the corresponding check actually happened.

SudokuNova v0.1.2 is being prepared as a deliberate release line while the repository continues its later 2.x development. Android remains the mature primary production surface while the Kotlin/Compose Multiplatform layer continues to expand. Start with [Cross-Platform Development and Builds](CROSS_PLATFORM.md) when working on Android shared UI, iOS/iPadOS, Windows, macOS, Linux, or Web.

## Choose Your Path

### I want to use SudokuNova

1. [Getting Started](GETTING_STARTED.md)
2. [Installation](INSTALLATION.md)
3. [User Guide](USER_GUIDE.md)
4. [Feature Reference](FEATURES.md)
5. [Game Rules](GAME_RULES.md)
6. [Keyboard Shortcuts](KEYBOARD_SHORTCUTS.md)
7. [FAQ](FAQ.md)
8. [Troubleshooting](TROUBLESHOOTING.md)

### I want to build or contribute

1. [Development Setup](DEVELOPMENT_SETUP.md)
2. [Cross-Platform Development and Builds](CROSS_PLATFORM.md)
3. [Shared Cross-Platform Active-Game Persistence](SHARED_PERSISTENCE.md)
4. [Shared Cross-Platform User Settings](SHARED_SETTINGS.md)
5. [Project Structure](PROJECT_STRUCTURE.md)
6. [Architecture](ARCHITECTURE.md)
7. [Building](BUILDING.md)
8. [Testing](TESTING.md)
9. [Quality Gates](QUALITY_GATES.md)
10. [Repository File Reference and Documentation Coverage](REPOSITORY_FILE_REFERENCE.md)
11. [Repository Consistency Guards](REPOSITORY_GUARDS.md)
12. [CI/CD](CI_CD.md)
13. [Exact-Head Verification](EXACT_HEAD_VERIFICATION.md)
14. [Performance Benchmarking and Evidence](PERFORMANCE_BENCHMARKING.md)
15. [Contributing Guide](CONTRIBUTING_GUIDE.md)
16. [`../CONTRIBUTING.md`](../CONTRIBUTING.md)

### I am changing Sudoku logic

1. [Sudoku Engine](SUDOKU_ENGINE.md)
2. [Puzzle Generation](PUZZLE_GENERATION.md)
3. [Difficulty System](DIFFICULTY_SYSTEM.md)
4. [Learning and Advanced Hints](LEARNING_AND_HINTS.md)
5. [Testing](TESTING.md)
6. [Performance](PERFORMANCE.md)
7. [Performance Benchmarking and Evidence](PERFORMANCE_BENCHMARKING.md)
8. [Glossary](GLOSSARY.md)

### I am changing cross-platform code

1. [Cross-Platform Development and Builds](CROSS_PLATFORM.md)
2. [Shared Cross-Platform Active-Game Persistence](SHARED_PERSISTENCE.md)
3. [Shared Cross-Platform User Settings](SHARED_SETTINGS.md)
4. [Architecture](ARCHITECTURE.md)
5. [Project Structure](PROJECT_STRUCTURE.md)
6. [Building](BUILDING.md)
7. [Testing](TESTING.md)
8. [Quality Gates](QUALITY_GATES.md)
9. [CI/CD](CI_CD.md)
10. [Repository File Reference and Documentation Coverage](REPOSITORY_FILE_REFERENCE.md)
11. [Repository Consistency Guards](REPOSITORY_GUARDS.md)

The portable code lives primarily in `sudoku-engine/`, `sharedUI/`, and `iosApp/`. Android's existing `app/` module remains the primary production application while shared parity is expanded incrementally.

### I am changing persistence/import/backup

1. [Shared Cross-Platform Active-Game Persistence](SHARED_PERSISTENCE.md)
2. [Shared Cross-Platform User Settings](SHARED_SETTINGS.md)
3. [Shared Puzzle Exchange](SHARED_PUZZLE_EXCHANGE.md)
4. [Data Storage](DATA_STORAGE.md)
5. [Data Formats](DATA_FORMATS.md)
6. [Backup / Restore](BACKUP_RESTORE.md)
7. [v0.7 Transfer/Backup Technical Record](TRANSFER_BACKUP_V07.md)
8. [Privacy](PRIVACY.md)
9. [Security](SECURITY.md)
10. [`../SECURITY.md`](../SECURITY.md)
11. [Testing](TESTING.md)

### I am preparing v0.1.2

Read in this order:

1. [v0.1.2 Release Line](V0_1_2_RELEASE.md)
2. [Cross-Platform Development and Builds](CROSS_PLATFORM.md)
3. [Shared Cross-Platform Active-Game Persistence](SHARED_PERSISTENCE.md)
4. [Shared Cross-Platform User Settings](SHARED_SETTINGS.md)
5. [Shared Puzzle Exchange](SHARED_PUZZLE_EXCHANGE.md)
6. [Building](BUILDING.md)
7. [Production Signing](PRODUCTION_SIGNING.md)
8. [Production Release Validation Workflow](PRODUCTION_RELEASE_VALIDATION.md)
9. [Performance Benchmarking and Evidence](PERFORMANCE_BENCHMARKING.md)
10. [Play Store Release Preparation](PLAY_STORE_RELEASE.md)
11. [GitHub Repository Settings](GITHUB_REPOSITORY_SETTINGS.md)
12. [Exact-Head Verification](EXACT_HEAD_VERIFICATION.md)
13. [Releasing](RELEASING.md)
14. [Release Checklist](RELEASE_CHECKLIST.md)
15. [Release QA](RELEASE_QA.md)
16. [QA Matrix](QA_MATRIX.md)
17. [Quality Gates](QUALITY_GATES.md)
18. [Repository File Reference and Documentation Coverage](REPOSITORY_FILE_REFERENCE.md)
19. [Repository Consistency Guards](REPOSITORY_GUARDS.md)
20. [CI/CD](CI_CD.md)
21. [Testing](TESTING.md)
22. [Performance](PERFORMANCE.md)
23. [Accessibility](ACCESSIBILITY.md)
24. [Privacy](PRIVACY.md)
25. [`../SECURITY.md`](../SECURITY.md)
26. [`../THIRD_PARTY_NOTICES.md`](../THIRD_PARTY_NOTICES.md)
27. [`../CHANGELOG.md`](../CHANGELOG.md)
28. [`../ROADMAP.md`](../ROADMAP.md)
29. [`../what_changed.md`](../what_changed.md)

For later 2.x development, use the applicable release-line documents such as [2.0.14 Release Line](V2_0_14_RELEASE.md) and [2.0.15 Release Line](V2_0_15_RELEASE.md). Production signing, notarization, store publication, physical-device QA, browser compatibility, and other external evidence must not be inferred from source compilation.

## Product and User Documentation

### [Getting Started](GETTING_STARTED.md)
Fast first-use orientation and project entry points.

### [Installation](INSTALLATION.md)
Installation options and requirements for the application and development environment.

### [User Guide](USER_GUIDE.md)
Screen-by-screen Android user guide covering the mature production feature set.

### [Feature Reference](FEATURES.md)
Implementation-aligned product capabilities and explicit current non-features.

### [Game Rules](GAME_RULES.md)
Classic 9×9 Sudoku rules and gameplay terminology.

### [Keyboard Shortcuts](KEYBOARD_SHORTCUTS.md)
Hardware-keyboard navigation/input reference and QA expectations.

### [FAQ](FAQ.md)
Common user and developer questions.

### [Troubleshooting](TROUBLESHOOTING.md)
Build, runtime, and debugging troubleshooting guidance.

## Architecture and Codebase

### [Architecture](ARCHITECTURE.md)
System-level boundaries between Android, shared engine/UI, persistence, teaching, and platform services.

### [Cross-Platform Development and Builds](CROSS_PLATFORM.md)
Authoritative platform matrix, KMP/Compose boundaries, Android shared host, Desktop/Web/iOS entry points, build commands, CI targets, and evidence limits.

### [Shared Cross-Platform Active-Game Persistence](SHARED_PERSISTENCE.md)
Versioned `SNG1` active-game snapshot encoding, fail-closed restore rules, common store interfaces, native Android/Desktop/Web/Apple adapters, Compose autosave behavior, compatibility policy, and runtime evidence boundaries.

### [Shared Cross-Platform User Settings](SHARED_SETTINGS.md)
Versioned `SNS1` settings encoding, portable settings state/store boundaries, native local adapters, localized theme behavior, compatibility rules, privacy limits, tests, and runtime evidence boundaries.

### [Shared Puzzle Exchange](SHARED_PUZZLE_EXCHANGE.md)
Shared SNP1 puzzle import/export, imported-session provenance, SNG2 persistence, validation rules, and cross-platform exchange boundaries.

### [Project Structure](PROJECT_STRUCTURE.md)
Repository tree, modules, packages, source/test locations, workflows, and change-placement rules.

### [Repository File Reference and Documentation Coverage](REPOSITORY_FILE_REFERENCE.md)
Path ownership model for every tracked repository area and the fail-closed documentation-coverage contract.

### [Sudoku Engine](SUDOKU_ENGINE.md)
Domain reference for board, solver, generator, difficulty, teaching traces, hints, practice, puzzle codes, and validated puzzle exchange.

### [Puzzle Generation](PUZZLE_GENERATION.md)
Generation, uniqueness, determinism, and calibration behavior.

### [Difficulty System](DIFFICULTY_SYSTEM.md)
Seven difficulty targets and calibration principles.

### [Learning and Advanced Hints](LEARNING_AND_HINTS.md)
Structured teaching evidence, supported logical techniques, practice architecture, localization, accessibility, and tests.

### [Glossary](GLOSSARY.md)
Canonical Sudoku, engine, gameplay, persistence, platform, release, and repository terminology.

## Data, Persistence, Privacy, and Security

### [Shared Cross-Platform Active-Game Persistence](SHARED_PERSISTENCE.md)
Portable active-game snapshot/storage contract and per-platform local adapter boundaries for the shared UI.

### [Shared Cross-Platform User Settings](SHARED_SETTINGS.md)
Portable local user-settings format/state/storage contract and target adapter boundaries.

### [Shared Puzzle Exchange](SHARED_PUZZLE_EXCHANGE.md)
Portable puzzle-code exchange, imported-session provenance, SNG2 restore behavior, and validation boundaries.

### [Data Storage](DATA_STORAGE.md)
DataStore/Room responsibilities, reset behavior, migrations, and local-data integrity.

### [Data Formats](DATA_FORMATS.md)
Compatibility reference for board strings, puzzle/backup formats, DataStore, active-game serialization, and Room schemas.

### [Backup / Restore](BACKUP_RESTORE.md)
User/developer backup and restore behavior.

### [v0.7 Transfer/Backup Technical Record](TRANSFER_BACKUP_V07.md)
Milestone-specific transfer/backup technical history.

### [Privacy](PRIVACY.md)
Current open-source application privacy behavior.

### [Security](SECURITY.md)
Technical security guidance inside the documentation library.

### [Root Security Policy](../SECURITY.md)
Authoritative vulnerability-reporting and repository security policy.

## UI, Design, Accessibility, and Localization

### [UI / UX](UI_UX.md)
Screen and interaction design principles.

### [Design System](DESIGN_SYSTEM.md)
Material 3, theme, and visual conventions.

### [Accessibility](ACCESSIBILITY.md)
TalkBack semantics, font scaling, contrast, motion, keyboard, and release accessibility checks.

### [Localization](LOCALIZATION.md)
English/Hindi resource rules, translation parity, placeholders, and localized accessibility/teaching content.

### [Keyboard Shortcuts](KEYBOARD_SHORTCUTS.md)
Hardware-keyboard input behavior and accessibility expectations.

## Development, Build, and CI

### [Development Setup](DEVELOPMENT_SETUP.md)
Contributor environment/tooling setup.

### [Building](BUILDING.md)
JDK/Gradle/AGP/Kotlin/SDK requirements, Android APK/AAB tasks, release outputs, signing integration, and build locations.

### [Cross-Platform Development and Builds](CROSS_PLATFORM.md)
Commands for shared engine/UI, Android, Desktop, Web/Wasm, and iOS frameworks plus platform-specific evidence boundaries.

### [Testing](TESTING.md)
Engine, shared-state, JVM, Android, instrumentation, release-tooling, Macrobenchmark, lint, and manual QA strategy.

### [Quality Gates](QUALITY_GATES.md)
Compact map of deterministic repository/build gates and real-world evidence boundaries.

### [Repository Consistency Guards](REPOSITORY_GUARDS.md)
Deterministic local/CI guards for documentation, release identity, secrets, translations, and release-output integrity.

### [CI/CD](CI_CD.md)
GitHub Actions quality gates, exact-head verification, release evidence, and protected validation paths.

### [Exact-Head Verification](EXACT_HEAD_VERIFICATION.md)
Rule that workflow evidence applies only to the exact commit tested.

### [Performance](PERFORMANCE.md)
Main-thread/ANR rules, algorithm/data/UI performance guidance, and measurement policy.

### [Performance Benchmarking and Evidence](PERFORMANCE_BENCHMARKING.md)
Macrobenchmark architecture, startup/frame tests, physical-device execution, retained evidence, and CI boundaries.

## Contribution and Maintenance

### [Contributing Guide](CONTRIBUTING_GUIDE.md)
Detailed contributor workflow complementing root `CONTRIBUTING.md`.

### [Maintainer Guide](MAINTAINER_GUIDE.md)
Branch/PR discipline, issue triage, review, dependency, security, localization, accessibility, documentation, and release maintenance.

### [GitHub Repository Settings](GITHUB_REPOSITORY_SETTINGS.md)
Recommended branch protection/rulesets, required checks, release environments, merge/review policy, Actions permissions, and security settings.

### [Documentation Standards](DOCUMENTATION_STANDARDS.md)
Rules for implementation status, verification claims, tracked-file ownership, links, style, and release-documentation audits.

### [Changelog Guide](CHANGELOG_GUIDE.md)
How to maintain release-oriented changelog content.

### [Root Contributing Policy](../CONTRIBUTING.md)
Primary repository contribution policy.

### [Code of Conduct](../CODE_OF_CONDUCT.md)
Community behavior standards.

## Quality Assurance and Historical Evidence

### [QA Matrix](QA_MATRIX.md)
General QA matrix for supported behavior and environments.

### [Release QA](RELEASE_QA.md)
Evidence-oriented release QA matrix; manual rows require real checks.

### [v0.9 Hardening Audit](V09_HARDENING_AUDIT.md)
Historical source-audit findings and fixes for performance, accessibility, localization, persistence, backup, security, and release gates.

### [Post-RC Validation Evidence](POST_RC_VALIDATION_EVIDENCE.md)
Historical exact-head PR #28 Android CI/API-35 evidence and repository-guard validation.

### [Historical PR #28 Branch Freeze](BRANCH_FREEZE.md)
Historical record of the PR #28 freeze rule and verified final head/workflow pair.

### [v1.0 RC Evidence Worksheet](V1_RELEASE_CANDIDATE.md)
Historical v1.0 RC real-target evidence worksheet.

### [v1.0 Release Evidence Ledger](V1_RELEASE_EVIDENCE.md)
Historical ledger for verified v1 RC/post-RC work and pending production evidence at that checkpoint.

### [v1.0 Release Notes Source](V1_RELEASE_NOTES.md)
Historical canonical v1 stable-release notes source.

### [v1.0 RC Preparation](V1_RELEASE_PREP.md)
Historical v1.0 RC handoff, candidate metadata, artifact/signing pipeline, automated gates, and manual evidence boundaries.

## Current Release and Planning

### [v0.1.2 Release Line](V0_1_2_RELEASE.md)
Current dedicated v0.1.2 source/version/release-preparation authority for `versionCode 2016` / `versionName 0.1.2` and its evidence boundary.

### [2.0.14 Release Line](V2_0_14_RELEASE.md)
Later 2.x source/version/release authority for `versionCode 2014` / `versionName 2.0.14`, portable settings persistence, keyboard parity, and its evidence boundary.

### [2.0.15 Release Line](V2_0_15_RELEASE.md)
Later 2.x source/version/release-preparation authority for shared puzzle exchange and imported-session provenance.

### [Production Signing](PRODUCTION_SIGNING.md)
Four-variable secret-backed Android signing configuration, fail-closed rules, and certificate validation.

### [Production Release Validation Workflow](PRODUCTION_RELEASE_VALIDATION.md)
Protected manual workflow for signed APK/AAB validation and certificate-bound evidence.

### [Play Store Release Preparation](PLAY_STORE_RELEASE.md)
Store identity/listing draft, asset checklist, privacy/data facts, release artifact checklist, and rollout discipline.

### [Releasing](RELEASING.md)
End-to-end release process from freeze through verification, signing, QA, exact-head evidence, tag, GitHub Release, and store submission.

### [Release Checklist](RELEASE_CHECKLIST.md)
Concrete release checklist.

### [Documentation Roadmap](ROADMAP.md)
Documentation-specific planning history.

### [Repository Roadmap](../ROADMAP.md)
Authoritative current product milestone roadmap.

### [Changelog](../CHANGELOG.md)
Release history and current unreleased work.

### [What Changed](../what_changed.md)
Current implementation, verification, and handoff ledger.

### [Archived Implementation Ledger](archive/what_changed_through_2026-08-19.md)
Immutable detailed history through 2026-08-19.

## Repository Policies and Metadata

- [Main README](../README.md)
- [License](../LICENSE)
- [Authors](../AUTHORS.md)
- [Contributing](../CONTRIBUTING.md)
- [Code of Conduct](../CODE_OF_CONDUCT.md)
- [Security Policy](../SECURITY.md)
- [Support](../SUPPORT.md)
- [Third-Party Notices](../THIRD_PARTY_NOTICES.md)
- [Changelog](../CHANGELOG.md)
- [Roadmap](../ROADMAP.md)
- [Implementation Log](../what_changed.md)
- [CODEOWNERS](../.github/CODEOWNERS)
- [Funding metadata](../.github/FUNDING.yml)
- [Generated release-note config](../.github/release.yml)

## Historical vs Current Authority

`V0_1_2_RELEASE.md` is the current dedicated v0.1.2 preparation authority for this branch. `V2_0_14_RELEASE.md` and `V2_0_15_RELEASE.md` describe later 2.x development lines and must not be treated as evidence that v0.1.2 was publicly shipped. `CROSS_PLATFORM.md` is the current authority for repository-supported non-Android build targets and shared-platform architecture. `SHARED_SETTINGS.md` is the authority for the portable `SNS1` settings contract, and `SHARED_PUZZLE_EXCHANGE.md` is the authority for shared puzzle exchange and imported-session provenance.

## Documentation Maintenance Rule

When code changes, update the narrowest relevant guide in the same work. Every tracked file must retain documentation ownership, and every tracked `docs/*.md` guide must remain discoverable from this index.

Run before merging structural documentation/repository changes:

```bash
python -m unittest scripts.tests.test_verify_documentation_coverage
python scripts/verify_documentation_coverage.py
python scripts/verify_documentation_links.py
```

The root `README.md` should remain a concise landing page; this file is the detailed navigation hub.

## Project Links

- Repository: https://github.com/sanskarIN/SudokuNova
- Developer: https://www.github.com/sanskarIN
- Buy Me a Coffee: https://buymeacoffee.com/sanskarIN
- Support: `supportramsandesh@gmail.com`
- Business: `sanskarin@outlook.in`
- Business: `sanskarin.business@gmail.com`

**SudokuNova — Think. Solve. Master the Grid.**  
**Made by the Sanskar**
