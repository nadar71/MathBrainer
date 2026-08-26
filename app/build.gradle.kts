import java.util.Properties
import javax.xml.parsers.DocumentBuilderFactory

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.hilt.android)
}

val googleTestAdMobAppId = "ca-app-pub-3940256099942544~3347511713"
val googleTestBannerAdId = "ca-app-pub-3940256099942544/6300978111"
val googleTestDeviceId = "33BE2250B43518CCDA7DE426D04EE231"

val localReleaseProperties = Properties().apply {
    val propertiesFile = rootProject.file("keystore.properties")
    if (propertiesFile.isFile) {
        propertiesFile.inputStream().use(::load)
    }
}

fun releaseProperty(name: String): String =
    providers.gradleProperty(name).orNull
        ?: providers.environmentVariable(name).orNull
        ?: localReleaseProperties.getProperty(name).orEmpty()

val releaseAdMobAppId = releaseProperty("ADMOB_APP_ID")
val releaseBannerAdId = releaseProperty("ADMOB_BANNER_ID")
val releaseKeystoreFile = releaseProperty("KEYSTORE_FILE")
val releaseKeystorePassword = releaseProperty("KEYSTORE_PASSWORD")
val releaseKeyAlias = releaseProperty("KEY_ALIAS")
val releaseKeyPassword = releaseProperty("KEY_PASSWORD")

