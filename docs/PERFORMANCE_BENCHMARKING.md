# Performance Benchmarking and Evidence

SudokuNova includes a dedicated `:macrobenchmark` module for reproducible release-like Android startup and frame measurements. The harness exists to make real v1.0 performance evidence easier to collect; committed benchmark code is **not** itself proof that performance passed on a physical device.

## What is implemented

The benchmark path consists of:

- `:app` build type `benchmark`, initialized from `release`;
- R8/resource shrinking inherited from the release configuration;
- `benchmark` uses the debug signing key only so maintainers can benchmark locally without exposing the production keystore;
- `benchmark` remains non-debuggable;
- `app/src/benchmark/AndroidManifest.xml` enables shell profiling only for the benchmark variant;
- target-app `androidx.profileinstaller:profileinstaller:1.4.1` support for Macrobenchmark profile capture/reset and shader-cache operations;
- separate `:macrobenchmark` `com.android.test` module;
- Macrobenchmark library `1.4.1`;
- benchmark test-manifest visibility for `in.sanskar.sudokunova`;
- API-29 benchmark-output compatibility isolated to the test APK;
- cold-start timing benchmark;
- warm-start timing benchmark;
- startup frame-timing benchmark;
- `CompilationMode.None()` for a defined compilation starting state;
- ten iterations per benchmark method;
- standard CI compilation gate for the benchmark harness.

The production `release` manifest is not made profileable by this setup because the `<profileable>` declaration lives only in the `benchmark` source set. The Macrobenchmark test APK's package-visibility/output compatibility declarations likewise live only in `macrobenchmark/src/main/AndroidManifest.xml` and do not alter the production app manifest.

## Why ProfileInstaller is explicit

Android's Macrobenchmark setup guidance requires the target app to include a sufficiently recent ProfileInstaller so the benchmark framework can reliably perform profile capture/reset and shader-cache handling. SudokuNova pins `androidx.profileinstaller:profileinstaller:1.4.1` explicitly in the version catalog and app dependency graph rather than relying on an incidental transitive dependency.

This dependency does **not** mean SudokuNova currently ships a project-generated Baseline Profile. ProfileInstaller and a generated Baseline Profile are separate concerns; the Baseline Profile boundary remains documented below.

## Why this is a separate module

Macrobenchmarks execute outside the app process and drive the target app as a user/system would. This makes them appropriate for whole-app measurements such as startup and rendered-frame timing while preserving release-like behavior in the app under test.

The benchmark module targets:

`in.sanskar.sudokunova`

The target package is intentionally explicit so a benchmark cannot silently measure the debug package suffix.

## Supported benchmark targets

The `:macrobenchmark` module requires Android API 29 or newer. For stable-release evidence, use a representative **physical** Android device on a current supported OS version.

For the committed `CompilationMode.None()` startup suite, Android 14 / API 34 or newer is the preferred evidence target because modern Macrobenchmark can reset compilation state while preserving app state more reliably. API 29–33 remain useful for compatibility/smoke work, but runs on older releases can require reinstall behavior while compilation state is reset.

Do not treat emulator timings as production evidence. Emulators are useful for compilation/smoke/debugging of the benchmark harness but share host resources and are not representative performance targets.

## Build the benchmark harness

Linux/macOS:

```bash
./gradlew :macrobenchmark:assembleBenchmark --stacktrace
```

Windows:

```bat
gradlew.bat :macrobenchmark:assembleBenchmark --stacktrace
```

This is also compiled by standard Android CI. A successful compile proves the benchmark source/build graph is valid; it does not produce a physical-device performance pass.

## Run on a connected physical device

Connect exactly the intended target device, verify it with `adb devices`, then run:

Linux/macOS:

```bash
./gradlew :macrobenchmark:connectedBenchmarkAndroidTest --stacktrace
```

Windows:

```bat
gradlew.bat :macrobenchmark:connectedBenchmarkAndroidTest --stacktrace
```

Before recording evidence:

- use a physical target rather than an emulator;
- prefer Android 14 / API 34 or newer for the committed compilation-reset workflow;
- avoid simultaneously running heavy unrelated workloads on the target/host;
- keep the target’s power/thermal state reasonably consistent across comparison runs;
- record whether the device is charging;
- record exact Android build/version and device model;
- use the exact intended source commit;
- do not change benchmark iterations between compared runs without recording that change.

## Current benchmark methods

`StartupBenchmark.coldStartup`

- cold process startup;
- `StartupTimingMetric`;
- no pre-existing compilation state;
- ten iterations.

`StartupBenchmark.warmStartup`

- warm process startup;
- `StartupTimingMetric`;
- no pre-existing compilation state;
- ten iterations.

`StartupBenchmark.startupFrameTiming`

- cold startup;
- `FrameTimingMetric`;
- no pre-existing compilation state;
- ten iterations.

The benchmark deliberately calls `pressHome()` during setup and launches the real application through `startActivityAndWait()` in the measured block.

## Evidence to retain

For each release-candidate benchmark run, record:

- exact Git commit SHA;
- application ID;
- versionCode/versionName;
- device manufacturer/model;
- Android version/build;
- physical/emulator status;
- build/benchmark variant;
- benchmark method;
- compilation mode;
- iteration count;
- benchmark output artifact/report path;
- timing percentiles reported by Macrobenchmark;
- trace artifact location when captured;
- unexpected thermal/background-load observations;
- pass/block decision and reason.

Put the concise release result in `V1_RELEASE_EVIDENCE.md` and detailed observations in `V1_RELEASE_CANDIDATE.md` or an attached non-sensitive evidence artifact.

## Interpreting results

Do not invent a performance threshold just to convert a measurement into PASS/FAIL. First establish a trustworthy baseline on representative hardware.

When comparing two commits:

- keep the same device/OS where practical;
- keep benchmark method and iteration count identical;
- compare the same reported metric/percentile;
- inspect traces when a regression is material rather than relying only on one aggregate number;
- rerun suspicious outliers before treating them as a source regression;
- do not hide a reproducible regression by changing compilation mode, device, iteration count, or benchmark scope.

## CI boundary

Standard CI runs:

```bash
./gradlew :macrobenchmark:assembleBenchmark --stacktrace
```

It intentionally does not turn hosted-emulator timings into a stable-release performance claim.

The API-35 instrumentation workflow remains a functional Compose/Room correctness gate. It is not the physical-device performance evidence gate.

## Baseline Profile boundary

The v1.0 release line currently adds measurement infrastructure, not a generated Baseline Profile optimization. Introducing or refreshing Baseline Profiles changes the performance-delivery pipeline and should be done as a separately reviewed optimization with its own generation, packaging, benchmark comparison, and exact-release verification.

The presence of ProfileInstaller is required benchmark/profile infrastructure and must not be described as proof that a SudokuNova-generated Baseline Profile exists or improves startup.

Do not claim Baseline Profile performance benefits unless a profile is actually generated, packaged, and measured for SudokuNova.

## Release gate

The v1.0 performance row remains `PENDING` until representative physical-device measurements are actually run and recorded. The existence of `:macrobenchmark` reduces the manual work needed to produce that evidence but does not satisfy it automatically.
