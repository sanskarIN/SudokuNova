# SudokuNova Testing Guide

SudokuNova treats deterministic correctness and regression coverage as merge requirements. The testing strategy spans the platform-independent Sudoku engine, Android JVM logic, repository/documentation/release tooling, Compose/Room connected tests, static analysis, release builds, artifact verification, Macrobenchmark harness compilation, physical-device performance measurement, and real manual/production release QA.

## Testing Layers

The project uses complementary layers:

1. `sudoku-engine` JVM tests for Sudoku truth and deterministic domain behavior;
2. Android app JVM tests for codecs/models/presentation-independent application logic;
3. Python regression tests for release tooling and repository consistency guards;
4. Android instrumentation tests for Compose, Room, lifecycle-adjacent and integrated flows;
5. direct documentation-link, tracked-file documentation-coverage, release-contract, translation and security verification scripts;
6. release-signing configuration fail-closed verification;
7. Android debug/release lint;
8. debug/release APK and release AAB builds;
9. release APK/AAB/R8 structural/application/version/checksum verification;
10. Macrobenchmark harness compilation plus representative physical-device startup/frame measurement;
11. real manual accessibility/device/performance/signing/store QA.

No single layer replaces the others.

## Engine Tests

Run:

```bash
./gradlew :sudoku-engine:test --stacktrace
```

The engine suite covers categories such as:

### Board correctness

- parse/serialize behavior;
- row/column/box validation;
- conflict detection;
- candidate calculation;
- immutable board updates;
- invalid input handling.

### Solver correctness

- known puzzle solving;
- invalid-board rejection;
- unsolvable behavior;
- solution counting;
- unique-solution checks;
- search metrics.

### Generator correctness

- deterministic seeded generation;
- generated-board validity;
- unique-solution preservation;
- clue/difficulty target behavior;
- deterministic generation corpora;
- performance/complexity regression evidence where implemented.

### Difficulty/logical analysis

- logical technique accounting;
- difficulty calibration;
- corpus expectations;
- deterministic logical results.

### Teaching/hints

Current teaching tests cover:

- deterministic teaching traces;
- Naked Single;
- Hidden Single;
- Naked Pair;
- Pointing Pair / Triple;
- Box-Line Reduction;
- Hidden Pair;
- Naked Triple;
- Hidden Triple;
- X-Wing;
- exact source/target/elimination evidence;
- legal controlled candidate states;
- generated-puzzle solution safety;
- guarantee that candidate eliminations do not remove the unique solved value;
- hint technique identity for multi-step chains;
- explicit Reveal fallback separation.

### Practice catalog

Practice tests verify:

- every supported logical technique has practice coverage;
- deterministic catalog lookup;
- unique answer choices;
- correct answer inclusion;
- wrong-answer rejection;
- structured evidence availability.

## Android JVM Tests

Run:

```bash
./gradlew :app:testDebugUnitTest --stacktrace
```

The app-module JVM suite covers categories including:

- `GameStateCodec` round trips and malformed-state rejection;
- settings/statistics calculations;
- learning-progress invariants/mastery calculations;
- backup codec/model behavior;
- bounded backup file reading;
- transfer/persistence helper behavior;
- pure UI/presentation helpers where Android runtime is unnecessary.

The Android app module is configured around JUnit4. Tests in this module should use the configured framework consistently unless the build is deliberately migrated.

## Backup Boundary Tests

`BackupFileIoTest` includes direct regression coverage for bounded reads, including:

- UTF-8 content within the limit;
- exact-limit content;
- empty input rejection;
- oversized input rejection;
- positive maximum-size requirement.

This protects the pre-parser memory boundary in addition to `BackupCodec`'s structural validation.

## Repository Consistency Guard Tests

Repository guards that can block CI/releases have deterministic Python regression suites under `scripts/tests/`.

Run the documentation-link suite:

```bash
python -m unittest scripts.tests.test_verify_documentation_links
```

It covers repository-local Markdown target handling, ignored/generated locations, repository-boundary rejection, and supported link forms.

Run the complete tracked-file documentation-coverage suite:

```bash
python -m unittest scripts.tests.test_verify_documentation_coverage
```

It verifies:

- representative path resolution for Android source/tests/resources/Room schemas/benchmark overlays;
- Sudoku engine source/tests;
- Macrobenchmark;
- repository scripts/tests;
- GitHub workflows/metadata;
- Gradle/root/editor files;
- root documents and the `docs/` library;
- rejection of unknown/unowned tracked paths;
- rejection when a coverage rule points to an untracked canonical document;
- deterministic Markdown report rendering;
- Windows path-separator normalization while preserving legitimate leading-dot paths such as `.github/`.

