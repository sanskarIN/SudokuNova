# Production Release Validation Workflow

SudokuNova includes a manual, protected GitHub Actions workflow at `.github/workflows/release-validation.yml` for validating a release build that is signed with the intended production/upload key.

This workflow is deliberately separate from ordinary pull-request CI. Untrusted pull-request code must never receive production signing material.

## What the workflow verifies

A successful run on an exact selected ref proves all of the following for the artifacts produced by that run:

- repository secret guard passes;
- release-verifier regression tests pass, including CLI-boundary validation;
- English/Hindi translation parity passes;
- release APK and AAB build successfully with R8/resource shrinking;
- exactly one signed release APK and one release AAB are selected;
- APK archive structure is valid;
- AAB archive structure is valid;
- APK `output-metadata.json` has production `applicationId = in.sanskar.sudokunova`;
- APK `output-metadata.json` has the requested `versionCode` and `versionName`;
- the APK's embedded manifest independently reports the expected production application ID, version code, and version name;
- the APK's embedded manifest independently reports the source-controlled `minSdk` and `targetSdk` values;
- the APK's embedded manifest reports `debuggable=false`;
- operator-supplied version metadata is validated before use;
- source, ordinary CI, and protected-workflow application/version/SDK expectations remain synchronized by `scripts/verify_release_contract.py`;
- R8 `mapping.txt` exists and is non-empty;
- APK signature passes `apksigner verify --verbose --print-certs`;
- APK signature verification reports at least one verified v2-or-newer APK signature scheme;
- AAB signature passes `jarsigner -verify -certs`;
- AAB signer certificate can be inspected with `keytool -printcert -jarfile`;
- APK signer certificate SHA-256 matches the protected expected fingerprint;
- AAB signer/upload certificate SHA-256 matches the protected expected fingerprint;
- artifact SHA-256 hashes and byte sizes are written to evidence;
- normalized signer certificate fingerprints are written to evidence;
- embedded APK identity/SDK/debuggable values are written to evidence;
- repository/ref/commit/workflow-run/application/version/SDK context is written to evidence.

A successful run does **not** prove device QA, Play Console acceptance, final store declarations, branch protection, rollout safety, or other manual evidence.

## Required GitHub Environment

Create a GitHub Environment named:

`production-release`

Recommended controls:

- restrict who can approve/use the environment;
- add required reviewers where the repository/account plan supports them;
- restrict deployment branches/tags to intended release refs;
- keep all production signing values as environment secrets, not repository variables;
- review environment access before every stable release.

The workflow intentionally fails when any required secret is missing.

Environment ref restrictions are important because the workflow checks out the selected ref before running repository scripts/build logic. Only trusted release refs should be allowed to receive production signing secrets.

## Required protected secrets

Configure these secrets in the `production-release` environment:

- `SUDOKUNOVA_KEYSTORE_BASE64` — base64 representation of the release/upload keystore bytes;
- `SUDOKUNOVA_KEYSTORE_PASSWORD` — keystore password;
- `SUDOKUNOVA_KEY_ALIAS` — key alias;
- `SUDOKUNOVA_KEY_PASSWORD` — key password;
- `SUDOKUNOVA_APK_CERT_SHA256` — expected APK signer certificate SHA-256 fingerprint;
- `SUDOKUNOVA_AAB_CERT_SHA256` — expected AAB signer/upload certificate SHA-256 fingerprint.

Certificate fingerprints may use hexadecimal with or without colon separators. The workflow checks their format before the signed build, and the verifier normalizes them before comparison.

Do not commit these values. Do not place keystore bytes, passwords, tokens, or private keys in issues, pull requests, documentation, screenshots, workflow inputs, or ordinary CI configuration.

### Least-privilege secret exposure

The protected workflow deliberately does **not** place production secrets in job-wide environment variables. Secrets are scoped to the minimum steps that require them:

- the preflight secret/format check receives all required protected values only to confirm presence and certificate-fingerprint shape;
- the keystore-materialization step receives only the base64 keystore value;
- the Gradle signed-build step receives only the keystore password, key alias, and key password, while the temporary keystore path comes from `$GITHUB_ENV`;
- the artifact-verification step receives only the expected certificate SHA-256 values;
- repository security tests, release-verifier unit tests, translation checks, artifact discovery, evidence recording, and artifact-upload steps do not receive signing passwords or keystore bytes.

This reduces accidental exposure surface but does not make an untrusted ref safe. The signed build necessarily runs repository-controlled Gradle logic while signing credentials are available, so GitHub Environment ref restrictions and reviewer/access controls remain mandatory.

## Preparing the keystore secret

Prepare the base64 value in a trusted local environment. Keep the output private and store it directly as the environment secret. The workflow decodes it into `$RUNNER_TEMP`, applies restrictive file permissions through `umask 077`, exports only the temporary path to Gradle, and removes the file in an `always()` cleanup step.

