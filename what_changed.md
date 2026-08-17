# What Changed

## Current Version

**v0.1.0-development**

Current working branch: `feature/bootstrap-v0.1`

Android application ID: `in.sanskar.sudokunova`  
Kotlin source namespace: `com.sanskar.sudokunova`

The Kotlin namespace intentionally differs from the Android application ID because `in` is a Kotlin language keyword and cannot be used as a normal first package segment without escaping.

## Current Phase

SudokuNova has progressed from an empty repository containing only `LICENSE` into a playable Android project with a platform-independent Sudoku engine, Compose UI, persistence, tests, CI, repository policies, and full v0.1 documentation foundation.

The current milestone is **not being called the final v1.0 release**. Advanced variants, full history storage, archive challenges, richer hint techniques, import/export, and full release/device QA remain later milestones so the project does not trade correctness for feature count.

## Completed

### Project / Build Foundation

- Added Gradle Kotlin DSL project configuration.
- Added `app` and `sudoku-engine` modules.
- Added centralized version catalog.
- Configured JDK 17.
- Configured Android compile/target SDK 37 and min SDK 26.
- Configured Kotlin 2.4.10, AGP 9.3.0, and Gradle 9.5.0.
- Added debug/release build types.
- Enabled release minification/resource shrinking.
- Added ProGuard foundation.
- Added `.editorconfig`.
- Added Android/secrets-aware `.gitignore`.
- Added official Gradle 9.5 wrapper JAR generated in GitHub Actions.
- Verified wrapper JAR SHA-256 in automation before committing it.

### Sudoku Engine

- Added immutable Classic 9×9 `SudokuBoard`.
- Added parse/serialize support.
- Added row/column/box validation.
- Added per-cell conflict detection.
- Added candidate calculation.
- Added MRV backtracking solver.
- Added invalid/no-solution handling.
- Added multi-solution counting for uniqueness checks.
- Added solve metrics.
- Added seeded deterministic solved-grid generation.
- Added clue carving with uniqueness re-check after every accepted removal.
- Added seven difficulties: Beginner, Easy, Medium, Hard, Expert, Master, Extreme.
- Added development-stage difficulty scoring from clue pressure and solver metrics.
- Added Naked Single hint detection.
- Added Hidden Single hint detection for rows, columns, and boxes.
- Added optional stronger reveal fallback.

### Android Application

- Added Android SplashScreen integration.
- Added edge-to-edge Compose application entry point.
- Added Material 3 app shell.
- Added light/dark/system theme modes.
- Added Material You dynamic color.
- Added typography foundation.
- Added original SudokuNova vector logo.
- Added adaptive launcher icon.
- Added monochrome themed icon.
- Added dark-compatible splash resources.
- Added minimal Android manifest with no unnecessary sensitive permissions.

### Home / Navigation

- Added Home destination.
- Added Continue Game when a valid active game exists.
- Added difficulty-based Quick Play.
- Added deterministic offline Daily Challenge entry point.
- Added Custom Puzzle entry point.
- Added Learn, Statistics, Settings, About, and Support entry points.
- Added navigation for all implemented destinations.

### Game Screen

- Added responsive 9×9 Sudoku board.
- Added phone and wider/tablet layout behavior.
- Added active-cell highlight.
- Added peer row/column/box highlight.
- Added same-number highlight.
- Added conflict/mistake state.
- Added original clue vs. player number distinction.
- Added candidate-note rendering.
- Added number pad.
- Added Notes mode.
- Added Eraser.
- Added Undo.
- Added Redo.
- Added Hint flow.
- Added Pause/Resume.
- Added timer.
- Added mistake counter/limit.
- Added completion percentage.
- Added restart.
- Added completion dialog.
- Added failed-game dialog when configured mistake limit is reached.
- Added automatic peer-note cleanup after correct placements when enabled.

### Persistence / State Recovery

- Added Preferences DataStore repository.
- Added versioned active-game codec.
- Persisted puzzle, solution, current board, notes, selected cell, elapsed time, mistakes, hints, difficulty, seed, pause/status metadata, and Daily Challenge marker.
- Added automatic active-game saving.
- Added Continue/Resume flow.
- Added malformed saved-state rejection instead of trusting corrupted text.
- Added local user settings persistence.

