# Building SudokuNova

## Required Tooling

Current repository configuration:

- JDK 17
- Gradle 9.5
- Android Gradle Plugin 9.3.1
- Kotlin 2.4.10
- Android compile/target SDK 37
- Android application min SDK 26
- Macrobenchmark test-module min SDK 29
- AndroidX Benchmark Macrobenchmark 1.4.1
- AndroidX ProfileInstaller 1.4.1

Use the committed Gradle wrapper rather than a globally installed Gradle version so local and CI builds use the same Gradle distribution.

## Current 2.0.12 Metadata

The active source/release line uses:

- application ID: `in.sanskar.sudokunova`
- version code: `2012`
- version name: `2.0.12`
- minimum SDK: `26`
- target SDK: `37`
- compile SDK: `37`

`docs/V2_0_12_RELEASE.md` is the current release authority. Historical `1000 / 1.0.0-rc.1` metadata belongs to the verified v1 RC evidence and must not be used as the current build contract.

Any later distributed build must use a version code strictly greater than every code already accepted by the intended distribution platform.

## Clone

```bash
git clone https://github.com/sanskarIN/SudokuNova.git
cd SudokuNova
```

For the current final 2.0.12 source line before PR #30 is merged:

```bash
git switch docs/complete-repository-coverage
```

Use `main` after the corresponding pull request is actually verified and merged. Do not assume an open release branch has already landed.

## Gradle Sync

Android Studio performs sync when the project opens. From the command line, any Gradle task will configure the build. A lightweight check is:

```bash
./gradlew tasks
```

Windows:

```bat
gradlew.bat tasks
```

## Clean

```bash
./gradlew clean
```

Do not make clean builds the default development workflow. Use `clean` when stale generated state is actually suspected or when producing final signed release evidence.

## Debug APK

Linux/macOS:

```bash
./gradlew :app:assembleDebug
```

Windows:

```bat
gradlew.bat :app:assembleDebug
```

Expected output:

```text
app/build/outputs/apk/debug/app-debug.apk
```

The debug build uses the `.debug` application-ID suffix and `-debug` version-name suffix.

## Release APK

Compile and shrink the release APK with:

```bash
./gradlew :app:assembleRelease
```

Windows:

```bat
gradlew.bat :app:assembleRelease
```

Expected output directory:

```text
app/build/outputs/apk/release/
```

Without production-signing environment values, the ordinary CI/local verification output is expected to be an unsigned release APK such as:

```text
app/build/outputs/apk/release/app-release-unsigned.apk
```

The release build enables R8 code shrinking and resource shrinking.

## Android App Bundle (AAB)

Linux/macOS:

```bash
./gradlew :app:bundleRelease
```

Windows:

```bat
gradlew.bat :app:bundleRelease
```

Expected output:

```text
app/build/outputs/bundle/release/app-release.aab
```

A store-distributable production artifact must be signed through the protected release environment described in [Production Signing](PRODUCTION_SIGNING.md) and [Production Release Validation](PRODUCTION_RELEASE_VALIDATION.md). Repository source control intentionally contains no production signing secret.

## Release Mapping Output

When minification succeeds, preserve the generated release mapping files together with the exact source commit used for the build:

```text
app/build/outputs/mapping/release/
```

The main mapping file used by the release verifier is:

```text
app/build/outputs/mapping/release/mapping.txt
```

Mapping output is needed to de-obfuscate matching production crash traces. Preserve it securely for every public release.

## Benchmark Build and Macrobenchmark Harness

SudokuNova has a release-like app build type named `benchmark` plus a separate `:macrobenchmark` Android test module.

The app benchmark variant:

- is initialized from the production `release` build type;
- preserves release R8/resource shrinking behavior;
- remains non-debuggable;
- uses debug signing only so local measurement does not require production signing material;
- enables shell profiling only through `app/src/benchmark/AndroidManifest.xml`;
- keeps the production release manifest unchanged by that benchmark-only profiling declaration.

The app includes AndroidX ProfileInstaller 1.4.1 so Android's Macrobenchmark tooling can perform supported profile/reset and shader-cache operations. This dependency does **not** mean SudokuNova currently ships a project-generated Baseline Profile.

