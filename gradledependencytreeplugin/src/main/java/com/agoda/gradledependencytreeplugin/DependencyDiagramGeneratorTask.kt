package com.agoda.gradledependencytreeplugin

import com.agoda.gradledependencytreeplugin.DependencyNodeMapper.mapToDependencyNode
import com.agoda.gradledependencytreeplugin.cycledetector.CycleDetector
import com.github.maxstepanovski.contract.model.ClassType
import com.github.maxstepanovski.contract.model.EdgeEntity
import com.github.maxstepanovski.contract.model.GraphEntity
import com.github.maxstepanovski.contract.model.NodeEntity
import com.google.gson.Gson
import org.gradle.api.Project
import org.gradle.api.artifacts.ResolvedDependency
import org.gradle.api.tasks.diagnostics.DependencyReportTask

import java.io.File
import java.io.FileWriter
import java.util.*

abstract class DependencyReportGenerator : DependencyReportTask() {

    private val dependencies: MutableSet<DependencyNode> = mutableSetOf()
    private val nodes = mutableMapOf<String, NodeEntity>()
    private val edges = mutableListOf<EdgeEntity>()

    init {
        outputFile = File("dependencies.txt")
    }

    override fun generate(project: Project) {
        super.generate(project)
        val inputConfiguration = configurations?.ifEmpty {
            taskConfigurations
        } ?: taskConfigurations
        inputConfiguration.filter { it.isCanBeResolved }.forEach { config ->
            println("Generating dependency diagram for config: ${config.name}")
            val cycleDetector = CycleDetector()
            config.resolvedConfiguration.firstLevelModuleDependencies.forEach {
                cycleDetector.detectCycle(it.mapToDependencyNode())
                dfs(it)
            }
            val topLevelDependencies =
                dependencies.intersect(config.resolvedConfiguration.firstLevelModuleDependencies.map {
                    it.mapToDependencyNode()
                }.toSet())
            println("Dependencies for configuration ${config.name}")
            println("↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓")
            try {
                serializeDependencies(config.name, topLevelDependencies)
                println("Successfully serialized gradle dependencies to .diagram file")
            } catch (e: java.io.IOException) {
                println("An error occurred.")
                e.printStackTrace()
            }
            println()
        }
    }

    private fun serializeDependencies(config: String, topLevelDependencies: Set<DependencyNode>) {
        val firstDependencies = DependencyNode(project.name, project.displayName)
        nodes[firstDependencies.id] = NodeEntity(
            firstDependencies.id, firstDependencies.name, ClassType.CLASS, emptyList(), emptyList(), 0, 0, ""
        )
        topLevelDependencies.forEach {
            edges.add(EdgeEntity(UUID.randomUUID().toString(), firstDependencies.id, it.id))
            dfsNodes(it)
        }
        try {
            val fileName = "diagrams/${config}.diagram"
            val file = File(project.buildDir, fileName)
            file.parentFile.mkdirs()
            val myWriter = FileWriter(file)
            myWriter.write(Gson().toJson(GraphEntity(firstDependencies.id, nodes, edges)))
            myWriter.close()
            println("Successfully wrote to the diagram file.")
        } catch (e: java.io.IOException) {
            println("An error occurred.")
            e.printStackTrace()
        }
    }

    private fun dfsNodes(node: DependencyNode) {
        if (nodes[node.id] != null) {
            return
        }
        nodes[node.id] = NodeEntity(node.id, node.name, ClassType.CLASS, emptyList(), emptyList(), 0, 0, "")
        node.children.forEach { child ->
            edges.add(EdgeEntity(UUID.randomUUID().toString(), node.id, child.id))
            dfsNodes(child)
        }
    }


    private fun dfs(resolvedDependency: ResolvedDependency) {
        dependencies.add(resolvedDependency.mapToDependencyNode())
        val dependency =
            dependencies.find { it == resolvedDependency.mapToDependencyNode()}!!
        resolvedDependency.children.forEach { child ->
            dependencies.add(child.mapToDependencyNode())
            dependency.children.add(dependencies.find { it == child.mapToDependencyNode()}!!)
            dfs(child)
        }
    }
}

