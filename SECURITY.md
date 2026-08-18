# Security Policy

## Supported Versions

SudokuNova is currently pre-1.0. Security fixes are applied to the latest actively developed branch and then included in the next release. After 1.0, this section will list maintained release lines explicitly.

Current hardening line: `v0.9.x` development toward the first stable Classic Sudoku release.

## Reporting a Vulnerability

Please **do not open a public GitHub issue** for an exploitable or suspected security vulnerability that could put users at risk.

Report it privately to:

- Support: `supportramsandesh@gmail.com`
- Business: `sanskarin@outlook.in`

Include, when possible:

- a clear description of the issue;
- affected version/commit;
- Android version/device information when relevant;
- reproduction steps that do not expose private user data;
- expected vs. actual behavior;
- impact assessment;
- any suggested mitigation.

Do not include real credentials, private keys, tokens, or personal user data in a report.

## Responsible Disclosure

Please allow reasonable time for validation and remediation before discussing an exploitable issue publicly. Once a fix is available, the project may document the issue in release notes or a security advisory with enough detail to help users update safely.

## Security Principles

SudokuNova aims to:

- request no unnecessary sensitive Android permissions;
- keep core gameplay offline-first;
- avoid embedded secrets and credentials;
- validate custom/imported data before use;
- bound imported backup data before parsing;
- fail closed when backup integrity or schema validation fails;
- keep dependencies reviewed and updated deliberately;
- review exported Android components and external input paths;
- keep local gameplay data within app-controlled persistence or user-selected transfer files;
- never commit signing credentials or private certificates;
- keep release signing external to ordinary source code and pull-request builds.

## Android Permission and Component Surface

The current base manifest declares no runtime permission requests. The launcher `MainActivity` is exported because Android requires the launcher entry point to be externally discoverable through its `MAIN`/`LAUNCHER` intent filter.

When adding future Android components:

- default components to non-exported unless external invocation is intentionally required;
- do not add broad storage permissions for backup/restore when the Storage Access Framework can provide user-selected document access;
- justify every new permission in code review and release documentation;
- never add accessibility service, notification listener, package visibility, overlay, SMS, call-log, contacts, location, microphone, camera, or network-sensitive permissions without a real product requirement and explicit security review.

## Backup and Transfer Security

SudokuNova backup/restore is local and user-directed.

The transfer layer must preserve these gates:

- input streams are read with a maximum-byte boundary before parsing;
- empty and oversized payloads are rejected;
- backup envelope version and integrity data are validated;
- malformed or unsupported data must fail closed rather than partially restoring untrusted state;
- restored Sudoku boards must continue to pass the same domain validation used by normal app flows;
- reset and restore operations must not silently cross persistence boundaries that were not selected by the user;
- backup files are not treated as executable content.

Changing or removing these limits requires dedicated tests and security review.

## Signing and Secret Management

Production signing material is intentionally absent from source control.

Never commit:

- Android keystores (`.jks`, `.keystore`);
- PKCS#12/private-key bundles (`.p12`, `.pfx`);
- PEM/SSH private keys;
- signing passwords or aliases paired with passwords;
- cloud/service-account credentials;
- API tokens;
- environment dumps containing secrets;
- production `local.properties` or other machine-local credential files.

If CI signing is introduced, secrets must come from protected GitHub repository/environment secrets at runtime. Pull requests from forks must remain safe without secret access. Build logic must not print secret values into Gradle or GitHub Actions logs.

## Release Hardening

The v0.9 release-hardening gate includes:

- debug and release lint;
- engine and Android JVM tests;
- instrumentation-test compilation;
- API-35 connected tests;
- release APK assembly with minification/resource shrinking;
- release AAB assembly;
- dependency/license review;
- manifest permission/export review;
- backup size/integrity regression tests;
- documentation that distinguishes verified CI results from manual checks not yet performed.

A release-quality claim must be supported by the exact tested commit and workflow evidence.

## Dependency and Supply-Chain Expectations

- Use the committed Gradle wrapper.
- Prefer dependencies already controlled through the version catalog.
- Review dependency additions for maintenance status, license compatibility, transitive risk, and actual necessity.
- Keep `THIRD_PARTY_NOTICES.md` synchronized with shipped third-party software where notices are required.
- Avoid adding analytics, advertising, tracking, or cloud SDKs as incidental dependencies to security/release work.
- Do not disable wrapper validation, lint, R8, tests, or backup validation merely to make CI pass.

## Privacy Expectations

SudokuNova's Classic gameplay, learning progress, statistics, history, saved puzzles, challenges, settings, and backup data are designed to work locally without requiring a cloud account.

Security changes must not silently introduce telemetry, remote profiling, advertising identifiers, or user-data upload. Any future networked feature requires an explicit product/privacy review and documentation update before release.

## Scope

Security reports may cover application code, the Sudoku engine, persistence, file handling, build/release configuration, dependencies, workflows, and project infrastructure.

For ordinary bugs that do not present a security risk, please use GitHub Issues.

**Made by the Sanskar**
