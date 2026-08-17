# Hardware Keyboard Shortcuts

SudokuNova v0.2 adds keyboard shortcuts for tablets, Chromebooks, desktop-mode Android devices, and other Android devices with a hardware keyboard.

When the playable game screen is ready, it requests keyboard focus so common solving actions can be used without tapping the on-screen controls.

## Shortcuts

| Key | Action |
|---|---|
| `1`–`9` | Enter/select Sudoku number according to the configured input mode |
| Numpad `1`–`9` | Enter/select Sudoku number according to the configured input mode |
| `Backspace` | Erase the selected editable cell |
| `Delete` | Erase the selected editable cell |
| `N` | Toggle Notes mode |
| `H` | Request a hint |
| `P` | Pause or resume the game |
| `Ctrl+Z` | Undo |
| `Ctrl+Y` | Redo |

## Input Modes

### Cell first

1. Select a Sudoku cell.
2. Press `1`–`9` to enter that value.

### Number first

1. Press `1`–`9` to choose a number.
2. Tap editable cells to place that selected number.
3. Press the same number again to clear the number selection.

When Notes mode is enabled, number input changes candidate notes instead of committing a final value.

## Accessibility and Focus

Keyboard support supplements touch and TalkBack interaction rather than replacing them. Release QA should verify:

- Visible/understandable focus behavior on large-screen devices.
- Shortcuts do not interfere with dialogs or text-entry screens.
- Number input respects original clues and game status.
- Undo/redo behavior matches touch controls.
- RTL presentation remains correct.

Keyboard behavior should be included in tablet/Chromebook QA before a stable release is declared.
