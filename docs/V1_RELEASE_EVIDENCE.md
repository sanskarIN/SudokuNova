# SudokuNova v1.0 Release Evidence

This file is the evidence ledger for the first stable SudokuNova release. It distinguishes repository/automated evidence from manual device, signing, and store evidence.

## Release Candidate

- Branch: `release/v1.0-readiness`
- Candidate versionName: `1.0.0-rc1`
- Candidate versionCode: `990`
- Stable `1.0.0` must use a versionCode greater than `990` if this candidate is distributed through a channel that reserves version codes.
- Application ID: `in.sanskar.sudokunova`
- Namespace: `com.sanskar.sudokunova`
- Minimum Android API: 26
- Compile/target SDK: 37

The stable release is **not** yet claimed. Do not change this document to say otherwise until the corresponding evidence exists.

## Automated Repository Evidence

Record only exact-head results.

| Gate | Exact head | Run/evidence | Result |
|---|---|---|---|
| Repository secret guard | Pending RC verification | Pending | Pending |
| Release helper unit tests | Pending RC verification | Pending | Pending |
| English/Hindi parity | Pending RC verification | Pending | Pending |
| Sudoku engine tests | Pending RC verification | Pending | Pending |
| Android JVM tests | Pending RC verification | Pending | Pending |
| AndroidTest compilation | Pending RC verification | Pending | Pending |
| Debug + release lint | Pending RC verification | Pending | Pending |
| Debug APK | Pending RC verification | Pending | Pending |
| R8/resource-shrunk release APK | Pending RC verification | Pending | Pending |
| Release AAB | Pending RC verification | Pending | Pending |
| Release artifact ZIP integrity | Pending RC verification | Pending | Pending |
| SHA-256 checksum generation | Pending RC verification | Pending | Pending |
| API-35 connected Compose/Room | Pending RC verification | Pending | Pending |

When a commit changes after a green run, its old result is historical evidence only and must not be copied into the exact-head row.

## Artifact Evidence

The repository provides:

```bash
python scripts/verify_release_artifacts.py \
  path/to/app-release.apk \
  path/to/app-release.aab \
  --checksums-out SHA256SUMS
```

For final **signed** production artifacts, require signature verification:

```bash
python scripts/verify_release_artifacts.py \
  path/to/signed-release.apk \
  path/to/signed-release.aab \
  --require-signature \
  --checksums-out SHA256SUMS
```

`--require-signature` uses `apksigner` for APK and `jarsigner` for AAB when available. It fails if the required verifier is unavailable or the artifact cannot be verified.

Never commit production keystores, keys, passwords, tokens, or service-account credentials.

## Manual Device / Accessibility Evidence

These rows require an actual target. Automated CI does not count as manual evidence.

| Check | Target / OS | Evidence | Result |
|---|---|---|---|
| Fresh install / launch | Pending | Pending | Pending |
| Upgrade from supported prior build | Pending | Pending | Pending |
| Process-death active-game restoration | Pending | Pending | Pending |
| TalkBack core navigation | Pending | Pending | Pending |
| TalkBack Sudoku cell/hint semantics | Pending | Pending | Pending |
| 200% font scale | Pending | Pending | Pending |
| Narrow phone | Pending | Pending | Pending |
| Large phone | Pending | Pending | Pending |
| Tablet / large window | Pending | Pending | Pending |
| Portrait / landscape or window resize | Pending | Pending | Pending |
| High contrast | Pending | Pending | Pending |
| Reduced motion | Pending | Pending | Pending |
| Hardware keyboard | Pending | Pending | Pending |
| Release-only R8 smoke | Pending | Pending | Pending |
| Backup export/restore | Pending | Pending | Pending |
| Puzzle import/share | Pending | Pending | Pending |

## Measured Performance Evidence

Do not write subjective benchmark claims as measurements.

Record target, exact commit, input, method, and result for:

- startup;
- frame/rendering behavior during gameplay/navigation;
- puzzle generation;
- hint computation;
- large bounded backup import/restore;
- History/Saved Puzzles with substantial local records;
- memory and ANR observations.

Status: **Pending real measurement**.

## Signing Evidence

Production signing status: **Pending**.

Before completion record:

- who/what signing environment performed signing (without exposing secrets);
- signed APK/AAB artifact identity;
- `apksigner` / `jarsigner` verification result;
- SHA-256 checksums;
- signed APK installation smoke result when direct APK distribution is intended;
- signed AAB upload validation result for the selected store/testing track.

## Store / Publication Evidence

Status: **Pending**.

Before completion verify the current store requirements at submission time and record:

- actual screenshots from the release UI;
- icon/feature graphic used;
- final short/full description;
- privacy policy URL and binary-policy match;
- Data Safety answers;
- content rating;
- target API acceptance;
- support contact;
- release notes;
- final signed AAB upload result;
- rollout/publication state.

Do not claim Play Store publication until the store has actually accepted/published the release.

## Stable Promotion Gate

Promote from `1.0.0-rc1` to stable `1.0.0` only when:

1. exact-head Android CI is green;
2. exact-head API-35 connected instrumentation is green;
3. release APK/AAB integrity/checksum evidence exists;
4. required manual device/accessibility/lifecycle QA is recorded;
5. measured release performance has no release-blocking defect;
6. production signing is configured outside Git and final signed artifacts are verified;
7. store/repository release assets represent the actual release UI;
8. no critical/high release blocker is open;
9. final versionCode is higher than any distributed candidate code;
10. release notes, changelog, roadmap, README, and `what_changed.md` match the final release commit.

Until all applicable rows are satisfied, this remains release-candidate preparation rather than a stable-production claim.
