package com.servicein.domain.model

data class ConnectivityState(
    val isInternetConnected: Boolean = true,
    val isLocationEnabled: Boolean = true
)