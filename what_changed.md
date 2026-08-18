# What Changed

## Current Development State

**Repository:** `https://github.com/sanskarIN/SudokuNova`  
**Latest merged milestone on `main`:** `v0.8.0 — Learning and Advanced Hints`  
**v0.8 focused issue:** `#21` — closed as completed  
**v0.8 pull request:** `#22` — merged  
**Final verified v0.8 PR head:** `b63c8019cfc2b6f606247af1543586a7ede1b3df`  
**v0.8 merge commit:** `f07e6496ff5de5bfdfb7676b527a31f71f8b912c`  
**Final standard Android CI:** run `32121249242` — GREEN  
**Final API-35 instrumentation:** run `32121249202` — GREEN  
**Current focused milestone:** `v0.9 — Release Hardening`  
**Current focused issue:** `#23` — `v0.9: release hardening, accessibility, performance, security, and production QA`  
**Current v0.9 draft pull request:** `#25` — `feat: harden SudokuNova v0.9 release quality`  
**Current v0.9 branch:** `feature/v0.9-release-hardening`  
**Android application ID:** `in.sanskar.sudokunova`  
**Kotlin source namespace:** `com.sanskar.sudokunova`  
**Android versionCode on v0.9 branch:** `900`  
**Android versionName on v0.9 branch:** `0.9.0`  
**License:** MIT  
**Project commit email:** `sanskarin@outlook.in`

SudokuNova now has one cumulative verified v0.1–v0.8 line on `main`. The v0.8 feature branch was 0 commits behind `main` before merge, the final PR head passed both required workflows, PR #22 was merged with the verified expected head SHA, and issue #21 was closed only after the merge completed.

## v0.9 Release-Hardening Work In Progress

The v0.9 branch was created from post-v0.8 `main` and is intentionally still a draft release-hardening line. No v0.9 release-quality, physical-device, accessibility, performance, or store-readiness claim is made until the corresponding evidence is actually recorded.

Implemented so far on the v0.9 branch:

1. Android development metadata advanced to `versionCode = 900` and `versionName = "0.9.0"`.
2. Standard Android CI expanded to verify `:app:lintRelease` in addition to debug lint.
3. Standard Android CI expanded to build the release APK with R8/resource shrinking enabled.
4. Standard Android CI expanded to build the release Android App Bundle.
5. Successful CI release verification uploads short-lived release APK/AAB/mapping outputs as workflow artifacts for evidence, not as automatically production-publishable packages.
6. Backup-file JVM regression coverage expanded to prove exact-limit acceptance, oversized rejection, empty rejection, UTF-8 decoding, and positive-limit enforcement.
7. `docs/BUILDING.md` expanded with debug/release APK, AAB, release mapping, translation parity, release lint, signing, Windows/Linux/macOS command, reproducibility, and evidence guidance.
8. `SECURITY.md` expanded with manifest/permission expectations, backup/transfer fail-closed requirements, signing/secret-management rules, dependency/supply-chain expectations, privacy expectations, and release-hardening gates.
9. `docs/RELEASE_QA.md` added as a non-fabricated release evidence matrix covering automated gates, installation/lifecycle, every major app area, accessibility, font/window sizes, performance smoke checks, security/privacy, release artifacts, and store-screenshot readiness.
10. Draft PR #25 opened so all later v0.9 commits remain reviewable and workflow evidence can be tied to exact branch heads.
11. `scripts/verify_repository_security.py` added to reject committed Android signing/private-key bundles, known credential config filenames, PEM private-key material, and obvious GitHub token patterns.
12. Standard Android CI now executes the repository security guard before build/test work.
13. The current Android manifest was audited: it declares no runtime permissions; `MainActivity` is exported only as the launcher entry point.
14. Direct dependency and build/test tooling notice coverage was audited and `THIRD_PARTY_NOTICES.md` was expanded accordingly.
15. Room schema/index/migration configuration was audited: the database remains explicitly versioned, `MIGRATION_1_2` is registered, destructive fallback is not enabled, and existing principal filter/identity indexes remain present. No speculative migration was added merely to increase commit count.
16. Sudoku cells now expose Compose `selected` accessibility semantics in addition to their existing localized content descriptions.
17. Stable per-cell Compose test tags were added for deterministic accessibility regression coverage.
18. API-35 connected coverage now includes selecting two Sudoku cells and asserting selected/unselected semantics transitions.
19. `CHANGELOG.md` now distinguishes implemented v0.9 hardening from verification/manual checks that are still pending.
20. `ROADMAP.md` now records completed audits and keeps final workflow/manual QA gates unchecked until evidence exists.
21. Draft PR #25 description was refreshed to match the current implementation and its non-fabrication rules.

