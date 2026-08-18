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

Status: **Completed and merged**

Focused issue: `#23` — `v0.9: release hardening, accessibility, performance, security, and production QA`.

Completed release-hardening work:

- [x] Audit automated regression suites and add bounded-backup edge coverage
- [x] Complete source-level accessibility semantics review across current major screens
- [x] Complete source-level selected-state/large-text accessibility hardening
- [x] Make Settings toggle rows single semantic switch targets and prevent chip-group large-text overflow
- [x] Harden Game/History/Learn/Custom/Challenges/Saved/Share/Transfer layouts against obvious large-text collisions
- [x] Expose Number-first digit selection and Notes mode semantically
- [x] Complete source-level main-thread/performance review and preserve deterministic engine benchmark/corpus coverage
- [x] Move game hint computation off the main thread with stale-result protection
- [x] Move Custom Puzzle uniqueness/solve work off the main thread with cancellation and stale-board protection
- [x] Harden puzzle-code validation against stale asynchronous results
- [x] Preserve solver/generator/teaching deterministic regression coverage
- [x] Re-audit backup file-I/O boundary and preserve bounded off-main-thread architecture
- [x] Audit Room schema/index/migration configuration without speculative migration
- [x] Audit import/export/backup security and privacy boundaries
- [x] Audit manifest runtime permission/export surface
- [x] Add repository secret/signing-material CI guard
- [x] Audit direct dependency/license notice coverage
- [x] Add release R8/shrinking verification to CI
- [x] Add debug APK, release APK, and release AAB verification tasks to CI
- [x] Add release-signing guidance that keeps production credentials outside Git
- [x] Add device QA matrix/manual release checklist without fabricating device results
- [x] Complete source-level navigation/lifecycle/restoration and crash/ANR-sensitive path review
- [x] Add UI/store screenshot-readiness checklist
- [x] Add CODEOWNERS and GitHub funding metadata
- [x] Audit final repository for TODO/FIXME/NotImplemented/debug-print placeholders
- [x] Complete documentation accuracy audit for v0.9 source/tooling changes
- [x] Add game/custom-cell selected semantics and stable connected test targeting
- [x] Add connected selected-state regressions
- [x] Move Custom Puzzle status/error presentation to paired English/Hindi resources
- [x] Move game-load/abandon error presentation to typed localized resources
- [x] Remove hardcoded English grammar from game completion summary
- [x] Align in-app English/Hindi privacy summary with current DataStore, Room, and transfer/backup behavior
- [x] Repair API-35 Custom Puzzle visibility coverage for the intentional adaptive full-width action layout
- [x] Migrate connected Compose activity rule usage to non-deprecated v2 API
- [x] Final exact-head Android CI green — run #583 / `32139568718`
- [x] Final exact-head API-35 connected gate green — run #155 / `32139568591`
- [x] Final verified PR head — `7bc5d095cfdde17dc92250581e3bc28a6fbc54c9`
- [x] PR #25 merged — `18944dc56757e1c1c9d51939cb0cafa72e4b5ee2`
- [x] Stale v0.5–v0.7 issue #4 closed and stale alternate PRs #11/#15/#24 closed as superseded

### v0.9 evidence boundary

v0.9 deliberately completes **source, automated, documentation, and repository hardening**. It does not fabricate manual production evidence. Real-device/assistive-technology/signing/store validation remains a v1.0 gate and is tracked in issue #5.

## v1.0 — Stable Classic Sudoku Release

Status: **In progress — issue #5 / draft PR #27 / RC1 preparation**

The v1.0 line is evidence-driven production validation, not another uncontrolled feature-expansion phase.

### Repository-side RC1 preparation

- [x] Create `release/v1.0-rc1-prep` from verified post-v0.9 `main`
- [x] Open draft PR #27 for the authoritative v1.0 RC preparation path
- [x] Advance candidate metadata to `versionCode 1000` / `versionName 1.0.0-rc.1`
- [x] Add deterministic release APK/AAB/R8 structural/version verifier
- [x] Add SHA-256/byte-size release evidence generation
- [x] Add direct unit coverage for the release verifier
- [x] Add optional secret-backed release signing through four environment variables
- [x] Fail closed when release signing is partially configured
- [x] Add CI regression proving partial signing fails closed
- [x] Add CI release-output verification/checksum gate and artifact retention
- [x] Add production signing handbook
- [x] Add Play Store/listing/privacy/release preparation guide
- [x] Add v1.0 manual release-candidate evidence worksheet
- [x] Add generated GitHub release-note configuration
- [x] Add repository-settings/branch-protection checklist
- [x] Add v1.0 RC repository handoff documentation
- [ ] Enable `main` protection/ruleset and required checks in GitHub repository settings; current connected GitHub tool does not expose this write action
- [ ] Final exact-head Android CI green for PR #27
- [ ] Final exact-head API-35 connected gate green for PR #27
- [ ] Record final RC repository-side verification evidence in `what_changed.md`
- [ ] Promote/merge PR #27 only after repository-side exact-head gates are green and no repository-blocking defect remains

### Required stable-release manual/production validation

- [ ] real TalkBack traversal/focus-order QA on representative target(s)
- [ ] representative 200% font-scale, narrow-phone, tablet/large-window, resize/orientation QA
- [ ] high-contrast and reduced-motion manual verification
- [ ] measured startup/frame/memory/ANR evidence on representative targets
- [ ] process-death and lifecycle-restoration manual scenarios
- [ ] secure production signing outside Git using the intended production/upload key
- [ ] signed release APK certificate/signature verification
- [ ] signed production/production-equivalent APK installation smoke QA
- [ ] AAB distribution-platform validation
- [ ] final R8 release-variant smoke QA on signed artifacts
- [ ] real store/repository screenshots and listing/privacy disclosure review
- [ ] final stable version-code decision after any RC store-track uploads
- [ ] final release notes, immutable `v1.0.0` tag, GitHub Release, and Android publication only after evidence exists

### Stable v1.0 product contract

Stable v1.0 must preserve:

- polished Classic 9×9 gameplay
- reliable generator/solver and unique solutions
- seven supported difficulty levels with calibrated scoring
- Quick Play and Daily/Weekly Challenges
- Custom Puzzle and safe sharing/import/export
- Notes, undo/redo, advanced hints, timer, mistake modes
- autosave/resume
- statistics/history/achievements
- interactive learning center and local practice progress
- Light/Dark/System/Dynamic Color
- accessibility and English/Hindi localization
- Settings, About, Support, privacy/security documentation
- complete CI/tests/build/release documentation
- secure production-ready APK/AAB release process

### Stable promotion rule

Do not tag `v1.0.0` until the exact source commit has both automated and required manual/production evidence. If RC version code `1000` is accepted by a store/distribution track, the final stable build must use a strictly higher version code.

## v1.x — Carefully Selected Extensions

Possible post-1.0 work should be selected for quality and maintainability rather than feature count. Candidates may include additional Sudoku variants, expanded learning techniques, richer statistics, optional sync/export integrations, or additional platform clients only after Classic Sudoku remains stable and the privacy/security model is explicit.
