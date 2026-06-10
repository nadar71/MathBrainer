// Top-level build file where you can add configuration options common to all sub-projects/modules.
// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.hilt.android) apply false
}

/*buildscript {
    ext{
        kotlin_vers            = '1.8.0'
        appcompat_vers         = '1.6.1'
        constraintlayout_vers  = '2.1.4'
        play_services_ads_vers = '22.4.0'
        consent_library_vers   = '1.0.8'
        lifecycle_vers         = '2.6.2'
        lifecycle_ext_vers     = '2.2.0'
        room_vers              = '2.5.2'
    }

    repositories {
        mavenCentral()
        google()
        jcenter()
    }
    dependencies {
        classpath 'com.android.tools.build:gradle:8.1.1'
        classpath "org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_vers"
    }
}

allprojects {
    repositories {
        mavenCentral()
        google()
        jcenter()
        maven{url 'https://jitpack.io'}
    }
}

tasks.register('clean', Delete) {
    delete rootProject.buildDir
}*/


