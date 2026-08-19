# What Changed

## Current Development State

**Repository:** `https://github.com/sanskarIN/SudokuNova`  
**Base branch:** `main`  
**Base commit for this continuation:** `4b42c009e6efc9a03806d5cf0122802c0cee7203`  
**Working branch:** `release/v1.0-verifier-hardening-2`  
**Pull request:** `#29` — `release: harden embedded APK production verification`  
**Stable release:** **not yet claimed**  
**Primary remaining-release tracker:** issue `#5`

The cumulative historical version of this ledger has been preserved without rewriting or dropping its earlier entries at:

`docs/archive/what_changed_through_2026-08-19.md`

This root file is the active continuation ledger from the `4b42c009e6efc9a03806d5cf0122802c0cee7203` checkpoint forward.

---

## Continuation Goal — 2026-08-19

Continue every repository-side v1.0 task that can be completed truthfully in source control, with granular commits, while refusing to fabricate evidence for actions that require a real device, production secrets, GitHub administration, Play Console access, or an actual stable publication decision.

The previous post-RC work had already established strong release-output, signing-certificate, Macrobenchmark, documentation, and exact-head CI evidence. This continuation focused on a remaining release-assurance gap: release verification trusted Android Gradle Plugin `output-metadata.json` for application/version identity, but it did not independently prove that the APK itself contained the expected application/version/SDK/debuggable values.

That gap is now addressed on PR #29.

---

## Release Verifier Hardening

### Embedded APK manifest identity inspection

`scripts/verify_release_outputs.py` now supports an explicit `--require-apk-manifest` mode.

When required, the verifier locates Android SDK `apkanalyzer` and independently reads these values from the built APK:

- application ID;
- version code;
- version name;
- minimum SDK;
- target SDK;
- debuggable state.

The artifact is rejected when:

- `apkanalyzer` is required but unavailable;
- any expected scalar cannot be inspected;
- the embedded application ID differs from the expected production application ID;
- the embedded version code differs;
- the embedded version name differs;
- the embedded minimum SDK differs;
- the embedded target SDK differs;
- the release APK reports `debuggable=true`.

This is intentionally independent of `output-metadata.json`. Ordinary metadata verification remains in place, so CI now validates both build metadata and the artifact-embedded identity instead of relying on only one representation.

### Deterministic embedded-identity evidence

The verifier can write `apk-identity.txt` through `--apk-identity-output`.

The evidence records:

- `application_id`;
- `version_code`;
- `version_name`;
- `min_sdk`;
- `target_sdk`;
- `debuggable`.

This gives a protected stable-release run a small non-secret identity artifact that can be retained alongside checksum, signature, and workflow-context evidence.

### APK signature-scheme hardening

Certificate-bound APK validation still uses `apksigner verify --verbose --print-certs`, but successful process exit and certificate output are no longer sufficient by themselves.

The verifier now parses verified APK signature schemes and requires at least one verified **v2-or-newer** signature scheme for mandatory signed-release validation.

A v1-only verified APK is rejected by the production-verification path.

### SDK argument validation

The verifier now validates expected SDK values before reading artifacts:

- expected minimum SDK must be positive when supplied;
- expected target SDK must be positive when supplied;
- expected target SDK cannot be lower than expected minimum SDK;
- SDK expectations and identity-evidence output cannot be supplied without `--require-apk-manifest`.

The checks use explicit `is not None` handling so invalid numeric zero is treated as supplied input and rejected rather than accidentally bypassing the guard through Python truthiness.

---

## Release-Contract Hardening

`scripts/verify_release_contract.py` previously synchronized:

- production application ID;
- version code;
- version name.

It now additionally synchronizes:

- `minSdk`;
- `targetSdk`.

`app/build.gradle.kts` remains the source-controlled release identity. The guard now compares that source contract with both:

- ordinary `.github/workflows/ci.yml` expectations;
- protected `.github/workflows/release-validation.yml` expectations.

The contract parser rejects:

- non-positive version/SDK integers;
- target SDK lower than minimum SDK;
- unsafe release-version-name characters;
- a production application ID using the `.debug` suffix;
- duplicate/missing expected contract values;
- ordinary/protected workflow drift from Gradle source metadata.

Current RC contract remains:

