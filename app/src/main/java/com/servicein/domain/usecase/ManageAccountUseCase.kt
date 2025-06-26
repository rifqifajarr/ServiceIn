package com.servicein.domain.usecase

import com.servicein.domain.model.User
import javax.inject.Inject

class ManageAccountUseCase @Inject constructor(
    private val accountService: AccountService
) {
    suspend fun signIn(idToken: String): Result<User> {
        if (idToken.isBlank()) {
            return Result.failure(IllegalArgumentException("ID token cannot be blank"))
        }
        return accountService.signInWithGoogle(idToken)
    }

    fun signOut(): Result<Unit> {
        return try {
            accountService.signOut()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}