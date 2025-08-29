package com.goblenstudios.thrainer.repositories

import com.goblenstudios.thrainer.dtos.CreateUserCardDto
import com.goblenstudios.thrainer.dtos.ReturnCardDto
import com.goblenstudios.thrainer.dtos.ReturnUserCardDto
import com.goblenstudios.thrainer.dtos.UpdateUserCardDto
import com.goblenstudios.thrainer.services.UserCardService

class UserCardRepository(val userCardService: UserCardService) {

    suspend fun createUserCard(dto: CreateUserCardDto): Result<ReturnUserCardDto> {
        return try {
            val response = userCardService.createUserCard(dto)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateUserCard(dto: UpdateUserCardDto): Result<ReturnUserCardDto> {
        return try {
            val response = userCardService.updateUserCard(dto)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getBestUsersByCard(cardId: Long): Result<ReturnUserCardDto> {
        return try {
            val response = userCardService.getBestUsersByCard(cardId)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUserCardsByUserAndDeck(userId: Long, deckId: Long): Result<List<ReturnUserCardDto>> {
        return try {
            val response = userCardService.getUserCardsByUserAndDeck(userId, deckId)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUserCardsByUser(userId: Long): Result<List<ReturnUserCardDto>> {
        return try {
            val response = userCardService.getUserCardsByUser(userId)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun markCardAsCorrect(userId: Long, cardId: Long): Result<ReturnUserCardDto> {
        return try {
            val response = userCardService.markCardAsCorrect(userId, cardId)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun markCardAsWrong(userId: Long, cardId: Long): Result<ReturnUserCardDto> {
        return try {
            val response = userCardService.markCardAsWrong(userId, cardId)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun practiceCardsByDeck(userId: Long, deckId: Long, limit: Int = 0): Result<List<ReturnCardDto>> {
        return try {
            val response = userCardService.practiceCardsByDeck(userId, deckId, limit)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}