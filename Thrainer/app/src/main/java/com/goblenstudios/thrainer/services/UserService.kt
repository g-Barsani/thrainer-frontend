package com.goblenstudios.thrainer.services

import com.goblenstudios.thrainer.dtos.ReturnUserDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface UserService {

    @GET("api/users/{id})")
    suspend fun getUserById(@Path("id") id: Long): ReturnUserDto

    @GET("api/users/by-email")
    suspend fun getUserByEmail(@Query("email") email: String): ReturnUserDto

    @GET("api/users/search")
    suspend fun searchUsersByName(@Query("name") name: String): List<ReturnUserDto>

    @GET("api/users/engaged")
    suspend fun getMostEngagedUsers(): List<ReturnUserDto>

    @GET("api/users")
    suspend fun getAllUsers(): List<ReturnUserDto>
}