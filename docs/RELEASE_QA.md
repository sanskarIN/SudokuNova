# SudokuNova Release QA Matrix

This document defines the v0.9 production-readiness checks for Classic Sudoku. It is a checklist and evidence template; an unchecked item is not a claim that the result was performed.

## Evidence Header

Record for every release candidate:

- Git commit SHA:
- Branch/tag:
- Android CI workflow run:
- API-35 instrumentation workflow run:
- Build date:
- Tester/reviewer:
- JDK version:
- Gradle wrapper version:
- Android SDK/API used:

## Automated Merge Gates

- [ ] English/Hindi translation parity passes.
- [ ] `:sudoku-engine:test` passes.
- [ ] `:app:testDebugUnitTest` passes.
- [ ] `:app:assembleDebugAndroidTest` passes.
- [ ] `:app:lintDebug` passes.
- [ ] `:app:lintRelease` passes.
- [ ] `:app:assembleDebug` passes.
- [ ] `:app:assembleRelease` passes with R8/resource shrinking enabled.
- [ ] `:app:bundleRelease` passes.
- [ ] API-35 connected Compose/Room tests pass.
- [ ] No unresolved release-blocking CI failure exists on the exact final head.

## Installation and Launch

- [ ] Fresh debug install launches to the expected first-run state.
- [ ] Upgrade from a previously supported development build preserves compatible local data.
- [ ] App cold launch completes without crash.
- [ ] App warm resume completes without crash.
- [ ] Process recreation restores supported navigation/game state correctly.
- [ ] Rotation/window-size changes do not corrupt active state.

## Home

- [ ] Major actions are keyboard/switch/TalkBack reachable in logical order.
- [ ] Large font does not hide required actions.
- [ ] Narrow phone layout remains usable without clipped controls.
- [ ] Larger/tablet window does not create unusably stretched content.
- [ ] Reduced-motion preference does not depend on animation for understanding.

## Game

- [ ] New puzzle generation starts a valid unique-solution puzzle.
- [ ] Resume restores exact puzzle/progress state.
- [ ] Given cells cannot be edited.
- [ ] Number entry works in normal mode.
- [ ] Notes work and do not overwrite givens.
- [ ] Undo/redo preserve expected state.
- [ ] Conflict/error feedback is understandable without color alone.
- [ ] Pause/resume timer behavior is correct.
- [ ] Completion records expected statistics/history.
- [ ] Board remains usable with large font and narrow width.
- [ ] Every cell has useful accessibility semantics.

## Hints and Teaching Evidence

- [ ] Supported logical hint uses structured teaching evidence.
- [ ] Advanced hint title reflects the hardest enabling supported technique.
- [ ] Final placement matches the unique solution.
- [ ] Candidate eliminations never remove the unique solved value.
- [ ] Reveal remains clearly distinguished from a logical technique.
- [ ] Source, target, elimination, and placement meaning are not color-only.

## Learn and Practice

- [ ] All supported techniques remain available.
- [ ] Lesson view increments once per intended view action.
- [ ] Practice first-answer-only rule holds.
- [ ] Correct and incorrect feedback are readable by TalkBack.
- [ ] Mastery/progress remains bounded and deterministic.
- [ ] Reset learning progress does not reset unrelated app data.
- [ ] English/Hindi lesson and practice resources remain in parity.

## Challenges

- [ ] Daily challenge identity is deterministic for its date.
- [ ] Weekly challenge identity is deterministic for its week.
- [ ] Challenge completion is recorded once.
- [ ] Challenge history/result state survives restart.
- [ ] Date-bound challenge state does not corrupt ordinary games.

## Custom Puzzle

- [ ] Invalid board is rejected.
- [ ] Contradictory board is rejected.
- [ ] Non-unique board is rejected.
- [ ] Valid unique puzzle can be saved/played.
- [ ] Editing and validation remain accessible at large font.

## History and Replay

- [ ] History ordering is stable and meaningful.
- [ ] Completed entries open correctly.
- [ ] Replay provenance survives persistence/restore.
- [ ] Deleting/clearing history affects only selected data.

## Saved Puzzles

- [ ] Save action persists expected puzzle metadata.
- [ ] Saved puzzle opens with correct givens and provenance.
- [ ] Delete action removes only selected saved puzzle.
- [ ] Empty-state messaging is accessible.

## Statistics

- [ ] Aggregate counts match completed-game actions.
- [ ] Best-time logic is stable across restart.
- [ ] Reset statistics does not reset unrelated settings/history unless explicitly documented.
- [ ] Charts/progress information has a non-color-only textual equivalent where applicable.

