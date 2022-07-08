package com.app.lovelocal_assignment.model

import com.google.gson.annotations.SerializedName

data class ProductData(
    @SerializedName("products")
    var products: List<Product> = listOf()
)