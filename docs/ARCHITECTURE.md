# SudokuNova Architecture

SudokuNova uses a layered Kotlin Multiplatform architecture that preserves the mature Android application while extracting reusable Sudoku truth and portable gameplay presentation for Android, iOS/iPadOS, Desktop, and Web.

The guiding rule is **share correctness and portable interaction where it improves maintainability, but do not force platform-specific capabilities into common code merely to claim symmetry**.

See [`CROSS_PLATFORM.md`](CROSS_PLATFORM.md) for the platform matrix and host build commands, and [`PROJECT_STRUCTURE.md`](PROJECT_STRUCTURE.md) for exact repository paths.

## Module Boundary

### `:sudoku-engine`

Kotlin Multiplatform domain module with no UI or platform-framework dependency.

It owns:

- `SudokuBoard`;
- `SudokuSolver`;
- `SudokuGenerator`;
- difficulty/calibration/logical analysis;
- `LogicalSolver`;
- `TeachingStep` / teaching traces;
- `TeachingStepFinder`;
- `HintEngine`;
- `TeachingPracticeCatalog`;
- puzzle-code behavior.

This module defines Sudoku truth: legality, solving, uniqueness, deterministic generation, logical candidate transformations, teaching evidence, and offline practice evidence.

It must not depend on:

- Android `Context`, resources, lifecycle, Room, DataStore, or navigation;
- Compose UI APIs;
- UIKit/SwiftUI;
- browser DOM APIs;
- Desktop window APIs;
- platform-specific storage or sharing APIs.

Current KMP targets include Android, Desktop JVM, iOS arm64, iOS Simulator arm64, and Web/Wasm.

### `:sharedUI`

Compose Multiplatform module that owns the portable gameplay surface and shared interaction state.

Common responsibilities include:

- puzzle generation through `:sudoku-engine`;
- difficulty selection;
- cell selection;
- number entry;
- candidate notes;
- peer-note cleanup;
- conflict feedback;
- erase;
- bounded undo;
- engine-backed hints;
- reset/new-game behavior;
- responsive Sudoku presentation;
- portable Material 3 controls/status presentation.

The shared UI intentionally does **not** yet replace the mature Android application's full feature set. Android-only history, saved puzzles, Room/DataStore persistence, transfer/backup, navigation, advanced learning surfaces, release tooling, and other platform integrations remain in `:app` until equivalent multiplatform service boundaries are designed and tested.

Platform entry points under this module are thin hosts:

- Desktop window → `desktopMain`;
- iOS/iPadOS Compose `UIViewController` bridge → `iosMain`;
- browser/Wasm viewport → `wasmJsMain`.

### `:app`

The Android application remains SudokuNova's primary production application.

It owns mature Android-specific responsibilities:

- exported launcher activity and splash integration;
- full Android Compose navigation/screens;
- Material theme/design implementation;
- ViewModels and Android lifecycle integration;
- Preferences DataStore;
- Room persistence/history/saved data;
- active-game encoding/restoration;
- Android English/Hindi resources;
- Android accessibility semantics;
- clipboard/share/document-picker flows;
- backup/transfer integration;
- local learning/statistics persistence;
- Android application identity/version/signing/release configuration;
- Android JVM/instrumentation test infrastructure.

`:app` consumes both `:sudoku-engine` and `:sharedUI`.

`MainActivity` remains the exported launcher. `CrossPlatformActivity` is deliberately non-exported and provides an Android host for the shared UI so maintainers can validate portable behavior without replacing mature Android product functionality before parity exists.

### `:macrobenchmark`

Separate Android test module targeting the release-like `:app` benchmark variant.

It owns startup/frame performance instrumentation and remains outside the production application process contract.

Hosted CI compiles this harness; representative performance evidence requires an actual connected physical-device run.

### `iosApp/`

Non-Gradle SwiftUI host source directory for the generated `SudokuNovaSharedUI.framework`.

It contains the minimal Apple bridge from SwiftUI to the Kotlin/Compose `MainViewController`.

The repository source does not pretend that framework linking alone equals a complete distributable iOS/iPadOS application. A real Apple release still needs an Xcode app target/project, bundle identity, assets/capabilities, signing/provisioning, device QA, and store validation.

## Dependency Direction

The intended dependency direction is:

