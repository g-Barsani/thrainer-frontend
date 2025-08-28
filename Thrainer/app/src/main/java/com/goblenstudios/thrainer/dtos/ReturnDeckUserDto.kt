package com.goblenstudios.thrainer.dtos

data class ReturnDeckUserDto (
    val idDeckUser: Integer,
    val userId: Long,
    val userName: String,
    val deckId: Long,
    val deckName: String,
    )