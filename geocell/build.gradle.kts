plugins {
	kotlin("jvm") version "1.9.25"
	kotlin("plugin.jpa") version "1.9.25"
	kotlin("plugin.spring") version "1.9.25"
	id("org.springframework.boot") version "3.4.3"
	id("io.spring.dependency-management") version "1.1.7"
}

group = "sdato"
version = "1.0.0"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

repositories {
	mavenCentral()
}

dependencies {
	// Spring Boot Starter Web (for webapps)
	implementation("org.springframework.boot:spring-boot-starter-web")

	// Spring Boot Starter Data JPA (for BD data access)
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")

	// Spring Boot Starter Security (for authentication and authnorization)
	implementation("org.springframework.boot:spring-boot-starter-security")

	// Spring Boot Starer validation (for data validation)
	implementation("org.springframework.boot:spring-boot-starter-validation")

	// Jackson Module Kotlin (for serialization/deserialization JSON)
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

	// Kotlin reflection API
	implementation("org.jetbrains.kotlin:kotlin-reflect")

	// Spring Boot Session Management (for session managment)
	implementation("org.springframework.session:spring-session-core")

	// PostgreSQL (for production)
	runtimeOnly("org.postgresql:postgresql")

	// Spring Boot Starter Test
	testImplementation("org.springframework.boot:spring-boot-starter-test")

	// Spring Security Test
	testImplementation("org.springframework.security:spring-security-test")

	// Kotlin Test
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")

	// Production Test
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin {
	compilerOptions {
		freeCompilerArgs.addAll("-Xjsr305=strict")
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}
