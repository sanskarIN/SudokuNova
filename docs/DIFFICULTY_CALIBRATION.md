# Difficulty Calibration

SudokuNova v0.3 introduces a second, human-logic-oriented difficulty evidence layer alongside the existing search-metric rating.

## Why Two Ratings Exist During v0.3

The original development rating uses clue pressure and solver search metrics such as guesses, backtracks, and search depth. Those metrics are useful for detecting computational complexity, but they do not fully describe how a human solves a Sudoku.

The v0.3 calibration layer therefore remains **observational** rather than silently replacing the established generator acceptance rule. Generated puzzles still must be valid and uniquely solvable, and existing generation safeguards remain authoritative while the logical model gathers deterministic evidence.

## Logic-Only Solver

`LogicalSolver` never reads the completed solution grid. It applies only techniques that can be proven from the current candidate state:

1. Naked Single
2. Hidden Single
3. Naked Pair eliminations
4. Pointing Pair / Pointing Triple eliminations
5. Box-Line Reduction eliminations

When none of the supported techniques can progress, the solver stops and returns a valid partial board instead of guessing.

The result records:

- technique usage counts,
- candidate eliminations,
- hardest supported technique used,
- remaining unresolved cells,
- whether the supported logic solved the puzzle completely.

## Logical Difficulty Analyzer

`LogicalDifficultyAnalyzer` provides additional deterministic signals:

- starting number of empty cells,
- Naked Single placements,
- Hidden Single placements,
- Naked Pair signals,
- Pointing signals,
- Box-Line Reduction signals,
- unresolved cells after singles,
- a calibration-only logical score.

Pattern signals are evidence that a technique is available or structurally relevant; they do not claim it is the only valid next move.

## Combined Calibration

`DifficultyCalibrator` combines:

- the legacy search-metric score,
- hardest supported logical technique,
- unresolved cells after supported logic,
- candidate eliminations,
- logical pattern evidence.

It produces `CalibratedDifficultyAssessment`, including a suggested difficulty band. The requested difficulty and original `DifficultyAssessment` remain preserved for comparison.

## Generated Puzzle Metadata

Every newly generated puzzle now carries optional calibrated metadata through `GeneratedPuzzle.calibratedAssessment`.

This metadata can be used by tests, debug tools, future benchmark reports, and later threshold calibration without changing the public gameplay label prematurely.

## Deterministic Regression Coverage

The engine test suite includes:

- deterministic generation across every difficulty,
- validity and uniqueness across all difficulty levels,
- logical analyzer determinism,
- logic-only solver correctness against known and generated puzzles,
- calibration determinism,
- generated-puzzle calibration metadata checks.

Fixed seeds are used so a future algorithm change that moves a puzzle's behavior can be reproduced and investigated.

## Current Limitations

The logical solver does not yet implement every advanced Sudoku technique. In particular, X-Wing, Swordfish, coloring/chains, and advanced fish/wing techniques are outside the current v0.3 supported solver set.

Therefore:

- a puzzle left unresolved is not automatically impossible to solve logically,
- `ADVANCED_TECHNIQUES_LIKELY` means the currently implemented techniques were insufficient,
- the calibrated score is not yet a stable user-facing contract.

## Promotion to a Release Gate

Before the calibrated model replaces or materially changes public difficulty labels:

1. Expand the deterministic corpus across many seeds and known reference puzzles.
2. Add more supported logical techniques with correctness tests.
3. Compare score distributions per requested difficulty.
4. Review overlap and outliers.
5. Adjust thresholds using evidence rather than clue count alone.
6. Preserve user-facing label stability where practical.
7. Document any release-visible recalibration in `CHANGELOG.md`.

Correctness and unique solvability always take priority over matching a requested difficulty label.
