package com.github.maxstepanovski.projecttreeplugin.data.model

import com.google.gson.annotations.SerializedName

data class EdgeEntity(
        @SerializedName("id")
        val id: String,
        @SerializedName("from")
        val from: String,
        @SerializedName("to")
        val to: String
)