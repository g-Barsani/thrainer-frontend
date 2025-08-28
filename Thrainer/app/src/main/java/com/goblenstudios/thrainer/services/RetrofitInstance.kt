package com.goblenstudios.thrainer.services

object RetrofitInstance {
    private val retrofit by lazy {
        retrofit2.Retrofit.Builder()
            .baseUrl("http://192.168.1.108:8080/api/")
            .addConverterFactory(retrofit2.converter.gson.GsonConverterFactory.create())
            .build()
    }

    val authService: AuthService by lazy {
        retrofit.create(AuthService::class.java)
    }

    val deckService: DeckService by lazy {
        retrofit.create(DeckService::class.java)
    }
}