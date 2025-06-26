package com.servicein.domain.usecase

import com.servicein.domain.repository.IOrderRepository
import javax.inject.Inject

class ChangeOrderStatusUseCase @Inject constructor(
    private val orderRepository: IOrderRepository
) {
    suspend fun completeOrder(orderId: String, rating: Int, review: String): Result<Unit> {
        if (orderId.isBlank()) {
            return Result.failure(IllegalArgumentException("Order ID tidak boleh kosong."))
        }
        if (rating !in 1..5) {
            return Result.failure(IllegalArgumentException("Rating harus dalam rentang 1 hingga 5."))
        }

        return orderRepository.completeOrder(orderId, rating, review)
    }
}