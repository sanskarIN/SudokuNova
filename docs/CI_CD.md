# SudokuNova CI/CD and Automated Quality Gates

SudokuNova uses GitHub Actions as a verification system. The current repository automates build/test/release-artifact evidence, but it does **not** automatically publish a production release or store submission.

## Workflow Overview

Current workflows live under `.github/workflows/`.

### Android CI — `ci.yml`

The standard pull-request gate verifies source integrity, repository/documentation consistency, tests, lint, release compilation, performance-harness compilation and RC artifact evidence.

Current v1.0 RC stages include:

1. checkout;
2. Java 17 setup;
3. Gradle wrapper/cache validation;
4. Android SDK `apkanalyzer` discovery for embedded-manifest inspection;
5. repository secret/signing-material guard;
6. release-output verifier Python unit tests and CLI-boundary tests;
7. repository-guard regression tests for documentation links, complete tracked-file documentation coverage and release source/workflow identity;
8. partial release-signing fail-closed regression;
9. direct repository consistency guards for documentation links, complete tracked-file documentation coverage and release source/workflow identity;
10. English/Hindi translation parity;
11. `:sudoku-engine:test`;
12. `:app:testDebugUnitTest`;
13. `:app:assembleDebugAndroidTest`;
14. `:macrobenchmark:assembleBenchmark`;
15. debug and release Android lint;
16. debug APK assembly;
17. release APK assembly with R8/resource shrinking;
18. release Android App Bundle assembly;
19. release APK/AAB/R8 mapping structural/application/version verification;
20. independent APK-embedded application/version/SDK/debuggable verification;
21. SHA-256/byte-size and embedded-identity evidence generation;
22. report/test-result artifact upload;
23. successful release APK/AAB/R8 mapping/checksum/identity artifact upload for short-lived verification evidence.

### Android Instrumentation — `instrumentation.yml`

The connected gate runs Android Compose/Room tests on an API-35 emulator target. The workflow configures Java/Gradle, KVM access where supported, disables animations, runs connected tests, and uploads instrumentation reports.

This gate is particularly important for:

- Compose navigation and semantics;
- Room database behavior/migrations;
- history/saved-puzzle behavior;
- challenge flows;
- transfer/backup flows;
- Learn/practice UI behavior;
- accessibility semantic regressions that can be asserted reliably.

### Production Release Validation — `release-validation.yml`

This is a separate manually dispatched workflow for trusted signed-release validation. It uses the `production-release` GitHub Environment and is intentionally outside ordinary pull-request execution.

It requires protected signing secrets, validates the operator-supplied version inputs, reconstructs the keystore only in `$RUNNER_TEMP`, builds signed R8 APK/AAB outputs, requires production `applicationId = in.sanskar.sudokunova`, binds the expected `minSdk`/`targetSdk` values to the source release contract, independently inspects those identity values plus `debuggable=false` from the built APK, verifies signatures, requires a verified v2-or-newer APK signature scheme, binds both artifacts to protected expected signer-certificate SHA-256 fingerprints, records hashes/sizes/identity/signature fingerprints/exact workflow context, and removes the temporary keystore in cleanup.

The workflow uploads non-secret production validation evidence after success. Signed APK/AAB upload is opt-in and short-lived rather than automatic.

A successful protected run is still not a substitute for physical-device, accessibility, performance, Play Console, listing/privacy, branch-protection, or publication evidence.

See `PRODUCTION_RELEASE_VALIDATION.md` and `PRODUCTION_SIGNING.md`.

## Pull-Request Gate Policy

A pull request intended for merge should not be treated as verified until the required workflows are green on the **exact final head commit**.

If a code/documentation commit that triggers the pull-request workflows is added after a successful run, the new head must be verified again.

Do not cite a workflow run from an older head as evidence for a newer head.

PR #27 completed and merged the verified RC1 repository-preparation line. PR #28 completed and merged the verified post-RC validation/performance-tooling line. Any later hardening/documentation/stable-promotion pull request must independently satisfy the same exact-final-head rule before merge.

## Repository Security Guard

