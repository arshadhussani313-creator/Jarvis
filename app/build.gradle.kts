plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.jarvis.ai"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.jarvis.ai"
        minSdk = 29
        targetSdk = 34
        versionCode = 1
     buildConfigField("String","GEMINI_API_KEY","\"AQ.Ab8RN6J-Mf7CUpg2iFqB-54KUsw_Tw8utokqipngzLCA10hlcA\"")
versionName = "0.2.0-stage2" 
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            isMinifyEnabled = false
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
        compose = true
'buildConfig = true'
    }

    packaging {
        jniLibs {
            pickFirsts.add("lib/arm64-v8a/libandroidx.graphics.path.so")
            pickFirsts.add("lib/armeabi-v7a/libandroidx.graphics.path.so")
        }
    }
}

dependencies {
implementation("com.google.ai.client.generativeai:generativeai:0.9.0")

   implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    debugImplementation(libs.androidx.ui.tooling)
}
