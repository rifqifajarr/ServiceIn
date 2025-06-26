package com.servicein.domain.repository

import com.servicein.domain.model.Chat
import kotlinx.coroutines.flow.Flow

interface IChatRepository {
    suspend fun createOrGetChat(
        shopId: String,
        customerId: String,
        shopName: String,
        customerName: String,
    ): Result<String>

    suspend fun sendMessage(
        chatId: String,
        message: String,
    ): Result<Unit>

    fun getChatMessages(chatId: String): Flow<Chat?>

    suspend fun deleteChatByCustomerAndShop(customerId: String, shopId: String): Result<Unit>
}