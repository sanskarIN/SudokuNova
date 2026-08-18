# SudokuNova v1.0 Release Notes Source

This is the canonical draft source for the first stable release notes. It may be shortened for GitHub/store surfaces, but it must not be expanded with features that are not implemented in the final release commit.

Do **not** publish these as final stable notes while the build remains `1.0.0-rc.1` or required production/manual evidence is pending.

## SudokuNova 1.0 — Think. Solve. Master the Grid.

SudokuNova is an offline-first, open-source Classic 9×9 Sudoku app for Android built with Kotlin, Jetpack Compose, Material 3, and a platform-independent Sudoku engine.

### Core gameplay

- Seven supported difficulty levels from Beginner through Extreme.
- Deterministic puzzle generation with validity and unique-solution verification.
- Cell-first and Number-first input modes.
- Notes, erase, undo, redo, pause, restart, timer, mistake controls, autosave, and resume.
- Hardware-keyboard navigation and input support.
- Light, Dark, System, Material You dynamic color, High Contrast, and Reduced Motion preferences.

### Smart hints and learning

- Structured logical teaching evidence for Naked Single, Hidden Single, Naked Pair, Pointing Pair/Triple, Box-Line Reduction, Hidden Pair, Naked Triple, Hidden Triple, and X-Wing.
- Hint source, target, candidate-elimination, and placement evidence.
- Reveal kept explicitly separate from logical techniques.
- Offline Learn center with lessons, deterministic technique practice, progress, and mastery tracking.

### Challenges and custom Sudoku

- Deterministic Daily Challenge and Weekly Challenge flows.
- Challenge archive/performance records.
- Custom Puzzle editor with contradiction, solvability, and unique-solution validation.
- Save and replay custom puzzles.

### Local history and saved puzzles

- Room-backed completed-game History.
- Saved Puzzles and Favorites.
- Replay provenance and statistics safeguards.
- Local statistics, streaks, and achievements.

### Sharing, import, export, and backup

- Versioned `SNP1` puzzle codes with bounds/checksum validation.
- Unique-solution validation before imported play.
- Clipboard/share/document-picker flows using explicit user actions.
- Versioned `SNB1` local backup/restore with bounds, checksum, and duplicate-safe merge behavior.
- No broad storage permission is required.

### Accessibility and localization

- English and Hindi maintained resources with CI parity checks.
- Semantic Sudoku cell coordinate/value/clue/conflict/selection information.
- Semantic teaching evidence for hints.
- Selected-state semantics for important persistent controls.
- Large-text/adaptive source hardening across major screens.
- Hardware keyboard support.

Real TalkBack, representative 200% font/device/window, high-contrast, reduced-motion and keyboard evidence remains a stable-release requirement until actually recorded.

### Privacy and security

The open-source base app is designed to work without:

- an account or login;
- advertising;
- analytics SDKs;
- a SudokuNova-operated cloud backend.

Settings, gameplay state, learning progress, History, Saved Puzzles, and challenge records are stored locally using Preferences DataStore and Room. User-controlled backup files are integrity-checked but are not encrypted.

The current base manifest declares no runtime permissions. Repository CI checks for common committed signing/private-key/credential material.

The v1.0 release pipeline also adds:

- all-or-nothing secret-backed production signing configuration;
- a CI regression that rejects partial signing setup;
- release APK/AAB archive-structure checks;
- exact release version-metadata checks;
- non-empty R8 mapping verification;
- SHA-256/byte-size release evidence;
- optional mandatory APK/AAB signature verification for protected signed-release validation.

### Open source and project health

- MIT licensed.
- Complete build, testing, architecture, privacy, security, accessibility, data-format, release, and maintainer documentation.
- Structured issue forms and pull-request template.
- Dependabot for Gradle/GitHub Actions.
- CODEOWNERS and optional funding metadata.
- Generated GitHub release-note category configuration.

## Minimum compatibility

- Android API 26 or newer.

## Stable verification fields

Fill these only from the exact approved stable release evidence:

- Release commit: `PENDING`
- Tag: `PENDING`
- versionCode: `PENDING`
- versionName: `PENDING`
- Android CI run: `PENDING`
- API-35 connected instrumentation run: `PENDING`
- APK SHA-256: `PENDING`
- AAB SHA-256: `PENDING`
- R8 mapping SHA-256: `PENDING`
- APK signature verification/certificate identity: `PENDING`
- AAB signature/distribution validation: `PENDING`
- Manual release QA: `PENDING`
- Store/publication status: `PENDING`

## Known limitations

Only list confirmed release limitations here. Do not invent or hide known blockers.

Current stable-release blockers/evidence gaps are tracked in issue #5, `V1_RELEASE_EVIDENCE.md`, and `V1_RELEASE_CANDIDATE.md` until actually completed.