`scripts/verify_no_secrets.py` is executed by standard CI.

It is designed to reject committed material such as:

- Android keystores;
- private-key files;
- obvious credential assignments/patterns.

This is a defense-in-depth repository guard, not a substitute for GitHub secret scanning or careful review.

Production signing material must remain outside version control.

## Documentation and Repository-Contract Gates

Standard Android CI runs deterministic regression tests for the repository guards and then executes the guards against the checked-out pull-request head.

### Documentation link integrity

```bash
python -m unittest scripts.tests.test_verify_documentation_links
python scripts/verify_documentation_links.py
```

This rejects missing repository-local Markdown file/image targets and links that escape the repository root.

### Complete tracked-file documentation coverage

```bash
python -m unittest scripts.tests.test_verify_documentation_coverage
python scripts/verify_documentation_coverage.py
```

The direct guard obtains the exact tracked-file set with `git ls-files -z`. Every tracked path must resolve to a maintained documentation area, every canonical document referenced by that area must itself be tracked, and every tracked detailed `docs/*.md` guide must be discoverable from `docs/README.md`. A new uncovered path or hidden guide fails closed.

Use this for an audit-friendly mapping of every tracked path:

```bash
python scripts/verify_documentation_coverage.py --verbose
```

See `REPOSITORY_FILE_REFERENCE.md` for the ownership taxonomy and `REPOSITORY_GUARDS.md` for guard semantics.

### Release source/workflow identity

```bash
python -m unittest scripts.tests.test_verify_release_contract
python scripts/verify_release_contract.py
```

`scripts/verify_release_contract.py` treats `app/build.gradle.kts` as the source-controlled release contract for:

- production application ID;
- version code;
- version name;
- minimum SDK;
- target SDK.

The guard compares those values with the expectations encoded in ordinary CI and the protected production-validation workflow. CI fails if any release identity drifts. The parser also rejects non-positive release/SDK integers, unsafe version-name characters, a production application ID ending in `.debug`, and a target SDK lower than the minimum SDK.

These repository-contract gates are deliberately cheap and run before the Gradle source/build workload. They reduce the chance of spending a full Android build on a branch that already has deterministic repository drift.

## Release-Verifier Unit Gate

The RC line runs:

```bash
python -m unittest scripts.tests.test_verify_release_outputs
python -m unittest scripts.tests.test_verify_release_cli_validation
```

This verifies both the pure-Python release-output checker and its command-line input boundaries before the verifier is trusted to validate built artifacts.

Coverage includes:

- minimum valid APK/AAB archive structures;
- missing required archive entry rejection;
- single release metadata parsing;
- multiple release metadata element rejection;
- production application ID parsing/rejection when missing;
- wrong expected application ID rejection;
- deterministic checksum-manifest content;
- embedded APK manifest application/version/minimum-SDK/target-SDK/debuggable inspection;
- embedded debuggable-release and SDK-drift rejection;
- stable embedded-identity evidence output;
- expected SDK argument positivity and minimum/target ordering validation;
- certificate-fingerprint normalization;
- `apksigner` certificate-digest parsing;
- `apksigner` verified-signature-scheme parsing;
- v1-only APK signature rejection when signed validation is required;
- `keytool` certificate-fingerprint parsing;
- expected APK signer identity acceptance/rejection;
- expected AAB signer identity acceptance/rejection;
- normalized signature-evidence output;
- missing or invalid signature-verifier tool results.

## Partial-Signing Fail-Closed Gate

`app/build.gradle.kts` supports optional release signing through four environment variables, but partial configuration is forbidden.

CI intentionally invokes Gradle with only a test alias value and requires configuration to fail with the expected partial-signing error.

This proves the repository will not silently interpret a half-configured production-signing environment as permission to produce an unsigned release.

See `PRODUCTION_SIGNING.md`.

## Translation Parity Gate

`scripts/verify_translations.py` verifies the maintained English/Hindi resource contract.

When adding player-facing strings:

1. add the default/English resource;
2. add the Hindi counterpart;
3. preserve compatible formatting placeholders;
4. run the verifier locally;
5. do not bypass parity to land an untranslated feature.

