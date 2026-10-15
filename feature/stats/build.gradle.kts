plugins {
    id("chitieu.android.library")
    id("chitieu.android.library.compose")
    id("chitieu.android.hilt")
}

android {
    namespace = "hung.lct.chitieu.feature.stats"
}

dependencies {
    implementation(project(":core:model"))
    implementation(project(":core:designsystem"))
    implementation(project(":core:ui"))
}