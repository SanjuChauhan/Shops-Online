package com.app.lovelocal_assignment.model

import com.google.gson.annotations.SerializedName

data class Product(
    @SerializedName("id")
    var id: Int = 0,

    @SerializedName("name")
    var name: String = "",

    @SerializedName("image")
    var image: String = ""
)