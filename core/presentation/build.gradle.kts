plugins {
    alias(libs.plugins.convention.cmp.library)
}

kotlin {

    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.kotlin.stdlib)
                implementation(projects.core.domain)
                implementation(libs.components.resources)

                implementation(libs.material3.adaptive )
                implementation(libs.bundles.koin.common )


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