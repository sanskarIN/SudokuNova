# What Changed

## Current Development State — SudokuNova 2.0.12 Cross-Platform Continuation — 2026-08-20

**Repository:** `https://github.com/sanskarIN/SudokuNova`  
**Base branch:** `main`  
**Verified 2.0.12 baseline merge:** `5fdafd77332b4889c5bd64bd23b1c4869ade0962`  
**Active branch:** `feature/v2.0.12-cross-platform`  
**Active pull request:** `#33`  
**Head immediately before this ledger commit:** `71167112f3717766a6eea067c0cd958978b960bc`  
**Android source release target:** `2.0.12`  
**Android versionCode:** `2012`  
**Android versionName:** `2.0.12`  
**Android application ID:** `in.sanskar.sudokunova`  
**minSdk:** `26`  
**targetSdk / compileSdk:** `37`  
**JDK/JVM target:** `17`  
**Android Gradle Plugin:** `9.3.1`  
**Kotlin:** `2.4.10`  
**Compose Multiplatform:** `1.11.1`  
**License:** MIT

This ledger commit intentionally becomes a **new exact PR head** after the SHA shown above. Therefore all runs from `71167112...` or any earlier PR #33 head are historical as soon as this commit lands. The pull request's actual final head SHA and its workflow results are the authority for merge verification.

No more branch edits are intended after this ledger commit unless the new exact-head CI exposes a concrete defect.

---

## Verified 2.0.12 Baseline Was Merged First

Before beginning the new cross-platform work, the final Android 2.0.12 repository/release line was verified and merged rather than layering new platform code on top of an unverified or stale branch.

PR #30:

- final verified head: `f097f7fb58a4d9b5e4f87b998b01e9d082ac9f29`;
- required Android CI: green on that exact head;
- required API-35 Android Instrumentation: green on that exact head;
- merge commit on `main`: `5fdafd77332b4889c5bd64bd23b1c4869ade0962`.

That merge preserved the current Android release identity:

```text
applicationId = in.sanskar.sudokunova
versionCode   = 2012
versionName   = 2.0.12
minSdk        = 26
targetSdk     = 37
compileSdk    = 37
```

It also preserved the established repository/release gates, including tracked-file documentation ownership, documentation-link integrity, translation parity, Android tests/lint/builds, R8/AAB verification, embedded APK identity checks, fail-closed release signing, and protected certificate-bound production validation.

The cross-platform work is deliberately based on this merged line.

---

## Why the Earlier Cross-Platform Branch Was Replaced

The older cross-platform PR #32 was inspected before new work continued.

Its hosted jobs failed during Gradle configuration before platform-specific compilation. The common root issue was `sharedUI/build.gradle.kts` using DSL/API shapes that did not compile with the repository's Kotlin/Gradle/Android plugin line, including invalid compiler-option/source-set/Web configuration access.

PR #32 also diverged from the newly verified 2.0.12 baseline and conflicted with release-sensitive Android/CI files.

Rather than patching every failed job independently or downgrading the verified 2.0.12 toolchain, a clean integration branch was created from merge `5fdafd...`:

```text
feature/v2.0.12-cross-platform
```

The new line keeps:

- Android Gradle Plugin 9.3.1;
- Kotlin 2.4.10;
- JDK/JVM 17;
- compile/target SDK 37;
- `versionCode 2012` / `versionName 2.0.12`;
- the mature Android production launcher;
- the existing Android release/signing/evidence contract.

---

## Kotlin Multiplatform Engine Migration

`sudoku-engine` was migrated from a JVM-only Gradle configuration to Kotlin Multiplatform without moving Sudoku truth into platform code.

Current targets:

- Android KMP library target;
- Desktop JVM;
- iOS arm64;
- iOS Simulator arm64;
- Web/Wasm browser.

The existing production tree remains:

```text
sudoku-engine/src/main/kotlin/com/sanskar/sudokunova/engine/
```

and is mapped into `commonMain`.

