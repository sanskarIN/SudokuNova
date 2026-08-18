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

Status: **Implementation complete; final verification in progress on PR #22**

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
- [ ] Final clean-head Android CI green
- [ ] Final clean-head API-35 connected instrumentation green
- [ ] Merge PR #22 and close issue #21

## v0.9 — Release Hardening

Status: **Next milestone after v0.8 merge**

- Full automated regression-suite audit
- Accessibility audit and focus-order review
- Performance and memory audit
- Security/privacy audit
- Dependency and license audit
- Device QA matrix
- Release-build shrinking verification
- UI polish and screenshot readiness
- Documentation accuracy audit
- Crash/ANR hardening and release checklist

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

Potential additions after Classic 9×9 quality is stable:

- Mini 4×4 / 6×6
- Diagonal Sudoku
- Hyper/Windoku
- Killer Sudoku
- Jigsaw/Irregular Sudoku
- Additional board sizes where UX remains practical
- Home-screen Daily Challenge widget
- Optional reminders, default off

## v2.0+ — Longer-Term Possibilities

Only if they can be implemented without compromising privacy or Android quality:

- Larger/advanced variants including Samurai
- Optional cloud backup/sync
- Cross-device progress
- Community puzzle sharing with moderation/safety design
- Web/desktop/iOS clients reusing platform-independent Sudoku logic
- Tournament/time-challenge concepts

## Project Principles

- No ads by default in the open-source base.
- No account required for core play or learning progress.
- No unnecessary sensitive permissions.
- No feature is marked complete unless its implementation exists.
- Advanced solving techniques require deterministic evidence and correctness tests.
- Advanced variants do not block a high-quality Classic release.

☕ Support continued open-source development: https://buymeacoffee.com/sanskarIN

**Made by the Sanskar**
