package com.servicein.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.servicein.domain.model.Chat
import com.servicein.domain.model.Message
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatRepository @Inject constructor(
    firestore: FirebaseFirestore,
) {
    private val chatCollection = firestore.collection("chats")

    suspend fun createOrGetChat(
        shopId: String,
        customerId: String,
        shopName: String,
        customerName: String,
    ): Result<String> {
        return try {
            // Check if chat already exists
            val existingChat = chatCollection
                .whereEqualTo("shopId", shopId)
                .whereEqualTo("customerId", customerId)
                .get()
                .await()

            if (existingChat.documents.isNotEmpty()) {
                Result.success(existingChat.documents.first().id)
            } else {
                // Create new chat
                val newChat = Chat(
                    shopId = shopId,
                    customerId = customerId,
                    shopName = shopName,
                    customerName = customerName,
                )
                val docRef = chatCollection.add(newChat).await()
                Result.success(docRef.id)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun sendMessage(
        chatId: String,
        message: String,
    ): Result<Unit> {
        return try {
            val chatRef = chatCollection.document(chatId)
            val chat = chatRef.get().await().toObject<Chat>()?.copy(id = chatRef.id)

            if (chat != null) {
                val updatedChat = chat.copy(
                    messages = chat.messages + Message(
                        text = message,
                        senderType = "customer",
                    ),
                )

                chatRef.set(updatedChat).await()
                Result.success(Unit)
            } else {
                Result.failure(Exception("Chat not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getChatMessages(chatId: String): Flow<Chat?> = callbackFlow {
        val listener = chatCollection.document(chatId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val chat = snapshot?.toObject<Chat>()
                trySend(chat)
            }

        awaitClose { listener.remove() }
    }

    suspend fun deleteChatByCustomerAndShop(customerId: String, shopId: String): Result<Unit> {
        return try {
            val querySnapshot = chatCollection
                .whereEqualTo("customerId", customerId)
                .whereEqualTo("shopId", shopId)
                .get()
                .await()

            if (querySnapshot.documents.isEmpty()) {
                return Result.failure(Exception("Chat not found"))
            }

            // Asumsikan hanya ada satu chat untuk kombinasi customerId & shopId
            val chatDoc = querySnapshot.documents.first()
            chatDoc.reference.delete().await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}