The existing regression tree remains:

```text
sudoku-engine/src/test/kotlin/com/sanskar/sudokunova/engine/
```

and is mapped into `commonTest`.

The suite already uses `kotlin.test`, so a risky mass JUnit rewrite was not required.

The engine continues to own:

- immutable board truth;
- validation/conflicts/candidates;
- solving and solution counting;
- deterministic generation;
- uniqueness;
- difficulty analysis/calibration;
- logical solving;
- structured teaching evidence;
- hints;
- deterministic practice;
- puzzle-code behavior.

It remains independent of Compose, Android resources/lifecycle, Room/DataStore, UIKit, browser DOM APIs, and Desktop windows.

---

## New `sharedUI` Compose Multiplatform Module

A new Gradle module was added:

```text
:sharedUI
```

It uses:

- Kotlin Multiplatform;
- Android Kotlin Multiplatform library plugin;
- Compose Multiplatform;
- Kotlin Compose compiler plugin;
- portable Compose runtime/foundation/UI/Material 3 dependencies.

Targets:

- Android;
- Desktop JVM;
- iOS arm64;
- iOS Simulator arm64;
- Web/Wasm.

The module builds on `:sudoku-engine` rather than duplicating domain rules.

### Portable gameplay state

Added:

```text
sharedUI/src/commonMain/kotlin/com/sanskar/sudokunova/shared/SharedGameState.kt
```

Implemented behavior:

- generated puzzles;
- Beginner/Easy/Medium/Hard/Expert/Master/Extreme difficulty selection;
- fixed clue protection;
- editable cell selection;
- number entry;
- candidate notes;
- peer-note cleanup after placement;
- conflict/wrong-move/status feedback;
- erase;
- bounded 100-step undo history;
- engine-backed hints;
- reset;
- new game;
- solved-state detection.

### Portable gameplay tests

Added:

```text
sharedUI/src/commonTest/kotlin/com/sanskar/sudokunova/shared/SharedGameStateTest.kt
```

Current regression coverage includes:

- fixed clues cannot be overwritten;
- note mode behavior;
- number placement and note removal;
- undo board/note restoration;
- reset to starting puzzle;
- hint progression;
- board validity after hint use.

### Responsive shared UI

Added:

```text
sharedUI/src/commonMain/kotlin/com/sanskar/sudokunova/shared/SudokuNovaSharedApp.kt
```

The shared Material 3 surface includes:

- SudokuNova header/branding;
- difficulty picker;
- responsive 9×9 board;
- visual fixed/selected/conflict states;
- candidate-note rendering;
- number pad;
- Notes, Erase, Undo, Hint, Reset, New actions;
- portable status messages;
- bounded board width for larger windows and responsive width for smaller surfaces.

This is a cross-platform gameplay foundation, not a false claim that every mature Android feature has already been ported.

---

## Android Integration Without Regressing the Mature App

The Android application now consumes both shared modules:

```text
implementation(project(":sudoku-engine"))
implementation(project(":sharedUI"))
```

Added:

```text
app/src/main/java/com/sanskar/sudokunova/CrossPlatformActivity.kt
```

The activity hosts `SudokuNovaSharedApp()` and is registered as:

- non-exported;
- normal SudokuNova theme;
- not a launcher.

`MainActivity` remains the exported `MAIN` / `LAUNCHER` activity.

This staged design protects the feature-rich Android product while shared parity grows. It avoids the low-quality approach of deleting Android-only capabilities merely so every platform appears superficially identical.

Android-only capabilities currently retained in the mature app include, among others:

- full navigation/screens;
- Room/DataStore persistence;
- history/saved puzzles;
- challenge/custom puzzle flows;
- learning/statistics/achievements;
- backup/transfer/import/export;
- English/Hindi Android resources;
- mature Android accessibility integration;
- Android release/signing/artifact validation;
- Macrobenchmark infrastructure.

---

## Desktop Support

Added Desktop entry point:

