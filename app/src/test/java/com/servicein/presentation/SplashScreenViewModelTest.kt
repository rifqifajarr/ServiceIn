package com.servicein.presentation

import android.util.Log
import com.servicein.domain.usecase.ManagePreferencesUseCase
import com.servicein.ui.navigation.Screen
import com.servicein.ui.screen.splashScreen.SplashScreenViewModel
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class SplashScreenViewModelTest {

    private val mockPreferencesUseCase: ManagePreferencesUseCase = mockk()
    private lateinit var viewModel: SplashScreenViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = SplashScreenViewModel(mockPreferencesUseCase)

        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        every { Log.e(any(), any(), any()) } returns 0
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `onAppStart navigates to Home when customer is logged in`() = runTest {
        every { mockPreferencesUseCase.customerName } returns flowOf("John")
        every { mockPreferencesUseCase.customerId } returns flowOf("cust123")

        var route: String? = null
        var popUp: String? = null
        val mockNavigate: (String, String) -> Unit = { r, p ->
            route = r
            popUp = p
        }

        viewModel.onAppStart(mockNavigate)
        advanceUntilIdle()

        assertEquals(Screen.Home.route, route)
        assertEquals(Screen.SplashScreen.route, popUp)
    }

    @Test
    fun `onAppStart navigates to Login when customer is not logged in`() = runTest {
        every { mockPreferencesUseCase.customerName } returns flowOf("")
        every { mockPreferencesUseCase.customerId } returns flowOf("")

        var route: String? = null
        var popUp: String? = null
        val mockNavigate: (String, String) -> Unit = { r, p ->
            route = r
            popUp = p
        }

        viewModel.onAppStart(mockNavigate)
        advanceUntilIdle()

        assertEquals(Screen.Login.route, route)
        assertEquals(Screen.SplashScreen.route, popUp)
    }
}
