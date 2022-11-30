pluginManagement {
    repositories {
        maven {
            isAllowInsecureProtocol = true
            url = uri("http://localhost:3000/repository/maven-releases/")
            credentials {
                username = System.getenv("MAVEN_USER")
                password = System.getenv("MAVEN_PASSWORD")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

rootProject.name = "teldrassil"
include("graph-contract", "gradledependencytreeplugin")