```text
sharedUI/src/desktopMain/kotlin/com/sanskar/sudokunova/shared/desktop/Main.kt
```

Compose Desktop configuration defines:

- `mainClass` for the shared app;
- application name `SudokuNova`;
- package version `2.0.12`;
- vendor `Sanskar`;
- native format declarations:
  - Windows MSI;
  - macOS DMG;
  - Linux DEB.

Development command:

```bash
./gradlew :sharedUI:run
```

Application-image command:

```bash
./gradlew :sharedUI:createDistributable
```

Host-native package/signing/notarization/reputation/distribution evidence remains external.

---

## iOS and iPadOS Foundation

Added Kotlin/Compose UIKit bridge:

```text
sharedUI/src/iosMain/kotlin/com/sanskar/sudokunova/shared/ios/MainViewController.kt
```

The module produces a static framework named:

```text
SudokuNovaSharedUI.framework
```

Added SwiftUI host sources:

```text
iosApp/SudokuNovaApp.swift
iosApp/ContentView.swift
iosApp/README.md
```

The SwiftUI wrapper uses `UIViewControllerRepresentable` to host the Kotlin `MainViewController()`.

Simulator framework command:

```bash
./gradlew :sharedUI:linkDebugFrameworkIosSimulatorArm64
```

Device framework command:

```bash
./gradlew :sharedUI:linkReleaseFrameworkIosArm64
```

Evidence boundary is explicit: these sources/framework targets do not prove a complete signed Xcode app, provisioning, physical-device QA, TestFlight/App Store acceptance, or publication.

---

## Web/Wasm Foundation

Added:

```text
sharedUI/src/wasmJsMain/kotlin/com/sanskar/sudokunova/shared/web/Main.kt
sharedUI/src/wasmJsMain/resources/index.html
sharedUI/src/wasmJsMain/resources/styles.css
```

The browser target mounts the shared Compose application into `#webApp` and builds a production WebAssembly distribution.

Production distribution command:

```bash
./gradlew :sharedUI:wasmJsBrowserDistribution
```

Development server command:

```bash
./gradlew :sharedUI:wasmJsBrowserDevelopmentRun
```

A generated Wasm distribution is build evidence, not proof of every intended browser/device combination.

---

## Hosted Cross-Platform CI

Added:

```text
.github/workflows/cross-platform.yml
```

The workflow validates:

### Shared code — Ubuntu

- `:sudoku-engine:desktopTest`;
- `:sharedUI:desktopTest`;
- `:sharedUI:compileKotlinDesktop`;
- `:sharedUI:compileKotlinWasmJs`.

### Android shared integration — Ubuntu

- `:app:assembleDebug`.

### Web — Ubuntu

- `:sharedUI:wasmJsBrowserDistribution`;
- production Web artifact upload.

### iOS — macOS

- `:sharedUI:linkDebugFrameworkIosSimulatorArm64`;
- framework artifact upload.

### Desktop — matrix

- Linux `createDistributable` + artifact upload;
- Windows `createDistributable` + artifact upload;
- macOS `createDistributable` + artifact upload.

This workflow does not receive Android production-signing secrets.

---

## Existing Android CI Was Strengthened, Not Replaced

`.github/workflows/ci.yml` now runs the shared correctness/compile path before the established Android workload:

1. repository security/release-tool/documentation/release-contract guards;
2. translation parity;
3. `:sudoku-engine:desktopTest`;
4. `:sharedUI:desktopTest`;
5. shared Desktop/Wasm compilation;
6. Android JVM tests;
7. instrumentation-test compilation;
8. Macrobenchmark compilation;
9. debug/release lint;
10. debug APK;
11. R8/resource-shrunk release APK;
12. release AAB;
13. exact 2.0.12 application/version/SDK/debuggable verification;
14. SHA-256/APK-identity evidence;
15. report/release artifact upload.

The API-35 connected Android Instrumentation workflow remains separate and required.

---

## Documentation Enforcement Expanded

