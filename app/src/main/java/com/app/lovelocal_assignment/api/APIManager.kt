package com.app.lovelocal_assignment.api

import com.my.favourite.movies.constants.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class APIManager private constructor() {
    val defaultAPI: DefaultApi? = ApiClient().createService(DefaultApi::class.java)
    private val executor: Executor

    fun <T> callAPI(api: Call<T>?, listener: APIResponseListener) {
        executor.execute {
            api?.enqueue(object : Callback<T> {
                override fun onResponse(call: Call<T>, response: Response<T>) {
                    handleAPIResponse(response.code(), response, listener)
                }

                override fun onFailure(call: Call<T>, t: Throwable) {
                    listener.onFail(SERVER_ERROR_MESSAGE)
                }
            })
        }
    }

    private fun <T> handleAPIResponse(
        status: Int,
        response: Response<T>,
        listener: APIResponseListener
    ) {
        when (status) {
            200, 201 -> {
                listener.onSuccess(response.body())
            }
            400 -> {
                // Bad request
                setFailureMessage(
                    listener,
                    BAD_REQUEST_ERROR_MESSAGE
                )
            }
            401 -> {
                // Unauthorized
                setFailureMessage(
                    listener,
                    UNAUTHORISED_ERROR_MESSAGE
                )
            }
            403 -> {
                // Forbidden
                setFailureMessage(
                    listener,
                    FORBIDDEN_ERROR_MESSAGE
                )
            }
            500 -> {
                // Internal Server Error
                setFailureMessage(
                    listener,
                    INTERNAL_SERVER_ERROR_MESSAGE
                )
            }
            else -> {
                setFailureMessage(
                    listener,
                    SERVER_ERROR_MESSAGE
                )
            }
        }
    }

    private fun setFailureMessage(
        listener: APIResponseListener,
        message: String
    ) {
        listener.onFail(message)
    }

    companion object {
        private var sAPIManager: APIManager? = null
        val instance: APIManager?
            get() {
                if (sAPIManager == null) {
                    sAPIManager = APIManager()
                }
                return sAPIManager
            }
    }

    init {
        executor = Executors.newSingleThreadExecutor()
    }
}