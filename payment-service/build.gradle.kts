import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
buildscript {
	repositories {
		mavenLocal()
		maven(url = "https://registry.tcsbank.ru/repository/gradle-plugins/")
	}
}
plugins {
	id("org.springframework.boot") version "2.6.3"
	id("io.spring.dependency-management") version "1.0.11.RELEASE"
	kotlin("jvm")
	kotlin("plugin.spring") version "1.6.10"
	id("org.jetbrains.kotlin.plugin.jpa") version "1.6.10"
}

group = "io.github.sirvaulterscoff"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
	mavenLocal()
	maven(url = "https://nexus-new.tcsbank.ru/repository/mvn-maven-proxy/")
	maven(url = "https://nexus-new.tcsbank.ru/repository/accounting-group/")
	maven(url = "https://nexus-new.tcsbank.ru/repository/java-commons-group/")
	maven(url = "https://nexus-new.tcsbank.ru/repository/mvn-thirdparty/")
	maven(url = "https://nexus-new.tcsbank.ru/repository/jcenter/")
	maven(url = "https://nexus-new.tcsbank.ru/repository/mvn-springio-plugins-release/")
	maven(url = "https://nexus-new.tcsbank.ru/repository/mvn-compliance-commons/")
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	runtimeOnly("org.postgresql:postgresql")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "17"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}
