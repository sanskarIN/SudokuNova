# Contributor Extension Guide

The root `CONTRIBUTING.md` defines the standard workflow. This guide focuses on adding common SudokuNova capabilities safely. `REPOSITORY_FILE_REFERENCE.md` defines the documentation owner for every tracked repository area, and new paths must pass the tracked-file documentation coverage guard.

## Add a Difficulty

1. Extend `Difficulty` with target/scoring metadata.
2. Update generator/rating behavior.
3. Add deterministic generation tests.
4. Verify uniqueness for representative seeds.
5. Add any UI resource label in both maintained locales.
6. Update `DIFFICULTY_SYSTEM.md`, roadmap, and changelog when user-visible.
7. Run documentation-link and tracked-file coverage guards if files/docs changed.

Do not classify difficulty from clue count alone.

## Add a Sudoku Variant

1. Define the variant's exact constraints.
2. Decide whether board geometry/value domain changes.
3. Introduce a rules abstraction at the engine/domain boundary.
4. Add board validation tests.
5. Extend solution counting/solver logic.
6. Add generator tests proving valid unique puzzles.
7. Decide whether `SNP1`, active-game state, backup, challenge identity, or Room schemas need a new version/migration.
8. Only then add UI/navigation.
9. Add English/Hindi resources for player-facing text.
10. Test accessibility for new region/constraint presentation.
11. Document the rules, persistence/compatibility impact, and every new tracked path.

Avoid scattering variant-specific conditionals throughout Classic UI code.

## Add a Hint Technique

1. Document the logical rule.
2. Add a deterministic puzzle/state that demonstrates the technique.
3. Implement detection without using solution knowledge unless the technique explicitly represents a Reveal fallback.
4. Return structured engine evidence rather than localized player-facing prose.
5. Add false-positive and solved-value-safety tests.
6. Update Learn content and Android localized presentation when appropriate.
7. Preserve accessibility evidence that does not rely only on color.

## Add a Tutorial Lesson

- Use original wording.
- Explain *why* the move is valid.
- Do not rely only on color.
- Keep terminology consistent with `LogicalTechnique`, structured teaching evidence, and `GLOSSARY.md`.
- Keep player-facing prose in localized Android resources rather than `sudoku-engine`.
- Add interactive examples only when state correctness can be tested.

## Add an Achievement

Achievements should derive from reliable stored facts. Do not add unhealthy pressure or paywalls.

1. Define exact unlock criteria.
2. Decide what persistence is required.
3. Handle existing users/migrations.
4. Add criteria tests.
5. Add accessible, localized UI text.
6. Review backup/restore and reset behavior if the achievement is persisted.

## Add a Theme

Verify:

- original clues;
- user numbers;
- Notes;
- selected/peer/same-number states;
- conflicts/errors;
- hint source/target/final-placement states;
- dialogs;
- light/dark relationship if applicable;
- high-contrast behavior;
- reduced-motion behavior where applicable;
- color-independent meaning;
- large-font/narrow-window reachability.

## Add a Setting

1. Add typed state to `UserSettings`.
2. Add a DataStore key/default.
3. Add repository read/write behavior.
4. Expose through ViewModel/state.
5. Render in Settings with a clear localized description.
6. Ensure default behavior is safe/privacy-conscious.
7. Add tests for behavior that can regress.
8. Decide whether backup/reset/privacy documentation changes.
9. Preserve accessibility role/state semantics.

## Add a Translation

Follow `LOCALIZATION.md`.

- Keep URLs/emails/technical IDs exact.
- Preserve placeholder signatures.
- Include accessibility strings, not only visible labels.
- Test longer strings.
- Do not declare RTL support before layout/manual validation exists.
- Update translation tooling deliberately if a new locale becomes maintained.

## Add a Data Migration

For future Room schemas:

1. increment schema version deliberately;
2. write an explicit migration;
3. preserve the exported old/new schema history;
4. add migration tests using old schema fixtures;
5. preserve user data unless loss is intentional, disclosed, and appropriate;
6. test upgrade through supported versions;
7. update `DATA_STORAGE.md` and `DATA_FORMATS.md`.

For `GameStateCodec`, `SNP1`, `SNB1`, challenge identifiers, DataStore contracts, or other compatibility-sensitive formats, either decode/migrate old versions or safely reject unsupported versions. Never silently change an existing version's semantics.

## Add a Dependency

Before adding one:

- confirm the platform/library does not already solve the problem;
- check maintenance status and official release/security information;
- check license compatibility;
- consider binary size/startup/security impact;
- review min/compile/target SDK and R8 implications;
- add required third-party notice;
- use the version catalog;
- run affected build/test/lint gates.

## Add a New Repository Area or Tool

When adding a new top-level module, source set, script family, workflow family, generated-but-tracked contract, or other new path family:

1. decide the architectural owner;
2. add the narrow canonical documentation;
3. add a specific `CoverageRule` in `scripts/verify_documentation_coverage.py` if the existing taxonomy does not already own the path intentionally;
4. add representative regression coverage in `scripts/tests/test_verify_documentation_coverage.py`;
5. run:

```bash
python -m unittest scripts.tests.test_verify_documentation_coverage
python scripts/verify_documentation_coverage.py
python scripts/verify_documentation_links.py
```

Do not make CI green by assigning an unrelated broad documentation owner.

## Pull Request Definition of Done

- Focused implementation.
- Correct layer/module ownership.
- Tests added/updated.
- Repository guard tests updated when their contract changes.
- Engine/JVM/connected/lint/build gates run as applicable.
- Accessibility/localization/privacy/security considered.
- Persistent-format/migration/backup compatibility considered.
- Performance impact considered; measured claims have real evidence.
- Documentation matches actual behavior.
- Every new tracked path has canonical documentation ownership.
- Documentation links are valid.
- No placeholders, credentials, signing material, or private data.
- Conventional Commit history remains meaningful.
- Required GitHub workflows pass on the exact final PR head before merge.
- Manual/production/device/store evidence is never inferred from source/CI alone.
