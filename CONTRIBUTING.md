# Contributing to SudokuNova

Thank you for helping improve SudokuNova. Contributions may include code, tests, documentation, accessibility improvements, translations, bug reports, and design refinements.

## Before You Start

1. Read `README.md`, `CODE_OF_CONDUCT.md`, and relevant files in `docs/`.
2. Search existing issues before opening a duplicate.
3. Keep changes focused and avoid unrelated refactors in the same pull request.
4. Never commit credentials, signing keys, tokens, private certificates, or personal data.

## Development Setup

Requirements:

- JDK 17
- Android SDK 37
- Android Studio compatible with AGP 9.3.0
- Git

```bash
git clone https://github.com/sanskarIN/SudokuNova.git
cd SudokuNova
./gradlew :sudoku-engine:test :app:testDebugUnitTest :app:lintDebug :app:assembleDebug
```

See `docs/DEVELOPMENT_SETUP.md` and `docs/BUILDING.md` for detailed setup.

## Branches

Use a focused branch name, for example:

- `feature/game-history`
- `feature/advanced-hints`
- `a11y/talkback-board`
- `fix/timer-restoration`
- `docs/build-guide`

`main` is the stable integration branch.

## Coding Principles

- Prefer correctness and readability over cleverness.
- Keep Sudoku logic independent from Android UI code when possible.
- Use immutable state at UI boundaries.
- Use structured coroutines; never use `GlobalScope`.
- Do not hard-code user-facing strings in new production UI where a resource is appropriate.
- Keep touch targets and screen-reader semantics in mind.
- Do not add dependencies unless they provide clear value.
- Do not introduce unnecessary permissions or analytics.
- Keep public APIs small and documented where their intent is not obvious.

## Tests

Changes to the Sudoku engine should normally include unit tests. Bug fixes should include a regression test when practical.

Before opening a pull request, run:

```bash
./gradlew :sudoku-engine:test
./gradlew :app:testDebugUnitTest
./gradlew :app:lintDebug
./gradlew :app:assembleDebug
```

For UI behavior that cannot be covered by unit tests, describe manual verification in the pull request.

## Commit Messages

Use Conventional Commits:

```text
feat: add history filters
fix: prevent duplicate completion statistics
test: cover invalid custom puzzle import
a11y: improve number pad semantics
perf: reduce generator allocations
docs: update setup guide
ci: add release verification job
```

Keep commits small and meaningful. One focused concern per commit is preferred when reasonable.

## Pull Requests

A good pull request includes:

- What changed and why
- Related issue, when applicable
- Tests performed
- Screenshots or recordings for visible UI changes
- Accessibility impact
- Documentation changes
- Any migration or compatibility impact

CI must pass before merge unless a maintainer explicitly documents why a check is unavailable.

## Adding a Difficulty

1. Update the difficulty model in `sudoku-engine`.
2. Define generation targets and scoring expectations.
3. Add deterministic generation tests.
4. Verify puzzles remain uniquely solvable.
5. Update UI labels and documentation.

## Adding a Sudoku Variant

Do not mix variant rules into Classic Sudoku conditionals throughout the UI. Define the rules at the engine/domain boundary first, add validation and solver tests, then expose the variant to presentation code.

## Adding a Tutorial Lesson

Use original educational wording. Explain the logical reason a technique works rather than only naming it. Keep examples accessible without relying only on color.

## Adding a Theme

Verify board readability, clue/user-number distinction, error states, selected cells, notes, dark mode behavior, and adequate contrast.

## Adding a Translation

Use Android string resources and preserve placeholders. Avoid translating developer names, URLs, email addresses, or technical identifiers that must remain exact.

## Database or Data Schema Changes

Saved-game encodings and persistent models must be versioned. Preserve user data where practical and add migration/regression coverage before production release.

## Security Reports

Do not open a public issue for an exploitable vulnerability. Follow `SECURITY.md`.

## Support Development

If you want to support continued open-source development without contributing code:

☕ https://buymeacoffee.com/sanskarIN

**Made by the Sanskar**
