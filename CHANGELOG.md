# Changelog

All notable SudokuNova changes are documented here. The project follows Semantic Versioning where practical.

## [Unreleased]

Current post-v0.9 work is tracked toward **v1.0 stable release validation and production readiness** in issue #5. Manual device/TalkBack/font-scale/performance/process-death/signed-artifact/store evidence remains unclaimed until actually performed.

## [0.9.0] - 2026-08-18

### Release Hardening

v0.9 hardened the cumulative Classic Sudoku implementation for release-quality engineering without adding an unrelated Sudoku variant or cloud/analytics dependency.

#### Added
- CI repository-security guard that rejects committed Android signing/private-key file types, known credential-config filenames, PEM private-key material, and obvious GitHub token patterns.
- Stable `scripts/verify_no_secrets.py` entry point for the repository security verifier used by CI and documentation.
- Direct JVM regression coverage for bounded backup-file reads, including UTF-8 decoding, empty/oversized rejection, exact-limit acceptance, and invalid-limit rejection.
- Release QA matrix in `docs/RELEASE_QA.md` covering automated gates, lifecycle, every major app area, accessibility, font/window sizes, performance smoke checks, security/privacy, artifacts, and store-screenshot readiness.
- Stable semantic test tags for individual game-board and Custom Puzzle editor cells.
- Connected Compose coverage for selected Sudoku-cell accessibility semantics on both the game board and Custom Puzzle editor.
- English/Hindi v0.9 resources for Custom Puzzle validation/solver statuses and typed game-load error states.
- Complete categorized documentation set covering end-user workflows, features, project structure, engine internals, data formats, testing, CI/CD, performance, maintenance, release operations, keyboard input, glossary, privacy, security, accessibility, and documentation standards.
- `.github/CODEOWNERS` with explicit default and release/security/docs/app/engine ownership.
- `.github/FUNDING.yml` exposing the optional Buy Me a Coffee support link through GitHub funding metadata.

#### Changed
- Android metadata advanced to `versionCode 900` / `versionName 0.9.0`.
- Standard Android CI verifies both debug and release lint.
- Standard Android CI assembles the minified/resource-shrunk release APK and release AAB.
- Successful CI release verification retains short-lived APK/AAB/R8 mapping outputs as build evidence.
- Sudoku game cells expose selected state through Compose accessibility semantics in addition to localized content descriptions.
- Number-first digit selection and Notes mode expose semantic selected state instead of relying only on color.
- Game text-action rows can horizontally scroll so localized/large-font labels do not collide.
- Custom Puzzle editor cells expose localized row/column/value descriptions, conflict descriptions, selected semantics, and stable test tags.
- Custom Puzzle text actions are stacked at full width for large-font/localized layouts.
- Settings toggle rows act as one merged switch target; trailing switches are presentation-only, preventing duplicate/ambiguous interaction semantics.
- Settings theme/input/mistake chip groups can horizontally scroll rather than overflow at larger text sizes.
- History filter, metric, and badge rows can horizontally scroll at larger text sizes, and empty text layout placeholders were removed.
- Learn technique Study/Practice actions are full-width stacked controls rather than constrained half-width buttons.
- Puzzle-code Copy/Share actions are full-width stacked controls.
- Backup & Transfer Copy/Share/Export/Import actions are full-width stacked controls.
- Challenge status is stacked below the challenge title/difficulty rather than competing for one row at large text sizes.
- Saved Puzzles and Challenges no longer use empty `Text("")` nodes as layout spacers.
- Home uses the maintained credit resource instead of a hardcoded duplicate string.
- The in-app English/Hindi privacy summary matches current DataStore + Room storage, local learning/history/saved/challenge records, and explicit sharing/import/export/backup behavior.
- Hint computation runs on `Dispatchers.Default` and discards results when the requested board is no longer current.
- Custom Puzzle uniqueness validation and solution preview run on `Dispatchers.Default`, cancel superseded solver work, and discard stale-board results.
- Puzzle-code validation cancels superseded work and refuses to publish a result for input that changed while uniqueness analysis was running.
- Transfer text edits no longer clear an unrelated backup/restore busy state.
- Custom Puzzle ViewModel state uses typed statuses rather than player-facing English prose; Compose resolves those statuses through locale resources.
- Game load/abandon errors use typed state with a separate localized presentation mapping rather than exposing exception prose.
- Game completion summary uses the maintained localized completion resource instead of concatenated English `mistake(s)` / `hint(s)` text.
- Room migration override naming matches the Room API without changing schema behavior.
- Connected Compose tests use the non-deprecated `androidx.compose.ui.test.junit4.v2.createAndroidComposeRule` API.
- Adaptive connected tests scroll to full-width Custom Puzzle actions before visibility assertions, matching the intentional large-text layout while preserving the same behavior assertions.
- `docs/BUILDING.md` documents debug/release APK, AAB, R8 mapping, Windows/Unix verification commands, release signing boundaries, reproducibility evidence, and release-quality claim rules.
- `SECURITY.md` documents Android permission/export rules, bounded/fail-closed backup expectations, signing/secret rules, privacy expectations, dependency/supply-chain review, and release-hardening gates.
- `THIRD_PARTY_NOTICES.md` maps the direct AndroidX/Compose/Room/KSP/build/test tooling families and identifies the version catalog as the dependency source of truth.
- `docs/MAINTAINER_GUIDE.md` documents CODEOWNERS, Dependabot, issue forms, PR templates, funding metadata, security/support routing, and CI ownership boundaries.
- Stale documentation that described connected testing, advanced hints, backup/restore, or current storage/security behavior as future work was corrected to match the implemented repository.

