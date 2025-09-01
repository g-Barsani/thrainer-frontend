package com.goblenstudios.thrainer.dtos

data class CreateDeckDto (
    val name: String,
    val idUserCreator: Long,
    val isPublic: Boolean,
)