- application ID: `in.sanskar.sudokunova`;
- version code: `1000`;
- version name: `1.0.0-rc.1`;
- minimum SDK: `26`;
- target SDK: `37`.

These are still RC values. They have **not** been promoted to final stable metadata in this continuation.

---

## Test Coverage Added

### `scripts/tests/test_verify_release_outputs.py`

Regression coverage now includes:

- mocked `apkanalyzer` extraction of application/version/minimum-SDK/target-SDK/debuggable values;
- rejection of a debuggable release APK;
- rejection of target-SDK drift;
- deterministic `apk-identity.txt` evidence;
- parsing of verified `apksigner` signature schemes;
- acceptance of an APK with a verified v2 signature plus the expected certificate;
- rejection of a v1-only APK in mandatory production-signature mode;
- the existing package/version/archive/signature/certificate/hash behavior.

### `scripts/tests/test_verify_release_contract.py`

Coverage now includes:

- Gradle minimum/target SDK parsing;
- rejection of target SDK below minimum SDK;
- ordinary-CI SDK expectation parsing;
- protected-workflow SDK expectation parsing;
- SDK drift reporting together with package/version drift.

### `scripts/tests/test_verify_release_cli_validation.py`

A dedicated CLI-boundary regression module was added for:

- zero expected minimum SDK rejection before artifact access;
- target SDK below minimum SDK rejection before artifact access;
- identity-evidence output rejection when manifest verification is not enabled.

Both ordinary Android CI and protected production validation execute this new module.

---

## Ordinary Android CI Changes

`.github/workflows/ci.yml` now:

1. locates Android SDK `apkanalyzer` explicitly and adds its directory to `GITHUB_PATH`;
2. runs the existing release-output verifier tests;
3. runs the new release-verifier CLI-boundary tests;
4. keeps documentation-link and release-contract regression tests;
5. keeps the direct repository consistency guards;
6. keeps engine/JVM/instrumentation-compilation/Macrobenchmark-compilation/lint/build gates;
7. verifies release `output-metadata.json` identity/version;
8. independently verifies APK-embedded application/version/minimum-SDK/target-SDK/debuggable values;
9. writes both checksum evidence and `apk-identity.txt`;
10. retains the generated identity evidence through the existing `release-evidence/**` artifact path.

Ordinary CI still does **not** receive production signing credentials and therefore does not claim production certificate identity.

### Tool-discovery fail-closed review

A final patch review found that the first version of the `apkanalyzer` fallback ran `find` under `set -euo pipefail`. If the expected SDK subdirectory itself were missing, that pipeline could terminate the step before the intended explicit “Unable to locate Android SDK apkanalyzer” error branch.

The fallback now suppresses the expected missing-directory diagnostic and terminates the discovery pipeline successfully so the explicit empty-result guard owns the failure message. This does not weaken verification: a missing tool still fails the job, but now through the deliberate fail-closed branch.

---

## Protected Production Release Validation Changes

`.github/workflows/release-validation.yml` now pins these source-contract values in job environment:

- `EXPECTED_APPLICATION_ID = in.sanskar.sudokunova`;
- `EXPECTED_MIN_SDK = 26`;
- `EXPECTED_TARGET_SDK = 37`.

The protected workflow now:

- runs release-contract regression coverage;
- runs direct source/workflow contract verification before signing work;
- runs release-output and CLI-boundary verifier tests;
- locates both `apksigner` and `apkanalyzer`;
- verifies the signed APK's embedded production identity;
- proves the signed APK is non-debuggable;
- proves the embedded minimum/target SDK match the source contract;
- requires a verified APK v2-or-newer signature scheme;
- preserves expected certificate SHA-256 comparison for APK and AAB;
- writes production `apk-identity.txt`;
- adds minimum/target SDK values to `workflow-context.txt`;
- continues to keep production signing values step-scoped;
- continues to materialize the keystore only under `$RUNNER_TEMP`;
- continues to remove the materialized keystore in an `always()` cleanup step.

The same final patch review hardened fallback discovery for both `apksigner` and `apkanalyzer`, so absent SDK tool directories produce the workflow's intentional explicit failure instead of an incidental `find`/pipefail termination.

No production secrets were added to repository source.

---

## Documentation Updated

