package com.servicein.ui.component

import android.Manifest
import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.google.android.gms.location.FusedLocationProviderClient
import com.servicein.core.util.LocationPermissionHandler

@Composable
fun <T> RequestLocationPermission(
    viewModel: T,
    hasPermission: Boolean,
    context: Context,
    fusedLocationClient: FusedLocationProviderClient
) where T : LocationPermissionHandler{
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions.any { it.value }
        viewModel.setPermissionGranted(granted)
        if (granted) {
            viewModel.getLastKnownLocation(context, fusedLocationClient)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.updatePermissionStatus(context)
        if (!hasPermission) {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        } else {
            viewModel.getLastKnownLocation(context, fusedLocationClient)
        }
    }
}