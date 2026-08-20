# SudokuNova Documentation Standards

Documentation is a maintained part of SudokuNova. It must describe the repository that exists, not an imagined future product.

## Core Rule

A document may describe a capability as **implemented** only when corresponding code/configuration exists in the repository.

Use explicit words for status:

- **Implemented** — code exists.
- **Automatically verified** — a relevant automated test/workflow passed for the referenced head.
- **Manually verified** — a human/device/accessibility check was actually performed and recorded.
- **Planned** — future roadmap work.
- **Experimental** — implemented but intentionally not a stable compatibility promise.

Never convert “planned” into “implemented” merely because it appears in a prompt, issue, roadmap, or design proposal.

## Documentation Layers

### Root documents

Keep root files focused on repository-wide entry points/policies:

- `README.md` — concise project landing page;
- `CHANGELOG.md` — notable release changes;
- `ROADMAP.md` — milestone plan/status;
- `CONTRIBUTING.md` — contributor entry point;
- `SECURITY.md` — vulnerability policy;
- `SUPPORT.md` — support channels;
- `THIRD_PARTY_NOTICES.md` — licensing notices;
- `what_changed.md` — detailed evidence/handoff log.

### `docs/`

Put detailed guides/reference material under `docs/` and link **every tracked Markdown guide** from `docs/README.md`. A guide that exists in Git but is absent from the documentation hub is treated as incomplete documentation.

## Tracked-File Documentation Ownership

Every Git-tracked repository file must belong to a documented area. The authoritative coverage contract is [Repository File Reference and Documentation Coverage](REPOSITORY_FILE_REFERENCE.md), enforced by:

```bash
python scripts/verify_documentation_coverage.py
```

The verifier obtains the current file set from `git ls-files -z`; it does not rely on a manually frozen repository tree. Every path must resolve to a coverage rule, and each rule must point to canonical documentation files that are themselves tracked.

The same verifier also checks documentation discoverability: every tracked `docs/*.md` guide except `docs/README.md` itself must be linked from the documentation index.

Use:

```bash
python scripts/verify_documentation_coverage.py --verbose
```

to print the documentation owner for every tracked path during an audit. Use `--markdown` when a deterministic per-file ownership table is useful for review.

When adding a new path family or top-level module, do not hide it under an unrelated broad rule simply to make CI pass. Add a precise ownership rule, regression coverage, and the corresponding documentation before merge. When adding a detailed guide, add it to `docs/README.md` in the same work.

Coverage establishes **ownership and discoverability**, not factual correctness. A passing coverage check must therefore be combined with implementation review, local-link verification, tests/builds, privacy/security review, and exact release evidence where applicable.

## Audience Categories

Every document should primarily serve one or more audiences:

- player/end user;
- contributor;
- engine developer;
- Android developer;
- translator;
- accessibility reviewer;
- security reviewer;
- maintainer/release manager.

Write from that audience's perspective and link to deeper material instead of repeating entire files.

## Naming

Use stable, descriptive uppercase Markdown names for major references, such as:

```text
USER_GUIDE.md
PROJECT_STRUCTURE.md
DATA_FORMATS.md
CI_CD.md
```

Do not create multiple nearly identical guides with unclear authority.

Historical milestone-specific documents may remain when they preserve important implementation context, but current general guides should be the primary entry point. Historical pages must be labeled clearly enough that they cannot be mistaken for current branch/release state.

## Source of Truth

When docs disagree with source code:

1. determine whether code or docs are wrong;
2. inspect tests/configuration/persistent formats;
3. fix the incorrect side;
4. add regression coverage if the mismatch exposed a behavior defect;
5. update cross-links.

For current technical facts, source/configuration is generally authoritative. For intended security/release policy, the policy document may define requirements that code must satisfy.

## Version and Date Claims

Do not hard-code “latest” version claims across many files unnecessarily.

When a version must be stated:

- obtain it from current build configuration;
- identify branch/release context if needed;
- update the statement during version changes.

Use exact dates for release/evidence records, not vague “today/yesterday”.

## Verification Claims

A statement such as:

```text
Android CI is green.
```

is incomplete for release evidence unless the intended context is clear. Prefer:

```text
Android CI run <id> passed on head <sha>.
```

For a final release/merge claim, verify no later commit invalidated that evidence.

Manual QA claims should include target/device information where practical.

## `what_changed.md` Rules

`what_changed.md` is intentionally detailed.

It may include:

- branch/PR/issue;
- exact commits;
- defects discovered;
- files changed;
- workflow runs;
- manual QA evidence;
- handoff/pending work.

It must not include fabricated green tests, device results, signed-release claims, or publication status.

Keep historical sections unless there is a factual correction. Do not erase prior milestone history just to shorten the file.

The top/current-state block must be synchronized with the actual current build/release line. Historical version facts belong in historical sections instead of remaining as a misleading “current” header.

