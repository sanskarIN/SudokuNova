plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.ksp)
}

val releaseStorePath = providers.environmentVariable("SUDOKUNOVA_KEYSTORE_PATH").orNull
val releaseStorePassword = providers.environmentVariable("SUDOKUNOVA_KEYSTORE_PASSWORD").orNull
val releaseKeyAlias = providers.environmentVariable("SUDOKUNOVA_KEY_ALIAS").orNull
val releaseKeyPassword = providers.environmentVariable("SUDOKUNOVA_KEY_PASSWORD").orNull
val releaseSigningValues = listOf(
    releaseStorePath,
    releaseStorePassword,
    releaseKeyAlias,
    releaseKeyPassword,
)
val configuredReleaseSigningValues = releaseSigningValues.count { !it.isNullOrBlank() }
if (configuredReleaseSigningValues in 1..3) {
    error(
        "Release signing is partially configured. Provide all of " +
            "SUDOKUNOVA_KEYSTORE_PATH, SUDOKUNOVA_KEYSTORE_PASSWORD, " +
            "SUDOKUNOVA_KEY_ALIAS, and SUDOKUNOVA_KEY_PASSWORD, or provide none.",
    )
}
val hasReleaseSigning = configuredReleaseSigningValues == releaseSigningValues.size

android {
    namespace = "com.sanskar.sudokunova"
    compileSdk = 37

    defaultConfig {
        applicationId = "in.sanskar.sudokunova"
        minSdk = 26
        targetSdk = 37
        versionCode = 2015
        versionName = "2.0.15"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables.useSupportLibrary = true
    }

    signingConfigs {
        if (hasReleaseSigning) {
            create("release") {
                storeFile = file(requireNotNull(releaseStorePath))
                storePassword = requireNotNull(releaseStorePassword)
                keyAlias = requireNotNull(releaseKeyAlias)
                keyPassword = requireNotNull(releaseKeyPassword)
            }
        }
    }

    buildTypes {
        debug {
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            if (hasReleaseSigning) {
                signingConfig = signingConfigs.getByName("release")
            }
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
        create("benchmark") {
            initWith(getByName("release"))
            matchingFallbacks += listOf("release")
            signingConfig = signingConfigs.getByName("debug")
            isDebuggable = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
