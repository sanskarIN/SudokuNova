# What Changed

## Current Development State

**Repository:** `https://github.com/sanskarIN/SudokuNova`  
**Latest merged milestone on `main`:** `v0.9.0 — Release Hardening`  
**v0.9 focused issue:** `#23` — completed after verified merge  
**v0.9 pull request:** `#25` — merged  
**Final verified v0.9 PR head:** `7bc5d095cfdde17dc92250581e3bc28a6fbc54c9`  
**v0.9 merge commit:** `18944dc56757e1c1c9d51939cb0cafa72e4b5ee2`  
**Final v0.9 Android CI:** run `32139568718` — GREEN  
**Final v0.9 API-35 instrumentation:** run `32139568591` — GREEN  
**Current focused milestone:** `v1.0 — Stable Release Validation and Production Readiness`  
**Current focused issue:** `#5` — `v1.0: stable release validation and production readiness`  
**Android application ID:** `in.sanskar.sudokunova`  
**Kotlin source namespace:** `com.sanskar.sudokunova`  
**Android versionCode:** `900`  
**Android versionName:** `0.9.0`  
**License:** MIT  
**Project commit email:** `sanskarin@outlook.in`

SudokuNova now has one cumulative verified v0.1–v0.9 line on `main`. v0.9 was merged only after the exact final PR head passed both required workflows. The remaining work is not another uncontrolled feature-expansion phase: issue #5 now tracks real manual/production evidence required before a credible v1.0 stable release.

## v0.9 Release-Hardening Work In Progress

The v0.9 branch was created from post-v0.8 `main` as a focused release-hardening line. This historical section records the implementation phase before merge. It does not imply that physical-device, accessibility, performance, signing, or store-readiness evidence was fabricated.

Implemented on the v0.9 line:

1. Android development metadata advanced to `versionCode = 900` and `versionName = "0.9.0"`.
2. Standard Android CI expanded to verify `:app:lintRelease` in addition to debug lint.
3. Standard Android CI expanded to build the release APK with R8/resource shrinking enabled.
4. Standard Android CI expanded to build the release Android App Bundle.
5. Successful CI release verification uploads short-lived release APK/AAB/mapping outputs as workflow artifacts for evidence, not as automatically production-publishable packages.
6. Backup-file JVM regression coverage expanded to prove exact-limit acceptance, oversized rejection, empty rejection, UTF-8 decoding, and positive-limit enforcement.
7. `docs/BUILDING.md` expanded with debug/release APK, AAB, release mapping, translation parity, release lint, signing, Windows/Linux/macOS command, reproducibility, and evidence guidance.
8. `SECURITY.md` expanded with manifest/permission expectations, backup/transfer fail-closed requirements, signing/secret-management rules, dependency/supply-chain expectations, privacy expectations, and release-hardening gates.
9. `docs/RELEASE_QA.md` added as a non-fabricated release evidence matrix covering automated gates, installation/lifecycle, every major app area, accessibility, font/window sizes, performance smoke checks, security/privacy, release artifacts, and store-screenshot readiness.
10. Draft PR #25 opened so all v0.9 commits remained reviewable and workflow evidence could be tied to exact branch heads.
11. `scripts/verify_repository_security.py` added to reject committed Android signing/private-key bundles, known credential config filenames, PEM private-key material, and obvious GitHub token patterns.
12. Standard Android CI executes the repository security guard before build/test work.
13. The Android manifest was audited: it declares no runtime permissions; `MainActivity` is exported only as the launcher entry point.
14. Direct dependency and build/test tooling notice coverage was audited and `THIRD_PARTY_NOTICES.md` expanded accordingly.
15. Room schema/index/migration configuration was audited: the database remains explicitly versioned, `MIGRATION_1_2` is registered, destructive fallback is not enabled, and existing principal filter/identity indexes remain present. No speculative migration was added merely to increase commit count.
16. Sudoku cells expose Compose `selected` accessibility semantics in addition to localized content descriptions.
17. Stable per-cell Compose test tags were added for deterministic accessibility regression coverage.
18. API-35 connected coverage includes selecting Sudoku cells and asserting selected/unselected semantics transitions.
19. `CHANGELOG.md` distinguishes implemented hardening from manual checks that are still evidence-driven.
20. `ROADMAP.md` records completed source/automation audits and hands real manual production validation to v1.0.
21. PR #25 description records the exact verified final head, workflow runs, and evidence boundary.

Focused early v0.9 commits include:

- `79589232926d7f3ef3c3ef21865e68baa8ca4cd3` — `chore(release): start v0.9 development version`;
- `319885bcdc899353404bbc48ddf1107e85a9c43d` — `ci(release): verify release lint APK AAB and R8 outputs`;
- `ffd2cd25295d6ba4c390fdbc98c85cedf84359d3` — `test(backup): expand bounded file read regression coverage`;
- `ccc28b2863a8bff1661a805ce5920fe69980c089` — `docs(build): harden release APK AAB and signing guidance`;
- `34a04be6b07a5d991bd9c17112769badfb5b68b6` — `docs(security): define v0.9 permission backup and signing gates`;
- `885701e15a5d1bc07b1900c83c70256810bbaaf7` — `docs(qa): add v0.9 release validation matrix`;
- `573ce18801a823fc85b6bdde27ea87bf0ca88b3c` — `docs: record current v0.9 hardening progress`;
- `ff9a2178e506746fbc7f5ffd2411df1517254b9b` — `security(ci): add deterministic repository secret guard`;
- `ba84b3677df9db91fff114092bf465b1a49d6249` — `ci(security): enforce repository secret guard`;
- `8d64d87c027f60d914025dab450015995b4b3199` — `docs(licenses): audit direct dependencies and build tooling`;
- `89fc1921f87d455c6f674eabc01f2b6df48792e1` — `a11y(game): expose selected Sudoku cell semantics`;
- `c16fb6ef78c15b6d24b1917569077dff182af9a3` — `testability(game): add stable Sudoku cell semantic tags`;
- `0c4d4ccf00cc4fee55bc7e9853d220d34b5b3175` — `test(android): verify Sudoku selected-cell semantics`;
- `57a607251f9910f1207a53b40391aeee2b6f852e` — `docs(changelog): record implemented v0.9 hardening work`;
- `ee089f7db9202593e1e572cee2bfaf64e9f1b186` — `docs(roadmap): track concrete v0.9 hardening progress`.

