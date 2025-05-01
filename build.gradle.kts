// root build.gradle.kts

import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.google.gms:google-services:4.3.15")
    }
}

plugins {
    id("com.android.application")    version "8.9.0" apply false
    id("com.android.library")        version "8.9.0" apply false

    // ↑ Kotlin plugins bumped to 1.9.0 ↑
    id("org.jetbrains.kotlin.android") version "1.9.0" apply false
    id("org.jetbrains.kotlin.kapt")    version "1.9.0" apply false
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
        // now that we’re on Kotlin 1.9, JVM 21 is valid:
        jvmTarget = "21"

        // export the internal Javac APIs so KAPT will run under JDK 21+
        freeCompilerArgs += listOf(
            "-J--add-exports=jdk.compiler/com.sun.tools.javac.main=ALL-UNNAMED",
            "-J--add-exports=jdk.compiler/com.sun.tools.javac.api=ALL-UNNAMED"
        )
    }
}
