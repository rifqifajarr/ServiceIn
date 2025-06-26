package com.servicein.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.servicein.domain.model.Shop
import com.servicein.domain.repository.IShopRepository
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ShopRepository @Inject constructor(
    firestore: FirebaseFirestore,
): IShopRepository {
    private val shopCollection = firestore.collection("shops")

    override suspend fun getAllShops(): Result<List<Shop>> {
        return try {
            val documents = shopCollection.get().await()
            val shops = documents.mapNotNull { it.toObject(Shop::class.java) }
            Result.success(shops)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getShopById(id: String): Result<Shop?> {
        return try {
            val document = shopCollection.document(id).get().await()
            val shop = document.toObject(Shop::class.java)
            Result.success(shop)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun addToWallet(shopId: String, amount: Int): Result<Unit> {
        return try {
            val shopResult = getShopById(shopId)
            val shop = shopResult.getOrNull() ?: return Result.failure(Exception("Shop not found"))

            val updatedWallet = shop.wallet + amount
            val updatedShop = shop.copy(wallet = updatedWallet)

            updateShop(updatedShop)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun updateShop(shop: Shop): Result<Unit> {
        return try {
            shopCollection.document(shop.id)
                .set(shop)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}