## Engine Gate

Run:

```bash
./gradlew :sudoku-engine:test --stacktrace
```

This is the fastest correctness gate for domain changes and covers board, solver, generator, difficulty, teaching evidence, hint logic and practice behavior.

Because `sudoku-engine` has no Android dependency, engine failures should normally be diagnosed before investigating Android build issues.

## Android JVM Gate

Run:

```bash
./gradlew :app:testDebugUnitTest --stacktrace
```

This covers pure/local Android-module behavior that does not require an emulator, including state codecs, persistence models, transfer parsing and learning/statistics behavior.

The app module uses JUnit4 for these tests unless the build configuration is intentionally changed.

## Instrumentation Compilation Gate

Run:

```bash
./gradlew :app:assembleDebugAndroidTest --stacktrace
```

This verifies that Android test sources and the test APK compile even before an emulator run.

Compilation success is not equivalent to connected-test success.

## Macrobenchmark Compilation Gate

Standard CI also runs:

```bash
./gradlew :macrobenchmark:assembleBenchmark --stacktrace
```

This proves that the release-like `benchmark` app variant, the separate `com.android.test` Macrobenchmark module and its instrumentation APK remain buildable together.

The benchmark path intentionally differs from the normal debug/instrumentation path:

- the app `benchmark` build type is initialized from `release`;
- release R8/resource shrinking behavior is preserved;
- the target app remains non-debuggable;
- debug signing is used for local benchmarkability without production credentials;
- `<profileable android:shell="true">` is present only in the benchmark source set;
- AndroidX ProfileInstaller is included in the target app so Macrobenchmark can perform supported profile/reset and shader-cache operations;
- the benchmark test APK explicitly declares visibility of `in.sanskar.sudokunova`;
- current benchmark methods cover cold startup, warm startup and cold-start frame timing.

A successful hosted CI compile does **not** satisfy the v1.0 performance evidence row. Representative timing evidence must come from the connected Macrobenchmark suite on physical hardware and be recorded with the exact release commit, device/OS and raw output/traces:

```bash
./gradlew :macrobenchmark:connectedBenchmarkAndroidTest --stacktrace
```

See `PERFORMANCE_BENCHMARKING.md`.

## Lint Gates

Current release verification runs both:

```bash
./gradlew :app:lintDebug :app:lintRelease --stacktrace
```

Release lint matters because release-only resource shrinking/minification/configuration can differ from debug.

A lint failure should be fixed or, if a suppression is genuinely necessary, narrowly justified at the relevant code/configuration point.

## Build Gates

### Debug APK

```bash
./gradlew :app:assembleDebug --stacktrace
```

### Release APK

```bash
./gradlew :app:assembleRelease --stacktrace
```

The release build has minification and resource shrinking enabled. In ordinary CI, production signing secrets are absent, so the release APK is a verification artifact rather than a production-signed binary.

### Release AAB

```bash
./gradlew :app:bundleRelease --stacktrace
```

The AAB is the expected store-oriented Android bundle format. Final production distribution still requires proper secure signing and distribution-platform validation.

## Release-Output Verification Gate

After release APK/AAB/R8 mapping generation, ordinary CI runs `scripts/verify_release_outputs.py` with the exact expected RC identity:

- `applicationId in.sanskar.sudokunova`;
- `versionCode 1000`;
- `versionName 1.0.0-rc.1`;
- `minSdk 26`;
- `targetSdk 37`.

The verifier requires:

- non-empty release APK;
- valid ZIP-based APK structure;
- APK manifest and primary DEX entries;
- non-empty release AAB;
- AAB bundle config, base manifest and base DEX entries;
- non-empty R8 `mapping.txt`;
- exactly one APK release metadata element;
- exact expected application ID;
- exact expected version code/name;
- exact embedded APK application/version/minimum-SDK/target-SDK identity;
- embedded `debuggable=false`.

It writes SHA-256 and byte-size evidence for APK, AAB and mapping to:

```text
app/build/outputs/release-evidence/sha256.txt
```

