# Security Design

For vulnerability reporting, use the repository-level [`SECURITY.md`](../SECURITY.md). This document describes engineering practices.

## Current Threat Surface

SudokuNova's open-source base is intentionally small:

- No login/account
- No analytics SDK
- No advertising SDK
- No location/contact/microphone/camera/SMS/call-log permission
- No remote gameplay API
- Local DataStore persistence
- Explicit external browser/email actions from About/Support

Reducing network and permission surface lowers both privacy and security risk.

## Android Manifest

The current manifest exposes the launcher activity as required for the launcher intent filter. Other externally reachable components should remain unexported unless a documented feature needs them.

Future deep links must treat all input as untrusted.

## Secrets

Never commit:

- Android signing keystores
- Signing passwords
- GitHub tokens
- API keys
- Service-account credentials
- Private certificates

`.gitignore` contains common exclusions, but exclusion patterns are not a substitute for reviewing commits.

## Custom / Imported Puzzle Data

The custom editor validates values, row/column/box consistency, solvability, and uniqueness before enabling the current Play flow.

Future file/import/backup support must additionally enforce:

- File-size limits
- Schema/version checks
- Numeric/string bounds
- Controlled parsing
- Exception handling
- Rejection of unexpected fields/types when appropriate
- No executable content handling

## Dependencies

Dependabot is configured for Gradle and GitHub Actions. Updates should still be reviewed rather than merged blindly, especially major version changes.

## Local Storage

Gameplay data is not sensitive authentication material. If future account/cloud features introduce tokens or sensitive data, they must use appropriate Android security mechanisms and documented lifecycle/rotation behavior.

## Release Security Checks

Before stable release:

- Review exported components
- Review permissions
- Review deep links/import paths
- Scan for committed secrets
- Review dependency advisories
- Test release shrinking
- Ensure debug-only tooling is absent/disabled in release
- Verify privacy documentation matches actual behavior
- Verify signing credentials are supplied only through secure release infrastructure
