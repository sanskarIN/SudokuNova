# v1.0 Release Candidate Evidence Worksheet

This is the authoritative manual/production evidence worksheet for SudokuNova v1.0. Repository automation has prepared and verified the RC1 repository line, but this document must not mark real device, accessibility-service, signing, performance, repository-admin, or store checks complete from source review/CI alone.

Current merged candidate metadata:

- candidate version name: `1.0.0-rc.1`
- candidate version code: `1000`
- package: `in.sanskar.sudokunova`
- final verified PR #27 source head: `7016e21f36c8ecb8a495c446ffd8b57e9f20a4ea`
- PR #27 merge commit: `2329881aff8dabaf8d040918e16b6113e3900245`
- stable publication: **not yet claimed**

If RC version code `1000` is uploaded to a store track that reserves it, the later stable build must use a higher version code.

## Evidence header

Repository-side automated evidence is prefilled from the exact verified RC1 head. Manual/production identity fields remain blank until real validation occurs.

- Source commit SHA: `7016e21f36c8ecb8a495c446ffd8b57e9f20a4ea`
- GitHub PR: `#27` — merged as `2329881aff8dabaf8d040918e16b6113e3900245`
- Standard Android CI run: `#635 / 32151771317 — PASS`
- API-35 connected run: `#188 / 32151771297 — PASS`
- Unsigned RC APK SHA-256: `422a151ab3bb47268a69548ce5669b7a141169cc822400d3ef1376fa476b53c7`
- Unsigned RC AAB SHA-256: `1bbbb2f227fc432efa74fa6efe16f2f17ae3aa5bf4a59ffac9e2e71de9a7cdfd`
- RC R8 mapping SHA-256: `0f8b128679e858e0d835f0e3d23bfb629448efc4215703dd6c0f69b155e3f3ac`
- CI artifact ID: `9330415157`
- CI artifact digest: `sha256:0f1fa33127f6ae46d633c039bf0aad2e308b11e94bbd5567dd0fbc4805b4263c`
- Signing certificate digest/fingerprint: `________________` — **PENDING production signing**
- Manual tester: `________________`
- Manual test date/time/timezone: `________________`

The hashes above are for unsigned repository-CI RC verification artifacts. They are not substitutes for final signed stable artifact hashes/signature identity.

Do not copy manual evidence from an earlier source commit after the stable candidate changes.

## Target matrix

Record real tested targets. Add rows rather than replacing evidence.

| Target | Android/API | Form factor | Font scale | Theme/contrast | Input/accessibility | Result | Notes |
|---|---|---|---|---|---|---|---|
| `________________` | `_____` | `_____` | `_____` | `_____` | `_____` | `_____` | `_____` |
| `________________` | `_____` | `_____` | `_____` | `_____` | `_____` | `_____` | `_____` |
| `________________` | `_____` | `_____` | `_____` | `_____` | `_____` | `_____` | `_____` |

## 1. Installation and launch

- [ ] signed production/production-equivalent APK installs successfully on intended direct-install target;
- [ ] installed package is `in.sanskar.sudokunova`;
- [ ] version name/code match the final candidate;
- [ ] cold launch reaches the expected splash/home flow;
- [ ] no startup crash or obvious ANR;
- [ ] upgrade from an existing supported SudokuNova build preserves compatible local data;
- [ ] reinstall/uninstall behavior matches documented local-data expectations;
- [ ] release build contains no visible debug-only labels or tooling UI.

Evidence/notes:

`______________________________________________________________________________`

## 2. Core gameplay

Test at least one fresh Classic 9×9 game and one resumed game.

- [ ] difficulty selection works;
- [ ] generated puzzle is playable and visually correct;
- [ ] cell selection works;
- [ ] number entry works;
- [ ] Cell-first mode works;
- [ ] Number-first mode works;
- [ ] Notes mode works;
- [ ] erase works;
- [ ] undo/redo work;
- [ ] pause/resume works;
- [ ] timer behavior is sensible across pause/background/foreground;
- [ ] mistake mode/limit behavior matches settings;
- [ ] hint opens and evidence is understandable;
- [ ] applying hint changes only the supported placement;
- [ ] restart confirmation/behavior works;
- [ ] completion dialog/stats are correct;
- [ ] active game resumes after ordinary app recreation/backgrounding.

Evidence/notes:

`______________________________________________________________________________`

## 3. Challenges

- [ ] Daily Challenge opens and plays;
- [ ] Weekly Challenge opens and plays;
- [ ] archive/history content is reachable;
- [ ] challenge completion records are not duplicated incorrectly;
- [ ] challenge identity/provenance remains correct after resume/relaunch.

