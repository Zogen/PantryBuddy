import java.util.Properties

val localProps = Properties().apply {
    val localPropsFile = rootProject.file("local.properties")
    if (localPropsFile.exists()) {
        load(localPropsFile.inputStream())
    }
}

plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.pantrybuddy"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.pantrybuddy"
        minSdk = 21
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "EDAMAM_APP_ID", "\"${localProps["EDAMAM_APP_ID"]}\"")
        buildConfigField("String", "EDAMAM_APP_KEY", "\"${localProps["EDAMAM_APP_KEY"]}\"")
    }

    buildFeatures {
        buildConfig = true
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation(libs.retrofit)
    implementation(libs.gson)
    implementation(libs.glide)
    implementation(libs.androidx.work.runtime)
    implementation(kotlin("script-runtime"))
}

buildscript {
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.10") // or the version you're using
    }
}
