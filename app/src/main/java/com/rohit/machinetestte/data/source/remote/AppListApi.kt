package com.rohit.machinetestte.data.source.remote

import com.rohit.machinetestte.base.reponses.AppsResponse
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST


interface AppListApi {

    @POST("/v1/apps/list")
    @FormUrlEncoded
    fun apps(
        @Field("kid_id") kId: String
    ): Call<AppsResponse>

}