Focused v0.9 commits currently include:

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

PR #25 currently contains the cumulative v0.9 implementation and remains draft. Earlier exact heads started Android CI and API-35 instrumentation runs, but subsequent commits intentionally superseded those heads. The final v0.9 workflow evidence must therefore come from the exact final head after all intended implementation/documentation updates, not from an earlier successful/cancelled run.

## Final v0.8 Verification — GREEN

The exact clean PR head `b63c8019cfc2b6f606247af1543586a7ede1b3df` passed every required merge gate.

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

The final translation gate remained green after all v0.8 localization changes. The earlier successful parity stage reported 250 localized English/Hindi keys, and the final head passed the same parity verifier again.

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

The connected gate includes the repaired v0.8 Learn lesson/practice smoke flow in addition to the existing navigation, persistence, challenge, history, saved-puzzle, settings, custom-puzzle, Room, and transfer regression coverage.

## v0.8 Merge Record

PR #22 was moved out of draft only after both final-head workflows were green.

The merge operation used:

- expected head SHA: `b63c8019cfc2b6f606247af1543586a7ede1b3df`;
- merge method: merge commit;
- resulting merge commit: `f07e6496ff5de5bfdfb7676b527a31f71f8b912c`.

After the successful merge, issue #21 was explicitly closed with state reason `completed`.

No implementation commit was added to the PR branch after the final successful workflow pair, so the verified head and merged implementation are the same code/documentation state.

## v0.8 Structured Teaching Evidence

The Sudoku engine now has a platform-independent teaching evidence model rather than embedding player-facing hint prose in domain logic.

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

It applies candidate eliminations to an internal candidate state and applies placements through new immutable `SudokuBoard` values. Placing a value also removes that value from row/column/box peers.

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

`LogicalSolver` was refactored so it does not maintain a second independent candidate-solving implementation.

It now consumes `TeachingStepFinder.trace()` and derives:

- per-technique usage counts;
- candidate-elimination count;
- hardest technique;
- placement count;
- unresolved-cell count;
- final logical board state.

This aligns logical difficulty evidence, teaching evidence, and hint behavior around one deterministic candidate transformation pipeline.

## Supported v0.8 Techniques

### Naked Single

A cell has exactly one remaining candidate. The step records the source/target cell and the final placement.

### Hidden Single

Within a row, column, or box, one digit can occur in only one remaining candidate cell. The evidence records the source unit and the final placement.

### Naked Pair

Two cells in one unit contain the same two candidates. Those two candidates are eliminated from other cells in the same unit.

### Pointing Pair / Triple

Within one box, every remaining candidate for a digit lies on one row or one column. The digit is eliminated from the matching line outside that box.

### Box-Line Reduction

Within one row or column, all remaining candidates for one digit lie inside one box. The digit is eliminated from other cells in that box.

### Hidden Pair

Two digits are restricted to the same two cells in a unit. Every other candidate is removed from those two source cells.

### Naked Triple

Three cells in one unit contain candidates whose union is exactly three digits. Those digits are removed from other cells in the same unit.

### Hidden Triple

Three digits are restricted to the same three cells in a unit. Candidates outside those digits are removed from the source cells.

### X-Wing

For a candidate digit:

- two rows with exactly the same two candidate columns form a row-oriented X-Wing and eliminate the candidate from other cells in those two columns;
- two columns with exactly the same two candidate rows form the transposed X-Wing and eliminate the candidate from other cells in those two rows.

## Legal Candidate-State Testability

Advanced-technique tests use an internal controlled-candidate entry point so specific patterns can be proven deterministically.

Overrides are accepted only when they:

- reference board indices 0–80;
- target cells that are currently empty;
- contain at least one candidate;
- remain a subset of candidates that are actually legal on the supplied Sudoku board.

Impossible test-only candidate states are rejected instead of silently bypassing Sudoku legality.

## HintEngine Refactor

`HintEngine` consumes structured teaching traces instead of maintaining user-facing explanation logic inside the engine.

Behavior now is:

- invalid boards fail closed;
- completed boards return no hint;
- supported logical teaching is preferred;
- a logical hint contains the teaching steps through the first supported placement;
- the game applies only the final proven placement;
- the displayed hint identity is the hardest technique in the teaching chain;
- Reveal is a separate solver-backed fallback when the supported teaching pipeline cannot reach a placement;
- Reveal does not claim a logical source unit, candidate-elimination chain, or unsupported technique.

