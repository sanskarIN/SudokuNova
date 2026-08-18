# SudokuNova Roadmap

This roadmap describes intended milestones without promising dates. Correctness, Sudoku validity, data integrity, accessibility, and crash-free behavior take priority over feature count.

## v0.1 — Foundation and First Playable Build

Status: **Completed**

- [x] Repository/build foundation
- [x] Kotlin + Jetpack Compose + Material 3
- [x] Platform-independent Sudoku engine module
- [x] Board validation/candidates/conflicts
- [x] Solver and uniqueness checking
- [x] Seeded generator
- [x] Seven difficulty targets
- [x] First playable Classic 9×9 board
- [x] Notes, undo/redo, eraser, hints, pause, timer
- [x] DataStore settings and active-game restore
- [x] Daily Challenge deterministic seed
- [x] Custom puzzle validation/solve/play flow
- [x] Basic statistics and achievements
- [x] Learning-center foundation
- [x] Theme and adaptive-layout foundation
- [x] CI, testing foundation, and repository policies
- [x] Documentation/build verification foundation

## v0.2 — Core Gameplay Hardening

Status: **Completed**

- [x] Gameplay regression coverage
- [x] Number-first/cell-first input behavior
- [x] Lifecycle/process restoration hardening
- [x] Validation and mistake controls
- [x] Haptic/sound settings
- [x] Hardware keyboard controls
- [x] Board accessibility semantics
- [x] Generator regression/performance checks

## v0.3 — Puzzle and Difficulty System

Status: **Completed**

- [x] Logical/complexity-based difficulty evidence
- [x] Richer solving-technique analysis
- [x] Generation benchmarks and deterministic corpus coverage
- [x] Puzzle-code groundwork
- [x] Generator cancellation/performance controls

## v0.4 — UX, Accessibility, Localization

Status: **Completed**

- [x] Onboarding/learning foundation
- [x] High-contrast presentation
- [x] Reduced-motion support
- [x] TalkBack-oriented semantics
- [x] Adaptive phone/tablet layout
- [x] English resource cleanup
- [x] Hindi translation foundation
- [x] Translation parity verification

## v0.5 — Player Data and History

Status: **Completed**

- [x] Versioned local history storage
- [x] Player statistics
- [x] Best-time and completion records
- [x] Favorites and saved puzzles
- [x] History browsing
- [x] Achievements/statistics expansion
- [x] Safe reset controls

## v0.6 — Challenges

Status: **Completed**

- [x] Daily Challenge archive
- [x] Challenge performance records
- [x] Weekly Challenge
- [x] Deterministic challenge identity
- [x] Challenge statistics
- [x] Challenge UI and regression coverage

## v0.7 — Custom Sudoku and Safe Sharing

Status: **Completed**

- [x] Save/archive custom puzzles
- [x] Replay custom puzzles
- [x] Versioned puzzle-code sharing
- [x] Strict import validation, checksums, and size limits
- [x] Text/clipboard/share flows
- [x] Result sharing/export support
- [x] Versioned local backup/import/export
- [x] Duplicate-safe restore behavior
- [x] English/Hindi transfer resources
- [x] Standard CI and API-35 connected verification

## v0.8 — Learning and Advanced Hints

Status: **Completed**

- [x] Platform-independent structured `TeachingStep` evidence
- [x] Deterministic teaching traces
- [x] Naked Single and Hidden Single placement evidence
- [x] Naked Pair evidence
- [x] Pointing Pair / Triple evidence
- [x] Box-Line Reduction evidence
- [x] Hidden Pair evidence
- [x] Naked Triple evidence
- [x] Hidden Triple evidence
- [x] X-Wing evidence for row/column forms
- [x] Explicit Reveal fallback kept separate from teaching logic
- [x] HintEngine consumes teaching traces
- [x] In-game source/target/elimination/placement highlighting
- [x] Accessibility descriptions for teaching evidence
- [x] English/Hindi hint and learning resources
- [x] Deterministic offline practice catalog covering every supported technique
- [x] Interactive practice answer states
- [x] Local per-technique learning/practice progress
- [x] Safe learning-progress reset
- [x] Engine solution-safety corpus tests
- [x] Direct advanced-technique evidence tests
- [x] Learning-progress JVM tests
- [x] Compose Learn/practice smoke coverage
- [x] Complete learning/hints documentation
- [x] Final clean-head Android CI green — run `32121249242`
- [x] Final clean-head API-35 connected instrumentation green — run `32121249202`
- [x] PR #22 merged as `f07e6496ff5de5bfdfb7676b527a31f71f8b912c`
- [x] Issue #21 closed as completed

