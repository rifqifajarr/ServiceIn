package com.servicein.domain.model

import com.servicein.core.util.OrderStatus
import com.servicein.core.util.OrderType

data class Order(
    val id: String = "",
    val customerId: String = "",
    val customerName: String = "",
    val technicianName: String = "",
    val dateTime: String = "", // Format ISO 8601
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val orderStatus: String = OrderStatus.RECEIVED.name,
    val orderType: String = OrderType.ROUTINE_SERVICE.name,
    val rating: Int = 0,
    val review: String = "",
    val shopId: String = "",
    val value: Int = 0
) {
    constructor() : this("", "", "", "", "", 0.0, 0.0, "", "", 0, "", "", 0)

    val statusEnum: OrderStatus
        get() = OrderStatus.valueOf(orderStatus)

    val typeEnum: OrderType
        get() = OrderType.valueOf(orderType)
}