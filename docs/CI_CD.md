# SudokuNova CI/CD and Automated Quality Gates

SudokuNova uses GitHub Actions as a verification system. The repository automates source, shared-platform, Android, packaging, and release-artifact evidence, but it does **not** automatically prove production signing, physical-device quality, store acceptance, notarization, or public distribution.

For cross-platform build commands and platform evidence boundaries, see [`CROSS_PLATFORM.md`](CROSS_PLATFORM.md). For the Android 2.0.12 release contract, see [`V2_0_12_RELEASE.md`](V2_0_12_RELEASE.md).

## Workflow Overview

Current workflows live under `.github/workflows/`.

### Android CI — `ci.yml`

The standard pull-request gate protects both the shared KMP layers and the mature Android release line.

Current 2.0.12 stages are:

1. checkout;
2. Java 17 setup;
3. Gradle wrapper/cache setup and wrapper validation;
4. Android SDK `apkanalyzer` discovery;
5. repository secret/signing-material guard;
6. release-output verifier unit and CLI-boundary tests;
7. documentation-link, documentation-coverage, and release-contract regression tests;
8. partial release-signing fail-closed regression;
9. direct documentation-link, documentation-coverage, and release-contract guards;
10. English/Hindi translation parity;
11. `:sudoku-engine:desktopTest`;
12. `:sharedUI:desktopTest`;
13. `:sharedUI:compileKotlinDesktop` and `:sharedUI:compileKotlinWasmJs`;
14. `:app:testDebugUnitTest`;
15. `:app:assembleDebugAndroidTest`;
16. `:macrobenchmark:assembleBenchmark`;
17. debug and release Android lint;
18. debug APK assembly;
19. R8/resource-shrunk release APK assembly;
20. release AAB assembly;
21. release APK/AAB/mapping structural verification;
22. exact `in.sanskar.sudokunova` / `2012` / `2.0.12` identity verification;
23. embedded APK minSdk 26 / targetSdk 37 / `debuggable=false` verification;
24. SHA-256, byte-size, and APK-identity evidence generation;
25. short-lived report/test-result artifact upload;
26. successful unsigned release-output evidence upload.

The shared tests/compilation are intentionally ahead of the expensive Android release work. A portable source failure should be fixed at its source instead of surfacing separately in every platform job.

### Cross-Platform CI — `cross-platform.yml`

This workflow validates repository-supported shared/platform build targets on appropriate hosted operating systems.

#### Shared code — Ubuntu

Runs:

```bash
./gradlew :sudoku-engine:desktopTest
./gradlew :sharedUI:desktopTest
./gradlew :sharedUI:compileKotlinDesktop
./gradlew :sharedUI:compileKotlinWasmJs
```

This protects portable engine correctness, portable gameplay-state behavior, Desktop compilation, and Web/Wasm compilation.

#### Android shared integration — Ubuntu

Runs:

```bash
./gradlew :app:assembleDebug
```

This proves that the mature Android application can consume `:sudoku-engine` and `:sharedUI`. It does not replace Android CI's stricter lint/release/artifact gates.

#### Web/Wasm distribution — Ubuntu

Runs:

```bash
./gradlew :sharedUI:wasmJsBrowserDistribution
```

and uploads the generated production distribution from:

```text
sharedUI/build/dist/wasmJs/productionExecutable/
```

A successful build proves repository packaging for the exact head. Browser/device compatibility remains a separate runtime QA responsibility.

#### iOS Simulator framework — macOS

Runs:

```bash
./gradlew :sharedUI:linkDebugFrameworkIosSimulatorArm64
```

and uploads the generated `SudokuNovaSharedUI.framework`.

This validates Kotlin/Native framework linking on macOS. It does not prove Xcode host completeness, Apple signing/provisioning, physical-device behavior, TestFlight/App Store validation, or publication.

#### Desktop application images — Linux, Windows, macOS

Each host runs:

```bash
./gradlew :sharedUI:createDistributable
```

and uploads the host application image from `sharedUI/build/compose/binaries/main/app/`.

The shared Desktop configuration also declares MSI, DMG, and DEB native package formats. Signing, notarization, installer reputation, and distribution-channel acceptance are external evidence.

### Android Instrumentation — `instrumentation.yml`

The connected Android gate runs Compose/Room tests on an API-35 emulator. It configures Java/Gradle, KVM where supported, disables animations, executes connected tests, and uploads reports.

This protects behavior such as:

- Compose navigation and semantics;
- Room behavior and migrations;
- history/saved-puzzle flows;
- challenge flows;
- backup/transfer flows;
- Learn/practice UI behavior;
- accessibility semantics that can be asserted automatically.

