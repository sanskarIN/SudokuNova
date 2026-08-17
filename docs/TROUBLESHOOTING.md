# Troubleshooting

## Gradle Uses the Wrong Java Version

Check:

```bash
java -version
./gradlew --version
```

The current build requires JDK 17. In Android Studio, also verify the project's Gradle JDK setting.

## Android SDK Platform Missing

If compilation reports a missing SDK/platform, install Android SDK Platform 37 through Android Studio SDK Manager, then sync/rebuild.

## Wrapper Fails to Start

Confirm these files exist:

- `gradlew`
- `gradlew.bat`
- `gradle/wrapper/gradle-wrapper.properties`
- `gradle/wrapper/gradle-wrapper.jar`

Do not download an arbitrary wrapper JAR from an unofficial source. The project bootstrap workflow generates the official pinned wrapper and checks its SHA-256.

## Engine Test Fails

Run only engine tests first:

```bash
./gradlew :sudoku-engine:test --stacktrace
```

If a deterministic generator test fails, record the exact seed and generated puzzle before changing the algorithm.

## App Test Fails

```bash
./gradlew :app:testDebugUnitTest --stacktrace
```

Inspect the specific assertion/root exception, not only the final Gradle failure line.

## Lint Fails

```bash
./gradlew :app:lintDebug
```

Open generated lint reports under the app module's `build/reports` directory. Prefer fixing the issue to suppressing it. Add a suppression only when the warning is understood and the code is intentionally safe.

## Puzzle Generation Appears Slow

Generation performs repeated uniqueness solving and can be CPU-intensive for harder targets. It is dispatched away from the Android main thread. If a reproducible performance regression appears, report the seed/difficulty/device and measure it before optimizing.

## Active Game Does Not Resume

Check whether the saved text decodes under the current `GameStateCodec` version. Malformed/unsupported state should be rejected safely rather than crashing. If a schema change caused the regression, add a codec migration or intentional fallback and regression test.

## Custom Puzzle Cannot Be Played

The current Play flow requires exactly one solution. Validate the puzzle and read the status message. Common causes:

- Duplicate clue in a row
- Duplicate clue in a column
- Duplicate clue in a box
- No solution
- Multiple solutions
- Too few clues to proceed with the editor's uniqueness pre-check

## UI Looks Different on Another Device

Material You dynamic color can intentionally change in-app colors. Compare with dynamic color disabled before treating color differences as a rendering bug.

For layout issues include window size/orientation/font scale in a bug report.

## CI Is Red but Local Build Passes

Compare:

- JDK version
- Exact commit SHA
- Gradle/AGP/Kotlin versions
- SDK availability
- CI task that failed

Use the GitHub Actions job log and uploaded verification reports. Do not merge solely because one local environment passes.

## Still Need Help

Public reproducible bugs: https://github.com/sanskarIN/SudokuNova/issues  
Support: `supportramsandesh@gmail.com`
