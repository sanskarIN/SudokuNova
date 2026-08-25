# SudokuNova Hardware Keyboard Reference

SudokuNova supports hardware-keyboard interaction on the mature Android game and provides a portable keyboard/focus path in the shared Compose Multiplatform board.

Do not assume repository compilation is real hardware evidence on every target. This guide separates source-backed mappings from platform runtime QA.

## Mature Android Game Actions

The Android game input handling supports keyboard actions for:

- moving the selected cell with arrow keys;
- entering digits `1` through `9`;
- erasing/clearing an editable cell;
- toggling Notes mode;
- requesting a hint.

Exact Android key-event mapping is owned by the Android game UI input handler and remains subject to real hardware-keyboard QA.

## Shared Compose 2.0.14 Mapping

The shared Android/Desktop/Web/iOS Compose board has a common focusable grid handler with these repository-backed mappings:

- `↑` — move one row up;
- `↓` — move one row down;
- `←` — move one column left;
- `→` — move one column right;
- `1` through `9` — enter the digit using the existing game-state rules;
- `N` — toggle Notes mode;
- `H` — request a hint;
- `Backspace` — erase the selected editable cell;
- `Delete` — erase the selected editable cell.

The on-screen number pad and action buttons remain available. Keyboard support is an enhancement, not the only interaction path.

When no shared cell is selected, the first navigation key establishes a deterministic selection at cell index `0`. Movement is clamped at board edges and cannot create an index outside `0..80`.

The shared state rejects zero-delta or multi-cell movement requests through its deterministic navigation API. Regression tests cover initial selection, ordinary movement, edge clamping, and invalid movement requests.

## Arrow Navigation

Use the arrow keys to move the selected cell:

- `↑` — previous row;
- `↓` — next row;
- `←` — previous column;
- `→` — next column.

Navigation must stay within the 9×9 board. It must not produce an invalid selected index.

## Number Entry

Shared digit keys `1`–`9` call the same `SharedGameState.enter` path as the visible number pad.

Therefore existing game-state invariants still apply:

- a fixed clue cannot be overwritten;
- no selection produces the normal select-an-editable-cell status;
- normal mode places the digit;
- Notes mode toggles the candidate note;
- placed values use the existing conflict/correctness/status path;
- peer-note cleanup remains owned by shared state.

The mature Android number-first/cell-first preference model is broader than the current shared UI behavior. The 2.0.14 keyboard mapping does not falsely claim full number-first interaction parity across every target.

## Erase

The erase action clears an editable value or notes according to the same game-state rules used by the on-screen Erase control.

It must not erase original clues. The shared grid accepts both `Backspace` and `Delete` while the grid owns focus.

## Notes

`N` toggles candidate-note entry in the shared focusable grid. Once Notes mode is active, digit input toggles candidates for the selected editable cell.

The shared visible Notes action remains available and exposes selected-state semantics when Notes mode is active.

## Hint

`H` requests the same engine-backed hint path used by the shared visible Hint action.

The hint implementation remains responsible for selecting the hint cell, placing the safe value, cleaning peer notes, and publishing the localized hint status.

## Focus Behavior

The shared Sudoku grid is explicitly focusable. Keyboard events are handled only when the grid participates in focus; normal platform focus traversal should remain available outside the board.

Repository compilation can prove the common focus/key APIs build on hosted targets, but it does not prove real focus traversal quality on every OS/browser/device. Manual target QA remains required before production parity claims.

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
- Notes state/action behavior;
- Hint action behavior;
- behavior in portrait/landscape or resizable windows where supported;
- actual Android/Desktop/Web/Apple focus traversal before claiming runtime parity.

## Platform Shortcut Boundary

The shared mapping is intentionally small and game-focused. Do not expand it by intercepting common OS/browser modifier shortcuts without a reviewed cross-platform interaction requirement.

Runtime QA should specifically check that the board does not create an unacceptable keyboard trap or interfere with expected platform navigation outside the focused game surface.

## Changing Shortcuts

When adding or changing key mappings:

1. keep the on-screen action available; keyboard shortcuts are an enhancement, not the only path;
2. avoid conflicts with common system/navigation shortcuts;
3. keep movement and edit rules in deterministic shared state where possible;
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
- remember that hosted CI compilation is not proof of real target keyboard quality.
