package com.goblenstudios.thrainer.services

import com.goblenstudios.thrainer.dtos.CreateCardDto
import com.goblenstudios.thrainer.dtos.ReturnCardDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface CardService {

    @GET("api/cards/{id}")
    suspend fun getCardById(@Path("id") id: Long): ReturnCardDto

    @GET("api/cards/deck/{deckId}")
    suspend fun getCardsByDeck(@Path("deckId") deckId: Long): List<ReturnCardDto>

    @POST("api/cards")
    suspend fun createCard(@Body dto: CreateCardDto): ReturnCardDto

    @DELETE("api/cards/{id}")
    suspend fun deleteCard(@Path("id") id: Long): Void

}
