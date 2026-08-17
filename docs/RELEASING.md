# Releasing SudokuNova

SudokuNova is currently pre-1.0. This guide defines the intended controlled release process; it does not imply that a production release has already been signed or published.

## Versioning

Use Semantic Versioning where practical:

- `0.x.y` during development
- `1.0.0` first stable Classic release
- `1.x.0` backward-compatible feature releases
- `1.x.y` bug/security fixes
- `2.0.0` for intentionally incompatible product/data/API changes when warranted

Android `versionCode` must increase monotonically for distributed builds.

## Before a Release Candidate

- Freeze the intended scope.
- Ensure `ROADMAP.md` reflects what moved in/out.
- Update `CHANGELOG.md` with actual user-visible changes.
- Remove accidental placeholders/dead code.
- Review dependency/license notices.
- Complete `RELEASE_CHECKLIST.md` and `QA_MATRIX.md`.

## Verification

At minimum:

```bash
./gradlew clean
./gradlew :sudoku-engine:test :app:testDebugUnitTest :app:lintDebug :app:assembleDebug
./gradlew :app:assembleRelease :app:bundleRelease
```

Release builds require dedicated device/emulator smoke testing because code shrinking/resource shrinking can expose problems absent in debug.

## Signing

Never commit production signing material.

Production signing should be provided through secure local/CI secrets, including only what the build needs at runtime. Do not print passwords/keystore contents into logs.

The repository intentionally does not contain:

- Release keystore
- Keystore password
- Key password
- Private signing certificate material

## APK and AAB

Build outputs:

```bash
./gradlew :app:assembleRelease
./gradlew :app:bundleRelease
```

Validate the final signed artifact, not only an unsigned/intermediate output.

## Git Tag

After the exact release commit is verified:

```bash
git tag -a vX.Y.Z -m "SudokuNova vX.Y.Z"
git push origin vX.Y.Z
```

Do not tag a commit before its release checks complete.

## GitHub Release

A GitHub Release should include:

- Version/tag
- Concise release notes derived from `CHANGELOG.md`
- Compatibility/minimum Android information
- Known limitations
- Checksums for distributed binary artifacts when binaries are attached
- Links to source/license/privacy/security/support information

## Play Store Preparation

Before a store submission, verify current Google Play requirements at release time. Store listing assets should match the actual application UI. Do not publish screenshots of mock/unimplemented features.

Prepare:

- App name/description
- App icon/feature graphic
- Real screenshots
- Privacy policy URL/content matching behavior
- Data safety answers matching the shipped binary
- Content rating declarations
- Signed AAB
- Release notes

## Rollback

If a bad release is discovered:

1. Stop/promote rollout only as supported by the distribution platform.
2. Identify whether the issue affects data integrity/security.
3. Fix forward with a higher version code.
4. Add regression coverage.
5. Document the fix in changelog/security notes as appropriate.

A previously distributed Android package generally cannot be replaced with a lower `versionCode` as an ordinary update.
