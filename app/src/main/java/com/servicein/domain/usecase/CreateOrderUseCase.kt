package com.servicein.domain.usecase

import com.servicein.core.util.OrderType
import com.servicein.domain.repository.IOrderRepository
import javax.inject.Inject

class CreateOrderUseCase @Inject constructor (private val orderRepository: IOrderRepository) {
    suspend operator fun invoke(
        customerName: String,
        customerId: String,
        shopId: String,
        shopName: String,
        orderType: OrderType,
        latitude: Double,
        longitude: Double,
        dateTime: String,
        value: Int,
    ): Result<Unit> {
        val genericErrorMessage = "Data pesanan tidak lengkap atau tidak valid."

        if (customerName.isBlank()) {
            return Result.failure(IllegalArgumentException(genericErrorMessage))
        }
        if (customerId.isBlank()) {
            return Result.failure(IllegalArgumentException(genericErrorMessage))
        }
        if (shopId.isBlank()) {
            return Result.failure(IllegalArgumentException(genericErrorMessage))
        }
        if (shopName.isBlank()) {
            return Result.failure(IllegalArgumentException(genericErrorMessage))
        }
        if (dateTime.isBlank()) {
            return Result.failure(IllegalArgumentException(genericErrorMessage))
        }
        if (value <= 0) {
            return Result.failure(IllegalArgumentException(genericErrorMessage))
        }

        return orderRepository.createOrder(
            customerName = customerName,
            customerId = customerId,
            shopId = shopId,
            shopName = shopName,
            orderType = orderType,
            latitude = latitude,
            longitude = longitude,
            dateTime = dateTime,
            value = value,
        )
    }
}