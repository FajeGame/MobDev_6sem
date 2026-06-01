import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}

val localProperties = Properties().apply {
    val file = rootProject.file("local.properties")
    if (file.exists()) {
        file.inputStream().use { load(it) }
    }
}

android {
    namespace = "com.example.semka_6sem"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.example.semka_6sem"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        val yandexClientId = localProperties.getProperty("yandex.client.id", "replace_me")
        buildConfigField("String", "YANDEX_CLIENT_ID", "\"$yandexClientId\"")
        resValue("string", "yandex_client_id", yandexClientId)
        manifestPlaceholders["YANDEX_CLIENT_ID"] = yandexClientId
    }

    flavorDimensions += "version"
    productFlavors {
        create("demo") {
            dimension = "version"
            isDefault = true
            applicationIdSuffix = ".demo"
            buildConfigField("boolean", "IS_DEMO", "true")
            buildConfigField("String", "FLAVOR_LABEL", "\"demo\"")
            buildConfigField("String", "BACKEND_URL", "\"https://habits-demo.example.com\"")
        }
        create("full") {
            dimension = "version"
            buildConfigField("boolean", "IS_DEMO", "false")
            buildConfigField("String", "FLAVOR_LABEL", "\"full\"")
            buildConfigField("String", "BACKEND_URL", "\"https://habits.example.com\"")
        }
    }

    signingConfigs {
        create("release") {
            val debugStore = File(System.getProperty("user.home"), ".android/debug.keystore")
            if (debugStore.exists()) {
                storeFile = debugStore
                storePassword = "android"
                keyAlias = "androiddebugkey"
                keyPassword = "android"
            }
        }
    }

    buildTypes {
        debug {
            isMinifyEnabled = false
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            signingConfig = signingConfigs.getByName("release")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    implementation(project(":domain"))
    implementation(project(":data"))

    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.1.4")

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.10.0")
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation("androidx.compose.material:material-icons-extended")

    implementation(libs.navigation.compose)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.work.runtime.ktx)
    implementation(libs.kotlinx.coroutines.android)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.9.0")

    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.messaging)

    implementation(libs.mlkit.text.recognition)
    implementation(libs.yandex.authsdk)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}

if (file("google-services.json").exists()) {
    apply(plugin = "com.google.gms.google-services")
}
