# SudokuNova CI/CD and Automated Quality Gates

SudokuNova uses GitHub Actions as a verification system. The repository automates source, shared-platform, Android, packaging, and release-artifact evidence, but it does **not** automatically prove production signing, physical-device quality, store acceptance, notarization, or public distribution.

For cross-platform commands/boundaries see [`CROSS_PLATFORM.md`](CROSS_PLATFORM.md). For the current release contract see [`V2_0_13_RELEASE.md`](V2_0_13_RELEASE.md).

## Workflow Overview

Current workflows live under `.github/workflows/`.

### Android CI — `ci.yml`

The standard pull-request gate protects both the shared KMP layers and the mature Android release line.

Current 2.0.13 stages include:

1. checkout, Java 17, Gradle wrapper/cache validation;
2. Android SDK `apkanalyzer` discovery;
3. repository secret/signing-material guard;
4. release-output verifier unit/CLI tests;
5. documentation-link, documentation-coverage, release-contract, and translation regression tests;
6. partial release-signing fail-closed regression;
7. direct documentation, release-contract, and translation guards;
8. `:sudoku-engine:desktopTest`;
9. `:sharedUI:desktopTest`;
10. `:sharedUI:compileKotlinDesktop` and `:sharedUI:compileKotlinWasmJs`;
11. `:app:testDebugUnitTest`;
12. `:app:assembleDebugAndroidTest`;
13. `:macrobenchmark:assembleBenchmark`;
14. debug/release lint;
15. debug APK;
16. R8/resource-shrunk release APK;
17. release AAB;
18. exact `in.sanskar.sudokunova` / `2013` / `2.0.13` identity verification;
19. embedded APK minSdk 26 / targetSdk 37 / `debuggable=false` verification;
20. SHA-256, byte-size, and APK-identity evidence;
21. short-lived report and successful unsigned release-output uploads.

Shared tests/compilation remain ahead of expensive Android release work so portable failures are fixed at their source.

### Cross-Platform CI — `cross-platform.yml`

This workflow validates repository-supported targets on appropriate hosted operating systems.

#### Shared code — Ubuntu

```bash
./gradlew :sudoku-engine:desktopTest
./gradlew :sharedUI:desktopTest
./gradlew :sharedUI:compileKotlinDesktop
./gradlew :sharedUI:compileKotlinWasmJs
```

The engine suite includes the established `SNP1` compatibility vector and common `PuzzleExchangeService` unique-solution acceptance. Shared gameplay tests include deterministic keyboard-style grid movement and active-game persistence/restore behavior.

#### Android shared integration — Ubuntu

```bash
./gradlew :app:assembleDebug
```

This proves the mature Android app can consume both shared modules. It does not replace Android CI's stricter release gates.

#### Web/Wasm distribution — Ubuntu

```bash
./gradlew :sharedUI:wasmJsBrowserDistribution
```

A successful build proves repository packaging for that exact head, not browser/device compatibility.

#### iOS Simulator framework — macOS

```bash
./gradlew :sharedUI:linkDebugFrameworkIosSimulatorArm64
```

This proves Kotlin/Native framework linking, not Xcode production-host completeness, signing/provisioning, device behavior, TestFlight/App Store acceptance, or publication.

#### Desktop application images — Linux, Windows, macOS

Each host runs:

```bash
./gradlew :sharedUI:createDistributable
```

The shared Desktop package version is `2.0.13` and is now checked by the release-contract guard against Android `versionName`. Signing, notarization, installer reputation, and clean-machine distribution QA remain external evidence.

### Android Instrumentation — `instrumentation.yml`

The connected gate runs Compose/Room tests on API 35. It protects real connected behavior such as navigation/semantics, Room flows/migrations, challenges, backup/transfer, Learn/practice, and automatically assertable accessibility behavior.

Instrumentation-test compilation in Android CI is not equivalent to this connected-test evidence.

### Production Release Validation — `release-validation.yml`

