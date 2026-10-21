buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:8.2.2") // Assicurati che la versione corrisponda alla tua
        classpath("com.google.gms:google-services:4.4.0") // Assicurati che la versione corrisponda alla tua
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:2.7.5") // Assicurati che la versione corrisponda alla tua
    }
}

plugins {
    id("com.android.application") version "8.2.2" apply false // Assicurati che la versione corrisponda alla tua
    id("com.google.gms.google-services") version "4.4.0" apply false // Assicurati che la versione corrisponda alla tua
}