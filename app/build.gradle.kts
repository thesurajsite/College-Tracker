plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
}

android {
    namespace = "com.collegetracker"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.collegetracker"
        minSdk = 27
        targetSdk = 33
        versionCode = 34
        versionName = "2.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
//    kotlinOptions {
//        jvmTarget = "1.8"
//    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    implementation("androidx.room:room-ktx:2.6.1")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")


    //ROOM DATABSE DEPENDENCIES
    val room_version = "2.6.1"

    implementation("androidx.room:room-runtime:$room_version")
    // To use Kotlin annotation processing tool (kapt)
    kapt("androidx.room:room-compiler:$room_version")
    // optional - Kotlin Extensions and Coroutines support for Room
    implementation("androidx.room:room-ktx:$room_version")

    //DEPENDENCIES FROM GITHUB FOR COUROURITINE
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0-RC")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.0-RC")

    //SDP DEPENDENCIES
    implementation ("com.intuit.sdp:sdp-android:1.1.0")

    //In-app Update
    implementation("com.google.android.play:app-update-ktx:2.1.0")

    //Circular Progress Bar
    implementation ("com.mikhaellopez:circularprogressbar:3.1.0")
}