## Final v0.8 Verification — GREEN

The exact clean PR head `b63c8019cfc2b6f606247af1543586a7ede1b3df` passed every required v0.8 merge gate.

### Standard Android CI — GREEN

**Workflow:** `Android CI`  
**Run number:** `417`  
**Run ID:** `32121249242`

Completed successfully:

1. Set up job.
2. Checkout sources.
3. Set up Java 17.
4. Set up Gradle cache and validate wrapper.
5. Verify English/Hindi translation parity.
6. Verify `:sudoku-engine:test`.
7. Verify `:app:testDebugUnitTest`.
8. Compile Android instrumentation tests through `:app:assembleDebugAndroidTest`.
9. Run `:app:lintDebug`.
10. Assemble debug APK through `:app:assembleDebug`.
11. Upload reports.
12. Post-job cleanup.

The final translation gate remained green after all v0.8 localization changes.

### API-35 Connected Instrumentation — GREEN

**Workflow:** `Android Instrumentation`  
**Run number:** `50`  
**Run ID:** `32121249202`

Completed successfully:

1. Set up job.
2. Checkout the exact final PR head.
3. Set up Java 17.
4. Validate/configure Gradle.
5. Enable KVM access.
6. Run connected Compose and Room tests on the API-35 emulator.
7. Upload instrumentation reports.
8. Post-job cleanup.

The connected gate included the repaired v0.8 Learn lesson/practice smoke flow in addition to navigation, persistence, challenge, history, saved-puzzle, settings, custom-puzzle, Room, and transfer regression coverage.

## v0.8 Merge Record

PR #22 was moved out of draft only after both final-head workflows were green.

The merge operation used:

- expected head SHA: `b63c8019cfc2b6f606247af1543586a7ede1b3df`;
- merge method: merge commit;
- resulting merge commit: `f07e6496ff5de5bfdfb7676b527a31f71f8b912c`.

After the successful merge, issue #21 was explicitly closed with state reason `completed`.

## v0.8 Structured Teaching Evidence

The Sudoku engine has a platform-independent teaching evidence model rather than embedding player-facing hint prose in domain logic.

`TeachingStep` records:

- logical technique;
- source cells;
- optional source row/column/3×3 box;
- target cells;
- exact candidate eliminations;
- optional final placement.

Supporting evidence types validate:

- board cell indices;
- candidate values;
- placement values;
- duplicate source cells;
- duplicate target cells;
- duplicate elimination records;
- the requirement that a teaching step has a real placement or at least one elimination.

This model is reusable by the logical solver, hints, learning content, practice, accessibility presentation, and future platform clients without importing Android UI concepts into `sudoku-engine`.

## Deterministic Teaching-Step Finder

`TeachingStepFinder` owns the candidate-state pipeline used by v0.8 teaching logic.

It applies candidate eliminations to an internal candidate state and applies placements through immutable `SudokuBoard` values. Placing a value also removes that value from row/column/box peers.

Technique search order is intentionally deterministic and simple-to-advanced:

1. Naked Single
2. Hidden Single
3. Naked Pair
4. Pointing Pair / Triple
5. Box-Line Reduction
6. Hidden Pair
7. Naked Triple
8. Hidden Triple
9. X-Wing

The implementation includes both row-oriented and column-oriented X-Wing detection.

`TeachingTrace` records:

- initial board;
- final board;
- ordered teaching steps;
- solved state;
- unresolved-cell count.

Trace generation has an explicit positive maximum-step bound and requires a valid Sudoku board.

## Shared Logical Pipeline

`LogicalSolver` consumes `TeachingStepFinder.trace()` and derives:

- per-technique usage counts;
- candidate-elimination count;
- hardest technique;
- placement count;
- unresolved-cell count;
- final logical board state.

This aligns logical difficulty evidence, teaching evidence, and hint behavior around one deterministic candidate transformation pipeline.

## Supported v0.8 Techniques

### Naked Single

A cell has exactly one remaining candidate. The step records the source/target cell and final placement.

### Hidden Single

Within a row, column, or box, one digit can occur in only one remaining candidate cell. Evidence records the source unit and final placement.

### Naked Pair

Two cells in one unit contain the same two candidates. Those candidates are eliminated from other cells in the same unit.

### Pointing Pair / Triple

Within one box, every remaining candidate for a digit lies on one row or column. The digit is eliminated from the matching line outside the box.

### Box-Line Reduction

Within one row or column, all remaining candidates for one digit lie inside one box. The digit is eliminated from other cells in that box.

### Hidden Pair

Two digits are restricted to the same two cells in a unit. Other candidates are removed from those source cells.

### Naked Triple

Three cells in one unit contain candidates whose union is exactly three digits. Those digits are removed from other cells in the unit.

### Hidden Triple

Three digits are restricted to the same three cells in a unit. Candidates outside those digits are removed from the source cells.

### X-Wing

For a candidate digit:

- two rows with exactly the same two candidate columns form a row-oriented X-Wing and eliminate the candidate from other cells in those columns;
- two columns with exactly the same two candidate rows form the transposed X-Wing and eliminate the candidate from other cells in those rows.

## Legal Candidate-State Testability

Advanced-technique tests use an internal controlled-candidate entry point so specific patterns can be proven deterministically.

Overrides are accepted only when they:

- reference board indices 0–80;
- target cells currently empty;
- contain at least one candidate;
- remain a subset of candidates actually legal on the supplied Sudoku board.

Impossible test-only candidate states are rejected instead of silently bypassing Sudoku legality.

## HintEngine Refactor

`HintEngine` consumes structured teaching traces instead of maintaining player-facing explanation logic inside the engine.

Behavior:

- invalid boards fail closed;
- completed boards return no hint;
- supported logical teaching is preferred;
- a logical hint contains teaching steps through the first supported placement;
- the game applies only the final proven placement;
- displayed hint identity is the hardest technique in the teaching chain;
- Reveal is a separate solver-backed fallback when the supported teaching pipeline cannot reach a placement;
- Reveal does not claim a logical source unit, candidate-elimination chain, or unsupported technique.

## Advanced Hint Identity Defect Found and Fixed

