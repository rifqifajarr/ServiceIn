package com.servicein.core.util

import android.content.Context
import com.google.android.gms.location.FusedLocationProviderClient

interface LocationPermissionHandler {
    fun setPermissionGranted(granted: Boolean)
    fun updatePermissionStatus(context: Context)
    fun getLastKnownLocation(context: Context, fusedLocationClient: FusedLocationProviderClient)
}