Compile the benchmark harness without running measurements:

Linux/macOS:

```bash
./gradlew :macrobenchmark:assembleBenchmark --stacktrace
```

Windows:

```bat
gradlew.bat :macrobenchmark:assembleBenchmark --stacktrace
```

This task is part of standard Android CI. It proves only that the benchmark app/test graph compiles.

Run the committed startup/frame benchmarks on a connected representative physical target with:

Linux/macOS:

```bash
./gradlew :macrobenchmark:connectedBenchmarkAndroidTest --stacktrace
```

Windows:

```bat
gradlew.bat :macrobenchmark:connectedBenchmarkAndroidTest --stacktrace
```

The benchmark test module targets API 29+. For the current compilation-reset workflow, Android 14 / API 34+ is the preferred release-evidence target. Do not use hosted-emulator timing as production performance evidence.

See [Performance Benchmarking and Evidence](PERFORMANCE_BENCHMARKING.md) for device, evidence and interpretation rules.

## Production Signing

SudokuNova supports optional release signing through four environment values:

- `SUDOKUNOVA_KEYSTORE_PATH`
- `SUDOKUNOVA_KEYSTORE_PASSWORD`
- `SUDOKUNOVA_KEY_ALIAS`
- `SUDOKUNOVA_KEY_PASSWORD`

Behavior is deliberate:

- none configured → unsigned CI-safe release build;
- all four configured → release signing enabled;
- only some configured → Gradle configuration fails closed.

See [Production Signing](PRODUCTION_SIGNING.md) for secure examples and certificate verification.

## Protected Signed-Release Validation

The manually dispatched `.github/workflows/release-validation.yml` workflow is intended for a restricted GitHub Environment named `production-release`.

Current protected-workflow defaults are:

- version code `2012`;
- version name `2.0.12`;
- application ID `in.sanskar.sudokunova`;
- minimum SDK `26`;
- target SDK `37`.

When the environment is actually configured, the workflow can:

- reconstruct the release keystore temporarily outside the repository;
- build signed release APK/AAB outputs;
- require the exact application/version/SDK source contract;
- inspect embedded APK application/version/minSdk/targetSdk/debuggable identity;
- require `debuggable=false`;
- verify APK/AAB signatures;
- require a verified APK v2-or-newer signature scheme;
- require the expected APK and AAB signer-certificate SHA-256 identities;
- write non-secret hash/identity/signature/workflow-context evidence;
- remove the temporary keystore afterward.

The source-controlled workflow does not prove that the environment, reviewers, allowed refs, secrets, or production key have been configured. See [Production Release Validation](PRODUCTION_RELEASE_VALIDATION.md).

## Release Contract Guard

Before building release artifacts, verify the source/workflow identity contract:

```bash
python -m unittest scripts.tests.test_verify_release_contract
python scripts/verify_release_contract.py
```

The guard treats `app/build.gradle.kts` as the source contract and requires ordinary CI plus protected production validation to agree on:

- application ID;
- version code;
- version name;
- minimum SDK;
- target SDK.

It also rejects invalid numeric values, target SDK below minimum SDK, unsafe release version names, duplicate/missing contract values, and a production application ID ending in `.debug`.

## Documentation / Repository Guards

Run:

```bash
python -m unittest scripts.tests.test_verify_documentation_links
python -m unittest scripts.tests.test_verify_documentation_coverage
python scripts/verify_documentation_links.py
python scripts/verify_documentation_coverage.py
```

The documentation-coverage verifier reads `git ls-files -z`, so every tracked file must have maintained documentation ownership and every tracked detailed `docs/*.md` guide must remain discoverable from `docs/README.md`.

For a per-file audit:

```bash
python scripts/verify_documentation_coverage.py --verbose
```

## Release Artifact Verifier

The current release line includes:

```text
scripts/verify_release_outputs.py
```

It validates:

