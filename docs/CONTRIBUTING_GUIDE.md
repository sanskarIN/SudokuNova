# Contributor Extension Guide

The root `CONTRIBUTING.md` defines the standard workflow. This guide focuses on adding common SudokuNova capabilities safely.

## Add a Difficulty

1. Extend `Difficulty` with target/scoring metadata.
2. Update generator/rating behavior.
3. Add deterministic generation tests.
4. Verify uniqueness for representative seeds.
5. Add any UI resource label.
6. Update `DIFFICULTY_SYSTEM.md`, roadmap, and changelog when user-visible.

Do not classify difficulty from clue count alone.

## Add a Sudoku Variant

1. Define the variant's exact constraints.
2. Decide whether board geometry/value domain changes.
3. Introduce a rules abstraction at the engine/domain boundary.
4. Add board validation tests.
5. Extend solution counting/solver logic.
6. Add generator tests proving valid unique puzzles.
7. Only then add UI/navigation.
8. Test accessibility for new region/constraint presentation.

Avoid scattering variant-specific conditionals throughout Classic UI code.

## Add a Hint Technique

1. Document the logical rule.
2. Add a deterministic puzzle/state that demonstrates the technique.
3. Implement detection without using solution knowledge unless the technique explicitly represents a reveal.
4. Return an original educational explanation.
5. Add false-positive tests.
6. Update Learn content when appropriate.

## Add a Tutorial Lesson

- Use original wording.
- Explain *why* the move is valid.
- Do not rely only on color.
- Keep terminology consistent with `HintTechnique`/learning content.
- Add interactive examples only when state correctness can be tested.

## Add an Achievement

Achievements should derive from reliable stored facts. Do not add unhealthy pressure or paywalls.

1. Define exact unlock criteria.
2. Decide what persistence is required.
3. Handle existing users/migrations.
4. Add criteria tests.
5. Add accessible UI text.

## Add a Theme

Verify:

- Original clues
- User numbers
- Notes
- Selected/peer/same-number states
- Conflicts/errors
- Dialogs
- Light/dark relationship if applicable
- Contrast and color-independent meaning

## Add a Setting

1. Add typed state to `UserSettings`.
2. Add a DataStore key/default.
3. Add repository read/write behavior.
4. Expose through ViewModel.
5. Render in Settings with a clear description.
6. Ensure default behavior is safe/privacy-conscious.
7. Add tests for behavior that can regress.

## Add a Translation

Follow `LOCALIZATION.md`. Keep URLs/emails/technical IDs exact. Test longer strings and RTL before declaring RTL-language support.

## Add a Data Migration

For future Room schemas:

1. Increment schema version deliberately.
2. Write explicit migration.
3. Add migration tests using old schema fixtures.
4. Preserve user data unless loss is intentional, disclosed, and appropriate.
5. Test upgrade through multiple supported versions.

For `GameStateCodec`, either decode/migrate old versions or safely reject unsupported encoded states.

## Add a Dependency

Before adding one:

- Confirm the platform/library does not already solve the problem.
- Check maintenance status and license compatibility.
- Consider binary size/startup/security impact.
- Add required third-party notice.
- Use the version catalog.

## Pull Request Definition of Done

- Focused implementation
- Tests added/updated
- Lint/build passes
- Accessibility/privacy/security considered
- Documentation matches actual behavior
- No placeholders/secrets
- Conventional Commit history remains meaningful