### `docs/PRODUCTION_RELEASE_VALIDATION.md`

Updated to document:

- independent APK-embedded identity verification;
- `minSdk`/`targetSdk` release-contract binding;
- `debuggable=false` requirement;
- v2-or-newer APK signature-scheme requirement;
- CLI-boundary validation;
- new `apk-identity.txt` evidence;
- SDK values in workflow-context evidence;
- stable-evidence fields that should be copied only after a real successful protected run.

### `docs/CI_CD.md`

Updated to document:

- APK Analyzer discovery;
- embedded manifest verification in ordinary CI;
- SDK-aware release-contract verification;
- new CLI-boundary regression tests;
- new embedded-identity evidence;
- protected signed-release v2-or-newer scheme requirement;
- the unchanged boundary between automated repository evidence and real physical-device/store evidence.

### `docs/RELEASE_CHECKLIST.md`

Added explicit unchecked release requirements for:

- correct `minSdk`/`targetSdk`;
- release-contract guard success;
- structural output verification;
- output-metadata identity verification;
- independent APK-embedded identity/SDK verification;
- `debuggable=false` verification;
- `apk-identity.txt` retention;
- protected production validation;
- APK v2-or-newer signature verification;
- trusted APK/AAB certificate fingerprint matching;
- release-contract and release-output evidence belonging to the exact final head.

No manual rows were marked complete by documentation edits.

---

## Granular Commits in This Continuation

The branch was intentionally developed through small commits rather than one large change.

1. `8a8f60dd954dd3de930df8020436e86cd6c5ba2e` — `release: verify embedded APK manifest identity`
2. `2165511502ad5038f5d82b0939900d244c4bd85d` — `test(release): cover embedded APK manifest verification`
3. `3f6472d56ac4ed13fb0d275a42f97f21aa25d1dd` — `release: bind SDK levels into source contract`
4. `e6342929586aeb28518b50ffaea2923490a90a45` — `test(release): cover SDK release contract drift`
5. `cb0c13090561022b12ef76108639056a9b44eaa6` — `ci(release): inspect embedded release APK manifest`
6. `132fd7b6c28b85e68407968f880604739fdf5946` — `ci(release): bind signed APK to embedded manifest`
7. `c603d604187cd93bac015570b4c98cd3d8c2aa11` — `release: validate APK SDK expectation bounds`
8. `632e52f3daab19ff4a22272efe356abd74e4157e` — `test(release): cover release verifier CLI bounds`
9. `1c635261b1828c579be37e337372ccedec2d485a` — `ci(release): run verifier CLI validation tests`
10. `2119a2fc245cfe656cf0ebc446c4d796c8d9158e` — `ci(release): run verifier CLI bounds in production gate`
11. `a147b29739d068f65590cb4c711c5bc5b871f2e1` — `docs(release): document embedded APK identity evidence`
12. `62679b98ab71f197e61e9d22459874c4a687b7d2` — `docs(ci): record embedded APK and SDK contract gates`
13. `5f030f8d2eed96a0be3f789e7f09ae538f6b41d3` — `docs(release): add embedded identity release checks`
14. `e1e7af6da0eed77e2accd3fe7241ba412217ce5e` — `docs(progress): archive cumulative development ledger`
15. `fc8c0b7a5b18ca3dff55e619ed8b5ffdf0fc6871` — `docs(progress): refresh v1.0 release hardening ledger`
16. `a2a29514a95d90f29409059d79f962114e4730f0` — `ci(release): fail clearly when APK Analyzer is absent`
17. `71026852cd5b497a576c55ba7e82c81a428fd5a6` — `ci(release): harden Android tool discovery`

This final active-ledger update is intentionally another separate documentation commit so the implementation, test, workflow, review-fix, and progress-record changes remain individually traceable.

GitHub's Git commit objects for this continuation report author/committer `Sanskar <sanskarin@outlook.in>`, matching the requested commit email configuration.

---

## Pull Request

PR `#29` was opened from:

`release/v1.0-verifier-hardening-2` → `main`

Title:

`release: harden embedded APK production verification`

The PR is linked to issue `#5` and explicitly preserves the stable-release evidence boundary.

### Merge policy for PR #29

Do **not** merge merely because GitHub reports the PR as mergeable or because an older branch head passed checks.

