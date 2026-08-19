# Repository Consistency Guards

SudokuNova uses small deterministic repository guards to prevent documentation, repository-structure, security, localization, and release-contract drift before expensive Android build work begins.

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

## Complete tracked-file documentation coverage

Run:

```bash
python scripts/verify_documentation_coverage.py
```

Unit tests:

```bash
python -m unittest scripts.tests.test_verify_documentation_coverage
```

The coverage guard reads the authoritative tracked-file set with `git ls-files -z`. Every tracked path must resolve to a documented repository area. Each area names one or more canonical documentation files, and those files must themselves still be tracked.

The same guard also requires **every tracked Markdown guide below `docs/`** to be linked from `docs/README.md`. A guide therefore cannot be silently present in Git while remaining undiscoverable from the maintained documentation hub.

A new path fails closed when it is outside the maintained taxonomy. A new `docs/*.md` guide also fails closed until it is indexed. Together these checks make "document every file" and "do not hide detailed guides" enforceable repository invariants rather than static checklists that become stale as files are added or moved.

For audit output:

```bash
python scripts/verify_documentation_coverage.py --verbose
python scripts/verify_documentation_coverage.py --markdown
```

`--verbose` prints one ownership line for every tracked file. `--markdown` emits a deterministic per-file table suitable for review/evidence capture. The successful command summary also reports how many detailed guides are indexed.

The complete area-to-document mapping and maintenance rules are defined in [Repository File Reference and Documentation Coverage](REPOSITORY_FILE_REFERENCE.md). The categorized guide inventory is [SudokuNova Documentation](README.md).

Coverage is deliberately specific for Android instrumentation tests, JVM tests, Room schemas, resources, benchmark overlays, Android app source, Sudoku engine source/tests, Macrobenchmark, verification scripts/tests, GitHub workflows/metadata, Gradle/build files, repository metadata, and the documentation library. Narrow rules are evaluated before module-level fallbacks.

A passing coverage guard proves that every tracked path has documentation ownership and every tracked detailed guide is discoverable from the docs index. It does not prove the prose is factually current, nor does it replace implementation review, link verification, build/test/manual/release evidence.

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

## Repository security guard

Run the compatibility entry point:

```bash
python scripts/verify_no_secrets.py
```

The underlying repository security verifier rejects committed signing/private-key bundles, known credential configuration filenames, PEM private-key material, and obvious credential/token patterns covered by the deterministic policy. See the root [`SECURITY.md`](../SECURITY.md) for the authoritative reporting/security policy.

The guard is defense in depth, not a claim that automated scanning can identify every possible secret.

## Translation parity guard

Run:

```bash
python scripts/verify_translations.py
```

The translation guard keeps the maintained English/Hindi resource contract synchronized. See [Localization](LOCALIZATION.md) for scope and placeholder/accessibility expectations.

## Release artifact verifier

`scripts/verify_release_outputs.py` validates generated release artifacts rather than only source configuration. Depending on invocation it verifies expected package/version metadata, required APK/AAB structure, archive integrity, R8 mapping presence, SHA-256 evidence, signatures, and expected signer-certificate identity.

Regression tests:

```bash
python -m unittest scripts.tests.test_verify_release_outputs
```

See [Building](BUILDING.md), [Production Signing](PRODUCTION_SIGNING.md), and [Production Release Validation](PRODUCTION_RELEASE_VALIDATION.md) before treating artifact verification as release evidence.

## CI policy

Standard Android CI runs repository-guard regression tests and direct guard commands before the main Gradle verification/build workload. This includes documentation links, complete tracked-file documentation ownership plus detailed-guide index completeness, release source/workflow identity, repository security, and translation parity at their appropriate stages.

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

When adding, moving, renaming, or deleting repository files, run both the documentation-coverage and Markdown-link guards. If a new path family is intentional, add a narrow coverage rule and regression test rather than disguising it under an unrelated area. If a detailed guide is added, ensure `docs/README.md` links it before merge.
