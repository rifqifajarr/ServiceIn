package com.servicein.domain.repository

import com.google.firebase.firestore.ListenerRegistration
import com.servicein.core.util.OrderStatus
import com.servicein.core.util.OrderType
import com.servicein.domain.model.Order

interface IOrderRepository {

    fun listenToOrderById(
        id: String,
        onResult: (Result<Order?>) -> Unit
    ): ListenerRegistration?

    suspend fun getOrdersByCustomerIdAndStatus(
        customerId: String,
        status: List<OrderStatus>
    ): Result<List<Order>>

    fun listenToOrdersByCustomerId(
        customerId: String,
        orderStatus: List<OrderStatus>,
        onOrdersChanged: (List<Order>) -> Unit,
        onError: (Exception) -> Unit
    ): ListenerRegistration

    suspend fun completeOrder(
        orderId: String,
        rating: Int,
        review: String,
    ): Result<Unit>

    suspend fun createOrder(
        customerName: String,
        customerId: String,
        shopId: String,
        shopName: String,
        orderType: OrderType,
        latitude: Double,
        longitude: Double,
        dateTime: String,
        value: Int,
    ): Result<Unit>
}