## Advanced Hint Identity Defect Found and Fixed

During review, a multi-step chain such as Naked Pair → Naked Single originally reported the final Naked Single as the hint title. That hid the elimination technique that enabled the placement.

The corrected `SudokuHint.technique` selects the maximum-ranked logical technique from the chain while `SudokuHint.placement` still returns the final placement step.

`SudokuHintTest` verifies:

- a multi-step advanced chain reports its hardest technique;
- the final placement remains unchanged;
- a direct placement reports its own technique;
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
- the correct answer is always included;
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
- the correct logical technique.

The catalog contains at least one exercise for every v0.8 supported technique.

No account, network request, cloud API, Android resource, or player-facing prose is required to construct an exercise.

## LearnViewModel

The Android learning state layer now supports:

- observing local learning progress;
- recording lesson views;
- starting deterministic practice by technique;
- unanswered state;
- first-answer-only submission;
- correct/incorrect feedback state;
- deterministic next-practice selection;
- practice close;
- learning-progress reset.

Once a practice answer has been submitted, repeated taps do not create extra attempts.

## Interactive Learn Screen

The previous read-only learning page is now an interactive learning center.

It preserves the introductory lessons for:

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

The Learn LazyColumn, technique study buttons, technique practice buttons, and answer choices now have stable semantic test tags.

This allows connected Compose tests to:

- scroll the lazy list by index so the target item is actually composed;
- target a specific logical technique without relying on duplicate labels;
- keep user-facing text assertions for the visible dialog and result.

These tags fixed the first connected-test failure without weakening the actual UI flow.

## Local Learning Progress Model

`TechniqueLearningProgress` stores:

- lesson views;
- practice attempts;
- practice successes.

It validates:

- non-negative lesson views;
- non-negative attempts;
- non-negative successes;
- successes must not exceed attempts.

It derives a bounded mastery percentage from:

- lesson exposure;
- practice depth;
- practice accuracy.

A technique is not marked mastered until it has at least:

- three attempts;
- three successes;
- sufficient derived mastery.

`LearningProgress` aggregates every supported technique and exposes:

- total lesson views;
- total attempts;
- total successes;
- mastered-technique count;
- overall mastery percentage.

## DataStore Learning Persistence

`AppPreferencesRepository` now stores per-technique counters for:

- lesson views;
- practice attempts;
- practice successes.

Keys are generated from the logical-technique name and stored in the existing Preferences DataStore.

Learning counter increments are bounded to prevent integer overflow.

`resetLearningProgress()` removes only learning counters. It does not remove:

- user settings;
- active game;
- aggregate game statistics;
- Room History;
- Saved Puzzles;
- custom puzzles;
- Daily/Weekly challenge records.

Learning progress remains fully local and requires no account or cloud backend.

## Android Learning JVM Tests

`LearningProgressTest` covers:

- default zero state for every supported technique;
- lesson-only progress without false mastery;
- repeated correct practice reaching mastery;
- aggregate totals;
- invalid success/attempt relationships.

### JUnit Framework Defect Found and Fixed

The first v0.8 standard CI attempt reached Android unit-test compilation after translation parity and all engine tests had already passed. The new app JVM test imported `kotlin.test`, but the Android `app` module is configured with JUnit4.

The fix changed the app test to:

- `org.junit.Test`;
- `org.junit.Assert.assertEquals`;
- `org.junit.Assert.assertFalse`;
- `org.junit.Assert.assertTrue`.

The final CI run then passed the Android JVM test stage and all later standard CI stages.

## Localized Hint Presentation

`HintPresentation.kt` converts structured engine evidence into Android resources.

The Android layer owns localized:

- technique names;
- row labels;
- column labels;
- box labels;
- cell labels;
- Naked Single explanations;
- Hidden Single explanations;
- advanced elimination-chain explanations;
- Reveal fallback explanations;
- teaching-chain metadata.

This keeps `sudoku-engine` free from English/Hindi player-facing strings.

## English / Hindi v0.8 Resources

New paired resource files:

- `app/src/main/res/values/learning_strings_v08.xml`;
- `app/src/main/res/values-hi/learning_strings_v08.xml`.

They include:

