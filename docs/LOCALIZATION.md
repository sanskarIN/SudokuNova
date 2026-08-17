# Localization

SudokuNova should keep user-facing text localization-ready even while English is the initial complete language.

## Current State

Core branding/contact strings exist in Android resources, while some development-stage Compose screens still contain direct English strings. Moving remaining user-facing text into resources is part of localization hardening before stable release.

## Target Languages

Initial priority:

- English
- Hindi

Possible future translations include Spanish, French, German, Portuguese, Japanese, Korean, Chinese, and Arabic based on contributor interest and QA capacity.

## Adding a Translation

1. Keep the base English text in `app/src/main/res/values/strings.xml`.
2. Add a locale-specific `values-<locale>/strings.xml` resource directory.
3. Preserve formatting placeholders exactly.
4. Do not translate identifiers that must remain exact, including URLs, package/application IDs, email addresses, and the developer name where branding requires it.
5. Test layouts with longer translated text.
6. Test RTL behavior for RTL languages before claiming support.
7. Update documentation and `what_changed.md` for a completed translation milestone.

## Translation Quality

- Prefer natural meaning over literal word-for-word translation.
- Keep Sudoku technique terminology consistent across Learn and hints.
- Avoid machine-translated strings being marked final without review.
- Ensure accessibility descriptions are translated too.
- Keep error messages specific and actionable.

## Developer Rule

New production UI should prefer string resources rather than hard-coded display text. Existing direct English strings are tracked as development debt for v0.4 localization hardening.

## Sudoku Terminology

Technique names may include a standard English term followed by a localized explanation if the localized Sudoku community commonly uses the English name. Consistency matters more than inventing unique terminology.
