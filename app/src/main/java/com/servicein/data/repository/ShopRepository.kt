package com.servicein.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.servicein.domain.model.Shop
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ShopRepository @Inject constructor(
    firestore: FirebaseFirestore,
) {
    private val shopCollection = firestore.collection("shops")

    suspend fun getAllShops(): Result<List<Shop>> {
        return try {
            val documents = shopCollection.get().await()
            val shops = documents.mapNotNull { it.toObject(Shop::class.java) }
            Result.success(shops)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getShopById(id: String): Result<Shop?> {
        return try {
            val document = shopCollection.document(id).get().await()
            val shop = document.toObject(Shop::class.java)
            Result.success(shop)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}