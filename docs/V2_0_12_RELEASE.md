# SudokuNova 2.0.12 Release Line

This document is the current release authority for the SudokuNova **2.0.12** source line. It supersedes v1.0 RC documents for current-version decisions while preserving those older files as historical evidence.

The existence of this document and source metadata does **not** mean 2.0.12 has been published. Production signing, real-device QA, repository administration, store validation, final exact-head verification, tagging, GitHub Release creation, and distribution remain evidence-driven actions.

## Current Source Contract

- Product: SudokuNova
- Android application ID: `in.sanskar.sudokunova`
- Kotlin namespace: `com.sanskar.sudokunova`
- `versionName`: `2.0.12`
- `versionCode`: `2012`
- `minSdk`: `26`
- `targetSdk`: `37`
- `compileSdk`: `37`
- Java/JVM target: `17`
- License: MIT

`versionCode = 2012` follows the project's established release-number pattern and is strictly greater than the previous RC code `1000`.

## Version Sources of Truth

The release identity is intentionally duplicated only at controlled verification boundaries:

1. `app/build.gradle.kts` is the Android source contract.
2. `.github/workflows/ci.yml` contains expected values used to verify ordinary CI release artifacts.
3. `.github/workflows/release-validation.yml` contains protected workflow defaults and immutable application/SDK expectations.
4. `scripts/verify_release_contract.py` fails when these three locations disagree.
5. `scripts/verify_release_outputs.py` verifies generated artifacts against the expected version/application/SDK identity.

Before merging any version change, run:

```bash
python -m unittest scripts.tests.test_verify_release_contract
python scripts/verify_release_contract.py
```

## Repository-Side Quality Gate

The 2.0.12 pull-request head must pass the complete standard Android CI gate, including:

- repository secret/signing-material verification;
- release-verifier unit and CLI-boundary tests;
- documentation-link tests and direct verification;
- complete tracked-file documentation-coverage tests and direct verification;
- release source/workflow contract tests and direct verification;
- partial release-signing fail-closed regression;
- English/Hindi translation parity;
- `:sudoku-engine:test`;
- `:app:testDebugUnitTest`;
- `:app:assembleDebugAndroidTest`;
- `:macrobenchmark:assembleBenchmark`;
- debug and release Android lint;
- debug APK assembly;
- release APK assembly with R8/resource shrinking;
- release AAB assembly;
- release APK/AAB/R8 structure verification;
- exact `versionCode 2012` and `versionName 2.0.12` verification;
- embedded APK application/version/minSdk/targetSdk verification;
- release APK `debuggable=false` verification;
- SHA-256/byte-size evidence generation;
- APK identity evidence generation.

The API-35 Android Instrumentation workflow must also pass on the same exact final pull-request head before merge.

## Exact-Head Rule

A successful workflow run proves only the commit SHA it tested.

For 2.0.12:

1. stop changing the release pull-request branch;
2. record the exact head SHA;
3. require both `Android CI` and `Android Instrumentation` on that SHA;
4. do not reuse green runs from any earlier 1.0 RC, post-RC, or intermediate 2.0.12 commit;
5. merge only after the required pair is green on the final head;
6. record the final verified head and run IDs in `what_changed.md` and release evidence.

See `EXACT_HEAD_VERIFICATION.md`.

## Ordinary CI Artifact Contract

Ordinary CI builds unsigned verification artifacts because production signing material must not be exposed to pull-request jobs.

The expected release-verifier invocation includes:

```text
--expected-version-code 2012
--expected-version-name 2.0.12
--expected-application-id in.sanskar.sudokunova
--require-apk-manifest
--expected-min-sdk 26
--expected-target-sdk 37
```

The verifier requires:

- valid non-empty APK and AAB archives;
- required archive entries;
- a non-empty R8 mapping file;
- exactly one APK output metadata element;
- exact application ID/version metadata;
- exact application ID/version/minimum-SDK/target-SDK values embedded in the APK;
- `debuggable=false` in the release APK;
- deterministic SHA-256/byte-size evidence.

The generated `apk-identity.txt` records the independently inspected APK identity.

Unsigned CI artifacts are verification outputs, not production release binaries.

## Protected Production Validation

The protected `Production Release Validation` workflow defaults to:

- expected version code `2012`;
- expected version name `2.0.12`;
- production application ID `in.sanskar.sudokunova`;
- minimum SDK `26`;
- target SDK `37`.

A real protected run additionally requires external secrets for:

