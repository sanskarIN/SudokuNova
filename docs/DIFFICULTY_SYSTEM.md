# Difficulty System

SudokuNova exposes seven labels:

1. Beginner
2. Easy
3. Medium
4. Hard
5. Expert
6. Master
7. Extreme

## Current Implementation

Each `Difficulty` currently defines:

- A target clue-count range used during generation.
- A minimum scoring threshold.

`DifficultyRater` combines:

- Number of empty cells
- Solver guesses
- Backtracks
- Maximum recursion depth

The score is then mapped to a difficulty label using configured minimum thresholds.

## Important Limitation

The current scoring system is a development-stage approximation. It does **not** yet fully model how a human solver experiences a puzzle, and clue count is not treated as sufficient evidence by itself.

For a stable release, difficulty should increasingly reflect the logical techniques needed, branching pressure, and measured calibration against a deterministic puzzle corpus.

## Calibration Plan

Future work should:

- Instrument logical solving techniques.
- Record the hardest required technique.
- Use deterministic benchmark puzzles for every difficulty.
- Compare generated puzzle distributions rather than isolated examples.
- Avoid changing labels casually after public release without migration/communication.
- Test edge cases where low clue count is still logically simple or high clue count is unexpectedly difficult.

## Generator Contract

A requested difficulty is a target; uniqueness and valid Sudoku rules always take priority. A generator must never accept an invalid or non-unique puzzle merely to satisfy a score/clue target.

## Testing

At minimum, tests should cover:

- Every difficulty enum value can be generated.
- Generated puzzles are valid.
- Generated puzzles have one solution.
- Seeded generation is deterministic.
- Rating thresholds map monotonically as intended.
- Future technique classifiers have technique-specific regression puzzles.