- all nine supported technique names;
- Reveal;
- row/column/box/cell labels;
- direct hint explanation templates;
- elimination-chain explanation templates;
- teaching-chain descriptions;
- source semantics;
- target semantics;
- placement semantics;
- candidate-elimination semantics;
- learning progress labels;
- practice prompts/results/actions;
- reset confirmation;
- Hidden Pair lesson;
- Naked Triple lesson;
- Hidden Triple lesson;
- X-Wing lesson.

The final standard CI head passed English/Hindi translation parity.

## In-Game Teaching Evidence

`GameRoute` observes the pending `SudokuHint` and passes it through the game surface to `SudokuBoardView` while the hint dialog is open.

The board derives:

- all source cells across the teaching chain;
- all target cells;
- final placement cell and value;
- candidate eliminations grouped by target cell.

Presentation priority is:

1. conflict/error;
2. final hint placement;
3. teaching target;
4. teaching source;
5. ordinary selection/same-number/peer highlighting.

The explanatory evidence does not silently mutate player notes or candidate state in the UI. Applying the hint still performs only the final supported placement through the existing game-state path.

## Accessibility Evidence

Teaching evidence is not represented only by color.

Affected cells can announce localized descriptions for:

- teaching source;
- teaching target;
- final placement target and value;
- candidate-elimination target and exact candidates to remove.

Existing semantic information for row, column, value, original clue, and conflict is preserved.

The accessibility documentation now explicitly includes v0.8 hint evidence and Learn/practice dialogs in the release QA checklist.

## Connected Compose Test Defect Found and Fixed

The first v0.8 API-35 attempt compiled production and Android-test source successfully, installed the APKs, and started 13 tests. Twelve passed; the new Learn flow test failed because it searched by text for a `Naked Single` LazyColumn item before that off-screen item was composed.

The repair was split into focused commits:

- `testability(learn): add stable technique semantic tags`;
- `fix(androidTest): make Learn practice smoke test deterministic`.

The corrected test now:

- enters Learn;
- confirms Learning Progress;
- scrolls the tagged LazyColumn to the first technique item;
- opens Naked Single through its technique-specific Study tag;
- closes the lesson;
- starts Naked Single practice through its technique-specific Practice tag;
- verifies the practice dialog/prompt;
- selects the Naked Single answer through its technique-specific choice tag;
- verifies the localized correct-result text.

The final API-35 run passed the complete connected suite.

## Release Metadata

Android app metadata after v0.8:

- `versionCode = 800`;
- `versionName = "0.8.0"`;
- debug application ID suffix remains configured;
- debug version suffix remains configured.

Current build stack remains:

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

### `README.md`

Updated to the cumulative v0.8 feature set and then, after merge, handed off the Current Development Status to v0.9 release hardening while preserving the verified v0.8 workflow record.

### `CHANGELOG.md`

v0.8 is now recorded as `[0.8.0] - 2026-08-18` with implementation, safety, correctness, defect-fix, and exact verification/merge evidence. `[Unreleased]` now points to v0.9 release-hardening work rather than claiming unimplemented completion.

### `ROADMAP.md`

v0.8 is marked Completed. The final Android CI, API-35 instrumentation, merge, and issue-closure items are all checked with real run/commit evidence. v0.9 is now In Progress and linked to issue #23.

### `docs/LEARNING_AND_HINTS.md`

Contains the complete teaching/hint/practice architecture, technique definitions, Reveal policy, Learn architecture, local progress behavior, localization rules, correctness tests, verification commands, and future extension requirements.

### `docs/ACCESSIBILITY.md`

Documents source/target/elimination/placement semantics, TalkBack checks, contrast requirements, large-font expectations, hardware keyboard checks, and v0.8 teaching evidence.

### `docs/DATA_STORAGE.md`

Documents the DataStore learning counters, bounded behavior, scoped reset behavior, current Room use, import/backup integrity, persistent-format rules, and data-integrity constraints.

### `docs/ARCHITECTURE.md`

Reflects the current engine/app boundaries, Room/DataStore persistence, shared teaching pipeline, offline practice catalog, safe transfer boundary, accessibility boundary, navigation, and testing strategy.

### `docs/LOCALIZATION.md`

Reflects English/Hindi maintained resources, v0.8 learning string files, translation parity, placeholder safety, accessibility localization, and the rule that player-facing explanation prose belongs in the Android layer.

## Complete v0.8 File-by-File Implementation Map

The v0.8 PR changed 30 files including `what_changed.md`. The implementation was split into 40 focused commits before merge. The list below intentionally includes every v0.8 PR file.

