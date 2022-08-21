package com.github.maxstepanovski.projecttreeplugin.data.repository

import com.github.maxstepanovski.projecttreeplugin.data.mapper.ClassWrapperToGraphEntityMapper
import com.github.maxstepanovski.projecttreeplugin.data.mapper.GraphEntityToGraphViewMapper
import com.github.maxstepanovski.projecttreeplugin.data.mapper.GraphViewToGraphEntityMapper
import com.github.maxstepanovski.projecttreeplugin.data.model.GraphEntity
import com.github.maxstepanovski.projecttreeplugin.model.ClassWrapper
import com.github.maxstepanovski.projecttreeplugin.ui.DiagramEditorProvider
import com.github.maxstepanovski.projecttreeplugin.ui.GraphView
import com.google.gson.Gson
import com.intellij.openapi.project.Project
import java.io.*

class DiagramRepository(
        private val project: Project
) {
    private val writeMapper = ClassWrapperToGraphEntityMapper()
    private val readMapper = GraphEntityToGraphViewMapper()
    private val updateMapper = GraphViewToGraphEntityMapper()
    private val gson = Gson()

    fun shouldCreateFile(className: String): Boolean {
        return File(getFilePath(className)).exists().not()
    }

    fun getFilePath(className: String): String {
        val fileName = "${className}${DiagramEditorProvider.FILE_NAME_POSTFIX}"
        return "${project.basePath}/$fileName"
    }

    fun saveToFile(rootNode: ClassWrapper) {
        val graphEntity = writeMapper.map(rootNode)
        writeToFile(graphEntity, rootNode.name)
    }

    fun saveToFile(graphView: GraphView) {
        val graphEntity = updateMapper.map(graphView)
        writeToFile(graphEntity, graphView.rootNode.name)
    }

    fun readFromFile(filePath: String): GraphView {
        val bufferedReader = BufferedReader(FileReader(filePath))
        val graphEntity = gson.fromJson(bufferedReader, GraphEntity::class.java)
        return readMapper.map(graphEntity)
    }

    private fun writeToFile(graphEntity: GraphEntity, className: String) {
        val jsonString = gson.toJson(graphEntity)
        val fileWriter = FileWriter(getFilePath(className))
        try {
            fileWriter.write(jsonString)
            fileWriter.flush()
            fileWriter.close()
        } catch (io: IOException) {
            io.printStackTrace()
        }
    }
}