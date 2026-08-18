# Accessibility

Accessibility is a release requirement for SudokuNova, not an optional polish step.

## Implemented Foundation

Current code includes:

- Semantic descriptions for Sudoku cells including row, column, value, original-clue state, and conflict state.
- Teaching-hint semantics for source cells, target cells, final placement targets, and exact candidate eliminations.
- Material components for controls where practical.
- Responsive layouts for narrow and wider screens.
- Light/dark/dynamic-color support.
- High-contrast and reduced-motion preference foundations.
- Pause state represented with readable text rather than visual effect alone.
- Hardware-keyboard movement, number entry, erase, notes, and hint shortcuts on the game board.

These foundations do not replace real assistive-technology testing.

## Required Release Checks

### TalkBack

Verify:

- Every interactive cell is discoverable.
- Cell descriptions are concise but sufficient.
- Original clues are distinguishable from editable cells.
- Conflict/error state is announced.
- Teaching-hint source/target roles are announced when a hint is open.
- Candidate elimination targets announce which candidates should be removed.
- The final hint placement announces both the target cell and value.
- Number pad and game actions have clear labels.
- Focus order remains logical.
- Dialogs move focus correctly and return it appropriately.

### Font Scaling

Test large system font sizes. Text should not overlap or make critical controls unreachable. Board cells should prioritize value readability and avoid scaling note text beyond what the grid can support.

The v0.8 Learn screen is scrollable and keeps lesson/practice actions inside responsive cards so technique content remains reachable with larger text.

### Contrast

Check:

- Clues vs. user entries
- Selection vs. peer highlight
- Error state
- Notes
- Teaching source vs. teaching target vs. placement target
- Disabled actions
- Light and dark themes
- Dynamic colors
- High-contrast preference

Do not communicate correctness, error, or teaching evidence only by color. Teaching evidence is duplicated in content descriptions and localized hint text.

### Touch Targets

Controls should aim for Android-recommended touch target sizing. Dense Sudoku cells are constrained by the board itself; surrounding actions must remain comfortably tappable.

### Motion

Non-essential animations should respect reduced-motion preference when animation is expanded in future milestones.

### Keyboard / Large Screens

The game board supports hardware-keyboard navigation and actions. Verify focus visibility, arrow behavior, number input, erase, notes toggling, and hint shortcuts on tablet/Chromebook-sized windows.

## v0.8 Teaching Evidence Semantics

When a `SudokuHint` is present, `SudokuBoardView` derives the union of evidence from its teaching chain.

For each affected cell, semantics can announce one or more of:

- teaching source;
- teaching target;
- candidate elimination target and candidate list;
- final hint placement target and value.

Visual emphasis uses Material theme roles and borders, but semantic descriptions remain the authoritative non-color representation. Conflicts keep presentation priority over hint emphasis so an error is not visually hidden by a teaching state.

Reveal fallback has a final placement target but no fabricated logical source cells or eliminations.

## Issue Reporting

Use the Accessibility issue template in `.github/ISSUE_TEMPLATE/accessibility.yml` with device, Android version, assistive technology, barrier description, and reproduction steps.

## Release Checklist

Before stable release verify at minimum:

- TalkBack on a representative device/emulator
- 200% font scaling where practical
- Light/dark contrast
- High-contrast mode
- Reduced motion
- Portrait and landscape/window resize where supported
- Hardware-keyboard operation on a large-screen target
- Standard phone and tablet
- Minimum supported Android version
- Hint evidence with both a direct placement and an elimination chain
- Learn lesson/practice dialogs

## v0.4 High-Contrast and Localized Semantics

The High Contrast setting affects the playable Sudoku board. It increases major/minor grid strength, emphasizes selected/conflict borders, strengthens peer-cell distinction, and increases note emphasis.

Sudoku cell semantic descriptions are Android resources and have English/Hindi variants. They announce row, column, empty/value state, original clue state, and conflict state without relying on color alone.

## Verification

The connected Android workflow runs Compose instrumentation on API 35. It is a regression gate, not a substitute for manual TalkBack testing. Accessibility regressions discovered manually should receive dedicated tests when practical.
