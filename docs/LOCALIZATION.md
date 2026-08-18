# Localization

SudokuNova keeps player-facing Android text in resources so gameplay, learning, accessibility, transfer, and settings surfaces can be translated without changing Sudoku-domain logic.

## Supported Languages

Current maintained application languages:

- English (`values/`)
- Hindi (`values-hi/`)

Future translations may be added based on contributor interest and QA capacity. A locale should not be called supported until its required resources, formatting placeholders, layouts, and accessibility text have been reviewed.

## Resource Rule

New production UI text should use Android string resources rather than hard-coded display text.

The `sudoku-engine` module must not contain player-facing localized explanation prose. It may expose structured domain evidence such as:

- logical-technique identity;
- cell indices;
- row/column/box identity;
- candidate eliminations;
- placements.

The Android layer converts that evidence into localized display and accessibility text.

## Current Resource Sets

The project uses milestone-specific resource files in addition to the original `strings.xml` files. v0.8 learning/hint resources are paired in:

```text
app/src/main/res/values/learning_strings_v08.xml
app/src/main/res/values-hi/learning_strings_v08.xml
```

They contain:

- names for every supported teaching/hint technique;
- row/column/box/cell labels;
- logical hint explanation templates;
- Reveal fallback text;
- teaching-chain summaries;
- source/target/elimination/placement accessibility descriptions;
- learning-progress text;
- practice prompts and result messages;
- reset confirmation text;
- Hidden Pair, Naked Triple, Hidden Triple, and X-Wing lesson text.

Existing v0.4 English/Hindi resource sets continue to supply the earlier learning lessons and core app UI.

## Translation Parity Gate

Run:

```bash
python scripts/verify_translations.py
```

The standard GitHub Actions workflow also runs this check before Gradle verification. The script compares required English/Hindi localized keys and fails when one maintained locale is missing a required key.

A v0.8 verification run confirmed parity for 250 localized keys before later Gradle stages were reached. The final merge head must pass the same parity gate again together with all build/test checks.

## Adding or Changing a Translation

1. Add/update the base English resource.
2. Add/update the matching Hindi resource in the same change when the key is in the maintained localized surface.
3. Preserve formatting placeholders and argument order exactly where Android formatting requires them.
4. Do not translate exact identifiers such as package/application IDs, URLs, email addresses, or technical values that must remain literal.
5. Keep standard Sudoku technique terminology consistent across Learn, hints, practice, and accessibility text.
6. Test longer translated text at larger font scales.
7. Run `python scripts/verify_translations.py`.
8. Run Android lint and relevant UI/instrumentation tests.

## Placeholder Safety

Formatting placeholders such as `%1$d`, `%2$s`, and escaped percent values must remain type-compatible between locales. Reordering is acceptable only when positional placeholders make the mapping explicit.

For example, Hindi may change word order while still using `%1$d` and `%2$s` to preserve the original typed arguments.

## Sudoku Terminology

Technique names such as Naked Single, Hidden Pair, Naked Triple, Hidden Triple, and X-Wing may keep their standard English Sudoku term while the surrounding explanation is localized. This avoids inventing uncommon terminology and keeps lessons aligned with wider Sudoku references.

Consistency matters across:

- hint titles;
- lesson headings;
- practice choices;
- progress cards;
- accessibility explanations.

## Accessibility Localization

Accessibility descriptions are part of the localized product surface. In v0.8, a pending hint can announce:

- teaching source cell;
- teaching target cell;
- candidate elimination target and exact candidates;
- final placement target and value.

These descriptions must remain available in every maintained language and must not rely on color terminology to convey correctness.

## Translation Quality

- Prefer natural meaning over literal word-for-word translation.
- Keep instructions concise enough for small screens and TalkBack.
- Avoid marking unreviewed machine translation as final.
- Keep errors specific and actionable.
- Test dialogs and cards with realistic font scaling.
- Review punctuation and numeral placement in both English and Hindi.

## Adding a New Locale

Before declaring another locale supported:

- create the appropriate `values-<locale>/` resource set;
- cover all parity-managed keys;
- verify placeholders;
- review game, learning, settings, import/export, and accessibility surfaces;
- test RTL behavior if applicable;
- document the locale in this file and release notes;
- extend automated parity logic if required.
