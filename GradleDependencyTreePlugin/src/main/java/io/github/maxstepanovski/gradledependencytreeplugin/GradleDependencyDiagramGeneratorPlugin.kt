package io.github.maxstepanovski.gradledependencytreeplugin

import org.gradle.api.Plugin
import org.gradle.api.Project

class GradleDependencyDiagramGeneratorPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.tasks.register("generateGradleDependencyReport", DependencyReportGenerator::class.java)
    }
}