### Settings

- Theme selection.
- Dynamic color toggle.
- Peer highlighting toggle.
- Same-number highlighting toggle.
- Auto-check toggle.
- Auto-remove-notes toggle.
- Timer visibility toggle.
- Haptics preference foundation.
- Sounds preference foundation.
- Reduced-motion preference foundation.
- High-contrast preference foundation.
- Unlimited/3/5 mistake-limit selection.
- Statistics reset with confirmation.

### Statistics / Achievements

- Games started.
- Games completed.
- Games abandoned data model support.
- Completion rate.
- Best time.
- Total play time.
- Mistakes.
- Hints used.
- Perfect games.
- No-hint games.
- Current streak.
- Longest streak.
- Basic achievements including first puzzle, 10 wins, 100 wins, perfect game, 7-day streak, and 30-day streak.

### Daily Challenge

- Added offline deterministic seed based on local epoch day.
- Added challenge game marker in saved state.
- Current implementation does not require a server or account.

### Custom Sudoku

- Added editable custom board.
- Added clear/erase/input controls.
- Added row/column/box contradiction detection.
- Added minimum-clue uniqueness pre-check guidance.
- Added no-solution detection.
- Added multiple-solution detection.
- Added exactly-one-solution validation.
- Added solution preview.
- Added validated custom-puzzle Play flow.

### Learning

- Added original educational in-app lessons covering:
  - Sudoku rules
  - candidates/pencil marks
  - Naked Single
  - Hidden Single
  - Naked Pair
  - Pointing Pair/Triple
  - Box-Line Reduction
  - X-Wing
  - solving habits

### Accessibility Foundation

- Added semantic descriptions to Sudoku cells with row, column, value, original-clue, and conflict information.
- Added adaptive layouts.
- Added theme/contrast foundation.
- Added reduced-motion and high-contrast preference foundations.
- Added accessibility issue template and release QA requirements.

### Privacy / Security

- No account required.
- No analytics SDK in base app.
- No ads in base app.
- No location/contacts/microphone/camera/SMS/call-log permissions.
- Added Android backup/data extraction rules.
- Added privacy policy matching current implementation.
- Added security engineering documentation.
- Added private vulnerability reporting guidance.
- Added secret/signing exclusions.
- Added Dependabot configuration.

### Repository / Community

- Added professional README.
- Added MIT licensing references.
- Added contribution guide.
- Added Code of Conduct.
- Added Security policy.
- Added Support guide.
- Added AUTHORS.
- Added third-party notices.
- Added changelog.
- Added roadmap.
- Added pull request template.
- Added issue templates for bugs, features, docs, accessibility, and performance.
- Added security/support contact links.
- Added weekly Gradle/GitHub Actions Dependabot updates.
- Added BMC and developer attribution in appropriate documentation/UI.

## Testing Added

### Sudoku Engine

- Board parse/serialize round trip.
- Conflict validation.
- Candidate calculation.
- Known-puzzle solving.
- Invalid puzzle rejection.
- Unique solution confirmation.
- Seeded deterministic puzzle generation.
- Generated puzzle validity.
- Generated puzzle uniqueness.

### Android JVM Tests

- Active game codec round trip.
- Malformed active save rejection.
- Statistics completion-rate calculations.

### CI Quality Gate

Android CI is configured to run:

1. `:sudoku-engine:test`
2. `:app:testDebugUnitTest`
3. `:app:lintDebug`
4. `:app:assembleDebug`
5. report upload

The branch has been iteratively fixed based on real CI failures instead of only assuming compilation success.

## Bugs / Build Problems Fixed

### Kotlin package keyword failure

**Problem:** Suggested package `in.sanskar.sudokunova` begins with Kotlin keyword `in`, causing Kotlin parser/compiler errors.

**Fix:** Migrated source namespace to `com.sanskar.sudokunova` while preserving Android application ID `in.sanskar.sudokunova`.

### Statistics reset type issue