The repository itself never requires a committed keystore.

## Running the workflow

From GitHub Actions, choose **Production Release Validation**, select the exact branch/tag/ref intended for validation, then provide:

- `expected_version_code` — exact positive decimal Android version code for that ref;
- `expected_version_name` — exact release-safe Android version name beginning with an alphanumeric character and then using only alphanumeric characters, `.`, `_`, `+`, or `-`;
- `upload_signed_artifacts` — normally leave `false`; set `true` only when intentionally retaining the signed APK/AAB as short-lived workflow artifacts.

The production application ID and SDK expectations are intentionally not operator inputs. The workflow pins the application ID to `in.sanskar.sudokunova`, `minSdk` to `26`, and `targetSdk` to `37` for the current source contract. `scripts/verify_release_contract.py` fails when those workflow expectations drift from `app/build.gradle.kts` or ordinary CI.

For the current **2.0.12** source metadata, the workflow defaults are `versionCode 2012` and `versionName 2.0.12`. Publication must use the exact values committed on the release ref; any later store-accepted build must use a strictly higher version code.

See `V2_0_12_RELEASE.md` for the current release-line evidence requirements. Older v1 release documents remain historical evidence only.

## Embedded APK manifest verification

`output-metadata.json` is useful build metadata, but production validation must also inspect the artifact that will actually be installed. The verifier therefore uses Android SDK `apkanalyzer` to read these values directly from the release APK:

- application ID;
- version code;
- version name;
- minimum SDK;
- target SDK;
- debuggable state.

The verifier fails closed if `apkanalyzer` is unavailable when `--require-apk-manifest` is requested, if any inspected scalar value cannot be read, if an expected identity/SDK value differs, or if the release APK is marked debuggable.

The expected SDK values must be positive, and an expected target SDK lower than the expected minimum SDK is rejected before artifact access. This keeps malformed release-verifier input from being mistaken for artifact failure.

## Evidence artifacts

Every successful run uploads `production-release-validation-evidence` for 30 days. It contains:

- `sha256.txt` — release APK/AAB/R8 mapping SHA-256 and byte-size evidence;
- `signatures.txt` — normalized APK/AAB signer certificate SHA-256 evidence;
- `apk-identity.txt` — application ID, version code/name, minimum SDK, target SDK, and debuggable state read from the APK itself;
- `verification.txt` — verifier summary including the validated application/version/SDK identity;
- `workflow-context.txt` — repository, commit SHA, ref, run ID/attempt, expected application ID, expected version metadata, and expected SDK contract.

When `upload_signed_artifacts=true`, the workflow separately uploads `signed-production-release-artifacts` for 7 days containing the signed APK, AAB, and R8 mapping. This is opt-in to reduce unnecessary retention of production binaries.

## Package, SDK, and certificate identity rule

A signature being cryptographically valid is insufficient. The release must also belong to the intended Android package, carry the intended application/version/SDK identity, be non-debuggable, and be signed by the intended certificate.

`scripts/verify_release_outputs.py` supports:

```bash
python scripts/verify_release_outputs.py \
  --apk path/to/signed-release.apk \
  --aab path/to/signed-release.aab \
  --mapping path/to/mapping.txt \
  --metadata path/to/output-metadata.json \
  --expected-version-code <final-code> \
  --expected-version-name <final-name> \
  --expected-application-id in.sanskar.sudokunova \
  --require-apk-manifest \
  --expected-min-sdk <source-min-sdk> \
  --expected-target-sdk <source-target-sdk> \
  --apk-identity-output path/to/apk-identity.txt \
  --require-signatures \
  --expected-apk-cert-sha256 <expected-apk-cert-sha256> \
  --expected-aab-cert-sha256 <expected-aab-cert-sha256> \
  --output path/to/sha256.txt \
  --signature-output path/to/signatures.txt
```

The verifier fails closed when the expected application ID is missing/different for manifest verification, when the embedded APK identity or SDK contract differs, when the APK is debuggable, when APK verification lacks a verified v2-or-newer signature scheme, or when an expected certificate fingerprint does not match any reported signer fingerprint.

For Play App Signing, distinguish the local upload-key certificate from the app-signing certificate used by Google Play for distributed APKs. Record the correct certificate identity for each stage and validate the Play-distributed artifact separately where required.

## Release evidence boundary

After a real successful protected run, copy only non-secret evidence into the current release ledger defined by `V2_0_12_RELEASE.md` and `what_changed.md`:

- exact commit/ref;
- workflow run ID/attempt;
- application ID;
- version code/name;
- minimum and target SDK;
- embedded debuggable state;
- APK/AAB/R8 hashes and sizes;
- non-secret certificate SHA-256 fingerprints;
- signed artifact validation result.

Do not mark manual device/accessibility/performance/store/admin rows complete unless those checks were actually performed and recorded.
