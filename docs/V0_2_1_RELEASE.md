# SudokuNova v0.2.1 — Windows/Desktop Release

**Scope:** focused patch release improving the Windows desktop experience, retaining the shared gameplay engine and cross-platform release pipeline.

## Release identity

- GitHub release tag: `v0.2.1`
- Android `versionName`: `0.2.1`
- Android `versionCode`: `2021` (must increase for existing installs)
- Compose Desktop public `packageVersion`: `0.2.1`
- macOS native `packageVersion`: `1.2.1` because jpackage rejects 0.x on macOS
- Windows MSI `ProductVersion`: `0.2.1`
- Developer website: https://sanskarin.github.io

## Windows improvements

1. **Native desktop Game and Help menus.** The Game menu provides New puzzle, Undo move, Toggle notes, Hint, Reset puzzle and Exit. Ctrl+N, Ctrl+Z, Ctrl+M and Ctrl+H activate the first four actions on Windows; macOS uses Command equivalents. The common Sudoku grid no longer consumes Control/Meta-modified combinations.
2. **More comfortable default desktop window size.** The first window opens at 1100 × 820 density-independent pixels and remains resizable. The existing shared layout adapts to window width and keeps scrollable controls available on smaller screens.
3. **Website discovery.** The desktop Help menu opens the developer website and source repository using the default browser; unsupported browser environments show the complete URL so it can be opened manually. Android's About screen also exposes the website with English/Hindi labels.
4. **Installer behavior.** MSI now offers a per-user installation and installation-path chooser, places SudokuNova in a named Start-menu group, and uses a stable `upgradeUuid` for future releases: `7E35B8B7-C375-5C8F-A3D0-94529BEBF96F`.
5. **Installer integrity.** Windows CI reads the MSI Property table and rejects unexpected `ProductName`, `ProductVersion`, or `UpgradeCode`. All release files are still checked for expected formats and published with SHA-256 hashes.

### Upgrade compatibility

The explicit MSI upgrade identifier is new in v0.2.1. Since v0.2.0 did not set that same identifier explicitly, **in-place MSI upgrades from v0.2.0 are not guaranteed**. If an upgrade fails or a duplicate installation appears, uninstall the v0.2.0 application using Windows Settings before installing v0.2.1. Back up any valuable puzzle state first. Do not remove user preferences manually unless troubleshooting warrants it.

## Release automation

The GitHub Actions workflow `.github/workflows/publish-release-assets.yml` runs on the first merge updating that workflow on `main`. It validates the v0.2.1 tag/source contract, executes unit tests, builds required Windows, macOS, Linux, Web, Android and iOS simulator artifacts, verifies that all required files are present, creates `SHA256SUMS.txt`, and publishes a GitHub Release **only if every required job succeeds**. The Windows job additionally validates embedded MSI version and upgrade metadata.

Expected assets:

- `SudokuNova-v0.2.1-windows.msi`
- `SudokuNova-v0.2.1-macos.dmg`
- `SudokuNova-v0.2.1-linux.deb`
- `SudokuNova-v0.2.1-web.zip`
- `SudokuNova-v0.2.1-android-DEBUG.apk`
- `SudokuNova-v0.2.1-ios-simulator-framework.zip`
- `SHA256SUMS.txt`

If complete trusted Android signing secrets and certificate fingerprints are configured, the workflow may also attach a signed production APK and AAB. Without those secrets, the only Android download is a **testing-only DEBUG APK**, and the iOS framework is **not an IPA**. Windows installer code-signing and macOS notarization still require separate credentials and manual release evidence.

## Required verification

- Confirm `Android CI`, `Android Instrumentation`, and `Cross-Platform CI` pass on the exact final PR commit.
- Confirm the Windows packaging stage and `scripts/verify_windows_msi.ps1` succeed on a genuine Windows runner.
- After merging, inspect `Publish Cross-Platform Release Assets` and confirm the release is **published**, not draft, with all expected binaries and matching checksums.
- Run manual Windows installation, uninstall/upgrade, application startup, DPI/resize, keyboard accessibility, website-open and restored-game tests on actual machines before claiming broad production compatibility.

## Maintainer note

Do not move an already published `v0.2.0` tag. Publish `v0.2.1` as a new patch release. Never report unknown runtime checks as passed.
