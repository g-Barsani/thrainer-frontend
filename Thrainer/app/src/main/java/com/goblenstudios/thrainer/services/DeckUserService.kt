package com.goblenstudios.thrainer.services

import com.goblenstudios.thrainer.dtos.ReturnDeckUserDto
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface DeckUserService {

    @POST("deck-users/copy")
    suspend fun copyDeckToUser(@Query("userId") userId: Long,
                                @Query("deckId") deckId: Long
    ): ReturnDeckUserDto

    @GET("deck-users/user/{userId}")
    suspend fun getDeckUsersByUser(@Path("userId") userId: Long): List<ReturnDeckUserDto>

    @GET("deck-users/deck/{deckId}")
    suspend fun getDeckUsersByDeck(@Path("deckId") deckId: Long): List<ReturnDeckUserDto>


}