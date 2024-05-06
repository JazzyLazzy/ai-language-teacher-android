@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
    id("org.jetbrains.kotlin.plugin.serialization")
    kotlin("kapt")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.example.lingo_ai"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.lazarus.lingo_ai"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.RobolTestRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
        viewBinding = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.11"
    }


    signingConfigs {
        create("release") {
            storeFile = file("../../lingo_ai_keys/la_key.jks")
            storePassword = "Bogus42069"
            keyAlias = "la"
            keyPassword = "Bogus42069"
        }
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation(platform(libs.compose.bom))
    implementation(libs.ui)
    implementation(libs.ui.graphics)
    implementation(libs.ui.tooling.preview)
    implementation(libs.androidx.material3.android)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.monitor)
    androidTestImplementation(platform(libs.compose.bom))
    androidTestImplementation(libs.ui.test.junit4)
    implementation("androidx.compose.runtime:runtime-livedata:1.6.6")
    /*val composeBom = platform("androidx.compose:compose-bom:2024.03.00")
    implementation(composeBom)
    androidTestImplementation(composeBom)*/

    /*implementation("androidx.appcompat:1.61")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation("androidx.compose:runtime:1.6.4")
    implementation("androidx.compose:foundation:1.6.4")
    implementation("androidx.compose:foundation-layout:1.6.4")
    implementation("androidx.compose:ui-util:1.6.4")
    implementation("androidx.compose:animation:1.6.4")
    implementation("androidx.compose.material3:1.2.0")
    implementation("androidx.compose:ui-tooling-preview:1.6.4")
    implementation("androidx.compose:runtime-livedata:1.6.4")
    debugImplementation("androidx.compose:ui-tooling:1.6.4")
    implementation("androidx.navigation:navigation-compose:2.5.0")
    implementation("androidx.compose.runtime:runtime:1.6.4")
    implementation("androidx.compose.compiler:compiler:1.6.4")*/


    //implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")


    implementation(libs.core.ktx)
    implementation(libs.appcompat)
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    implementation(libs.androidx.constraintlayout)
    implementation(project(":cloudapi"))
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation("com.squareup.okhttp3:okhttp:4.9.3")
    implementation("com.github.heremaps:oksse:0.9.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
    implementation("androidx.compose.material:material-icons-extended:1.6.6")
    implementation("androidx.activity:activity:1.8.1")
    debugImplementation(libs.ui.tooling)
    debugImplementation(libs.ui.test.manifest)

    implementation("com.google.dagger:hilt-android:2.48.1")
    kapt("com.google.dagger:hilt-android-compiler:2.48.1")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")
    androidTestImplementation("com.google.dagger:hilt-android-testing:2.48.1")


    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    testImplementation ("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.1")
    testImplementation ("junit:junit:4.13.2")

    testImplementation ("androidx.test:core-ktx:1.5.0")
    testImplementation("org.robolectric:robolectric:4.4")

}
