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
    id("org.jetbrains.kotlin.android") version "1.9.20" apply false
    id("com.google.devtools.ksp")    version "1.9.20-1.0.14" apply false
}



tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = "21"
        freeCompilerArgs += listOf(
            "-J--add-exports=jdk.compiler/com.sun.tools.javac.main=ALL-UNNAMED",
            "-J--add-exports=jdk.compiler/com.sun.tools.javac.api=ALL-UNNAMED"
        )
    }
}
