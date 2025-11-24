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



                implementation(compose.components.resources)
                implementation(compose.components.uiToolingPreview)
                implementation(libs.bundles.koin.common)
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