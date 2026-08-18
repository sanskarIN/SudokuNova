# Building SudokuNova

This guide covers the supported Android build and verification outputs for the current SudokuNova development line.

## Current Toolchain

The repository currently targets:

- Kotlin: `2.4.10`
- Android Gradle Plugin: `9.3.1`
- Gradle wrapper: `9.5.0`
- Java/JVM: `17`
- Compile SDK: `37`
- Target SDK: `37`
- Minimum SDK: `26`
- Compose BOM: `2026.08.00`
- Room: `2.8.3`
- KSP: `2.3.10`

Always prefer the Gradle wrapper committed to this repository instead of a separately installed global Gradle version.

## Required Software

Install:

1. JDK 17.
2. Android Studio compatible with the repository Android Gradle Plugin.
3. Android SDK Platform 37.
4. Current Android SDK Build Tools accepted by AGP 9.3.1.
5. Android SDK Platform Tools for `adb` if you want command-line installation/testing.
6. Python 3 for repository verification scripts.

The API-35 emulator is used by the repository's connected-instrumentation workflow. Local connected tests can use a compatible emulator/device on the required test API.

## Clone

```bash
git clone https://github.com/sanskarIN/SudokuNova.git
cd SudokuNova
```

For an active feature/release branch, check out that branch after cloning.

## Confirm Java and Gradle

Linux/macOS:

```bash
java -version
./gradlew --version
```

Windows PowerShell / Command Prompt:

```bat
java -version
gradlew.bat --version
```

The Gradle JVM must resolve to Java 17 for the supported build configuration.

## Full Local Verification

Linux/macOS:

```bash
python scripts/verify_translations.py
python scripts/verify_release_hygiene.py
./gradlew :sudoku-engine:test --stacktrace
./gradlew :app:testDebugUnitTest --stacktrace
./gradlew :app:assembleDebugAndroidTest --stacktrace
./gradlew :app:lintDebug --stacktrace
./gradlew :app:assembleDebug --stacktrace
./gradlew :app:lintRelease --stacktrace
./gradlew :app:assembleRelease --stacktrace
./gradlew :app:bundleRelease --stacktrace
```

Windows:

```bat
python scripts\verify_translations.py
python scripts\verify_release_hygiene.py
gradlew.bat :sudoku-engine:test --stacktrace
gradlew.bat :app:testDebugUnitTest --stacktrace
gradlew.bat :app:assembleDebugAndroidTest --stacktrace
gradlew.bat :app:lintDebug --stacktrace
gradlew.bat :app:assembleDebug --stacktrace
gradlew.bat :app:lintRelease --stacktrace
gradlew.bat :app:assembleRelease --stacktrace
gradlew.bat :app:bundleRelease --stacktrace
```

Do not treat one successful APK build as equivalent to the full release gate. The repository intentionally verifies engine tests, Android JVM tests, instrumentation-test compilation, debug lint/build, release lint/build, R8/resource shrinking, and AAB creation separately.

## Debug APK

Build:

```bash
./gradlew :app:assembleDebug
```

Output:

```text
app/build/outputs/apk/debug/app-debug.apk
```

The debug build uses the configured `.debug` application ID suffix and `-debug` version suffix.

Install on an attached device/emulator:

```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

## Debug Android-Test APK

Build the app and instrumentation-test APKs:

```bash
./gradlew :app:assembleDebugAndroidTest
```

Typical outputs are under:

```text
app/build/outputs/apk/debug/
app/build/outputs/apk/androidTest/debug/
```

Normally you should let Gradle run connected tests instead of manually installing the test APK.

## Connected Instrumentation Tests

With a compatible device/emulator already running:

```bash
./gradlew :app:connectedDebugAndroidTest --stacktrace
```

The GitHub `Android Instrumentation` workflow runs the connected Compose/Room suite on an API-35 x86_64 emulator with KVM access and animations disabled.

Connected tests are required for the final clean pull-request head. Compiling `assembleDebugAndroidTest` alone is not a substitute for actually executing the connected suite.

## Release APK

Build the release APK:

```bash
./gradlew :app:assembleRelease --stacktrace
```

The release build enables:

- code minification through R8;
- Android resource shrinking;
- `proguard-android-optimize.txt`;
- project-specific `app/proguard-rules.pro`.

Because no private signing credentials are committed to the repository, command-line/CI release output is intentionally unsigned unless a developer supplies signing outside source control.

Typical output:

```text
app/build/outputs/apk/release/app-release-unsigned.apk
```

R8 mapping/retrace data is written under:

```text
app/build/outputs/mapping/release/
```

Preserve the mapping file for every published release so obfuscated crash traces can be retraced.

## Release Android App Bundle (AAB)

Build:

```bash
./gradlew :app:bundleRelease --stacktrace
```

Typical output:

```text
app/build/outputs/bundle/release/app-release.aab
```

An AAB is a publishing bundle, not a directly installable APK. Google Play generates optimized APK sets from a signed bundle.

The repository CI verifies that the release bundle can be built with the same release shrinking configuration as the APK path.

## Signing Policy

Private signing material must never be committed.

The repository `.gitignore` excludes common sensitive files including:

- `*.jks`
- `*.keystore`
- `*.p12`
- `*.pem`
- `*.key`
- `keystore.properties`
- `secrets.properties`
- `.env` / `.env.*`
- `local.properties`

The v0.9 `verify_release_hygiene.py` gate also checks these exclusions and fails if signing/secret files are present in the source tree.

### Recommended production signing workflow

For an actual store release, use Android Studio's **Generate Signed App Bundle or APK** flow with a private keystore stored outside the repository, or provide signing credentials through a secure local/CI secret mechanism.

Do not paste a keystore password, key password, private key, or production signing file into source code, Gradle scripts, issue comments, pull requests, documentation, or CI logs.

### APK signing from Android SDK tools

If you deliberately use command-line Android SDK signing tools, keep the keystore path and passwords outside source control. APK signing should happen only after the release APK has been aligned as required by the installed Android build tools. Verify the final signature with the SDK signing verification tool before distribution.

The exact Android SDK Build Tools directory can vary by machine, so the repository does not hard-code a Build Tools version or a private keystore path.

### AAB signing

A production AAB must be signed before store upload. Android Studio's signed-bundle workflow is the least error-prone supported path for contributors who do not maintain an external release-signing pipeline.

CI intentionally uploads **unsigned** release artifacts for verification only. They must not be presented as production store binaries.

## GitHub Actions Artifacts

The standard `Android CI` workflow uploads:

### `verification-reports`

May include:

- Gradle test reports/results;
- Android lint reports;
- generated Room schema output used during migration review.

### `unsigned-release-artifacts`

On a fully successful standard CI run:

- unsigned release APK;
- release AAB;
- R8 mapping output.

These artifacts prove that the release build path compiles and shrinks. They are not a substitute for production signing or manual device QA.

## Room Schema Output

Room is configured with:

```text
room.schemaLocation = app/schemas
```

The database uses `exportSchema = true`.

Release hardening treats schema history as migration evidence. Generated schemas should be reviewed whenever the Room schema version changes, and production migrations must be exercised by instrumentation tests before merge.

## Translation Verification

Run:

```bash
python scripts/verify_translations.py
```

This checks maintained English/Hindi resource parity. Player-facing strings changed in one maintained locale must have their matching resource key in the other maintained locale before merge.

## Release/Security Hygiene Verification

Run:

```bash
python scripts/verify_release_hygiene.py
```

The v0.9 check currently enforces invariants such as:

- no permissions in the main Android manifest;
- no unexpected exported components;
- cleartext traffic explicitly disabled;
- Android cloud backup excludes persisted app data;
- legacy backup restricted to device-to-device transport;
- release minification/resource shrinking enabled;
- optimized default ProGuard configuration present;
- source/line metadata retained for retracing;
- common signing/secret files ignored and absent;
- release lint/APK/AAB tasks present in CI;
- unsigned release artifacts uploaded only after a successful release build.

## Clean Builds

If generated state becomes stale:

```bash
./gradlew clean
```

Then rerun the required task. Do not use `clean` as a routine workaround for a reproducible source/configuration defect; fix the defect instead.

## Windows JDK Selection

If Windows is using the wrong JDK for Gradle, set `JAVA_HOME` for the current PowerShell session, for example:

```powershell
$env:JAVA_HOME = 'C:\Program Files\Java\jdk-17'
$env:Path = "$env:JAVA_HOME\bin;$env:Path"
java -version
gradlew.bat --version
```

Use the actual JDK 17 path installed on the machine.

## Android SDK Selection

A local Android SDK path belongs in `local.properties`, for example:

```properties
sdk.dir=C\:\\Users\\YOUR_USER\\AppData\\Local\\Android\\Sdk
```

`local.properties` is machine-specific and must remain untracked.

## Release Verification Checklist

Before a production-candidate commit can be considered release-ready, verify on the exact clean head:

- [ ] English/Hindi translation parity passes.
- [ ] Release/security hygiene script passes.
- [ ] `:sudoku-engine:test` passes.
- [ ] `:app:testDebugUnitTest` passes.
- [ ] `:app:assembleDebugAndroidTest` passes.
- [ ] `:app:lintDebug` passes.
- [ ] `:app:assembleDebug` passes.
- [ ] `:app:lintRelease` passes.
- [ ] `:app:assembleRelease` passes with R8/resource shrinking.
- [ ] `:app:bundleRelease` passes.
- [ ] API-35 connected instrumentation passes.
- [ ] Release mapping output is preserved.
- [ ] Signed production artifact is created outside source control.
- [ ] Signed artifact is verified before distribution.
- [ ] Manual release QA is completed and recorded truthfully.

The manual QA matrix is maintained in `docs/RELEASE_QA.md`.

## Troubleshooting

### Wrong Java version

Confirm `java -version`, `JAVA_HOME`, Android Studio's Gradle JDK, and `./gradlew --version`/`gradlew.bat --version` all point to Java 17.

### Missing Android SDK

Install SDK Platform 37 and accepted Build Tools through Android Studio's SDK Manager, then confirm `local.properties` points at the correct local SDK.

### Lint failure

Read the full lint report under `app/build/reports/` or the path printed by CI. Do not suppress a release warning unless the suppression is documented and the underlying behavior is intentionally safe.

### R8 failure

Treat missing classes, reflection warnings, or resource-shrinking issues as release blockers. Add only the narrow keep rules actually required by the affected library/application code.

### Connected test failure

Use the instrumentation report to distinguish a product defect from an unstable test-selection assumption. A test should be repaired only when product behavior is already correct; do not weaken a legitimate correctness/accessibility assertion merely to make CI green.

### Release artifact missing

The CI release-artifact upload uses `if-no-files-found: error`, so a successful standard workflow must produce the expected release APK/AAB paths. If output naming changes because of a build-system upgrade, update the documented/automated path deliberately.

## CI Source of Truth

`.github/workflows/ci.yml` is the automated standard verification source of truth.  
`.github/workflows/instrumentation.yml` is the connected Android source of truth.

Documentation must be updated whenever those release gates or output paths change.
