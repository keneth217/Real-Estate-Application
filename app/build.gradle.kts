plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.gms.google-services")
    id("kotlin-parcelize")
//
//    id("com.google.gms.google-services")
}

android {
    namespace = "com.keneth.realestateapplication"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.keneth.realestateapplication"
        minSdk = 24
        targetSdk = 35
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    //firebase
    // implementation(platform("com.google.firebase:firebase-bom:32.6.0"))
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-firestore")
//datastore
    implementation("androidx.datastore:datastore-preferences:1.0.0")

    implementation("com.google.firebase:firebase-analytics")

    implementation("com.google.android.gms:play-services-auth:20.7.0")// Use the latest version
    implementation(platform("com.google.firebase:firebase-bom:33.9.0"))

    implementation("androidx.compose.material:material:1.5.0")
    implementation("androidx.compose.material:material:1.7.7")
    implementation("androidx.graphics:graphics-shapes:1.0.1")
    ///implementation(libs.androidx.navigation.runtime.ktx)
//swipe to refresh

    implementation("com.google.accompanist:accompanist-swiperefresh:0.36.0")
    implementation(libs.firebase.storage.ktx)

    val lifecycleversion = "2.8.7"
    val retrofitversion = "2.9.0"

    val navversion = "2.8.7"

    implementation("androidx.navigation:navigation-compose:$navversion")

    // Jetpack Navigation Compose
    implementation("androidx.navigation:navigation-compose:$navversion")

    // ViewModel for Jetpack Compose
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycleversion")

    // Retrofit & Gson Converter
    implementation("com.squareup.retrofit2:retrofit:$retrofitversion")
    implementation("com.squareup.retrofit2:converter-gson:$retrofitversion")


    //image loading
    implementation("io.coil-kt.coil3:coil-compose:3.1.0")
    implementation("io.coil-kt.coil3:coil-network-okhttp:3.1.0")

    implementation("io.coil-kt:coil-compose:2.4.0") // Use the latest version
    //material icons
    implementation("com.google.android.material:material:1.8.0")




    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}