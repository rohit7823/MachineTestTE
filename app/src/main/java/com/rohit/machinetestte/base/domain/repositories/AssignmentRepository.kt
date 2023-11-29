package com.rohit.machinetestte.base.domain.repositories

import com.rohit.machinetestte.base.reponses.AppsResponse
import com.rohit.machinetestte.base.utility.RestResponse
import kotlinx.coroutines.flow.Flow

interface AssignmentRepository {
    suspend fun apps(kId: String): RestResponse<AppsResponse>
}