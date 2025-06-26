package com.servicein.domain.usecase

import com.servicein.domain.model.Chat
import com.servicein.domain.repository.IChatRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ManageChatUseCase @Inject constructor(
    private val chatRepository: IChatRepository
) {
    suspend fun createOrGetChat(
        shopId: String,
        customerId: String,
        shopName: String,
        customerName: String,
    ): Result<String> {
        if (shopId.isBlank() || customerId.isBlank() || shopName.isBlank() || customerName.isBlank()) {
            return Result.failure(IllegalArgumentException("Semua parameter chat harus diisi."))
        }
        return chatRepository.createOrGetChat(shopId, customerId, shopName, customerName)
    }

    suspend fun sendMessage(
        chatId: String,
        message: String,
    ): Result<Unit> {
        if (chatId.isBlank()) {
            return Result.failure(IllegalArgumentException("Chat ID tidak boleh kosong."))
        }
        if (message.isBlank()) {
            return Result.failure(IllegalArgumentException("Pesan tidak boleh kosong."))
        }
        return chatRepository.sendMessage(chatId, message)
    }

    suspend fun deleteChat(customerId: String, shopId: String): Result<Unit> {
        if (customerId.isBlank() || shopId.isBlank()) {
            return Result.failure(IllegalArgumentException("Customer ID dan Shop ID tidak boleh kosong."))
        }
        return chatRepository.deleteChatByCustomerAndShop(customerId, shopId)
    }

    fun observeChat(chatId: String): Flow<Chat?> {
        if (chatId.isBlank()) {
            throw IllegalArgumentException("Chat ID tidak boleh kosong.")
        }
        return chatRepository.getChatMessages(chatId)
    }
}