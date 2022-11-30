
plugins {
    id("java")
    id("java-gradle-plugin")
    kotlin("jvm") version "1.6.10"
    id("com.gradle.plugin-publish") version "1.1.0"
}

group = "com.agoda.maxstepanovski"
version = "0.0.1"

gradlePlugin {
    plugins {
        create("GradleDependencyDiagramGeneratorPlugin") {
            id = "com.agoda.maxstepanovski.gradledependencytreeplugin"
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

publishing {

    repositories {
        maven {
            val releaseUrl = "https://nexus.agodadev.io/repository/maven-releases/"
            val snapshotUrl = "https://nexus.agodadev.io/repository/maven-snapshots/"
            name = "gradle-dependency-diagram-generator-plugin"
            isAllowInsecureProtocol = true
            url = uri(if(version.toString().endsWith("SNAPSHOT")) snapshotUrl else releaseUrl)
            credentials {
                username = System.getenv("MAVEN_USER")
                password = System.getenv("MAVEN_PASSWORD")
            }
        }
    }
}

repositories {
    mavenCentral()
    maven {
        val releaseUrl = "https://nexus.agodadev.io/repository/maven-releases/"
        val snapshotUrl = "https://nexus.agodadev.io/repository/maven-snapshots/"
        isAllowInsecureProtocol = true
        url = uri(if(version.toString().endsWith("SNAPSHOT")) snapshotUrl else releaseUrl)
        credentials {
            username = System.getenv("MAVEN_USER")
            password = System.getenv("MAVEN_PASSWORD")
        }
    }
}

dependencies {
    implementation("com.agoda.maxstepanovski:graph-contract:0.0.5")
    implementation(project(":graph-contract"))
    implementation("com.google.code.gson:gson:2.7")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}