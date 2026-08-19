# SudokuNova Post-RC Validation Evidence

This document records repository-side evidence for the post-RC v1.0 validation-hardening merge completed on 2026-08-19. It does not replace the stable-release evidence ledger and does not claim manual-device, production-signing, repository-admin, or store/publication completion.

## Pull Request

- Pull request: #28 — `release: harden SudokuNova v1.0 validation`
- Branch: `release/v1.0-validation-hardening`
- Exact verified head: `c3e0e3fc217062e374a434cfea46235fd6595f83`
- Merge method: merge commit
- Merge commit: `27640cb9089ddae4a9242bb84a8927c3761201f4`

The merge operation used the exact verified head SHA so a branch move could not silently substitute an unverified commit.

## Android CI

- Workflow: Android CI
- Run number: 706
- Run ID: `32211246803`
- Result: GREEN

The exact final head passed:

1. checkout;
2. Java 17 setup;
3. Gradle wrapper/cache validation;
4. repository signing/private-key/credential guard;
5. release-output verifier regression suite;
6. documentation-link and release-contract guard regression suites;
7. partial production-signing fail-closed regression;
8. direct documentation-link and source/workflow release-contract guards;
9. English/Hindi translation parity;
10. `:sudoku-engine:test`;
11. `:app:testDebugUnitTest`;
12. `:app:assembleDebugAndroidTest`;
13. `:macrobenchmark:assembleBenchmark`;
14. `:app:lintDebug` and `:app:lintRelease`;
15. debug APK assembly;
16. R8/resource-shrunk release APK assembly;
17. release Android App Bundle assembly;
18. release APK/AAB/R8 structure and RC metadata verification;
19. SHA-256 evidence generation;
20. verification-report upload;
21. release-output/evidence upload;
22. cleanup.

### Android CI artifacts

`unsigned-release-builds`:

- artifact ID: `9351009095`;
- size: `12,794,807` bytes;
- GitHub artifact digest: `sha256:432c0741cf94ee459fcb58c07eaa5316776f38abd15f91827fd04a2e4fb2225c`;
- recorded expiry: 2026-09-02.

`verification-reports`:

- artifact ID: `9351008412`;
- size: `578,445` bytes;
- GitHub artifact digest: `sha256:8374a7a82fe604e0b516d7768a8c563d16030bdbe4862cc26509ce5ce83cf651`;
- recorded expiry: 2026-09-02.

These are repository-CI artifacts. They are not production-signed release packages.

## API-35 Connected Instrumentation

- Workflow: Android Instrumentation
- Run number: 229
- Run ID: `32211246802`
- Result: GREEN

The exact final head passed the API-35 x86_64 connected Compose/Room test job, including KVM setup, connected tests, report upload, and cleanup.

## Defect Found During Exact-Head Validation

Historical Android CI run #697 / ID `32208530447` on head `d5016fbdc530b41413a26d8da3476895e03a463b` failed while compiling the Macrobenchmark source because `StartupBenchmark.kt` used `androidx.test.filters.LargeTest` without declaring the AndroidX Test Runner dependency that supplies the annotation contract.

The repair was intentionally split into focused commits:

- `fd95be04b251f6a1189c32a21ca3960a4c9e276d` — `build(deps): expose AndroidX test runner for macrobenchmarks`;
- `c4afa584f80bb53de58472da13b75580750994d8` — `fix(benchmark): add test runner annotation dependency`.

The exact final Android CI run #706 subsequently passed `:macrobenchmark:assembleBenchmark`, proving the compile failure was repaired on the merged head.

## Repository Guard Enforcement Gap Found During Audit

The branch contained documentation-link and release-contract verifier scripts/tests, but ordinary/protected workflow enforcement initially did not match the documented PR contract. The audit closed that gap through focused commits:

- `b2c5f8ef187a0aa5fed627d79ac138d055473b54` — `ci(guards): execute repository consistency regression suites`;
- `5b971059c59ac8a7d4600938c4087a647b4a1416` — `ci(release): enforce source workflow contract before signing`;
- `c3e0e3fc217062e374a434cfea46235fd6595f83` — `docs(licenses): record direct AndroidX test runner usage`.

Android CI #706 passed the newly added guard tests and direct guard commands on the exact final head.

## Current Stable-Release Boundary

The following remain deliberately unclaimed and must stay open in issue #5 until real evidence exists:

- `main` branch/ruleset protection and required-check administration;
- production-release GitHub Environment access/ref/reviewer configuration and protected signing secrets;
- production/upload key material and recovery process;
- a real protected Production Release Validation workflow execution on the intended release ref;
- production signer/upload certificate evidence;
- signed APK installation smoke and distribution-platform AAB validation;
- physical-device Macrobenchmark startup/frame measurements and retained traces;
- memory/ANR measurements;
- real TalkBack traversal/focus-order QA;
- representative 200% font, phone/tablet/window/orientation QA;
- high-contrast and reduced-motion device QA;
- process-death/lifecycle real-target QA;
- final signed-artifact R8 smoke QA;
- current store screenshots/listing/privacy/data/content/target-API validation;
- final stable Android versionCode decision;
- stable `versionName = 1.0.0` promotion;
- fresh exact-head stable CI/API-35 verification after stable metadata/source changes;
- final signed artifact hashes/signature evidence;
- final `SHIP` decision;
- immutable `v1.0.0` tag, GitHub Release, and Android/store publication.

PR #28 materially improves the repository's ability to produce trustworthy release and performance evidence, but it does not convert source-controlled tooling into production evidence.

**Made by the Sanskar**
