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