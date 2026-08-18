# What Changed

## Current Development State

**Repository:** `https://github.com/sanskarIN/SudokuNova`  
**Latest merged milestone on `main`:** `v0.7.0`  
**Current implementation milestone:** `v0.8.0 — Learning and Advanced Hints`  
**v0.8 focused issue:** `#21`  
**v0.8 pull request:** `#22` — `feat: add v0.8 learning and advanced hints`  
**v0.8 branch:** `feature/v0.8-learning-advanced-hints`  
**Branch base:** `71ad519d1cc1fc02c4bf6ed8f5133832418943b7`  
**Branch state before this change log update:** 39 focused commits ahead of `main`, 0 behind  
**Android application ID:** `in.sanskar.sudokunova`  
**Kotlin source namespace:** `com.sanskar.sudokunova`  
**Android versionCode:** `800`  
**Android versionName:** `0.8.0`  
**License:** MIT  
**Project commit email:** `sanskarin@outlook.in`

v0.8 implementation is feature-complete on the pull-request branch. It is not considered merged or released until the standard Android CI and API-35 connected instrumentation workflows are green on the final clean PR head.

## v0.8 Implementation

### Structured teaching evidence

The Sudoku engine now has a platform-independent teaching evidence model rather than storing hint prose in domain code.

`TeachingStep` records:

- logical technique;
- source cells;
- optional source row/column/3×3 box;
- target cells;
- exact candidate eliminations;
- optional final placement.

Supporting evidence types validate cell/value bounds and reject duplicate/empty evidence states where applicable.

### Deterministic teaching-step finder

`TeachingStepFinder` owns a deterministic candidate-state pipeline. It applies candidate eliminations internally and applies placements through immutable `SudokuBoard` values.

Technique order is intentionally simple-to-advanced:

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

### Shared logical pipeline

`LogicalSolver` no longer maintains an independent duplicate candidate-solving implementation. It consumes `TeachingStepFinder.trace()` and derives:

- technique usage counts;
- candidate-elimination counts;
- hardest technique;
- unresolved-cell count;
- final logical board state.

This keeps difficulty evidence, teaching evidence, and hints aligned to the same Sudoku logic.

### HintEngine refactor

`HintEngine` now consumes structured teaching traces.

Behavior:

- invalid boards fail closed;
- completed boards return no hint;
- supported logical chains are preferred;
- a logical hint contains every teaching step through the first supported placement;
- the value applied to the game remains the final proven placement;
- the hint identity is the hardest logical technique used in the chain;
- Reveal remains an explicit solver-backed fallback when the supported teaching pipeline cannot reach a placement;
- Reveal does not fabricate source cells or candidate-elimination evidence.

### Advanced hint identity correction

A review defect was found before merge: a chain such as Naked Pair → Naked Single originally displayed the final Single as the hint title. That hid the advanced elimination that actually enabled the placement.

The fix makes `SudokuHint.technique` report the hardest ranked technique in its teaching chain while `SudokuHint.placement` remains the final placement. Dedicated engine tests cover advanced-chain identity, direct placement identity, and Reveal identity.

## Advanced Technique Correctness

### Hidden Pair

Two digits restricted to the same two cells in a unit cause other candidates to be removed from those source cells.

### Naked Triple

Three cells whose candidates form the same three-digit union reserve those digits for the triple and remove them from other cells in the unit.

### Hidden Triple

Three digits restricted to the same three cells keep only those digits in the source cells and remove extra candidates there.

### X-Wing

For a digit:

- two rows with the same two candidate columns form the row-oriented pattern and eliminate the candidate from other cells in those columns;
- two columns with the same two candidate rows form the transposed pattern and eliminate the candidate from other cells in those rows.

### Legal candidate-state probes

Tests can inject controlled candidate subsets through an internal testability path. Overrides are accepted only when they:

- target an empty board cell;
- are non-empty;
- remain a subset of candidates that are actually legal under Sudoku constraints.

This allows deterministic pattern tests without creating impossible candidate states that production logic could never reach.

## Engine Correctness Tests

### Teaching trace safety

`TeachingStepFinderTest` verifies deterministic traces and solution safety.

For known and generated puzzles:

