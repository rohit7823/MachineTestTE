package com.rohit.machinetestte.presentation.others

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import android.util.Log
import androidx.core.content.getSystemService
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

class AppConnectivity @Inject constructor(
    @ApplicationContext context: Context,
    coroutineScope: CoroutineScope
) {
    private val connectivityManager: ConnectivityManager = context.getSystemService()!!

    private val networkCallback = AppNetworkCallback()

    private val _currentNetwork = MutableStateFlow(provideDefaultNetwork())

    val connectivityStatusFlow: StateFlow<Boolean> =
        _currentNetwork
            .map {
                it.isConnected()
            }
            .stateIn(
                scope = coroutineScope,
                started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000L),
                initialValue = true,
            )

    val isConnected: Boolean
        get() = connectivityStatusFlow.value

    private fun NetworkCapabilities?.isNetworkCapabilitiesValid(): Boolean = when {
        this == null -> false
        hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED) &&
                (hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                        hasTransport(NetworkCapabilities.TRANSPORT_VPN) ||
                        hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                        hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) -> true
        else -> false
    }

    private fun provideDefaultNetwork(): CurrentNetwork {
        return CurrentNetwork(
            isListening = false,
            networkCapabilities = null,
            isAvailable = false,
            isBlocked = false
        )
    }

    private data class CurrentNetwork(
        val isListening: Boolean,
        val networkCapabilities: NetworkCapabilities?,
        val isAvailable: Boolean,
        val isBlocked: Boolean
    )

    private fun CurrentNetwork.isConnected(): Boolean {
        // Since we don't know the network state if NetworkCallback is not registered.
        // We assume that it's disconnected.
        return isListening &&
                isAvailable &&
                !isBlocked &&
                networkCapabilities.isNetworkCapabilitiesValid()
    }

    fun stopListenNetworkState() {
        if (!_currentNetwork.value.isListening) {
            return
        }

        _currentNetwork.update {
            it.copy(isListening = false)
        }

        connectivityManager.unregisterNetworkCallback(networkCallback)
    }

    fun listeningNetworkState() {
        if (_currentNetwork.value.isListening) {
            return
        }

        _currentNetwork.update {
            provideDefaultNetwork().copy(isListening = true)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            connectivityManager.registerDefaultNetworkCallback(networkCallback)
        } else {
            connectivityManager.registerNetworkCallback(
                NetworkRequest.Builder()
                    .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                    .addCapability(NetworkCapabilities.NET_CAPABILITY_SUPL)
                    .addCapability(NetworkCapabilities.NET_CAPABILITY_NOT_RESTRICTED)
                    .addCapability(NetworkCapabilities.NET_CAPABILITY_TRUSTED)
                    .build(),
                networkCallback
            )
        }
    }

    private inner class AppNetworkCallback : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(
            network: Network
        ) {
            Log.d("NetworkState", "onAvailable")
            _currentNetwork.update {
                it.copy(
                    isAvailable = true,
                )
            }
        }

        override fun onLost(
            network: Network
        ) {
            Log.d("NetworkState", "onLost")
            _currentNetwork.update {
                it.copy(
                    isAvailable = false,
                    networkCapabilities = null
                )
            }
        }

        override fun onUnavailable() {
            Log.d("NetworkState", "onUnavailable")
            _currentNetwork.update {
                it.copy(
                    isAvailable = false,
                    networkCapabilities = null
                )
            }
        }

        override fun onCapabilitiesChanged(
            network: Network,
            networkCapabilities: NetworkCapabilities
        ) {
            _currentNetwork.update {
                it.copy(networkCapabilities = networkCapabilities)
            }
        }

        override fun onBlockedStatusChanged(
            network: Network,
            blocked: Boolean
        ) {
            _currentNetwork.update {
                it.copy(isBlocked = blocked)
            }
        }
    }
}