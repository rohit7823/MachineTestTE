package com.rohit.machinetestte.data.impl

import com.rohit.machinetestte.base.domain.repositories.AssignmentRepository
import com.rohit.machinetestte.base.reponses.AppsResponse
import com.rohit.machinetestte.base.utility.RestResponse
import com.rohit.machinetestte.data.source.remote.AppListApi
import com.rohit.machinetestte.presentation.others.handleResponse
import retrofit2.Retrofit

import javax.inject.Inject

class AssignmentRepositoryImpl @Inject constructor(
    private val restClient: Retrofit
) : AssignmentRepository {

    override suspend fun apps(kId: String): RestResponse<AppsResponse> =
        restClient.create(AppListApi::class.java).apps(kId).handleResponse()

}