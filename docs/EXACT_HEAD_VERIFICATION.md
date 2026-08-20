# Exact-Head Verification

A successful workflow run proves only the commit SHA it tested. This rule applies to every SudokuNova pull request or release line whose required workflows are used as merge/release evidence.

## Required final-head rule

Before merge:

1. stop changing the pull-request branch;
2. record the exact final head SHA;
3. require the configured merge-gate workflows to run against that SHA;
4. confirm every required gate is successful on that same SHA;
5. merge with an expected-head check where tooling permits it;
6. record the verified head, run IDs, and merge commit when the change is milestone/release significant.

For the current repository, the usual required pull-request pair is:

- `Android CI / verify`;
- `Android Instrumentation / connected-tests`.

A commit after a green run makes the earlier run historical evidence for the older head. It must not be reused as final-head evidence for the newer commit.

## Documentation-only and tooling changes

Documentation, CI, repository-guard, or maintenance changes can still alter release instructions, workflow behavior, source identity contracts, contributor requirements, or evidence records. When pull-request workflows are configured to run for those changes, they are subject to the same exact-head rule.

For documentation structure changes, also require the local deterministic checks:

```bash
python -m unittest scripts.tests.test_verify_documentation_links
python -m unittest scripts.tests.test_verify_documentation_coverage
python scripts/verify_documentation_links.py
python scripts/verify_documentation_coverage.py
```

## Historical PR #28 example

PR #28 followed this policy and merged only after final head:

```text
c3e0e3fc217062e374a434cfea46235fd6595f83
```

passed:

- Android CI #706 / `32211246803`;
- Android Instrumentation #229 / `32211246802`.

It merged as `27640cb9089ddae4a9242bb84a8927c3761201f4`. See [Post-RC Validation Evidence](POST_RC_VALIDATION_EVIDENCE.md).

## Production evidence remains separate

Exact-head CI proves only the automated repository gates executed by those workflows. It does not by itself prove:

- production signing or expected certificate identity;
- representative physical-device performance;
- TalkBack/manual accessibility QA;
- 200% font/window/orientation/contrast/motion QA;
- process-death/manual lifecycle QA;
- GitHub branch/ruleset/environment administration;
- Play Console/store acceptance;
- stable tag, GitHub Release, or publication.

Use [v1.0 Release Candidate Evidence Worksheet](V1_RELEASE_CANDIDATE.md), [v1.0 Release Evidence Ledger](V1_RELEASE_EVIDENCE.md), [Production Release Validation](PRODUCTION_RELEASE_VALIDATION.md), [Performance Benchmarking](PERFORMANCE_BENCHMARKING.md), and [Releasing](RELEASING.md) for those separate evidence requirements.
