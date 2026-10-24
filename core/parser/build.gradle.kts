plugins {
    id("chitieu.jvm.library")
    alias(libs.plugins.kotlin.serialization)
}

sourceSets {
    test {
        resources {
            srcDirs(rootProject.file("docs/shared/parser"))
        }
    }
}

dependencies {
    testImplementation(libs.junit)
    testImplementation(libs.serialization.json)
}
