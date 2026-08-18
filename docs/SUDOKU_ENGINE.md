# SudokuNova Sudoku Engine

The `sudoku-engine` Gradle module contains the correctness-critical Classic 9×9 Sudoku domain. It intentionally has no Android dependency so validation, solving, generation, difficulty analysis, sharing formats, hints, teaching evidence, and practice logic can be tested quickly and reused independently from the Android UI.

## Module Boundary

Engine code lives under:

```text
sudoku-engine/src/main/kotlin/com/sanskar/sudokunova/engine/
```

The engine must not depend on:

- Android `Context`;
- Compose;
- Android resources;
- Room;
- DataStore;
- Android lifecycle/ViewModels;
- player-facing localized prose.

The Android `app` module translates engine evidence into localized UI presentation.

## `SudokuBoard`

`SudokuBoard` is the immutable Classic Sudoku board model.

### Representation

- 81 integer cells;
- `0` = empty;
- `1..9` = placed value;
- board size = 9;
- box size = 3.

Public board updates create a new board rather than exposing mutable internal state.

### Parsing and serialization

The board can be parsed from an 81-character representation. Numeric `0` represents empty; parsing also supports `.` as an empty marker where accepted by the board parser.

Canonical share/persistence formats generally use numeric puzzle strings.

### Validation

Board validity checks rows, columns, and standard 3×3 boxes for duplicate non-zero digits.

Important distinctions:

- valid does not mean solvable;
- solvable does not mean unique;
- a playable custom/imported/generated puzzle must pass the appropriate unique-solution gate.

### Conflicts

`hasConflict(index)` identifies whether a non-zero value participates in a row/column/box duplicate.

### Candidates

Candidate calculation returns digits legal for an empty cell under current row/column/box constraints.

Candidates are a logical state derived from the board, not the same thing as player Notes stored by the Android game state.

## `SudokuSolver`

`SudokuSolver` performs recursive backtracking with a minimum-remaining-values (MRV) choice.

At each search step it chooses an empty cell with the fewest legal candidates, reducing unnecessary branching compared with naive left-to-right search.

### Main behavior

The solver can:

- reject invalid starting boards;
- find a first solution;
- count solutions up to a caller-provided limit;
- distinguish unsolvable/unique/multiple-solution states;
- expose search metrics.

### Solution analysis

`analyze(board, solutionLimit)` requires a positive limit and returns a `SolveResult` containing:

- first solution if found;
- number of solutions discovered up to the limit;
- solver metrics.

Unique-solution validation uses a limit of at least two; exactly one discovered solution is required.

### Metrics

`SolveMetrics` records search information including:

- visited nodes;
- guesses;
- backtracks;
- maximum depth.

Metrics are useful for diagnostics/difficulty/performance analysis but do not replace correctness checks.

## `SudokuGenerator`

The generator produces deterministic puzzles when given the same difficulty/seed under the same algorithm version.

High-level process:

1. construct a complete valid solution grid;
2. consider clues for removal in seeded randomized order;
3. retain a removal only when the puzzle remains uniquely solvable;
4. continue toward the requested difficulty target;
5. rate/calibrate the result using the current difficulty system.

### Generator invariants

Generated playable puzzles must retain:

- valid clues;
- at least one solution;
- exactly one solution;
- deterministic behavior for deterministic tests/seeds.

Do not weaken uniqueness checks to improve generation speed.

## Difficulty Model

`Difficulty.kt`, `DifficultyCalibrator.kt`, and `LogicalDifficultyAnalyzer.kt` support seven difficulty targets:

1. Beginner
2. Easy
3. Medium
4. Hard
5. Expert
6. Master
7. Extreme

Difficulty is based on more than clue count. Current analysis incorporates logical/search/complexity evidence.

See `DIFFICULTY_SYSTEM.md` and `PUZZLE_GENERATION.md`.

## Logical Techniques

The shared logical pipeline supports:

1. Naked Single
2. Hidden Single
3. Naked Pair
4. Pointing Pair / Triple
5. Box-Line Reduction
6. Hidden Pair
7. Naked Triple
8. Hidden Triple
9. X-Wing

Technique ordering is intentionally deterministic from simpler to more advanced patterns.

## `TeachingStep`

A `TeachingStep` is structured evidence, not player-facing prose.

A step can record:

- logical technique;
- source cells;
- source row/column/box where applicable;
- target cells;
- exact candidate eliminations;
- optional final placement.

Evidence validation prevents invalid cell/candidate values, duplicate evidence entries, and empty “steps” with neither placement nor elimination.

This structure allows the same truth to power:

- logical solver analysis;
- hints;
- Learn content/practice;
- accessibility descriptions;
- future platform clients.

## `TeachingStepFinder`

`TeachingStepFinder` owns the deterministic candidate-state transformation pipeline.

It derives legal candidate sets, searches supported techniques, applies candidate eliminations to its internal candidate state, and applies proven placements to immutable boards.

