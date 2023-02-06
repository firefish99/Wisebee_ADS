pluginManagement {
    repositories {
        mavenLocal()
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenLocal()
        google()
        mavenCentral()
        maven(url = "https://jitpack.io")
    }
    versionCatalogs {
        create("libs") {
            from("no.nordicsemi.android.gradle:version-catalog:1.2.2")
        }
    }
}
rootProject.name = "Wisebee_ADS"

include(":app")
include(":scanner")
include(":autodoor:spec")
include(":autodoor:ui")
include(":autodoor:ble")

if (file("../Android-Common-Libraries").exists()) {
    includeBuild("../Android-Common-Libraries")
}
//if (file("../Android-BLE-Library").exists()) {
//    includeBuild("../Android-BLE-Library")
//}
