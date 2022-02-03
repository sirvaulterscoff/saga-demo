rootProject.name = "booking-service"
pluginManagement {
    repositories {
        maven(url = "https://nexus-new.tcsbank.ru/repository/gradle-plugins")
        maven(url = "https://nexus-new.tcsbank.ru/repository/java-commons-group")
    }

    plugins {
        kotlin("jvm") apply false version "1.6.10"
    }
}


