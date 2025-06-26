package com.servicein.domain.usecase

import com.servicein.domain.model.Shop
import com.servicein.domain.repository.IShopRepository
import javax.inject.Inject

class GetShopsUseCase @Inject constructor(
    private val shopRepository: IShopRepository
) {
    suspend operator fun invoke(): Result<List<Shop>> {
        return shopRepository.getAllShops()
    }

    suspend operator fun invoke(shopId: String): Result<Shop?> {
        return shopRepository.getShopById(shopId)
    }
}