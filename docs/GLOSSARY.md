# SudokuNova Glossary

This glossary standardizes terminology used in code, documentation, issues, tests, and release notes.

## Sudoku Terms

**Board** — The 9×9 Classic Sudoku grid containing 81 cells.

**Cell** — One position in the Sudoku board, identified internally by an index `0..80` and conceptually by row/column.

**Clue / Original clue / Given** — A value present in the initial puzzle and not editable by the player.

**Editable cell** — A non-clue cell the player can solve or annotate.

**Candidate** — A digit that is currently legal for an empty cell under row/column/box constraints.

**Notes** — Player-visible candidate marks. Teaching evidence may describe candidate eliminations without automatically mutating player notes.

**Peer** — A cell sharing a row, column, or 3×3 box with another cell.

**Conflict** — A Sudoku rule violation involving duplicate non-zero values in a row, column, or box.

**Valid board** — A board whose currently placed non-zero values do not violate row/column/box constraints. Validity alone does not prove solvability or uniqueness.

**Solvable puzzle** — A puzzle with at least one complete solution.

**Unique solution** — Exactly one solution exists. SudokuNova requires unique solvability for generated and accepted playable imported/custom puzzles.

**Multiple-solution puzzle** — A puzzle with at least two solutions; rejected from normal custom/import play.

**Puzzle string** — The 81-character serialized board representation, normally digits with `0` for empty.

## Solver and Generator Terms

**Solver** — `SudokuSolver`, which searches for solutions and can count solutions up to a caller-provided bound.

**MRV (Minimum Remaining Values)** — Solver heuristic that chooses an empty cell with the fewest legal candidates to reduce branching.

**Visited node** — A search state counted by solver metrics.

**Guess** — A solver branch where more than one candidate is available.

**Backtrack** — A search path that cannot lead to a solution and is abandoned.

**Maximum depth** — Deepest recursion/search level reached during analysis.

**Generator** — `SudokuGenerator`, which builds a complete grid, removes clues, preserves uniqueness, and rates/calibrates the resulting puzzle.

**Seed** — Deterministic input used to reproduce generation/challenge behavior.

**Difficulty calibration** — Process that combines puzzle characteristics/logical/search evidence to map a puzzle to a supported difficulty target.

**Corpus test** — Deterministic set of puzzles/seeds used to catch regressions across a wider sample than a single unit test.

## Logical Technique Terms

**Teaching step** — Structured logical evidence containing technique, source/target cells, candidate eliminations and/or placement.

**Teaching trace** — Ordered sequence of teaching steps applied to a board/candidate state.

**Naked Single** — An empty cell has exactly one remaining legal candidate.

**Hidden Single** — Within a row, column, or box, one candidate digit appears in only one possible cell.

**Naked Pair** — Two cells in a unit share the same two candidates, allowing those candidates to be removed from other cells in the unit.

**Pointing Pair / Triple** — In one box, all candidates for a digit lie on one row or column, allowing elimination from that line outside the box.

**Box-Line Reduction** — In one row/column, all candidates for a digit lie in one box, allowing elimination from other cells in that box.

**Hidden Pair** — Two digits in a unit can appear only in the same two cells, allowing other candidates to be removed from those cells.

**Naked Triple** — Three cells in a unit have candidates whose union is exactly three digits, allowing elimination from other cells.

**Hidden Triple** — Three digits in a unit are restricted to the same three cells, allowing other candidates to be removed from those cells.

**X-Wing** — Candidate pattern across two rows/two columns that allows eliminations in the corresponding columns/rows.

**Reveal** — Explicit solver-backed hint fallback when the supported logical teaching pipeline cannot produce a placement. Reveal is not a logical technique.

**Candidate elimination** — Structured evidence that a candidate can be removed from a target cell.

**Placement** — A logically or solver-proven final digit for a specific cell.

## Gameplay Terms

**Cell-first input** — Select cell, then choose a number.

**Number-first input** — Select a number first, then use it with a cell according to the current input flow.

**Active game** — Current in-progress game persisted for resume.

**Mistake** — Player placement detected as inconsistent with the authoritative solution when automatic checking is enabled.

**Mistake limit** — Optional configured threshold after which the game can fail.

**Perfect game** — Current data contract treats a completed game as perfect when it has zero mistakes and zero hints used.

**Replay** — Playing a puzzle sourced from a previous history record.

**Replay provenance** — Metadata connecting a replay to its origin so statistics/backup behavior does not treat it as an unrelated first play.

## Challenge Terms

**Daily Challenge** — Deterministic date-keyed challenge.

**Weekly Challenge** — Deterministic week-keyed challenge, separated from Daily seed identity.

**Challenge key** — Stable date/week-derived identifier for a challenge instance.

**Challenge result** — Local record of completed challenge performance.

## Learning Terms

**Lesson view** — Local count indicating a technique lesson was opened/studied.

**Practice attempt** — Recorded submission for a technique practice exercise.

**Practice success** — Correct practice submission.

**Technique mastery** — Derived bounded percentage/state based on lesson exposure and practice behavior.

**Overall mastery** — Aggregate learning progress across supported techniques.

## Data and Transfer Terms

**DataStore** — Android Preferences DataStore used for lightweight settings/state/counters.

**Room** — Android persistence layer used for structured history, saved puzzles and challenge records.

**Migration** — Explicit database transformation from one schema version to another while preserving supported user data.

**SNP1** — Version 1 puzzle-sharing code format.

**SNB1** — Version 1 local backup text format.

**Checksum** — CRC32 integrity/error-detection value used in the share/backup formats. It is not encryption or authentication.

**Fail closed** — Reject malformed/unsupported/unsafe input rather than trying to guess what it meant.

**Bounded parser** — Parser with explicit limits on input bytes/length/records/counters to prevent unreasonable resource usage.

## Android and UI Terms

**Compose** — Jetpack Compose UI toolkit used by SudokuNova.

**Material 3** — Design component/theming system used by the UI.

**Semantic node** — Accessibility/test representation of a Compose element.

**Test tag** — Stable semantic identifier used by connected tests when text/position is not reliable enough.

**High Contrast** — Preference that strengthens board differentiation beyond ordinary theme presentation.

**Reduced Motion** — Preference boundary for minimizing non-essential motion.

**Dynamic color** — Material You color integration on supported Android versions/devices.

## Build and Release Terms

**Debug APK** — Development Android package using the debug build type/application suffix.

**Release APK** — Release build output processed with configured release shrinking/minification; it is not necessarily production-signed.

**AAB** — Android App Bundle used for store-oriented distribution.

**R8** — Android code shrinker/optimizer used in the release build.

**Mapping file** — R8 mapping output used to de-obfuscate stack traces for the exact build.

**Exact-head verification** — Required workflows pass on the precise commit intended for merge/release, with no later unverified commit.

**Release candidate** — Build/commit proposed for release and undergoing final QA; not automatically a published release.

**Release blocker** — Defect that prevents responsible release/merge according to current quality rules.

## Repository Terms

**`main`** — Primary merged repository branch.

**Milestone branch** — Focused development branch for a release/milestone.

**Draft PR** — Pull request intentionally not yet ready for final merge/review status.

**`what_changed.md`** — Detailed evidence/handoff log. It should record only work/results that actually occurred.

**Roadmap** — Planned/completed milestone scope; not proof that a feature exists.

**Changelog** — Release-oriented record of actual notable changes.
