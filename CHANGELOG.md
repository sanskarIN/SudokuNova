# Changelog

All notable SudokuNova changes are documented here. The project follows Semantic Versioning where practical.

## [Unreleased]

### Added
- Android application foundation using Kotlin, Jetpack Compose, and Material 3.
- Platform-independent Sudoku engine module.
- Immutable 9×9 board model with validation, conflicts, candidates, and serialization.
- Sudoku solver with solution counting and uniqueness checks.
- Seeded puzzle generator with seven difficulty targets.
- Naked Single, Hidden Single, and reveal-fallback hints.
- Responsive game board, number pad, notes, eraser, undo, redo, hint, pause, timer, restart, mistake handling, and progress display.
- DataStore-backed preferences, active-game persistence, resume, and local statistics.
- Daily Challenge deterministic seed flow.
- Custom puzzle editor with contradiction, solvability, and uniqueness validation.
- Basic achievements and Sudoku learning center.
- Light, dark, system, and dynamic Material You theme support.
- Original launcher, monochrome, and splash vector assets.
- GitHub Actions build/test/lint automation.
- Open-source repository policies, support documentation, and contributor guidance.

### Fixed
- Replaced the invalid Kotlin source namespace beginning with the reserved `in` keyword by `com.sanskar.sudokunova`, while preserving Android application ID `in.sanskar.sudokunova`.
- Corrected statistics-reset handling for heterogeneous DataStore preference keys.
- Corrected custom-puzzle solution preview so it does not overwrite the original playable clues.
- Corrected theme-label string transformation for Kotlin compilation.
- Made Gradle Wrapper bootstrap commits rebase before pushing to avoid concurrent branch update rejection.

### Security
- Added a minimal-permission Android manifest and responsible vulnerability disclosure policy.
- Added Android backup/data extraction policy and secret exclusions in `.gitignore`.

### Accessibility
- Added semantic Sudoku cell descriptions and adaptive board/layout foundations.
- Added high-contrast and reduced-motion preference foundations.

### Documentation
- Added README, contributing guide, code of conduct, security policy, support guide, authorship information, and third-party notices.

## [0.1.0] - Planned

The first tagged development milestone will formalize the project foundation after CI is green and the v0.1 documentation/quality gates are complete.