### Root documentation/build metadata

1. `CHANGELOG.md` — v0.8 release-line implementation/safety/test record.
2. `README.md` — cumulative v0.8 product/build/test/privacy/accessibility status.
3. `ROADMAP.md` — v0.8 implementation and gate tracking.
4. `what_changed.md` — complete implementation, defect, verification, and handoff record.
5. `app/build.gradle.kts` — versionCode `800`, versionName `0.8.0`.

### Android application source

6. `app/src/main/java/com/sanskar/sudokunova/data/AppPreferencesRepository.kt` — per-technique learning persistence, bounded counters, reset.
7. `app/src/main/java/com/sanskar/sudokunova/data/LearningProgress.kt` — pure learning/mastery model and aggregate state.
8. `app/src/main/java/com/sanskar/sudokunova/ui/game/GameScreen.kt` — pending hint flow and localized structured hint dialog.
9. `app/src/main/java/com/sanskar/sudokunova/ui/game/HintPresentation.kt` — Android resource-backed names/explanations.
10. `app/src/main/java/com/sanskar/sudokunova/ui/game/SudokuBoard.kt` — visual and semantic source/target/elimination/placement evidence.
11. `app/src/main/java/com/sanskar/sudokunova/ui/learn/LearnScreen.kt` — interactive progress/lesson/practice/reset UI and stable test tags.
12. `app/src/main/java/com/sanskar/sudokunova/ui/learn/LearnViewModel.kt` — practice state and learning progress actions.

### Android resources

13. `app/src/main/res/values/learning_strings_v08.xml` — English v0.8 learning/hint/accessibility resources.
14. `app/src/main/res/values-hi/learning_strings_v08.xml` — Hindi parity resources.

### Android tests

15. `app/src/test/java/com/sanskar/sudokunova/data/LearningProgressTest.kt` — JUnit4 learning progress tests.
16. `app/src/androidTest/java/com/sanskar/sudokunova/MainActivityTest.kt` — connected Learn lesson/practice smoke coverage plus previous app smoke coverage.

### Documentation

17. `docs/ACCESSIBILITY.md` — teaching semantics and release accessibility checks.
18. `docs/ARCHITECTURE.md` — current domain/app/persistence/teaching architecture.
19. `docs/DATA_STORAGE.md` — local learning persistence and existing Room/DataStore integrity.
20. `docs/LEARNING_AND_HINTS.md` — complete v0.8 technical guide.
21. `docs/LOCALIZATION.md` — English/Hindi v0.8 localization and parity rules.

### Sudoku engine production source

22. `sudoku-engine/src/main/kotlin/com/sanskar/sudokunova/engine/HintEngine.kt` — teaching-chain hint behavior, hardest-technique identity, explicit Reveal fallback.
23. `sudoku-engine/src/main/kotlin/com/sanskar/sudokunova/engine/LogicalSolver.kt` — advanced technique registry and shared teaching-trace solving.
24. `sudoku-engine/src/main/kotlin/com/sanskar/sudokunova/engine/TeachingPractice.kt` — deterministic offline practice catalog.
25. `sudoku-engine/src/main/kotlin/com/sanskar/sudokunova/engine/TeachingStep.kt` — structured teaching evidence model.
26. `sudoku-engine/src/main/kotlin/com/sanskar/sudokunova/engine/TeachingStepFinder.kt` — deterministic candidate-state detection for all supported v0.8 techniques.

### Sudoku engine tests

27. `sudoku-engine/src/test/kotlin/com/sanskar/sudokunova/engine/AdvancedTeachingTechniqueTest.kt` — Hidden Pair/Triple, Naked Triple, X-Wing controlled-evidence tests.
28. `sudoku-engine/src/test/kotlin/com/sanskar/sudokunova/engine/SudokuHintTest.kt` — advanced chain/direct placement/Reveal hint identity tests.
29. `sudoku-engine/src/test/kotlin/com/sanskar/sudokunova/engine/TeachingPracticeCatalogTest.kt` — practice coverage/determinism/evidence tests.
30. `sudoku-engine/src/test/kotlin/com/sanskar/sudokunova/engine/TeachingStepFinderTest.kt` — deterministic trace and solution-safety corpus tests.

## Focused v0.8 Commit Policy

The v0.8 implementation was intentionally split into focused Conventional Commit-style changes rather than a single giant commit. The branch reached 40 commits and 30 changed files before merge.

Commit categories included:

