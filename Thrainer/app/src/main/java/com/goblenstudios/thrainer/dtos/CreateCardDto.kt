package com.goblenstudios.thrainer.dtos

data class CreateCardDto (
    val question: String,
    val answer: String,
    val deckId: Long

)