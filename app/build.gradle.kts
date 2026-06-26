plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.skillslot.app"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.skillslot.app"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "0.3.0-phase2"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }

    buildFeatures {
        compose = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(project(":core:model"))
    implementation(project(":core:data"))
    implementation(project(":core:domain"))
    implementation(project(":feature-slot"))
    implementation(project(":feature-puzzle"))
    implementation(project(":feature-progression"))
    implementation(project(":feature-leaderboard"))
    implementation(project(":puzzle-engine"))
    implementation(project(":puzzles:puzzle-wordsearch"))
    implementation(project(":puzzles:puzzle-sudoku"))
    implementation(project(":puzzles:puzzle-ballsort"))
    implementation(project(":puzzles:puzzle-maze"))
    implementation(project(":puzzles:puzzle-boggle"))
    implementation(project(":puzzles:puzzle-memory"))
    implementation(project(":puzzles:puzzle-nonogram"))
    implementation(project(":puzzles:puzzle-sliding"))
    implementation(project(":puzzles:puzzle-connect"))
    implementation(project(":puzzles:puzzle-sequence"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.hilt.android)
    implementation(libs.hilt.navigation.compose)
    ksp(libs.hilt.compiler)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.text.google.fonts)
    implementation(libs.coil.compose)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)

    debugImplementation(libs.androidx.compose.ui.tooling)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
