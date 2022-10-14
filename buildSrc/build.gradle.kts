import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
    id("java")
    kotlin("jvm") version "1.6.10"
}

group = "com.github.maxstepanovski.projecttreeplugin"
version = "0.0.1"

repositories {
    gradlePluginPortal()
    mavenCentral()
}

dependencies {
    implementation("com.google.code.gson:gson:2.7")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
    implementation(kotlin("stdlib-jdk8"))
}

gradlePlugin {
    plugins {
        create("gradle-dependency-diagram") {
            id = "com.github.maxstepanovski.projecttreeplugin.GradleDependencyReportGeneratorPlugin"
            implementationClass = "com.github.maxstepanovski.projecttreeplugin.gradledependencyreportplugin"
        }
    }
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
