package com.rohit.machinetestte.presentation.others

import android.util.Log
import com.google.gson.JsonParseException
import com.rohit.machinetestte.base.utility.RestResponse
import kotlinx.coroutines.suspendCancellableCoroutine
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Invocation
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

suspend fun <T> Call<T>.handleResponse(): RestResponse<T> = try {
    suspendCancellableCoroutine { continuation ->
        enqueue(object : Callback<T> {
            override fun onResponse(call: Call<T>, response: retrofit2.Response<T>) {
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body == null) {
                        val invocation = call.request().tag(Invocation::class.java)!!
                        val method = invocation.method()
                        val e = KotlinNullPointerException(
                            "Response from " +
                                    method.declaringClass.name +
                                    '.' +
                                    method.name +
                                    " was null but response body type was declared as non-null"
                        )
                        continuation.resumeWithException(e)
                    } else {
                        continuation.resume(RestResponse.Success(body))
                    }
                } else {
                    continuation.resumeWithException(HttpException(response))
                }
            }

            override fun onFailure(call: Call<T>, t: Throwable) {
                continuation.resumeWithException(t)
            }
        })

        continuation.invokeOnCancellation {
            cancel()
        }
    }
} catch (ex: HttpException) {
    Log.d("TESTING", "HttpException: ${ex.message}")
    RestResponse.Error(message = ex.message)
} catch (ex: JsonParseException) {
    Log.d("TESTING", "JsonParseException: ${ex.message}")
    RestResponse.Error(message = ex.message)
} catch (ex: KotlinNullPointerException) {
    Log.d("TESTING", "KotlinNullPointerException: ${ex.message}")
    RestResponse.Error(message = ex.message)
} catch (ex: RuntimeException) {
    Log.d("TESTING", "RuntimeException: ${ex.message}")
    RestResponse.Error(message = ex.message)
} catch (ex: IllegalArgumentException) {
    Log.d("TESTING", "IllegalArgumentException: ${ex.message}")
    RestResponse.Error(message = ex.message)
} catch (ex: Exception) {
    Log.d("TESTING", "Exception: ${ex.message}")
    RestResponse.Error(message = ex.message)
}
