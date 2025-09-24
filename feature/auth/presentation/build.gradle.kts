plugins {
    alias(libs.plugins.convention.kmp.library)
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