It also writes the independently inspected APK identity to:

```text
app/build/outputs/release-evidence/apk-identity.txt
```

A successful ordinary verifier result does **not** prove production certificate identity or device installability.

### Signed/certificate-bound mode

A protected release environment additionally uses:

```text
--expected-application-id in.sanskar.sudokunova
--require-apk-manifest
--expected-min-sdk <source value>
--expected-target-sdk <source value>
--apk-identity-output <path>
--require-signatures
--expected-apk-cert-sha256 <trusted fingerprint>
--expected-aab-cert-sha256 <trusted fingerprint>
--signature-output <path>
```

In this mode the verifier requires the production package/version/SDK/non-debuggable identity from the APK itself, requires `apksigner`, `jarsigner`, and `keytool`, verifies cryptographic signatures, requires a verified APK v2-or-newer signature scheme, extracts normalized signer SHA-256 fingerprints, and fails when identities do not match the trusted expected values.

The expected fingerprints must come from a trusted release record or platform certificate record—not from the artifact being tested.

## Connected API-35 Gate

The repository workflow runs connected functional tests on API 35. To reproduce locally, use a compatible emulator/device and:

```bash
./gradlew :app:connectedDebugAndroidTest --stacktrace
```

Local device/emulator names and hardware acceleration vary by environment.

This connected functional gate is separate from physical-device Macrobenchmark evidence. An emulator is appropriate for deterministic Compose/Room integration verification but should not be treated as representative production timing evidence.

## Artifact Policy

Ordinary CI may upload:

- test reports;
- lint/build reports;
- unsigned release APK output;
- release AAB output;
- R8 mapping output;
- SHA-256 release evidence;
- embedded APK identity/SDK/debuggable evidence.

Ordinary CI currently compiles the Macrobenchmark harness but does not publish hosted-emulator timing numbers as performance evidence.

A real physical-device benchmark evidence package may retain the benchmark output and traces according to the release evidence process, provided it contains no sensitive/private data.

The protected production-validation workflow may upload:

- non-secret signature/hash/embedded-identity/workflow-context evidence by default;
- signed APK/AAB/R8 mapping only after explicit workflow input opts into short-lived artifact retention.

These artifacts are verification evidence and have limited retention. They should not automatically be described as published production releases.

Do not publish a CI-built artifact as production unless application/package identity, version/SDK identity, signing, certificate identity, provenance, manual QA, legal/store metadata and the exact final artifact have been validated.

## Production Signing Boundary

Normal pull-request CI intentionally receives no production signing secrets.

The build supports secret-backed signing only when all four required `SUDOKUNOVA_*` build environment values are available in a controlled release environment. Ordinary PRs, forks and untrusted code must not receive those secrets.

The protected workflow additionally requires a base64 keystore secret and expected APK/AAB signer certificate SHA-256 fingerprints in the `production-release` GitHub Environment. The workflow pins the application ID and SDK expectations itself rather than accepting them as dispatch inputs.

See:

- `REPOSITORY_GUARDS.md`;
- `REPOSITORY_FILE_REFERENCE.md`;
- `PRODUCTION_SIGNING.md`;
- `PRODUCTION_RELEASE_VALIDATION.md`;
- `PERFORMANCE_BENCHMARKING.md`;
- `V1_RELEASE_CANDIDATE.md`;
- `V1_RELEASE_EVIDENCE.md`;
- `RELEASING.md`.

## No Automatic Production Deployment

A green ordinary CI run does not authorize the system to:

- create a Play Store release;
- publish an APK/AAB publicly;
- create a stable production tag automatically;
- expose signing secrets;
- claim physical-device QA;
- claim TalkBack/200% font/process-death validation;
- claim representative startup/frame/memory/ANR performance evidence merely because the Macrobenchmark harness compiles;
- claim production-signed artifact identity.

A successful protected production-validation run can establish package/version/SDK/debuggable/signature/certificate identity for the exact generated artifacts, but it still does not authorize store publication or substitute for the remaining manual/admin/store evidence.

Release/publishing remains a controlled maintainer action documented in the release guides.
