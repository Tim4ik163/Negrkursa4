plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.negrskursack"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.negrskursack"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Добавлено для медиа-сервисов
        buildConfigField("String", "MEDIA_SESSION_TAG", "\"MusicService\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
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

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
}

dependencies {
    // Базовые зависимости
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)

    // Архитектурные компоненты
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.lifecycle.service) // Добавлено для сервисов

    // Навигация
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)

    // Медиа-зависимости
    implementation(libs.androidx.media3.exoplayer)
    implementation(libs.androidx.media3.session)
    implementation("androidx.media:media:1.7.0")

    // RecyclerView
    implementation("androidx.recyclerview:recyclerview:1.3.2")

    // Тестирование
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}