Run the release source/workflow contract suite:

```bash
python -m unittest scripts.tests.test_verify_release_contract
```

It protects package/version identity synchronization between `app/build.gradle.kts`, ordinary CI expected release metadata, and protected release-validation defaults.

After the regression suites, run the guards against the actual checked-out repository:

```bash
python scripts/verify_documentation_links.py
python scripts/verify_documentation_coverage.py
python scripts/verify_release_contract.py
```

The documentation-coverage command obtains the complete tracked-file set from `git ls-files -z`; it fails if even one tracked path is outside the maintained documentation ownership taxonomy. For a per-file audit, use:

```bash
python scripts/verify_documentation_coverage.py --verbose
```

A green ownership guard proves every tracked path has a canonical documentation area. It does not prove the prose is factually current, so it complements rather than replaces source review, link checks, builds/tests, and manual/release evidence.

See `REPOSITORY_GUARDS.md` and `REPOSITORY_FILE_REFERENCE.md`.

## Release-Output Verifier Tests

The v1.0 line includes pure-Python regression coverage for `scripts/verify_release_outputs.py`.

Run:

```bash
python -m unittest scripts.tests.test_verify_release_outputs
```

The tests verify:

- minimum valid APK/AAB archive structures are accepted;
- required archive entries are enforced;
- APK release metadata requires exactly one element;
- version code/name metadata is parsed correctly;
- production `applicationId` metadata is parsed and missing/wrong identities are rejected when required;
- checksum-manifest output is deterministic and includes hash, byte size and path;
- certificate SHA-256 normalization accepts supported colon/no-colon forms and rejects malformed values;
- `apksigner` signer-certificate digest parsing;
- `keytool` signer-certificate fingerprint parsing;
- missing signature-verifier tools fail mandatory signed verification;
- unsigned AAB output is rejected;
- expected APK signer-certificate mismatch is rejected;
- expected AAB signer/upload-certificate mismatch is rejected;
- normalized non-secret signature evidence is written deterministically.

The verifier itself runs later in CI against the actual Gradle-generated release outputs.

## Partial Release-Signing Fail-Closed Regression

`app/build.gradle.kts` permits production signing only when all four required environment values are supplied:

- `SUDOKUNOVA_KEYSTORE_PATH`;
- `SUDOKUNOVA_KEYSTORE_PASSWORD`;
- `SUDOKUNOVA_KEY_ALIAS`;
- `SUDOKUNOVA_KEY_PASSWORD`.

A partial configuration is an error. Standard CI deliberately invokes Gradle with only a harmless test alias and requires configuration to fail with the expected partial-signing message.

This verifies a critical release invariant: an apparently configured signing environment must not silently fall back to an unsigned artifact.

Do not place real signing secrets in test commands.

## Android Instrumentation Test Compilation

Run:

```bash
./gradlew :app:assembleDebugAndroidTest --stacktrace
```

This ensures Android test code and the test APK compile. It is a fast gate for API/test-source mistakes but does not prove runtime behavior.

## Connected Compose/Room Tests

Run on a configured emulator/device:

```bash
./gradlew :app:connectedDebugAndroidTest --stacktrace
```

GitHub Actions runs the connected suite on an API-35 x86_64 emulator target.

Connected coverage includes important flows such as:

- Home entry points;
- Challenges/archive navigation;
- Custom Puzzle reachability and selected-state behavior;
- History and Saved Puzzles;
- Settings input controls;
- Learn lesson/practice flow;
- Room persistence/migration behavior;
- transfer/backup integrated behavior;
- selected Sudoku-cell accessibility semantics;
- adaptive scroll reachability for hardened large-text layouts;
- prior navigation/state regression paths preserved across milestones.

The exact suite evolves with the codebase; source under `app/src/androidTest/` is authoritative.

## Stable Compose Test Selectors

Prefer user-visible semantics/text where it is unambiguous and actually composed.

Use stable test tags when:

- multiple controls share the same label;
- a LazyColumn item is off-screen/not composed;
- a specific logical technique must be targeted deterministically;
- a board/editor cell needs stable coordinate identity.

Current examples include Learn list/technique tags, Sudoku game-cell tags and Custom Puzzle editor-cell tags.

Do not add test-only production APIs when normal semantics can provide a stable target.

## Accessibility Semantics Tests

Automated tests can reliably assert properties such as:

- selected state;
- content descriptions;
- tagged element identity;
- visible dialog/action state;
- reachability after deliberate scrolling.

