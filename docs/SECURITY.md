# SudokuNova Security Design

For vulnerability reporting, supported-version policy, and responsible disclosure, use the repository-level [`SECURITY.md`](../SECURITY.md). This document explains the technical security model of the current application and v1.0 release-candidate pipeline.

## Security Goals

SudokuNova aims to reduce attack and privacy surface by keeping the open-source base application offline-first and avoiding unnecessary permissions/network dependencies.

Core goals:

- no embedded credentials/secrets;
- no broad storage access for transfer;
- no unnecessary sensitive Android runtime permissions;
- strict validation of external/imported data;
- bounded parsers/file reads;
- unique-solution checks for playable external/custom puzzles;
- explicit Room migrations/data integrity;
- production signing material outside version control;
- all-or-nothing release-signing configuration;
- release builds verified with R8/resource shrinking;
- release APK/AAB/R8 outputs checked for expected structure/version and hashed for evidence;
- dependencies and third-party notices reviewed deliberately.

## Current External Surface

The current base application has:

- no login/account system;
- no SudokuNova-operated gameplay backend;
- no ads SDK;
- no analytics SDK;
- no location/contact/microphone/camera/SMS/call-log permission for core play;
- explicit Android share/document-picker/clipboard flows;
- explicit external GitHub, Buy Me a Coffee, and email actions.

Reducing background network and permission surface lowers risk but does not eliminate the need for input validation and release review.

## Android Manifest / Exported Components

The launcher activity is exported because Android requires launcher intent-filter entry points to be externally launchable.

Other components should remain non-exported unless a documented feature specifically requires external reachability.

When adding an Activity, Service, Receiver, Provider, deep link, or intent filter:

1. decide whether it must be exported;
2. treat all incoming extras/URIs as untrusted;
3. validate scheme/host/path/content where relevant;
4. avoid exposing privileged internal operations;
5. update manifest/security/privacy documentation;
6. add tests for external-input handling.

## Permission Policy

Do not request a sensitive permission merely for convenience.

File import/export uses Android's Storage Access Framework/document-picker content URIs instead of broad storage permission.

Any future sensitive permission must have:

- clear user-facing purpose;
- least-privilege scope;
- runtime handling where required;
- denial-safe behavior;
- privacy/store disclosure updates;
- release QA.

## Puzzle Import Security — `SNP1`

Puzzle codes are untrusted external text.

The codec validates:

- maximum code length;
- exact supported version;
- field structure;
- difficulty enum;
- exactly 81 numeric puzzle cells;
- CRC32 checksum;
- Sudoku board validity.

The Android acceptance flow additionally requires exactly one solution before imported play.

CRC32 provides integrity/error detection; it is not cryptographic authentication.

Do not add executable content, arbitrary class deserialization, or URLs with implicit execution semantics to puzzle codes.

## Backup Security — `SNB1`

User backup text is untrusted during restore.

Current controls include:

- `2 MiB` maximum UTF-8 size;
- bounded stream reading;
- exact version/header/footer structure;
- CRC32 integrity check;
- bounded record counts;
- bounded line count;
- bounded text fields;
- bounded counters/timestamps;
- strict enum/boolean/numeric parsing;
- puzzle/solution validation;
- rejection of unknown record types;
- duplicate-safe repository restore behavior.

`SNB1` is **not encrypted**. Security documentation must not imply confidentiality.

## File I/O

External document reads must remain bounded. Avoid unbounded `readBytes()`/`readText()` for user-selected files.

File I/O and restore work should run off the main thread to avoid ANR/resource abuse.

Do not trust a MIME type or filename extension as proof that content is safe/valid.

## Database and Local Persistence

Room/DataStore contain gameplay data, not authentication secrets, but integrity still matters.

Rules:

- no destructive migration fallback as a shortcut;
- preserve explicit migrations;
- retain schema history;
- validate restored records before insert/update;
- preserve uniqueness/provenance constraints;
- use DataStore for bounded lightweight values, not arbitrary large external blobs;
- avoid logging backup/user content unnecessarily.

If future account credentials/tokens are introduced, they require a separate secure-storage/lifecycle design and privacy/security review.

## Replay / Statistics Integrity

Replay provenance is security-adjacent data integrity: restored/replayed games should not be able to masquerade as unrelated first completions and corrupt aggregate records.

Changes to backup/history/replay logic should retain regression coverage for provenance and Favorite behavior.

## Main-Thread Abuse / ANR

Untrusted or expensive work can become a denial-of-service vector even without a network attacker.

Keep CPU-heavy solver/generator/hint analysis on background CPU dispatchers and file/database work on appropriate asynchronous/I/O paths.

Use explicit input and loop bounds. See `PERFORMANCE.md`.

## Secrets and Repository Guard

Never commit:

- Android production keystores;
- private keys;
- keystore passwords;
- key passwords;
- API tokens;
- service-account credentials;
- private certificates;
- production secret configuration.

Standard CI runs:

```bash
python scripts/verify_no_secrets.py
```

The script rejects committed signing/private-key file patterns and obvious credential-like content covered by the repository guard.

This is defense in depth; maintainers must still review commits and repository/platform secret-scanning alerts.

## v1.0 Release Signing

Production signing belongs in secure local/protected release infrastructure.

The current build reads these environment variables only when release signing is intentionally configured:

- `SUDOKUNOVA_KEYSTORE_PATH`;
- `SUDOKUNOVA_KEYSTORE_PASSWORD`;
- `SUDOKUNOVA_KEY_ALIAS`;
- `SUDOKUNOVA_KEY_PASSWORD`.

The configuration is fail closed:

- no variables → unsigned release verification is allowed;
- all four variables → release signing is enabled;
- one to three variables → Gradle configuration fails.

CI includes a regression test for the partial-signing failure path.

Requirements for real production signing:

- secrets supplied at runtime;
- no password echoing in logs;
- least-privilege access;
- protected keystore backup/recovery outside the repository;
- final signed artifact verified;
- expected certificate fingerprint recorded securely;
- no debug signing presented as production signing;
- no production secrets exposed to ordinary/untrusted pull-request workflows.

See [Production Signing](PRODUCTION_SIGNING.md).

## Release Artifact Verification

The v1.0 RC line includes `scripts/verify_release_outputs.py`.

After Gradle builds the release outputs, the verifier requires:

- a non-empty ZIP-valid release APK;
- expected APK manifest and primary DEX entries;
- a non-empty ZIP-valid release AAB;
- expected AAB bundle config, base manifest and primary DEX entries;
- a non-empty R8 `mapping.txt`;
- exactly one APK release metadata element;
- exact expected candidate version code/name;
- SHA-256 plus byte-size evidence for APK, AAB and mapping.

For RC1 the expected metadata is:

- `versionCode 1000`;
- `versionName 1.0.0-rc.1`.

This catches missing, corrupt, wrong-version or unexpected-path release outputs. It does **not** prove:

- production signing certificate identity;
- signed APK installability;
- distribution-platform AAB acceptance;
- production rollout safety.

Those remain manual/production gates in `V1_RELEASE_CANDIDATE.md`.

## Dependency and Supply-Chain Review

Dependencies/plugins/actions should be reviewed before adoption/update.

Review:

- official source/release information;
- known security advisories;
- license compatibility;
- transitive behavior;
- Android/Gradle/Kotlin compatibility;
- new permissions/network behavior;
- R8/consumer rules;
- action permissions.

Keep GitHub Actions `permissions` minimal for the job's purpose.

`THIRD_PARTY_NOTICES.md` must be updated when new redistributed material requires notice.

Dependabot is configured for Gradle and GitHub Actions, but dependency automation does not remove the need for deliberate review.

## GitHub Repository Settings Boundary

Some controls live outside source control.

At v1.0 RC preparation start, the GitHub API reported `main` as unprotected. The connected GitHub tool used for repository work does not expose a branch-protection/ruleset mutation action.

Recommended `main` protection, required status checks, force-push/deletion restrictions, Actions permissions and security-feature settings are documented in [GitHub Repository Settings](GITHUB_REPOSITORY_SETTINGS.md).

Do not mark those settings enabled until GitHub actually reports them enabled.

## R8 / Release Shrinking

The release build enables minification and resource shrinking.

Security/correctness review must verify release builds rather than assuming debug behavior carries over.

Watch for:

- reflection-dependent code removed/renamed;
- serialization/model assumptions;
- resource removal;
- debug-only behavior leaking into release;
- stack-trace de-obfuscation needs.

Store R8 mapping output securely with the exact build provenance when needed for crash analysis.

## Logging

Do not log:

- backup content;
- full user-created puzzle titles if unnecessary;
- secrets/tokens;
- signing values;
- private support-report data.

For debugging external-format errors, prefer structural error categories/lengths/checksum state over dumping the entire payload.

## Privacy Boundary

Security and privacy documentation must agree.

If a new feature adds:

- analytics;
- crash-report transmission;
- cloud sync;
- accounts;
- ads;
- remote multiplayer/leaderboards;
- new sensitive permissions;

then update `PRIVACY.md`, root `SECURITY.md` as applicable, store disclosures, third-party notices, and release QA before shipping.

## Automated Security / Release Checklist

For the v1.0 RC repository-side gate verify:

- repository secret guard;
- release-verifier unit tests;
- partial-signing fail-closed regression;
- manifest permission/export audit remains valid;
- malformed `SNP1` inputs;
- non-unique imported puzzle rejection;
- malformed/oversized `SNB1` inputs;
- bounded file reads;
- duplicate restore behavior;
- Room migration integrity;
- debug/release lint;
- release R8 APK/AAB builds;
- release APK/AAB/R8 structural/version/hash evidence;
- API-35 connected tests;
- dependency/license review;
- Android backup rule/privacy alignment.

## Manual / Production Security Checklist

Before stable v1.0 publication also verify on real release targets:

- production signing values supplied through the intended secure environment;
- signed APK certificate fingerprint matches the intended key;
- signed APK install/launch smoke test passes where applicable;
- AAB passes the distribution platform's validation;
- no secret values appear in logs/artifacts;
- screenshots/listing/privacy declarations reveal no private/internal data and match the binary;
- required GitHub repository protections are actually enabled if chosen as a release requirement.

Record results in `V1_RELEASE_CANDIDATE.md`; do not infer them from green CI.

## Reporting a Vulnerability

Do not post exploit-sensitive details in a public issue.

Follow [`../SECURITY.md`](../SECURITY.md) for the current private reporting channels and responsible disclosure policy.
