package com.servicein.ui.screen.order

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.model.LatLng
import com.servicein.BuildConfig
import com.servicein.core.util.LocationPermissionHandler
import com.servicein.core.util.MapUtil
import com.servicein.core.util.OrderType
import com.servicein.data.repository.OrderRepository
import com.servicein.data.repository.ShopRepository
import com.servicein.data.service.RouteService
import com.servicein.domain.model.Shop
import com.servicein.domain.preference.AppPreferencesManager
import com.servicein.ui.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class OrderViewModel @Inject constructor(
    private val shopRepository: ShopRepository,
    private val appPreferencesManager: AppPreferencesManager,
    private val orderRepository: OrderRepository,
) : ViewModel(), LocationPermissionHandler {
    private val _isSearchingShop = MutableStateFlow(false)
    val isSearchingShop: StateFlow<Boolean> = _isSearchingShop.asStateFlow()

    private val _displayedAddress = MutableStateFlow<String?>(null)
    val displayedAddress: StateFlow<String?> = _displayedAddress.asStateFlow()

    private val _userLocation = MutableStateFlow<LatLng?>(null)
    val userLocation: StateFlow<LatLng?> = _userLocation.asStateFlow()

    private val _hasLocationPermission = MutableStateFlow(false)
    val hasLocationPermission: StateFlow<Boolean> = _hasLocationPermission.asStateFlow()

    private val _selectedOrderType = MutableStateFlow<OrderType?>(null)
    val selectedOrderType: StateFlow<OrderType?> = _selectedOrderType.asStateFlow()

    private val _selectedLocation = MutableStateFlow<LatLng?>(null)
    val selectedLocation: StateFlow<LatLng?> = _selectedLocation.asStateFlow()

    private val _selectedDate = MutableStateFlow<LocalDateTime?>(null)
    val selectedDate: StateFlow<LocalDateTime?> = _selectedDate.asStateFlow()

    private val _selectedShop = MutableStateFlow<Shop?>(null)
    val selectedShop: StateFlow<Shop?> = _selectedShop.asStateFlow()

    private val _routePolyline = MutableStateFlow<List<LatLng>>(emptyList())
    val routePolyline: StateFlow<List<LatLng>> = _routePolyline.asStateFlow()

    fun createOrder(value: Int, routeAndPopUp: (String, String) -> Unit) {
        _isSearchingShop.value = true
        viewModelScope.launch {
            orderRepository.createOrder(
                customerName = appPreferencesManager.customerName.first(),
                customerId = appPreferencesManager.customerId.first(),
                orderType = selectedOrderType.value!!,
                dateTime = selectedDate.value!!.toString(),
                value = value,
                latitude = selectedLocation.value!!.latitude,
                longitude = selectedLocation.value!!.longitude
            ).fold(
                onSuccess = {
                    routeAndPopUp(Screen.Home.route, Screen.OrderLocation.route)
                    _isSearchingShop.value = false
                },
                onFailure = {
                    Log.e("OrderViewModel", "error create order: $it")
                    _isSearchingShop.value = false
                }
            )
        }
    }

    suspend fun getRoutePolyline(origin: LatLng, destination: LatLng) {
        if (_selectedLocation.value != null) {
            _routePolyline.value = RouteService().getRoutePolyline(
                origin = origin,
                destination = destination,
                apiKey = BuildConfig.DIRECTIONS_API_KEY
            )
        }
    }

    fun getSelectedShopData(shopId: String) {
        _isSearchingShop.value = true
        viewModelScope.launch {
            shopRepository.getShopById(shopId).fold(
                onSuccess = {
                    _selectedShop.value = it
                    _isSearchingShop.value = false
                    Log.d("OrderViewModel", "getSelectedShopData: $it")
                },
                onFailure = {
                    Log.e("OrderViewModel", "error get shop: $it")
                    _isSearchingShop.value = false
                }
            )
        }
    }

    fun pickRecommendedShop() {
        _isSearchingShop.value = true
        viewModelScope.launch {
            val shops: List<Shop>
            shopRepository.getAllShops().fold(
                onSuccess = {
                    shops = it
                    val sorted = shops.sortedWith(
                        compareBy(
                            { data -> MapUtil.calculateDistanceInKm(
                                _userLocation.value!!.latitude, _userLocation.value!!.longitude,
                                data.latitude, data.longitude
                            ) },
                            { -it.rating }
                        )
                    )
                    Log.d("OrderViewModel", "Sorted Shops: $sorted")
                    _selectedShop.value = sorted.first()
                    _isSearchingShop.value = false
                },
                onFailure = {
                    Log.e("OrderViewModel", "Error fetching shops", it)
                    _isSearchingShop.value = false
                }
            )
        }
    }

    fun setSelectedDate(date: LocalDateTime) {
        _selectedDate.value = date
    }

    fun setSelectedLocation(location: LatLng) {
        _selectedLocation.value = location
    }

    fun selectOrderType(orderType: OrderType) {
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
}