# SudokuNova Documentation

This directory is the complete maintained documentation hub for SudokuNova.

Documentation follows one rule: a feature is described as implemented only when corresponding source/configuration exists in the repository. Future work is labeled planned, and verification claims are recorded only after the relevant automated/manual check actually occurred.

## Choose Your Path

### I want to use SudokuNova

Start with:

1. [Getting Started](GETTING_STARTED.md)
2. [Installation](INSTALLATION.md)
3. [User Guide](USER_GUIDE.md)
4. [Feature Reference](FEATURES.md)
5. [Game Rules](GAME_RULES.md)
6. [FAQ](FAQ.md)
7. [Troubleshooting](TROUBLESHOOTING.md)

### I want to build or contribute

Start with:

1. [Development Setup](DEVELOPMENT_SETUP.md)
2. [Project Structure](PROJECT_STRUCTURE.md)
3. [Repository File Reference and Documentation Coverage](REPOSITORY_FILE_REFERENCE.md)
4. [Architecture](ARCHITECTURE.md)
5. [Building](BUILDING.md)
6. [Testing](TESTING.md)
7. [Repository Consistency Guards](REPOSITORY_GUARDS.md)
8. [CI/CD](CI_CD.md)
9. [Performance Benchmarking and Evidence](PERFORMANCE_BENCHMARKING.md)
10. [Contributing Guide](CONTRIBUTING_GUIDE.md)
11. [`../CONTRIBUTING.md`](../CONTRIBUTING.md)

### I am changing Sudoku logic

Read:

1. [Sudoku Engine](SUDOKU_ENGINE.md)
2. [Puzzle Generation](PUZZLE_GENERATION.md)
3. [Difficulty System](DIFFICULTY_SYSTEM.md)
4. [Learning and Advanced Hints](LEARNING_AND_HINTS.md)
5. [Testing](TESTING.md)
6. [Performance](PERFORMANCE.md)
7. [Performance Benchmarking and Evidence](PERFORMANCE_BENCHMARKING.md)
8. [Glossary](GLOSSARY.md)

### I am changing persistence/import/backup

Read:

1. [Data Storage](DATA_STORAGE.md)
2. [Data Formats](DATA_FORMATS.md)
3. [Backup / Restore](BACKUP_RESTORE.md)
4. [v0.7 Transfer/Backup Technical Record](TRANSFER_BACKUP_V07.md)
5. [Privacy](PRIVACY.md)
6. [Security](SECURITY.md)
7. [`../SECURITY.md`](../SECURITY.md)
8. [Testing](TESTING.md)

### I am preparing v1.0 or another release

Read in this order:

1. [v1.0 RC Preparation](V1_RELEASE_PREP.md)
2. [Post-RC Validation Evidence](POST_RC_VALIDATION_EVIDENCE.md)
3. [Building](BUILDING.md)
4. [Production Signing](PRODUCTION_SIGNING.md)
5. [Production Release Validation Workflow](PRODUCTION_RELEASE_VALIDATION.md)
6. [Performance Benchmarking and Evidence](PERFORMANCE_BENCHMARKING.md)
7. [v1.0 RC Evidence Worksheet](V1_RELEASE_CANDIDATE.md)
8. [v1.0 Release Evidence Ledger](V1_RELEASE_EVIDENCE.md)
9. [Play Store Release Preparation](PLAY_STORE_RELEASE.md)
10. [GitHub Repository Settings](GITHUB_REPOSITORY_SETTINGS.md)
11. [Releasing](RELEASING.md)
12. [Release Checklist](RELEASE_CHECKLIST.md)
13. [Release QA](RELEASE_QA.md)
14. [QA Matrix](QA_MATRIX.md)
15. [Repository File Reference and Documentation Coverage](REPOSITORY_FILE_REFERENCE.md)
16. [Repository Consistency Guards](REPOSITORY_GUARDS.md)
17. [CI/CD](CI_CD.md)
18. [Testing](TESTING.md)
19. [Performance](PERFORMANCE.md)
20. [Accessibility](ACCESSIBILITY.md)
21. [Privacy](PRIVACY.md)
22. [`../SECURITY.md`](../SECURITY.md)
23. [`../THIRD_PARTY_NOTICES.md`](../THIRD_PARTY_NOTICES.md)
24. [`../CHANGELOG.md`](../CHANGELOG.md)
25. [`../ROADMAP.md`](../ROADMAP.md)
26. [`../what_changed.md`](../what_changed.md)

