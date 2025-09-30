package com.goblenstudios.thrainer.services

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

object RetrofitInstance {
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    private val retrofit by lazy {
        retrofit2.Retrofit.Builder()
            .baseUrl("http://192.168.1.108:8080/api/")
            .client(okHttpClient)
            .addConverterFactory(retrofit2.converter.gson.GsonConverterFactory.create())
            .build()
    }

    val authService: AuthService by lazy {
        retrofit.create(AuthService::class.java)
    }

    val deckService: DeckService by lazy {
        retrofit.create(DeckService::class.java)
    }

    val deckUserService: DeckUserService by lazy {
        retrofit.create(DeckUserService::class.java)
    }

    val cardService: CardService by lazy {
        retrofit.create(CardService::class.java)
    }

    val userService: UserService by lazy {
        retrofit.create(UserService::class.java)
    }

    val userCardService: UserCardService by lazy {
        retrofit.create(UserCardService::class.java)
    }

}