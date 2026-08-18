# Production Signing

SudokuNova keeps production signing material completely outside Git. The repository can build unsigned release artifacts in ordinary CI, while a trusted local/release environment can opt into signing through four environment variables.

## Required environment variables

All four values must be supplied together:

- `SUDOKUNOVA_KEYSTORE_PATH` — absolute or project-resolvable path to the release keystore.
- `SUDOKUNOVA_KEYSTORE_PASSWORD` — keystore password.
- `SUDOKUNOVA_KEY_ALIAS` — release key alias.
- `SUDOKUNOVA_KEY_PASSWORD` — key password.

If none are supplied, the release build remains unsigned and is suitable for reproducible CI verification only.

If only some are supplied, Gradle fails immediately. SudokuNova deliberately does not silently fall back to an unsigned build when a release-signing environment appears to be partially configured.

## Local PowerShell example

Set values only in the current terminal/session or a secure secret-management layer. Do not commit them to scripts, Gradle files, shell profiles, screenshots, issue comments, build logs, or documentation copies.

```powershell
$env:SUDOKUNOVA_KEYSTORE_PATH = "C:\secure\sudokunova-release.jks"
$env:SUDOKUNOVA_KEYSTORE_PASSWORD = "<secret>"
$env:SUDOKUNOVA_KEY_ALIAS = "<alias>"
$env:SUDOKUNOVA_KEY_PASSWORD = "<secret>"
.\gradlew.bat clean :app:assembleRelease :app:bundleRelease
```

Clear sensitive variables after the release session:

```powershell
Remove-Item Env:SUDOKUNOVA_KEYSTORE_PATH
Remove-Item Env:SUDOKUNOVA_KEYSTORE_PASSWORD
Remove-Item Env:SUDOKUNOVA_KEY_ALIAS
Remove-Item Env:SUDOKUNOVA_KEY_PASSWORD
```

## Linux/macOS example

```bash
export SUDOKUNOVA_KEYSTORE_PATH="/secure/sudokunova-release.jks"
export SUDOKUNOVA_KEYSTORE_PASSWORD="<secret>"
export SUDOKUNOVA_KEY_ALIAS="<alias>"
export SUDOKUNOVA_KEY_PASSWORD="<secret>"
./gradlew clean :app:assembleRelease :app:bundleRelease
unset SUDOKUNOVA_KEYSTORE_PATH SUDOKUNOVA_KEYSTORE_PASSWORD \
  SUDOKUNOVA_KEY_ALIAS SUDOKUNOVA_KEY_PASSWORD
```

## GitHub Actions / CI

The normal repository CI intentionally does not receive production signing secrets. It verifies an unsigned release APK/AAB, R8 mapping, version metadata, archive structure, and SHA-256 evidence.

If a future protected release workflow is introduced, it must:

1. run only from a protected/manual release context;
2. reconstruct or mount the keystore from a GitHub Environment or equivalent secret store;
3. expose the four variables only for the release-build step;
4. avoid printing secret values or keystore contents;
5. delete temporary keystore material after use;
6. verify the signed APK/AAB before publication;
7. retain only intended release artifacts and checksums;
8. never run untrusted pull-request code with production secrets.

## Keystore handling

The repository `.gitignore` and security verifier reject common keystore/private-key paths and obvious secret material. These are defense-in-depth controls, not permission to store secrets in the working tree.

Recommended operational rules:

- keep the authoritative keystore in encrypted, access-controlled storage;
- maintain a tested recovery backup in a separate secure location;
- restrict access to the smallest possible maintainer set;
- record alias/certificate fingerprints in a secure release record;
- never regenerate/replace the production key casually after public distribution;
- use Play App Signing where appropriate, while protecting the upload key separately.

## Verifying signed APKs

Use Android SDK Build Tools `apksigner` on the actual signed APK:

```bash
apksigner verify --verbose --print-certs app-release.apk
```

Record the certificate digest/fingerprint in private release evidence and confirm it matches the expected production/upload certificate.

For AABs, use the signing/verification tooling required by the distribution workflow and verify the bundle before upload. A successful Gradle build alone is not evidence that the intended production key was used.

## Version-code rule

The v1.0 RC branch uses `versionCode 1000` and `versionName 1.0.0-rc.1`. If that exact version code is uploaded to a distribution track that reserves it, the later stable build must use a strictly higher `versionCode`.

Do not reuse a version code that a store has already accepted.

## Release evidence boundary

Repository CI can prove:

- release compilation succeeds;
- R8/resource shrinking succeeds;
- APK/AAB archives have expected structural entries;
- APK output metadata has the expected version;
- mapping output exists;
- SHA-256 evidence is generated.

Repository CI without production secrets cannot prove:

- production certificate identity;
- installability of the signed production artifact on representative devices;
- Play Console acceptance;
- production rollout safety.

Those checks remain mandatory before the stable v1.0 publication claim.
