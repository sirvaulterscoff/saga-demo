buildscript {
    repositories {
        mavenLocal()
        maven(url = "https://registry.tcsbank.ru/repository/gradle-plugins/")
    }
}

plugins {
    kotlin("jvm")
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
}