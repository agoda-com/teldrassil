package com.github.maxstepanovski.projecttreeplugin.data.model

import com.github.maxstepanovski.projecttreeplugin.model.ClassType
import com.google.gson.annotations.SerializedName

data class NodeEntity(
        @SerializedName("id") val id: String,
        @SerializedName("name") val name: String,
        @SerializedName("class_type") val classType: ClassType,
        @SerializedName("fields") val fields: List<String>,
        @SerializedName("methods") val methods: List<String>,
        @SerializedName("x") val x: Int,
        @SerializedName("y") val y: Int,
        @SerializedName("fullClassName") val fullClassName: String,
)
