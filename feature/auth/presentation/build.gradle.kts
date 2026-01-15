plugins {
    alias(libs.plugins.convention.cmp.feature)
}

kotlin {

    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.kotlin.stdlib)

                implementation(projects.core.domain)
                implementation(projects.feature.auth.domain)
                implementation(projects.core.designsystem)
                implementation(projects.core.presentation)

                implementation(libs.components.resources)
                implementation(libs.ui.tooling.preview)
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