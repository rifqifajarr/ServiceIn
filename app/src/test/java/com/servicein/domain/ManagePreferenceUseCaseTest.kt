package com.servicein.domain

import com.servicein.domain.preferences.IAppPreferencesManager
import com.servicein.domain.usecase.ManagePreferencesUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull

class ManagePreferencesUseCaseTest {

    private lateinit var mockPreferencesManager: IAppPreferencesManager
    private lateinit var managePreferencesUseCase: ManagePreferencesUseCase

    @Before
    fun setUp() {
        mockPreferencesManager = mockk()
        managePreferencesUseCase = ManagePreferencesUseCase(mockPreferencesManager)
    }

    @Test
    fun `setCustomerId calls setCustomerId on preferences manager`() = runTest {
        val customerId = "testCustomerId123"
        coEvery { mockPreferencesManager.setCustomerId(customerId) } returns Unit

        managePreferencesUseCase.setCustomerId(customerId)

        coVerify { mockPreferencesManager.setCustomerId(customerId) }
    }

    @Test
    fun `setCustomerName calls setCustomerName on preferences manager`() = runTest {
        val customerName = "Test Customer"
        coEvery { mockPreferencesManager.setCustomerName(customerName) } returns Unit

        managePreferencesUseCase.setCustomerName(customerName)

        coVerify { mockPreferencesManager.setCustomerName(customerName) }
    }

    @Test
    fun `clearAllPreferences calls clearAll on preferences manager`() = runTest {
        coEvery { mockPreferencesManager.clearAll() } returns Unit

        managePreferencesUseCase.clearAllPreferences()

        coVerify { mockPreferencesManager.clearAll() }
    }

    @Test
    fun `customerId flow returns customerId from preferences manager`() = runTest {
        val expectedCustomerId = "flowCustomerId"
        every { mockPreferencesManager.customerId } returns flowOf(expectedCustomerId)

        val customerIdFlow = managePreferencesUseCase.customerId

        assertNotNull(customerIdFlow)
        assertEquals(expectedCustomerId, customerIdFlow.first())
    }

    @Test
    fun `customerName flow returns customerName from preferences manager`() = runTest {
        val expectedCustomerName = "Flow Customer Name"
        every { mockPreferencesManager.customerName } returns flowOf(expectedCustomerName)

        val customerNameFlow = managePreferencesUseCase.customerName

        assertNotNull(customerNameFlow)
        assertEquals(expectedCustomerName, customerNameFlow.first())
    }
}