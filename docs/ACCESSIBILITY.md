# SudokuNova Accessibility

Accessibility is a release requirement for SudokuNova, not optional polish. Automated semantics are required where practical, but they do not replace real assistive-technology, font-scaling, contrast, keyboard, and device testing.

## Accessibility Principles

SudokuNova aims to ensure that:

- game state is not communicated only by color;
- interactive controls have meaningful semantic labels/roles;
- Sudoku cells expose enough context to solve/navigate with assistive technology;
- selected/conflict/clue/hint states are represented semantically;
- large text does not make critical content unreachable;
- high-contrast users can distinguish important board states;
- non-essential motion respects the reduced-motion preference boundary;
- hardware-keyboard users can operate the game board;
- dialogs and scrolling content have logical focus/navigation behavior.

## Sudoku Cell Semantics

Interactive Sudoku cells expose localized descriptions derived from the current state.

Semantic information can include:

- row number;
- column number;
- empty/value state;
- original clue state;
- conflict/mistake state;
- selected state;
- teaching source state;
- teaching target state;
- candidate elimination evidence;
- final hint placement/value.

The v0.9 board also exposes Compose selected semantics, so the selected cell is not represented only by its visual border/background.

Stable per-cell test tags identify board coordinates for deterministic connected regression tests without replacing user-facing semantics.

## Custom Puzzle Editor Semantics

Custom Puzzle editor cells now follow the same basic semantic contract as gameplay cells where applicable:

- localized row/column coordinates;
- empty/value state;
- selected state;
- conflict state;
- stable per-cell test tags for deterministic connected coverage.

The connected test suite verifies selected-state transitions in the editor. Manual TalkBack traversal and verbosity still require real QA.

## Original vs Editable Cells

Original clues must remain distinguishable from editable cells.

The distinction is represented in the content description and by visual styling. Editing actions must remain disabled for original clues regardless of touch or keyboard input.

## Conflict / Mistake State

A conflict/error state must not be represented only by error color.

Cell semantics append a localized conflict description. Visual error presentation keeps priority over ordinary selection/peer/hint emphasis so a teaching state does not hide an error.

## Hint / Teaching Semantics

A `SudokuHint` can contain structured teaching steps. The board derives the union of evidence needed for accessible presentation.

Affected cells can announce:

- teaching source;
- teaching target;
- candidate elimination target and exact candidate list;
- final placement target and value.

Reveal fallback may announce a final placement target/value but must not fabricate logical source cells or candidate eliminations.

## Selected State Regression Coverage

The v0.9 connected Compose suite includes board-cell and Custom Puzzle editor semantics regressions that target stable cell tags and check selected-state behavior.

Automated checks are useful for preventing accidental semantics removal, but they do not prove the quality of TalkBack navigation order/verbosity on a real device.

## Number Pad / Game Actions

Visible game actions should have meaningful accessible labels and remain reachable with touch exploration/focus.

Important actions include:

- digits 1–9;
- Notes;
- Erase;
- Undo;
- Redo;
- Hint;
- Pause/Resume;
- Restart/end actions where shown.

In Number-first mode, the persistent selected digit exposes semantic selected state. Notes mode also exposes selected state, so neither mode depends only on button color.

The main game action rows can horizontally scroll at larger text sizes instead of forcing all localized labels into one fixed-width row.

Icon-only buttons require content descriptions.

## Settings Semantics

Settings toggle rows are exposed as one merged switch target. The row owns the switch role/state/action and the trailing Material switch is presentation-only, avoiding two independently focusable controls for the same setting.

Theme, input-mode, and mistake-limit chip groups can horizontally scroll so larger/localized labels remain reachable instead of overflowing a single row.

## Large-Text Source Hardening

The v0.9 source audit removed several obvious fixed-width assumptions before manual 200% font QA:

- Game action rows can horizontally scroll.
- Settings chip groups can horizontally scroll.
- History filters, metrics, and badges can horizontally scroll.
- Learn Study/Practice actions are stacked at full width.
- Custom Puzzle text actions are stacked at full width.
- Puzzle-code Copy/Share actions are stacked at full width.
- Backup & Transfer Copy/Share/Export/Import actions are stacked at full width.
- Challenge title/difficulty/status information is stacked instead of competing in one row.
- Empty `Text("")` layout placeholders were removed from Saved Puzzles/Challenges/History/Learn where found.

These are source-level defenses against obvious clipping/collision. They are **not** evidence that every screen passes 200% font scale on every target device.

## Dialogs

Dialog content must remain:

- readable;
- focusable in logical order;
- dismissible using a clearly labeled action;
- reachable at large font sizes;
- safe for screen-reader traversal.

Important dialog families include:

- hint explanations;
- Learn lessons;
- technique practice/results;
- destructive/reset confirmations;
- validation/transfer feedback.

Release QA should verify that focus moves into the dialog and returns sensibly after dismissal.

## Learn Accessibility

The Learn screen uses scrollable content and stable technique actions.

Accessibility review should cover:

- overall progress text;
- technique titles;
- mastery percentages/progress indicators;
- Study action;
- Practice action;
- practice question/evidence;
- answer choices;
- correct/incorrect result;
- reset-learning confirmation.

Study/Practice controls are full-width stacked actions to avoid predictable large-font collisions.

