plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt.android)
}

android {
    namespace = "eu.indiewalkabout.mathbrainer"
    compileSdk = 35


    defaultConfig {
        applicationId = "eu.indiewalkabout.mathbrainer"
        minSdk = 26
        targetSdk = 35
        versionCode = 10
        versionName = "2.0.0"

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

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.savedstate)
    implementation(libs.androidx.ui.test.android)

    // compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.runtime)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.foundation)
    implementation(libs.foundation.layout)
    implementation(libs.androidx.material)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material3.android)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.constraintlayout.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.animation.core.android)
    implementation(libs.androidx.foundation.android)

    // Coroutines
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)

    // hilt
    implementation(libs.hilt.android)
    implementation(libs.androidx.hilt.common)
    implementation(libs.androidx.hilt.navigation.compose)
    ksp(libs.hilt.android.compiler)

    // Accompanist lib for compose integration
    implementation(libs.accompanist.pager)
    implementation(libs.accompanist.permissions)

    // Core library desugaring
    coreLibraryDesugaring(libs.desugar.jdk.libs)

    // gson
    implementation(libs.gson)

    // Room
    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.room.ktx)
    testImplementation(libs.androidx.room.testing)

    // coil
    implementation(libs.coil.compose)
    implementation(libs.coil.compose.v210)

    // Retrofit
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.okhttp)
    implementation(libs.logging.interceptor)
    implementation(libs.okhttp.urlconnection)

    // Multidex
    implementation(libs.multidex)

    // WorkManager
    implementation(libs.androidx.work.runtime.ktx)

    // Ad mob
    implementation(libs.playservices.ads)
    implementation(libs.user.messaging.platform)

    // Unity
    implementation(libs.unity.ads)

    // Preference
    implementation(libs.androidx.preference.ktx)

    // Kotpref SharePreferences lib: https://github.com/chibatching/Kotpref
    implementation(libs.kotpref)
    implementation(libs.initializer)
    implementation(libs.enum.support)
    implementation(libs.gson.support)
    implementation(libs.livedata.support)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx.v251)
    implementation(libs.preference.screen.dsl)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)

    // testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

}


/*apply plugin: 'com.android.application'
apply plugin: 'kotlin-android'
apply plugin: 'kotlin-kapt'
apply plugin: 'kotlin-parcelize'



configurations {
    ktlint
}

android {
    compileSdkVersion 34
    defaultConfig {
        applicationId "eu.indiewalkabout.mathbrainer"
        minSdkVersion 19
        targetSdkVersion 34
        versionCode 3
        versionName "2.0.0"
        testInstrumentationRunner "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables.useSupportLibrary = true
        // signingConfig signingConfigs.release
        multiDexEnabled true
    }

    buildTypes {
        release {
            zipAlignEnabled true
            jniDebuggable false
            renderscriptDebuggable false
            minifyEnabled false
            proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.pro'
            ndk {
                debugSymbolLevel 'FULL'
            }
        }
        debug {
            debuggable true
            minifyEnabled false
        }
    }

    flavorDimensions "MathBrainer"
    productFlavors {
        production {
            dimension "MathBrainer"
            resValue "string", "app_name", "MathBrainer"
        }

        myTesting {
            dimension "MathBrainer"
            resValue "string", "app_name", "MathBrainer Test"
            applicationIdSuffix ".testing"
        }
    }

    testOptions {
        unitTests {
            includeAndroidResources = true
            unitTests.returnDefaultValues = true
        }
    }

    compileOptions {
        sourceCompatibility JavaVersion.VERSION_17
        targetCompatibility JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17
    }

    lintOptions {
        abortOnError false
    }

    dataBinding {
        enabled true
    }

    viewBinding {
        enabled true
    }
    namespace 'eu.indiewalkabout.mathbrainer'


}


dependencies {
    implementation fileTree(dir: 'libs', include: ['*.jar'])
    implementation "androidx.appcompat:appcompat:$appcompat_vers"
    implementation "androidx.constraintlayout:constraintlayout:$constraintlayout_vers"

    implementation "com.google.android.gms:play-services-ads:$play_services_ads_vers"

    // GDPR
    implementation "com.google.android.ads.consent:consent-library:$consent_library_vers"

    // https://ktlint.github.io/#getting-started
    ktlint 'com.pinterest:ktlint:0.39.0'

    // LiveData dependencies
    implementation "androidx.lifecycle:lifecycle-runtime-ktx:$lifecycle_vers"
    implementation "androidx.lifecycle:lifecycle-livedata-ktx:$lifecycle_vers"
    implementation "androidx.lifecycle:lifecycle-extensions:$lifecycle_ext_vers"

    // Room dependencies
    implementation "androidx.room:room-runtime:$room_vers"
    kapt "androidx.room:room-compiler:$room_vers"
    implementation "androidx.room:room-ktx:$room_vers"
        testImplementation "androidx.room:room-testing:$room_vers"

    // Unity
    implementation 'com.unity3d.ads:unity-ads:4.7.0'

    // kotlin
    implementation "org.jetbrains.kotlin:kotlin-stdlib-jdk7:$kotlin_vers"


    // Testing code should not be included in the main code.
    // Once https://issuetracker.google.com/128612536 is fixed this can be fixed.
    implementation "androidx.test:core:1.5.0"

    //  AndroidX Test - local unit tests - JVM testing
    testImplementation "junit:junit:4.13.2"
    testImplementation "org.hamcrest:hamcrest-all:1.3"
    testImplementation "androidx.arch.core:core-testing:2.2.0"
    testImplementation 'androidx.test:runner:1.5.2'
    testImplementation "androidx.test:core-ktx:1.5.0"
    testImplementation "androidx.test.ext:junit:1.1.5"
    testImplementation "org.robolectric:robolectric:4.3.1"
    testImplementation 'org.mockito:mockito-core:2.25.0'
    testImplementation 'org.mockito:mockito-inline:2.13.0'
    testImplementation "io.mockk:mockk:1.9.3"


    // AndroidX Test - Instrumented testing
    androidTestImplementation "junit:junit:4.13.2"
    androidTestImplementation 'androidx.test:runner:1.5.2'
    androidTestImplementation "androidx.test.ext:junit:1.1.5"
    androidTestImplementation "androidx.arch.core:core-testing:2.2.0"
    androidTestImplementation "androidx.test:rules:1.5.0"
    androidTestImplementation "androidx.test.espresso:espresso-core:3.6.0-alpha01"
    androidTestImplementation "androidx.test.espresso:espresso-intents:3.6.0-alpha01"
    androidTestImplementation "androidx.test.espresso:espresso-contrib:3.6.0-alpha01"


}*/



