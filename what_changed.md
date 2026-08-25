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
**Desktop package version:** `2.0.14`  
**Android application ID:** `in.sanskar.sudokunova`  
**minSdk:** `26`  
**targetSdk / compileSdk:** `37`  
**JDK/JVM:** `17`  
**Android Gradle Plugin:** `9.3.1`  
**Kotlin:** `2.4.10`  
**Compose Multiplatform:** `1.11.1`  
**Room:** `2.8.4`  
**License:** MIT

This is the active implementation/handoff ledger. The 2.0.14 line starts only after the 2.0.13 preparation branch was exact-head verified and merged. Historical details remain available through Git history, merged PRs, `CHANGELOG.md`, `docs/V2_0_13_RELEASE.md`, `docs/V2_0_12_RELEASE.md`, and `docs/archive/`.

Exact-head evidence applies only to the commit SHA that was actually tested. PR #44 must not merge until Android CI, Android Instrumentation, and Cross-Platform CI are all green on the same final head after every source/documentation commit.

---

## Verified 2.0.13 Baseline Merged Before This Work

PR #41 final verified head:

```text
61371eab0c22197ffd4250805cf58caa6425418f
```

Exact-head workflow evidence:

- Android CI #1013 / run `32733975637` — SUCCESS;
- Android Instrumentation #384 / run `32733975646` — SUCCESS;
- Cross-Platform CI #118 / run `32733975747` — SUCCESS.

PR #41 was merged to `main` as:

```text
adabaf9841eadaa172c8a236b2cbe4a6c4ef6699
```

That baseline already included:

- 2013 / 2.0.13 Android source metadata;
- Desktop packageVersion 2.0.13;
- deterministic shared grid navigation;
- focusable shared arrow-key navigation plus Backspace/Delete erase;
- Notes selected-state semantics;
- `PuzzleExchangeService` around `SNP1`;
- unique-solution imported-puzzle acceptance;
- release-contract synchronization including Desktop package version;
- the previously merged KMP/Compose foundation and `SNG1` active-game persistence line.

No earlier 2.0.12/2.0.13 workflow result is final evidence for 2.0.14.

---

## 2.0.14 Shared User-Settings Foundation

### Portable settings model

Added:

```text
sharedUI/src/commonMain/kotlin/com/sanskar/sudokunova/shared/SharedUserSettings.kt
```

The portable model now contains:

- `SharedTheme`:
  - `SYSTEM`;
  - `LIGHT`;
  - `DARK`;
- `SharedInputMode`:
  - `CELL_FIRST`;
  - `NUMBER_FIRST`;
- `SharedUserSettings` fields for:
  - theme;
  - dynamic color preference;
  - input mode;
  - peer highlighting;
  - same-number highlighting;
  - automatic mistake checking;
  - automatic note removal;
  - timer visibility;
  - haptics;
  - sounds;
  - reduced motion;
  - high contrast;
  - mistake limit.

Mistake limits fail closed outside the mature supported values:

```text
0 = unlimited
3
5
```

The model intentionally mirrors portable preference vocabulary without claiming every field already changes behavior on every shared target.

### Common settings storage boundary

Added:

```text
SharedSettingsPersistence.kt
```

It defines:

- `SHARED_SETTINGS_STORAGE_KEY = "sudokunova.shared.settings.v1"`;
- `SharedSettingsStore` for typed settings load/save/clear;
- `SharedSettingsTextStore` for the minimal native text-storage boundary;
- `EncodedSharedSettingsStore` to compose native text storage with the common codec.

No Android `Context`, DataStore, Java preferences, browser DOM, or Foundation type is imported into this common interface layer.

---

## Deterministic `SNS1` Settings Format

Added:

