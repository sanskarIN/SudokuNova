# SudokuNova 2.0.13 Release Line

This document is the current release authority for the SudokuNova **2.0.13** source line. It supersedes `V2_0_12_RELEASE.md` for current-version decisions while preserving 2.0.12 and older release documents as historical evidence.

The existence of this document and source metadata does **not** mean 2.0.13 has been published. Production signing, real-device QA, repository administration, store validation, final exact-head verification, tagging, GitHub Release creation, and distribution remain evidence-driven actions.

## Current Source Contract

- Product: SudokuNova
- Android application ID: `in.sanskar.sudokunova`
- Kotlin namespace: `com.sanskar.sudokunova`
- `versionName`: `2.0.13`
- `versionCode`: `2013`
- Desktop package version: `2.0.13`
- `minSdk`: `26`
- `targetSdk`: `37`
- `compileSdk`: `37`
- Java/JVM target: `17`
- License: MIT

`versionCode = 2013` is strictly greater than the 2.0.12 code `2012`.

## 2.0.13 Repository Scope

This patch line builds on the exact-head verified and merged cross-platform foundation and shared active-game persistence work. Repository-verifiable 2.0.13 work includes:

- deterministic shared 9×9 grid movement used by keyboard/focus input;
- shared Compose keyboard navigation for arrow keys plus Backspace/Delete erase behavior;
- explicit notes-mode selection semantics in the portable action surface;
- a common `PuzzleExchangeService` around the existing `SNP1` puzzle-code format;
- import-side unique-solution validation so a syntactically valid but ambiguous puzzle code is rejected;
- preservation of the established `SNP1` compatibility vector;
- Room 2.8.4 maintenance on the verified post-persistence base;
- continued English/Hindi shared-resource parity, active-game persistence, and cross-platform CI.

The mature Android application remains the primary production surface. Shared-platform feature parity continues incrementally and must not be overstated.

## Version Sources of Truth

The release identity is intentionally duplicated only at controlled verification boundaries:

1. `app/build.gradle.kts` is the Android source contract.
2. `sharedUI/build.gradle.kts` carries the Desktop package version.
3. `.github/workflows/ci.yml` contains expected values used to verify ordinary CI release artifacts.
4. `.github/workflows/release-validation.yml` contains protected workflow defaults and immutable application/SDK expectations.
5. `scripts/verify_release_contract.py` fails when the Android source and workflow identity locations disagree.
6. `scripts/verify_release_outputs.py` verifies generated Android artifacts against the expected version/application/SDK identity.

Before merging any version change, run:

```bash
python -m unittest scripts.tests.test_verify_release_contract
python scripts/verify_release_contract.py
```

## Repository-Side Quality Gate

The final 2.0.13 pull-request head must pass the complete standard Android and cross-platform gates, including:

- repository secret/signing-material verification;
- release-verifier unit and CLI-boundary tests;
- documentation-link and documentation-coverage regression/direct guards;
- release source/workflow contract regression/direct guards;
- translation key and placeholder parity;
- partial release-signing fail-closed regression;
- shared engine tests, including puzzle-code exchange uniqueness tests;
- shared gameplay-state tests, including deterministic grid navigation;
- shared Desktop and Web/Wasm compilation;
- Android unit and instrumentation-test compilation;
- Macrobenchmark harness compilation;
- debug and release Android lint;
- debug APK assembly;
- release APK assembly with R8/resource shrinking;
- release AAB assembly;
- exact `versionCode 2013` and `versionName 2.0.13` verification;
- embedded APK application/version/minSdk/targetSdk verification;
- release APK `debuggable=false` verification;
- SHA-256/byte-size and APK-identity evidence generation;
- Web/Wasm production distribution;
- iOS Simulator framework linking;
- Linux, Windows, and macOS Desktop application-image builds;
- Android shared-UI integration build.

The final merge decision requires Android CI, Android Instrumentation, and Cross-Platform CI to be green on one exact final PR head SHA.

## Exact-Head Rule

A successful workflow run proves only the commit SHA it tested. For 2.0.13:

1. stop changing the release-prep branch;
2. record the exact final head SHA;
3. require `Android CI`, `Android Instrumentation`, and `Cross-Platform CI` on that same SHA;
4. do not combine green runs from different SHAs;
5. merge only after all required workflow families are green on the final head;
6. record final verified head and run IDs in `what_changed.md` and the pull request.

