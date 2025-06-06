package com.servicein.ui.screen.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.servicein.data.repository.ChatRepository
import com.servicein.domain.model.Chat
import com.servicein.domain.preference.AppPreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
    private val appPreferenceManager: AppPreferencesManager,
) : ViewModel() {
    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    private val _messageText = MutableStateFlow("")
    val messageText: StateFlow<String> = _messageText.asStateFlow()

    private var currentChatId: String = ""

    fun initChat(shopId: String, shopName: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            val customerId = appPreferenceManager.customerId.first()
            val customerName = appPreferenceManager.customerName.first()
            chatRepository.createOrGetChat(shopId, customerId, shopName, customerName)
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
            chatRepository.getChatMessages(chatId).collect { chat ->
                if (chat != null) {
                    val messages = combineMessages(chat)
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        chat = chat,
                        messages = messages,
                        error = null
                    )
                }
            }
        }
    }

    private fun combineMessages(chat: Chat): List<ChatMessage> {
        val allMessages = mutableListOf<ChatMessage>()

        // Combine and sort messages (assuming they're in chronological order)
        val maxSize = maxOf(chat.customerMessages.size, chat.shopMessages.size)

        for (i in 0 until maxSize) {
            if (i < chat.customerMessages.size) {
                allMessages.add(
                    ChatMessage(
                        text = chat.customerMessages[i],
                        isFromCurrentUser = true
                    )
                )
            }
            if (i < chat.shopMessages.size) {
                allMessages.add(
                    ChatMessage(
                        text = chat.shopMessages[i],
                        isFromCurrentUser = false
                    )
                )
            }
        }

        return allMessages
    }

    fun updateMessageText(text: String) {
        _messageText.value = text
    }

    fun sendMessage() {
        val message = _messageText.value.trim()
        if (message.isNotEmpty() && currentChatId.isNotEmpty()) {
            viewModelScope.launch {
                chatRepository.sendMessage(
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