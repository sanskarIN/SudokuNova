# What Changed

## Current Development State — SudokuNova 2.0.14 Preparation — 2026-08-25

**Repository:** `https://github.com/sanskarIN/SudokuNova`  
**Base branch:** `main`  
**Verified 2.0.13 merge baseline:** `adabaf9841eadaa172c8a236b2cbe4a6c4ef6699`  
**Active branch:** `release/v2.0.14-prep`  
**Active pull request:** `#44`  
**Release evidence tracker:** `#43`  
**Current source release target:** `2.0.14`  
**Android versionCode:** `2014`  
**Android versionName:** `2.0.14`  
**Desktop packageVersion:** `2.0.14`  
**Android application ID:** `in.sanskar.sudokunova`  
**minSdk:** `26`  
**targetSdk / compileSdk:** `37`  
**JDK/JVM:** `17`  
**Android Gradle Plugin:** `9.3.1`  
**Kotlin:** `2.4.10`  
**Compose Multiplatform:** `1.11.1`  
**Room:** `2.8.4`  
**License:** MIT

This is the active implementation and handoff ledger for the 2.0.14 source line. It is intentionally updated **before** the final exact-head workflow freeze. Once the final candidate head is green, workflow/run evidence should be recorded in PR #44 and issue #43 without adding another documentation-only source commit and invalidating those successful runs.

Repository preparation does not equal a public release. Production signing, representative device/runtime/accessibility/performance checks, Apple/Desktop/Web distribution evidence, store acceptance, final `SHIP`, immutable tagging, GitHub Release creation, and public distribution remain evidence-gated work in issue #43.

---

## Verified 2.0.13 Baseline

PR #41 final verified head:

```text
61371eab0c22197ffd4250805cf58caa6425418f
```

Exact-head workflow evidence:

- Android CI #1013 / run `32733975637` — SUCCESS;
- Android Instrumentation #384 / run `32733975646` — SUCCESS;
- Cross-Platform CI #118 / run `32733975747` — SUCCESS.

PR #41 merged to `main` as:

```text
adabaf9841eadaa172c8a236b2cbe4a6c4ef6699
```

The 2.0.14 branch was created directly from that merge, so no stale pre-2.0.13 line is being reused.

The inherited verified baseline includes:

- Kotlin Multiplatform `sudoku-engine`;
- Compose Multiplatform `sharedUI`;
- mature Android launcher retained alongside the staged shared host;
- Desktop, iOS/iPadOS framework, and Web/Wasm entry points;
- English/Hindi shared resources and parity guard;
- shared Sudoku-cell semantics and non-color conflict evidence;
- deterministic grid navigation and focusable arrow/erase handling;
- versioned `SNG1` active-game persistence and native local adapters;
- `PuzzleExchangeService` with `SNP1` compatibility and unique-solution acceptance;
- Android/Desktop release-version synchronization;
- hosted Android, instrumentation, and cross-platform CI families.

Older workflow results are historical only and are not final evidence for 2.0.14.

---

# 2.0.14 Implementation

## Portable Shared User-Settings Model

Added:

```text
sharedUI/src/commonMain/kotlin/com/sanskar/sudokunova/shared/SharedUserSettings.kt
```

The common model defines:

```text
SharedTheme.SYSTEM
SharedTheme.LIGHT
SharedTheme.DARK

SharedInputMode.CELL_FIRST
SharedInputMode.NUMBER_FIRST
```

`SharedUserSettings` now provides a portable representation for:

- theme;
- dynamic-color preference;
- input mode;
- peer highlighting;
- same-number highlighting;
- automatic mistake checking;
- automatic note removal;
- timer visibility;
- haptics preference;
- sound preference;
- reduced-motion preference;
- high-contrast preference;
- mistake limit.

Mistake limits are validated against the mature supported values:

```text
0 = unlimited
3
5
```

The schema is deliberately broader than the first shared UI behavior. Persisting a field does not falsely claim that every target already implements the corresponding native feedback or interaction semantics.

---

## Common Settings Storage Boundary

Added:

```text
sharedUI/src/commonMain/kotlin/com/sanskar/sudokunova/shared/SharedSettingsPersistence.kt
```

It defines:

- `SHARED_SETTINGS_STORAGE_KEY = "sudokunova.shared.settings.v1"`;
- `SharedSettingsStore` for typed load/save/clear;
- `SharedSettingsTextStore` as the minimal native text-storage boundary;
- `EncodedSharedSettingsStore` to compose native storage with the common codec.