## Settings

- [ ] Theme selection persists.
- [ ] High-contrast behavior improves distinguishability without hiding content.
- [ ] Reduced-motion setting disables or minimizes non-essential motion where supported.
- [ ] Language/localized resources remain readable.
- [ ] Destructive reset actions require clear confirmation.

## Backup and Transfer

- [ ] Export creates a non-empty bounded backup.
- [ ] Import rejects empty payloads.
- [ ] Import rejects payloads above the configured byte limit.
- [ ] Import rejects unsupported schema/version.
- [ ] Integrity mismatch fails closed.
- [ ] Malformed content fails closed without partial restore.
- [ ] Restore preserves validated puzzle/state invariants.
- [ ] Restore/reset boundaries match the user's selected operation.
- [ ] No broad storage permission is required for user-selected document access.

## Accessibility Manual Pass

Perform with TalkBack or another Android screen reader on at least one real device/emulator setup and record the environment.

- [ ] Home traversal order is logical.
- [ ] Game board traversal is understandable.
- [ ] Hint dialog/evidence is announced meaningfully.
- [ ] Learn/practice controls have unique useful labels.
- [ ] Challenge controls are reachable.
- [ ] Custom Puzzle controls are reachable.
- [ ] History and Saved Puzzles items expose action context.
- [ ] Settings switches/selectors announce current state.
- [ ] Backup/Transfer actions explain consequences.
- [ ] About/support links are understandable.
- [ ] Focus returns to a sensible element after dismissing dialogs.

## Font Scale and Window Matrix

Suggested manual matrix:

| Scenario | 1.0× | 1.3× | 1.5×+ |
|---|---:|---:|---:|
| Narrow phone portrait | [ ] | [ ] | [ ] |
| Typical phone portrait | [ ] | [ ] | [ ] |
| Phone landscape | [ ] | [ ] | [ ] |
| Large/tablet portrait | [ ] | [ ] | [ ] |
| Large/tablet landscape | [ ] | [ ] | [ ] |

For each cell verify no required control becomes unreachable, clipped, overlapped, or dependent on horizontal scrolling that was not intentionally designed.

## Performance Smoke Checks

Record device/emulator and methodology before recording numbers.

- [ ] Typical puzzle generation completes without perceptible UI freeze.
- [ ] Solver/hint request does not create sustained main-thread blocking.
- [ ] Teaching trace generation remains bounded.
- [ ] Backup parsing of maximum allowed input remains bounded.
- [ ] History/Saved Puzzle lists remain responsive with a representative local dataset.
- [ ] App launch does not perform avoidable heavy work on the main thread.

Do not publish benchmark numbers without a repeatable benchmark harness and recorded environment.

## Security and Privacy

- [ ] Manifest contains only required exported components.
- [ ] Manifest contains no unnecessary sensitive permission.
- [ ] Repository contains no production keystore/private key.
- [ ] Repository contains no signing password/API token/service credential.
- [ ] Release build does not require committed secrets.
- [ ] Backup parser remains size-bounded and fail-closed.
- [ ] No analytics/ad/tracking SDK was introduced by release hardening.
- [ ] Third-party notices reflect shipped dependencies where required.

## Release Artifacts

- [ ] Debug APK exists for the exact candidate commit.
- [ ] Release APK build succeeds for the exact candidate commit.
- [ ] Release AAB build succeeds for the exact candidate commit.
- [ ] R8 mapping output is retained with the candidate evidence.
- [ ] Production-signing procedure is performed outside source control when publishing.
- [ ] Final artifact versionCode/versionName match release metadata.

## Store Screenshot Readiness

- [ ] Home screen capture contains no debug-only UI.
- [ ] Game capture uses a valid, readable puzzle state.
- [ ] Learn/hints capture accurately represents implemented behavior.
- [ ] Settings/About capture shows current branding/version.
- [ ] Screenshots contain no private local data.
- [ ] English/Hindi screenshots are captured only if both are intentionally part of store assets.

## Release Decision

Release candidate may advance only when:

- all required automated gates are green on the exact head;
- all release-blocking manual checks have recorded evidence;
- no known critical/high-severity defect remains open;
- documentation describes actual results rather than planned results;
- production signing and store publication steps, if performed, use protected credentials outside Git.

Final decision:

- [ ] APPROVED
- [ ] REJECTED

Reason / blocking defects:

```text
Record evidence here.
```
