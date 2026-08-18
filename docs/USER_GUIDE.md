# SudokuNova User Guide

This guide explains how to use the current SudokuNova Android application. It follows the implemented Classic 9×9 experience and avoids documenting unimplemented future features.

## 1. Home

Home is the primary entry point. From here you can start normal games, open challenge flows, create custom puzzles, resume or browse local data, learn solving techniques, inspect statistics, change settings, manage backup/transfer, and open About information.

Available areas include:

- normal difficulty-based play;
- Daily Challenge;
- Challenges archive;
- Custom Puzzle;
- History;
- Saved Puzzles;
- Learn;
- Statistics;
- Settings;
- Backup & Transfer;
- About.

## 2. Starting a Normal Game

Choose one of the supported difficulty levels. SudokuNova generates a valid puzzle with a unique solution and opens the game board.

The seven difficulty targets are Beginner, Easy, Medium, Hard, Expert, Master, and Extreme.

Starting a normal game creates an active local game state. Progress is periodically persisted so the game can be restored after ordinary lifecycle/process interruption.

## 3. Understanding the Board

A Classic Sudoku board has 81 cells arranged as 9 rows, 9 columns, and nine 3×3 boxes.

Original clues are fixed and cannot be erased or replaced. Editable cells can contain a solved value or notes/candidates.

Depending on settings, the board can visually emphasize:

- the selected cell;
- row/column/box peers;
- matching numbers;
- conflicts or automatically detected mistakes;
- teaching-hint source cells;
- teaching-hint target cells;
- hint candidate eliminations;
- the final hint placement.

Accessibility semantics provide non-color descriptions for important board states.

## 4. Selecting Cells and Numbers

SudokuNova supports two input styles.

### Cell first

1. Select an editable cell.
2. Tap a number 1–9.
3. The number is entered into the selected cell, or toggled as a note when Notes mode is active.

### Number first

1. Choose a number.
2. Select a compatible editable cell.
3. The selected-number workflow is used according to the current game input behavior.

The preferred input mode is stored in Settings.

## 5. Notes

Notes are candidate marks used while solving.

- Enable Notes mode.
- Choose one or more candidate values for an editable cell.
- Tapping an existing note removes it.
- Erasing a cell clears its value/notes as applicable.

When automatic note removal is enabled, a correctly placed solved value can remove that candidate from peer-cell notes.

## 6. Undo and Redo

SudokuNova keeps a bounded local undo/redo history for the active game.

Use Undo to return to an earlier board state and Redo to reapply an undone state. New edits clear the redo branch, which is standard undo/redo behavior.

## 7. Mistakes and Conflicts

Sudoku rules prohibit duplicate non-zero values in the same row, column, or 3×3 box.

When automatic mistake checking is enabled, a player entry can also be compared with the authoritative solution. Settings control mistake checking and the optional mistake limit.

Conflict and mistake information is not represented only by color; cell semantics include error information where applicable.

## 8. Pause and Timer

Use Pause to hide the active grid state behind a paused presentation and stop active solving interaction.

The timer increases while a game is in the playing state and is not paused. Timer visibility can be controlled in Settings. Active state is periodically persisted.

## 9. Hints

Use Hint when you want guided assistance.

SudokuNova prefers logical teaching evidence. A hint may explain one or more steps that enable a final placement. Supported strategies include:

- Naked Single;
- Hidden Single;
- Naked Pair;
- Pointing Pair / Triple;
- Box-Line Reduction;
- Hidden Pair;
- Naked Triple;
- Hidden Triple;
- X-Wing.

The hint presentation can identify source cells, target cells, candidate removals, and the final placement.

If the supported logical teaching pipeline cannot reach a placement, SudokuNova can use a separate Reveal fallback. Reveal is solver-backed and is deliberately not presented as a logical strategy.

Applying a hint places only the final proven value rather than silently applying every candidate elimination to the player's notes.

## 10. Restarting or Ending a Game

Restart returns the current puzzle to its original clues and resets active-game progress such as notes, elapsed time, mistakes, and hints used.

Ending/abandoning a game clears the active game and returns control to the broader application flow. Abandoned-game accounting is kept separate from completed-game recording.

## 11. Completing a Puzzle

A game completes when the board equals the unique solved board.

Completion can record local information such as:

- puzzle/difficulty;
- elapsed time;
- mistakes;
- hints used;
- timestamps;
- perfect-game status;
- challenge identity when relevant;
- replay provenance when relevant.

The active save is cleared after completion recording.

## 12. Daily and Weekly Challenges

Open Challenges to access deterministic challenge content.

### Daily Challenge

