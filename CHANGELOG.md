# Changelog

All notable SudokuNova changes are documented here. The project follows Semantic Versioning where practical.

## [Unreleased]

### v0.9 — Release Hardening

Focused development is tracked in issue #23. Planned work is limited to release hardening: regression gaps, accessibility, performance, security/privacy, dependency/license review, release APK/AAB verification, device QA, lifecycle/crash hardening, and documentation accuracy.

No v0.9 item should be moved into the completed release history until its implementation and required verification evidence exist.

## [0.8.0] - 2026-08-18

### Learning and Advanced Hints

#### Added
- Platform-independent `TeachingStep` evidence model with technique, source cells/unit, target cells, exact candidate eliminations, and optional placement.
- Deterministic `TeachingStepFinder` candidate-state pipeline.
- Structured evidence for Naked Single, Hidden Single, Naked Pair, Pointing Pair/Triple, Box-Line Reduction, Hidden Pair, Naked Triple, Hidden Triple, and X-Wing.
- Row-oriented and column-oriented X-Wing elimination detection.
- Deterministic teaching traces reused by the logical solver and hint engine.
- Explicit Reveal fallback kept separate from supported logical teaching evidence.
- In-game hint source/target/elimination/placement highlighting.
- Accessibility descriptions for teaching sources, targets, placement values, and exact eliminated candidates.
- English and Hindi v0.8 learning/hint resources with translation-parity enforcement.
- Deterministic offline practice catalog covering every supported logical technique.
- Interactive Learn practice answer states with correct/incorrect feedback.
- Local per-technique lesson/practice progress stored in Preferences DataStore.
- Per-technique and overall mastery presentation plus safe learning-progress reset.
- Hidden Pair, Naked Triple, Hidden Triple, X-Wing, trace-safety, practice-catalog, learning-progress, and Compose Learn/practice tests.
- `docs/LEARNING_AND_HINTS.md` with the complete teaching/hint/practice architecture and verification rules.

#### Changed
- `LogicalSolver` now consumes the same teaching-step pipeline used by hints instead of maintaining separate candidate logic.
- `HintEngine` no longer owns player-facing English explanation strings; Android resources render localized names and explanations.
- Multi-step hints report the hardest logical technique in the chain while still applying only the final proven placement.
- Learn is now an interactive learning center instead of a read-only lesson list.
- Android version metadata advanced to `versionCode 800` / `versionName 0.8.0`.

#### Safety / Correctness
- Advanced candidate-state probes may only use candidates that are legal subsets for the current Sudoku board.
- Generated-puzzle corpus tests verify that teaching placements agree with the unique solution and candidate eliminations never remove the solved value.
- Invalid or complete boards fail closed in hint/teaching-step entry points.
- Practice progress cannot modify Sudoku truth, solver behavior, game history, or puzzle generation.
- The new Android learning-model tests use the app module's configured JUnit4 test stack.
- Connected Learn/practice smoke coverage uses stable semantic test tags rather than relying on off-screen LazyColumn text discovery.

#### Verification
- Final verified PR head: `b63c8019cfc2b6f606247af1543586a7ede1b3df`.
- Standard Android CI run `32121249242`: green.
- API-35 connected instrumentation run `32121249202`: green.
- PR #22 merged as `f07e6496ff5de5bfdfb7676b527a31f71f8b912c`.
- Issue #21 closed as completed.

### v0.7 — Safe Sharing, Import, Export, and Backup
- Added versioned puzzle codes with checksum/bounds validation.
- Added strict import parsing and size limits.
- Added safe text/clipboard/share flows and Android document-picker file transfer.
- Added result sharing/export support.
- Added versioned local backup and duplicate-safe restore behavior.
- Added English/Hindi transfer resources, security documentation, and connected regression coverage.

### v0.6 — Challenges
- Added challenge archive flows, deterministic challenge identity, weekly challenges, challenge records, and challenge UI/tests.

### v0.5 — Player Data and History
- Added Room-backed history/saved-puzzle foundations, player records, replay/browse flows, and local data controls.

### v0.4 — UX, Accessibility, and Localization
- Added English/Hindi resource-backed localization for core Home, Game, Settings, Custom Puzzle, Statistics, About, Learn, difficulty, theme, and Sudoku accessibility text.
- Promoted translation-resource parity verification into CI.
- Added visible High Contrast board behavior with stronger grid lines, state borders, peer distinction, and note emphasis.
- Added adaptive-layout and accessibility foundations.

### v0.3 — Difficulty and Engine Hardening
- Added logical/complexity difficulty evidence, deterministic calibration corpus, generation benchmarks, and richer engine regression coverage.

### v0.2 — Gameplay Hardening
- Expanded gameplay regression tests, input modes, lifecycle restoration, hardware-keyboard controls, and settings-backed interaction behavior.

### v0.1 — Foundation
- Android application foundation using Kotlin, Jetpack Compose, and Material 3.
- Platform-independent Sudoku engine module.
- Immutable 9×9 board model with validation, conflicts, candidates, and serialization.
- Sudoku solver with solution counting and uniqueness checks.
- Seeded puzzle generator with seven difficulty targets.
- Responsive game board, number pad, notes, eraser, undo, redo, hint, pause, timer, restart, mistake handling, and progress display.
- DataStore-backed preferences, active-game persistence, resume, and local statistics.
- Daily Challenge deterministic seed flow.
- Custom puzzle editor with contradiction, solvability, and uniqueness validation.
- Basic achievements and Sudoku learning center.
- Light, dark, system, and dynamic Material You theme support.
- Original launcher, monochrome, and splash vector assets.
- GitHub Actions build/test/lint automation.
- Open-source repository policies, support documentation, and contributor guidance.

## Fixed over the development line

- Replaced the invalid Kotlin source namespace beginning with the reserved `in` keyword by `com.sanskar.sudokunova`, while preserving Android application ID `in.sanskar.sudokunova`.
- Corrected statistics-reset handling for heterogeneous DataStore preference keys.
- Corrected custom-puzzle solution preview so it does not overwrite the original playable clues.
- Corrected theme-label string transformation for Kotlin compilation.
- Hardened transfer/import/restore behavior against malformed or duplicate input.
- Corrected v0.8 Android learning test framework imports.
- Corrected v0.8 LazyColumn connected-test navigation.
- Corrected v0.8 multi-step hint technique identity.

## Security and privacy

- Minimal-permission Android manifest and responsible vulnerability disclosure policy.
- Android backup/data extraction policy and secret exclusions in `.gitignore`.
- No account required for core gameplay or learning progress.
- No cloud dependency for teaching, hints, practice, or local learning progress.

## Accessibility

- Semantic Sudoku cell descriptions and adaptive board/layout foundations.
- High-contrast and reduced-motion preference foundations.
- v0.8 teaching-evidence semantics for source, target, candidate elimination, and placement roles.
- v0.9 tracks the complete release accessibility audit.

## Documentation

- README, contributing guide, code of conduct, security policy, support guide, authorship information, third-party notices, build/release docs, transfer docs, accessibility docs, data-storage docs, and v0.8 learning/hint documentation.