#### Audited
- Android manifest declares no runtime permissions; the launcher activity is exported only for its launcher intent filter.
- Room uses explicit schema versioning and `MIGRATION_1_2`; destructive migration fallback is not enabled.
- Current history/saved-puzzle entities define indexes for their principal filtering/identity fields, so no speculative schema migration was added merely for hardening.
- Main-thread review covered game, custom-puzzle, transfer, challenge, history, saved-puzzle, learning, settings, home, and statistics state layers; blocking solver work found in Game/Custom/Transfer paths is dispatched off the UI thread where applicable.
- Source-level large-text/accessibility review covered Game, Settings, Home/About, Custom Puzzle, History, Saved Puzzles, Challenges, Learn, sharing, and Backup & Transfer.
- Repository source search found no remaining `TODO`, `FIXME`, `NotImplementedException`, or obvious debug-print placeholder paths requiring v0.9 cleanup.
- Public-project scaffolding includes contribution/conduct/security/support policies, structured issue forms, PR template, Dependabot, CODEOWNERS, funding metadata, CI, and API-35 connected automation.

#### Regression fixes found during verification
- API-35 run `32129482037` exposed a race in the selected-game-cell semantics test: Easy puzzle generation is asynchronous, so the test attempted to assert a board node before the generated board was composed. The test now waits for the stable first-cell semantic tag before performing the same selected/unselected assertions.
- API-35 run `32134443558` exposed a Custom Puzzle visibility assertion that assumed the pre-hardening compact action layout. After actions were intentionally stacked for large-text accessibility, `Play puzzle` could be below the viewport. The test now verifies editor-cell semantics first and scrolls to Validate/Save/Play before visibility assertions; product behavior checks were not weakened.

#### Final verification and merge
- **Final verified PR head:** `7bc5d095cfdde17dc92250581e3bc28a6fbc54c9`.
- **Android CI:** run #583 / ID `32139568718` — GREEN.
  - repository security guard
  - English/Hindi translation parity
  - `:sudoku-engine:test`
  - `:app:testDebugUnitTest`
  - `:app:assembleDebugAndroidTest`
  - `:app:lintDebug` + `:app:lintRelease`
  - debug APK assembly
  - R8/resource-shrunk release APK assembly
  - release AAB assembly
  - verification and release-artifact uploads
- **API-35 connected instrumentation:** run #155 / ID `32139568591` — GREEN.
- **PR #25 merge commit:** `18944dc56757e1c1c9d51939cb0cafa72e4b5ee2`.
- v0.9 implementation was merged only after both required workflows were green on the exact final head.

#### Evidence boundary
Manual TalkBack traversal, representative 200% font/device QA, high-contrast/reduced-motion device validation, measured startup/frame/memory/ANR traces, process-death scenarios, signed production artifact verification, and real store screenshot/listing review were **not** fabricated as v0.9 results. Those remaining stable-release tasks are tracked in issue #5 for v1.0.

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
- `LogicalSolver` consumes the same teaching-step pipeline used by hints instead of maintaining separate candidate logic.
- `HintEngine` no longer owns player-facing English explanation strings; Android resources render localized names and explanations.
- Multi-step hints report the hardest logical technique in the chain while still applying only the final proven placement.
- Learn is an interactive learning center instead of a read-only lesson list.
- Android version metadata advanced to `versionCode 800` / `versionName 0.8.0`.

#### Safety / Correctness
- Advanced candidate-state probes may only use candidates that are legal subsets for the current Sudoku board.
- Generated-puzzle corpus tests verify that teaching placements agree with the unique solution and candidate eliminations never remove the solved value.
- Invalid or complete boards fail closed in hint/teaching-step entry points.
- Practice progress cannot modify Sudoku truth, solver behavior, game history, or puzzle generation.
- Android learning-model tests use the app module's configured JUnit4 test stack.
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