They cannot replace real TalkBack focus/gesture experience, 200% font-layout judgment, high-contrast/reduced-motion device review, or physical keyboard testing.

Use `ACCESSIBILITY.md` and `V1_RELEASE_CANDIDATE.md` for stable-release manual evidence.

## Translation Verification

Run:

```bash
python scripts/verify_translations.py
```

The script protects English/Hindi resource parity and formatting compatibility.

A feature with new player-facing text is incomplete until both maintained locales are updated.

## Repository Security Verification

Run:

```bash
python scripts/verify_no_secrets.py
```

This catches committed signing/private-key material and obvious credential patterns covered by the repository guard.

It is not a replacement for manual review, GitHub secret scanning/push protection, or secure key storage.

## Android Lint

Debug and release lint:

```bash
./gradlew :app:lintDebug :app:lintRelease --stacktrace
```

Release lint matters because release-only configuration/resource behavior can differ from debug.

## Build Verification

### Debug APK

```bash
./gradlew :app:assembleDebug --stacktrace
```

### Release APK with R8/resource shrinking

```bash
./gradlew :app:assembleRelease --stacktrace
```

### Release AAB

```bash
./gradlew :app:bundleRelease --stacktrace
```

### Macrobenchmark harness

```bash
./gradlew :macrobenchmark:assembleBenchmark --stacktrace
```

Successful Macrobenchmark assembly proves the performance test module and release-like benchmark variant compile together. It does not produce representative timing evidence until the connected benchmark is run on an appropriate physical target.

Successful release assembly verifies release compilation/shrinking but does not by itself prove artifact structure/application/version metadata, production signing, certificate identity or device QA.

## Release Artifact Verification

After the v1.0 RC unsigned release outputs exist, run:

```bash
python scripts/verify_release_outputs.py \
  --apk app/build/outputs/apk/release/app-release-unsigned.apk \
  --aab app/build/outputs/bundle/release/app-release.aab \
  --mapping app/build/outputs/mapping/release/mapping.txt \
  --metadata app/build/outputs/apk/release/output-metadata.json \
  --expected-version-code 1000 \
  --expected-version-name 1.0.0-rc.1 \
  --expected-application-id in.sanskar.sudokunova \
  --output app/build/outputs/release-evidence/sha256.txt
```

The verifier requires:

- non-empty APK/AAB/mapping outputs;
- ZIP-valid APK/AAB archives;
- core expected archive entries;
- exactly one APK release metadata element;
- exact production application ID when requested;
- exact RC `versionCode` / `versionName`;
- a non-empty R8 mapping;
- SHA-256/byte-size evidence for APK, AAB and mapping.

CI uploads the checksum evidence with the short-lived release build outputs after success.

This does **not** prove production signing or store acceptance.

## Recommended Broad v1.0 RC Local Gate

```bash
python scripts/verify_no_secrets.py
python -m unittest scripts.tests.test_verify_documentation_links
python -m unittest scripts.tests.test_verify_documentation_coverage
python -m unittest scripts.tests.test_verify_release_contract
python -m unittest scripts.tests.test_verify_release_outputs
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
  --expected-version-code 1000 \
  --expected-version-name 1.0.0-rc.1 \
  --expected-application-id in.sanskar.sudokunova \
  --output app/build/outputs/release-evidence/sha256.txt
```

Windows can use `gradlew.bat` with the same Gradle tasks and PowerShell line continuation for the verifier command.

Connected functional instrumentation and physical-device Macrobenchmarks should be run separately on appropriate targets.

## Determinism Rules

Deterministic tests are strongly preferred for correctness-critical Sudoku logic and release/repository tooling.

Use:

- fixed seeds for generation;
- fixed known puzzles for solver/teaching tests;
- deterministic candidate-state fixtures for advanced technique evidence;
- fixed timestamps/keys when testing challenge/history formats where practical;
- stable artifact manifest ordering and explicit expected application/version metadata;
- stable path fixtures for repository guard acceptance/rejection;
- Git's tracked-file set rather than filesystem walking for documentation ownership.

Avoid tests that depend on random global state, wall-clock timing, network availability, or iteration order that is not part of the contract.

Performance measurement is inherently time-based, so it is handled as a dedicated benchmark/evidence workflow rather than a fragile shared-runner wall-clock unit-test threshold.

## Performance Tests

Prefer deterministic complexity metrics before fragile wall-clock assertions.

Useful solver/generator evidence includes:

- node counts;
- guesses;
- backtracks;
- depth;
- clue counts;
- logical technique counts;
- candidate elimination counts;
- fixed-seed execution measurements when a stable benchmark environment exists.

