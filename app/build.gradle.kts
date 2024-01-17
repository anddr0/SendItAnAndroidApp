plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
//    kotlin("jvm") version "1.9.22"
    kotlin("plugin.serialization") version "1.9.22"
}

android {
    namespace = "com.example.sendit"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.sendit"
        minSdk = 26
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
        viewBinding = true
    }
}
buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.22")
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
//    implementation ("androidx.fragment:fragment-ktx:1.2.5")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.6")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.6")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.2")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    //Supabase
    implementation(platform("io.github.jan-tennert.supabase:bom:2.0.4"))
    implementation("io.github.jan-tennert.supabase:postgrest-kt:2.0.4")
    implementation("io.ktor:ktor-client-android:2.3.7")
    implementation("com.google.code.gson:gson:2.9.0")
    // Google Sign-In
    implementation ("com.google.android.gms:play-services-auth:20.7.0")
    //Serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
}