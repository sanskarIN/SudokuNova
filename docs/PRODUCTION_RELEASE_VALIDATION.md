# Production Release Validation Workflow

SudokuNova includes a manual, protected GitHub Actions workflow at `.github/workflows/release-validation.yml` for validating a release build that is signed with the intended production/upload key.

This workflow is deliberately separate from ordinary pull-request CI. Untrusted pull-request code must never receive production signing material.

## What the workflow verifies

A successful run on an exact selected ref proves all of the following for the artifacts produced by that run:

- repository secret guard passes;
- release-verifier regression tests pass;
- English/Hindi translation parity passes;
- release APK and AAB build successfully with R8/resource shrinking;
- exactly one signed release APK and one release AAB are selected;
- APK archive structure is valid;
- AAB archive structure is valid;
- APK `output-metadata.json` has production `applicationId = in.sanskar.sudokunova`;
- APK `output-metadata.json` has the requested `versionCode` and `versionName`;
- operator-supplied version metadata is validated before use;
- R8 `mapping.txt` exists and is non-empty;
- APK signature passes `apksigner verify --verbose --print-certs`;
- AAB signature passes `jarsigner -verify -certs`;
- AAB signer certificate can be inspected with `keytool -printcert -jarfile`;
- APK signer certificate SHA-256 matches the protected expected fingerprint;
- AAB signer/upload certificate SHA-256 matches the protected expected fingerprint;
- artifact SHA-256 hashes and byte sizes are written to evidence;
- normalized signer certificate fingerprints are written to evidence;
- repository/ref/commit/workflow-run/application/version context is written to evidence.

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

The production application ID is intentionally not an operator input. The workflow pins it to `in.sanskar.sudokunova` so a manual dispatch cannot accidentally validate a different package as SudokuNova.

For the current RC1 source metadata the version defaults are `1000` and `1.0.0-rc.1`. Stable publication must use the final values actually committed for the stable ref.

## Evidence artifacts

Every successful run uploads `production-release-validation-evidence` for 30 days. It contains:

- `sha256.txt` — release APK/AAB/R8 mapping SHA-256 and byte-size evidence;
- `signatures.txt` — normalized APK/AAB signer certificate SHA-256 evidence;
- `verification.txt` — verifier summary including the validated application/version identity;
- `workflow-context.txt` — repository, commit SHA, ref, run ID/attempt, expected application ID, and expected version metadata.

When `upload_signed_artifacts=true`, the workflow separately uploads `signed-production-release-artifacts` for 7 days containing the signed APK, AAB, and R8 mapping. This is opt-in to reduce unnecessary retention of production binaries.

## Package and certificate identity rule

A signature being cryptographically valid is insufficient. The release must also belong to the intended Android package and be signed by the intended certificate.

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
  --require-signatures \
  --expected-apk-cert-sha256 <expected-apk-cert-sha256> \
  --expected-aab-cert-sha256 <expected-aab-cert-sha256> \
  --output path/to/sha256.txt \
  --signature-output path/to/signatures.txt
```

The verifier fails closed when the expected application ID is missing/different or when an expected fingerprint does not match any reported signer fingerprint.

For Play App Signing, distinguish the local upload-key certificate from the app-signing certificate used by Google Play for distributed APKs. Record the correct certificate identity for each stage and validate the Play-distributed artifact separately where required.

## Stable-release evidence boundary

After a real successful protected run, copy only non-secret evidence into `V1_RELEASE_EVIDENCE.md`:

- exact commit/ref;
- workflow run ID/attempt;
- application ID;
- version code/name;
- APK/AAB/R8 hashes and sizes;
- non-secret certificate SHA-256 fingerprints;
- signed artifact validation result.

Do not mark manual device/accessibility/performance/store/admin rows complete unless those checks were actually performed and recorded.
