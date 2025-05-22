package com.servicein.domain.model

import com.google.android.gms.maps.model.LatLng
import com.servicein.core.util.OrderStatus
import com.servicein.core.util.OrderType
import java.time.LocalDateTime

data class Order(
    val orderType: OrderType,
    val customerName: String,
    val address: LatLng,
    val dateTime: LocalDateTime,
    val value: Int,
    val orderStatus: OrderStatus,
    val rating: Int = 0
)