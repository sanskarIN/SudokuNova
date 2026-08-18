# GitHub Repository Settings Checklist

SudokuNova keeps most quality controls in versioned repository files, but some protections live in GitHub repository settings and cannot be enforced by source code alone.

At the start of the v1.0 RC preparation, the GitHub API reported that `main` was **not protected**. This document records the recommended settings so a maintainer can enable them in GitHub without confusing source-level CI configuration with repository-policy enforcement.

## `main` branch / ruleset

Recommended baseline for a solo-maintainer public repository:

- protect `main` (or create an equivalent repository ruleset targeting `main`);
- require pull requests before merging where the chosen ruleset supports the solo-maintainer workflow;
- require these status checks before merge:
  - `Android CI` / `verify`;
  - `Android Instrumentation` / `connected-tests`;
- require branches to be up to date before merge if this does not create unnecessary queue churn;
- require conversation resolution before merge;
- block force pushes to `main`;
- block branch deletion for `main`;
- do not allow bypass except for a deliberately controlled emergency path;
- keep required checks tied to the exact final PR head.

### Review-count note for a solo maintainer

Do **not** require one approving review unless another trusted reviewer/collaborator is actually available. A pull-request author cannot meaningfully satisfy an independent-review requirement by self-approval. When a second maintainer is available, enable at least one approval and consider dismissing stale approvals when new commits are pushed.

## Required checks

The current versioned workflows are:

- `.github/workflows/ci.yml` — security guard, release-verifier tests, signing fail-closed regression, translation parity, engine tests, app JVM tests, AndroidTest compilation, debug/release lint, debug APK, R8 release APK, release AAB, release artifact verification and SHA-256 evidence;
- `.github/workflows/instrumentation.yml` — API-35 connected Compose/Room tests.

Repository settings should require both workflow jobs for release branches/PRs before merge.

## Merge methods

Repository metadata currently allows merge commits, squash merges and rebase merges. For milestone/release PRs, SudokuNova has historically preserved focused commits through a normal merge commit.

Recommended policy:

- use merge commits for large milestone/release branches when preserving focused history is valuable;
- use squash for small external contributions only when the contributor history is noisy and no evidence references individual commits;
- never rewrite an already released/tagged commit to make history look cleaner.

## Automatically delete merged branches

If GitHub repository settings provide this option, enabling automatic deletion of merged feature branches is recommended after the release process no longer needs the branch ref. The merge commit and PR history preserve the work.

Do not delete a branch before:

- the PR is actually merged;
- exact-head workflow evidence is recorded;
- any release/tag reference that depends on it is finalized.

## Actions permissions

Repository workflows currently request `contents: read`. Keep the default workflow token as least-privileged as practical.

For future production release automation:

- do not expose production signing secrets to ordinary pull-request workflows;
- use a protected GitHub Environment or equivalent secret-management boundary;
- require deliberate/manual approval for production publication where practical;
- avoid `pull_request_target` for workflows that execute untrusted PR code with privileged secrets;
- pin/maintain third-party actions deliberately and review Dependabot updates;
- keep artifact retention finite unless legal/release evidence requires longer archival elsewhere.

## Security features

Where available for the repository/account plan, enable and periodically review:

- Dependabot alerts;
- Dependabot security updates;
- secret scanning / push protection;
- code scanning if adopted;
- private vulnerability reporting if appropriate for the project.

The repository's `scripts/verify_no_secrets.py` is defense in depth and does not replace platform secret scanning.

## CODEOWNERS and review routing

`.github/CODEOWNERS` defines ownership routing for the repository. Confirm that GitHub recognizes the owner account and that future collaborators are added deliberately.

If branch/ruleset settings require code-owner review, ensure the repository has an independent reviewer before enabling that requirement; otherwise a solo-maintainer release could become impossible to merge.

## Issue and PR hygiene

The repository already contains:

- structured issue forms;
- security/support contact routing;
- a pull-request template;
- Dependabot configuration;
- CODEOWNERS;
- funding metadata.

Recommended repository setting:

- disable unrestricted blank issues unless there is a deliberate reason to allow them (the current issue-template config already requests this behavior).

## Releases

For a stable v1.0 GitHub Release:

- tag the exact approved source commit;
- use generated release notes only as a starting point;
- manually verify generated categories/links;
- attach only intended public artifacts;
- publish checksums;
- never attach a keystore, secrets, `local.properties`, private test data or internal logs;
- preserve the R8 mapping securely even if it is not published publicly.

## Manual settings evidence

After enabling repository settings, record:

- date/time;
- maintainer;
- protected branch/ruleset name;
- required checks;
- approval requirement;
- bypass policy;
- screenshot or exported settings evidence if useful and non-sensitive.

Then update `what_changed.md` with the fact that settings were actually enabled. Do not mark this checklist complete merely because the file exists.
