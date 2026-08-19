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

## GitHub Actions / protected release validation

The normal repository CI intentionally does not receive production signing secrets. It verifies an unsigned release APK/AAB, R8 mapping, version metadata, archive structure, and SHA-256 evidence.

The repository also contains a separate manual workflow, `.github/workflows/release-validation.yml`, named **Production Release Validation**. It is designed to run through a protected GitHub Environment named `production-release` and must not be exposed to untrusted pull-request code.

The protected workflow:

1. requires every configured release secret before doing release work;
2. reconstructs the keystore only under `$RUNNER_TEMP` with restrictive permissions;
3. exports the temporary keystore path to the existing Gradle release-signing contract;
4. runs the repository security guard, release-verifier tests, and translation parity check;
5. builds the signed release APK and AAB with R8/resource shrinking;
6. verifies APK and AAB cryptographic signatures;
7. compares APK and AAB signer certificate SHA-256 fingerprints with protected expected values;
8. records hashes, sizes, signer fingerprints, exact commit/ref, and workflow-run context;
9. uploads non-secret verification evidence for 30 days;
10. uploads signed release binaries only when the workflow operator explicitly opts in;
11. removes the materialized keystore in an `always()` cleanup step.

See [Production Release Validation Workflow](PRODUCTION_RELEASE_VALIDATION.md) for environment configuration, secret names, evidence files, and operation rules.

## Keystore handling

The repository `.gitignore` and security verifier reject common keystore/private-key paths and obvious secret material. These are defense-in-depth controls, not permission to store secrets in the working tree.

Recommended operational rules:

- keep the authoritative keystore in encrypted, access-controlled storage;
- maintain a tested recovery backup in a separate secure location;
- restrict access to the smallest possible maintainer set;
- record alias/certificate fingerprints in a secure release record;
- never regenerate/replace the production key casually after public distribution;
- use Play App Signing where appropriate, while protecting the upload key separately.

## Verifying signed APK/AAB artifacts

Use Android SDK Build Tools `apksigner` on the actual signed APK:

```bash
apksigner verify --verbose --print-certs app-release.apk
```

Record the certificate digest/fingerprint and confirm it matches the expected production/upload certificate.

For AABs, verify JAR-signature integrity with the JDK tooling and inspect the signer certificate with `keytool`. Complete the distribution platform's own bundle validation before upload. A successful Gradle build alone is not evidence that the intended production key was used.

The v1.0 release verifier can require both signature checks, bind them to expected certificate identities, and retain the stricter structure/version/mapping/hash checks:

```bash
python scripts/verify_release_outputs.py \
  --apk path/to/signed-release.apk \
  --aab path/to/signed-release.aab \
  --mapping path/to/mapping.txt \
  --metadata path/to/output-metadata.json \
  --expected-version-code <final-version-code> \
  --expected-version-name 1.0.0 \
  --output path/to/sha256.txt \
  --require-signatures \
  --expected-apk-cert-sha256 <expected-apk-cert-sha256> \
  --expected-aab-cert-sha256 <expected-aab-cert-sha256> \
  --signature-output path/to/signatures.txt
```

With `--require-signatures`:

- APK verification requires `apksigner` on `PATH`, requires a reported signer certificate SHA-256 digest, and fails if signature verification fails;
- AAB verification requires `jarsigner` on `PATH`, requires explicit `jar verified` output, and rejects unsigned output;
- AAB signer-certificate evidence additionally requires `keytool` on `PATH`;
- `--expected-apk-cert-sha256` fails closed if the verified APK is signed by a different certificate;
- `--expected-aab-cert-sha256` fails closed if the verified AAB is signed by a different certificate;
- certificate fingerprints accept hexadecimal with or without colon separators and are normalized to lowercase 64-character SHA-256 hex;
- `--signature-output` writes normalized non-secret signer fingerprint evidence;
- missing verifier tools fail the command rather than silently skipping signature checks.

A cryptographically valid signature from the wrong key is not acceptable release evidence. The expected certificate fingerprint must come from a trusted release record, not from the artifact being validated.

### Play App Signing distinction

If Play App Signing is used, distinguish:

- the **upload-key certificate** used to sign the AAB submitted to Google Play;
- the **app-signing certificate** used by Google Play for APKs delivered to users.

The protected repository workflow proves the identity of the locally produced signed APK/AAB against the configured expected fingerprints. It does not replace validation of the Play-distributed artifact or Play Console certificate records.

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

A real successful protected signed-release validation run can additionally prove, for its exact selected ref and generated artifacts:

- the APK signature is valid;
- the AAB signature is valid;
- the APK signer certificate SHA-256 matches the configured expected identity;
- the AAB signer/upload certificate SHA-256 matches the configured expected identity;
- normalized signature evidence and exact workflow context were recorded.

Repository CI without production secrets cannot prove:

- intended production certificate identity;
- installability of the signed production artifact on representative devices;
- Play Console acceptance;
- production rollout safety.

Even after a successful protected workflow, device/install/accessibility/performance/store checks remain separate real-world evidence requirements. Those checks remain mandatory before the stable v1.0 publication claim and must be recorded in `V1_RELEASE_CANDIDATE.md` / `V1_RELEASE_EVIDENCE.md`.
