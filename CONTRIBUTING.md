# Contributing to SudokuNova

Thank you for helping improve SudokuNova. Contributions may include code, tests, documentation, accessibility improvements, translations, bug reports, release tooling, performance work, and design refinements.

## Before You Start

1. Read `README.md`, `CODE_OF_CONDUCT.md`, and the relevant files in `docs/`.
2. Use `docs/README.md` to choose the correct guide and `docs/REPOSITORY_FILE_REFERENCE.md` when adding/moving repository paths.
3. Search existing issues before opening a duplicate.
4. Keep changes focused and avoid unrelated refactors in the same pull request.
5. Never commit credentials, signing keys, tokens, private certificates, or personal data.

## Development Setup

Current build requirements include:

- JDK 17
- Android SDK 37
- Android Gradle Plugin 9.3.1
- Gradle wrapper 9.5
- Kotlin 2.4.10
- Git

Use the repository wrapper instead of relying on a machine-global Gradle version.

```bash
git clone https://github.com/sanskarIN/SudokuNova.git
cd SudokuNova
./gradlew :sudoku-engine:test :app:testDebugUnitTest :app:lintDebug :app:assembleDebug
```

See `docs/DEVELOPMENT_SETUP.md` and `docs/BUILDING.md` for detailed setup and platform-specific notes.

## Branches

Use a focused branch name, for example:

- `feature/game-history`
- `feature/advanced-hints`
- `a11y/talkback-board`
- `fix/timer-restoration`
- `docs/build-guide`

`main` is the integration branch for reviewed work. Release readiness still depends on the evidence requirements documented in the release guides; do not infer production readiness merely from a branch name.

## Coding Principles

- Prefer correctness and readability over cleverness.
- Keep Sudoku logic independent from Android UI code when possible.
- Use immutable state at UI boundaries.
- Use structured coroutines; never use `GlobalScope`.
- Keep heavy solver, file, database, and validation work off the main thread.
- Do not hard-code player-facing strings in new production UI where a resource is appropriate.
- Maintain English/Hindi resource parity for supported player-facing text.
- Keep touch targets, screen-reader semantics, large-text reachability, contrast, and motion preferences in mind.
- Do not add dependencies unless they provide clear value and their licensing/supply-chain impact is reviewed.
- Do not introduce unnecessary permissions, analytics, accounts, or network behavior.
- Keep public APIs small and documented where their intent is not obvious.
- Preserve persistent-format compatibility or version/migrate it explicitly.

## Repository Documentation Ownership

Every Git-tracked path must belong to a maintained documentation area. The repository enforces this with:

```bash
python -m unittest scripts.tests.test_verify_documentation_coverage
python scripts/verify_documentation_coverage.py
```

For a complete per-file ownership audit:

```bash
python scripts/verify_documentation_coverage.py --verbose
```

When adding a new module, source set, top-level directory, script family, or repository metadata path, update `scripts/verify_documentation_coverage.py`, its regression tests, and the relevant canonical documentation rather than hiding the path beneath an unrelated rule.

Also run the Markdown target guard after documentation/path changes:

```bash
python -m unittest scripts.tests.test_verify_documentation_links
python scripts/verify_documentation_links.py
```

See `docs/REPOSITORY_FILE_REFERENCE.md`, `docs/REPOSITORY_GUARDS.md`, and `docs/DOCUMENTATION_STANDARDS.md`.

## Tests

Changes to the Sudoku engine should normally include unit tests. Bug fixes should include a regression test when practical. Repository/release guard changes should include deterministic acceptance and failure-path tests.

Before opening a pull request, run the narrowest relevant checks first and then a broad local gate appropriate to the change. A strong repository-wide pre-PR set is:

```bash
python scripts/verify_no_secrets.py
python -m unittest scripts.tests.test_verify_documentation_links
python -m unittest scripts.tests.test_verify_documentation_coverage
python -m unittest scripts.tests.test_verify_release_contract
python -m unittest scripts.tests.test_verify_release_outputs
python scripts/verify_documentation_links.py
python scripts/verify_documentation_coverage.py
python scripts/verify_release_contract.py
python scripts/verify_translations.py
./gradlew :sudoku-engine:test \
  :app:testDebugUnitTest \
  :app:assembleDebugAndroidTest \
  :macrobenchmark:assembleBenchmark \
  :app:lintDebug \
  :app:lintRelease \
  :app:assembleDebug \
  --stacktrace
```

