plugins {
    `java-library`
    `maven-publish`
    id("xyz.jpenilla.run-paper") version "3.0.2"
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.21"
}

repositories {
    mavenCentral()
    mavenLocal()
    maven {
        name = "papermc"
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
    maven {
        url = uri("https://repo.azisaba.net/repository/maven-public/")
    }
    maven {
        url = uri("https://repo.acrylicstyle.xyz/repository/maven-public/")
    }
}

dependencies {
    paperweight.paperDevBundle("1.21.11-R0.1-SNAPSHOT")
    compileOnly(libs.org.jetbrains.annotations)
    compileOnly(libs.org.projectlombok.lombok)
    compileOnly(libs.net.azisaba.azisabaachievements.api)
    annotationProcessor(libs.org.projectlombok.lombok)
}

group = "net.azisaba"
version = "2.0.0"
description = "AzisabaLobby"
java.sourceCompatibility = JavaVersion.VERSION_25

publishing {
    publications.create<MavenPublication>("maven") {
        from(components["java"])
    }
}

tasks {
    runServer {
        minecraftVersion("1.21.11")
    }
}

tasks.processResources {
    val props = mapOf(
        "name" to project.name,
        "version" to project.version,
        "description" to project.description,
    )
    inputs.properties(props)
    filteringCharset = "UTF-8"
    filesMatching("plugin.yml") {
        expand(props)
    }
}

tasks.withType<JavaCompile>() {
    options.encoding = "UTF-8"
}

tasks.withType<Javadoc>() {
    options.encoding = "UTF-8"
}
