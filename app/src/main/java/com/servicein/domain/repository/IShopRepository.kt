package com.servicein.domain.repository

import com.servicein.domain.model.Shop

interface IShopRepository {
    suspend fun getAllShops(): Result<List<Shop>>

    suspend fun getShopById(id: String): Result<Shop?>

    suspend fun addToWallet(shopId: String, amount: Int): Result<Unit>
}