Use `docs/TESTING.md` for the complete release build/artifact-verification and connected-test commands.

For UI behavior that cannot be covered by automation, describe what was actually verified. Do not mark TalkBack, physical-device performance, signed-artifact, lifecycle, or store checks complete unless they were genuinely performed.

## Commit Messages

Use focused Conventional Commit-style messages:

```text
feat: add history filters
fix: prevent duplicate completion statistics
test: cover invalid custom puzzle import
a11y: improve number pad semantics
perf: reduce generator allocations
docs: update setup guide
ci: add release verification job
```

Keep commits small and meaningful. One focused concern per commit is preferred when reasonable. Do not artificially split inseparable edits solely to inflate commit count.

## Pull Requests

A good pull request includes:

- what changed and why;
- related issue, when applicable;
- tests/checks actually performed;
- screenshots or recordings for visible UI changes when useful;
- accessibility impact;
- localization impact;
- documentation changes;
- persistence/migration/format compatibility impact;
- privacy/security/permission impact;
- performance impact where relevant;
- release/signing impact where relevant.

Required workflows must pass on the **exact final pull-request head** before merge. A green workflow run from an older commit is historical evidence only.

Do not weaken a deterministic guard merely to make a pull request green. Fix the underlying defect or document and review an intentional contract change.

## Adding a Difficulty

1. Update the difficulty model in `sudoku-engine`.
2. Define generation targets and scoring expectations.
3. Add deterministic generation tests.
4. Verify puzzles remain uniquely solvable.
5. Update UI labels and documentation.
6. Run the tracked-file ownership guard if new files were added.

## Adding a Sudoku Variant

Do not mix variant rules into Classic Sudoku conditionals throughout the UI. Define the rules at the engine/domain boundary first, add validation and solver tests, then expose the variant to presentation code. Treat persistent formats, accessibility, localization, migration, and documentation ownership as part of the design rather than follow-up cleanup.

## Adding a Tutorial Lesson

Use original educational wording. Explain the logical reason a technique works rather than only naming it. Keep examples accessible without relying only on color and keep engine evidence separate from localized Android presentation.

## Adding a Theme

Verify board readability, clue/user-number distinction, error states, selected cells, notes, dark mode behavior, high-contrast behavior, reduced-motion interaction where applicable, and adequate color-independent meaning.

## Adding a Translation

Use Android string resources and preserve formatting placeholders. Avoid translating developer names, URLs, email addresses, or technical identifiers that must remain exact. Update the maintained translation-parity contract where a new locale is intentionally supported.

## Database or Data Schema Changes

Saved-game encodings and persistent models must be versioned. Preserve user data where practical and add migration/regression coverage before production release.

For Room changes:

- increment the database version deliberately;
- add an explicit migration;
- preserve exported schema history;
- add migration tests;
- do not use destructive fallback as a shortcut.

For `GameStateCodec`, `SNP1`, `SNB1`, challenge identifiers, or other compatibility-sensitive formats, update `docs/DATA_FORMATS.md` and relevant tests in the same work.

## Security Reports

Do not open a public issue for an exploitable vulnerability. Follow `SECURITY.md`.

## Documentation Definition of Done

For a material change:

- update the narrowest canonical guide;
- ensure `docs/README.md` indexes any new major documentation page;
- run documentation-link verification;
- run complete tracked-file documentation coverage verification;
- update `CHANGELOG.md` only for release-notable changes;
- update `what_changed.md` for milestone/evidence/handoff-significant work;
- keep planned/manual/production claims explicitly distinct from implemented/automatically verified facts.

## Support Development

If you want to support continued open-source development without contributing code:

☕ https://buymeacoffee.com/sanskarIN

**Made by the Sanskar**