This manually dispatched workflow is reserved for trusted Android production-signing validation through the `production-release` GitHub Environment.

Current source defaults bind:

- application ID `in.sanskar.sudokunova`;
- versionCode `2013`;
- versionName `2.0.13`;
- minSdk 26;
- targetSdk 37.

When the external environment is actually configured, it can reconstruct a temporary keystore outside the repository, build signed R8 APK/AAB outputs, verify embedded identity, require verified APK v2-or-newer signing, verify AAB signing, bind signer certificates to protected SHA-256 fingerprints, retain non-secret evidence, and remove temporary signing material.

Committed workflow source is not proof that reviewers, allowed refs, secrets, keys, or a successful protected run exist.

## Exact-Head Pull-Request Policy

A pull request is not verified until every required workflow family is green on the **same exact final head SHA**.

For current cross-platform/release changes:

- Android CI — green on final head;
- Android Instrumentation — green on final head;
- Cross-Platform CI — every matrix job green on final head.

Any later source or documentation commit invalidates older runs as final evidence. Never combine a green Android run from one SHA with green Apple/Desktop/Web jobs from another SHA.

Historical 2.0.12/v1 evidence remains valid only for its exact historical commits.

## Concurrency and Cancellation

PR workflows cancel superseded in-progress work where configured. A cancelled old run is expected when a new commit replaces its head. Only the replacement exact head matters for merge evidence.

## Repository Security Guard

```bash
python scripts/verify_no_secrets.py
```

The guard rejects committed signing/private-key and obvious credential material. Production signing material remains outside Git.

## Documentation and Repository-Contract Gates

### Documentation links

```bash
python -m unittest scripts.tests.test_verify_documentation_links
python scripts/verify_documentation_links.py
```

### Tracked-file documentation ownership

```bash
python -m unittest scripts.tests.test_verify_documentation_coverage
python scripts/verify_documentation_coverage.py
```

The direct guard uses `git ls-files -z` and fails when a tracked path has no ownership rule, a canonical guide is missing, or a detailed `docs/*.md` file is absent from `docs/README.md`.

### Release identity synchronization

```bash
python -m unittest scripts.tests.test_verify_release_contract
python scripts/verify_release_contract.py
```

The current guard synchronizes:

- Android `app/build.gradle.kts` application/version/SDK identity;
- Desktop `sharedUI/build.gradle.kts` `packageVersion` with Android `versionName`;
- ordinary Android CI expected application/version/SDK values;
- protected production-validation defaults and immutable application/SDK expectations.

It rejects duplicate/missing contract values, unsafe release names, invalid SDK ordering, production IDs ending in `.debug`, and Android/Desktop version drift.

### Release-verifier tests

```bash
python -m unittest scripts.tests.test_verify_release_outputs
python -m unittest scripts.tests.test_verify_release_cli_validation
```

Coverage includes archive structure, release metadata, embedded APK identity, SDK/debuggable checks, deterministic checksum evidence, signature-scheme parsing, certificate fingerprints, and CLI boundaries.

### Translation parity

```bash
python -m unittest scripts.tests.test_verify_translations
python scripts/verify_translations.py
```

The guard covers the established Android localization scope plus full shared English/Hindi key and placeholder parity.

## Shared Engine Gate

```bash
./gradlew :sudoku-engine:desktopTest --stacktrace
```

This is shared correctness evidence for board/solver/generator/difficulty/teaching/hints/practice, `SNP1`, and validated puzzle exchange.

## Shared Gameplay Gate

```bash
./gradlew :sharedUI:desktopTest --stacktrace
```

This protects fixed clues, notes, number entry, undo/reset/hints, typed statuses, deterministic grid movement, snapshots, `SNG1`, store adapters/interfaces, and restore/autosave state wiring.

## Shared Compile Gate

```bash
./gradlew :sharedUI:compileKotlinDesktop :sharedUI:compileKotlinWasmJs --stacktrace
```

The shared keyboard/focus/resource/semantics APIs must compile rather than being disabled for a failing target.

