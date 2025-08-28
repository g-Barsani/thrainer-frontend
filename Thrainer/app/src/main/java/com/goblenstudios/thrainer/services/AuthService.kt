package com.goblenstudios.thrainer.services

import AuthResponseDto
import com.goblenstudios.thrainer.dtos.CreateUserDto
import com.goblenstudios.thrainer.dtos.ReturnUserDto
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface AuthService {
    @POST("auth/login")
    suspend fun login(
        @Query("email") email: String,
        @Query("password") password: String
    ): AuthResponseDto

    @POST("auth/register")
    suspend fun register(
        @Body createUserDto: CreateUserDto
    ): ReturnUserDto
}