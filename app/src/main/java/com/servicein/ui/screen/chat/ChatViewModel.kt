package com.servicein.ui.screen.chat

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.servicein.domain.model.Chat
import com.servicein.domain.usecase.ManageChatUseCase
import com.servicein.domain.usecase.ManagePreferencesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatUseCase: ManageChatUseCase,
    private val preferencesUseCase: ManagePreferencesUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    private val _messageText = MutableStateFlow("")
    val messageText: StateFlow<String> = _messageText.asStateFlow()

    private var currentChatId: String = ""

    fun initChat(shopId: String, shopName: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            val customerId = preferencesUseCase.customerId.first()
            val customerName = preferencesUseCase.customerName.first()
            chatUseCase.createOrGetChat(shopId, customerId, shopName, customerName)
                .onSuccess { chatId ->
                    currentChatId = chatId
                    observeChat(chatId)
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message
                    )
                }
        }
    }

    private fun observeChat(chatId: String) {
        viewModelScope.launch {
            chatUseCase.observeChat(chatId).collect { chat ->
                Log.d("ChatViewModel", "Received chat: ${chat?.id}, messages count: ${chat?.messages?.size}")

                if (chat != null) {
                    val messages = combineMessages(chat)
                    Log.d("ChatViewModel", "Combined messages count: ${messages.size}")

                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        chat = chat,
                        messages = messages,
                        error = null
                    )
                } else {
                    Log.w("ChatViewModel", "Chat is null")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Chat not found"
                    )
                }
            }
        }
    }

    private fun combineMessages(chat: Chat): List<ChatMessage> {
        return chat.messages
            .sortedBy { it.timestamp }
            .map { message ->
                ChatMessage(
                    text = message.text,
                    isFromCurrentUser = message.senderType == "customer",
                    timestamp = message.timestamp
                )
            }
    }

    fun updateMessageText(text: String) {
        _messageText.value = text
    }

    fun sendMessage() {
        val message = _messageText.value.trim()
        if (message.isNotEmpty() && currentChatId.isNotEmpty()) {
            viewModelScope.launch {
                chatUseCase.sendMessage(
                    chatId = currentChatId,
                    message = message,
                ).onSuccess {
                    _messageText.value = ""
                }.onFailure { error ->
                    _uiState.value = _uiState.value.copy(error = error.message)
                }
            }
        }
    }

    fun setChatId(chatId: String) {
        currentChatId = chatId
    }
}

data class ChatUiState(
    val isLoading: Boolean = false,
    val chat: Chat? = null,
    val error: String? = null,
    val messages: List<ChatMessage> = emptyList()
)

data class ChatMessage(
    val text: String,
    val isFromCurrentUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)