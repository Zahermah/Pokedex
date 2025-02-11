package com.example.pokedex.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import javax.inject.Inject


class NetworkConnectivityObserver @Inject constructor(
    private val context: Context
) {
    private val connectivityManager: ConnectivityManager by lazy {
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }


    sealed class ConnectionState {
        object Available : ConnectionState()
        object Unavailable : ConnectionState()
        object Lost : ConnectionState()
    }


    fun observe(): Flow<ConnectionState> = callbackFlow {
        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                trySend(ConnectionState.Available)
            }

            override fun onLost(network: Network) {
                super.onLost(network)
                trySend(ConnectionState.Lost)
            }

            override fun onUnavailable() {
                super.onUnavailable()
                trySend(ConnectionState.Unavailable)
            }
        }

        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .build()

        try {
            connectivityManager.registerNetworkCallback(request, callback)
        } catch (e: SecurityException) {
            close(e)
        }

        awaitClose {
            connectivityManager.unregisterNetworkCallback(callback)
        }
    }.distinctUntilChanged()


}
