# UI and UX

SudokuNova aims for a calm, fast Sudoku experience where the board remains the visual priority.

## Current Navigation

Implemented destinations:

- Home
- Game
- Custom Puzzle
- Learn
- Statistics
- Settings
- About

The home screen also opens the external Buy Me a Coffee support page through an explicit user action.

## Home Hierarchy

Home prioritizes:

1. Continue Game, when a valid active save exists.
2. Daily Challenge.
3. Difficulty-based Quick Play.
4. Custom Puzzle, Learn, Statistics, Settings, About, and Support.

The support action is visible but does not block or interrupt gameplay.

## Game Screen

The game screen displays:

- Difficulty and progress
- Timer, when enabled
- Mistake count/limit
- Responsive Sudoku board
- Number pad
- Undo/redo
- Eraser
- Notes mode
- Hint
- Pause/resume
- Restart

On wider layouts the board and controls are arranged side-by-side. On smaller widths the controls follow the board vertically.

## Board Visual States

The current board distinguishes:

- Original clues
- User-entered numbers
- Selected cell
- Peer row/column/box
- Same values
- Mistakes/conflicts
- Pencil notes

Important state should not rely on color alone; semantics and text/weight distinctions should continue to improve during accessibility hardening.

## Motion

Animations are intentionally minimal in the current milestone. A reduced-motion preference exists as a foundation. New animation should be subtle, optional where appropriate, and never delay input.

## Empty/Error States

Implemented flows provide direct error messaging for puzzle-generation failure and custom-puzzle validation. Richer history/favorites empty states will be added with those features.

## Destructive Actions

Statistics reset requires confirmation. Future delete/import/overwrite operations must follow the same principle when data loss is not easily reversible.

## Donations and Branding

`Made by the Sanskar` and Buy Me a Coffee attribution should remain tasteful. No watermark belongs over the playable board, and donations must never appear as a requirement for core play.

## UX Quality Checks

For visible changes, verify:

- Small phone width
- Standard phone
- Tablet/wide window
- Light and dark themes
- Dynamic color on/off
- Large text
- TalkBack semantics/focus impact
- Orientation/window resizing when relevant
- No board/control overlap or unreachable actions
