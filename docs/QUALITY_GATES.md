# Quality Gates

SudokuNova's automated repository consistency guards are documented in [Repository Consistency Guards](REPOSITORY_GUARDS.md). Use that guide together with [CI/CD](CI_CD.md), [Testing](TESTING.md), and [Building](BUILDING.md) when preparing changes for merge or release.

Current deterministic pre-build guards include:

- repository secret/signing-material verification;
- release-output verifier unit tests;
- release source/workflow identity-contract tests;
- release source/workflow identity-contract verification;
- repository-local Markdown link tests;
- repository-local Markdown link verification;
- English/Hindi translation parity;
- partial release-signing fail-closed verification.

These gates are source-controlled checks. They do not replace connected Android tests, physical-device performance evidence, manual accessibility QA, production signing/certificate evidence, repository-admin settings, or store/publication validation.