- repeated traces must be equal;
- each placement must match the unique solved value;
- no candidate elimination may remove the solved value of its target cell;
- invalid/completed progress states fail closed where required.

Generated corpus coverage spans the supported difficulty enum with deterministic seeds.

### Advanced technique tests

`AdvancedTeachingTechniqueTest` directly validates:

- Hidden Pair source/evidence/eliminations;
- Naked Triple source/evidence/eliminations;
- Hidden Triple source/evidence/eliminations;
- row-oriented X-Wing targets and eliminated digit;
- rejection of impossible candidate overrides.

### Hint identity tests

`SudokuHintTest` verifies:

- multi-step chains report the hardest technique;
- the final placement remains the action applied;
- direct placements retain their own technique;
- Reveal remains a separate fallback identity.

### Offline practice catalog tests

`TeachingPracticeCatalogTest` verifies:

- every supported `LogicalTechnique` has practice coverage;
- practice lookup is deterministic;
- choices are unique;
- the correct technique is included;
- incorrect choices are rejected;
- elimination exercises contain target and candidate evidence.

## Offline Learning and Practice

### TeachingPracticeCatalog

The engine now contains a deterministic, platform-independent practice catalog. Practice exercises store:

- stable exercise ID;
- structured `TeachingStep` evidence;
- bounded/unique technique choices;
- correct-answer identity.

There is at least one practice exercise for every v0.8 supported technique.

The catalog contains no Android resources or player-facing prose.

### LearnViewModel

The Android learning state layer now supports:

- lesson-view recording;
- starting practice for a technique;
- unanswered state;
- first-answer-only recording;
- correct/incorrect result state;
- deterministic next practice;
- closing practice;
- resetting learning progress.

Repeated taps after a practice question is answered do not create duplicate attempts.

### Learn screen

The old read-only Learn page is now an interactive learning center with:

- existing Sudoku/candidate/solving-habit introductory lessons preserved;
- all nine supported technique lessons;
- overall mastery card;
- mastered-technique count;
- aggregate practice results;
- per-technique mastery bars;
- per-technique practice statistics;
- Study Technique controls;
- Practice controls;
- localized lesson dialogs;
- localized correct/incorrect practice feedback;
- reset confirmation.

Stable semantic test tags were added to the lazy learning list, technique study/practice controls, and practice answer choices so connected tests do not depend on off-screen text discovery.

## Local Learning Progress

### Data model

`TechniqueLearningProgress` stores:

- lesson views;
- practice attempts;
- practice successes.

It derives a bounded mastery percentage from lesson exposure, practice depth, and accuracy.

Mastery is not claimed without repeated practice: at least three attempts, at least three successful attempts, and sufficient derived mastery are required.

`LearningProgress` aggregates all supported techniques and derives:

- total lesson views;
- total attempts;
- total successes;
- mastered-technique count;
- overall mastery percentage.

### DataStore persistence

`AppPreferencesRepository` now persists three bounded counters per supported logical technique:

- `learning_lesson_views_*`;
- `learning_practice_attempts_*`;
- `learning_practice_successes_*`.

Learning counters are bounded to prevent integer overflow.

`resetLearningProgress()` removes only learning counters. It does not erase:

- user settings;
- active game;
- gameplay statistics;
- Room history;
- saved/custom puzzles;
- challenge records.

No account, cloud service, analytics SDK, ad SDK, or network connection is required for v0.8 learning progress.

### App JVM tests

`LearningProgressTest` covers:

- zero-state initialization for every technique;
- lesson-only progress without false mastery;
- repeated successful practice reaching mastery;
- aggregate totals;
- rejection of impossible success/attempt counters.

A CI defect was caught before merge because this Android module uses JUnit4 while the first version of the new test imported `kotlin.test`. The test was corrected to use `org.junit.Test` and `org.junit.Assert` in a dedicated fix commit.

## Localized Hint Presentation

`HintPresentation.kt` maps structured domain evidence into Android resources.

The Android layer localizes:

- technique names;
- cell labels;
- row/column/box labels;
- Naked Single explanations;
- Hidden Single explanations;
- elimination-chain explanations;
- Reveal fallback explanations;
- teaching-chain metadata.

No player-facing English/Hindi hint explanation is stored in `sudoku-engine`.

## English / Hindi Localization

New paired files:

