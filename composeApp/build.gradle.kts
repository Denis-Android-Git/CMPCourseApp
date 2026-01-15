plugins {
    alias(libs.plugins.convention.cmp.application)
    alias(libs.plugins.compose.hot.reload)
}

kotlin {
    sourceSets {
        androidMain.dependencies {
            implementation(libs.ui.tooling.preview)
            implementation(libs.androidx.activity.compose)
            implementation(libs.koin.android)

            implementation(libs.core.splashscreen)
        }
        commonMain.dependencies {
            implementation(projects.core.data)
            implementation(projects.core.domain)
            implementation(projects.core.designsystem)
            implementation(projects.core.presentation)
            implementation(projects.feature.auth.presentation)
            implementation(projects.feature.auth.domain)
            implementation(projects.feature.chat.data)
            implementation(projects.feature.chat.domain)
            implementation(projects.feature.chat.presentation)
            implementation(projects.feature.chat.database)

            implementation(libs.runtime.v1100)
            implementation(libs.foundation.v1100)
            implementation(libs.material3.v190)
            implementation(libs.ui.v1100)
            implementation(libs.components.resources)
            implementation(libs.ui.tooling.preview)
            implementation(libs.jetbrains.compose.viewmodel)
            implementation(libs.jetbrains.lifecycle.compose)
            implementation(libs.jetbrains.compose.navigation)
            implementation(libs.bundles.koin.common)
        }
        iosMain.dependencies {
            api(project(":core:domain")) //access to core/data in Xcode

        }
    }
}