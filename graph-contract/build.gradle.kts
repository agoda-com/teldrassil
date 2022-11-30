import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("java")
    kotlin("jvm") version "1.6.10"
    id("maven-publish")
}

group = "com.agoda.maxstepanovski"
version = "0.0.1"

repositories {
    mavenCentral()
}

publishing {

    publications {
        create<MavenPublication>("maven") {
            artifactId = project.name
            version = project.version.toString()
            from(components["java"])
        }
    }

    repositories {
        maven {
            val releaseUrl = "https://nexus.agodadev.io/repository/maven-releases/"
            val snapshotUrl = "https://nexus.agodadev.io/repository/maven-snapshots/"
            name = "teldrassil-graph-contract"
            url = uri(if(version.toString().endsWith("SNAPSHOT")) snapshotUrl else releaseUrl)
            credentials {
                username = System.getenv("MAVEN_USER")
                password = System.getenv("MAVEN_PASSWORD")
            }
        }
    }
}

dependencies {
    implementation("com.google.code.gson:gson:2.7")
    implementation(kotlin("stdlib-jdk8"))
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}
val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = "1.8"
}
val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
    jvmTarget = "1.8"
}