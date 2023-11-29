package com.rohit.machinetestte.presentation.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rohit.machinetestte.base.domain.repositories.AssignmentRepository
import com.rohit.machinetestte.base.utility.RestResponse
import com.rohit.machinetestte.presentation.others.toUiModel
import com.rohit.machinetestte.presentation.ui.states.AppModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


@HiltViewModel
class AssignmentViewModel @Inject constructor(
    private val repository: AssignmentRepository
) : ViewModel() {


    private val _appsStateList = mutableStateListOf<AppModel>()
    private val _apps = MutableStateFlow(_appsStateList)
    val apps = _apps.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), mutableStateListOf())

    val notifier = mutableStateOf("")

    var loading by mutableStateOf(false)
        private set

    init {
        fetch()
    }

    fun fetch(id: String = "378") {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                loading = true
                val response = repository.apps(kId = id)
                loading = false
                when (response) {
                    is RestResponse.Success -> {
                        response.data?.run {
                            when (success == true && data?.apps != null) {
                                true -> {
                                    _appsStateList.clear()
                                    data?.apps?.let {
                                        it.map { app ->
                                            app.toUiModel()
                                        }
                                    }?.let {
                                        _appsStateList.addAll(it)
                                    }
                                }

                                false -> {
                                    notifier.value = message ?: "Getting an unknown Error!!"
                                }
                            }
                        }
                    }

                    is RestResponse.Error -> {
                        notifier.value = response.message ?: "Getting an unknown Error!!"
                    }
                }
            }.runCatching {

            }
        }
    }
}