import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    `java-library`
    `maven-publish`
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "org.valkyrienskies"
version = "3.4"

repositories {
    mavenCentral()
    maven {
        name = "VS Maven"
        url = uri(project.findProperty("vs_maven_url") ?: "https://maven.valkyrienskies.org/")

        val vsMavenUsername = project.findProperty("vs_maven_username") as String?
        val vsMavenPassword = project.findProperty("vs_maven_password") as String?

        if (vsMavenPassword != null && vsMavenUsername != null) {
            credentials {
                username = vsMavenUsername
                password = vsMavenPassword
            }
        }
    }
}

val shade by configurations.creating
configurations.implementation.get().extendsFrom(shade)

dependencies {
    shade("com.github.zafarkhaja:java-semver:0.9.0")

    implementation("com.google.code.findbugs:jsr305:3.0.2")
    implementation("com.google.code.gson:gson:2.8.0") // Minecraft has GSON already
    implementation("com.electronwill.night-config:toml:3.6.0") // included in Forge
    implementation("org.apache.commons:commons-lang3:3.7") // included in Minecraft
}

tasks.named<ShadowJar>("shadowJar") {
    configurations = listOf(shade)
    minimize()
}

publishing {
    repositories {
        val ghpUser = (project.findProperty("gpr.user") ?: System.getenv("USERNAME")) as String?
        val ghpPassword = (project.findProperty("gpr.key") ?: System.getenv("TOKEN")) as String?
        // Publish to Github Packages
        if (ghpUser != null && ghpPassword != null) {
            println("Publishing to GitHub Packages ($version)")
            maven {
                name = "GithubPackages"
                url = uri("https://maven.pkg.github.com/ValkyrienSkies/valkyrien-dependency-downloader")
                credentials {
                    username = ghpUser
                    password = ghpPassword
                }
            }
        }

        val vsMavenUsername = project.findProperty("vs_maven_username") as String?
        val vsMavenPassword = project.findProperty("vs_maven_password") as String?
        val vsMavenUrl = project.findProperty("vs_maven_url") as String?
        if (vsMavenUrl != null && vsMavenPassword != null && vsMavenUsername != null) {
            println("Publishing to VS Maven ($version)")
            maven {
                url = uri(vsMavenUrl)
                credentials {
                    username = vsMavenUsername
                    password = vsMavenPassword
                }
            }
        }
    }
    publications {
        create<MavenPublication>("library") {
            from(components["java"])
        }
    }
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}