plugins {
    kotlin("jvm") version "1.9.25"
    kotlin("plugin.jpa") version "1.9.25"
    kotlin("plugin.spring") version "1.9.25"
    id("org.springframework.boot") version "3.4.3"
    id("io.spring.dependency-management") version "1.1.7"
    id("org.jlleitschuh.gradle.ktlint") version "12.2.0"
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

    // Spring Boot Starter Security (for authentication and authorization)
    implementation("org.springframework.boot:spring-boot-starter-security")

    // Spring Boot Starter Validation (for data validation)
    implementation("org.springframework.boot:spring-boot-starter-validation")

    // Jackson Module Kotlin (for serialization/deserialization JSON)
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    // Kotlin reflection API
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    // Spring Boot Session Management (for session management)
    implementation("org.springframework.session:spring-session-core")

    // JTS (Java Topology Suite) for spatial data
    implementation("org.locationtech.jts:jts-core:1.20.0")

    // Hibernate Spatial for PostGIS
    // implementation("org.hibernate:hibernate-spatial:6.6.11.Final")
    implementation("net.postgis:postgis-jdbc:2024.1.0")

    // PostgreSQL (for production)
    runtimeOnly("org.postgresql:postgresql")

    // Spring Boot Starter Test
    testImplementation("org.springframework.boot:spring-boot-starter-test")

    // Spring Security Test
    testImplementation("org.springframework.security:spring-security-test")

    // Kotlin Test
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")

    // In memory test database
    // testImplementation("com.h2database:h2")

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