For historical source-hardening evidence, also read [v0.9 Hardening Audit](V09_HARDENING_AUDIT.md).

## Product and User Documentation

### [Getting Started](GETTING_STARTED.md)

Fast first-use orientation and project entry points.

### [Installation](INSTALLATION.md)

Installation options/requirements for the Android application and development context.

### [User Guide](USER_GUIDE.md)

Complete screen-by-screen guide covering Home, normal games, input modes, Notes, undo/redo, pause, hints, challenges, custom puzzles, History, Saved Puzzles, Learn, Statistics, Settings, transfer/backup, About and privacy expectations.

### [Feature Reference](FEATURES.md)

Implementation-aligned list of product capabilities and explicit current non-features.

### [Game Rules](GAME_RULES.md)

Classic 9×9 Sudoku rules and gameplay terminology.

### [Keyboard Shortcuts](KEYBOARD_SHORTCUTS.md)

Hardware-keyboard navigation/input reference and QA expectations.

### [FAQ](FAQ.md)

Common user/developer questions.

### [Troubleshooting](TROUBLESHOOTING.md)

Build/runtime/debugging troubleshooting guidance.

## Architecture and Codebase

### [Architecture](ARCHITECTURE.md)

System-level boundaries between the Android app, engine, persistence, teaching pipeline and platform services.

### [Project Structure](PROJECT_STRUCTURE.md)

Repository tree, three Gradle modules, packages, major source files, test locations, Room schemas, workflows and change-placement rules.

### [Repository File Reference and Documentation Coverage](REPOSITORY_FILE_REFERENCE.md)

Path-by-path ownership model for every Git-tracked file family, with canonical documentation, change rules, audit commands, and the fail-closed `git ls-files` coverage contract.

### [Sudoku Engine](SUDOKU_ENGINE.md)

Complete domain reference for `SudokuBoard`, solver, generator, difficulty, teaching steps/traces, logical solver, hints, Reveal, practice and `SNP1`.

### [Puzzle Generation](PUZZLE_GENERATION.md)

Generation/uniqueness/determinism details.

### [Difficulty System](DIFFICULTY_SYSTEM.md)

Seven difficulty targets and calibration principles.

### [Learning and Advanced Hints](LEARNING_AND_HINTS.md)

Structured teaching evidence, supported logical techniques, Learn/practice architecture, localization/accessibility presentation and safety tests.

### [Glossary](GLOSSARY.md)

Canonical Sudoku, engine, gameplay, persistence, Android, release and repository terminology.

## Data, Persistence and Transfer

### [Data Storage](DATA_STORAGE.md)

DataStore/Room responsibilities, reset behavior, migrations and local-data integrity.

### [Data Formats](DATA_FORMATS.md)

Compatibility reference for board strings, `SNP1`, `SNB1`, DataStore, active-game serialization, Room schemas and Android backup boundaries.

### [Backup / Restore](BACKUP_RESTORE.md)

User/developer backup and restore behavior.

### [v0.7 Transfer/Backup Technical Record](TRANSFER_BACKUP_V07.md)

Milestone-specific technical record retained for implementation history.

### [Privacy](PRIVACY.md)

Current open-source base application privacy policy/behavior.

### [Security](SECURITY.md)

Technical security guidance within docs.

### [Root Security Policy](../SECURITY.md)

Authoritative private vulnerability reporting and repository security policy.

## Android UI, Design and Accessibility

### [UI / UX](UI_UX.md)

Screen/interaction design principles.

### [Design System](DESIGN_SYSTEM.md)

Material 3/theme/visual design conventions.

### [Accessibility](ACCESSIBILITY.md)

TalkBack-oriented semantics, font scaling, contrast, motion, keyboard and release accessibility checks.

### [Localization](LOCALIZATION.md)

English/Hindi resource rules, translation parity, placeholders and localized accessibility/teaching content.

### [Keyboard Shortcuts](KEYBOARD_SHORTCUTS.md)

Hardware keyboard input behavior and accessibility expectations.

## Development and Build

### [Development Setup](DEVELOPMENT_SETUP.md)

Environment/tooling setup for contributors.

### [Building](BUILDING.md)

