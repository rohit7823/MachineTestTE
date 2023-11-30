package com.rohit.machinetestte.base.domain.entities

import com.google.gson.annotations.SerializedName

data class App(
    @SerializedName("app_id")
    val appID: Long? = null,
    @SerializedName("fk_kid_id")
    val fkKidID: Long? = null,
    @SerializedName("kid_profile_image")
    val kidProfileImage: String? = null,
    @SerializedName("app_name")
    val appName: String? = null,
    @SerializedName("app_icon")
    val appIcon: String? = null,
    @SerializedName("app_package_name")
    val appPackageName: String? = null,
    @SerializedName("status")
    val status: String? = null
) {

    fun checkStatus(): Status? {
        return when (status?.lowercase()) {
            Status.Active.key -> Status.Active
            Status.Inactive.key -> Status.Inactive
            else -> null
        }
    }

}