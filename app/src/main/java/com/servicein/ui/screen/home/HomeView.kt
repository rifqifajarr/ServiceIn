package com.servicein.ui.screen.home

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.servicein.R
import com.servicein.core.util.MapUtil
import com.servicein.ui.component.ShopRecommendationItem
import com.servicein.ui.component.TopUpBottomSheet
import com.servicein.ui.component.UserInfo
import com.servicein.ui.component.WalletAndHistory
import com.servicein.ui.component.WalletBottomSheet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeView(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    viewModel: HomeViewModel = viewModel()
) {
    val context = LocalContext.current

    val walletBottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showWalletBottomSheet by remember { mutableStateOf(false) }

    val topUpBottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showTopUpBottomSheet by remember { mutableStateOf(false) }

    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    val userLocation by viewModel.userLocation.collectAsState()
    val hasLocationPermission by viewModel.hasLocationPermission.collectAsState()
    val cameraPositionState = rememberCameraPositionState()

    val nearestShops by viewModel.nearestShop.collectAsState()

    val scrollState = rememberScrollState()

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
        viewModel.updatePermissionStatus(context)
        viewModel.getNearestShop()
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
            cameraPositionState.position = CameraPosition.fromLatLngZoom(it, 13f)
        }
    }

    LaunchedEffect(showWalletBottomSheet) {
        if (showWalletBottomSheet) {
            walletBottomSheetState.show()
        } else {
            walletBottomSheetState.hide()
        }
    }

    LaunchedEffect(showTopUpBottomSheet) {
        if (showTopUpBottomSheet) {
            topUpBottomSheetState.show()
        } else {
            topUpBottomSheetState.hide()
        }
    }

    if (showWalletBottomSheet) {
        WalletBottomSheet(
            wallet = 100000,
            sheetState = walletBottomSheetState,
            onDismissRequest = {
                showWalletBottomSheet = false
            },
            onTopUpButtonClick = {
                showTopUpBottomSheet = true
                showWalletBottomSheet = false
            }
        )
    }

    if (showTopUpBottomSheet) {
        TopUpBottomSheet(
            wallet = 100000,
            sheetState = topUpBottomSheetState,
            onDismissRequest = {
                showTopUpBottomSheet = false
            },
            onTopUpButtonClick = {}
        )
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface,
        floatingActionButton = {
            Button(
                onClick = {},
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 62.dp)
                    .height(60.dp)
            ) {
                Text(
                    text = stringResource(R.string.order_service),
                    style = MaterialTheme.typography.titleMedium,
                )
            }
        },
        floatingActionButtonPosition = FabPosition.Center,

        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 28.dp)
                .verticalScroll(scrollState)
        ) {
            Spacer(modifier = Modifier.height(28.dp))
            Image(
                painter = painterResource(R.drawable.logo_text_cyan),
                contentDescription = "ServiceIn Logo",
                contentScale = ContentScale.FillWidth,
                modifier = Modifier
                    .width(158.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            UserInfo(username = "Rifqi Fajar", onProfileButtonClick = {
                navController.navigate("form")
            })
            Spacer(modifier = Modifier.height(32.dp))
            WalletAndHistory(
                onWalletButtonClick = {
                    showWalletBottomSheet = true
                },
                onHistoryButtonClick = {
                    navController.navigate("history")
                },
                modifier = Modifier
                    .fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = stringResource(R.string.recommended_shop),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .align(Alignment.Start)
            )
            Spacer(modifier = Modifier.height(12.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(nearestShops) { shop ->
                    ShopRecommendationItem(
                        shop = shop,
                        onItemClick = {
                            viewModel.selectShop(shop)
                            navController.navigate("shop_detail")
                        }
                    )
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = stringResource(R.string.nearest_shop),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .align(Alignment.Start)
            )
            Spacer(modifier = Modifier.height(12.dp))
            GoogleMap(
                cameraPositionState = cameraPositionState,
                uiSettings = MapUiSettings(
                    mapToolbarEnabled = false,
                    compassEnabled = false,
                    zoomControlsEnabled = false,
                    scrollGesturesEnabled = true,
                    zoomGesturesEnabled = true,
                ),
                properties = MapProperties(
                    isMyLocationEnabled = hasLocationPermission
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(450.dp)
                    .clip(RoundedCornerShape(20.dp))
            ) {
                nearestShops.forEach { shop ->
                    Marker(
                        state = MarkerState(position = shop.address),
                        icon = MapUtil.rememberCustomMarkerIcon(context, R.drawable.shop_marker),
                        title = shop.shopName,
                        snippet = "Rating: ${shop.rating}",
                        onInfoWindowClick = {
                            viewModel.selectShop(shop)
                            navController.navigate("shop_detail")
                        }
                    )
                }
            }
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}