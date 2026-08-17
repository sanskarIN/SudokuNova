# Puzzle Generation

## Goals

SudokuNova generation must produce valid Classic 9×9 puzzles with exactly one solution, without blocking the Android main thread.

## Current Algorithm

For a requested difficulty and seed:

1. Create a seeded Kotlin `Random` instance.
2. Generate a complete solved 9×9 grid using randomized legal candidates.
3. Shuffle the 81 cell indices.
4. Attempt to remove each clue.
5. After each removal, ask `SudokuSolver` whether the puzzle still has exactly one solution.
6. Keep the removal only when uniqueness remains true.
7. Stop when the requested clue target is reached or no more accepted removals are needed.
8. Assess the result using solve metrics and the current difficulty score.
9. Retry a limited number of seeded attempts and keep the closest successful result if an exact target is not reached.

## Determinism

Providing the same difficulty and seed should reproduce the same generated puzzle and solution under the same engine implementation. Unit tests protect this behavior for representative seeds.

The Daily Challenge derives a seed from the local calendar epoch day, allowing basic offline daily puzzle behavior without a server.

## Uniqueness

Clue count alone never proves uniqueness. Each accepted removal is checked by searching for up to two solutions. The generator accepts the puzzle as unique only when the solver reports exactly one solution.

## Performance

Android calls generation through `Dispatchers.Default` so CPU-intensive work does not execute on the main UI thread. Future profiling should add measured benchmarks before introducing caches or background pre-generation.

## Difficulty Caveat

The current generator combines target clue ranges with solver-search metrics. This is an initial model, not a complete human-technique classifier. Difficulty calibration is planned for v0.3 and must be based on evidence and regression corpora rather than labels alone.

## Regression Rules

A generator bug fix should include a deterministic seed that reproduces the failure where practical. Tests should assert validity, completion of the solution, and unique solvability of the puzzle.
