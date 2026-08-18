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
3. [Architecture](ARCHITECTURE.md)
4. [Building](BUILDING.md)
5. [Testing](TESTING.md)
6. [CI/CD](CI_CD.md)
7. [Contributing Guide](CONTRIBUTING_GUIDE.md)
8. [`../CONTRIBUTING.md`](../CONTRIBUTING.md)

### I am changing Sudoku logic

Read:

1. [Sudoku Engine](SUDOKU_ENGINE.md)
2. [Puzzle Generation](PUZZLE_GENERATION.md)
3. [Difficulty System](DIFFICULTY_SYSTEM.md)
4. [Learning and Advanced Hints](LEARNING_AND_HINTS.md)
5. [Testing](TESTING.md)
6. [Performance](PERFORMANCE.md)
7. [Glossary](GLOSSARY.md)

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

### I am preparing a release

Read:

1. [Releasing](RELEASING.md)
2. [Release Checklist](RELEASE_CHECKLIST.md)
3. [Release QA](RELEASE_QA.md)
4. [QA Matrix](QA_MATRIX.md)
5. [CI/CD](CI_CD.md)
6. [Testing](TESTING.md)
7. [Building](BUILDING.md)
8. [Performance](PERFORMANCE.md)
9. [Accessibility](ACCESSIBILITY.md)
10. [Privacy](PRIVACY.md)
11. [`../SECURITY.md`](../SECURITY.md)
12. [`../THIRD_PARTY_NOTICES.md`](../THIRD_PARTY_NOTICES.md)
13. [`../CHANGELOG.md`](../CHANGELOG.md)
14. [`../what_changed.md`](../what_changed.md)

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

Repository tree, packages, major source files, test locations, Room schemas, workflows and change-placement rules.

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

Debug/release APK, release AAB, R8 mapping, lint/tests, signing boundaries and output locations.

### [Testing](TESTING.md)

Complete engine/JVM/instrumentation/lint/release/manual QA strategy aligned with the cumulative v0.9 branch.

### [CI/CD](CI_CD.md)

GitHub Actions quality gates, exact-head verification, security/translation scripts, release artifacts and failure triage.

### [Performance](PERFORMANCE.md)

Main-thread/ANR rules, solver/generator/hint/import/Room/DataStore/Compose performance guidance and measurement policy.

## Contribution and Maintenance

### [Contributing Guide](CONTRIBUTING_GUIDE.md)

Detailed contributor workflow complementing root `CONTRIBUTING.md`.

### [Maintainer Guide](MAINTAINER_GUIDE.md)

Branch/PR discipline, issue triage, review checklists, dependency/security/localization/accessibility/documentation/release maintenance.

### [Documentation Standards](DOCUMENTATION_STANDARDS.md)

Rules for implementation status, verification claims, persistent format docs, privacy/security accuracy, links, style and release documentation audits.

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

v0.9 evidence-oriented release-hardening matrix. Manual rows must not be marked complete without real checks.

### [Accessibility](ACCESSIBILITY.md)

Accessibility-specific QA requirements.

### [Performance](PERFORMANCE.md)

Performance/ANR review and measurement guidance.

## Release and Planning

### [Releasing](RELEASING.md)

End-to-end release process from scope freeze through exact-head verification, signing, manual QA, tagging, GitHub release, store submission and fix-forward handling.

### [Release Checklist](RELEASE_CHECKLIST.md)

Concrete release checklist.

### [Release QA](RELEASE_QA.md)

Detailed release evidence matrix.

### [Documentation Roadmap](ROADMAP.md)

Historical/documentation-specific planning file.

### [Repository Roadmap](../ROADMAP.md)

Authoritative current product milestone roadmap.

### [Changelog](../CHANGELOG.md)

Release history and current unreleased work.

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

## Historical vs Current Documents

Some files intentionally preserve milestone-specific detail, such as `TRANSFER_BACKUP_V07.md`. Use current general references (`FEATURES.md`, `USER_GUIDE.md`, `SUDOKU_ENGINE.md`, `DATA_FORMATS.md`, `TESTING.md`, `RELEASING.md`) for the current contract, and milestone-specific pages when investigating historical decisions.

## Documentation Maintenance Rule

When code changes, update the narrowest relevant guide in the same work. See [Documentation Standards](DOCUMENTATION_STANDARDS.md).

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