The required repository-side merge condition remains:

- Android CI green on the exact final PR head;
- API-35 connected instrumentation green on the exact final PR head;
- no later branch commit after those successful runs;
- no unresolved regression found in the PR diff/review.

Before this ledger refresh, Android CI run `#723 / 32240224413` and API-35 instrumentation run `#233 / 32240224396` had been created for head `71026852cd5b497a576c55ba7e82c81a428fd5a6`. Because this ledger refresh creates a newer PR head, those older-head runs must **not** be used as final merge evidence even if they later pass. The new ledger head requires its own exact-head checks.

---

## Tooling / Environment Notes

### Local clone limitation encountered

A local container `git clone` attempt could not resolve `github.com` because that execution environment had no usable DNS/network path.

No repository state was fabricated from that failed local attempt. Development continued through the connected GitHub repository API so source reads/writes and commit identities remained attached to the real repository.

### GitHub administration boundary

The connected repository tools can create/update source, branches, commits, pull requests, reviews, and merges, but repository/account-level settings must still be backed by actual GitHub evidence before being marked complete.

The project must not claim branch rulesets/protection, protected-environment reviewer restrictions, production secrets, Play Console validation, or publication unless those states are actually verified.

---

## Remaining v1.0 Work — Still Not Complete

Issue `#5` remains open because these items require evidence outside ordinary repository source editing.

### GitHub administration

- [ ] Verify/enable the intended `main` branch protection or ruleset.
- [ ] Verify required status checks are enforced for the final release policy.
- [ ] Verify the `production-release` GitHub Environment exists and is restricted appropriately.
- [ ] Verify protected environment reviewers/ref restrictions as applicable.
- [ ] Verify all required signing/certificate secrets exist in that environment without exposing their values.

### Manual accessibility / device QA

- [ ] Real TalkBack traversal and action semantics.
- [ ] Representative 200% font-scale QA.
- [ ] High-contrast manual review.
- [ ] Reduced-motion manual review.
- [ ] Large-screen/window/resizing manual review.
- [ ] Hardware-keyboard behavior where applicable.
- [ ] Minimum-supported Android device/environment QA.
- [ ] Representative modern phone QA.
- [ ] Representative large-phone/tablet QA.

### Lifecycle / reliability evidence

- [ ] Real process-death/resume scenarios.
- [ ] Large valid backup/restore responsiveness review.
- [ ] Release-scope crash/ANR review with real runtime evidence.

### Physical-device performance evidence

- [ ] Run connected Macrobenchmark tests on representative physical hardware.
- [ ] Record exact commit/device/OS/raw startup results.
- [ ] Record frame-timing evidence.
- [ ] Record memory evidence.
- [ ] Do not substitute hosted compilation/emulator success for representative performance evidence.

### Production signing / artifact evidence

- [ ] Configure real protected production/upload signing material outside source control.
- [ ] Run **Production Release Validation** on the exact intended stable ref.
- [ ] Verify signed APK/AAB certificate identities against trusted records.
- [ ] Review generated `apk-identity.txt`, `signatures.txt`, `sha256.txt`, `verification.txt`, and `workflow-context.txt`.
- [ ] Install/smoke-test the final signed APK when direct APK distribution is intended.
- [ ] Smoke-test the final signed/R8 release behavior.

### Store / stable publication

- [ ] Review current target-API/store requirements at publication time.
- [ ] Capture screenshots from the actual current release UI.
- [ ] Verify store listing claims against implemented functionality.
- [ ] Verify privacy/Data Safety answers against the actual binary/data behavior.
- [ ] Finalize stable version code/name.
- [ ] Re-run exact-head Android CI and connected instrumentation after final stable metadata changes.
- [ ] Record SHIP decision.
- [ ] Create final stable tag only after all release gates are backed by evidence.
- [ ] Create GitHub Release only after the exact stable commit is approved.
- [ ] Perform store/direct publication only after the corresponding checks are complete.

---

## Evidence Boundary

What is complete in this continuation is **repository-side release-verification hardening**.

What is **not** complete is the real-world stable release.

No unchecked manual/admin/signing/store item above should be treated as complete merely because PR #29 is eventually green or merged. A green PR can prove the repository logic and automated gates on its exact head; it cannot prove actions that were never actually performed.
