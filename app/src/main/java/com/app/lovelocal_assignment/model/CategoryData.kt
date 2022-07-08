package com.app.lovelocal_assignment.model

import com.google.gson.annotations.SerializedName

data class CategoryData(
    @SerializedName("id")
    var id: Int = 0,

    @SerializedName("name")
    var name: String = "",

    var image: Int = 0
)