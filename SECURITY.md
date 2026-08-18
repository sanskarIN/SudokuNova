# Security Policy

## Supported Versions

SudokuNova is preparing the first stable Classic Sudoku release through the `v1.0` release-candidate line. Until stable v1.0 is published, security fixes are applied to the latest actively developed branch and included in the next candidate/release as appropriate.

Current release-candidate preparation line: `release/v1.0-rc1-prep` (`1.0.0-rc.1`).

After stable 1.0, this section will list maintained release lines explicitly.

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
- keep production signing external to ordinary source code and pull-request builds;
- fail closed when a release-signing environment is only partially configured;
- keep unsigned CI artifact verification clearly distinct from production-signed release validation.

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

### v1.0 release-signing contract

The Android release build can opt into signing only through these environment variables:

- `SUDOKUNOVA_KEYSTORE_PATH`;
- `SUDOKUNOVA_KEYSTORE_PASSWORD`;
- `SUDOKUNOVA_KEY_ALIAS`;
- `SUDOKUNOVA_KEY_PASSWORD`.

The build intentionally has three states:

- none set → unsigned release verification is allowed for ordinary CI;
- all four set → the release signing configuration is enabled;
- one to three set → Gradle configuration fails immediately.

Standard CI includes a regression check for the partial-configuration failure path. Ordinary pull-request CI intentionally does not receive production signing secrets.

If protected CI signing is introduced later, secrets must come from a protected repository/environment secret store at runtime. Untrusted pull-request code must not receive production signing material. Build logic and workflows must not print secret values into logs, reports, artifacts, or issue/PR output.

See `docs/PRODUCTION_SIGNING.md` for the operational procedure.

## Release Artifact Integrity

The v1.0 RC pipeline does more than check that Gradle returned success.

`scripts/verify_release_outputs.py` verifies the generated release outputs by checking:

- expected APK/AAB archive structure;
- non-empty R8 mapping output;
- exact candidate version metadata from APK output metadata;
- SHA-256 and byte-size evidence for APK, AAB, and mapping.

This protects against publishing or reviewing an unexpected/missing artifact path or version. It does **not** prove production certificate identity, device installability, Play Console acceptance, or rollout safety.

Production-signed APK certificate identity must be verified separately with Android signing tools and recorded in the v1.0 release-candidate evidence worksheet.

## Automated Release Gates

The v1.0 RC repository-side gate includes:

- repository secret/signing-material guard;
- release-output verifier unit tests;
- partial release-signing fail-closed regression;
- English/Hindi translation parity;
- engine and Android JVM tests;
- instrumentation-test compilation;
- debug and release lint;
- API-35 connected tests;
- debug APK assembly;
- release APK assembly with minification/resource shrinking;
- release AAB assembly;
- release APK/AAB/R8 structural/version verification;
- SHA-256 release evidence generation;
- dependency/license review carried forward from v0.9;
- manifest permission/export review;
- backup size/integrity regression coverage;
- documentation that distinguishes automated results from manual/production checks not yet performed.

A release-quality claim must be supported by the exact tested commit and workflow evidence. Stable v1.0 additionally requires the real manual/production checks in `docs/V1_RELEASE_CANDIDATE.md`.

## GitHub Repository Security Settings

Source-controlled security policy cannot enforce every GitHub administrative setting.

At the beginning of v1.0 RC preparation, the GitHub API reported that `main` was not protected. The connected repository tool used during this preparation does not expose branch-protection/ruleset mutation, so protection must be enabled through repository administration and later recorded as actual evidence.

Recommended settings are documented in `docs/GITHUB_REPOSITORY_SETTINGS.md`, including required Android CI/API-35 checks, force-push/deletion protection, least-privilege Actions settings, and secret-scanning/security-feature review.

Do not describe these settings as enabled until GitHub actually reports them enabled.

## Dependency and Supply-Chain Expectations

- Use the committed Gradle wrapper.
- Prefer dependencies already controlled through the version catalog.
- Review dependency additions for maintenance status, license compatibility, transitive risk, and actual necessity.
- Keep `THIRD_PARTY_NOTICES.md` synchronized with shipped third-party software where notices are required.
- Review Dependabot updates deliberately rather than blindly merging all toolchain changes.
- Avoid adding analytics, advertising, tracking, or cloud SDKs as incidental dependencies to security/release work.
- Do not disable wrapper validation, lint, R8, tests, release-output verification, signing fail-closed behavior, or backup validation merely to make CI pass.
- Third-party GitHub Actions must not receive production signing secrets in untrusted pull-request contexts.

## Privacy Expectations

SudokuNova's Classic gameplay, learning progress, statistics, history, saved puzzles, challenges, settings, and backup data are designed to work locally without requiring a cloud account.

Security changes must not silently introduce telemetry, remote profiling, advertising identifiers, or user-data upload. Any future networked feature requires an explicit product/privacy review and documentation update before release.

Store/privacy declarations must describe the exact production binary rather than assumptions from an older development build.

## Stable v1.0 Evidence Boundary

Repository automation can prove source/build/artifact properties. It cannot by itself prove:

- real TalkBack traversal quality;
- representative 200% font/device/window behavior;
- high-contrast/reduced-motion experience on real targets;
- measured startup/frame/memory/ANR performance;
- process-death behavior on representative targets;
- intended production certificate identity;
- signed production artifact installation;
- store-side AAB validation;
- real listing/privacy/screenshot accuracy;
- production rollout safety.

Those checks remain pending until performed and recorded in `docs/V1_RELEASE_CANDIDATE.md`. A stable `v1.0.0` tag/publication must not be created solely from green repository CI.

## Scope

Security reports may cover application code, the Sudoku engine, persistence, file handling, build/release configuration, dependencies, workflows, signing configuration, release artifact verification, and project infrastructure.

For ordinary bugs that do not present a security risk, please use GitHub Issues.

**Made by the Sanskar**