- non-empty APK/AAB/mapping outputs;
- valid ZIP-based APK/AAB structure;
- expected APK and AAB core entries;
- exactly one APK release metadata element;
- expected production application ID;
- expected version code/name;
- optional independent APK embedded application/version/minSdk/targetSdk/debuggable verification;
- release APK `debuggable=false` when embedded verification is required;
- SHA-256 and byte-size evidence for APK, AAB and mapping;
- optional APK/AAB cryptographic signatures;
- a verified APK v2-or-newer signature scheme in mandatory signed-release mode;
- optional expected APK/AAB signer-certificate SHA-256 identities;
- optional normalized signer-certificate evidence output;
- optional deterministic APK identity evidence output.

Run the verifier tests:

```bash
python -m unittest scripts.tests.test_verify_release_outputs
python -m unittest scripts.tests.test_verify_release_cli_validation
```

After unsigned 2.0.12 outputs exist, verify them with:

```bash
python scripts/verify_release_outputs.py \
  --apk app/build/outputs/apk/release/app-release-unsigned.apk \
  --aab app/build/outputs/bundle/release/app-release.aab \
  --mapping app/build/outputs/mapping/release/mapping.txt \
  --metadata app/build/outputs/apk/release/output-metadata.json \
  --expected-version-code 2012 \
  --expected-version-name 2.0.12 \
  --expected-application-id in.sanskar.sudokunova \
  --require-apk-manifest \
  --expected-min-sdk 26 \
  --expected-target-sdk 37 \
  --output app/build/outputs/release-evidence/sha256.txt \
  --apk-identity-output app/build/outputs/release-evidence/apk-identity.txt
```

PowerShell:

```powershell
python scripts/verify_release_outputs.py `
  --apk app/build/outputs/apk/release/app-release-unsigned.apk `
  --aab app/build/outputs/bundle/release/app-release.aab `
  --mapping app/build/outputs/mapping/release/mapping.txt `
  --metadata app/build/outputs/apk/release/output-metadata.json `
  --expected-version-code 2012 `
  --expected-version-name 2.0.12 `
  --expected-application-id in.sanskar.sudokunova `
  --require-apk-manifest `
  --expected-min-sdk 26 `
  --expected-target-sdk 37 `
  --output app/build/outputs/release-evidence/sha256.txt `
  --apk-identity-output app/build/outputs/release-evidence/apk-identity.txt
```

This unsigned verifier path is artifact-integrity/build evidence. It does not prove production certificate identity or device installability.

For the actual signed release artifact, use the certificate-bound mode documented in `PRODUCTION_SIGNING.md`, `PRODUCTION_RELEASE_VALIDATION.md`, and `V2_0_12_RELEASE.md` rather than treating successful unsigned CI as production-signing evidence.

## Unit Tests

```bash
./gradlew :sudoku-engine:test
./gradlew :app:testDebugUnitTest
```

Windows:

```bat
gradlew.bat :sudoku-engine:test
gradlew.bat :app:testDebugUnitTest
```

## Android Instrumentation-Test Compilation

```bash
./gradlew :app:assembleDebugAndroidTest
```

This verifies connected-test sources compile before an emulator/device run.

## Macrobenchmark-Test Compilation

```bash
./gradlew :macrobenchmark:assembleBenchmark
```

This is a build/compatibility gate, not a performance-result gate.

## Android Lint

Debug and release lint must both pass:

```bash
./gradlew :app:lintDebug :app:lintRelease
```

## Repository Security Guard

```bash
python scripts/verify_no_secrets.py
```

This is defense in depth against accidentally committed signing/private-key/obvious credential material.

## Translation Parity

English/Hindi resource parity is a required release gate:

```bash
python scripts/verify_translations.py
```

## Full 2.0.12 Local Non-Connected Verification

Linux/macOS:

