# Building SudokuNova

## Required Tooling

Current repository configuration:

- JDK 17
- Gradle 9.5
- Android Gradle Plugin 9.3.1
- Kotlin 2.4.10
- Android compile/target SDK 37
- Android min SDK 26

Use the committed Gradle wrapper rather than a globally installed Gradle version so local and CI builds use the same Gradle distribution.

The current v1 readiness branch uses:

- `versionName = "1.0.0-rc1"`
- `versionCode = 990`

Stable `1.0.0` must use a versionCode higher than every candidate code that has been distributed.

## Clone

```bash
git clone https://github.com/sanskarIN/SudokuNova.git
cd SudokuNova
```

## Gradle Sync

Android Studio performs sync when the project opens. A lightweight command-line configuration check is:

```bash
./gradlew tasks
```

Windows:

```bat
gradlew.bat tasks
```

## Clean

```bash
./gradlew clean
```

Do not make clean builds the default workflow. Use `clean` only when stale generated state is actually suspected.

## Debug APK

```bash
./gradlew :app:assembleDebug
```

Windows:

```bat
gradlew.bat :app:assembleDebug
```

Expected output:

```text
app/build/outputs/apk/debug/app-debug.apk
```

The debug build uses the `.debug` application-ID suffix and `-debug` version-name suffix.

## Release APK

```bash
./gradlew :app:assembleRelease
```

Expected output directory:

```text
app/build/outputs/apk/release/
```

The release build enables R8 code shrinking and resource shrinking. CI verifies release compilation/R8 without requiring repository-committed production credentials.

## Release AAB

```bash
./gradlew :app:bundleRelease
```

Expected output:

```text
app/build/outputs/bundle/release/app-release.aab
```

A distributable production package must be signed using protected credentials outside version control.

## Release Mapping Output

Preserve the matching R8 mapping directory with the exact release commit:

```text
app/build/outputs/mapping/release/
```

## Repository / Release Helper Checks

```bash
python scripts/verify_no_secrets.py
python -m unittest discover -s scripts/tests -p 'test_*.py' -v
python scripts/verify_translations.py
```

The release-helper tests cover the artifact integrity/checksum verifier as well as its important failure paths.

## Unit Tests

```bash
./gradlew :sudoku-engine:test
./gradlew :app:testDebugUnitTest
```

## Android Instrumentation-Test Compilation

```bash
./gradlew :app:assembleDebugAndroidTest
```

This verifies connected-test sources compile before an emulator/device run.

## Android Lint

```bash
./gradlew :app:lintDebug :app:lintRelease
```

Both variants are release gates.

## Full v1.0-RC Non-Connected Verification

Linux/macOS:

```bash
python scripts/verify_no_secrets.py
python -m unittest discover -s scripts/tests -p 'test_*.py' -v
python scripts/verify_translations.py
./gradlew :sudoku-engine:test \
  :app:testDebugUnitTest \
  :app:assembleDebugAndroidTest \
  :app:lintDebug \
  :app:lintRelease \
  :app:assembleDebug \
  :app:assembleRelease \
  :app:bundleRelease \
  --stacktrace
python scripts/verify_release_artifacts.py \
  app/build/outputs/apk/release/*.apk \
  app/build/outputs/bundle/release/*.aab \
  --checksums-out app/build/outputs/SHA256SUMS
```

Windows PowerShell:

```powershell
python scripts/verify_no_secrets.py
python -m unittest discover -s scripts/tests -p 'test_*.py' -v
python scripts/verify_translations.py
.\gradlew.bat :sudoku-engine:test `
  :app:testDebugUnitTest `
  :app:assembleDebugAndroidTest `
  :app:lintDebug `
  :app:lintRelease `
  :app:assembleDebug `
  :app:assembleRelease `
  :app:bundleRelease `
  --stacktrace
python scripts/verify_release_artifacts.py `
  app/build/outputs/apk/release/*.apk `
  app/build/outputs/bundle/release/*.aab `
  --checksums-out app/build/outputs/SHA256SUMS
```

API-35 connected tests remain a separate emulator/device gate.

## Release Artifact Integrity / Checksums

`scripts/verify_release_artifacts.py` accepts APK/AAB files and:

- rejects missing, empty, unsupported, or corrupt containers;
- tests ZIP entry integrity;
- computes SHA-256;
- optionally writes deterministic checksum lines;
- optionally requires signature verification through platform tools.

Example for CI-style release verification artifacts:

```bash
python scripts/verify_release_artifacts.py \
  app/build/outputs/apk/release/*.apk \
  app/build/outputs/bundle/release/*.aab \
  --checksums-out app/build/outputs/SHA256SUMS
```

Example for final signed production artifacts:

```bash
python scripts/verify_release_artifacts.py \
  path/to/signed-release.apk \
  path/to/signed-release.aab \
  --require-signature \
  --checksums-out SHA256SUMS
```

`--require-signature` requires `apksigner` for APK and `jarsigner` for AAB. If a required verifier is unavailable or the artifact does not verify, the command fails.

## Install Debug APK with ADB

```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

For a production-signed APK, install and smoke-test the exact signed file intended for direct distribution, not a different local build.

## CI Release Verification

`.github/workflows/ci.yml` verifies:

1. repository security guard;
2. release-helper unit tests;
3. English/Hindi translation parity;
4. Sudoku engine tests;
5. Android JVM tests;
6. Android instrumentation-test compilation;
7. debug and release lint;
8. debug APK;
9. R8/resource-shrunk release APK;
10. release AAB;
11. APK/AAB integrity and SHA-256 generation;
12. reports and short-lived release artifact evidence, including `SHA256SUMS`.

CI-produced release files remain verification artifacts unless production signing and manual release QA are completed.

## Gradle Wrapper / Reproducibility Evidence

For a release investigation record:

- exact Git SHA;
- versionCode/versionName;
- Java version;
- Gradle wrapper version;
- AGP/Kotlin versions;
- Android SDK environment;
- command executed;
- workflow run IDs;
- artifact SHA-256 values.

## Build Types

### Debug

- development/testing build;
- debug application-ID suffix;
- debug version-name suffix.

### Release

- minification enabled;
- resource shrinking enabled;
- optimized default ProGuard rules plus `app/proguard-rules.pro`;
- release lint/R8/AAB/integrity gates required;
- production signing external to source control.

## Release-Signing Rules

Never commit:

- `.jks`, `.keystore`, `.p12`, `.pfx`;
- PEM/private signing keys;
- keystore/key passwords;
- signing tokens/service-account credentials;
- generated secret-bearing configuration.

If production CI signing is configured later, it must consume protected runtime secrets, remain safe for forks/PRs where secrets are unavailable, and must not print secret material.

## Common Failure Checks

If a build fails:

1. confirm Java 17;
2. confirm Android SDK 37;
3. confirm the intended branch/commit;
4. confirm the Gradle wrapper is used;
5. read the first real compilation/test/lint/R8/artifact error;
6. use `clean` only when stale generated state is plausible;
7. inspect `app/proguard-rules.pro` before adding broad keep rules;
8. check `TROUBLESHOOTING.md` and CI logs.

## Release Evidence Policy

A release-quality statement must be tied to actual evidence. Source review/CI may be recorded as automated evidence; physical-device coverage, TalkBack, performance measurements, production signing, signed-artifact validation, and store publication must not be claimed until they are actually performed.

Use `V1_RELEASE_EVIDENCE.md` for the v1.0 evidence ledger.
