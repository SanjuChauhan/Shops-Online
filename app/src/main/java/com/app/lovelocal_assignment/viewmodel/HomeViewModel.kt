package com.app.lovelocal_assignment.viewmodel

import android.location.Address
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.lovelocal_assignment.api.APIResponseListener
import com.app.lovelocal_assignment.model.CategoryResponse
import com.app.lovelocal_assignment.model.Product
import com.app.lovelocal_assignment.model.ProductResponse
import com.my.favourite.movies.constants.*
import com.my.favourite.movies.repository.CategoryRepository.Companion.categoryRepository

class HomeViewModel : ViewModel() {

    val strToastMessage = MutableLiveData<String>()
    val isShowProgressDialog = MutableLiveData<Boolean>()
    val strGreetingMessage = MutableLiveData("")
    val strSearch = MutableLiveData("")
    val latitude = MutableLiveData(27)
    val longitude = MutableLiveData(80)
    val selectedProduct = MutableLiveData<Product>()
    val categoryResponse = MutableLiveData<CategoryResponse>()
    val productDataListMutableData = MutableLiveData<List<Product>>()
    val locationListMutableData = MutableLiveData<List<Address>>()

    /**
     * Call Category List API.
     */
    fun callCategoryListAPI(categoryId: Int) {
        isShowProgressDialog.postValue(true)
        val queryMap = HashMap<String, String>()
        queryMap[KEY_PAGE] = "1"
        queryMap[KEY_RECORDS] = "10"
        queryMap[KEY_LATITUDE] = latitude.value.toString()
        queryMap[KEY_LONGITUDE] = longitude.value.toString()

        categoryRepository.getCategoryList(queryMap, categoryId, object : APIResponseListener {
            override fun onSuccess(response: Any?) {
                val searchResponse = response as CategoryResponse
                isShowProgressDialog.postValue(false)
                categoryResponse.postValue(searchResponse)
            }

            override fun onFail(message: String?) {
                strToastMessage.postValue(message)
                isShowProgressDialog.postValue(false)
            }
        })
    }

    /**
     * Call Category List API.
     */
    fun callSearchedProductsAPI(strSearch: String) {
        val queryMap = HashMap<String, String>()
        queryMap[KEY_PAGE] = "1"
        queryMap[KEY_SEARCH_TEXT] = strSearch
        queryMap[KEY_RECORDS] = "10"
        queryMap[KEY_LATITUDE] = latitude.value.toString()
        queryMap[KEY_LONGITUDE] = longitude.value.toString()

        categoryRepository.getSearchedProducts(queryMap, object : APIResponseListener {
            override fun onSuccess(response: Any?) {
                val productResponse = response as ProductResponse
                if(productResponse.data.isNotEmpty()) {
                    productDataListMutableData.postValue(productResponse.data[0].products)
                }
            }

            override fun onFail(message: String?) {
                strToastMessage.postValue(message)
                isShowProgressDialog.postValue(false)
            }
        })
    }

}