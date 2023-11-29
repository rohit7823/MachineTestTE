package com.rohit.machinetestte.presentation.others

import androidx.compose.runtime.mutableStateOf
import com.rohit.machinetestte.base.domain.entities.App
import com.rohit.machinetestte.presentation.ui.states.AppModel

fun App.toUiModel(): AppModel = AppModel(
    appID, fkKidID, kidProfileImage,
    appName, appIcon, appPackageName,
    status = mutableStateOf(checkStatus())
)