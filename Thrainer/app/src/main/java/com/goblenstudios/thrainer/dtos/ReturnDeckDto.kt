package com.goblenstudios.thrainer.dtos

data class ReturnDeckDto (
    val idDeck: Long,
    val name: String,
    val isPublic: Boolean,
    val idUserCreator: Long,
    val creatorUserName: String,
    val numberOfCards: Integer,
)