# Play Store Release Preparation

This document prepares SudokuNova **2.0.12** for a future Android store release. It intentionally avoids claiming that Play Console submission, policy review, production signing, device QA, or publication has already happened.

Always review the current Play Console requirements at release time because store policies, declarations, screenshots, target-API requirements, and form wording can change independently of this repository.

Current release authority: `V2_0_12_RELEASE.md`.

## Product identity

- App name: **SudokuNova**
- Tagline: **Think. Solve. Master the Grid.**
- Application ID: `in.sanskar.sudokunova`
- Current source version: `2.0.12`
- Android versionCode: `2012`
- Minimum Android API: `26`
- Target Android API: `37`
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
- [ ] phone screenshots captured from the exact 2.0.12 release/production-equivalent build;
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
- consistent with the exact 2.0.12 release build;
- free from draft/internal-only language;
- updated if telemetry/accounts/cloud behavior ever changes.

A repository-hosted page may be used as an interim public reference, but store acceptance of any particular URL must be verified in the current console.

## Release artifact checklist

Before uploading an AAB:

- [ ] source metadata is exactly `versionCode 2012` / `versionName 2.0.12`;
- [ ] Android `versionCode 2012` is strictly greater than every previously accepted distributed version code;
- [ ] `python scripts/verify_release_contract.py` confirms source/ordinary-CI/protected-workflow identity synchronization;
- [ ] production/upload signing is configured through secrets outside Git;
- [ ] `clean :app:bundleRelease` succeeds;
- [ ] R8 mapping is preserved;
- [ ] release-output verifier confirms exact application/version/SDK identity;
- [ ] embedded APK identity evidence reports `debuggable=false`;
- [ ] signed artifact/certificate identity is verified;
- [ ] APK validation reports a verified v2-or-newer signature scheme;
- [ ] SHA-256 artifact evidence is saved;
- [ ] exact source commit/tag is recorded;
- [ ] API-35 connected suite and standard CI are green on the exact final 2.0.12 source head;
- [ ] current manual evidence requirements in `V2_0_12_RELEASE.md` are complete;
- [ ] release notes match the actual code;
- [ ] backup/restore migration compatibility has been smoke-tested against existing user data where practical.

## Suggested release notes for 2.0.12

Use only after the final 2.0.12 candidate has passed all mandatory evidence and received a `SHIP` decision:

> SudokuNova 2.0.12 delivers polished Classic 9×9 Sudoku with seven difficulty levels, Daily and Weekly Challenges, custom puzzles, advanced logical hints, interactive learning, local history/statistics, safe sharing and backup, English/Hindi support, accessibility features, and an offline-first privacy model. This release also includes extensive correctness, migration, security, performance, accessibility, R8, release-identity and API-35 verification work.

Do not publish these notes while mandatory exact-head, production-signing, manual accessibility/device/performance, or store validation remains incomplete.

## Final distribution identity

Before submission, record and verify:

- exact final source SHA;
- `versionCode 2012` / `versionName 2.0.12`;
- production application ID `in.sanskar.sudokunova`;
- `minSdk 26` / `targetSdk 37`;
- final AAB SHA-256 and size;
- final APK SHA-256 and size if direct APK distribution is used;
- R8 mapping SHA-256 and size;
- APK embedded identity evidence;
- APK signer certificate SHA-256;
- AAB upload signer certificate SHA-256;
- distribution-platform app-signing certificate identity where applicable;
- protected workflow run ID/result;
- current store validation results.

If Play App Signing is used, do not confuse the local upload-key certificate with the app-signing certificate used for APKs delivered to users.

## Rollout discipline

For the 2.0.12 release:

1. preserve the exact source SHA and production artifact hashes;
2. use the safest staged/testing track appropriate to the distribution plan before full rollout;
3. review crash/ANR/store feedback during rollout;
4. stop or reduce rollout if a release-blocking defect appears;
5. fix forward from the exact released tag/commit rather than mixing unrelated feature work;
6. update `CHANGELOG.md`, `ROADMAP.md`, `what_changed.md`, the GitHub Release and store notes with actual final evidence.

## Tag and publication boundary

Do not create or advertise `v2.0.12` as released merely because the source metadata uses 2.0.12.

The tag/GitHub Release/store publication must wait until:

- exact-final-head Android CI is green;
- exact-final-head API-35 instrumentation is green;
- required production signing/certificate evidence exists;
- required real-device/accessibility/performance QA exists;
- current store validation is complete;
- no critical/high release blocker remains;
- final decision is explicitly `SHIP`.

## Not yet evidence

This document is preparation only. It does not prove:

- Play Console acceptance;
- store policy compliance at a future date;
- signed artifact identity;
- device compatibility;
- screenshot quality;
- representative physical-device performance;
- repository-admin configuration;
- production rollout success;
- creation of the `v2.0.12` tag or GitHub Release.

Those claims require the real release workflow and must be recorded only after they happen.
