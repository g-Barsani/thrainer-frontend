package com.goblenstudios.thrainer.services

import com.goblenstudios.thrainer.dtos.CreateDeckDto
import com.goblenstudios.thrainer.dtos.ReturnDeckDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface DeckService {
    @GET("decks/public")
    suspend fun getAllPublicDecks(): List<ReturnDeckDto>

    @GET("decks/popular")
    suspend fun getMostPopularDecks(): List<ReturnDeckDto>

    @GET("decks/search")
    suspend fun searchDecksByName(@Query("namePart") name: String): List<ReturnDeckDto>

    @GET("decks/user/{userId}")
    suspend fun getDecksByUser(@Path ("userId") userId: Long): List<ReturnDeckDto>

    @POST("decks")
    suspend fun createDeck(@Body dto: CreateDeckDto): ReturnDeckDto
}