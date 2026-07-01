plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.skillslot.feature.puzzle"
    compileSdk = 35

    defaultConfig {
        minSdk = 26
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
}

dependencies {
    implementation(project(":core:model"))
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
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.lifecycle.runtime.compose)
}
