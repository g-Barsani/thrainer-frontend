package com.goblenstudios.thrainer.repositories

import AuthResponseDto
import com.goblenstudios.thrainer.services.AuthService

class AuthRepository(private val authService: AuthService) {
    suspend fun login(email: String, password: String): Result<AuthResponseDto> {
        return try {
            val response = authService.login(email, password)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}