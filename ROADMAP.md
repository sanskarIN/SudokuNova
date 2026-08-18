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

Focused issue: `#23` — completed.

Key completion evidence:

- [x] source-level accessibility/large-text/main-thread/lifecycle/security/privacy/persistence audits
- [x] selected-state and Custom Puzzle semantics plus connected regressions
- [x] English/Hindi localization hardening
- [x] repository secret guard and dependency/license audit
- [x] debug/release lint and R8 release APK/AAB CI gates
- [x] complete release/build/security/accessibility/performance/data/user/maintainer documentation
- [x] CODEOWNERS, FUNDING, Dependabot, issue/PR templates and repository governance
- [x] final exact-head Android CI green — `32139568718`
- [x] final exact-head API-35 instrumentation green — `32139568591`
- [x] final verified PR head — `7bc5d095cfdde17dc92250581e3bc28a6fbc54c9`
- [x] PR #25 merged — `18944dc56757e1c1c9d51939cb0cafa72e4b5ee2`

Manual production evidence was deliberately handed to v1.0 instead of being fabricated in v0.9.

## v1.0 — Stable Classic Sudoku Release

Status: **Release candidate in progress — issue #5 / `release/v1.0-readiness`**

Current candidate metadata:

- `versionName = 1.0.0-rc1`
- `versionCode = 990`
- stable `1.0.0` must use a higher versionCode than every distributed candidate.

### Repository/automation preparation

- [x] create dedicated v1.0 release-candidate branch from verified post-v0.9 `main`
- [x] advance candidate metadata to `1.0.0-rc1` without claiming stable release
- [x] add deterministic APK/AAB ZIP-integrity and SHA-256 verifier
- [x] add optional mandatory signature verification through `apksigner`/`jarsigner`
- [x] add Python unit regression tests for the release-artifact verifier
- [x] run release-helper unit tests from standard Android CI
- [x] verify release APK/AAB integrity and generate `SHA256SUMS` in standard Android CI
- [x] retain `SHA256SUMS` with CI release evidence artifacts
- [x] add `docs/V1_RELEASE_EVIDENCE.md`
- [x] add canonical `docs/V1_RELEASE_NOTES.md`
- [x] add truthful `docs/STORE_LISTING.md` and screenshot capture/safety plan
- [x] update build/CI/release documentation for RC→stable promotion and artifact verification
- [x] correct stale build-tool documentation to AGP 9.3.1 / Gradle 9.5
- [ ] exact-head Android CI green for the final v1 readiness PR head
- [ ] exact-head API-35 connected instrumentation green for the final v1 readiness PR head

### Manual / production validation still required

- [ ] real TalkBack traversal/focus-order QA on representative target(s)
- [ ] representative 200% font-scale, narrow-phone, tablet/large-window, resize/orientation QA
- [ ] high-contrast and reduced-motion manual verification
- [ ] measured startup/frame/memory/ANR evidence on representative targets
- [ ] process-death and lifecycle-restoration manual scenarios
- [ ] secure production signing outside Git
- [ ] final signed release APK/AAB signature verification and intended installation/upload smoke checks
- [ ] final R8 release-variant manual smoke QA on signed artifacts
- [ ] real store/repository screenshots and final listing/privacy disclosure review
- [ ] promote candidate metadata to stable `1.0.0` with a valid higher versionCode
- [ ] final stable exact-head automated verification after any promotion commit
- [ ] immutable stable tag, GitHub Release, and Android publication only after all evidence exists

Stable v1.0 must preserve Classic 9×9 correctness, unique solutions, seven difficulty levels, challenges, custom puzzles, safe transfer/backup, advanced hints, offline learning, local data/history/statistics, accessibility/localization, privacy/security, and the verified release pipeline.

## v1.x — Carefully Selected Extensions

Post-1.0 work should prioritize quality and maintainability. Potential extensions may include carefully selected Sudoku variants, expanded learning techniques, richer statistics, optional explicit sync/export integrations, or additional platform clients only after Classic Sudoku remains stable and the privacy/security model stays explicit.