- `feat(engine)`;
- `refactor(engine)`;
- `test(engine)`;
- `testability(engine)`;
- `feat(data)`;
- `test(data)`;
- `feat(i18n)`;
- `feat(ui)`;
- `feat(game)`;
- `feat(learn)`;
- `feat(accessibility)`;
- `test(android)`;
- `testability(learn)`;
- `fix(test)`;
- `fix(engine)`;
- `fix(androidTest)`;
- `fix(ui)`;
- `chore(release)`;
- `docs` variants.

The same focused-commit policy continues for v0.9.

## v0.1–v0.7 Cumulative Foundation Preserved

The v0.8 merge preserves all earlier verified work.

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

Room-backed:

- completed-game History;
- Favorite History;
- replay provenance;
- Saved Puzzles;
- custom-puzzle saving;
- difficulty summaries;
- replay-safe statistics.

### v0.6 challenges

- deterministic Daily and ISO Weekly challenge keys;
- type-separated seeds;
- challenge archive;
- first-completion performance storage;
- Room migration coverage;
- challenge provenance in active-game state.

### v0.7 safe sharing/import/export/backup

- versioned `SNP1` puzzle codes;
- strict bounds/checksum/format validation;
- unique-solution validation before imported play;
- versioned `SNB1` backup format;
- import size/count/timestamp/counter bounds;
- duplicate-safe Room restore;
- Favorite promotion without demotion;
- replay-provenance preservation;
- clipboard/share/document-picker transfer;
- no broad storage permission;
- file I/O moved off the main thread;
- result sharing;
- English/Hindi transfer resources;
- connected Room/Compose regression coverage.

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

v0.9 preserves these connected gates and expands release-build/QA evidence where safe and practical. The connected suite now also includes selected-cell accessibility-semantics transition coverage.

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

## v0.9 Handoff

Focused issue #23 is open:

**`v0.9: release hardening, accessibility, performance, security, and production QA`**

Draft PR #25 carries the current implementation and remains intentionally draft while release-blocking/manual/evidence work remains.

v0.9 is deliberately a quality milestone rather than a new feature-family milestone.

Remaining scope includes:

1. finish accessibility semantics/focus-order review across all major screens;
2. large-font and adaptive-layout manual QA;
3. high-contrast and reduced-motion manual QA;
4. performance/memory audit and additional bounded regression checks where practical;
5. main-thread blocking work audit outside the already hardened backup path;
6. crash/ANR/lifecycle restoration audit;
7. documentation accuracy audit after final code changes;
8. final exact-head standard CI green;
9. final exact-head API-35 connected gate green;
10. only then decide whether PR #25 is ready to merge and issue #23 can close.

### v0.9 non-goals

- no Sudoku variants merely to increase feature count;
- no cloud account dependency;
- no ads/analytics SDK as part of hardening;
- no weakening unique-solution checks;
- no weakening import/backup validation;
- no weakening teaching evidence correctness;
- no release-quality claim without actual evidence.

## v1.0 Direction

Stable Classic Sudoku v1.0 should be tagged only after v0.9 release-hardening requirements are satisfied with evidence.

The existing v0.1–v0.8 feature set already includes the intended core Classic gameplay, challenges, custom puzzles, player data, safe sharing/backup, advanced hints, offline learning/practice, localization, and accessibility foundations. v0.9 is the evidence and hardening bridge between that feature set and a credible v1.0 stable release.

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

A complete implementation-aligned documentation audit was performed on the v0.9 branch. The work preserved the existing historical documentation while adding missing current references and correcting stale pages that still described implemented v0.7/v0.8 functionality as planned.

### New documentation added

- `docs/FEATURES.md` — complete implemented product feature reference and explicit current non-features.
- `docs/USER_GUIDE.md` — end-to-end player guide covering every current major screen/workflow.
- `docs/PROJECT_STRUCTURE.md` — repository/module/package/test/schema/workflow map and change-placement rules.
- `docs/CI_CD.md` — GitHub Actions gates, exact-head policy, scripts, release artifacts, and failure triage.
- `docs/DATA_FORMATS.md` — board strings, `SNP1`, `SNB1`, DataStore, active-game, Room, Android backup, and compatibility rules.
- `docs/PERFORMANCE.md` — main-thread, solver/generator/hints, parser, Room/DataStore, Compose, memory, measurement, and ANR hardening guide.
- `docs/MAINTAINER_GUIDE.md` — maintainer priorities, review/triage, dependency/security/localization/accessibility/release maintenance, and handoff rules.
- `docs/KEYBOARD_SHORTCUTS.md` — hardware keyboard behavior and release QA expectations.
- `docs/GLOSSARY.md` — canonical Sudoku, logical-technique, data, Android, build, and repository terminology.
- `docs/DOCUMENTATION_STANDARDS.md` — rules for implemented/planned/verified claims, links, security/privacy, persistent formats, and release documentation accuracy.

