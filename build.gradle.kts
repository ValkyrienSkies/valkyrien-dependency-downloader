plugins {
    id("java")
}

group = "org.valkyrienskies"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.github.zafarkhaja:java-semver:0.9.0")

    implementation("com.google.code.gson:gson:2.8.0") // Minecraft has GSON already
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}