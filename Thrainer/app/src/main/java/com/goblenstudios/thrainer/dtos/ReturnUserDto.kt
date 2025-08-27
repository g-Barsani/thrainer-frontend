package com.goblenstudios.thrainer.dtos

data class ReturnUserDto (
    val idUser: Long,
    val name: String,
    val email: String,
    val isPublic: Boolean
)