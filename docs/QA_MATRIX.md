# SudokuNova QA Matrix

This matrix defines broad manual/regression coverage for release candidates. It does **not** claim that these checks have already passed on physical devices. Record actual release evidence in `RELEASE_QA.md` and `what_changed.md`.

## Device / Environment Matrix

| Environment | Minimum coverage goal |
|---|---|
| Minimum supported Android | API 26 target using a suitable emulator/device |
| Standard phone | Mainstream portrait phone viewport |
| Large phone | Large portrait viewport + gesture navigation |
| Tablet / large screen | Adaptive wider layout and hardware keyboard where available |
| API-35 CI emulator | Connected Compose/Room regression suite |
| Current compile/target environment | API 37 build/lint compatibility |
| Release build | R8/resource-shrunk APK smoke test |

Do not mark a physical-device category complete merely because an emulator passed.

## Install / Launch / Lifecycle

- [ ] Fresh install launches without crash.
- [ ] Splash branding is correct.
- [ ] Home becomes usable.
- [ ] Background/foreground retains safe state.
- [ ] Configuration/window recreation retains expected state.
- [ ] Process kill/reopen restores a valid active game when expected.
- [ ] Corrupt active-game state fails safely.
- [ ] Completed game does not incorrectly restore as in-progress.

## Home / Navigation

- [ ] Normal difficulty actions reachable.
- [ ] Daily Challenge reachable.
- [ ] Challenges archive reachable.
- [ ] Custom Puzzle reachable.
- [ ] History reachable.
- [ ] Saved Puzzles reachable.
- [ ] Learn reachable.
- [ ] Statistics reachable.
- [ ] Settings reachable.
- [ ] Backup & Transfer reachable.
- [ ] About reachable.
- [ ] Back navigation returns to expected destination.

## Normal Game

- [ ] Every supported difficulty can produce a valid playable board.
- [ ] Original clues cannot be edited.
- [ ] Cell-first input works.
- [ ] Number-first input works.
- [ ] Number entry 1–9 works.
- [ ] Notes toggle/add/remove works.
- [ ] Auto-remove notes behavior matches setting.
- [ ] Erase works only on editable state.
- [ ] Undo/redo restores expected states.
- [ ] Pause prevents solving interaction and timer progression as intended.
- [ ] Resume is correct.
- [ ] Timer visibility setting works.
- [ ] Same-number highlighting works.
- [ ] Peer highlighting works.
- [ ] Conflict/mistake presentation is correct.
- [ ] Mistake limit / unlimited behavior is correct.
- [ ] Restart restores original clues and resets session counters.
- [ ] Completion records once.
- [ ] Active save clears after completion as intended.

## Hint / Teaching

Test direct and multi-step cases where practical:

- [ ] Naked Single hint.
- [ ] Hidden Single hint.
- [ ] Naked Pair evidence.
- [ ] Pointing Pair / Triple evidence.
- [ ] Box-Line Reduction evidence.
- [ ] Hidden Pair evidence.
- [ ] Naked Triple evidence.
- [ ] Hidden Triple evidence.
- [ ] X-Wing evidence.
- [ ] Source cells/targets are presented correctly.
- [ ] Candidate eliminations are described correctly.
- [ ] Final placement is correct.
- [ ] Multi-step hint title reports the hardest enabling technique.
- [ ] Applying a hint changes only the intended final placement path.
- [ ] Reveal is clearly distinct from logical evidence.
- [ ] Requesting a hint does not visibly freeze the UI.
- [ ] Stale hint result is not published after board state changes.

## Learning Center

- [ ] Intro lessons are readable/reachable.
- [ ] Every supported technique card is reachable.
- [ ] Study action opens the correct technique.
- [ ] Practice starts the correct deterministic exercise.
- [ ] Answer choices are usable.
- [ ] Correct feedback is correct.
- [ ] Incorrect feedback is correct.
- [ ] One submitted exercise does not record duplicate attempts on repeated taps.
- [ ] Next/close practice behaves correctly.
- [ ] Technique progress updates locally.
- [ ] Overall mastery updates correctly.
- [ ] Learning reset clears only learning progress.

