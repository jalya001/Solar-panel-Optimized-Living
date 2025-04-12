plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
//    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.22" // Update this line

    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "no.solcellepaneller"
    compileSdk = 35

    defaultConfig {
        applicationId = "no.solcellepaneller"
        minSdk = 26
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

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.15"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.navigation.runtime.android)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.protolite.well.known.types)
    implementation(libs.androidx.runtime.livedata)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    //Project dependencies
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.ktor.client.cio)
    implementation(libs.ktor.ktor.client.okhttp)
    implementation(libs.ktor.client.content.negotiation.v236)
    implementation(libs.ktor.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.ktor.client.content.negotiation)
//    implementation(libs.kotlinx.serialization.json)
    implementation(libs.ui)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
//    implementation(libs.androidx.activity.compose.v140)
    implementation(libs.kotlin.stdlib)
    //Project independencies

    implementation ("io.ktor:ktor-client-core:2.3.6")
    implementation("io.ktor:ktor-client-cio:2.3.6")
    implementation("io.ktor:ktor-client-okhttp:2.3.6")
    implementation("io.ktor:ktor-client-content-negotiation:2.3.6")
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.6")




    implementation ("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")
    implementation ("androidx.navigation:navigation-compose:2.8.9")

    //mapdependencies
    implementation("com.google.maps.android:maps-compose:4.2.0")
    implementation("com.google.android.gms:play-services-maps:18.2.0")
    implementation ("com.google.maps.android:android-maps-utils:2.2.0")

    //Chart dependencies
    implementation("co.yml:ycharts:2.1.0")

    //Icons
    implementation("androidx.compose.material:material-icons-extended-android:1.7.8")
}