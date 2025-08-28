package com.goblenstudios.thrainer.dtos

data class DeckDto (
    val idDeck: Long,
    val idUserCreator: UserDto,
    val name: String,
    val isPublic: Boolean
)