# Changelog

All notable SudokuNova changes are documented here. The project follows Semantic Versioning where practical.

## [Unreleased]

### v0.9 — Release Hardening

Focused development is tracked in issue #23 and draft PR #25. v0.9 is limited to release hardening: regression gaps, accessibility, performance, security/privacy, dependency/license review, release APK/AAB verification, device QA, lifecycle/crash hardening, and documentation accuracy.

#### Added
- CI repository-security guard that rejects committed Android signing/private-key file types, known credential-config filenames, PEM private-key material, and obvious GitHub token patterns.
- Stable `scripts/verify_no_secrets.py` entry point for the repository security verifier used by CI and documentation.
- Direct JVM regression coverage for bounded backup-file reads, including UTF-8 decoding, empty/oversized rejection, exact-limit acceptance, and invalid-limit rejection.
- Release QA matrix in `docs/RELEASE_QA.md` covering automated gates, lifecycle, every major app area, accessibility, font/window sizes, performance smoke checks, security/privacy, artifacts, and store-screenshot readiness.
- Stable semantic test tags for individual game-board and Custom Puzzle editor cells.
- Connected Compose coverage for selected Sudoku-cell accessibility semantics on both the game board and Custom Puzzle editor.
- English/Hindi v0.9 resources for Custom Puzzle validation/solver statuses and typed game-load error states.
- Complete categorized documentation set covering end-user workflows, features, project structure, engine internals, data formats, testing, CI/CD, performance, maintenance, release operations, keyboard input, glossary, privacy, security, accessibility, and documentation standards.

#### Changed
- Android development metadata advanced to `versionCode 900` / `versionName 0.9.0` on the v0.9 branch.
- Standard Android CI now verifies both debug and release lint.
- Standard Android CI now assembles the minified/resource-shrunk release APK and release AAB.
- Successful CI release verification retains short-lived APK/AAB/R8 mapping outputs as build evidence.
- Sudoku game cells now expose selected state through Compose accessibility semantics in addition to the existing localized content description.
- Number-first digit selection and Notes mode now expose semantic selected state instead of relying only on color.
- Game text-action rows can horizontally scroll so localized/large-font labels do not collide.
- Custom Puzzle editor cells now expose localized row/column/value descriptions, conflict descriptions, selected semantics, and stable test tags.
- Custom Puzzle text actions are stacked at full width for large-font/localized layouts.
- Settings toggle rows now act as one merged switch target; trailing switches are presentation-only, preventing duplicate/ambiguous interaction semantics.
- Settings theme/input/mistake chip groups can horizontally scroll rather than overflow at larger text sizes.
- History filter, metric, and badge rows can horizontally scroll at larger text sizes, and empty text layout placeholders were removed.
- Learn technique Study/Practice actions are full-width stacked controls rather than constrained half-width buttons.
- Puzzle-code Copy/Share actions are full-width stacked controls.
- Backup & Transfer Copy/Share/Export/Import actions are full-width stacked controls.
- Challenge status is stacked below the challenge title/difficulty rather than competing for one row at large text sizes.
- Saved Puzzles and Challenges no longer use empty `Text("")` nodes as layout spacers.
- Home now uses the maintained credit resource instead of a hardcoded duplicate string.
- The in-app English/Hindi privacy summary now matches current DataStore + Room storage, local learning/history/saved/challenge records, and explicit sharing/import/export/backup behavior.
- Hint computation runs on `Dispatchers.Default` and discards results when the requested board is no longer current.
- Custom Puzzle uniqueness validation and solution preview now run on `Dispatchers.Default`, cancel superseded solver work, and discard stale-board results.
- Puzzle-code validation cancels superseded work and refuses to publish a result for input that changed while uniqueness analysis was running.
- Transfer text edits no longer clear an unrelated backup/restore busy state.
- Custom Puzzle ViewModel state now uses typed statuses rather than player-facing English prose; Compose resolves those statuses through locale resources.
- Game load/abandon errors now use typed state with a separate localized presentation mapping rather than exposing exception prose.
- Game completion summary now uses the maintained localized completion resource instead of concatenated English `mistake(s)` / `hint(s)` text.
- Room migration override naming now matches the Room API without changing schema behavior.
- `docs/BUILDING.md` now documents debug/release APK, AAB, R8 mapping, Windows/Unix verification commands, release signing boundaries, reproducibility evidence, and release-quality claim rules.
- `SECURITY.md` now documents Android permission/export rules, bounded/fail-closed backup expectations, signing/secret rules, privacy expectations, dependency/supply-chain review, and v0.9 security gates.
- `THIRD_PARTY_NOTICES.md` now maps the direct AndroidX/Compose/Room/KSP/build/test tooling families and identifies the version catalog as the dependency source of truth.
- Stale documentation that still described connected testing, advanced hints, backup/restore, or current storage/security behavior as future work has been corrected to match the implemented repository.

#### Audited
- Current Android manifest declares no runtime permissions; the launcher activity is exported only for its launcher intent filter.
- Room uses explicit schema versioning and `MIGRATION_1_2`; destructive migration fallback is not enabled.
- Current history/saved-puzzle entities already define indexes for their principal filtering/identity fields, so no speculative schema migration was added merely for hardening.
- Main-thread review covered the game, custom-puzzle, transfer, challenge, history, saved-puzzle, learning, settings, home, and statistics state layers; blocking solver work found in Game/Custom/Transfer paths is now dispatched off the UI thread where applicable.
- Source-level large-text/accessibility review covered Game, Settings, Home/About, Custom Puzzle, History, Saved Puzzles, Challenges, Learn, sharing, and Backup & Transfer. Manual 200% font/TalkBack verification remains separate and unclaimed.

#### Regression fixes found during v0.9 verification
- API-35 run `32129482037` exposed a race in the selected-game-cell semantics test: Easy puzzle generation is asynchronous, so the test attempted to assert a board node before the generated board was composed. The test now waits for the stable first-cell semantic tag before performing the same selected/unselected assertions.

#### Verification in progress
- Final exact-head standard Android CI and API-35 connected instrumentation evidence will be recorded only after both workflows complete successfully.
- Manual TalkBack/device/font-scale/store-readiness items remain evidence checklists rather than claimed results until actually performed.

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