```bash
python scripts/verify_no_secrets.py
python -m unittest scripts.tests.test_verify_documentation_links
python -m unittest scripts.tests.test_verify_documentation_coverage
python -m unittest scripts.tests.test_verify_release_contract
python -m unittest scripts.tests.test_verify_release_outputs
python -m unittest scripts.tests.test_verify_release_cli_validation
python scripts/verify_documentation_links.py
python scripts/verify_documentation_coverage.py
python scripts/verify_release_contract.py
python scripts/verify_translations.py
./gradlew :sudoku-engine:test \
  :app:testDebugUnitTest \
  :app:assembleDebugAndroidTest \
  :macrobenchmark:assembleBenchmark \
  :app:lintDebug \
  :app:lintRelease \
  :app:assembleDebug \
  :app:assembleRelease \
  :app:bundleRelease \
  --stacktrace
python scripts/verify_release_outputs.py \
  --apk app/build/outputs/apk/release/app-release-unsigned.apk \
  --aab app/build/outputs/bundle/release/app-release.aab \
  --mapping app/build/outputs/mapping/release/mapping.txt \
  --metadata app/build/outputs/apk/release/output-metadata.json \
  --expected-version-code 2012 \
  --expected-version-name 2.0.12 \
  --expected-application-id in.sanskar.sudokunova \
  --require-apk-manifest \
  --expected-min-sdk 26 \
  --expected-target-sdk 37 \
  --output app/build/outputs/release-evidence/sha256.txt \
  --apk-identity-output app/build/outputs/release-evidence/apk-identity.txt
```

Windows PowerShell:

```powershell
python scripts/verify_no_secrets.py
python -m unittest scripts.tests.test_verify_documentation_links
python -m unittest scripts.tests.test_verify_documentation_coverage
python -m unittest scripts.tests.test_verify_release_contract
python -m unittest scripts.tests.test_verify_release_outputs
python -m unittest scripts.tests.test_verify_release_cli_validation
python scripts/verify_documentation_links.py
python scripts/verify_documentation_coverage.py
python scripts/verify_release_contract.py
python scripts/verify_translations.py
.\gradlew.bat :sudoku-engine:test `
  :app:testDebugUnitTest `
  :app:assembleDebugAndroidTest `
  :macrobenchmark:assembleBenchmark `
  :app:lintDebug `
  :app:lintRelease `
  :app:assembleDebug `
  :app:assembleRelease `
  :app:bundleRelease `
  --stacktrace
python scripts/verify_release_outputs.py `
  --apk app/build/outputs/apk/release/app-release-unsigned.apk `
  --aab app/build/outputs/bundle/release/app-release.aab `
  --mapping app/build/outputs/mapping/release/mapping.txt `
  --metadata app/build/outputs/apk/release/output-metadata.json `
  --expected-version-code 2012 `
  --expected-version-name 2.0.12 `
  --expected-application-id in.sanskar.sudokunova `
  --require-apk-manifest `
  --expected-min-sdk 26 `
  --expected-target-sdk 37 `
  --output app/build/outputs/release-evidence/sha256.txt `
  --apk-identity-output app/build/outputs/release-evidence/apk-identity.txt
```

API-35 connected functional tests and representative physical-device Macrobenchmarks remain separate connected gates.

## Partial-Signing Fail-Closed Regression

CI intentionally proves that a partial signing environment is rejected. A local equivalent on Bash-compatible systems is:

```bash
SUDOKUNOVA_KEY_ALIAS=partial-test ./gradlew :app:tasks --quiet
```

That command should fail with the repository's partial-signing configuration error. Do not set real secret values merely to test this guard.

## Install Debug APK with ADB

```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

For signed release installation, use the exact signed artifact and complete the manual evidence in [SudokuNova 2.0.12 Release Line](V2_0_12_RELEASE.md).

## CI Release Verification

`.github/workflows/ci.yml` verifies on the current 2.0.12 line:

1. Android SDK `apkanalyzer` discovery;
2. repository security guard;
3. release-artifact verifier unit and CLI-boundary tests;
4. documentation-link, documentation-coverage, and release-contract regression tests;
5. partial release-signing fail-closed regression;
6. direct documentation-link, documentation-coverage, and release-contract guards;
7. English/Hindi translation parity;
8. Sudoku engine tests;
9. Android JVM unit tests;
10. Android instrumentation-test compilation;
11. Macrobenchmark harness compilation;
12. debug lint;
13. release lint;
14. debug APK assembly;
15. release APK assembly with R8/resource shrinking;
16. release AAB assembly;
17. release APK/AAB/mapping structure plus exact `2012 / 2.0.12` application/version metadata;
18. embedded application/version/minSdk/targetSdk/debuggable verification;
19. SHA-256 and APK-identity evidence generation;
20. verification reports and release outputs as short-lived workflow artifacts.

