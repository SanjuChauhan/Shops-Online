package com.app.lovelocal_assignment.model

import com.google.gson.annotations.SerializedName

class ProductResponse {
    @SerializedName("success")
    var success: Boolean = false

    @SerializedName("data")
    var data: List<ProductData> = listOf()

    @SerializedName("count")
    var count: Int = 0

    @SerializedName("prev")
    var prev: String = ""

    @SerializedName("next")
    var next: String = ""
}