package com.goblenstudios.thrainer.repositories

import com.goblenstudios.thrainer.dtos.ReturnUserDto
import com.goblenstudios.thrainer.services.UserService

class UserRepository(val userService: UserService) {

    suspend fun getUserById(id: Long): Result<ReturnUserDto>{
        return try {
            val response = userService.getUserById(id)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUserByEmail(email: String): Result<ReturnUserDto>{
        return try {
            val response = userService.getUserByEmail(email)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun searchUsersByName(name: String): Result<List<ReturnUserDto>>{
        return try {
            val response = userService.searchUsersByName(name)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getMostEngagedUsers(): Result<List<ReturnUserDto>>{
        return try {
            val response = userService.getMostEngagedUsers()
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAllUsers(): Result<List<ReturnUserDto>>{
        return try {
            val response = userService.getAllUsers()
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}