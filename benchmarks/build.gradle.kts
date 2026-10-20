plugins {
    id("com.android.test")
}

android {
    namespace = "hung.lct.chitieu.benchmarks"
    compileSdk = 37

    defaultConfig {
        minSdk = 26
        targetSdk = 37
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    targetProjectPath = ":app"
}
