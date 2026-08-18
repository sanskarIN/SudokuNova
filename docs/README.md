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

### I am preparing v1.0

Start with:

1. [v1.0 Release Evidence](V1_RELEASE_EVIDENCE.md)
2. [Releasing](RELEASING.md)
3. [Release Checklist](RELEASE_CHECKLIST.md)
4. [Release QA](RELEASE_QA.md)
5. [v1.0 Release Notes](V1_RELEASE_NOTES.md)
6. [Store Listing Source](STORE_LISTING.md)
7. [QA Matrix](QA_MATRIX.md)
8. [CI/CD](CI_CD.md)
9. [Testing](TESTING.md)
10. [Building](BUILDING.md)
11. [Performance](PERFORMANCE.md)
12. [Accessibility](ACCESSIBILITY.md)
13. [Privacy](PRIVACY.md)
14. [`../SECURITY.md`](../SECURITY.md)
15. [`../THIRD_PARTY_NOTICES.md`](../THIRD_PARTY_NOTICES.md)
16. [`../CHANGELOG.md`](../CHANGELOG.md)
17. [`../what_changed.md`](../what_changed.md)

The v1.0 readiness branch uses `1.0.0-rc1` until required manual/signing/store evidence exists. Automated CI success alone is not a stable-production claim.

## Product and User Documentation

### [Getting Started](GETTING_STARTED.md)
Fast first-use orientation and project entry points.

### [Installation](INSTALLATION.md)
Installation options/requirements for the Android application and development context.

### [User Guide](USER_GUIDE.md)
Complete screen-by-screen guide for the current application.

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
Domain reference for board, solver, generator, difficulty, teaching, hints, practice and puzzle codes.

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
Authoritative vulnerability reporting and repository security policy.

## Android UI, Design and Accessibility

### [UI / UX](UI_UX.md)
Screen/interaction design principles.

### [Design System](DESIGN_SYSTEM.md)
Material 3/theme/visual design conventions.

### [Accessibility](ACCESSIBILITY.md)
TalkBack-oriented semantics, font scaling, contrast, motion, keyboard and release accessibility checks.

### [Localization](LOCALIZATION.md)
English/Hindi resource rules, translation parity, placeholders and localized accessibility/teaching content.

## Development and Build

### [Development Setup](DEVELOPMENT_SETUP.md)
Environment/tooling setup for contributors.

### [Building](BUILDING.md)
Debug/release APK, release AAB, R8 mapping, release-helper tests, artifact integrity/checksums, signing boundaries and output locations.

### [Testing](TESTING.md)
Complete engine/JVM/instrumentation/lint/release/manual QA strategy.

### [CI/CD](CI_CD.md)
GitHub Actions gates, exact-head verification, release-helper tests, artifact integrity/checksum evidence and failure triage.

### [Performance](PERFORMANCE.md)
Main-thread/ANR rules, solver/generator/hint/import/Room/DataStore/Compose performance guidance and measurement policy.

## Contribution and Maintenance

### [Contributing Guide](CONTRIBUTING_GUIDE.md)
Detailed contributor workflow complementing root `CONTRIBUTING.md`.

### [Maintainer Guide](MAINTAINER_GUIDE.md)
Branch/PR discipline, issue triage, review, dependency/security/localization/accessibility/documentation/release maintenance.

### [Documentation Standards](DOCUMENTATION_STANDARDS.md)
Rules for implementation status, verification claims, persistent format docs, privacy/security accuracy, links, style and release audits.

### [Changelog Guide](CHANGELOG_GUIDE.md)
How to maintain release-oriented changelog content.

### [Code of Conduct](../CODE_OF_CONDUCT.md)
Community behavior standards.

## Quality Assurance

### [Testing](TESTING.md)
Automated test layers and deterministic regression strategy.

### [QA Matrix](QA_MATRIX.md)
General QA matrix.

### [Release QA](RELEASE_QA.md)
Evidence-oriented release matrix. Manual rows must not be marked complete without real checks.

### [v0.9 Hardening Audit](V09_HARDENING_AUDIT.md)
Historical source-audit findings/fixes that established the v1.0 baseline.

### [v1.0 Release Evidence](V1_RELEASE_EVIDENCE.md)
Authoritative first-stable evidence ledger separating automated, manual, measured-performance, signing and store evidence.

## Release and Planning

### [Releasing](RELEASING.md)
End-to-end controlled release process including RC→stable promotion and signed-artifact verification.

### [Release Checklist](RELEASE_CHECKLIST.md)
Concrete release checklist.

### [v1.0 Release Notes](V1_RELEASE_NOTES.md)
Canonical public release-notes source with verification placeholders that must be filled from the exact stable release commit.

### [Store Listing Source](STORE_LISTING.md)
Truthful listing copy, screenshot capture plan, privacy preparation and publication rules.

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

Milestone-specific files such as `TRANSFER_BACKUP_V07.md` and `V09_HARDENING_AUDIT.md` preserve implementation history. Use the current general references plus `V1_RELEASE_EVIDENCE.md` for the active v1.0 readiness contract.

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
