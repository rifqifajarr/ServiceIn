package com.servicein.ui.screen.order

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import com.servicein.R
import com.servicein.core.util.MapUtil
import com.servicein.core.util.OrderType
import com.servicein.core.util.Util
import com.servicein.core.util.Util.toDisplayString
import com.servicein.domain.model.Shop
import com.servicein.ui.component.RequestLocationPermission
import com.servicein.ui.component.SearchPlaceBottomSheet
import com.servicein.ui.navigation.Screen
import java.time.LocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderLocationView(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    viewModel: OrderViewModel = hiltViewModel(),
    shopId: String,
) {
    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    val displayedAddress by viewModel.displayedAddress.collectAsState()
    val hasLocationPermission by viewModel.hasLocationPermission.collectAsState()
    val userLocation by viewModel.userLocation.collectAsState()

    val cameraPositionState = rememberCameraPositionState()
    val userMarkerState = remember { MarkerState() }
    val shopMarkerState = remember { MarkerState() }
    val routePoints by viewModel.routePolyline.collectAsState()

    val isSearchingShop by viewModel.isSearchingShop.collectAsState()
    var isLocationConfirmed by remember { mutableStateOf(false) }
    var showSearchPlaceBottomSheet by remember { mutableStateOf(false) }
    val searchSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val selectedShop by viewModel.selectedShop.collectAsState()
    val selectedOrderType by viewModel.selectedOrderType.collectAsState()
    val selectedDate by viewModel.selectedDate.collectAsState()
    val selectedLocation by viewModel.selectedLocation.collectAsState()

    RequestLocationPermission(viewModel, hasLocationPermission, context, fusedLocationClient)

    LaunchedEffect(userLocation) {
        userLocation?.let {
            cameraPositionState.position = CameraPosition.fromLatLngZoom(it, 15f)
            userMarkerState.position = it
            if (displayedAddress.isNullOrBlank()) {
                viewModel.getAddressFromLocation(context, it)
            }
            if (!isLocationConfirmed) {
                viewModel.setSelectedLocation(it)
            }
        }
    }

    LaunchedEffect(isLocationConfirmed, selectedShop, selectedLocation) {
        if (shopId != "") {
            viewModel.getSelectedShopData(shopId)
        } else {
            viewModel.pickRecommendedShop()
        }

        if (selectedShop != null && selectedLocation != null) {
            val shopLocation = LatLng(selectedShop!!.latitude, selectedShop!!.longitude)
            if (shopLocation.latitude != 0.0 && shopLocation.longitude != 0.0 &&
                selectedLocation!!.latitude != 0.0 && selectedLocation!!.longitude != 0.0) {
                cameraPositionState.position = CameraPosition.fromLatLngZoom(selectedLocation!!, 15f)
                Log.d("OrderLocationView", "Shop: ${shopLocation.latitude}, ${shopLocation.longitude}")
                Log.d("OrderLocationView", "Customer: ${selectedLocation!!.latitude}, ${selectedLocation!!.longitude}")

                if (isLocationConfirmed) {
                    viewModel.getRoutePolyline(
                        origin = shopLocation,
                        destination = selectedLocation!!
                    )
                }
            }
        }

        if (selectedDate == null) {
            viewModel.setSelectedDate(LocalDateTime.now())
        }
    }

    if (showSearchPlaceBottomSheet) {
        SearchPlaceBottomSheet(
            sheetState = searchSheetState,
            onDismissRequest = { showSearchPlaceBottomSheet = false },
            onLocationSelected = {
                viewModel.setUserLocation(context, it)
                showSearchPlaceBottomSheet = false
            }
        )
    }

    Column(
        verticalArrangement = Arrangement.Center,
        modifier = modifier.fillMaxSize()
    ) {
        if (isSearchingShop) {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        } else {
            if (userLocation != null) {
                val shopLocation: LatLng? = if (selectedShop != null) {
                    LatLng(selectedShop!!.latitude, selectedShop!!.longitude)
                } else {
                    null
                }
                GoogleMapView(
                    hasLocationPermission,
                    cameraPositionState,
                    userMarkerState,
                    shopMarkerState,
                    userLocation,
                    shopLocation,
                    isLocationConfirmed,
                    routePoints,
                    modifier = Modifier.weight(1f)
                )
            }

            LocationInfoSection(
                displayedAddress,
                isLocationConfirmed,
                onChangeLocation = {
                    if (isLocationConfirmed) {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.OrderLocation.route)
                        }
                    } else {
                        showSearchPlaceBottomSheet = true
                    }
                },
                orderType = selectedOrderType,
                date = selectedDate,
                shop = selectedShop,
                customerLocation = selectedLocation,
                onConfirm = { value ->
                    if (!isLocationConfirmed && userLocation != null) {
                        viewModel.setSelectedLocation(selectedLocation!!)
                        isLocationConfirmed = true
                    } else {
                        viewModel.createOrder(
                            value,
                            routeAndPopUp = { route, popUp ->
                                navController.navigate(route) {
                                    popUpTo(popUp)
                                }
                            }
                        )
                    }
                }
            )
        }
    }
}

