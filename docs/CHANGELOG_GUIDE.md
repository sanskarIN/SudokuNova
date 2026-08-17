# Changelog Guide

`CHANGELOG.md` records notable user/developer-facing release changes. It is not a dump of every Git commit.

## Sections

Use relevant categories:

- Added
- Changed
- Fixed
- Performance
- Accessibility
- Security
- Documentation
- Deprecated
- Removed

## Unreleased First

Keep ongoing user-visible work under `[Unreleased]`. When releasing:

1. Confirm entries describe behavior that actually shipped.
2. Move relevant entries into a version section.
3. Add the release date in `YYYY-MM-DD` format.
4. Start/retain a new `[Unreleased]` section for subsequent work.

## What Belongs

Include:

- New gameplay feature
- Behavior change
- Important bug fix
- Data migration
- Performance improvement users/contributors may notice
- Accessibility improvement
- Security fix/advisory at an appropriate disclosure level
- Toolchain/build requirement change
- Major documentation/release-process change

Usually omit:

- Formatting-only changes
- Typo fixes with no meaningful effect
- Mechanical refactors with no public/developer impact
- Every small test addition unless it represents significant coverage

## Wording

Prefer outcome-oriented language:

Good:

- `Fixed active-game restoration after malformed save data.`
- `Added deterministic Daily Challenge generation for offline play.`

Avoid vague entries:

- `Fixed bugs.`
- `Updated code.`

## Relationship to `what_changed.md`

- `CHANGELOG.md`: release/user-facing notable change history.
- `what_changed.md`: active project implementation log, files, tests, CI findings, remaining work.
- Git history: atomic technical history.

Do not substitute one for all three.

## Security

Do not publish exploit-enabling details before a security fix/disclosure is ready. Coordinate with `SECURITY.md`.