Current JDK/Gradle/AGP/Kotlin/SDK requirements, debug/release APK, release AAB, R8 mapping, benchmark build/harness, secret-backed signing, release artifact verifier, SHA-256 evidence, lint/tests and output locations.

### [Testing](TESTING.md)

Complete engine/JVM/instrumentation/release-tooling/Macrobenchmark/lint/release/manual QA strategy. v1.0 RC adds artifact-verifier, package/certificate identity, signing fail-closed and benchmark-compilation checks on top of the verified v0.9 suites.

### [Repository Consistency Guards](REPOSITORY_GUARDS.md)

Deterministic local/CI guards for documentation links, complete tracked-file documentation ownership, release source/workflow identity, repository security, translation parity, and release-output validation.

### [CI/CD](CI_CD.md)

GitHub Actions quality gates, exact-head verification, repository guards, Macrobenchmark compilation, unsigned release evidence, and the separate protected signed-release validation path.

### [Performance](PERFORMANCE.md)

Main-thread/ANR rules, solver/generator/hint/import/Room/DataStore/Compose performance guidance and measurement policy.

### [Performance Benchmarking and Evidence](PERFORMANCE_BENCHMARKING.md)

Release-like Macrobenchmark architecture, ProfileInstaller boundary, cold/warm startup and frame benchmarks, physical-device execution, retained evidence, comparison discipline, CI boundary and Baseline Profile non-claim.

## Contribution and Maintenance

### [Contributing Guide](CONTRIBUTING_GUIDE.md)

Detailed contributor workflow complementing root `CONTRIBUTING.md`.

### [Maintainer Guide](MAINTAINER_GUIDE.md)

Branch/PR discipline, issue triage, review checklists, dependency/security/localization/accessibility/documentation/release maintenance.

### [GitHub Repository Settings](GITHUB_REPOSITORY_SETTINGS.md)

Recommended `main` protection/ruleset, required CI checks, protected release environment, merge/review policy, Actions permissions and security settings. This file distinguishes settings that must be enabled in GitHub administration from source-controlled configuration.

### [Documentation Standards](DOCUMENTATION_STANDARDS.md)

Rules for implementation status, verification claims, tracked-file documentation ownership, persistent format docs, privacy/security accuracy, links, style and release documentation audits.

### [Repository File Reference and Documentation Coverage](REPOSITORY_FILE_REFERENCE.md)

Complete repository ownership map. The accompanying verifier obtains every current tracked path from Git and fails when a path has no documented area.

### [Changelog Guide](CHANGELOG_GUIDE.md)

How to maintain release-oriented changelog content.

### [Root Contributing Policy](../CONTRIBUTING.md)

Primary repository contribution policy.

### [Code of Conduct](../CODE_OF_CONDUCT.md)

Community behavior standards.

## Quality Assurance

### [Testing](TESTING.md)

Automated test layers and deterministic regression strategy.

### [QA Matrix](QA_MATRIX.md)

General QA matrix.

### [Release QA](RELEASE_QA.md)

v0.9 evidence-oriented hardening matrix retained as a supporting release reference. Manual rows must not be marked complete without real checks.

### [v0.9 Hardening Audit](V09_HARDENING_AUDIT.md)

Concrete source-audit findings and fixes for main-thread work, stale async results, accessibility, localization, Room, transfer/backup, security, and release gates.

### [Post-RC Validation Evidence](POST_RC_VALIDATION_EVIDENCE.md)

Exact PR #28 head, Android CI/API-35 run IDs, artifact digests, Macrobenchmark compile-defect repair, repository-guard enforcement audit, merge SHA, and stable-release evidence boundary.

### [v1.0 RC Evidence Worksheet](V1_RELEASE_CANDIDATE.md)

Authoritative real-target worksheet for installation, gameplay, TalkBack, 200% font/adaptive layouts, contrast/motion, keyboard, process death, measured performance/ANR/memory, production signing, signed artifacts, store assets and final ship/no-ship decision.

### [v1.0 Release Evidence Ledger](V1_RELEASE_EVIDENCE.md)

Concise exact-evidence ledger separating verified RC1 repository evidence, post-RC validation/performance tooling, and still-pending signed, physical-device, administrative and store evidence.

### [Accessibility](ACCESSIBILITY.md)

Accessibility-specific QA requirements.

### [Performance](PERFORMANCE.md)

Performance/ANR review and measurement guidance.

