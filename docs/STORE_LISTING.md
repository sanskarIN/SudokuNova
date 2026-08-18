# SudokuNova Store Listing Source

This file is a source-of-truth draft for public listing text and capture planning. Store-specific field limits, policy wording, target-API rules, image dimensions, and review requirements must be checked against the current store documentation at submission time.

Do not add claims that are not implemented in the release binary.

## App Name

**SudokuNova**

## Tagline

**Think. Solve. Master the Grid.**

## Short Description Draft

Modern offline Sudoku with smart hints, learning, challenges, custom puzzles, history, backup, themes, accessibility, and English/Hindi support.

## Full Description Draft

SudokuNova is a modern, open-source Classic 9×9 Sudoku experience built for focused offline play, learning, and long-term puzzle mastery.

Choose from seven difficulty levels, play deterministic Daily and Weekly Challenges, create your own Sudoku puzzles, save favorites, review History, track local statistics, and learn solving techniques through structured hints and offline practice.

### Play your way

- Seven difficulty levels from Beginner through Extreme.
- Cell-first and Number-first input modes.
- Notes, erase, undo, redo, pause, restart, timer, and mistake controls.
- Autosave and resume.
- Hardware keyboard support.

### Learn logical Sudoku

SudokuNova teaches supported solving techniques with structured evidence instead of only revealing a number. Current techniques include Naked Single, Hidden Single, Naked Pair, Pointing Pair/Triple, Box-Line Reduction, Hidden Pair, Naked Triple, Hidden Triple, and X-Wing.

The Learn center includes offline lessons, deterministic practice, progress, and mastery tracking.

### Challenges and custom puzzles

- Daily Challenge.
- Weekly Challenge.
- Challenge history/performance records.
- Custom Puzzle editor.
- Contradiction, solvability, and unique-solution validation.
- Save and replay custom puzzles.

### History, saved puzzles, and statistics

Completed games and saved puzzles are stored locally. SudokuNova supports favorites, replay provenance, local statistics, streaks, and achievements without requiring an account.

### Safe sharing and backup

SudokuNova supports versioned puzzle sharing/import and user-controlled local backup/restore through explicit Android system actions. Imported puzzles are validated before play. Backup files use integrity checks and bounded parsing.

### Accessibility and personalization

- English and Hindi resources.
- Light, Dark, and System themes.
- Material You dynamic color where supported.
- High Contrast preference.
- Reduced Motion preference.
- Semantic Sudoku cell and hint information.
- Adaptive layouts and large-text source hardening.
- Hardware keyboard controls.

### Offline-first and privacy-conscious

The open-source base app is designed to work without an account, advertising, analytics SDKs, or a SudokuNova-operated cloud backend. Gameplay/settings/history/learning/challenge data is stored locally through Android app storage. Sharing, import, export, and backup happen through explicit user actions.

User-controlled backup files are integrity-checked but are not encrypted. Review the repository privacy documentation before distribution.

### Open source

SudokuNova is MIT licensed and includes public documentation for architecture, building, testing, privacy, security, accessibility, data formats, backup/restore, CI/CD, and releases.

**Made by the Sanskar**

## Support / Contact Source

- Repository: `https://github.com/sanskarIN/SudokuNova`
- GitHub profile: `https://www.github.com/sanskarIN`
- Business: `sanskarin@outlook.in`
- Business: `sanskarin.business@gmail.com`
- Support: `supportramsandesh@gmail.com`
- Optional open-source support: `https://buymeacoffee.com/sanskarIN`

## Screenshot Capture Plan

All screenshots must come from the actual release candidate/stable binary. Do not use mock screens that imply unavailable features.

Recommended coverage:

1. Home — SudokuNova branding, difficulty choices, primary destinations.
2. Active Game — board, timer, notes/actions, current theme.
3. Smart Hint — visible structured teaching evidence.
4. Learn — technique list/progress.
5. Technique Practice — actual practice question/result state.
6. Daily/Weekly Challenges — real challenge screen.
7. Custom Puzzle — editor and validation actions.
8. History — populated real/local test data suitable for public screenshots.
9. Saved Puzzles — saved/favorite flow.
10. Statistics — representative local statistics.
11. Settings — theme/input/accessibility controls.
12. Backup & Transfer — explicit user-controlled transfer actions.

Capture at least one screenshot demonstrating Hindi localization if the chosen public listing wants to highlight it.

## Screenshot Safety Rules

Before using an image publicly:

- remove private notifications/system overlays;
- ensure no personal clipboard/document data is visible;
- use only test/demo puzzle history suitable for publication;
- confirm text matches the released binary;
- confirm no debug suffix, debug-only UI, or development warning is visible unless intentionally documenting a development build;
- confirm accessibility/state visuals are not misleading;
- do not edit screenshots to add controls or results that were not present in the app.

## Feature Graphic / Repository Artwork Direction

Use existing SudokuNova branding and the tagline `Think. Solve. Master the Grid.`. Keep national/political imagery, unrelated brand marks, and misleading award/rating claims out of release art.

## Privacy / Data Safety Preparation

Before submission, derive answers from the actual final binary and current privacy documentation, not from this draft alone.

Current open-source base design documented by the repository:

- no app account/login;
- no ads SDK;
- no analytics SDK;
- no SudokuNova-operated cloud backend;
- local DataStore + Room persistence;
- explicit user-triggered share/import/export/backup flows;
- no runtime permissions declared in the current manifest.

Re-audit the final binary/manifest before submitting any privacy or Data Safety declaration.

## Publication Gate

Do not label this listing as published until the selected store has actually accepted and made the release available according to the intended rollout state.