## Changelog Rules

`CHANGELOG.md` should emphasize user/developer-visible release changes, not every tiny commit.

Separate categories such as:

- Added;
- Changed;
- Fixed;
- Security/Correctness;
- Verification.

Do not put an unfinished milestone into completed release history.

## Roadmap Rules

The roadmap is a plan/status tracker, not proof.

Use checkboxes/status labels carefully:

- check only completed work;
- keep manual QA unchecked until performed;
- include issue/PR links when useful;
- avoid promising dates without a real commitment.

## Code Examples

Code/command examples should:

- match the current Gradle/module names;
- distinguish Unix shell from Windows commands where needed;
- avoid credentials/secrets;
- avoid destructive commands unless clearly explained;
- be short enough to remain maintainable.

## Links

For repository-internal docs, prefer relative links:

```markdown
[Testing](TESTING.md)
[Roadmap](../ROADMAP.md)
```

Use absolute external URLs for project/contact links only when appropriate.

After renaming/deleting docs, search for broken references. A new tracked `docs/*.md` page must also be linked from `docs/README.md`.

Run both documentation guards after structural documentation work:

```bash
python scripts/verify_documentation_links.py
python scripts/verify_documentation_coverage.py
```

The first catches missing local Markdown targets. The second catches tracked files with no documentation owner, canonical documentation rules that point at missing files, and detailed guides that are not discoverable from `docs/README.md`.

## Security-Sensitive Documentation

Never paste real:

- tokens;
- passwords;
- keystore secrets;
- private keys;
- recovery codes;
- private user data.

Examples should use obvious placeholders.

Security vulnerability reproduction should avoid publishing exploit-sensitive details before responsible disclosure permits it.

## Privacy Documentation

Privacy/data documentation must match the shipped code.

If the app gains analytics, ads, cloud sync, accounts, crash reporting, or new permissions, update privacy/security/store-data documentation before release.

Do not claim “no data leaves the device” if explicit share/export/external-link features exist. The accurate statement is that the base app has no SudokuNova-operated telemetry/backend and data leaves only through documented user/platform behavior.

## Localization Documentation

When documenting player-facing labels, avoid making English text the only selector/contract. Connected tests may use English when the test locale is intentionally English, but stable semantic tags are preferred where duplicate/lazy text creates brittleness.

Keep English/Hindi localization guidance synchronized with resource parity tooling.

## Accessibility Documentation

Accessibility claims should distinguish:

- semantics implemented in code;
- automated semantics assertions;
- manual TalkBack/large-font/keyboard/device verification.

Do not imply automated Compose tests replace assistive-technology QA.

## Persistent Format Documentation

Changes to:

- Room schema;
- DataStore keys;
- `GameStateCodec`;
- `SNP1`;
- `SNB1`;
- challenge identifiers;

must update compatibility documentation and tests.

If a format changes incompatibly, version it rather than silently changing old semantics.

## Release Documentation

Before release/tagging, cross-check:

- `README.md` version/status;
- `CHANGELOG.md`;
- `ROADMAP.md`;
- `BUILDING.md`;
- `RELEASING.md`;
- `RELEASE_CHECKLIST.md`;
- `RELEASE_QA.md`;
- `PRIVACY.md`;
- `SECURITY.md`;
- `THIRD_PARTY_NOTICES.md`;
- `REPOSITORY_FILE_REFERENCE.md` when repository structure changed;
- `docs/README.md` for complete detailed-guide discoverability;
- `what_changed.md`.

Also run the documentation coverage and link guards against the exact release source so repository structure, detailed-guide indexing, and references are included in release readiness.

## New Documentation Checklist

Before adding a new page:

1. Does an existing page already own this subject?
2. Is the new page clearly scoped?
3. Are facts source-aligned?
4. Are future items labeled planned?
5. Are internal links relative?
6. Is it linked from `docs/README.md`?
7. Does it create duplication that will drift?
8. Does it expose security/private data?
9. Does the repository coverage guard still pass?

## Documentation Review Checklist

For substantial releases, audit:

- stale version numbers;
- stale “planned” statements for implemented work;
- stale “implemented” statements for removed work;
- obsolete commands;
- missing new screens/features;
- broken file links;
- tracked files without documentation ownership;
- tracked detailed guides missing from `docs/README.md`;
- historical milestone pages that read like current state;
- incorrect package/module names;
- build-stack mismatches;
- data-format mismatches;
- privacy/permission mismatches;
- release-evidence accuracy.

## Style

Prefer:

- descriptive headings;
- compact paragraphs;
- lists for actual enumerations;
- code fences for commands/formats;
- explicit warnings where data/security could be affected;
- exact terminology from `GLOSSARY.md`.

Avoid marketing superlatives in technical references. Correctness and traceability matter more than promotional language.
