package com.servicein.ui.component

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.servicein.core.connectivity.ConnectivityManager
import kotlinx.coroutines.delay

@Composable
fun GlobalDialogContainer(
    connectivityManager: ConnectivityManager,
    activity: Activity
) {
    val connectivityState by connectivityManager.connectivityState.collectAsState()
    var showInternetDialog by remember { mutableStateOf(false) }
    var showLocationDialog by remember { mutableStateOf(false) }
    var locationDialogDismissed by remember { mutableStateOf(false) }

    // Show dialogs based on connectivity state
    LaunchedEffect(connectivityState.isInternetConnected) {
        showInternetDialog = !connectivityState.isInternetConnected
    }

    LaunchedEffect(connectivityState.isLocationEnabled) {
        if (!connectivityState.isLocationEnabled && !locationDialogDismissed) {
            // Delay to avoid showing location dialog immediately
            delay(1000)
            showLocationDialog = true
        } else if (connectivityState.isLocationEnabled) {
            showLocationDialog = false
            locationDialogDismissed = false
        }
    }

    // Internet Dialog
    if (showInternetDialog) {
        NoInternetDialog(
            onDismiss = {
                // Don't allow dismissing internet dialog - it's critical
            },
            onOpenSettings = {
                connectivityManager.openWifiSettings(activity)
            },
            onRetry = {
                // The dialog will automatically hide when connection is restored
            }
        )
    }

    // Location Dialog
    if (showLocationDialog) {
        LocationDisabledDialog(
            onDismiss = {
                // Don't allow dismissing location dialog - it's critical
            },
            onOpenSettings = {
                connectivityManager.openLocationSettings(activity)
                showLocationDialog = false
                locationDialogDismissed = true
            },
            onIgnore = {
                showLocationDialog = false
                locationDialogDismissed = true
            }
        )
    }
}