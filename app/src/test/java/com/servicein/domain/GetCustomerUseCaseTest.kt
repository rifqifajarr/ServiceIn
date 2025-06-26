package com.servicein.domain

import com.servicein.domain.model.Customer
import com.servicein.domain.repository.ICustomerRepository
import com.servicein.domain.usecase.GetCustomerUseCase
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class GetCustomerUseCaseTest {
    private lateinit var mockCustomerRepository: ICustomerRepository
    private lateinit var getCustomerUseCase: GetCustomerUseCase

    @Before
    fun setUp() {
        // Inisialisasi mock sebelum setiap tes
        mockCustomerRepository = mockk()
        // Buat instance Use Case dengan mock repository
        getCustomerUseCase = GetCustomerUseCase(mockCustomerRepository)
    }

    @Test
    fun `invoke returns success with customer when repository returns customer`() = runTest {
        // Given: Sebuah customer ID dan objek Customer yang valid
        val customerId = "testCustomerId123"
        val expectedCustomer = Customer(id = customerId, customerName = "John Doe", wallet = 100)

        // Ketika customerRepository.getCustomerById dipanggil dengan customerId, kembalikan Result.success(expectedCustomer)
        // Gunakan coEvery untuk suspend functions
        coEvery { mockCustomerRepository.getCustomerById(customerId) } returns Result.success(expectedCustomer)

        // When: Use Case dipanggil
        val result = getCustomerUseCase(customerId)

        // Then: Hasilnya adalah Result.success dan berisi customer yang diharapkan
        assertTrue(result.isSuccess)
        assertEquals(expectedCustomer, result.getOrNull())
    }

    @Test
    fun `invoke returns success with null when repository returns null`() = runTest {
        // Given: Sebuah customer ID
        val customerId = "nonExistentCustomerId"

        // Ketika customerRepository.getCustomerById dipanggil, kembalikan Result.success(null)
        coEvery { mockCustomerRepository.getCustomerById(customerId) } returns Result.success(null)

        // When: Use Case dipanggil
        val result = getCustomerUseCase(customerId)

        // Then: Hasilnya adalah Result.success dan berisi null
        assertTrue(result.isSuccess)
        assertEquals(null, result.getOrNull())
    }

    @Test
    fun `invoke returns failure when repository returns failure`() = runTest {
        // Given: Sebuah customer ID dan Exception simulasi
        val customerId = "errorCustomerId"
        val expectedException = RuntimeException("Network error")

        // Ketika customerRepository.getCustomerById dipanggil, kembalikan Result.failure(expectedException)
        coEvery { mockCustomerRepository.getCustomerById(customerId) } returns Result.failure(expectedException)

        // When: Use Case dipanggil
        val result = getCustomerUseCase(customerId)

        // Then: Hasilnya adalah Result.failure dan berisi Exception yang diharapkan
        assertTrue(result.isFailure)
        assertEquals(expectedException, result.exceptionOrNull())
    }

    @Test
    fun `invoke returns failure when customerId is blank`() = runTest {
        // Given: Customer ID kosong
        val customerId = ""

        // When: Use Case dipanggil
        val result = getCustomerUseCase(customerId)

        // Then: Hasilnya adalah Result.failure dengan IllegalArgumentException
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is IllegalArgumentException)
        assertEquals("Customer ID tidak boleh kosong.", result.exceptionOrNull()?.message)
    }
}