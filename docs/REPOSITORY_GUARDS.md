# Repository Consistency Guards

SudokuNova uses small deterministic repository guards to prevent documentation and release-contract drift before expensive Android build work begins.

## Documentation link integrity

Run:

```bash
python scripts/verify_documentation_links.py
```

Unit tests:

```bash
python -m unittest scripts.tests.test_verify_documentation_links
```

The guard checks repository-local Markdown file and image targets, including inline and reference-definition links. It ignores external URLs and in-document anchor-only links, rejects links that escape the repository root, and skips generated/internal directories such as `build`, `.gradle`, `.idea`, and `.git`.

The check deliberately verifies target-file existence rather than renderer-specific heading-anchor generation.

## Release source/workflow contract

Run:

```bash
python scripts/verify_release_contract.py
```

Unit tests:

```bash
python -m unittest scripts.tests.test_verify_release_contract
```

The guard treats `app/build.gradle.kts` as the current Android release identity source and requires the ordinary CI verifier plus the protected release-validation workflow defaults to agree on:

- production `applicationId`;
- Android `versionCode`;
- Android `versionName`.

It also rejects non-positive version codes, unsafe release-version characters, duplicate source identity declarations, and a production application ID ending in `.debug`.

This source/workflow consistency check complements—but does not replace—`scripts/verify_release_outputs.py`, which validates the actual generated APK/AAB/R8 outputs and can additionally verify signatures and expected certificate identities.

## CI policy

Standard Android CI runs the guard unit tests and both direct guard commands before Gradle compilation/test work.

The protected Production Release Validation workflow also runs the release source/workflow contract guard before building signed APK/AAB outputs.

A guard failure must be fixed at the source of the drift. Do not bypass or weaken a guard merely to make a release branch green.

## Stable-promotion rule

When changing package/version metadata for stable `1.0.0` or a later release:

1. update `app/build.gradle.kts` deliberately;
2. update ordinary CI expected artifact metadata in the same change;
3. update protected release-validation defaults in the same change;
4. run the release-contract verifier;
5. run release-output verification against the artifacts built from that exact source;
6. record new exact-head workflow and artifact evidence rather than reusing earlier RC evidence.

When moving, renaming, or deleting documentation files, run the Markdown-link guard before merge so repository-local references remain valid.