## Custom Puzzle

- [ ] Empty editor safe.
- [ ] Duplicate row clue rejected.
- [ ] Duplicate column clue rejected.
- [ ] Duplicate box clue rejected.
- [ ] Unsolvable puzzle rejected.
- [ ] Multiple-solution puzzle rejected.
- [ ] Known unique puzzle accepted.
- [ ] Solution/validation flow does not lose original clues.
- [ ] Accepted puzzle opens normal Game flow.
- [ ] Save puzzle works.
- [ ] Saved custom puzzle can be replayed.

## Daily / Weekly Challenges

- [ ] Same Daily key reproduces deterministic identity under the same engine algorithm.
- [ ] Different Daily key changes identity.
- [ ] Weekly identity is deterministic.
- [ ] Daily and Weekly seed spaces do not collide by design.
- [ ] Challenge works offline.
- [ ] Completion result records once.
- [ ] Existing completion remains visible in archive.
- [ ] Replay/return path does not corrupt challenge stats.
- [ ] Device date/week boundary behavior is understood.

## History

- [ ] Completed game appears once.
- [ ] Difficulty/time/mistake/hint metadata is correct.
- [ ] Perfect state matches zero-mistake/zero-hint rule.
- [ ] Favorite toggle persists.
- [ ] Replay opens the correct puzzle.
- [ ] Replay provenance survives.
- [ ] Replay completion does not inflate original statistics.
- [ ] Delete/reset behavior affects only intended record(s).

## Saved Puzzles

- [ ] Save unique puzzle.
- [ ] Duplicate puzzle reconciliation behaves safely.
- [ ] Favorite state persists.
- [ ] Favorite promotion is preserved during restore/deduplication.
- [ ] Saved title/source/difficulty are correct.
- [ ] Replay/open works.
- [ ] Delete/reset scope is correct.

## Statistics / Achievements

- [ ] Game started/completed counters change once per intended event.
- [ ] Completion rate is correct.
- [ ] Best time changes only when improved.
- [ ] Mistake/hint totals are correct.
- [ ] Perfect/no-hint criteria are correct.
- [ ] Streak same-day behavior is correct.
- [ ] Consecutive-day behavior is correct.
- [ ] Gap resets current streak where intended.
- [ ] Difficulty summaries exclude replay inflation.
- [ ] Challenge statistics align with recorded challenge results.
- [ ] Statistics reset does not erase unrelated settings/active game unless explicitly documented.

## Settings

- [ ] Light/Dark/System theme.
- [ ] Dynamic color on supported Android.
- [ ] Input mode persists.
- [ ] Peer/same-number highlights persist.
- [ ] Auto-check setting persists.
- [ ] Auto-remove notes persists.
- [ ] Timer visibility persists.
- [ ] Haptics setting works.
- [ ] Sound setting works.
- [ ] Reduced motion persists/affects applicable behavior.
- [ ] High contrast persists/affects board presentation.
- [ ] Mistake limit persists and validates allowed range.
- [ ] Reset confirmations accurately describe scope.

## Backup & Transfer

### Puzzle code (`SNP1`)

- [ ] Valid code decodes.
- [ ] Invalid checksum rejected.
- [ ] Unsupported version rejected.
- [ ] Oversized input rejected.
- [ ] Invalid board rejected.
- [ ] Non-unique playable import rejected at Android acceptance boundary.
- [ ] Share/copy path exports intended content only.

### Backup (`SNB1`)

- [ ] Valid export generated.
- [ ] Valid import round trip.
- [ ] Empty input rejected.
- [ ] Oversized input rejected.
- [ ] Exact max boundary behaves as tested.
- [ ] Invalid checksum rejected.
- [ ] Unknown record type rejected.
- [ ] Invalid enum/number/timestamp rejected.
- [ ] Duplicate restore is safe.
- [ ] Favorite promotion is preserved.
- [ ] Replay provenance is preserved.
- [ ] Document picker read/write works without broad storage permission.
- [ ] Large valid dataset within limits remains responsive.

## Room / DataStore

