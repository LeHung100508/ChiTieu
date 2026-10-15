pluginManagement {
    includeBuild("build-logic")
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "ChiTieu"
include(":app")
include(":feature:budget")
include(":feature:onboarding")
include(":feature:quickadd")
include(":feature:settings")
include(":feature:stats")
include(":feature:transactions")
include(":core:model")
include(":core:parser")
include(":core:database")
include(":core:datastore")
include(":core:data")
include(":core:designsystem")
include(":core:ui")
include(":core:testing")
include(":benchmarks")