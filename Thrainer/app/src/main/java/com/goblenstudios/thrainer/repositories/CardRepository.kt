package com.goblenstudios.thrainer.repositories

import com.goblenstudios.thrainer.dtos.CreateCardDto
import com.goblenstudios.thrainer.dtos.ReturnCardDto
import com.goblenstudios.thrainer.services.CardService

class CardRepository(val cardService: CardService) {

    suspend fun getCardById(id: Long): Result<ReturnCardDto>{

        return try {
            val response = cardService.getCardById(id)
            Result.success(response)
        }
        catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getCardsByDeck(deckId: Long): Result<List<ReturnCardDto>>{

        return try {
            val response = cardService.getCardsByDeck(deckId)
            Result.success(response)
        }
        catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createCard(dto: CreateCardDto): Result<ReturnCardDto>{

        return try {
            val response = cardService.createCard(dto)
            Result.success(response)
        }
        catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteCard(id: Long): Result<Void>{

        return try {
            val response = cardService.deleteCard(id)
            Result.success(response)
        }
        catch (e: Exception) {
            Result.failure(e)
        }
    }
}