- `app/src/main/res/values/learning_strings_v08.xml`
- `app/src/main/res/values-hi/learning_strings_v08.xml`

They cover:

- all nine logical technique names;
- Reveal;
- row/column/box/cell labels;
- hint explanations;
- teaching-chain descriptions;
- source/target/placement/elimination accessibility descriptions;
- learning-progress labels;
- practice prompts/results;
- reset dialog text;
- Hidden Pair lesson;
- Naked Triple lesson;
- Hidden Triple lesson;
- X-Wing lesson.

The first v0.8 CI attempt passed translation parity for **250 localized string keys** before reaching later Gradle stages. The final PR head must pass the parity check again.

## In-Game Teaching Evidence

`GameRoute` passes the pending `SudokuHint` through the game screen to the Sudoku board while the hint dialog is open.

`SudokuBoardView` derives:

- all source cells in the teaching chain;
- all target cells;
- final placement cell/value;
- candidate eliminations grouped by target cell.

Presentation priority preserves correctness visibility:

1. conflict/error;
2. final hint placement;
3. teaching target;
4. teaching source;
5. ordinary selection/same-value/peer highlighting.

Applying a hint still changes only the final supported placement; candidate evidence is explanatory and does not silently rewrite player notes or board values.

## Accessibility

Hint evidence is not color-only.

Affected Sudoku cells can announce localized semantics for:

- teaching source;
- teaching target;
- final placement target/value;
- candidate elimination target and exact candidates to remove.

Existing row/column/value/original-clue/conflict semantics remain present.

Documentation now explicitly requires TalkBack checks for teaching evidence and Learn/practice dialogs during release hardening.

## Connected Compose Coverage

`MainActivityTest` now includes Learn/practice smoke coverage.

The first API-35 run compiled the entire application and Android test source successfully, launched all 13 connected tests, and found one failure in the new Learn smoke test: it attempted to find the off-screen `Naked Single` LazyColumn item before Compose had created that item.

This was a test-selection defect, not an app crash or production compile failure.

The repair:

- gives the Learn LazyColumn a stable semantic test tag;
- scrolls to the first technique item by LazyList index;
- selects Study/Practice controls by technique-specific tags;
- selects the practice answer by technique-specific tag;
- keeps visible-text assertions for the actual user-facing dialog/result.

The corrected final head must pass API-35 connected instrumentation again before merge.

## Release Metadata

Android application metadata is now:

- `versionCode = 800`
- `versionName = "0.8.0"`
- debug builds continue to append `-debug` and the debug application ID suffix.

Toolchain remains:

- Android Gradle Plugin `9.3.1`
- Kotlin `2.4.10`
- KSP `2.3.10`
- Room `2.8.3`
- Compose BOM `2026.08.00`
- compile SDK `37`
- target SDK `37`
- min SDK `26`
- Java/JVM `17`
- Gradle `9.5`

## Documentation Updated for v0.8

### `README.md`

Updated from the early-milestone presentation to the cumulative v0.8 product state, including advanced hints, learning, practice, local progress, transfer, privacy, accessibility, build commands, tests, and next milestone.

### `CHANGELOG.md`

Added a complete v0.8 Unreleased section with additions, changes, safety/correctness notes, release metadata, and cumulative milestone context.

### `ROADMAP.md`

Marks v0.1–v0.7 complete and records the implemented v0.8 scope. Final CI/API-35/merge boxes remain intentionally incomplete until the exact final PR head is verified and merged.

### `docs/LEARNING_AND_HINTS.md`

New complete architecture/reference document for:

- teaching evidence;
- all supported techniques;
- hint behavior;
- Reveal fallback;
- game-board presentation;
- Learn/practice architecture;
- local progress;
- correctness tests;
- localization;
- verification gates;
- future technique extension rules.

### `docs/ACCESSIBILITY.md`

Expanded with teaching-evidence semantics, TalkBack checks, contrast requirements, Learn dialog checks, and release verification expectations.

### `docs/DATA_STORAGE.md`

Expanded with Preferences DataStore learning counters, bounded/reset behavior, Room structured storage, backup/versioning rules, and data-integrity constraints.

### `docs/ARCHITECTURE.md`

Updated the old architecture description to reflect:

- Room already being in production use;
- the shared teaching-step pipeline;
- practice catalog boundary;
- learning persistence;
- safe transfer boundary;
- accessibility boundary;
- current navigation/testing strategy.

### `docs/LOCALIZATION.md`

Updated to reflect English/Hindi as maintained languages, v0.8 resource files, structured engine evidence, placeholder safety, accessibility localization, and the translation parity gate.

## Complete v0.8 File-by-File Change Map

The branch comparison against `main` immediately before this `what_changed.md` update contained 29 product/test/documentation/build files and 39 focused commits. This list intentionally includes every compared v0.8 file rather than only highlighting selected files.

### Root documentation/build metadata

1. `CHANGELOG.md` — complete v0.8 release-line changes and cumulative milestone record.
2. `README.md` — current v0.8 capabilities, architecture, build/test/privacy/accessibility information.
3. `ROADMAP.md` — milestone completion state and v0.8 gate tracking.
4. `app/build.gradle.kts` — versionCode `800` / versionName `0.8.0`.

### Android application source

5. `app/src/main/java/com/sanskar/sudokunova/data/AppPreferencesRepository.kt` — local per-technique learning persistence/reset.
6. `app/src/main/java/com/sanskar/sudokunova/data/LearningProgress.kt` — progress/mastery model.
7. `app/src/main/java/com/sanskar/sudokunova/ui/game/GameScreen.kt` — live pending-hint evidence flow and localized hint dialog.
8. `app/src/main/java/com/sanskar/sudokunova/ui/game/HintPresentation.kt` — Android resource-backed hint names/explanations.
9. `app/src/main/java/com/sanskar/sudokunova/ui/game/SudokuBoard.kt` — source/target/elimination/placement visual and accessibility evidence.
10. `app/src/main/java/com/sanskar/sudokunova/ui/learn/LearnScreen.kt` — interactive learning center, progress, lessons, practice, reset, stable test tags.
11. `app/src/main/java/com/sanskar/sudokunova/ui/learn/LearnViewModel.kt` — practice and progress interaction state.

### Android localization resources

12. `app/src/main/res/values/learning_strings_v08.xml` — English v0.8 learning/hint/accessibility resources.
13. `app/src/main/res/values-hi/learning_strings_v08.xml` — Hindi parity resources.

### Android tests

14. `app/src/test/java/com/sanskar/sudokunova/data/LearningProgressTest.kt` — learning-model JVM tests using module-standard JUnit4.
15. `app/src/androidTest/java/com/sanskar/sudokunova/MainActivityTest.kt` — Learn lesson/practice connected smoke coverage plus prior application smoke tests.

### Documentation

16. `docs/ACCESSIBILITY.md` — v0.8 hint evidence and release accessibility requirements.
17. `docs/ARCHITECTURE.md` — current engine/app/persistence/teaching architecture.
18. `docs/DATA_STORAGE.md` — learning persistence and current DataStore/Room rules.
19. `docs/LEARNING_AND_HINTS.md` — complete v0.8 teaching/learning/hint technical guide.
20. `docs/LOCALIZATION.md` — v0.8 localization/parity rules.

### Sudoku engine production source

21. `sudoku-engine/src/main/kotlin/com/sanskar/sudokunova/engine/HintEngine.kt` — structured hint chain + explicit Reveal fallback + hardest-technique identity.
22. `sudoku-engine/src/main/kotlin/com/sanskar/sudokunova/engine/LogicalSolver.kt` — advanced technique registry and shared teaching-trace solver.
23. `sudoku-engine/src/main/kotlin/com/sanskar/sudokunova/engine/TeachingPractice.kt` — deterministic offline practice catalog.
24. `sudoku-engine/src/main/kotlin/com/sanskar/sudokunova/engine/TeachingStep.kt` — structured evidence model.
25. `sudoku-engine/src/main/kotlin/com/sanskar/sudokunova/engine/TeachingStepFinder.kt` — deterministic candidate-state detector for all supported v0.8 techniques.

### Sudoku engine tests

