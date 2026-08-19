# SudokuNova Performance and ANR Hardening

Performance work in SudokuNova is correctness-sensitive. A faster implementation is not acceptable if it weakens unique-solution checks, teaching evidence, backup validation, persistence integrity, or accessibility.

## Performance Goals

The project should aim for:

- responsive input and navigation;
- no avoidable long-running CPU work on the Android main thread;
- no avoidable blocking file/database work on the main thread;
- bounded memory use for imported/exported data;
- deterministic, reasonably bounded solver/generator behavior;
- stable scrolling/layout behavior across phones and larger windows;
- release builds that remain functionally correct under R8/resource shrinking.

These are engineering goals, not fabricated benchmark claims. Concrete timing/device numbers should be recorded only when measured.

## Main-Thread Rules

The Android UI thread should primarily perform UI/state coordination.

Potentially expensive work should use an appropriate coroutine dispatcher:

- CPU-heavy solver/generator/hint analysis → `Dispatchers.Default`;
- file/database/import/export I/O → `Dispatchers.IO` where the called API can block;
- Compose state publication/navigation → main thread/lifecycle-aware coroutine context.

Current examples include off-main-thread puzzle generation, custom-puzzle solving, transfer validation, backup I/O, and v0.9 hint computation hardening.

## Hint Computation

Teaching/hint analysis can search candidate patterns and may fall back to solver work. It should not block taps/rendering on the main thread.

The v0.9 game ViewModel launches hint computation on `Dispatchers.Default` and verifies that the active board has not changed before publishing the computed hint. This stale-result protection prevents a delayed hint from being applied to a different board state.

When modifying hint logic, preserve:

- off-main computation;
- stale board/state protection;
- cancellation when the ViewModel is cleared;
- correctness of the final placement.

## Generator

`SudokuGenerator` can be computationally expensive because clue removal repeatedly checks unique solvability and then evaluates difficulty.

Rules:

- call generation away from the main thread;
- use fixed seeds in tests/benchmarks;
- preserve unique-solution validation;
- do not replace uniqueness checks with a clue-count shortcut;
- investigate pathological seeds instead of hiding them with arbitrary success claims;
- keep deterministic corpus coverage.

## Solver

`SudokuSolver` uses a minimum-remaining-values search strategy and collects metrics.

Performance-sensitive changes should compare:

- visited nodes;
- guesses;
- backtracks;
- maximum depth;
- solution-count correctness.

A solver optimization must still correctly distinguish invalid, unsolvable, unique, and multiple-solution boards.

## Teaching Pipeline

`TeachingStepFinder` maintains a deterministic candidate-state pipeline for supported logical strategies.

Performance changes must not:

- reorder technique priority unintentionally;
- produce different evidence for identical deterministic inputs without a documented reason;
- remove a candidate that equals the unique solved value;
- apply unsupported/fabricated logical explanations;
- create unbounded step loops.

Trace generation has an explicit maximum-step bound and tests should retain that safety property.

## Puzzle Import

`SNP1` puzzle codes have a hard maximum length and strict parser structure.

The decode itself is cheap, but imported play still performs unique-solution analysis. That analysis belongs off the main thread in Android presentation/state code.

Never skip unique-solution validation merely to make import feel faster.

## Backup Import/Export

Backup hardening focuses on both performance and memory safety.

Current boundaries include:

- `2 MiB` maximum backup size;
- bounded history/saved/challenge record counts;
- bounded lines and text fields;
- bounded stream reads before string creation;
- background I/O/restore work.

Do not replace bounded streaming with unbounded `readText()`/`readBytes()` on user-provided content.

## Room

Room work is asynchronous through suspend/Flow APIs and the repository must avoid enabling main-thread database queries.

Schema/index design should match query patterns. Current indexes cover important history/saved/challenge lookup/sort fields.

When a query becomes hot or a table grows materially:

1. inspect the SQL/DAO;
2. verify an appropriate index exists;
3. avoid loading columns/rows that are not needed;
4. keep Flow collectors lifecycle-aware;
5. benchmark with realistic record counts;
6. add migration tests if schema changes.

## Preferences DataStore

DataStore is suitable for small preference/state values, not large archives.

Keep large structured histories in Room and bounded export formats rather than adding increasingly large serialized blobs to preferences.

Avoid rapid redundant writes from every UI recomposition. Persist state at deliberate state-change/timer intervals.

