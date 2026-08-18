# Learning, Teaching Steps, and Advanced Hints

SudokuNova v0.8 treats learning and hints as a correctness-sensitive extension of the Sudoku engine. The Android UI does not invent solving explanations. It renders structured evidence produced by the platform-independent `sudoku-engine` module.

## Design goals

- Keep the solving evidence deterministic.
- Keep engine evidence free of Android resources and raw UI strings.
- Never label a technique as supported unless an implementation and correctness tests exist.
- Prefer a logical teaching chain over a direct reveal.
- Keep Reveal visibly separate as the stronger fallback.
- Store learning progress locally; no account or cloud service is required.
- Make source cells, target cells, placements, and candidate eliminations understandable without relying on color alone.

## Structured teaching evidence

The core model is `TeachingStep`.

Each step records:

- `technique`: the supported logical technique.
- `sourceCells`: cells that establish the pattern.
- `sourceUnit`: row, column, or 3×3 box when one unit is the direct source.
- `targetCells`: cells changed or resolved by the step.
- `candidateEliminations`: exact `(cellIndex, candidate)` removals.
- `placement`: optional final `(cellIndex, value)` placement.

A step must contain either at least one candidate elimination or a placement. Cell indices and Sudoku values are validated when the evidence object is created.

`TeachingStepFinder` maintains the candidate state needed for chained elimination logic. Candidate eliminations are applied to that internal state, while placements update the immutable `SudokuBoard` and remove the placed value from peers.

## Supported techniques in v0.8

The deterministic teaching pipeline checks techniques in this order:

1. Naked Single
2. Hidden Single
3. Naked Pair
4. Pointing Pair / Triple
5. Box-Line Reduction
6. Hidden Pair
7. Naked Triple
8. Hidden Triple
9. X-Wing

The order is intentional. Simpler evidence is preferred when more than one supported logical move is available.

### Naked Single

A cell has exactly one remaining candidate. The teaching step places that candidate in the cell.

### Hidden Single

Within one row, column, or box, a digit can appear in exactly one candidate cell. The teaching step identifies the source unit and places that digit.

### Naked Pair

Two cells in the same unit contain the same two candidates. Those candidates are removed from other cells in that unit.

### Pointing Pair / Triple

Inside one box, every remaining candidate for one digit lies in the same row or column. That digit is removed from the matching line outside the box.

### Box-Line Reduction

Within one row or column, every remaining candidate for one digit lies in the same box. That digit is removed from other cells in that box.

### Hidden Pair

Two digits are restricted to the same two cells within a unit. Other candidates are removed from those two source cells.

### Naked Triple

Three cells within a unit contain candidates drawn from the same three-digit set. Those three digits are removed from other cells in the unit.

### Hidden Triple

Three digits are restricted to the same three cells within a unit. Candidates outside those three digits are removed from the source cells.

### X-Wing

For a candidate digit, two rows can each contain exactly the same two candidate columns. The digit is then removed from the other cells in those columns. The implementation also supports the transposed form using two columns and matching rows.

## Hint behavior

`HintEngine` asks `TeachingStepFinder` for a deterministic trace from the current valid board.

If the trace reaches a supported placement, the hint contains the teaching steps through that placement. This means an advanced hint can first show one or more candidate eliminations and then the logical placement enabled by them.

If the supported teaching pipeline cannot reach a placement, `HintEngine` may use the exact solver to produce an explicit Reveal fallback. Reveal is represented separately from `TeachingStep`; it is not misreported as a logical technique.

Invalid boards and completed boards fail closed and return no hint.

## Game-board presentation

While a hint is open, the game board receives the pending `SudokuHint` and derives:

- all source cells in the teaching chain;
- all target cells;
- the final placement cell and value;
- candidate eliminations grouped by target cell.

The Compose board visually emphasizes those roles and adds content descriptions describing source, target, placement, and eliminated candidates. Conflict presentation still has priority over teaching emphasis.

All player-facing technique names and explanations are Android string resources. The engine contains no English/Hindi explanation text.

## Learning Center

The Learn screen includes:

- overall local mastery;
- mastered-technique count;
- total practice results;
- per-technique mastery progress;
- localized lesson dialogs;
- deterministic offline practice exercises;
- local progress reset controls.

The original introductory Sudoku, candidate, and solving-habit lessons remain available alongside the nine technique lessons.

## Practice model

`TeachingPracticeCatalog` is platform-independent and deterministic. Every supported `LogicalTechnique` has at least one structured practice exercise.

A `TeachingPracticeExercise` contains:

- a stable exercise id;
- a valid `TeachingStep` evidence object;
- unique answer choices including the correct technique.

The Android `LearnViewModel` owns the interactive state:

- unanswered;
- answered/correct;
- answered/incorrect;
- next exercise;
- close practice.

Only the first submitted answer for an exercise is recorded. Repeated taps after answering do not create additional attempts.

## Local learning progress

Learning progress is stored in the existing Preferences DataStore. Each supported technique has counters for:

- lesson views;
- practice attempts;
- successful practice answers.

`TechniqueLearningProgress` derives a bounded mastery percentage from lesson exposure, practice depth, and accuracy. A technique is not marked mastered until it has at least three attempts, at least three successes, and sufficient derived mastery.

Counters are bounded to prevent integer overflow. Resetting learning progress removes only learning counters; game history, saved puzzles, active games, statistics, and user settings are not changed.

No account, analytics service, network permission, or cloud storage is required.

## Correctness tests

The v0.8 engine test suite contains multiple layers.

### Trace determinism and solution safety

For known and generated puzzles, teaching traces are repeated and compared. Every placement must equal the unique solved value. Every candidate elimination must differ from the unique solved value for its cell.

### Advanced-technique pattern tests

Controlled candidate states are required to remain subsets of legal Sudoku candidates. Dedicated tests validate:

- Hidden Pair evidence;
- Naked Triple evidence;
- Hidden Triple evidence;
- row-oriented X-Wing evidence;
- rejection of impossible candidate overrides.

This test-only probe goes through the same production candidate-state detector and technique ordering.

### Practice catalog tests

The catalog must cover every supported technique, remain deterministic, include the correct answer, reject incorrect answers, and expose elimination targets for elimination exercises.

### Android tests

The app test suite covers the pure learning-progress model. Compose instrumentation covers reaching the Learn center, opening a technique lesson, starting practice, submitting a correct answer, and displaying the result.

## Localization

The v0.8 learning/hint resources are paired in:

- `app/src/main/res/values/learning_strings_v08.xml`
- `app/src/main/res/values-hi/learning_strings_v08.xml`

`python scripts/verify_translations.py` is part of CI and must keep English/Hindi resource keys in parity.

## Verification gates

Before v0.8 is merged, the final clean head must pass:

```bash
python scripts/verify_translations.py
./gradlew :sudoku-engine:test --stacktrace
./gradlew :app:testDebugUnitTest --stacktrace
./gradlew :app:assembleDebugAndroidTest --stacktrace
./gradlew :app:lintDebug --stacktrace
./gradlew :app:assembleDebug --stacktrace
```

The repository's Android Instrumentation workflow must also pass the connected test suite on API 35 for the same final clean head.

## Future extension rule

A new solving technique should not be added to `LogicalTechnique`, the hint UI, or the learning catalog until all of the following exist:

1. deterministic detection;
2. structured source/target/elimination evidence;
3. solution-safety or equivalent correctness tests;
4. localized player-facing explanation resources;
5. practice representation when the technique is exposed in Learn.

This keeps the learning system evidence-driven rather than feature-count driven.
