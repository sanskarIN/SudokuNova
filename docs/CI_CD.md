# SudokuNova CI/CD and Automated Quality Gates

SudokuNova uses GitHub Actions as a verification system. The repository automates build/test/release-artifact evidence, but it does **not** automatically publish a production release or store submission.

## Workflow Overview

Current workflows live under `.github/workflows/`.

### Android CI — `ci.yml`

The standard pull-request gate verifies the code and release build pipeline.

Current stages include:

1. checkout;
2. Java 17 setup;
3. Gradle wrapper/cache validation;
4. repository secret/signing-material guard;
5. Python release-helper unit tests;
6. English/Hindi translation parity;
7. `:sudoku-engine:test`;
8. `:app:testDebugUnitTest`;
9. `:app:assembleDebugAndroidTest`;
10. debug and release Android lint;
11. debug APK assembly;
12. release APK assembly with minification/resource shrinking;
13. release Android App Bundle assembly;
14. release APK/AAB ZIP-integrity verification and SHA-256 checksum generation;
15. report/test-result artifact upload;
16. successful release APK/AAB/R8 mapping/`SHA256SUMS` upload for short-lived verification evidence.

### Android Instrumentation — `instrumentation.yml`

The connected gate runs Android Compose/Room tests on an API-35 emulator target. The workflow configures Java/Gradle, KVM access where supported, disables animations, runs connected tests, and uploads instrumentation reports.

This gate is particularly important for Compose navigation/semantics, Room migrations, History/Saved Puzzles, challenges, transfer/backup, Learn/practice, and accessibility semantic regressions that can be asserted reliably.

## Pull-Request Gate Policy

A pull request intended for merge is not verified until the required workflows are green on the **exact final head commit**. If a later commit changes that head, prior runs become historical evidence only.

## Repository Security Guard

`scripts/verify_no_secrets.py` runs in standard CI. It rejects common committed signing/private-key/credential material. This is defense-in-depth, not a substitute for provider-side secret scanning or careful review.

Production signing material must remain outside version control.

## Release Helper Tests

The release-candidate line adds pure-Python tests for repository release helpers:

```bash
python -m unittest discover -s scripts/tests -p 'test_*.py' -v
```

These tests currently cover release-artifact verification behavior, including valid ZIP-based APK/AAB containers, checksums, missing files, unsupported extensions, corrupted artifacts, and mandatory-signature-tool failure behavior.

## Translation Parity Gate

```bash
python scripts/verify_translations.py
```

Default/English and Hindi maintained resources must stay in parity, including compatible placeholders and accessibility strings.

## Engine Gate

```bash
./gradlew :sudoku-engine:test --stacktrace
```

This covers board, solver, generator, difficulty, teaching evidence, hint logic, practice, determinism, and correctness-sensitive domain behavior.

## Android JVM Gate

```bash
./gradlew :app:testDebugUnitTest --stacktrace
```

This covers Android-module behavior that does not require an emulator, including state codecs, persistence models, transfer/backup logic, and learning/statistics behavior.

## Instrumentation Compilation Gate

```bash
./gradlew :app:assembleDebugAndroidTest --stacktrace
```

Compilation success is required but is not equivalent to connected-test success.

## Lint Gates

```bash
./gradlew :app:lintDebug :app:lintRelease --stacktrace
```

Release lint is retained because release resource/minification/configuration paths differ from debug.

## Build Gates

```bash
./gradlew :app:assembleDebug --stacktrace
./gradlew :app:assembleRelease --stacktrace
./gradlew :app:bundleRelease --stacktrace
```

The release build has minification/resource shrinking enabled. CI release outputs are verification artifacts unless production signing and manual release evidence are explicitly completed.

## Artifact Integrity / Checksum Gate

After release APK/AAB assembly, CI runs:

```bash
python scripts/verify_release_artifacts.py \
  app/build/outputs/apk/release/*.apk \
  app/build/outputs/bundle/release/*.aab \
  --checksums-out app/build/outputs/SHA256SUMS
```

This gate validates that each artifact:

- exists and is non-empty;
- has a supported `.apk`/`.aab` extension;
- is a valid ZIP-based Android artifact without a corrupt entry;
- receives a SHA-256 digest.

The generated checksum manifest is uploaded with the release verification artifacts.

This **does not** claim a CI artifact is production-signed. For final signed artifacts use `--require-signature` in the controlled signing environment. APK verification uses `apksigner`; AAB verification uses `jarsigner`.

## Connected API-35 Gate

To reproduce locally on a suitable device/emulator:

```bash
./gradlew :app:connectedDebugAndroidTest --stacktrace
```

The repository workflow uses API 35 as its connected regression target. Manual minimum/latest/device-class QA remains separate release evidence.

## Artifact Policy

CI may upload test/lint reports, release APK, release AAB, R8 mapping, and `SHA256SUMS`. These are short-lived verification evidence and must not automatically be described as a stable production release.

## No Automatic Production Deployment

A green CI run does not authorize or prove Play Store publication, signed production distribution, manual accessibility/device QA, or production signing. Those remain controlled maintainer actions documented in `RELEASING.md`, `RELEASE_CHECKLIST.md`, `RELEASE_QA.md`, and `V1_RELEASE_EVIDENCE.md`.

## Failure Triage

When CI fails:

1. identify the first failing stage;
2. inspect its report/log;
3. reproduce the narrow task locally when practical;
4. add or repair regression coverage for code defects;
5. avoid broad suppression/cache deletion as a first response;
6. push a focused fix;
7. verify the new exact head.

Typical ordering:

- security/helper/translation failure → repository/tool/resource issue;
- engine failure → domain correctness issue;
- app JVM failure → Android-module pure logic issue;
- AndroidTest compilation failure → test/API/Compose issue;
- lint failure → static-analysis/resource/API issue;
- release build/artifact verification failure → R8/build/artifact issue;
- connected failure → emulator/runtime/Room/Compose/state issue.

## Evidence Recording

For v1.0, record exact automated results in `V1_RELEASE_EVIDENCE.md`. `what_changed.md` carries the cumulative project history.

Do not write `GREEN`, `verified`, `device-tested`, `signed`, `published`, or `release-ready` before the corresponding evidence exists.

## Recommended Local Pre-Push Check

```bash
python scripts/verify_no_secrets.py
python -m unittest discover -s scripts/tests -p 'test_*.py' -v
python scripts/verify_translations.py
./gradlew :sudoku-engine:test :app:testDebugUnitTest :app:assembleDebugAndroidTest :app:lintDebug :app:lintRelease :app:assembleDebug :app:assembleRelease :app:bundleRelease --stacktrace
python scripts/verify_release_artifacts.py app/build/outputs/apk/release/*.apk app/build/outputs/bundle/release/*.aab --checksums-out app/build/outputs/SHA256SUMS
```

Run connected instrumentation and required manual release QA separately on suitable targets.
