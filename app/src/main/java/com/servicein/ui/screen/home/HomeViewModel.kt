package com.servicein.ui.screen.home

import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import android.Manifest
import android.util.Log
import com.servicein.domain.model.Shop

class HomeViewModel(): ViewModel() {
    private val _userLocation = MutableStateFlow<LatLng?>(null)
    val userLocation: StateFlow<LatLng?> = _userLocation.asStateFlow()

    private val _hasLocationPermission = MutableStateFlow(false)
    val hasLocationPermission: StateFlow<Boolean> = _hasLocationPermission.asStateFlow()

    private val _nearestShop = MutableStateFlow<List<Shop>>(emptyList())
    val nearestShop: StateFlow<List<Shop>> = _nearestShop.asStateFlow()

    private val _selectedShop = MutableStateFlow<Shop?>(null)
    val selectedShop: StateFlow<Shop?> = _selectedShop.asStateFlow()

    fun getNearestShop() {
        _nearestShop.value = listOf(
            Shop(
                shopName = "Bengkel Sehat Motor",
                rating = 5,
                address = LatLng(-6.914744, 107.609810) // Alun-Alun Bandung
            ),
            Shop(
                shopName = "Cihampelas Motor Service",
                rating = 4,
                address = LatLng(-6.927364, 107.634575) // Cihampelas Walk
            ),
            Shop(
                shopName = "BTC Auto Garage",
                rating = 3,
                address = LatLng(-6.886544, 107.615038) // BTC Fashion Mall
            ),
            Shop(
                shopName = "Metro Motor Bandung",
                rating = 4,
                address = LatLng(-6.940178, 107.627847) // Metro Indah Mall
            ),
            Shop(
                shopName = "Ujungberung Motor",
                rating = 5,
                address = LatLng(-6.869970, 107.572586) // Ujungberung
            ),
            Shop(
                shopName = "Cimahi Speed Garage",
                rating = 3,
                address = LatLng(-6.903449, 107.573116) // Cimahi
            ),
            Shop(
                shopName = "Padalarang Auto Service",
                rating = 4,
                address = LatLng(-6.915820, 107.742612) // Padalarang
            ),
            Shop(
                shopName = "Taman Sari Motor",
                rating = 5,
                address = LatLng(-6.931157, 107.598206) // Taman Sari
            ),
            Shop(
                shopName = "Dago Racing Garage",
                rating = 4,
                address = LatLng(-6.917647, 107.603201) // Dago
            ),
            Shop(
                shopName = "Riau Motor Works",
                rating = 5,
                address = LatLng(-6.914145, 107.623409) // Jalan Riau
            )
        )
    }

    fun selectShop(shop: Shop) {
        _selectedShop.value = shop
    }

    fun setUserLocation(context: Context, latLng: LatLng) {
        _userLocation.value = latLng
    }

    fun updatePermissionStatus(context: Context) {
        val granted = ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED

        _hasLocationPermission.value = granted
    }

    fun setPermissionGranted(granted: Boolean) {
        _hasLocationPermission.value = granted
    }

    fun getLastKnownLocation(
        context: Context,
        fusedLocationClient: FusedLocationProviderClient
    ) {
        if (ActivityCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.w("FormViewModel", "getLastKnownLocation dipanggil tanpa izin yang memadai.")
            return
        }

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                location?.let {
                    val latLng = LatLng(it.latitude, it.longitude)
                    _userLocation.value = latLng
                }
            }
    }
}