**Problem:** A heterogeneous list of DataStore preference keys could not be removed generically in a type-safe way.

**Fix:** Reset now explicitly removes each typed statistics key.

### Custom puzzle solution preview data-loss bug

**Problem:** Showing the solution replaced the editable puzzle board and would have lost the original clue puzzle for Play.

**Fix:** Added a separate solution-preview state so original clues remain intact.

### Theme label transform compilation issue

**Problem:** Invalid `replaceFirstChar` function reference form.

**Fix:** Replaced it with a valid transform lambda.

### Incorrect solver regression assertion

**Problem:** Test expected cell index 2 to solve to `1`, while the known correct solution is `4`.

**Fix:** Corrected the test expectation after verifying the solved board.

### Compose `weight` compiler issue

**Problem:** Explicit imports of `androidx.compose.foundation.layout.weight` resolved to an internal parent-data property in the current Compose API.

**Fix:** Removed invalid explicit imports; `Modifier.weight(...)` remains correctly resolved inside Row/Column scopes.

### Generator compiler warning

**Problem:** Unnecessary non-null assertion on current best generated puzzle.

**Fix:** Replaced with safe local nullable handling.

### Gradle wrapper automation push race

**Problem:** The wrapper workflow generated and checksum-verified the JAR successfully but branch commits advanced before its push.

**Fix:** Added fetch/rebase and bounded retry logic. The official Gradle wrapper JAR is now present on the working branch.

## Documentation Added

Root documentation:

- `README.md`
- `CONTRIBUTING.md`
- `CODE_OF_CONDUCT.md`
- `SECURITY.md`
- `SUPPORT.md`
- `AUTHORS.md`
- `THIRD_PARTY_NOTICES.md`
- `CHANGELOG.md`
- `ROADMAP.md`
- `what_changed.md`

`docs/` documentation:

- `README.md`
- `GETTING_STARTED.md`
- `INSTALLATION.md`
- `DEVELOPMENT_SETUP.md`
- `ARCHITECTURE.md`
- `SUDOKU_ENGINE.md`
- `PUZZLE_GENERATION.md`
- `DIFFICULTY_SYSTEM.md`
- `GAME_RULES.md`
- `UI_UX.md`
- `DESIGN_SYSTEM.md`
- `ACCESSIBILITY.md`
- `LOCALIZATION.md`
- `TESTING.md`
- `SECURITY.md`
- `PRIVACY.md`
- `DATA_STORAGE.md`
- `BACKUP_RESTORE.md`
- `BUILDING.md`
- `RELEASING.md`
- `CONTRIBUTING_GUIDE.md`
- `TROUBLESHOOTING.md`
- `FAQ.md`
- `ROADMAP.md`
- `CHANGELOG_GUIDE.md`
- `RELEASE_CHECKLIST.md`
- `QA_MATRIX.md`

## Git / Commit Policy Used

Repository work uses small Conventional Commit messages such as:

- `feat:`
- `fix:`
- `test:`
- `docs:`
- `build:`
- `ci:`
- `chore:`
- `refactor:`
- `ui:`

Requested commit identity is used for project-authored commits:

- Name: `Sanskar`
- Email: `sanskarin@outlook.in`

Development history is intentionally preserved through many focused commits instead of one giant implementation commit.

## Current CI State

- The official Gradle 9.5 wrapper bootstrap workflow has completed successfully after checksum verification and committed the wrapper JAR.
- Full Android CI is running against the latest manually authored build/code state after the Kotlin namespace, solver-test, and Compose `weight` fixes.
- This section must be updated again when the latest full build/test/lint/assemble run completes.

## Remaining Work

### v0.2 — Gameplay Hardening

- More gameplay regression tests.
- Full Compose instrumentation test suite.
- Process-death/recreation instrumentation coverage.
- Number-first/smart-selection input modes.
- Hardware keyboard controls for tablets/Chromebooks.
- Complete haptics/sound implementation behind settings.
- More configurable validation modes.
- Full string-resource conversion for localization.

### v0.3 — Difficulty / Engine Calibration