### Existing documentation corrected/expanded

- `docs/TESTING.md` now reflects the actual cumulative engine/JVM/Compose/Room/security/translation/lint/release/manual QA strategy rather than saying connected UI coverage is only planned.
- `docs/SUDOKU_ENGINE.md` now documents all supported v0.8 techniques, teaching traces, practice catalog, HintEngine hardest-technique identity, Reveal fallback, `SNP1`, and current engine invariants rather than calling advanced techniques future work.
- `docs/BACKUP_RESTORE.md` now documents the implemented `SNB1` user export/import path rather than describing it as planned.
- `docs/PRIVACY.md` now reflects current Preferences DataStore plus Room History/Saved/Challenge storage, learning progress, explicit sharing/export actions, user backup behavior, Android backup rules, and permission surface.
- `docs/SECURITY.md` now reflects actual `SNP1`/`SNB1` validation, bounded file I/O, Room/DataStore integrity, repository secret guard, signing, supply-chain review, release R8, logging, and privacy boundaries.
- `docs/ACCESSIBILITY.md` now includes selected-cell semantics, advanced teaching evidence, connected semantics regression coverage, Learn/dialog/font/contrast/motion/keyboard/large-screen checks, and explicit automated-vs-manual evidence rules.
- `docs/QA_MATRIX.md` now covers the complete current application, all supported logical hint techniques, Learn, Daily/Weekly Challenges, History/Saved, `SNP1`, `SNB1`, Room/DataStore, performance, release builds, accessibility, security/privacy, and store assets.
- `docs/RELEASE_CHECKLIST.md` now matches v0.9 security/translation/debug+release lint/R8/AAB/API-35 exact-head gates plus complete manual release requirements.
- `docs/RELEASING.md` now defines the end-to-end controlled release flow from scope freeze through exact-head verification, secure signing, R8 smoke QA, manual QA, tag/GitHub Release/store submission, monitoring, and fix-forward handling.
- `docs/README.md` is now the categorized documentation hub with user, contributor, engine, data, accessibility, QA, maintainer, and release paths.
- root `README.md` now exposes the complete documentation hub and high-value references directly from the repository landing page.

### Documentation accuracy defect found and fixed

During final cross-check, several new guides used the intended public command name `scripts/verify_no_secrets.py` while the existing implementation file was `scripts/verify_repository_security.py`.

Rather than duplicating security logic, a stable compatibility entry point `scripts/verify_no_secrets.py` was added that delegates to the existing verifier `main()` function. Standard Android CI was then updated to call the documented entry point. The authoritative security checks remain implemented only once in `verify_repository_security.py`.

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

No manual device, TalkBack, signed-production, Play Store publication, or final v0.9 exact-head green workflow claim is made by this documentation pass. Because this log update itself creates a new head, required v0.9 final workflow evidence must still be taken from the later exact final head before PR #25 is promoted/merged.

## v0.9 Runtime, Accessibility, and Localization Audit — 2026-08-18

The next hardening pass audited UI-facing asynchronous work, stale-result handling, selected-state semantics, and localization boundaries. The work intentionally fixed defects found by source/CI evidence instead of marking broad manual QA items complete.

### API-35 regression found and fixed

API-35 instrumentation run `32129482037` on head `371ffc95f12617bd4ac116eeab4e83837f5cd7a3` failed one connected test: `MainActivityTest.gameBoardExposesSelectedCellSemantics` attempted to assert `sudoku-cell-0-0` immediately after tapping Easy while puzzle generation was still asynchronous.

The test contract was not weakened. Commit `e77a1cc716c89232fccf00169df4fef98a27e3c0` (`fix(androidTest): wait for generated game board semantics`) now waits for the stable first-cell semantics tag to enter the Compose tree, then performs the same selected/unselected assertions.

### Main-thread and stale-result fixes

