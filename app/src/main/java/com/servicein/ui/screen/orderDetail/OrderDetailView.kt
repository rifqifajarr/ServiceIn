package com.servicein.ui.screen.orderDetail

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import com.servicein.R
import com.servicein.core.util.MapUtil
import com.servicein.core.util.OrderStatus
import com.servicein.core.util.OrderType
import com.servicein.core.util.Util
import com.servicein.ui.component.RatingReviewBottomSheet
import com.servicein.ui.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailView(
    modifier: Modifier = Modifier,
    orderId: String,
    viewModel: OrderDetailViewModel = hiltViewModel(),
    navController: NavHostController
) {
    val context = LocalContext.current

    val order by viewModel.order.collectAsState()
    val shop by viewModel.shop.collectAsState()
    val isShopDataLoading by viewModel.isShopDataLoading.collectAsState()

    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    val hasLocationPermission by viewModel.hasLocationPermission.collectAsState()
    val cameraPositionState = rememberCameraPositionState()
    val userLocation by viewModel.userLocation.collectAsState()
    val routePoints by viewModel.routePolyline.collectAsState()

    var showRatingSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    if (showRatingSheet) {
        RatingReviewBottomSheet(
            sheetState = sheetState,
            onDismissRequest = { showRatingSheet = false },
            onSubmitReview = { rating, review ->
                viewModel.markOrderFinished(orderId, rating, review) { route, popUp ->
                    navController.navigate(route) {
                        popUpTo(popUp)
                    }
                }
                showRatingSheet = false
            }
        )
    }

    val orderMarkerState =
        remember { MarkerState(position = LatLng(0.0, 0.0)) }
    val shopMarkerState =
        remember { MarkerState(position = LatLng(0.0, 0.0)) }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true

        viewModel.setPermissionGranted(granted)

        if (granted) {
            viewModel.getLastKnownLocation(context, fusedLocationClient)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.getOrderData(orderId)
        viewModel.updatePermissionStatus(context)
        if (!hasLocationPermission) {
            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        } else {
            viewModel.getLastKnownLocation(context, fusedLocationClient)
        }
    }

    LaunchedEffect(userLocation) {
        userLocation?.let {
            cameraPositionState.position = CameraPosition.fromLatLngZoom(it, 9f)
        }
    }

    LaunchedEffect(isShopDataLoading) {
        if (order != null && userLocation != null) {
            val orderLatLng = LatLng(order!!.latitude, order!!.longitude)
            val shopLatLng = LatLng(shop!!.latitude, shop!!.longitude)
            orderMarkerState.position = orderLatLng
            shopMarkerState.position = shopLatLng
            val bounds = LatLngBounds.builder()
                .include(orderLatLng)
                .include(shopLatLng)
                .build()

            cameraPositionState.move(
                CameraUpdateFactory.newLatLngBounds(bounds, 150)
            )

            viewModel.getRoutePolyline(shopLatLng, orderLatLng)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
    ) {
        GoogleMap(
            cameraPositionState = cameraPositionState,
            uiSettings = MapUiSettings(
                zoomControlsEnabled = false,
                compassEnabled = false,
                mapToolbarEnabled = false,
                myLocationButtonEnabled = false,
            ),
            properties = MapProperties(
                isMyLocationEnabled = hasLocationPermission
            ),
            modifier = Modifier
                .weight(1f)
        ) {
            Marker(
                state = orderMarkerState,
                icon = MapUtil.rememberCustomMarkerIcon(context, R.drawable.customer_marker)
            )
            Marker(
                state = shopMarkerState,
                icon = MapUtil.rememberCustomMarkerIcon(context, R.drawable.shop_marker)
            )
            if (routePoints.isNotEmpty()) {
                Polyline(
                    points = routePoints,
                    color = MaterialTheme.colorScheme.primary,
                    width = 10f
                )
            }
        }
        Box(
            Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(horizontal = 24.dp, vertical = 24.dp)
        ) {
            if (isShopDataLoading) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.align(
                        Alignment.Center
                    )
                )
            } else {
                Column {
                    Text(
                        when (order?.orderType) {
                            OrderType.LIGHT_REPAIR.name -> "Perbaikan Ringan"
                            OrderType.ROUTINE_SERVICE.name -> "Service Rutin"
                            OrderType.EMERGENCY_SERVICE.name -> "Perbaikan Darurat"
                            else -> ""
                        },
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(Modifier.height(12.dp))
                    if (order != null) {
                        Text(
                            MapUtil.getAddressFromLocation(
                                context,
                                LatLng(order!!.latitude, order!!.longitude)
                            ),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    Spacer(Modifier.height(12.dp))
                    Text(
                        Util.formatRupiah(order?.value ?: 0),
                        style = MaterialTheme.typography.titleMedium
                    )
                    if (order?.technicianName != "") {
                        Spacer(Modifier.height(12.dp))
                        Text(
                            stringResource(R.string.technicianName, order?.technicianName ?: ""),
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                    Spacer(Modifier.height(24.dp))
                    if (order?.statusEnum == OrderStatus.RECEIVED) {
                        Text(
                            stringResource(R.string.waiting_shop),
                            style = MaterialTheme.typography.titleMedium,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.primary,
                        )
                    } else {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Button(
                                onClick = {
                                    showRatingSheet = true
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = Color.White
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = 8.dp)
                                    .height(60.dp)
                            ) {
                                Text(
                                    text = stringResource(R.string.finish_order),
                                    textAlign = TextAlign.Center,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Button(
                                enabled = order != null,
                                onClick = {
                                    navController.navigate(
                                        Screen.Chat.createRoute(
                                            order!!.shopId,
                                            order!!.shopName
                                        )
                                    )
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.surface,
                                    contentColor = MaterialTheme.colorScheme.primary,
                                ),
                                border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary),
                                modifier = Modifier
                                    .padding(horizontal = 6.dp)
                                    .height(60.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ChatBubble,
                                    contentDescription = "Chat Icon",
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}