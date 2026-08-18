# SudokuNova Hardware Keyboard Reference

SudokuNova includes hardware-keyboard support for the playable Sudoku board. This is useful on tablets, Chromebooks, desktop-style Android environments, and phones with attached keyboards.

## Supported Game Actions

The current game input handling supports keyboard actions for:

- moving the selected cell with arrow keys;
- entering digits `1` through `9`;
- erasing/clearing an editable cell;
- toggling Notes mode;
- requesting a hint.

Exact key-event mapping is owned by the game UI input handler and should remain covered by regression/manual keyboard QA when changed.

## Arrow Navigation

Use the arrow keys to move the selected cell:

- `↑` — previous row;
- `↓` — next row;
- `←` — previous column;
- `→` — next column.

Navigation must stay within the 9×9 board. It must not produce an invalid selected index.

## Number Entry

Number keys `1`–`9` select/enter Sudoku values according to the current input mode and Notes state.

Original clue cells remain non-editable even when input comes from a keyboard.

## Erase

The keyboard erase action clears an editable value or notes according to the same game rules used by the on-screen Erase control.

It must not erase original clues.

## Notes

The keyboard Notes shortcut toggles candidate-note entry. Once Notes mode is active, digit input toggles candidates for the selected editable cell.

The visual Notes control and keyboard state should remain synchronized through the same game state.

## Hint

The keyboard Hint shortcut requests the same logical/Reveal hint flow used by the visible Hint action.

Hint computation is performed away from the main thread in the v0.9 hardening line and stale computed results are discarded if the board changes before the hint is published.

## Accessibility Expectations

Keyboard support is part of the accessibility/large-screen quality surface.

Release QA should verify:

- visible selected-cell state;
- selected semantic state;
- logical arrow movement;
- no focus/input trap;
- clue protection;
- digit entry;
- erase;
- Notes toggle;
- Hint action;
- behavior in portrait/landscape or resizable windows where supported.

## Changing Shortcuts

When adding or changing key mappings:

1. keep the on-screen action available; keyboard shortcuts are an enhancement, not the only path;
2. avoid conflicts with common system/navigation shortcuts;
3. update this file;
4. update accessibility documentation if the interaction model changes;
5. add automated coverage where the key-event path can be tested reliably;
6. perform manual hardware-keyboard QA before claiming release verification.

## Troubleshooting

If keyboard input does not work:

- click/tap the game board to ensure the app/game surface is active;
- verify the game is not paused/completed;
- verify the selected cell is editable for edit actions;
- test the on-screen equivalent to distinguish keyboard input from game-state issues;
- confirm the Android emulator/device is receiving the physical keyboard rather than consuming keys in host controls.
