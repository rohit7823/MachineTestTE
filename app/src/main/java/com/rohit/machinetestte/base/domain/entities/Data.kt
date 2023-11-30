package com.rohit.machinetestte.base.domain.entities

import com.google.gson.annotations.SerializedName

data class Data (
    @SerializedName("app_list")
    val apps: List<App>? = null,
    @SerializedName("usage_access")
    val usageAccess: Long? = null
)