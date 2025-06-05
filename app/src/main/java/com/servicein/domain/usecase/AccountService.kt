package com.servicein.domain.usecase

import com.servicein.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AccountService {
    val currentUser: Flow<User?>
    val currentUserId: String

    fun hasUser(): Boolean
    fun getUserProfile(): User
    suspend fun signInWithGoogle(idToken: String): Result<User>
    fun signOut()
}