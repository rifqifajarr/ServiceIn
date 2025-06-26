package com.servicein.domain.usecase

import com.servicein.core.util.OrderStatus
import com.servicein.data.repository.OrderRepository
import com.servicein.domain.model.Order
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class GetOrderUseCase @Inject constructor(
    private val orderRepository: OrderRepository
) {
     fun listenActiveOrder(customerId: String): Flow<Result<List<Order>>> = callbackFlow {
        if (customerId.isBlank()) {
                trySend(Result.failure(IllegalArgumentException("Customer ID tidak boleh kosong")))
            }

        val listenerRegistration = orderRepository.listenToOrdersByCustomerId(
            customerId,
            listOf(OrderStatus.RECEIVED, OrderStatus.ACCEPTED, OrderStatus.FINISHED),
            onOrdersChanged = { trySend(Result.success(it)) },
            onError = { trySend(Result.failure(it)) }
        )

        awaitClose { listenerRegistration.remove() }
    }

    suspend fun getOrderHistory(customerId: String): Result<List<Order>> {
        if (customerId.isBlank()) {
                return Result.failure(IllegalArgumentException("Customer ID tidak boleh kosong"))
            }
        return orderRepository.getOrdersByCustomerIdAndStatus(
            customerId,
            listOf(OrderStatus.COMPLETED, OrderStatus.REJECTED)
        )
    }

    fun listenToOrderById(orderId: String): Flow<Result<Order?>> = callbackFlow {
        if (orderId.isBlank()) {
            trySend(Result.failure(IllegalArgumentException("Order ID tidak boleh kosong"))).isSuccess
            close()
            return@callbackFlow
        }

        val listenerRegistration = orderRepository.listenToOrderById(
            orderId,
            onResult = { trySend(it) },
        )

        awaitClose {
            listenerRegistration?.remove()
        }
    }
}