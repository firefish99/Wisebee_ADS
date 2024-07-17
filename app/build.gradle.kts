plugins {
    // https://github.com/NordicSemiconductor/Android-Gradle-Plugins/blob/main/plugins/src/main/kotlin/AndroidApplicationComposeConventionPlugin.kt
    alias(libs.plugins.nordic.application.compose)
    // https://github.com/NordicSemiconductor/Android-Gradle-Plugins/blob/main/plugins/src/main/kotlin/AndroidHiltConventionPlugin.kt
    alias(libs.plugins.nordic.hilt)
}

android {
    namespace = "com.wisebee.autodoor"
    defaultConfig {
        applicationId = "com.wisebee.autodoor"
        versionCode=16
        versionName="1.2.2"
        resourceConfigurations.add("en")
    }
}

dependencies {
    implementation(project(":scanner"))
    implementation(project(":autodoor:spec"))
    implementation(project(":autodoor:ui"))
    implementation(project(":autodoor:ble"))

    implementation(libs.nordic.theme)
    implementation(libs.nordic.navigation)

    implementation(libs.timber)

    implementation(libs.androidx.activity.compose)
}