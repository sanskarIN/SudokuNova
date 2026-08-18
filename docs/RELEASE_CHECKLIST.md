# SudokuNova Release Checklist

Use this checklist for every release candidate. Do not mark an item complete without corresponding evidence.

For the detailed process use `RELEASING.md`. For device/manual evidence use `RELEASE_QA.md` and `QA_MATRIX.md`.

## 1. Version and Source

- [ ] Intended release commit SHA is identified.
- [ ] Release branch/PR scope is frozen.
- [ ] `versionName` is correct.
- [ ] Android `versionCode` is greater than all previously distributed builds.
- [ ] Application ID/namespace are correct.
- [ ] `CHANGELOG.md` contains actual release changes.
- [ ] Root `ROADMAP.md` reflects delivered/deferred scope.
- [ ] `what_changed.md` records current implementation/evidence accurately.
- [ ] `README.md` and `docs/README.md` reflect the current release.
- [ ] No release-blocking TODO/sample/placeholder/dead navigation remains.

## 2. Repository Security / Localization

- [ ] `python scripts/verify_no_secrets.py` passes.
- [ ] No keystore/private key/password/token/service credential is committed.
- [ ] `python scripts/verify_translations.py` passes.
- [ ] English/Hindi player-facing resources are in parity.
- [ ] New accessibility strings are localized.
- [ ] New hint/Learn strings are localized.

## 3. Automated Engine / JVM Tests

- [ ] `:sudoku-engine:test` passes.
- [ ] `:app:testDebugUnitTest` passes.
- [ ] Deterministic generator/solver corpus passes.
- [ ] Teaching trace/advanced technique safety tests pass.
- [ ] Backup/transfer boundary tests pass.
- [ ] Room/DataStore model tests pass as applicable.

## 4. Android Test / Lint Gates

- [ ] `:app:assembleDebugAndroidTest` passes.
- [ ] `:app:lintDebug` passes.
- [ ] `:app:lintRelease` passes.
- [ ] API-35 connected Compose/Room suite passes.
- [ ] Final connected run tested the exact intended head commit.

## 5. Build Outputs

- [ ] Debug APK builds.
- [ ] Release APK builds with R8/minification/resource shrinking.
- [ ] Release AAB builds.
- [ ] R8 mapping output exists for the release build where expected.
- [ ] CI release output artifacts are retained/identified for verification where applicable.
- [ ] Release APK is smoke-tested on an appropriate target before publication.
- [ ] Final signed artifact is tested when production signing exists.

## 6. Sudoku Correctness

- [ ] Generated puzzles are valid.
- [ ] Generated puzzles have exactly one solution.
- [ ] Every supported difficulty can generate under deterministic corpus/test coverage.
- [ ] Solver distinguishes invalid/unsolvable/unique/multiple-solution cases.
- [ ] Notes work.
- [ ] Undo/redo works.
- [ ] Hints do not corrupt board/player state.
- [ ] Hint placements agree with the unique solution.
- [ ] Candidate eliminations never remove the solved value.
- [ ] Reveal is presented separately from logical techniques.
- [ ] Completion records exactly once.
- [ ] Custom puzzle invalid/no-solution/multiple-solution/unique cases are handled correctly.
- [ ] Imported puzzle requires unique solvability before play.

## 7. Gameplay / Lifecycle

- [ ] Cell-first input works.
- [ ] Number-first input works.
- [ ] Original clues remain protected.
- [ ] Pause/resume works.
- [ ] Timer behavior is correct.
- [ ] Mistake checking/limit behavior is correct.
- [ ] Restart resets intended session values only.
- [ ] Active-game resume works after process recreation/termination test.
- [ ] Corrupt active-game state fails safely.
- [ ] Hint computation does not block the main thread noticeably.
- [ ] Stale asynchronous hint results cannot affect a changed board.

## 8. Challenges / Custom / History / Saved

- [ ] Daily Challenge deterministic identity works.
- [ ] Weekly Challenge deterministic identity works.
- [ ] Daily/Weekly identities remain separated.
- [ ] Challenge completion records correctly.
- [ ] Custom puzzle save/play works.
- [ ] History record fields are correct.
- [ ] Favorite state persists.
- [ ] Saved Puzzle uniqueness/deduplication is safe.
- [ ] Replay provenance persists.
- [ ] Replay does not inflate original statistics.

## 9. Learning / Hints

- [ ] All supported technique lessons are reachable.
- [ ] All supported techniques have practice coverage.
- [ ] Practice feedback is correct.
- [ ] Duplicate taps do not create duplicate attempts.
- [ ] Technique/overall mastery is correct.
- [ ] Learning reset affects only learning data.
- [ ] In-game teaching source/target/elimination/placement presentation is correct.

