# Design System

SudokuNova currently uses Material 3 as its component foundation and defines project-specific theme/board behavior in Compose.

## Color

The app supports:

- Light color scheme
- Dark color scheme
- System theme selection
- Material You dynamic color on compatible Android versions

The original launcher/splash identity uses a blue Sudoku-grid motif. Dynamic color may change in-app colors without changing the core brand artwork.

## Typography

`SudokuNovaTypography` defines project-level Material typography choices using the system sans-serif family. Current priorities are readability, hierarchy, and resilience under font scaling rather than decorative fonts.

## Shape and Surface

Material 3 component shapes are used for cards, number-pad surfaces, chips, dialogs, and action surfaces. New components should avoid introducing one-off radii/elevations without a reusable need.

## Sudoku Board Tokens

The board currently derives colors from the active `MaterialTheme` for:

- Surface/background
- Original/user numbers
- Primary selection
- Secondary same-value highlighting
- Peer highlighting
- Error/conflict state
- Grid lines

Future refactoring may centralize these as explicit board design tokens once theme variants/high-contrast behavior need additional control.

## Spacing

Current layouts use a small consistent set of Compose `dp` spacing values. New screens should prefer predictable spacing rhythm and minimum touch target requirements over dense visual packing.

## Icons

The app currently uses Material icons for interface actions and original vector artwork for SudokuNova branding. Do not copy another Sudoku application's logo or unlicensed icon artwork.

## Accessibility Requirements

Design changes must preserve:

- Readable contrast
- Non-color cues for important meaning
- Usable touch targets
- Screen-reader labels
- Large-font behavior
- Reduced-motion compatibility

## Brand Credit

Appropriate attribution text:

**Made by the Sanskar**

Support link:
https://buymeacoffee.com/sanskarIN

Neither should visually compete with the puzzle board during play.
