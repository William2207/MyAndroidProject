plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.myproject"
    compileSdk = 35
buildFeatures{
    viewBinding = true
}
    defaultConfig {
        applicationId = "com.example.myproject"
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
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation ("de.hdodenhof:circleimageview:3.1.0")
    // Retrofit
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    // Gson converter để parse JSON
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
    // OkHttp logging (tùy chọn, để debug request/response)
    implementation ("com.squareup.okhttp3:logging-interceptor:4.9.0")

    //glide xu ly anh
    implementation ("com.github.bumptech.glide:glide:4.14.2")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.14.2")

    implementation ("com.squareup.retrofit2:converter-scalars:2.9.0")

    implementation ("com.google.android.material:material:1.11.0")

    // base widgets
    implementation ("com.hendraanggrian.appcompat:socialview:0.1")

    // auto-complete widgets
    implementation ("com.hendraanggrian.appcompat:socialview-autocomplete:0.1")

    //file picker
    implementation ("androidx.activity:activity:1.7.0")

    //swipe fresh layout
    implementation ("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")

    //Exoplay
    implementation("androidx.media3:media3-exoplayer:1.6.1")
    implementation("androidx.media3:media3-exoplayer-dash:1.6.1")
    implementation("androidx.media3:media3-ui:1.6.1")
    implementation("androidx.media3:media3-ui-compose:1.6.1")

}