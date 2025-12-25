plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.webmasterdotnetvn.quanlychitieu"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.webmasterdotnetvn.quanlychitieu"
        minSdk = 24
        targetSdk = 36
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
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    // Để dùng các component xịn như BottomAppBar, TabLayout, MaterialCardView...
    implementation("com.google.android.material:material:1.12.0")
// (Dùng bản mới nhất cũng được)
// Thư viện vẽ biểu đồ
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
    implementation(platform("com.google.firebase:firebase-bom:32.7.0"))

    // Các thư viện Firebase (Không cần ghi version vì đã có BoM quản lý)
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-auth")      // Đăng nhập
    implementation("com.google.firebase:firebase-firestore") // Cơ sở dữ liệu
}