Compilation success in Android CI is not equivalent to this connected-test evidence.

### Production Release Validation — `release-validation.yml`

This manually dispatched workflow is reserved for trusted Android production-signing validation through the `production-release` GitHub Environment.

It can, when the external environment is actually configured:

- validate operator-supplied version values;
- reconstruct the keystore temporarily outside the repository;
- build signed R8 APK/AAB outputs;
- require application ID `in.sanskar.sudokunova`;
- bind versionCode `2012`, versionName `2.0.12`, minSdk 26, and targetSdk 37 to source/workflow expectations;
- inspect embedded APK identity and require `debuggable=false`;
- verify APK/AAB signatures;
- require an APK v2-or-newer verified signature scheme;
- bind APK/AAB signer certificates to protected expected SHA-256 fingerprints;
- record non-secret hashes, sizes, identity, signature fingerprints, and workflow context;
- remove the temporary keystore during cleanup.

Committed workflow source is not proof that the protected environment, reviewers, allowed refs, secrets, or production key are configured. A successful protected workflow is still not Play Console, physical-device, accessibility, or publication evidence.

## Exact-Head Pull-Request Policy

A pull request is not verified until every required workflow is green on the **same exact final head SHA**.

For cross-platform changes, the expected PR evidence set is:

- Android CI — green on final head;
- Android Instrumentation — green on final head;
- Cross-Platform CI — every matrix job green on final head.

If any commit is added after those runs, the older runs become historical evidence and the new head must run again. This includes documentation commits when the pull-request workflow path rules cause a new run.

Never combine a green Android run from one SHA with a green iOS/Desktop/Web run from another SHA and call the later branch verified.

Historical green v1 and 2.0.12-pre-cross-platform runs remain valid only for the exact commits they tested.

## Concurrency and Cancellation

PR workflows use concurrency groups and cancel superseded in-progress work where configured. Cancellation caused by a newer commit is expected; the replacement head is the only one that matters for merge evidence.

A cancelled old run must not be interpreted as a project failure when a newer head intentionally superseded it.

## Repository Security Guard

Standard CI executes:

```bash
python scripts/verify_no_secrets.py
```

The guard rejects committed signing/private-key and obvious credential material. It is defense in depth, not a substitute for GitHub secret scanning or careful review.

Production signing material must remain outside version control.

## Documentation and Repository-Contract Gates

### Documentation link integrity

```bash
python -m unittest scripts.tests.test_verify_documentation_links
python scripts/verify_documentation_links.py
```

This rejects missing repository-local Markdown targets and repository-escaping links.

### Complete tracked-file documentation coverage

```bash
python -m unittest scripts.tests.test_verify_documentation_coverage
python scripts/verify_documentation_coverage.py
```

The direct guard obtains the authoritative tracked-file set with `git ls-files -z` and fails when:

- a tracked path has no ownership rule;
- a rule points to missing canonical documentation;
- a detailed `docs/*.md` guide is not discoverable from `docs/README.md`.

The taxonomy includes Android, `sudoku-engine/`, `sharedUI/`, `iosApp/`, Macrobenchmark, workflows, scripts, Gradle configuration, root policy files, and the detailed docs library.

Audit every tracked path with:

```bash
python scripts/verify_documentation_coverage.py --verbose
```

### Release source/workflow identity

```bash
python -m unittest scripts.tests.test_verify_release_contract
python scripts/verify_release_contract.py
```

The guard treats `app/build.gradle.kts` as the Android source release contract and requires ordinary CI plus protected production validation to agree on:

- application ID;
- version code;
- version name;
- minimum SDK;
- target SDK.

It also rejects invalid numeric values, invalid SDK ordering, unsafe version names, duplicate/missing values, and a production application ID ending in `.debug`.

Cross-platform source extraction must not silently alter this Android release identity.

## Release-Verifier Unit Gate

CI runs:

```bash
python -m unittest scripts.tests.test_verify_release_outputs
python -m unittest scripts.tests.test_verify_release_cli_validation
```

Coverage includes archive structure, release metadata parsing, embedded APK identity, SDK/debuggable checks, deterministic checksum evidence, certificate fingerprint normalization/parsing, verified signature-scheme parsing, v1-only signature rejection when mandatory signed validation is requested, and CLI argument boundaries.

## Partial-Signing Fail-Closed Gate

`app/build.gradle.kts` accepts either zero or all four release-signing environment values. CI intentionally sets only one fake alias and requires Gradle configuration to fail with the expected partial-signing message.

This proves a half-configured environment cannot silently produce a supposedly production-ready unsigned artifact.

