package com.rohit.machinetestte.base.reponses

import com.rohit.machinetestte.base.domain.entities.Data

data class AppsResponse (
    val success: Boolean? = null,
    val data: Data? = null,
    val message: String? = null
)