Common code imports no Android `Context`, DataStore, Java Preferences, browser DOM storage, or Apple Foundation type.

---

## Deterministic `SNS1` Settings Format

Added:

```text
sharedUI/src/commonMain/kotlin/com/sanskar/sudokunova/shared/SharedSettingsCodec.kt
```

Current format prefix:

```text
SNS1
```

Current deterministic field order:

```text
theme
dynamicColor
inputMode
highlightPeers
highlightSameNumbers
autoCheckMistakes
autoRemoveNotes
showTimer
haptics
sounds
reducedMotion
highContrast
mistakeLimit
```

The default vector is pinned by regression coverage to:

```text
SNS1|theme=SYSTEM|dynamicColor=1|inputMode=CELL_FIRST|highlightPeers=1|highlightSameNumbers=1|autoCheckMistakes=1|autoRemoveNotes=1|showTimer=1|haptics=1|sounds=0|reducedMotion=0|highContrast=0|mistakeLimit=3
```

Fail-closed validation rejects:

- empty payloads;
- payloads over 512 characters;
- unsupported versions;
- incorrect field counts;
- malformed key/value pairs;
- unknown keys;
- duplicate keys;
- missing fields;
- booleans other than `0` or `1`;
- unknown enum values;
- unsupported mistake limits.

`SNS1` is a compatibility and validation format, not encryption and not a secret store.

---

## Shared Settings Tests

Added:

```text
sharedUI/src/commonTest/kotlin/com/sanskar/sudokunova/shared/SharedSettingsCodecTest.kt
sharedUI/src/commonTest/kotlin/com/sanskar/sudokunova/shared/EncodedSharedSettingsStoreTest.kt
sharedUI/src/commonTest/kotlin/com/sanskar/sudokunova/shared/SharedSettingsStateTest.kt
```

Coverage includes:

- exact deterministic default encoding;
- non-default round trips;
- unsupported-version rejection;
- malformed-boolean rejection;
- unknown-enum rejection;
- invalid mistake-limit rejection;
- missing/duplicate/extra/oversized payload rejection;
- typed encoded-store save/load/clear;
- corrupt stored payload fail-closed behavior;
- observable settings update/replace;
- settings save/restore/clear;
- synchronous nullable suspend-result handling without confusing a successful `null` with an unfinished coroutine.

---

## Observable Shared Settings State

Added:

```text
sharedUI/src/commonMain/kotlin/com/sanskar/sudokunova/shared/SharedSettingsState.kt
sharedUI/src/commonMain/kotlin/com/sanskar/sudokunova/shared/PersistedSharedSettingsState.kt
```

`SharedSettingsState` provides observable common state plus:

- immutable-transform update;
- replacement;
- restore from `SharedSettingsStore`;
- save;
- clear stored settings.

`rememberPersistedSharedSettingsState`:

1. starts from safe defaults;
2. attempts one restore;
3. keeps the UI usable if storage/decode fails;
4. marks restoration complete;
5. autosaves later observable changes.

Storage failure is not allowed to become a gameplay failure.

---

## Native Local Settings Adapters

### Android staged shared host

Added:

```text
app/src/main/java/com/sanskar/sudokunova/CrossPlatformSharedPreferencesSettingsTextStore.kt
```

It uses a private `SharedPreferences` file:

```text
sudokunova_shared_settings
```

The mature Android application retains its existing DataStore settings authority. The shared-host adapter does not silently replace or migrate mature Android settings.

### Desktop

Added:

```text
sharedUI/src/desktopMain/kotlin/com/sanskar/sudokunova/shared/desktop/DesktopPreferencesSettingsTextStore.kt
```

It uses:

```text
Preferences.userRoot().node("in/sanskar/sudokunova")
```

and flushes writes/removals.

### Web/Wasm

Added:

```text
sharedUI/src/wasmJsMain/kotlin/com/sanskar/sudokunova/shared/web/WebLocalStorageSettingsTextStore.kt
```

It uses browser `localStorage` with the shared settings key.

Browser quotas, clearing behavior, private/privacy modes, and compatibility remain runtime-evidence concerns.

### iOS/iPadOS

Added:

```text
sharedUI/src/iosMain/kotlin/com/sanskar/sudokunova/shared/ios/AppleUserDefaultsSettingsTextStore.kt
```

It uses `NSUserDefaults.standardUserDefaults`.

