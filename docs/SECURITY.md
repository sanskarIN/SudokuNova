# SudokuNova Security Design

For vulnerability reporting, supported-version policy, and responsible disclosure, use the repository-level [`SECURITY.md`](../SECURITY.md). This document explains the technical security model of the current application.

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
- release builds verified with R8/resource shrinking;
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

## Secrets and Signing

Never commit:

- Android production keystores;
- private keys;
- keystore passwords;
- key passwords;
- API tokens;
- service-account credentials;
- private certificates;
- production secret configuration.

The v0.9 standard CI runs:

```bash
python scripts/verify_no_secrets.py
```

The script rejects committed signing/private-key file patterns and obvious credential-like content covered by the repository guard.

This is defense-in-depth; maintainers must still review commits and repository/platform secret-scanning alerts.

## Release Signing

Production signing belongs in secure local/CI release infrastructure.

Requirements:

- secrets supplied at runtime;
- no password echoing in logs;
- least-privilege access;
- protected keystore backup/recovery outside the repository;
- final signed artifact verified;
- no debug signing presented as production signing.

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

## Security Testing Checklist

For release hardening verify:

- repository secret guard;
- manifest permission/export audit;
- malformed `SNP1` inputs;
- non-unique imported puzzle rejection;
- malformed/oversized `SNB1` inputs;
- bounded file reads;
- duplicate restore behavior;
- Room migration integrity;
- release R8 APK/AAB builds;
- dependency/license review;
- Android backup rule/privacy alignment.

## Reporting a Vulnerability

Do not post exploit-sensitive details in a public issue.

Follow [`../SECURITY.md`](../SECURITY.md) for the current private reporting channels and responsible disclosure policy.