## v0.9 — Release Hardening

Status: **In progress — issue #23 / draft PR #25**

Focused issue: `#23` — `v0.9: release hardening, accessibility, performance, security, and production QA`.

Release-hardening work:

- [x] Begin automated regression-suite audit and add bounded-backup edge coverage
- [ ] Complete accessibility semantics and focus-order audit across every major screen
- [ ] Complete large-font and adaptive-layout manual QA
- [ ] Complete high-contrast and reduced-motion manual QA
- [ ] Complete performance and memory audit
- [x] Move game hint computation off the main thread with stale-result protection
- [x] Move Custom Puzzle uniqueness/solve work off the main thread with cancellation and stale-board protection
- [x] Harden puzzle-code validation against stale asynchronous results
- [x] Preserve existing solver/generator/teaching deterministic regression coverage
- [x] Re-audit backup file I/O boundary and preserve bounded off-main-thread architecture
- [x] Audit Room schema/index/migration configuration without adding a speculative migration
- [x] Audit import/export/backup security and privacy boundaries
- [x] Audit manifest runtime permission/export surface
- [x] Add repository secret/signing-material CI guard
- [x] Audit direct dependency/license notice coverage
- [x] Add release R8/shrinking verification to CI
- [x] Add debug APK, release APK, and release AAB verification tasks to CI
- [x] Add release-signing guidance that keeps production credentials outside Git
- [x] Add device QA matrix and manual release checklist without fabricating device results
- [ ] Complete crash/ANR and lifecycle restoration audit
- [x] Add UI/store screenshot-readiness checklist
- [ ] Complete documentation accuracy audit after final implementation
- [x] Expose selected Sudoku game-cell state through accessibility semantics
- [x] Add stable Sudoku game-cell semantic tags
- [x] Add connected regression coverage for selected game-cell semantics
- [x] Expose Custom Puzzle editor cell descriptions/selected/conflict semantics
- [x] Add stable Custom Puzzle editor cell semantic tags and connected selected-state regression
- [x] Move Custom Puzzle status/error presentation to paired English/Hindi resources
- [x] Move game-load/abandon error presentation to typed localized resources
- [x] Remove hardcoded English grammar from the game completion summary
- [ ] Final standard CI green on exact clean head
- [ ] Final API-35 connected gate green on exact clean head
- [ ] Mark PR #25 ready only after release-blocking audits and exact-head gates are complete
- [ ] Merge PR #25 and close issue #23 only after verified evidence exists

## v1.0 — Stable Classic Sudoku Release

Target scope:

- Polished Classic 9×9 gameplay
- Reliable generator/solver and unique solutions
- Seven supported difficulty levels with calibrated scoring
- Quick Play and Daily/Weekly Challenges
- Custom Puzzle and safe sharing/import/export
- Notes, undo/redo, advanced hints, timer, mistake modes
- Autosave/resume
- Statistics/history/achievements
- Interactive learning center and local practice progress
- Light/Dark/System/Dynamic Color
- Accessibility and English/Hindi localization
- Settings, About, Support, privacy/security documentation
- Complete CI/tests/build/release documentation
- Production-ready APK/AAB release process

## v1.x — Carefully Selected Extensions

Possible post-1.0 work should be selected for quality and maintainability rather than feature count. Candidates may include additional Sudoku variants, expanded learning techniques, richer statistics, optional sync/export integrations, or additional platform clients only after Classic Sudoku remains stable and the privacy/security model is explicit.
