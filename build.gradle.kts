// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    val compose_version by extra("1.3.0")
    repositories {
        google()
    }
    dependencies{
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.44.2")
    }
}

@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.kotlinAndroid) apply false
    kotlin("jvm") version "1.9.23" apply false
    kotlin("plugin.serialization") version "1.9.21" apply false
    id("com.google.dagger.hilt.android") version "2.48.1" apply false
    kotlin("kapt") version "1.9.23"
}

true // Needed to make the Suppress annotation work for the plugins block
