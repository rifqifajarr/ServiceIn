package com.servicein

import com.servicein.core.util.OrderType
import com.servicein.domain.repository.IOrderRepository
import com.servicein.domain.usecase.CreateOrderUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class CreateOrderUseCaseTest {

    private val mockOrderRepository: IOrderRepository = mockk()
    private lateinit var createOrderUseCase: CreateOrderUseCase

    @Before
    fun setUp() {
        createOrderUseCase = CreateOrderUseCase(mockOrderRepository)
    }

    @Test
    fun `invoke should return success when order creation is successful`() = runTest {
        val customerName = "John Doe"
        val customerId = "cust123"
        val shopId = "shop456"
        val shopName = "Bengkel A"
        val orderType = OrderType.ROUTINE_SERVICE
        val latitude = 1.0
        val longitude = 2.0
        val dateTime = "2024-12-25T10:00:00"
        val value = 150000

        coEvery {
            mockOrderRepository.createOrder(
                customerName, customerId, shopId, shopName, orderType, latitude, longitude, dateTime, value
            )
        } returns Result.success(Unit)

        val result = createOrderUseCase(
            customerName, customerId, shopId, shopName, orderType, latitude, longitude, dateTime, value
        )

        assertTrue(result.isSuccess)

        coVerify(exactly = 1) {
            mockOrderRepository.createOrder(
                customerName, customerId, shopId, shopName, orderType, latitude, longitude, dateTime, value
            )
        }
    }

    @Test
    fun `invoke should return failure when order creation fails in repository`() = runTest {
        val customerName = "John Doe"
        val customerId = "cust123"
        val shopId = "shop456"
        val shopName = "Bengkel A"
        val orderType = OrderType.ROUTINE_SERVICE
        val latitude = 1.0
        val longitude = 2.0
        val dateTime = "2024-12-25T10:00:00"
        val value = 150000
        val expectedException = Exception("Network error")

        coEvery {
            mockOrderRepository.createOrder(any(), any(), any(), any(), any(), any(), any(), any(), any())
        } returns Result.failure(expectedException)

        val result = createOrderUseCase(
            customerName, customerId, shopId, shopName, orderType, latitude, longitude, dateTime, value
        )

        assertTrue(result.isFailure)
        assertEquals(expectedException, result.exceptionOrNull())
        coVerify(exactly = 1) {
            mockOrderRepository.createOrder(
                customerName, customerId, shopId, shopName, orderType, latitude, longitude, dateTime, value
            )
        }
    }

    @Test
    fun `invoke should return failure for incomplete customer name`() = runTest {
        val customerName = ""
        val customerId = "cust123"
        val shopId = "shop456"
        val shopName = "Bengkel A"
        val orderType = OrderType.ROUTINE_SERVICE
        val latitude = 1.0
        val longitude = 2.0
        val dateTime = "2024-12-25T10:00:00"
        val value = 150000

        val result = createOrderUseCase(
            customerName, customerId, shopId, shopName, orderType, latitude, longitude, dateTime, value
        )

        assertTrue(result.isFailure)
        assertEquals("Data pesanan tidak lengkap atau tidak valid.", result.exceptionOrNull()?.message)
        coVerify(exactly = 0) { mockOrderRepository.createOrder(any(), any(), any(), any(), any(), any(), any(), any(), any()) }
    }

    @Test
    fun `invoke should return failure for invalid value`() = runTest {
        val customerName = "John Doe"
        val customerId = "cust123"
        val shopId = "shop456"
        val shopName = "Bengkel A"
        val orderType = OrderType.ROUTINE_SERVICE
        val latitude = 1.0
        val longitude = 2.0
        val dateTime = "2024-12-25T10:00:00"
        val value = 0

        val result = createOrderUseCase(
            customerName, customerId, shopId, shopName, orderType, latitude, longitude, dateTime, value
        )

        assertTrue(result.isFailure)
        assertEquals("Data pesanan tidak lengkap atau tidak valid.", result.exceptionOrNull()?.message)
        coVerify(exactly = 0) { mockOrderRepository.createOrder(any(), any(), any(), any(), any(), any(), any(), any(), any()) }
    }
}