android {
    namespace = "eu.indiewalkabout.mathbrainer"
    compileSdk = 36

    defaultConfig {
        applicationId = "eu.indiewalkabout.mathbrainer"
        minSdk = 26
        targetSdk = 36
        versionCode = 13
        versionName = "3.0.2"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("release") {
            if (releaseKeystoreFile.isNotBlank()) {
                storeFile = rootProject.file(releaseKeystoreFile)
            }
            storePassword = releaseKeystorePassword
            keyAlias = releaseKeyAlias
            keyPassword = releaseKeyPassword
        }
    }

    buildTypes {
        getByName("debug") {
            resValue("string", "admob_app_id", googleTestAdMobAppId)
            resValue("string", "admob_key_app_id", googleTestAdMobAppId)
            buildConfigField("String", "ADMOB_BANNER_ID", "\"$googleTestBannerAdId\"")
            buildConfigField("String", "ADMOB_TEST_DEVICE_ID", "\"$googleTestDeviceId\"")
        }

        getByName("release") {
            resValue("string", "admob_app_id", releaseAdMobAppId)
            resValue("string", "admob_key_app_id", releaseAdMobAppId)
            buildConfigField("String", "ADMOB_BANNER_ID", "\"$releaseBannerAdId\"")
            buildConfigField("String", "ADMOB_TEST_DEVICE_ID", "\"\"")
            signingConfig = signingConfigs.getByName("release")
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
        buildConfig = true
    }
}

fun requireValidReleaseConfiguration() {
    val requiredProperties = mapOf(
        "ADMOB_APP_ID" to releaseAdMobAppId,
        "ADMOB_BANNER_ID" to releaseBannerAdId,
        "KEYSTORE_FILE" to releaseKeystoreFile,
        "KEYSTORE_PASSWORD" to releaseKeystorePassword,
        "KEY_ALIAS" to releaseKeyAlias,
        "KEY_PASSWORD" to releaseKeyPassword
    )
    val missingProperties = requiredProperties.filterValues { it.isBlank() }.keys
    val sampleAdMobProperties = mapOf(
        "ADMOB_APP_ID" to releaseAdMobAppId,
        "ADMOB_BANNER_ID" to releaseBannerAdId
    ).filterValues { it == googleTestAdMobAppId || it == googleTestBannerAdId }.keys
    val missingKeystore = releaseKeystoreFile.isNotBlank() && !rootProject.file(releaseKeystoreFile).isFile

    check(missingProperties.isEmpty() && sampleAdMobProperties.isEmpty() && !missingKeystore) {
        buildString {
            append("Release configuration is invalid.")
            if (missingProperties.isNotEmpty()) {
                append(" Missing: ${missingProperties.joinToString()}.")
            }
            if (sampleAdMobProperties.isNotEmpty()) {
                append(" Google sample IDs are not allowed: ${sampleAdMobProperties.joinToString()}.")
            }
            if (missingKeystore) {
                append(" KEYSTORE_FILE does not exist: $releaseKeystoreFile.")
            }
            append(" Supply values through -P properties, environment variables, or untracked keystore.properties.")
        }
    }
}

fun isReleasePackagingTask(taskName: String): Boolean =
    taskName == "assembleRelease" ||
        taskName == "bundleRelease" ||
        taskName.startsWith("packageRelease") ||
        taskName.startsWith("signRelease")

val validateReleaseConfiguration = tasks.register("validateReleaseConfiguration") {
    group = "verification"
    description = "Checks that release-only AdMob and signing inputs are present and not Google sample IDs."
    doLast { requireValidReleaseConfiguration() }
}

val requestedReleasePackaging = gradle.startParameter.taskNames
    .map { it.substringAfterLast(':') }
    .any { taskName ->
        isReleasePackagingTask(taskName) || taskName == "assemble" || taskName == "bundle"
    }

if (requestedReleasePackaging) {
    requireValidReleaseConfiguration()
}

tasks.configureEach {
    if (isReleasePackagingTask(name)) {
        dependsOn(validateReleaseConfiguration)
    }
}

val verifyReleaseManifest = tasks.register("verifyReleaseManifest") {
    group = "verification"
    description = "Verifies the merged release manifest has one variant-owned AdMob application ID."
    dependsOn("processReleaseMainManifest")

    val mergedManifest = layout.buildDirectory.file(
        "intermediates/merged_manifest/release/processReleaseMainManifest/AndroidManifest.xml"
    )
    inputs.file(mergedManifest)

    doLast {
        val document = DocumentBuilderFactory.newInstance()
            .newDocumentBuilder()
            .parse(mergedManifest.get().asFile)
        val metaData = document.getElementsByTagName("meta-data")
        val applicationIdEntries = (0 until metaData.length).mapNotNull { index ->
            metaData.item(index).attributes.getNamedItem("android:name")
                ?.takeIf { it.nodeValue == "com.google.android.gms.ads.APPLICATION_ID" }
                ?.let { metaData.item(index) }
        }

        check(applicationIdEntries.size == 1) {
            "Expected exactly one com.google.android.gms.ads.APPLICATION_ID entry in the merged release manifest, found ${applicationIdEntries.size}."
        }
        check(applicationIdEntries.single().attributes.getNamedItem("android:value")?.nodeValue == "@string/admob_app_id") {
            "The AdMob application ID must use @string/admob_app_id."
        }

        val appNamespace = "eu.indiewalkabout.mathbrainer"
        val exportedAppComponents = listOf("activity", "activity-alias", "service", "receiver")
            .flatMap { componentType ->
                val components = document.getElementsByTagName(componentType)
                (0 until components.length).mapNotNull { index ->
                    val component = components.item(index)
                    val componentName = component.attributes.getNamedItem("android:name")?.nodeValue
                        ?.let { name -> if (name.startsWith('.')) "$appNamespace$name" else name }
                    componentName?.takeIf {
                        component.attributes.getNamedItem("android:exported")?.nodeValue == "true" &&
                            it.startsWith(appNamespace)
                    }
                }
            }
        check(exportedAppComponents == listOf(
            "eu.indiewalkabout.mathbrainer.feat_home.presentation.ui.HomeGameActivity"
        )
        ) {
            "Only HomeGameActivity may be exported from the app namespace; found: $exportedAppComponents."
        }
    }
}

tasks.named("check") {
    dependsOn(verifyReleaseManifest)
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.savedstate)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.runtime)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.foundation)
    implementation(libs.foundation.layout)
    implementation(libs.androidx.material)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.navigation3.ui)
    implementation(libs.androidx.constraintlayout.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.animation.core.android)
    implementation(libs.androidx.foundation.android)

    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.serialization.json)

    implementation(libs.hilt.android)
    implementation(libs.androidx.hilt.common)
    implementation(libs.androidx.hilt.navigation.compose)
    kapt(libs.hilt.android.compiler)

    implementation(libs.accompanist.pager)
    implementation(libs.accompanist.permissions)

    coreLibraryDesugaring(libs.desugar.jdk.libs)

    implementation(libs.gson)

    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.room.ktx)
    testImplementation(libs.androidx.room.testing)

    implementation(libs.coil.compose)

    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.okhttp)
    implementation(libs.logging.interceptor)
    implementation(libs.okhttp.urlconnection)

    implementation(libs.multidex)
    implementation(libs.playservices.ads)
    implementation(libs.user.messaging.platform)

    implementation(libs.androidx.preference.ktx)

    implementation(libs.kotpref)
    implementation(libs.initializer)
    implementation(libs.enum.support)
    implementation(libs.gson.support)
    implementation(libs.livedata.support)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.preference.screen.dsl)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)

    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}
