# SudokuNova Testing Guide

SudokuNova treats deterministic correctness and regression coverage as merge requirements. The testing strategy spans the platform-independent Sudoku engine, Android JVM logic, Compose/Room connected tests, static analysis, release builds, and manual release QA.

## Testing Layers

The project uses several complementary layers:

1. `sudoku-engine` JVM tests for Sudoku truth and deterministic domain behavior;
2. Android app JVM tests for codecs/models/presentation-independent application logic;
3. Android instrumentation tests for Compose, Room, lifecycle-adjacent and integrated flows;
4. translation/security verification scripts;
5. Android debug/release lint;
6. debug/release APK and release AAB builds;
7. manual accessibility/device/release QA.

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
- Custom Puzzle reachability;
- History and Saved Puzzles;
- Settings input controls;
- Learn lesson/practice flow;
- Room persistence/migration behavior;
- transfer/backup integrated behavior;
- selected Sudoku-cell accessibility semantics;
- prior navigation/state regression paths preserved across milestones.

The exact suite evolves with the codebase; use source under `app/src/androidTest/` as the authoritative list.

## Stable Compose Test Selectors

Prefer user-visible semantics/text where it is unambiguous and actually composed.

Use stable test tags when:

- multiple controls share the same label;
- a LazyColumn item is off-screen/not composed;
- a specific logical technique must be targeted deterministically;
- a board cell needs stable coordinate identity.

Current examples include Learn list/technique tags and Sudoku board-cell tags.

Do not add test-only production APIs when normal semantics can provide a stable target.

## Accessibility Semantics Tests

Automated tests can reliably assert properties such as:

- selected state;
- content descriptions;
- tagged element identity;
- visible dialog/action state.

They cannot replace manual TalkBack focus/gesture experience, font-scaling judgment, contrast review, or physical keyboard testing.

Use `ACCESSIBILITY.md` and `RELEASE_QA.md` for manual release expectations.

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

This catches committed signing/private-key material and obvious credential patterns covered by the v0.9 repository guard.

It is not a replacement for manual review or platform secret scanning.

## Android Lint

Debug and release lint:

```bash
./gradlew :app:lintDebug :app:lintRelease --stacktrace
```

Release lint is part of v0.9 hardening because release-only configuration/resource behavior must be checked separately from debug.

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

Successful release assembly verifies release compilation/shrinking but does not by itself prove production signing or device QA.

## Recommended Broad Local Gate

```bash
python scripts/verify_no_secrets.py
python scripts/verify_translations.py
./gradlew :sudoku-engine:test \
  :app:testDebugUnitTest \
  :app:assembleDebugAndroidTest \
  :app:lintDebug \
  :app:lintRelease \
  :app:assembleDebug \
  :app:assembleRelease \
  :app:bundleRelease \
  --stacktrace
```

Windows can use `gradlew.bat` with the same Gradle tasks.

Connected instrumentation should be run separately on a supported emulator/device.

## Determinism Rules

Deterministic tests are strongly preferred for correctness-critical Sudoku logic.

Use:

- fixed seeds for generation;
- fixed known puzzles for solver/teaching tests;
- deterministic candidate-state fixtures for advanced technique evidence;
- fixed timestamps/keys when testing challenge/history formats where practical.

Avoid tests that depend on random global state, wall-clock timing, network availability, or iteration order that is not part of the contract.

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

Do not introduce an arbitrary millisecond threshold on shared CI without a measured baseline and variance analysis.

See `PERFORMANCE.md`.

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

## Exact-Head Rule

A successful workflow run applies to the commit it tested.

If the PR head changes, old success is historical evidence only. Before merge/release, verify the final exact head.

`what_changed.md` should record exact run IDs/head SHAs only after the runs complete.

## Manual QA

Automated tests do not fully cover:

- TalkBack experience;
- 200% font scaling/layout judgment;
- physical hardware keyboard behavior;
- device-specific dynamic color;
- install/update behavior across representative devices;
- thermal/memory performance;
- Play Store listing/asset correctness.

Use `QA_MATRIX.md` and `RELEASE_QA.md`. Do not mark manual rows as passed until they were actually performed.

## CI Reference

See `CI_CD.md` for the complete GitHub Actions gate and artifact policy.
