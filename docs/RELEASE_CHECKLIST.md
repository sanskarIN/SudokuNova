# Release Checklist

Use this checklist for every release candidate. Do not mark items complete without evidence.

## Version and Source

- [ ] Intended release commit is identified and frozen.
- [ ] `versionName` is correct.
- [ ] Android `versionCode` is greater than all previously distributed builds.
- [ ] `CHANGELOG.md` is updated with actual changes.
- [ ] `ROADMAP.md` reflects the delivered scope.
- [ ] `what_changed.md` is updated.
- [ ] No accidental TODO/sample/placeholder/dead navigation remains in release scope.

## Build and Tests

- [ ] `./gradlew clean` succeeds.
- [ ] `:sudoku-engine:test` passes.
- [ ] `:app:testDebugUnitTest` passes.
- [ ] Required instrumentation/UI tests pass.
- [ ] `:app:lintDebug` passes.
- [ ] Debug APK builds.
- [ ] Release APK builds.
- [ ] Release AAB builds.
- [ ] Code/resource shrinking release build is smoke-tested.
- [ ] No reproducible crash or ANR is known in the release scope.

## Sudoku Correctness

- [ ] Generated sample puzzles are valid.
- [ ] Generated sample puzzles have exactly one solution.
- [ ] Every supported difficulty can generate successfully.
- [ ] Notes work.
- [ ] Undo/redo works.
- [ ] Hints work without corrupting state.
- [ ] Completion is detected exactly once.
- [ ] Custom puzzle validation correctly handles invalid/no-solution/multiple-solution/unique cases.
- [ ] Active-game resume works after process recreation/termination tests.

## Data

- [ ] Existing supported saved data upgrades safely.
- [ ] Statistics are not double-counted.
- [ ] Reset/destructive actions clearly communicate impact.
- [ ] Import/backup migrations are tested if those features are part of the release.

## Accessibility

- [ ] TalkBack core game navigation checked.
- [ ] Cell semantics checked.
- [ ] Number pad/actions checked.
- [ ] Large font scaling checked.
- [ ] Contrast checked in light/dark themes.
- [ ] High-contrast behavior checked.
- [ ] Reduced-motion behavior checked.
- [ ] Tablet/large-window behavior checked.
- [ ] Keyboard navigation checked when supported.

## Privacy and Security

- [ ] Privacy policy matches the shipped binary.
- [ ] Permissions reviewed; no unnecessary permission exists.
- [ ] Exported components reviewed.
- [ ] External/deep-link/import input reviewed.
- [ ] Dependencies reviewed for known critical advisories.
- [ ] Secret scan/repository review completed.
- [ ] No keystore/password/token/private certificate is committed.
- [ ] `THIRD_PARTY_NOTICES.md` is current.

## Device QA

- [ ] Minimum supported Android API/device/emulator.
- [ ] Modern standard phone.
- [ ] Large phone.
- [ ] Tablet/large screen.
- [ ] Latest target Android version.
- [ ] Portrait/window resize/orientation behavior checked as applicable.
- [ ] Dark mode checked.
- [ ] Dynamic color checked where supported.

## Store / Release Assets

- [ ] Launcher icon is correct.
- [ ] Monochrome/adaptive icon is correct.
- [ ] Splash is correct.
- [ ] Screenshots represent actual current UI.
- [ ] Feature/social graphics represent actual current product.
- [ ] Store description contains no unimplemented claims.
- [ ] Privacy/Data Safety answers match actual behavior.

## Signing and Distribution

- [ ] Production signing uses secure external secrets.
- [ ] Final signed APK/AAB is installed/tested as applicable.
- [ ] Artifact checksums generated when distributing binaries through GitHub.
- [ ] Git tag points to the exact verified release commit.
- [ ] GitHub Release notes prepared from changelog.
- [ ] Rollback/fix-forward plan considered for data/security-sensitive changes.

## Final Approval

- [ ] CI is green on the release commit.
- [ ] Known limitations are documented.
- [ ] Critical/high-severity release-blocking issues are resolved.
- [ ] Documentation links work.
- [ ] Support/security contact details are correct.
- [ ] BMC mention remains optional/non-intrusive.
- [ ] `Made by the Sanskar` attribution is present in appropriate locations.