Android release-like startup/frame measurements use `:macrobenchmark`:

```bash
./gradlew :macrobenchmark:connectedBenchmarkAndroidTest --stacktrace
```

The committed suite currently measures cold startup, warm startup and cold-start frame timing with a defined no-compilation starting state and ten iterations per benchmark. Record the physical device, OS, exact commit and raw benchmark output/traces.

Do not introduce an arbitrary millisecond threshold on shared CI without a measured baseline and variance analysis. Do not use hosted-emulator timing as stable-production evidence.

Stable v1.0 still requires real measured startup/frame/memory/ANR evidence on representative target(s). The Macrobenchmark harness addresses startup/frame reproducibility but does not automatically measure memory or establish ANR absence.

See `PERFORMANCE.md`, `PERFORMANCE_BENCHMARKING.md` and `V1_RELEASE_CANDIDATE.md`.

## Database Migration Tests

Room schema changes require migration coverage.

A migration test should demonstrate that:

- the old schema can be created/loaded;
- migration runs successfully;
- existing records survive as intended;
- new indexes/constraints exist;
- DAO behavior still works.

Do not use destructive fallback as a substitute for a required migration.

## Transfer/Parser Fuzz-Style Cases

For external text formats, test malformed classes such as:

- empty input;
- oversized input;
- unsupported version;
- wrong field count;
- invalid enum;
- invalid number;
- invalid timestamp/counter;
- checksum mismatch;
- invalid Sudoku board;
- non-unique imported puzzle at the Android acceptance boundary;
- duplicate restore records.

Parsers should return failure/reject input rather than crash the app.

## Regression Testing Rule

For an important defect:

1. reproduce it;
2. identify root cause;
3. add a failing regression test when practical;
4. implement the smallest correct fix;
5. run the narrow test first;
6. run the broader affected module gate;
7. run final required CI/connected gates before merge;
8. document release-relevant defects in `CHANGELOG.md`/`what_changed.md`.

For repository/documentation structure defects, also add or update the narrow coverage/link guard test that would prevent recurrence.

## Exact-Head Rule

A successful workflow run applies only to the commit it tested.

If the PR head changes, old success is historical evidence only. Before merge/release, verify the final exact head.

PR #27 satisfied this rule before it was merged for repository-side RC1 preparation. PR #28 independently satisfied it and was merged from verified head `c3e0e3fc217062e374a434cfea46235fd6595f83` after Android CI `#706 / 32211246803` and API-35 Android Instrumentation `#229 / 32211246802` passed. Later pull requests—including the documentation-completion PR #30—must independently satisfy the same rule on their own final heads.

`what_changed.md` should record exact run IDs/head SHAs only after the runs complete.

## Manual / Production QA

Automated tests do not fully cover:

- TalkBack traversal/focus order;
- 200% font scaling/layout judgment;
- high-contrast/reduced-motion real-device behavior;
- physical hardware keyboard behavior;
- device-specific dynamic color;
- install/update behavior across representative devices;
- process-death/lifecycle behavior on real targets;
- measured startup/frame/memory/ANR behavior;
- production signing certificate identity;
- signed production artifact installation;
- distribution-platform AAB validation;
- Play Store listing/privacy/assets correctness.

Use `V1_RELEASE_CANDIDATE.md` as the authoritative v1.0 worksheet and `PLAY_STORE_RELEASE.md` for publication preparation. Do not mark any manual row passed until it was actually performed.

## Stable v1.0 Evidence Boundary

The verified merged RC1 preparation proves that its exact repository-side RC source and automation were green. The verified merged PR #28 proves the additional release-validation/performance tooling was green on its own exact final head.

Neither result by itself proves that stable `v1.0.0` is ready to ship.

Stable promotion additionally requires the actual manual/production evidence described above plus a final exact stable source SHA, signed artifacts, certificate verification, release hashes, representative physical-device performance evidence, and a deliberate `SHIP` decision.

Documentation/repository guard success also does not substitute for these stable-production requirements.

## CI Reference

See `CI_CD.md` for the complete GitHub Actions gate and artifact policy, `REPOSITORY_GUARDS.md` and `REPOSITORY_FILE_REFERENCE.md` for repository/documentation consistency enforcement, `PRODUCTION_SIGNING.md` and `PRODUCTION_RELEASE_VALIDATION.md` for signing/identity verification, `PERFORMANCE_BENCHMARKING.md` for physical performance evidence, and `RELEASING.md` for the RC-to-stable process.