26. `sudoku-engine/src/test/kotlin/com/sanskar/sudokunova/engine/AdvancedTeachingTechniqueTest.kt` — Hidden Pair/Triple, Naked Triple, X-Wing controlled-evidence tests.
27. `sudoku-engine/src/test/kotlin/com/sanskar/sudokunova/engine/SudokuHintTest.kt` — hardest-technique, final-placement, direct-placement, Reveal contract tests.
28. `sudoku-engine/src/test/kotlin/com/sanskar/sudokunova/engine/TeachingPracticeCatalogTest.kt` — complete deterministic practice coverage.
29. `sudoku-engine/src/test/kotlin/com/sanskar/sudokunova/engine/TeachingStepFinderTest.kt` — trace determinism and solution-safety corpus coverage.

`what_changed.md` itself becomes the 30th changed file when this update is committed.

## Pre-Merge Verification Record

### First standard Android CI attempt

The first broad v0.8 verification head successfully passed:

- checkout/toolchain setup;
- English/Hindi translation parity for 250 keys;
- all `:sudoku-engine:test` tests;
- application production Kotlin compilation.

It then failed at Android JVM test compilation because the new app test used `kotlin.test` imports even though `app` is configured with JUnit4. This was fixed in `fix(test): use JUnit4 assertions for app learning tests`.

### First API-35 connected attempt

The application and Android-test sources compiled, APKs installed, and 13 tests started. Twelve tests passed; the new Learn test failed because it searched for a non-composed off-screen LazyColumn item.

This was fixed by:

- `testability(learn): add stable technique semantic tags`;
- `fix(androidTest): make Learn practice smoke test deterministic`.

### Required final gate

Before PR #22 can leave draft state and merge, the exact final clean PR head must pass:

```bash
python scripts/verify_translations.py
./gradlew :sudoku-engine:test --stacktrace
./gradlew :app:testDebugUnitTest --stacktrace
./gradlew :app:assembleDebugAndroidTest --stacktrace
./gradlew :app:lintDebug --stacktrace
./gradlew :app:assembleDebug --stacktrace
```

and:

```bash
./gradlew :app:connectedDebugAndroidTest --stacktrace
```

on the repository API-35 emulator workflow.

No gate will be marked green in this file until it actually completes successfully on the exact final implementation head.

## v0.2–v0.7 Foundation Preserved

The v0.8 branch is cumulative from the verified v0.7 main line and preserves the existing implementation for:

- v0.2 gameplay hardening, input modes, keyboard controls, persistence, haptics/sounds;
- v0.3 logical difficulty, deterministic generation/corpus verification;
- v0.4 English/Hindi localization, accessibility, high contrast and adaptive layout;
- v0.5 Room-backed History, Favorites, Saved Puzzles, replay provenance and player data;
- v0.6 Daily/Weekly Challenges and challenge persistence;
- v0.7 puzzle codes, strict import validation, result sharing, Android document transfer, versioned backup/restore, duplicate-safe restore and transfer security rules.

The branch is 0 commits behind `main` at the recorded comparison point, so none of the cumulative v0.2–v0.7 work has been dropped.

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

1. English/Hindi translation parity.
2. engine tests.
3. Android JVM tests.
4. instrumentation-test APK compilation.
5. Android lint.
6. debug APK assembly.
7. report upload/post-job cleanup.

### API-35 connected instrumentation

`.github/workflows/instrumentation.yml` runs connected Compose/Room tests on an Android API-35 x86_64 Pixel 6 emulator with KVM and animations disabled.

## Next Milestone After v0.8 Merge

v0.9 remains release hardening, not another uncontrolled feature expansion:

- full regression-suite audit;
- TalkBack/focus-order/large-font accessibility audit;
- performance and memory profiling;
- dependency and license audit;
- security/privacy audit;
- device QA matrix;
- release shrinking/signing verification;
- production APK/AAB validation;
- screenshots/store asset polish;
- final documentation accuracy audit;
- crash/ANR hardening.

v1.0 should be tagged only after these release gates are genuinely satisfied.

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

## Commit Policy

v0.8 continues the project policy of focused Conventional Commit-style changes (`feat:`, `fix:`, `test:`, `testability:`, `docs:`, `build:`, `ci:`, `chore:`, `refactor:`) rather than collapsing the implementation into one giant commit.

The final post-merge documentation commit will record the exact green workflow run IDs, merge commit, issue closure, completed v0.8 status, and v0.9 handoff after those events actually occur.
