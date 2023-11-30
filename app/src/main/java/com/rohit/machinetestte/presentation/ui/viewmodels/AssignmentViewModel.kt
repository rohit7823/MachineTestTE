package com.rohit.machinetestte.presentation.ui.viewmodels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rohit.machinetestte.R
import com.rohit.machinetestte.base.domain.entities.Status
import com.rohit.machinetestte.base.domain.repositories.AssignmentRepository
import com.rohit.machinetestte.base.utility.RestResponse
import com.rohit.machinetestte.presentation.others.AppConnectivity
import com.rohit.machinetestte.presentation.others.castListToRequiredTypes
import com.rohit.machinetestte.presentation.others.toUiModel
import com.rohit.machinetestte.presentation.ui.states.AppModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


@HiltViewModel
class AssignmentViewModel @Inject constructor(
    private val repository: AssignmentRepository,
    private val connectivity: AppConnectivity
) : ViewModel() {


    private val _appsStateList = mutableStateListOf<AppModel>()
    private val _apps = MutableStateFlow(_appsStateList)
    val apps =
        _apps.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), mutableStateListOf())

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    val notifier = mutableStateOf("")

    val pages = listOf(R.string.applications, R.string.settings)

    val appConnectivity = connectivity.connectivityStatusFlow

    val openAppIntent = mutableStateOf("")

    var loading by mutableStateOf(false)
        private set

    init {
        connectivity.listeningNetworkState()

        viewModelScope.launch {
            connectivity.connectivityStatusFlow.collect {
                if (it) {
                    fetch()
                }
            }
        }

    }

    suspend fun fetch(id: String = "378") {
        loading = true
        val response = repository.apps(kId = id)
        loading = false
        when (response) {
            is RestResponse.Success -> {
                response.data?.run {
                    when (success == true && data?.apps != null) {
                        true -> {
                            data?.apps?.map {
                                it.toUiModel()
                            }?.let { data ->
                                _appsStateList.clear()
                                _appsStateList.addAll(data)
                                _apps.update {
                                    _appsStateList
                                }
                            }
                        }

                        false -> {
                            message ?: "Getting an unknown Error!!"
                        }
                    }
                }
            }

            is RestResponse.Error -> {
                response.message ?: "Getting an unknown Error!!"
            }
        }
    }

    fun onTapStatus(appModel: AppModel, state: Boolean) {
        val idx = _appsStateList.indexOf(appModel)
        _appsStateList[idx].status.value = if (state) Status.Active else Status.Inactive
        appModel.appPackageName?.let {
            openAppIntent.value = it
        }
    }

    fun searchApps(query: TextFieldValue) {
        _searchQuery.update {
            query.text
        }
    }

    fun filterApps(query: String) {
        if (query.isBlank()) {
            _apps.update {
                _appsStateList
            }
            return
        }
        _apps.update {
            it.filter { model ->
                model.appName?.contains(other = query, ignoreCase = true) ?: false
            }.toMutableStateList()
        }
    }

    override fun onCleared() {
        connectivity.stopListenNetworkState()
        super.onCleared()
    }
}