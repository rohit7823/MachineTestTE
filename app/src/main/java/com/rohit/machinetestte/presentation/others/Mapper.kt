package com.rohit.machinetestte.presentation.others

import androidx.compose.runtime.mutableStateOf
import com.rohit.machinetestte.base.domain.entities.App
import com.rohit.machinetestte.presentation.ui.states.AppModel

fun App.toUiModel(): AppModel = AppModel(
    appID = appID, fkKidID = fkKidID, kidProfileImage = kidProfileImage,
    appName = appName, appIcon = appIcon, appPackageName = appPackageName,
    status = mutableStateOf(checkStatus())
)