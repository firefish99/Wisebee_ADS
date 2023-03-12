plugins {
    // https://github.com/NordicSemiconductor/Android-Gradle-Plugins/blob/main/plugins/src/main/kotlin/AndroidFeatureConventionPlugin.kt
    alias(libs.plugins.nordic.feature)
    // https://developer.android.com/kotlin/parcelize
    id("kotlin-parcelize")
}

android {
    namespace = "com.wisebee.autodoor.control"
}

dependencies {
    implementation(project(":autodoor:spec"))
    implementation(project(":autodoor:ble"))

    implementation(libs.nordic.theme)
    implementation(libs.nordic.uilogger)
    implementation(libs.nordic.uiscanner)
    implementation(libs.nordic.navigation)
    implementation(libs.nordic.permission)
    implementation(libs.nordic.log.timber)

    implementation(libs.androidx.compose.material.iconsExtended)
}