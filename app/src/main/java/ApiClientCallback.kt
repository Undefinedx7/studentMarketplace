package com.example.studentmarketplace

class ApiClientCallback {

    interface ApiResponseListener {
        fun onSuccess(response: ApiResponse)
        fun onError(error: ApiError)
    }

    class ApiError(val message: String)

    data class ApiResponse(val data: Any?, val message: String, val success: Boolean)

}