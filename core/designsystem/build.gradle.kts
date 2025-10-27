plugins {
    alias(libs.plugins.convention.cmp.library)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.kotlin.stdlib)
                implementation(compose.components.resources)
                implementation(compose.components.uiToolingPreview)

                implementation(projects.core.presentation)

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