@Composable
fun GoogleMapView(
    isLocationEnabled: Boolean,
    cameraPositionState: CameraPositionState,
    markerState: MarkerState,
    shopMarkerState: MarkerState,
    userLocation: LatLng?,
    shopLocation: LatLng?,
    showShopMarker: Boolean,
    routePoints: List<LatLng>,
    modifier: Modifier
) {
    GoogleMap(
        cameraPositionState = cameraPositionState,
        uiSettings = MapUiSettings(zoomControlsEnabled = false),
        properties = MapProperties(isMyLocationEnabled = isLocationEnabled),
        modifier = modifier
    ) {
        Marker(state = markerState, icon = MapUtil.rememberCustomMarkerIcon(LocalContext.current, R.drawable.customer_marker))
        if (showShopMarker && shopLocation != null && userLocation != null) {
            val bounds = LatLngBounds.builder().include(userLocation).include(shopLocation).build()
            cameraPositionState.move(CameraUpdateFactory.newLatLngBounds(bounds, 150))
            shopMarkerState.position = shopLocation
            Marker(state = shopMarkerState, icon = MapUtil.rememberCustomMarkerIcon(LocalContext.current, R.drawable.shop_marker))

            if (routePoints.isNotEmpty()) {
                Polyline(
                    points = routePoints,
                    color = MaterialTheme.colorScheme.primary,
                    width = 10f
                )
            }
        }
    }
}

@Composable
fun LocationInfoSection(
    displayedAddress: String?,
    isConfirmed: Boolean,
    orderType: OrderType?,
    date: LocalDateTime?,
    shop: Shop?,
    customerLocation: LatLng?,
    onChangeLocation: () -> Unit,
    onConfirm: (Int) -> Unit
) {
    Box(
        Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(24.dp)
    ) {
        var priceList: List<Int> = listOf(0)
        Column {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    if (!isConfirmed) stringResource(R.string.order_location) else stringResource(R.string.confirm_order),
                    style = MaterialTheme.typography.titleMedium
                )
                Button(
                    onClick = onChangeLocation,
                    shape = RoundedCornerShape(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                ) {
                    Text(
                        if (!isConfirmed) stringResource(R.string.change_location) else stringResource(R.string.cancel),
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(Modifier.height(12.dp))
            Text(
                text = displayedAddress ?: stringResource(
                    if (isConfirmed) R.string.loading_address else R.string.need_location_permission
                ),
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(Modifier.height(12.dp))

            if (isConfirmed && orderType != null && shop != null && customerLocation != null) {
                if (date != null) {
                    Text(Util.formatDateTime(date), style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(12.dp))
                }
                Text(orderType.toDisplayString(), style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(12.dp))

                val shopLocation = LatLng(shop.latitude, shop.longitude)
                val distance = Util.calculateDistanceInMeters(shopLocation, customerLocation).toInt()
                Log.d("OrderLocationView", "distance: $distance")
                priceList = Util.calculatePriceBreakdown(orderType, distance)

                RenderPriceBreakdown(priceList)
            }

            Spacer(Modifier.height(24.dp))
            Button(
                onClick = { onConfirm(priceList.sum()) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text(
                    if (!isConfirmed) stringResource(R.string.confirm_location) else stringResource(R.string.confirm_order),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun RenderPriceBreakdown(
    priceList: List<Int>,
) {
    val labels = listOf("Jasa Service", "Biaya Transportasi", "Fee Aplikasi")
    val currentBalance = 200_000
    val newBalance = currentBalance - priceList.sum()

    labels.zip(priceList).forEach { (label, price) ->
        Spacer(Modifier.height(6.dp))
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(label, style = MaterialTheme.typography.bodyLarge)
            Text(Util.formatRupiah(price), style = MaterialTheme.typography.bodyMedium)
        }
    }

    Spacer(Modifier.height(12.dp))
    Text("Subtotal: ${Util.formatRupiah(priceList.sum())}", style = MaterialTheme.typography.titleMedium)
    Spacer(Modifier.height(6.dp))
    Text("Saldo: ${Util.formatRupiah(currentBalance)}", style = MaterialTheme.typography.titleMedium)
    Spacer(Modifier.height(6.dp))
    Text(
        if (newBalance >= 0) "Sisa saldo: ${Util.formatRupiah(newBalance)}"
        else "Saldo tidak mencukupi: ${Util.formatRupiah(newBalance)}",
        style = MaterialTheme.typography.titleMedium
    )
}