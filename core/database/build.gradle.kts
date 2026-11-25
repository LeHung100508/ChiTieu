plugins {
    id("chitieu.android.library")
    id("chitieu.android.room")
}

android {
    namespace = "hung.lct.chitieu.core.database"
}

dependencies {
    implementation(project(":core:model"))
    testImplementation(libs.junit)
    implementation(libs.serialization.json)
}