## Active Game Persistence

SudokuNova periodically saves an active game rather than persisting every elapsed second.

When adjusting persistence frequency, balance:

- resume fidelity;
- write amplification;
- battery/I/O overhead;
- lifecycle safety.

Explicit user actions that materially change game state should continue to persist as appropriate.

## Compose Performance

Compose guidance for this project:

- hoist state to ViewModels/routes rather than duplicating state in deep composables;
- use stable keys/tags where lists/tests require stable identity;
- avoid heavy solver/database/file work inside composables;
- avoid allocating large transformed collections on every frame when the value can be derived/memoized at a higher state boundary;
- keep LazyColumn content lazy;
- do not use animation when reduced-motion behavior would be violated;
- verify large-font and narrow-width layouts without excessive nested scrolling.

## Sudoku Board Rendering

The 9×9 board is fixed-size in cell count, which is favorable for predictable rendering. Performance changes should preserve:

- crisp major/minor grid lines;
- semantic nodes for interactive cells;
- readable notes;
- high-contrast borders;
- selection/conflict/hint priority.

Do not merge the entire board into one inaccessible canvas solely for rendering speed.

## Memory Safety

Avoid retaining:

- Activity/Context references in long-lived singleton/domain objects;
- unbounded undo history;
- unbounded imported text;
- unbounded generated puzzle corpora at runtime;
- large bitmap assets when vector resources are sufficient.

The active game undo stack is explicitly bounded.

## Release Build Performance

Release builds use R8/minification and resource shrinking.

Before stable release verify:

- release APK launches;
- navigation works;
- Room/DataStore models survive shrinking;
- reflection/serialization assumptions are covered by consumer/proguard rules where needed;
- backup/import still works;
- teaching/hints work;
- no resource referenced dynamically is removed.

A debug-only pass is insufficient.

## Macrobenchmark Harness

SudokuNova now includes a dedicated `:macrobenchmark` module and a release-like `benchmark` app build type.

The benchmark variant:

- is initialized from `release`;
- inherits release R8/resource shrinking behavior;
- remains non-debuggable;
- uses debug signing only so local benchmarking does not require production signing credentials;
- enables `<profileable android:shell="true">` only through `app/src/benchmark/AndroidManifest.xml`, so this profiling declaration is not added to the production release manifest.

The benchmark suite currently measures:

- cold startup timing;
- warm startup timing;
- frame timing during cold startup.

Standard CI compiles the harness with:

```bash
./gradlew :macrobenchmark:assembleBenchmark --stacktrace
```

Physical-device release evidence is collected separately with:

```bash
./gradlew :macrobenchmark:connectedBenchmarkAndroidTest --stacktrace
```

CI compilation proves the harness remains buildable. It does not convert hosted-emulator output into real performance evidence.

See [Performance Benchmarking and Evidence](PERFORMANCE_BENCHMARKING.md) for target requirements, commands, evidence fields, interpretation rules and the Baseline Profile boundary.

## Measurement Strategy

When collecting real performance evidence, record:

- exact commit SHA;
- build type;
- Android version;
- device/emulator model;
- warm/cold state;
- test input/seed;
- sample count;
- metric definition;
- raw results or artifact location.

For solver/generator measurements, fixed seeds/puzzles are mandatory for useful comparison.

For Android startup/frame evidence, use the committed Macrobenchmark harness on a representative physical target and retain the generated output/traces with the exact release-candidate commit.

## Regression Thresholds

Do not invent timing thresholds without a measured baseline. Prefer initial deterministic functional/complexity metrics, then introduce wall-clock thresholds only when the CI runner variance and expected runtime are understood.

A benchmark that flakes because of shared-runner noise is not a reliable quality gate.

## ANR/Crash Review Checklist

For each release candidate inspect:

- solver/generator/hint calls from UI paths;
- file reads/writes;
- Room access;
- backup restore;
- lifecycle/recreation flows;
- long loops without cancellation/bounds;
- coroutine jobs surviving stale state;
- exceptions from malformed external input;
- large allocations from import/export;
- release-only shrinking behavior.

## Performance Bug Reporting

A useful report includes:

- app version/commit if known;
- Android version/device;
- screen/action;
- exact puzzle/seed/backup when safe to share;
- whether the problem reproduces after restart;
- approximate duration/frequency;
- log/trace information with private data removed.
