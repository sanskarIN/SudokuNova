pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    // Kotlin/Wasm setup tasks attach their Node.js distribution repository at the project level.
    // Prefer the centrally declared repositories while allowing that toolchain-managed repository.
    repositoriesMode.set(RepositoriesMode.PREFER_PROJECT)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "SudokuNova"
include(":app")
include(":sudoku-engine")
include(":sharedUI")
include(":macrobenchmark")
