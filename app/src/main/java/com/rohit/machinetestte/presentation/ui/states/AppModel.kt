package com.rohit.machinetestte.presentation.ui.states

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import com.rohit.machinetestte.base.domain.entities.Status

@Stable
data class AppModel(
    val appID: Long? = null,
    val fkKidID: Long? = null,
    val kidProfileImage: String? = null,
    val appName: String? = null,
    val appIcon: String? = null,
    val appPackageName: String? = null,
    val status: MutableState<Status?> = mutableStateOf(null)
)
