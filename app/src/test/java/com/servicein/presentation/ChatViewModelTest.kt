package com.servicein.presentation

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.servicein.domain.usecase.ManageChatUseCase
import com.servicein.domain.usecase.ManagePreferencesUseCase
import com.servicein.ui.screen.chat.ChatViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class ChatViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var mockChatUseCase: ManageChatUseCase
    private lateinit var mockPreferencesUseCase: ManagePreferencesUseCase
    private lateinit var viewModel: ChatViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        mockChatUseCase = mockk(relaxed = true)
        mockPreferencesUseCase = mockk(relaxed = true)

        viewModel = ChatViewModel(mockChatUseCase, mockPreferencesUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initChat handles createOrGetChat failure`() = runTest {
        val shopId = "shop1"
        val shopName = "Shop A"
        val customerId = "cust1"
        val customerName = "Customer B"
        val errorMessage = "Failed to create chat"
        val expectedException = RuntimeException(errorMessage)

        every { mockPreferencesUseCase.customerId } returns flowOf(customerId)
        every { mockPreferencesUseCase.customerName } returns flowOf(customerName)
        coEvery { mockChatUseCase.createOrGetChat(shopId, customerId, shopName, customerName) } returns Result.failure(expectedException)

        viewModel.initChat(shopId, shopName)
        advanceUntilIdle()

        assertNull(viewModel.uiState.first().chat)
        assertFalse(viewModel.uiState.first().isLoading)
        assertEquals(errorMessage, viewModel.uiState.first().error)

        coVerify { mockChatUseCase.createOrGetChat(shopId, customerId, shopName, customerName) }
        verify(exactly = 0) { mockChatUseCase.observeChat(any()) }
    }

    @Test
    fun `updateMessageText updates message text state`() = runTest {
        val text = "New message"
        viewModel.updateMessageText(text)
        assertEquals(text, viewModel.messageText.first())
    }

    @Test
    fun `sendMessage sends message and clears text on success`() = runTest {
        val chatId = "chat123"
        val message = "Hello world"
        viewModel.setChatId(chatId)
        viewModel.updateMessageText(message)

        coEvery { mockChatUseCase.sendMessage(chatId, message) } returns Result.success(Unit)

        viewModel.sendMessage()
        advanceUntilIdle()

        assertEquals("", viewModel.messageText.first())
        assertNull(viewModel.uiState.first().error)
        coVerify { mockChatUseCase.sendMessage(chatId, message) }
    }

    @Test
    fun `sendMessage sets error on failure`() = runTest {
        val chatId = "chat123"
        val message = "Hello world"
        val errorMessage = "Send failed"
        val expectedException = RuntimeException(errorMessage)
        viewModel.setChatId(chatId)
        viewModel.updateMessageText(message)

        coEvery { mockChatUseCase.sendMessage(chatId, message) } returns Result.failure(expectedException)

        viewModel.sendMessage()
        advanceUntilIdle()

        assertEquals(message, viewModel.messageText.first()) // Text not cleared on failure
        assertEquals(errorMessage, viewModel.uiState.first().error)
        coVerify { mockChatUseCase.sendMessage(chatId, message) }
    }

    @Test
    fun `sendMessage does nothing if message is empty`() = runTest {
        viewModel.updateMessageText("")
        viewModel.sendMessage()
        advanceUntilIdle()

        coVerify(exactly = 0) { mockChatUseCase.sendMessage(any(), any()) }
    }

    @Test
    fun `sendMessage does nothing if currentChatId is empty`() = runTest {
        viewModel.setChatId("")
        viewModel.updateMessageText("Some text")
        viewModel.sendMessage()
        advanceUntilIdle()

        coVerify(exactly = 0) { mockChatUseCase.sendMessage(any(), any()) }
    }
}