A multi-step chain such as Naked Pair → Naked Single originally reported only the final Naked Single as the hint title. The corrected `SudokuHint.technique` selects the maximum-ranked logical technique from the chain while `SudokuHint.placement` still returns the final placement step.

`SudokuHintTest` verifies:

- a multi-step advanced chain reports its hardest technique;
- final placement remains unchanged;
- direct placement reports its own technique;
- Reveal remains a separate fallback identity.

## Engine Correctness Coverage

### `TeachingStepFinderTest`

Verifies:

- deterministic repeat traces;
- known-puzzle solving through teaching steps;
- generated-puzzle solution safety;
- every teaching placement equals the unique solved value;
- no candidate elimination removes the unique solved value from its target cell;
- empty/invalid progress states fail closed where designed.

Generated corpus coverage uses deterministic seeds across the supported `Difficulty` enum.

### `AdvancedTeachingTechniqueTest`

Directly verifies:

- Hidden Pair source cells and exact extra-candidate eliminations;
- Naked Triple source set and elimination domain;
- Hidden Triple source set and exact extra-candidate eliminations;
- row-oriented X-Wing source cells and target-line restrictions;
- rejection of illegal candidate overrides.

### `TeachingPracticeCatalogTest`

Verifies:

- every supported `LogicalTechnique` has practice coverage;
- catalog lookup is deterministic;
- answer choices are unique;
- correct answer is always included;
- wrong answers are rejected;
- elimination exercises expose candidate and target evidence.

### `SudokuHintTest`

Verifies hint technique/placement/fallback contracts independently from Android UI.

## Offline Practice Catalog

`TeachingPracticeCatalog` is platform-independent and deterministic.

Each `TeachingPracticeExercise` contains:

- stable exercise ID;
- structured `TeachingStep` evidence;
- unique answer choices;
- correct logical technique.

The catalog contains at least one exercise for every v0.8 supported technique.

No account, network request, cloud API, Android resource, or player-facing prose is required to construct an exercise.

## LearnViewModel

The Android learning state layer supports:

- observing local learning progress;
- recording lesson views;
- starting deterministic practice by technique;
- unanswered state;
- first-answer-only submission;
- correct/incorrect feedback state;
- deterministic next-practice selection;
- practice close;
- learning-progress reset.

Repeated taps after submission do not create extra attempts.

## Interactive Learn Screen

The previous read-only learning page became an interactive learning center.

It preserves introductory lessons for:

- what Sudoku is;
- candidates;
- solving habits.

It adds technique learning cards for all nine supported strategies with:

- lesson title;
- mastery percentage;
- progress indicator;
- correct/attempt statistics;
- Study Technique action;
- Practice action.

The screen also contains:

- overall mastery;
- mastered-technique count;
- total practice results;
- localized lesson dialogs;
- localized practice prompt/evidence/result dialogs;
- safe local learning-progress reset confirmation.

## Stable Learn Test Semantics

The Learn LazyColumn, technique study buttons, technique practice buttons, and answer choices have stable semantic test tags.

This lets connected Compose tests:

- scroll the lazy list by index so the target item is composed;
- target a specific logical technique without relying on duplicate labels;
- keep user-facing text assertions for visible dialogs/results.

## Local Learning Progress Model

`TechniqueLearningProgress` stores:

- lesson views;
- practice attempts;
- practice successes.

It validates non-negative counts and that successes do not exceed attempts. It derives bounded mastery from lesson exposure, practice depth, and accuracy. `LearningProgress` aggregates every supported technique.

## DataStore Learning Persistence

`AppPreferencesRepository` stores per-technique counters for lesson views, practice attempts, and successes in Preferences DataStore. Counter increments are bounded. `resetLearningProgress()` removes only learning counters and does not remove settings, active game, aggregate statistics, Room History, Saved Puzzles, custom puzzles, or challenge records.

Learning progress remains fully local and requires no account/cloud backend.

## Android Learning JVM Tests

`LearningProgressTest` covers default state, lesson-only progress, repeated correct practice/mastery, aggregate totals, and invalid success/attempt relationships.

### JUnit Framework Defect Found and Fixed

The first v0.8 standard CI attempt exposed a test-framework mismatch: the Android app module uses JUnit4, while the new test imported `kotlin.test`. The test was moved to `org.junit.Test` and JUnit4 assertions; the final CI run passed.

## Localized Hint Presentation

`HintPresentation.kt` converts structured engine evidence into Android resources for technique names, units/cells, direct explanations, advanced elimination chains, Reveal fallback, and teaching metadata. This keeps `sudoku-engine` free from English/Hindi player-facing strings.

## English / Hindi v0.8 Resources

Paired `learning_strings_v08.xml` files include all supported technique names, Reveal, row/column/box/cell labels, hint explanations, teaching semantics, learning progress, practice UI/results, reset confirmation, and advanced-technique lessons. Translation parity remained green.

## In-Game Teaching Evidence

`GameRoute` observes a pending `SudokuHint` and passes structured evidence through the game surface. The board derives source cells, target cells, final placement/value, and candidate eliminations grouped by target cell.

Presentation priority is:

1. conflict/error;
2. final hint placement;
3. teaching target;
4. teaching source;
5. ordinary selection/same-number/peer highlighting.

Applying a hint still performs only the final supported placement.

## Accessibility Evidence

Teaching evidence is not represented only by color. Affected cells can announce teaching source, target, final placement/value, candidate-elimination target, and exact candidates to remove while preserving row, column, value, clue, conflict, and selected state semantics.

## Connected Compose Test Defect Found and Fixed

The first v0.8 API-35 attempt had one Learn test failure because it searched for an off-screen LazyColumn item before composition. Stable technique tags plus deterministic scroll-to-index targeting fixed the test without weakening the flow. Final API-35 v0.8 run passed.

## Release Metadata

Android metadata after v0.8 was:

- `versionCode = 800`;
- `versionName = "0.8.0"`.

Current v0.9 stack is:

- Android Gradle Plugin `9.3.1`;
- Kotlin `2.4.10`;
- KSP `2.3.10`;
- Room `2.8.3`;
- Compose BOM `2026.08.00`;
- compile SDK `37`;
- target SDK `37`;
- min SDK `26`;
- Java/JVM target `17`;
- Gradle wrapper `9.5`.