```text
Platform hosts / mature Android application
               ↓
            sharedUI
               ↓
         sudoku-engine
```

Android may also consume `sudoku-engine` directly for feature-rich app paths not yet migrated to `sharedUI`.

The domain engine must never depend upward on UI/platform modules.

## Portable Unidirectional State Flow

The shared gameplay surface follows a simple unidirectional flow:

1. UI emits an intent such as select, enter, note, erase, undo, hint, reset, or new game.
2. `SharedGameState` validates/transforms the action.
3. `sudoku-engine` supplies Sudoku-specific truth/generation/hint reasoning.
4. immutable `SudokuBoard` values and Compose-observable state are updated;
5. Compose re-renders the portable surface.

Fixed clue ownership remains derived from the generated starting puzzle rather than mutable UI flags.

Undo uses bounded snapshots to prevent unbounded history growth.

## Mature Android State Flow

The existing Android application continues to follow unidirectional state principles:

1. Compose emits an intent;
2. a ViewModel validates/transforms it;
3. shared domain code handles Sudoku reasoning where appropriate;
4. immutable UI/domain state changes;
5. Compose observes/render state;
6. persistent data writes through Android repository boundaries.

This architecture can coexist with `SharedGameState` while platform parity is developed incrementally.

## Game Domain State

The mature Android `GameState` contains richer session state such as:

- original puzzle;
- solved board;
- current board;
- notes;
- selection/input mode;
- timer;
- mistakes/hints;
- difficulty and seed;
- pause/status metadata;
- challenge/custom/replay metadata where applicable.

`GameStateCodec` provides explicit versioned persistence. Malformed or unsupported encoded state is rejected rather than partially trusted.

The portable `SharedGameState` currently models the subset necessary for shared gameplay and does not claim persistence parity with Android.

## Teaching and Hint Architecture

Teaching evidence lives in `sudoku-engine`, not UI strings.

`TeachingStep` records structured evidence including:

- `LogicalTechnique`;
- source cells;
- optional source unit;
- target cells;
- exact candidate eliminations;
- optional final placement.

`TeachingStepFinder` maintains deterministic candidate state and searches supported techniques. Candidate eliminations modify internal candidate state; placements produce new immutable boards.

`LogicalSolver` and `HintEngine` consume the same evidence pipeline so logical difficulty, hints, and teaching do not drift into separate rule implementations.

A `SudokuHint` may represent a chain ending in a supported placement. If no supported logical chain reaches a placement, Reveal remains an explicit solver-backed fallback rather than fabricated teaching evidence.

Platform/player-facing prose should be resolved by presentation/localization layers.

See [`LEARNING_AND_HINTS.md`](LEARNING_AND_HINTS.md).

## Learning Architecture

`TeachingPracticeCatalog` stays in the shared engine because practice evidence must be deterministic and platform-neutral.

The mature Android `LearnViewModel` owns Android interaction state and `AppPreferencesRepository` persists lesson/practice progress. Learning progress never changes Sudoku correctness, generation, solving, or history semantics.

When learning UI moves into common code, keep structured technique evidence shared while localization/storage integrations remain behind explicit boundaries.

## Persistence Boundary

### Android Preferences DataStore

Used for lightweight Android-local state such as:

- settings;
- active game;
- aggregate statistics/streaks;
- learning counters.

### Android Room

Used for structured/queryable records such as history, saved/custom puzzles, and challenge-related data.

Persistence rules:

- explicit schema/version boundaries;
- tested migrations before schema changes merge;
- no silent destructive migration where user data should survive;
- imported data treated as untrusted;
- reset actions scoped to the intended domain.

### Shared persistence future boundary

The current shared UI is intentionally ephemeral. If persistence is added cross-platform, define an interface/use-case boundary in common code and implement platform storage adapters separately rather than importing Android persistence APIs into shared code.

See [`DATA_STORAGE.md`](DATA_STORAGE.md).

## Transfer and External Input Boundary

Puzzle codes belong naturally in the shared engine when they are pure validated formats. File picker, clipboard, share-sheet, backup storage, and repository mutations remain platform/application responsibilities.

External input must be validated for version, size, bounds, checksum, and duplicate-safety before it affects local state.

Never assume content is trusted because it arrived through a system picker or share mechanism.

## Platform Host Boundaries

### Android

