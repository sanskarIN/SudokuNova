# QA Matrix

Record pass/fail evidence for release candidates. This file defines coverage; it does not claim the matrix has already been completed on physical devices.

## Device Matrix

| Environment | Minimum coverage |
|---|---|
| Low-end / minimum SDK | API 26, constrained memory/CPU emulator or representative device |
| Standard phone | Current mainstream Android phone size |
| Large phone | Large portrait viewport and gesture navigation |
| Tablet | Large-screen adaptive game layout |
| Latest Android | API 37 target environment |

## Core Flow

| Area | Checks |
|---|---|
| Launch | Splash, cold start, Home visible, no crash |
| New game | Every difficulty opens valid playable board |
| Selection | Cell selection/peer/same-number highlights correct |
| Input | Values enter only editable cells |
| Notes | Toggle/add/remove/cleanup behavior correct |
| Eraser | Clears editable value/notes safely |
| Undo/redo | Restores expected in-session states |
| Hint | Explanation shown, optional apply works |
| Pause | Timer stops and board is obscured |
| Resume | Timer/game state continue correctly |
| Completion | Correct board triggers one completion record |
| Mistake limit | Configured limit/unlimited behavior correct |
| Restart | Puzzle returns to original clues with counters reset |

## Persistence

- [ ] Background and foreground app.
- [ ] Rotate/resize where configuration recreation occurs.
- [ ] Kill process and reopen.
- [ ] Confirm board values restored.
- [ ] Confirm notes restored.
- [ ] Confirm elapsed time restored.
- [ ] Confirm mistakes/hints restored.
- [ ] Confirm selected cell/state remains safe.
- [ ] Corrupted saved-game text does not crash.

## Custom Puzzle

Test:

- [ ] Empty editor
- [ ] Duplicate row clue
- [ ] Duplicate column clue
- [ ] Duplicate box clue
- [ ] No-solution puzzle
- [ ] Multiple-solution puzzle
- [ ] Known unique puzzle
- [ ] Solution preview preserves original clues
- [ ] Unique puzzle opens Game flow

## Daily Challenge

- [ ] Same local date generates same deterministic challenge under same engine version.
- [ ] Different date changes seed.
- [ ] Works without network.
- [ ] Completion records normally.
- [ ] Device date-change behavior is understood/documented.

## Statistics / Achievements

- [ ] Games started increments once.
- [ ] Completion increments once.
- [ ] Best time updates only when improved.
- [ ] Mistakes/hints totals accumulate correctly.
- [ ] Perfect/no-hint counts match criteria.
- [ ] Same-day completion does not incorrectly add a streak day.
- [ ] Consecutive-day completion increments streak.
- [ ] Gap resets current streak.
- [ ] Statistics reset leaves settings/active game unchanged.

## Themes and Layout

Test combinations:

- [ ] Light
- [ ] Dark
- [ ] System
- [ ] Dynamic color on supported device
- [ ] Dynamic color disabled
- [ ] Narrow phone
- [ ] Wide/tablet
- [ ] Large font scale
- [ ] High contrast preference
- [ ] Reduced motion preference

## Accessibility

- [ ] TalkBack cell navigation
- [ ] Original/editable distinction
- [ ] Conflict announcement
- [ ] Number pad labels/actions
- [ ] Dialog focus
- [ ] 200% font scaling where practical
- [ ] Touch targets
- [ ] Keyboard navigation when supported

## Offline / Privacy

- [ ] Core game starts with network disabled.
- [ ] Daily Challenge works offline.
- [ ] No login prompt.
- [ ] No unexpected sensitive permission prompt.
- [ ] External GitHub/BMC/email action occurs only after explicit tap.

## Build Variants

- [ ] Debug APK smoke test
- [ ] Release APK smoke test
- [ ] Release AAB generated
- [ ] Minified release keeps required classes/resources

## Regression Evidence

For each release-blocking bug fixed during QA, link the issue/commit and note whether automated regression coverage was added.
