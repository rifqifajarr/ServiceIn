package com.servicein.domain

import com.servicein.domain.model.User
import com.servicein.domain.usecase.AccountService
import com.servicein.domain.usecase.ManageAccountUseCase
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue

class ManageAccountUseCaseTest {

    private lateinit var mockAccountService: AccountService
    private lateinit var manageAccountUseCase: ManageAccountUseCase

    @Before
    fun setUp() {
        mockAccountService = mockk()
        manageAccountUseCase = ManageAccountUseCase(mockAccountService)
    }

    // --- Test untuk signIn ---

    @Test
    fun `signIn returns success with user when accountService succeeds`() = runTest {
        // Given
        val idToken = "someValidGoogleIdToken"
        val expectedUser = User(uid = "user123", email = "test@example.com")

        // Ketika accountService.signInWithGoogle dipanggil, kembalikan Result.success(expectedUser)
        coEvery { mockAccountService.signInWithGoogle(idToken) } returns Result.success(expectedUser)

        // When
        val result = manageAccountUseCase.signIn(idToken)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(expectedUser, result.getOrNull())
    }

    @Test
    fun `signIn returns failure when accountService fails`() = runTest {
        // Given
        val idToken = "someValidGoogleIdToken"
        val expectedException = Exception("Authentication failed")

        // Ketika accountService.signInWithGoogle dipanggil, kembalikan Result.failure(expectedException)
        coEvery { mockAccountService.signInWithGoogle(idToken) } returns Result.failure(expectedException)

        // When
        val result = manageAccountUseCase.signIn(idToken)

        // Then
        assertTrue(result.isFailure)
        assertEquals(expectedException, result.exceptionOrNull())
    }

    @Test
    fun `signIn returns failure when idToken is blank`() = runTest {
        // Given
        val idToken = ""

        // When
        val result = manageAccountUseCase.signIn(idToken)

        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is IllegalArgumentException)
        assertEquals("ID token cannot be blank", result.exceptionOrNull()?.message)
    }

    @Test
    fun `signIn returns failure when idToken is whitespace`() = runTest {
        // Given
        val idToken = "   "

        // When
        val result = manageAccountUseCase.signIn(idToken)

        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is IllegalArgumentException)
        assertEquals("ID token cannot be blank", result.exceptionOrNull()?.message)
    }

    // --- Test untuk signOut ---

    @Test
    fun `signOut returns success when accountService succeeds`() {
        // Given
        // Ketika accountService.signOut dipanggil, tidak lakukan apa-apa (berhasil)
        every { mockAccountService.signOut() } returns Unit

        // When
        val result = manageAccountUseCase.signOut()

        // Then
        assertTrue(result.isSuccess)
    }

    @Test
    fun `signOut returns failure when accountService throws exception`() {
        // Given
        val expectedException = RuntimeException("Logout failed")

        // Ketika accountService.signOut dipanggil, lemparkan exception
        every { mockAccountService.signOut() } throws expectedException

        // When
        val result = manageAccountUseCase.signOut()

        // Then
        assertTrue(result.isFailure)
        assertEquals(expectedException, result.exceptionOrNull())
    }
}