- `8aae5673cfd882b42ede4697241138aa71e548e7` — `fix(room): align migration parameter with Room API`; removes a Room override naming warning without changing migration SQL/schema behavior.
- `95d81234a40f0eb6afb336a98e3089165e23aff2` — `perf(custom): move puzzle solving off main thread`; Custom Puzzle uniqueness validation and solution preview now use `Dispatchers.Default`, cancellation, and stale-board checks.
- `92090f7b012ee148ca104f36e0598315f206d372` — `fix(transfer): discard stale puzzle validation results`; imported puzzle uniqueness results are not published after the input has changed.
- `52c844b115337bc35d4de77039b961c9ccb238d6` — `fix(transfer): preserve busy state during text edits`; text edits no longer clear a busy state owned by unrelated backup/restore work.

The state-layer source review covered Game, Custom Puzzle, Transfer, Challenges, History, Saved Puzzles, Learn, Statistics, Home, and app/settings state. CPU-heavy game generation/hints, Custom Puzzle solving, and imported-puzzle uniqueness analysis are dispatched away from the UI thread; backup document I/O remains on `Dispatchers.IO`. This is a source audit, not a measured device-performance claim.

### Custom Puzzle accessibility hardening

- `a8bbfde1b96a38f5d68a70bfaa3d925e8b5a2669` — `a11y(custom): expose editor cell semantics`; editor cells now expose localized row/column/value descriptions, conflict descriptions, selected semantics, and stable `custom-sudoku-cell-<row>-<column>` tags.
- `7d2917ac69bbb54fc588220034d122c90bf09713` — `test(android): cover custom editor selected semantics`; connected coverage verifies the initial selected cell and selected/unselected transition after clicking another editor cell.

### Custom Puzzle localization hardening

Paired v0.9 resources were added for all validation/solve states:

- `ef450ae0313eb0482933adf03073a8975a4815bd` — `feat(i18n): add custom puzzle status resources`;
- `01b6c245f8512f632a0355454b396b9c65c0ee28` — `feat(i18n): add Hindi custom puzzle status parity`.

`6cb7991a1480f6ebecad4abd5d0fa4004c6548f1` (`refactor(custom): move status prose out of ViewModel`) replaced raw English `String` state with typed `CustomPuzzleMessage` values. `548ac91e21132cb790f79b1610751847c6ff72dc` (`feat(i18n): localize custom puzzle status presentation`) maps those values to Android resources in Compose.

### Game error/completion localization hardening

- `25e57e7193b8948bf470f749d57bcde8dabcdae4` — `feat(i18n): add game error status resources`;
- `91cd04c15d588d04904c348e1c6fe7adecfc4acd` — `feat(i18n): add Hindi game error status parity`;
- `c3be90fa144194b6f6ccd3c7a0eca4ef90ebe1fe` — `refactor(game): expose typed localized error states`; game load/abandon state no longer forwards raw exception prose;
- `0bf5f130a8863663b718ee5641cc2e73326bb4da` — `feat(i18n): localize typed game error presentation`; a separate Compose presentation mapper resolves typed game errors to paired resources;
- `2827ba6bc9d1e4bd1981922cf844cb6a68a73bc4` — `fix(i18n): localize game completion summary`; removes concatenated English `mistake(s)` / `hint(s)` fragments and uses the maintained completion-summary resource.

### Audit documentation and roadmap

- `3171df6d1de83a936040196b39c978f8d4d293c0` — `docs(changelog): record additional v0.9 hardening fixes`;
- `95a1e6cd7a2aed77b545bc44692ae8e3b8007661` — `docs(roadmap): track additional v0.9 audit fixes`;
- `1142351450229b13eeaa768330f8baad3cdee9c8` — `docs(audit): record v0.9 hardening findings` adds `docs/V09_HARDENING_AUDIT.md` with findings, fixes, and explicit manual-evidence exclusions;
- `f187f8b68811cca7af396170e927d918a5fb94f9` — `docs(index): link v0.9 hardening audit`.

Manual TalkBack traversal/focus-order, representative 200% font/device layout checks, high-contrast/reduced-motion device QA, measured memory/frame/ANR traces, signed-production artifact checks, and store-readiness checks remain unclaimed. Final exact-head Android CI/API-35 evidence is also still pending after this log commit.

## Commit Policy

Project-authored work continues to use focused Conventional Commit-style messages (`feat:`, `fix:`, `test:`, `testability:`, `a11y:`, `perf:`, `docs:`, `build:`, `ci:`, `chore:`, `refactor:`) rather than one giant implementation commit.

This file must continue to record only verification, merge, device, accessibility, security, and release claims that actually occurred.