- Technique-aware logical difficulty model.
- Calibrated deterministic puzzle corpus.
- Generator/solver benchmarks.
- Advanced hint-technique implementation/tests.

### v0.4 — UX / Accessibility / Localization

- Onboarding and first-game tooltips.
- Full TalkBack audit.
- Large-text/font-scale audit.
- High-contrast implementation refinement.
- Reduced-motion audit.
- Hindi translation.
- Tablet/foldable refinements.

### v0.5 — Player Data

- Room-backed game history.
- Per-difficulty statistics.
- Favorites/saved puzzles.
- History filtering.
- Expanded achievements.
- Versioned migrations/tests.

### v0.6 — Challenges

- Daily Challenge calendar/archive.
- Daily performance history.
- Weekly Challenge.
- Optional special challenge modes.

### v0.7 — Custom Puzzle / Sharing / Backup

- Save/archive custom puzzles.
- Safe puzzle-code sharing.
- Export/import with schema validation and size limits.
- Backup/restore UI.
- Optional result image export.

### v0.8+ — Advanced Learning and Variants

- More logical hint techniques.
- Practice mode.
- Learning progress.
- Selected variants after Classic 9×9 is hardened, such as Mini/6×6/Diagonal/Hyper/Killer/Jigsaw as quality permits.
- Large/complex variants remain later work rather than destabilizing v1.0 Classic.

### v0.9 / v1.0 — Release Hardening

- Full device QA matrix.
- Performance profiling.
- Accessibility audit.
- Security/privacy audit.
- Dependency/license audit.
- Release signing workflow using secure secrets only.
- Production APK/AAB verification.
- Real screenshots/store assets.
- Final documentation accuracy audit.
- Stable GitHub release/tag when all release gates pass.

## Known Limitations

- Classic 9×9 is the implemented gameplay variant; additional variants are planned.
- Difficulty scoring is a development-stage approximation and needs human-technique calibration.
- Current achievements are a basic subset.
- Full game history/favorites are not yet implemented.
- Daily Challenge archive/calendar is not yet implemented.
- Haptics/sounds have persisted preference foundations but the full feedback playback layer remains later work.
- High contrast/reduced motion have preference foundations and require complete UI behavior audits.
- Some Compose user-facing strings remain directly embedded in source and must be migrated to Android string resources before localization is called complete.
- A complete instrumentation/device QA suite is still required before stable release.

## Branding / Support Verified

- Project: **SudokuNova**
- Tagline: **Think. Solve. Master the Grid.**
- Repository: https://github.com/sanskarIN/SudokuNova
- GitHub: https://www.github.com/sanskarIN
- Buy Me a Coffee: https://buymeacoffee.com/sanskarIN
- Business: `sanskarin@outlook.in`
- Business: `sanskarin.business@gmail.com`
- Support: `supportramsandesh@gmail.com`
- Credit: **Made by the Sanskar**
- License: **MIT**

## v0.2 Hardening Verification Update

Additional v0.2 work now implemented and pushed:

- Saved-game structural integrity validation through `GameStateIntegrity`.
- Codec rejects corrupted original clues, invalid selected-cell indexes, and inconsistent completed states.
- Regression tests cover structural integrity and corrupted codec payloads.
- Hardware keyboard shortcuts for 1–9/numpad, erase, Notes, Hint, Pause, Ctrl+Z, and Ctrl+Y.
- Number-first selection is persisted immediately and codec v1 saves migrate to v2 safely.
- Haptics and sound settings now affect actual game interactions.
- Compose smoke tests are scroll-safe on smaller devices.
- Standard CI compiles the instrumentation APK.
- Pull requests to `main` run connected Compose tests on an Android API 35 emulator through `.github/workflows/instrumentation.yml`.
- `docs/KEYBOARD_SHORTCUTS.md` documents hardware-keyboard behavior.

Fresh-clone local verification on the pushed v0.2 branch passed the current non-device quality gate:

- `:sudoku-engine:test`
- `:app:testDebugUnitTest`
- `:app:assembleDebugAndroidTest`
- `:app:lintDebug`
- `:app:assembleDebug`

The pull-request emulator run is the next merge gate.
