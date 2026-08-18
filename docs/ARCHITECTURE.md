# Architecture

SudokuNova uses a small modular architecture centered on a platform-independent Sudoku domain engine and an Android application layer. The project prefers explicit boundaries and deterministic tests over module count.

## Module Boundary

### `sudoku-engine`

Pure Kotlin/JVM logic with no Android dependency:

- `SudokuBoard`
- `SudokuSolver`
- `SudokuGenerator`
- `Difficulty` / logical difficulty analysis
- `LogicalSolver`
- `TeachingStep` / `TeachingTrace`
- `TeachingStepFinder`
- `HintEngine`
- `TeachingPracticeCatalog`

This module owns Sudoku truth: legality, solving, uniqueness, deterministic generation, logical candidate transformations, teaching evidence, and offline practice evidence. It does not own Android resources, localized prose, Compose state, persistence, or platform sharing APIs.

### `app`

Android-specific responsibilities:

- Main activity and splash integration
- Compose navigation and screens
- Material 3 theme/design system
- ViewModels and lifecycle-aware UI state
- Preferences DataStore
- Room-backed structured local records
- Active-game encoding/restoration
- Android localization resources
- Android accessibility semantics
- Clipboard/share/document-picker flows
- Local learning-progress persistence
- Application metadata and release packaging

## Unidirectional State Flow

The app follows unidirectional state principles:

1. Compose emits an intent such as selecting a cell, entering a number, requesting a hint, answering practice, or changing a setting.
2. A ViewModel validates or transforms the action.
3. Domain code handles Sudoku-specific reasoning where appropriate.
4. Immutable UI/domain state is updated.
5. Compose observes state and renders it.
6. Persistent data is written through repository boundaries.

The board and engine types remain immutable at public boundaries, reducing accidental mutation and making deterministic comparison practical in tests.

## Game Domain State

`GameState` contains the playable session state, including:

- Original puzzle
- Solved board
- Current board
- Notes per cell
- Selected cell and selected number
- Notes mode
- Timer value
- Mistakes and hints used
- Difficulty and seed
- Pause/status metadata
- Challenge identity where applicable
- Custom/replay metadata where applicable

`GameStateCodec` provides an explicit versioned encoding for active-game persistence. Malformed or unsupported encoded state is rejected instead of being partially trusted.

## Teaching and Hint Architecture

v0.8 adds a correctness-sensitive teaching pipeline inside `sudoku-engine`.

`TeachingStep` is structured evidence rather than display text. A step records:

- the `LogicalTechnique`;
- source cells;
- an optional row/column/box source unit;
- target cells;
- exact candidate eliminations;
- an optional final placement.

`TeachingStepFinder` maintains a deterministic candidate state and searches supported techniques from simpler to more advanced. Eliminations mutate only the internal candidate state; placements produce a new immutable `SudokuBoard` and remove that value from peers.

`LogicalSolver` and `HintEngine` consume the same teaching-step pipeline. This prevents the difficulty analyzer, hint system, and learning UI from drifting into separate implementations of the same Sudoku rules.

A `SudokuHint` may contain a chain of teaching steps ending in a supported placement. Its displayed technique is the hardest technique in the chain, while the applied game action remains the final placement. If no supported logical chain reaches a placement, Reveal remains an explicit solver-backed fallback and does not fabricate teaching evidence.

Player-facing names/explanations are resolved only in the Android layer from localized resources.

See [LEARNING_AND_HINTS.md](LEARNING_AND_HINTS.md).

## Learning Architecture

`TeachingPracticeCatalog` lives in the engine because practice evidence must remain deterministic and platform-independent. Exercises carry structured `TeachingStep` evidence and technique choices, not prose.

`LearnViewModel` owns Android interaction state:

- selected technique;
- current deterministic practice exercise;
- unanswered/answered result state;
- lesson-view and practice-recording actions.

`LearningProgress` is a pure app model derived from local counters. `AppPreferencesRepository` persists per-technique lesson views, attempts, and successes in Preferences DataStore. Learning progress never affects puzzle correctness, generation, solving, or game history.

## Persistence

### Preferences DataStore

Used for lightweight key/value state:

- User settings
- Active game
- Aggregate statistics/streak data
- v0.8 per-technique learning counters

### Room

Used for structured/queryable local records such as history, saved/custom puzzles, and challenge-related data introduced by later milestones.

Persistence rules:

- explicit schema/version boundaries;
- tested migrations before schema changes merge;
- no silent destructive migration;
- imported data treated as untrusted;
- reset actions scoped to the requested data domain.

See [DATA_STORAGE.md](DATA_STORAGE.md).

## Safe Transfer Boundary

Puzzle codes, imports, exports, and backups introduced in v0.7 are application-layer services because they interact with Android clipboard/share/document APIs and local repositories.

The parser/validation path must enforce version, size, bounds, checksum, and duplicate-safety rules before imported values affect local state. External text is never assumed trustworthy simply because Android delivered it through a system picker or share intent.

## Dependency Construction

The current project uses straightforward constructor/local composition instead of a dependency-injection framework. Hilt or another DI framework should be introduced only when the object graph becomes complex enough to justify the build and conceptual overhead.

## Coroutines

Puzzle generation and persistence work are kept off blocking UI paths. ViewModels use `viewModelScope`; no `GlobalScope` is used. Long-running generation, import, or future benchmark work should remain cancellable and lifecycle-aware.

## Navigation

Navigation Compose connects the implemented product surfaces including Home, Game, Challenges, Custom Puzzle, History, Saved Puzzles, Learn, Statistics, Settings, and About. Route/state inputs are validated before they become playable Sudoku state.

## Accessibility Boundary

The engine reports evidence as indices, values, units, and candidate removals. The Android layer converts that data into both visual emphasis and localized accessibility descriptions. This keeps domain logic independent of TalkBack/Compose while ensuring source/target/elimination meaning is not conveyed by color alone.

## Testing Strategy

The architecture is designed so the most correctness-sensitive work is covered below the UI layer:

- engine unit tests for board/solver/generator/logical techniques;
- solution-safety corpus tests for teaching traces;
- controlled-candidate tests for advanced eliminations;
- practice-catalog determinism tests;
- app JVM tests for pure state/progress models;
- Android instrumentation for Room and critical Compose flows;
- CI translation parity, lint, debug build, and Android-test compilation;
- API-35 connected tests as a pull-request gate.

## Future Evolution

Likely future boundaries should be added only when justified by real complexity:

- reusable variant-rule abstractions;
- richer repository/use-case layers for release-scale data flows;
- a design-system package if component duplication becomes material;
- optional sync isolated behind explicit privacy-aware interfaces;
- additional platform clients reusing `sudoku-engine`.

The project should not split into excessive modules solely to match an architectural diagram.
