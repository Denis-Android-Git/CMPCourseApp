import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    `kotlin-dsl`
}
group = "com.example.convention.buildlogic"

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.android.tools.common)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.compose.gradlePlugin)
    compileOnly(libs.androidx.room.gradle.plugin)

    implementation(libs.buildkonfig.gradlePlugin)
    implementation(libs.buildkonfig.compiler)
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}
kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_17
    }
}
tasks {
    validatePlugins {
        enableStricterValidation = true
        failOnWarning = true
    }
}
gradlePlugin {
    plugins {
        register("androidApplication") {
            id = "com.example.convention.android.application"
            implementationClass = "AndroidAppConventionPlugIn"
        }
    }
    plugins {
        register("androidComposeApplication") {
            id = "com.example.convention.android.application.compose"
            implementationClass = "AndroidAppComposeConventionPlugin"
        }
    }
    plugins {
        register("cmpApplication") {
            id = "com.example.convention.cmp.application"
            implementationClass = "CmpAppConventionPlugin"
        }
    }
    plugins {
        register("kmpLibrary") {
            id = "com.example.convention.kmp.library"
            implementationClass = "KmpLibraryConventionPlugin"
        }
    }
    plugins {
        register("cmpLibrary") {
            id = "com.example.convention.cmp.library"
            implementationClass = "CmpLibraryConventionPlugin"
        }
    }
    plugins {
        register("cmpFeature") {
            id = "com.example.convention.cmp.feature"
            implementationClass = "CmpFeatureConventionPlugin"
        }
    }
    plugins {
        register("buildKonfig") {
            id = "com.example.convention.buildkonfig"
            implementationClass = "BuildKonfigConventionPlugin"
        }
    }
    plugins {
        register("room") {
            id = "com.example.convention.room"
            implementationClass = "RoomConventionPlugin"
        }
    }
}