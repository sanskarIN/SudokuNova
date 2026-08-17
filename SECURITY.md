# Security Policy

## Supported Versions

SudokuNova is currently pre-1.0. Security fixes are applied to the latest actively developed branch and then included in the next release. After 1.0, this section will list maintained release lines explicitly.

## Reporting a Vulnerability

Please **do not open a public GitHub issue** for an exploitable or suspected security vulnerability that could put users at risk.

Report it privately to:

- Support: `supportramsandesh@gmail.com`
- Business: `sanskarin@outlook.in`

Include, when possible:

- A clear description of the issue
- Affected version/commit
- Android version/device information when relevant
- Reproduction steps that do not expose private user data
- Expected vs. actual behavior
- Impact assessment
- Any suggested mitigation

Do not include real credentials, private keys, tokens, or personal user data in a report.

## Responsible Disclosure

Please allow reasonable time for validation and remediation before discussing an exploitable issue publicly. Once a fix is available, the project may document the issue in release notes or a security advisory with enough detail to help users update safely.

## Security Principles

SudokuNova aims to:

- Request no unnecessary sensitive Android permissions
- Keep core gameplay offline-first
- Avoid embedded secrets and credentials
- Validate custom/imported data before use
- Keep dependencies reviewed and updated deliberately
- Review exported Android components and external input paths
- Use local storage only for the current base application's gameplay data
- Never commit signing credentials or private certificates

## Scope

Security reports may cover application code, the Sudoku engine, persistence, file handling, build/release configuration, dependencies, and project infrastructure.

For ordinary bugs that do not present a security risk, please use GitHub Issues.

**Made by the Sanskar**