Framework compilation does not prove physical-device lifecycle, backup, signing/provisioning, TestFlight, or App Store behavior.

---

## Shared Host Integration

Updated Android `CrossPlatformActivity` to own and restore/save both:

- `SharedGameState` through `EncodedSharedGameStore`;
- `SharedSettingsState` through `EncodedSharedSettingsStore`.

Updated Desktop, Web, and Apple entry points to create independent active-game and settings stores, restore/autosave both states, and pass them into the shared application.

The new settings path remains local/offline and introduces no cloud/account/analytics dependency.

---

## Shared Theme Behavior

Updated:

```text
sharedUI/src/commonMain/kotlin/com/sanskar/sudokunova/shared/SudokuNovaSharedApp.kt
```

The shared app now accepts both gameplay and settings state.

Theme behavior:

- `SYSTEM` follows `isSystemInDarkTheme()`;
- `LIGHT` uses the shared Material 3 light color scheme;
- `DARK` uses the shared Material 3 dark color scheme.

A localized System/Light/Dark picker updates `SharedSettingsState`, exposes selected semantics, applies the change immediately, and persists it through the host settings store.

The stored `dynamicColor` preference is not misrepresented as portable Material You behavior. Android-only dynamic color still requires an Android-specific implementation decision.

---

## Shared Localization

Updated both shared resource sets:

```text
sharedUI/src/commonMain/composeResources/values/strings.xml
sharedUI/src/commonMain/composeResources/values-hi/strings.xml
```

Added matching English/Hindi keys for:

- Theme;
- System;
- Light;
- Dark.

The existing deterministic translation key/placeholder parity guard remains authoritative.

---

## Shared Hardware-Keyboard Parity

The inherited 2.0.13 shared grid already supported:

- Up/Down/Left/Right movement;
- Backspace/Delete erase.

2.0.14 adds:

- `1` through `9` → existing `SharedGameState.enter(value)` path;
- `N` → `toggleNotesMode()`;
- `H` → `hint()`.

The visible number pad and actions remain available.

Routing digit input through `SharedGameState.enter` preserves existing invariants:

- fixed clues cannot be overwritten;
- Notes mode toggles candidates rather than placing a value;
- normal entry retains conflict/correctness status behavior;
- peer-note cleanup remains centralized in shared state.

Real hardware-keyboard/focus behavior remains target-specific runtime QA.

---

## 2.0.14 Version Promotion and Release Contract

Android source metadata is now:

```text
versionCode = 2014
versionName = 2.0.14
```

Compose Desktop native distribution metadata is now:

```text
packageVersion = 2.0.14
```

Ordinary Android CI release verification expects:

```text
versionCode = 2014
versionName = 2.0.14
applicationId = in.sanskar.sudokunova
minSdk = 26
targetSdk = 37
```

Protected Production Release Validation defaults are now:

```text
expected_version_code = 2014
expected_version_name = 2.0.14
```

The existing release-contract guard continues to fail when Android source, ordinary CI, protected defaults, or Desktop package version drift apart.

---

# Documentation Synchronization

Added:

```text
docs/SHARED_SETTINGS.md
docs/V2_0_14_RELEASE.md
```

Updated for the real 2.0.14 source/behavior/evidence boundary:

```text
README.md
CHANGELOG.md
docs/README.md
docs/CROSS_PLATFORM.md
docs/KEYBOARD_SHORTCUTS.md
docs/BUILDING.md
docs/CI_CD.md
docs/PRODUCTION_RELEASE_VALIDATION.md
docs/TESTING.md
what_changed.md
```

`docs/SHARED_SETTINGS.md` is the detailed authority for:

- `SNS1` format and validation;
- common settings state/store interfaces;
- platform adapters;
- restore/autosave ownership;
- compatibility policy;
- privacy/security boundaries;
- tests;
- runtime evidence limits.

`docs/V2_0_14_RELEASE.md` is the current source/release preparation authority.

The documentation hub indexes both new guides, preserving the fail-closed detailed-guide discoverability rule.

The build, CI/CD, testing, and protected-validation guides were explicitly moved off their stale 2.0.12/2.0.13 contracts and now consistently describe 2014 / 2.0.14.

---

# Release Tracking

Created issue #43:

```text
v2.0.14: release validation and production readiness
```

It separates repository exact-head gates from:

- protected signed Android validation;
- Android real-device QA;
- accessibility/adaptive-layout QA;
- representative performance evidence;
- Apple/Desktop/Web runtime/distribution evidence;
- store acceptance and final `SHIP` decision.