The repository already had a fail-closed documentation ownership guard. Cross-platform work was integrated into that system rather than bypassing it.

Updated:

```text
scripts/verify_documentation_coverage.py
scripts/tests/test_verify_documentation_coverage.py
```

New ownership areas cover:

- `sharedUI/src/commonTest/`;
- other `sharedUI/src/` implementation/resources;
- `sharedUI/` module configuration;
- `iosApp/` Apple host sources.

The guard still requires:

- every `git ls-files` path to have an owner;
- every canonical documentation target to exist;
- every detailed `docs/*.md` guide to be indexed from `docs/README.md`.

---

## Cross-Platform Documentation Added and Reconciled

Added:

```text
docs/CROSS_PLATFORM.md
iosApp/README.md
```

Reworked/aligned:

```text
README.md
docs/README.md
docs/ARCHITECTURE.md
docs/PROJECT_STRUCTURE.md
docs/BUILDING.md
docs/TESTING.md
docs/CI_CD.md
docs/REPOSITORY_FILE_REFERENCE.md
CHANGELOG.md
THIRD_PARTY_NOTICES.md
what_changed.md
```

The documentation now explains:

- the real KMP module/target graph;
- Android mature-production vs shared-foundation roles;
- build/test commands for Android/Desktop/Web/iOS frameworks;
- hosted cross-platform CI;
- path ownership;
- third-party Compose/Kotlin/platform tooling;
- exact-head merge rules;
- production evidence boundaries.

The root README was intentionally made a clearer public landing page rather than retaining stale PR #30 pre-merge instructions and excessive historical implementation detail. Deep history remains preserved in the documentation library and Git/PR history.

---

## Third-Party Notice Audit

`THIRD_PARTY_NOTICES.md` now covers the new direct/tooling families introduced by the multiplatform work:

- Kotlin Multiplatform / Kotlin/Native / Kotlin/Wasm;
- Compose Multiplatform runtime/foundation/UI/Material 3/Desktop/Apple/Web tooling;
- Android KMP build tooling;
- Kotlin Test for shared regression suites;
- GitHub-hosted multi-OS runner/toolchain boundary;
- Android SDK, Xcode/Apple SDKs, JDK `jpackage`, and browser runtime requirements.

The dependency/source-of-truth section points maintainers at the version catalog and all relevant module/workflow files rather than pretending a prose notice is a complete transitive inventory.

---

## Meaningful Atomic Commit Structure

The cross-platform line was intentionally implemented through many focused commits rather than one opaque mega-commit. Major commit families include:

### Build foundation

- `build(kmp): add multiplatform and Compose dependencies`
- `build(kmp): register cross-platform Gradle plugins`
- `build(kmp): include shared UI module`
- `refactor(engine): migrate Sudoku engine to Kotlin Multiplatform`
- `build(engine): normalize KMP source-set accessors`
- `feat(shared-ui): add corrected Compose Multiplatform module`
- `build(android): consume shared multiplatform UI`

### Shared product work

- `feat(shared-ui): add portable gameplay state`
- `test(shared-ui): cover portable gameplay state`
- `feat(shared-ui): add responsive multiplatform Sudoku UI`

### Platform hosts

- `feat(desktop): add shared desktop launcher`
- `feat(ios): add shared Compose UIViewController bridge`
- `feat(ios): add SwiftUI bridge for shared Compose UI`
- `feat(ios): add SwiftUI application entry point`
- `feat(web): add Compose Wasm launcher`
- `feat(web): add production WebAssembly host page`
- `feat(web): add responsive browser shell styles`
- `feat(android): add shared UI host activity`
- `feat(android): register shared UI host activity`

### CI and repository guards

- `ci(android): validate shared multiplatform code before release gates`
- `ci(cross-platform): add hosted multi-OS build matrix`
- `feat(docs-guard): cover multiplatform source areas`
- `test(docs-guard): cover multiplatform documentation ownership`

### Documentation/quality