- [ ] Room database opens on current schema.
- [ ] Supported migration path succeeds.
- [ ] Existing history survives migration.
- [ ] Existing saved puzzles survive migration.
- [ ] Challenge schema/indexes exist.
- [ ] No destructive migration fallback occurs unexpectedly.
- [ ] DataStore settings survive restart.
- [ ] Active game survives restart/process recreation as expected.
- [ ] Learning counters survive restart.
- [ ] Scoped resets do not erase unrelated categories.

## Themes / Layout / Font

Test:

- [ ] Light.
- [ ] Dark.
- [ ] System theme.
- [ ] Dynamic color enabled/disabled on supported device.
- [ ] Narrow phone.
- [ ] Standard phone.
- [ ] Large phone.
- [ ] Tablet/large window.
- [ ] Portrait.
- [ ] Landscape/resize where supported.
- [ ] Large font scale.
- [ ] 200% font scale where practical.
- [ ] No critical action becomes unreachable.
- [ ] Sudoku value/note readability remains acceptable.

## Accessibility

- [ ] TalkBack can discover interactive cells.
- [ ] Row/column/value description is correct.
- [ ] Original/editable distinction is announced.
- [ ] Conflict/error state is announced.
- [ ] Selected cell state is exposed semantically.
- [ ] Hint source/target roles are announced.
- [ ] Candidate elimination candidates are announced.
- [ ] Final hint placement/value is announced.
- [ ] Number pad/actions have useful labels.
- [ ] Dialog focus order is logical.
- [ ] Focus returns sensibly after dialogs.
- [ ] High-contrast states are distinguishable.
- [ ] Information is not color-only.
- [ ] Touch targets are usable where board density permits.
- [ ] Hardware keyboard navigation/input works.

## Privacy / Security

- [ ] Core game works with network disabled.
- [ ] Challenge generation works offline.
- [ ] No login prompt.
- [ ] No unexpected sensitive permission request.
- [ ] External browser/BMC/email only opens after explicit action.
- [ ] Manifest exported components reviewed.
- [ ] `verify_no_secrets.py` passes.
- [ ] No production keystore/private key/credential committed.
- [ ] Backup parser remains bounded.
- [ ] Puzzle import remains bounded/validated.
- [ ] Privacy policy matches actual storage/sharing.
- [ ] Android backup XML reviewed.
- [ ] Third-party notices/dependencies reviewed.

## Performance / ANR Smoke

- [ ] Starting a puzzle does not create an obvious UI ANR.
- [ ] Hint request leaves UI responsive.
- [ ] Large allowed backup import remains bounded/responsive.
- [ ] History/Saved lists remain usable with substantial local datasets.
- [ ] Repeated navigation does not show obvious memory/leak symptoms.
- [ ] Release build startup/navigation is acceptable on representative targets.

Record actual measurements only when measured according to `PERFORMANCE.md`.

## Build / Release Variants

- [ ] Translation verifier passes.
- [ ] Repository security guard passes.
- [ ] Engine tests pass.
- [ ] App JVM tests pass.
- [ ] Android test APK compiles.
- [ ] Debug lint passes.
- [ ] Release lint passes.
- [ ] Debug APK builds.
- [ ] Release APK builds with R8/resource shrinking.
- [ ] Release AAB builds.
- [ ] API-35 connected suite passes.
- [ ] Release APK manually smoke-tested when release candidate requires it.
- [ ] Final signed artifact tested when production signing exists.

## Store / Asset Readiness

- [ ] Launcher/adaptive/monochrome icons correct.
- [ ] Splash correct.
- [ ] Screenshots show real current UI.
- [ ] Screenshots avoid private test data.
- [ ] Feature graphic/description contains no unimplemented claims.
- [ ] Privacy/Data Safety answers match shipped binary.
- [ ] Support/security links are valid.

## Regression Evidence

For every release-blocking defect discovered during QA, record:

- issue/description;
- affected commit/build;
- reproduction;
- root cause;
- fix commit;
- regression test if practical;
- final verification evidence.

Do not mark a release blocker resolved solely because it could not be reproduced once.