Room annotation processing uses KSP2.

## Documentation Completed for v0.8

`README.md`, `CHANGELOG.md`, `ROADMAP.md`, `docs/LEARNING_AND_HINTS.md`, `docs/ACCESSIBILITY.md`, `docs/DATA_STORAGE.md`, `docs/ARCHITECTURE.md`, and `docs/LOCALIZATION.md` were synchronized with the verified cumulative v0.8 implementation.

## Complete v0.8 File-by-File Implementation Map

The v0.8 PR changed 30 files including `what_changed.md` and was split into 40 focused commits before merge.

### Root documentation/build metadata

1. `CHANGELOG.md`
2. `README.md`
3. `ROADMAP.md`
4. `what_changed.md`
5. `app/build.gradle.kts`

### Android application source

6. `app/src/main/java/com/sanskar/sudokunova/data/AppPreferencesRepository.kt`
7. `app/src/main/java/com/sanskar/sudokunova/data/LearningProgress.kt`
8. `app/src/main/java/com/sanskar/sudokunova/ui/game/GameScreen.kt`
9. `app/src/main/java/com/sanskar/sudokunova/ui/game/HintPresentation.kt`
10. `app/src/main/java/com/sanskar/sudokunova/ui/game/SudokuBoard.kt`
11. `app/src/main/java/com/sanskar/sudokunova/ui/learn/LearnScreen.kt`
12. `app/src/main/java/com/sanskar/sudokunova/ui/learn/LearnViewModel.kt`

### Android resources

13. `app/src/main/res/values/learning_strings_v08.xml`
14. `app/src/main/res/values-hi/learning_strings_v08.xml`

### Android tests

15. `app/src/test/java/com/sanskar/sudokunova/data/LearningProgressTest.kt`
16. `app/src/androidTest/java/com/sanskar/sudokunova/MainActivityTest.kt`

### Documentation

17. `docs/ACCESSIBILITY.md`
18. `docs/ARCHITECTURE.md`
19. `docs/DATA_STORAGE.md`
20. `docs/LEARNING_AND_HINTS.md`
21. `docs/LOCALIZATION.md`

### Sudoku engine production source

22. `sudoku-engine/src/main/kotlin/com/sanskar/sudokunova/engine/HintEngine.kt`
23. `sudoku-engine/src/main/kotlin/com/sanskar/sudokunova/engine/LogicalSolver.kt`
24. `sudoku-engine/src/main/kotlin/com/sanskar/sudokunova/engine/TeachingPractice.kt`
25. `sudoku-engine/src/main/kotlin/com/sanskar/sudokunova/engine/TeachingStep.kt`
26. `sudoku-engine/src/main/kotlin/com/sanskar/sudokunova/engine/TeachingStepFinder.kt`

### Sudoku engine tests

27. `sudoku-engine/src/test/kotlin/com/sanskar/sudokunova/engine/AdvancedTeachingTechniqueTest.kt`
28. `sudoku-engine/src/test/kotlin/com/sanskar/sudokunova/engine/SudokuHintTest.kt`
29. `sudoku-engine/src/test/kotlin/com/sanskar/sudokunova/engine/TeachingPracticeCatalogTest.kt`
30. `sudoku-engine/src/test/kotlin/com/sanskar/sudokunova/engine/TeachingStepFinderTest.kt`

## Focused v0.8 Commit Policy

The v0.8 implementation was intentionally split into focused Conventional Commit-style changes rather than a single giant commit. The same focused-commit policy continued through v0.9.

## v0.1–v0.7 Cumulative Foundation Preserved

### v0.1 foundation

- Android/Kotlin/Compose foundation;
- core Sudoku board/solver/generator;
- first playable game;
- notes, undo/redo, hint, pause, timer;
- basic settings/statistics/learning;
- CI/documentation foundation.

### v0.2 gameplay hardening

- Cell-first and Number-first input;
- persisted input mode and selection;
- keyboard arrows / 1–9 / erase / Notes / Hint;
- settings-controlled haptic and click feedback;
- lifecycle/process restore hardening.

### v0.3 puzzle/difficulty system

- logical/complexity evidence;
- difficulty calibration;
- generator benchmarks/corpus tests;
- unique-solution requirement retained as authoritative.

### v0.4 accessibility/localization

- English/Hindi resource-backed core UI;
- translation-parity CI;
- localized difficulty/theme labels;
- Sudoku accessibility semantics;
- high contrast;
- reduced-motion foundation;
- adaptive layouts.

### v0.5 local player data

Room-backed History, Favorite History, replay provenance, Saved Puzzles, custom-puzzle saving, difficulty summaries, and replay-safe statistics.

### v0.6 challenges

Deterministic Daily/ISO Weekly challenge keys, type-separated seeds, challenge archive, first-completion performance storage, Room migration coverage, and challenge provenance.

### v0.7 safe sharing/import/export/backup

Versioned `SNP1` puzzle codes, strict bounds/checksum validation, unique-solution validation before imported play, `SNB1` backup, import bounds, duplicate-safe restore, favorite/replay provenance preservation, clipboard/share/document-picker transfer, no broad storage permission, off-main-thread file I/O, result sharing, English/Hindi resources, and connected Room/Compose coverage.

## Current Navigation

- Home
- Game
- Challenges
- Custom Puzzle
- History
- Saved Puzzles
- Learn
- Statistics
- Settings
- Backup & Transfer
- About

## Permanent CI / QA Gates

### Standard Android CI

`.github/workflows/ci.yml` verifies:

1. repository signing/private-key/obvious credential guard;
2. English/Hindi translation parity;
3. engine tests;
4. Android JVM tests;
5. Android instrumentation-test APK compilation;
6. debug and release Android lint;
7. debug APK assembly;
8. release APK assembly with R8/resource shrinking;
9. release AAB assembly;
10. verification report upload;
11. successful release APK/AAB/mapping artifact upload for short-lived CI evidence.

### API-35 Connected Instrumentation

`.github/workflows/instrumentation.yml` runs connected Compose/Room tests on an Android API-35 x86_64 Pixel 6 emulator with KVM access and animations disabled.

## Important Historical Defects Already Fixed

