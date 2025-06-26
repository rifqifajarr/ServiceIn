package com.servicein.domain

import com.servicein.domain.model.Chat
import com.servicein.domain.repository.IChatRepository
import com.servicein.domain.usecase.ManageChatUseCase
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ManageChatUseCaseTest {

    private lateinit var mockChatRepository: IChatRepository
    private lateinit var manageChatUseCase: ManageChatUseCase

    @Before
    fun setUp() {
        mockChatRepository = mockk()
        manageChatUseCase = ManageChatUseCase(mockChatRepository)
    }

    // --- Test untuk createOrGetChat ---

    @Test
    fun `createOrGetChat returns success with chat ID when repository succeeds`() = runTest {
        // Given
        val shopId = "shop1"
        val customerId = "customer1"
        val shopName = "Shop A"
        val customerName = "Customer B"
        val expectedChatId = "newChatId123"

        coEvery { mockChatRepository.createOrGetChat(shopId, customerId, shopName, customerName) } returns Result.success(expectedChatId)

        // When
        val result = manageChatUseCase.createOrGetChat(shopId, customerId, shopName, customerName)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(expectedChatId, result.getOrNull())
    }

    @Test
    fun `createOrGetChat returns failure when repository fails`() = runTest {
        // Given
        val shopId = "shop1"
        val customerId = "customer1"
        val shopName = "Shop A"
        val customerName = "Customer B"
        val expectedException = RuntimeException("Network error")

        coEvery { mockChatRepository.createOrGetChat(shopId, customerId, shopName, customerName) } returns Result.failure(expectedException)

        // When
        val result = manageChatUseCase.createOrGetChat(shopId, customerId, shopName, customerName)

        // Then
        assertTrue(result.isFailure)
        assertEquals(expectedException, result.exceptionOrNull())
    }

    @Test
    fun `createOrGetChat returns failure when shopId is blank`() = runTest {
        // Given
        val shopId = ""
        val customerId = "customer1"
        val shopName = "Shop A"
        val customerName = "Customer B"

        // When
        val result = manageChatUseCase.createOrGetChat(shopId, customerId, shopName, customerName)

        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is IllegalArgumentException)
        assertEquals("Semua parameter chat harus diisi.", result.exceptionOrNull()?.message)
    }

    @Test
    fun `createOrGetChat returns failure when customerId is blank`() = runTest {
        // Given
        val shopId = "shop1"
        val customerId = ""
        val shopName = "Shop A"
        val customerName = "Customer B"

        // When
        val result = manageChatUseCase.createOrGetChat(shopId, customerId, shopName, customerName)

        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is IllegalArgumentException)
        assertEquals("Semua parameter chat harus diisi.", result.exceptionOrNull()?.message)
    }

    // --- Test untuk sendMessage ---

    @Test
    fun `sendMessage returns success when repository succeeds`() = runTest {
        // Given
        val chatId = "chat123"
        val message = "Hello there!"

        coEvery { mockChatRepository.sendMessage(chatId, message) } returns Result.success(Unit)

        // When
        val result = manageChatUseCase.sendMessage(chatId, message)

        // Then
        assertTrue(result.isSuccess)
    }

    @Test
    fun `sendMessage returns failure when repository fails`() = runTest {
        // Given
        val chatId = "chat123"
        val message = "Hello there!"
        val expectedException = Exception("Failed to send message")

        coEvery { mockChatRepository.sendMessage(chatId, message) } returns Result.failure(expectedException)

        // When
        val result = manageChatUseCase.sendMessage(chatId, message)

        // Then
        assertTrue(result.isFailure)
        assertEquals(expectedException, result.exceptionOrNull())
    }

    @Test
    fun `sendMessage returns failure when chatId is blank`() = runTest {
        // Given
        val chatId = ""
        val message = "Test message"

        // When
        val result = manageChatUseCase.sendMessage(chatId, message)

        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is IllegalArgumentException)
        assertEquals("Chat ID tidak boleh kosong.", result.exceptionOrNull()?.message)
    }

    @Test
    fun `sendMessage returns failure when message is blank`() = runTest {
        // Given
        val chatId = "chat123"
        val message = ""

        // When
        val result = manageChatUseCase.sendMessage(chatId, message)

        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is IllegalArgumentException)
        assertEquals("Pesan tidak boleh kosong.", result.exceptionOrNull()?.message)
    }

    // --- Test untuk deleteChat ---

    @Test
    fun `deleteChat returns success when repository succeeds`() = runTest {
        // Given
        val customerId = "cust1"
        val shopId = "shop1"

        coEvery { mockChatRepository.deleteChatByCustomerAndShop(customerId, shopId) } returns Result.success(Unit)

        // When
        val result = manageChatUseCase.deleteChat(customerId, shopId)

        // Then
        assertTrue(result.isSuccess)
    }

    @Test
    fun `deleteChat returns failure when repository fails`() = runTest {
        // Given
        val customerId = "cust1"
        val shopId = "shop1"
        val expectedException = Exception("Failed to delete chat")

        coEvery { mockChatRepository.deleteChatByCustomerAndShop(customerId, shopId) } returns Result.failure(expectedException)

        // When
        val result = manageChatUseCase.deleteChat(customerId, shopId)

        // Then
        assertTrue(result.isFailure)
        assertEquals(expectedException, result.exceptionOrNull())
    }

    @Test
    fun `deleteChat returns failure when customerId is blank`() = runTest {
        // Given
        val customerId = ""
        val shopId = "shop1"

        // When
        val result = manageChatUseCase.deleteChat(customerId, shopId)

        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is IllegalArgumentException)
        assertEquals("Customer ID dan Shop ID tidak boleh kosong.", result.exceptionOrNull()?.message)
    }

    @Test
    fun `deleteChat returns failure when shopId is blank`() = runTest {
        // Given
        val customerId = "cust1"
        val shopId = ""

        // When
        val result = manageChatUseCase.deleteChat(customerId, shopId)

        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is IllegalArgumentException)
        assertEquals("Customer ID dan Shop ID tidak boleh kosong.", result.exceptionOrNull()?.message)
    }

    // --- Test untuk observeChat ---

    @Test
    fun `observeChat returns flow of chat from repository`() = runTest {
        // Given
        val chatId = "chat123"
        val expectedChat = Chat(id = chatId, shopId = "s1", customerId = "c1", shopName = "Shop A", customerName = "Cust B")

        // every for non-suspend function and returns a Flow
        every { mockChatRepository.getChatMessages(chatId) } returns flowOf(expectedChat)

        // When
        val chatFlow = manageChatUseCase.observeChat(chatId)

        // Then
        assertNotNull(chatFlow)
        assertEquals(expectedChat, chatFlow.first()) // Collect the first emitted item
    }
}