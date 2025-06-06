package com.servicein.ui.screen.orderDetail

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
import com.google.firebase.firestore.ListenerRegistration
import com.servicein.BuildConfig
import com.servicein.data.repository.OrderRepository
import com.servicein.data.repository.ShopRepository
import com.servicein.data.service.RouteService
import com.servicein.domain.model.Order
import com.servicein.domain.model.Shop
import com.servicein.ui.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderDetailViewModel @Inject constructor(
    private val orderRepository: OrderRepository,
    private val shopRepository: ShopRepository
) : ViewModel() {
    private val _order = MutableStateFlow<Order?>(null)
    val order: StateFlow<Order?> = _order.asStateFlow()

    private val _shop = MutableStateFlow<Shop?>(null)
    val shop: StateFlow<Shop?> = _shop.asStateFlow()

    private val _isShopDataLoading = MutableStateFlow(false)
    val isShopDataLoading: StateFlow<Boolean> = _isShopDataLoading.asStateFlow()

    private val _hasLocationPermission = MutableStateFlow(false)
    val hasLocationPermission: StateFlow<Boolean> = _hasLocationPermission.asStateFlow()

    private val _userLocation = MutableStateFlow<LatLng?>(null)
    val userLocation: StateFlow<LatLng?> = _userLocation.asStateFlow()

    private val _routePolyline = MutableStateFlow<List<LatLng>>(emptyList())
    val routePolyline: StateFlow<List<LatLng>> = _routePolyline.asStateFlow()

    private var orderListener: ListenerRegistration? = null

    fun markOrderFinished(
        orderId: String,
        rating: Int,
        review: String,
        routeAndPopUp: (String, String) -> Unit
    ) {
        _isShopDataLoading.value = true
        viewModelScope.launch {
            orderRepository.completeOrder(orderId, rating, review).fold(
                onSuccess = {
                    Log.d("OrderDetailViewModel", "Order marked as finished")
                    shopRepository.addToWallet(_order.value!!.shopId, _order.value!!.value).fold(
                        onSuccess = {
                            Log.d("OrderDetailViewModel", "Wallet updated successfully")
                            _isShopDataLoading.value = false
                            routeAndPopUp(Screen.Home.route, Screen.OrderDetail.route)
                        },
                        onFailure = {
                            Log.d("OrderDetailViewModel", "Error updating wallet: ${it.message}")
                        }
                    )
                },
                onFailure = {
                    _isShopDataLoading.value = false
                    Log.d("OrderDetailViewModel", "Error marking order as finished: ${it.message}")
                }
            )
        }
    }

    private fun getShopData(shopId: String) {
        _isShopDataLoading.value = true
        viewModelScope.launch {
            shopRepository.getShopById(shopId).fold(
                onSuccess = { shop ->
                    _shop.value = shop
                    _isShopDataLoading.value = false
                    Log.d("OrderDetailViewModel", "Shop: $shop")
                },
                onFailure = {
                    _shop.value = null
                    _isShopDataLoading.value = false
                    Log.d("OrderDetailViewModel", "Error: ${it.message}")
                }
            )
        }
    }

    fun getOrderData(orderId: String) {
        _isShopDataLoading.value = true
        orderListener?.remove()
        viewModelScope.launch {
            orderListener = orderRepository.listenToOrderById(
                orderId,
                onResult = { result ->
                    result.fold(
                        onSuccess = { order ->
                            _order.value = order
                            getShopData(order?.shopId ?: "")
                            Log.d("OrderDetailViewModel", "Order: $order")
                        },
                        onFailure = {
                            _order.value = null
                            _isShopDataLoading.value = false
                            Log.d("OrderDetailViewModel", "Error: ${it.message}")
                        }
                    )
                }
            )
        }
    }

    suspend fun getRoutePolyline(origin: LatLng, destination: LatLng) {
        if (_userLocation.value != null) {
            _routePolyline.value = RouteService().getRoutePolyline(
                origin = origin,
                destination = destination,
                apiKey = BuildConfig.DIRECTIONS_API_KEY
            )
        }
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
            Log.w("OrderDetailViewModel", "getLastKnownLocation dipanggil tanpa izin yang memadai.")
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

    fun updatePermissionStatus(context: Context) {
        Log.d("OrderDetailViewModel", "updatePermissionStatus dipanggil")
        val granted = ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED

        _hasLocationPermission.value = granted
        Log.d("OrderDetailViewModel", _hasLocationPermission.value.toString())
    }

    fun setPermissionGranted(granted: Boolean) {
        _hasLocationPermission.value = granted
    }

    override fun onCleared() {
        orderListener?.remove()
        super.onCleared()
    }
}