- invalid Kotlin `in.*` source package keyword conflict;
- invalid Compose `weight` imports;
- solver regression assertion issue;
- DataStore typed reset issue;
- custom-puzzle solution preview data loss;
- Gradle wrapper bootstrap push race;
- Android unit-test framework mismatch;
- divergent phase-branch histories that could drop cumulative work;
- Room kapt/plugin conflict replaced by KSP2;
- missing cumulative DAO/database files;
- duplicate localization helpers;
- Compose locale observability lint issue;
- obsolete untranslated resources;
- challenge saved-state provenance mismatch;
- replay-statistics inflation risk;
- connected Compose duplicate/off-screen-node assumptions;
- v0.7 main-thread backup file I/O;
- v0.7 duplicate Favorite-state loss;
- v0.7 replay-provenance loss after restore;
- v0.8 app learning test using the wrong test framework imports;
- v0.8 Learn connected test searching an uncomposed LazyColumn item;
- v0.8 advanced hint chain reporting only the final Single instead of the hardest enabling technique.

## v1.0 Direction

Stable Classic Sudoku v1.0 should be tagged/published only after issue #5 production-validation requirements are satisfied with evidence.

## Branding / Support

- Project: **SudokuNova**
- Tagline: **Think. Solve. Master the Grid.**
- Repository: `https://github.com/sanskarIN/SudokuNova`
- GitHub: `https://www.github.com/sanskarIN`
- Buy Me a Coffee: `https://buymeacoffee.com/sanskarIN`
- Business: `sanskarin@outlook.in`
- Business: `sanskarin.business@gmail.com`
- Support: `supportramsandesh@gmail.com`
- Credit: **Made by the Sanskar**
- License: **MIT**

## v0.9 Complete Documentation Pass — 2026-08-18

A complete implementation-aligned documentation audit was performed on the v0.9 branch. It preserved historical documentation while adding missing current references and correcting stale pages that described implemented v0.7/v0.8 functionality as planned.

### New documentation added

- `docs/FEATURES.md`
- `docs/USER_GUIDE.md`
- `docs/PROJECT_STRUCTURE.md`
- `docs/CI_CD.md`
- `docs/DATA_FORMATS.md`
- `docs/PERFORMANCE.md`
- `docs/MAINTAINER_GUIDE.md`
- `docs/KEYBOARD_SHORTCUTS.md`
- `docs/GLOSSARY.md`
- `docs/DOCUMENTATION_STANDARDS.md`

### Existing documentation corrected/expanded

`docs/TESTING.md`, `docs/SUDOKU_ENGINE.md`, `docs/BACKUP_RESTORE.md`, `docs/PRIVACY.md`, `docs/SECURITY.md`, `docs/ACCESSIBILITY.md`, `docs/QA_MATRIX.md`, `docs/RELEASE_CHECKLIST.md`, `docs/RELEASING.md`, `docs/README.md`, and root `README.md` were aligned to the implemented cumulative repository.

### Documentation accuracy defect found and fixed

Several guides used the intended public command `scripts/verify_no_secrets.py` while the implementation was `scripts/verify_repository_security.py`. A stable compatibility entry point was added that delegates to the authoritative verifier; CI now uses the documented entry point.

### Focused documentation/consistency commits

- `ebe3549b53ad31c24eee398a860c7a0ed1c54535` — `docs(features): add complete product feature reference`.
- `b2d0a838adb48406d32079c0f3168cb12b476324` — `docs(user): add complete application user guide`.
- `a664afaed6db2f51227d677f6feb1f66c875aedb` — `docs(structure): document repository and source layout`.
- `1db2fe3109c6e815ad3ca09b9b3f1f422cb6c939` — `docs(ci): document automated quality and release gates`.
- `1ef457c7a7b3a84604f47f9651368c53919f82e2` — `docs(data): document puzzle backup and persistence formats`.
- `f799d13b6be0cdfde6a6992a4cdeae53880f9bb3` — `docs(perf): add performance and ANR hardening guide`.
- `2e1805a2fae85ae868df6339428313383805aa06` — `docs(maintainers): add project maintenance handbook`.
- `b7521465da008f3a3c269d57782e841b9f506f7f` — `docs(input): add hardware keyboard reference`.
- `1f13f6ec765bce13785c86f6336ef628cff069e1` — `docs(reference): add SudokuNova terminology glossary`.
- `fc5ee162a5c2aba065285f619c34f15612587551` — `docs(meta): define documentation maintenance standards`.
- `91a15f9566ca83b55735df93fdc85f4c7192c24b` — `docs(testing): align test guide with cumulative v0.9 suite`.
- `e5da0341d460358c0175ec7e73bd2889e516720e` — `docs(engine): document complete solver generator and teaching pipeline`.
- `1e36c7aba80e61169487f682ab023d2365b7683a` — `docs(release): expand controlled release and signing process`.
- `a8cd240107b6239daf609520ee8b7b575dbb509f` — `docs(index): build complete categorized documentation map`.
- `ec8985ca6a48caed9a33788c56de0893218fcd21` — `docs(backup): replace obsolete planned backup guide`.
- `717a60d6908ceacba828190f3802f3c252da02c6` — `docs(privacy): align policy with current local data features`.
- `58e08795a780483401743c06372cecf78f03cc0f` — `docs(security): align technical security guide with v0.9`.
- `cb067036687ce274d270f9fa4e5bedba64ef0c0d` — `docs(qa): align general QA matrix with current feature set`.
- `dab469d50f28e726e5d16411e83fbe001ab5f6fb` — `docs(release): align checklist with v0.9 quality gates`.
- `fa1ce632687e4985e152d3dc88d3ad6e0752e24b` — `docs(a11y): document v0.9 semantics and release checks`.
- `c077f14518883e5b7784169f08a55956fcee7680` — `docs(readme): link complete SudokuNova documentation hub`.
- `5b3739a678abffb9d18575d76666f346083e9acb` — `chore(scripts): add documented security verifier entry point`.
- `c0b92433cbe772c0643f28a4e82be23cd71a297a` — `ci(security): use documented repository security command`.

## v0.9 Runtime, Accessibility, and Localization Audit — 2026-08-18

The hardening pass audited UI-facing asynchronous work, stale-result handling, selected-state semantics, and localization boundaries.

### API-35 regression found and fixed

