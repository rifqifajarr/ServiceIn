package com.servicein.domain

import com.servicein.domain.repository.ICustomerRepository
import com.servicein.domain.usecase.ManageWalletUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue

class ManageWalletUseCaseTest {

    private lateinit var mockCustomerRepository: ICustomerRepository
    private lateinit var manageWalletUseCase: ManageWalletUseCase

    @Before
    fun setUp() {
        mockCustomerRepository = mockk()
        manageWalletUseCase = ManageWalletUseCase(mockCustomerRepository)
    }

    @Test
    fun `add returns success with new balance when repository succeeds`() = runTest {
        val customerId = "cust123"
        val amount = 50
        val expectedBalance = 150
        coEvery { mockCustomerRepository.addToWallet(customerId, amount) } returns Result.success(expectedBalance)

        val result = manageWalletUseCase.add(customerId, amount)

        assertTrue(result.isSuccess)
        assertEquals(expectedBalance, result.getOrNull())
    }

    @Test
    fun `add returns failure when repository fails`() = runTest {
        val customerId = "cust123"
        val amount = 50
        val expectedException = Exception("Repo error")
        coEvery { mockCustomerRepository.addToWallet(customerId, amount) } returns Result.failure(expectedException)

        val result = manageWalletUseCase.add(customerId, amount)

        assertTrue(result.isFailure)
        assertEquals(expectedException, result.exceptionOrNull())
    }

    @Test
    fun `add returns failure when customerId is blank`() = runTest {
        val customerId = ""
        val amount = 50

        val result = manageWalletUseCase.add(customerId, amount)

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is IllegalArgumentException)
        assertEquals("Customer ID tidak boleh kosong.", result.exceptionOrNull()?.message)
    }

    @Test
    fun `add returns failure when amount is zero or negative`() = runTest {
        val customerId = "cust123"
        val amount = 0

        val result = manageWalletUseCase.add(customerId, amount)

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is IllegalArgumentException)
        assertEquals("Jumlah penambahan harus positif.", result.exceptionOrNull()?.message)
    }

    @Test
    fun `deduct returns success with new balance when repository succeeds`() = runTest {
        val customerId = "cust123"
        val amount = 30
        val expectedBalance = 70
        coEvery { mockCustomerRepository.deductFromWallet(customerId, amount) } returns Result.success(expectedBalance)

        val result = manageWalletUseCase.deduct(customerId, amount)

        assertTrue(result.isSuccess)
        assertEquals(expectedBalance, result.getOrNull())
    }

    @Test
    fun `deduct returns failure when repository fails`() = runTest {
        val customerId = "cust123"
        val amount = 30
        val expectedException = Exception("Insufficient balance")
        coEvery { mockCustomerRepository.deductFromWallet(customerId, amount) } returns Result.failure(expectedException)

        val result = manageWalletUseCase.deduct(customerId, amount)

        assertTrue(result.isFailure)
        assertEquals(expectedException, result.exceptionOrNull())
    }

    @Test
    fun `deduct returns failure when customerId is blank`() = runTest {
        val customerId = ""
        val amount = 30

        val result = manageWalletUseCase.deduct(customerId, amount)

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is IllegalArgumentException)
        assertEquals("Customer ID tidak boleh kosong.", result.exceptionOrNull()?.message)
    }

    @Test
    fun `deduct returns failure when amount is zero or negative`() = runTest {
        val customerId = "cust123"
        val amount = 0

        val result = manageWalletUseCase.deduct(customerId, amount)

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is IllegalArgumentException)
        assertEquals("Jumlah pengurangan harus positif.", result.exceptionOrNull()?.message)
    }
}