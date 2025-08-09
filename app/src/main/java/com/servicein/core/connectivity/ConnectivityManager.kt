package com.servicein.core.connectivity

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Network
import android.net.NetworkRequest
import android.os.Build
import android.provider.Settings
import androidx.core.app.ActivityCompat
import com.servicein.domain.model.ConnectivityState
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ConnectivityManager @Inject constructor(@ApplicationContext private val context: Context) {
    private val _connectivityState = MutableStateFlow(ConnectivityState())
    val connectivityState: StateFlow<ConnectivityState> = _connectivityState.asStateFlow()

    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as android.net.ConnectivityManager
    private val locationManager =
        context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    private val networkCallback = object : android.net.ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            _connectivityState.value = _connectivityState.value.copy(isInternetConnected = true)
        }

        override fun onLost(network: Network) {
            _connectivityState.value = _connectivityState.value.copy(isInternetConnected = false)
        }

        override fun onUnavailable() {
            _connectivityState.value = _connectivityState.value.copy(isInternetConnected = false)
        }
    }

    private val locationProviderListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {}
        override fun onProviderEnabled(provider: String) {
            checkLocationStatus()
        }

        override fun onProviderDisabled(provider: String) {
            checkLocationStatus()
        }
    }

    fun startMonitoring() {
        // Monitor network connectivity
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            connectivityManager.registerDefaultNetworkCallback(networkCallback)
        } else {
            val request = NetworkRequest.Builder().build()
            connectivityManager.registerNetworkCallback(request, networkCallback)
        }

        // Monitor location services
        try {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    5000L,
                    0f,
                    locationProviderListener
                )
                locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    5000L,
                    0f,
                    locationProviderListener
                )
            }
        } catch (e: Exception) {
            // Handle permission issues
        }

        // Initial status check
        checkInitialStatus()
    }

    fun stopMonitoring() {
        connectivityManager.unregisterNetworkCallback(networkCallback)
        locationManager.removeUpdates(locationProviderListener)
    }

    private fun checkInitialStatus() {
        checkInternetStatus()
        checkLocationStatus()
    }

    private fun checkInternetStatus() {
        val activeNetwork = connectivityManager.activeNetworkInfo
        val isConnected = activeNetwork?.isConnectedOrConnecting == true
        _connectivityState.value = _connectivityState.value.copy(isInternetConnected = isConnected)
    }

    private fun checkLocationStatus() {
        val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        val isLocationEnabled = isGpsEnabled || isNetworkEnabled

        _connectivityState.value =
            _connectivityState.value.copy(isLocationEnabled = isLocationEnabled)
    }

    fun openLocationSettings(activity: Activity) {
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        activity.startActivity(intent)
    }

    fun openWifiSettings(activity: Activity) {
        val intent = Intent(Settings.ACTION_WIFI_SETTINGS)
        activity.startActivity(intent)
    }
}