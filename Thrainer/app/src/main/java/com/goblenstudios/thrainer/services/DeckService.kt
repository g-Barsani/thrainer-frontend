package com.goblenstudios.thrainer.services

import com.goblenstudios.thrainer.dtos.ReturnDeckDto
import retrofit2.http.GET

interface DeckService {
    @GET("api/decks/public")
    suspend fun getAllPublicDecks(): List<ReturnDeckDto>

    @GET("api/decks/popular")
    suspend fun getMostPopularDecks(): List<ReturnDeckDto>

    @GET("api/decks/search")
    suspend fun searchDecksByNAME(name: String): List<ReturnDeckDto>
}