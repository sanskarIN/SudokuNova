# Testing

SudokuNova treats automated tests as a quality gate, especially for the Sudoku engine and state persistence.

## Current Automated Tests

### `sudoku-engine`

Current coverage includes:

- Puzzle parse/serialize round trip
- Row-conflict detection
- Candidate calculation
- Solving a known Sudoku
- Invalid-puzzle rejection
- Unique-solution confirmation
- Seeded deterministic generation
- Generated-puzzle validity and uniqueness

### `app`

Current JVM tests include:

- Active `GameStateCodec` round trip
- Rejection of malformed saved-game text
- Statistics completion-rate calculations

## Commands

Engine:

```bash
./gradlew :sudoku-engine:test
```

App JVM tests:

```bash
./gradlew :app:testDebugUnitTest
```

Android lint:

```bash
./gradlew :app:lintDebug
```

Debug build:

```bash
./gradlew :app:assembleDebug
```

Full current gate:

```bash
./gradlew :sudoku-engine:test :app:testDebugUnitTest :app:lintDebug :app:assembleDebug
```

## Regression Testing Rule

For an important bug:

1. Reproduce it.
2. Identify the root cause.
3. Add a regression test when practical.
4. Fix the root cause.
5. Re-run relevant tests/build/lint.
6. Commit the fix with a focused message.
7. Update `CHANGELOG.md`/`what_changed.md` when appropriate.

## UI and Instrumentation Testing

Compose instrumentation dependencies are configured, but the current milestone does not yet contain the full requested UI suite. Planned flows include:

- App launch
- Start game
- Cell/number input
- Notes
- Undo/redo
- Pause/resume
- Completion
- Settings
- Daily Challenge
- Custom Puzzle
- Persistence across recreation/process death

Do not mark these as fully tested until instrumentation tests and/or documented device QA exist.

## Generator Tests

Generator tests should use fixed seeds. This makes failures reproducible and prevents a random failing puzzle from becoming impossible to investigate.

## Manual QA

Before a release candidate, complete `QA_MATRIX.md` on representative devices/emulators. Automated tests do not replace TalkBack, layout, performance, and exploratory gameplay checks.

## CI

`.github/workflows/ci.yml` runs the current engine tests, app JVM tests, lint, and debug assembly. A red CI run blocks merge until the failure is understood and corrected.
