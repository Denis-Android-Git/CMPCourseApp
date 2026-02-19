plugins {
    alias(libs.plugins.convention.cmp.feature)
}

kotlin {

    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.kotlin.stdlib)

                implementation(projects.feature.chat.domain)
                implementation(projects.core.domain)
                implementation(projects.core.designsystem)
                implementation(projects.core.data) //!!!not needed

                implementation(libs.components.resources)
                implementation(libs.ui.tooling.preview)
                implementation(libs.bundles.koin.common)

                implementation(libs.material3.adaptive)
                implementation(libs.material3.adaptive.layout)
                implementation(libs.material3.adaptive.navigation)
                implementation(libs.jetbrains.compose.backhandler)
                implementation(libs.components.resources)

                implementation(libs.jetbrains.navigation3.ui)
                implementation(libs.androidx.navigationevent)

            }
        }
        androidMain {
            dependencies {

            }
        }

        iosMain {
            dependencies {
            }
        }
    }

}
