plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    alias(libs.plugins.about.libraries)
}

object AppInfo {
    const val APP_NAME = "Material Photo Widget"
    const val APPLICATION_ID = "com.fibelatti.photowidget"

    private const val VERSION_MAJOR = 1
    private const val VERSION_MINOR = 3
    private const val VERSION_PATCH = 0
    private const val VERSION_BUILD = 0

    val versionCode: Int =
        (VERSION_MAJOR * 1_000_000 + VERSION_MINOR * 10_000 + VERSION_PATCH * 100 + VERSION_BUILD)
            .also { println("versionCode: $it") }

    @Suppress("KotlinConstantConditions")
    val versionName: String =
        StringBuilder("$VERSION_MAJOR.$VERSION_MINOR")
            .apply { if (VERSION_PATCH != 0) append(".$VERSION_PATCH") }
            .toString()
            .also { println("versionName: $it") }
}

android {
    val compileSdkVersion: Int by project
    val targetSdkVersion: Int by project
    val minSdkVersion: Int by project

    namespace = "com.fibelatti.photowidget"
    compileSdk = compileSdkVersion

    buildFeatures {
        buildConfig = true
        compose = true
        viewBinding = true
    }

    androidResources {
        generateLocaleConfig = true
    }

    defaultConfig {
        applicationId = AppInfo.APPLICATION_ID
        versionCode = AppInfo.versionCode
        versionName = AppInfo.versionName
        targetSdk = targetSdkVersion
        minSdk = minSdkVersion

        base.archivesName = "$applicationId-v$versionName-$versionCode"

        vectorDrawables.useSupportLibrary = true
    }

    signingConfigs {
        getByName("debug") {
            storeFile = File("$rootDir/keystore/debug.keystore")
            keyAlias = "androiddebugkey"
            keyPassword = "android"
            storePassword = "android"
        }
    }

    buildTypes {
        getByName("debug") {
            applicationIdSuffix = ".debug"
            isMinifyEnabled = false
        }

        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            setProguardFiles(
                listOf(
                    getDefaultProguardFile("proguard-android-optimize.txt"),
                    File("proguard-rules.pro"),
                ),
            )
        }
    }

    androidComponents {
        onVariants { variant ->
            val appName =
                StringBuilder().apply {
                    append(AppInfo.APP_NAME)
                    if (variant.name.contains("debug", ignoreCase = true)) append(" Dev")
                }.toString()

            variant.resValues.put(
                variant.makeResValueKey("string", "app_name"),
                com.android.build.api.variant.ResValue(appName, null),
            )
        }
    }

    sourceSets {
        forEach { sourceSet -> getByName(sourceSet.name).java.srcDirs("src/${sourceSet.name}/kotlin") }
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.composeCompiler.get()
    }

    packaging {
        resources.excludes.add("META-INF/LICENSE.md")
        resources.excludes.add("META-INF/LICENSE-notice.md")
    }
}

dependencies {
    implementation(project(":ui"))

    // Kotlin
    implementation(libs.kotlin)
    implementation(libs.coroutines.core)
    implementation(libs.coroutines.android)

    // Android Platform
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.graphics.shapes)
    implementation(libs.lifecycle.runtime.compose)
    implementation(libs.work.runtime.ktx)

    // Compose
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.runtime)
    implementation(libs.compose.material3)
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.tooling.preview)
    debugImplementation(libs.compose.ui.tooling)

    // Misc
    ksp(libs.dagger.hilt.compiler)
    ksp(libs.hilt.compiler)
    implementation(libs.dagger.hilt.android)
    implementation(libs.hilt.work)
    implementation(libs.hilt.navigation.compose)

    implementation(libs.ucrop)

    implementation(libs.about.libraries)

    debugImplementation(libs.leakcanary)
}
