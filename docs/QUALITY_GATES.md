# Quality Gates

SudokuNova's automated repository consistency guards are documented in [Repository Consistency Guards](REPOSITORY_GUARDS.md). Use that guide together with [CI/CD](CI_CD.md), [Testing](TESTING.md), [Building](BUILDING.md), and the [Repository File Reference](REPOSITORY_FILE_REFERENCE.md) when preparing changes for merge or release.

## Deterministic pre-build guards

Current source-controlled checks include:

- repository secret/signing-material verification;
- release-output verifier unit tests;
- release source/workflow identity-contract tests;
- release source/workflow identity-contract verification;
- repository-local Markdown link tests;
- repository-local Markdown link verification;
- complete tracked-file documentation-coverage tests;
- complete tracked-file documentation-coverage verification;
- documentation-index completeness for every tracked `docs/*.md` guide;
- English/Hindi translation parity;
- partial release-signing fail-closed verification.

The documentation coverage verifier obtains the authoritative tracked file set from Git rather than from a manually copied tree:

```bash
python -m unittest scripts.tests.test_verify_documentation_coverage
python scripts/verify_documentation_coverage.py
```

For a per-file ownership audit:

```bash
python scripts/verify_documentation_coverage.py --verbose
```

## Build and test gates

Standard Android CI then verifies the platform-independent engine, Android JVM tests, instrumentation-test compilation, Macrobenchmark harness compilation, debug/release lint, debug APK, R8/resource-shrunk release APK, release AAB, release-output metadata/structure, and SHA-256 evidence according to [CI/CD](CI_CD.md).

The API-35 Android Instrumentation workflow separately runs connected Compose/Room coverage.

## Evidence boundary

These gates are source-controlled checks. They do not replace:

- real connected/manual behavior outside automated coverage;
- representative physical-device startup/frame/memory/ANR evidence;
- real TalkBack, 200% font, window/orientation, contrast, motion, or hardware-keyboard QA;
- production signing and trusted signer-certificate evidence;
- protected GitHub Environment/branch/ruleset administration;
- distribution-platform/store validation;
- final stable SHIP, tag, release, or publication evidence.

A green documentation-coverage gate proves every tracked path has a documentation owner and every detailed guide is discoverable from `docs/README.md`; it does not prove every statement in those documents is current. Implementation alignment and exact-head verification remain separate review requirements.
