package com.servicein.presentation

import android.location.Location
import android.util.Log
import com.google.android.gms.maps.model.LatLng
import com.servicein.core.util.OrderType
import com.servicein.domain.model.Shop
import com.servicein.domain.usecase.CreateOrderUseCase
import com.servicein.domain.usecase.GetCustomerUseCase
import com.servicein.domain.usecase.GetShopsUseCase
import com.servicein.domain.usecase.ManagePreferencesUseCase
import com.servicein.domain.usecase.ManageWalletUseCase
import com.servicein.ui.navigation.Screen
import com.servicein.ui.screen.order.OrderViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import junit.framework.TestCase.assertNull
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime

@ExperimentalCoroutinesApi
class OrderViewModelTest {

    private val mockCreateOrderUseCase: CreateOrderUseCase = mockk()
    private val mockGetCustomerUseCase: GetCustomerUseCase = mockk()
    private val mockGetShopsUseCase: GetShopsUseCase = mockk()
    private val mockAppPreferencesManager: ManagePreferencesUseCase = mockk()
    private val mockManageWalletUseCase: ManageWalletUseCase = mockk()

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var orderViewModel: OrderViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        orderViewModel = OrderViewModel(
            mockGetShopsUseCase,
            mockAppPreferencesManager,
            mockGetCustomerUseCase,
            mockManageWalletUseCase,
            mockCreateOrderUseCase,
        )

        coEvery { mockAppPreferencesManager.customerId } returns flowOf("customer123")
        coEvery { mockAppPreferencesManager.customerName } returns flowOf("Test Customer")

        mockkStatic(Location::class)
        every { Location.distanceBetween(any(), any(), any(), any(), any()) } answers {
            val resultsArray = it.invocation.args[4] as FloatArray
            resultsArray[0] = 100f
        }

        mockkStatic(Log::class)
        every { Log.e(any(), any()) } returns 0
        every { Log.d(any(), any()) } returns 0
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // --- Pengujian createOrder ---

    @Test
    fun `createOrder should successfully create order and deduct wallet`() = runTest {
        val value = 150_000
        val customerId = "customer123"
        val shopId = "shop456"
        val shopName = "Test Shop"
        val orderType = OrderType.ROUTINE_SERVICE
        val selectedDate = LocalDateTime.now()
        val selectedLocation = LatLng(10.0, 20.0)

        orderViewModel.setSelectedShop( Shop(id = shopId, shopName = shopName, latitude = 0.0, longitude = 0.0) )
        orderViewModel.selectOrderType(orderType)
        orderViewModel.setSelectedDate(selectedDate)
        orderViewModel.setSelectedLocation(selectedLocation)

        coEvery {
            mockCreateOrderUseCase(
                any(), any(), any(), any(), any(), any(), any(), any(), any()
            )
        } returns Result.success(Unit)

        val newBalanceAfterDeduction = 50_000
        coEvery {
            mockManageWalletUseCase.deduct(customerId, value)
        } returns Result.success(newBalanceAfterDeduction)

        var routeCalled: String? = null
        var popUpCalled: String? = null
        val mockRouteAndPopUp: (String, String) -> Unit = { route, popUp ->
            routeCalled = route
            popUpCalled = popUp
        }

        orderViewModel.createOrder(value, mockRouteAndPopUp)
        advanceUntilIdle()

        assertTrue(routeCalled == Screen.Home.route)
        assertTrue(popUpCalled == Screen.OrderLocation.route)

        coVerify(exactly = 1) {
            mockCreateOrderUseCase(
                customerName = "Test Customer",
                customerId = customerId,
                shopId = shopId,
                shopName = shopName,
                orderType = orderType,
                latitude = selectedLocation.latitude,
                longitude = selectedLocation.longitude,
                dateTime = selectedDate.toString(),
                value = value
            )
        }
        coVerify(exactly = 1) { mockManageWalletUseCase.deduct(customerId, value) }
    }

    @Test
    fun `createOrder should handle failure in creating order`() = runTest {
        val value = 150000
        val orderType = OrderType.ROUTINE_SERVICE
        val selectedDate = LocalDateTime.now()
        val selectedLocation = LatLng(10.0, 20.0)

        orderViewModel.setSelectedShop(Shop(id = "shop456", shopName = "Test Shop", latitude = 0.0, longitude = 0.0))
        orderViewModel.selectOrderType(orderType)
        orderViewModel.setSelectedDate(selectedDate)
        orderViewModel.setSelectedLocation(selectedLocation)

        val expectedException = Exception("Order creation failed")
        coEvery { mockCreateOrderUseCase(any(), any(), any(), any(), any(), any(), any(), any(), any()) } returns Result.failure(expectedException)

        var routeCalled: String? = null
        val mockRouteAndPopUp: (String, String) -> Unit = { route, _ -> routeCalled = route }

        orderViewModel.createOrder(value, mockRouteAndPopUp)
        advanceUntilIdle()

        assertNull(routeCalled)

        coVerify(exactly = 1) { mockCreateOrderUseCase(any(), any(), any(), any(), any(), any(), any(), any(), any()) }
    }

    @Test
    fun `createOrder should handle failure in deducting wallet`() = runTest {
        val value = 150000
        val customerId = "customer123"
        val orderType = OrderType.ROUTINE_SERVICE
        val selectedDate = LocalDateTime.now()
        val selectedLocation = LatLng(10.0, 20.0)

        orderViewModel.setSelectedShop( Shop(id = "shop456", shopName = "Test Shop", latitude = 0.0, longitude = 0.0) )
        orderViewModel.selectOrderType(orderType)
        orderViewModel.setSelectedDate(selectedDate)
        orderViewModel.setSelectedLocation(selectedLocation)

        coEvery { mockCreateOrderUseCase(any(), any(), any(), any(), any(), any(), any(), any(), any()) } returns Result.success(Unit)
        val expectedException = Exception("Wallet deduction failed")
        coEvery { mockManageWalletUseCase.deduct(customerId, value) } returns Result.failure(expectedException)

        var routeCalled: String? = null
        val mockRouteAndPopUp: (String, String) -> Unit = { route, _ -> routeCalled = route }

        orderViewModel.createOrder(value, mockRouteAndPopUp)
        advanceUntilIdle()

        assertNull(routeCalled)

        coVerify(exactly = 1) { mockCreateOrderUseCase(any(), any(), any(), any(), any(), any(), any(), any(), any()) }

        coVerify(exactly = 1) { mockManageWalletUseCase.deduct(customerId, value) }
    }
}