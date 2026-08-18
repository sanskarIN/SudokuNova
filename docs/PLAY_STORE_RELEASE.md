# Play Store Release Preparation

This document prepares SudokuNova for a future stable Android store release. It intentionally avoids claiming that Play Console submission, policy review, production signing, device QA, or publication has already happened.

Always review the current Play Console requirements at release time because store policies, declarations, screenshots, target-API requirements, and form wording can change independently of this repository.

## Product identity

- App name: **SudokuNova**
- Tagline: **Think. Solve. Master the Grid.**
- Application ID: `in.sanskar.sudokunova`
- Developer credit: **Made by the Sanskar**
- Support email: `supportramsandesh@gmail.com`
- Business email: `sanskarin@outlook.in`
- Repository: `https://github.com/sanskarIN/SudokuNova`
- License: MIT

## Suggested store title

`SudokuNova – Offline Sudoku`

Keep the final title within the store's current title limit.

## Suggested short description

`Modern offline Sudoku with smart hints, challenges, learning, stats and custom puzzles.`

Recheck the current short-description character limit before publishing.

## Suggested full description

SudokuNova is a modern Classic 9×9 Sudoku app designed for focused offline play, learning, and long-term improvement.

Highlights:

- seven difficulty levels from Beginner to Extreme;
- deterministic Daily and Weekly Challenges;
- notes, undo/redo, timer, mistake controls, pause and restart;
- advanced logical hints with structured teaching evidence;
- interactive learning and technique practice;
- custom puzzle creation, validation, solving, saving and replay;
- local History, Saved Puzzles, statistics, streaks and achievements;
- safe puzzle sharing/import with versioned validation;
- user-controlled local backup and restore;
- English and Hindi resources;
- light, dark, system and dynamic-color themes;
- keyboard support, high-contrast and reduced-motion preferences;
- offline-first architecture with no account required;
- open-source code and documentation.

SudokuNova's open-source base app does not include advertising, analytics, a SudokuNova-operated cloud backend, or an account/login requirement. Player data is kept locally unless the user explicitly chooses an Android sharing, import, export or backup action.

## Store asset checklist

Do not mark an item complete until the exact asset has been reviewed at the required current Play Console dimensions.

- [ ] high-resolution app icon exported from the canonical launcher artwork;
- [ ] phone screenshots from a release/production-equivalent build;
- [ ] tablet/large-screen screenshots if required or supported by the chosen listing;
- [ ] feature graphic if required/desired;
- [ ] screenshots show only implemented features;
- [ ] no debug labels, emulator overlays, personal notifications or unrelated account data;
- [ ] English text is proofread;
- [ ] Hindi screenshots/text are reviewed if localized listing assets are published;
- [ ] accessibility/high-contrast screenshots are accurate if used in marketing;
- [ ] screenshots do not imply cloud sync, multiplayer, ads-free purchase, or other non-features;
- [ ] national symbols/third-party trademarks are not used inaccurately;
- [ ] no production signing secrets, internal paths, email inboxes or private data appear in captures.

## Privacy and data declarations

Repository facts that should be reflected accurately in the store declarations:

- no user account/login in the open-source base app;
- no advertising SDK;
- no analytics SDK;
- no SudokuNova-operated remote backend;
- no runtime permissions declared by the current manifest;
- app settings/active game/statistics/learning progress use local Preferences DataStore;
- History, Saved Puzzles and challenge records use local Room storage;
- explicit user actions can share/export/import puzzle or backup data through Android system surfaces;
- `SNB1` backups are integrity-checked but not encrypted by SudokuNova;
- Android platform backup behavior can depend on device/distribution configuration and the app's XML backup rules.

Use these facts to answer the current Play Console Data safety/privacy forms. Do not copy old screenshots or remembered form answers without reviewing the current console questions.

## Public privacy-policy page

The repository contains the canonical policy at `docs/PRIVACY.md`. Before production publication, confirm the store-listed privacy URL is:

- publicly accessible without authentication;
- stable enough for long-term distribution;
- consistent with the exact release build;
- free from draft/internal-only language;
- updated if telemetry/accounts/cloud behavior ever changes.

A repository-hosted page may be used as an interim public reference, but store acceptance of any particular URL must be verified in the current console.

## Release artifact checklist

Before uploading an AAB:

- [ ] stable version metadata finalized;
- [ ] stable `versionCode` is strictly greater than any previously accepted store version code;
- [ ] production/upload signing configured through secrets outside Git;
- [ ] `clean :app:bundleRelease` succeeds;
- [ ] R8 mapping is preserved;
- [ ] signed artifact/certificate identity verified;
- [ ] SHA-256 artifact evidence saved;
- [ ] exact source commit/tag recorded;
- [ ] API-35 connected suite and standard CI are green on the candidate source;
- [ ] manual RC checklist in `V1_RELEASE_CANDIDATE.md` is complete;
- [ ] release notes match the actual code;
- [ ] backup/restore migration compatibility has been smoke-tested against existing user data where practical.

## Suggested release notes for v1.0

Use only after the final stable candidate has passed all required evidence:

> SudokuNova 1.0 delivers polished Classic 9×9 Sudoku with seven difficulty levels, Daily and Weekly Challenges, custom puzzles, advanced logical hints, interactive learning, local history/statistics, safe sharing and backup, English/Hindi support, accessibility features, and an offline-first privacy model. This release also includes extensive correctness, migration, security, performance, accessibility, R8 and API-35 verification work.

Do not publish these notes while the build is still `1.0.0-rc.1` or while mandatory manual validation remains incomplete.

## Rollout discipline

For the stable release:

1. preserve the exact source SHA and production artifact hashes;
2. use the safest staged/testing track appropriate to the distribution plan before full rollout;
3. review crash/ANR/store feedback during rollout;
4. stop or reduce rollout if a release-blocking defect appears;
5. fix forward from the exact released tag/commit rather than mixing unrelated feature work;
6. update `CHANGELOG.md`, `ROADMAP.md`, `what_changed.md`, the GitHub Release and store notes with the actual final evidence.

## Not yet evidence

This document is preparation only. It does not prove:

- Play Console acceptance;
- store policy compliance at a future date;
- signed artifact identity;
- device compatibility;
- screenshot quality;
- production rollout success.

Those claims require the real release workflow and must be recorded only after they happen.
