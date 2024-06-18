// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    extra.apply {
        set("room_version", "2.6.1")
        set("nav_version", "2.7.7")
        set("paging_version", "3.2.1")
    }

    dependencies {
        classpath(libs.hilt.android.gradle.plugin)
    }
}

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
    id("androidx.navigation.safeargs.kotlin") version "2.7.7" apply false
    id("com.google.dagger.hilt.android") version "2.44" apply false
    id("com.google.devtools.ksp") version "1.9.22-1.0.17" apply false
}