When a placement is applied, the placed digit is removed from peer candidate sets.

### Trace

A teaching trace records:

- initial board;
- final board;
- ordered steps;
- whether the logical process solved the board;
- unresolved-cell count.

Trace generation uses an explicit maximum-step bound to avoid unbounded loops.

### Controlled candidate states in tests

Advanced-technique tests can use controlled candidate overrides, but only when overrides:

- target valid cell indices;
- target empty cells;
- are non-empty candidate sets;
- are subsets of candidates actually legal for that board.

This testability mechanism does not permit impossible Sudoku states to masquerade as valid evidence.

## `LogicalSolver`

`LogicalSolver` consumes teaching traces instead of maintaining an independent competing candidate solver.

It derives logical results such as:

- per-technique usage counts;
- candidate elimination count;
- hardest technique;
- placement count;
- unresolved count;
- final logical board.

Sharing the pipeline reduces the risk that difficulty analysis and hint explanations disagree about Sudoku truth.

## `HintEngine`

`HintEngine` uses structured teaching evidence.

Behavior:

- invalid boards fail closed;
- complete boards produce no hint;
- supported logical steps are preferred;
- a hint can contain the chain through the first supported placement;
- the reported hint technique is the hardest technique in that chain;
- the final placement remains the value/cell that the app can apply;
- Reveal remains an explicit solver-backed fallback.

### Why hardest technique matters

A chain such as Naked Pair → Naked Single is logically enabled by the pair. Reporting only “Naked Single” would hide the important reasoning. The hint therefore reports the highest-ranked technique present in the enabling chain while preserving the final placement.

## Reveal Fallback

Reveal is used only when the supported logical teaching pipeline cannot reach a placement and the solver can still provide one safely.

Reveal must not fabricate:

- source unit;
- candidate eliminations;
- unsupported logical strategy.

The Android UI should label/present Reveal separately from supported techniques.

## `TeachingPracticeCatalog`

The engine includes a deterministic offline practice catalog.

Each exercise contains:

- stable exercise ID;
- structured teaching-step evidence;
- unique answer choices;
- correct technique.

The catalog contains practice coverage for every supported logical technique.

Because the catalog lives in the engine, exercise truth can be tested without Android resources/network/account dependencies.

## `PuzzleCodeCodec`

The engine owns the platform-independent `SNP1` puzzle-share format:

```text
SNP1.<DIFFICULTY>.<81-digit-puzzle>.<CRC32>
```

The codec enforces length, version, enum, payload, checksum and board-validity constraints.

Unique solvability is checked at the Android acceptance boundary before imported play.

See `DATA_FORMATS.md`.

## Android Presentation Boundary

The engine should expose structured data such as:

- technique enum;
- source/target cell indices;
- unit information;
- eliminated candidates;
- placements.

The Android app should own:

- localized technique names;
- English/Hindi explanations;
- cell labels such as “row 2 column 5”;
- colors/borders;
- Compose dialogs;
- TalkBack content descriptions.

Do not put Android resource IDs or translated prose into `sudoku-engine`.

## Threading

Engine classes are ordinary JVM logic and do not select Android coroutine dispatchers themselves.

Callers must schedule expensive solver/generator/hint work appropriately. The Android application uses background dispatchers for CPU-heavy analysis in UI flows.

## Correctness Invariants

Engine changes must preserve:

- board immutability expectations;
- Classic row/column/box rules;
- invalid-board rejection;
- correct solution counting;
- unique generated puzzles;
- deterministic seeded behavior where promised;
- legal candidate states;
- teaching placements equal the solved value;
- teaching eliminations never remove the solved value;
- explicit Reveal separation;
- bounded logical traces.

## Testing

Engine tests live under:

```text
sudoku-engine/src/test/kotlin/com/sanskar/sudokunova/engine/
```

Run:

```bash
./gradlew :sudoku-engine:test --stacktrace
```

The suite includes board, solver, generator, difficulty, logical corpus, advanced teaching, practice and hint regression coverage.

See `TESTING.md` for the complete strategy.

## Adding a New Logical Technique

A new supported technique should normally include:

1. technique enum/ranking decision;
2. deterministic detector;
3. structured `TeachingStep` evidence;
4. candidate-state application logic;
5. direct positive test;
6. negative/legality tests;
7. solution-safety regression coverage;
8. logical solver integration;
9. hint-chain integration;
10. practice catalog coverage;
11. localized English/Hindi names/explanations;
12. accessibility presentation;
13. Learn content;
14. documentation updates.

Do not add a technique only as UI prose with no correctness evidence.

## Future Variant Architecture

Classic 9×9 rules are the current stable domain focus.

If future variants are added, avoid scattering `if (variant)` checks through Classic logic. Introduce explicit constraint/rules abstractions so validation, solver, generator and presentation can share coherent variant rules without destabilizing Classic behavior.
