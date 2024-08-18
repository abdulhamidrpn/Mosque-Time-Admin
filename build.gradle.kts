plugins {
    id("com.android.application") version libs.versions.agp.get() apply false
    id("com.android.library") version libs.versions.agpLibrary.get() apply false
    id("org.jetbrains.kotlin.android") version libs.versions.kotlin.get() apply false
    id("com.google.gms.google-services") version libs.versions.googleServices.get() apply false
}

tasks.register<Delete>("clean") {
    delete(rootProject.buildDir)
}
