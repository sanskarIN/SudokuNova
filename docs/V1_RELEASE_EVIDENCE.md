# SudokuNova v1.0 Release Evidence Ledger

This is the concise exact-evidence ledger for the first stable SudokuNova release. Use [v1.0 Release Candidate Evidence Worksheet](V1_RELEASE_CANDIDATE.md) for the detailed manual test matrix.

## Current candidate

- Branch: `release/v1.0-rc1-prep`
- Draft PR: `#27`
- Candidate versionName: `1.0.0-rc.1`
- Candidate versionCode: `1000`
- Application ID: `in.sanskar.sudokunova`
- Namespace: `com.sanskar.sudokunova`
- Minimum Android API: `26`
- Compile/target SDK: `37`
- Stable `v1.0.0`: **not yet claimed**

If version code `1000` is accepted by a distribution track, the stable build must use a strictly higher version code.

## Repository-side RC evidence

Record results only for the exact final PR #27 head.

| Gate | Exact head | Run/evidence | Result |
|---|---|---|---|
| Repository secret guard | Pending final RC head | Pending | PENDING |
| Release-verifier Python tests | Pending final RC head | Pending | PENDING |
| Partial signing fails closed | Pending final RC head | Pending | PENDING |
| English/Hindi parity | Pending final RC head | Pending | PENDING |
| Sudoku engine tests | Pending final RC head | Pending | PENDING |
| Android JVM tests | Pending final RC head | Pending | PENDING |
| AndroidTest compilation | Pending final RC head | Pending | PENDING |
| Debug + release lint | Pending final RC head | Pending | PENDING |
| Debug APK | Pending final RC head | Pending | PENDING |
| R8/resource-shrunk release APK | Pending final RC head | Pending | PENDING |
| Release AAB | Pending final RC head | Pending | PENDING |
| APK/AAB/R8 structure/version check | Pending final RC head | Pending | PENDING |
| SHA-256/size evidence generation | Pending final RC head | Pending | PENDING |
| API-35 connected Compose/Room | Pending final RC head | Pending | PENDING |

When the branch head changes after a green run, the previous run becomes historical evidence only.

## RC build artifact evidence

Normal PR CI verifies unsigned build outputs using:

```bash
python scripts/verify_release_outputs.py \
  --apk app/build/outputs/apk/release/app-release-unsigned.apk \
  --aab app/build/outputs/bundle/release/app-release.aab \
  --mapping app/build/outputs/mapping/release/mapping.txt \
  --metadata app/build/outputs/apk/release/output-metadata.json \
  --expected-version-code 1000 \
  --expected-version-name 1.0.0-rc.1 \
  --output app/build/outputs/release-evidence/sha256.txt
```

Record from the exact approved candidate:

- APK SHA-256: `PENDING`
- AAB SHA-256: `PENDING`
- R8 mapping SHA-256: `PENDING`
- CI artifact name/id: `PENDING`

## Production-signed artifact evidence

For the final signed artifacts, the same verifier can require platform signature checks:

```bash
python scripts/verify_release_outputs.py \
  --apk path/to/signed-release.apk \
  --aab path/to/signed-release.aab \
  --mapping path/to/mapping.txt \
  --metadata path/to/output-metadata.json \
  --expected-version-code <final-code> \
  --expected-version-name 1.0.0 \
  --output path/to/sha256.txt \
  --require-signatures
```

`--require-signatures` requires:

- `apksigner` verification for the APK;
- `jarsigner` verification for the AAB.

Production evidence:

- Production/upload signing configured outside Git: `PENDING`
- APK signature verification: `PENDING`
- AAB signature verification: `PENDING`
- Expected certificate fingerprint/digest confirmed: `PENDING`
- Signed APK install/launch smoke: `PENDING`
- AAB testing/store-track validation: `PENDING`

Never record passwords, keystore bytes, private keys, tokens, or other secrets here.

## Manual device and accessibility evidence

These require actual target execution. CI does not satisfy them.

| Check | Target / OS | Evidence | Result |
|---|---|---|---|
| Fresh install / launch | Pending | Pending | PENDING |
| Upgrade from supported prior build | Pending | Pending | PENDING |
| Active-game process-death restoration | Pending | Pending | PENDING |
| TalkBack core navigation/focus order | Pending | Pending | PENDING |
| TalkBack Sudoku/hint semantics | Pending | Pending | PENDING |
| 200% font scale | Pending | Pending | PENDING |
| Narrow phone/window | Pending | Pending | PENDING |
| Large phone/window | Pending | Pending | PENDING |
| Tablet / large window | Pending | Pending | PENDING |
| Portrait/landscape/resize | Pending | Pending | PENDING |
| High Contrast | Pending | Pending | PENDING |
| Reduced Motion | Pending | Pending | PENDING |
| Hardware keyboard | Pending | Pending | PENDING |
| Release-only R8 smoke | Pending | Pending | PENDING |
| Backup export/restore | Pending | Pending | PENDING |
| Puzzle import/share | Pending | Pending | PENDING |

The detailed checklist and notes fields are in `V1_RELEASE_CANDIDATE.md`.

## Measured performance evidence

Status: **PENDING real measurement**.

Record target, exact commit, input, method and result for:

- startup;
- gameplay/navigation frame behavior;
- puzzle generation;
- hint computation;
- Custom Puzzle solving/validation;
- bounded backup import/restore;
- History/Saved Puzzles with representative records;
- memory behavior;
- ANR observations.

Do not convert subjective observations into invented benchmark numbers.

## Repository settings evidence

At RC-prep start, GitHub reported `main` as unprotected. The connected repository tool used for this work does not expose branch-protection/ruleset mutation.

- `main` protection/ruleset enabled: `PENDING`
- Required `Android CI / verify`: `PENDING`
- Required `Android Instrumentation / connected-tests`: `PENDING`
- Force-push/deletion restrictions reviewed: `PENDING`
- Security/Dependabot/secret-scanning settings reviewed: `PENDING`

See `GITHUB_REPOSITORY_SETTINGS.md`.

## Store / publication evidence

Status: **PENDING**.

Before completion record:

- actual screenshots from the final release UI;
- final listing title/short/full description;
- public privacy policy URL and binary-policy match;
- current data/privacy declarations;
- current content/app-access/other required declarations;
- target API acceptance;
- support contact;
- final release notes;
- final signed AAB upload/test result;
- staged rollout/publication state.

See `PLAY_STORE_RELEASE.md`.

## Stable promotion gate

Promote to stable `1.0.0` only when:

1. repository-side RC preparation is merged from an exact green head;
2. any stable-metadata/source changes receive fresh exact-head Android CI and API-35 verification;
3. release APK/AAB/R8 integrity/hash evidence exists;
4. required device/accessibility/lifecycle QA is recorded;
5. measured release performance has no release-blocking defect;
6. production signing is configured outside Git and signed artifacts are verified;
7. repository/store release assets represent the actual release UI;
8. no release-blocking issue remains;
9. final versionCode is higher than every accepted distributed candidate code;
10. README, changelog, roadmap, release notes and `what_changed.md` match the final stable source commit;
11. final decision in `V1_RELEASE_CANDIDATE.md` is `SHIP`.

Until those conditions are satisfied, SudokuNova remains in release-candidate preparation rather than a stable-production claim.
