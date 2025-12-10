plugins {
    kotlin("jvm") version "2.2.20"
    id("com.gradleup.shadow") version "9.1.0"
    id("io.ktor.plugin") version "3.0.3"
    id("org.jetbrains.kotlin.plugin.serialization") version "2.1.10"
}

application {
    mainClass.set("net.serverwars.sunsetPlugin.MainKt")
}

group = "net.serverwars"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/") {
        name = "papermc-repo"
    }
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.8-R0.1-SNAPSHOT")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    // Ktor HTTP Client (CIO engine for async network requests)
    implementation("io.ktor:ktor-client-core")
    implementation("io.ktor:ktor-client-cio")
    implementation("io.ktor:ktor-serialization-kotlinx-json")
    implementation("io.ktor:ktor-client-content-negotiation")

    // Time stuff
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.7.1")

    // Kyori Adventure minimessages
    implementation("net.kyori:adventure-text-minimessage:4.24.0")
}

val targetJavaVersion = 21
kotlin {
    jvmToolchain(targetJavaVersion)
}

// Fill property files with environment variables
tasks.processResources {
    filesMatching("**/*.properties") {
        expand(System.getenv())
    }
}

tasks.shadowJar {
    archiveFileName.set("sunset-plugin-${project.version}.jar")
    destinationDirectory = file("${System.getenv("BUILD_DESTINATION_DIRECTORY")}")
}

tasks.build {
    dependsOn("shadowJar")
}


tasks.processResources {
    val props = mapOf("version" to version)
    inputs.properties(props)
    filteringCharset = "UTF-8"
    filesMatching("paper-plugin.yml") {
        expand(props)
    }
}
