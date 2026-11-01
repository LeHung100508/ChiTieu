plugins {
    id("chitieu.jvm.library")
    alias(libs.plugins.kotlin.serialization)
}

sourceSets {
    main {
        resources {
            srcDirs(rootProject.file("docs/shared/parser"))
            include("category_keywords.json")
        }
    }
    test {
        resources {
            srcDirs(rootProject.file("docs/shared/parser"))
        }
    }
}

dependencies {
    implementation(libs.serialization.json)

    testImplementation(libs.junit)
    testImplementation(libs.serialization.json)
}
