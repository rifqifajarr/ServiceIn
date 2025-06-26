package com.servicein.ui.screen.home

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
import com.servicein.core.util.MapUtil
import com.servicein.domain.model.Customer
import com.servicein.domain.model.Order
import com.servicein.domain.model.Shop
import com.servicein.domain.usecase.GetCustomerUseCase
import com.servicein.domain.usecase.GetOrderUseCase
import com.servicein.domain.usecase.GetShopsUseCase
import com.servicein.domain.usecase.ManageAccountUseCase
import com.servicein.domain.usecase.ManagePreferencesUseCase
import com.servicein.domain.usecase.ManageWalletUseCase
import com.servicein.ui.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor (
    private val managePreferencesUseCase: ManagePreferencesUseCase,
    private val getCustomerUseCase: GetCustomerUseCase,
    private val manageWalletUseCase: ManageWalletUseCase,
    private val getShopsUseCase: GetShopsUseCase,
    private val getOrderUseCase: GetOrderUseCase,
    private val manageAccountUseCase: ManageAccountUseCase,
): ViewModel() {
    private val _isShopLoading = MutableStateFlow(false)
    val isShopLoading: StateFlow<Boolean> = _isShopLoading.asStateFlow()

    private val _isUserDataLoading = MutableStateFlow(false)
    val isUserDataLoading: StateFlow<Boolean> = _isUserDataLoading.asStateFlow()

    private val _userLocation = MutableStateFlow<LatLng?>(null)
    val userLocation: StateFlow<LatLng?> = _userLocation.asStateFlow()

    private val _hasLocationPermission = MutableStateFlow(false)
    val hasLocationPermission: StateFlow<Boolean> = _hasLocationPermission.asStateFlow()

    private val _nearestShop = MutableStateFlow<List<Shop>>(emptyList())
    val nearestShop: StateFlow<List<Shop>> = _nearestShop.asStateFlow()

    private val _recommendedShop = MutableStateFlow<List<Shop>>(emptyList())
    val recommendedShop: StateFlow<List<Shop>> = _recommendedShop.asStateFlow()

    private val _activeOrder = MutableStateFlow<List<Order>>(emptyList())
    val activeOrder: StateFlow<List<Order>> = _activeOrder.asStateFlow()

    private val _selectedShop = MutableStateFlow<Shop?>(null)
    val selectedShop: StateFlow<Shop?> = _selectedShop.asStateFlow()

    private val _customer = MutableStateFlow<Customer?>(null)
    val customer: StateFlow<Customer?> = _customer.asStateFlow()

    fun logout(routeAndPopUp: (String, String) -> Unit) {
        _isUserDataLoading.value = true
        viewModelScope.launch {
            manageAccountUseCase.signOut()
            managePreferencesUseCase.clearAllPreferences()
            _customer.value = null
            _isUserDataLoading.value = false
            routeAndPopUp(Screen.SplashScreen.route, Screen.Home.route)
        }
    }

    fun getCustomerData() {
        _isUserDataLoading.value = true
        viewModelScope.launch {
            val id = managePreferencesUseCase.customerId.first()
            getCustomerUseCase(id).fold(
                onSuccess = {
                    _customer.value = it
                    _isUserDataLoading.value = false
                    Log.d("HomeViewModel", "Customer : $it")
                },
                onFailure = {
                    Log.e("HomeViewModel", "Error fetching customer data", it)
                    _isUserDataLoading.value = false
                }
            )
        }
    }

    fun topUpWallet(amount: Int) {
        _isUserDataLoading.value = true
        viewModelScope.launch {
            val shopId = managePreferencesUseCase.customerId.first()
            manageWalletUseCase.add(shopId, amount).fold(
                onSuccess = {
                    getCustomerData()
                    _isUserDataLoading.value = false
                    Log.d("HomeViewModel", "Withdraw from wallet successful")
                },
                onFailure = {
                    _isUserDataLoading.value = false
                    Log.d("HomeViewModel", "Withdraw from wallet failed: ${it.message}")
                }
            )
        }
    }

    fun getActiveOrder() {
        _isShopLoading.value = true
        viewModelScope.launch {
            getOrderUseCase.listenActiveOrder(
                customerId = managePreferencesUseCase.customerId.first()
            ).onEach { result ->
                result.onSuccess { activeOrders ->
                    _activeOrder.value = activeOrders
                    Log.d("HomeViewModel", "Active Order : $activeOrders")
                    _isShopLoading.value = false
                }.onFailure {
                    Log.e("HomeViewModel", "Error fetching active order", it)
                    _activeOrder.value = emptyList()
                    _isShopLoading.value = false
                }
            }.collect()
        }
    }

    private fun getRecommendedShops() {
        val shops = _nearestShop.value
        if (_userLocation.value != null) {
            val sorted = shops.sortedWith(
                compareBy(
                    { MapUtil.calculateDistanceInKm(
                        _userLocation.value!!.latitude, _userLocation.value!!.longitude,
                        it.latitude, it.longitude
                    ) },
                    { -it.rating }
                )
            )
            Log.d("HomeViewModel", "Sorted Shops: $sorted")
            _recommendedShop.value = sorted.take(3)
        }
    }

    fun getShopsData() {
        _isShopLoading.value = true
        viewModelScope.launch {
            getShopsUseCase().fold(
                onSuccess = {
                    _nearestShop.value = it
                    getRecommendedShops()
                    Log.d("HomeViewModel", "Shops : $it")
                    _isShopLoading.value = false
                },
                onFailure = {
                    Log.e("HomeViewModel", "Error fetching shops", it)
                    _isShopLoading.value = false
                }
            )
        }
    }

    fun selectShop(shop: Shop) {
        _selectedShop.value = shop
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