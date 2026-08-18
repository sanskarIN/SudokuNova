# SudokuNova CI/CD and Automated Quality Gates

SudokuNova uses GitHub Actions as a verification system. The current repository automates build/test/release-artifact evidence, but it does **not** automatically publish a production release or store submission.

## Workflow Overview

Current workflows live under `.github/workflows/`.

### Android CI — `ci.yml`

The standard pull-request gate verifies source integrity, tests, lint, release compilation and RC artifact evidence.

Current v1.0 RC stages include:

1. checkout;
2. Java 17 setup;
3. Gradle wrapper/cache validation;
4. repository secret/signing-material guard;
5. release-output verifier Python unit tests;
6. partial release-signing fail-closed regression;
7. English/Hindi translation parity;
8. `:sudoku-engine:test`;
9. `:app:testDebugUnitTest`;
10. `:app:assembleDebugAndroidTest`;
11. debug and release Android lint;
12. debug APK assembly;
13. release APK assembly with R8/resource shrinking;
14. release Android App Bundle assembly;
15. release APK/AAB/R8 mapping structural/version verification;
16. SHA-256/byte-size release evidence generation;
17. report/test-result artifact upload;
18. successful release APK/AAB/R8 mapping/checksum artifact upload for short-lived verification evidence.

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

If a code/documentation commit that triggers the pull-request workflows is added after a successful run, the new head must be verified again.

Do not cite a workflow run from an older head as evidence for a newer head.

PR #27 is the active v1.0 RC preparation PR. Its repository-side work must remain draft until its final intended head passes both required workflows and no repository-blocking defect remains.

## Repository Security Guard

`scripts/verify_no_secrets.py` is executed by standard CI.

It is designed to reject committed material such as:

- Android keystores;
- private-key files;
- obvious credential assignments/patterns.

This is a defense-in-depth repository guard, not a substitute for GitHub secret scanning or careful review.

Production signing material must remain outside version control.

## Release-Verifier Unit Gate

The RC line runs:

```bash
python -m unittest scripts.tests.test_verify_release_outputs
```

This verifies the pure-Python release-output checker before it is trusted to validate built artifacts.

Coverage includes:

- minimum valid APK/AAB archive structures;
- missing required archive entry rejection;
- single release metadata parsing;
- multiple release metadata element rejection;
- deterministic checksum-manifest content.

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

After release APK/AAB/R8 mapping generation, CI runs `scripts/verify_release_outputs.py` with the exact expected RC metadata:

- `versionCode 1000`;
- `versionName 1.0.0-rc.1`.

The verifier requires:

- non-empty release APK;
- valid ZIP-based APK structure;
- APK manifest and primary DEX entries;
- non-empty release AAB;
- AAB bundle config, base manifest and base DEX entries;
- non-empty R8 `mapping.txt`;
- exactly one APK release metadata element;
- exact expected version code/name.

It then writes SHA-256 and byte-size evidence for APK, AAB and mapping to:

```text
app/build/outputs/release-evidence/sha256.txt
```

A successful verifier result does **not** prove production certificate identity or device installability.

## Connected API-35 Gate

The repository workflow runs connected tests on API 35. To reproduce locally, use a compatible emulator/device and:

```bash
./gradlew :app:connectedDebugAndroidTest --stacktrace
```

Local device/emulator names and hardware acceleration vary by environment.

## Artifact Policy

CI may upload:

- test reports;
- lint/build reports;
- unsigned release APK output;
- release AAB output;
- R8 mapping output;
- SHA-256 release evidence.

These artifacts are verification evidence and have limited retention. They should not automatically be described as production releases.

Do not publish a CI-built artifact as production unless signing, certificate identity, provenance, manual QA, legal/store metadata and the exact final artifact have been validated.

## Production Signing Boundary

Normal pull-request CI intentionally receives no production signing secrets.

The build supports secret-backed signing only when all four required `SUDOKUNOVA_*` environment values are available in a controlled release environment. Ordinary PRs, forks and untrusted code must not receive those secrets.

See:

- `PRODUCTION_SIGNING.md`;
- `V1_RELEASE_CANDIDATE.md`;
- `RELEASING.md`.

## No Automatic Production Deployment

A green CI run does not authorize the system to:

- create a Play Store release;
- publish an APK/AAB publicly;
- create a stable production tag automatically;
- expose signing secrets;
- claim physical-device QA;
- claim TalkBack/200% font/performance/process-death validation;
- claim signed artifact identity.

Release/publishing remains a controlled maintainer action documented in the release guides.

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

- repository security failure → committed secret/signing-material issue;
- release-verifier unit failure → verifier/test contract issue;
- partial-signing guard failure → signing configuration regression;
- translation script failure → localization resource issue;
- engine test failure → domain logic/test issue;
- app unit-test failure → Android-module pure logic issue;
- instrumentation compilation failure → Android test/API/Compose compile issue;
- lint failure → API/resource/static-analysis issue;
- release build failure → R8/resource/shrinking/build configuration issue;
- release-output verifier failure → artifact path/structure/version/mapping issue;
- connected failure → emulator/runtime/Room/Compose/state issue.

## Evidence Recording

`what_changed.md` may record exact workflow run IDs only after results exist. Do not write `GREEN`, `verified`, `device-tested`, `signed`, `store-ready`, or `release-ready` in advance.

The repository roadmap/checklists should distinguish:

- implemented;
- automatically verified;
- manually verified;
- production-signed/validated;
- still pending.

## Recommended Local Pre-Push Check

For a broad RC change:

```bash
python scripts/verify_no_secrets.py
python -m unittest scripts.tests.test_verify_release_outputs
python scripts/verify_translations.py
./gradlew :sudoku-engine:test :app:testDebugUnitTest :app:assembleDebugAndroidTest :app:lintDebug :app:lintRelease :app:assembleDebug :app:assembleRelease :app:bundleRelease --stacktrace
```

Then run `scripts/verify_release_outputs.py` against the generated release outputs using the expected candidate metadata.

Run connected instrumentation separately on a suitable emulator/device.

## Stable v1.0 Evidence Boundary

PR #27 can prove repository-side RC preparation. Stable `v1.0.0` additionally requires the real-target evidence in `V1_RELEASE_CANDIDATE.md`, including TalkBack, large-font/adaptive layout, contrast/motion, lifecycle/process death, measured performance/ANR/memory, production signing, signed artifact verification, and store/repository release assets.
