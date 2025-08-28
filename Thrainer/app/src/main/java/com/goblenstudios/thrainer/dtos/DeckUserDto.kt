package com.goblenstudios.thrainer.dtos

data class DeckUserDto(
    val idDeckUser: Int,
    val user: UserDto,
    val deck: DeckDto
)