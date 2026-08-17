# Frequently Asked Questions

## Is SudokuNova open source?

Yes. The repository uses the MIT License. Third-party dependencies remain under their own licenses.

## Does core gameplay require internet?

No. The current base application's Sudoku generation, solving, play, settings, statistics, custom-puzzle validation, and Daily Challenge seed operate locally.

## Does SudokuNova require an account?

No.

## Does the base app contain ads or analytics?

No ads or remote analytics SDK are included in the current open-source base implementation.

## Which Sudoku size is implemented now?

Classic 9×9 is the current implemented gameplay target. Additional variants/sizes are roadmap items and should not be described as shipped until their code/tests exist.

## Why are there seven difficulties?

The current UI supports Beginner, Easy, Medium, Hard, Expert, Master, and Extreme. The current development-stage rating combines clue targets and solver-search metrics. Human-technique calibration is planned for later hardening.

## Are generated puzzles guaranteed unique?

The generator removes a clue only when solver analysis still finds exactly one solution. Generator tests also check uniqueness for deterministic samples.

## Can I enter my own Sudoku?

Yes. The Custom Puzzle screen can enter clues, detect contradictions, solve/analyze the puzzle, determine whether exactly one solution exists, preview a solution, and start a validated unique puzzle.

## What happens when I close the app during a game?

The active game is periodically persisted locally. The Home screen can offer Continue Game when a valid playing state is available.

## Where is data stored?

Current settings, active game, and aggregate statistics use Android Preferences DataStore on the device. See `DATA_STORAGE.md` and `PRIVACY.md`.

## Why is the Kotlin namespace `com.sanskar.sudokunova` while the application ID is `in.sanskar.sudokunova`?

`in` is a Kotlin language keyword and cannot be used as an ordinary first package identifier. The internal Kotlin namespace therefore uses `com.sanskar.sudokunova`, while the Android application ID preserves `in.sanskar.sudokunova`.

## Can I contribute?

Yes. Read `../CONTRIBUTING.md` and `CONTRIBUTING_GUIDE.md`, then open a focused issue or pull request.

## Where do I report a security issue?

Do not publish exploitable vulnerabilities in a public issue. Follow `../SECURITY.md`.

## How can I support development?

Star/share/contribute to the repository, or optionally use:

☕ https://buymeacoffee.com/sanskarIN

Core gameplay must not depend on a donation.

## Who maintains SudokuNova?

Developer: Sanskar  
GitHub: https://www.github.com/sanskarIN  
Support: `supportramsandesh@gmail.com`
