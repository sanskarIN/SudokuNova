# Sudoku Engine

The `sudoku-engine` module contains the correctness-critical Classic 9×9 Sudoku logic and has no Android dependency.

## Board Representation

`SudokuBoard` stores 81 integer cells:

- `0` = empty
- `1..9` = placed value

Public operations return new board instances rather than exposing mutable internal storage. The board can be parsed from or serialized to an 81-character puzzle string, where `0` or `.` represent empty cells during parsing.

## Validation

The board validates Sudoku constraints across:

- 9 rows
- 9 columns
- 9 standard 3×3 boxes

`hasConflict(index)` identifies a duplicate non-zero value involving a particular cell. Candidate calculation removes values already present in the cell's row, column, and box.

## Solver

`SudokuSolver` uses recursive backtracking with a minimum-remaining-values choice: at each step it prefers the empty cell with the fewest candidates. This reduces unnecessary branching compared with always scanning left-to-right.

The solver can:

- Reject an invalid starting board
- Find a solution
- Count solutions up to a caller-provided limit
- Support uniqueness validation by searching for up to two solutions
- Collect basic search metrics such as visited nodes, guesses, backtracks, and maximum depth

Uniqueness is true only when exactly one solution is discovered under a two-solution search limit.

## Generator

`SudokuGenerator`:

1. Builds a complete valid grid using randomized legal placement.
2. Removes clues in randomized order.
3. Retains each removal only if the puzzle still has a unique solution.
4. Stops near the target clue range for the requested difficulty.
5. Rates the puzzle using the current scoring model.

Generation accepts a seed so tests and Daily Challenge behavior can be deterministic.

## Hint Engine

The current hint engine supports:

- Naked Single
- Hidden Single in row/column/box
- A stronger solver-backed reveal fallback

Advanced strategies are planned and should be added only with correctness tests and clear explanations.

## Testing Expectations

Engine changes should normally cover:

- Valid and invalid boards
- Candidate calculation
- Known puzzle solving
- No-solution behavior
- Multiple-solution/uniqueness behavior
- Deterministic seeded generation
- Unique generated puzzles
- Technique-specific hint behavior as the hint system expands

## Variant Design

Future variants should not be implemented as scattered `if (variant)` checks throughout the Classic engine. Variant constraints should be represented at the engine/domain boundary so validation, solving, generation, and UI can share a coherent rules model.
