package com.servicein.domain.model

import com.google.android.gms.maps.model.LatLng

data class Shop(
    val id: Int,
    val shopName: String,
    val address: LatLng,
    val rating: Int,
)
