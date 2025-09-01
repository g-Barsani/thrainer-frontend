package com.goblenstudios.thrainer.services

import com.goblenstudios.thrainer.dtos.CreateUserCardDto
import com.goblenstudios.thrainer.dtos.ReturnCardDto
import com.goblenstudios.thrainer.dtos.ReturnUserCardDto
import com.goblenstudios.thrainer.dtos.UpdateUserCardDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface UserCardService {

    @POST("user-cards")
    suspend fun createUserCard( @Body dto: CreateUserCardDto): ReturnUserCardDto

    @PUT("user-cards")
    suspend fun updateUserCard( @Body dto: UpdateUserCardDto): ReturnUserCardDto

    @GET("user-cards/{cardId}")
    suspend fun getBestUsersByCard( @Body cardId: Long): ReturnUserCardDto

    @GET("user-cards/user/{userId}/deck/{deckId}")
    suspend fun getUserCardsByUserAndDeck( @Path("userId") userId: Long, @Path("deckId") deckId: Long): List<ReturnUserCardDto>

    @GET("user-cards/user/{userId}")
    suspend fun getUserCardsByUser( @Path("userId") userId: Long): List<ReturnUserCardDto>

    @POST("user-cards/{userId}/correct/{cardId}")
    suspend fun markCardAsCorrect( @Path("userId") userId: Long, @Path("cardId") cardId: Long): ReturnUserCardDto

    @POST("user-cards/{userId}/wrong/{cardId}")
    suspend fun markCardAsWrong( @Path("userId") userId: Long, @Path("cardId") cardId: Long): ReturnUserCardDto

    @GET("user-cards/practice")
    suspend fun practiceCardsByDeck(
        @Query("userId") userId: Long,
        @Query("deckId") deckId: Long,
        @Query("limit") limit: Int = 0
    ): List<ReturnCardDto>

}