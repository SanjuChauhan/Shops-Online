package com.app.lovelocal_assignment.api

import com.app.lovelocal_assignment.model.CategoryResponse
import com.app.lovelocal_assignment.model.ProductResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.QueryMap

interface DefaultApi {

    /**
     * get Category List
     */
    @GET("search/category/{categoryId}")
    fun getCategoryList(
        @Path("categoryId") param: Int,
        @QueryMap headers: Map<String, String>
    ): Call<CategoryResponse>

    /**
     * get Search Products
     */
    @GET("search/product")
    fun getSearchedProducts(
        @QueryMap headers: Map<String, String>
    ): Call<ProductResponse>

}