### [Performance Benchmarking and Evidence](PERFORMANCE_BENCHMARKING.md)

Authoritative source for representative physical-device startup/frame measurement and retained Macrobenchmark evidence.

## Release and Planning

### [v1.0 RC Preparation](V1_RELEASE_PREP.md)

Verified repository-side v1.0 RC handoff, candidate metadata, artifact/signing pipeline, automated gates, manual evidence boundaries and stable-promotion rules.

### [Post-RC Validation Evidence](POST_RC_VALIDATION_EVIDENCE.md)

Verified exact-head repository evidence for PR #28 and its merge, with all production/manual/admin/store boundaries retained.

### [Production Signing](PRODUCTION_SIGNING.md)

Four-variable secret-backed signing configuration, fail-closed rules, certificate-bound artifact verification and secure release-environment guidance.

### [Production Release Validation Workflow](PRODUCTION_RELEASE_VALIDATION.md)

Protected manual GitHub Actions workflow setup, required environment secrets/restrictions, signed APK/AAB validation, expected certificate identity checks, evidence artifacts and clear boundaries on what the workflow cannot prove.

### [Play Store Release Preparation](PLAY_STORE_RELEASE.md)

Store identity/listing draft, asset checklist, project privacy/data facts, release artifact checklist, draft stable notes and rollout discipline. Current store requirements must still be reviewed at actual publication time.

### [GitHub Repository Settings](GITHUB_REPOSITORY_SETTINGS.md)

Repository-admin settings that cannot be guaranteed merely by committed source files.

### [Releasing](RELEASING.md)

End-to-end RC/stable process from scope freeze through package/version/artifact verification, production signing, certificate identity, physical/manual QA and performance evidence, exact-head verification, stable promotion, tag, GitHub Release, store submission and fix-forward handling.

### [Release Checklist](RELEASE_CHECKLIST.md)

Concrete general release checklist.

### [Release QA](RELEASE_QA.md)

Detailed supporting release evidence matrix.

### [Documentation Roadmap](ROADMAP.md)

Historical/documentation-specific planning file.

### [Repository Roadmap](../ROADMAP.md)

Authoritative current product milestone roadmap.

### [Changelog](../CHANGELOG.md)

Release history and current unreleased RC work.

### [What Changed](../what_changed.md)

Detailed implementation/verification/handoff history.

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
- [Repository File Reference](REPOSITORY_FILE_REFERENCE.md)
- [Repository Consistency Guards](REPOSITORY_GUARDS.md)
- [Post-RC Validation Evidence](POST_RC_VALIDATION_EVIDENCE.md)
- [CODEOWNERS](../.github/CODEOWNERS)
- [Funding metadata](../.github/FUNDING.yml)
- [Generated release-note config](../.github/release.yml)

## Historical vs Current Documents

Some files intentionally preserve milestone-specific detail, such as `TRANSFER_BACKUP_V07.md` and `V09_HARDENING_AUDIT.md`. Use current general references (`FEATURES.md`, `USER_GUIDE.md`, `SUDOKU_ENGINE.md`, `DATA_FORMATS.md`, `BUILDING.md`, `TESTING.md`, `REPOSITORY_FILE_REFERENCE.md`, `REPOSITORY_GUARDS.md`, `PERFORMANCE.md`, `PERFORMANCE_BENCHMARKING.md`, `V1_RELEASE_PREP.md`, `POST_RC_VALIDATION_EVIDENCE.md`, `PRODUCTION_SIGNING.md`, `PRODUCTION_RELEASE_VALIDATION.md`, `V1_RELEASE_CANDIDATE.md`, `V1_RELEASE_EVIDENCE.md`, `PLAY_STORE_RELEASE.md`, `RELEASING.md`) for the current contract, and milestone-specific pages when investigating historical decisions.

## Documentation Maintenance Rule

When code changes, update the narrowest relevant guide in the same work. See [Documentation Standards](DOCUMENTATION_STANDARDS.md).

Every tracked file must retain documentation ownership. Run:

```bash
python -m unittest scripts.tests.test_verify_documentation_coverage
python scripts/verify_documentation_coverage.py
python scripts/verify_documentation_links.py
```

before merging structural documentation/repository changes. See [Repository File Reference and Documentation Coverage](REPOSITORY_FILE_REFERENCE.md) and [Repository Consistency Guards](REPOSITORY_GUARDS.md).

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