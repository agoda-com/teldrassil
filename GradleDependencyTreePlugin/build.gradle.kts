
plugins {
    id("java")
    id("java-gradle-plugin")
    kotlin("jvm") version "1.6.10"
    id("com.gradle.plugin-publish") version "1.1.0"
}

group = "io.github.maxstepanovski.projecttreeplugin"
version = "0.0.5"

gradlePlugin {
    plugins {
        create("GradleDependencyDiagramGeneratorPlugin") {
            id = "io.github.maxstepanovski.gradledependencytreeplugin"
            implementationClass = "io.github.maxstepanovski.gradledependencytreeplugin.GradleDependencyDiagramGeneratorPlugin"
            displayName = "Gradle Dependency Tree Visualizer Plugin for Tedrasil"
        }
    }
}

pluginBundle {  // Removed in Gradle 8+
    website = "https://plugins.jetbrains.com/plugin/20022-teldrassil"
    vcsUrl = "https://plugins.jetbrains.com/plugin/20022-teldrassil"
    tags = listOf("dependency", "visualizer", "kotlin", "intelliJ")
    // Individual descriptions for plugins can be set via the java-gradle-plugin, see below.
    description = "Dependency tree generator is a gradle plugin that generates gradle dependency graphs in a format that can be rendered by Teldrail https://plugins.jetbrains.com/plugin/20022-teldrassil"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":graph-contract"))
    implementation("com.google.code.gson:gson:2.7")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}