- release keystore bytes;
- keystore password;
- key alias;
- key password;
- expected APK signer certificate SHA-256;
- expected AAB signer/upload certificate SHA-256.

The workflow is designed to:

- materialize the keystore only under `$RUNNER_TEMP`;
- keep production credentials out of the repository;
- build signed R8 APK/AAB outputs;
- verify embedded APK identity and non-debuggable status;
- require a verified APK v2-or-newer signature scheme;
- verify AAB signing;
- bind artifacts to trusted expected signer fingerprints;
- retain non-secret identity/hash/signature/workflow-context evidence;
- remove the temporary keystore in cleanup.

A successful protected run is necessary production evidence, but it still does not replace real-device or store validation.

## Manual Release Evidence Still Required

Do not claim 2.0.12 production-ready solely from source review or CI. Record real evidence for applicable items below.

### Installation and lifecycle

- install the exact signed release APK where direct APK distribution is intended;
- launch and smoke-test the exact signed build;
- verify resume/background/foreground behavior;
- verify representative process-death restoration scenarios;
- verify local state, history, saved puzzles, challenges, learning progress, and backup/restore behavior remain intact.

### Accessibility and adaptive UI

- TalkBack traversal and focus order;
- Sudoku cell semantics and selected/error/hint evidence;
- representative 200% font-scale behavior;
- narrow phone, large phone, tablet/large-window layouts;
- resize/orientation behavior where applicable;
- high-contrast behavior;
- reduced-motion behavior;
- hardware-keyboard paths where applicable.

### Performance

Run representative physical-device Macrobenchmark evidence rather than relying on emulator timing:

```bash
./gradlew :macrobenchmark:connectedBenchmarkAndroidTest --stacktrace
```

Record exact commit, device, OS/API level, benchmark output, traces where relevant, startup/frame findings, memory observations, and ANR/crash findings.

### Store and distribution

- validate current target-API/store requirements at submission time;
- capture screenshots from the current 2.0.12 binary;
- review listing text and release notes;
- review privacy/data/content declarations against actual behavior;
- validate the final AAB through the intended distribution platform;
- distinguish upload-key and Play app-signing certificate identity where applicable;
- verify the final distributed artifact when platform tooling permits it.

## Release Decision

The 2.0.12 source line is not considered published until all mandatory evidence is complete and a deliberate release decision is recorded.

Required final fields:

- final source commit SHA;
- final pull request;
- Android CI run ID/result on exact final head;
- API-35 instrumentation run ID/result on exact final head;
- production validation run ID/result;
- APK SHA-256 and size;
- AAB SHA-256 and size;
- R8 mapping SHA-256 and size;
- APK embedded identity evidence;
- APK signer certificate SHA-256;
- AAB signer/upload certificate SHA-256;
- representative physical-device QA evidence;
- accessibility/manual QA evidence;
- store validation evidence;
- final decision: `SHIP` or `NO-SHIP`;
- decision owner/date;
- immutable `v2.0.12` tag only after `SHIP`;
- GitHub Release/store publication only after `SHIP`.

## Historical v1 Documents

The following remain useful historical evidence and should not be rewritten to pretend they were 2.0.12 work:

- `V1_RELEASE_PREP.md`;
- `V1_RELEASE_CANDIDATE.md`;
- `V1_RELEASE_EVIDENCE.md`;
- `V1_RELEASE_NOTES.md`;
- `POST_RC_VALIDATION_EVIDENCE.md`;
- `BRANCH_FREEZE.md`.

They document how the v1 RC and post-RC repository hardening was prepared and verified. This file is the current release-version authority.

## Supporting References

Use these together with this document:

- `BUILDING.md`;
- `TESTING.md`;
- `CI_CD.md`;
- `REPOSITORY_GUARDS.md`;
- `REPOSITORY_FILE_REFERENCE.md`;
- `PRODUCTION_SIGNING.md`;
- `PRODUCTION_RELEASE_VALIDATION.md`;
- `PERFORMANCE_BENCHMARKING.md`;
- `ACCESSIBILITY.md`;
- `RELEASE_CHECKLIST.md`;
- `RELEASE_QA.md`;
- `PLAY_STORE_RELEASE.md`;
- `GITHUB_REPOSITORY_SETTINGS.md`;
- `RELEASING.md`.

## Evidence Boundary

Repository-side completion means the source, tests, workflows, release contracts, documentation ownership, and automated gates are prepared for 2.0.12. It must never be described as proof that production signing, physical-device QA, repository administration, Play Console acceptance, tagging, GitHub Release creation, or publication has already occurred.
