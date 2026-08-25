# SudokuNova Hardware Keyboard Reference

SudokuNova supports hardware-keyboard interaction on the mature Android game and now also provides a portable keyboard/focus baseline in the shared Compose Multiplatform board.

Do not assume every shortcut is already identical on every target. This guide separates the mature Android behavior from the shared 2.0.13 behavior so repository support is not overstated.

## Mature Android Game Actions

The Android game input handling supports keyboard actions for:

- moving the selected cell with arrow keys;
- entering digits `1` through `9`;
- erasing/clearing an editable cell;
- toggling Notes mode;
- requesting a hint.

Exact Android key-event mapping is owned by the Android game UI input handler and remains subject to real hardware-keyboard QA.

## Shared Compose 2.0.13 Baseline

The shared Android/Desktop/Web/iOS Compose board now has a common focusable grid handler with these repository-backed mappings:

- `↑` — move one row up;
- `↓` — move one row down;
- `←` — move one column left;
- `→` — move one column right;
- `Backspace` — erase the selected editable cell;
- `Delete` — erase the selected editable cell.

When no shared cell is selected, the first navigation key establishes a deterministic selection at cell index `0`. Movement is clamped at board edges and cannot create an index outside `0..80`.

The shared state rejects zero-delta or multi-cell movement requests through its deterministic navigation API. Regression tests cover initial selection, ordinary movement, edge clamping, and invalid movement requests.

Digit-entry, Notes-toggle, Hint, and additional shortcut parity remain separate shared-platform work until their key mappings are implemented and compiled across all supported targets.

## Arrow Navigation

Use the arrow keys to move the selected cell:

- `↑` — previous row;
- `↓` — next row;
- `←` — previous column;
- `→` — next column.

Navigation must stay within the 9×9 board. It must not produce an invalid selected index.

## Number Entry

On the mature Android game, number keys `1`–`9` select/enter Sudoku values according to the current input mode and Notes state.

Original clue cells remain non-editable even when input comes from a keyboard.

Portable shared direct-digit key mapping is not yet claimed in 2.0.13; the visible number pad remains available on shared targets.

## Erase

The erase action clears an editable value or notes according to the same game-state rules used by the on-screen Erase control.

It must not erase original clues. The shared grid accepts both `Backspace` and `Delete` while the grid owns focus.

## Notes

The mature Android keyboard Notes shortcut toggles candidate-note entry. Once Notes mode is active, digit input toggles candidates for the selected editable cell.

The shared visible Notes action remains available and now exposes selected-state semantics when Notes mode is active. A portable Notes keyboard shortcut is not yet claimed.

## Hint

The mature Android keyboard Hint shortcut requests the same logical/Reveal hint flow used by the visible Hint action.

The shared visible Hint action remains available. Portable Hint-key mapping remains future parity work.

## Focus Behavior

The shared Sudoku grid is explicitly focusable. Keyboard events are handled only when the grid participates in focus; normal platform focus traversal should remain available outside the board.

Repository compilation can prove the common focus/key APIs build on supported targets, but it does not prove real focus traversal quality on every OS/browser/device. Manual target QA remains required before production parity claims.

## Accessibility Expectations

Keyboard support is part of the accessibility/large-screen quality surface.

Release QA should verify:

- visible selected-cell state;
- selected semantic state;
- logical arrow movement;
- no focus/input trap;
- clue protection;
- digit entry where implemented;
- erase;
- Notes state/action behavior;
- Hint action behavior;
- behavior in portrait/landscape or resizable windows where supported;
- actual Desktop/Web/Apple focus traversal before claiming shared runtime parity.

## Changing Shortcuts

When adding or changing key mappings:

1. keep the on-screen action available; keyboard shortcuts are an enhancement, not the only path;
2. avoid conflicts with common system/navigation shortcuts;
3. keep movement rules in deterministic shared state where possible;
4. update this file;
5. update accessibility documentation if the interaction model changes;
6. add automated coverage where the state/key-event path can be tested reliably;
7. compile every shared target before merging;
8. perform manual hardware-keyboard QA before claiming release verification.

## Troubleshooting

If keyboard input does not work:

- click/tap or focus the game board so the game surface can receive keys;
- verify the selected cell is editable for edit actions;
- test the on-screen equivalent to distinguish keyboard input from game-state issues;
- confirm the emulator/device/browser/desktop host is receiving the physical keyboard rather than consuming keys in host controls;
- remember that shared 2.0.13 currently claims arrow navigation plus Backspace/Delete only, not full Android shortcut parity.