API-35 instrumentation run `32129482037` failed because `MainActivityTest.gameBoardExposesSelectedCellSemantics` asserted a game-board node before asynchronous puzzle generation completed. Commit `e77a1cc716c89232fccf00169df4fef98a27e3c0` made the test wait for the stable semantic tag before performing the same selected/unselected assertions.

### Main-thread and stale-result fixes

- `8aae5673cfd882b42ede4697241138aa71e548e7` — Room warning cleanup without schema change.
- `95d81234a40f0eb6afb336a98e3089165e23aff2` — Custom Puzzle solve/uniqueness work moved off main thread.
- `92090f7b012ee148ca104f36e0598315f206d372` — stale puzzle validation results discarded.
- `52c844b115337bc35d4de77039b961c9ccb238d6` — transfer busy-state ownership preserved.

### Custom Puzzle accessibility/localization hardening

Editor cells gained localized coordinate/value/conflict/selected semantics and stable tags. Connected coverage verifies selected-state transitions. Raw English validation/solve state moved from the ViewModel to typed state with paired English/Hindi resources.

### Game error/completion localization hardening

Raw exception/player-facing error prose moved to typed state plus localized presentation. Completion summary uses maintained localized resources instead of English fragments.

### Audit documentation

`docs/V09_HARDENING_AUDIT.md` records concrete findings/fixes and explicit manual-evidence exclusions.

## v0.9 Large-Text, Control-State, and Privacy Source Audit — 2026-08-18

### Settings

Whole toggle rows form one merged `Role.Switch` target. Theme/input/mistake chips can horizontally scroll at larger text sizes.

### Game controls

Number-first digit selection and Notes mode expose selected semantics. Game action rows can horizontally scroll.

### Custom Puzzle

Text actions are stacked full-width; the numeric pad remains digit-only. Editor semantics remain intact.

### History, Challenges, Saved Puzzles, Learn

History filters/metrics/badges can scroll; challenge status is vertically stacked; Learn Study/Practice controls are full-width; empty text spacers were removed.

### Sharing and Backup & Transfer

Puzzle-code and backup transfer text actions are full-width stacked controls while existing safe/bounded behavior remains.

### Home / in-app privacy

Home uses the maintained credit resource. English/Hindi privacy summaries describe current DataStore + Room storage and explicit user-driven transfer/backup surfaces.

This source audit never claimed manual TalkBack, 200% font, physical device, measured performance, signed artifact, or store asset validation.

## v0.9 Final Repository Completion Pass — 2026-08-18

### Final API-35 regression found and fixed

Exact head `0a1eba2afe11cdaeb4cedb5cc46fb67a4e72ed62` produced Android CI run `32134443544` green but API-35 run `32134443558` failed `customPuzzleEditorIsReachable` because the test assumed the old compact Custom Puzzle action layout. The accessible full-width layout legitimately placed `Play puzzle` below the viewport.

Commit `93045a538941f56f91b76bf61f3ec2d6397a7c6c` — `fix(androidTest): align connected tests with adaptive layouts` — kept the accessible product layout and made the test scroll Validate/Save/Play into view. It also migrated to `androidx.compose.ui.test.junit4.v2.createAndroidComposeRule`.

### Final open-source project tooling

The repository already contained MIT licensing, contribution/conduct/security/support docs, authors/notices, structured issue forms, PR template, Dependabot for Gradle/Actions, CI, API-35 instrumentation, and repository secret verification.

Final additions:

- `ac651e98f6fa338dc66ada7f36e7a2867de6331b` — `.github/FUNDING.yml` with Buy Me a Coffee metadata.
- `4ded7d9ae1cc5b3c249ed7a6143eefe267253b7a` — `.github/CODEOWNERS` with default and high-value surface ownership.

### Final repository hygiene/source audit

Rechecked manifest permissions/export surface, release R8/shrinking configuration, ProGuard boundary, localized hint presentation, source placeholders, obvious debug leakage, and public repository scaffolding. No release-cleanup `TODO`/`FIXME`/`NotImplementedException`/obvious debug-print path was found by the final targeted searches.

### Final documentation synchronization

- `ce3dbb46d639b55b9ee1f66d9314c9dd83e04b3f` — maintainer automation/ownership documentation.
- `9256c56bf89e7a8e87ebe2ebb264440b2a565747` — changelog final governance/adaptive-test record.
- `d96e9ab309f0184b9ad72d196ba45cecef5701e9` — final hardening audit findings.
- `4b9384b585947e5d87045107ce4f5b34fb3c8544` — final source-level roadmap audit closure.
- `e08322a10ab66e2aed857308ef24b2e0ae48443a` — README project-health alignment.
- `7bc5d095cfdde17dc92250581e3bc28a6fbc54c9` — final pre-merge `what_changed.md` source/tooling completion record.

## Final v0.9 Verification — GREEN

The exact final PR head `7bc5d095cfdde17dc92250581e3bc28a6fbc54c9` passed every required v0.9 automated merge gate.

### Android Instrumentation — GREEN

**Run number:** `155`  
**Run ID:** `32139568591`

The API-35 x86_64 Pixel 6 connected job passed setup, Java 17, Gradle wrapper/cache validation, KVM enablement, the full connected Compose/Room test suite, report upload, and cleanup. The repaired asynchronous board semantics test and adaptive Custom Puzzle reachability test are included in this exact final-head run.

### Android CI — GREEN

**Run number:** `583`  
**Run ID:** `32139568718`

The exact final head passed:

1. checkout and Java 17 setup;
2. Gradle wrapper/cache validation;
3. repository security guard;
4. English/Hindi translation parity;
5. `:sudoku-engine:test`;
6. `:app:testDebugUnitTest`;
7. `:app:assembleDebugAndroidTest`;
8. `:app:lintDebug` and `:app:lintRelease`;
9. debug APK assembly;
10. R8/resource-shrunk release APK assembly;
11. release AAB assembly;
12. verification report upload;
13. release APK/AAB/R8 mapping artifact upload;
14. cleanup.

No branch commit was added after this workflow pair before merge.

## v0.9 Merge and Repository Cleanup Record

PR #25 was promoted out of draft only after both exact-head workflows were green.

Merge used:

- expected head: `7bc5d095cfdde17dc92250581e3bc28a6fbc54c9`;
- merge method: merge commit;
- merge commit: `18944dc56757e1c1c9d51939cb0cafa72e4b5ee2`.

