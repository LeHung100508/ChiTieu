plugins {
    id("chitieu.android.library.compose")
}

android {
    namespace = "hung.lct.chitieu.core.designsystem"
}

dependencies {
    api(libs.androidx.compose.material3)
    api(libs.androidx.compose.material.icons.core)

    testImplementation(libs.junit)
}
