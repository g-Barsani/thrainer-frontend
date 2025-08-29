package com.goblenstudios.thrainer.dtos

data class UserDto(
    val idUser: Long,
    val name: String,
    val email: String,
    val password: String,
    val deckUsers: List<DeckUserDto>
)