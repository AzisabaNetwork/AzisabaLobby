plugins {
    `java-library`
    `maven-publish`
    id("xyz.jpenilla.run-paper") version "3.0.2"
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
    compileOnly(libs.com.destroytokyo.paper.api)
    compileOnly(libs.org.jetbrains.annotations)
    compileOnly(libs.org.projectlombok.lombok)
    compileOnly(libs.org.spigotmc.spigot)
    compileOnly(libs.net.azisaba.azisabaachievements.api)
    annotationProcessor(libs.org.projectlombok.lombok)
}

group = "net.azisaba"
version = "1.3.0"
description = "AzisabaLobby"
java.sourceCompatibility = JavaVersion.VERSION_1_8

publishing {
    publications.create<MavenPublication>("maven") {
        from(components["java"])
    }
}

tasks {
    runServer {
        minecraftVersion("1.12.2")
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
