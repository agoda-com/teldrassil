package com.github.maxstepanovski.projecttreeplugin.model

import com.google.gson.annotations.SerializedName

enum class ClassType {
    @SerializedName("class")
    CLASS,
    @SerializedName("abstract_class")
    ABSTRACT_CLASS,
    @SerializedName("interface")
    INTERFACE,
    @SerializedName("enum")
    ENUM,
    @SerializedName("data_class")
    DATA_CLASS,
    @SerializedName("object")
    OBJECT
}
