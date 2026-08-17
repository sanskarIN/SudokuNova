# Getting Started with SudokuNova

## For Players

SudokuNova's current development build focuses on Classic 9×9 Sudoku. From Home you can:

1. Choose one of seven difficulty levels.
2. Open the Daily Challenge.
3. Continue the locally saved active game when one exists.
4. Create and validate a custom Sudoku.
5. Open the learning center, statistics, settings, About, or support links.

Core play is designed to work offline. No account is required.

## Game Controls

- Tap a cell to select it.
- Tap a number from 1–9 to enter it.
- Toggle **Notes** to add/remove pencil candidates.
- **Erase** clears an editable cell and its notes.
- **Undo** and **Redo** move through in-session changes.
- **Hint** first looks for an educational supported technique, then offers a stronger reveal fallback when needed.
- **Pause** stops the local timer and obscures the board.
- **Restart** resets the current puzzle state.

Original clues cannot be edited.

## Daily Challenge

The current Daily Challenge is generated deterministically from the local calendar day and selected internal challenge difficulty. It does not require a server. The challenge archive/calendar is planned for a later milestone.

## Custom Puzzle

Enter the given clues, then select **Validate**. SudokuNova checks:

- Row contradictions
- Column contradictions
- 3×3 box contradictions
- Solvability
- Whether the puzzle has exactly one solution

Only a unique puzzle can proceed through the current **Play puzzle** flow.

## For Developers

Start with:

- `INSTALLATION.md`
- `DEVELOPMENT_SETUP.md`
- `ARCHITECTURE.md`
- `BUILDING.md`
- `TESTING.md`

Repository:
https://github.com/sanskarIN/SudokuNova

☕ https://buymeacoffee.com/sanskarIN

**Made by the Sanskar**