## 10. Data / Migration / Backup

- [ ] Existing supported Room database upgrades safely.
- [ ] Required Room migration tests pass.
- [ ] No destructive migration fallback has been introduced accidentally.
- [ ] Room schemas are exported/current.
- [ ] DataStore keys/reset scope remain compatible.
- [ ] Statistics are not double-counted.
- [ ] Destructive/reset actions clearly communicate scope.
- [ ] `SNP1` bounds/version/checksum validation remains intact.
- [ ] `SNB1` bounds/version/checksum/record validation remains intact.
- [ ] Bounded backup stream read remains intact.
- [ ] Duplicate restore behavior is safe.
- [ ] Favorite promotion/replay provenance survive restore.

## 11. Accessibility

- [ ] TalkBack core navigation checked manually.
- [ ] Sudoku coordinate/value/clue semantics checked.
- [ ] Conflict state announced.
- [ ] Selected cell semantic state checked.
- [ ] Hint source/target/elimination/placement semantics checked.
- [ ] Number pad/actions checked.
- [ ] Dialog focus/return behavior checked.
- [ ] Large font scaling checked.
- [ ] 200% font scaling checked where practical.
- [ ] Contrast checked in light/dark themes.
- [ ] High-contrast behavior checked.
- [ ] Reduced-motion behavior checked.
- [ ] Tablet/large-window behavior checked.
- [ ] Hardware keyboard navigation/input checked when supported.

## 12. Privacy / Security

- [ ] Privacy policy matches the shipped binary.
- [ ] Android backup/data extraction rules reviewed against privacy policy.
- [ ] Manifest contains no unnecessary sensitive permission.
- [ ] Exported components reviewed.
- [ ] Deep-link/external input surface reviewed if present.
- [ ] Puzzle/backup input is treated as untrusted.
- [ ] No unbounded external file read introduced.
- [ ] Dependencies reviewed for critical advisories/behavior.
- [ ] `THIRD_PARTY_NOTICES.md` is current.
- [ ] Production signing material is external/secrets-only.
- [ ] Release logs/artifacts contain no secret material.

## 13. Performance / ANR

- [ ] Puzzle generation remains off main thread.
- [ ] Custom/import solve analysis remains off main thread.
- [ ] Hint analysis remains off main thread.
- [ ] Backup file I/O/restore remains off main thread.
- [ ] Large valid backup within limits does not cause obvious ANR.
- [ ] History/Saved lists remain usable with substantial records.
- [ ] No reproducible release-scope crash or ANR remains.
- [ ] Any benchmark claim has exact device/commit/input evidence.

## 14. Device QA

- [ ] Minimum supported Android target tested.
- [ ] Standard modern phone tested.
- [ ] Large phone tested.
- [ ] Tablet/large screen tested.
- [ ] Current/latest target environment tested as available.
- [ ] Portrait checked.
- [ ] Landscape/window resize checked as applicable.
- [ ] Dark mode checked.
- [ ] Dynamic color checked where supported.

Do not mark device rows passed from documentation/CI alone.

## 15. Store / Release Assets

- [ ] Launcher icon correct.
- [ ] Adaptive/monochrome icon correct.
- [ ] Splash correct.
- [ ] Screenshots are captured from actual current UI.
- [ ] Screenshots contain no private test data.
- [ ] Store description contains no unimplemented claims.
- [ ] Privacy/Data Safety answers match actual binary behavior.
- [ ] Content rating/target API requirements checked against current store rules.
- [ ] Support/security/privacy links are valid.

## 16. Signing / Distribution

- [ ] Production signing uses secure external secrets.
- [ ] Final signed AAB is the artifact validated for store upload.
- [ ] Final signed APK tested if distributed directly.
- [ ] Checksums generated for public binary attachments where appropriate.
- [ ] Git tag points to exact verified release commit.
- [ ] GitHub Release notes prepared from changelog.
- [ ] Rollback/fix-forward approach considered for data/security-sensitive changes.

## 17. Final Exact-Head Approval

- [ ] Final PR/release head SHA recorded.
- [ ] Android CI is green on that exact SHA.
- [ ] API-35 connected instrumentation is green on that exact SHA.
- [ ] No later commit invalidated those runs.
- [ ] Known limitations are documented.
- [ ] No critical/high release blocker remains unresolved.
- [ ] Documentation links/status statements are accurate.
- [ ] Support/business/security contact details are correct.
- [ ] Manual QA claims are backed by actual evidence.
- [ ] `Made by the Sanskar` attribution remains present where intended.

Only after the required items are complete should the release be tagged/published according to `RELEASING.md`.
