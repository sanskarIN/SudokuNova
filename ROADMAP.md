# SudokuNova Roadmap

This roadmap describes intended milestones without promising dates. Correctness, Sudoku validity, data integrity, accessibility, and crash-free behavior take priority over feature count.

## v0.1 — Foundation and First Playable Build

Status: **In progress**

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
- [ ] Complete v0.1 documentation set
- [ ] Green build/test/lint gate on integration branch
- [ ] Manual device/emulator smoke test before milestone tag

## v0.2 — Core Gameplay Hardening

- Expand gameplay regression tests
- Improve number-first/smart-selection input options
- Persist undo/redo state if release testing proves it necessary
- Strengthen process-death and lifecycle restoration tests
- Add configurable validation modes
- Add more complete haptics/sound implementation behind settings
- Improve keyboard input for tablets/Chromebooks
- Improve board accessibility semantics and focus order
- Improve generator performance profiling

## v0.3 — Puzzle and Difficulty System

- Calibrate difficulty using technique/complexity evidence, not clue count alone
- Add richer solving-technique analysis
- Add generation benchmarks
- Add deterministic difficulty regression corpus
- Add puzzle-code format specification
- Add stronger generator cancellation/performance controls

## v0.4 — UX, Accessibility, Localization

- Onboarding and replayable first-game tooltips
- Expanded high-contrast presentation
- Reduced-motion audit
- TalkBack navigation audit
- Large-font and orientation QA
- English string-resource cleanup
- Hindi translation foundation
- Tablet/foldable layout refinement

## v0.5 — Player Data and History

- Versioned history storage
- Per-difficulty statistics
- Best/average times by difficulty
- Favorites and saved puzzles
- History filters
- Expanded achievements
- Safe data-reset controls

## v0.6 — Challenges

- Daily Challenge archive/calendar
- Daily performance history
- Weekly Challenge
- Optional special challenges
- Fair no-mistake and speed challenges
- Challenge-related statistics

## v0.7 — Custom Sudoku and Sharing

- Save/archive custom puzzles
- Replay custom puzzles
- Safe text/puzzle-code sharing
- Import validation and size limits
- Optional result image export
- Versioned local backup/import/export design

## v0.8 — Learning and Advanced Hints

- Interactive practice states
- Naked/Hidden Pairs and Triples
- Pointing Pair/Triple
- Box-Line Reduction
- X-Wing
- Additional techniques only after explanation and correctness tests exist
- Learning progress stored locally

## v0.9 — Release Hardening

- Full automated regression suite
- Accessibility audit
- Performance audit
- Security/privacy audit
- Dependency/license audit
- Device QA matrix
- Release-build shrinking verification
- UI polish and screenshot readiness
- Documentation accuracy audit

## v1.0 — Stable Classic Sudoku Release

Target scope:

- Polished Classic 9×9 gameplay
- Reliable generator/solver and unique solutions
- Seven supported difficulty levels with calibrated scoring
- Quick Play and Daily Challenge
- Custom Puzzle
- Notes, undo/redo, hints, timer, mistake modes
- Autosave/resume
- Statistics/history/achievements
- Learning center
- Light/Dark/System/Dynamic Color
- Accessibility and localization-ready resources
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
- No account required for core play.
- No unnecessary sensitive permissions.
- No feature is marked complete unless its implementation exists.
- Advanced variants do not block a high-quality Classic release.

☕ Support continued open-source development: https://buymeacoffee.com/sanskarIN

**Made by the Sanskar**