Evidence/notes:

`______________________________________________________________________________`

## 4. Custom Puzzle

- [ ] editor cells are reachable and selectable;
- [ ] number input/erase/clear work;
- [ ] contradiction feedback works;
- [ ] too-few-clues feedback works;
- [ ] no-solution feedback works;
- [ ] multiple-solution feedback works when applicable;
- [ ] unique validation works;
- [ ] solution preview works;
- [ ] save works without duplicate corruption;
- [ ] play custom puzzle works;
- [ ] long-running validation remains responsive and stale results do not overwrite later edits.

Evidence/notes:

`______________________________________________________________________________`

## 5. Learning and hints

- [ ] Learn screen opens;
- [ ] technique study dialog opens/closes;
- [ ] practice answer flow works;
- [ ] progress updates locally;
- [ ] reset learning progress affects only documented learning counters;
- [ ] Naked Single presentation is correct;
- [ ] Hidden Single presentation is correct;
- [ ] advanced elimination technique presentation remains coherent for sampled techniques;
- [ ] Reveal fallback is clearly distinguished from a logical technique.

Evidence/notes:

`______________________________________________________________________________`

## 6. History, Saved Puzzles and statistics

- [ ] completed game appears in History;
- [ ] History filters work;
- [ ] favorite toggle works;
- [ ] replay flow works without inflating normal aggregate stats incorrectly;
- [ ] Saved Puzzles add/play/favorite/delete flows work;
- [ ] statistics values are plausible after controlled test games;
- [ ] safe reset operations affect only the documented scope.

Evidence/notes:

`______________________________________________________________________________`

## 7. Sharing, import, export and backup

Use only non-sensitive test data.

- [ ] puzzle code copy/share works;
- [ ] valid puzzle code import works;
- [ ] malformed/tampered code is rejected;
- [ ] imported puzzle uniqueness validation completes without blocking UI;
- [ ] backup export produces a file through Android system UI;
- [ ] backup import/restore works for a controlled dataset;
- [ ] duplicate-safe restore behavior is preserved;
- [ ] Favorites/provenance are preserved according to documented merge rules;
- [ ] oversized/invalid backup is rejected safely;
- [ ] app requests no broad storage permission for these flows.

Evidence/notes:

`______________________________________________________________________________`

## 8. TalkBack / screen-reader traversal

Perform real accessibility-service traversal. Automated semantics tests do not satisfy this section.

- [ ] Home traversal/focus order is sensible;
- [ ] Game board cells announce coordinate/value/clue/conflict/selection information appropriately;
- [ ] hint source/target/elimination/placement information is understandable without color alone;
- [ ] Game controls have meaningful roles/labels/states;
- [ ] Learn/practice dialogs have usable focus order;
- [ ] Challenges are navigable;
- [ ] Custom Puzzle editor/actions are navigable;
- [ ] History is navigable;
- [ ] Saved Puzzles is navigable;
- [ ] Settings switches announce state once without duplicate targets;
- [ ] Backup & Transfer is navigable;
- [ ] Statistics is navigable;
- [ ] About/support content is navigable;
- [ ] dialogs return focus sensibly after closing where applicable.

Evidence/notes:

`______________________________________________________________________________`

## 9. Font scale and adaptive layouts

Test representative narrow and larger windows at normal and large text. Include 200% font scale where supported by the target environment.

- [ ] Home remains usable;
- [ ] Game controls remain reachable;
- [ ] Sudoku board remains usable;
- [ ] Settings options remain reachable;
- [ ] History filters/metrics remain reachable;
- [ ] Learn Study/Practice actions remain reachable;
- [ ] Custom Puzzle actions remain reachable by scrolling;
- [ ] Challenge content does not collide destructively;
- [ ] Saved Puzzles content remains readable;
- [ ] puzzle sharing actions remain reachable;
- [ ] Backup & Transfer actions remain reachable;
- [ ] landscape/resize does not lose critical state;
- [ ] larger/tablet window does not create unusable empty/overstretched UI.

Evidence/notes:

`______________________________________________________________________________`

## 10. High contrast and reduced motion

- [ ] High Contrast improves board-state differentiation without hiding text/content;
- [ ] selection, conflict, hint source/target and fixed clues remain distinguishable;
- [ ] Reduced Motion removes/reduces optional motion as designed;
- [ ] disabling Reduced Motion restores expected motion behavior;
- [ ] neither preference changes Sudoku correctness or stored game state.

Evidence/notes:

`______________________________________________________________________________`

## 11. Keyboard / hardware input

Where a physical/hardware keyboard is available:

