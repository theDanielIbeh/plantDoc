plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("androidx.navigation.safeargs.kotlin")
    id("kotlin-kapt")
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.10"
}

android {
    namespace = "com.example.plantdoc"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.plantdoc"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        dataBinding = true
        mlModelBinding = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)

    implementation("androidx.fragment:fragment-ktx:1.6.2")
    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    implementation("androidx.paging:paging-runtime-ktx:3.2.1")

    implementation(libs.tensorflow.lite.support)
    implementation(libs.tensorflow.lite.metadata)
    //For Gradle, add to dependencies section of build.gradle
    implementation(libs.cloudinary.android)

    implementation(libs.androidx.navigation.fragment)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.room.ktx)
    implementation("androidx.room:room-runtime:2.6.1")
    implementation(libs.tensorflow.lite.gpu)
    ksp("androidx.room:room-compiler:2.6.1")

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Hilt
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)
    testImplementation(libs.hilt.android.testing)
    androidTestImplementation(libs.hilt.android.testing)
    kaptTest(libs.hilt.android.compiler)
    kaptAndroidTest(libs.hilt.android.compiler)

    implementation(libs.androidx.datastore.preferences)

    implementation(libs.gson)

    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.extensions)

    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
// Retrofit with Scalar Converter
    implementation("com.squareup.retrofit2:converter-scalars:2.9.0")

    implementation("com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:1.0.0")
    implementation("com.squareup.okhttp3:okhttp:4.11.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")

    // Paging 3 Integration
    implementation("androidx.paging:paging-runtime-ktx:${rootProject.extra["paging_version"]}")
    // alternatively - without Android dependencies for tests
    testImplementation("androidx.paging:paging-common-ktx:${rootProject.extra["paging_version"]}")
    // optional - RxJava2 support
    implementation("androidx.paging:paging-rxjava2-ktx:${rootProject.extra["paging_version"]}")
    // optional - RxJava3 support
    implementation("androidx.paging:paging-rxjava3:${rootProject.extra["paging_version"]}")
    // optional - Guava ListenableFuture support
    implementation("androidx.paging:paging-guava:${rootProject.extra["paging_version"]}")

    // optional - Paging 3 Integration
    implementation("androidx.room:room-paging:2.6.1")

    //Glide(Images)
    implementation("com.github.bumptech.glide:glide:4.14.2")
    annotationProcessor("com.github.bumptech.glide:compiler:4.14.2")
}