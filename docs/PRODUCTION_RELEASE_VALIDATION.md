# Production Release Validation Workflow

SudokuNova includes a manual protected GitHub Actions workflow at `.github/workflows/release-validation.yml` for validating an Android release build signed with the intended production/upload key.

The workflow is deliberately separate from ordinary pull-request CI. Untrusted pull-request code must never receive production signing material.

## Current 2.0.14 Contract

```text
applicationId          = in.sanskar.sudokunova
versionCode            = 2014
versionName            = 2.0.14
Desktop packageVersion = 2.0.14
minSdk                 = 26
targetSdk              = 37
```

The protected workflow defaults to `2014` / `2.0.14`. `scripts/verify_release_contract.py` also requires Desktop `packageVersion`, ordinary Android CI expectations, and protected workflow defaults to match Android source identity.

See [`V2_0_14_RELEASE.md`](V2_0_14_RELEASE.md) and issue #43 for current release-evidence requirements. Older release documents remain historical.

## What a Successful Protected Run Verifies

For artifacts produced by that exact selected ref/run, a successful workflow verifies that:

- release-contract and release-verifier regression tests pass;
- repository secret guard passes;
- English/Hindi translation parity passes;
- source/ordinary CI/protected defaults/Desktop package version are synchronized;
- release APK/AAB build with R8/resource shrinking;
- exactly one signed release APK and AAB are selected;
- APK/AAB archive structures are valid;
- output metadata and embedded APK manifest carry expected application/version identity;
- embedded minSdk/targetSdk match expectations;
- release APK reports `debuggable=false`;
- `mapping.txt` exists and is non-empty;
- APK signature verification succeeds and reports a verified v2-or-newer scheme;
- AAB signature verification succeeds;
- APK/AAB signer certificate SHA-256 values match protected expected fingerprints;
- hashes/sizes, signer fingerprints, APK identity, and immutable workflow context are recorded as non-secret evidence.

A successful protected run still does not prove device QA, accessibility/performance quality, Play Console acceptance, store declarations, repository branch protection, rollout safety, or publication.

## Required GitHub Environment

Configure an environment named:

```text
production-release
```

Recommended controls:

- trusted required reviewers where supported;
- restricted deployment branches/tags/refs;
- production signing values as environment secrets rather than repository variables;
- environment access review before each release.

Ref restrictions matter because repository-controlled Gradle logic runs while signing credentials are available. Only trusted immutable/intended release refs should receive them.

Committed workflow YAML does not prove any of these administration controls are actually configured.

## Required Protected Secrets

- `SUDOKUNOVA_KEYSTORE_BASE64`;
- `SUDOKUNOVA_KEYSTORE_PASSWORD`;
- `SUDOKUNOVA_KEY_ALIAS`;
- `SUDOKUNOVA_KEY_PASSWORD`;
- `SUDOKUNOVA_APK_CERT_SHA256`;
- `SUDOKUNOVA_AAB_CERT_SHA256`.

Certificate fingerprints may contain hexadecimal with or without colons; the workflow validates and normalizes them before comparison.

Never commit these values or place them in issues, pull requests, documentation, screenshots, workflow inputs, chat, or ordinary CI.

## Least-Privilege Secret Exposure

The workflow scopes protected values to the smallest practical steps:

- preflight checks only required values/fingerprint shape;
- keystore materialization receives only base64 keystore bytes;
- signed Gradle build receives password/alias/password plus the temporary keystore path;
- artifact verification receives expected certificate fingerprints;
- repository guards/tests/evidence recording do not receive keystore passwords.

This reduces exposure but does not make an untrusted ref safe.

## Temporary Keystore Handling

The workflow decodes the keystore under `$RUNNER_TEMP`, applies restrictive permissions via `umask 077`, exports only the temporary path to Gradle, and removes the file in an `always()` cleanup step.

The repository never requires a committed keystore.

## Running the Workflow

In GitHub Actions choose **Production Release Validation**, select the exact trusted ref, and provide:

```text
expected_version_code = 2014
expected_version_name = 2.0.14
upload_signed_artifacts = false (normally)
```

Application ID and SDK values are source/workflow-pinned rather than operator-controlled inputs.

If `upload_signed_artifacts=true`, signed APK/AAB/mapping outputs are retained separately for short-lived deliberate handling. Do not enable that option casually.

Any later store-accepted Android build must use a version code greater than a previously accepted build.

## Embedded APK Manifest Verification

Production validation independently reads the installable APK with Android SDK tooling for:

- application ID;
- version code;
- version name;
- minSdk;
- targetSdk;
- debuggable state.

The verifier fails closed when required tooling is unavailable, identity cannot be read, values differ, or the release APK is debuggable.

## Evidence Artifacts

Successful validation retains non-secret evidence such as:

- `sha256.txt` — APK/AAB/R8 hashes and byte sizes;
- `signatures.txt` — normalized certificate fingerprints;
- `apk-identity.txt` — embedded package/version/SDK/debuggable state;
- `verification.txt` — verifier result summary;
- `workflow-context.txt` — repository/ref/commit/run/application/version/SDK context.

Treat the exact commit/ref and workflow run ID as part of the evidence identity.

## Representative Certificate-Bound Verification

```bash
python scripts/verify_release_outputs.py \
  --apk path/to/signed-release.apk \
  --aab path/to/signed-release.aab \
  --mapping path/to/mapping.txt \
  --metadata path/to/output-metadata.json \
  --expected-version-code 2014 \
  --expected-version-name 2.0.14 \
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

For Play App Signing, distinguish the local upload-key certificate from the app-signing certificate used for distributed APKs. Validate the correct identity at the correct stage.

## Relationship to PR CI

PR #44 must first pass ordinary Android CI, Android Instrumentation, and Cross-Platform CI on one exact final head. Those workflows receive no production signing material.

Protected validation is a later real release-evidence step and must run only on the intended trusted release ref. A green PR does not substitute for it, and a successful protected run on another SHA does not prove the final release ref.

## Evidence Recording Rule

After a real protected 2.0.14 run, record only non-secret evidence in issue #43 and appropriate release records:

- exact commit/ref and run ID/attempt;
- application/version/SDK/debuggable identity;
- APK/AAB/R8 hashes/sizes;
- non-secret certificate fingerprints;
- verification result.

Do not mark manual device/accessibility/performance/store/admin rows complete unless those checks were actually performed and recorded.

Do not create `v2.0.14`, a GitHub Release, or public/store distribution solely because this workflow succeeds; the final `SHIP` decision also requires the remaining evidence in issue #43.