- `docs(cross-platform): add 2.0.12 platform build and evidence guide`
- `docs(ios): document native host integration and signing boundary`
- `docs(index): integrate cross-platform documentation hub`
- `docs(structure): map multiplatform modules and ownership`
- `docs(build): align build guide with multiplatform 2.0.12`
- `docs(ci): document exact-head cross-platform quality gates`
- `docs(testing): cover shared and host-specific verification`
- `docs(architecture): define staged multiplatform boundaries`
- `docs(files): add multiplatform path ownership taxonomy`
- `docs(readme): present verified baseline and multiplatform direction`
- `docs(changelog): record cross-platform foundation and verified baseline`
- `docs(licenses): cover Compose Multiplatform and platform tooling`
- this ledger commit.

---

## Validation Performed During This Continuation

### Repository/API inspection

The repository, pull requests, exact commits, files, and GitHub Actions failures were inspected directly through GitHub.

The old cross-platform failure was traced to a shared Gradle configuration defect rather than being guessed separately for each platform job.

The verified 2.0.12 Android line was merged only after its exact-head Android CI and API-35 Instrumentation were confirmed green.

### Engine test compatibility audit

Representative and advanced engine tests were inspected after KMP migration. They already import `kotlin.test` rather than JUnit-only APIs, supporting their placement in `commonTest`.

### Local runner limitation

A local clone/Gradle validation attempt from the assistant execution environment was blocked by DNS/network resolution to GitHub. Therefore no local Gradle success is claimed from that environment.

Hosted GitHub Actions are the authoritative validation path for PR #33.

### Current PR workflow evidence rule

During development, multiple PR #33 heads started queued workflow runs. Every subsequent documentation/code commit superseded those heads. Those queued/cancelled/older runs are **not** final evidence.

After this ledger commit creates the new exact head, merge verification must use only runs associated with that final SHA.

Required final PR #33 set:

1. `Android CI` — green;
2. `Android Instrumentation` — green;
3. every `Cross-Platform CI` job — green.

If any of these fail, the branch must be changed only to repair the concrete failure, then the entire exact-head evidence set is required again on the new SHA.

---

## Production Evidence Not Claimed

The current repository source/build work does **not** claim completion of:

- Android production-keystore/certificate validation unless the protected workflow actually succeeds;
- final signed Android installation/upgrade QA on representative devices;
- TalkBack/200% font/contrast/reduced-motion/hardware-keyboard/manual accessibility QA for the final production artifact;
- representative physical-device startup/frame/memory/ANR evidence;
- Play Console acceptance or public Android release;
- complete Xcode project/distribution setup;
- Apple signing/provisioning/physical-device QA/TestFlight/App Store acceptance;
- macOS signing/notarization;
- Windows code signing/reputation;
- Linux distribution-repository compatibility;
- broad Web browser/device compatibility;
- final public `v2.0.12` tag/GitHub Release/store publication.

These must remain explicit real-world release tasks rather than fabricated completions.

---

## Documentation and Historical Records

Detailed cumulative development history through 2026-08-19 is preserved at:

```text
docs/archive/what_changed_through_2026-08-19.md
```

Current Android 2.0.12 release authority:

```text
docs/V2_0_12_RELEASE.md
```

Current cross-platform authority:

```text
docs/CROSS_PLATFORM.md
```

Historical v1 evidence remains in the v1-specific release/evidence documents and merged PR history. Historical workflow runs prove only their exact historical commits.

---

## Current Handoff Rule

PR #33 is the single clean cross-platform integration line based on the verified 2.0.12 `main` baseline.

Do not merge it merely because GitHub reports it conflict-free. Merge only after the **new exact final head created by this ledger commit** satisfies all required Android and cross-platform workflows.

If it passes, the merge establishes the repository-side cross-platform foundation. It still does not convert external signing/store/device/browser requirements into completed evidence.

**SudokuNova — Think. Solve. Master the Grid.**  
**Made by the Sanskar**