Repository tracker cleanup performed during the final pass:

- stale v0.5–v0.7 issue #4 closed as completed;
- stale alternate PR #24 closed as superseded by cumulative v0.9 PR #25;
- stale historical phase PR #15 closed as superseded by the merged cumulative line;
- stale historical phase PR #11 closed as superseded by the merged cumulative line;
- issue #5 rewritten as the focused `v1.0: stable release validation and production readiness` tracker.

Post-merge documentation commits on `main` record the immutable verified evidence without changing the already-tested PR implementation:

- `f0f10fd8623d9de5fa233ea73f3878a0aa6b01ea` — `docs(changelog): finalize verified v0.9 release history`;
- `7f8bf056e45c1d11e40332125906d1c5a12e3b2e` — `docs(roadmap): mark verified v0.9 hardening complete`;
- `26523aa3126d5710dbe1171908b2545eca11f62d` — `docs(readme): hand off verified v0.9 to v1.0 validation`.

### Evidence boundary after v0.9

The following are **not** claimed as completed and remain issue #5 / v1.0 work:

- real TalkBack traversal/focus-order QA;
- representative 200% font/device/window QA;
- high-contrast/reduced-motion manual device verification;
- measured startup/frame/memory/ANR evidence;
- process-death/lifecycle manual scenarios;
- secure production signing and signed artifact verification;
- real store/repository screenshots/listing/privacy disclosure review;
- v1.0 tag/GitHub Release/Android publication.

This separation is intentional: v0.9 completed source, automation, documentation, security/privacy, accessibility semantics, data-integrity, build, and public-repository hardening; v1.0 must earn the remaining production claims with real evidence.

## Commit Policy

Project-authored work uses focused Conventional Commit-style messages (`feat:`, `fix:`, `test:`, `testability:`, `a11y:`, `perf:`, `docs:`, `build:`, `ci:`, `chore:`, `refactor:`) rather than one giant implementation commit.

This file records only verification, merge, device, accessibility, security, and release claims supported by actual repository evidence.

## v1.0 RC1 Repository-Side Release Preparation — 2026-08-18

The repository-side v1.0 RC1 preparation was completed after the verified v0.9 merge while preserving the rule that stable-production, device, signing, repository-admin, and store claims require actual evidence.

### Authoritative RC1 line

- Preparation branch: `release/v1.0-rc1-prep`.
- Pull request: `#27` — `release: prepare SudokuNova v1.0 rc1`.
- Candidate `versionCode`: `1000`.
- Candidate `versionName`: `1.0.0-rc.1`.
- Application ID: `in.sanskar.sudokunova`.
- Stable `v1.0.0`: not claimed.

If candidate code `1000` is accepted or reserved by a distribution track during testing, the stable build must use a strictly higher Android versionCode.

### Release artifact verifier

Added `scripts/verify_release_outputs.py` with deterministic fail-closed checks for:

- non-empty release APK/AAB/R8 mapping outputs;
- ZIP-valid APK and AAB archives;
- required APK entries `AndroidManifest.xml` and `classes.dex`;
- required AAB entries `BundleConfig.pb`, `base/manifest/AndroidManifest.xml`, and `base/dex/classes.dex`;
- non-empty R8 `mapping.txt`;
- exactly one APK release element in `output-metadata.json`;
- exact expected versionCode/versionName;
- SHA-256 and byte-size evidence for APK, AAB, and mapping.

The same verifier also supports protected signed-release validation through `--require-signatures`:

- APK verification requires `apksigner`;
- AAB verification requires `jarsigner`;
- missing tools fail rather than silently skipping;
- unsigned AAB output is rejected even if `jarsigner` exits zero;
- a successful cryptographic signature still must be compared with the expected production/upload certificate identity by the maintainer.

### Release-verifier regression tests

Added `scripts/tests/test_verify_release_outputs.py` covering:

- valid minimum APK/AAB structures;
- required-entry rejection;
- valid single APK metadata parsing;
- multiple release-element rejection;
- deterministic hash/size evidence output;
- missing/successful `apksigner` cases;
- missing `jarsigner` case;
- unsigned-AAB rejection;
- explicit verified-AAB success.

### Secret-backed signing configuration

`app/build.gradle.kts` now reads production signing only from:

- `SUDOKUNOVA_KEYSTORE_PATH`;
- `SUDOKUNOVA_KEYSTORE_PASSWORD`;
- `SUDOKUNOVA_KEY_ALIAS`;
- `SUDOKUNOVA_KEY_PASSWORD`.

Behavior is intentionally all-or-nothing:

- none configured → unsigned CI-safe release build;
- all four configured → release signing enabled;
- one to three configured → Gradle configuration fails immediately.

Standard Android CI contains a dedicated regression proving the partial-signing path fails closed. No production keystore, key, or password was committed or exposed to ordinary PR CI.

### Release/publication documentation and tooling

Added:

- `.github/release.yml` — generated GitHub release-note category configuration;
- `docs/PRODUCTION_SIGNING.md` — secure signing environment, key handling, signature verification, certificate-identity, and version-code guidance;
- `docs/PLAY_STORE_RELEASE.md` — truthful store listing copy, assets, privacy/data facts, artifact checklist, and rollout discipline;
- `docs/V1_RELEASE_CANDIDATE.md` — detailed real-device/manual/production evidence worksheet and SHIP decision;
- `docs/V1_RELEASE_EVIDENCE.md` — concise exact-head/run/artifact/signature/manual/store evidence ledger;
- `docs/V1_RELEASE_NOTES.md` — canonical truthful stable-release notes source;
- `docs/GITHUB_REPOSITORY_SETTINGS.md` — recommended branch/ruleset, required-check, Actions, and security-admin settings;
- `docs/V1_RELEASE_PREP.md` — RC1 repository handoff and stable-promotion rules.

Updated and synchronized:

- root `README.md`;
- root `CHANGELOG.md`;
- root `ROADMAP.md`;
- root `SECURITY.md`;
- `docs/SECURITY.md`;
- `docs/BUILDING.md`;
- `docs/TESTING.md`;
- `docs/CI_CD.md`;
- `docs/RELEASING.md`;
- `docs/README.md`.

