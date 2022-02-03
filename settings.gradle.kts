rootProject.name = "saga-demo"
pluginManagement {
    repositories {
        maven(url = "https://nexus-new.tcsbank.ru/repository/gradle-plugins")
        maven(url = "https://nexus-new.tcsbank.ru/repository/java-commons-group")
    }

    plugins {
        val kotlinVersion: String by settings

        kotlin("jvm") apply false version "1.6.10"
    }
}

include(":booking-service")
include(":payment-service")

