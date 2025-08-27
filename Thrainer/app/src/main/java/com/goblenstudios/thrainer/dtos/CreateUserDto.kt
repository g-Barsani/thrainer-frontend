package com.goblenstudios.thrainer.dtos

data class CreateUserDto (
    val name: String,
    val email: String,
    val password: String,
    val isPublic: Boolean
)