plugins {
    // https://github.com/NordicSemiconductor/Android-Gradle-Plugins/blob/main/plugins/src/main/kotlin/AndroidLibraryConventionPlugin.kt
    alias(libs.plugins.nordic.library)
    // https://github.com/NordicSemiconductor/Android-Gradle-Plugins/blob/main/plugins/src/main/kotlin/AndroidLKotlinConventionPlugin.kt
    alias(libs.plugins.nordic.kotlin)
}

android {
    namespace = "com.wisebee.autodoor.transport_ble"
}

dependencies {
    implementation(project(":autodoor:spec"))

    // Import BLE Library
    implementation(libs.nordic.ble.ktx)
    // BLE events are logged using Timber
    implementation(libs.timber)
    // Coroutines
    implementation(libs.kotlinx.coroutines.core)
}