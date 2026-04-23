plugins {
    alias(libs.plugins.convention.android.application)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.google.services)
}

dependencies {
    implementation(projects.composeApp)
    implementation(platform(libs.androidx.compose.bom))
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.material3)
    implementation(libs.core.splashscreen)
    implementation(libs.koin.android)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation(project.dependencies.platform(libs.firebase.bom))
    implementation(libs.firebase.messaging)
    implementation(libs.koin.android)
}