See `EXACT_HEAD_VERIFICATION.md`.

## Ordinary CI Artifact Contract

Ordinary CI builds unsigned verification artifacts because production signing material must not be exposed to pull-request jobs.

The expected release-verifier invocation includes:

```text
--expected-version-code 2013
--expected-version-name 2.0.13
--expected-application-id in.sanskar.sudokunova
--require-apk-manifest
--expected-min-sdk 26
--expected-target-sdk 37
```

Unsigned CI artifacts are verification outputs, not production release binaries.

## Protected Production Validation

The protected `Production Release Validation` workflow defaults to:

- expected version code `2013`;
- expected version name `2.0.13`;
- production application ID `in.sanskar.sudokunova`;
- minimum SDK `26`;
- target SDK `37`.

A real protected run additionally requires external release-keystore credentials and trusted APK/AAB certificate SHA-256 fingerprints. Those secrets remain outside Git. A successful protected run is necessary production evidence but still does not replace real-device or distribution validation.

## Cross-Platform Evidence Boundary

Repository compilation and hosted CI can prove source/build support. They do not by themselves prove:

- physical-device lifecycle restoration;
- representative Hindi layout behavior;
- real TalkBack, VoiceOver, desktop accessibility, or browser accessibility behavior;
- complete pointer/touch/keyboard/focus behavior on every target;
- clean-machine installer behavior;
- Windows signing/reputation;
- macOS signing/notarization/Gatekeeper behavior;
- browser compatibility/privacy-mode persistence behavior;
- Apple signing/provisioning/App Store acceptance;
- public Web deployment or store publication.

These remain explicit evidence requirements in `CROSS_PLATFORM.md` and issue #34.

## Manual Release Evidence Still Required

Do not claim 2.0.13 production-ready solely from source review or CI. Record real evidence for applicable installation/lifecycle, accessibility/adaptive UI, performance, signing, repository-administration, and store/distribution checks.

Representative physical-device Android performance evidence remains:

```bash
./gradlew :macrobenchmark:connectedBenchmarkAndroidTest --stacktrace
```

Record the exact commit, device, OS/API level, benchmark output, traces where relevant, startup/frame findings, memory observations, and ANR/crash findings.

## Release Decision

The 2.0.13 source line is not considered published until all mandatory evidence is complete and a deliberate release decision is recorded.

Required final fields include:

- final source commit SHA and pull request;
- exact-head Android CI, Android Instrumentation, and Cross-Platform CI run IDs/results;
- production validation run ID/result when performed;
- final APK/AAB/R8 hashes and APK embedded identity evidence;
- signer-certificate evidence when production signing is performed;
- representative manual/device/accessibility/performance evidence;
- distribution/store validation evidence;
- final decision: `SHIP` or `NO-SHIP`;
- decision owner/date;
- immutable `v2.0.13` tag only after `SHIP`;
- GitHub Release/store/public distribution only after `SHIP`.

## Historical Release Documents

`V2_0_12_RELEASE.md` remains the authority for the historical 2.0.12 source line and must not be rewritten to pretend later parity work was part of that release. v1 release-preparation and evidence documents likewise remain historical.

## Supporting References

Use this document together with:

- `CROSS_PLATFORM.md`;
- `SHARED_PERSISTENCE.md`;
- `BUILDING.md`;
- `TESTING.md`;
- `CI_CD.md`;
- `REPOSITORY_GUARDS.md`;
- `PRODUCTION_SIGNING.md`;
- `PRODUCTION_RELEASE_VALIDATION.md`;
- `PERFORMANCE_BENCHMARKING.md`;
- `ACCESSIBILITY.md`;
- `LOCALIZATION.md`;
- `KEYBOARD_SHORTCUTS.md`;
- `RELEASE_CHECKLIST.md`;
- `RELEASE_QA.md`;
- `PLAY_STORE_RELEASE.md`;
- `GITHUB_REPOSITORY_SETTINGS.md`;
- `RELEASING.md`.

## Evidence Boundary

Repository-side completion means the source, tests, workflows, release contracts, documentation ownership, and automated gates are prepared for 2.0.13. It must never be described as proof that production signing, physical-device QA, platform administration, notarization, store acceptance, tagging, GitHub Release creation, or publication has already occurred.