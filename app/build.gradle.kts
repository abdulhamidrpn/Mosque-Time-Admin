plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.rpn.adminmosque"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.rpn.adminmosque"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
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
        buildConfig = true
        viewBinding = true
        dataBinding = true
    }
}

dependencies {
    implementation(libs.coreKtx)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.navigationFragmentKtx)
    implementation(libs.navigationUiKtx)
    implementation(libs.testCoreKtx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.extJunit)
    androidTestImplementation(libs.espressoCore)
    implementation(libs.lifecycleLivedataKtx)
    implementation(libs.lifecycleViewmodelKtx)
    implementation(libs.workRuntime)
    implementation(libs.lifecycleRuntime)
    kapt(libs.lifecycleCompiler)
    implementation(libs.coroutinesCore)
    implementation(libs.coroutinesAndroid)
    implementation(libs.koinAndroid)
    implementation(libs.koinWorkmanager)
    implementation(libs.gson)
    implementation(libs.retrofit)
    implementation(libs.retrofitConverterGson)
    implementation(libs.glide)
    kapt(libs.glideCompiler)
    implementation(libs.dexter)
    implementation(libs.shimmer)
    implementation(platform(libs.firebaseBom))
    implementation(libs.firebaseStorageKtx)
    implementation(libs.firebaseFirestoreKtx)
    implementation(libs.firebaseAuthKtx)
    implementation(libs.firebaseCore)
    implementation(libs.playServicesAuth)
    implementation(libs.calendarView)
    implementation(libs.sdpAndroid)
    implementation(libs.sspAndroid)
}

kapt {
    correctErrorTypes = true
}
