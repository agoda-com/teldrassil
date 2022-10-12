package model

import com.google.gson.annotations.SerializedName
import model.ClassType

data class NodeEntity(
        @SerializedName("id") val id: String,
        @SerializedName("name") val name: String,
        @SerializedName("class_type") val classType: ClassType = ClassType.CLASS,
        @SerializedName("fields") val fields: List<String> = emptyList(),
        @SerializedName("methods") val methods: List<String> = emptyList(),
        @SerializedName("x") val x: Int = 0,
        @SerializedName("y") val y: Int = 0,
        @SerializedName("fullClassName") val fullClassName: String = "",
)
