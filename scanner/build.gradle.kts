plugins {
    // https://github.com/NordicSemiconductor/Android-Gradle-Plugins/blob/main/plugins/src/main/kotlin/AndroidFeatureConventionPlugin.kt
    alias(libs.plugins.nordic.feature)
}

android {
    namespace = "com.wisebee.autodoor.scanner"
}

dependencies {
    implementation(project(":autodoor:spec"))
    implementation(project(":autodoor:ui"))

    implementation(libs.nordic.uiscanner)
    implementation(libs.nordic.navigation)
}