The platform host can use mature Android services and `sharedUI` together. Shared UI must not acquire Android-only dependencies merely because the Android host needs them.

### iOS/iPadOS

`MainViewController()` exposes Compose through UIKit. SwiftUI host code wraps that controller. Apple-specific services should be implemented through dedicated adapters rather than called directly from common UI/domain code.

### Desktop

The Desktop entry point owns window lifecycle. Native packaging is host-specific even though the Compose UI is shared.

### Web/Wasm

The Web entry point owns browser viewport mounting and static host resources. Browser-specific persistence, clipboard, URL/share, lifecycle, and compatibility behavior should remain behind browser/platform boundaries.

## Dependency Construction

The project currently prefers straightforward construction/composition over a heavy dependency-injection framework. Introduce a DI framework only when the multiplatform/platform service graph becomes complex enough to justify its build/runtime/conceptual cost.

For shared services, constructor-injected interfaces are preferred because they are easy to test and implement per platform.

## Concurrency

Android generation/persistence work must remain off blocking UI paths and lifecycle-aware. Shared code should avoid assuming Android coroutines/lifecycle primitives.

As multiplatform async services are introduced, prefer structured concurrency and explicit scopes owned by host/application lifecycle rather than global work.

## Navigation

The mature Android app owns full Navigation Compose routing across Home, Game, Challenges, Custom Puzzle, History, Saved Puzzles, Learn, Statistics, Settings, Backup/Transfer, About, and other implemented screens.

The initial shared UI intentionally represents a self-contained Sudoku gameplay surface rather than duplicating the Android navigation graph.

Future common navigation should only be introduced when enough feature parity exists to justify it.

## Accessibility Boundary

The engine reports structured facts as indices, values, units, placements, and candidate removals.

Presentation layers turn those facts into:

- visual emphasis;
- localized descriptions;
- platform semantics/focus behavior;
- keyboard/pointer/touch interaction.

Do not put TalkBack/UIKit/browser-specific accessibility APIs into the engine.

Cross-platform accessibility must be verified on real target surfaces; shared source does not guarantee equivalent assistive-technology behavior everywhere.

## Localization Boundary

Android currently has a mature English/Hindi resource parity gate.

The initial portable UI uses a limited common text surface. As shared UI expands, introduce a cross-platform localization strategy rather than duplicating hard-coded prose across source sets. Preserve placeholder semantics and accessibility phrasing when migrating strings.

## Testing Architecture

Correctness is pushed as low in the stack as practical:

- `sudoku-engine:desktopTest` — domain truth;
- `sharedUI:desktopTest` — portable gameplay state;
- shared Desktop/Wasm compilation — common UI compatibility;
- Android JVM tests — app-local pure behavior;
- Android instrumentation — integrated Compose/Room behavior;
- cross-platform hosted builds — Android shared integration, Web distribution, iOS framework, Desktop images;
- repository/release Python guards — deterministic source/release integrity;
- Macrobenchmark compile + physical device execution — performance path;
- manual target QA — accessibility/lifecycle/input/store/distribution evidence.

See [`TESTING.md`](TESTING.md) and [`CI_CD.md`](CI_CD.md).

## Release Architecture

Android production release identity remains isolated in `:app` and its release workflows. Cross-platform build additions must not silently alter:

- application ID `in.sanskar.sudokunova`;
- versionCode `2012`;
- versionName `2.0.12`;
- minSdk 26;
- targetSdk/compileSdk 37;
- fail-closed production signing;
- release artifact verification.

Other platforms will require their own real package/bundle identities, signing, notarization/store metadata, and evidence before a production release is claimed.

## Evolution Rules

Prefer future changes in this order:

1. put reusable Sudoku correctness in `sudoku-engine`;
2. put truly portable gameplay state/presentation in `sharedUI`;
3. define common interfaces for platform services when at least two platforms need the abstraction;
4. implement host-specific adapters in platform source sets/apps;
5. migrate mature Android features only with equivalent tests and no regression in Android behavior;
6. avoid module/interface proliferation without concrete complexity;
7. keep evidence boundaries explicit in docs/CI.

Potential future abstractions may include shared persistence interfaces, navigation, localization, platform sharing/clipboard, adaptive window services, and additional Sudoku variants—but only when their concrete requirements justify them.
