# Accessibility

Accessibility is a release requirement for SudokuNova, not an optional polish step.

## Implemented Foundation

Current code includes:

- Semantic descriptions for Sudoku cells including row, column, value, original-clue state, and conflict state.
- Material components for controls where practical.
- Responsive layouts for narrow and wider screens.
- Light/dark/dynamic-color support.
- High-contrast and reduced-motion preference foundations.
- Pause state represented with readable text rather than visual effect alone.

These foundations do not replace real assistive-technology testing.

## Required Release Checks

### TalkBack

Verify:

- Every interactive cell is discoverable.
- Cell descriptions are concise but sufficient.
- Original clues are distinguishable from editable cells.
- Conflict/error state is announced.
- Number pad and game actions have clear labels.
- Focus order remains logical.
- Dialogs move focus correctly and return it appropriately.

### Font Scaling

Test large system font sizes. Text should not overlap or make critical controls unreachable. Board cells should prioritize value readability and avoid scaling note text beyond what the grid can support.

### Contrast

Check:

- Clues vs. user entries
- Selection vs. peer highlight
- Error state
- Notes
- Disabled actions
- Light and dark themes
- Dynamic colors
- High-contrast preference

Do not communicate correctness/error only by color.

### Touch Targets

Controls should aim for Android-recommended touch target sizing. Dense Sudoku cells are constrained by the board itself; surrounding actions must remain comfortably tappable.

### Motion

Non-essential animations should respect reduced-motion preference when animation is expanded in future milestones.

### Keyboard / Large Screens

Tablet/Chromebook keyboard navigation is planned for hardening. When implemented, verify focus visibility, arrow/tab behavior, number input, notes toggling, and action shortcuts.

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
- Standard phone and tablet
- Minimum supported Android version
