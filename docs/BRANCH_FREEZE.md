# v1.0 Validation-Hardening Branch Freeze

This page is a **historical PR #28 release-hardening record**. PR #28 has already satisfied its exact-head merge rule and merged. Current work must use the general [Exact-Head Verification](EXACT_HEAD_VERIFICATION.md), [CI/CD](CI_CD.md), and [Releasing](RELEASING.md) rules rather than treating this page as an active branch status report.

## Historical PR #28 rule

PR #28 was intended to merge only after the exact final branch head passed both required repository workflows. Earlier successful or queued workflow runs applied only to the commit they tested.

Required merge gates were:

- `Android CI / verify`;
- `Android Instrumentation / connected-tests`.

If the branch changed after a successful run, the relevant exact-head verification had to be repeated.

## Historical result

The rule was satisfied on final PR #28 head:

```text
c3e0e3fc217062e374a434cfea46235fd6595f83
```

Verified workflow evidence:

- Android CI run #706 / `32211246803` — PASS;
- Android Instrumentation run #229 / `32211246802` — PASS.

PR #28 then merged as:

```text
27640cb9089ddae4a9242bb84a8927c3761201f4
```

See [Post-RC Validation Evidence](POST_RC_VALIDATION_EVIDENCE.md) for the full immutable record.

## Evidence boundary

The historical branch freeze proved only repository-side exact-head merge readiness for PR #28. It did not prove physical-device performance, production signing, repository-admin configuration, manual accessibility/device QA, store validation, or stable publication. Those requirements remain governed by the current v1.0 release evidence and issue #5.
