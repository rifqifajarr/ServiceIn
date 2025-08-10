package com.servicein.data.notification

import android.content.Context
import android.content.Intent
import com.servicein.MainActivity
import com.servicein.domain.model.Chat
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatDetector @Inject constructor(
    private val notificationService: NotificationService,
    @ApplicationContext private val context: Context
) {
    private val previousChatStates = mutableMapOf<String, ChatState>()

    data class ChatState(
        val lastMessageCount: Int,
        val lastMessageTimestamp: Long,
        val lastMessageFromShop: Boolean
    )

    private fun createCurrentChatState(chat: Chat): ChatState {
        val lastMessage = chat.messages.maxByOrNull { it.timestamp }

        return ChatState(
            lastMessageCount = chat.messages.size,
            lastMessageTimestamp = lastMessage?.timestamp ?: 0L,
            lastMessageFromShop = lastMessage?.senderType == "shop"
        )
    }

    fun detectAndHandleNewChats(chatId: String, chat: Chat) {
        val previousState = previousChatStates[chatId]
        val currentState = createCurrentChatState(chat)

        when {
            previousState == null && chat.messages.isNotEmpty() -> {
                handleNewChat(chat)
            }

            previousState != null && isNewMessageFromShop(previousState, currentState) -> {
                handleNewChat(chat)
            }
        }

        previousChatStates[chatId] = currentState
    }

    private fun isNewMessageFromShop(
        previousState: ChatState,
        currentState: ChatState,
    ): Boolean {
        // Cek apakah ada pesan baru dan pesan terakhir dari shop
        val hasNewMessage = currentState.lastMessageCount > previousState.lastMessageCount
        val lastMessageFromShop = currentState.lastMessageFromShop
        val newerTimestamp = currentState.lastMessageTimestamp > previousState.lastMessageTimestamp

        return hasNewMessage && lastMessageFromShop && newerTimestamp
    }

    private fun handleNewChat(chat: Chat) {
        val lastMessage = chat.messages.maxByOrNull { it.timestamp }
        val messagePreview = lastMessage?.text?.take(50) ?: "Pesan baru"

        val intent = Intent(context, MainActivity::class.java).apply {
            putExtra("chatId", chat.id)
            putExtra("shopName", chat.shopName)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        notificationService.showNotification(
            title = "Pesan Baru dari ${chat.shopName}",
            message = messagePreview,
            intent = intent
        )
    }

    fun clearChatHistory() {
        previousChatStates.clear()
    }
}