package com.servicein.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.servicein.domain.model.Customer
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CustomerRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
) {
    private val customerCollection = firestore.collection("customers")

    suspend fun createCustomer(customerId: String, customerName: String): Result<Unit> {
        return try {
            val customer = Customer(id = customerId, customerName = customerName)
            customerCollection.document(customerId).set(customer).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getCustomerById(customerId: String): Result<Customer?> {
        return try {
            val document = customerCollection.document(customerId).get().await()
            val customer = if (document.exists()) {
                document.toObject<Customer>()?.copy(id = document.id)
            } else {
                null
            }
            Result.success(customer)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun addToWallet(customerId: String, amount: Int): Result<Int> {
        return try {
            val newBalance = firestore.runTransaction { transaction ->
                val docRef = customerCollection.document(customerId)
                val snapshot = transaction.get(docRef)
                val customer = snapshot.toObject<Customer>()

                if (customer != null) {
                    val newWalletBalance = customer.wallet + amount
                    transaction.update(docRef, "wallet", newWalletBalance)
                    newWalletBalance
                } else {
                    throw Exception("Customer not found")
                }
            }.await()

            Result.success(newBalance)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Deduct money from wallet (transaction-safe)
    suspend fun deductFromWallet(customerId: String, amount: Int): Result<Int> {
        return try {
            val newBalance = firestore.runTransaction { transaction ->
                val docRef = customerCollection.document(customerId)
                val snapshot = transaction.get(docRef)
                val customer = snapshot.toObject<Customer>()

                if (customer != null) {
                    if (customer.wallet >= amount) {
                        val newWalletBalance = customer.wallet - amount
                        transaction.update(docRef, "wallet", newWalletBalance)
                        newWalletBalance
                    } else {
                        throw Exception("Insufficient balance")
                    }
                } else {
                    throw Exception("Customer not found")
                }
            }.await()

            Result.success(newBalance)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun customerExists(customerId: String): Result<Boolean> {
        return try {
            val document = customerCollection.document(customerId).get().await()
            Result.success(document.exists())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}