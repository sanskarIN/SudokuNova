# SudokuNova CI/CD and Automated Quality Gates

SudokuNova uses GitHub Actions as a verification system. The current repository automates build/test/release-artifact evidence, but it does **not** automatically publish a production release or store submission.

## Workflow Overview

Current workflows live under `.github/workflows/`.

### Android CI — `ci.yml`

The standard pull-request gate verifies the code and release build pipeline.

Current stages include:

1. checkout;
2. Java 17 setup;
3. Gradle wrapper/cache validation;
4. repository secret/signing-material guard;
5. English/Hindi translation parity;
6. `:sudoku-engine:test`;
7. `:app:testDebugUnitTest`;
8. `:app:assembleDebugAndroidTest`;
9. debug and release Android lint;
10. debug APK assembly;
11. release APK assembly with minification/resource shrinking;
12. release Android App Bundle assembly;
13. report/test-result artifact upload;
14. successful release APK/AAB/R8 mapping artifact upload for short-lived verification evidence.

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

## Pull-Request Gate Policy

A pull request intended for merge should not be treated as verified until the required workflows are green on the **exact final head commit**.

If a code/documentation commit that triggers CI is added after a successful run, the new head must be verified again.

Do not cite a workflow run from an older head as evidence for a newer head.

## Repository Security Guard

`scripts/verify_no_secrets.py` is executed by standard CI.

It is designed to reject committed material such as:

- Android keystores;
- private-key files;
- obvious credential assignments/patterns.

This is a defense-in-depth repository guard, not a substitute for GitHub secret scanning or careful review.

Production signing material must remain outside version control.

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

## Lint Gates

Current release hardening runs both:

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

The release build has minification and resource shrinking enabled. A successful unsigned/unconfigured release assembly proves release compilation/R8 processing but is not by itself a distributable production-signing claim.

### Release AAB

```bash
./gradlew :app:bundleRelease --stacktrace
```

The AAB is the expected store-oriented Android bundle format. Final production distribution still requires proper secure signing and store-side validation.

## Connected API-35 Gate

The repository workflow runs connected tests on API 35. To reproduce locally, use an API-compatible emulator/device and the relevant connected task, for example:

```bash
./gradlew :app:connectedDebugAndroidTest --stacktrace
```

Local device/emulator names and hardware acceleration vary by environment.

## Artifact Policy

CI may upload:

- test reports;
- lint/build reports;
- release APK output;
- release AAB output;
- R8 mapping output.

These artifacts are verification evidence and have limited retention. They should not automatically be described as production releases.

Do not publish a CI-built artifact as production unless signing, provenance, QA, legal/store metadata and the final exact artifact have been validated.

## No Automatic Production Deployment

The current repository does not treat a green CI run as permission to:

- create a Play Store release;
- publish an APK/AAB publicly;
- create a signed production tag automatically;
- expose signing secrets;
- claim physical-device QA.

Release/publishing remains a controlled maintainer action documented in `RELEASING.md`, `RELEASE_CHECKLIST.md`, and `RELEASE_QA.md`.

## Failure Triage

When CI fails:

1. identify the first failing stage;
2. inspect its report/log rather than only the final Gradle error;
3. reproduce the narrow task locally when possible;
4. add/fix regression coverage for code defects;
5. avoid broad cache deletion or suppression as a first response;
6. push a focused fix;
7. verify the new exact head.

Typical ordering is:

- security/translation script failure → repository/resource issue;
- engine test failure → domain logic/test issue;
- app unit-test failure → Android-module pure logic issue;
- instrumentation compilation failure → Android test/API/Compose compile issue;
- lint failure → API/resource/static-analysis issue;
- release build failure → R8/resource/shrinking/build config issue;
- connected failure → emulator/runtime/Room/Compose/state issue.

## Evidence Recording

`what_changed.md` may record exact workflow run IDs only after results exist. Do not write `GREEN`, `verified`, `device-tested`, or `release-ready` in advance.

The repository roadmap/checklists should distinguish:

- implemented;
- automatically verified;
- manually verified;
- still pending.

## Recommended Local Pre-Push Check

For a broad change:

```bash
python scripts/verify_no_secrets.py
python scripts/verify_translations.py
./gradlew :sudoku-engine:test :app:testDebugUnitTest :app:assembleDebugAndroidTest :app:lintDebug :app:lintRelease :app:assembleDebug :app:assembleRelease :app:bundleRelease --stacktrace
```

Run connected instrumentation separately on a suitable emulator/device.
