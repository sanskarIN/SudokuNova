plugins {
    alias(libs.plugins.android.test)
}

android {
    namespace = "com.sanskar.sudokunova.macrobenchmark"
    compileSdk = 37

    defaultConfig {
        // Release evidence should use modern physical devices where shell profiling of a
        // non-debuggable app is supported through the benchmark-only <profileable> manifest.
        minSdk = 29
        targetSdk = 37
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    targetProjectPath = ":app"
    experimentalProperties["android.experimental.self-instrumenting"] = true

    buildTypes {
        create("benchmark") {
            isDebuggable = true
            matchingFallbacks += listOf("release")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    implementation(libs.androidx.benchmark.macro.junit4)
    implementation(libs.androidx.test.ext.junit)
}
