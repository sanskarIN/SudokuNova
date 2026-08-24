# Production Release Validation Workflow

SudokuNova includes a manual, protected GitHub Actions workflow at `.github/workflows/release-validation.yml` for validating a release build signed with the intended production/upload key.

This workflow is deliberately separate from ordinary pull-request CI. Untrusted pull-request code must never receive production signing material.

## What a successful run verifies

A successful run on an exact selected ref proves for artifacts produced by that run that:

- repository secret guard passes;
- release-contract and release-verifier regression tests pass;
- English/Hindi translation parity passes;
- source, ordinary CI, protected defaults, and Desktop package version remain synchronized where applicable;
- release APK/AAB build with R8/resource shrinking;
- exactly one signed release APK and AAB are selected;
- APK/AAB archive structures are valid;
- APK output metadata and embedded manifest report the expected application/version identity;
- embedded minSdk/targetSdk match source expectations;
- release APK reports `debuggable=false`;
- R8 `mapping.txt` exists and is non-empty;
- APK passes `apksigner verify --verbose --print-certs`;
- APK reports at least one verified v2-or-newer signature scheme;
- AAB passes `jarsigner -verify -certs`;
- APK/AAB signer certificate SHA-256 values match protected expected fingerprints;
- artifact hashes/sizes, signer fingerprints, embedded APK identity, and immutable workflow context are recorded as non-secret evidence.

A successful protected run does **not** prove device QA, accessibility/performance quality, Play Console acceptance, final store declarations, repository branch protection, rollout safety, or publication.

## Current 2.0.13 Contract

Current source/default identity:

```text
applicationId          = in.sanskar.sudokunova
versionCode            = 2013
versionName            = 2.0.13
Desktop packageVersion = 2.0.13
minSdk                 = 26
targetSdk              = 37
```

The protected workflow defaults to `2013` / `2.0.13`. `scripts/verify_release_contract.py` additionally requires Desktop `packageVersion` to match Android `versionName`, ordinary CI to match Android source identity, and protected defaults to match that same Android contract.

See [`V2_0_13_RELEASE.md`](V2_0_13_RELEASE.md) for current release-line evidence requirements. [`V2_0_12_RELEASE.md`](V2_0_12_RELEASE.md) and v1 documents remain historical.

## Required GitHub Environment

Create/configure a GitHub Environment named:

```text
production-release
```

Recommended controls:

- restrict who can approve/use it;
- add required reviewers where supported;
- restrict deployment branches/tags to intended trusted release refs;
- keep production signing values as environment secrets, not repository variables;
- review environment access before every stable release.

The workflow intentionally fails when any required secret is missing.

Ref restrictions are critical because repository-controlled Gradle logic runs while signing credentials are available. Only trusted release refs should be allowed to receive them.

## Required Protected Secrets

Configure:

- `SUDOKUNOVA_KEYSTORE_BASE64` — base64 release/upload keystore bytes;
- `SUDOKUNOVA_KEYSTORE_PASSWORD`;
- `SUDOKUNOVA_KEY_ALIAS`;
- `SUDOKUNOVA_KEY_PASSWORD`;
- `SUDOKUNOVA_APK_CERT_SHA256` — expected APK signer certificate SHA-256;
- `SUDOKUNOVA_AAB_CERT_SHA256` — expected AAB signer/upload certificate SHA-256.

Fingerprints may use hexadecimal with or without colons. The workflow validates shape before build and normalizes them before comparison.

Never commit these values or place them in issues, PRs, documentation, screenshots, workflow inputs, or ordinary CI.

## Least-Privilege Secret Exposure

The workflow does not place production secrets in job-wide environment variables:

- preflight receives required protected values only to check presence/fingerprint shape;
- keystore materialization receives only the base64 keystore;
- signed Gradle build receives only password/alias/password plus a temporary keystore path;
- artifact verification receives only expected non-secret certificate fingerprints;
- repository guards, verifier tests, translation checks, evidence recording, and artifact upload do not receive keystore bytes/passwords.

This reduces exposure but does not make an untrusted ref safe.

## Keystore Materialization

The workflow decodes the keystore under `$RUNNER_TEMP`, uses restrictive permissions via `umask 077`, exports only the temporary path to Gradle, and removes the file in an `always()` cleanup step.

The repository itself never requires a committed keystore.

## Running the Workflow

From GitHub Actions choose **Production Release Validation**, select the exact trusted ref, and provide:

- `expected_version_code` — exact positive decimal Android code;
- `expected_version_name` — exact release-safe version name;
- `upload_signed_artifacts` — normally `false`, set `true` only when intentionally retaining short-lived signed outputs.

For the current 2.0.13 source the defaults are:

```text
expected_version_code = 2013
expected_version_name = 2.0.13
```

The application ID and SDK values are intentionally not operator-controlled inputs; they are pinned by workflow/source contract.

Any later store-accepted Android build must use a version code strictly greater than a previously accepted one.

## Embedded APK Manifest Verification

`output-metadata.json` is useful build metadata, but production validation also inspects the installable APK with Android SDK `apkanalyzer` for:

- application ID;
- version code;
- version name;
- minSdk;
- targetSdk;
- debuggable state.

The verifier fails closed if required tooling is unavailable, a scalar cannot be read, an expected identity/SDK differs, or release APK is debuggable.

## Evidence Artifacts

Every successful run uploads `production-release-validation-evidence` for the configured retention period. Evidence includes:

- `sha256.txt` — APK/AAB/R8 hashes and byte sizes;
- `signatures.txt` — normalized signer certificate fingerprints;
- `apk-identity.txt` — application/version/SDK/debuggable state read from APK;
- `verification.txt` — verifier result summary;
- `workflow-context.txt` — repository/ref/commit/run/application/version/SDK context.

When `upload_signed_artifacts=true`, signed APK/AAB/mapping are uploaded separately for short-lived retention.

## Package, SDK, and Certificate Identity Rule

A cryptographically valid signature is insufficient. The build must also belong to the intended package, carry the intended version/SDK identity, be non-debuggable, and match expected certificate identity.

Representative verifier form:

```bash
python scripts/verify_release_outputs.py \
  --apk path/to/signed-release.apk \
  --aab path/to/signed-release.aab \
  --mapping path/to/mapping.txt \
  --metadata path/to/output-metadata.json \
  --expected-version-code 2013 \
  --expected-version-name 2.0.13 \
  --expected-application-id in.sanskar.sudokunova \
  --require-apk-manifest \
  --expected-min-sdk 26 \
  --expected-target-sdk 37 \
  --apk-identity-output path/to/apk-identity.txt \
  --require-signatures \
  --expected-apk-cert-sha256 <expected-apk-cert-sha256> \
  --expected-aab-cert-sha256 <expected-aab-cert-sha256> \
  --output path/to/sha256.txt \
  --signature-output path/to/signatures.txt
```

For Play App Signing, distinguish the local upload-key certificate from the app-signing certificate used for distributed APKs. Validate the correct identity at each stage.

## Release Evidence Boundary

After a real successful protected run, record only non-secret evidence under `V2_0_13_RELEASE.md` / `what_changed.md`:

- exact commit/ref and run ID/attempt;
- application/version/SDK identity;
- embedded debuggable state;
- APK/AAB/R8 hashes and sizes;
- non-secret certificate SHA-256 fingerprints;
- signed-artifact validation result.

Do **not** mark manual device/accessibility/performance/store/admin rows complete unless those checks were actually performed and recorded.
