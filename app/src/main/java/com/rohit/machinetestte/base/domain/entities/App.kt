package com.rohit.machinetestte.base.domain.entities

data class App(
    val appID: Long? = null,
    val fkKidID: Long? = null,
    val kidProfileImage: String? = null,
    val appName: String? = null,
    val appIcon: String? = null,
    val appPackageName: String? = null,
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