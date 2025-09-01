package com.goblenstudios.thrainer.repositories

import com.goblenstudios.thrainer.dtos.CreateDeckDto
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

    suspend fun searchDecksByName(name: String): Result<List<ReturnDeckDto>> {
        return try {
            val response = deckService.searchDecksByName(name)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createDeck(dto: CreateDeckDto): Result<ReturnDeckDto> {
        return try {
            val response = deckService.createDeck(dto)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


}