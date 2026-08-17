# Game Rules

## Classic 9×9 Sudoku

A standard Sudoku board contains 81 cells arranged as 9 rows, 9 columns, and nine 3×3 boxes.

The objective is to fill every empty cell with a digit from 1 through 9 so that:

- Every row contains each digit 1–9 exactly once.
- Every column contains each digit 1–9 exactly once.
- Every 3×3 box contains each digit 1–9 exactly once.

The starting clues are fixed and cannot be changed during normal play.

## Notes

Notes/pencil marks are player aids, not placed answers. SudokuNova stores a set of candidate digits per editable cell. When automatic note cleanup is enabled and a correct value is placed, the current implementation removes that candidate from peer cells in the same row, column, and box.

## Mistake Modes

The current settings expose:

- Unlimited mistakes (`0` limit)
- 3-mistake limit
- 5-mistake limit
- Automatic mistake checking on/off

When automatic checking is on, an entered value is compared with the known unique solution. Reaching a configured non-zero limit changes the game status to failed.

Additional validation modes described in the product roadmap are planned, not currently implemented.

## Completion

A game is complete when the current board exactly matches the unique solved board. Completion statistics are recorded once, and the active saved game is removed.

## Hints

Current hints attempt educational logic first:

- Naked Single
- Hidden Single

If neither supported technique is available, a stronger solution-backed reveal suggestion may be offered. The user chooses whether to apply it.

## Pause

Pausing stops timer advancement and hides the Sudoku board in the current UI so the player cannot continue studying cells while the timer is stopped.

## Daily Challenge

The current Daily Challenge uses the same Classic rules and a deterministic daily seed. Archive/calendar and special challenge rules are planned for later milestones.

## Custom Puzzle Rules

A custom puzzle must have valid row/column/box clues and exactly one solution before the current app enables its Play flow.
