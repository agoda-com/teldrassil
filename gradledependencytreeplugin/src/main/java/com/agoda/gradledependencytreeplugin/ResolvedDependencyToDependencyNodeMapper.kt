package com.agoda.gradledependencytreeplugin

import org.gradle.api.artifacts.ResolvedDependency

object DependencyNodeMapper {
    fun ResolvedDependency.mapToDependencyNode(): DependencyNode {
        return DependencyNode(this.name, this.moduleName)
    }
}