# Development Setup

## Toolchain

The current project configuration uses:

- JDK 17
- Gradle 9.5.0
- Android Gradle Plugin 9.3.0
- Kotlin 2.4.10
- compileSdk / targetSdk 37
- minSdk 26

Use the repository Gradle Wrapper after it is present and verified. CI also provisions the pinned Gradle version independently to verify the project.

## Recommended Android Studio Setup

1. Open the repository root.
2. Set the Gradle JDK to Java 17.
3. Install Android SDK Platform 37.
4. Let Gradle sync finish before editing generated/imported project models.
5. Use a representative phone emulator for daily work and add tablet/minimum-SDK emulators for release QA.

## Repository Modules

- `app`: Android application, Compose UI, ViewModels, DataStore persistence, navigation.
- `sudoku-engine`: Android-independent board, solver, generator, difficulty, and hint logic.

Keep engine logic out of Android-specific code unless platform integration is genuinely required.

## Git Identity

For this repository, project commits are expected to use:

```bash
git config user.name "Sanskar"
git config user.email "sanskarin@outlook.in"
```

These commands modify repository-local Git configuration when run inside the repository without `--global`.

## Recommended Local Workflow

```bash
git switch main
git pull --ff-only
git switch -c feature/your-focused-change
```

Implement a focused unit of work, then run the applicable verification tasks before committing.

## Verification

Fast engine test:

```bash
./gradlew :sudoku-engine:test
```

Android unit tests:

```bash
./gradlew :app:testDebugUnitTest
```

Lint:

```bash
./gradlew :app:lintDebug
```

Debug build:

```bash
./gradlew :app:assembleDebug
```

Full current CI-equivalent verification:

```bash
./gradlew :sudoku-engine:test :app:testDebugUnitTest :app:lintDebug :app:assembleDebug
```

## Source Namespace vs. Application ID

Kotlin source uses `com.sanskar.sudokunova` because `in` is a Kotlin language keyword and cannot be used as an unescaped first package identifier. The Android application ID remains:

`in.sanskar.sudokunova`

Do not change the public application ID casually once production distribution begins.

## Secrets

The project should build for development without private secrets. Production signing configuration must remain outside version control. `.gitignore` excludes common signing/secret files.
