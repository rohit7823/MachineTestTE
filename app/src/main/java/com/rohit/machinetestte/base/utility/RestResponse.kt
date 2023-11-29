package com.rohit.machinetestte.base.utility
sealed class RestResponse<T>(
    val data: T? = null,
    val message: String? = null,
    val progress: Int? = null,
    val state: Boolean = false,
) {
    class Success<T>(data: T) : RestResponse<T>(data)
    class Error<T>(message: String? = null, data: T? = null) : RestResponse<T>(data, message)

}