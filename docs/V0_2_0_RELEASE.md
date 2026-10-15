# SudokuNova v0.2.0 — Release Packaging and Publication

**Status:** release candidate preparation; not a published or verified binary release.

## Release identity

- Public release/tag: `v0.2.0`
- Android `versionName`: `0.2.0`
- Android `versionCode`: `2020` (monotonic, deliberately **not** reset to 20)
- Android application ID: `in.sanskar.sudokunova`
- Desktop `packageVersion`: `0.2.0`
- Android minimum API: 26

The repository previously carried internal `2.0.15` source metadata even though
the most recent publicly published tag was `v0.1.1`. This release reconciles
the **public display version** without lowering the Android upgrade code.
Future Android releases must use `versionCode > 2020`. The merged 2.0.15 source features are retained in this public v0.2.0 release. Resolve version conflicts before merging.

## What happens when a GitHub Release is published?

The workflow `Publish Cross-Platform Release Assets`
(`.github/workflows/publish-release-assets.yml`) starts on `release.published`
and supports an explicit manual retry with an existing published tag.

1. The workflow checks out the exact release tag, rejects unsafe/mismatched
   version strings, validates release metadata and runs the engine, shared UI,
   Android unit tests, and Python packaging checks.
2. Separate GitHub-hosted runners build each platform's installable/archive format.
3. Every build stages exactly one expected, nonempty artifact. Archive integrity
   and platform package headers are checked.
4. The final job runs **only if every required platform build succeeds**.
   It verifies the complete platform matrix and creates `SHA256SUMS.txt`.
5. The final job uploads the assets and checksums to the **already published**
   GitHub Release using its release tag.

Assets do **not** appear instantly when the Release is created. They appear when
the hosted build, tests, and verification succeed. On failure, no claim of
successful publication should be made. See **Actions** for the failure and logs.

**Important:** GitHub release events require this workflow file to be merged
into the repository default branch before the Release is published. After successful platform builds, the first merge to `main` automatically creates the release as a draft, attaches verified binaries and SHA-256 checksums, and then publishes it. Do not create a competing manual tag while the workflow is running.

## Expected GitHub Release downloads

| Asset | Intended audience and meaning |
| --- | --- |
| `SudokuNova-v0.2.0-windows.msi` | Windows desktop installer |
| `SudokuNova-v0.2.0-macos.dmg` | macOS desktop installer, not notarized here |
| `SudokuNova-v0.2.0-linux.deb` | Debian-family Linux installer |
| `SudokuNova-v0.2.0-android-DEBUG.apk` | **Testing only**: Android debug-signed build with `.debug` application ID suffix |
| `SudokuNova-v0.2.0-web.zip` | WebAssembly/browser distribution, for static hosting |
| `SudokuNova-v0.2.0-ios-simulator-framework.zip` | iOS simulator development framework; **not an IPA or installable iOS app** |
| `SHA256SUMS.txt` | SHA-256 hashes of the generated files |

If and only if all six trusted production-signing inputs are configured,
and the signed APK/AAB pass the repository's certificate and manifest
verification, **two additional assets** are attached:

- `SudokuNova-v0.2.0-android-signed.apk` — verified signed production APK.
- `SudokuNova-v0.2.0-android-signed.aab` — verified signed Android App Bundle for store submission.

The six secrets are `SUDOKUNOVA_KEYSTORE_BASE64`,
`SUDOKUNOVA_KEYSTORE_PASSWORD`, `SUDOKUNOVA_KEY_ALIAS`,
`SUDOKUNOVA_KEY_PASSWORD`, `SUDOKUNOVA_APK_CERT_SHA256`, and
`SUDOKUNOVA_AAB_CERT_SHA256`. Certificate fingerprints must come from a
trusted independent signing record, not the output being verified. A partial
configuration fails closed; an absent configuration produces only the clearly
labelled debug APK. Secrets and keystores are never release assets.

macOS native `jpackage` rejects a major version of zero. Its internal installer version is `1.2.0` while the public release remains `v0.2.0`. The Desktop installers do not gain platform code signatures automatically.
A signed, notarized macOS app, a reputation-signed Windows MSI and a
user-installable iOS IPA require additional platform-specific credentials,
policies, testing and (for iOS) an Xcode host build target.

## Maintainer steps

1. Complete code review and wait for exact-head CI, instrumentation and
   cross-platform checks to pass; perform device/runtime and accessibility QA.
2. Merge the release PR into the default branch. Check source version, release
   contract and build results again on the merge commit.
3. Configure trusted Android production signing secrets if production Android
   downloads are expected. Follow `docs/PRODUCTION_SIGNING.md`.
4. Create an immutable `v0.2.0` tag **on the verified release commit**, then
   publish a GitHub Release from that tag.
5. Inspect **Actions → Publish Cross-Platform Release Assets**. After it
   succeeds, confirm the expected assets and `SHA256SUMS.txt` exist on the
   release page and verify downloaded files against their SHA-256 checksums.
6. Confirm supported platform installation, runtime, and compatibility
   independently. Do not advertise an unsigned build as a signed production binary.

If this fails, fix the cause, revalidate the intended tag, and manually dispatch
the workflow with `tag = v0.2.0`. It is safe to re-upload verified filenames via
`gh release upload --clobber`, but do not move a published tag to conceal a defect.

## Local checks

```bash
python -m unittest scripts.tests.test_release_artifacts
python -m scripts.release_artifacts check-tag --tag v0.2.0
python scripts/verify_release_contract.py
./gradlew :sudoku-engine:desktopTest :sharedUI:desktopTest :app:testDebugUnitTest
```

The tests and source checks alone do not prove all runners can package installers
or that users can install them; GitHub Actions and real-device evidence are required.
