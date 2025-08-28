package com.goblenstudios.thrainer.repositories

import com.goblenstudios.thrainer.dtos.ReturnDeckUserDto
import com.goblenstudios.thrainer.services.DeckUserService

class DeckUserRepository(private val deckUserService: DeckUserService) {
    suspend fun copyDeckToUser(userId: Long, deckId: Long): Result<ReturnDeckUserDto> {
        return try {
            val response = deckUserService.copyDeckToUser(userId, deckId)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getDeckUsersByUser(userId: Long): Result<List<ReturnDeckUserDto>> {
        return try {
            val response = deckUserService.getDeckUsersByUser(userId)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getDeckUsersByDeck(deckId: Long): Result<List<ReturnDeckUserDto>> {
        return try {
            val response = deckUserService.getDeckUsersByDeck(deckId)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}