The release documentation also corrected stale build-tool text to Gradle `9.5` and Android Gradle Plugin `9.3.1`.

### Duplicate RC path audit and cleanup

An older alternate PR #26 (`release/v1.0-readiness`) used version code `990` and a different helper/documentation set. It was audited before closure rather than discarded blindly.

Useful stronger work intentionally absorbed into PR #27 included:

- mandatory APK signature-verification concept;
- mandatory AAB signature-verification concept;
- concise release-evidence ledger concept;
- canonical stable release-notes source concept.

Those concepts were retained inside the stricter PR #27 artifact verifier and current v1 release documentation. PR #26 was commented and closed as superseded so only one authoritative RC path remained.

### GitHub repository-settings boundary

At RC-preparation start, the GitHub API reported `main` as unprotected. The connected GitHub repository tool available in this project does not expose branch-protection/ruleset mutation.

Therefore:

- no false claim was made that `main` protection was enabled;
- `docs/GITHUB_REPOSITORY_SETTINGS.md` documents the recommended admin settings;
- issue #5 remains open for actual repository-admin evidence.

## Final v1.0 RC1 Repository Verification — GREEN

The exact final PR #27 head `7016e21f36c8ecb8a495c446ffd8b57e9f20a4ea` passed every repository-side merge gate.

### Android CI — GREEN

**Run number:** `635`  
**Run ID:** `32151771317`

Passed on the exact final head:

1. checkout;
2. Java 17 setup;
3. Gradle wrapper/cache validation;
4. repository security guard;
5. release-verifier Python tests;
6. partial release-signing fail-closed regression;
7. English/Hindi translation parity;
8. `:sudoku-engine:test`;
9. `:app:testDebugUnitTest`;
10. `:app:assembleDebugAndroidTest`;
11. `:app:lintDebug` and `:app:lintRelease`;
12. debug APK;
13. R8/resource-shrunk release APK;
14. release AAB;
15. release APK/AAB/R8 structure verification;
16. exact `versionCode 1000` / `versionName 1.0.0-rc.1` metadata verification;
17. SHA-256/size evidence generation;
18. verification-report upload;
19. release-output/evidence upload;
20. cleanup.

### API-35 Connected Instrumentation — GREEN

**Run number:** `188`  
**Run ID:** `32151771297`

The API-35 x86_64 connected Compose/Room suite passed on the same exact final head.

No PR #27 branch commit was added after this successful workflow pair before merge.

### Exact RC1 CI artifact evidence

Workflow artifact:

- name: `unsigned-release-builds`;
- artifact ID: `9330415157`;
- artifact size: `12,793,995` bytes;
- GitHub artifact digest: `sha256:0f1fa33127f6ae46d633c039bf0aad2e308b11e94bbd5567dd0fbc4805b4263c`;
- recorded expiry: `2026-09-01`.

Generated `release-evidence/sha256.txt`:

- unsigned APK SHA-256: `422a151ab3bb47268a69548ce5669b7a141169cc822400d3ef1376fa476b53c7` — `1,849,599` bytes;
- release AAB SHA-256: `1bbbb2f227fc432efa74fa6efe16f2f17ae3aa5bf4a59ffac9e2e71de9a7cdfd` — `4,349,513` bytes;
- R8 mapping SHA-256: `0f8b128679e858e0d835f0e3d23bfb629448efc4215703dd6c0f69b155e3f3ac` — `39,198,732` bytes.

These are unsigned repository-CI verification artifacts. They are not production-signed release packages or certificate evidence.

## v1.0 RC1 Merge and Post-Merge Evidence Record

PR #27 was moved out of draft only after both exact-final-head workflows were green.

Merge used:

- expected head: `7016e21f36c8ecb8a495c446ffd8b57e9f20a4ea`;
- merge method: merge commit;
- merge commit: `2329881aff8dabaf8d040918e16b6113e3900245`.

Issue #5 was intentionally kept open and updated so every completed repository-side RC item is checked while real stable-production/admin/manual gates remain pending.

Post-merge documentation/evidence commits on `main` include:

- `2880402104bbc142d366d717b316c6864596ec0f` — `docs(readme): record verified v1.0 rc1 preparation merge`;
- `2d22ced5e9e1d29ef788eada58c304148bb387af` — `docs(changelog): record verified v1.0 rc1 preparation merge`;
- `2517d79b6ba2732459f15ae1a88b8665c05e3362` — `docs(roadmap): record verified v1.0 rc1 preparation merge`;
- `1e4a1d93566cecc12d183e55c62aafaa48039e73` — `docs(release): fill verified rc1 automated evidence`;
- `dbda8a8e33088a871fac80b704631a326732f78f` — `docs(qa): prefill verified rc1 automated evidence`;
- `8f3264c2cf9d86fe816ee264a5ce0a2c62030c6c` — `docs(release): hand off merged rc1 to stable validation`.

A previous post-merge `what_changed.md` write compressed historical detail. That was treated as a documentation defect. This corrective update restores the immutable pre-RC history before this appendix and appends RC1 evidence instead, preserving the project rule that cumulative history must not be shortened.

### Stable v1.0 work still pending

The following remain deliberately unclaimed and keep issue #5 open:

- GitHub `main` protection/ruleset enablement and required-check administration;
- real TalkBack traversal/focus-order QA;
- representative 200% font-scale / narrow phone / large phone / tablet / resize / orientation QA;
- high-contrast and reduced-motion manual validation;
- measured startup/frame/memory/ANR evidence;
- process-death/lifecycle real-target validation;
- actual production/upload signing key configuration outside Git;
- signed APK verification and expected production/upload certificate identity;
- signed AAB verification/distribution-platform validation;
- signed production/production-equivalent APK installation smoke;
- final release-only R8 smoke QA on signed artifacts;
- real store/repository screenshots, listing, privacy/data/content, and current target-API review;
- final stable Android versionCode decision after any RC distribution-track uploads;
- stable `versionName = 1.0.0` promotion;
- fresh exact-head stable Android CI and API-35 verification after final stable changes;
- final signed stable artifact hashes and signature evidence;
- final `SHIP` decision;
- immutable `v1.0.0` tag;
- GitHub Release;
- Android/store publication.

Repository-side RC1 preparation is complete, verified, and merged. Stable production readiness is not complete until the evidence above actually exists.