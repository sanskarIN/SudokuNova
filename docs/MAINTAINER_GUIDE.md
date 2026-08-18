# SudokuNova Maintainer Guide

This guide describes how to maintain SudokuNova as a correctness-first open-source Android project. It complements `CONTRIBUTING.md`, `CONTRIBUTING_GUIDE.md`, `RELEASING.md`, and the technical documentation.

## Maintainer Priorities

When priorities conflict, prefer:

1. Sudoku correctness and unique-solution integrity;
2. user-data integrity and safe migrations;
3. security/privacy;
4. accessibility;
5. crash/ANR avoidance;
6. deterministic regression coverage;
7. maintainable architecture;
8. UX polish;
9. feature count.

A feature is not worth shipping if it weakens the earlier priorities.

## Branch and Pull-Request Discipline

Use focused branches for milestone/feature work. Keep `main` in a state that represents merged, reviewable work.

For milestone pull requests:

- start from current `main`;
- use focused Conventional Commit-style commits;
- keep the PR draft while major work or required verification is incomplete;
- do not merge until required workflows are green on the exact final head;
- record final workflow evidence before declaring the milestone verified;
- close the milestone issue only after successful merge when that is the stated process.

## Commit Policy

Preferred commit categories include:

- `feat`;
- `fix`;
- `refactor`;
- `test`;
- `testability`;
- `a11y`;
- `perf`;
- `security`;
- `build`;
- `ci`;
- `docs`;
- `chore`.

Examples:

```text
fix(engine): preserve unique-solution analysis
perf(game): move hint analysis off main thread
test(backup): reject oversized streams
docs(data): document SNB1 compatibility rules
ci(release): verify R8 release bundle
```

Avoid artificially splitting inseparable one-line edits only to inflate commit count. Focused history is useful; noisy history is not.

## Commit Identity

Project-authored work uses the configured project commit identity. The documented project commit email is:

```text
sanskarin@outlook.in
```

The connector/API or local Git environment controls the actual author metadata available for a particular commit; do not falsely claim metadata that was not applied.

## Issue Triage

Classify reports by impact:

### Release blocker

Examples:

- invalid Sudoku accepted as valid;
- generated puzzle lacks unique solution;
- restore corrupts/deletes user data;
- migration fails/destructively resets data;
- exploitable security/privacy defect;
- reproducible startup/crash/ANR on supported Android;
- release build broken;
- required accessibility path unusable;
- final CI/connected gate red.

### High priority

Examples:

- common gameplay state loss;
- incorrect statistics/history provenance;
- major localization/accessibility regression;
- frequent but recoverable performance stall.

### Normal

Examples:

- isolated visual defect;
- documentation typo;
- enhancement request;
- non-critical polish.

Security vulnerabilities should follow private reporting instructions in root `SECURITY.md`, not ordinary public triage.

## Reviewing Engine Changes

For `sudoku-engine` changes, verify:

- Android independence is preserved;
- board validity rules remain correct;
- solver returns correct solution counts;
- unique-solution checks are not weakened;
- generator preserves uniqueness;
- deterministic seeds remain deterministic unless intentionally versioned;
- teaching evidence never removes the solved candidate;
- technique ordering changes are intentional;
- Reveal remains clearly separate from logical evidence;
- regression tests cover the changed behavior.

## Reviewing UI Changes

For Compose/UI changes, check:

- no heavy solver/file/database work is performed in composables or on the main thread;
- state survives ordinary lifecycle behavior;
- focus order remains sensible;
- content descriptions/semantics are not removed;
- high-contrast behavior is still readable;
- reduced-motion preference is respected;
- large text/narrow widths remain usable;
- English/Hindi resources stay in parity;
- stable test tags are used only where they improve robust test targeting.

## Reviewing Persistence Changes

For DataStore/Room changes:

- identify existing user data affected;
- provide explicit Room migration when schema changes;
- preserve exported schema history;
- avoid destructive fallback;
- keep indexes aligned with query patterns;
- do not move large archives into Preferences DataStore;
- verify reset scope precisely;
- preserve replay/challenge/favorite provenance;
- review backup format compatibility.

## Reviewing Transfer Changes

For puzzle/backup imports:

- keep hard input bounds;
- reject unknown/malformed versions;
- preserve checksum validation;
- preserve unique-solution validation before imported play;
- do not broaden Android permissions unnecessarily;
- keep file I/O off the main thread;
- avoid leaking unrelated data into share payloads;
- add parser/oversize/duplicate regression tests.

## Dependency Updates

Dependency changes should be deliberate.

Review:

- official release notes/security advisories;
- Android/Gradle/Kotlin compatibility;
- min/compile/target SDK effects;
- license compatibility;
- R8/consumer rules;
- test framework compatibility;
- repository actions versions.

Update `THIRD_PARTY_NOTICES.md` when a new dependency/material requires notice.

Do not update many unrelated build tools in the same commit unless they must move together for compatibility.

## Localization Maintenance

English/default and Hindi are maintained resource sets.

For new strings:

1. add both locales;
2. preserve placeholder signatures;
3. avoid concatenating fragments that produce unnatural grammar;
4. localize accessibility content as well as visible text;
5. run `python scripts/verify_translations.py`.

## Accessibility Maintenance

Accessibility is a release requirement.

During review, verify:

- actions have meaningful labels;
- Sudoku cell semantics retain coordinate/value/clue/conflict/selection information;
- hint evidence is not color-only;
- dialogs have usable focus behavior;
- large font content remains reachable;
- keyboard input still works where supported.

Manual TalkBack/device evidence must not be claimed until actually performed.

## Security Maintenance

Never commit:

- keystores;
- private keys;
- signing passwords;
- API tokens;
- service-account credentials;
- private production configuration.

Run:

```bash
python scripts/verify_no_secrets.py
```

Treat this as one control among review, GitHub secret scanning/settings, least privilege, and secure release environments.

## Documentation Maintenance

Every material change should update the narrowest relevant documentation.

Examples:

- new player feature → `FEATURES.md`, `USER_GUIDE.md`;
- engine behavior → `SUDOKU_ENGINE.md`;
- data format → `DATA_FORMATS.md`;
- persistence schema → `DATA_STORAGE.md`;
- build command → `BUILDING.md`;
- CI gate → `CI_CD.md`;
- release process → `RELEASING.md`/checklists;
- security/privacy → corresponding policy;
- milestone state → root `ROADMAP.md`, `CHANGELOG.md`, `what_changed.md` as appropriate.

`what_changed.md` is evidence-oriented. Do not use it to claim a future test result.

## Release Candidate Process

Before promoting a release candidate:

1. freeze scope;
2. complete code/documentation audit;
3. run the full automated gate;
4. run API-35 connected tests;
5. verify release APK/AAB/R8 outputs;
6. review dependencies/licenses;
7. verify manifest/permissions/privacy;
8. execute manual release QA matrix on actual targets as available;
9. verify large-font/accessibility flows;
10. verify backup/restore and lifecycle scenarios;
11. update changelog/release notes;
12. record exact evidence;
13. tag only after the intended release commit is final.

## Handling a Failed Verification

Never hide or relabel a failed gate as successful.

Instead:

1. preserve the failing evidence/log;
2. isolate root cause;
3. add a regression test when practical;
4. implement a focused fix;
5. run the exact affected gate;
6. run the full required final-head gate before merge;
7. document the defect/fix if release-relevant.

## Backporting

Pre-1.0 maintenance normally targets the active development line. If a security/data-integrity issue requires a backport to a published line, create a dedicated branch from the affected release tag/commit and do not mix unrelated feature work.

After 1.0, maintained version lines should be listed explicitly in `SECURITY.md`.

## Deprecation and Removal

Before removing a feature/format/key/schema field:

- determine whether existing user data depends on it;
- provide migration/compatibility where needed;
- communicate the removal in changelog/release notes;
- remove stale documentation/tests only after the code contract changes;
- avoid silent destructive resets.

## Maintainer Handoff

A handoff should include:

- current branch/PR/issue;
- exact head SHA;
- current version metadata;
- implemented work;
- unresolved defects;
- workflow status/evidence;
- pending manual QA;
- relevant documentation pages;
- whether a release/tag/merge has actually happened.

`what_changed.md` is designed to carry this detailed project history across sessions/maintainers.
