package com.servicein.ui.screen.order

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.model.LatLng
import com.servicein.BuildConfig
import com.servicein.core.util.LocationPermissionHandler
import com.servicein.core.util.MapUtil
import com.servicein.core.util.OrderType
import com.servicein.data.service.RouteService
import com.servicein.domain.model.Shop
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDateTime

class OrderViewModel: ViewModel(), LocationPermissionHandler {
    private val _displayedAddress = MutableStateFlow<String?>(null)
    val displayedAddress: StateFlow<String?> = _displayedAddress.asStateFlow()

    private val _userLocation = MutableStateFlow<LatLng?>(null)
    val userLocation: StateFlow<LatLng?> = _userLocation.asStateFlow()

    private val _hasLocationPermission = MutableStateFlow(false)
    val hasLocationPermission: StateFlow<Boolean> = _hasLocationPermission.asStateFlow()

    private val _selectedOrderType = MutableStateFlow<OrderType?>(null)
    val selectedOrder: StateFlow<OrderType?> = _selectedOrderType.asStateFlow()

    private val _selectedLocation = MutableStateFlow<LatLng?>(null)
    val selectedLocation: StateFlow<LatLng?> = _selectedLocation.asStateFlow()

    private val _selectedDate = MutableStateFlow<LocalDateTime?>(null)
    val selectedDate: StateFlow<LocalDateTime?> = _selectedDate.asStateFlow()

    private val _selectedShop = MutableStateFlow<Shop?>(null)
    val selectedShop: StateFlow<Shop?> = _selectedShop.asStateFlow()

    private val _routePolyline = MutableStateFlow<List<LatLng>>(emptyList())
    val routePolyline: StateFlow<List<LatLng>> = _routePolyline.asStateFlow()

    suspend fun getRoutePolyline(origin: LatLng, destination: LatLng) {
        if (_userLocation.value != null) {
            _routePolyline.value = RouteService().getRoutePolyline(
                origin = origin,
                destination = destination,
                apiKey = BuildConfig.DIRECTIONS_API_KEY
            )
        }
    }

    fun getSelectedShopData(shopId: Int) {
        _selectedShop.value = _shopList.find { it.id == shopId }
    }

    fun setSelectedDate(date: LocalDateTime) {
        _selectedDate.value = date
    }

    fun setSelectedLocation(location: LatLng) {
        _selectedLocation.value = location
    }

    fun selectOrder(orderType: OrderType) {
        _selectedOrderType.value = orderType
    }

    fun setUserLocation(context: Context, latLng: LatLng) {
        _userLocation.value = latLng
        _displayedAddress.value = MapUtil.getAddressFromLocation(context, latLng)
    }

    override fun updatePermissionStatus(context: Context) {
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

    override fun setPermissionGranted(granted: Boolean) {
        _hasLocationPermission.value = granted
    }

    override fun getLastKnownLocation(
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

    fun getAddressFromLocation(context: Context, latLng: LatLng) {
        _displayedAddress.value = MapUtil.getAddressFromLocation(context, latLng)
    }

    val _shopList = listOf(
        Shop(
            id = 0,
            shopName = "Bengkel Sehat Motor",
            rating = 5,
            address = LatLng(-6.914744, 107.609810) // Alun-Alun Bandung
        ),
        Shop(
            id = 1,
            shopName = "Cihampelas Motor Service",
            rating = 4,
            address = LatLng(-6.927364, 107.634575) // Cihampelas Walk
        ),
        Shop(
            id = 2,
            shopName = "BTC Auto Garage",
            rating = 3,
            address = LatLng(-6.886544, 107.615038) // BTC Fashion Mall
        ),
        Shop(
            id = 3,
            shopName = "Metro Motor Bandung",
            rating = 4,
            address = LatLng(-6.940178, 107.627847) // Metro Indah Mall
        ),
        Shop(
            id = 4,
            shopName = "Ujungberung Motor",
            rating = 5,
            address = LatLng(-6.869970, 107.572586) // Ujungberung
        ),
        Shop(
            id = 5,
            shopName = "Cimahi Speed Garage",
            rating = 3,
            address = LatLng(-6.903449, 107.573116) // Cimahi
        ),
        Shop(
            id = 6,
            shopName = "Padalarang Auto Service",
            rating = 4,
            address = LatLng(-6.915820, 107.742612) // Padalarang
        ),
        Shop(
            id = 7,
            shopName = "Taman Sari Motor",
            rating = 5,
            address = LatLng(-6.931157, 107.598206) // Taman Sari
        ),
        Shop(
            id = 8,
            shopName = "Dago Racing Garage",
            rating = 4,
            address = LatLng(-6.917647, 107.603201) // Dago
        ),
        Shop(
            id = 9,
            shopName = "Riau Motor Works",
            rating = 5,
            address = LatLng(-6.914145, 107.623409) // Jalan Riau
        )
    )
}