```text
SharedSettingsCodec.kt
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

Default encoding is pinned by regression test to:

```text
SNS1|theme=SYSTEM|dynamicColor=1|inputMode=CELL_FIRST|highlightPeers=1|highlightSameNumbers=1|autoCheckMistakes=1|autoRemoveNotes=1|showTimer=1|haptics=1|sounds=0|reducedMotion=0|highContrast=0|mistakeLimit=3
```

Validation rules include:

- payload must not be empty;
- maximum payload length is 512 characters;
- version must be exactly `SNS1`;
- field count must be exact;
- every field must be a valid key/value pair;
- unknown keys are rejected;
- duplicate keys are rejected;
- missing fields are rejected;
- booleans are exactly `0` or `1`;
- enum values must be known;
- mistake limits must pass model validation.

`SNS1` is a compatibility/validation format, not encryption and not a secret store.

---

## Shared Settings Regression Coverage

Added:

```text
sharedUI/src/commonTest/kotlin/com/sanskar/sudokunova/shared/SharedSettingsCodecTest.kt
sharedUI/src/commonTest/kotlin/com/sanskar/sudokunova/shared/EncodedSharedSettingsStoreTest.kt
sharedUI/src/commonTest/kotlin/com/sanskar/sudokunova/shared/SharedSettingsStateTest.kt
```

Coverage includes:

- exact deterministic default encoding;
- non-default settings round trips;
- unsupported version rejection;
- malformed boolean rejection;
- unknown enum rejection;
- invalid mistake-limit rejection;
- missing field rejection;
- extra/duplicate field rejection;
- oversized payload rejection;
- typed encoded-store save/load/clear;
- corrupt stored payload fail-closed behavior;
- observable settings update/replace;
- settings save/restore/clear behavior;
- nullable synchronous suspend-test completion without confusing a successful `null` result with an incomplete coroutine.

---

## Observable Shared Settings State

Added:

```text
SharedSettingsState.kt
PersistedSharedSettingsState.kt
```

`SharedSettingsState` owns observable common settings and supports:

- replace;
- immutable-transform update;
- restore from `SharedSettingsStore`;
- save to store;
- clearing stored settings.

`rememberPersistedSharedSettingsState`:

1. starts with safe default settings;
2. attempts one restore;
3. catches storage/decode failure without making the UI unusable;
4. marks restore complete;
5. autosaves later observable settings changes.

This intentionally mirrors the resilient ownership model used by shared active-game persistence.

---

## Native Local Settings Adapters

### Android staged shared host

Added:

```text
app/src/main/java/com/sanskar/sudokunova/CrossPlatformSharedPreferencesSettingsTextStore.kt
```

Behavior:

- private `SharedPreferences` file `sudokunova_shared_settings`;
- key defaults to `sudokunova.shared.settings.v1`;
- local read/write/clear only.

The mature Android launcher retains its existing DataStore settings model. This staged shared adapter does not silently replace or migrate mature Android DataStore state.

### Desktop JVM

Added:

```text
sharedUI/src/desktopMain/kotlin/com/sanskar/sudokunova/shared/desktop/DesktopPreferencesSettingsTextStore.kt
```

Uses:

```text
Preferences.userRoot().node("in/sanskar/sudokunova")
```

and flushes writes/removals.

### Web/Wasm

Added:

```text
sharedUI/src/wasmJsMain/kotlin/com/sanskar/sudokunova/shared/web/WebLocalStorageSettingsTextStore.kt
```

Uses browser `localStorage` with the shared settings key.

Browser private mode, quota, site-data clearing, and compatibility behavior remain real runtime evidence requirements.

### iOS/iPadOS

Added:

```text
sharedUI/src/iosMain/kotlin/com/sanskar/sudokunova/shared/ios/AppleUserDefaultsSettingsTextStore.kt
```

Uses:

```text
NSUserDefaults.standardUserDefaults
```

Framework compilation does not prove physical-device lifecycle, backup, signing, provisioning, TestFlight, or App Store behavior.

---

## Host Integration

Updated Android staged shared host:

```text
CrossPlatformActivity.kt
```

It now owns both:

- `EncodedSharedGameStore` + `SharedGameState`;
- `EncodedSharedSettingsStore` + `SharedSettingsState`.

Game and settings restoration occurs from activity lifecycle scope. Both are saved on `onStop`; settings storage failures remain best-effort and do not make gameplay unusable.

Updated Desktop entry point:

```text
sharedUI/src/desktopMain/.../Main.kt
```

It now remembers both game and settings encoded stores, restores/autosaves both states, and passes both to the shared app.

Updated Web entry point:

```text
sharedUI/src/wasmJsMain/.../Main.kt
```

It now composes `localStorage` game and settings stores independently and passes both restored states to the shared app.

Updated Apple Compose bridge:

```text
sharedUI/src/iosMain/.../MainViewController.kt
```

It now composes `NSUserDefaults` game and settings stores independently and passes both restored states to the shared app.

---

## Shared Theme Behavior

Updated:

```text
SudokuNovaSharedApp.kt
```

The shared app now accepts:

```text
state: SharedGameState
settingsState: SharedSettingsState
```

Theme behavior:

- `SYSTEM` follows `isSystemInDarkTheme()`;
- `LIGHT` applies a shared Material 3 light scheme;
- `DARK` applies a shared Material 3 dark scheme.

A localized theme picker exposes System/Light/Dark and persists changes through `SharedSettingsState`.

The stored `dynamicColor` preference is deliberately not represented as cross-platform Material You behavior. Android-only dynamic color is not falsely generalized to Desktop/Web/Apple targets.

### Shared localization parity

Updated both:

```text
sharedUI/src/commonMain/composeResources/values/strings.xml
sharedUI/src/commonMain/composeResources/values-hi/strings.xml
```

Added parity-matched keys for:

- Theme;
- System;
- Light;
- Dark.

The existing translation parity guard remains authoritative for key/placeholder synchronization.

---

## Shared Keyboard Parity Expansion

2.0.13 already provided:

- Up/Down/Left/Right deterministic grid movement;
- Backspace/Delete erase.

2.0.14 adds common key-down mappings for:

- `1` through `9` → existing `SharedGameState.enter(value)` path;
- `N` → `toggleNotesMode()`;
- `H` → `hint()`.

The visible number pad, Notes, Hint, and Erase controls remain available. Keyboard access is an enhancement, not an exclusive input path.

Because digit keys use the existing state method, current invariants remain centralized:

- fixed clues cannot be overwritten;
- Notes mode toggles candidate notes rather than placing a value;
- normal entry uses existing conflict/correctness status rules;
- peer-note cleanup remains in shared game state.

Real target hardware-keyboard/focus behavior still requires Android/Desktop/Web/Apple runtime QA.

---

## 2.0.14 Version Promotion

Updated Android source metadata:

```text
versionCode = 2014
versionName = 2.0.14
```

Updated Compose Desktop native distribution metadata:

```text
packageVersion = 2.0.14
```

Updated ordinary Android CI expected release identity:

```text
expected versionCode = 2014
expected versionName = 2.0.14
```

Updated protected Production Release Validation defaults:

```text
expected_version_code = 2014
expected_version_name = 2.0.14
```

The existing release-contract guard continues to require Android source identity, ordinary CI expectations, protected workflow defaults, and Desktop packageVersion to remain synchronized.

---

## 2.0.14 Documentation Added / Updated

Added:

```text
docs/SHARED_SETTINGS.md
docs/V2_0_14_RELEASE.md
```

Updated:

```text
README.md
CHANGELOG.md
docs/README.md
docs/CROSS_PLATFORM.md
docs/KEYBOARD_SHORTCUTS.md
what_changed.md
```

`docs/SHARED_SETTINGS.md` documents:

- portable model boundaries;
- `SNS1` exact format and validation;
- platform adapters;
- restore/autosave ownership;
- privacy/security boundaries;
- compatibility policy;
- automated coverage;
- production/runtime evidence limits.

`docs/V2_0_14_RELEASE.md` is the current source/release preparation authority and explicitly separates repository preparation from production signing/device/store/publication evidence.

The documentation index now includes both new detailed guides so the fail-closed detailed-guide discoverability rule remains satisfiable.

---

## Release / Tracking State

Created issue #43:

```text
v2.0.14: release validation and production readiness
```

It separates:

- repository exact-head gates;
- protected Android signed-release evidence;
- Android real-device QA;
- accessibility/adaptive behavior;
- performance evidence;
- Apple/Desktop/Web runtime/distribution evidence;
- final store/distribution `SHIP` decision.

Opened draft PR #44:

```text
release: prepare SudokuNova 2.0.14 shared settings parity
```

Base:

```text
adabaf9841eadaa172c8a236b2cbe4a6c4ef6699
```

PR #44 remains draft while the branch changes. Intermediate workflow runs are diagnostic only; they cannot become final merge evidence after a later commit.

---

## Atomic Commit Ledger — 2.0.14 Work So Far

The branch intentionally uses focused commits rather than one opaque mega-commit.

### Common settings model / format / tests

- `00579753881177480c5c95cd87784ec1500a8c51` — `feat(shared-settings): add portable user settings model`
- `1573f023cb3cefabc1e97807aa876fabe11e855d` — `feat(shared-settings): add common settings store boundary`
- `5ed25f8e2e220a850e77e815af7140e7152f7c18` — `feat(shared-settings): add deterministic SNS1 codec`
- `ef2869bad2f7df0aa0b86a528494f447b175ba09` — `test(shared-settings): cover SNS1 settings codec`
- `e01a0a47f5b1593c8d6f07b64653ab4ba6f179b5` — `test(shared-settings): cover encoded settings store`
- `a7b05aa3b6ecef8af5f2b71b4cc6d26d37deb698` — `feat(shared-settings): add observable settings state`
- `82733d50acde875b499456d7ed1357ef39f4486b` — `feat(shared-settings): add Compose restore and autosave state`
- `72eec3f24031b87109e8a32f07d06d20760c55ba` — `test(shared-settings): cover settings state persistence`

### Native local adapters

- `81b0943d95f8e5e1f5552277d0a3c29f3495d28b` — `feat(android): add shared settings preferences adapter`
- `b6a3d63d37346f1c659613ce7e3ddf3e15bd4596` — `feat(desktop): add shared settings preferences adapter`
- `76dfea4d9bd62de94db9cb3dbd00a2c9c5a88611` — `feat(web): add shared settings localStorage adapter`
- `e3dbfd24ad2dd8d7a3871374f413c39328c70999` — `feat(ios): add shared settings user-defaults adapter`

### Shared UI / localization / host wiring

- `45bffbd8638b257ffe178cc86caf5cef4ecd33dc` — `feat(shared-settings): add localized theme controls`
- `7949398f4879326b690bd648838a479a4f64a0c3` — `feat(shared-settings): localize Hindi theme controls`
- `61db48c457a999d65d2982c91cef834c7850b392` — `feat(shared-ui): apply persisted theme and keyboard parity`
- `58c73275737621fb68301fc005e275f4653b2d3f` — `feat(android): restore and persist shared settings`
- `c1d5b17e05dadb5392dfed079250ee643fa71c45` — `feat(desktop): restore and autosave shared settings`
- `ad60c1e0908b9d31f0d9a061c649437a4cac2587` — `feat(web): restore and autosave shared settings`
- `0a5ba469078a8dcec2f1eebbdc046dca23a24b96` — `feat(ios): restore and autosave shared settings`

### Version / CI contract

- `8532b83bf3106c839e447d87443bf150f224a9fb` — `build(android): promote source version to 2.0.14`
- `a674a66cfe911c53f56d49f9a801bf42d3e01a66` — `build(desktop): promote package version to 2.0.14`
- `38b300fd757a21774c5e6c0183e30e8355da8953` — `ci(android): verify 2.0.14 release outputs`
- `30038eb46cc1d87dd36107cfc12d99b475f83958` — `ci(release): promote protected validation defaults to 2.0.14`

### Documentation

- `5db79f4abf15972eafb309be79b3034858c115ed` — `docs(release): add 2.0.14 preparation authority`
- `bdffa7e4e3dad205b1cf0614f9deb6749a54e33f` — `docs(settings): document shared SNS1 persistence contract`
- `70811eaa3a7dfe84d79b8e6896813a40676555f2` — `docs(index): add shared settings and 2.0.14 guides`
- `40e07ee34c3db4daac3bed8065292a3a844640e3` — `docs(input): document shared 2.0.14 keyboard parity`
- `de56179f4bab7e515714d0603f64de9fa9f97983` — `docs(readme): align public status with 2.0.14`
- `be9531532daba183d53ce55146a789b9fbe94fe7` — `docs(cross-platform): add shared settings parity`
- `a2cdef441a06dd96685d1da61309cb8f4a3580a3` — `docs(changelog): record 2.0.14 settings parity`

This ledger update itself becomes a later commit and therefore changes the exact PR head again.

---

## Privacy / Security Properties Preserved

The 2.0.14 settings work adds no network synchronization path.

It introduces no:

- account dependency;
- remote analytics;
- advertising identifier;
- telemetry upload;
- cloud settings database;
- credential storage.

The settings payload is local preference state only. It must not be used for passwords, API tokens, signing keys, keystores, or other secrets.

Existing repository security, fail-closed signing, artifact identity, documentation ownership, and translation guards remain in place.

---

## What Is Still Intentionally Incomplete

Repository work still remains after this 2.0.14 settings slice, including:

### Shared product parity

- common history/saved-puzzle model and local adapters;
- custom/imported puzzle gameplay provenance and persistence;
- platform-safe clipboard/share/file-picker abstractions;
- challenges/custom-puzzle shared presentation;
- learning/statistics shared presentation/persistence;
- behavioral implementation of additional portable settings fields where cross-platform semantics are justified.

### Performance / lifecycle

- move expensive portable generation/hint work off render-critical paths where measurements justify it;
- common cancellation/lifecycle ownership for long-running work;
- deterministic shared performance regressions where meaningful;
- real platform startup/render/input measurements.

### Real target evidence

- Android production signing and physical-device QA;
- TalkBack/VoiceOver/browser/Desktop accessibility validation;
- representative large-font/high-contrast/reduced-motion checks;
- real hardware keyboard/focus/pointer/touch behavior;
- settings persistence across real target lifecycle/restart/privacy modes;
- Apple app host/signing/provisioning/TestFlight/App Store evidence;
- Windows clean-machine/MSI/signing/reputation evidence;
- macOS DMG/signing/notarization/Gatekeeper evidence;
- Linux DEB clean install/remove/upgrade evidence;
- Web browser support/runtime/accessibility/privacy-mode matrix;
- store/publication evidence.

These cannot be truthfully completed by source changes alone.

---

## Final 2.0.14 Merge Gate — Pending

At the time of this ledger update, PR #44 is still changing and no workflow run is final evidence yet.

The final exact PR head must pass:

1. Android CI;
2. Android Instrumentation;
3. Cross-Platform CI.

Android CI must cover, among other existing gates:

- repository security guard;
- release-verifier tests;
- documentation-link tests/direct verification;
- documentation-coverage tests/direct verification;
- release-contract tests/direct verification;
- translation parity;
- shared engine tests;
- shared gameplay/settings tests;
- Desktop/Web shared compile;
- Android JVM tests;
- instrumentation compilation;
- Macrobenchmark compilation;
- debug/release lint;
- debug APK;
- R8 release APK;
- release AAB;
- exact embedded `in.sanskar.sudokunova` / 2014 / 2.0.14 / minSdk 26 / targetSdk 37 / non-debuggable release identity;
- SHA-256/evidence outputs.

Cross-Platform CI must continue to cover:

- shared code/tests;
- Android shared integration;
- Web/Wasm production distribution;
- iOS Simulator framework linking;
- Linux Desktop application image;
- Windows Desktop application image;
- macOS Desktop application image.

Only after all three workflow families succeed on one exact frozen head may PR #44 be marked ready and considered for merge.

---

## Evidence Boundary After Repository Merge

Even a future exact-head verified PR #44 merge will mean **repository-prepared 2.0.14**, not automatically **publicly shipped 2.0.14**.

Issue #43 remains open for real protected signing, device/runtime/accessibility/performance, platform distribution, store, and final SHIP evidence.

Do not create an immutable `v2.0.14` tag, GitHub Release, store/public distribution claim, or final SHIP claim until those real gates are explicitly satisfied.