- [ ] arrow navigation works;
- [ ] digits 1–9 work;
- [ ] erase/delete/backspace behavior works;
- [ ] Notes shortcut works;
- [ ] Hint shortcut works;
- [ ] keyboard actions respect paused/completed/non-editable states;
- [ ] focus does not get trapped unexpectedly.

Evidence/notes:

`______________________________________________________________________________`

## 12. Lifecycle and process death

Use platform/developer tools appropriate to the test device/emulator.

- [ ] background/foreground preserves active game correctly;
- [ ] rotation/configuration change preserves expected state;
- [ ] process recreation restores a valid PLAYING state when appropriate;
- [ ] invalid/corrupt saved state fails closed rather than crashing or restoring impossible data;
- [ ] challenge/replay/custom provenance is preserved;
- [ ] pending/transient dialogs do not corrupt persistent state;
- [ ] no reproducible lifecycle crash/ANR observed.

Evidence/notes:

`______________________________________________________________________________`

## 13. Performance / ANR / memory evidence

Record the tool/method, target and observations. Source review alone does not complete this section.

- [ ] cold startup observed/measured;
- [ ] puzzle generation does not cause an unacceptable UI freeze;
- [ ] hint computation remains responsive;
- [ ] Custom Puzzle solve/validate remains responsive;
- [ ] import validation remains responsive;
- [ ] backup import/export remains responsive for representative supported data size;
- [ ] scrolling key lists/screens does not show obvious sustained jank;
- [ ] no reproducible ANR;
- [ ] no obvious unbounded memory growth during repeated game/navigation loops;
- [ ] any measured regression is investigated before stable release.

Tool/method/results:

`______________________________________________________________________________`

## 14. Production signing

Follow `PRODUCTION_SIGNING.md`.

- [ ] all four signing values were supplied through a secure environment;
- [ ] no signing secrets exist in Git/diff/logs/artifacts;
- [ ] clean signed APK/AAB build succeeds;
- [ ] `scripts/verify_release_outputs.py --require-signatures` succeeds on the exact signed artifacts;
- [ ] `apksigner verify --verbose --print-certs` succeeds for signed APK;
- [ ] certificate fingerprint matches expected production/upload certificate;
- [ ] signed APK installs/launches where direct installation is part of validation;
- [ ] AAB passes the distribution platform's pre-upload/upload validation;
- [ ] R8 mapping is retained securely for the exact release;
- [ ] final signed-artifact SHA-256 evidence is archived.

Evidence/notes:

`______________________________________________________________________________`

## 15. Store/repository release assets

Follow `PLAY_STORE_RELEASE.md`.

- [ ] final icon reviewed;
- [ ] required screenshots captured from correct release build;
- [ ] screenshots contain no private/debug information;
- [ ] listing title/short/full descriptions reviewed;
- [ ] privacy policy URL publicly accessible and consistent with build;
- [ ] current data/privacy declarations completed accurately;
- [ ] current content/app-access/other required store declarations completed accurately;
- [ ] release notes match actual stable code;
- [ ] repository README/CHANGELOG/ROADMAP/what_changed reflect final stable release only after publication decision.

Evidence/notes:

`______________________________________________________________________________`

## 16. Stable-release decision

All mandatory fields below must be explicit.

- Automated RC1 repository-side exact-head gates: `PASS` — PR #27 head `7016e21f36c8ecb8a495c446ffd8b57e9f20a4ea`
- Fresh stable-metadata/source exact-head gates: `PENDING`
- Manual functional matrix: `PENDING`
- Accessibility/TalkBack: `PENDING`
- Font/adaptive layout: `PENDING`
- Contrast/motion: `PENDING`
- Lifecycle/process death: `PENDING`
- Performance/ANR/memory: `PENDING`
- GitHub repository-admin protection/settings: `PENDING`
- Production signing: `PENDING`
- Store assets/declarations: `PENDING`
- Known release blockers/evidence gaps: `manual/admin/production/stable-promotion evidence listed above remains pending`
- Final decision: `PENDING`
- Decision owner: `________________`
- Decision date: `________________`

## Evidence integrity rules

- Never mark a manual item complete because CI passed.
- Never reuse evidence from a different source SHA without explaining why it remains applicable.
- Never include passwords, keystore bytes, private keys or sensitive device/account data in this file.
- If a candidate changes after manual QA, determine which checks must be repeated.
- A stable `v1.0.0` tag/release must point to the exact approved source commit.
- `what_changed.md` must record the final approved SHA, workflow IDs, merge/tag/release evidence and the boundary of any checks that were not performed.