Off-screen LazyColumn items must remain reachable by accessibility scrolling; automated tests use stable tags/scroll-to-index only for deterministic testing.

## Font Scaling

Test large system font sizes, including 200% where practical.

Requirements:

- no essential action is permanently clipped/unreachable;
- cards/lists remain scrollable;
- dialog content remains reachable;
- labels may wrap rather than overlap;
- Sudoku solved values remain readable;
- note text remains legible without overflowing the fixed grid;
- top bars/navigation controls remain usable.

The Sudoku board is geometrically constrained, so board note typography may need bounded sizing independent from ordinary body-text scaling.

## Contrast

Review contrast/state differentiation for:

- original clues vs user entries;
- selected cell;
- peers;
- same-number highlight;
- conflict/error;
- notes;
- teaching source;
- teaching target;
- final hint placement;
- disabled controls;
- light theme;
- dark theme;
- dynamic color;
- High Contrast preference.

High Contrast strengthens grid lines, selected/conflict borders, peer distinction and note emphasis.

Do not rely on a single hue difference for correctness/error/teaching meaning; semantic/text descriptions remain the non-color path.

## Motion

The Reduced Motion preference is the project boundary for non-essential animations.

New animation work should:

- check the preference;
- avoid making information depend on animation;
- avoid long/repeated decorative motion when reduced motion is enabled;
- keep state transitions understandable without motion.

## Touch Targets

Controls outside the dense Sudoku grid should aim for Android-recommended touch target sizing.

Sudoku cells are constrained by fitting a 9×9 board on the screen. Within that constraint:

- the whole cell remains tappable;
- text/notes should not create tiny separate tap targets;
- surrounding controls should not be unnecessarily dense.

Full-width stacked actions are preferred when two long localized labels would otherwise be forced into small half-width controls.

## Hardware Keyboard

The game supports keyboard navigation/actions, including arrows, digits, erase, Notes, and Hint pathways.

Manual QA should verify:

- visible selected state;
- semantic selected state;
- arrow movement bounds;
- digit input;
- clue protection;
- erase;
- Notes toggle;
- Hint;
- no input/focus trap.

See `KEYBOARD_SHORTCUTS.md`.

## Large Screens / Resizable Windows

Verify phone and larger/tablet-style widths.

Important checks:

- board does not grow beyond intended usable maximum;
- controls remain reachable;
- lists/cards use available width sensibly;
- orientation/window resize does not lose state;
- hardware keyboard focus/input remains usable.

## Themes / Dynamic Color

Accessibility must be checked across:

- Light;
- Dark;
- System;
- Dynamic Color where supported;
- High Contrast on/off.

Dynamic Color can alter actual color relationships, so visual review is still required even when Material semantic color roles are used.

## Localization and Accessibility

Cell/hint/action semantics are player-facing text and belong in Android resources.

English and Hindi maintained resources must stay in parity. A new accessibility description is incomplete until both maintained locales are updated and `verify_translations.py` passes.

The in-app privacy summary is also maintained in both locales and reflects the current local DataStore/Room + explicit transfer/backup behavior rather than an older DataStore-only state.

## Automated Accessibility-Related Tests

Automated coverage can verify deterministic properties such as:

- semantic selected state;
- content descriptions;
- visible tagged controls;
- dialog/result presence;
- Learn flow accessibility-relevant semantics.

The connected API-35 workflow is a regression gate, not a full accessibility certification.

## Required Manual Release Checks

Before a stable release, verify at minimum:

- [ ] TalkBack on a representative device/emulator;
- [ ] Home navigation;
- [ ] Sudoku cell traversal;
- [ ] clue/editable distinction;
- [ ] conflict announcement;
- [ ] selected-state behavior;
- [ ] hint source/target/elimination/placement announcements;
- [ ] direct-placement hint;
- [ ] multi-step elimination-chain hint;
- [ ] Reveal fallback;
- [ ] Number Pad/actions and selected digit state;
- [ ] Notes selected-state announcement;
- [ ] Settings switch rows;
- [ ] Custom Puzzle editor cell traversal;
- [ ] dialog focus/return behavior;
- [ ] Learn lesson/practice;
- [ ] 200% font scaling where practical;
- [ ] light/dark contrast;
- [ ] dynamic color;
- [ ] High Contrast;
- [ ] Reduced Motion;
- [ ] narrow phone;
- [ ] tablet/large window;
- [ ] hardware keyboard;
- [ ] minimum supported Android target.

Do not mark these complete from code inspection alone.

## Accessibility Bug Reporting

Use the repository accessibility issue template when available and include:

- device/emulator;
- Android version;
- assistive technology and version/settings;
- screen/action;
- expected vs actual behavior;
- whether the problem blocks task completion;
- reproduction steps;
- screenshot/screen recording only when safe and useful.

For accessibility defects involving sensitive/security information, follow the root security reporting policy instead of posting private details publicly.

## Contributor Rule

A UI feature is not complete if it introduces an accessible regression without a justified plan/fix.

When adding a new interactive feature, review:

1. semantic label/role/state;
2. focus order;
3. large-font layout;
4. contrast/non-color communication;
5. reduced-motion behavior;
6. keyboard support where applicable;
7. English/Hindi accessibility strings;
8. automated regression coverage where reliable;
9. release manual QA impact.