Opened draft PR #44:

```text
release: prepare SudokuNova 2.0.14 shared settings parity
```

Base commit:

```text
adabaf9841eadaa172c8a236b2cbe4a6c4ef6699
```

Intermediate workflow runs are diagnostic only. Every later source/documentation commit supersedes their merge evidence.

---

# Atomic 2.0.14 Commit Ledger

## Common settings model, format, and tests

- `00579753881177480c5c95cd87784ec1500a8c51` — `feat(shared-settings): add portable user settings model`
- `1573f023cb3cefabc1e97807aa876fabe11e855d` — `feat(shared-settings): add common settings store boundary`
- `5ed25f8e2e220a850e77e815af7140e7152f7c18` — `feat(shared-settings): add deterministic SNS1 codec`
- `ef2869bad2f7df0aa0b86a528494f447b175ba09` — `test(shared-settings): cover SNS1 settings codec`
- `e01a0a47f5b1593c8d6f07b64653ab4ba6f179b5` — `test(shared-settings): cover encoded settings store`
- `a7b05aa3b6ecef8af5f2b71b4cc6d26d37deb698` — `feat(shared-settings): add observable settings state`
- `82733d50acde875b499456d7ed1357ef39f4486b` — `feat(shared-settings): add Compose restore and autosave state`
- `72eec3f24031b87109e8a32f07d06d20760c55ba` — `test(shared-settings): cover settings state persistence`

## Native adapters

- `81b0943d95f8e5e1f5552277d0a3c29f3495d28b` — `feat(android): add shared settings preferences adapter`
- `b6a3d63d37346f1c659613ce7e3ddf3e15bd4596` — `feat(desktop): add shared settings preferences adapter`
- `76dfea4d9bd62de94db9cb3dbd00a2c9c5a88611` — `feat(web): add shared settings localStorage adapter`
- `e3dbfd24ad2dd8d7a3871374f413c39328c70999` — `feat(ios): add shared settings user-defaults adapter`

## Shared UI, localization, and host wiring

- `45bffbd8638b257ffe178cc86caf5cef4ecd33dc` — `feat(shared-settings): add localized theme controls`
- `7949398f4879326b690bd648838a479a4f64a0c3` — `feat(shared-settings): localize Hindi theme controls`
- `61db48c457a999d65d2982c91cef834c7850b392` — `feat(shared-ui): apply persisted theme and keyboard parity`
- `58c73275737621fb68301fc005e275f4653b2d3f` — `feat(android): restore and persist shared settings`
- `c1d5b17e05dadb5392dfed079250ee643fa71c45` — `feat(desktop): restore and autosave shared settings`
- `ad60c1e0908b9d31f0d9a061c649437a4cac2587` — `feat(web): restore and autosave shared settings`
- `0a5ba469078a8dcec2f1eebbdc046dca23a24b96` — `feat(ios): restore and autosave shared settings`

## Version and CI contract

- `8532b83bf3106c839e447d87443bf150f224a9fb` — `build(android): promote source version to 2.0.14`
- `a674a66cfe911c53f56d49f9a801bf42d3e01a66` — `build(desktop): promote package version to 2.0.14`
- `38b300fd757a21774c5e6c0183e30e8355da8953` — `ci(android): verify 2.0.14 release outputs`
- `30038eb46cc1d87dd36107cfc12d99b475f83958` — `ci(release): promote protected validation defaults to 2.0.14`

## Documentation

- `5db79f4abf15972eafb309be79b3034858c115ed` — `docs(release): add 2.0.14 preparation authority`
- `bdffa7e4e3dad205b1cf0614f9deb6749a54e33f` — `docs(settings): document shared SNS1 persistence contract`
- `70811eaa3a7dfe84d79b8e6896813a40676555f2` — `docs(index): add shared settings and 2.0.14 guides`
- `40e07ee34c3db4daac3bed8065292a3a844640e3` — `docs(input): document shared 2.0.14 keyboard parity`
- `de56179f4bab7e515714d0603f64de9fa9f97983` — `docs(readme): align public status with 2.0.14`
- `be9531532daba183d53ce55146a789b9fbe94fe7` — `docs(cross-platform): add shared settings parity`
- `a2cdef441a06dd96685d1da61309cb8f4a3580a3` — `docs(changelog): record 2.0.14 settings parity`
- `183e4f60ed78042fe6e48f578394f06554fc421e` — `docs(ledger): record complete 2.0.14 preparation work`
- `6ff0a2962ed25c72436a9b1fa9a40fdbfd249e43` — `docs(build): align build guide with 2.0.14`
- `b5f1cdc0465ba0dcb377090007945d8b602faa2f` — `docs(ci): align automated gates with 2.0.14`
- `2368390e13b884f02ac01870d49a57e0e63e72c0` — `docs(release): align protected validation with 2.0.14`
- `74656de3280a21299043c767fcc152ab3ff491b8` — `docs(testing): align coverage with 2.0.14`

