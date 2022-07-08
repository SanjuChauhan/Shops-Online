package com.my.favourite.movies.repository

import com.app.lovelocal_assignment.api.APIManager
import com.app.lovelocal_assignment.api.APIResponseListener

open class CategoryRepository {

    /**
     * Get Category List
     */
    fun getCategoryList(
        queryMap: Map<String, String>,
        categoryId: Int,
        listener: APIResponseListener
    ) {
        val call = APIManager.instance?.defaultAPI?.getCategoryList(categoryId, queryMap)
        APIManager.instance?.callAPI(call, listener)
    }

    /**
     * Get Searched Products List
     */
    fun getSearchedProducts(
        queryMap: Map<String, String>,
        listener: APIResponseListener
    ) {
        val call = APIManager.instance?.defaultAPI?.getSearchedProducts(queryMap)
        APIManager.instance?.callAPI(call, listener)
    }

    companion object {
        @JvmStatic
        val categoryRepository: CategoryRepository
            get() {
                return CategoryRepository()
            }
    }
}