A date-derived challenge identity ensures the same challenge identity can be reproduced for the same date under the current algorithm.

### Weekly Challenge

Weekly challenges use an ISO-week-style identity and a separate deterministic seed space from Daily challenges.

The archive can display recorded challenge results. Challenge completion is stored locally.

## 13. Custom Puzzle

Custom Puzzle lets you enter your own clues.

Before play, SudokuNova verifies that the puzzle:

- has valid row/column/box clues;
- is solvable;
- has exactly one solution.

A contradictory, unsolvable, or non-unique puzzle is rejected rather than launched as a normal game.

Validated custom puzzles can be played and can be saved through the local saved-puzzle system.

## 14. History

History contains locally recorded completed games.

Depending on the record, the History area can expose:

- difficulty;
- completion time;
- mistakes;
- hints;
- perfect status;
- favorite state;
- replay actions;
- source/provenance information.

Replays preserve provenance so replay completions do not masquerade as new first-play records.

## 15. Saved Puzzles

Saved Puzzles contains locally saved puzzle entries, including custom/imported puzzles when saved through supported flows.

Saved entries may contain:

- title;
- puzzle;
- known solution when applicable;
- difficulty;
- source;
- creation timestamp;
- Favorite state.

Puzzle content has a uniqueness constraint in local storage to reduce duplicate saved entries.

## 16. Learn

The Learn center provides offline educational content.

It includes:

- Sudoku basics;
- candidates;
- solving habits;
- lessons for all supported logical techniques;
- technique practice;
- per-technique progress;
- overall mastery.

Studying a technique records local lesson exposure. Practice records attempts/successes. Repeated taps after an answer is submitted do not create duplicate attempts for the same exercise state.

Use the reset action to clear only learning progress. It does not erase game history, saved puzzles, challenge records, settings, or the active game.

## 17. Statistics

Statistics summarizes locally stored gameplay information. Values can include completion, timing, streak, difficulty, perfect-game, mistake, hint, and challenge-oriented summaries according to the current data model.

Replay-aware logic prevents replays from incorrectly inflating original completion statistics.

## 18. Settings

Settings controls player preferences such as:

- app theme;
- Material You dynamic color;
- input mode;
- peer highlighting;
- same-number highlighting;
- automatic mistake checking;
- automatic note cleanup;
- timer visibility;
- haptics;
- sound feedback;
- reduced motion;
- high contrast;
- mistake limit;
- local reset operations.

Settings are stored locally with Preferences DataStore.

## 19. Theme and Accessibility Preferences

SudokuNova supports light/dark/system behavior and dynamic color where the Android version/device supports it.

High Contrast strengthens board-state differentiation. Reduced Motion is the preference boundary for non-essential motion.

For assistive technology, the board exposes spoken descriptions for value, coordinate, clue state, conflict state, selected state, and teaching-hint evidence.

## 20. Keyboard Controls

On devices with a hardware keyboard, the game supports keyboard-based navigation and input. See `KEYBOARD_SHORTCUTS.md` for the maintained shortcut reference.

## 21. Puzzle Code Import and Sharing

SudokuNova uses `SNP1` puzzle codes.

When importing a code, the application validates format, checksum, board legality, and unique solvability before accepting it for play.

Sharing uses user-initiated Android surfaces such as copy/share. No public SudokuNova server is required.

## 22. Backup and Restore

Open Backup & Transfer to export/import local application data through Android's document picker.

The current `SNB1` format is bounded and checksum-protected. Import is fail-closed: malformed, oversized, unsupported, or integrity-invalid data is rejected.

Backups can contain settings, history, saved puzzles, and challenge results according to the current format.

Keep backup files private if your play history or custom puzzle titles are sensitive to you.

## 23. About and Support

About shows application identity/version, open-source information, project support, contact information, and the privacy summary.

Project links include:

- GitHub repository: `https://github.com/sanskarIN/SudokuNova`
- Buy Me a Coffee: `https://buymeacoffee.com/sanskarIN`
- Support: `supportramsandesh@gmail.com`

## 24. Privacy Expectations

The open-source base app has no account, ads, analytics, or SudokuNova-operated cloud backend. Core gameplay data stays local to application/device storage unless you explicitly use share/export or Android backup/device-transfer facilities.

Read `PRIVACY.md` and `DATA_STORAGE.md` for the maintained data-handling reference.

## 25. If Something Goes Wrong

Use `TROUBLESHOOTING.md` for build/runtime troubleshooting. For ordinary application bugs, report a reproducible GitHub issue. For a suspected exploitable security problem, follow the private reporting instructions in the repository `SECURITY.md` instead of posting sensitive details publicly.
