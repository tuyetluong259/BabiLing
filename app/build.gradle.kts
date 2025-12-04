import java.util.Properties
import java.nio.charset.StandardCharsets
val signingProperties = Properties()
val signingFile = rootProject.file("signing.properties")

if (signingFile.exists()) {
    // Đã thêm cách đọc mạnh mẽ để khắc phục lỗi I/O
    signingFile.reader(StandardCharsets.UTF_8).use { reader ->
        signingProperties.load(reader)
    }
}

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.gms.google-services")
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.example.babiling"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.babiling"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        vectorDrawables.useSupportLibrary = true
    }

    signingConfigs {
        create("releaseConfig"){
            storeFile = file("D:/Android_Keys/babiling/babiling_keys")

            storePassword = signingProperties.getProperty("KEYSTORE_STORE_PASSWORD")
                ?: throw GradleException("KEYSTORE_STORE_PASSWORD not found in signing.properties")

            keyAlias = "key0"

            keyPassword = signingProperties.getProperty("KEYSTORE_KEY_PASSWORD")
                ?: throw GradleException("KEYSTORE_KEY_PASSWORD not found in signing.properties")
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            signingConfig = signingConfigs.getByName("releaseConfig")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.10"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // Compose BOM (Quản lý phiên bản cho các thư viện Compose)
    implementation(platform(libs.androidx.compose.bom))

    // Core & Lifecycle
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.navigation.compose)

    // Compose UI
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)

    // ✨ 1. THÊM THƯ VIỆN ICON MỞ RỘNG ĐỂ SỬA LỖI "VolumeUp" ✨
    implementation(libs.androidx.compose.material.icons.extended)

    // Firebase BOM (Quản lý phiên bản cho các thư viện Firebase)
    implementation(platform(libs.firebase.bom))

    // Firebase libraries
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.auth.ktx)
    implementation(libs.firebase.firestore.ktx)
    implementation(libs.firebase.storage.ktx)
    implementation(libs.firebase.database.ktx)
    implementation(libs.firebase.appcheck.ktx)
    implementation(libs.firebase.appcheck.playintegrity)

    // Google Services
    implementation(libs.google.play.services.auth)
    implementation(libs.kotlinx.coroutines.play.services)

    // Room
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // Other Libraries (đã được thống nhất)
    implementation(libs.accompanist.insets.ui)
    implementation(libs.gson)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    //quiz
    implementation("androidx.media3:media3-exoplayer:1.3.1")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.7.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    //notification
    val work_version = "2.9.0"
    implementation("androidx.work:work-runtime-ktx:$work_version")
    implementation("io.coil-kt:coil-compose:2.5.0")
}
