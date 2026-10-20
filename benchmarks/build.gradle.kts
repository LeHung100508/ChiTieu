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

// Khai báo phụ thuộc rõ ràng để thỏa mãn Task Validation của Gradle
tasks.matching { it.name.startsWith("kover") }.configureEach {
    dependsOn(tasks.matching { it.name.startsWith("compile") })
}