This ledger-finalization commit itself becomes the next branch head. It intentionally does not attempt to pre-state its own SHA.

---

# Privacy and Security Properties Preserved

The 2.0.14 settings work adds no network synchronization path and no new third-party dependency.

It introduces no:

- account requirement;
- remote analytics;
- advertising identifier;
- telemetry upload;
- cloud settings database;
- credential storage.

The settings payload is local preference state only. It must never be repurposed for passwords, API tokens, signing keys, keystores, or other secrets.

Existing fail-closed secret/signing checks, artifact identity verification, documentation ownership, translation parity, Sudoku correctness, `SNP1`, and `SNG1` invariants remain in place.

---

# Remaining Repository Work After 2.0.14

## Shared product parity

- common history/saved-puzzle model and local adapters;
- imported/custom puzzle gameplay provenance and persistence;
- platform-safe clipboard/share/file-picker abstractions;
- challenges/custom-puzzle shared presentation;
- learning/statistics shared presentation/persistence;
- behavior for additional portable settings fields where cross-platform semantics are justified.

## Performance and lifecycle

- move expensive portable generation/hint work off render-critical paths where measurement justifies it;
- define common cancellation/lifecycle ownership for long-running work;
- add deterministic shared performance regressions where meaningful;
- collect real platform startup/render/input measurements.

## Real target evidence

- Android production signing and representative physical-device QA;
- TalkBack/VoiceOver/browser/Desktop accessibility validation;
- large-font/high-contrast/reduced-motion checks;
- real hardware keyboard/focus/pointer/touch behavior;
- settings persistence across real target restart/lifecycle/privacy modes;
- Apple app host/signing/provisioning/TestFlight/App Store evidence;
- Windows clean-machine/MSI/signing/reputation evidence;
- macOS DMG/signing/notarization/Gatekeeper evidence;
- Linux DEB clean install/remove/upgrade evidence;
- Web browser support/runtime/accessibility/privacy-mode matrix;
- store/publication evidence.

These cannot be truthfully completed through source code alone.

---

# Final 2.0.14 Repository Merge Gate

After this ledger-finalization commit, freeze the branch except for concrete CI-discovered defects.

The exact final head must independently pass:

1. Android CI;
2. Android Instrumentation;
3. Cross-Platform CI.

Android CI must retain:

- repository security guard;
- release verifier unit/CLI tests;
- documentation-link/coverage regression and direct guards;
- release-contract regression/direct guard;
- translation parity;
- shared engine tests;
- shared gameplay/persistence/settings tests;
- Desktop/Web common compilation;
- Android JVM tests;
- Android instrumentation compilation;
- Macrobenchmark compilation;
- debug/release lint;
- debug APK;
- R8 release APK;
- release AAB;
- exact embedded `in.sanskar.sudokunova` / 2014 / 2.0.14 / minSdk 26 / targetSdk 37 / non-debuggable identity;
- deterministic release evidence.

Cross-Platform CI must retain:

- shared code/tests;
- Android shared integration;
- Web/Wasm production distribution;
- iOS Simulator framework linking;
- Linux Desktop image;
- Windows Desktop image;
- macOS Desktop image.

Do not disable targets, weaken validation, or mix successful runs from different SHAs to make the merge appear green.

After all three workflow families succeed on the same frozen head, record that SHA and the run IDs in PR #44 and issue #43, mark the PR ready, and merge using the verified head.

---

# Evidence Boundary After Merge

A future exact-head verified merge of PR #44 means **repository-prepared 2.0.14**. It does not automatically mean **publicly shipped 2.0.14**.

Issue #43 remains open until real protected signing, device/runtime/accessibility/performance, platform distribution, store, and final `SHIP` evidence is completed.

Do not create an immutable `v2.0.14` tag, GitHub Release, store/public distribution claim, or final SHIP claim solely from repository CI.
