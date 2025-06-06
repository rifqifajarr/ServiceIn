package com.servicein.data.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.servicein.core.util.OrderStatus
import com.servicein.core.util.OrderType
import com.servicein.domain.model.Order
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OrderRepository @Inject constructor(
    firestore: FirebaseFirestore
) {
    private val ordersCollection = firestore.collection("orders")

    companion object {
        private const val TAG = "OrderRepository"
    }

    suspend fun getOrderById(id: String): Result<Order?> {
        return try {
            if (id.isBlank()) {
                return Result.failure(IllegalArgumentException("Order ID tidak boleh kosong"))
            }

            val document = ordersCollection.document(id).get().await()
            val order = if (document.exists()) {
                document.toObject(Order::class.java)?.copy(id = document.id)
            } else {
                null
            }

            Result.success(order)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting order by ID: $id", e)
            Result.failure(e)
        }
    }

    suspend fun getOrdersByCustomerIdAndStatus(
        customerId: String,
        status: List<OrderStatus>
    ): Result<List<Order>> {
        return try {
            val querySnapshot = ordersCollection
                .whereEqualTo("customerId", customerId)
                .whereIn("orderStatus", status.map { it.name })
                .orderBy("dateTime", Query.Direction.DESCENDING)
                .get()
                .await()

            val orderList = querySnapshot.documents.mapNotNull { doc ->
                doc.toObject(Order::class.java)?.copy(id = doc.id)
            }
            Result.success(orderList)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting orders for shop ID: $customerId and status: $status", e)
            Result.failure(e)
        }
    }

    suspend fun completeOrder(
        orderId: String,
        rating: Int,
        review: String,
    ): Result<Unit> {
        return try {
            ordersCollection.document(orderId)
                .update(
                    mapOf(
                        "orderStatus" to OrderStatus.COMPLETED.name,
                        "rating" to rating,
                        "review" to review,
                    )
                )
                .await()

            Log.d(TAG, "Order completed: $orderId")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error updating order status", e)
            Result.failure(e)
        }
    }

    suspend fun createOrder(
        customerName: String,
        customerId: String,
        orderType: OrderType,
        latitude: Double,
        longitude: Double,
        dateTime: String,
        value: Int,
    ): Result<Unit> {
        return try {
            val docRef = ordersCollection.document()
            val newOrder = Order(
                id = docRef.id,
                customerName = customerName,
                customerId = customerId,
                orderStatus = OrderStatus.RECEIVED.name,
                orderType = orderType.name,
                latitude = latitude,
                longitude = longitude,
                dateTime = dateTime,
                value = value,
            )
            docRef.set(newOrder).await()
            Log.d(TAG, "Order created: $newOrder")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error updating order status", e)
            Result.failure(e)
        }
    }
}