## Translation Parity Gate

```bash
python scripts/verify_translations.py
```

Android player-facing English/Hindi resources must preserve key and formatting-placeholder parity. Shared UI currently uses a limited portable surface; localization parity must be extended deliberately as more mature Android UI is moved into common code.

## Shared Engine Gate

```bash
./gradlew :sudoku-engine:desktopTest --stacktrace
```

The engine tests use `kotlin.test` and protect board, solver, generator, difficulty, logical analysis, teaching evidence, hints, practice, and puzzle-code behavior in a platform-neutral test source set.

Because the engine is shared by Android, Desktop, iOS, and Web, a failure here is a shared correctness failure rather than an Android-only defect.

## Shared Gameplay-State Gate

```bash
./gradlew :sharedUI:desktopTest --stacktrace
```

This protects portable gameplay-state behavior including fixed clues, notes, number entry, undo/reset consistency, and hint progression.

## Shared Compile Gates

```bash
./gradlew :sharedUI:compileKotlinDesktop :sharedUI:compileKotlinWasmJs --stacktrace
```

These checks catch common Compose/source-set/API issues before host packaging.

## Android JVM Gate

```bash
./gradlew :app:testDebugUnitTest --stacktrace
```

This covers Android-module behavior that does not require an emulator, including state codecs, persistence/transfer models, learning/statistics behavior, and pure presentation helpers.

## Android Instrumentation Compilation Gate

```bash
./gradlew :app:assembleDebugAndroidTest --stacktrace
```

This proves connected-test sources compile; it does not prove they pass on an emulator/device.

## Macrobenchmark Compilation Gate

```bash
./gradlew :macrobenchmark:assembleBenchmark --stacktrace
```

This proves the release-like benchmark variant and test APK remain buildable together. Hosted compilation is not representative physical-device performance evidence.

Physical measurement uses:

```bash
./gradlew :macrobenchmark:connectedBenchmarkAndroidTest --stacktrace
```

Record the exact source SHA, device/OS, command, raw output, and traces when using results as release evidence.

## Android Lint and Release Build Gates

Android CI runs both:

```bash
./gradlew :app:lintDebug :app:lintRelease
```

and the full debug/release build path. The release path includes R8/resource shrinking and AAB generation so cross-platform work cannot regress the mature Android distribution graph unnoticed.

## Artifact Evidence

Ordinary Android CI verifies the unsigned release APK/AAB/mapping and writes deterministic hash/identity evidence. Cross-Platform CI uploads short-lived Web, iOS Simulator framework, and Desktop application-image artifacts when the corresponding hosted builds succeed.

Artifact retention is for build/review evidence. A CI artifact is not automatically a production-distribution artifact.

## Required External Evidence

Source and hosted CI cannot by themselves establish:

- Android production signing/certificate identity unless the protected signing workflow actually succeeds;
- physical-device Android installation/lifecycle/accessibility/performance quality;
- Apple bundle signing/provisioning, physical-device execution, TestFlight/App Store acceptance;
- macOS signing/notarization;
- Windows code signing/reputation;
- Linux distribution-channel compatibility;
- broad Web browser/device compatibility;
- Play Store/App Store listing/privacy/rollout approval;
- repository ruleset/environment administration unless verified separately.

Keep these states explicit in release documentation and `what_changed.md`.

## Failure Triage Order

When a workflow fails:

1. identify the exact failing head SHA;
2. read the first failed job/step, not only the final Gradle stack trace;
3. if Gradle configuration fails, fix the shared build graph before platform-specific jobs;
4. if `sudoku-engine` fails, treat it as a domain/shared correctness issue;
5. if `sharedUI` compile/tests fail, isolate common vs Desktop/Wasm/iOS source-set behavior;
6. if only one host packaging job fails, investigate that host/toolchain rather than weakening other jobs;
7. if Android release guards fail, preserve the 2.0.12 identity/signing/evidence contract and fix source drift;
8. if documentation guards fail, document/index the new area rather than adding a broad bypass;
9. push the focused fix and discard older-run evidence because the head changed;
10. require a new green exact-head set before merge.

## Local Pre-Push Verification

A strong non-connected baseline is:

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

Then run platform-specific connected/package checks on the required host OSes.

## Evidence Recording Rule

For every meaningful validation record:

- exact commit SHA;
- workflow name and run ID, or exact local command;
- host OS/runner or physical device/OS;
- result;
- artifact/hash/identity details when relevant;
- known evidence boundary.

Never rewrite “configured” as “verified,” “built” as “published,” or “CI green” as “production-ready on every target.”
