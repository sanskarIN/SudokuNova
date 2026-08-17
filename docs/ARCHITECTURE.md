# Architecture

SudokuNova uses a deliberately small modular architecture for the current milestone. The goal is separation of concerns without creating modules that add more maintenance cost than value.

## Module Boundary

### `sudoku-engine`

Pure Kotlin/JVM logic with no Android dependency:

- `SudokuBoard`
- `SudokuSolver`
- `SudokuGenerator`
- `Difficulty` / `DifficultyRater`
- `HintEngine`

This boundary makes the most correctness-sensitive algorithms fast to unit-test and potentially reusable by future platforms.

### `app`

Android-specific responsibilities:

- Main activity and splash integration
- Compose navigation and screens
- Material theme/design foundation
- ViewModels and UI state
- Local DataStore persistence
- Active-game encoding/restoration
- Statistics/settings UI
- Android resources and application metadata

## State Flow

The app follows unidirectional state principles:

1. UI emits an intent such as select cell, enter number, erase, request hint, or change setting.
2. A ViewModel validates/transforms the action.
3. Immutable state is updated.
4. Compose observes the state and renders it.
5. Persistent state is written through `AppPreferencesRepository` where appropriate.

The board and engine types are immutable at public boundaries, reducing accidental mutation bugs.

## Game State

`GameState` contains:

- Original puzzle
- Solved board
- Current board
- Notes per cell
- Selected cell
- Notes mode
- Timer value
- Mistakes
- Hints used
- Difficulty and seed
- Pause/status state
- Daily Challenge marker

`GameStateCodec` provides a versioned text encoding for current active-game persistence. Invalid/malformed encoded data is rejected instead of being trusted blindly.

## Persistence

Current persistence uses Preferences DataStore for:

- User settings
- Active game
- Aggregate statistics/streak data

A structured database is planned when history, saved/favorite puzzles, and richer challenge archives require relational/queryable storage. It should not be introduced merely for architectural appearance.

## Dependency Injection

The current code uses straightforward constructor/local composition rather than Hilt. Dependency injection may be introduced when object graph complexity justifies it; until then, simpler composition reduces build and conceptual overhead.

## Coroutines

Puzzle generation is dispatched away from the main thread. ViewModels use `viewModelScope`; no `GlobalScope` is used. Long-running future generator/benchmark work should remain cancellable and lifecycle-aware.

## Navigation

Navigation Compose currently connects Home, Game, Custom Puzzle, Learn, Statistics, Settings, and About. Route inputs include difficulty, daily/resume flags, and custom-puzzle data.

External input/deep-link support is not yet exposed as a production feature; when added, all external puzzle data must be validated before use.

## Future Evolution

As features grow, likely boundaries include:

- Structured history/challenge persistence
- Repositories/use cases for complex domain flows
- A reusable design-system package only if repetition warrants it
- Variant rules separated at engine/domain boundaries
- Export/import services with strict schema validation

The project should not split into excessive modules solely to match a diagram.
