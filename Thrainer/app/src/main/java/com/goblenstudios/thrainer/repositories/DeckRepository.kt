package com.goblenstudios.thrainer.repositories

import com.goblenstudios.thrainer.services.DeckService
import com.goblenstudios.thrainer.dtos.ReturnDeckDto

class DeckRepository(private val deckService: DeckService) {

    suspend fun getAllPublicDecks(): Result<List<ReturnDeckDto>> {
        return try {
            val response = deckService.getAllPublicDecks()
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getMostPopularDecks(): Result<List<ReturnDeckDto>> {
        return try {
            val response = deckService.getMostPopularDecks()
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}