## Android JVM / Instrumentation Compilation / Macrobenchmark Gates

```bash
./gradlew :app:testDebugUnitTest --stacktrace
./gradlew :app:assembleDebugAndroidTest --stacktrace
./gradlew :macrobenchmark:assembleBenchmark --stacktrace
```

Connected instrumentation and representative physical-device Macrobenchmark evidence remain separate from compilation.

## Android Lint and Release Build Gates

```bash
./gradlew :app:lintDebug :app:lintRelease --stacktrace
./gradlew :app:assembleDebug :app:assembleRelease :app:bundleRelease --stacktrace
```

The release path retains R8/resource shrinking so shared work cannot silently weaken the mature Android distribution graph.

## Artifact Evidence

Ordinary Android CI verifies the unsigned release APK/AAB/mapping and writes deterministic hash/identity evidence. Cross-Platform CI uploads short-lived Web, iOS Simulator framework, and Desktop application-image artifacts after successful host builds.

A CI artifact is not automatically a production-distribution artifact.

## Required External Evidence

Source and hosted CI cannot by themselves establish:

- Android production signing/certificate identity unless protected validation succeeds;
- physical-device Android lifecycle/accessibility/performance quality;
- Apple production host/signing/provisioning/device/App Store evidence;
- macOS notarization/Gatekeeper evidence;
- Windows signing/reputation/clean-machine evidence;
- Linux clean install/upgrade/remove/distribution evidence;
- intended Web browser/device/accessibility/persistence compatibility;
- store listing/privacy/rollout acceptance;
- repository ruleset/environment administration unless checked separately.

Keep these states explicit in release docs and `what_changed.md`.

## Failure Triage Order

When a workflow fails:

1. identify the exact failing head SHA;
2. read the first failed job/step;
3. if Gradle/KMP configuration fails, fix the shared build graph before platform jobs;
4. if `sudoku-engine` fails, treat it as shared correctness/import-exchange failure;
5. if `sharedUI` tests/compile fail, isolate common state/resource/input APIs instead of dropping a target;
6. if only one host packaging job fails, investigate that host/toolchain;
7. if Android release guards fail, preserve the 2013/2.0.13 identity/signing/evidence contract and fix drift;
8. if release-contract guard fails, check Android source, Desktop package version, ordinary CI, and protected defaults together;
9. if documentation guards fail, document/index the new area instead of broad bypasses;
10. push the focused fix, discard older-run evidence, and require a new exact-head set.

## Local Pre-Push Verification

```bash
python scripts/verify_no_secrets.py
python -m unittest scripts.tests.test_verify_documentation_links
python -m unittest scripts.tests.test_verify_documentation_coverage
python -m unittest scripts.tests.test_verify_release_contract
python -m unittest scripts.tests.test_verify_release_outputs
python -m unittest scripts.tests.test_verify_release_cli_validation
python -m unittest scripts.tests.test_verify_translations
python scripts/verify_documentation_links.py
python scripts/verify_documentation_coverage.py
python scripts/verify_release_contract.py
python scripts/verify_translations.py
./gradlew \
  :sudoku-engine:desktopTest \
  :sharedUI:desktopTest \
  :sharedUI:compileKotlinDesktop \
  :sharedUI:compileKotlinWasmJs \
  :app:testDebugUnitTest \
  :app:assembleDebugAndroidTest \
  :macrobenchmark:assembleBenchmark \
  :app:lintDebug \
  :app:lintRelease \
  :app:assembleDebug \
  :app:assembleRelease \
  :app:bundleRelease \
  --stacktrace
```

Then run connected/platform-native/runtime checks on the required hosts.

## Evidence Recording Rule

For every meaningful validation record capture the exact commit SHA, workflow/run ID or local command, host/device, result, artifact/hash identity when relevant, and evidence boundary.

Never rewrite “configured” as “verified,” “built” as “published,” or “CI green” as “production-ready on every target.”