`.github/workflows/instrumentation.yml` separately runs the API-35 connected Compose/Room suite.

`.github/workflows/release-validation.yml` separately performs protected signed-release identity verification only after a maintainer deliberately dispatches it in a correctly configured `production-release` environment.

CI-produced unsigned release artifacts are build-verification outputs. Macrobenchmark compilation is test-harness evidence. Neither is automatically a production-publishable package or representative physical-device performance result.

## Gradle Wrapper Verification

The repository wrapper is pinned through `gradle/wrapper/gradle-wrapper.properties`. GitHub's Gradle setup action validates wrapper JARs during CI.

For reproducible investigation of a release failure, record at minimum:

- exact Git commit SHA;
- Java version;
- Gradle wrapper version;
- Android SDK/Build Tools environment;
- command executed;
- relevant workflow run ID when CI was used;
- release artifact SHA-256/identity evidence when outputs were produced;
- physical device/OS and raw benchmark output when performance evidence is being collected.

## Build Types

### Debug

- Development build.
- Debug application-ID suffix.
- Debug version-name suffix.
- Intended for local/CI testing.

### Benchmark

- Initialized from `release`.
- Minification/resource shrinking inherited from release.
- Non-debuggable target app.
- Debug signed for local performance measurement.
- Shell profileable only through the benchmark source-set manifest.
- Target of the separate `:macrobenchmark` test module.
- Not a production-distribution artifact.

### Release

- Minification enabled.
- Resource shrinking enabled.
- Uses optimized default ProGuard rules plus `app/proguard-rules.pro`.
- Must pass release lint, R8, AAB and release-output verification for the current merge/release gate.
- Can remain unsigned for normal CI verification.
- Uses secret-backed signing only when all required release-signing environment values exist.
- Production release manifest is not made shell-profileable by the benchmark source set.

## Release-Signing Rules

SudokuNova follows these source-control rules:

- no `.jks`, `.keystore`, `.p12`, `.pfx`, PEM private keys, or signing passwords in Git;
- no signing secret embedded in Gradle files, `local.properties`, Android resources, Kotlin/Java code, or workflow YAML;
- no ordinary pull-request workflow may receive production signing secrets;
- signing configuration is all-or-nothing and fails closed when partial;
- fork and pull-request builds remain safe when signing secrets are unavailable;
- protected signing workflow secrets are scoped to the minimum practical workflow steps;
- a failed or missing production-signing step must never be bypassed by committing credentials.

## Common Failure Checks

If a build fails:

1. Confirm Java 17.
2. Confirm Android SDK 37 is installed.
3. Confirm you are building the intended branch and exact commit.
4. Confirm the Gradle wrapper rather than an unrelated global Gradle is being used.
5. Check whether one to three `SUDOKUNOVA_*` signing variables were accidentally inherited from the shell; partial configuration is intentionally rejected.
6. Run `./gradlew clean` only when stale build state is suspected.
7. Read the first real compilation/test/lint/R8/verifier error rather than only the final Gradle stack trace.
8. For release-only failures, inspect R8 diagnostics and `app/proguard-rules.pro` before adding broad keep rules.
9. If artifact verification fails, inspect the exact built path, `output-metadata.json`, mapping output, embedded APK identity, expected production application ID and expected `2012 / 2.0.12` values.
10. If the release-contract guard fails, reconcile `app/build.gradle.kts`, ordinary CI, and protected release workflow expectations rather than weakening the guard.
11. If documentation coverage fails, document the new path/index instead of adding an unrelated broad ownership escape hatch.
12. If Macrobenchmark assembly fails, inspect `:app` benchmark variant resolution, benchmark manifest/profileable configuration, ProfileInstaller dependency, test package visibility and `:macrobenchmark` build-type matching before changing the release build.
13. Check `TROUBLESHOOTING.md` and CI logs.

## Release Evidence Policy

A release-quality statement must be tied to actual evidence. Documentation may describe required checks before they run, but it must not claim physical-device coverage, accessibility success, performance numbers, production signing, certificate identity, repository administration, Play Console acceptance, tag creation, GitHub Release publication, or store readiness unless those checks/actions were actually performed and recorded.
